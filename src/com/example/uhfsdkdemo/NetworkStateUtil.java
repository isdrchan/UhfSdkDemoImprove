package com.example.uhfsdkdemo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkStateUtil {
	
	public static boolean checkNetworkState(Context context) {  
	    if (context != null) {  
	        ConnectivityManager mConnectivityManager = (ConnectivityManager) context  
	                .getSystemService(Context.CONNECTIVITY_SERVICE);  
	        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();  
	        if (mNetworkInfo != null) {  
	            return mNetworkInfo.isAvailable();  
	        }  
	    }  
	    return false;  
	}
	
	public static Boolean checkNetworkStateAndShowAlert(final Context context) {
		//当前网络连接状态
		boolean networkAvailable = checkNetworkState(context);
		if(!networkAvailable) {
			//当前无网络连接
			Toast.makeText(context, "当前无网络连接，请检查您的网络配置", Toast.LENGTH_SHORT).show();
		}
		//返回网络连接状态
		return networkAvailable;
	}
}