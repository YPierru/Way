package com.yanp.way.route.cards;

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

import com.yanp.way.R;
import com.yanp.way.gps.activity.GPSNavigation;
import com.yanp.way.route.Route;
import com.yanp.way.route.activity.CreateRoute;
import com.yanp.way.route.activity.SeeRoute;

public class CardRouteHeader extends CardHeader {

	
	private Route route;
	private Context context;

	public CardRouteHeader(Context context, int headerLayout, Route route) {
		super(context, headerLayout);
		this.route=route;
		this.context=context;
		init();
	}
	
	private void init(){
		setTitle(this.route.getName());
		if(this.route.isValidate()){
			setPopupMenu(R.menu.cardeheader_menu_routevalidate, new CardHeader.OnClickCardHeaderPopupMenuListener() {
	            @Override
	            public void onMenuItemClick(BaseCard card, MenuItem item) {
	            	switch(item.getItemId()){
	            		case R.id.item_voir:
	            			Intent toSeeRoute = new Intent(context,SeeRoute.class);
							toSeeRoute.putExtra("route", (Parcelable) route);
							toSeeRoute.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(toSeeRoute);
	            		break;
	            		
	            		case R.id.item_gps:
							Intent toGPSNavigation = new Intent(context,GPSNavigation.class);
							toGPSNavigation.putExtra("Route_for_navigation_gps", (Parcelable) route);
							toGPSNavigation.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(toGPSNavigation);
	            		break;
	            	}
	            }
	        });
		}else{
			setPopupMenu(R.menu.cardeheader_menu_routeunvalidate, new CardHeader.OnClickCardHeaderPopupMenuListener() {
	            @Override
	            public void onMenuItemClick(BaseCard card, MenuItem item) {
	            	
	            	Intent toFinishTrajet = new Intent(context,CreateRoute.class);
					toFinishTrajet.putExtra("route",(Parcelable) route);
					toFinishTrajet.putExtra("type_of_route","not_finish");
					toFinishTrajet.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(toFinishTrajet);
	            }
	        });
		}
	}

	@Override
	public void setupInnerViewElements(ViewGroup parent, View view) {
		super.setupInnerViewElements(parent, view);
		String status;
		String dateCreation = this.route.getDateCreation();

		TextView tvSubDate = (TextView)view.findViewById(R.id.tv_cardroute_headerlayout_subtitle_date);
		tvSubDate.setText(" - "+dateCreation);
		if(this.route.isValidate()){
			status=this.context.getResources().getString(R.string.finished);
		}else{
			status=this.context.getResources().getString(R.string.in_progress);
		}

		TextView tvSubtitleStatus = (TextView) view.findViewById(R.id.tv_cardroute_headerlayout_subtitle_status);
		if(status.equals(this.context.getResources().getString(R.string.in_progress))){
			tvSubtitleStatus.setTextColor(Color.parseColor("#ffa62d"));
		}else{
			tvSubtitleStatus.setTextColor(Color.parseColor("#55bc00"));
		}
		tvSubtitleStatus.setText(status);
		
	}


}
