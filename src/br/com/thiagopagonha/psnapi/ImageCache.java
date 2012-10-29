package br.com.thiagopagonha.psnapi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

/**
 * Image cache resolver
 */
public class ImageCache {

	public static int getImage(String avatar) throws IOException {
		//First create a new URL object 
		URL url = new URL(avatar);

		String fileName = md5sum(avatar);
		
		//Next create a file, the example below will save to the SDCARD using JPEG format
		File file = new File("/sdcard/psnapi/" +  fileName +"png");

		//Next create a Bitmap object and download the image to bitmap
		Bitmap bitmap = BitmapFactory.decodeStream(url.openStream());

		//Finally compress the bitmap, saving to the file previously created
		bitmap.compress(CompressFormat.PNG, 100, new FileOutputStream(file));
		
		return -1;
	}
	
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
