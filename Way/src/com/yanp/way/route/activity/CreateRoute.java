package com.yanp.way.route.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.widget.RadialMenuWidget;
import com.example.widget.RadialMenuWidget.RadialMenuEntry;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.yanp.way.Constants;
import com.yanp.way.PlaceJSONParser;
import com.yanp.way.R;
import com.yanp.way.asyncTasks.CallsDirRespAPIAndDrawRoute;
import com.yanp.way.gps.activity.GPSNavigation;
import com.yanp.way.route.Route;
import com.yanp.way.route.RoutesCollection;


/**
 * Create the route
 * @author YPierru
 *
 */
public class CreateRoute extends Activity {

	private GoogleMap googleMap;
	private Polyline polylineRoute;
	private ArrayList<Marker> listMarkers= new ArrayList<Marker>();			
	private Route currentRoute;
	private RadialMenuWidget mWheelMenu;
	private LinearLayout linearLayourWheel;
	private AutoCompleteTextView atvPlaces;
	private MenuItem itemWheelMenu, itemSearch, itemHelp;
	private boolean wheelEnable=false,canBeDraw=false,correctionEnable=false, onSearch=false, firstLocationFind=false;
	private TextView countIntermediaryPoints;
	private int amountOfInterPointsRemaining=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_map_create);

		this.linearLayourWheel=(LinearLayout)findViewById(R.id.linearLayoutForWheel);

		this.googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		this.googleMap.setMyLocationEnabled(true);
		this.googleMap.setOnMyLocationChangeListener(myLocationChangeListener);
		this.googleMap.setOnMarkerDragListener(null);

		this.currentRoute = getIntent().getExtras().getParcelable("route");
		
		this.countIntermediaryPoints = (TextView)findViewById(R.id.tv_createroute_waypoints);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle(this.currentRoute.getName());
		
		String typeOfRoute = getIntent().getExtras().getString("type_of_route");
		if (typeOfRoute.equals("not_finish")) {
			actionIfRouteNotFinish();
		}

		settingMapOneLongClickOneNewStartingPoint();
		
	}
	
	/**
	 * Place the camera where the user is
	 */
	private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
	    @Override
	    public void onMyLocationChange(Location location) {
	    	if(!firstLocationFind){
		        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
		        if(googleMap != null){
		            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
		        }
		        firstLocationFind=true;
	    	}
	    }
	};

	/**
	 * Specific action if the route received is not finish
	 */
	private void actionIfRouteNotFinish(){
			
		setMapOneClickOneMarker();
		//Log.d("DEBUG", "SIZE="+this.currentRoute.getListMarkers().size());
		ArrayList<LatLng> tmpListMarkers = this.currentRoute.getListMarkersLatLng();
		this.amountOfInterPointsRemaining=tmpListMarkers.size()-1;
		
		if(this.amountOfInterPointsRemaining>0){
			displayIndicatorIntermediaryPoints();
		}
		
		//If there's not marker, the route can't be draw
		if(tmpListMarkers.size()==1){
			canBeDraw=false;
		}else{
			canBeDraw=true;
		}
		
		//Place the marker on the map and add them in the listMarkers
		for (int i = 0; i < tmpListMarkers.size(); i++) {
			if (i == 0) {
				this.listMarkers.add(putMarker(tmpListMarkers.get(i), getResources().getString(R.string.start),true, false));
			} else {
				this.listMarkers.add(putMarker(tmpListMarkers.get(i), "NO", true, true));
			}
		}
		
		CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(tmpListMarkers.get(0), Constants.ZOOM_GENERAL);
		this.googleMap.animateCamera(cu, Constants.ZOOM_SPEED_MS, null);
			
	}


	/**
	 * Behavior of a long click : a long click add a new starting point
	 */
	private void settingMapOneLongClickOneNewStartingPoint() {
		this.googleMap.setOnMapLongClickListener(new OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng point) {
				final LatLng pts=point;
				
				if(listMarkers.size()>0){
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(CreateRoute.this);
					alertDialog.setTitle(getResources().getString(R.string.cr_alert_route_erased_title));
					alertDialog.setMessage(getResources().getString(R.string.cr_alert_route_erased_message))
							   .setCancelable(false)
							   .setPositiveButton(getResources().getString(R.string.yes),
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
												int id) {
											dialog.cancel();
											eraseRouteAndAddNewStartingPoint(pts);
										}
									})
							   .setNegativeButton(getResources().getString(R.string.no),
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
												int id) {
											dialog.cancel();
										}
									})
							   .create();
					alertDialog.show();
				}else{
					eraseRouteAndAddNewStartingPoint(pts);
				}
				
				
			}
		});
	}
	
	/**
	 * Erase all in the map a place a new starting point
	 * @param point
	 */
	private void eraseRouteAndAddNewStartingPoint(LatLng point){

		this.googleMap.clear();
		this.listMarkers.clear();
		this.amountOfInterPointsRemaining=0; 
		this.countIntermediaryPoints.setVisibility(View.INVISIBLE);

		CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(point,Constants.ZOOM_NEW_ROUTE);
		this.googleMap.animateCamera(cu, Constants.ZOOM_SPEED_MS, null);
	
		this.listMarkers.add(putMarker(point, getResources().getString(R.string.start), true, false));
		this.currentRoute.getListMarkers().clear();
		this.currentRoute.getListMarkers().add(point.latitude, point.longitude);

		setMapOneClickOneMarker();
	}

	/**
	 * Behavior of the simpleClick, according to the number of possible intermediary points remaining
	 */
	private void setMapOneClickOneMarker() {
		this.googleMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {
				//There is only 8 intermediary points possible
				if(amountOfInterPointsRemaining<8){
					amountOfInterPointsRemaining++;
					displayIndicatorIntermediaryPoints();
					listMarkers.add(putMarker(point, "NO", true, true));
					canBeDraw=true;
					currentRoute.getListMarkers().add(point.latitude, point.longitude);
				}else{
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.cr_limit_markers_reach), Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
	/**
	 * Display the remaining amount of intermediary points
	 */
	private void displayIndicatorIntermediaryPoints(){
		if(countIntermediaryPoints.getVisibility()==View.INVISIBLE){
			countIntermediaryPoints.setVisibility(View.VISIBLE);
		}
		switch(this.amountOfInterPointsRemaining){
			case 1: case 2: case 3: case 4: case 5: 
				countIntermediaryPoints.setTextColor(Color.rgb(16, 173, 13));
			break;
			
			case 6: case 7:
				countIntermediaryPoints.setTextColor(Color.rgb(255, 133, 51));
			break;
			
			case 8:
				countIntermediaryPoints.setTextColor(Color.rgb(232, 65, 32));
			break;
		}
		countIntermediaryPoints.setText(this.amountOfInterPointsRemaining+"/8");
	}
	
	/**
	 * Set an empty click listener
	 */
	private void settingMapClickListenerCorrectionMode(){
		this.googleMap.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng arg0) {}
		});
	}
	

	/**
	 * Add a new marker on the map
	 * @param position
	 * @param infoMessage
	 * @param draggable - if true, the marker could be moved by the user, if false he can't.
	 * @param intermediaryPoint - the icon is bigger if it's not an intermediary point
	 * @return the new marker
	 */
	private Marker putMarker(LatLng position, String infoMessage, boolean draggable, boolean intermediaryPoint) {
		int idIcMarker;
		if(intermediaryPoint){
			idIcMarker=R.drawable.ic_marker_inter;
		}else{
			idIcMarker=R.drawable.ic_marker_princ;
		}
		
		Marker marker = this.googleMap.addMarker(new MarkerOptions()
				.icon(BitmapDescriptorFactory.fromResource(idIcMarker))
				.anchor(0.0f, 1.0f)
				.position(position));
		
		if(!infoMessage.equals("NO")){
			marker.setTitle(infoMessage);
		}
		
		marker.setDraggable(draggable);
		marker.showInfoWindow();
		return marker;
	}

	
	@Override
	public void onBackPressed() {
		actionIfUserWantsBack();
	}
	
	public void setPolyline(Polyline p){
		this.polylineRoute=p;
	}

	/**
	 * Behavior when touch the Home button
	 */
	public boolean onOptionsItemSelected(MenuItem menuItem) {

		if (menuItem.getItemId() == android.R.id.home) {
			if (onSearch) {
				closeSearchBar();
			} else {
				actionIfUserWantsBack();
			}
		}
		return true;
	}

	/**
	 * Action if the user wants to go back
	 */
	public void actionIfUserWantsBack() {

		if (this.listMarkers.size() > 0) {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			alertDialog.setTitle(getResources().getString(R.string.cr_alert_back_pressed_title));
			alertDialog
					.setMessage(getResources().getString(R.string.cr_alert_back_pressed_message))
					.setCancelable(false)
					.setPositiveButton(getResources().getString(R.string.ok),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int id) {
									dialog.cancel();
									finish();
								}
							})
					.setNeutralButton(getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							actionSave();
						}
					})
					.setNegativeButton(getResources().getString(R.string.cancel),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int id) {
									dialog.cancel();
								}
							}).create();
			alertDialog.show();
		} else {
			super.onBackPressed();
		}
	}

	/**
	 * Add the items on the action bar
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		
		itemSearch = menu.add(getResources().getString(R.string.cr_item_search)).setIcon(R.drawable.ic_action_search);
		itemSearch.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		itemSearch.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if(isNetworkAvailable()){
					openSearchBar();
				}else{
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(
							CreateRoute.this);
					alertDialog.setTitle(getResources().getString(R.string.cr_alert_no_internet_title));
					alertDialog
							.setMessage(Html.fromHtml(getResources().getString(R.string.cr_alert_no_internet_message)))
							.setCancelable(true)
							.setPositiveButton(getResources().getString(R.string.ok),
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
												int id) {
											dialog.cancel();
										}
									}).create();
					alertDialog.show();
				}
				return true;

			}
		});

		itemHelp = menu.add(getResources().getString(R.string.cr_item_help)).setIcon(R.drawable.ic_action_help);
		itemHelp.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		itemHelp.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						CreateRoute.this);
				String message;
				if(Constants.CURRENT_LANGUAGE.getLanguage().equals("fr")){
					message="<b>Appui long</b> : efface tout sur la carte et place un <u>point de départ</u><br/><b>Appui simple</b> : place un point par lequel <u>vous voulez passer</u><br />La <b>loupe</b> : lance une barre de recherche pour trouvez une adresse, un lieu<br />La <b>roue</b> : lance le menu. Vous pourrez : <br /><u><em>Dessiner</em></u> votre trajet (si vous avez au moins 2 points)<br /><u><em>Sauvegarder</em></u> : si vous avez un trajet, le sauvegarde, vous pourrez ensuite soit lancer le <u>GPS</u> soit <u>quitter</u><br /><u><em>Changer type carte</em></u> : changer le type de la carte (normal, hybride, terrain, satellite)<br /><u><em>Corriger</em></u> : active un mode particulier. Si vous avez un trajet dessiné, l'efface. Vous pouvez supprimer un point en cliquant dessus. Pour quitter ce mode, cliquer sur dessiner, terminer ou correction";
				}else{
					message="<b>Long press</b> : erase everything on the map and put a <u>starting point</u><br/><b>Short press</b> : put an <u>intermediary point</u><br />The <b>magnifying glass</b> : display a search bar for finding a city, a street<br />The <b>wheel</b> : display the menu. You can : <br /><u><em>Draw</em></u> your route (if you have at least 2 points)<br /><u><em>Save</em></u> : if you have a route, save it, then you could launch the <u>GPS navigation</u> or <u>exit</u><br /><u><em>Change map</em></u> : change map type (normal, hybrid, ground, satellite)<br /><u><em>Correction Mode</em></u> : enable the correction feature. If you have a drawing route, erase it. You can delete an intermediary point by clicking on it. If you want to leave this mode, click on draw, save or correction again.";
				}
				alertDialog.setTitle(getResources().getString(R.string.cr_alert_help_title));
				alertDialog
						.setMessage(
								Html.fromHtml(message))
						.setCancelable(false)
						.setPositiveButton(getResources().getString(R.string.ok),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								}).create();
				alertDialog.show();
				return false;
			}
		});

		
		itemWheelMenu = menu.add(getResources().getString(R.string.cr_item_wheel)).setIcon(R.drawable.ic_action_wheel);
		itemWheelMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		itemWheelMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {

						if (!wheelEnable) {
							
							//Creation of the wheel menu
							wheelEnable = true;
							mWheelMenu = new RadialMenuWidget(getBaseContext());
							
							int xLayoutSize = linearLayourWheel.getHeight();
							int yLayoutSize = linearLayourWheel.getWidth();
							mWheelMenu.setSourceLocation(xLayoutSize,yLayoutSize);
							
							mWheelMenu.setIconSize(15, 30);
							mWheelMenu.setTextSize(Constants.TEXT_SIZE_WHEEL);

							//According to the state of the differents boolean, the wheel will not be the same
							mWheelMenu.setCenterCircle(new WheelMenu(getResources().getString(R.string.cr_wheel_menu_close), true, android.R.drawable.ic_menu_close_clear_cancel));
							
							mWheelMenu.addMenuEntry(new SaveMenu());
							
							if(canBeDraw){
								mWheelMenu.addMenuEntry(new WheelMenu(getResources().getString(R.string.cr_wheel_menu_draw), true, 0));
							}
							if(listMarkers.size()>=1 && correctionEnable){
								mWheelMenu.addMenuEntry(new WheelMenu(getResources().getString(R.string.cr_wheel_menu_exit_correction), true, 0));
							}
							if(listMarkers.size()>1 && !correctionEnable){
								mWheelMenu.addMenuEntry(new WheelMenu(getResources().getString(R.string.cr_wheel_menu_correction), true, 0));
							}
							
							mWheelMenu.addMenuEntry(new MapTypeMenu());

							linearLayourWheel.addView(mWheelMenu);
						} 
						//Suppression
						else {
							((LinearLayout) mWheelMenu.getParent()).removeView(mWheelMenu);
							wheelEnable = false;
						}

						return false;
					}
				});

		return true;
	}
	
	
	/**
	 * Check if user has an internet connection
	 * @return
	 */
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	/**
	 * Opening the search bar
	 */
	public void openSearchBar() {

		onSearch = true;
		itemHelp.setVisible(false);
		itemWheelMenu.setVisible(false);
		itemSearch.setVisible(false);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setIcon(R.drawable.ic_action_search);

		LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.actionbar_search, null);

		actionBar.setCustomView(v);

		atvPlaces = (AutoCompleteTextView) findViewById(R.id.actv_search_places);
		
		showKeyboard();

		//When a character is written
		atvPlaces.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				//When the text change, the results are reloaded
				PlacesTask placesTask = new PlacesTask();
				placesTask.execute(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
	}

	public void closeSearchBar() {

		closeKeyboard();
		onSearch = false;
		itemSearch.setVisible(true);
		itemHelp.setVisible(true);
		itemWheelMenu.setVisible(true);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowCustomEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setIcon(R.drawable.ic_way_icon);
	}
	
	public void showKeyboard(){
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(atvPlaces, InputMethodManager.SHOW_IMPLICIT);
	}
	
	public void closeKeyboard(){
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(atvPlaces.getWindowToken(), 0);
	}
	
	/**
	 * Reach JSON data using an URL
	 * @param stringUrl - The URL
	 * @return the JSON data
	 * @throws IOException
	 */
	private String downloadUrl(String stringUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {


			URL url = new URL(stringUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuilder sb = new StringBuilder();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}
		
	/**
	 * Draw the route on the map, calling an asynctask
	 */
	public void actionDraw() {
		
		
		
		if (this.polylineRoute != null) {
			this.polylineRoute.remove();
		}

		if (correctionEnable) {
			correctionEnable = false;
			changeActionBarOnCorrectionMode();
			setMapOneClickOneMarker();
		}

		CallsDirRespAPIAndDrawRoute getRoute = new CallsDirRespAPIAndDrawRoute(this,this.currentRoute,this.listMarkers, this.googleMap);
		getRoute.execute();
	}

	/**
	 * Save the route on RouteCollection
	 */
	public void actionSave() {
		
		if (this.listMarkers.size() > 0) {
			RoutesCollection mRC = RoutesCollection.getInstance();
			this.currentRoute.setSave(true);
			
			if (!mRC.replace(this.currentRoute)) {
				mRC.add(this.currentRoute);
				this.currentRoute.setIndexCollection(mRC.size()-1);
			}
			
			mRC.saveRoutesCollection();
		}
	}
	
	/**
	 * Enable the correction mode, change the behavior of the listeners
	 */
	public void actionCorrection() {
		if (!correctionEnable) {
			
			correctionEnable = true;

			//Change the color of the action bar
			changeActionBarOnCorrectionMode();
			
			this.currentRoute.setValidate(false);
			
			//Remove the actual polyline
			if (this.polylineRoute != null) {
				this.polylineRoute.remove();
			}
			
			settingMapClickListenerCorrectionMode();
			
			//In correction mode, when the user click on a marker it erase it.
			this.googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {

				@Override
				public boolean onMarkerClick(Marker marker) {
					amountOfInterPointsRemaining--;
					if(amountOfInterPointsRemaining==0){
						countIntermediaryPoints.setVisibility(View.INVISIBLE);
					}else{
						displayIndicatorIntermediaryPoints();
					}
					
					marker.hideInfoWindow();
					
					for (int i = 0; i < listMarkers.size(); i++) {
						if (listMarkers.get(i).getId().equals(marker.getId())) {
							if (i == 0) {
								Toast.makeText(
										getApplicationContext(),
										getResources().getString(R.string.cr_delete_starting_point),
										Toast.LENGTH_SHORT).show();
							} else {
								listMarkers.remove(i);
								currentRoute.setListMarkersMk(listMarkers);
								marker.remove();
								/*if(i==1){
									
								}*/
							}
							break;
						}
					}

					if (listMarkers.size() == 1) {
						canBeDraw = false;
					}
					
					return false;
				}
			});
			Toast.makeText(getApplicationContext(),getResources().getString(R.string.cr_correction_mode_enable), Toast.LENGTH_SHORT).show();
		} 
		
		else {
			correctionEnable = false;
			changeActionBarOnCorrectionMode();
			setMapOneClickOneMarker();
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.cr_correction_mode_disable), Toast.LENGTH_SHORT).show();
		}
	}
	
	private void changeActionBarOnCorrectionMode(){
		if(correctionEnable){
			getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff8000")));
			getActionBar().setTitle(this.currentRoute.getName()+" [Correction]");
		}else{
			getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1F1F1F")));
			getActionBar().setTitle(this.currentRoute.getName());
		}
	}
	
	

	/*
	 * Ci-dessous les menu de la roue
	 */
	
	public class WheelMenu implements RadialMenuEntry {
	
		private String name;
		private boolean closeWheelWhenTouch;
		private int idIcon;
	
		public WheelMenu(String name, boolean c, int i) {
			this.name = name;
			this.closeWheelWhenTouch = c;
			this.idIcon = i;
		}
	
		public String getName() {
			return getLabel();
		}
	
		public String getLabel() {
			if (this.name.equals("Close")) {
				return null;
			} else {
				return this.name;
			}
		}
	
		public int getIcon() {
			return this.idIcon;
		}
	
		public List<RadialMenuEntry> getChildren() {
			return null;
		}
	
		/**
		 * Set the differents actions according to the typed item
		 */
		public void menuActiviated() {
	
			if (this.name.equals(getResources().getString(R.string.cr_map_type_normal))) {
				googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			} else if (this.name.equals(getResources().getString(R.string.cr_map_type_hybrid))) {
				googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			} else if (this.name.equals(getResources().getString(R.string.cr_map_type_satellite))) {
				googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			} else if (this.name.equals(getResources().getString(R.string.cr_map_type_ground))) {
				googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			} else if (this.name.equals(getResources().getString(R.string.cr_wheel_menu_draw))) {
				actionDraw();
			} else if (this.name.equals(getResources().getString(R.string.cr_wheel_menu_correction)) || this.name.equals(getResources().getString(R.string.cr_wheel_menu_exit_correction))) {
				actionCorrection();
			} else if (this.name.equals(getResources().getString(R.string.exit))) {
				finish();
			} else if (this.name.equals(getResources().getString(R.string.gps))){
				if(currentRoute.isValidate()){
					Intent toGPSRunner = new Intent(getApplicationContext(),GPSNavigation.class);
					toGPSRunner.putExtra("Route_for_navigation_gps", (Parcelable) currentRoute);
					toGPSRunner.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					getApplicationContext().startActivity(toGPSRunner);
				}else{
					Toast.makeText(getApplicationContext(),getResources().getString(R.string.cr_draw_route_before_gps), Toast.LENGTH_SHORT).show();
				}
			}
	
			if (this.closeWheelWhenTouch) {
				((LinearLayout) mWheelMenu.getParent()).removeView(mWheelMenu);
				wheelEnable = false;
			}
		}
	
		
	}
	public class MapTypeMenu implements RadialMenuEntry {
		public String getName() {
			return "NewTestMenu";
		}

		public String getLabel() {
			return getResources().getString(R.string.cr_change_map_type);
		}

		public int getIcon() {
			return 0;
		}

		private List<RadialMenuEntry> children = new ArrayList<RadialMenuEntry>(
				Arrays.asList(new WheelMenu(getResources().getString(R.string.cr_map_type_normal), true, 0), 
							  new WheelMenu(getResources().getString(R.string.cr_map_type_hybrid), true, 0),
							  new WheelMenu(getResources().getString(R.string.cr_map_type_satellite), true, 0),
							  new WheelMenu(getResources().getString(R.string.cr_map_type_ground), true, 0)));

		public List<RadialMenuEntry> getChildren() {
			return children;
		}

		public void menuActiviated() {
		}
	}
		
	public class SaveMenu implements RadialMenuEntry {
	
		public String getName() {
			return "SaveMenu";
		}

		public String getLabel() {
			return getResources().getString(R.string.cr_wheel_menu_save);
		}

		public int getIcon() {
			return 0;
		}

		private List<RadialMenuEntry> children = new ArrayList<RadialMenuEntry>(
				Arrays.asList(new WheelMenu(getResources().getString(R.string.exit), true, 0), new WheelMenu(
						getResources().getString(R.string.gps), true, 0)));

		public List<RadialMenuEntry> getChildren() {
			return children;
		}

		public void menuActiviated() {
			actionSave();
			Toast.makeText(getApplicationContext(),currentRoute.getName()+" "+getResources().getString(R.string.cr_is_saved), Toast.LENGTH_SHORT).show();
		}
	}

	// Fetches all places from GooglePlaces AutoComplete Web Service
	private class PlacesTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... place) {

		
			
			// For storing data from web service
			String data = "";

			// Obtain browser key from https://code.google.com/apis/console
			String key = "key="+getResources().getString(R.string.api_key);
			String input = "";

			try {
				input = "input=" + URLEncoder.encode(place[0], "utf-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			// place type to be searched
			String types = "types=geocode";

			// Sensor enabled
			String sensor = "sensor=false";

			String components = "components=country:"+Constants.CURRENT_LANGUAGE.getLanguage();

			// Building the parameters to the web service
			String parameters = input + "&" + types + "&" + components + "&"
					+ sensor + "&" + key;

			// Output format
			String output = "json";

			// Building the url to the web service
			String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"
					+ output + "?" + parameters;

			try {
				// Fetching the data from we service
				data = downloadUrl(url);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}

			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			//Log.d("DEBUUUUUG", "résultat = "+result);
			
			// Creating ParserTask
			ParserTask parserTask = new ParserTask();

			// Starting Parsing the JSON string returned by Web Service
			parserTask.execute(result);
		}
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends
			AsyncTask<String, Integer, List<HashMap<String, String>>> {

		JSONObject jObject;

		@Override
		protected List<HashMap<String, String>> doInBackground(
				String... jsonData) {

			List<HashMap<String, String>> places = null;

			PlaceJSONParser placeJsonParser = new PlaceJSONParser();

			try {
				jObject = new JSONObject(jsonData[0]);

				// Getting the parsed data as a List construct
				places = placeJsonParser.parse(jObject);

			} catch (Exception e) {
				Log.d("Exception", e.toString());
			}
			return places;
		}

		@Override
		protected void onPostExecute(List<HashMap<String, String>> result) {

			String[] from = new String[] { "description" };
			int[] to = new int[] { android.R.id.text1 };
			final List<HashMap<String, String>> finalResults = result;

			// Creating a SimpleAdapter for the AutoCompleteTextView
			SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result,
					android.R.layout.simple_list_item_1, from, to);

			// Setting the adapter
			atvPlaces.setAdapter(adapter);
			atvPlaces.showDropDown();
			atvPlaces.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					
					String ref = finalResults.get(position).get("reference");
					ParserUrlTask parserUrlTask = new ParserUrlTask();
					parserUrlTask.execute(ref.toString());
					closeSearchBar();
				}

			});
		}
	}

	private class ParserUrlTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... place) {
			String data = "";

			// Obtain browser key from https://code.google.com/apis/console
			String key = "key="+getResources().getString(R.string.api_key);

			String reference = "";

			try {
				reference = "reference=" + URLEncoder.encode(place[0], "utf-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			// place type to be searched

			// Sensor enabled
			String sensor = "sensor=true";

			// Building the parameters to the web service
			String parameters = reference + "&" + sensor + "&" + key;

			// Output format
			String output = "json";

			// Building the url to the web service
			String url = "https://maps.googleapis.com/maps/api/place/details/"
					+ output + "?" + parameters;

			try {
				// Fetching the data from we service
				data = downloadUrl(url);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;

		}

		@Override
		protected void onPostExecute(String result) {

			super.onPostExecute(result);
			try {
				//We place the view to the location found
				JSONObject locations = new JSONObject(result)
						.getJSONObject("result").getJSONObject("geometry")
						.getJSONObject("location");
				double lat = Double.parseDouble(locations.getString("lat"));
				double lng = Double.parseDouble(locations.getString("lng"));

				CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), Constants.ZOOM_GENERAL);
				googleMap.animateCamera(cu, Constants.ZOOM_SPEED_MS, null);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}	
}
