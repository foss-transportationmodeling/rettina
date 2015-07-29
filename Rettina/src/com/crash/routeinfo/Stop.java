/*
 * Rettina - 2015
 * Mitchell Thornton
 * Professor Konduri
 * University of Connecticut
 */

/*
 * Stop acts as a data that holds all the necessary information for each stop such as stop description, ID, latLng, etc.  Routes are
 * composed of Stops. 
 */

package com.crash.routeinfo;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class Stop {
	
	// Setting up the variables that this class will use
	private String routeID;
	private String stopID;
	private String stopDescription;
	private LatLng latLng;
	private ArrayList<LatLng> routePoints = new ArrayList<LatLng>();
	private String routeTitle;
	private String arrival_time;
	private String departure_time;


	// Constructor for setting up the Stop class
	
	private Marker marker;
	
	public Stop(String _routeID, String _stopID, String _stopDescription, LatLng _latLng){
		routeID = _routeID;
		stopID = _stopID;
		stopDescription = _stopDescription;
		latLng = _latLng;
	}

	
	public String getRouteID() {
		return routeID;
	}

	
	public void setRouteID(String routeID) {
		this.routeID = routeID;
	}

	
	public String getStopID() {
		return stopID;
	}

	
	public void setStopID(String stopID) {
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


	public String getDeparture_time() {
		return departure_time;
	}


	public void setDeparture_time(String departure_time) {
		this.departure_time = departure_time.substring(0, 5);
	}


	public String getArrival_time() {
		return arrival_time;
	}


	public void setArrival_time(String arrival_time) {
		this.arrival_time = arrival_time.substring(0, 5);
	}
	
	
}
