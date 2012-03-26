package com.huewu.study.diki;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.huewu.study.diki.view.ImageEditorView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
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

public class DoodleBrowser extends SherlockFragmentActivity {
	
	private static final String TAG = "DoodleBrowser";
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setIcon(new ColorDrawable(Color.TRANSPARENT));
		getSupportActionBar().setTitle("Doodle WiKi");
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.navibar_tile));
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);		
		
		Tab tab = null;
		tab = getSupportActionBar().newTab();
		tab.setText("A");
		getSupportActionBar().addTab(tab);
		
		tab = getSupportActionBar().newTab();
		tab.setText("B");
		getSupportActionBar().addTab(tab);
		
		tab = getSupportActionBar().newTab();
		tab.setText("C");
		getSupportActionBar().addTab(tab);
	}	

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}	
	
	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		
        menu.add(R.string.new_doodle)
        	.setIcon(R.drawable.btn_edit)
        	.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		
		return super.onCreateOptionsMenu(menu);
	}	
	
}//end of class
