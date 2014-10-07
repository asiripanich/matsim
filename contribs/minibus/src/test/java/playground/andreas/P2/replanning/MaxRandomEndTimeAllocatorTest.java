/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2011 by the members listed in the COPYING,        *
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

package playground.andreas.P2.replanning;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.testcases.MatsimTestUtils;
import playground.andreas.P2.operator.Operator;
import playground.andreas.P2.operator.PPlan;
import playground.andreas.P2.routeProvider.PScenarioHelper;

import java.util.ArrayList;


public class MaxRandomEndTimeAllocatorTest {
	@Rule public MatsimTestUtils utils = new MatsimTestUtils();
	
	@Test
    public final void testRun() {
	
		Operator coop = PScenarioHelper.createTestCooperative(utils.getOutputDirectory());
		ArrayList<String> param = new ArrayList<>();
		param.add("0");
		param.add("1");
		param.add("false");
		MaxRandomEndTimeAllocator strat = new MaxRandomEndTimeAllocator(param);
		PPlan testPlan = null;
		
		coop.getBestPlan().setEndTime(40000.0);

		Assert.assertEquals("Compare number of vehicles", 1.0, coop.getBestPlan().getNVehicles(), MatsimTestUtils.EPSILON);
		Assert.assertEquals("Compare end time", 40000.0, coop.getBestPlan().getEndTime(), MatsimTestUtils.EPSILON);
		Assert.assertNull("Test plan should be null", testPlan);
		
		coop.getBestPlan().setNVehicles(2);
		
		// enough vehicles for testing, but mutation range 0
		testPlan = strat.run(coop);
		
		Assert.assertEquals("Compare number of vehicles", 2.0, coop.getBestPlan().getNVehicles(), MatsimTestUtils.EPSILON);
		Assert.assertEquals("Compare end time", 40000.0, coop.getBestPlan().getEndTime(), MatsimTestUtils.EPSILON);
		Assert.assertNotNull("Test plan should be not null", testPlan);
		Assert.assertEquals("There should be one vehicle bought", 1.0, testPlan.getNVehicles(), MatsimTestUtils.EPSILON);
		Assert.assertEquals("Compare end time", 40000.0, testPlan.getEndTime(), MatsimTestUtils.EPSILON);
		
		param = new ArrayList<>();
		param.add("900");
		param.add("10");
		param.add("false");
		strat = new MaxRandomEndTimeAllocator(param);
		
		// enough vehicles for testing
		testPlan = strat.run(coop);
		
		Assert.assertEquals("Compare number of vehicles", 2.0, coop.getBestPlan().getNVehicles(), MatsimTestUtils.EPSILON);
		Assert.assertEquals("Compare start time", 40000.0, coop.getBestPlan().getEndTime(), MatsimTestUtils.EPSILON);
		Assert.assertNotNull("Test plan should be not null", testPlan);
		Assert.assertEquals("There should be one vehicle bought", 1.0, testPlan.getNVehicles(), MatsimTestUtils.EPSILON);
		Assert.assertEquals("Compare end time", 40070.0, testPlan.getEndTime(), MatsimTestUtils.EPSILON);
	}
}