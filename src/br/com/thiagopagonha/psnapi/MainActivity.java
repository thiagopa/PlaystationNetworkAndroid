package br.com.thiagopagonha.psnapi;

import static br.com.thiagopagonha.psnapi.CommonUtilities.*;
import static br.com.thiagopagonha.psnapi.CommonUtilities.EXTRA_MESSAGE;
import static br.com.thiagopagonha.psnapi.CommonUtilities.SENDER_ID;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity {

	
	TextView mDisplay;
	AsyncTask<Void, Void, Void> mRegisterTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);
		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);
		
		setContentView(R.layout.activity_main);
		mDisplay = (TextView) findViewById(R.id.display);
		
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			// Automatically registers application on startup.
			GCMRegistrar.register(this, SENDER_ID);
		} else {
			// Device is already registered on GCM, check server.
			if (!GCMRegistrar.isRegisteredOnServer(this)) {
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				final Context context = this;
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						ServerUtilities.register(context, regId);
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}

				};
				mRegisterTask.execute(null, null, null);
			}
		}
		refreshView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		/*
		 * Typically, an application registers automatically, so options below
		 * are disabled. Uncomment them if you want to manually register or
		 * unregister the device (you will also need to uncomment the equivalent
		 * options on options_menu.xml).
		 */
		/*
		 * case R.id.options_register: GCMRegistrar.register(this, SENDER_ID);
		 * return true; case R.id.options_unregister:
		 * GCMRegistrar.unregister(this); return true;
		 */
		case R.id.options_clear:
			clearLog();
			return true;
		case R.id.options_exit:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onDestroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		unregisterReceiver(mHandleMessageReceiver);
		GCMRegistrar.onDestroy(this);
		super.onDestroy();
		
	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			
			String currentDateTimeString = DateFormat.getTimeFormat(context).format(GregorianCalendar.getInstance().getTime());
			
			appendLog("[" + currentDateTimeString + "]" + newMessage + "\n");
			
			refreshView();
		}
	};
	
	
	private static final String FILENAME = "friends.txt";

	private String readLog()  {
		
		InputStream inputStream;
		try {
			inputStream = openFileInput(FILENAME);
	     
		    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		     
		    int i;
		    i = inputStream.read();
		    while (i != -1) {
		    	byteArrayOutputStream.write(i);
		    	i = inputStream.read();
		    } 
		    inputStream.close();
		    
		    return byteArrayOutputStream.toString();
		} catch(IOException io) {
			Log.e(TAG, "Não foi possível ler arquivo de log");
		}
	  
	    return null;
	}
	

	private void clearLog()  {
		try {
			openFileOutput(FILENAME, MODE_PRIVATE);
			refreshView();
		} catch (FileNotFoundException e) {
			Log.e(TAG, "Não foi possível reiniciar arquivo de log");
		}
	}
	
	private void appendLog(String toBeLogged)  {
		try {
			FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_APPEND);
			fos.write(toBeLogged.getBytes());
			fos.close();
		} catch (IOException io) {
			Log.e(TAG, "Não foi possível atualizar arquivo de log");
		}
	}
	
	private void refreshView() {
		mDisplay.setText(readLog());
	}
	

}
