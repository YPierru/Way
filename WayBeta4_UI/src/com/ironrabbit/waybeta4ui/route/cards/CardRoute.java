package com.ironrabbit.waybeta4ui.route.cards;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ironrabbit.waybeta4ui.R;
import com.ironrabbit.waybeta4ui.route.Route;
import com.ironrabbit.waybeta4ui.route.RoutesCollection;

public class CardRoute extends Card {
	private Route mRoute;
	private Context context;

	public CardRoute(Context context, Route r) {
		this(context, R.layout.cardroute_inner_layout, r);
	}

	private CardRoute(Context context, int innerLayout, Route r) {
		super(context, innerLayout);
		this.mRoute = r;
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
				mRoutesCollection.remove(mRoute);
				mRoutesCollection.syncRouteIndex();
				mRoutesCollection.saveRoutesCollection();
			}
		});
		
		setOnUndoSwipeListListener(new OnUndoSwipeListListener() {
	          @Override
	          public void onUndoSwipe(Card card) {
					RoutesCollection mRoutesCollection = RoutesCollection.getInstance();				
					mRoutesCollection.add(mRoute.getIndexCollection(), mRoute);
					mRoutesCollection.saveRoutesCollection();
	          }
        });

		CardHeader cardRouteHeader = new CardRouteHeader(this.context,
				R.layout.cardroute_header_layout, this.mRoute);
		addCardHeader(cardRouteHeader);
		/*
		 * CardExpand cardRouteExpand = new CardRouteExpand(this.context);
		 * addCardExpand(cardRouteExpand);
		 */
	}

	@Override
	public void setupInnerViewElements(ViewGroup parent, View view) {

		TextView kilometrage = (TextView) view
				.findViewById(R.id.tv_cardroute_innerlayout_kilometrage);
		TextView duree = (TextView) view
				.findViewById(R.id.tv_cardroute_innerlayout_duree);
		TextView addrDepart = (TextView) view
				.findViewById(R.id.tv_cardroute_innerlayout_addrdepart);
		TextView addrArrivee = (TextView) view
				.findViewById(R.id.tv_cardroute_innerlayout_addrarrivee);
		TextView tjnonfini = (TextView)view.findViewById(R.id.tv_cardroute_innerlayout_tjnonfini);

		if (mRoute.isValidate()) {
			tjnonfini.setVisibility(View.GONE);
			addrDepart.setVisibility(View.VISIBLE);
			addrArrivee.setVisibility(View.VISIBLE);
			double dist = this.mRoute.getDistTotal();
			if (dist < 1000) {
				kilometrage.setText((int) dist + "m - ");
			} else {
				kilometrage.setText((dist / 1000) + "Km - ");
			}

			int dureeSecond = this.mRoute.getDureeTotal();
			int heures = (dureeSecond / 3600);
			int minutes = ((dureeSecond % 3600) / 60);
			if (heures == 0) {
				duree.setText(minutes + "min");
			} else {
				duree.setText(heures + "h" + minutes + "min");
			}

			addrDepart.setText(this.mRoute.getStartAddress());
			addrArrivee.setText(this.mRoute.getEndAddress());
		}else{
			tjnonfini.setVisibility(View.VISIBLE);
			addrDepart.setVisibility(View.GONE);
			addrArrivee.setVisibility(View.GONE);
		}
	}
}
