package com.crash.customlist;

import java.util.ArrayList;
import com.crash.routeinfo.Stop;
import com.google.android.gms.maps.model.PolylineOptions;

public class ListModel {

	private String routeName;
	private PolylineOptions polyLine;
	private ArrayList<Stop> stops;
	
	
	public String getRouteName() {
		return routeName;
	}
	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}
	public PolylineOptions getPolyLine() {
		return polyLine;
	}
	public void setPolyLine(PolylineOptions polyLine) {
		this.polyLine = polyLine;
	}
	public ArrayList<Stop> getStops() {
		return stops;
	}
	public void setStops(ArrayList<Stop> stops) {
		this.stops = stops;
	}
	
}
