package com.crash.routeinfo;

import java.util.ArrayList;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class Route {
	private int RouteID;
	private String routeTitle;
	private ArrayList<Stop> stops = new ArrayList<Stop>();
	private int color;
	
	public ArrayList<LatLng> routePoints = new ArrayList<LatLng>();

	private PolylineOptions polyLine;
	private Polyline pl;
	private ArrayList<Marker> markers = new ArrayList<Marker>();
	
	
	private ArrayList<Integer> tripIDs = new ArrayList<Integer>();

	// Need to add a tripID field since everything is based off of the tripID now...


	public Route(int id){
		RouteID = id;
	}
	
	
	public Route(int id, ArrayList<Stop> pts){
		RouteID = id;
		stops = pts;
	}
	
	public Route(int id, Stop pts){
		RouteID = id;
		stops.add(pts);
	}
	
	public void getRoutes(){
		//new GetJSON().execute();
		//stops.put(key, value);
		}
	
	public void setPolyLineOptions(PolylineOptions p){
		polyLine = p;
	}
	
	public void setPolyLine(Polyline p){
		pl = p;
	}
	
	public int getRouteID() {
		return RouteID;
	}

	public void setRouteID(int routeID) {
		RouteID = routeID;
	}

	public ArrayList<Stop> getStops() {
		return stops;
	}

	public void setStops(ArrayList<Stop> routePoints) {
		this.stops = routePoints;
	}
	
	public void addStops(ArrayList<Stop> arrayList) {
		for(int i = 0; i < arrayList.size(); i++){
		stops.add(arrayList.get(i));
		}
	}
	
	public void addStop(Stop addedStop) {
		stops.add(addedStop);
		
	}


	public PolylineOptions getPolyLineOptions() {

		return polyLine;
	}
	
	public Polyline getPolyLine() {

		return pl;
	}
	
	
	
	public void printRoute() {
		//System.out.println("Route ID: " + RouteID);
		//System.out.println("Stops: ");

		for(int i = 0; i < stops.size(); i++){
			//System.out.println(stops.get(i).getStopDescription());
		}
		
	}
	
	public void printPoly(){
	}
	
	public void setAllRoutePoints(){
		for(int i = 0; i < stops.size(); i++){
			routePoints.addAll(stops.get(i).getRoutePoints());
			//System.out.println("Route Points: " + routePoints.size());

		}
		
	}

	public String getRouteTitle() {
		return routeTitle;
	}

	public void setRouteTitle(String routeTitle) {
		this.routeTitle = routeTitle;
	}
	
	public void showStops(GoogleMap map){
		for (int i = 0; i < stops.size(); i++) {
			
			Marker temp = map.addMarker(new MarkerOptions()
			.position(stops.get(i).getLatLng()).title(
					(stops.get(i).getStopDescription())));
			
			stops.get(i).setMarker(temp);
			
					
					markers.add(stops.get(i).getMarker());
					
		}
	}
	
	public void hideStops(GoogleMap map){
		
		for (int i = 0; i < markers.size(); i++) {
			markers.get(i).remove();

		}
	}


	public int getColor() {
		return color;
	}

	
	public void setColor(String color) {
		
		this.color = Color.parseColor(color);
	}


	public ArrayList<Integer> getTripIDs() {
		return tripIDs;
	}


	public void setTripIDs(ArrayList<Integer> tripIDs) {
		this.tripIDs = tripIDs;
	}
	

	public void addTripID(int tripID) {
		this.tripIDs.add(tripID);
	}
}

