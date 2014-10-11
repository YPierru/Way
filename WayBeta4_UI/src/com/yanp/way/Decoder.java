package com.yanp.way;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.maps.GeoPoint;

/**
 * Decode the coordinates transmits by Google Maps API
 * @author YPierru
 *
 */
public class Decoder {

	/**
	 * 
	 * @param encoded - The encoded string transmits by maps API
	 * @return ArrayList of all the point (LatLng) decode by the function
	 */
	public static ArrayList<LatLng> decodePoly(String encoded) {

		ArrayList<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E6),
					(int) (((double) lng / 1E5) * 1E6));
			LatLng ll = new LatLng(p.getLatitudeE6() / 1E6,
					p.getLongitudeE6() / 1E6);
			poly.add(ll);
		}

		return poly;
	}

}
