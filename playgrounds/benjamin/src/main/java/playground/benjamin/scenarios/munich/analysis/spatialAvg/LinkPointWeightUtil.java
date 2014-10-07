/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2014 by the members listed in the COPYING,        *
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

package playground.benjamin.scenarios.munich.analysis.spatialAvg;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


public class LinkPointWeightUtil implements LinkWeightUtil {

	private static final Logger logger = Logger.getLogger(LinkPointWeightUtil.class);
	
	double xMin;
	double xMax;
	double yMin;
	double yMax;
	int noOfXbins;
	int noOfYbins;
	
	double smoothinRadiusSquared_m;
	double area_in_smoothing_circle_sqkm;
	Collection<SimpleFeature> featuresInVisBoundary;
	CoordinateReferenceSystem targetCRS;
	
	public LinkPointWeightUtil(double xMin, double xMax, double yMin,	double yMax, int noOfXbins, int noOfYbins, double smoothingRadius_m, String visBoundaryShapeFile, CoordinateReferenceSystem targetCRS) {
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		this.noOfXbins = noOfXbins;
		this.noOfYbins = noOfYbins;
		
		this.smoothinRadiusSquared_m = smoothingRadius_m * smoothingRadius_m;
		this.area_in_smoothing_circle_sqkm = (Math.PI * smoothingRadius_m * smoothingRadius_m) / (1000. * 1000.);
		this.featuresInVisBoundary = ShapeFileReader.getAllFeatures(visBoundaryShapeFile);
		this.targetCRS = targetCRS;
	}
	

	public LinkPointWeightUtil(SpatialAveragingInputData inputData, int noOfXbins2, int noOfYbins2, double smoothingRadius_m) {
		this(inputData.getMinX(), inputData.getMaxX(), 
								inputData.getMinY(), inputData.getMaxY(), 
								noOfXbins2, noOfYbins2, 
								smoothingRadius_m, 
								inputData.getMunichShapeFile(), inputData.getTargetCRS());
	}


	@Override
	public Double getWeightFromLink(Link link, Coord cellCentroid) {
//		double linkcenterx = 0.5 * link.getFromNode().getCoord().getX() + 0.5 * link.getToNode().getCoord().getX();
//		double linkcentery = 0.5 * link.getFromNode().getCoord().getY() + 0.5 * link.getToNode().getCoord().getY();
		double linkcenterx = link.getCoord().getX();
		double linkcentery = link.getCoord().getY();
		double cellCentroidX = cellCentroid.getX();
		double cellCentroidY = cellCentroid.getY();
		return calculateWeightOfPointForCell(linkcenterx, linkcentery, cellCentroidX, cellCentroidY);
	}

	@Override
	public Double getNormalizationFactor() {
		return (1/this.area_in_smoothing_circle_sqkm);
	}
	
	private double calculateWeightOfPointForCell(double x1, double y1, double x2, double y2) {
		double distanceSquared = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
//		System.out.println("distance squared" + distanceSquared);
//		if(distanceSquared < 125000.)System.out.println("small distance");
		return Math.exp((-distanceSquared) / (smoothinRadiusSquared_m));
	}

	public boolean isInResearchArea(Coord coord) {
		Double xCoord = coord.getX();
		Double yCoord = coord.getY();
		
		if(xCoord > xMin && xCoord < xMax){
			if(yCoord > yMin && yCoord < yMax){
				return true;
			}
		}
		return false;
	}

	
}

