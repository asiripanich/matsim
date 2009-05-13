package playground.anhorni.locationchoice.preprocess.planmodifications;

import org.matsim.core.api.facilities.ActivityFacilities;
import org.matsim.core.api.population.Population;
import org.matsim.core.network.NetworkLayer;

public abstract class Modifier {

	protected Population plans=null;
	protected NetworkLayer network=null;
	protected ActivityFacilities  facilities=null;


	public Modifier(Population plans, NetworkLayer network, ActivityFacilities  facilities) {
		this.plans=plans;
		this.network=network;
		this.facilities=facilities;
	}

	public abstract void modify();

}
