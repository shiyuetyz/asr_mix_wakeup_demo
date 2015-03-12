package cn.yunzhisheng.demo.ui;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import cn.yunzhisheng.asr.online.oral.USCRecognizer;
import cn.yunzhisheng.asr.online.oral.USCRecognizerListener;
import cn.yunzhisheng.common.FileTool;
import cn.yunzhisheng.common.USCError;
import cn.yunzhisheng.common.net.Network;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.demo.AppApplication;
import cn.yunzhisheng.demo.R;
import cn.yunzhisheng.demo.broadcast.INetWorkListen;
import cn.yunzhisheng.demo.file.Filesize;
import cn.yunzhisheng.demo.view.DemoPrivatePreference;
import cn.yunzhisheng.demo.view.LinedTextView;
import cn.yunzhisheng.demo.view.MicrophoneControl;

public class Oral_online extends Activity {

	private int oralIndex = 0;
	private LinedTextView oralSrcTextView;
	private ImageView preBtn;
	private ImageView nextBtn;
	private Button recordBtn;
	private TextView textviewSorce;
	private TextView infortext;
	private ImageView mImageView;
	private TextView mText;
	private USCRecognizer recognizer;
	private static final String APP_KEY = "azp2mafdw5wha5tywcja7bzxdthqq5u2cseaazig";
	private ArrayList<String> orals = new ArrayList<String>();
	private String[] orals_my;
	private static final String TAG = "OralRecognizer";
	private boolean isRecording;
	private ProgressDialog initRecordingDialog;
	private ProgressDialog processingDialog;
	private Button btn_palyall;
	private Button btn_palyword;
	private Word[] mUserWords;
	private Word mUserWord;
	private String mDir;
	private Context mContext;
	private PlayPcmThread mPlayPcmThread;
	private MicrophoneControl mMicrophoneControl;
	private BackgroundColorSpan mFocusSpan = new BackgroundColorSpan(Color.GRAY);
	public final static int STATE_IDLE = 0;
	public final static int STATE_PLAY = 1;
	private int mState;
	private boolean stateNum;
	
	private void setState(int state) {
		switch (state) {
		case STATE_IDLE:
			preBtn.setEnabled(oralIndex != 0);
			nextBtn.setEnabled(oralIndex != orals_my.length - 1);
			btn_palyall.setEnabled(mUserWords != null);
			btn_palyword.setEnabled(false);
			mState = STATE_IDLE;
			btn_palyall.setText("播放录音");
			break;
		case STATE_PLAY:
			preBtn.setEnabled(false);
			nextBtn.setEnabled(false);
			btn_palyall.setEnabled(true);
			btn_palyword.setEnabled(false);
			
			mState = STATE_PLAY;
			btn_palyall.setText("停止播放");
			break;
		default:
			break;
		}
		mState = state;
	}

	private void reset() {
		textviewSorce.setText("");
		mUserWords = null;
		mUserWord = null;
		oralSrcTextView.setText(orals_my[oralIndex]);
		btn_palyall.setEnabled(false);
		btn_palyword.setEnabled(false);
		btn_palyword.setTextColor(Color.argb(75, 30, 30, 30));
		btn_palyall.setTextColor(Color.argb(75, 30, 30, 30));
		preBtn.setEnabled(oralIndex != 0);
		nextBtn.setEnabled(oralIndex != orals_my.length - 1);
		if (oralIndex != 0) {
			preBtn.setVisibility(View.VISIBLE);
		} else {
			preBtn.setVisibility(View.GONE);
		}
		if (oralIndex != orals_my.length - 1) {
			nextBtn.setVisibility(View.VISIBLE);
		} else {
			nextBtn.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_oral_online);
		mContext = this;
		if (FileTool.isSDCardExists() && Filesize.getSDAvailableSize() >= 100) {
			mDir = Environment.getExternalStorageDirectory().getAbsolutePath()
					+ File.separator + "oralEduOnline/";
		} else if (Filesize.getRomAvailableSize() >= 100) {
			mDir = mContext.getFilesDir().getAbsolutePath() + File.separator
					+ "oralEduOnline/";
		} else {
			Toast.makeText(mContext, "手机SD卡或内存不足,无法口语评测", Toast.LENGTH_LONG)
					.show();
		}
		orals_my = getResources().getStringArray(R.array.sentences);
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

		readOralText();
		
		recognizer = new USCRecognizer(this, APP_KEY);
		recognizer.setRecordingDataEnable(true);
		recognizer.setListener(recognizerListener);
		initLayout();

	}

