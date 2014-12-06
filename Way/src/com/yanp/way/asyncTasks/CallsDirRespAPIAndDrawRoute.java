package com.yanp.way.asyncTasks;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yanp.way.Constants;
import com.yanp.way.Decoder;
import com.yanp.way.R;
import com.yanp.way.route.Route;
import com.yanp.way.route.activity.CreateRoute;
import com.yanp.way.route.downloaded.DirectionsResponse;
import com.yanp.way.route.downloaded.Legs;

/*
 * R????cup????re les d????tails d'un trajet via l'API DirectionsResponse
 */

/**
 * Get the details of a route using Google DirectionsResponse API, draw this route on the map.
 * @author YPierru
 *
 */
public class CallsDirRespAPIAndDrawRoute extends AsyncTask<Void, Void, DirectionsResponse> {

	private String url_pattern;
	private ProgressDialog progressDialog;
	private Route route;
	private ArrayList<Marker> listMarkers;
	private CreateRoute createRouteInstance;
	private GoogleMap googleMap;
	
	/**
	 * 
	 * @param createRouteInstance - Reference of the activity where the route will be draw
	 * @param route - The route, this object will be filled with data here
	 * @param listMarkers - The list of the markers created on the map by the user
	 * @param googleMap - The Google Map
	 */
	public CallsDirRespAPIAndDrawRoute(CreateRoute createRouteInstance, Route route,ArrayList<Marker> listMarkers, GoogleMap googleMap) {
		this.createRouteInstance=createRouteInstance;
		this.progressDialog = new ProgressDialog(createRouteInstance);
		this.route=route;
		this.listMarkers=listMarkers;
		this.googleMap=googleMap;
		this.url_pattern=this.createRouteInstance.getResources().getString(R.string.url_pattern_directionsapi)+"&language="+Constants.CURRENT_LANGUAGE.getLanguage();
	}
	
	/**
	 * Display the informative message during the wait
	 */
	protected void onPreExecute() {
		super.onPreExecute();
		this.progressDialog.setMessage(this.createRouteInstance.getResources().getString(R.string.drawing_in_progress)+"...");
		this.progressDialog.show();
	}

	
	protected DirectionsResponse doInBackground(Void... arg0) {
		
		String str=constructStringPoints();
		DirectionsResponse dataFromDirRespAPI = callAPI(str);
		return dataFromDirRespAPI;
	}
	
	/**
	 * Transform a list of LatLng points, in a String with the pattern : "point1.x,point1.y|point2.x,point2.y|...|pointN.x,pointN.y"
	 * @return the String of the points
	 */
	private String constructStringPoints(){
		/**
		 * First, we get the LatLng positions of the markers.
		 */
		ArrayList<LatLng>listIntermediaryPoints = this.route.getListMarkersLatLng();
		
		/**
		 * The first and last points will be in the URL, we must remove them here.
		 */
		listIntermediaryPoints.remove(0);
		listIntermediaryPoints.remove(listIntermediaryPoints.size() - 1);
		String intermiediaryPoints = "&waypoints=";

		/**
		 * Construction of the String
		 */
		for (int i = 0; i < listIntermediaryPoints.size(); i++) {
			intermiediaryPoints += listIntermediaryPoints.get(i).latitude + ","
					+ listIntermediaryPoints.get(i).longitude;
			if (i + 1 < listIntermediaryPoints.size()) {
				intermiediaryPoints += "|";
			}
		}

		return intermiediaryPoints;
	}
	
