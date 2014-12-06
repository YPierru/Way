package com.yanp.way.route.activity;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingLeftInAnimationAdapter;
import com.yanp.way.Constants;
import com.yanp.way.R;
import com.yanp.way.route.Route;
import com.yanp.way.route.RoutesCollection;
import com.yanp.way.route.cards.CardRoute;

public class ListRoutesCards extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_trajet_display_cards);		
		populateCards();
	}

	
	/**
	 * Create the list of the routes
	 */
	private void populateCards() {
		final ArrayList<Route> listRoutes;
		ArrayList<Card> listCardRoute = new ArrayList<Card>();
		Card card;
		CardArrayAdapter cardArrayAdapter;
		AnimationAdapter animCardArrayAdapter;
		

		listRoutes=RoutesCollection.getInstance().getListRoutes();
		getActionBar().setTitle(getResources().getString(R.string.lrc_name));

		/**
		 * Create a card for each route in the list
		 */
		for (int i = 0; i < listRoutes.size(); i++) {
			card = new CardRoute(getApplicationContext(),listRoutes.get(i));
			card.setId(""+i);
			listCardRoute.add(card);
		}
		
		cardArrayAdapter = new CardArrayAdapter(this,listCardRoute);
		cardArrayAdapter.setEnableUndo(true);
		
		CardListView cardListView = (CardListView) findViewById(R.id.cardsList);
		
		/**
		 * Set the animation when the card appears.
		 */
		animCardArrayAdapter = new SwingLeftInAnimationAdapter(cardArrayAdapter);
		animCardArrayAdapter.setAbsListView(cardListView);
		cardListView.setExternalAdapter(animCardArrayAdapter, cardArrayAdapter);

		if (listRoutes.size() == 0) {
			alertNoRoutesSave();
		}

	}
	
	
	/**
	 * If there is no route previously save, display chich asks to the user if he want to create a new route.
	 */
	public void alertNoRoutesSave(){
		final AlertDialog.Builder alert = new AlertDialog.Builder(
				ListRoutesCards.this).setTitle(getResources().getString(R.string.lrc_alert_no_route_title));
		String message;
		if(Constants.CURRENT_LANGUAGE.getLanguage().equals("fr")){
			message="Vous n'avez <b>aucun trajets</b>, commencez par en créer un !";
		}else{
			message="You don't have <b>any routes</b>, create one !";
		}
		alert.setMessage(Html.fromHtml(message));
		alert.setCancelable(true);
		alert.setPositiveButton(getResources().getString(R.string.lrc_alert_no_route_positive_button),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						dialogCreateNewRoute();
					}
				});
		/*alert.setNegativeButton("Plus tard",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});*/
		alert.show();
	}

	/**
	 * Item for creating a new route
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item_NouveauTrajet = menu.add(getResources().getString(R.string.lrc_item_new_route)).setIcon(
				R.drawable.ic_action_new);
		item_NouveauTrajet.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		item_NouveauTrajet
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						dialogCreateNewRoute();
						return false;
					}
				});
		return true;
	}

	
	
	/*
	 * Return current date format dd/mm/yyyy hh:mm
	 */
	private String getCurrentDayTime() {
		Date aujourdhui = new Date();
		DateFormat shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		return shortDateFormat.format(aujourdhui);
	}

	public void onRestart() {
		super.onRestart();
		populateCards();
	}
	
	

	/*
	 * Dialog for creating a new Route
	 */
	public void dialogCreateNewRoute() {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.lrc_alert_new_route_title));
		
		final EditText input = new EditText(getApplicationContext());
		
		input.setHint(getResources().getString(R.string.lrc_alert_new_route_title));
		input.setTextColor(Color.BLACK);
		alert.setView(input);
		
		alert.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				
				String value = input.getText().toString().trim();
				
				if(value.isEmpty()){
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.lrc_alert_new_route_noname), Toast.LENGTH_SHORT).show();
					dialog.cancel();
					dialogCreateNewRoute();
					
				}else if(RoutesCollection.getInstance().isNameAlreadyPresent(value)){
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.lrc_alert_new_route_existp1)+" "+value+" "+getResources().getString(R.string.lrc_alert_new_route_existp2), Toast.LENGTH_SHORT).show();
					dialog.cancel();
					dialogCreateNewRoute();
					
				}else{
					
					Route newRoute = new Route(value, false, false,getCurrentDayTime());
					Intent toCreateRouteActivity = new Intent(ListRoutesCards.this, CreateRoute.class);
					toCreateRouteActivity.putExtra("route",(Parcelable) newRoute);
					toCreateRouteActivity.putExtra("type_of_route", "create_new_route");
					startActivity(toCreateRouteActivity);
				}
			}
		});
		alert.show();
	}

}
