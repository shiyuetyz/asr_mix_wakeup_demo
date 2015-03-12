package cn.yunzhisheng.demo.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.widget.Toast;

public class MyBroadcastRecevier extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		// TODO Auto-generated method stub
		State wifiState = null;
		State mobileState = null;
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		if (wifiState != null && mobileState != null
				&& State.CONNECTED != wifiState
				&& State.CONNECTED == mobileState) {
			// 手机网络连接成功(2g,3g)
			
			Toast.makeText(context, "手机网络连接成功(2g,3g)", Toast.LENGTH_SHORT).show();
			
		} else if (wifiState != null && mobileState != null
				&& State.CONNECTED != wifiState
				&& State.CONNECTED != mobileState) {
			// 手机没有任何的网络
			
			Toast.makeText(context, "无线网络连接,无法进行识别！", Toast.LENGTH_SHORT).show();
			
		} else if (wifiState != null && State.CONNECTED == wifiState) {
			// 无线网络连接成功
			
			Toast.makeText(context, "无线网络连接成功！", Toast.LENGTH_SHORT).show();
		}

	}

}
