package com.example.uhfsdkdemo;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class UploadActivity extends Activity implements OnClickListener{
	
	private ImageView imageView;
	private TextView tvEPC;
	private Button btnBack;
	private Button btnUpload;
	private ProgressDialog progressDialog;
	private Bundle bundle;
	private String picPath;
	private Bitmap bitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_upload);
		
		imageView = (ImageView) findViewById(R.id.imgv_upload);
		tvEPC = (TextView) findViewById(R.id.tv_epc_upload);
		btnBack = (Button) findViewById(R.id.btn_back_upload);
		btnUpload = (Button) findViewById(R.id.btn_upload_upload);
		
		btnBack.setOnClickListener(this);
		btnUpload.setOnClickListener(this);
				
		//获取图片路径
		bundle = this.getIntent().getExtras();
		picPath = bundle.getString("picPath");
		
		//加载图片资源到view
		bitmap = BitmapFactory.decodeFile(picPath);
//		bitmap = BitmapUtil.rotaingImageView(90, bitmap);  //把图片旋转为正的方向 
		imageView.setImageBitmap(bitmap);
		tvEPC.setText(Data.getChooseEPC());
		
	}
	
	private Response postRequest() throws IOException {
		File file = new File(picPath);
		OkHttpClient mOkHttpClient = new OkHttpClient();
		RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
		RequestBody requestBody = new MultipartBuilder()
		     .type(MultipartBuilder.FORM)
		     .addPart(Headers.of(
		          "Content-Disposition", 
		              "form-data; name=\"epc\""), 
		          RequestBody.create(null, Data.getChooseEPC()))
		     .addPart(Headers.of(
		         "Content-Disposition", 
		         "form-data; name=\"mFile\";filename=\"" + Data.getChooseEPC() + ".jpg\""), fileBody)
		     .build();
		Request request = new Request.Builder()
		    .url("http://baidu.com")
		    .post(requestBody)
		    .build();

		Call call = mOkHttpClient.newCall(request);
		Response response = call.execute();
		return response;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.btn_back_upload:
			FileUtil.deleteFile(picPath); //先清除上照片缓存
			finish();
			Intent intent = new Intent(UploadActivity.this, CameraActivity.class);
			startActivity(intent);
			break;
			
		case R.id.btn_upload_upload:
			if(NetworkStateUtil.checkNetworkStateAndShowAlert(UploadActivity.this)) {
				progressDialog = ProgressDialog.show(this, null, "正在上传", false, true);
			}
			new UploadAsyncTask().execute();
			break;
			
		default:
			break;
		}
	}
	
	private class UploadAsyncTask extends AsyncTask<Void, Void, Response> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected Response doInBackground(Void... params) {
			try {
				Response response = postRequest();
				return response;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Response result) {
			super.onPostExecute(result);
			String resultString = null;
			try {
				resultString = result.body().string();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(resultString != null) {
				progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), "上传成功", Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}
	
}
