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
	
	private String RouteID;		// RouteID should be a unique field
	private String routeTitle;	// Route Title will hold the name of the Route
	
	private ArrayList<Stop> stops = new ArrayList<Stop>();	// Each Route will hold 'Stops'
	private int color;			// Most GTFS data has a color associated with each route
	
	private int sched_StartPos, sched_EndPos;	// Used to work with the 'Schedule' Fragment
	
	public ArrayList<LatLng> routePoints = new ArrayList<LatLng>();	// Is used to draw the polyline

	private PolylineOptions polyLine;	// The Route's PolylineOptions
	private Polyline pl;				// The Route's Polyline
	private ArrayList<Marker> markers = new ArrayList<Marker>();	// The Markers that are displayed on the map to show Stops
	
	// Most Routes have multiple tripIDs when retrieving data from GTFS
	// The TripID's correspond to different trips along that given route, i.e Traveling different times during the day
	private ArrayList<String> tripIDs = new ArrayList<String>();	
	
	private boolean clicked = false;	// Used to tell if the route is currently 'Clicked' by the user
	private boolean navMode = false;	// Used to tell if the route is currently in 'Navigation Mode'

	// Constructor
	public Route(String id){
		RouteID = id;
	}
	
	// Constructor
	public Route(String id, ArrayList<Stop> pts){
		RouteID = id;
		stops = pts;
	}
	
	// Constructor
	public Route(String id, Stop pts){
		RouteID = id;
		stops.add(pts);
	}
	
	// Set the PolyLineOptions
	public void setPolyLineOptions(PolylineOptions p){
		polyLine = p;
	}
	
	// Set the PolyLine
	public void setPolyLine(Polyline p){
		pl = p;
	}
	
	// Get the RouteID
	public String getRouteID() {
		return RouteID;
	}

	// Set the RouteID
	public void setRouteID(String routeID) {
		RouteID = routeID;
	}

	// Get the Stops for this Route
	public ArrayList<Stop> getStops() {
		return stops;
	}

	// Set the Stops for this Route
	public void setStops(ArrayList<Stop> routePoints) {
		this.stops = routePoints;
	}
	
	// Add Stops to this Route
	public void addStops(ArrayList<Stop> arrayList) {
		for(int i = 0; i < arrayList.size(); i++){
		stops.add(arrayList.get(i));
		}
	}
	
	// Add a Stop to this Route
	public void addStop(Stop addedStop) {
		stops.add(addedStop);
		
	}

	// Get PolyLineOptions
	public PolylineOptions getPolyLineOptions() {
		return polyLine;
	}
	
	// Get PolyLine
	public Polyline getPolyLine() {
		return pl;
	}
	
	// Used to print out the stops in the route
	public void printRoute() {
		System.out.println("Stops: ");

		for(int i = 0; i < stops.size(); i++){
			System.out.println(stops.get(i).getStopDescription());
		}
		
	}
	
	// Set the 'RoutePoints' which are lat/lng PolyLine points
	public void setAllRoutePoints(){
		for(int i = 0; i < stops.size(); i++){
			routePoints.addAll(stops.get(i).getRoutePoints());
		}
		
	}

	// Get the Route Title
	public String getRouteTitle() {
		return routeTitle;
	}

	// Set the Route Title
	public void setRouteTitle(String routeTitle) {
		this.routeTitle = routeTitle;
	}
	
	// Show the Stops on the Google Map
	public void showStops(GoogleMap map){
		for (int i = 0; i < stops.size(); i++) {
			Marker temp = map.addMarker(new MarkerOptions()
			.position(stops.get(i).getLatLng()).title(
					(stops.get(i).getStopDescription())));
			
			stops.get(i).setMarker(temp);
			
					markers.add(stops.get(i).getMarker());		
		}
	}
	
	// Hide the Stops on the Google Map
	public void hideStops(GoogleMap map){
		
		for (int i = 0; i < markers.size(); i++) {
			markers.get(i).remove();
		}
	}


	// Get the Color for this Route
	public int getColor() {
		return color;
	}

	
	// Set the Color for this Route
	public void setColor(String color) {
		this.color = Color.parseColor("#"+color);
	}


	// Get the TripId's for this route
	public ArrayList<String> getTripIDs() {
		return tripIDs;
	}

	// Set the TripId's for this route
	public void setTripIDs(ArrayList<String> tripIDs) {
		this.tripIDs = tripIDs;
	}
	
	// add a TripId for this route
	public void addTripID(String tripID) {
		this.tripIDs.add(tripID);
	}


	// Determines whether this Route is currently clicked or not
	public boolean isClicked() {
		return clicked;
	}

	// Sets whether this Route is currently clicked or not
	public void setClicked(boolean clicked) {
		this.clicked = clicked;
	}
	
	// Hides the PolyLine
	public void hidePolyLine(){
		pl.setVisible(false);
	}
	
	// Shows the PolyLine
	public void showPolyLine(){
		pl.setVisible(true);
	}

	// Get's if the Route is currently in Navigation Mode
	public boolean isNavMode() {
		return navMode;
	}

	// Sets if the Route is currently in Navigation Mode
	public void setNavMode(boolean navMode) {
		this.navMode = navMode;
	}

	// Gets the Routes Starting Schedule position... (Still in the code but obsolete in this build)
	public int getSched_StartPos() {
		return sched_StartPos;
	}

	// Sets the Routes Starting Schedule position... (Still in the code but obsolete in this build)
	public void setSched_StartPos(int sched_StartPos) {
		this.sched_StartPos = sched_StartPos;
	}

	// Gets the Routes Ending Schedule position... (Still in the code but obsolete in this build)
	public int getSched_EndPos() {
		return sched_EndPos;
	}
	
	// Sets the Routes Ending Schedule position... (Still in the code but obsolete in this build)
	public void setSched_EndPos(int sched_EndPos) {
		this.sched_EndPos = sched_EndPos;
	}
}

