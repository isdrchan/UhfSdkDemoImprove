package com.example.uhfsdkdemo.history;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.example.uhfsdkdemo.R;

public class PictureActivity extends Activity {
	
	private ImageView imgv;
	private Button btnBack;
	private Intent intent;
	private String picPath;
	private Bitmap bitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_picture);
		
		intent = getIntent();
		picPath = intent.getStringExtra("picPath");
		
		imgv = (ImageView) findViewById(R.id.imgv_picture);
		btnBack = (Button) findViewById(R.id.btn_back_picture);
		
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		if(picPath != null) {
			bitmap = BitmapFactory.decodeFile(picPath);
			imgv.setImageBitmap(bitmap);
		}
	}
}
