package com.yanp.way.gps;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yanp.way.R;

/**
 * Manage the position of the user during the GPS navigation
 * @author YPierru
 *
 */
public class UserPosition {

	private LatLng currentPosition;
	private Marker myMarker;
	private boolean followNavigation;
	private int indexPointToFollow;
	private float bearing;

	public UserPosition() {
		this.myMarker = null;
		this.followNavigation = false;
		this.indexPointToFollow = 0;
		this.bearing = 0;
	}

	/**
	 * Set the current position of the user
	 * @param lat
	 * @param lng
	 * @param bearing
	 * @param map
	 */
	public void setCurrentPos(Double lat, Double lng, float bearing,
			GoogleMap map) {
		this.currentPosition = new LatLng(lat, lng);
		this.bearing = bearing;
		this.addCurrentPosOnMap(map);
	}

	/**
	 * Get the LatLng current position of the user
	 * @return
	 */
	public LatLng getCurrentPos() {
		return this.currentPosition;
	}

	/**
	 * Get the index point the user must reach
	 * @return
	 */
	public int getIndexPointToFollow() {
		return this.indexPointToFollow;
	}

	public void setToNextPointToFollow() {
		this.indexPointToFollow++;
	}

	public boolean isFollowingNavigation() {
		return this.followNavigation;
	}

	public void setFollowingNavigation(boolean followNavigation) {
		this.followNavigation = followNavigation;
	}

	/**
	 * Add the user's marker on the map and define the camera animation
	 * @param map The Google Map
	 */
	public void addCurrentPosOnMap(GoogleMap map) {

		if (this.myMarker == null) {
			this.myMarker = map.addMarker(new MarkerOptions()
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.android))
					.anchor(0.0f, 1.0f).position(this.currentPosition));

			CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(
					this.currentPosition, 18);
			map.animateCamera(cu);
		} else {
			this.myMarker.setPosition(this.currentPosition);
			CameraPosition cp = new CameraPosition.Builder()
					.target(this.currentPosition)
					.tilt(90)
					.bearing(this.bearing)
					.zoom(18)
					.build();
			map.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
		}
	}

}
