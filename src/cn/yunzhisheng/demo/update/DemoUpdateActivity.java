package cn.yunzhisheng.demo.update;


import cn.yunzhisheng.demo.R;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

//public class DemoUpdateActivity extends Activity implements OnClickListener{
//
//	public static final String TAG = "AssistantUpdateActivity";
//	
//	public final static int VERSION_CHECK_TIME_OUT = 8000;
//	//code:1000;message:您当前已经是最新版本了！
//	public final static int VERSION_LATEST = 1000;
//	//code:-101008;message:没有指定更新文件，请先检测更新
//	public final static int VERSION_FILE_NOT_FOUND = -101008;
//	
//	private ImageView mBack;
//	private Button mAssistantUpdate;
//	private Button mVersionCheck;
//	private TextView mVersionLog;
//	
//	private TextView mAssistantNewestVersion;
//	private TextView mAssistantNewVersionLog;
//	private TextView mAssistantVersionCode;
//	
//	private UpdateService mService;
//	
//	private SharedPreferences mSP;
//	private SharedPreferences.Editor mEditor;
//	
//	private BroadcastReceiver mReceiver = new BroadcastReceiver(){
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			mHandler.removeMessages(0);
//			String action = intent.getAction();
//			if(ConnectService.ACTION_SERVER_UPDATE_INFO.equals(action)){
//				//判断服务端有无更新
//				boolean hasNew = intent.getBooleanExtra(SessionPreference.KEY_HAS_NEW, false);
//				String oldCode = intent.getStringExtra(SessionPreference.KEY_OLD_CODE);
//				Log.d(TAG, "hasNew : "+hasNew+" oldCode : "+oldCode);
//				mEditor.putString("assistantOldCode", oldCode);
//				if(hasNew){
//					String newCode = intent.getStringExtra(SessionPreference.KEY_NEW_CODE);
//					String versionLog = intent.getStringExtra(SessionPreference.KEY_LOG);
//					mAssistantNewestVersion.setText("最新版本 "+newCode);
//					mAssistantNewestVersion.setTextColor(getResources().getColor(R.color.version_info));
//					mAssistantNewVersionLog.setText(versionLog);
//					mVersionLog.setVisibility(View.VISIBLE);
//					mAssistantUpdate.setVisibility(View.VISIBLE);
//					mAssistantNewVersionLog.setVisibility(View.GONE);
//				}else{
//					//当前服务器已是最新版本
//					mAssistantNewestVersion.setText(getResources().getString(R.string.newest_version));
//					mAssistantNewestVersion.setTextColor(getResources().getColor(R.color.current_version_newest));
//					mVersionLog.setVisibility(View.GONE);
//					mAssistantNewVersionLog.setVisibility(View.GONE);
//					mAssistantUpdate.setVisibility(View.GONE);
//				}
//				mAssistantVersionCode.setText(oldCode);
//			}
//		}
//		
//	};
//	private Handler mHandler = new Handler(){
//		public void handleMessage(android.os.Message msg) {
//			switch (msg.what) {
//			case 0:
//				mAssistantNewestVersion.setText(getResources().getString(R.string.check_version_time_out));
//				mAssistantNewestVersion.setTextColor(getResources().getColor(R.color.current_version_newest));
//				String oldCode = mSP.getString("assistantOldCode", "");
//				Log.d(TAG, "history  oldCode : "+oldCode);
//				//如果之前获得过记录，则用历史记录
//				if(TextUtils.isEmpty(oldCode)){
//					mAssistantVersionCode.setText("");
//				}else{
//					mAssistantVersionCode.setText(oldCode);
//				}
//				mVersionLog.setVisibility(View.GONE);
//				mAssistantNewVersionLog.setVisibility(View.GONE);
//				mAssistantUpdate.setVisibility(View.GONE);
//				break;
//			}
//		};
//	};
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.demo_update);
//		mSP = getSharedPreferences("connect_host", 0);
//		mEditor = mSP.edit();
//		initView();
//		initReceiver();
//		mService = ConnectService.getService();
//		mService.doSendUpdateDataToServer();
//		mHandler.sendEmptyMessageDelayed(0, VERSION_CHECK_TIME_OUT);
//	}
//	private void initReceiver(){
//		IntentFilter filter = new IntentFilter();
//		filter.addAction(ConnectService.ACTION_SERVER_UPDATE_INFO);
//		registerReceiver(mReceiver, filter);
//	}
//	
//	private void initView(){
//		mBack = (ImageView)findViewById(R.id.tv_version_update_back);
//		mAssistantUpdate = (Button)findViewById(R.id.btn_assistant_update);
//		mAssistantUpdate.setVisibility(View.GONE);
//		mVersionCheck = (Button)findViewById(R.id.btn_assistant_check);
//		mVersionLog = (TextView)findViewById(R.id.assistant_version_log);
//		mVersionLog.setVisibility(View.GONE);
//		
//		mBack.setOnClickListener(this);
//		mAssistantUpdate.setOnClickListener(this);
//		mVersionCheck.setOnClickListener(this);
//		mVersionLog.setOnClickListener(this);
//		
//		mAssistantNewestVersion = (TextView)findViewById(R.id.assistant_newest_version);
//		mAssistantNewVersionLog = (TextView)findViewById(R.id.assistant_new_version_log);
//		mAssistantVersionCode = (TextView)findViewById(R.id.assistant_version_code);
//	}
//
//	@Override
//	public void onClick(View v) {
//		// TODO Auto-generated method stub
//		switch (v.getId()) {
//		case R.id.tv_version_update_back:
//			this.finish();
//			break;
//		case R.id.btn_assistant_update:
//			if(mService != null){
//				mService.doSendDownloadUpdateToServer();
//			}
//			break;
//		case R.id.btn_assistant_check:
//			if(mService != null){
//				mService.doSendUpdateDataToServer();
//				mAssistantNewestVersion.setTextColor(getResources().getColor(android.R.color.white));
//				mAssistantNewestVersion.setText("正在检查版本...");
//				mVersionLog.setVisibility(View.GONE);
//				mAssistantNewVersionLog.setVisibility(View.GONE);
//				mAssistantUpdate.setVisibility(View.GONE);
//			}
//			break;
//		case R.id.assistant_version_log:
//			if(mAssistantNewVersionLog.getVisibility() == View.VISIBLE){
//				mAssistantNewVersionLog.setVisibility(View.GONE);
//				Drawable dra = getResources().getDrawable(R.drawable.show_log);
//				dra.setBounds(0, 0, dra.getMinimumWidth(), dra.getMinimumHeight());
//				mVersionLog.setCompoundDrawables(null, null, dra, null);
//			}else{
//				mAssistantNewVersionLog.setVisibility(View.VISIBLE);
//				Drawable dra = getResources().getDrawable(R.drawable.hide_log);
//				dra.setBounds(0, 0, dra.getMinimumWidth(), dra.getMinimumHeight());
//				mVersionLog.setCompoundDrawables(null, null, dra, null);
//			}
//			break;
//		}
//	}
//
//
//}
