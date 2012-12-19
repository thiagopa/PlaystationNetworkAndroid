package br.com.thiagopagonha.psnapi;

import static br.com.thiagopagonha.psnapi.utils.CommonUtilities.REFRESH_FRIENDS;
import static br.com.thiagopagonha.psnapi.utils.CommonUtilities.TAG;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import br.com.thiagopagonha.psnapi.model.Friend;
import br.com.thiagopagonha.psnapi.model.FriendsDBHelper;
import br.com.thiagopagonha.psnapi.utils.ImageCache;

public class FriendActivity extends Activity {

	private ScheduledExecutorService scheduleTaskExecutor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// -- Registra o Receiver para esse determinado evento
		registerReceiver(refreshFriends, new IntentFilter(REFRESH_FRIENDS));
		// -- Desenha a Tela
		renderView();
		
	}

	// -- Necessário, mesmo :P
	protected void onDestroy() {
		scheduleTaskExecutor.shutdown();
		unregisterReceiver(refreshFriends);
		super.onDestroy();
		
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

		// -- Ajusta o tamanho das colunas, preservando a primeira que é a do avatar
		table.setColumnStretchable(1, true);
		table.setColumnStretchable(2, true);
		
		table.setColumnShrinkable(1, true);
		table.setColumnShrinkable(2, true);
		
		FriendsDBHelper friendsDBHelper = new FriendsDBHelper(getApplicationContext());
		
		 for (Friend friend : friendsDBHelper.getFriends()) {
			 TableRow row = new TableRow(this);
			 // -- Mantém todas as colunas com conteúdo alinhado no centro
			 row.setGravity(Gravity.CENTER_VERTICAL);  
			 
			 ImageView avatar = new ImageView(this);
			 // -- Pulo do gato, aqui é pego a imagem do cache ou feito o download
			 avatar.setImageBitmap( ImageCache.getImage(friend.getAvatarSmall()) );
			 // -- Não deixa a imagem deformar
			 avatar.setScaleType(ScaleType.CENTER);
			 
			 // -- Nome do usuário da PSN
			 TextView psnId = new TextView(this);
			 psnId.setText(friend.getPsnId());
			 psnId.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18); 
			 psnId.setTypeface(Typeface.SERIF, Typeface.BOLD);
			 
			 // -- O jogo que está jogando
			 TextView playing = new TextView(this);
			 playing.setText(friend.getPlaying());
			 playing.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16); 
			 playing.setTypeface(Typeface.SERIF, Typeface.BOLD);
			 playing.setTextColor(Color.BLUE);
			 
			 // -- Adiciona as views na tabela
			 row.addView(avatar);
			 row.addView(psnId);
			 row.addView(playing);
			 
			 table.addView(row);
		}
		
		friendsDBHelper.close();
		 
		setContentView(table);
	}
}
