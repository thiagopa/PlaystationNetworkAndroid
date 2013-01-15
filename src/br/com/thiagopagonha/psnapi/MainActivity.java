package br.com.thiagopagonha.psnapi;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Window;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
