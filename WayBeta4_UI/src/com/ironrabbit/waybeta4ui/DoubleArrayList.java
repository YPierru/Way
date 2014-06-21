package com.ironrabbit.waybeta4ui;

import java.io.Serializable;
import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

/*
 * Objet permettant de manipuler 2 listes d'objets ?? la fois
 */

public class DoubleArrayList<A> implements Parcelable,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<A> listA1;
	private ArrayList<A> listA2;
	
	public DoubleArrayList(){
		this.listA1 = new ArrayList<A>();
		this.listA2 = new ArrayList<A>();
	}
	
	public DoubleArrayList(Parcel in){
		this.listA1 = new ArrayList<A>();
		in.readList(this.listA1, getClass().getClassLoader());
		this.listA2 = new ArrayList<A>();
		in.readList(this.listA2, getClass().getClassLoader());
	}
	
	public void add(A a, A b){
		this.listA1.add(a);
		this.listA2.add(b);
	}
	
	public ArrayList<A> get(int index){
		ArrayList<A> tmpRtr = new ArrayList<A>();
		tmpRtr.add(this.listA1.get(index));
		tmpRtr.add(this.listA2.get(index));
		return tmpRtr;
	}
	
	public int size(){
		return this.listA1.size();
	}
	
	public void remove(int index){
		this.listA1.remove(index);
		this.listA2.remove(index);
	}
	
	public void clear(){
		this.listA1.clear();
		this.listA2.clear();
	}
	
	public static final Parcelable.Creator<DoubleArrayList<?>> CREATOR = new Parcelable.Creator<DoubleArrayList<?>>() {

		@Override
		public DoubleArrayList<?> createFromParcel(Parcel source) {
			return new DoubleArrayList<Object>(source);
		}

		@Override
		public DoubleArrayList<?>[] newArray(int size) {
			return new DoubleArrayList[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(this.listA1);
		dest.writeList(this.listA2);
	}
}
