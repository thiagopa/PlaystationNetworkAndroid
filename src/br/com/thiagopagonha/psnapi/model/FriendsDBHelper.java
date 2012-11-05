package br.com.thiagopagonha.psnapi.model;

import static br.com.thiagopagonha.psnapi.model.FriendsOpenHelper.TABLE_NAME;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
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

		db.insert(TABLE_NAME, null, values);
	}
	
	public List<Friend> getFriends() {
		return null;
	}

}
