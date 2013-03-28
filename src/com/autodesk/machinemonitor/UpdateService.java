package com.autodesk.machinemonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import 	android.os.Process;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class UpdateService extends Service {
    public final static String TAG = UpdateService.class.getSimpleName();
    
    private final static int UPDATE_MSG = 1002;
    
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private String mImei;
    private String mOwerName;
    private String mOSVersion;
    private String mManufacturer;
    private String mStatus = "Available";
    
    public final static String UPDATESERVICE = "UPDATESERVICE";
    public final static String EXCKUSIVE = "EXCKUSIVE";
    public final static int INTENT_UPDATE = 1;
    
	// Handler that receives messages from the thread
	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == UPDATE_MSG) {
				//?action=update&imei=1232&owner=ying&version=4.0.4&manufacturer=google&status=available
				try {
					mOwerName = URLEncoder.encode(mOwerName, "UTF-8");
					if (mOwerName.equals(URLEncoder.encode(MainMoniterActivity.PUTITBACK, "UTF-8"))){
						mStatus = "Available";
						mOwerName = "";
					}
					else
						mStatus = "Unavailable";
					mOSVersion = URLEncoder.encode(mOSVersion, "UTF-8");
					mManufacturer = URLEncoder.encode(mManufacturer, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} 
				String url = MainMoniterActivity.BASIC_URL + "?action=update&imei=" + mImei + "&owner=" + 
				                             mOwerName + "&version=" + mOSVersion + "&manufacturer=" + mManufacturer +
				                             "&status=" + mStatus;
				update(url);
			}
		}
	}

	@Override
	public void onCreate() {
		// Start up the thread running the service. Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block. We also make it
		// background priority so CPU-intensive work will not disrupt our UI.
		HandlerThread thread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		// Get the HandlerThread's Looper and use it for our Handler
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null){
			Toast.makeText(this, "Update failed.", Toast.LENGTH_SHORT).show();
		}else{
			mOwerName = intent.getStringExtra(UPDATESERVICE);
			if (mOwerName != null && mOwerName.length() > 0){
				mOSVersion = Build.VERSION.RELEASE;
				mManufacturer = Build.MODEL;
				TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				// get IMEI
				mImei = tm.getDeviceId();
				boolean error = false;
				if (mOSVersion == null || mOSVersion.length() == 0)
					error = true;
				if (mManufacturer == null || mManufacturer.length() == 0)
					error = true;
				if (!error)
					mServiceHandler.sendMessage(Message.obtain(mServiceHandler, UPDATE_MSG));
			}
		}
			
		// If we get killed, after returning from here, restart
		return START_STICKY;
	}
	
	public void update(String url){ 
		BufferedReader in = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);

			}
			in.close();
			String content = sb.toString();
			Log.i(TAG, content);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
        	e.printStackTrace();
        }
    } 

	@Override
	public IBinder onBind(Intent intent) {
		// We don't provide binding, so return null
		return null;
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
	}
}