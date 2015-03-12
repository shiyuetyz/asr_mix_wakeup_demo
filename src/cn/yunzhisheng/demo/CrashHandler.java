/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName	: CrashHandler.java
 * @ProjectName	: vui_voicetv_mobile
 * @PakageName	: cn.yunzhisheng.voicetv.mobile
 * @Author		: Conquer
 * @CreateDate	: 2014-3-19
 */
package cn.yunzhisheng.demo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.yunzhisheng.common.FileTool;
import cn.yunzhisheng.demo.file.Filesize;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

/**
 * @Module		: 隶属模块名
 * @Comments	: 描述
 * @Author		: Conquer
 * @CreateDate	: 2014-3-19
 * @ModifiedBy	: Conquer
 * @ModifiedDate: 2014-3-19
 * @Modified: 
 * 2014-3-19: 实现基本功能
 */
public class CrashHandler implements UncaughtExceptionHandler {
	public static final String TAG = "CrashHandler";

	//Log文件
	private static String PATH ="";  
    private static final String FILE_NAME = "crash.txt";  
  
	
	private static CrashHandler crashHandler;
	
	private Thread.UncaughtExceptionHandler defaultExceptionHandler;
	
	private Context mContext;
	private CrashHandler(){
	}
	
	public static CrashHandler getInstance(){
		if(crashHandler == null){
			crashHandler = new CrashHandler();
		}
		return crashHandler;
	}
	
	public void init(Context context){
		//获取系统默认的异常处理器
		defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		//将当前实例设为系统默认的异常处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
		mContext = context.getApplicationContext();
		
	    if(FileTool.isSDCardExists() &&  Filesize.getSDAvailableSize() >= 100){//sd卡
	    	PATH = Environment.getExternalStorageDirectory().getPath() + "/YunZhiShengDemo/crash/";
		} else if(Filesize.getRomAvailableSize() >= 100){//手机内存
			PATH = mContext.getFilesDir().getAbsolutePath() + "/YunZhiShengDemo/crash/";
		} else{
			Toast.makeText(mContext, "手机SD卡或内存不足,无法口语评测", Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub
		try {
			//在SD卡生成Log日志
			dumpExceptionToSDCard(ex);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ex.printStackTrace();
		
		//如果系统提供了默认的异常处理器，则交给系统去结束程序，否则自己做
		if(defaultExceptionHandler != null){
			defaultExceptionHandler.uncaughtException(thread, ex);
		}else{
			Process.killProcess(Process.myPid());
		}
	}
	
	private void dumpExceptionToSDCard(Throwable ex) throws IOException {  
        //判断SD是否存在  
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  
                Log.w(TAG, "sdcard unmounted,skip dump exception");  
                return;  
        }  
  
        File dir = new File(PATH);  
        if (!dir.exists()) {  
            dir.mkdirs();  
        }  
        long current = System.currentTimeMillis();  
        String time = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date(current))+"_";  
        //以当前时间创建log文件  
        File file = new File(PATH + FILE_NAME);  
        try {  
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file,true)));  
            //导出发生异常的时间  
            pw.println(time);  
            //导出手机信息  
            dumpPhoneInfo(pw);  
  
            pw.println();  
            //导出异常的调用栈信息  
            ex.printStackTrace(pw);  
  
            pw.close();  
        } catch (Exception e) {  
            Log.e(TAG, "dump crash info failed");  
        }  
    }  
	  private void dumpPhoneInfo(PrintWriter pw) throws NameNotFoundException {  
	        //应用的版本名称和版本号  
	        PackageManager pm = mContext.getPackageManager();  
	        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);  
	        pw.print("App Version: ");  
	        pw.print(pi.versionName);  
	        pw.print('_');  
	        pw.println(pi.versionCode);  
	  
	        //android版本号  
	        pw.print("OS Version: ");  
	        pw.print(Build.VERSION.RELEASE);  
	        pw.print("_");  
	        pw.println(Build.VERSION.SDK_INT);  
	  
	        //手机制造商  
	        pw.print("Vendor: ");  
	        pw.println(Build.MANUFACTURER);  
	  
	        //手机型号  
	        pw.print("Model: ");  
	        pw.println(Build.MODEL);  
	  
	        //cpu架构  
	        pw.print("CPU ABI: ");  
	        pw.println(Build.CPU_ABI);  
	    }  
}
