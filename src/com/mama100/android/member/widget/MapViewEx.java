package com.mama100.android.member.widget;



import android.content.Context;
import android.util.AttributeSet;

import com.baidu.mapapi.MapView;

public class MapViewEx extends MapView {

	public MapViewEx(Context arg0) {
		super(arg0);
	}
	
	public MapViewEx(Context arg0, AttributeSet arg1) {
		super(arg0, arg1);
	}
	
	public MapViewEx(Context arg0, AttributeSet arg1, int arg2) {
		super(arg0, arg1, arg2);
	}

	public float metersToPixels(float meters){
		return getProjection().metersToEquatorPixels(meters);
	}
	
	public float pixelsToMeters(float pixels){
		return 1/(getProjection().metersToEquatorPixels(pixels));
	}
	
}
