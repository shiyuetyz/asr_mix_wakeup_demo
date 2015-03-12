package cn.yunzhisheng.demo.ui;
import java.util.ArrayList;
import java.util.List;
import cn.yunzhisheng.demo.R;
import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.yunzhisheng.common.util.ErrorUtil;
import cn.yunzhisheng.tts.offline.basic.ITTSControl;
import cn.yunzhisheng.tts.offline.basic.TTSFactory;
import cn.yunzhisheng.tts.offline.basic.TTSPlayerListener;
import cn.yunzhisheng.vui.wakeup.WakeUpRecognizer;
import cn.yunzhisheng.vui.wakeup.WakeUpRecognizerListener;

public class Wakeup_part extends Activity implements TTSPlayerListener{
	private static final String TAG = "Wakeup_part";
	private TextView tv_result;
	private WakeUpRecognizer wakeUpRecognizer = null;
	public boolean recordingState = false;
	public Vibrator mVibrator;
	private AlphaAnimation alphaAnimation1;
	private ImageView mImageView;
	private ImageView mImageView2;
	private ITTSControl mTTSPlayer;
	private TextView mTextView;
	private ImageView mImageViewSucess;
	private TextView mText;
	private Dialog mBuildDialog;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			// 要做的事情
			mImageView.clearAnimation();
			mImageView.setVisibility(View.INVISIBLE);
			mTextView.setVisibility(View.INVISIBLE);
			mImageViewSucess.setVisibility(View.INVISIBLE);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Log.d(TAG, "onCreate");

		mVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);// /

		setContentView(R.layout.fragment_wakeup);

		mTTSPlayer = TTSFactory.createTTSControl(this, "appkey");
		mTTSPlayer.setTTSListener(this);

		mTextView = (TextView) findViewById(R.id.tv_text);

		mImageView2 = (ImageView) findViewById(R.id.logo_image);
		mImageView2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		mText = (TextView) findViewById(R.id.textview1);
		mText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		tv_result = (TextView) findViewById(R.id.tv_result);
		mImageView = (ImageView) findViewById(R.id.imageview1);
		mImageViewSucess = (ImageView)findViewById(R.id.imageview_success);
		mBuildDialog = LoadingDialog.createLoadingDialog(this, "正在初始化,请稍后...",false);
		mBuildDialog.show();

		wakeUpRecognizer = new WakeUpRecognizer(Wakeup_part.this);
		wakeUpRecognizer.setListener(listener);

		new Thread(new Runnable() {
			@Override
			public void run() {
				wakeUpRecognizer.initModel();
				List<String> commandData = new ArrayList<String>();
				commandData.add("你好魔方");
				/** ---设置唤醒命令词集合--- */
				wakeUpRecognizer.setCommandData(commandData);
			}
		}).start();
		
		mTTSPlayer.initTTSEngine(getApplicationContext());
	}

	@Override
	public void finish() {
		super.finish();

		Log.d(TAG, "finish");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d(TAG, "onRestart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "onStop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		if (wakeUpRecognizer != null) {
			wakeUpRecognizer.release();
		}
		if (mTTSPlayer != null) {
			mTTSPlayer.releaseTTSEngine();
		}
	}

	private WakeUpRecognizerListener listener = new WakeUpRecognizerListener() {

		@Override
		public void onWakeUpInitDone() {
			runOnUiThread(new Runnable() {
				public void run() {
					Log.d(TAG, "onInitDone");
					if (mBuildDialog != null) {
						mBuildDialog.dismiss();
					}

					wakeUpRecognizer.start();
					mImageView.setVisibility(ImageView.INVISIBLE);
				}
			});
		}

		@Override
		public void onWakeUpRecordingStart() {
			runOnUiThread(new Runnable() {
				public void run() {
					Log.d(TAG, "onRecordingStart");
					recordingState = true;
				}
			});

		}

		@Override
		public void onWakeUpError(final ErrorUtil error) {
			runOnUiThread(new Runnable() {
				public void run() {
					Log.d(TAG, "onEnd errorCode = " + error.code
							+ "...errorMessage = " + error.message);
					recordingState = false;
					ToastMessage("语音唤醒服务异常  异常信息：" + error.message);
				}
			});

		}

		@Override
		public void onWakeUpSuccess(final String reString) {
			runOnUiThread(new Runnable() {
				public void run() {
					Log.d(TAG, "onResult result = " + reString);
					mTextView.setVisibility(View.VISIBLE);
					mTextView.setText("唤醒成功");
					mTTSPlayer.play("唤醒成功");
					if (mVibrator != null) {
						mVibrator.vibrate(300);
					}
					mImageView.setVisibility(View.VISIBLE);
					mImageViewSucess.setVisibility(View.VISIBLE);
					alphaAnimation1 = new AlphaAnimation(0.1f, 1.0f);
					alphaAnimation1.setDuration(300);
					alphaAnimation1.setRepeatCount(Animation.INFINITE);
					alphaAnimation1.setRepeatMode(Animation.REVERSE);
					mImageView.setAnimation(alphaAnimation1);
					alphaAnimation1.start();
					Message message = new Message();
					mHandler.sendMessageDelayed(message, 2500);
				}
			});
		}

		@Override
		public void onWakeUpRecordingStop() {
			runOnUiThread(new Runnable() {
				public void run() {
					Log.d(TAG, "onRecordingStop");
					recordingState = false;
				}
			});
		}

		@Override
		public void onWakeUpRecognizeStop() {
			
		}

	};

	private void ToastMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onBuffer() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayBegin() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayEnd() {
		// TODO Auto-generated method stub
		
	}

}
