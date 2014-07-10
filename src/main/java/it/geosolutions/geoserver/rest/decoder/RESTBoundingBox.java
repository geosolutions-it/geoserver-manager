package it.geosolutions.geoserver.rest.decoder;

import org.jdom.Element;

/**
 * Parse a Boundingbox of the following structure
 * 
 * <minx>472800.0</minx>
 * <maxx>817362.0</maxx>
 * <miny>35053.40625</miny>
 * <maxy>301500.0</maxy>
 * <crs class="projected">EPSG:21781</crs>
 * 
 * @author nmandery
 */
public class RESTBoundingBox {

	protected Element bboxElem;
	
	public RESTBoundingBox(Element bboxElem) {
		this.bboxElem = bboxElem;
	}
	
	public String getCRS() {
		return this.bboxElem.getChildText("crs");
	}
	
	protected double getEdge(String edge) {
		return Double.parseDouble(this.bboxElem.getChildText(edge));
	}

	public double getMinX() {
		return this.getEdge("minx");
	}
	
	public double getMaxX() {
		return this.getEdge("maxx");
	}

	public double getMinY() {
		return this.getEdge("miny");
	}

	public double getMaxY() {
		return this.getEdge("maxy");
	}

}
