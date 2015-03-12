package cn.yunzhisheng.demo.ui;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.yunzhisheng.common.util.ErrorUtil;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.demo.R;
import cn.yunzhisheng.demo.update.IUpdateListener;
import cn.yunzhisheng.demo.update.IUpdateOperate;
import cn.yunzhisheng.demo.update.UpdateDoing;
import cn.yunzhisheng.demo.update.UpdateManager;

public class MainActivity extends Activity implements OnClickListener {
	private Intent mIntent;

	private static final String TAG = "MainActivity";
	private String mobileVersionCode = "获取版本号失败";
	public final static int VERSION_CHECK_TIME_OUT = 5000;
	// code:1000;message:您当前已经是最新版本了！
	public final static int VERSION_LATEST = 1000;
	// code:-101008;message:没有指定更新文件，请先检测更新
	public final static int VERSION_FILE_NOT_FOUND = -101008;
	
	private final static boolean ISNEW_VERSION = false;

	private LinearLayout mChineseLinearLayout;
	private LinearLayout mEnglishLinearLayout;
	private LinearLayout mContoneseLinearLayout;
	private LinearLayout mTtsLinearLayout;
	private LinearLayout mOralLinearLayout;
	private LinearLayout mWakeupLinearLayout;
	private LinearLayout mVoiceprintLinearLayout;
	private LinearLayout mOralOnLinearLayout;

	private ImageView mImageView;
	private ImageView mImageView2;
	private ImageView mImageView3;
	private ImageView mImageView4;
	private ImageView mImageView5;
	private ImageView mImageView6;
	private ImageView mImageView7;
	private ImageView mImageView8;
	private ImageView mImageView9;
	private TextView versiontext;

	private Context mContext;

	private Application application;
	private ProgressDialog mDownloadDialog = null;
	private ImageView mCheckBtn;
	private Button mUpdateBtn;
	private UpdateManager mUpdateManager;
	private UpdateDoing mUpdateDoing;
	private IUpdateOperate mIUpdateOperate;
	private ProgressDialog mProgressDialog;
	
	private NotificationManager mNotificationManager;
	private Notification mNotification;
	
	private PendingIntent mPendingIntent;
	private Intent mNotificationIntent;
	

	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {  
	        // 要做的事情  
			switch (msg.what) {
			case 10001:
				LogUtil.d(TAG,"10002");
				showDownloadDialog();
				break;
				
				
			case 10002:
				LogUtil.d(TAG,"10002");
				float length =msg.getData().getFloat("length");
				float total =msg.getData().getFloat("total");
				if(mDownloadDialog != null){
					
					mDownloadDialog.setMax((int)length);
					mDownloadDialog.setProgress((int)total);
				}
				
				break;
			default:
				break;
			}
	    } 
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		application = getApplication();
		mIntent = new Intent();
		versiontext = (TextView) findViewById(R.id.versiontext);
		mChineseLinearLayout = (LinearLayout) findViewById(R.id.chinese_lin);
		mChineseLinearLayout.setOnClickListener(this);
		mEnglishLinearLayout = (LinearLayout) findViewById(R.id.english_lin);
		mEnglishLinearLayout.setOnClickListener(this);
		mContoneseLinearLayout = (LinearLayout) findViewById(R.id.contonese_lin);
		mContoneseLinearLayout.setOnClickListener(this);
		mTtsLinearLayout = (LinearLayout) findViewById(R.id.tts_lin);
		mTtsLinearLayout.setOnClickListener(this);
		mOralLinearLayout = (LinearLayout) findViewById(R.id.oral_lin);
		mOralLinearLayout.setOnClickListener(this);
		mWakeupLinearLayout = (LinearLayout) findViewById(R.id.wakeup_lin);
		mWakeupLinearLayout.setOnClickListener(this);
		mVoiceprintLinearLayout = (LinearLayout) findViewById(R.id.voiceprint_lin);
		mVoiceprintLinearLayout.setOnClickListener(this);
		mOralOnLinearLayout = (LinearLayout) findViewById(R.id.oral_online_lin);
		mOralOnLinearLayout.setOnClickListener(this);
		mImageView2 = (ImageView) findViewById(R.id.imageView3);// 英
		mImageView3 = (ImageView) findViewById(R.id.imageView4);// 粤
		mImageView4 = (ImageView) findViewById(R.id.imageView5);// tts
		mImageView6 = (ImageView) findViewById(R.id.imageView7);// /oral
		mImageView7 = (ImageView) findViewById(R.id.imageView8);// wakeup
		mImageView9 = (ImageView)findViewById(R.id.imageView9);//orla_online
		versiontext.setText("版本:v" + getPackageCode());
		versiontext.setOnClickListener(this);
		mUpdateDoing = new UpdateDoing(mContext);
		mUpdateDoing.initUpdate();
		mCheckBtn = (ImageView) findViewById(R.id.CheckVender);
		mCheckBtn.setOnClickListener(this);
		mIUpdateOperate = (IUpdateOperate) mUpdateDoing
				.getOperate(UpdateDoing.OPERATE_UPDATE);
		mIUpdateOperate.setUpdateListener(mIUpdateListener);
		
