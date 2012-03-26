package com.huewu.study.diki.view;

import com.huewu.study.diki.view.ImageEditorGestureDetector.OnGestureListener;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class ImageNaviView extends ImageView {
	
	private PointF mCurPos = new PointF();
	private Paint mOutlinePainter = new Paint();
	
	public ImageNaviView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mOutlinePainter.setStrokeWidth(30);
		mOutlinePainter.setStyle(Paint.Style.STROKE);
		mOutlinePainter.setColor(Color.rgb(200, 70, 30));
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Matrix matrix = getImageMatrix();
		float[] values = new float[9];
		matrix.getValues(values);

		//super.onDraw(canvas);
		canvas.save();
		canvas.translate(values[2], values[5]);
		canvas.scale(values[0], values[4]);
		
		mOutlinePainter.setStrokeWidth( 4.f / values[0] );
		
		Drawable d = getDrawable();
		if( d != null )
		{
			d.draw(canvas);		
			canvas.drawRect(d.getBounds(), mOutlinePainter);
		}
		
		canvas.restore();
	}

}//end of class