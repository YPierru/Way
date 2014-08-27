package com.yanpierru.way.route.activity;

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
import com.yanpierru.way.R;
import com.yanpierru.way.route.Route;
import com.yanpierru.way.route.RoutesCollection;
import com.yanpierru.way.route.cards.CardRoute;

public class ListRoutesCards extends Activity {

	//private String[] arrayTypeRoute;
	//private String mTypeRouteCurrent;
	private CardListView mCardListView;
	//private DrawerLayout mDrawerLayout;
	//private ListView mDrawerList;
	//private ActionBarDrawerToggle mDrawerToggle;
	//private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_trajet_display_cards);
		//getActionBar().setHomeButtonEnabled(true);
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		//mTypeRouteCurrent = getLastRouteTypeCreated();
		
		/*
		 * Tableau utilise pour navigationdrawer
		 */
		//arrayTypeRoute = new String[] { "Voiture", "Coureur" };

		
		/*
		 * Initialisation du drawerlayout (navigationdrawer)
		 */
		/*mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.listViewLeftDrawer);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,GravityCompat.START);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item, arrayTypeRoute));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		mDrawerToggle = new ActionBarDrawerToggle(	
				this,
				mDrawerLayout, 
				R.drawable.ic_drawer,
				R.string.drawer_open, 
				R.string.drawer_close
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle("Type de trajet");
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);*/
		
		
		mCardListView = (CardListView) findViewById(R.id.cardsList);
		

