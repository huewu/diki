package com.huewu.study.diki;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class DoodleAdapter extends ArrayAdapter<Doodle> {
	

	private static final String TAG = "PartyAdapter";
	
	private LayoutInflater inflater = null;
	public static final HashMap<String, Bitmap> IMAGE_CACHE = new HashMap<String, Bitmap>();

	public DoodleAdapter(Context context) {
		super( context, R.layout.doodle_adapter);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if( convertView == null )
		{
			convertView = inflater.inflate(R.layout.doodle_adapter, null);
		}
		String basePath = "https://github.com/huewu/diki/wiki/";

		Doodle doodle = getItem(position);
		try {
			doodle.URL = new URL( basePath + doodle.FileName);
		} catch (MalformedURLException e1) {
		}
		
		TextView title = (TextView) convertView.findViewById(R.id.title);
		title.setText(doodle.Name);
		final ImageView thumb = (ImageView) convertView.findViewById(R.id.thumbnail);
		
		new AsyncTask<URL, Object, Bitmap>()
		{
			URL mURL = null;
			@Override
			protected Bitmap doInBackground(URL... params) {
				mURL = params[0];
				
				try
				{
					Bitmap cached = getCache(mURL.toString());
					if(cached != null)
						return cached;
					
					URLConnection conn = mURL.openConnection();
					InputStream is = conn.getInputStream();

					Bitmap bitmap = BitmapFactory.decodeStream(is);
					is.close();
					
					setCache(mURL.toString(), bitmap);
					return bitmap;
				}
				catch( IOException e)
				{
					return null;
				}
			}
			

			private void setCache(String string, Bitmap bitmap) {
				IMAGE_CACHE.put(string, bitmap);
				//File file = new File();
			}

			private Bitmap getCache(String string) {
				return IMAGE_CACHE.get(string);
			}

			@Override
			protected void onPostExecute(Bitmap bitmap) {
				if(bitmap != null)
				{
					thumb.setImageBitmap(bitmap);
				}					
			};

		}.execute(doodle.URL);		
		
		return convertView;
		
	}

}// end of class
