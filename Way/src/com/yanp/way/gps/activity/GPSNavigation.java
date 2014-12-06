package com.yanp.way.gps.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.joda.time.DateTime;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.yanp.way.Chrono;
import com.yanp.way.Constants;
import com.yanp.way.R;
import com.yanp.way.gps.UserPosition;
import com.yanp.way.route.Route;
import com.yanp.way.route.downloaded.Step;


/*
 * DO
 * On affiche la position de l'utilisateur, uniquement via le GPS, pour plus de pr??cision.
 * On affiche le parcours
 * TODO
 * Synchroniser la position de l'user avec celle du parcours :
 * Lorsque l'user franchit une step, les informations de direction sont mise ?? jour.
 * Trouver des ??l??ments graphiques sympa pour afficher les infos de directions
 * 
 */

/**
 * Display the GPS navigation on a selected route.
 * @author YPierru
 *
 */
public class GPSNavigation extends Activity implements SensorEventListener,TextToSpeech.OnInitListener {
	
	private LocationManager locationManager;
	private TextToSpeech textToSpeech;
	private MyLocationListener myLocationListener;
	private ArrayList<LatLng> listPointsToFollow=new ArrayList<LatLng>();
	private Route mRoute;
	private ArrayList<Step> listSteps;
	private GoogleMap googleMap;
	private int totalDistance=0;
	private int totalDuration=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_gps);
		
		//The screen will not sleep during the navigaion
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
				
		
		getActionBar().hide();

		
		this.googleMap = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.mapGPS)).getMap();
		this.googleMap.getUiSettings().setZoomControlsEnabled(false);
		
		mRoute = getIntent().getExtras().getParcelable("Route_for_navigation_gps");
		
		this.textToSpeech = new TextToSpeech(this, this);
		
		initTotalDurationDistance();
		
		initListPointsToFollow();
		
		//On dessine le trajet
		drawRoute();
	}
	
	/**
	 * Get the total distance and duration of the route
	 */
	public void initTotalDurationDistance(){
		this.listSteps=mRoute.getListSteps();
		
		for(int i=0;i<this.listSteps.size();i++){
			this.totalDistance+=this.listSteps.get(i).getDistance().getValue();
			this.totalDuration+=this.listSteps.get(i).getDuration().getValue();
		}
	}
	
	/**
	 * Init the list of the points (LatLng) the user will follow, from the list of the Steps
	 */
	public void initListPointsToFollow(){
		for (int i = 0; i < this.listSteps.size(); i++) {
			
			this.listPointsToFollow.add(new LatLng(this.listSteps.get(i).getStart_location().getLat(), 
												   this.listSteps.get(i).getStart_location().getLng()));
			if(i+1==this.listSteps.size()){
				this.listPointsToFollow.add(new LatLng(this.listSteps.get(i).getEnd_location().getLat(), 
						  							   this.listSteps.get(i).getEnd_location().getLng()));
			}
		}
	}
	
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			
			int result = this.textToSpeech.setLanguage(Constants.CURRENT_LANGUAGE);
			
			if (result == TextToSpeech.LANG_MISSING_DATA || 
			    result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("error", "This Language is not supported");
			}
			
		} else {
			Log.e("error", "Initilization Failed!");
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.textToSpeech.shutdown();
	}
	
	/**
	 * Draw the route on the map
	 */
	private void drawRoute(){
		ArrayList<LatLng> listPointsOverview = mRoute.getPointsWhoDrawsPolylineLatLng();
		ArrayList<LatLng> listStepMarkers = mRoute.getListMarkersLatLng();
		
		LatLng startingPoint = listPointsOverview.get(0);
		LatLng destinationPoint = listPointsOverview.get(listPointsOverview.size()-1);
		
		setMarker(startingPoint, getResources().getString(R.string.start), false);
		
		CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(startingPoint,Constants.ZOOM_GENERAL);
		
		this.googleMap.animateCamera(cu, Constants.ZOOM_SPEED_MS, null);
		
		/*
		 * Place the marker on the map
		 */
		if(listStepMarkers.size()>2){
			for(int i=1;i<listStepMarkers.size()-1;i++){
				setMarker(listStepMarkers.get(i), getResources().getString(R.string.marker)+" "+i, true);
			}
		}

		/*
		 * Draw the line
		 */
		PolylineOptions options = new PolylineOptions()
									  .geodesic(false)
									  .width(Constants.WIDTH_POLYLINE_GPS)
									  .color(Constants.COLOR_POLYLINE_GPS);
		
		for (int i = 0; i < listPointsOverview.size(); i++) {
			options.add(listPointsOverview.get(i)); 
		}
		
		this.googleMap.addPolyline(options);
		
				
		setMarker(destinationPoint, getResources().getString(R.string.arrival), false);
	}
	
	/**
	 * 
	 * @param point LatLng- The LatLng point where you want the marker
	 * @param str String- The message for the info window
	 * @param isInter boolean- if true, the marker is an intermediary point (little image), if not it's the starting/destination point (big image)
	 */
	public void setMarker(LatLng point, String str, boolean isInter) {
		int markerIcn;
		if(isInter){
			markerIcn=R.drawable.ic_marker_inter;
		}else{
			markerIcn=R.drawable.ic_marker_princ;
		}
		this.googleMap.addMarker(
				new MarkerOptions()
						.icon(BitmapDescriptorFactory
								.fromResource(markerIcn))
						.anchor(0.0f, 1.0f) // Anchors the
											// marker on the
											// bottom left
						.position(point).title(str)).showInfoWindow();
	}

	
	@Override
	protected void onResume() {
		super.onResume();
		this.myLocationListener = new MyLocationListener(this.googleMap, this.listSteps, this.textToSpeech, this.listPointsToFollow, this.totalDistance, this.totalDuration);
		
		this.locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

		if(Constants.NETWORK_GPS){
			
			this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
														Constants.MIN_TIME_GPS_REQUEST_MS, 
														Constants.MIN_DIST_GPS_REQUEST_M,
														this.myLocationListener);
		}else{
			
			if (!this.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				buildAlertMessageNoGps();
			}else{
				this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
															Constants.MIN_TIME_GPS_REQUEST_MS, 
															Constants.MIN_DIST_GPS_REQUEST_M,
															this.myLocationListener);
			}
		}
			
	}

	/**
	 * Shows the dialog if the user doesn't have enable the GPS location.
	 */
	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				getResources().getString(R.string.alert_gps_disable))
				.setCancelable(false)
				.setPositiveButton(getResources().getString(R.string.ok),
						new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog,final int id) {
								startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							}
						})
				.setNegativeButton(getResources().getString(R.string.exit), new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						dialog.cancel();
						finish();
					}
				});
		
		final AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.locationManager.removeUpdates(this.myLocationListener);
	}

	/**
	 * Custom location listener.
	 * Core of the gps navigation.
	 * @author YPierru
	 *
	 */
	private class MyLocationListener implements LocationListener {
		
		private int distanceOfTheStep;
		private int totalRemainingDist;
		private int distUserNextPoint;
		private int secondsChrono;
		private int indexCurrentPoint;
		private int stepDuration=0;
		private int remainingTimeBeforeNextPoint=0;
		private int totalDistance;
		private int totalDuration;
		
		private Step currentStep;
		private Step nextStep=null;
		
		private boolean imlate=false;
		private boolean isSpeakingBeforeRoute=false;
		private boolean allInfoAreDisplaying=false;
		private boolean mustSpeak1000=true,mustSpeak500=true,mustSpeak200=true,mustSpeak50=true;
		
		private GoogleMap googleMap;
		
		private Chrono chronometer;
		
		private ArrayList<Step> listSteps;
		private ArrayList<LatLng> listPointsToFollow;
		
		private UserPosition userPosition;
		
		private TextToSpeech textToSpeech;

		public MyLocationListener(GoogleMap googleMap, ArrayList<Step> listSteps, TextToSpeech textToSpeech, ArrayList<LatLng> listPointsToFollow, int totalDistance, int totalDuration) {
			this.googleMap=googleMap;
			this.chronometer=new Chrono();
			this.listSteps=listSteps;
			this.userPosition = new UserPosition();
			this.textToSpeech=textToSpeech;
			this.listPointsToFollow=listPointsToFollow;
			this.totalDistance=totalDistance;
			this.totalDuration=totalDuration;
		}
		
		/**
		 * When the position of my user change
		 */
		@Override
		public void onLocationChanged(Location location) {			
			
			this.indexCurrentPoint = this.userPosition.getIndexPointToFollow();
			
			this.userPosition.setCurrentPos(location.getLatitude() , 
									   location.getLongitude(), 
									   location.getBearing(),
									   this.googleMap);
			
			
			this.distUserNextPoint=formatDist(distanceBetween(this.userPosition.getCurrentPos(),this.listPointsToFollow.get(this.indexCurrentPoint)));
			
			this.currentStep=this.listSteps.get(this.indexCurrentPoint);

			if(this.indexCurrentPoint<this.listPointsToFollow.size()-1){
				this.nextStep=this.listSteps.get(this.indexCurrentPoint+1);
			}

			/*
			 * Calcul the estimate remaining time before reaching the next point 
			 */
			
			calculateRemainingTimeBeforeNextPoint();
			
			if(!this.userPosition.isFollowingNavigation()){
				checkIfUserIsReachingTheStartingPoint();				
			}

			else{
				displayDist();
				
				this.distanceOfTheStep=this.listSteps.get(this.indexCurrentPoint).getDistance().getValue();
				
				this.totalRemainingDist=this.totalDistance-(this.distanceOfTheStep-this.distUserNextPoint);
				
				displayInfoFinish();
						
				speakAccordingToDistanceBetweenUserAndNextPoint();
								
				if(this.distUserNextPoint<Constants.RADIUS_DETECTION){
					actionWhenUserReachNextPoint();
				}
			}
		}
		
		public void actionWhenUserReachNextPoint(){
			this.mustSpeak1000=true;
			this.mustSpeak200=true;
			this.mustSpeak50=true;
			this.mustSpeak500=true;
			this.chronometer.stop();
			this.secondsChrono=(int)this.chronometer.getSeconds();
			
			if(this.secondsChrono>=this.stepDuration){
				this.imlate=true;
				this.remainingTimeBeforeNextPoint=this.secondsChrono-this.stepDuration;
			}else{
				this.imlate=false;
				this.remainingTimeBeforeNextPoint=this.stepDuration-this.secondsChrono;
			}
			
			this.chronometer.start();
			if(this.indexCurrentPoint<this.listPointsToFollow.size()-1){
				this.userPosition.setToNextPointToFollow();
				
				displayInstructions();
				this.totalDistance=this.totalDistance-this.distanceOfTheStep;
				
				this.distUserNextPoint=formatDist(distanceBetween(this.userPosition.getCurrentPos(), this.listPointsToFollow.get(this.indexCurrentPoint+1)));
				this.textToSpeech.speak(getResources().getString(R.string.gps_in)+" "+this.distUserNextPoint+" "+getResources().getString(R.string.gps_distance_unit)+", "+Html.fromHtml(this.listSteps.get(this.indexCurrentPoint+1).getHtml_instructions()).toString(), TextToSpeech.QUEUE_FLUSH, null);
				
				if(this.distUserNextPoint>500 && this.distUserNextPoint <=1000){
					this.mustSpeak1000=false;
				}else if(this.distUserNextPoint>200 && this.distUserNextPoint <=500){
					this.mustSpeak500=false;
				}else if(this.distUserNextPoint>50 && this.distUserNextPoint <=200){
					this.mustSpeak200=false;
				}else if(this.distUserNextPoint<=50){
					this.mustSpeak50=false;
				}
			}
			
			else{
				this.userPosition.setFollowingNavigation(false);
				this.textToSpeech.speak(getResources().getString(R.string.gps_arrival_phrase), TextToSpeech.QUEUE_FLUSH, null);
			}
		}
		
		public void speakAccordingToDistanceBetweenUserAndNextPoint(){
			if(500<this.distUserNextPoint && this.distUserNextPoint<=1000 && this.mustSpeak1000){
				this.mustSpeak1000=false;
				this.textToSpeech.speak(getResources().getString(R.string.gps_in)+" "+this.distUserNextPoint+" "+getResources().getString(R.string.gps_distance_unit)+", "+Html.fromHtml(this.listSteps.get(this.indexCurrentPoint).getHtml_instructions()).toString(), TextToSpeech.QUEUE_ADD, null);
			}
			if(200<this.distUserNextPoint && this.distUserNextPoint<=500 && this.mustSpeak500){
				this.mustSpeak500=false;
				this.textToSpeech.speak(getResources().getString(R.string.gps_in)+" "+this.distUserNextPoint+" "+getResources().getString(R.string.gps_distance_unit)+", "+Html.fromHtml(this.listSteps.get(this.indexCurrentPoint).getHtml_instructions()).toString(), TextToSpeech.QUEUE_ADD, null);
			}
			if(50<this.distUserNextPoint && this.distUserNextPoint<=200 && this.mustSpeak200){
				this.mustSpeak200=false;
				this.textToSpeech.speak(getResources().getString(R.string.gps_in)+" "+this.distUserNextPoint+" "+getResources().getString(R.string.gps_distance_unit)+", "+Html.fromHtml(this.listSteps.get(this.indexCurrentPoint).getHtml_instructions()).toString(), TextToSpeech.QUEUE_ADD, null);
			}
			if(this.distUserNextPoint<=50 && this.mustSpeak50){
				this.mustSpeak50=false;
				this.textToSpeech.speak(getResources().getString(R.string.gps_in)+" "+this.distUserNextPoint+" "+getResources().getString(R.string.gps_distance_unit)+", "+Html.fromHtml(this.listSteps.get(this.indexCurrentPoint).getHtml_instructions()).toString(), TextToSpeech.QUEUE_ADD, null);
			}
		}
		
		public void checkIfUserIsReachingTheStartingPoint(){
			if(this.distUserNextPoint<Constants.RADIUS_DETECTION && !this.allInfoAreDisplaying){
				actionWhenUserReachTheStartingPoint();
			}
			else if(this.distUserNextPoint>Constants.RADIUS_DETECTION){
				actionWhenUserDidNotReachTheStartingPoint();
			}
		}
		
		public void actionWhenUserReachTheStartingPoint(){
			this.allInfoAreDisplaying=true;
			this.textToSpeech.speak(Html.fromHtml(this.currentStep.getHtml_instructions()).toString()+". "+getResources().getString(R.string.gps_then)+", "+Html.fromHtml(this.nextStep.getHtml_instructions()).toString(), TextToSpeech.QUEUE_FLUSH, null);
			this.chronometer.start();
			setVisibleLayout();
			displayInstructions();
			this.userPosition.setFollowingNavigation(true);
			this.userPosition.setToNextPointToFollow();
		}
		
		public void actionWhenUserDidNotReachTheStartingPoint(){
			if(!this.isSpeakingBeforeRoute){
				this.isSpeakingBeforeRoute=true;
				String text=getResources().getString(R.string.gps_go_starting_point)+", "+mRoute.getStartAddress();
				this.textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
			}
			setVisibleInfoDirection();
			displayInstructions(getResources().getString(R.string.gps_youre_at)+" "+convertMeterToKm(this.distUserNextPoint)+" "+getResources().getString(R.string.gps_from_starting_point));
		}
		
		public void calculateRemainingTimeBeforeNextPoint(){
			int stepDuration;
			
			if(this.chronometer.isStart()){
				stepDuration=this.listSteps.get(this.indexCurrentPoint).getDuration().getValue();
				this.secondsChrono=(int)this.chronometer.getSeconds();
				if(this.secondsChrono>=stepDuration){
					this.imlate=true;
					this.remainingTimeBeforeNextPoint=this.secondsChrono-stepDuration;
				}
			}
			
		}
		
		/**
		 * Calcul the distance between 2 points
		 * @param sourcePoint
		 * @param targetPoint
		 * @return the distance between sourcePoint and targetPoint, in meters
		 */
		public int distanceBetween(LatLng sourcePoint, LatLng targetPoint) {
			double lat1 = sourcePoint.latitude;
			double lng1 = sourcePoint.longitude;
			double lat2 = targetPoint.latitude;
			double lng2 = targetPoint.longitude;

			double earthRadius = Constants.EARTH_RADIUS;
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
		}
		
		/**
		 * Display the informations about the end of the route
		 */
		public void displayInfoFinish(){
			TextView tv_gpsfinish = (TextView)findViewById(R.id.tv_gps_finish);

			int[] hoursMinutesToFinish=formatDuration(this.totalDuration);
			int[] hoursMinutesDay=getHoursMinutesToday();
			int[] hoursMinutesDelta=formatDuration(this.remainingTimeBeforeNextPoint);
			int hourFinish=hoursMinutesDay[0]+hoursMinutesToFinish[0];
			int minuteFinish=hoursMinutesDay[1]+hoursMinutesToFinish[1];
			
			if(imlate){
				hourFinish+=hoursMinutesDelta[0];
				minuteFinish+=hoursMinutesDelta[1];
			}else{
				hourFinish-=hoursMinutesDelta[0];
				minuteFinish-=hoursMinutesDelta[1];
			}
			/*
			 * TODO
			 * Faire en sorte d'avoir l'heure qui passe ?? 00h si
			 * par exemple il est 23h30 et trajet > 30 min
			 */
			tv_gpsfinish.setText(convertMeterToKm(this.totalRemainingDist)+" | "+hourFinish+"h"+minuteFinish);
		}
		
		/**
		 * Display the distance remaining before reaching the next point
		 */
		public void displayDist(){
			TextView tv_DistNextPoint = (TextView)findViewById(R.id.tv_gps_kmnextpoint);
			tv_DistNextPoint.setText(convertMeterToKm(this.distUserNextPoint));
		}
		
		public int[] formatDuration(int duration){
			int hours = duration / 3600;
			int minutes = (duration % 3600) / 60;
			int rtr[]={hours,minutes};
			return rtr;
		}
		
		public int[] getHoursMinutesToday(){
			DateTime dt=new DateTime();
			int rtr[]={dt.getHourOfDay(),dt.getMinuteOfHour()};
			return rtr;
		}
		
		public String convertMeterToKm(int d){
			if(d>1000){
				double distKm=d/(float)1000;
				DecimalFormat format=new DecimalFormat("#.#");
				return format.format(distKm)+"km";
			}else{
				return d+"m";
			}
		}
		
		/**
		 * Set visible the 3 panels
		 */
		public void setVisibleLayout(){
			setVisibleInfoFinish();
			setVisibleKmNextPoint();
			setVisibleInfoDirection();
		}
		
		public void setVisibleInfoFinish(){
			TextView tv_infoFinish=(TextView)findViewById(R.id.tv_gps_finish);
			if(tv_infoFinish.getVisibility()!=View.VISIBLE){
				tv_infoFinish.setVisibility(View.VISIBLE);
			}
		}
		
		public void setVisibleKmNextPoint(){
			TextView tv_kmNextPoint=(TextView)findViewById(R.id.tv_gps_kmnextpoint);
			if(tv_kmNextPoint.getVisibility()!=View.VISIBLE){
				tv_kmNextPoint.setVisibility(View.VISIBLE);
			}
		}
		
		public void setVisibleInfoDirection(){
			TextView tv_infoDirection=(TextView)findViewById(R.id.tv_gps_htmlInstructions);
			if(tv_infoDirection.getVisibility()!=View.VISIBLE){
				tv_infoDirection.setVisibility(View.VISIBLE);
			}
		}
		
		/**
		 * Display the instructions for reaching the next point
		 */
		public void displayInstructions(){
			TextView tv_Instructions = (TextView)findViewById(R.id.tv_gps_htmlInstructions);
			tv_Instructions.setText(Html.fromHtml(this.nextStep.getHtml_instructions().split("<div ")[0]));
		}
		
		/**
		 * Display the "infoToDisplay"
		 * @param infoToDisplay
		 */
		public void displayInstructions(String infoToDisplay){
			TextView tv_Instructions = (TextView)findViewById(R.id.tv_gps_htmlInstructions);
			tv_Instructions.setText(Html.fromHtml(infoToDisplay));
		}
		
		/**
		 * Format a distance, so it will end in 0 or 5
		 * @param distance
		 * @return the formated distance (int)
		 */
		public int formatDist(int distance){
			int lastDigit=distance%10;
			
			switch(lastDigit){
				case 1: case 2: case 3:
					distance=distance-lastDigit;
				break;
				
				case 4: case 6:
					distance=distance-lastDigit+5;
				break;
				
				case 7: case 8 : case 9:
					distance=distance-lastDigit+10;
				break;
			}
			
			return distance;
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			//
		}

		@Override
		public void onProviderEnabled(String provider) {
			//
		}

		@Override
		public void onProviderDisabled(String provider) {
			//
		}

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		//
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		//
	}
}
