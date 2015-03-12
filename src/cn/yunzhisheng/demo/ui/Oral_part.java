package cn.yunzhisheng.demo.ui;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.yunzhisheng.asrfix.USCRatingError;
import cn.yunzhisheng.asrfix.USCRatingListener;
import cn.yunzhisheng.asrfix.USCRatingManager;
import cn.yunzhisheng.common.FileTool;
import cn.yunzhisheng.common.net.Network;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.demo.R;
import cn.yunzhisheng.demo.broadcast.MyBroadcastRecevier;
import cn.yunzhisheng.demo.file.Filesize;
import cn.yunzhisheng.demo.view.LinedTextView;
import cn.yunzhisheng.demo.view.MicrophoneControl;
import cn.yunzhisheng.demo.view.DemoPrivatePreference;

public class Oral_part extends Activity implements OnClickListener,
		OnTouchListener, USCRatingListener {

	public final static String TAG = "Oral_part";
	private Context mContext;

	private USCRatingManager mManager;

	private LinedTextView mTvSentence;
	private TextView mTvScore;
	private Button mBtnUserSentence;
	private Button mBtnUserWord;
	private Button mBtnRecord;
	private ImageView mBtnPrev;
	private ImageView mBtnNext;
	private ImageView mImageView;
	private String mDir;
	private PlayPcmThread mPlayPcmThread;

	private Dialog mBuildDialog;
	private Dialog mProgressDialog;

	private String[] mSentences;
	private int mSentenceIndex;

	private Word[] mUserWords;
	private Word mUserWord;
	private BackgroundColorSpan mFocusSpan = new BackgroundColorSpan(Color.GRAY);

	public final static int STATE_IDLE = 0;
	public final static int STATE_PLAY = 1;
	private int mState;
	private TextView mText;
	private ProgressBar mProgressBar;
	
	
	private MicrophoneControl mMicrophoneControl;
	private MyBroadcastRecevier mBroadcastRecevier;
	
	private TextView infortext;
	private boolean stateNum;
	
	private void setState(int state) {
		switch (state) {
		case STATE_IDLE:
		
			mBtnPrev.setEnabled(mSentenceIndex != 0);
			mBtnNext.setEnabled(mSentenceIndex != mSentences.length - 1);
			mBtnUserSentence.setEnabled(mUserWords != null);
			mBtnUserWord.setEnabled(mUserWord != null);
			mBtnRecord.setEnabled(true);
			
			mState = STATE_IDLE;
			mBtnUserSentence.setText("播放录音");
			
			break;
		case STATE_PLAY:
			mBtnPrev.setEnabled(false);
			mBtnNext.setEnabled(false);
			mBtnUserSentence.setEnabled(true);
			mBtnUserWord.setEnabled(false);
			mBtnRecord.setEnabled(false);
			
			mState = STATE_PLAY;
			mBtnUserSentence.setText("停止播放");
			break;
		default:
			break;
		}
		mState = state;
	}

	private void reset() {
		mTvScore.setText("");
		mUserWords = null;
		mUserWord = null;
		mTvSentence.setText(mSentences[mSentenceIndex]);
		mBtnUserSentence.setEnabled(false);
		mBtnUserWord.setEnabled(false);
		mBtnUserWord.setTextColor(Color.argb(75, 30, 30,
				30));
		mBtnUserSentence.setTextColor(Color.argb(75, 30, 30,
				30));
		mBtnPrev.setEnabled(mSentenceIndex != 0);
		mBtnNext.setEnabled(mSentenceIndex != mSentences.length - 1);
		if (mSentenceIndex != 0) {
			mBtnPrev.setVisibility(View.VISIBLE);
		}else {
			mBtnPrev.setVisibility(View.GONE);
		}
		if (mSentenceIndex != mSentences.length - 1) {
			mBtnNext.setVisibility(View.VISIBLE);
		}else {
			mBtnNext.setVisibility(View.GONE);
		}
		
	}

	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.fragment_oral);
		mContext = this;
		
		infortext = (TextView)findViewById(R.id.infortext);
		
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
		
		if(FileTool.isSDCardExists() &&  Filesize.getSDAvailableSize() >= 100){
			mDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "oralEdu/";
		} else if(Filesize.getRomAvailableSize() >= 100){
			mDir = mContext.getFilesDir().getAbsolutePath() + File.separator + "oralEdu/";
		} else{
			Toast.makeText(mContext, "手机SD卡或内存不足,无法口语评测", Toast.LENGTH_LONG).show();
		}
		
		mSentences = getResources().getStringArray(R.array.sentences);
		
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

		mTvScore = (TextView) findViewById(R.id.totalScore);

		mBtnPrev = (ImageView) findViewById(R.id.buttonPrev);
		mBtnPrev.setOnClickListener(this);
		mBtnNext = (ImageView) findViewById(R.id.buttonNext);
		mBtnNext.setOnClickListener(this);

		mTvSentence = (LinedTextView) findViewById(R.id.tvSentence);
		mTvSentence.setText(mSentences[mSentenceIndex]);
		mTvSentence.setOnTouchListener(this);

		mBtnUserSentence = (Button) findViewById(R.id.buttonUserSentence);
		mBtnUserSentence.setOnClickListener(this);

		mBtnUserWord = (Button) findViewById(R.id.buttonUserWord);
		mBtnUserWord.setEnabled(false);
		mBtnUserWord.setOnClickListener(this);

		mBtnRecord = (Button) findViewById(R.id.buttonRecord);
		mBtnRecord.setText("按住说话");
		mBtnRecord.setOnTouchListener(this);

		mBtnUserWord.setTextColor(Color.argb(75, 30, 30, 30));
		mBtnUserSentence.setTextColor(Color.argb(75, 30, 30, 30));

		mProgressDialog = LoadingDialog.createLoadingDialog(this, "正在处理,请稍后...",true);
		reset();

		mBuildDialog = LoadingDialog.createLoadingDialog(this, "正在初始化,请稍后...",false);
		mBuildDialog.show();

		mManager = new USCRatingManager(this);
		mManager.setListener(this);
		mManager.setDebugMode(true);
		mManager.init(mDir);//初始化模型，口语评测运行的环境的路径

		
		
		mMicrophoneControl = (MicrophoneControl) findViewById(R.id.microphoneControl);

		mMicrophoneControl.onUnaviable();
		mMicrophoneControl.setEnabled(true);

		mMicrophoneControl.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN ){
						LogUtil.d(TAG, "in");
						mMicrophoneControl.onAviable();
						mMicrophoneControl.setEnabled(true);
						mMicrophoneControl.onRender();
						infortext.setText("正在倾听中...");
						mBtnUserSentence.setVisibility(View.GONE);
						mBtnUserWord.setVisibility(View.GONE);
						reset();
						String filename = mDir + "/sound/" + mSentenceIndex
								+ ".pcm";
						mManager.start(mSentences[mSentenceIndex], filename);
						
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					
						mBtnUserSentence.setVisibility(View.VISIBLE);
						mBtnUserWord.setVisibility(View.VISIBLE);
					
						infortext.setText("正在识别中...");
						mMicrophoneControl.onProcess();
						mMicrophoneControl.setEnabled(true);
						mProgressDialog.show();
						mManager.stop();
				
				} else if(event.getAction() == MotionEvent.ACTION_CANCEL){
					mBtnUserSentence.setVisibility(View.VISIBLE);
					mBtnUserWord.setVisibility(View.VISIBLE);
				
					mMicrophoneControl.onProcess();
					mMicrophoneControl.setEnabled(true);
					mProgressDialog.show();
					mManager.stop();
				}
				return true;
			}
		});

		
	}

	@Override
	protected void onResume() {
		super.onResume();
		mBtnPrev.setEnabled(mSentenceIndex != 0);
		mBtnNext.setEnabled(mSentenceIndex != mSentences.length - 1);
		mBtnUserSentence.setEnabled(mUserWords != null);
		mBtnUserWord.setEnabled(mUserWord != null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mManager.release();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonPrev:
			mSentenceIndex--;
			reset();
			break;
		case R.id.buttonNext:
			mSentenceIndex++;
			reset();
			break;
		case R.id.buttonUserSentence:
			if (mState == STATE_IDLE) {
				setState(STATE_PLAY);
				mPlayPcmThread = new PlayPcmThread(mDir + "/sound/"
						+ mSentenceIndex + ".pcm");
				mPlayPcmThread.setPlayerListener(new IPlayListener() {

					@Override
					public void onPlayEnd() {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								setState(STATE_IDLE);
							}
						});
					}
				});
				mPlayPcmThread.start();
			}else if (mState == STATE_PLAY) {
				setState(STATE_IDLE);
				mPlayPcmThread.reqStop();
			}
			break;
		case R.id.buttonUserWord:
			mPlayPcmThread = new PlayPcmThread(mDir + "/sound/"
					+ mSentenceIndex + ".pcm");
			int size = mUserWord.mSyllables.size();
			Syllable head = mUserWord.mSyllables.get(0);
			Syllable tail = mUserWord.mSyllables.get(size - 1);
			mPlayPcmThread.setDuration(head.mBegin << 5, tail.mEnd << 5);
			mPlayPcmThread.setPlayerListener(new IPlayListener() {

				@Override
				public void onPlayEnd() {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							setState(STATE_IDLE);
						}
					});
				}
			});
			mPlayPcmThread.start();
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (v.getId() == R.id.tvSentence) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				Layout layout = ((TextView) v).getLayout();
				int x = (int) event.getX();
				int y = (int) event.getY();
				if (layout != null && mUserWords != null) {
					int line = layout.getLineForVertical(y);
					int characterOffset = layout
							.getOffsetForHorizontal(line, x);

					int offset = 0;
					for (int i = 0; i < mUserWords.length; i++) {
						offset += mUserWords[i].vsize();
						if (characterOffset <= offset) {
							mUserWord = mUserWords[i];
							SpannableString ss = SSBuilder
									.buildString(mUserWords);
							ss.setSpan(mFocusSpan,
									offset - mUserWords[i].vsize(), offset,
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							mTvSentence.setText(ss);
							mBtnUserWord.setEnabled(true);
							mBtnUserWord.setTextColor(Color.argb(255, 30, 30,
									30));
							break;
						}
						offset += 1;
					}
				}
				return true;
			} 
		} else if (v.getId() == R.id.buttonRecord) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				reset();
				mBtnRecord.setText("松手停止");
				String filename = mDir + "/sound/" + mSentenceIndex
						+ ".pcm";
				mManager.start(mSentences[mSentenceIndex], filename);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				mProgressDialog.show();
				mBtnRecord.setText("按住说话");
				mManager.stop();
			}
			return true;
		}
		return false;
	}

	@Override
	public void onInitResult(final int result, final String msg) {//初始化
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mBuildDialog.dismiss();
				if (result != USCRatingError.RESULT_OK)
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void onRateResult(final int result, final String json) {//得到评测的结果
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mProgressDialog.dismiss();
				infortext.setText("按住说话");
			}
		});

		if (result == USCRatingError.RESULT_OK) {
			try {
				
				JSONObject obj = new JSONObject(json);
				final int score = obj.getInt("totalscore");
				JSONArray array = obj.getJSONArray("words");
				int size = array.length();
				Word[] words = new Word[size];
				for (int i = 0; i < size; i++) {
					JSONObject jw = array.getJSONObject(i);
					words[i] = new Word();
					words[i].addSyllable(jw.getString("word"),
							jw.getInt("score"), jw.getLong("begin"),
							jw.getLong("end"));
				}
				mUserWords = words;
				final SpannableString ss = SSBuilder.buildString(mUserWords);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mTvSentence.setText(ss);
						mBtnUserSentence.setEnabled(true);
						mBtnUserSentence.setTextColor(Color.argb(255, 30, 30,
								30));
						mBtnUserWord.setEnabled(false);
						mTvScore.setText("总分 " + score);
						mMicrophoneControl.onAviable();
						mMicrophoneControl.setEnabled(true);
						infortext.setText("按住说话");
						
					}
				});
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					String msg = ErrorMsg.getMsg(result);
					mTvScore.setText(msg);
					infortext.setText("按住说话");
					Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
				}
			});
		}
	}

	@Override
	public void onVolumeUpdate(final double volume) {//得到录音的回调
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				double mProVolum = volume * 50;
				LogUtil.d(TAG, "volume = " + volume + "mProVolum = " + mProVolum);
				DemoPrivatePreference.mRecordingVoiceVolumn = (float) mProVolum;
			}
		});
		 
	}
 
	@Override
	public void onRecordEnd() {//录音失败
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mTvScore.setText("");
				infortext.setText("按住说话");
			}
		});
	}
}
