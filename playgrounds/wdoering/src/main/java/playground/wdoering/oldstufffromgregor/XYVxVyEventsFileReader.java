/* *********************************************************************** *
 * project: org.matsim.*
 * XYZEventsFileReader.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2010 by the members listed in the COPYING,        *
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
package playground.wdoering.oldstufffromgregor;

import java.util.Stack;

import org.matsim.api.core.v01.events.ActivityEndEvent;
import org.matsim.api.core.v01.events.ActivityStartEvent;
import org.matsim.api.core.v01.events.PersonArrivalEvent;
import org.matsim.api.core.v01.events.PersonDepartureEvent;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.basic.v01.IdImpl;
import org.matsim.core.events.EventsReaderXMLv1;
import org.matsim.core.utils.io.MatsimXmlParser;
import org.xml.sax.Attributes;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Events file reader that handles XYVxVyEvents, link leave, link enter, departure
 * and arrival events its a lot of copy and paste code from EventsReaderXMLv1
 * The XYVxVy part is to be merged into EventsReaderXMLv1 some day
 * 
 * @author laemmel
 * 
 */
public class XYVxVyEventsFileReader extends MatsimXmlParser {

	private final EventsManager events;
	private final XYVxVyEventsFactoryImpl builder;

	public XYVxVyEventsFileReader(EventsManager events) {
		this.events = events;
		this.builder = new XYVxVyEventsFactoryImpl(events.getFactory());
		setValidating(false);// events-files have no DTD, thus they cannot
		// validate
	}

	@Override
	public void startTag(final String name, final Attributes atts, final Stack<String> context) {
		if (EventsReaderXMLv1.EVENT.equals(name)) {
			startEvent(atts);
		}
	}

