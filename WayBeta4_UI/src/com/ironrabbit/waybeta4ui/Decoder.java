package com.ironrabbit.waybeta4ui;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.maps.GeoPoint;

/*
 * Cette classe permet de deecoder des points transmis par Google en LatLng.
 */

public class Decoder {

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
