package com.ironrabbit.waybeta4ui.asyncTasks;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ironrabbit.waybeta4ui.Constantes;
import com.ironrabbit.waybeta4ui.Decoder;
import com.ironrabbit.waybeta4ui.route.Route;
import com.ironrabbit.waybeta4ui.route.activity.CreateRoute;
import com.ironrabbit.waybeta4ui.route.downloaded.DirectionsResponse;
import com.ironrabbit.waybeta4ui.route.downloaded.Legs;

/*
 * R????cup????re les d????tails d'un trajet via l'API DirectionsResponse
 */
public class GettingRoute extends AsyncTask<Void, Void, DirectionsResponse> {

	private LatLng origin, destination;
	private final String URL_PATTERN = "https://maps.googleapis.com/maps/api/directions/json?sensor=true&language=fr&";
	private DirectionsResponse myRoad;
	private String mode;
	private static ArrayList<LatLng> listWayPoints;// LIste des jalons
	private ProgressDialog mProgDialog;
	private Route mRoute;
	private ArrayList<LatLng> mListOverviewPolylinePoints;
	private ArrayList<Marker> mListMarkers;
	private CreateRoute mMotherActivity;
	private GoogleMap mMap;
	
	public GettingRoute(CreateRoute cmr, Route r,ArrayList<LatLng> all,ArrayList<Marker> am, GoogleMap m) {
		this.mMotherActivity=cmr;
		this.mProgDialog = new ProgressDialog(cmr);
		this.mRoute=r;
		this.mListOverviewPolylinePoints=all;
		this.mListMarkers=am;
		this.mMap=m;
	}
	
	

	protected void onPreExecute() {
		super.onPreExecute();
		//Log.d("DEBUUUUUG", "pre execute");
		this.mProgDialog.setMessage("Dessin en cours...");
		this.mProgDialog.show();
	}

	protected DirectionsResponse doInBackground(Void... arg0) {

		// Construction de l'URL

		//Log.d("DEBUUUUUG", "execute");

		listWayPoints = this.mRoute.getListMarkersLatLng();
		/*if (this.mRoute.getTypeRoute().equals("VOITURE")) {
			this.mode = "driving";
		} else {
			this.mode = "walking";
		}*/
		this.mode = "driving";
		//Log.d("DEBUUUUUG", this.mode);
		this.origin = listWayPoints.get(0);
		this.destination = listWayPoints.get(listWayPoints.size() - 1);
		listWayPoints.remove(0);
		listWayPoints.remove(listWayPoints.size() - 1);
		String wayPointsStr = "&waypoints=";

		for (int i = 0; i < listWayPoints.size(); i++) {
			wayPointsStr += listWayPoints.get(i).latitude + ","
					+ listWayPoints.get(i).longitude;
			if (i + 1 < listWayPoints.size()) {
				wayPointsStr += "|";
			}
		}

		URL url = null;
		try {
			url = new URL(this.URL_PATTERN + "mode=" + this.mode + "&"
					+ "origin=" + this.origin.latitude + ","
					+ this.origin.longitude + "&destination="
					+ this.destination.latitude + ","
					+ this.destination.longitude + wayPointsStr);
			// Appel de l'API, parsing du JSON r????cup????r????
			//Log.d("DEBUUUUUUUG", url.toString());
			InputStream is = url.openStream();
			String strRoad = IOUtils.toString(is);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			this.myRoad = gson.fromJson(strRoad, DirectionsResponse.class);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Log.d("DEBUUUUUG", "c??fini lol");
		return this.myRoad;
	}

	protected void onPostExecute(DirectionsResponse result) {
		super.onPostExecute(result);
		//Log.d("DEBUUUUUG", "post execute");
		/*
		 * Ici sera dessin?? le trajet.
		 */		
		if (this.mProgDialog.isShowing()) {
			
			
			DirectionsResponse myRoad = result;
			mRoute.getListSegment().clear();
			mRoute.getListSegment().add(myRoad);
			// tj.setDraw(true);

			// listRealPoints.clear();
			// Liste de tout les points du trajet (overview_polyline)
			mListOverviewPolylinePoints = Decoder.decodePoly(myRoad
					.getRoutes().get(0).getOverview_polyline().getPoints());

			// Le bloc ci-dessous permet de r??cup??rer les coo LatLng des
			// Markers apr??s correction de google
			List<Legs> listLegs = myRoad.getRoutes().get(0).getLegs();
			ArrayList<LatLng> tmpPoints = new ArrayList<LatLng>();
			for (int i = 0; i < listLegs.size(); i++) {
				tmpPoints.add(new LatLng(listLegs.get(i)
						.getStart_location().getLat(), listLegs.get(i)
						.getStart_location().getLng()));
				if (i + 1 == listLegs.size()) {
					tmpPoints.add(new LatLng(listLegs.get(i)
							.getEnd_location().getLat(), listLegs.get(i)
							.getEnd_location().getLng()));
				}
			}
			mRoute.setListMarkersLatLng(tmpPoints);
			// Met les Marker ?? leur nouvelle place
			for (int i = 0; i < mListMarkers.size(); i++) {
				mListMarkers.get(i).setPosition(tmpPoints.get(i));
			}
			// currentTrajet.setListMarker(listMarkers);

			// Log.d("DEBUUUUUUG","LT = "+tmpPoints.size()+" LMB = "+listMarkersBad.size());
			PolylineOptions options = new PolylineOptions().geodesic(false)
					.width(Constantes.WIDTH_POLYLINE).color(Constantes.COLOR_POLYLINE);
			for (int i = 0; i < mListOverviewPolylinePoints.size(); i++) {
				options.add(mListOverviewPolylinePoints.get(i));
			}
			mMotherActivity.setPolyline(mMap.addPolyline(options));
			mRoute.setPointsWhoDrawsPolylineLatLng(mListOverviewPolylinePoints);
			mRoute.setValidate(true);

			String strSubtitle = "";
			double dist = mRoute.getDistTotal();
			if (dist < 1000) {
				strSubtitle += ((int) dist + "m");
			} else {
				strSubtitle += ((dist / 1000) + "Km");
			}

			int dureeSecond = mRoute.getDureeTotal();
			int heures = (dureeSecond / 3600);
			int minutes = ((dureeSecond % 3600) / 60);
			if (heures == 0) {
				strSubtitle += " - ~" + (minutes + "min");
			} else {
				strSubtitle += " - ~" + (heures + "h" + minutes + "min");
			}
			this.mMotherActivity.getActionBar().setSubtitle(strSubtitle);
			
			
			this.mProgDialog.dismiss();
		}
	}

}
