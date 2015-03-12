package cn.yunzhisheng.demo.view;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

public class MyEditView extends TextView{

	public MyEditView(Context context) {
		super(context);
	}
	
	public MyEditView(Context context, AttributeSet attributeSet){
		super(context, attributeSet);
	}

	protected void onDraw(Canvas canvas){
		  int lineHeight=this.getLineHeight();
		  Paint mPaint=getPaint();
		  mPaint.setColor(Color.GRAY);//文本编辑线
		  int topPadding=this.getPaddingTop();
		  int leftPadding=this.getPaddingLeft();
		  float textSize=getTextSize();
		  setGravity(Gravity.LEFT|Gravity.TOP);
		  int y =(int)(topPadding+textSize+10);
//		  for(int i=0;i<getLineCount();i++){
		  for(int i=0;i<20;i++){
			  canvas.drawLine(leftPadding, y+20, getRight()-leftPadding, y+20, mPaint);
		   		y+=lineHeight;
		  }
		  canvas.translate(0, 0);
		  super.onDraw(canvas);
		 }
}
