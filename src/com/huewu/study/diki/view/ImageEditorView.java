package com.huewu.study.diki.view;

import com.huewu.study.diki.view.ImageEditorGestureDetector.OnGestureListener;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class ImageEditorView extends ImageView implements OnGestureListener{
	
	public static final int MODE_NAVI = 1001;
	public static final int MODE_DRAW = 1002;
	
	private PointF mCurPos = new PointF();
	private ImageEditorGestureDetector mDetector = null;
	
	private int mMode = MODE_NAVI;
	
	public ImageEditorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mDetector = ImageEditorGestureDetector.newInstance(context,this);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mDetector.onTouchEvent(event);
		return true;
	}

	@Override
	public void onDrag(float dx, float dy) {
		mCurPos.x += dx;
		mCurPos.y += dy;
		invalidate();
		
	}

	@Override
	public void onScale(float scaleFactor) {
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		//super.onDraw(canvas);
		canvas.save();
		canvas.translate(mCurPos.x, mCurPos.y);
		Drawable d = getDrawable();
		d.draw(canvas);
		canvas.restore();
	}

	public void setMode(int modeNavi) {
	}

}//end of class
