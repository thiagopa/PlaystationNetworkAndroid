package br.com.thiagopagonha.psnapi.model;

import static br.com.thiagopagonha.psnapi.model.FriendsOpenHelper.TABLE_NAME;
import static br.com.thiagopagonha.psnapi.utils.CommonUtilities.TAG;
import static br.com.thiagopagonha.psnapi.utils.CommonUtilities.convertDate;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


/**
 * 
 * Classe Usada para Listar e Inserir/Atualizar os Amigos no banco
 * 
 */
public class FriendsDBHelper {

	private SQLiteDatabase db;
	private FriendsOpenHelper helper;

	public FriendsDBHelper(Context context) {
		helper = new FriendsOpenHelper(context);
		db = helper.getdb();
	}

	/**
	 * Insere ou Atualiza um amigo e ainda de quebra verifica se é o mesmo jogo
	 * @param psnId
	 * @param playing
	 * @param avatarSmall
	 * @return <true> se for o mesmo jogo
	 */
	public boolean saveFriend(String psnId, String playing, String avatarSmall) {
		ContentValues values = new ContentValues();
		values.put("PSN_ID", psnId);
		values.put("PLAYING", playing);
		values.put("AVATAR_SMALL", avatarSmall);
		values.put("UPDATED", GregorianCalendar.getInstance().getTime().toString());

		String where = "PSN_ID=\"" + psnId + "\"";
		
		Cursor cursor = db.query(TABLE_NAME, new String[] { "PSN_ID" }, where , null ,null,null,null);
		
		boolean hasPsnId = cursor.moveToFirst();
		
		boolean isSameGame = hasPsnId && playing.equals(cursor.getString(1));
		
		cursor.close();
		
		if(!hasPsnId) {
			db.insert(TABLE_NAME, null, values);
		} else {
			db.update(TABLE_NAME, values, where, null);
		}
		
		return isSameGame;
		
	}
	
	public List<Friend> getFriends() {
		
		Cursor cursor = db.rawQuery("select PSN_ID, PLAYING, AVATAR_SMALL, UPDATED from " + TABLE_NAME, null);

		int size = cursor.getCount();
		
		Log.d(TAG,"Found " + size + " friends");
		
		List<Friend> friends = new ArrayList<Friend>(size);
		
		while(cursor.moveToNext()) {
			Friend friend = new Friend();
			
			friend.setPsnId(cursor.getString(0));
			friend.setPlaying(cursor.getString(1));
			friend.setAvatarSmall(cursor.getString(2));
			friend.setUpdated(convertDate(cursor.getString(3)));
			
			friends.add(friend);
		}
		
		cursor.close();
		
		return friends;
	}

	public void close() {
		db.close();
	}
	
}
