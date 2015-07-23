/*
 * Rettina - 2015
 * Mitchell Thornton
 * Professor Konduri
 * University of Connecticut
 */

/*
 * Route is used as a data type to handle all Routes.  It contains all the necessary information to draw the route on the map and
 * hold all the stops
 */


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
//	private int RouteID;
	
	private String RouteID;
	
	private String routeTitle;
	private ArrayList<Stop> stops = new ArrayList<Stop>();
	private int color;
	private int sched_StartPos, sched_EndPos;
	
	public ArrayList<LatLng> routePoints = new ArrayList<LatLng>();

	private PolylineOptions polyLine;
	private Polyline pl;
	private ArrayList<Marker> markers = new ArrayList<Marker>();
	private ArrayList<String> tripIDs = new ArrayList<String>();
	
	private boolean clicked = false;
	private boolean navMode = false;

	// Need to add a tripID field since everything is based off of the tripID now...


	public Route(String id){
		RouteID = id;
	}
	
	
	public Route(String id, ArrayList<Stop> pts){
		RouteID = id;
		stops = pts;
	}
	
	public Route(String id, Stop pts){
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
	
	public String getRouteID() {
		return RouteID;
	}

	public void setRouteID(String routeID) {
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
		System.out.println("Stops: ");

		for(int i = 0; i < stops.size(); i++){
			System.out.println(stops.get(i).getStopDescription());
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
		
		this.color = Color.parseColor("#"+color);
	}


	public ArrayList<String> getTripIDs() {
		return tripIDs;
	}


	public void setTripIDs(ArrayList<String> tripIDs) {
		this.tripIDs = tripIDs;
	}
	

	public void addTripID(String tripID) {
		this.tripIDs.add(tripID);
	}


	public boolean isClicked() {
		return clicked;
	}


	public void setClicked(boolean clicked) {
		this.clicked = clicked;
	}
	
	public void hidePolyLine(){
		
		System.out.println("Hiding Route: " + getRouteTitle());

		//pl.remove();
		pl.setVisible(false);

		
		//polyLine.visible(false);
		System.out.println("IS POLY VISIBLE: " + polyLine.isVisible());
		
		
		//pl.remove();
	}
	
	public void showPolyLine(){
		pl.setVisible(true);
	}


	public boolean isNavMode() {
		return navMode;
	}


	public void setNavMode(boolean navMode) {
		this.navMode = navMode;
	}


	public int getSched_StartPos() {
		return sched_StartPos;
	}


	public void setSched_StartPos(int sched_StartPos) {
		this.sched_StartPos = sched_StartPos;
	}


	public int getSched_EndPos() {
		return sched_EndPos;
	}


	public void setSched_EndPos(int sched_EndPos) {
		this.sched_EndPos = sched_EndPos;
	}
}

