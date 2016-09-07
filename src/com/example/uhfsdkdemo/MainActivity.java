package com.example.uhfsdkdemo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Surface;  
import android.view.SurfaceHolder;  
import android.view.SurfaceHolder.Callback;  
import android.view.SurfaceView; 

import com.android.hdhe.uhf.reader.NewSendCommendManager;
import com.android.hdhe.uhf.reader.Tools;
import com.android.hdhe.uhf.reader.UhfReader;

public class MainActivity extends Activity implements OnClickListener ,OnItemClickListener{

	private Button buttonClear;
	private Button buttonConnect;
	private Button buttonStart;
	private Button btnTakePicture;
	private SurfaceView surfaceView; //相机控件
	private TextView textVersion ;
	private ListView listViewData;
	private ArrayList<EPC> listEPC;
	private ArrayList<Map<String, Object>> listMap;
	private boolean runFlag = true;
	private boolean startFlag = false;
	private boolean connectFlag = false;
	private Camera camera; 
	private Camera.Parameters parameters = null;  
	private UhfReader reader ; //超高频读写器 
	private Bundle bundle = null; // 声明一个Bundle对象，用来存储数据  
	private boolean takePictureFlag = false;	//Camera.startPreview()之后，拍照Camera.takePicture()之前标记为flase
	private AutoFocusCallback myAutoFocusCallback = null;	//自动变焦回调
	private ScreenStateReceiver screenReceiver;
	private String picPath;
	private static final String TAG = "cyn";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setOverflowShowingAlways();
		setContentView(R.layout.main);
		initView();
		//获取读写器实例，若返回Null,则串口初始化失败
		reader = UhfReader.getInstance();
		if(reader == null){
			textVersion.setText("serialport init fail");
			setButtonClickable(buttonClear, false);
			setButtonClickable(buttonStart, false);
			setButtonClickable(buttonConnect, false);
			return ;
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//获取用户设置功率,并设置
		SharedPreferences shared = getSharedPreferences("power", 0);
		int value = shared.getInt("value", 26);
		Log.e("", "value" + value);
		reader.setOutputPower(value);
		Log.d(TAG, "asdas");
		
		//添加广播，默认屏灭时休眠，屏亮时唤醒
		screenReceiver = new ScreenStateReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(screenReceiver, filter);
		
		//定义自动变焦回调
		myAutoFocusCallback = new AutoFocusCallback() {
            
            public void onAutoFocus(boolean success, Camera camera) {
                // TODO Auto-generated method stub
                if(success) {	//success表示对焦成功
                	Log.i(TAG, "myAutoFocusCallback: success...");
                    //myCamera.setOneShotPreviewCallback(null);
                }
                else {
                    //未对焦成功
                    Log.i(TAG, "myAutoFocusCallback: 失败了...");
                }               
            }
        };
        
		//点击surface对焦
		surfaceView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!takePictureFlag) {
					camera.autoFocus(myAutoFocusCallback);
					Toast.makeText(getApplicationContext(), "对焦中",  
	                        Toast.LENGTH_SHORT).show(); 
				}
			}
		});
      		
		/**************************/
		
