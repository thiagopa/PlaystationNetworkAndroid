package br.com.thiagopagonha.psnapi.utils;

import static br.com.thiagopagonha.psnapi.utils.CommonUtilities.TAG;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

/**
 * Image cache resolver
 */
public class ImageCache {

	public static Bitmap getImage(String avatar) {
		try {
		
			//First create a new URL object 
			URL url = new URL(avatar);
	
			String fileName = md5sum(avatar);
			
			File file = new File(Environment.getExternalStorageDirectory(), fileName + ".png");

			Log.d(TAG,"Url is" + avatar);
			Log.d(TAG, "File is " + file.getAbsolutePath());
			
			if(!file.exists()) {
				Log.d(TAG, "Downloading and decoding file");
				//Next create a Bitmap object and download the image to bitmap
				Bitmap bitmap = BitmapFactory.decodeStream(url.openStream());
		
				//Finally compress the bitmap, saving to the file previously created
				bitmap.compress(CompressFormat.PNG, 100, new FileOutputStream(file));
				
				return bitmap;
			}
			
			return BitmapFactory.decodeStream(new FileInputStream(file));
					
		} catch(IOException io) {
			Log.e(TAG, "Erro ao manipular os arquivos de imagem" );
			Log.e(TAG, io.getMessage() );
		}
		return null;
	}
	
	/**
	 * Gera um hash da url do arquivo
	 * @param md5
	 * @return
	 */
	private static String md5sum(String md5) {
		   try {
		        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
		        byte[] array = md.digest(md5.getBytes());
		        StringBuffer sb = new StringBuffer();
		        for (int i = 0; i < array.length; ++i) {
		          sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
		       }
		        return sb.toString();
		    } catch (java.security.NoSuchAlgorithmException e) {
		    }
		    return null;
		}
}
