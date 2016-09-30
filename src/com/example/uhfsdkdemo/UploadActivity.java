package com.example.uhfsdkdemo;

import java.util.HashMap;
import java.util.Map;

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

import com.example.uhfsdkdemo.history.DBEPC;
import com.example.uhfsdkdemo.history.DateUtils;
import com.example.uhfsdkdemo.history.UHFDBManager;

public class UploadActivity extends Activity implements OnClickListener{
	
	private ImageView imageView;
	private TextView tvEPC;
	private Button btnBack;
	private Button btnUpload;
	private ProgressDialog progressDialog;
	private Bundle bundle;
	private String picPath;
	private Bitmap bitmap;
	private final String POST_URL = "http://192.168.1.156:9090/upload/"; 
	
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
	
//	private Response postRequest() throws IOException {
//		File file = new File(picPath);
//		OkHttpClient mOkHttpClient = new OkHttpClient();
//		RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
////		RequestBody requestBody = new MultipartBuilder()
////		     .type(MultipartBuilder.FORM)
////		     .addPart(Headers.of(
////		          "Content-Disposition", 
////		              "form-data; name=\"epc\""), 
////		          RequestBody.create(null, Data.getChooseEPC()))
////		     .addPart(Headers.of(
////		         "Content-Disposition", 
////		         "form-data; name=\"mFile\";filename=\"" + Data.getChooseEPC() + ".jpg\""), fileBody)
////		     .build();
//		Request request = new Request.Builder()
//		    .url("http://192.168.1.161/epc/")
////		    .post(requestBody)
//		    .build();
//
//		Call call = mOkHttpClient.newCall(request);
//		Response response = call.execute();
//		return response;
//	}

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
				progressDialog = ProgressDialog.show(this, null, "正在上传", false, false);
				new UploadAsyncTask().execute();
			}
			break;
			
		default:
			break;
		}
	}
	
	private class UploadAsyncTask extends AsyncTask<Void, Void, String> {
		
		private UHFDBManager uhfDBManager;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			uhfDBManager = new UHFDBManager(UploadActivity.this);
		}
		
		@Override
		protected String doInBackground(Void... params) {
//			try {
//				Response response = postRequest();
//				return response;
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			List<BasicNameValuePair> params2 = new LinkedList<BasicNameValuePair>();
//            params2.add(new BasicNameValuePair("epc", "111"));
//            String jsonString = HttpRequestUtil.postRequest("http://192.168.1.161/epc/", params2);
			Map<String, String> paramsMap = new HashMap<String, String>();
			paramsMap.put("epc", Data.getChooseEPC());
			Map<String, String> fileMap = new HashMap<String, String>();  
			fileMap.put("pic", picPath);
			String jsonString = HttpRequestUtil.postRequestWithFile(POST_URL, paramsMap, fileMap);
			return jsonString;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			String resultString = null;
			try {
				resultString = result;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(resultString != null) {
				progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), "上传成功", Toast.LENGTH_SHORT).show();
				Log.d("cyn", resultString + "1");
				
				//上传成功写入数据库
				uhfDBManager.add(new DBEPC(Data.getChooseEPC(), picPath, DateUtils.getCurrentTimestamp()));
				uhfDBManager.closeDB();
				
				finish();
			}
		}
	}
	
}
