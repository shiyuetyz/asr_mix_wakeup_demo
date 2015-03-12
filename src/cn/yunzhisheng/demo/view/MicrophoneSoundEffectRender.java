package cn.yunzhisheng.demo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import cn.yunzhisheng.demo.R;

public class MicrophoneSoundEffectRender extends SurfaceRunner {

	public static final String TAG = "MicrophoneSoundEffectRender";

	private Paint mPaint;
	private float mWidth, mHeight;
	private float mCenterX, mCenterY;
	private float mBmpWidth, mBmpHeight;

	private Bitmap mBackground;
	private Bitmap[] mForeground = new Bitmap[4];

	private RectF mDstRect;
	private Path mPath;

	public MicrophoneSoundEffectRender(Context app) {
		this(app, null);
	}

	public MicrophoneSoundEffectRender(Context app, AttributeSet attrs) {
		super(app, attrs);
		
		mPaint = new Paint();
		mPath = new Path();
		setZOrderOnTop(true);
		getHolder().setFormat(PixelFormat.RGBA_8888);
		setDelay(50);// 100ms
	}

	public void onUnaviable() {
		if (mBackground == null || mBackground.isRecycled()) {
			mBackground = BitmapFactory.decodeResource(getResources(), R.drawable.volume_mask);
		}
		
		if (mForeground[0] == null || mForeground[0].isRecycled()) {
			mForeground[0] = BitmapFactory.decodeResource(getResources(), R.drawable.mic_circle_1);
		}
		if (mForeground[1] == null || mForeground[1].isRecycled()) {
			mForeground[1] = BitmapFactory.decodeResource(getResources(), R.drawable.mic_circle_2);
		}
		if (mForeground[2] == null || mForeground[2].isRecycled()) {
			mForeground[2] = BitmapFactory.decodeResource(getResources(), R.drawable.mic_circle_3);
		}
		if (mForeground[3] == null || mForeground[3].isRecycled()) {
			mForeground[3] = BitmapFactory.decodeResource(getResources(), R.drawable.mic_circle_4);
		}
		mBmpWidth = mForeground[0].getWidth();
		mBmpHeight = mForeground[0].getHeight();
	}

	public void onDestroy() {
		if (mBackground != null && !mBackground.isRecycled()) {
			mBackground.recycle();
		}
		if (mForeground[0] != null && !mForeground[0].isRecycled()) {
			mForeground[0].recycle();
		}
		if (mForeground[1] != null && !mForeground[1].isRecycled()) {
			mForeground[1].recycle();
		}
		if (mForeground[2] != null && !mForeground[2].isRecycled()) {
			mForeground[2].recycle();
		}
		if (mForeground[3] != null && !mForeground[3].isRecycled()) {
			mForeground[3].recycle();
		}
	}

	public void init(float width, float height) {
		mWidth = width;
		mHeight = height;
		mCenterX = mWidth / 2;
		mCenterY = mHeight / 2;
	}

	@Override
	protected void doUpdate(long now) {
	}

	@Override
	protected void doDraw(Canvas canvas, long now) {
		if (mDstRect == null) mDstRect = new RectF(0, 0, mWidth, mHeight);
		clear(canvas);
		canvas.drawColor(0xffd9d9d9);
		canvas.save(Canvas.CLIP_SAVE_FLAG);
		float radius = mHeight * 4 / 5;
		float volume = mWidth * getVolume() / 200;
		mPath.reset();
		mPath.addCircle(mCenterX - volume, mCenterY, radius, Path.Direction.CW);
		mPath.addCircle(mCenterX + volume, mCenterY, radius, Path.Direction.CW);
		mPath.addRect(mCenterX - volume, 0, mCenterX + volume, mHeight, Path.Direction.CW);
		canvas.clipPath(mPath);
		canvas.drawColor(0xff33b5e5);
		canvas.restore();
		canvas.drawBitmap(mBackground, null, mDstRect, null);
		
		canvas.drawBitmap(mForeground[mCircleIndex], mCenterX - mBmpWidth / 2, mCenterY - mBmpHeight / 2, null);
		if(mCircleIndex < 3)
		{
			mCircleIndex++;
			if(mCircleIndex == 3)
				setDelay(100);
		}
	}

	public void clear(Canvas canvas) {
		if (mPaint != null) {
			mPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
			canvas.drawPaint(mPaint);
			mPaint.setXfermode(new PorterDuffXfermode(Mode.SRC));
		}
	}

	public static float getVolume() {
		return DemoPrivatePreference.mRecordingVoiceVolumn;
	}

	private int mCircleIndex = 0;
	
	public void startVoiceAnim() {
		setDelay(20);
		mCircleIndex = 0;
		surfaceStart();
	}

	public void stopVoiceAnim() {
		surfaceStop();
		Canvas canvas = getHolder().lockCanvas();
		if (canvas != null) {
			canvas.drawColor(0x00000000, android.graphics.PorterDuff.Mode.CLEAR);
			getHolder().unlockCanvasAndPost(canvas);
		}

	}
}
