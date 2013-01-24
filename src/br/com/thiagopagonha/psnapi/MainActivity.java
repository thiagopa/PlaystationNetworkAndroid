package br.com.thiagopagonha.psnapi;

import static br.com.thiagopagonha.psnapi.utils.CommonUtilities.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.GregorianCalendar;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Window;

public class MainActivity extends Activity {

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			
			String currentDateTimeString = DateFormat.getTimeFormat(context).format(GregorianCalendar.getInstance().getTime());
			
			appendLog("[" + currentDateTimeString + "] " + newMessage + "\n");
		}
	};
	
	private void appendLog(String toBeLogged)  {
		try {
			FileOutputStream fos = this.openFileOutput(FILENAME, Context.MODE_APPEND);
			fos.write(toBeLogged.getBytes());
			fos.close();
		} catch (IOException io) {
			Log.e(TAG, "Não foi possível atualizar arquivo de log");
		}
	}
	
	@Override
	protected void onDestroy() {
		this.unregisterReceiver(mHandleMessageReceiver);
		super.onDestroy();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));
		
		// -- Gambi permite que a Thread Principal faça chamadas à rede
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		
		setContentView(R.layout.activity_tabs);

		Resources res = getResources();

		ActionBar actionbar = getActionBar();
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		ActionBar.Tab FriendTab = actionbar.newTab()
				.setText(getString(R.string.friends))
				.setIcon(res.getDrawable(R.drawable.friends));
		ActionBar.Tab MessageTab = actionbar.newTab()
				.setText(getString(R.string.history))
				.setIcon(res.getDrawable(R.drawable.clock));

		Fragment FriendFragment = new FriendFragment();
		Fragment MessageFragment = new MessageFragment();

		FriendTab.setTabListener(new InnerTabsListener(FriendFragment));
		MessageTab.setTabListener(new InnerTabsListener(MessageFragment));

		actionbar.addTab(FriendTab);
		actionbar.addTab(MessageTab);

	}

	class InnerTabsListener implements ActionBar.TabListener {
		public Fragment fragment;

		public InnerTabsListener(Fragment fragment) {
			this.fragment = fragment;
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// Toast.makeText(StartActivity.appContext, "Reselected!",
			// Toast.LENGTH_LONG).show();
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			ft.replace(R.id.fragment_container, fragment);
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			ft.remove(fragment);
		}

	}

}