		if (mIUpdateOperate != null) {
			mIUpdateOperate.update();
		}
		
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	
	}

	private void initVersionCode() {
		PackageManager manager = getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			String vName = info.versionName;
			Log.d(TAG, "initVersionCode : " + vName);
			mobileVersionCode = vName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		Log.d("MainActivity", "onClick:id " + v.getId());
		switch (v.getId()) {
		case R.id.chinese_lin:
			mIntent.setClass(MainActivity.this, Chince_part.class);
			startActivity(mIntent);
			break;
		case R.id.english_lin:
			mIntent.setClass(MainActivity.this, English_part.class);
			startActivity(mIntent);
			break;
		case R.id.contonese_lin:
			mIntent.setClass(MainActivity.this, Cantonese_part.class);
			startActivity(mIntent);
			break;
		case R.id.tts_lin:
			mIntent.setClass(MainActivity.this, Tts_part.class);
			startActivity(mIntent);
			break;
		case R.id.wakeup_lin:
			mIntent.setClass(MainActivity.this, Wakeup_part.class);
			startActivity(mIntent);
			break;
		case R.id.oral_lin:
			mIntent.setClass(MainActivity.this, Oral_part.class);
			startActivity(mIntent);
			break;

		case R.id.voiceprint_lin:
			mIntent.setClass(MainActivity.this, Voiceprint_part.class);
			startActivity(mIntent);
			break;
		case R.id.oral_online_lin:
			mIntent.setClass(MainActivity.this, Oral_online.class);
			startActivity(mIntent);
			break;

		case R.id.CheckVender:
			if (mIUpdateOperate != null) {
				mIUpdateOperate.update();
			}
			break;
		case R.id.versiontext:
			if (mIUpdateOperate != null) {
				mIUpdateOperate.update();
			}
			break;
		default:
			break;
		}
	}

	private IUpdateListener mIUpdateListener = new IUpdateListener() {

		@Override
		public void onUpdateStart() {
			LogUtil.d(TAG, "onUpdateStart");
		}

		@Override
		public void onUpdateResult(final String newVersion, final String changeLog) {
			LogUtil.d(TAG, "onUpdateResult newVersion ： " + newVersion
					+ ";changeLog : " + changeLog);
			runOnUiThread(new Runnable() {
				public void run() {
					showUpdateDialog(newVersion, changeLog);
				}
			});;

		}

		@Override
		public void onError(final ErrorUtil error) {
			LogUtil.d(TAG, "onError error : " + error.message);
			runOnUiThread(new Runnable() {
				public void run() {
					if (error.message.equals("您当前已经是最新版本了！")) {
						mCheckBtn.setVisibility(View.GONE);
					}else {
						Toast.makeText(getApplicationContext(), "信息："+error.message,Toast.LENGTH_SHORT).show();
					}
					
				}
			});;
			
		}

		@Override
		public void onDownloadStart() {
			LogUtil.d(TAG, "onDownloadStart");
		}

		@Override
		public void onDownloadProgress(final long total, final long length) {
			runOnUiThread(new Runnable() {
				public void run() {
					if(mDownloadDialog != null){
						mDownloadDialog.setMax((int)length/1024);
						mDownloadDialog.setProgress((int)total/1024);
					}
				}
			});
			
		}

		@Override
		public void onDownloadComplete(String filepath) {
			LogUtil.d(TAG, "onDownloadComplete filepath : " + filepath);
			if(mDownloadDialog != null){
				mDownloadDialog.dismiss();
			}
			showInstallDialog();
		}

		@Override
		public void onCancel() {
			LogUtil.d(TAG, "onCancel");
		}
	};

	private void showUpdateDialog(String newVersion, String changeLog) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle("版本更新");
		builder.setMessage(newVersion + "\n" + changeLog);
		builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showDownloadDialog();
				if (mIUpdateOperate != null) {
					mIUpdateOperate.download();
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog alertDialog = builder.create();
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.show();
	}
	
	
	private void showInstallDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle("安装更新");
		builder.setMessage("您是否确定安装？");
		builder.setPositiveButton("安装", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (mIUpdateOperate != null) {
					mIUpdateOperate.install();
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		AlertDialog alertDialog = builder.create();
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.show();
	}
	
	private void showDownloadDialog(){
		 mDownloadDialog = new ProgressDialog(mContext);
		 mDownloadDialog.setTitle("正在下载...");
		 mDownloadDialog.setCancelable(false);
		 mDownloadDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "取消下载", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.e(TAG, "取消下载!!");
				 if(mIUpdateOperate != null){
					 mIUpdateOperate.cancel();
				 }
			}
		});
	
		 mDownloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		 mDownloadDialog.show();
	}

	/**
	 * 获取当前程序版本名
	 * 
	 * @return
	 */
	public String getPackageVersion() {
		String version = "";
		try {
			PackageManager pm = application.getPackageManager();
			PackageInfo pi = null;
			pi = pm.getPackageInfo(application.getPackageName(), 0);
			version = pi.versionName;
			System.out.println("getPackageVersion:" + version);
		} catch (Exception e) {
			version = ""; // failed, ignored
		}
		return version;
	}

	/**
	 * 获取当前程序包名
	 * 
	 * @return
	 */
	public String getPackageName() {
		String version = "";
		try {
			PackageManager pm = application.getPackageManager();
			PackageInfo pi = null;
			pi = pm.getPackageInfo(application.getPackageName(), 0);
			version = pi.packageName;

			System.out.println("getPackageName:" + version);
		} catch (Exception e) {
			version = ""; // failed, ignored
		}
		return version;
	}

	/**
	 * 获取当前程序版本code
	 * 
	 * @return
	 */
	public String getPackageCode() {
		String code = "";
		try {
			PackageManager pm = application.getPackageManager();
			PackageInfo pi = null;
			pi = pm.getPackageInfo(application.getPackageName(), 0);
			code = pi.versionName;
			System.out.println("getPackageCode:" + code);
		} catch (Exception e) {
			code = "1"; // failed, ignored
		}
		return code;
	}
}
