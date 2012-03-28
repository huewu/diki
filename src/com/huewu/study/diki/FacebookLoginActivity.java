package com.huewu.study.diki;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

public class FacebookLoginActivity extends Activity {
	
	protected static final String TAG = "FacebookLogin";
	Facebook facebook = new Facebook("193057250807581");
    String FILENAME = "AndroidSSO_data";
	private AsyncFacebookRunner mAsyncRunner;
    private SharedPreferences mPrefs;	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        
        /*
         * get existing access_token if any
         */
        mPrefs = getPreferences(MODE_PRIVATE);
        String access_token = mPrefs.getString("access_token", null);
        long expires = mPrefs.getLong("access_expires", 0);
        if(access_token != null){
        	facebook.setAccessToken(access_token);        	
    	}
        if(expires != 0){
        	facebook.setAccessExpires(expires);
        }
               
        if(!facebook.isSessionValid()){
        	authorize();
        }
        else 
        {
        	//bind();
        }
        
        //mAsyncRunner = new AsyncFacebookRunner(facebook);
    }
    
	private void bind() {
		Intent i = new Intent(this, FacebookService.class);
		bindService(i, new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				FacebookService s= ((FacebookService.LocalBinder) service).getService();
				s.setFacebook(facebook);
				
	        	Intent i = new Intent(FacebookLoginActivity.this, DoodleBrowser.class);
	        	startActivity(i);
			}
		}, BIND_AUTO_CREATE);
	}    
    
    @Override
    protected void onResume() {
    	super.onResume();
    	facebook.extendAccessToken(this, null);
    }
    
    public void authorize()
    {
        facebook.authorize(this, new String[] { "user_status", "user_photos", "friends_photos" }, new DialogListener(){

			@Override
			public void onComplete(Bundle values) {
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putString("access_token", facebook.getAccessToken());
                editor.putLong("access_expires", facebook.getAccessExpires());
                editor.commit();
			}

			@Override
			public void onFacebookError(FacebookError e) {
			}

			@Override
			public void onError(DialogError e) {
			}

			@Override
			public void onCancel() {
			}
        	
        });    	
    }
    
    public void handleClick( View v )
    {
    	switch( v.getId() )
    	{
    	case R.id.post:
    		mAsyncRunner.request( "me/feed", new DefaultRequestListener("me"));
    		break;
    	case R.id.me:
    		mAsyncRunner.request("me", new DefaultRequestListener("me"));
    		mAsyncRunner.request("me/posts", new DefaultRequestListener("me/posts"));
    		mAsyncRunner.request("me/friends", new DefaultRequestListener("me/friends"));
    		break;
    	case R.id.login:
    		authorize();
    		break;
    	case R.id.logout:
	    	{	    	
	    		mAsyncRunner.logout(this, new RequestListener() {
					
					@Override
					public void onMalformedURLException(MalformedURLException e, Object state) {
						Log.d(TAG, "onMalformedURLException:" + e.getMessage());
					}
					
					@Override
					public void onIOException(IOException e, Object state) {
						Log.d(TAG, "onIOException:" + e.getMessage());						
					}
					
					@Override
					public void onFileNotFoundException(FileNotFoundException e, Object state) {
						Log.d(TAG, "onFileNotFoundException:" + e.getMessage());
					}
					
					@Override
					public void onFacebookError(FacebookError e, Object state) {
						Log.d(TAG, "onFacebookError:" + e.getErrorType());
					}
					
					@Override
					public void onComplete(String response, Object state) {
						Log.d(TAG, "onComplete:" + response);
						try {
							JSONObject json = new JSONObject(response);
							int err = json.getInt("error_code");
							if ( err != 0 )
								authorize();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, null);
	    	}
    		break;
    	}
    }
}//end of class