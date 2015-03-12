package cn.yunzhisheng.demo.ui;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import cn.yunzhisheng.common.FileTool;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.demo.R;
import cn.yunzhisheng.demo.file.Filesize;
import cn.yunzhisheng.demo.view.MicrophoneControl;
import cn.yunzhisheng.demo.view.DemoPrivatePreference;
import cn.yunzhisheng.tts.offline.basic.ITTSControl;
import cn.yunzhisheng.tts.offline.basic.TTSFactory;
import cn.yunzhisheng.tts.offline.basic.TTSPlayerListener;

import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
import com.google.code.microlog4android.config.PropertyConfigurator;
import com.unisound.voiceprint.offline.core.ErrorUtil;
import com.unisound.voiceprint.offline.core.VoicePrintOfflineManager;
import com.unisound.voiceprint.offline.listener.IBuildListener;
import com.unisound.voiceprint.offline.listener.ILoginListener;
import com.unisound.voiceprint.offline.listener.IRegisterListener;


public class Voiceprint_part extends Activity implements TTSPlayerListener{
	private static final Logger logger = LoggerFactory.getLogger(); 
	private static final String TAG = "Voiceprint_part" ; 
	private ViewPager mViewPager;
	private ImageView mImageView;
	private TextView mRegTextView, mLoginTextView;
	private View mRegView, mLoginview;
	private List<View> mViews;
	private int mOffSet = 0;
	private int mCurrIndex = 0;
	
	private ImageView mImageViewt;
	private TextView mText;
	
	private Button mButton;
	private TextView mYLText;
	private TextView mInformationText;
	private ModelDB mDataBase;
	private String mCode;
	private Context mContext;
	private Dialog mDialog;
	private int mFeatureIndex;
	private int mFeatureCount;
	
	private TextView mTextViewResult;
	
	private Button mButtonRecord;
	
	private Dialog mDialog2;
	private byte[] mModel;
	
	
	private ImageView mImageViewSucces;

	private AlphaAnimation alphaAnimation1;
	private ImageView mImageViewForSusses;
	
	
	private MicrophoneControl mMicrophoneControl;
	
	private LinearLayout mlLinearLayout;
	
	private  int mViewWidth;
	
	private TextView infortext;
	public Vibrator mVibrator;
	
