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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.yunzhisheng.asr.online.oral.USCRecognizer;
import cn.yunzhisheng.asr.online.oral.USCRecognizerListener;
import cn.yunzhisheng.common.FileTool;
import cn.yunzhisheng.common.USCError;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.demo.R;
import cn.yunzhisheng.demo.file.Filesize;

public class More_text extends Activity {


	private int oralIndex = 0;
	private TextView oralSrcTextView;//阅读的范本
	private Button preBtn;
	private Button nextBtn;
	private Button recordBtn;
	private ProgressBar pbVolume;
	private TextView textviewSorce;//oral_srcore
	
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

	
	private BackgroundColorSpan mFocusSpan = new BackgroundColorSpan(Color.GRAY);
	
	public final static int STATE_IDLE = 0;
	public final static int STATE_PLAY = 1;
	private int mState;
	private void setState(int state) {
		switch (state) {
		case STATE_IDLE:
			
			break;
		case STATE_PLAY:
			
			break;
		default:
			break;
		}
		mState = state;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_text);
		mContext = this;
		if(FileTool.isSDCardExists() &&  Filesize.getSDAvailableSize() >= 100){//sd卡
			mDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "oralEduOnline/";
		} else if(Filesize.getRomAvailableSize() >= 100){//手机内存
			mDir = mContext.getFilesDir().getAbsolutePath() + File.separator + "oralEduOnline/";
		} else{
			Toast.makeText(mContext, "手机SD卡或内存不足,无法口语评测", Toast.LENGTH_LONG).show();
		}
		orals_my = getResources().getStringArray(R.array.sentences);
		readOralText();
		
		initLayout();
		
