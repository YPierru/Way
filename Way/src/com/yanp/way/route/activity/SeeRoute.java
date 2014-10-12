package com.yanp.way.route.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.yanp.way.Constants;
import com.yanp.way.R;
import com.yanp.way.route.Route;
/**
 * Display the route on a google map
 * @author YPierru
 *
 */
public class SeeRoute extends Activity {

	private GoogleMap googleMap;
	private Route route;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_map_see);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		this.googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapSee)).getMap();
		this.googleMap.setMyLocationEnabled(true);
		
		this.route = getIntent().getExtras().getParcelable("route");
		
		getActionBar().setTitle(route.getName());
		setSubtitle();
		drawRoute();
	}

	/**
	 * Draw the route on the map
	 */
	private void drawRoute() {
		ArrayList<LatLng> listPoints = route.getPointsWhoDrawsPolylineLatLng();
		
		setMarker(listPoints.get(0), "Départ");
		
		PolylineOptions options = new PolylineOptions().geodesic(false).width(10).color(Constants.COLOR_POLYLINE);
		
		for (int i = 0; i < listPoints.size(); i++) {
			options.add(listPoints.get(i));
		}
		
		this.googleMap.addPolyline(options);
		
		setMarker(listPoints.get(listPoints.size() - 1), "Arrivée");
		CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(listPoints.get(0),Constants.ZOOM_GENERAL);
		
		this.googleMap.animateCamera(cu, Constants.ZOOM_SPEED_MS, null);
	}

	/**
	 * Write some Route details on the Action bar
	 */
	private void setSubtitle() {
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
		
		getActionBar().setSubtitle(strSubtitle);
	}

	/**
	 * Put a marker on the map
	 * @param position
	 * @param infoMessage
	 */
	private void setMarker(LatLng position, String infoMessage) {
		Marker tmp = this.googleMap.addMarker(new MarkerOptions()
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.ic_marker_princ))
				.anchor(0.0f, 1.0f) // Anchors the
									// marker on the
									// bottom left
				.position(position).title(infoMessage));
		tmp.setDraggable(false);
		tmp.showInfoWindow();
	}

	public boolean onOptionsItemSelected(MenuItem menuItem) {
		onBackPressed();
		return true;
	}
}