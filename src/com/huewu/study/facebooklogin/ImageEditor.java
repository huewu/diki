package com.huewu.study.facebooklogin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class ImageEditor extends Activity {
	
	private static final String TAG = "ImageEditor";

	private ImageEditorView mEditor = null;
	private FrameLayout mContainer = null;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Save");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		mContainer.setClickable(true);
		
		//save view.
		mContainer.setDrawingCacheEnabled(true);
		Bitmap bitmap = mContainer.getDrawingCache();
		mContainer.removeAllViewsInLayout();
		ImageView iv = new ImageView(this);
		iv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		iv.setImageBitmap(bitmap);
		mContainer.addView(iv);
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.image_editor);
		mContainer = (FrameLayout) findViewById(R.id.container);
		mEditor = (ImageEditorView) findViewById(R.id.editor);
		mEditor.setClickable(true);
		mEditor.setLongClickable(true);
		
		mEditor.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				
				PointF downPt = mEditor.getActionDownPoint();
				PointF movePt = mEditor.getActionMovePoint();
				//if domwPt ~ movePt length is smaller than some threshold.
				
				if( ! movePt.equals(0, 0) )
				{
					float difX = movePt.x - downPt.x;
					float difY = movePt.y - downPt.y;
					float len = (float) Math.sqrt(difX * difX + difY * difY);
					
					if( len >  30)	//this is not long click.
						return false;
				}
				
				EditText tv = new EditText(ImageEditor.this);
				tv.setTextSize(20);
				tv.setSingleLine();
				tv.setText("진지하다!!! 궁서체다!!!");
				FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				param.leftMargin = (int) downPt.x;
				param.topMargin = (int) downPt.y;
				tv.setLayoutParams(param);
				tv.setClickable(true);
				tv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						v.setBackgroundColor(Color.TRANSPARENT);
					}
				});
				mContainer.addView(tv);
				return false;
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		Intent i = getIntent();
		
		if( ! i.getAction().equals( Intent.ACTION_SEND) ){
			return;	//invalid intent.
		}
		Uri imageUri = i.getExtras().getParcelable(Intent.EXTRA_STREAM);
		Log.d(TAG, "ImageUri:" + imageUri );
		
		mEditor.setImageURI(imageUri);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

}//end of class
