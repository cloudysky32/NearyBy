package com.skynet.nearyby.async;

import java.util.List;

import com.skynet.nearyby.model.Place;

public interface AsyncPlaceListener {
	public void doStuff(List<Place> placeList);
}