		/*
		 * Selon le dernier type de trajet sauvegard??, on place l'user sur la bonne liste
		 */
		/*if (mTypeRouteCurrent.equals(Constantes.TYPE_ROUTE_VOITURE)) {
			selectItem(0);
		} else {
			selectItem(1);
		}*/
		populateCards();
	}

	
	/*
	 * Recup??re le type du dernier trajet sauvegard??
	 */
	/*private String getLastRouteTypeCreated() {
		RoutesCollection mRoutesCollection = RoutesCollection.getInstance();
		if (mRoutesCollection.size() > 0) {
			Route lastRoute = mRoutesCollection
					.get(mRoutesCollection.size() - 1);
			if (lastRoute.getTypeRoute().equals(Constantes.TYPE_ROUTE_VOITURE)) {
				return Constantes.TYPE_ROUTE_VOITURE;
			} else {
				return Constantes.TYPE_ROUTE_COUREUR;
			}
		} else {
			return Constantes.TYPE_ROUTE_VOITURE;
		}
	}*/

	
	/*
	 * Cr??er la liste des cards, selon le type de trajet courant
	 */
	private void populateCards() {
		final RoutesCollection mRoutesCollection = RoutesCollection.getInstance();
		final ArrayList<Route> listRoutes;
		ArrayList<Card> listCardRoute = new ArrayList<Card>();
		Card card;
		CardArrayAdapter cardArrayAdapter;
		AnimationAdapter animCardArrayAdapter;
		
		
		
		/*if (mTypeRouteCurrent.equals(Constantes.TYPE_ROUTE_VOITURE)) {
			listRoutes = mRoutesCollection.getListRoutesVoiture();
			mTitle="Vos trajets voiture";
			getActionBar().setTitle(mTitle);
		} else {
			listRoutes = mRoutesCollection.getListRoutesCoureur();
			mTitle="Vos trajets pi??tons";
			getActionBar().setTitle(mTitle);
		}*/
		listRoutes=mRoutesCollection.getListRoutes();
		getActionBar().setTitle("Vos trajets");

		for (int i = 0; i < listRoutes.size(); i++) {
			//Log.d("DEBUUUUUUUUG", "Trajet=>"+listRoutes.get(i).getName()+" ID=>"+listRoutes.get(i).getIndexCollection());
			card = new CardRoute(getApplicationContext(),
					listRoutes.get(i));
			card.setId(""+i);
			listCardRoute.add(card);
		}
		
		cardArrayAdapter = new CardArrayAdapter(this,listCardRoute);
		cardArrayAdapter.setEnableUndo(true);
		mCardListView = (CardListView) findViewById(R.id.cardsList);
		animCardArrayAdapter = new SwingLeftInAnimationAdapter(cardArrayAdapter);
		animCardArrayAdapter.setAbsListView(mCardListView);
		mCardListView.setExternalAdapter(animCardArrayAdapter, cardArrayAdapter);

		if (listRoutes.size() == 0) {
			alertNoRoutesSave();
		}

	}
	
	
	/*
	 * Affiche un message indiquant qu'il n'y a pas de trajets de ce type sauvegard??s
	 */
	public void alertNoRoutesSave(){
		final AlertDialog.Builder alert = new AlertDialog.Builder(
				ListRoutesCards.this).setTitle("Aucun trajet !");
		alert.setMessage(Html.fromHtml("Vous n'avez <b>aucun trajets</b>, commencez par en créer un !"));
		alert.setCancelable(true);
		alert.setPositiveButton("Créer mon trajet",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						dialogCreateNewRoute();
					}
				});
		alert.setNegativeButton("Plus tard",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		alert.show();
	}

	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item_NouveauTrajet = menu.add("Créer un trajet").setIcon(
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

		/*MenuItem item_deleteAll = menu.add("Tout supprimer").setIcon(
				R.drawable.ic_action_discard);
		item_deleteAll.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		item_deleteAll
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						RoutesCollection mRoutesCollection = RoutesCollection
								.getInstance();
						mRoutesCollection.deleteFile();
						populateCards();
						return false;
					}
				});*/
		return true;
	}

	
	
	/*
	 * Retourne la date du jour format dd/mm/yyyy hh:mm
	 */
	private String getCurrentDayTime() {
		Date aujourdhui = new Date();
		DateFormat shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		return shortDateFormat.format(aujourdhui);
	}


	/*
	 * Automatiquement appel?? lors du retour
	 */
	public void onRestart() {
		super.onRestart();
		populateCards();
	}
	
	

	/*
	 * Fen??tre permettant la saisie d'un nom de trajet et la bascule vers la carte
	 */
	public void dialogCreateNewRoute() {
		final AlertDialog.Builder alert = new AlertDialog.Builder(
				ListRoutesCards.this).setTitle("Saisir le nom du trajet");
		final EditText input = new EditText(getApplicationContext());
		input.setHint("Nom du trajet");
		input.setTextColor(Color.BLACK);
		alert.setView(input);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString().trim();
				RoutesCollection mRC = RoutesCollection.getInstance();
				if(value.isEmpty()){
					Toast.makeText(getApplicationContext(), "Vous devez au moins saisir une lettre", Toast.LENGTH_SHORT).show();
					dialog.cancel();
					dialogCreateNewRoute();
				}else if(mRC.nameExists(value)){
					Toast.makeText(getApplicationContext(), "Le trajet "+value+" existe déjà", Toast.LENGTH_SHORT).show();
					dialog.cancel();
					dialogCreateNewRoute();
				}else{
					/*Route newTrajet = new Route(value, false, false,
							getCurrentDayTime(), mTypeRouteCurrent);*/
					Route newTrajet = new Route(value, false, false,
									getCurrentDayTime());
					Intent toCreateTrajetActivity = new Intent(
							ListRoutesCards.this, CreateRoute.class);
					toCreateTrajetActivity.putExtra("trajet",
							(Parcelable) newTrajet);
					toCreateTrajetActivity.putExtra("MODE", "Creation");
					startActivity(toCreateTrajetActivity);
				}
			}
		});
		alert.show();
	}

	
	
	/*private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		if(mDrawerList.getItemAtPosition(position).equals("Voiture")){
			mTypeRouteCurrent=Constantes.TYPE_ROUTE_VOITURE;
		}else{
			mTypeRouteCurrent=Constantes.TYPE_ROUTE_COUREUR;
		}
        mDrawerList.setItemChecked(position, true);
		mDrawerLayout.closeDrawer(mDrawerList);
		populateCards();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}*/

}
