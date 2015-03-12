/**
 * Copyright (c) 2012-2012 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : LinedEditText.java
 * @ProjectName : V Plus 1.0
 * @PakageName : cn.yunzhisheng.ishuoshuo.component
 * @Author : Brant
 * @CreateDate : 2012-6-15
 */
package cn.yunzhisheng.demo.view;
import cn.yunzhisheng.demo.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

public class LinedTextView extends TextView {

	public static final String TAG = "LinedTextView";
	private Rect mRect;
	private Paint mPaint;
	private int mDrawLineMode;
	public LinedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mRect = new Rect();
		mPaint = new Paint();

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LinedTextView);

//		int color = a.getColor(R.styleable.LinedTextView_line_color, 0XFFFFF333);
		int color = a.getColor(R.styleable.LinedTextView_line_color, 0XFFA0A0A0);
		float stokeWidth = a.getDimension(R.styleable.LinedTextView_line_stoke_width, 2);
		float dashOnWidth = a.getDimension(R.styleable.LinedTextView_line_dash_on_width, 2);
		float dashOffWidth = a.getDimension(R.styleable.LinedTextView_line_dash_off_width, 2);
		mDrawLineMode = a.getInt(R.styleable.LinedTextView_lines_according, 2);
		a.recycle();
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(stokeWidth);
		PathEffect effects = new DashPathEffect(new float[] { dashOnWidth, dashOffWidth }, 1);
		mPaint.setPathEffect(effects);
		mPaint.setColor(color);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int count = getLineCount();
		int lineHeight = getLineHeight();
		Rect r = mRect;
		Paint paint = mPaint;
		int baseline = 0;
		for (int i = 0; i < count; i++) {
			baseline = getLineBounds(i, r) + r.bottom - (i + 1) * lineHeight;
			canvas.drawLine(r.left, baseline, r.right, baseline, paint);
		}

		if (mDrawLineMode == 2) {
			int lines = (getHeight() - getPaddingTop() - getPaddingBottom()) / lineHeight;
			for (int i = count; i < lines; i++) {
				baseline += lineHeight;
				canvas.drawLine(r.left, baseline, r.right, baseline, paint);
			}
		}
		super.onDraw(canvas);
	}
}
