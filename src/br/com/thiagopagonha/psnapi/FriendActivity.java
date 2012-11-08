package br.com.thiagopagonha.psnapi;

import static br.com.thiagopagonha.psnapi.utils.CommonUtilities.REFRESH_FRIENDS;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import br.com.thiagopagonha.psnapi.model.Friend;
import br.com.thiagopagonha.psnapi.model.FriendsDBHelper;
import br.com.thiagopagonha.psnapi.utils.ImageCache;

public class FriendActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// -- Registra o Receiver para esse determinado evento
		registerReceiver(refreshFriends, new IntentFilter(REFRESH_FRIENDS));
		// -- Desenha a Tela
		renderView();
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
			 
			 row.setGravity(Gravity.CENTER_VERTICAL);  
			 
			 ImageView avatar = new ImageView(this);
			 avatar.setImageBitmap( ImageCache.getImage(friend.getAvatarSmall()) );
			 
			 TextView psnId = new TextView(this);
			 psnId.setText(friend.getPsnId());
			 psnId.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18); 
			 psnId.setTypeface(Typeface.SERIF, Typeface.BOLD);
			 
			 TextView playing = new TextView(this);
			 playing.setText(friend.getPlaying());

			 row.addView(avatar);
			 row.addView(psnId);
			 row.addView(playing);
			 
			 table.addView(row);
		}
		
		setContentView(table);
	}
	
}
