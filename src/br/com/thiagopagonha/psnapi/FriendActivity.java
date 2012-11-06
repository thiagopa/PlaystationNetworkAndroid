package br.com.thiagopagonha.psnapi;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import br.com.thiagopagonha.psnapi.model.Friend;
import br.com.thiagopagonha.psnapi.model.FriendsDBHelper;

public class FriendActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 TableLayout table = new TableLayout(this);  
		 table.setStretchAllColumns(true);  
		 table.setShrinkAllColumns(true);  
		
		 
		 for (Friend friend : new FriendsDBHelper(getApplicationContext()).getFriends()) {
			 TableRow row = new TableRow(this);
			 TextView psnId = new TextView(this);
			 psnId.setText(friend.getPsnId());

			 TextView playing = new TextView(this);
			 playing.setText(friend.getPlaying());
			 
			 // ImageCache.getImage(avatarSmall)
			 
			 row.addView(psnId);
			 row.addView(playing);
			 table.addView(row);
		}
		
		 
		 
		setContentView(table);
		
	}
	
}