	/**
	 * @param atts
	 */
	private void startEvent(Attributes atts) {
		double time = Double.parseDouble(atts.getValue("time"));
		String eventType = atts.getValue("type");
		if (LinkLeaveEvent.EVENT_TYPE.equals(eventType)) {
			this.events.processEvent(this.builder.getFactory().createLinkLeaveEvent(time, new IdImpl(atts.getValue(LinkLeaveEvent.ATTRIBUTE_PERSON)), new IdImpl(atts.getValue(LinkLeaveEvent.ATTRIBUTE_LINK)), null));
		} else if (LinkEnterEvent.EVENT_TYPE.equals(eventType)) {
			this.events.processEvent(this.builder.getFactory().createLinkEnterEvent(time, new IdImpl(atts.getValue(LinkEnterEvent.ATTRIBUTE_PERSON)), new IdImpl(atts.getValue(LinkEnterEvent.ATTRIBUTE_LINK)), null));
		} else if (ActivityEndEvent.EVENT_TYPE.equals(eventType)) {
			this.events.processEvent(this.builder.getFactory().createActivityEndEvent(time, new IdImpl(atts.getValue(ActivityEndEvent.ATTRIBUTE_PERSON)), new IdImpl(atts.getValue(ActivityEndEvent.ATTRIBUTE_LINK)), atts.getValue(ActivityEndEvent.ATTRIBUTE_FACILITY) == null ? null : new IdImpl(atts.getValue(ActivityEndEvent.ATTRIBUTE_FACILITY)), atts.getValue(ActivityEndEvent.ATTRIBUTE_ACTTYPE)));
		} else if (ActivityStartEvent.EVENT_TYPE.equals(eventType)) {
			this.events.processEvent(this.builder.getFactory().createActivityStartEvent(time, new IdImpl(atts.getValue(ActivityStartEvent.ATTRIBUTE_PERSON)), new IdImpl(atts.getValue(ActivityStartEvent.ATTRIBUTE_LINK)), atts.getValue(ActivityStartEvent.ATTRIBUTE_FACILITY) == null ? null : new IdImpl(atts.getValue(ActivityStartEvent.ATTRIBUTE_FACILITY)), atts.getValue(ActivityStartEvent.ATTRIBUTE_ACTTYPE)));
		} else if (PersonArrivalEvent.EVENT_TYPE.equals(eventType)) {
			String legMode = atts.getValue(PersonArrivalEvent.ATTRIBUTE_LEGMODE);
			String mode = legMode == null ? null : legMode.intern();
			this.events.processEvent(this.builder.getFactory().createAgentArrivalEvent(time, new IdImpl(atts.getValue(PersonArrivalEvent.ATTRIBUTE_PERSON)), new IdImpl(atts.getValue(PersonArrivalEvent.ATTRIBUTE_LINK)), mode));
		} else if (PersonDepartureEvent.EVENT_TYPE.equals(eventType)) {
			String legMode = atts.getValue(PersonDepartureEvent.ATTRIBUTE_LEGMODE);
			String mode = legMode == null ? null : legMode.intern();
			this.events.processEvent(this.builder.getFactory().createAgentDepartureEvent(time, new IdImpl(atts.getValue(PersonDepartureEvent.ATTRIBUTE_PERSON)), new IdImpl(atts.getValue(PersonDepartureEvent.ATTRIBUTE_LINK)), mode));
		} else if (XYVxVyEventImpl.EVENT_TYPE.equals(eventType)) {
			String x = atts.getValue(XYVxVyEventImpl.ATTRIBUTE_X);
			String y = atts.getValue(XYVxVyEventImpl.ATTRIBUTE_Y);
			String vx = atts.getValue(XYVxVyEventImpl.ATTRIBUTE_VX);
			String vy = atts.getValue(XYVxVyEventImpl.ATTRIBUTE_VY);
			String id = atts.getValue(XYVxVyEventImpl.ATTRIBUTE_PERSON);
			Event e = this.builder.createXYZAzimuthEvent(x, y, vx,vy, id, atts.getValue("time"));
			this.events.processEvent(e);
		} else if (DoubleValueStringKeyAtCoordinateEvent.EVENT_TYPE.equals(eventType)) {
			String x = atts.getValue(DoubleValueStringKeyAtCoordinateEvent.ATTRIBUTE_CENTER_X);
			String y = atts.getValue(DoubleValueStringKeyAtCoordinateEvent.ATTRIBUTE_CENTER_Y);
			String key = atts.getValue(DoubleValueStringKeyAtCoordinateEvent.ATTRIBUTE_KEY);
			String val = atts.getValue(DoubleValueStringKeyAtCoordinateEvent.ATTRIBUTE_VALUE);
			Event e = new DoubleValueStringKeyAtCoordinateEvent(new Coordinate(Double.parseDouble(x),Double.parseDouble(y)), Double.parseDouble(val), key, time);
			this.events.processEvent(e);

		} else if (ArrowEvent.EVENT_TYPE.equals(eventType)) {
			String fromX = atts.getValue(ArrowEvent.ATTRIBUTE_FROM_X);
			String fromY = atts.getValue(ArrowEvent.ATTRIBUTE_FROM_Y);
			String toX = atts.getValue(ArrowEvent.ATTRIBUTE_TO_X);
			String toY = atts.getValue(ArrowEvent.ATTRIBUTE_TO_Y);
			String id = atts.getValue(ArrowEvent.ATTRIBUTE_PERSON);
			String lineSegType = atts.getValue(ArrowEvent.ATTRIBUTE_LINE_SEG_TYPE);
			Coordinate c1 = new Coordinate(Double.parseDouble(fromX),Double.parseDouble(fromY));
			Coordinate c2 = new Coordinate(Double.parseDouble(toX),Double.parseDouble(toY));
			IdImpl idImpl = new IdImpl(id);
			int type = Integer.parseInt(lineSegType);
			ArrowEvent e = new ArrowEvent(idImpl, c1, c2, 0.f, 0.f, 0.f, type, time);
			this.events.processEvent(e);
		} else if (TickEvent.type.equals(eventType)) {
			TickEvent tick = new TickEvent(time);
			this.events.processEvent(tick);
		}

	}

	@Override
	public void endTag(String name, String content, Stack<String> context) {

	}
}
