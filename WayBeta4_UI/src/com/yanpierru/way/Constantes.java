package com.yanpierru.way;

import android.graphics.Color;
import android.os.Environment;

public final class Constantes {
	
	public static final boolean NETWORK_GPS=true;
	
	public static final int ZOOM_NEW_ROUTE=16;
	public static final int ZOOM_GENERAL=15;
	public static final int ZOOM_GPS=18;	
	public static final int ZOOM_SPEED_MS=600;
	
	public static final int USER_GPS_TILT=90;
	public static final int RADIUS_DETECTION=18;
	public static final int MIN_TIME_GPS_REQUEST_MS=5;
	public static final int MIN_DIST_GPS_REQUEST_M=0;
	
	public static final String NAME_FILE_ROUTES=".routescollection.way";
	public static final String PATH_FILE_ROUTES=Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
	
	public static final int TEXT_SIZE_WHEEL=13;
	
	public static final String API_KEY="AIzaSyB4m_X5XwwnhYenzLhIexv-glVWu-j_Egs";
	
	public static final int WIDTH_POLYLINE=10;
	public static final int COLOR_POLYLINE=Color.argb(120, 0, 180, 0);
	public static final int WIDTH_POLYLINE_GPS=12;
	public static final int COLOR_POLYLINE_GPS=Color.argb(200, 0, 120, 0);
	
	public static final double EARTH_RADIUS=3958.75;
	public static final int METER_CONVERSION=1609;
	
	//public final static String TYPE_ROUTE_VOITURE = "VOITURE";
	//public final static String TYPE_ROUTE_COUREUR = "COUREUR";

}
