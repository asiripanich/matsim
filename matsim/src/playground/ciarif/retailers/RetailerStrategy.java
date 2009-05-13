package playground.ciarif.retailers;

import java.util.Map;

import org.matsim.api.basic.v01.Id;
import org.matsim.core.api.facilities.ActivityFacility;

public interface RetailerStrategy {
	
	public void moveFacilities(Map<Id, ActivityFacility> facilities);
	public void moveRetailersFacilities(Map<Id, FacilityRetailersImpl> facilities);
}
