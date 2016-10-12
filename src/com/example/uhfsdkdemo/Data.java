package com.example.uhfsdkdemo;

public class Data {
	
	private static String chooseEPC;
	private static Boolean uploadFlag = false;
	
	public static String getChooseEPC() {
		return chooseEPC;
	}
	
	public static void setChooseEPC(String chooseEPC) {
		Data.chooseEPC = chooseEPC;
	}
	
	public static Boolean getUploadFlag() {
		return uploadFlag;
	}
	
	public static void setUploadFlag(Boolean uploadFlag) {
		Data.uploadFlag = uploadFlag;
	}
	
}
