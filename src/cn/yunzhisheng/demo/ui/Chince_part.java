package cn.yunzhisheng.demo.ui;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.yunzhisheng.common.USCError;
import cn.yunzhisheng.common.net.Network;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.demo.AppApplication;
import cn.yunzhisheng.demo.R;
import cn.yunzhisheng.demo.broadcast.INetWorkListen;
import cn.yunzhisheng.demo.view.DemoPrivatePreference;
import cn.yunzhisheng.demo.view.LinedTextView;
import cn.yunzhisheng.demo.view.MicrophoneControl;
import cn.yunzhisheng.pro.USCRecognizer;
import cn.yunzhisheng.pro.USCRecognizerListener;


public class Chince_part extends Activity {
	private LinedTextView mEditText;
	private TextView mTextView;
	private USCRecognizer mRecognizer;
	private int[] statues = { 1, 2, 3 };
	private int statue = 1;
	private LinearLayout tLinearLayout;
	private ImageView mImageView;
	private TextView mText;
	private boolean stateNum;
	private MicrophoneControl mMicrophoneControl;
	private TextView infortext;
	private GestureDetector mGestureDetector;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_china);
		tLinearLayout = (LinearLayout) findViewById(R.id.chinese_back);
		
		infortext = (TextView)findViewById(R.id.infortext);
		
		mText = (TextView) findViewById(R.id.textview1);

		mImageView = (ImageView) findViewById(R.id.logo_image);
		mImageView.setOnClickListener(new OnClickListener() {

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
				// TODO Auto-generated method stub
				finish();
			}
		});
		mEditText = (LinedTextView) findViewById(R.id.editText1);
		mTextView = (TextView) findViewById(R.id.vtext);
		initRecognizer();
		mMicrophoneControl = (MicrophoneControl) findViewById(R.id.microphoneControl);
		mMicrophoneControl.onUnaviable();
		mMicrophoneControl.setEnabled(true);
		
		
		mMicrophoneControl.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
						if (Network.isNetworkConnected(Chince_part.this)) {
							if(mMicrophoneControl !=null){
								mMicrophoneControl.onAviable();
								mMicrophoneControl.setEnabled(true);
								mMicrophoneControl.onRender();
								infortext.setText("正在倾听中...");
							}
							mRecognizer.start();
						}else {
							if(mMicrophoneControl !=null){
								mMicrophoneControl.setEnabled(false);
							}
							infortext.setText("按住说话");
							Toast.makeText(Chince_part.this, "当前无网络链接,无法识别！", Toast.LENGTH_SHORT)
							.show();
						}
						
				
				} else if (arg1.getAction() == MotionEvent.ACTION_UP) {
					if (Network.isNetworkConnected(Chince_part.this)) {
						stopRecord();
						if(mMicrophoneControl !=null){
							infortext.setText("正在识别中...");
							mMicrophoneControl.onProcess();
							mMicrophoneControl.setEnabled(true);
						}
					}else {
						if(mMicrophoneControl !=null){
							mMicrophoneControl.setEnabled(false);
						}
						infortext.setText("按住说话");
					}
				}else if (arg1.getAction() == MotionEvent.ACTION_OUTSIDE) {
					
				}

				return true;
			}
		});
		AppApplication.setListenet(new INetWorkListen() {
			
			@Override
			public void onConectChange() {
				runOnUiThread(new Runnable() {
					public void run() {
						stateNum = AppApplication
								.getNetChanged(Chince_part.this);
						LogUtil.d("China", "state:"+stateNum);
						if (stateNum) {
							if(mMicrophoneControl !=null){
								mMicrophoneControl.onAviable();
								mMicrophoneControl.setEnabled(true);
							}
							
						}else {
							if(mMicrophoneControl !=null){
								mMicrophoneControl.setEnabled(false);
							}
						}
					}
				});
			}
		});
	}



	private void initRecognizer() {
		mRecognizer = new USCRecognizer(this,
				"th7npuyelwhxmsc4dc4cs4q2dqxo2ieuhqugb7y5");
		mRecognizer.setBandwidth(USCRecognizer.BANDWIDTH_AUTO);
		mRecognizer.setLanguage("chinese");
		mRecognizer.setEngine("general");
		mRecognizer.setListener(new USCRecognizerListener() {

			@Override
			public void onResult(String arg0, boolean arg1) {
				LogUtil.d("chince", "--onResult");
				mEditText.append(arg0);
			}

			@Override
			public void onRecognizerStart() {
				LogUtil.d("chince", "--onRecognizerStart");
			}

			@Override
			public void onUpdateVolume(int arg0) {
				DemoPrivatePreference.mRecordingVoiceVolumn = (float) arg0;
			}

			@Override
			public void onVADTimeout() {
				LogUtil.d("chince", "--onVADTimeout");
			}

			@Override
			public void onEnd(USCError arg0) {
				runOnUiThread(new Runnable() {
					public void run() {
						infortext.setText("按住说话");
					}
				});
				mRecognizer.cancel();
				if(mMicrophoneControl !=null){
					mMicrophoneControl.onAviable();
					mMicrophoneControl.setEnabled(true);
				}
			}

			@Override
			public void onRecordingStop(List<byte[]> arg0) {
				LogUtil.d("chince", "--onRecordingStop");
			}

			@Override
			public void onSpeechStart() {
				LogUtil.d("chince", "--onSpeechStart");
			}

			@Override
			public void onUploadUserData(USCError arg0) {

			}
		});

	}

	public void stopRecord() {
		mRecognizer.stop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mRecognizer.cancel();
		if(mMicrophoneControl !=null){
			mMicrophoneControl.onDestroy();
			mMicrophoneControl = null;
		}
		
	}
}
