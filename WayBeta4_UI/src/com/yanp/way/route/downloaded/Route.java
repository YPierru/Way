package com.yanp.way.route.downloaded;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Route implements Parcelable,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Legs> legs;
	private Poly overview_polyline;
	
	public Route(){}
	
	public Route (Parcel in){
		this.legs = new ArrayList<Legs>();
		in.readList(this.legs, getClass().getClassLoader());
		this.overview_polyline=in.readParcelable(getClass().getClassLoader());
	}

	public List<Legs> getLegs() {
		return legs;
	}

	public void setLegs(List<Legs> legs) {
		this.legs = legs;
	}

	public Poly getOverview_polyline() {
		return overview_polyline;
	}

	public void setOverview_polyline(Poly overview_polyline) {
		this.overview_polyline = overview_polyline;
	}

	@Override
	public String toString() {
		return "Route [legs=" + legs + ", overview_polyline="
				+ overview_polyline + "]";
	}
	
	public static final Parcelable.Creator<Route> CREATOR = new Parcelable.Creator<Route>() {

		@Override
		public Route createFromParcel(Parcel source) {
			return new Route(source);
		}

		@Override
		public Route[] newArray(int size) {
			return new Route[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(this.legs);
		dest.writeParcelable(this.overview_polyline, 0);
	}

}
