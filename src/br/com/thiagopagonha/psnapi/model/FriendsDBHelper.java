package br.com.thiagopagonha.psnapi.model;

import static br.com.thiagopagonha.psnapi.model.FriendsOpenHelper.TABLE_NAME;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


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

	public void saveFriend(String psnId, String playing, String avatarSmall) {
		ContentValues values = new ContentValues();
		values.put("PSN_ID", psnId);
		values.put("PLAYING", playing);
		values.put("AVATAR_SMALL", avatarSmall);
		values.put("UPDATED", "now");

		String where = "PSN_ID=" + psnId;
		
		Cursor cursor = db.query(TABLE_NAME, new String[] { "PSN_ID" }, where , null ,null,null,null);
		
		boolean hasPsnId = cursor.moveToFirst();
		
		cursor.close();
		
		if(!hasPsnId) {
			db.insert(TABLE_NAME, null, values);
		} else {
			db.update(TABLE_NAME, values, where, null);
		}
		
		db.close();
		
	}
	
	public List<Friend> getFriends() {
		
		Cursor cursor = db.rawQuery("select PSN_ID, PLAYING, AVATAR_SMALL, UPDATED from " + TABLE_NAME, null);

		List<Friend> friends = new ArrayList<Friend>(cursor.getCount());
		
		while(cursor.moveToNext()) {
			Friend friend = new Friend();
			
			friend.setPsnId(cursor.getString(0));
			friend.setPlaying(cursor.getString(1));
			friend.setAvatarSmall(cursor.getString(2));
			//friend.setUpdated(cursor.getString(columnIndex))
			
			friends.add(friend);
		}
		
		cursor.close();
		db.close();
		
		return friends;
	}

}
