package com.yanpierru.way.gps;

import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.yanpierru.way.R;

public class UserPosition {

	private LatLng mCurrentPos;
	private ArrayList<LatLng> mHistoPos;
	private Marker mMyMarker;
	private boolean isOnRoute;
	private int mIndexPointToFollow;
	private Polyline mPolyline;
	private float mBearing;

	public UserPosition(LatLng p) {
		this.mCurrentPos = p;
		this.mMyMarker = null;
		this.mPolyline = null;
		this.mHistoPos = new ArrayList<LatLng>();
		this.mHistoPos.add(p);
		this.isOnRoute = false;
		this.mIndexPointToFollow = 0;
		this.mBearing = 0;
	}

	public UserPosition() {
		this.mMyMarker = null;
		this.mPolyline = null;
		this.mHistoPos = new ArrayList<LatLng>();
		this.isOnRoute = false;
		this.mIndexPointToFollow = 0;
		this.mBearing = 0;
	}

	public void setCurrentPos(Double lat, Double lng, float bearing,
			GoogleMap map) {
		this.mCurrentPos = new LatLng(lat, lng);
		this.mBearing = bearing;
		this.mHistoPos.add(this.mCurrentPos);
		this.addCurrentPosOnMap(map);
	}

	public LatLng getCurrentPos() {
		return this.mCurrentPos;
	}

	public int getIndexPointToFollow() {
		return this.mIndexPointToFollow;
	}

	public void setToNextPointToFollow() {
		this.mIndexPointToFollow++;
	}

	public boolean isOnRoute() {
		return this.isOnRoute;
	}

	public void setIsOnRoute(boolean b) {
		this.isOnRoute = b;
	}

	public void addCurrentPosOnMap(GoogleMap map) {

		if (this.mMyMarker == null) {
			this.mMyMarker = map.addMarker(new MarkerOptions()
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.android))
					.anchor(0.0f, 1.0f).position(this.mCurrentPos));

			CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(
					this.mCurrentPos, 18);
			map.animateCamera(cu);
		} else {
			this.mMyMarker.setPosition(this.mCurrentPos);
			CameraPosition cp = new CameraPosition.Builder()
					.target(this.mCurrentPos)
					.tilt(90)
					.bearing(this.mBearing)
					.zoom(18)
					.build();
			map.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
		}
	}

	/*
	 * public void drawUserRoute(GoogleMap map) { if (this.mPolyline == null) {
	 * if (this.mHistoPos.size() > 0) { PolylineOptions options = new
	 * PolylineOptions().geodesic(false) .width(15).color(Color.argb(255, 0, 0,
	 * 221)); for (int i = 0; i < this.mHistoPos.size(); i++) {
	 * options.add(this.mHistoPos.get(i)); } this.mPolyline =
	 * map.addPolyline(options); } } else {
	 * this.mPolyline.setPoints(this.mHistoPos); } }
	 */

	/*public int distanceBetween(LatLng p) {
		double lat1 = this.mCurrentPos.latitude;
		double lng1 = this.mCurrentPos.longitude;
		double lat2 = p.latitude;
		double lng2 = p.longitude;

		double earthRadius = 3958.75;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
				* Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = earthRadius * c;

		int meterConversion = 1609;

		return (int) (dist * meterConversion);
	}*/

}
