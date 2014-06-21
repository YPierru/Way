package com.ironrabbit.waybeta4ui.route.downloaded;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;




public class Step implements Parcelable,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DistDur distance;
	private DistDur duration;
	private MyPoint end_location;
	private String html_instructions;
	private Poly polyline;
	private MyPoint start_location;

	public Step(){}
	
	public Step(Parcel in){
		this.distance=in.readParcelable(getClass().getClassLoader());
		this.duration=in.readParcelable(getClass().getClassLoader());
		this.end_location=in.readParcelable(getClass().getClassLoader());
		this.html_instructions = in.readString();
		this.polyline=in.readParcelable(getClass().getClassLoader());
		this.start_location=in.readParcelable(getClass().getClassLoader());
	}
	
	public DistDur getDistance() {
		return distance;
	}

	public void setDistance(DistDur distance) {
		this.distance = distance;
	}

	public DistDur getDuration() {
		return duration;
	}

	public void setDuration(DistDur duration) {
		this.duration = duration;
	}

	public MyPoint getEnd_location() {
		return end_location;
	}

	public void setEnd_location(MyPoint end_location) {
		this.end_location = end_location;
	}

	public String getHtml_instructions() {
		return html_instructions;
	}

	public void setHtml_instructions(String html_instructions) {
		this.html_instructions = html_instructions;
	}

	public Poly getPolyline() {
		return polyline;
	}

	public void setPolyline(Poly polyline) {
		this.polyline = polyline;
	}

	public MyPoint getStart_location() {
		return start_location;
	}

	public void setStart_location(MyPoint start_location) {
		this.start_location = start_location;
	}

	@Override
	public String toString() {
		return "Step [distance=" + distance + ", duration=" + duration
				+ ", end_location=" + end_location + ", html_instructions="
				+ html_instructions + ", polyline=" + polyline
				+ ", start_location=" + start_location + "]";
	}
	
	public static final Parcelable.Creator<Step> CREATOR = new Parcelable.Creator<Step>() {

		@Override
		public Step createFromParcel(Parcel source) {
			return new Step(source);
		}

		@Override
		public Step[] newArray(int size) {
			return new Step[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(this.distance, 0);
		dest.writeParcelable(this.duration, 0);
		dest.writeParcelable(this.end_location, 0);
		dest.writeString(this.html_instructions);
		dest.writeParcelable(this.polyline, 0);
		dest.writeParcelable(this.start_location, 0);
	}

}
