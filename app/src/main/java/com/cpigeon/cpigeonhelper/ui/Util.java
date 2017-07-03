package com.cpigeon.cpigeonhelper.ui;

import android.location.Location;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;
import com.amap.api.trace.TraceLocation;

import java.util.ArrayList;
import java.util.List;

public class Util {
	/**
	 * 将AMapLocation List 转为TraceLocation list
	 * 
	 * @param list
	 * @return
	 */
	public static List<TraceLocation> parseTraceLocationList(
			List<Location> list) {
		List<TraceLocation> traceList = new ArrayList<TraceLocation>();
		if (list == null) {
			return traceList;
		}
		for (int i = 0; i < list.size(); i++) {
			TraceLocation location = new TraceLocation();
			Location amapLocation = list.get(i);
			location.setBearing(amapLocation.getBearing());
			location.setLatitude(amapLocation.getLatitude());
			location.setLongitude(amapLocation.getLongitude());
			location.setSpeed(amapLocation.getSpeed());
			location.setTime(amapLocation.getTime());
			traceList.add(location);
		}
		return traceList;
	}
	public static TraceLocation parseTraceLocation(Location amapLocation) {
		TraceLocation location = new TraceLocation();
		location.setBearing(amapLocation.getBearing());
		location.setLatitude(amapLocation.getLatitude());
		location.setLongitude(amapLocation.getLongitude());
		location.setSpeed(amapLocation.getSpeed());
		location.setTime(amapLocation.getTime());
		return  location;
	}

	/**
	 * 将AMapLocation List 转为LatLng list
	 * @param list
	 * @return
	 */
	public static List<LatLng> parseLatLngList(List<Location> list) {
		List<LatLng> traceList = new ArrayList<LatLng>();
		if (list == null) {
			return traceList;
		}
		for (int i = 0; i < list.size(); i++) {
			Location loc = list.get(i);
			double lat = loc.getLatitude();
			double lng = loc.getLongitude();
			LatLng latlng = new LatLng(lat, lng);
			traceList.add(latlng);
		}
		return traceList;
	}
	
	public static Location parseLocation(String latLonStr) {
		if (latLonStr == null || latLonStr.equals("") || latLonStr.equals("[]")) {
			return null;
		}
		String[] loc = latLonStr.split(",");
		AMapLocation location = null;
		if (loc.length == 6) {
			location = new AMapLocation(loc[2]);
			location.setProvider(loc[2]);
			location.setLatitude(Double.parseDouble(loc[0]));
			location.setLongitude(Double.parseDouble(loc[1]));
			location.setTime(Long.parseLong(loc[3]));
			location.setSpeed(Float.parseFloat(loc[4]));
			location.setBearing(Float.parseFloat(loc[5]));
		}else if(loc.length == 2){
			location = new AMapLocation("gps");
			location.setLatitude(Double.parseDouble(loc[0]));
			location.setLongitude(Double.parseDouble(loc[1]));
		}
		
		return location;
	}
	
	public static ArrayList<Location> parseLocations(String latLonStr) {
		ArrayList<Location> locations = new ArrayList<Location>();
		String[] latLonStrs = latLonStr.split(";");
		for (int i = 0; i < latLonStrs.length; i++) {
			Location location = Util.parseLocation(latLonStrs[i]);
			if (location != null) {
				locations.add(location);
			}
		}
		return locations;
	}
}
