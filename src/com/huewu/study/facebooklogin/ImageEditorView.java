package com.huewu.study.facebooklogin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class ImageEditorView extends ImageView{
	
	private float mOriginX = 0.f;
	private float mOriginY = 0.f;
	private float mImagePosX = 0.f;
	private float mImagePosY = 0.f;
	private Matrix mMatrix = null;
	
	private PointF mActionUpPoint = new PointF();
	private PointF mActionDownPoint = new PointF();
	private PointF mActionMovePoint = new PointF();

	public ImageEditorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mMatrix = new Matrix();
		mMatrix.reset();
		
		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				switch( event.getAction() )
				{
				case MotionEvent.ACTION_DOWN:
					mOriginX = event.getX();
					mOriginY = event.getY();
					mActionDownPoint = new PointF(event.getX(), event.getY());
					mActionMovePoint.set(0, 0);
					mActionUpPoint.set(0, 0);
					break;
				case MotionEvent.ACTION_MOVE:
					mActionMovePoint = new PointF(event.getX(), event.getY());
					float diffX = event.getX() - mOriginX;
					float diffY = event.getY() - mOriginY;
					moveTo(diffX, diffY);
					break;
				case MotionEvent.ACTION_UP:
					mImagePosX = event.getX() - mOriginX;
					mImagePosY = event.getY() - mOriginY;
					mActionUpPoint = new PointF(event.getX(), event.getY());
					break;
				}
				
				return false;
			}

			private void moveTo(float diffX, float diffY) {
				//mMatrix.postTranslate(mImagePosX, mImagePosY);
				mMatrix.reset();
				mMatrix.postTranslate(diffX, diffY);
				invalidate();
			}
		});
	}
	
	public PointF getActionUpPoint()
	{
		return mActionUpPoint;
	}
	
	public PointF getActionDownPoint() {
		return mActionDownPoint;
	}
	
	public PointF getActionMovePoint() {
		return mActionMovePoint;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		setImageMatrix(mMatrix);
		super.onDraw(canvas);
		
		//do something more.
	}

}//end of class