//		String serialNum = "";
//        try {.
//            Class<?> classZ = Class.forName("android.os.SystemProperties");
//            Method get = classZ.getMethod("get", String.class);
//            serialNum = (String) get.invoke(classZ, "ro.serialno");
//        } catch (Exception e) {
//        }
//        Log.e("serialNum", serialNum);
		
		/*************************/
		
		
		Thread thread = new InventoryThread();
		thread.start();
		//初始化声音池
		Util.initSoundPool(this);
		
		//打开相机
		SurfaceView surfaceView = (SurfaceView) this  
	            .findViewById(R.id.surfaceView);  
	    surfaceView.getHolder()  
	            .setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);  
	    surfaceView.getHolder().setFixedSize(1280, 720); //设置Surface分辨率  
	    surfaceView.getHolder().setKeepScreenOn(true);// 屏幕常亮  
	    surfaceView.getHolder().addCallback(new SurfaceCallback());//为SurfaceView的句柄添加一个回调函数\
	    
	    FileHandle.DeleteFolder(getCacheDir().getAbsolutePath() + "/pic"); //清除图片缓存
		        
		//假数据
	    List<EPC> list = new ArrayList<EPC>();
	    list.add(new EPC(1, "1234567890", 1));
	    addToList(list, "1");
	}
	
	private void initView(){
		buttonStart = (Button) findViewById(R.id.button_start);
		buttonConnect = (Button) findViewById(R.id.button_connect);
		buttonClear = (Button) findViewById(R.id.button_clear);
		listViewData = (ListView) findViewById(R.id.listView_data);
		textVersion = (TextView) findViewById(R.id.textView_version);
		surfaceView = (SurfaceView) findViewById(R.id.surfaceView);  
		btnTakePicture = (Button) findViewById(R.id.btn_take_picture);
		buttonStart.setOnClickListener(this);
		buttonConnect.setOnClickListener(this);
		buttonClear.setOnClickListener(this);
		btnTakePicture.setOnClickListener(this);
		setButtonClickable(buttonStart, false);
		listEPC = new ArrayList<EPC>();
		listViewData.setOnItemClickListener(this);
	}
	
	@Override
	protected void onPause() {
		startFlag = false;
		super.onPause();
	}
	
	/**
	 * 盘存线程
	 * @author Administrator
	 *
	 */
	class InventoryThread extends Thread{
		private List<byte[]> epcList;

		@Override
		public void run() {
			super.run();
			while(runFlag){
				if(startFlag){
//					reader.stopInventoryMulti()
					epcList = reader.inventoryRealTime(); //实时盘存
					if(epcList != null && !epcList.isEmpty()){
						//播放提示音
						Util.play(1, 0);
						for(byte[] epc:epcList){
							String epcStr = Tools.Bytes2HexString(epc, epc.length);
							addToList(listEPC, epcStr);
						}
					}
					epcList = null ;
					try {
						Thread.sleep(40);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	//将读取的EPC添加到LISTVIEW
		private void addToList(final List<EPC> list, final String epc){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					
					//第一次读入数据
					if(list.isEmpty()){
						EPC epcTag = new EPC();
						epcTag.setEpc(epc);
						epcTag.setCount(1);
						list.add(epcTag);
					}else{
						for(int i = 0; i < list.size(); i++){
							EPC mEPC = list.get(i);
							//list中有此EPC
							if(epc.equals(mEPC.getEpc())){
							mEPC.setCount(mEPC.getCount() + 1);
							list.set(i, mEPC);
							break;
						}else if(i == (list.size() - 1)){
							//list中没有此epc
							EPC newEPC = new EPC();
							newEPC.setEpc(epc);
							newEPC.setCount(1);
							list.add(newEPC);
							}
						}
					}
					//将数据添加到ListView
					listMap = new ArrayList<Map<String,Object>>();
					int idcount = 1;
					for(EPC epcdata:list){
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("ID", idcount);
						map.put("EPC", epcdata.getEpc());
						map.put("COUNT", epcdata.getCount());
						idcount++;
						listMap.add(map);
					}
					listViewData.setAdapter(new SimpleAdapter(MainActivity.this,
							listMap, R.layout.listview_item, 
							new String[]{"ID", "EPC", "COUNT"}, 
							new int[]{R.id.textView_id, R.id.textView_epc, R.id.textView_count}));
				}
			});
		}
		
	//设置按钮是否可用
	private void setButtonClickable(Button button, boolean flag){
		button.setClickable(flag);
		if(flag){
			button.setTextColor(Color.BLACK);
		}else{
			button.setTextColor(Color.GRAY);
		}
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(screenReceiver);
		runFlag = false;
		if(reader != null){
			reader.close();
		}
		super.onDestroy();
	}
	/**
	 * 清空listview
	 */
	private void clearData(){
		listEPC.removeAll(listEPC);
		listViewData.setAdapter(null);
	}
	

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.button_start:
			if(!startFlag){
				startFlag = true ;
				buttonStart.setText(R.string.stop_inventory);
			}else{
				startFlag = false;
				buttonStart.setText(R.string.inventory);
			}
			break;
		case R.id.button_connect:
			
			byte[] versionBytes = reader.getFirmware();
			if(versionBytes != null){
//				reader.setWorkArea(3);//设置成欧标
				Util.play(1, 0);
				String version = new String(versionBytes);
//				textVersion.setText(new String(versionBytes));
				setButtonClickable(buttonConnect, false);
				setButtonClickable(buttonStart, true);
			}
			setButtonClickable(buttonConnect, false);
			setButtonClickable(buttonStart, true);
			break;
			
		case R.id.button_clear:
			
			//////////////////////设置调制解调参数///////////////////////
//			reader.setOutputPower(value);
//			value -= 100;
//			reader.setRecvParam(mixer, if_g, values);
//			Log.e("", "value = " + value );
//			Toast.makeText(getApplicationContext(), "value = " + value, 0).show();
//			if(values < 864){
////				values = values + 64;
//			}else{
//				values = 432;
//			}
//			
//			if(if_g < 6){
//				if_g++;
//			}else{
//				if_g = 0;
//			}
//			
//			if(mixer < 7){
//				mixer++;
//			}else{
//				mixer = 0;
//			}
			
			clearData();
			break;
		case R.id.btn_take_picture:
			if (!takePictureFlag) {
				if (camera != null) {  
					camera.takePicture(null, null, new MyPictureCallback());
					btnTakePicture.setText("重拍");
					takePictureFlag = true;	//拍照标记
				}
			} else {
				FileHandle.deleteFile(picPath); //先清除上一张照片的缓存
				camera.startPreview(); // 重新开始预览  
				btnTakePicture.setText("拍照");
				takePictureFlag = false;	//拍照标记
			}
		default:
			break;
		}
	}
	 
	private int value = 2600;
