/**
 * Copyright (c) 2012-2012 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : MicrophoneControl.java
 * @ProjectName : V Plus 1.0
 * @PakageName : cn.yunzhisheng.vui.assistant.assistant.view
 * @Author : Brant
 * @CreateDate : 2012-5-24
 */
package cn.yunzhisheng.demo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.demo.R;

@SuppressLint("HandlerLeak")
public class MicrophoneControl extends FrameLayout {
	public static final String TAG = "MicrophoneControl";

	private MicrophoneSoundEffectRender mMicSoundRender;
	private Button mBtnMic;
	private ImageView mImageViewMicRecognizeBg;
	private ImageView mImageViewMicRecognize;
	private RotateAnimation mRotateAnimationMicRecognize;

	public MicrophoneControl(Context context) {
		this(context, null);
	}

	public MicrophoneControl(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MicrophoneControl(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		View.inflate(context, R.layout.mic_control, this);
		mBtnMic = (Button) findViewById(R.id.btnMic);
		mMicSoundRender = (MicrophoneSoundEffectRender) findViewById(R.id.micSoundRender);
		mImageViewMicRecognizeBg = (ImageView) findViewById(R.id.imageViewRecognizeBg);
		mImageViewMicRecognize = (ImageView) findViewById(R.id.imageViewRecognize);
		mRotateAnimationMicRecognize = new RotateAnimation(0, -360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateAnimationMicRecognize.setDuration(1000);
		mRotateAnimationMicRecognize.setInterpolator(new LinearInterpolator());
		mRotateAnimationMicRecognize.setRepeatCount(Animation.INFINITE);

		/*mBtnMic.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					break;
				case MotionEvent.ACTION_UP:
					break;
				default:
					break;
				}
				return false;
			}
		});*/
		
	}

	@Override
	public void setOnTouchListener(OnTouchListener l){
		mBtnMic.setOnTouchListener(l);
	}
	
	public void onDestroy() {
		mMicSoundRender.onDestroy();
		
		mMicSoundRender = null;
		mRotateAnimationMicRecognize = null;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (changed) {
			mMicSoundRender.init(getWidth(), getHeight());
		}
	}

	public void onUnaviable() {
		mBtnMic.setEnabled(false);
		mImageViewMicRecognizeBg.setVisibility(View.GONE);
		mImageViewMicRecognize.setVisibility(View.GONE);
		mMicSoundRender.onUnaviable();
	}

	public void setEnabled(boolean enable) {
		if (enable) {
			mBtnMic.setEnabled(true);
		} else {
			mBtnMic.setEnabled(false);
		}
	}

	public void onAviable() {
		mBtnMic.setVisibility(View.VISIBLE);
		mImageViewMicRecognizeBg.setVisibility(View.GONE);
		mImageViewMicRecognize.setVisibility(View.GONE);
		mImageViewMicRecognize.clearAnimation();
		mBtnMic.setEnabled(true);

		mMicSoundRender.stopVoiceAnim();
	}

	public void onProcess() {
		// 取消接收声音
		mMicSoundRender.stopVoiceAnim();
		// 开始识别
		mImageViewMicRecognizeBg.setVisibility(View.VISIBLE);
		mImageViewMicRecognize.setVisibility(View.VISIBLE);
		mImageViewMicRecognize.startAnimation(mRotateAnimationMicRecognize);
	}

	public void onRender() {
		// 开始接收声音
		mBtnMic.setEnabled(true);
		mMicSoundRender.startVoiceAnim();
		mImageViewMicRecognizeBg.setVisibility(View.VISIBLE);
		mImageViewMicRecognize.setVisibility(View.VISIBLE);
	}
}
