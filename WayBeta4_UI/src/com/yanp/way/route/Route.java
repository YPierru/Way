package com.yanp.way.route;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.yanp.way.DoubleArrayList;
import com.yanp.way.route.downloaded.DirectionsResponse;
import com.yanp.way.route.downloaded.Legs;
import com.yanp.way.route.downloaded.Step;

public class Route implements Parcelable,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<DirectionsResponse> listSegment;
	private String name;
	private boolean save;
	private boolean validate;
	private DoubleArrayList<Double> pointsWhoDrawsPolyline;
	private DoubleArrayList<Double> listLatLngMarkers; //List des points concernant les markers
	private String dateCreation;
	private String dateLastModification;
	private int idHash;
	private int indexCollection;

	public Route(ArrayList<DirectionsResponse> listSegment,
				 String name,
				 boolean save,
				 ArrayList<LatLng> pointsWhoDrawPolyline,
				 ArrayList<LatLng> listLatLngMarkers,
				 boolean validate,
				 String dateCreation,
				 String dateLastModification,
				 int idHash,
				 int indexCollection){
		
		this.listSegment=listSegment;
		this.name=name;
		this.save=save;
		
		this.pointsWhoDrawsPolyline=new DoubleArrayList<Double>();
		for(int i=0;i<pointsWhoDrawPolyline.size();i++){
			this.pointsWhoDrawsPolyline.add(pointsWhoDrawPolyline.get(i).latitude,pointsWhoDrawPolyline.get(i).longitude);
		}
		
		this.listLatLngMarkers=new DoubleArrayList<Double>();
		for(int i=0;i<listLatLngMarkers.size();i++){
			this.listLatLngMarkers.add(listLatLngMarkers.get(i).latitude,listLatLngMarkers.get(i).longitude);
		}
		
		this.validate=validate;
		this.dateCreation=dateCreation;
		this.dateLastModification=dateLastModification;
		this.idHash=idHash;
		this.indexCollection=indexCollection;
	}
	
	public Route(String name, boolean save, boolean validate,String dateCreation) {
		this.name = name;
		this.save = save;
		this.validate=validate;
		this.listSegment = new ArrayList<DirectionsResponse>();
		this.pointsWhoDrawsPolyline=new DoubleArrayList<Double>();
		this.listLatLngMarkers=new DoubleArrayList<Double>();
		this.dateCreation=dateCreation;
		this.dateLastModification=dateCreation;
		this.idHash=System.identityHashCode(this);
	}
	
	public Route(Parcel in){
		this.listSegment = new ArrayList<DirectionsResponse>();
		in.readList(this.listSegment, getClass().getClassLoader());
		this.name = in.readString();
		boolean[] bool = new boolean[2];
		in.readBooleanArray(bool);
		this.save = bool[0];
		this.validate = bool[1];
		this.pointsWhoDrawsPolyline=in.readParcelable(getClass().getClassLoader());
		this.listLatLngMarkers=in.readParcelable(getClass().getClassLoader());
		this.dateCreation=in.readString();
		this.dateLastModification=in.readString();
		this.idHash=in.readInt();
		this.indexCollection=in.readInt();
	}


	public ArrayList<DirectionsResponse> getListSegment() {
		return listSegment;
	}

	public void setListSegment(ArrayList<DirectionsResponse> listSegment) {
		this.listSegment = listSegment;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDateDerModif() {
		return dateLastModification;
	}

	public void setDateDerModif(String dateDerModif) {
		this.dateLastModification = dateDerModif;
	}

	public String getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(String dateCreation) {
		this.dateCreation = dateCreation;
	}

	public boolean isSave() {
		return save;
	}

	public void setSave(boolean s) {

		Date aujourdhui = new Date();
		DateFormat shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		this.dateCreation=shortDateFormat.format(aujourdhui);
		this.save = s;
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean v) {
		this.validate = v;
	}

	public DoubleArrayList<Double> getPointsWhoDrawsPolyline() {
		return pointsWhoDrawsPolyline;
	}

	public void setPointsWhoDrawsPolyline(
			DoubleArrayList<Double> pointsWhoDrawsPolyline) {
		this.pointsWhoDrawsPolyline = pointsWhoDrawsPolyline;
	}

	public ArrayList<LatLng> getPointsWhoDrawsPolylineLatLng() {
		ArrayList<LatLng> lp = new ArrayList<LatLng>();
		ArrayList<Double> ld = new ArrayList<Double>();
		for(int i=0;i<this.pointsWhoDrawsPolyline.size();i++){
			ld=this.pointsWhoDrawsPolyline.get(i);
			LatLng tmp = new LatLng(ld.get(0), ld.get(1));
			lp.add(tmp);
		}
		return lp;
	}

	public void setPointsWhoDrawsPolylineLatLng(ArrayList<LatLng> pwho) {
		this.pointsWhoDrawsPolyline.clear();
		Double lat,lng;
		for(int i=0;i<pwho.size();i++){
			lat = pwho.get(i).latitude;
			lng = pwho.get(i).longitude;
			this.pointsWhoDrawsPolyline.add(lat,lng);
		}
	}

	public DoubleArrayList<Double> getListMarkers() {
		return listLatLngMarkers;
	}

	public void setListMarkers(DoubleArrayList<Double> listMarkers) {
		this.listLatLngMarkers = listMarkers;
	}
	
	public void setListMarkersMk(ArrayList<Marker> lm){
		this.listLatLngMarkers.clear();
		for(int i=0;i<lm.size();i++){
			this.listLatLngMarkers.add(lm.get(i).getPosition().latitude, lm.get(i).getPosition().longitude);
		}
	}

	public ArrayList<LatLng> getListMarkersLatLng() {
		ArrayList<LatLng> lp = new ArrayList<LatLng>();
		ArrayList<Double> ld = new ArrayList<Double>();
		for(int i=0;i<this.listLatLngMarkers.size();i++){
			ld=this.listLatLngMarkers.get(i);
			LatLng tmp = new LatLng(ld.get(0), ld.get(1));
			lp.add(tmp);
		}
		return lp;
	}

	public void setListMarkersLatLng(ArrayList<LatLng> lm) {
		this.listLatLngMarkers.clear();
		Double lat,lng;
		for(int i=0;i<lm.size();i++){
			lat = lm.get(i).latitude;
			lng = lm.get(i).longitude;
			this.listLatLngMarkers.add(lat,lng);
		}
	}

	public int getIdHash() {
		return idHash;
	}

	public void setIdHash(int idHash) {
		this.idHash = idHash;
	}

	public int getIndexCollection() {
		return indexCollection;
	}

	public void setIndexCollection(int indexCollection) {
		this.indexCollection = indexCollection;
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
		dest.writeList(this.listSegment);
		dest.writeString(this.name);
		boolean[] arrayBool = { this.save,this.validate };
		dest.writeBooleanArray(arrayBool);
		dest.writeParcelable(this.pointsWhoDrawsPolyline,0);
		dest.writeParcelable(this.listLatLngMarkers,0);
		dest.writeString(this.dateCreation);
		dest.writeString(this.dateLastModification);
		dest.writeInt(this.idHash);
		dest.writeInt(this.indexCollection);
	}
	
	public ArrayList<Step> getListSteps(){
		ArrayList<Step> listSteps = new ArrayList<Step>();
		List<Legs> listLegs = this.listSegment.get(0).getRoutes().get(0).getLegs();
		for(int i=0;i<listLegs.size();i++){
			listSteps.addAll(listLegs.get(i).getSteps());
		}		
		return listSteps;
	}
	
	public int getDistTotal(){
		int distTotal=0;
		if(this.listSegment.size()>0){
			List<Legs> listLegs = this.listSegment.get(0).getRoutes().get(0).getLegs();
			for(int i=0;i<listLegs.size();i++){
				distTotal+=listLegs.get(i).getDistance().getValue();
			}
		}
		
		return distTotal;
	}
	
	public int getDureeTotal(){
		int dureeTotal=0;
		List<Legs> listLegs = this.listSegment.get(0).getRoutes().get(0).getLegs();
		for(int i=0;i<listLegs.size();i++){
			dureeTotal+=listLegs.get(i).getDuration().getValue();
		}
		
		return dureeTotal;
	}
	
	public String getStartAddress(){
		return this.listSegment.get(0).getRoutes().get(0).getLegs().get(0).getStart_address();
	}
	
	public String getEndAddress(){
		List<Legs> listLegs = this.listSegment.get(0).getRoutes().get(0).getLegs();
		return listLegs.get(listLegs.size()-1).getEnd_address();
	}
	
	public void removeLastDR(){
		this.listSegment.remove(this.listSegment.size()-1);
	}
}
