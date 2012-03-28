package com.huewu.study.diki;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class FacebookService extends Service{
	
	private static final String TAG = "FacebookService";

	private LocalBinder mBinder = new LocalBinder();

	public static Facebook facebook = new Facebook("193057250807581");

	String FILENAME = "AndroidSSO_data";
	
	private AsyncFacebookRunner mAsyncRunner;
    private SharedPreferences mPrefs;	

    
	public class LocalBinder extends Binder {

		public FacebookService getService() {
			return FacebookService.this;
		}

	}// end of inner class.

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "Service Created!");
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "Service Destroyed!");
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		Log.d(TAG, "Service Bounded!");
		return mBinder;
	}
	
	public Facebook geFacebook()
	{
		return facebook;
	}
	
	public void setFacebook(Facebook fb)
	{
		facebook = fb;
        mAsyncRunner = new AsyncFacebookRunner(facebook);
	}
	
	public void updateSteam( Activity host, String page, String file ) throws UnsupportedEncodingException, JSONException
	{		
		String basePath = "https://github.com/huewu/diki/wiki/";
		Bundle params = new Bundle();
		
		//use temp url.
		String tempFile = "doodle21332843882421.jpg";
		params.putString("picture", basePath + tempFile);
		params.putString("description", "만일 이 짤방에 관심이 있으시면 다음 링크를 방문해 보세요!");
		params.putString("name", "Doodle From Doodle Wiki!");
		params.putString("link", basePath + page);
		
		facebook.dialog(host, "feed", params, new DialogListener() {
			
			@Override
			public void onFacebookError(FacebookError e) {
			}
			
			@Override
			public void onError(DialogError e) {
			}
			
			@Override
			public void onComplete(Bundle values) {
			}
			
			@Override
			public void onCancel() {
			}
		});

//		mAsyncRunner.request("/me/feed", params, "POST", new RequestListener() {
//			
//			@Override
//			public void onMalformedURLException(MalformedURLException e, Object state) {
//				
//			}
//			
//			@Override
//			public void onIOException(IOException e, Object state) {
//			}
//			
//			@Override
//			public void onFileNotFoundException(FileNotFoundException e, Object state) {
//			}
//			
//			@Override
//			public void onFacebookError(FacebookError e, Object state) {
//			}
//			
//			@Override
//			public void onComplete(String response, Object state) {
//			}
//		}, null);
	}
	
    public void authorize(Activity host)
    {
        mPrefs = this.getSharedPreferences(FILENAME,MODE_PRIVATE);
        String access_token = mPrefs.getString("access_token", null);
        long expires = mPrefs.getLong("access_expires", 0);
        if(access_token != null){
        	facebook.setAccessToken(access_token);        	
    	}
        if(expires != 0){
        	facebook.setAccessExpires(expires);
        }
        	
        if(!facebook.isSessionValid()){
            facebook.authorize(host, new String[] { "user_status", "user_photos", "friends_photos" }, new DialogListener(){

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
    }

    private URL mBitmap = null;
	public void setOriginBitmap(URL uRL) {
		mBitmap = uRL;
	}
	
	public URL getOriginBitmap()
	{
		return mBitmap;
	}
	
	private String mName = "";
	public void setOriginName( String name )
	{
		mName = name;
	}

	public String getOriginName() {
		return mName;
	}


}//end of class
