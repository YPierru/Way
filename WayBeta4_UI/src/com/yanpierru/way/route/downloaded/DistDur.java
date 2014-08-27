package com.yanpierru.way.route.downloaded;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class DistDur implements Parcelable,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String text;
	private int value;
	
	public DistDur(){}

	public DistDur(Parcel in){
		this.text=in.readString();
		this.value=in.readInt();
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "DistDur [text=" + text + ", value=" + value + "]";
	}

	public static final Parcelable.Creator<DistDur> CREATOR = new Parcelable.Creator<DistDur>() {

		@Override
		public DistDur createFromParcel(Parcel source) {
			return new DistDur(source);
		}

		@Override
		public DistDur[] newArray(int size) {
			return new DistDur[size];
		}
	};
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.text);
		dest.writeInt(this.value);
	}

}
