package br.com.thiagopagonha.psnapi;

import static br.com.thiagopagonha.psnapi.utils.CommonUtilities.*;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
		// -- Registra o Receiver para esse determinado evento
		registerReceiver(refreshFriends, new IntentFilter(REFRESH_FRIENDS));
	}

	/**
	 * Receiver que recebe a chamada sempre que chega uma mensagem nova
	 */
	private BroadcastReceiver refreshFriends = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			renderView();
		}
	};
	
	/**
	 * Desenha a Tela dos amigos com as Informações vindas
	 * Diretamente da base de dados
	 */
	private void renderView() {
		
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
