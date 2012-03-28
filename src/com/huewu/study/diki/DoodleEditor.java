package com.huewu.study.diki;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.actionbarsherlock.app.SherlockActivity;
import com.huewu.study.diki.net.DoodleRequester;
import com.huewu.study.diki.net.DoodleRequester.CreateDoodleListener;
import com.huewu.study.diki.view.ImageEditorView;
import com.huewu.study.diki.view.ImageEditorView.EditMode;

public class DoodleEditor extends SherlockActivity implements OnTouchListener, OnCheckedChangeListener {

	private static final String TAG = "DoodleEditor";

	private static final int INPUT_TITLE_COMMENT = 1001;

	private ImageEditorView mEditor = null;
	private RelativeLayout mContainer = null;
	private RadioGroup mEditMenu = null;

	private FacebookService mBoundService;	

	private ServiceConnection mConn = new ServiceConnection() {


		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBoundService = ((FacebookService.LocalBinder) service).getService();
			mBoundService.authorize(DoodleEditor.this);
			
			if( mEditor.getDrawable() == null )
			{
				//use origin name.
				mDoodleName = mBoundService.getOriginName();
				URL mDoodleUrl = mBoundService.getOriginBitmap();
				
				new AsyncTask<URL, Object, Bitmap>()
				{
					URL mURL = null;
					@Override
					protected Bitmap doInBackground(URL... params) {
						mURL = params[0];
						
						try
						{
							Bitmap bitmap = DoodleAdapter.IMAGE_CACHE.get(mURL.toString());
							
							if( bitmap != null )
								return bitmap;
							
							URLConnection conn = mURL.openConnection();
							InputStream is = conn.getInputStream();

							bitmap = BitmapFactory.decodeStream(is);
							is.close();
							return bitmap;
						}
						catch( IOException e)
						{
							return null;
						}
					}

					@Override
					protected void onPostExecute(Bitmap bitmap) {
						if(bitmap != null)
						{
							mEditor.setImageBitmap(bitmap);
						}					
					};

				}.execute(mDoodleUrl);					
			}
		}
	};

	private String mDoodleName;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.doodle_editor);

		mContainer = (RelativeLayout) findViewById(R.id.container);
		mEditor = (ImageEditorView) findViewById(R.id.editor);		
		mEditor.setLongClickable(true);
		mEditor.setDrawingCacheEnabled(true);
		mEditMenu = (RadioGroup) findViewById(R.id.edit_menu);

		mEditMenu.setOnCheckedChangeListener(this);

		getSupportActionBar().setIcon(new ColorDrawable(Color.TRANSPARENT));
		getSupportActionBar().setTitle("Doodle Editor");
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.navibar_tile));

		Intent i = new Intent(this, FacebookService.class);
		bindService(i, mConn, BIND_AUTO_CREATE);
	}	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(mConn);
	}

	@Override
	protected void onResume() {
		super.onResume();

		Intent i = getIntent();		
		
		if(i.getExtras() == null){
			
		}else{
			Uri imageUri = i.getExtras().getParcelable(Intent.EXTRA_STREAM);
			Log.d(TAG, "ImageUri:" + imageUri );
			mEditor.setImageURI(imageUri);
			mEditor.invalidate();
			mDoodleName = "doodle_" + imageUri.getLastPathSegment();
		}
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

		menu.add("Undo")
		.setIcon(android.R.drawable.ic_menu_revert)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		menu.add("Clear")
		.setIcon(android.R.drawable.ic_menu_close_clear_cancel)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		menu.add("Save")
		.setIcon(android.R.drawable.ic_menu_add)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {

		String menu = item.getTitle().toString();

		//#1. first save file
		BitmapDrawable d = (BitmapDrawable) mEditor.getDrawable();		
		mEditor.setDrawingCacheEnabled(true);
		mEditor.invalidate();
		Bitmap bitmap = mEditor.getDrawingCache();
		Bitmap resizeBitmap = Bitmap.createScaledBitmap(
				bitmap, bitmap.getWidth() / 4, bitmap.getHeight() / 4, false);

		if( menu.equals("Save")) {
			
			//generate random doodle name.
			String comment = "Random Comment!!!! " + mDoodleName;

			//#1. first save file.
			try {
				DoodleRequester.createNewDoodle( 
						mDoodleName, 
						comment, 
						resizeBitmap,
						new CreateDoodleListener() {
							@Override
							public void onCreated(String pageName, String fileName) {
								Toast.makeText(DoodleEditor.this, "Doodle is created successufully!!", Toast.LENGTH_SHORT).show();
								
								try {
									mBoundService.updateSteam(DoodleEditor.this, pageName, fileName);
								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return super.onOptionsItemSelected(item);
	}

	private Random rand = new Random();
	private String generateRandomName() {
		return "doodle" + rand.nextInt(3);
	}

	private void bind() {
		Intent i = new Intent(this, FacebookService.class);
		bindService(i, new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				mBoundService = ((FacebookService.LocalBinder) service).getService();
			}
		}, BIND_AUTO_CREATE);
	}    	

	@Override
	public boolean onTouch(View arg0, MotionEvent ev) {
		return false;
	}

	private void attachEditText(float x, float y) {

		EditText edit = new EditText(this);
		edit.setSingleLine();
		edit.setText("Temp Text");
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		param.leftMargin = (int) x;
		param.topMargin = (int) y;

		edit.setLayoutParams(param);
		mContainer.addView(edit);
		edit.requestFocus();

		InputMethodManager ims = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// only will trigger it if no physical keyboard is open
		ims.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT);			

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch(checkedId)
		{
		case R.id.move:
			mEditor.setMode(EditMode.Move);
			break;
		case R.id.text:
			mEditor.setMode(EditMode.Text);
			break;
		case R.id.pen:
			mEditor.setMode(EditMode.Pen);
			break;
		}

	}

	@Override
	protected void onActivityResult(int reqCode, int resultCode, Intent arg2) {
		mBoundService.geFacebook().authorizeCallback(reqCode, resultCode, arg2);
	}	

}//end of class