	private void initLayout() {
		oralSrcTextView = (LinedTextView) findViewById(R.id.tvSentence);
		textviewSorce = (TextView) findViewById(R.id.totalScore);
		infortext = (TextView) findViewById(R.id.infortext);

		oralSrcTextView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Layout layout = ((TextView) v).getLayout();
					int x = (int) event.getX();
					int y = (int) event.getY();
					if (layout != null && mUserWords != null) {
						int line = layout.getLineForVertical(y);
						int characterOffset = layout.getOffsetForHorizontal(
								line, x);

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
								oralSrcTextView.setText(ss);
								
								btn_palyword.setEnabled(true);
								btn_palyword.setTextColor(Color.argb(255, 30, 30, 30));
								break;
							}
							offset += 1;
						}
					}
					return true;
				}
				return false;

			}
		});

		oralSrcTextView.setText(orals_my[oralIndex]);

		preBtn = (ImageView) findViewById(R.id.buttonPrev);
		preBtn.setOnClickListener(clickListener);

		nextBtn = (ImageView) findViewById(R.id.buttonNext);
		nextBtn.setOnClickListener(clickListener);

		initRecordingDialog = new ProgressDialog(this);
		initRecordingDialog.setMessage("正在初始化...");
		initRecordingDialog.setCancelable(false);

		processingDialog = new ProgressDialog(this);
		processingDialog.setMessage("正在识别...");
		processingDialog.setCancelable(false);

		btn_palyall = (Button) this.findViewById(R.id.buttonUserSentence);
		btn_palyall.setOnClickListener(clickListener);
		
		btn_palyword = (Button) this.findViewById(R.id.buttonUserWord);
		btn_palyword.setOnClickListener(clickListener);
		btn_palyword.setEnabled(false);
		
		btn_palyword.setTextColor(Color.argb(75, 30, 30, 30));
		btn_palyall.setTextColor(Color.argb(75, 30, 30, 30));

		
		mMicrophoneControl = (MicrophoneControl) findViewById(R.id.microphoneControl);

		mMicrophoneControl.onUnaviable();
		mMicrophoneControl.setEnabled(true);
		reset();
		mMicrophoneControl.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					LogUtil.d(TAG, "in");
					if (Network.isNetworkConnected(Oral_online.this)) {
						if (mMicrophoneControl != null) {
							mMicrophoneControl.onAviable();
							mMicrophoneControl.setEnabled(true);
							mMicrophoneControl.onRender(); 
							infortext.setText("正在倾听中...");
							reset();
						}
						recognizer.setOralText(orals_my[oralIndex]);					
						recognizer.setOption(20, true);
						recognizer.start();
					}else {
						if(mMicrophoneControl !=null){
							mMicrophoneControl.setEnabled(false);
						}
						infortext.setText("按住说话");
						Toast.makeText(Oral_online.this, "当前无网络链接,无法识别！", Toast.LENGTH_SHORT)
						.show();
					}
					
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					if (Network.isNetworkConnected(Oral_online.this)) {
						if (mMicrophoneControl != null) {
							btn_palyall.setVisibility(View.VISIBLE);
							btn_palyword.setVisibility(View.VISIBLE);

							infortext.setText("正在识别中...");
							mMicrophoneControl.onProcess();
							mMicrophoneControl.setEnabled(true);
						}
						recognizer.stop();
					}else {
						if(mMicrophoneControl !=null){
							mMicrophoneControl.setEnabled(false);
						}
						infortext.setText("按住说话");
					}
				} else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
					
					if (Network.isNetworkConnected(Oral_online.this)) {
						if (mMicrophoneControl != null) {
							btn_palyall.setVisibility(View.VISIBLE);
							btn_palyword.setVisibility(View.VISIBLE);

							mMicrophoneControl.onProcess();
							mMicrophoneControl.setEnabled(true);
						}
						recognizer.stop();
					}else {
						if(mMicrophoneControl !=null){
							mMicrophoneControl.setEnabled(false);
						}
						infortext.setText("按住说话");
					}
					
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
								.getNetChanged(Oral_online.this);
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

	private void readOralText() {
		InputStream in = this.getResources().openRawResource(R.raw.sample);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String str = null;
		try {
			while ((str = br.readLine()) != null) {
				orals.add(str);
			}
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		} finally {
			try {
				br.close();
				in.close();
			} catch (IOException e) {

			}

		}
	}


	private View.OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.buttonPrev:
				oralIndex--;
				reset();

				break;
			case R.id.buttonNext:
				oralIndex++;
				reset();

				break;

			case R.id.buttonUserSentence:

				if (mState == STATE_IDLE) {
					setState(STATE_PLAY);
					mPlayPcmThread = new PlayPcmThread(mDir + "/sound/"
							+ oralIndex + ".pcm");
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
				mPlayPcmThread = new PlayPcmThread(mDir + "/sound/" + oralIndex
						+ ".pcm");
				int size = mUserWord.mSyllables.size();

				Syllable head = mUserWord.mSyllables.get(0);
				Syllable tail = mUserWord.mSyllables.get(size - 1);

				Log.e("--Syllable", "size:" + size + "head:" + (head.mBegin)
						+ head.mText + "tail:" + (tail.mEnd) + tail.mText);
				mPlayPcmThread.setDuration(head.mBegin << 5, tail.mEnd << 5);
				mPlayPcmThread.setPlayerListener(new IPlayListener() {

					@Override
					public void onPlayEnd() {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
							}
						});
					}
				});
				mPlayPcmThread.start();
				break;
			default:
				break;
			}
		}

	};

	StringBuffer stringBuffer = new StringBuffer();
	List<byte[]> audioData = new ArrayList<byte[]>();
	private USCRecognizerListener recognizerListener = new USCRecognizerListener() {
		@Override
		public void onEnd(final USCError error) {
			Log.e(TAG, stringBuffer.toString());
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					infortext.setText("按住说话");
				}
			});
			List<byte[]> audioData = (List<byte[]>) recognizer.getOption(20);
			Log.e(TAG, "录音文件为空:" + audioData.size());
			if (audioData != null) {
				String filename = mDir + "/sound/" + oralIndex + ".pcm";
				writeWavFile(filename, audioData);
			} else {
				Log.e(TAG, "录音文件为空");
			}
			if (error != null) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						
						textviewSorce.setText(error.toString());
						infortext.setText("按住说话");
						Toast.makeText(mContext, error.toString(), Toast.LENGTH_LONG).show();
					}
				});
			} else {

				String result = stringBuffer.toString().trim();
				stringBuffer.delete(0, stringBuffer.length());
				JSONObject obj;
				try {
					obj = new JSONObject(result);
					JSONArray array = obj.getJSONArray("lines");
					JSONObject jsonObject = array.getJSONObject(0);
					final long score = jsonObject.getLong("score");
					JSONArray array2 = jsonObject.getJSONArray("words");
					JSONArray jsonReplace = new JSONArray();
					for (int i = 0; i < array2.length(); i++) {
						JSONObject jsonObject2 = (JSONObject)array2.get(i);
						if(!jsonObject2.toString().contains("sil")){
							jsonReplace.put(jsonObject2);
						}
					}
					LogUtil.d(TAG, "jsonReplace str : " + jsonReplace.toString());
 					
					int size = jsonReplace.length();
					Word[] words = new Word[size];

					for (int i = 0; i < size; i++) {
						JSONObject jw = jsonReplace.getJSONObject(i);
						words[i] = new Word();
						Log.e(TAG, "begin:" + jw.getDouble("begin") + "end:"
								+ jw.getDouble("end"));
						words[i].addSyllable(
								jw.getString("text"),
								(jw.getLong("score") * 10 % 10 >= 5 ? (int) (jw
										.getLong("score") + 1) : (int) (jw
										.getLong("score"))), (long) (jw
										.getDouble("begin") * 1000), (long) (jw
										.getDouble("end") * 1000));

					}
					mUserWords = words;

					final SpannableString ss = SSBuilder
							.buildString(mUserWords);

					runOnUiThread(new Runnable() {

						@Override
						public void run() {

							oralSrcTextView.setText(ss);
							btn_palyall.setEnabled(true);
							btn_palyall.setTextColor(Color.argb(255, 30, 30,
									30));
							btn_palyword.setEnabled(false);
							textviewSorce.setText("总分 " + score);
							mMicrophoneControl.onAviable();
							mMicrophoneControl.setEnabled(true);
							infortext.setText("按住说话");
						}
					});

				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (result.length() == 0) {
					Toast.makeText(Oral_online.this, "未按文本朗读", 0).show();
				}
			}

		}


		@Override
		public void onResult(String result, boolean isLast) {
			if (result != null) {
				stringBuffer.append(result);
			}

		}

		@Override
		public void onUpdateVolume(int volume) {
			DemoPrivatePreference.mRecordingVoiceVolumn = (float) volume;
		}

		@Override
		public void onVADTimeout() {

		}


		@Override
		public void onRecognizerStart() {
			// TODO Auto-generated method stub
			initRecordingDialog.dismiss();
		}


		@Override
		public void onRecordingStop() {
			// TODO Auto-generated method stub
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					textviewSorce.setText("");
					infortext.setText("按住说话");
				}
			});
		};

	};

	private void writeWavFile(String recordingDir, List<byte[]> audioData) {//将录音文件保存到文件
		int PCMSize = 0;
		if (audioData != null && audioData.size() > 0) {
			synchronized (audioData) {
				for (int i = 0; i < audioData.size(); i++) {
					byte[] buffer = audioData.get(i);
					if (buffer != null) {
						PCMSize += buffer.length;
					}
				}
			}
		}
		WaveHeader header = new WaveHeader();
		header.fileLength = PCMSize + (44 - 8);
		header.FmtHdrLeth = 16;
		header.BitsPerSample = 16;
		header.Channels = 1;
		header.FormatTag = 0x0001;
		header.SamplesPerSec = 16000;
		header.BlockAlign = (short) (header.Channels * header.BitsPerSample / 8);
		header.AvgBytesPerSec = header.BlockAlign * header.SamplesPerSec;
		header.DataHdrLeth = PCMSize;

		byte[] h;
		try {
			h = header.getHeader();
		} catch (IOException e1) {
			h = null;
		}

		File file = null;

		if (h != null && h.length == 44) {
			file = new File(recordingDir);
		} else {
			file = new File(recordingDir);
		}

		File parent = file.getParentFile();
		if (parent != null) {
			parent.mkdirs();
		}

		FileOutputStream os = null;
		BufferedOutputStream bos = null;
		try {
			os = new FileOutputStream(file, false);
			bos = new BufferedOutputStream(os);
			if (h != null) {
				bos.write(h, 0, h.length);
			}
			if (audioData != null && audioData.size() > 0) {
				synchronized (audioData) {
					for (int i = 0; i < audioData.size(); i++) {
						byte[] buffer = audioData.get(i);
						bos.write(buffer);
					}
				}
			}
			LogUtil.d(TAG, "writePcmFile: " + file.getAbsolutePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
