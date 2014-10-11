package com.yanp.way.route.downloaded;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class MyPoint implements Parcelable,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double lat;
	private double lng;

	public MyPoint(){}
	
	public MyPoint(Parcel in){
		this.lat=in.readDouble();
		this.lng=in.readDouble();
	}
	
	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	@Override
	public String toString() {
		return "MyPoint [lat=" + lat + ", lng=" + lng + "]";
	}

	public static final Parcelable.Creator<MyPoint> CREATOR = new Parcelable.Creator<MyPoint>() {

		@Override
		public MyPoint createFromParcel(Parcel source) {
			return new MyPoint(source);
		}

		@Override
		public MyPoint[] newArray(int size) {
			return new MyPoint[size];
		}
	};
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(this.lat);
		dest.writeDouble(this.lng);
	}

}
