package com.huewu.study.diki;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListActivity;
import com.huewu.study.diki.net.DoodleRequester;
import com.huewu.study.diki.view.ImageEditorView;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
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
import android.widget.ListView;

public class DoodleBrowser extends SherlockListActivity {
	
	private static final String TAG = "DoodleBrowser";
	private static final int SELECT_IMAGE = 100;
	private FacebookService mBoundService;
	
	private ServiceConnection mConn = new ServiceConnection() {
		

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBoundService = ((FacebookService.LocalBinder) service).getService();
			mBoundService.authorize(DoodleBrowser.this);
		}
	};
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.doodle_browser);

		getSupportActionBar().setIcon(new ColorDrawable(Color.TRANSPARENT));
		getSupportActionBar().setTitle("Doodle WiKi");
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.navibar_tile));
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);		
		
		Tab tab = null;
		tab = getSupportActionBar().newTab();
		tab.setText("HOT");
		getSupportActionBar().addTab(tab);
		
		tab = getSupportActionBar().newTab();
		tab.setText("My Doodle");
		getSupportActionBar().addTab(tab);
		
		tab = getSupportActionBar().newTab();
		tab.setText("Favorite");
		getSupportActionBar().addTab(tab);
		
		Intent i = new Intent(this, FacebookService.class);
		bindService(i, mConn, BIND_AUTO_CREATE);
		
		DoodleAdapter adapter = new DoodleAdapter(this);
		{
			Doodle d =  new Doodle();
			d.Name = "Doodle0";
			d.FileName = "doodle01332845408992.jpg"; 
			adapter.add(d);		
		}
		{
			Doodle d =  new Doodle();
			d.Name = "Doodle1";
			d.FileName = "doodle11332845071917.jpg"; 
			adapter.add(d);		
		}
		{
			Doodle d =  new Doodle();
			d.Name = "Doodle2";
			d.FileName = "doodle_02.jpg"; 
			adapter.add(d);		
		}
		{
			Doodle d =  new Doodle();
			d.Name = "Doodle3";
			d.FileName = "doodle_04.jpg"; 
			adapter.add(d);		
		}
		{
			Doodle d =  new Doodle();
			d.Name = "Doodle4";
			d.FileName = "doodle_07.jpg"; 
			adapter.add(d);		
		}
		{
			Doodle d =  new Doodle();
			d.Name = "Doodle5";
			d.FileName = "doodle_09.jpg"; 
			adapter.add(d);		
		}
		
		
		setListAdapter(adapter);
	}	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(mConn);
	}

	@Override
	protected void onResume() {
		super.onResume();
		DoodleRequester.requestDoodleList();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Log.i("Doodle", "Item Clicked:" + id);
		
		DoodleAdapter adpater = (DoodleAdapter) getListAdapter();
		Doodle d = adpater.getItem(position);
		ImageView thumb = (ImageView) v.findViewById(R.id.thumbnail);

		//Bitmap bitmap = bd.getBitmap();
		
		Log.i("Doodle", "Item Clicked:" + thumb);
		mBoundService.setOriginBitmap(d.URL);
		mBoundService.setOriginName(d.Name);
		Intent i = new Intent(this, DoodleEditor.class);
		startActivity(i);
	}
	
	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		
        menu.add(R.string.new_doodle)
        	.setIcon(R.drawable.btn_edit)
        	.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		
		return super.onCreateOptionsMenu(menu);
	}	
	
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		
		Intent i = new Intent(Intent.ACTION_PICK);
		i.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, SELECT_IMAGE);
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int reqCode, int resultCode, Intent arg2) {

		mBoundService.geFacebook().authorizeCallback(reqCode, resultCode, arg2);
		
		if(reqCode == SELECT_IMAGE && resultCode == Activity.RESULT_OK)
		{
			Intent i = new Intent(this, DoodleEditor.class);
			i.setData(arg2.getData());
			i.putExtra(Intent.EXTRA_STREAM, arg2.getData());
			startActivity(i);
		}
		
		
	}
	
}//end of class
