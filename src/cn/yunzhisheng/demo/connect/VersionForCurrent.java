package cn.yunzhisheng.demo.connect;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import cn.yunzhisheng.demo.R;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public class VersionForCurrent {
	public static final String appPackName = "cn.yunzhisheng.demo";

	public static int getVerCode(Context context) throws NameNotFoundException {
		int verCode = -1;
		verCode = context.getPackageManager().getPackageInfo(appPackName, 0).versionCode;
		return verCode;

	}

	public static String getVerName(Context context) {

		String verName = "";

		try {
			verName = context.getPackageManager()
					.getPackageInfo(appPackName, 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return verName;
	}

	public static String getAppName(Context context) {
		String appName = context.getResources().getText(R.string.app_name)
				.toString();
		return appName;
	}
	
	private boolean getServerVersion(){
		
		try {
			String newVersionJson = VersionForServer.getVersionForserver("path"+"appsersion");
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
}