	private String mGolblePath;
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {  
			mImageViewForSusses.clearAnimation();
			mImageViewForSusses.setVisibility(View.INVISIBLE);
			mYLText.setVisibility(View.INVISIBLE);
			mTextViewResult.setVisibility(View.INVISIBLE);
			mImageViewSucces.setVisibility(View.INVISIBLE);
	    } 
	};
	
	private ITTSControl mTTSPlayer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.voiceprint_mian);
		PropertyConfigurator.getConfigurator(this).configure();   
		final Dialog dialog = LoadingDialog.createLoadingDialog(this, "正在初始化,请稍后...",false);
		dialog.show();
		mVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);// /
		mContext = Voiceprint_part.this;
		mCode = "语音无处不在";
		
		infortext = (TextView)findViewById(R.id.infortext);
		initView();
		initListener();
		
		mImageViewt = (ImageView)findViewById(R.id.logo_image);
		mImageViewt.setOnClickListener(new OnClickListener() {
			
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
		
		mDataBase = new ModelDB(this);
		
		mMicrophoneControl = (MicrophoneControl) findViewById(R.id.microphoneControl);

		mMicrophoneControl.onUnaviable();
		mMicrophoneControl.setEnabled(true);
		mGolblePath = mContext.getFilesDir().getAbsolutePath() + File.separator + ".timesafervpdemo/";

		new Thread(new Runnable() {

			@Override
			public void run() {
				mTTSPlayer.initTTSEngine(getApplicationContext());
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						dialog.dismiss();
					}
				});
			}
		}).start();
		VoicePrintOfflineManager.getInstance(mContext).build(mContext, mGolblePath, new IBuildListener() {
			
			@Override
			public void onBuildError(String error) {
				Toast.makeText(mContext, "加载模型失败 error : " + error, Toast.LENGTH_LONG).show();
				logger.debug( TAG + "加载模型失败 error : " + error); 
				dialog.dismiss();
			}
			
			@Override
			public void onBuildEnd() {
				logger.debug(TAG +  "加载模型成功");
				dialog.dismiss();
			}
		});
		
		mFeatureCount = 4;
		updateNotice();
		VoicePrintOfflineManager.getInstance(mContext).initRegister();//注册声纹初始化
		
		mMicrophoneControl.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
					mMicrophoneControl.onAviable();
					mMicrophoneControl.setEnabled(true);
					mMicrophoneControl.onRender();
					infortext.setText("正在倾听中...");
					if (mCurrIndex == 0) {
						mTTSPlayer.stop();
						mYLText.setText("");
						mButton.setText("松手停止");
						

						//开始声纹的注册
						VoicePrintOfflineManager.getInstance(mContext).startRegister(
								mFeatureIndex == mFeatureCount - 1,
								new IRegisterListener() {

									@Override
									public void onRegisterEnd(byte[] model) {
										mFeatureIndex++;
										if (model == null) {
											runOnUiThread(new Runnable() {
												@Override
												public void run() {
													if (mDialog != null) {
														mDialog.dismiss();
													}
													
													mYLText.setVisibility(View.VISIBLE);
													SpannableStringBuilder ssb = new SpannableStringBuilder("注册成功，请继续");
													ssb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.dark_green)), 0, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
													mYLText.setText(ssb);
													updateNotice();
												}
											});
										} else {
											String mPath = mGolblePath + mCode + "_" + System.currentTimeMillis() + ".mdl";
											saveModelFile(mPath, model);
											saveModelPath(mCode, mPath);
											runOnUiThread(new Runnable() {
												@Override
												public void run() {
													if (mDialog != null) {
														mDialog.dismiss();
													}
													mTTSPlayer.play("声纹注册成功");
													if (mVibrator != null) {
														mVibrator.vibrate(300);
													}
													Toast.makeText(mContext, "声纹注册成功",
															Toast.LENGTH_SHORT).show();
													VoicePrintOfflineManager.getInstance(
															mContext).releaseRegister();
													mFeatureIndex = 0;
													updateNotice();
													mViewPager.setCurrentItem(1);
												}
											});
										}
										
									}

									@Override
									public void onRegisterError(final ErrorUtil errorUtil) {
										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												if (mDialog != null) {
													mDialog.dismiss();
												}
												mYLText.setVisibility(View.VISIBLE);
												logger.debug( TAG + "注册失败:" + errorUtil.message);
												SpannableStringBuilder ssb = new SpannableStringBuilder("注册失败:" + errorUtil.message);
												ssb.setSpan(new ForegroundColorSpan(Color.RED), 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
												mYLText.setText(ssb);
												updateNotice();
												
											}
										});
									}

									@Override
									public void onRecordVolumeUpdate(final int volume) {
										runOnUiThread(new Runnable() {

											@Override
											public void run() {
												DemoPrivatePreference.mRecordingVoiceVolumn = (float) volume;
												if (mDialog != null) {
													mDialog.dismiss();
												}
											}
										});
									}

									@Override
									public void onRecordEnd(byte[] data) {//得到录音数据
										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												if (mDialog != null) {
													mDialog.show();
												}
												mMicrophoneControl.onAviable();
												mMicrophoneControl.setEnabled(true);
											}
										});
										
										 
									}

									@Override
									public void onRecordError(final ErrorUtil errorUtil) {
										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												if (mDialog != null) {
													mDialog.dismiss();
												}
												mYLText.setVisibility(View.VISIBLE);
												logger.debug( TAG + "录音失败:" + errorUtil.message);
												SpannableStringBuilder ssb = new SpannableStringBuilder("录音失败:" + errorUtil.message);
												ssb.setSpan(new ForegroundColorSpan(Color.RED), 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
												mYLText.setText(ssb);
												mMicrophoneControl.onAviable();
												mMicrophoneControl.setEnabled(true);
											}
										});
									}
								});
						
					}else {
						mTTSPlayer.stop();
						mTextViewResult.setText("");
						mButtonRecord.setText("松手停止");
						VoicePrintOfflineManager.getInstance(mContext).startLogin(mModel, new ILoginListener() {
							
							@Override
							public void onRecordVolumeUpdate(final int volume) {
								runOnUiThread(new Runnable() {
									
									@Override
									public void run() {
										DemoPrivatePreference.mRecordingVoiceVolumn = (float) volume;
										if (mDialog2 != null) {
											mDialog2.dismiss();
										}
									}
								});
							}
							
							@Override
							public void onRecordError(final ErrorUtil errorUtil) {
								runOnUiThread(new Runnable() {
									
									@Override
									public void run() {
										if (mDialog2 != null) {
											mDialog2.dismiss();
										}
										mTextViewResult.setVisibility(View.VISIBLE);
										logger.debug( TAG + "录音失败:" + errorUtil.message);
										SpannableStringBuilder ssb = new SpannableStringBuilder("录音失败:" + errorUtil.message);
										ssb.setSpan(new ForegroundColorSpan(Color.RED), 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
										mTextViewResult.setText(ssb);
										mMicrophoneControl.onAviable();
										mMicrophoneControl.setEnabled(true);
										
									}
								});
							}
							
							@Override
							public void onRecordEnd(byte[] data) {
								runOnUiThread(new Runnable() {
									
									@Override
									public void run() {
										if (mDialog2 != null) {
											mDialog2.show();
										}
										mMicrophoneControl.onAviable();
										mMicrophoneControl.setEnabled(true);
									}
								});
							}
							
							@Override
							public void onLoginError(final ErrorUtil errorUtil) {
								runOnUiThread(new Runnable() {
									
									@Override
									public void run() {
										if (mDialog2 != null) {
											mDialog2.dismiss();
										}
										mTextViewResult.setVisibility(View.VISIBLE);
										logger.debug( "录音失败:" + errorUtil.message);
										SpannableStringBuilder ssb = new SpannableStringBuilder("登录失败 :" + errorUtil.message);
										ssb.setSpan(new ForegroundColorSpan(Color.RED), 0, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
										mTextViewResult.setText(ssb);
										mMicrophoneControl.onAviable();
										mMicrophoneControl.setEnabled(true);
									}
								});
							}
							
							@Override
							public void onLoginEnd(final float score, byte[] adaptedModel) {
								runOnUiThread(new Runnable() {
									
									@Override
									public void run() {
										if (mDialog2 != null) {
											mDialog2.dismiss();
										}
										mMicrophoneControl.onAviable();
										mMicrophoneControl.setEnabled(true);
										LogUtil.d(TAG, "score : " + score);
										if(score >= 1.60f){
											mTTSPlayer.play("登录成功");
											if (mVibrator != null) {
												mVibrator.vibrate(300);
											}
											mTextViewResult.setVisibility(View.VISIBLE);
											SpannableStringBuilder ssb = new SpannableStringBuilder("登录成功");
											ssb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.dark_green)), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
											mTextViewResult.setText(ssb);
											mImageViewSucces.setVisibility(View.VISIBLE);
											mImageViewForSusses.setVisibility(View.VISIBLE);
											alphaAnimation1 = new AlphaAnimation(0.1f, 1.0f);
									        alphaAnimation1.setDuration(300);
									        alphaAnimation1.setRepeatCount(Animation.INFINITE);
									        alphaAnimation1.setRepeatMode(Animation.REVERSE);
									        mImageViewForSusses.setAnimation(alphaAnimation1);
											alphaAnimation1.start();
											Message message = new Message();
											mHandler.sendMessageDelayed(message, 2500);
											
										}else{
											mTextViewResult.setVisibility(View.VISIBLE);
											SpannableStringBuilder ssb = new SpannableStringBuilder("登录失败");
											ssb.setSpan(new ForegroundColorSpan(Color.RED), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
											mTextViewResult.setText(ssb);
										}
									}
								});
							}
						});
						
					}
						
				} else if (arg1.getAction() == MotionEvent.ACTION_UP) {
						mMicrophoneControl.onProcess();
						mMicrophoneControl.setEnabled(true);
						infortext.setText("按住说话");
						if (mCurrIndex == 0) {
							runOnUiThread(new  Runnable() {
								public void run() {
									mDialog = LoadingDialog.createLoadingDialog(Voiceprint_part.this, "正在注册声纹,请稍后...",true);
									VoicePrintOfflineManager.getInstance(mContext).stopRegister();
									mYLText.setText("");
									mButton.setText("按住说话");
								}
							});
							
						}else {
							runOnUiThread(new Runnable() {
								public void run() {
									mDialog2 = LoadingDialog.createLoadingDialog(Voiceprint_part.this, "正在验证声纹,请稍后...",true);
									VoicePrintOfflineManager.getInstance(Voiceprint_part.this).stopLogin();
									mButtonRecord.setText("按住说话");
								}
							});
						}
				}

				return true;
			}
		});
		
	}
	
	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.vPager);
		mImageView = (ImageView) findViewById(R.id.cursor);

		mRegTextView = (TextView) findViewById(R.id.regText);
		mLoginTextView = (TextView) findViewById(R.id.loginText);

		LayoutInflater layoutInflater = getLayoutInflater();
		mRegView = layoutInflater.inflate(R.layout.fragment_voiceprint_register, null);
		mLoginview = layoutInflater.inflate(R.layout.fragment_voiceprint_login, null);

		mViews = new ArrayList<View>();
		mViews.add(mRegView);
		mViews.add(mLoginview);
		mlLinearLayout = (LinearLayout)findViewById(R.id.contentLayout);
		
		
		DisplayMetrics displayMetrics=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int screenW = displayMetrics.widthPixels;

		mRegTextView.getWidth();
		mOffSet=((screenW - getResources().getDimensionPixelSize(R.dimen.voiceprint)*2)/2) / 2;
		LayoutParams params = (LayoutParams) mImageView.getLayoutParams();
		params.width = ((screenW - getResources().getDimensionPixelSize(R.dimen.voiceprint)*2)) / 2;
		mImageView.setLayoutParams(params);
		Button btn_deleteButton = (Button)mRegView.findViewById(R.id.btn_delete);
		btn_deleteButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (VoicePrintOfflineManager.getInstance(Voiceprint_part.this).deleteWav() ==4000) {
					mFeatureIndex --;
					updateNotice();
				}
				
			}
		});
		mButton = (Button) mRegView.findViewById(R.id.buttonswzc1);
		mYLText = (TextView) mRegView.findViewById(R.id.yinliang);
		mInformationText = (TextView) mRegView.findViewById(R.id.informaton);
		
		mImageViewSucces = (ImageView)mLoginview.findViewById(R.id.imageview_success);
		mImageViewForSusses = (ImageView)mLoginview.findViewById(R.id.imageview1);
		mTextViewResult = (TextView) mLoginview.findViewById(R.id.loginResult);
		mButtonRecord = (Button) mLoginview.findViewById(R.id.btnRecord);
		mButtonRecord.setText("按住说话");
		mButtonRecord.setEnabled(false);
		
		mTTSPlayer = TTSFactory.createTTSControl(this, "appkey");
		mTTSPlayer.setTTSListener(this);
		
	}

	private void initListener() {
		mRegTextView.setOnClickListener(new MyOnClickListener(0));
		mLoginTextView.setOnClickListener(new MyOnClickListener(1));

		mViewPager.setAdapter(new MyPagerAdapter(mViews));
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		mViewPager.setCurrentItem(0);
	}

	private class MyPagerAdapter extends PagerAdapter {
		private List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mListViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mListViews.get(position));
			return mListViews.get(position);
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

	private class MyOnPageChangeListener implements OnPageChangeListener {
		int one = mOffSet * 2;
		
		public void onPageScrollStateChanged(int arg0) {
			mYLText.setText("");
			mTextViewResult.setText("");
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
			

		}

		public void onPageSelected(int arg0) {
			Animation animation = new TranslateAnimation(one*mCurrIndex, one*arg0 , 0, 0);
			mCurrIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(300);
			mImageView.startAnimation(animation);
			if (arg0 == 1) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						String path = loadModelPath(mCode);
						if(path != null && path.length() > 0){
							mModel = loadModelFile(path);
						}
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								if(mModel == null)
								{
									mButtonRecord.setTextColor(Color.argb(75, 255, 255, 255));
									Toast.makeText(mContext, "您还没有注册声纹", Toast.LENGTH_SHORT).show();
									mTTSPlayer.play("您还没有注册声纹");
									mMicrophoneControl.setEnabled(false);
								}
								else
								{
									
									mMicrophoneControl.onAviable();
									mMicrophoneControl.setEnabled(true);
									mButtonRecord.setEnabled(true);
									mButtonRecord.setTextColor(Color.argb(255, 255, 255, 255));
								}
							}
						});
					}
				}).start();
				
			}else {
				mTTSPlayer.stop();
				mMicrophoneControl.onAviable();
				mMicrophoneControl.setEnabled(true);
			}
				
		}

	}

	private class MyOnClickListener implements OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		public void onClick(View v) {
			mViewPager.setCurrentItem(index);
		}
	}
	

	private void updateNotice() {
		String notice = "请按住下方按钮，朗读出口令进行注册\n";
		notice = notice + (mFeatureIndex + 1) + "/" + mFeatureCount;
		mInformationText.setText(notice);
		mMicrophoneControl.onAviable();
		mMicrophoneControl.setEnabled(true);
	}
	private boolean saveModelFile(String path, byte[] data)
	{
		File dir = new File(path).getParentFile();
		if(!dir.exists())
			dir.mkdirs();
		boolean success = false;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(path);
			fos.write(data);
			success = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			if(fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return success;
	}
	
	private void saveModelPath(String code, String path)
	{
		SQLiteDatabase db = mDataBase.getReadableDatabase();
		if (db == null) {
		}else {
			Cursor c = db.query(ModelDB.MODEL_TABLE_NAME, new String[]{ModelDB.MODEL_ID ,ModelDB.MODEL_CODE}, ModelDB.MODEL_CODE + "=?", new String[]{code}, null, null, null);
			int count = c.getCount();
			if(count == 0)
			{
				ContentValues values = new ContentValues(2);
				values.put(ModelDB.MODEL_CODE, code);
				values.put(ModelDB.MODEL_PATH, path);
				db.insert(ModelDB.MODEL_TABLE_NAME, null, values);
			}
			else
			{
				ContentValues values = new ContentValues(1);
				values.put(ModelDB.MODEL_PATH, path);
				db.update(ModelDB.MODEL_TABLE_NAME, values, ModelDB.MODEL_CODE + "=?", new String[]{code});
			}
			
			c.close();
		}
		
		db.close();
	}
	
	
	private byte[] loadModelFile(String path)
	{
		byte[] model = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[10240];
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(path);
			int len = 0;
			while((len = fis.read(buffer)) != -1)
			{
				baos.write(buffer, 0, len);
			}
			model = baos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			if(fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			try {
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return model;
	}
	
	private String loadModelPath(String code)
	{
		String path = null;
		SQLiteDatabase db = mDataBase.getReadableDatabase();
		Cursor c = db.query(ModelDB.MODEL_TABLE_NAME, new String[]{ModelDB.MODEL_ID ,ModelDB.MODEL_PATH}, ModelDB.MODEL_CODE + "=?", new String[]{code}, null, null, null);
		int count = c.getCount();
		if(count > 0)
		{
			c.moveToFirst();
			path = c.getString(c.getColumnIndex(ModelDB.MODEL_PATH));
		}
		c.close();
		db.close();
		return path;
	}

	@Override
	public void onBuffer() {
		
	}

	@Override
	public void onCancel() {
		
	}

	@Override
	public void onPlayBegin() {
		
	}

	@Override
	public void onPlayEnd() {
		
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mTTSPlayer.releaseTTSEngine();
	}


}
