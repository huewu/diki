package com.huewu.study.diki.view;

import java.util.ArrayList;

import com.huewu.study.diki.view.ImageEditorGestureDetector.OnGestureListener;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ImageEditorView extends ImageView {
	
	public static final int MODE_MOVE = 1001;
	public static final int MODE_PEN = 1002;
	public static final int MODE_TEXT = 1003;
	
	public enum EditMode
	{
		Move, Pen, Text
	}

	protected static final String TAG = "ImageEditorView";
	
	private float mScaleFactor = 1.f;
	
	private PointF mCurPos = new PointF();
	private ImageEditorGestureDetector mMoveDetector = null;
	private ImageEditorGestureDetector mTextDetector = null;
	private ImageEditorGestureDetector mDrawDetector = null;
	
	private EditMode mMode = EditMode.Move;
	private MotionEvent mLastMotionEvent = null;
	
	private class Line
	{
		float fromX = 0f;
		float fromY = 0f;
		float toX = 0f;
		float toY = 0f;
		
		int color = Color.BLACK;
		int width = 3;
	}
	
	private ArrayList<Line> mLines = new ArrayList<Line>();
	
	public ImageEditorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mMoveDetector = ImageEditorGestureDetector.newInstance(context, new OnGestureListener() {
			
			private float mScalePivotX = 0.f;
			private float mScalePivotY = 0.f;

			@Override
			public void onScale(float scaleFactor, float px, float py) {
				mScaleFactor *= scaleFactor;
				mScaleFactor = Math.max( mScaleFactor, 0.5f );
				mScaleFactor = Math.min( mScaleFactor, 5.f );
				
				mScalePivotX = px;
				mScalePivotY = py;
				invalidate();
			}
			
			@Override
			public void onDrag(float dx, float dy) {
				mCurPos.x += dx;
				mCurPos.y += dy;
				invalidate();
			}
		});
		
		mDrawDetector = ImageEditorGestureDetector.newInstance(context, new OnGestureListener(){

			@Override
			public void onDrag(float dx, float dy) {
				Line l = new Line();
				l.fromX = mDrawDetector.getLastTouchX();
				l.fromY = mDrawDetector.getLastTouchY(); 
				l.toX = l.fromX + dx;
				l.toY = l.fromY + dy;
				mLines.add(l);
				invalidate();
			}

			@Override
			public void onScale(float scaleFactor, float px, float py) {
			}
			
		});
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mLastMotionEvent = event;
		
		switch( mMode )
		{
		case Move:
			mMoveDetector.onTouchEvent(event);
			break;
		case Pen:
			mDrawDetector.onTouchEvent(event);
			break;
		case Text:
			break;
		}

		return super.onTouchEvent(event);
	}
	
	public MotionEvent getLastMotionEvent()
	{
		return mLastMotionEvent;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		Matrix matrix = getImageMatrix();
		float[] values = new float[9];
		matrix.getValues(values);

		float oriScaleFacator = values[0];
		canvas.save();

		//set align
		canvas.translate(values[2], values[5] + 10);
		canvas.scale(values[0], values[4]);

		//apply positio & scale info.
		canvas.translate(mCurPos.x / oriScaleFacator, mCurPos.y / oriScaleFacator );
		canvas.scale(mScaleFactor, mScaleFactor);

		//draw a origin bitmap
		Drawable d = getDrawable();
		if( d != null )
			d.draw(canvas);
		
		//draw pens.
		canvas.restore();
		drawPens(canvas, 0, 0 );
	}

	private void drawPens(Canvas canvas, float offX, float offY ) {
		Paint p = new Paint();
		p.setColor(Color.RED);
		p.setStrokeWidth(8.f);
		
		canvas.drawLine(0f, 0f, 100f, 100f, p);
		
		for( Line l : mLines )
		{
			canvas.drawLine(l.fromX, l.fromY, l.toX, l.toY, p);
		}
	}

	public void setMode(EditMode mode) {
		mMode = mode;
	}
	
	@Override
	public void setImageURI(Uri uri) {
		
		mCurPos = new PointF();
		
		String path = getFilePathFromUri(uri);
		if( path == null )
			return;

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
		
		int ratio = getProperImageSize( opts.outWidth, opts.outHeight );
		
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = ratio;
		
		Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
		
		super.setImageBitmap(bitmap);
	}

	private int getProperImageSize(int outWidth, int outHeight) {
		int properResizeRatio = Math.max(outWidth / 800, outHeight / 800);
		if( properResizeRatio % 2 != 0 )
			properResizeRatio += 1;
		return properResizeRatio;
	}

	private String getFilePathFromUri(Uri uri) {
		Cursor c = getContext().getContentResolver().query(
				uri, 
				new String[]{ MediaStore.MediaColumns.DATA },
				null, null, null );
		
		if( c.moveToNext() == false )
		{
			c.close();
			return null;
		}
		
		String filePath = c.getString(0);
		c.close();
		return filePath;
	}

}//end of class
