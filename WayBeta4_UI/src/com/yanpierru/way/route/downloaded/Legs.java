package com.yanpierru.way.route.downloaded;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Legs  implements Parcelable,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DistDur distance;
	private DistDur duration;
	private String end_address;
	private MyPoint end_location;
	private String start_address;
	private MyPoint start_location;
	private List<Step> steps;
	
	public Legs(){}

	public Legs(Parcel in){
		this.distance=in.readParcelable(getClass().getClassLoader());
		this.duration=in.readParcelable(getClass().getClassLoader());
		this.end_address=in.readString();
		this.end_location=in.readParcelable(getClass().getClassLoader());
		this.start_address=in.readString();
		this.start_location=in.readParcelable(getClass().getClassLoader());
		this.steps = new ArrayList<Step>();
		in.readList(this.steps, getClass().getClassLoader());
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

	public String getEnd_address() {
		return end_address;
	}

	public void setEnd_address(String end_address) {
		this.end_address = end_address;
	}

	public MyPoint getEnd_location() {
		return end_location;
	}

	public void setEnd_location(MyPoint end_location) {
		this.end_location = end_location;
	}

	public String getStart_address() {
		return start_address;
	}

	public void setStart_address(String start_address) {
		this.start_address = start_address;
	}

	public MyPoint getStart_location() {
		return start_location;
	}

	public void setStart_location(MyPoint start_location) {
		this.start_location = start_location;
	}

	public List<Step> getSteps() {
		return steps;
	}

	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}

	@Override
	public String toString() {
		return "Legs [distance=" + distance + ", duration=" + duration
				+ ", end_address=" + end_address + ", end_location="
				+ end_location + ", start_address=" + start_address
				+ ", start_location=" + start_location + ", steps=" + steps
				+ "]";
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static final Parcelable.Creator<Legs> CREATOR = new Parcelable.Creator<Legs>() {

		@Override
		public Legs createFromParcel(Parcel source) {
			return new Legs(source);
		}

		@Override
		public Legs[] newArray(int size) {
			return new Legs[size];
		}
	};
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(this.distance, 0);
		dest.writeParcelable(this.duration, 0);
		dest.writeString(this.end_address);
		dest.writeParcelable(this.end_location, 0);
		dest.writeString(this.start_address);
		dest.writeParcelable(this.start_location, 0);
		dest.writeList(this.steps);
	}

}
