package com.example.uhfsdkdemo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;

public class FileUtil {
	
	/** 
     * 将拍下来的照片存放在Cache中 ,返回文件路径
     * @param data   
     * @throws IOException 
     */  
    public static String saveToCache(byte[] data, Context context) throws IOException {  
        Date date = new Date();  
        
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间  
        String filename = format.format(date) + ".jpg";  
//        File fileFolder = new File(Environment.getExternalStorageDirectory()  
//                + "/pic/");  
        //获取Cache目录
        File cachedir = new File(context.getCacheDir().getAbsolutePath() + "/pic/");
//        Log.e(TAG, "cachedir " + cachedir.getAbsolutePath());
        if (!cachedir.exists()) { // 如果目录不存在，则创建一个名为"pic"的目录  
        	cachedir.mkdir();  
        }  
        
        //DataSaveToCache
//        File jpgFile = new File(cachedir, filename);
//        FileOutputStream outputStream = new FileOutputStream(jpgFile); // 文件输出流  
//        outputStream.write(data); // 写入Cache中  
//        outputStream.close(); // 关闭输出流  
        
        //转换到bitmap，旋转90度，再输出到文件
        Bitmap bitmap = BitmapUtil.Bytes2Bitmap(data);
        File jpgFile = new File(cachedir, filename);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(jpgFile));    
        bitmap = BitmapUtil.rotaingImageView(90, bitmap);  //把图片旋转为正的方向 
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();    
        bos.close(); 
        
        return context.getCacheDir().getAbsolutePath() + "/pic/" + filename;
    }  
    
    
	/**
     * 删除单个文件
     * @param   filePath    被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {
    File file = new File(filePath);
        if (file.isFile() && file.exists()) {
        return file.delete();
        }
        return false;
    }

    /**
     * 删除文件夹以及目录下的文件
     * @param   filePath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String filePath) {
    	boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
            //删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
            //删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前空目录
        return dirFile.delete();
    }

    /**
     *  根据路径删除指定的目录或文件，无论存在与否
     *@param filePath  要删除的目录或文件
     *@return 删除成功返回 true，否则返回 false。
     */
    public static boolean DeleteFolder(String filePath) {
    File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
            // 为文件时调用删除文件方法
                return deleteFile(filePath);
            } else {
            // 为目录时调用删除目录方法
                return deleteDirectory(filePath);
            }
        }
    }

}
