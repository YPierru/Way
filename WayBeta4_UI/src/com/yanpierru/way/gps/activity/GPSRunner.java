package com.yanpierru.way.gps.activity;

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
import com.yanpierru.way.Chrono;
import com.yanpierru.way.Constantes;
import com.yanpierru.way.R;
import com.yanpierru.way.gps.UserPosition;
import com.yanpierru.way.route.Route;
import com.yanpierru.way.route.downloaded.Step;


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

public class GPSRunner extends Activity implements SensorEventListener,TextToSpeech.OnInitListener {
	
	private LocationManager mLocManag;
	private TextToSpeech textToSpeech;
	private MyLocationListener mLocList;
	static GPSRunner thisactivity;
	private ArrayList<LatLng> listPointsToFollow;
	private Route mRoute;
	private ArrayList<Step> mListSteps;
	private GoogleMap mMap;
	private UserPosition mUserPos;
	private int totalDistance=0;
	private int totalDuration=0;
	private boolean allInfoAreDisplaying=false;
	private boolean firstLocationFind=false;
	private boolean mustSpeak1000=true,mustSpeak500=true,mustSpeak200=true,mustSpeak50=true;
	private Chrono mChrono;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_gps);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		this.mUserPos = new UserPosition();

		mChrono=new Chrono();
		thisactivity = this;
		getActionBar().hide();

		mMap = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.mapGPS)).getMap();
		//mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		mMap.getUiSettings().setZoomControlsEnabled(false);
		mRoute = getIntent().getExtras().getParcelable("TRAJET");
		
		this.textToSpeech = new TextToSpeech(this, this);
		
		//On recup??re la liste des steps
		this.mListSteps=mRoute.getListSteps();
		
		for(int i=0;i<mListSteps.size();i++){
			totalDistance+=mListSteps.get(i).getDistance().getValue();
			//Log.d("DEBUUUUG", "step #"+(i+1)+" : "+mListSteps.get(i).getDistance().getValue()+" m??tres");
			totalDuration+=mListSteps.get(i).getDuration().getValue();
		}
		
		/*
		 * On r??cup??re la liste des points ?? suivre.
		 * A chaque nouveau point correspond une nouvelle instruction
		 */
		this.listPointsToFollow=new ArrayList<LatLng>();
		for (int i = 0; i < mListSteps.size(); i++) {
			listPointsToFollow.add(new LatLng(mListSteps.get(i).getStart_location()
					.getLat(), mListSteps.get(i).getStart_location().getLng()));
			if(i+1==mListSteps.size()){
				listPointsToFollow.add(new LatLng(mListSteps.get(i).getEnd_location()
						.getLat(), mListSteps.get(i).getEnd_location().getLng()));
			}
		}
		/*String str;
		for(int i=0;i<this.mListSteps.size();i++){
			str= this.mListSteps.get(i).getHtml_instructions().split("<div")[0];
			Log.d("debug.showInstr", str);
		}*/
		/*for(int i=0;i<this.listPointsToFollow.size();i++){
			Log.d("PTFL", this.listPointsToFollow.get(i).toString());
		}*/
		
		//On dessine le trajet
		drawRoute();
	}
	
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			int result = textToSpeech.setLanguage(Locale.FRANCE);
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("error", "This Language is not supported");
			}
		} else {
			Log.e("error", "Initilization Failed!");
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		textToSpeech.shutdown();
	}
	
	public static GPSRunner getInstance(){
		return thisactivity;
	}
	
	private void drawRoute(){
		//Departure
		ArrayList<LatLng> listPointsOverview = mRoute.getPointsWhoDrawsPolylineLatLng();
		ArrayList<LatLng> listStepMarkers = mRoute.getListMarkersLatLng();
		setMarker(listPointsOverview.get(0), "Départ", false);
		CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(listPointsOverview.get(0),
				16);
		mMap.animateCamera(cu, 600, null);
		if(listStepMarkers.size()>2){
			for(int i=1;i<listStepMarkers.size()-1;i++){
				setMarker(listStepMarkers.get(i), "Jalon "+i, true);
			}
		}
		//Tra??age du trajet
		PolylineOptions options = new PolylineOptions().geodesic(false)
				.width(Constantes.WIDTH_POLYLINE_GPS).color(Constantes.COLOR_POLYLINE_GPS);
		
		for (int i = 0; i < listPointsOverview.size(); i++) {
			options.add(listPointsOverview.get(i)); 
		}
		mMap.addPolyline(options);
		
		
		//On met le marker ?? l'arriv??e
		setMarker(listPointsOverview.get(listPointsOverview.size() - 1), "Arrivée", false);
	}
	
	public void setMarker(LatLng point, String str, boolean isInter) {
		int markerIcn;
		if(isInter){
			markerIcn=R.drawable.ic_marker_inter;
		}else{
			markerIcn=R.drawable.ic_marker_princ;
		}
		mMap.addMarker(
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
		mLocList = new MyLocationListener();
		mLocManag = (LocationManager) this.getSystemService(LOCATION_SERVICE);

		if(Constantes.NETWORK_GPS){
			mLocManag.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5, 0,mLocList);
		}else{
			if (!mLocManag.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				buildAlertMessageNoGps();
			}else{
				mLocManag.requestLocationUpdates(LocationManager.GPS_PROVIDER, Constantes.MIN_TIME_GPS_REQUEST_MS, Constantes.MIN_DIST_GPS_REQUEST_M,
					mLocList);
			}
		}
			
	}

	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Veuillez activer votre GPS pour continuer")
				.setCancelable(false)
				.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog,final int id) {
								startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							}
						})
				.setNegativeButton("Quitter", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						dialog.cancel();
						GPSRunner.getInstance().finish();
					}
				});
		
		final AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mLocManag.removeUpdates(mLocList);
	}

	private class MyLocationListener implements LocationListener {
		
		public int distStep;
		public int restDist;
		public int distUserNextPoint;
		public int secondsChrono;
		public int deltaChrono;
		public int stepDuration;
		public boolean imlate=false, isSpeakingBeforeRoute=false;

		@Override
		//Lorsque la position de mon utilisateur change...
		public void onLocationChanged(Location location) {
			
			int indexCurrentPoint = mUserPos.getIndexPointToFollow();
			mUserPos.setCurrentPos(location.getLatitude() , location.getLongitude(), location.getBearing(),mMap);
			distUserNextPoint=formatDist(distanceBetween(mUserPos.getCurrentPos(),listPointsToFollow.get(indexCurrentPoint)));
			Step currentStep=mListSteps.get(indexCurrentPoint);
			Step nextStep=null;
			if(indexCurrentPoint<listPointsToFollow.size()-1){
				nextStep=mListSteps.get(indexCurrentPoint+1);
			}

			if(mChrono.isStart()){
				stepDuration=mListSteps.get(indexCurrentPoint).getDuration().getValue();
				secondsChrono=(int)mChrono.getSeconds();
				if(secondsChrono>=stepDuration){
					imlate=true;
					deltaChrono=secondsChrono-stepDuration;
				}
				
			}
			if(!mUserPos.isOnRoute()){
				
				if(distUserNextPoint<Constantes.RADIUS_DETECTION && !allInfoAreDisplaying){
					allInfoAreDisplaying=true;
					//Toast.makeText(GPSRunner.this, "D??part",Toast.LENGTH_SHORT).show();
					
					String text=Html.fromHtml(currentStep.getHtml_instructions()).toString()+". puis, "+Html.fromHtml(nextStep.getHtml_instructions()).toString();
					textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
					mChrono.start();
					showLayout();
					displayInstructions(nextStep);
					mUserPos.setIsOnRoute(true);
					mUserPos.setToNextPointToFollow();
				}
				else if(distUserNextPoint>Constantes.RADIUS_DETECTION){
					
					if(!isSpeakingBeforeRoute){
						isSpeakingBeforeRoute=true;
						String text="Rejoingnez votre point de départ, "+mRoute.getStartAddress();
						textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
					}
					showInfoDirection();
					displayInstructions("Vous êtes à "+convertMeterToKm(distUserNextPoint)+" de votre point de départ");
				}
				
			}

			else{
				displayDist(distUserNextPoint);
				/*
				 * calcul de la distance restante jusqu'?? l'arriv??e
				 */
				distStep=mListSteps.get(indexCurrentPoint).getDistance().getValue();
				restDist=totalDistance-(distStep-distUserNextPoint);
				displayInfoFinish(restDist);
				
				//Log.d("DEBUUUUUUG", "totalD="+totalD+" resteD="+restDistance);
				
				if(500<distUserNextPoint && distUserNextPoint<=1000 && mustSpeak1000){
					mustSpeak1000=false;
					textToSpeech.speak("Dans "+distUserNextPoint+" mètres ,"+Html.fromHtml(mListSteps.get(indexCurrentPoint).getHtml_instructions()).toString(), TextToSpeech.QUEUE_ADD, null);
				}
				if(200<distUserNextPoint && distUserNextPoint<=500 && mustSpeak500){
					mustSpeak500=false;
					textToSpeech.speak("Dans "+distUserNextPoint+" mètres ,"+Html.fromHtml(mListSteps.get(indexCurrentPoint).getHtml_instructions()).toString(), TextToSpeech.QUEUE_ADD, null);
				}
				if(50<distUserNextPoint && distUserNextPoint<=200 && mustSpeak200){
					mustSpeak200=false;
					textToSpeech.speak("Dans "+distUserNextPoint+" mètres ,"+Html.fromHtml(mListSteps.get(indexCurrentPoint).getHtml_instructions()).toString(), TextToSpeech.QUEUE_ADD, null);
				}
				if(distUserNextPoint<=50 && mustSpeak50){
					
					mustSpeak50=false;
					textToSpeech.speak("Dans "+distUserNextPoint+" mètres ,"+Html.fromHtml(mListSteps.get(indexCurrentPoint).getHtml_instructions()).toString(), TextToSpeech.QUEUE_ADD, null);
				}
				if(distUserNextPoint<Constantes.RADIUS_DETECTION){
					mustSpeak1000=true;
					mustSpeak200=true;
					mustSpeak50=true;
					mustSpeak500=true;
					mChrono.stop();
					secondsChrono=(int)mChrono.getSeconds();
					if(secondsChrono>=stepDuration){
						imlate=true;
						deltaChrono=secondsChrono-stepDuration;
					}else{
						imlate=false;
						deltaChrono=stepDuration-secondsChrono;
					}
					mChrono.start();
					if(indexCurrentPoint<listPointsToFollow.size()-1){
						mUserPos.setToNextPointToFollow();
						displayInstructions(nextStep);
						totalDistance=totalDistance-distStep;
						distUserNextPoint=formatDist(distanceBetween(mUserPos.getCurrentPos(), listPointsToFollow.get(indexCurrentPoint+1)));
						textToSpeech.speak("Dans "+distUserNextPoint+" mètres "+Html.fromHtml(mListSteps.get(indexCurrentPoint+1).getHtml_instructions()).toString(), TextToSpeech.QUEUE_FLUSH, null);
						if(distUserNextPoint>500 && distUserNextPoint <=1000){
							mustSpeak1000=false;
						}else if(distUserNextPoint>200 && distUserNextPoint <=500){
							mustSpeak500=false;
						}else if(distUserNextPoint>50 && distUserNextPoint <=200){
							mustSpeak200=false;
						}else if(distUserNextPoint<=50){
							mustSpeak50=false;
						}
					}else{
						mUserPos.setIsOnRoute(false);
						textToSpeech.speak("Vous êtes arrivé", TextToSpeech.QUEUE_FLUSH, null);
					}
				}
			}
		}
		
		public int distanceBetween(LatLng p1, LatLng p2) {
			double lat1 = p1.latitude;
			double lng1 = p1.longitude;
			double lat2 = p2.latitude;
			double lng2 = p2.longitude;

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
		}
		
		public void displayInfoFinish(int restDist){
			TextView tv_gpsfinish = (TextView)findViewById(R.id.tv_gps_finish);
			//String distToFinish=convertMeterToKm(restDist);
			int[] hoursMinutesToFinish=formatDuration(totalDuration);
			int[] hoursMinutesDay=getHoursMinutesDay();
			int[] hoursMinutesDelta=formatDuration(deltaChrono);
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
			tv_gpsfinish.setText(convertMeterToKm(restDist)+" | "+hourFinish+"h"+minuteFinish);
		}
		
		public void displayDist(int dist){
			TextView tv_DistNextPoint = (TextView)findViewById(R.id.tv_gps_kmnextpoint);
			tv_DistNextPoint.setText(convertMeterToKm(dist));
		}
		
		public int[] formatDuration(int duration){
			int hours = duration / 3600;
			int minutes = (duration % 3600) / 60;
			int rtr[]={hours,minutes};
			return rtr;
		}
		
		public int[] getHoursMinutesDay(){
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
		
		public void showLayout(){
			showInfoFinish();
			showKmNextPoint();
			showInfoDirection();
		}
		
		public void showInfoFinish(){
			TextView tv_infoFinish=(TextView)findViewById(R.id.tv_gps_finish);
			if(tv_infoFinish.getVisibility()!=View.VISIBLE){
				tv_infoFinish.setVisibility(View.VISIBLE);
			}
		}
		
		public void showKmNextPoint(){
			TextView tv_kmNextPoint=(TextView)findViewById(R.id.tv_gps_kmnextpoint);
			if(tv_kmNextPoint.getVisibility()!=View.VISIBLE){
				tv_kmNextPoint.setVisibility(View.VISIBLE);
			}
		}
		
		public void showInfoDirection(){
			TextView tv_infoDirection=(TextView)findViewById(R.id.tv_gps_htmlInstructions);
			if(tv_infoDirection.getVisibility()!=View.VISIBLE){
				tv_infoDirection.setVisibility(View.VISIBLE);
			}
		}
		
		public void displayInstructions(Step cStep){
			TextView tv_Instructions = (TextView)findViewById(R.id.tv_gps_htmlInstructions);
			tv_Instructions.setText(Html.fromHtml(cStep.getHtml_instructions().split("<div ")[0]));
		}
		
		public void displayInstructions(String str){
			TextView tv_Instructions = (TextView)findViewById(R.id.tv_gps_htmlInstructions);
			tv_Instructions.setText(Html.fromHtml(str));
		}
		
		public int formatDist(int d){
			int lastDigit=d%10;
			
			switch(lastDigit){
				case 1: case 2: case 3:
					d=d-lastDigit;
				break;
				
				case 4: case 6:
					d=d-lastDigit+5;
				break;
				
				case 7: case 8 : case 9:
					d=d-lastDigit+10;
				break;
			}
			
			return d;
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			/*String newStatus = "";
			switch (status) {
			case LocationProvider.OUT_OF_SERVICE:
				newStatus = "OUT_OF_SERVICE";
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				newStatus = "TEMPORARILY_UNAVAILABLE";
				break;
			case LocationProvider.AVAILABLE:
				newStatus = "AVAILABLE";
				break;
			}
			Toast.makeText(GPSRunner.this, provider + " " + newStatus,
					Toast.LENGTH_SHORT).show();*/

		}

		@Override
		public void onProviderEnabled(String provider) {
			/*Toast.makeText(GPSRunner.this, "enable : " + provider,
					Toast.LENGTH_SHORT).show();*/
		}

		@Override
		public void onProviderDisabled(String provider) {
			/*Toast.makeText(GPSRunner.this, "disable : " + provider,
					Toast.LENGTH_SHORT).show();*/
		}

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
}
