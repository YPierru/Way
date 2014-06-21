package com.ironrabbit.waybeta4ui.route.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.location.GpsStatus.NmeaListener;
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
import com.ironrabbit.waybeta4ui.R;
import com.ironrabbit.waybeta4ui.route.Route;

public class SeeRoute extends Activity {

	private GoogleMap mMap;
	private Route mRoute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_map_see);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mMap = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.mapSee)).getMap();
		mMap.setMyLocationEnabled(true);
		mRoute = getIntent().getExtras().getParcelable("trajet");
		getActionBar().setTitle(mRoute.getName());
		setSubtitle();
		drawRoute();
	}

	private void drawRoute() {
		ArrayList<LatLng> listPoints = mRoute
				.getPointsWhoDrawsPolylineLatLng();
		setMarker(listPoints.get(0), "Départ");
		PolylineOptions options = new PolylineOptions().geodesic(false)
				.width(10).color(Color.argb(120, 0, 180, 0));
		for (int i = 0; i < listPoints.size(); i++) {
			options.add(listPoints.get(i));
		}
		mMap.addPolyline(options);
		setMarker(listPoints.get(listPoints.size() - 1), "Arrivée");
		CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(listPoints.get(0),
				15);
		mMap.animateCamera(cu, 600, null);
	}

	private void setSubtitle() {
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
		getActionBar().setSubtitle(strSubtitle);
	}

	private Marker setMarker(LatLng p, String str) {
		Marker tmp = mMap.addMarker(new MarkerOptions()
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.ic_marker_princ))
				.anchor(0.0f, 1.0f) // Anchors the
									// marker on the
									// bottom left
				.position(p).title(str));
		tmp.setDraggable(false);
		tmp.showInfoWindow();
		return tmp;
	}

	public boolean onOptionsItemSelected(MenuItem menuItem) {
		onBackPressed();
		return true;
	}
}