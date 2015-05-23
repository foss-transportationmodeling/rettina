package com.crash.routeinfo;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class Stop {
	
	// Setting up the variables that this class will use
	private int routeID;
	private int stopID;
	private String stopDescription;
	private LatLng latLng;
	private ArrayList<LatLng> routePoints = new ArrayList<LatLng>();
	private String routeTitle;

	// Constructor for setting up the Stop class
	
	private Marker marker;
	
	public Stop(int _routeID, int _stopID, String _stopDescription, LatLng _latLng){
		routeID = _routeID;
		stopID = _stopID;
		stopDescription = _stopDescription;
		latLng = _latLng;
	}

	
	public int getRouteID() {
		return routeID;
	}

	
	public void setRouteID(int routeID) {
		this.routeID = routeID;
	}

	
	public int getStopID() {
		return stopID;
	}

	
	public void setStopID(int stopID) {
		this.stopID = stopID;
	}

	
	public String getStopDescription() {
		return stopDescription;
	}

	public ArrayList<LatLng> getRoutePoints() {
		return routePoints;
	}
	
	public void setStopDescription(String stopDescription) {
		this.stopDescription = stopDescription;
	}

	
	public LatLng getLatLng() {
		return latLng;
	}

	
	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}
	
	public void fillRouteList(LatLng point){
		routePoints.add(point);
	}


	public Marker getMarker() {
		return marker;
	}


	public void setMarker(Marker marker) {
		this.marker = marker;
	}


	public String getRouteTitle() {
		return routeTitle;
	}


	public void setRouteTitle(String routeTitle) {
		this.routeTitle = routeTitle;
	}
	
	
}
