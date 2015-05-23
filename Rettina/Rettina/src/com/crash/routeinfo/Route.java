package com.crash.routeinfo;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class Route {
	private int RouteID;
	private String routeTitle;
	private ArrayList<Stop> stops = new ArrayList<Stop>();
	public ArrayList<LatLng> routePoints = new ArrayList<LatLng>();

	private PolylineOptions polyLine;

	
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
	
	public void setPolyLine(PolylineOptions p){
		polyLine = p;
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


	public PolylineOptions getPolyLine() {

		return polyLine;
	}
	
	public void printRoute() {
		//System.out.println("Route ID: " + RouteID);
		//System.out.println("Stops: ");

		for(int i = 0; i < stops.size(); i++){
			//System.out.println(stops.get(i).getStopDescription());
		}
		
	}
	
	public void printPoly(){
		//System.out.println("Poly size: " + polyLine.getPoints().size());
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
}

