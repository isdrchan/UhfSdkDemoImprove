package com.example.uhfsdkdemo;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class CameraActivity extends Activity implements OnClickListener{
	
	private Button btnBack;
	private Button btnTakePictureAndNext;
	private Camera camera; 
	private Camera.Parameters parameters = null;  
	private Bundle bundle = null; // 声明一个Bundle对象，用来存储数据  
	private String picPath;
	private AutoFocusCallback myAutoFocusCallback = null;	//自动变焦回调
	private boolean takePictureFlag = false;	//Camera.startPreview()之后，拍照Camera.takePicture()之前标记为flase0
	private static final String TAG = "cyn";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_camera);
		
		btnBack = (Button) findViewById(R.id.btn_back_camera);
		btnTakePictureAndNext = (Button) findViewById(R.id.btn_take_picture_and_next_camera);
		
		//打开相机
		SurfaceView surfaceView = (SurfaceView) this  
	            .findViewById(R.id.surfaceView);  
	    surfaceView.getHolder()  
	            .setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);  
	    surfaceView.getHolder().setFixedSize(1280, 720); //设置Surface分辨率  
	    surfaceView.getHolder().setKeepScreenOn(true);// 屏幕常亮  
	    surfaceView.getHolder().addCallback(new SurfaceCallback());//为SurfaceView的句柄添加一个回调函数
	    
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
          
  		//点击事件绑定
  		surfaceView.setOnClickListener(this);
  		btnBack.setOnClickListener(this);
  		btnTakePictureAndNext.setOnClickListener(this);
	    
	    //清除图片缓存
	    FileUtil.DeleteFolder(getCacheDir().getAbsolutePath() + "/pic"); 
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
                camera.setDisplayOrientation(getPreviewDegree(CameraActivity.this));  //根据手机方向设置照片角度
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
                picPath = FileUtil.saveToCache(data, getApplicationContext()); // 保存图片cache中  
                Toast.makeText(getApplicationContext(), "保存到：" + picPath, Toast.LENGTH_SHORT).show();
//                camera.startPreview(); // 拍完照后，重新开始预览  
  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back_camera:
			if(!takePictureFlag) {
				finish();
			} else {
				FileUtil.deleteFile(picPath); //先清除上一张照片的缓存
				camera.startPreview(); // 重新开始预览  
				btnTakePictureAndNext.setText("拍照");
				takePictureFlag = false;	//拍照标记
				btnBack.setText("上一步");
			}
			break;
			
		case R.id.btn_take_picture_and_next_camera:
			if (!takePictureFlag) {
				if (camera != null) {  
					camera.takePicture(null, null, new MyPictureCallback());
					btnTakePictureAndNext.setText("下一步");
					btnBack.setText("重新拍照");
					takePictureFlag = true;	//拍照标记
				}
			} else {
				FileUtil.deleteFile(picPath); //先清除上一张照片的缓存
				camera.startPreview(); // 重新开始预览  
				btnTakePictureAndNext.setText("拍照");
				takePictureFlag = false;	//拍照标记
			}
			break;
			
		case R.id.surfaceView:
			//点击surface对焦
			if(!takePictureFlag) {
				camera.autoFocus(myAutoFocusCallback);
				Toast.makeText(getApplicationContext(), "对焦中",  
                        Toast.LENGTH_SHORT).show(); 
			}
			break;
			
		default:
			break;
		}
	} 
	
}