	/**
	 * Construct the URL, call the API, parse the result
	 * @param intermediaryPointsString
	 * @return The JAVA struct from the JSON
	 */
	private DirectionsResponse callAPI(String intermediaryPointsString){
		
		DirectionsResponse dataFromDirRespAPI = null;
		
		/**
		 * See https://developers.google.com/maps/documentation/directions/#RequestParameters
		 */
		String mode = "driving";
		
		LatLng origin = this.route.getListMarkersLatLng().get(0);
		LatLng destination = this.route.getListMarkersLatLng().get(this.route.getListMarkersLatLng().size()-1);
		
		URL url = null;
		try {
			url = new URL(this.url_pattern 
						  + "&mode=" + mode 
						  + "&origin=" + origin.latitude + ","+ origin.longitude 
						  + "&destination=" + destination.latitude + ","+ destination.longitude 
						  + intermediaryPointsString);
			
			/**
			 * Here we get the data from the API.
			 * Then the JSON is convert to a JAVA structure (DirectionsResponse)
			 */
			InputStream is = url.openStream();
			String jsonData = IOUtils.toString(is);
			//Log.d("DEBUUUUUUUUG", jsonData);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			dataFromDirRespAPI = gson.fromJson(jsonData, DirectionsResponse.class);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return dataFromDirRespAPI;
	} 

	protected void onPostExecute(DirectionsResponse dataFromDirRespAPI) {
		super.onPostExecute(dataFromDirRespAPI);

		/**
		 * We add the data to the route		
		 */
		this.route.getListSegment().clear();
		this.route.getListSegment().add(dataFromDirRespAPI);
		
		
		
		replaceMarkers(dataFromDirRespAPI);
		
		drawRouteOnMap(dataFromDirRespAPI);

		writeRouteDetailsOnActionBar();
		
		this.progressDialog.dismiss();
	}
	
	/**
	 * Replace the markers on the road.
	 * @param dataFromDirRespAPI
	 */
	private void replaceMarkers(DirectionsResponse dataFromDirRespAPI){
		List<Legs> listLegs = dataFromDirRespAPI.getRoutes().get(0).getLegs();
		ArrayList<LatLng> tmpPoints = new ArrayList<LatLng>();
		
		for (int i = 0; i < listLegs.size(); i++) {
			
			tmpPoints.add(new LatLng(listLegs.get(i).getStart_location().getLat(), 
									 listLegs.get(i).getStart_location().getLng()));
			
			if (i + 1 == listLegs.size()) {
				tmpPoints.add(new LatLng(listLegs.get(i).getEnd_location().getLat(), 
										 listLegs.get(i).getEnd_location().getLng()));
			}
			
		}
		
		this.route.setListMarkersLatLng(tmpPoints);

		for (int i = 0; i < this.listMarkers.size(); i++) {
			this.listMarkers.get(i).setPosition(tmpPoints.get(i));
		}
	}
	
	/**
	 * Draw the route on the Google Map
	 */
	private void drawRouteOnMap(DirectionsResponse dataFromDirRespAPI){
		/**
		 * We decode the points of the route
		 */
		ArrayList<LatLng> listOverviewPolylinePoints = Decoder.decodePoly(dataFromDirRespAPI.getRoutes().get(0).getOverview_polyline().getPoints());
		
		PolylineOptions options = new PolylineOptions()
		 .geodesic(false)
		 .width(Constants.WIDTH_POLYLINE)
		 .color(Constants.COLOR_POLYLINE);
		
		for (int i = 0; i < listOverviewPolylinePoints.size(); i++) {
			options.add(listOverviewPolylinePoints.get(i));
		}
		
		this.createRouteInstance.setPolyline(googleMap.addPolyline(options));
		this.route.setPointsWhoDrawsPolylineLatLng(listOverviewPolylinePoints);
		this.route.setValidate(true);
	}
	
	/**
	 * Write the details of the route (distance, duration), on the action bar
	 */
	private void writeRouteDetailsOnActionBar(){
		
		String strSubtitle = "";
		double dist = route.getDistTotal();
		
		if (dist < 1000) {
			strSubtitle += ((int) dist + "m");
		} else {
			strSubtitle += ((dist / 1000) + "Km");
		}

		int dureeSecond = route.getDureeTotal();
		int heures = (dureeSecond / 3600);
		int minutes = ((dureeSecond % 3600) / 60);
		if (heures == 0) {
			strSubtitle += " - ~" + (minutes + "min");
		} else {
			strSubtitle += " - ~" + (heures + "h" + minutes + "min");
		}
		this.createRouteInstance.getActionBar().setSubtitle(strSubtitle);
	}
	
}
