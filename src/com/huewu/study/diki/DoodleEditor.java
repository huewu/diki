package com.huewu.study.diki;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.inputmethodservice.InputMethodService;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.huewu.study.diki.view.ImageEditorView;
import com.huewu.study.diki.view.ImageNaviView;

public class DoodleEditor extends SherlockActivity implements TabListener, OnTouchListener{
	
	private static final String TAG = "DoodleEditor";

	private ImageEditorView mEditor = null;
	private RelativeLayout mContainer = null;
	private ViewFlipper mFlipper = null;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.doodle_editor);
		
		mContainer = (RelativeLayout) findViewById(R.id.container);
		mFlipper = (ViewFlipper) findViewById(R.id.edit_ctx_pannel);
		mEditor = (ImageEditorView) findViewById(R.id.editor);
		mEditor.setClickable(true);
		mEditor.setLongClickable(true);
		
		mEditor.setOnTouchListener(this);
		
		getSupportActionBar().setIcon(new ColorDrawable(Color.TRANSPARENT));
		getSupportActionBar().setTitle("Doodle Editor");
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.navibar_tile));
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);		
		
		Tab tab = null;
		tab = getSupportActionBar().newTab();
		tab.setText("Edit");
		tab.setTabListener(this);
		getSupportActionBar().addTab(tab);
				
		tab = getSupportActionBar().newTab();
		tab.setText("Drawing");
		tab.setTabListener(this);
		getSupportActionBar().addTab(tab);				
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
	protected void onPause() {
		super.onPause();
		mEditor.setImageBitmap(null);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}	
	
	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		
        menu.add("Save")
        	.setIcon(android.R.drawable.ic_menu_send)
        	.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mFlipper.setDisplayedChild( tab.getPosition() );
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent ev) {
		//handle editor touch event.
		int idx = getSupportActionBar().getSelectedNavigationIndex();
		switch(idx)
		{
		case 0:	//edit & text
		{
			EditText edit = new EditText(this);
			edit.setSingleLine();
			edit.setText("Temp Text");
			RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			param.leftMargin = (int) ev.getX();
			param.topMargin = (int) ev.getY();
			edit.setLayoutParams(param);
			mContainer.addView(edit);
			edit.requestFocus();
			
			InputMethodManager ims = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			// only will trigger it if no physical keyboard is open
			ims.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT);			
		}
			break;
		case 1: //draw
		{
			mEditor.setMode(ImageEditorView.MODE_NAVI);
		}
			break;
		case 2:	//draw
			break;
		}
		return false;
	}

}//end of class