//	private int values = 432 ;
//	private int mixer = 0;
//	private int if_g = 0;
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		TextView epcTextview = (TextView) view.findViewById(R.id.textView_epc);
		String epc = epcTextview.getText().toString();
		//选择EPC
//		reader.selectEPC(Tools.HexString2Bytes(epc));
		
		Toast.makeText(getApplicationContext(), epc, 0).show();
		Intent intent = new Intent(this, MoreHandleActivity.class);
		intent.putExtra("epc", epc);
		startActivity(intent);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
//		Log.e("", "adfasdfasdf");
//		Intent intent = new Intent(this, SettingActivity.class);
//		startActivity(intent);
		Intent intent = new Intent(this, SettingPower.class);
		startActivity(intent);
		return super.onMenuItemSelected(featureId, item);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
	}
	
	@Override
  	public boolean onMenuOpened(int featureId, Menu menu) {
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod(
							"setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) {
				}
			}
		}
	return super.onMenuOpened(featureId, menu);
	}
	
	/**
	 * 在actionbar上显示菜单按钮
	 */
	private void setOverflowShowingAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			menuKeyField.setAccessible(true);
			menuKeyField.setBoolean(config, false);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 提供一个静态方法，用于根据手机方向获得相机预览画面旋转的角度  
    public static int getPreviewDegree(Activity activity) {  
        // 获得手机的方向  
        int rotation = activity.getWindowManager().getDefaultDisplay()  
                .getRotation();  
        int degree = 0;  
        // 根据手机的方向计算相机预览画面应该选择的角度  
        switch (rotation) {  
        case Surface.ROTATION_0:  
            degree = 90;  
            break;  
        case Surface.ROTATION_90:  
            degree = 0;  
            break;  
        case Surface.ROTATION_180:  
            degree = 270;  
            break;  
        case Surface.ROTATION_270:  
            degree = 180;  
            break;  
        }  
        return degree;  
    }  
    
	private final class SurfaceCallback implements Callback {  
		  
        // 拍照状态变化时调用该方法  
        @Override  
        public void surfaceChanged(SurfaceHolder holder, int format, int width,  
                int height) {  
            parameters = camera.getParameters(); // 获取各项参数  
            parameters.setPictureFormat(PixelFormat.JPEG); // 设置图片格式  
            parameters.setPreviewSize(width, height); // 设置预览大小  
            parameters.setPreviewFrameRate(5);  //设置每秒显示4帧  
            parameters.setPictureSize(width, height); // 设置保存的图片尺寸  
            parameters.setJpegQuality(80); // 设置照片质量  
//            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
              
//            //查看可用的照片质量
//            List<Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes(); 
//            List<Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
//            String supportedPreviewSizesNum = "";
//            for(int i = 0; i < supportedPreviewSizes.size(); i++) {
//            	supportedPreviewSizesNum += supportedPreviewSizes.get(i).height + "*" + supportedPreviewSizes.get(i).width + "  ";
//            }
//            Log.d(TAG, supportedPreviewSizesNum);
        }  
  
        // 开始拍照时调用该方法  
        @Override  
        public void surfaceCreated(SurfaceHolder holder) {  
            try {  
                camera = Camera.open(); // 打开摄像头  
                camera.setPreviewDisplay(holder); // 设置用于显示拍照影像的SurfaceHolder对象  
                camera.setDisplayOrientation(getPreviewDegree(MainActivity.this));  //根据手机方向设置照片角度
                camera.startPreview(); // 开始预览  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
  
        }  
  
        // 停止拍照时调用该方法  
        @Override  
        public void surfaceDestroyed(SurfaceHolder holder) {  
            if (camera != null) {  
                camera.release(); // 释放照相机  
                camera = null;  
            }  
        }  
    }  
	
    private final class MyPictureCallback implements PictureCallback {  
    	  
        @Override  
        public void onPictureTaken(byte[] data, Camera camera) {  
            try {  
                bundle = new Bundle();  
                bundle.putByteArray("bytes", data); //将图片字节数据保存在bundle当中，实现数据交换
                picPath = FileHandle.saveToCache(data, getApplicationContext()); // 保存图片cache中  
                Toast.makeText(getApplicationContext(), "保存到：" + picPath, Toast.LENGTH_SHORT).show();
//                camera.startPreview(); // 拍完照后，重新开始预览  
  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
    }  
}
