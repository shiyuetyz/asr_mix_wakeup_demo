/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : ControlApplication.java
 * @ProjectName : vui_voicetv_mobile
 * @PakageName : cn.yunzhisheng.voicetv.mobile
 * @Author : Conquer
 * @CreateDate : 2014-3-19
 */
package cn.yunzhisheng.demo;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.widget.Toast;
import cn.yunzhisheng.demo.broadcast.INetWorkListen;
import cn.yunzhisheng.preference.PrivatePreference;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Conquer
 * @CreateDate : 2014-3-19
 * @ModifiedBy : Conquer
 * @ModifiedDate: 2014-3-19
 * @Modified:
 * 2014-3-19: 实现基本功能
 */
public class AppApplication extends Application {
	public static final String TAG = "AppApplication";
	
	
	public static INetWorkListen mINetWorkListen;
	public static boolean stateNum = false;
	public static String NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
	@Override
	public void onCreate() {
		super.onCreate();
		CrashHandler crasHandler = CrashHandler.getInstance();
		crasHandler.init(this);
		
		PrivatePreference.init(this);
		IntentFilter filter = new IntentFilter();
		filter.addAction(NET_CHANGE_ACTION);
		this.registerReceiver(mReceiver, filter);
	
	}


	public static void setListenet(INetWorkListen listener){
		mINetWorkListen = listener;
	}
	public static boolean getNetChanged(Context mContext){
		State wifiState = null;
		State mobileState = null;
		ConnectivityManager cm = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		if (wifiState != null && mobileState != null
				&& State.CONNECTED != wifiState
				&& State.CONNECTED == mobileState) {
			// 手机网络连接成功(2g,3g)
			
			Toast.makeText(mContext, "手机网络连接成功(2g,3g)", Toast.LENGTH_SHORT).show();
			stateNum = true;
		} else if (wifiState != null && mobileState != null
				&& State.CONNECTED != wifiState
				&& State.CONNECTED != mobileState) {
			// 手机没有任何的网络
			
			Toast.makeText(mContext, "当前无网络链接,无法识别！", Toast.LENGTH_SHORT).show();
			stateNum = false;
		} else if (wifiState != null && State.CONNECTED == wifiState) {
			// 无线网络连接成功
			
			Toast.makeText(mContext, "无线网络连接成功！", Toast.LENGTH_SHORT).show();
			stateNum = true;
		}
		return stateNum;	
	}
	

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (NET_CHANGE_ACTION.equals(action)) {
				if(mINetWorkListen != null){
					mINetWorkListen.onConectChange();
				}
			}
		}
	};


}
