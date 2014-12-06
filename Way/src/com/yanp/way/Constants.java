package com.yanp.way;

import java.util.Locale;

import android.graphics.Color;
import android.os.Environment;

/**
 * @class Constantes
 * 
 * All the constants used by the app are here.
 * 
 * @author YPierru
 *
 */

public final class Constants {
	
	/**
	 * <b>{@value}</b><br />
	 * If true, the position is calculate with cellular data.
	 * If false, the app will use the phone's intern GPS.
	 */
	public static final boolean NETWORK_GPS=true;
	
	/**
	 * <b>{@value}</b><br />
	 * Level of zoom when the user creates a new route.
	 */
	public static final int ZOOM_NEW_ROUTE=16;
	
	/**
	 * <b>{@value}</b><br />
	 * Default level of zoom.
	 */
	public static final int ZOOM_GENERAL=15;
	
	/**
	 * <b>{@value}</b><br />
	 * Level of zoom during GPS navigation
	 */
	public static final int ZOOM_GPS=18;	
	
	/**
	 * <b>{@value}</b> ms<br />
	 * Speed of the camera animation
	 */
	public static final int ZOOM_SPEED_MS=600;
	
	/**
	 * <b>{@value}</b><br />
	 * Tilt of the camera view during GPS navigation
	 */
	public static final int USER_GPS_TILT=90;
	
	/**
	 * <b>{@value}</b> meters<br />
	 * Radius of detection of user's position on a point.
	 * If the user is closer than this value, consider that he is on the point.
	 */
	public static final int RADIUS_DETECTION=18;
	
	/**
	 * <b>{@value}</b> ms<br />
	 * Minimum time beetween 2 GPS requests.
	 */
	public static final int MIN_TIME_GPS_REQUEST_MS=5;

	/**
	 * <b>{@value}</b> ms<br />
	 * Minimum distance beetween 2 GPS requests.
	 */
	public static final int MIN_DIST_GPS_REQUEST_M=0;

	/**
	 * Path of the root of the external storage directory.
	 */
	public static final String PATH_FILE_ROUTES=Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
	
	/**
	 * <b>{@value}</b><br />
	 * Size of the text in the wheel.
	 */
	public static final int TEXT_SIZE_WHEEL=13;
	
	/**
	 * <b>{@value}</b><br />
	 * Width of the line draws between 2 points.
	 */
	public static final int WIDTH_POLYLINE=10;
	
	/**
	 * Color of the line draws between 2 points.
	 */
	public static final int COLOR_POLYLINE=Color.argb(120, 0, 180, 0);
	
	/**
	 * <b>{@value}</b><br />
	 * Width of the line draws during GPS navigation.
	 */
	public static final int WIDTH_POLYLINE_GPS=12;
	
	/**
	 * Color of the line draws during GPS navigation.
	 */
	public static final int COLOR_POLYLINE_GPS=Color.argb(200, 0, 120, 0);
	
	/**
	 * <b>{@value}</b><br />
	 * Earth radius.
	 */
	public static final double EARTH_RADIUS=3958.75;
	
	/**
	 * <b>{@value}</b><br />
	 * The current language of the device
	 */
	public static final Locale CURRENT_LANGUAGE=Locale.getDefault();

}
