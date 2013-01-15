/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.thiagopagonha.psnapi.gcm;

import static br.com.thiagopagonha.psnapi.gcm.Method.DELETE;
import static br.com.thiagopagonha.psnapi.gcm.Method.GET;
import static br.com.thiagopagonha.psnapi.gcm.Method.POST;
import static br.com.thiagopagonha.psnapi.utils.CommonUtilities.SERVER_URL;
import static br.com.thiagopagonha.psnapi.utils.CommonUtilities.TAG;
import static br.com.thiagopagonha.psnapi.utils.CommonUtilities.displayMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.util.Log;
import br.com.thiagopagonha.psnapi.R;
import br.com.thiagopagonha.psnapi.utils.CommonUtilities;

import com.google.android.gcm.GCMRegistrar;

/**
 * Helper class used to communicate with the demo server.
 */
public final class ServerUtilities {

	private static final int MAX_ATTEMPTS = 5;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
	private static final Random random = new Random();

	/**
	 * Manda uma requisição pro servidor, solicitando uma atualização de status
	 * Mas essa atualização é somente um ping, irá vir pelo C2M normalmente
	 * 
	 * @param context
	 */
	public static void sync(final Context context) {
		Log.i(TAG, "Sync device");
		try {
			request(GET, SERVER_URL + "whosonline", null);
		} catch (IOException e) {
			displayMessage(context,
					context.getString(R.string.server_sync_error));
		}
	}

	/**
	 * Register this account/device pair within the server.
	 * 
	 */
	public static void register(final Context context, final String regId) {
		Log.i(TAG, "registering device (regId = " + regId + ")");
		Map<String, String> params = new HashMap<String, String>();
		params.put("key", regId);
		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
		// Once GCM returns a registration id, we need to register it in the
		// demo server. As the server might be down, we will retry it a couple
		// times.
		for (int i = 1; i <= MAX_ATTEMPTS; i++) {
			Log.d(TAG, "Attempt #" + i + " to register");
			try {
				displayMessage(context, context.getString(
						R.string.server_registering, i, MAX_ATTEMPTS));
				request(POST, SERVER_URL, params);
				GCMRegistrar.setRegisteredOnServer(context, true);
				String message = context.getString(R.string.server_registered);
				CommonUtilities.displayMessage(context, message);
				return;
			} catch (IOException e) {
				// Here we are simplifying and retrying on any error; in a real
				// application, it should retry only on unrecoverable errors
				// (like HTTP error code 503).
				Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
				if (i == MAX_ATTEMPTS) {
					break;
				}
				try {
					Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
					Thread.sleep(backoff);
				} catch (InterruptedException e1) {
					// Activity finished before we complete - exit.
					Log.d(TAG, "Thread interrupted: abort remaining retries!");
					Thread.currentThread().interrupt();
					return;
				}
				// increase backoff exponentially
				backoff *= 2;
			}
		}
		String message = context.getString(R.string.server_register_error,
				MAX_ATTEMPTS);
		CommonUtilities.displayMessage(context, message);
	}

	/**
	 * Unregister this account/device pair within the server.
	 */
	public static void unregister(final Context context, final String regId) {
		Log.i(TAG, "unregistering device (regId = " + regId + ")");
		Map<String, String> params = new HashMap<String, String>();
		params.put("key", regId);
		try {
			request(DELETE, SERVER_URL, params);
			GCMRegistrar.setRegisteredOnServer(context, false);
			String message = context.getString(R.string.server_unregistered);
			CommonUtilities.displayMessage(context, message);
		} catch (IOException e) {
			// At this point the device is unregistered from GCM, but still
			// registered in the server.
			// We could try to unregister again, but it is not necessary:
			// if the server tries to send a message to the device, it will get
			// a "NotRegistered" error message and should unregister the device.
			String message = context.getString(
					R.string.server_unregister_error, e.getMessage());
			CommonUtilities.displayMessage(context, message);
		}
	}

	/**
	 * Issue a POST request to the server.
	 * 
	 * @param endpoint
	 *            POST address.
	 * @param params
	 *            request parameters.
	 * 
	 * @throws IOException
	 *             propagated from POST.
	 */
	private static void request(Method method, String endpoint,
			Map<String, String> params) throws IOException {

		String body = null;

		if (params != null && !params.isEmpty()) {
			StringBuilder bodyBuilder = new StringBuilder();
			Iterator<Entry<String, String>> iterator = params.entrySet()
					.iterator();
			// constructs the POST body using the parameters
			while (iterator.hasNext()) {
				Entry<String, String> param = iterator.next();
				bodyBuilder.append(param.getKey()).append('=')
						.append(param.getValue());
				if (iterator.hasNext()) {
					bodyBuilder.append('&');
				}
			}

			body = bodyBuilder.toString();
		}

		URI url;
		try {
			if (!POST.equals(method)) {
				url = new URI(endpoint + "?" + body);
			} else {
				url = new URI(endpoint);
			}

			Log.v(TAG, "Posting '" + body + "' to " + url);

			HttpUriRequest request;

			HttpClient httpclient = new DefaultHttpClient();

			if (POST.equals(method)) {
				List nameValuePairs = new ArrayList();

				for (Map.Entry<String, String> param : params.entrySet()) {
					nameValuePairs.add(new BasicNameValuePair(param.getKey(),
							param.getValue()));
				}

				request = new HttpPost(url);

				((HttpPost) request).setEntity(new UrlEncodedFormEntity(
						nameValuePairs));
			} else if (DELETE.equals(method)) {
				request = new HttpDelete(url);
			} else {
				request = new HttpGet(url);
			}

			HttpResponse response = httpclient.execute(request);
			InputStream content = response.getEntity().getContent();

			BufferedReader r = new BufferedReader(
					new InputStreamReader(content));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
				total.append(line);
			}

			// handle the response
			int status = response.getStatusLine().getStatusCode();
			if (status != 200) {
				throw new IOException("Post failed with error code " + status
						+ " and message " + total);
			}
		
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("invalid url: " + endpoint);
		}

	}
}
