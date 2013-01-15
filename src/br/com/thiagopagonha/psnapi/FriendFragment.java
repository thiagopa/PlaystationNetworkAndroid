package br.com.thiagopagonha.psnapi;

import static br.com.thiagopagonha.psnapi.utils.CommonUtilities.REFRESH_FRIENDS;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import br.com.thiagopagonha.psnapi.model.Friend;
import br.com.thiagopagonha.psnapi.model.FriendsDBHelper;
import br.com.thiagopagonha.psnapi.utils.ImageCache;

public class FriendFragment extends Fragment {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// -- Registra o Receiver para esse determinado evento
		getActivity().registerReceiver(refreshFriends, new IntentFilter(REFRESH_FRIENDS));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return renderView();
	}
	
	// -- Necessário, mesmo :P
	public void onDestroy() {
		getActivity().unregisterReceiver(refreshFriends);
		super.onDestroy();
		
	}
	
	/**
	 * Receiver que recebe a chamada sempre que chega uma mensagem nova
	 */
	private BroadcastReceiver refreshFriends = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			View view = renderView();
			
			getActivity().setContentView(view);
		}
	};
	
	/**
	 * Desenha a Tela dos amigos com as Informações vindas
	 * Diretamente da base de dados
	 */
	private View renderView() {
		
		Context context = getActivity().getApplicationContext();
		
		TableLayout table = new TableLayout(context);  

		// -- Ajusta o tamanho das colunas, preservando a primeira que é a do avatar
		table.setColumnStretchable(1, true);
		table.setColumnStretchable(2, true);
		
		table.setColumnShrinkable(1, true);
		table.setColumnShrinkable(2, true);
		
		FriendsDBHelper friendsDBHelper = new FriendsDBHelper(context);
		
		 for (Friend friend : friendsDBHelper.getFriends()) {
			 TableRow row = new TableRow(context);
			 // -- Mantém todas as colunas com conteúdo alinhado no centro
			 row.setGravity(Gravity.CENTER_VERTICAL);  
			 
			 ImageView avatar = new ImageView(context);
			 // -- Pulo do gato, aqui é pego a imagem do cache ou feito o download
			 avatar.setImageBitmap( ImageCache.getImage(friend.getAvatarSmall()) );
			 // -- Não deixa a imagem deformar
			 avatar.setScaleType(ScaleType.CENTER);
			 
			 // -- Nome do usuário da PSN
			 TextView psnId = new TextView(context);
			 psnId.setText(friend.getPsnId());
			 psnId.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18); 
			 psnId.setTypeface(Typeface.SERIF, Typeface.BOLD);
			 
			 // -- O jogo que está jogando
			 TextView playing = new TextView(context);
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
		 
		return table;
	}
}
