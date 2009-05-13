/* *********************************************************************** *
 * project: org.matsim.*
 * EventsToScore.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.locationchoice.facilityload;

import java.util.Iterator;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.matsim.api.basic.v01.Id;
import org.matsim.core.api.facilities.ActivityOption;
import org.matsim.core.api.facilities.ActivityFacilities;
import org.matsim.core.api.facilities.ActivityFacility;
import org.matsim.core.events.ActEndEvent;
import org.matsim.core.events.ActStartEvent;
import org.matsim.core.events.handler.ActEndEventHandler;
import org.matsim.core.events.handler.ActStartEventHandler;

/*
 * @author anhorni
 * Uses FacilityPenalty to manage the facililities' loads by taking care of activity start and end events.
 */

public class EventsToFacilityLoad implements ActStartEventHandler, ActEndEventHandler {

	private TreeMap<Id, FacilityPenalty> facilityPenalties;
	private final static Logger log = Logger.getLogger(EventsToFacilityLoad.class);

	public EventsToFacilityLoad(final ActivityFacilities facilities, int scaleNumberOfPersons,
			TreeMap<Id, FacilityPenalty> facilityPenalties) {
		super();
		
		this.facilityPenalties = facilityPenalties;
		
		log.info("facilities size: " + facilities.getFacilities().values().size());
		Iterator<? extends ActivityFacility> iter_fac = facilities.getFacilities().values().iterator();
		while (iter_fac.hasNext()){
			ActivityFacility f = iter_fac.next();
			
			double capacity = Double.MAX_VALUE;
			Iterator<? extends ActivityOption> iter_act = f.getActivityOptions().values().iterator();
			while (iter_act.hasNext()){
				ActivityOption act = iter_act.next();
				if (act.getCapacity() < capacity) {
					capacity = act.getCapacity();
				}
			}			
			this.facilityPenalties.put(f.getId(), new FacilityPenalty(capacity, scaleNumberOfPersons));
		}
	}
	
	/*
	 * Add an arrival event in "FacilityLoad" for every start of an activity
	 * Home activities are excluded.
	 */
	public void handleEvent(final ActStartEvent event) {
		ActivityFacility facility = event.getAct().getFacility();
		if (!(event.getAct().getType().startsWith("h") || event.getAct().getType().startsWith("tta"))) {
			this.facilityPenalties.get(facility.getId()).getFacilityLoad().addArrival(event.getTime());
		}
	}

	/*
	 * Add a departure event in "FacilityLoad" for every ending of an activity
	 * Home activities are excluded
	 */
	public void handleEvent(final ActEndEvent event) {
		ActivityFacility facility = event.getAct().getFacility();
		if (!(event.getAct().getType().startsWith("h") || event.getAct().getType().startsWith("tta"))) {
			this.facilityPenalties.get(facility.getId()).getFacilityLoad().addDeparture(event.getTime());
		}
	}

	public void finish() {
		Iterator<? extends FacilityPenalty> iter_fp = this.facilityPenalties.values().iterator();
		while (iter_fp.hasNext()){
			FacilityPenalty fp = iter_fp.next();
			fp.finish();
		}
		log.info("EventsToFacilityLoad finished");
	}


	public void reset(final int iteration) {
		log.info("Not really resetting anything here."); 
	}
	
	public void resetAll(final int iteration) {
		Iterator<? extends FacilityPenalty> iter_fp = this.facilityPenalties.values().iterator();
		while (iter_fp.hasNext()){
			FacilityPenalty fp = iter_fp.next();
			fp.reset();
		}
		log.info("EventsToFacilityLoad resetted");
	}

	public TreeMap<Id, FacilityPenalty> getFacilityPenalties() {
		return facilityPenalties;
	}

	public void setFacilityPenalties(TreeMap<Id, FacilityPenalty> facilityPenalties) {
		this.facilityPenalties = facilityPenalties;
	}
}
