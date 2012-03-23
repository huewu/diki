package com.huewu.study.facebooklogin;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.FacebookError;

public class DefaultRequestListener implements RequestListener{

	private static final String TAG = "RequestListener";
	
	private String mRequestToken = "";
	public DefaultRequestListener( String token )
	{
		mRequestToken = token;
	}

	@Override
	public void onMalformedURLException(MalformedURLException e, Object state) {
		Log.d(TAG, mRequestToken + ":onMalformedURLException:" + e.getMessage());
	}
	
	@Override
	public void onIOException(IOException e, Object state) {
		Log.d(TAG, mRequestToken + ":onIOException:" + e.getMessage());						
	}
	
	@Override
	public void onFileNotFoundException(FileNotFoundException e, Object state) {
		Log.d(TAG, mRequestToken + ":onFileNotFoundException:" + e.getMessage());
	}
	
	@Override
	public void onFacebookError(FacebookError e, Object state) {
		Log.d(TAG, mRequestToken + ":onFacebookError:" + e.getErrorType());
	}
	
	@Override
	public void onComplete(String response, Object state) {
		Log.d(TAG, "onComplete:" + response);
	}

}//end of class
