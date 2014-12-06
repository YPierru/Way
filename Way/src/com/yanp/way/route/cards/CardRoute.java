package com.yanp.way.route.cards;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yanp.way.R;
import com.yanp.way.route.Route;
import com.yanp.way.route.RoutesCollection;

/**
 * The card that we see in the lists
 * @author YPierru
 *
 */
public class CardRoute extends Card {
	private Route route;
	private Context context;

	public CardRoute(Context context, Route route) {
		this(context, R.layout.cardroute_inner_layout, route);
	}

	private CardRoute(Context context, int innerLayout, Route route) {
		super(context, innerLayout);
		this.route = route;
		this.context = context;
		init();
	}

	private void init() {
		setSwipeable(true);
		setType(2);
		setOnSwipeListener(new OnSwipeListener() {
			
			@Override
			public void onSwipe(Card card) {
				// TODO Auto-generated method stub
				RoutesCollection mRoutesCollection = RoutesCollection.getInstance();				
				mRoutesCollection.remove(route);
				mRoutesCollection.syncRouteIndex();
				mRoutesCollection.saveRoutesCollection();
			}
		});
		
		setOnUndoSwipeListListener(new OnUndoSwipeListListener() {
	          @Override
	          public void onUndoSwipe(Card card) {
					RoutesCollection mRoutesCollection = RoutesCollection.getInstance();				
					mRoutesCollection.add(route.getIndexCollection(), route);
					mRoutesCollection.saveRoutesCollection();
	          }
        });

		CardHeader cardRouteHeader = new CardRouteHeader(this.context,R.layout.cardroute_header_layout, this.route);
		addCardHeader(cardRouteHeader);

	}

	@Override
	public void setupInnerViewElements(ViewGroup parent, View view) {

		TextView kilometrage = (TextView) view
				.findViewById(R.id.tv_cardroute_innerlayout_kilometrage);
		TextView duration = (TextView) view
				.findViewById(R.id.tv_cardroute_innerlayout_duree);
		TextView addrStarting = (TextView) view
				.findViewById(R.id.tv_cardroute_innerlayout_addrdepart);
		TextView addrDestination = (TextView) view
				.findViewById(R.id.tv_cardroute_innerlayout_addrarrivee);
		TextView tv_RouteIsNotFinish = (TextView)view.findViewById(R.id.tv_cardroute_innerlayout_tjnonfini);
		tv_RouteIsNotFinish.setText(this.context.getResources().getString(R.string.in_progress));

		if (route.isValidate()) {
			tv_RouteIsNotFinish.setVisibility(View.GONE);
			addrStarting.setVisibility(View.VISIBLE);
			addrDestination.setVisibility(View.VISIBLE);
			
			double totalDistance = this.route.getDistTotal();
			
			if (totalDistance < 1000) {
				kilometrage.setText((int) totalDistance + "m - ");
			} else {
				kilometrage.setText((totalDistance / 1000) + "Km - ");
			}

			int dureeSecond = this.route.getDureeTotal();
			int heures = (dureeSecond / 3600);
			int minutes = ((dureeSecond % 3600) / 60);
			if (heures == 0) {
				duration.setText(minutes + "min");
			} else {
				duration.setText(heures + "h" + minutes + "min");
			}

			addrStarting.setText(this.route.getStartAddress());
			addrDestination.setText(this.route.getEndAddress());
		}else{
			tv_RouteIsNotFinish.setVisibility(View.VISIBLE);
			addrStarting.setVisibility(View.GONE);
			addrDestination.setVisibility(View.GONE);
		}
	}
}
