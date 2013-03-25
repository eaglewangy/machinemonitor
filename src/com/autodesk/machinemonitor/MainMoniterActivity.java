package com.autodesk.machinemonitor;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainMoniterActivity extends Activity {

	private ArrayList<String> members =new ArrayList<String>(); 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		members.add("Ying Wang");
		members.add("Qing XU");
		members.add("Jimmy Sun");
		
		String osversion = Build.VERSION.RELEASE;
		String menufacturer = Build.MANUFACTURER + " " + Build.BOARD;
		members.add(osversion);
		members.add(menufacturer);
		
		ListView listView = (ListView)findViewById(android.R.id.list);
		listView = (ListView)findViewById(android.R.id.list);  
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1, members);  
        listView.setAdapter(adapter);  
        
        Intent intent = new Intent(this, UpdateService.class);
        intent.putExtra(UpdateService.UPDATESERVICE, "User name");
        startService(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