		recognizer = new USCRecognizer(this, APP_KEY);
		recognizer.setRecordingDataEnable(true);
		recognizer.setListener(recognizerListener);
		
	}
	
	private void initLayout(){
		oralSrcTextView = (TextView) findViewById(R.id.oral_src_tx);
		textviewSorce = (TextView)findViewById(R.id.oral_srcore);
		
		oralSrcTextView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Layout layout = ((TextView) v).getLayout();
					int x = (int) event.getX();
					int y = (int) event.getY();
					if (layout != null && mUserWords != null) {
						int line = layout.getLineForVertical(y);
						int characterOffset = layout.getOffsetForHorizontal(line, x);

						int offset = 0;
						for (int i = 0; i < mUserWords.length; i++) {
							offset += mUserWords[i].vsize();
							if (characterOffset <= offset) {
								mUserWord = mUserWords[i];
								SpannableString ss = SSBuilder.buildString(mUserWords);
								ss.setSpan(mFocusSpan, offset - mUserWords[i].vsize(), offset, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
								oralSrcTextView.setText(ss);
								
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
		
//		oralSrcTextView.setText(orals.get(oralIndex));
		oralSrcTextView.setText(orals_my[oralIndex]);
		
		pbVolume = (ProgressBar) findViewById(R.id.volume_progress_bar);
		pbVolume.setMax(100);
		
		preBtn = (Button) findViewById(R.id.pre_sentence_btn);
		preBtn.setOnClickListener(clickListener);
		
		nextBtn = (Button) findViewById(R.id.next_sentence_btn);
		nextBtn.setOnClickListener(clickListener);
		recordBtn = (Button) findViewById(R.id.record_btn);
		recordBtn.setOnClickListener(clickListener);
		recordBtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
					mUserWords = null;
					mUserWord = null;
					isRecording = true;
					
					recognizer.setOralText(orals_my[oralIndex]);					
					oralSrcTextView.setText(orals_my[oralIndex]);
					textviewSorce.setText("");
					recognizer.setOption(20, true);
					recognizer.start();
					initRecordingDialog.show();
				}else if(arg1.getAction() == MotionEvent.ACTION_UP){
					isRecording = false;
					recognizer.stop();
					processingDialog.show();
				}
				return false;
			}
		});
		
		
		initRecordingDialog = new ProgressDialog(this);
		initRecordingDialog.setMessage("正在初始化...");
		initRecordingDialog.setCancelable(false);
		
		processingDialog = new ProgressDialog(this);
		processingDialog.setMessage("正在识别...");
		processingDialog.setCancelable(false);
		
		
		btn_palyall = (Button)this.findViewById(R.id.btn_palyall);
		btn_palyall.setOnClickListener(clickListener);
		btn_palyword = (Button)this.findViewById(R.id.btn_palyword);
		btn_palyword.setOnClickListener(clickListener);
		
		
	}
	
	private void readOralText(){
		InputStream in = this.getResources().openRawResource(R.raw.sample);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String str = null;
		try {
			while ((str = br.readLine()) != null) {
				orals.add(str);
			}
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}finally{
			try {
				br.close();
				in.close();
			} catch (IOException e) {
				
			}
			
		}
	}
	
	private void reset(){
		initRecordingDialog.dismiss();
		processingDialog.dismiss();
		isRecording = false;
	}

	private View.OnClickListener clickListener = new View.OnClickListener(){

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.pre_sentence_btn:
				if(oralIndex > 0){
					oralSrcTextView.setText(orals_my[--oralIndex]);
					mUserWords = null;
					mUserWord = null;
				}else{
					Toast.makeText(More_text.this, "没有上一句了", 0).show();
				}
				
				break;
			case R.id.next_sentence_btn:
				if(oralIndex < orals.size()-1){
					oralSrcTextView.setText(orals_my[++oralIndex]);
					mUserWords = null;
					mUserWord = null;
				}else{
					Toast.makeText(More_text.this, "没有下一句了", 0).show();
				}
				
				break;
			case R.id.record_btn:
				
				if(isRecording){
					
					isRecording = false;
					recognizer.stop();
					processingDialog.show();
					
				}else{
					
					isRecording = true;
					
					recognizer.setOralText(oralSrcTextView.getText().toString());					
					
					recognizer.start();
					initRecordingDialog.show();
														
				}
			case R.id.btn_palyall:
				mPlayPcmThread = new PlayPcmThread(mDir + "/sound/" + oralIndex + ".pcm");
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
			case R.id.btn_palyword:
				mPlayPcmThread = new PlayPcmThread(mDir + "/sound/"
						+ oralIndex + ".pcm");
				int size = mUserWord.mSyllables.size();
				
				Syllable head = mUserWord.mSyllables.get(0);
				Syllable tail = mUserWord.mSyllables.get(size - 1);
				
				Log.e("--Syllable","size:"+size+"head:"+(head.mBegin)+head.mText+"tail:"+(tail.mEnd)+tail.mText);
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
			default:
				break;
			}
		}
		
	};

	StringBuffer stringBuffer = new StringBuffer();
	 
	private USCRecognizerListener recognizerListener = new USCRecognizerListener(){
		@Override
		public void onEnd(USCError error) {
			Log.e(TAG, stringBuffer.toString());
			reset();
			List<byte[]> audioData = (List<byte[]>) recognizer.getOption(20);
			
			Log.e(TAG, "录音文件为空:"+audioData.size());
			//将录音文件保存到文件
			if (audioData != null) {
				String filename = mDir + "/sound/" + oralIndex + ".pcm";
				writeWavFile(filename,audioData);
			}else {
				Log.e(TAG, "录音文件为空");
			}
			
			
			if(error != null){
				Toast.makeText(More_text.this, "出错: "+error, Toast.LENGTH_SHORT).show();
			}else{
	
				
				String result = stringBuffer.toString().trim();
				stringBuffer.delete(0, stringBuffer.length());//清空StringBuffer便于下次数据的读取
				JSONObject obj;
				try {
					obj = new JSONObject(result);
					JSONArray array = obj.getJSONArray("lines");
					JSONObject jsonObject = array.getJSONObject(0);
					final long score = jsonObject.getLong("score");
					JSONArray array2 = jsonObject.getJSONArray("words");
					int size = array2.length(); 
					Word[] words = new Word[size];
					
					for(int i = 0; i < size; i++)
					{
						JSONObject jw = array2.getJSONObject(i);
							words[i] = new Word();
							Log.e(TAG, "begin:"+jw.getDouble("begin")+"end:"+ jw.getDouble("end") );
							words[i].addSyllable(jw.getString("text"), (jw.getLong("score")*10%10 >= 5 ? (int)(jw.getLong("score") + 1): (int)(jw.getLong("score"))),(long) (jw.getDouble("begin")*1000), (long) (jw.getDouble("end")*1000));
						
						
					}
					mUserWords = words;
				final SpannableString ss = SSBuilder.buildString(mUserWords);
				
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {

								int offset = 0;
								for (int i = 0; i < mUserWords.length; i++) {
									offset += mUserWords[i].vsize();
										mUserWord = mUserWords[i];
										SpannableString ss = SSBuilder.buildString(mUserWords);
										ss.setSpan(mFocusSpan, offset - mUserWords[i].vsize(), offset, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
										oralSrcTextView.setText(ss);
										textviewSorce.setText("总分："+score);
									offset += 1;
								}
						
						}
					});
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				
				if(result.length() == 0){
					Toast.makeText(More_text.this, "未按文本朗读", 0).show();
				}
			}
			
		}

		@Override
		public void onResult(String result, boolean isLast) {
			
			if(result != null){
				stringBuffer.append(result);
			}
			
		}

		@Override
		public void onUpdateVolume(int volume) {
			pbVolume.setProgress(volume);
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
			
			
		};
		
	};
	
	private void writeWavFile(String recordingDir, List<byte[]> audioData) {
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
		// 填入参数，比特率等等。这里用的是16位单声道 16000 hz
		WaveHeader header = new WaveHeader();
		// 长度字段 = 内容的大小（PCMSize) + 头部字段的大小(不包括前面4字节的标识符RIFF以及fileLength本身的4字节)
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
			// write header
			if (h != null) {
				bos.write(h, 0, h.length);
			}
			// write data stream
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
