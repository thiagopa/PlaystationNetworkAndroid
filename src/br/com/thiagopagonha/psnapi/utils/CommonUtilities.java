
package br.com.thiagopagonha.psnapi.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 */
public final class CommonUtilities {

    public static final String REFRESH_FRIENDS = "br.com.thiagopagonha.psnapi.REFRESH_FRIENDS";

	/**
     * Base URL of the Demo Server (such as http://my_host:8080/gcm-demo)
     */
    public static final String SERVER_URL = "http://psnservergcm.appspot.com/";

    /**
     * Google API project id registered to use GCM.
     */
    public static final String SENDER_ID = "175162298723";

    /**
     * Tag used on log messages.
     */
    public static final String TAG = "br.com.thiagopagonha.psnapi";

    /**
     * Intent used to display a message in the screen.
     */
    public static final String DISPLAY_MESSAGE_ACTION =
            "br.com.thiagopagonha.psnapi.DISPLAY_MESSAGE";

    /**
     * Intent's extra that contains the message to be displayed.
     */
    public static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static void displayMessage(Context context, String message) {
        Log.d(TAG, "displayMessage");
    	Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
        context.sendBroadcast(new Intent(REFRESH_FRIENDS));
    }
    
    public static final String PREFS_NAME = "UserInfo";
}
