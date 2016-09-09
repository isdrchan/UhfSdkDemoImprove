package com.example.uhfsdkdemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class UploadActivity extends Activity {
	
	private ImageView imageView;
	private TextView tvEPC;
	private Bundle bundle;
	private String picPath;
	private Bitmap bitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_upload);
		
		imageView = (ImageView) findViewById(R.id.imgv_upload);
		tvEPC = (TextView) findViewById(R.id.tv_epc_upload);
				
		//获取图片路径
		bundle = this.getIntent().getExtras();
		picPath = bundle.getString("picPath");
		
		//加载图片资源到view
		bitmap = BitmapFactory.decodeFile(picPath);
		bitmap = BitmapUtil.rotaingImageView(90, bitmap);  //把图片旋转为正的方向 
		imageView.setImageBitmap(bitmap);
		tvEPC.setText(Data.getChooseEPC());
	}
	
}
