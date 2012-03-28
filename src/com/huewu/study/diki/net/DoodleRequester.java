package com.huewu.study.diki.net;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;


public class DoodleRequester {
	
	private static final String host = "192.168.1.10";
	private static ExecutorService worker = Executors.newCachedThreadPool();
	
	public static interface CreateDoodleListener
	{
		void onCreated(String pageName, String fileName);
	}
	
	public static class DoodleRequest
	{
		public Bitmap bitmap;
		public String name;
		public String comment;
		public CreateDoodleListener listener;
	}
	
	private static class CreateDoodleTask extends AsyncTask<DoodleRequest, String, Uri>
	{
		private DoodleRequest request = null;
		private String fileName = "";
		private String pageName = "";

		@Override
		protected Uri doInBackground(DoodleRequest... params) {
			request = params[0];
			
			URL url = null;
			try {
				url = new URL("http://" + host + ":8080/doodles?comment='doodle_name'");

				pageName = request.name;
				fileName = request.name + System.currentTimeMillis() + ".jpg";

				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("content-type", "image/jpeg");
				conn.setRequestProperty("doodle-name", request.name);
				conn.setRequestProperty("file-name", fileName);
				conn.setRequestProperty("doodle-comment", request.comment);
				conn.connect();
				

				OutputStream os = conn.getOutputStream();

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				request.bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
				byte[] buf = stream.toByteArray();	

				ByteArrayInputStream bis = new ByteArrayInputStream(buf);

				byte buffer[] = new byte[1024];
				while( true )
				{
					int len = bis.read(buffer, 0, buffer.length);
					if(len <= 0)
						break;

					os.write(buffer, 0, len);
				}

				InputStream is = conn.getInputStream();
				
				while( true )
				{
					int len = is.read(buffer, 0, buffer.length);
					if(len <= 0)
						break;

					System.out.println("Resp:" + new String(buffer));
				}

				is.close();
				os.close();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			if(url == null)
				return null;
			
			//wait some thime.
			
			//SystemClock.sleep(1000);
			
			return Uri.parse(url.toString());
		}
		
		@Override
		protected void onPostExecute(Uri result) {
			if( request.listener != null )
				request.listener.onCreated(pageName, fileName);
		}
	}

	/**
	 * @param bitmap 
	 * @param name 
	 * @param args
	 * @throws IOException 
	 */
	public static void createNewDoodle(final String name, final String comment, 
			final Bitmap bitmap, CreateDoodleListener listener ) throws IOException {
		CreateDoodleTask task = new CreateDoodleTask();
		DoodleRequest req = new DoodleRequest();
		req.bitmap = bitmap;
		req.comment = comment;
		req.name = name;
		req.listener = listener;
		task.execute(req);
	}
	
	static class RequestDoodleTask extends AsyncTask<String, String, ArrayList<String>>
	{

		@Override
		protected ArrayList<String> doInBackground(String... params) {
			// TODO Auto-generated method stub
			URL url = null;
			try {
				url = new URL("http://" + host + ":8080/doodles?filter='" + params[0] +"'");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.connect();
				
				InputStream is = conn.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line = br.readLine();
				
				Log.d("DOODLE", "RESP:" + line);
				br.close();
				isr.close();
				is.close();
			}
			catch(Exception e)
			{
				
			}
			
			return null;
		}
		
	}
	
	public static void requestDoodleList() {
		RequestDoodleTask task = new RequestDoodleTask();
		task.execute("hot");
	}	
	
	private void postDoodleToFacebook()
	{
		
	}

	public static InputStream readFile( File path ) throws FileNotFoundException
	{
		FileInputStream fis = new  FileInputStream(path);
		return fis;
	}

}//end of class
