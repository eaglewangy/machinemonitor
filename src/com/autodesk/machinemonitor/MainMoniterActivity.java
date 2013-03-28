package com.autodesk.machinemonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainMoniterActivity extends Activity {
	private final static String TAG = MainMoniterActivity.class.getSimpleName();
	public final static String BASIC_URL = "http://10.148.221.78/mobile/mobile.php";
	public final static String PUTITBACK = "Put it back";
	
	private final static String mGetUsersUrl = BASIC_URL + "?action=users";
	private RadioGroup mRadioGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mRadioGroup = (RadioGroup) findViewById(R.id.members);
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton btn = (RadioButton) findViewById(checkedId);
				String name = btn.getText().toString().trim();
				
				Intent intent = new Intent(MainMoniterActivity.this, UpdateService.class);
		        intent.putExtra(UpdateService.UPDATESERVICE, name);
		        startService(intent);
			}
		});
		UsersTask usersTask = new UsersTask();
		usersTask.execute(mGetUsersUrl);
	}
	
	public String update(String url){ 
		BufferedReader in = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
				return null;
			}
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
			return content;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
        	e.printStackTrace();
        }
		return null;
    }
	
	private class UsersTask extends AsyncTask<String, Void, String[]> {
	      @Override
	      protected String[] doInBackground(String... params) {
	    	  String content = update(params[0]);
	    	  String[] members = null;
	    	  if (content != null && content.length() > 0){
	    		  members = content.split(",");
	    	  }
	          return members;
	      }      

	      @Override
	      protected void onPostExecute(String result[]){
	    	  if (result == null)
	    		  return;
	    	  
	    	  for (int i = 0; i < result.length; ++i){
	    		  RadioButton radio  = new RadioButton(MainMoniterActivity.this);
	    		  radio.setText(result[i].trim());
	    		  mRadioGroup.addView(radio);
	    	  }
	    	  
	    	  RadioButton radio  = new RadioButton(MainMoniterActivity.this);
    		  radio.setText(MainMoniterActivity.PUTITBACK);
    		  mRadioGroup.addView(radio);
	      }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
