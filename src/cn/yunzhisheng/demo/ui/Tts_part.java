package cn.yunzhisheng.demo.ui;

import cn.yunzhisheng.demo.R;
import cn.yunzhisheng.demo.view.LinedEditText;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.yunzhisheng.tts.offline.basic.ITTSControl;
import cn.yunzhisheng.tts.offline.basic.TTSFactory;
import cn.yunzhisheng.tts.offline.basic.TTSPlayerListener;

public class Tts_part extends Activity implements OnClickListener,
TTSPlayerListener{


	private ImageView mTextNextBtn;
	private ImageView mTextBackBtn;
	private Button mButtonPlay;
	private LinedEditText mEditText;
	private ImageView mImageView;
	private TextView mText;
	private int textCount = 0;

	private boolean flage = false; // 播放控制
	// 语音合成控件
	private ITTSControl mTTSPlayer;

	private static final String TAG = "Tts_part";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fragment_tts);
		
		mImageView = (ImageView)findViewById(R.id.logo_image);
		mImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		mText =(TextView)findViewById(R.id.textview1);
		mText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		mButtonPlay = (Button) findViewById(R.id.play_text_btn);
		mEditText = (LinedEditText) findViewById(R.id.text);
		mButtonPlay.setOnClickListener(this);
		// 初始化语音合成对象sand5g2nsc6a5bizpyc2tlzcuzdl646vdek5eti6
		mTTSPlayer = TTSFactory.createTTSControl(this, "appkey");
		mTTSPlayer.setTTSListener(this);
		mEditText.setText(Util.sourceText[0]);
		final Dialog loadDataDialog = LoadingDialog.createLoadingDialog(this, "正在初始化,请稍后...",false);
		loadDataDialog.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// 初始化合成引擎
				mTTSPlayer.initTTSEngine(getApplicationContext());
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mButtonPlay.setEnabled(true);
						loadDataDialog.dismiss();
					}
				});
			}
		}).start();

		Log.e(TAG, "getVersion:" + TTSFactory.getVersion());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 使用完成后释放引擎
		mTTSPlayer.releaseTTSEngine();
		Log.e(TAG, "releaseTTSEngine");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.play_text_btn:
			if (mEditText.getText().toString().trim().equals("")) {
				break;
			}
			if (mButtonPlay.getText().equals("")) {
				
			}
			updateUI();
			break;


		default:
			break;
		}
	}

	@Override
	public void onBuffer() {
		// 开始缓冲回调
		Log.e(TAG, "onBuffer");
	}

	@Override
	public void onPlayBegin() {
		// 开始播放回调
		Log.e(TAG, "onPlayBegin");
		flage = true;
	}

	@Override
	public void onPlayEnd() {
		// 播放完成回调
		Log.e(TAG, "onPlayEnd");
		flage = false;
		mButtonPlay.setText("播报");
	}

	@Override
	public void onCancel() {
		// 取消播放回调
		Log.e(TAG, "onCancel");

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Util.RESULT_CODE) {
			mTTSPlayer.setType(Util.type);
		}
	}

	// 更新UI
	private void updateUI() {
		
		if (mButtonPlay.getText().equals("播报")) {
			mButtonPlay.setText("停止");
			mTTSPlayer.play(mEditText.getText().toString());
			flage = true;
		} else if (mButtonPlay.getText().equals("停止")) {
			mButtonPlay.setText("播报");
			mTTSPlayer.stop();
			flage = false;
		}

	}




}
