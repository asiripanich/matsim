package playground.pieter.events;

import java.io.Serializable;
import java.util.Comparator;

import org.matsim.api.core.v01.events.Event;
import org.matsim.core.api.internal.MatsimComparator;

public class EventComparator implements Comparator<Event>, Serializable,
		MatsimComparator {



	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 * first, compare the ids of event generators. if they are the same, compare the event numbers (generated by the method passing them)
	 */
	@Override
	public int compare(Event o1, Event o2) {
		if(o1.getAttributes().get("person").equals(o2.getAttributes().get("person"))){
//			return Long.compare(Long.parseLong(o1.getAttributes().get("eventNumber")),
//					            Long.parseLong(o2.getAttributes().get("eventNumber"))
//					            );
			return Double.compare(o1.getTime(), o2.getTime());
		}else{
			return o1.getAttributes().get("person").compareTo(o2.getAttributes().get("person"));
		}
		
	}

}
