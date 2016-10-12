package com.example.uhfsdkdemo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.hdhe.uhf.reader.Tools;
import com.android.hdhe.uhf.reader.UhfReader;
import com.example.uhfsdkdemo.history.HistoryActivity;

public class MainActivity extends Activity implements OnClickListener ,OnItemClickListener{

	private Button buttonClear;
	private Button buttonConnect;
	private Button buttonStart;
	private Button btnNext;
	private Button btnHistory;
	private TextView textVersion ;
	private ListView listViewData;
	private TextView tvListViewEmpty;
	private ArrayList<EPC> listEPC;
	private ArrayList<Map<String, Object>> listMap;
	private boolean runFlag = true;
	private boolean startFlag = false;
	private boolean connectFlag = false;
	private UhfReader reader ; //超高频读写器 
	private ScreenStateReceiver screenReceiver;
	private String actionBarTitle;
	private int[] itemClickTime = new int[]{-1, 0};	//标记item的点击次数
	private static final String TAG = "cyn";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setOverflowShowingAlways(); //actionbar显示菜单
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
		        
		//假数据
//	    List<EPC> list = new ArrayList<EPC>();
//	    list.add(new EPC(1, "1234567890", 1));
//	    addToList(list, "1");
		
		//自动触发连接模块按钮事件
	    buttonConnect.performClick();
	    
	    //自动触发扫描
	    if(!startFlag) buttonStart.performClick();
	}
	
	private void initView(){
		actionBarTitle = "UHF(厂商)";
		buttonStart = (Button) findViewById(R.id.button_start);
		buttonConnect = (Button) findViewById(R.id.button_connect);
		buttonClear = (Button) findViewById(R.id.button_clear);
		btnNext = (Button) findViewById(R.id.btn_next_main);
		listViewData = (ListView) findViewById(R.id.listView_data);
		textVersion = (TextView) findViewById(R.id.textView_version);
		btnHistory = (Button) findViewById(R.id.btn_history_main);
		tvListViewEmpty = (TextView) findViewById(R.id.tv_listView_empty);
		buttonStart.setOnClickListener(this);
		buttonConnect.setOnClickListener(this);
		buttonClear.setOnClickListener(this);
		btnNext.setOnClickListener(this);
		setButtonClickable(buttonStart, false);
		listEPC = new ArrayList<EPC>();
		listViewData.setOnItemClickListener(this);
		btnHistory.setOnClickListener(this);
		
		//listView为空显示提示信息
		listViewData.setEmptyView(tvListViewEmpty);
	}
	
	@Override
	protected void onPause() {
//		startFlag = false;
		super.onPause();
		if(startFlag) buttonStart.performClick();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//上传成功则清除listview记录
		if(Data.getUploadFlag()) {
			Data.setUploadFlag(false);
			buttonClear.performClick();
		}
		if(!startFlag) buttonStart.performClick();
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
//						Util.play(1, 0);
						for(byte[] epc:epcList) {
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
					//第一次读到播放声音
					Util.play(1, 2);
					
					//不计算count,epc更新才添加进列表
					ListViewAdapter listViewAdapter = new ListViewAdapter(MainActivity.this, list);
					listViewData.setAdapter(listViewAdapter);
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
							//第一次读到播放声音
							Util.play(1, 2);
							
							//不计算count,epc更新才添加进列表
							ListViewAdapter listViewAdapter = new ListViewAdapter(MainActivity.this, list);
							listViewData.setAdapter(listViewAdapter);
						}
					}
				}
				//将数据添加到ListView
//				listMap = new ArrayList<Map<String,Object>>();
//				int idcount = 1;
//				for(EPC epcdata:list){
//					Map<String, Object> map = new HashMap<String, Object>();
//					map.put("ID", idcount);
//					map.put("EPC", epcdata.getEpc());
//					map.put("COUNT", epcdata.getCount());
//					idcount++;
//					listMap.add(map);
//				}
//				listViewData.setAdapter(
//						new SimpleAdapter(MainActivity.this,
//						listMap, R.layout.listview_item, 
//						new String[]{"ID", "EPC", "COUNT"}, 
//						new int[]{R.id.textView_id, R.id.textView_epc, R.id.textView_count}
//				));
				
				//将数据添加到ListView
				int idcount = 1;
				for(EPC epcdata:list){
					epcdata.setId(idcount);
					idcount++;
				}
//				ListViewAdapter listViewAdapter = new ListViewAdapter(MainActivity.this, list);
//				listViewData.setAdapter(listViewAdapter);
				
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
				startFlag = true;
				buttonStart.setText(R.string.stop_inventory);
				getActionBar().setTitle(actionBarTitle + "          状态:正在扫描...");
			}else{
				startFlag = false;
				buttonStart.setText(R.string.inventory);
				getActionBar().setTitle(actionBarTitle + "          状态:停止");
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
			btnNext.setEnabled(false);	//设置下一步为不可用状态
			clearData();
			break;
			
		case R.id.btn_next_main:
			Intent intent = new Intent(MainActivity.this, CameraActivity.class);
			startActivity(intent);
			break;
			
		case R.id.btn_history_main:
			Intent historyIntent = new Intent(MainActivity.this, HistoryActivity.class);
			startActivity(historyIntent);
			break;
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
//		TextView epcTextview = (TextView) view.findViewById(R.id.textView_epc);
//		String epc = epcTextview.getText().toString();
//		//选择EPC
////		reader.selectEPC(Tools.HexString2Bytes(epc));
//		Toast.makeText(getApplicationContext(), epc, 0).show();
//		Intent intent = new Intent(this, MoreHandleActivity.class);
//		intent.putExtra("epc", epc);
//		startActivity(intent);
		
		//触发radioButton的点击事件
		RadioButton radio= (RadioButton) view.findViewById(R.id.rb);
		radio.performClick();
		
		//设置下一步为可用
		btnNext.setEnabled(true);
//		
//		//点击两次进入MoreHandleActivity
//		if(itemClickTime[0] == position) {
//			itemClickTime[1] += 1; 
//		} else {
//			itemClickTime[0] = position;
//			itemClickTime[1] = 1;
//		}
//		if(itemClickTime[1] == 2) {
//			itemClickTime[1] = 0;	//计数器置0
//			TextView epcTextview = (TextView) view.findViewById(R.id.textView_epc);
//			String epc = epcTextview.getText().toString();
//			Toast.makeText(getApplicationContext(), "epc: " + epc, 0).show();
//			Intent intent = new Intent(this, MoreHandleActivity.class);
//			intent.putExtra("epc", epc);
//			startActivity(intent);
//		} else {
//			Toast.makeText(getApplicationContext(), "再点一次进入MoreHandleActivity", 0).show();
//		}
		
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
	
}
