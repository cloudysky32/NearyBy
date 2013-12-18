package com.skynet.nearyby.async;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.skynet.nearyby.Tags;
import com.skynet.nearyby.model.Place;

import android.os.AsyncTask;
import android.util.Log;

public class GooglePlacesAsyncTask extends AsyncTask<String, String, List<Place>> {

	private static final String GOOGLE_PLACE_API_KEY = "AIzaSyChZ7BgV717SdeQEvYvnens4uS0gVsu55E";
	private static final String GOOGLE_PLACE_API_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
	private AsyncPlaceListener asyncPlaceListener = null;
	
	public GooglePlacesAsyncTask(AsyncPlaceListener asyncPlaceListener) {
		this.asyncPlaceListener = asyncPlaceListener;
	}
	
	@Override
	protected List<Place> doInBackground(String... searchString) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response;
		String responseString = null;
		
		String lat = searchString[0];
		String lng = searchString[1];
		String search = searchString[2];

		List<Place> placeList = new ArrayList<Place>();
		
		if(!search.trim().equals("")) {
			// set parameters with google API KEY
		    List<NameValuePair> params = new LinkedList<NameValuePair>();

		    params.add(new BasicNameValuePair("location", lat + "," + lng));
		    params.add(new BasicNameValuePair("radius", "500"));
		    params.add(new BasicNameValuePair("types", "food"));
		    params.add(new BasicNameValuePair("name", search));
		    params.add(new BasicNameValuePair("sensor", "false"));
		    params.add(new BasicNameValuePair("key", GOOGLE_PLACE_API_KEY));

		    String paramString = URLEncodedUtils.format(params, "utf-8");
			
			try {
				response = httpClient.execute(new HttpGet(GOOGLE_PLACE_API_URL + paramString));
				
				StatusLine statusLine = response.getStatusLine();
				
				if(statusLine.getStatusCode() == HttpStatus.SC_OK) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					response.getEntity().writeTo(out);
					out.close();
					responseString = out.toString();
					
					placeList = PlaceJsonParser.placeJsonParser(responseString);
					
					Log.d(Tags.TAG_GOOGLE_PLACE, responseString);
				} else {
					response.getEntity().getContent().close();
					throw new IOException(statusLine.getReasonPhrase());
				}
				
			} catch (ClientProtocolException e) {
				Log.e(Tags.TAG_GOOGLE_PLACE, "Client Protocol Exception " + e.toString());
			} catch (IOException e) {
				Log.e(Tags.TAG_GOOGLE_PLACE, "IO Exception " + e.toString());
			}
		}
		
		return placeList;
	}
	
	@Override
	protected void onPostExecute(List<Place> result) {
		super.onPostExecute(result);
		
		if(asyncPlaceListener != null)
			asyncPlaceListener.doStuff(result);
	}
}
