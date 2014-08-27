package com.yanpierru.way.route.cards;

import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yanpierru.way.R;
import com.yanpierru.way.gps.activity.GPSRunner;
import com.yanpierru.way.route.Route;
import com.yanpierru.way.route.activity.CreateRoute;
import com.yanpierru.way.route.activity.SeeRoute;

public class CardRouteHeader extends CardHeader {

	
	private Route mRoute;
	private Context context;

	public CardRouteHeader(Context context, int headerLayout, Route r) {
		super(context, headerLayout);
		this.mRoute=r;
		this.context=context;
		init();
	}
	
	private void init(){
		//setButtonExpandVisible(true);
		setTitle(this.mRoute.getName());
		if(this.mRoute.isValidate()){
			setPopupMenu(R.menu.cardeheader_menu_routevalidate, new CardHeader.OnClickCardHeaderPopupMenuListener() {
	            @Override
	            public void onMenuItemClick(BaseCard card, MenuItem item) {
	            	switch(item.getItemId()){
	            		case R.id.item_voir:
	            			Intent toSeeRoute = new Intent(context,
									SeeRoute.class);
							toSeeRoute.putExtra("trajet", (Parcelable) mRoute);
							toSeeRoute.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(toSeeRoute);
	            		break;
	            		
	            		case R.id.item_gps:
							Intent toGPSRunner = new Intent(context,
									GPSRunner.class);
							toGPSRunner.putExtra("TRAJET", (Parcelable) mRoute);
							toGPSRunner.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(toGPSRunner);
	            		break;
	            	}
	            }
	        });
		}else{
			setPopupMenu(R.menu.cardeheader_menu_routeunvalidate, new CardHeader.OnClickCardHeaderPopupMenuListener() {
	            @Override
	            public void onMenuItemClick(BaseCard card, MenuItem item) {
	            	
	            	Intent toFinishTrajet = new Intent(context,CreateRoute.class);
					toFinishTrajet.putExtra("trajet",(Parcelable) mRoute);
					toFinishTrajet.putExtra("MODE","Modification");
					toFinishTrajet.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(toFinishTrajet);
	            }
	        });
		}
	}

	@Override
	public void setupInnerViewElements(ViewGroup parent, View view) {
		super.setupInnerViewElements(parent, view);
		String mStatus;
		String mDate = this.mRoute.getDateCreation();

		TextView tvSubDate = (TextView)view.findViewById(R.id.tv_cardroute_headerlayout_subtitle_date);
		tvSubDate.setText(" - "+mDate);
		if(this.mRoute.isValidate()){
			mStatus="Terminé";
		}else{
			mStatus="En cours";
		}

		TextView tvSubtitleStatus = (TextView) view.findViewById(R.id.tv_cardroute_headerlayout_subtitle_status);
		if(mStatus.equals("En cours")){
			tvSubtitleStatus.setTextColor(Color.parseColor("#ffa62d"));
		}else{
			tvSubtitleStatus.setTextColor(Color.parseColor("#55bc00"));
		}
		tvSubtitleStatus.setText(mStatus);
		
	}


}
