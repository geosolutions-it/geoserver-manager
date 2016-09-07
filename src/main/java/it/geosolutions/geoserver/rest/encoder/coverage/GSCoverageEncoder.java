/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *
 *  Copyright (C) 2007,2011 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package it.geosolutions.geoserver.rest.encoder.coverage;

import org.jdom.Element;

import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder;
import it.geosolutions.geoserver.rest.encoder.dimensions.GSCoverageDimensionEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.utils.ElementUtils;

/**
 * Creates an XML
 * 
 * @author ETj (etj at geo-solutions.it)
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 * 
 */
public class GSCoverageEncoder extends GSResourceEncoder {

    public final static String NATIVECOVERAGENAME = "nativeCoverageName";

    private final static String NATIVE_FORMAT="nativeFormat";
    private final static String SUPPORTED_FORMATS="supportedFormats";
        
    private final static String REQUEST_SRS="requestSRS";
    private final static String RESPONSE_SRS="responseSRS";
        
    private final Element supportedFormatsListEncoder = new Element(SUPPORTED_FORMATS);
    public final static String DIMENSIONS = "dimensions";
    
    final private Element dimensionsEncoder = new Element(DIMENSIONS);
    
    public GSCoverageEncoder() {
        super("coverage");
        addContent(supportedFormatsListEncoder);
    }

    /**
     * @param key
     * @param dimensionInfo
     * @deprecated Use {@link GSResourceEncoder#addMetadataDimension(String, GSDimensionInfoEncoder)} this method will be removed soon
     */
    protected void addMetadata(String key, GSDimensionInfoEncoder dimensionInfo) {
        super.addMetadata(key, dimensionInfo);
    }

    /**
     * @deprecated Use {@link GSResourceEncoder#setMetadataDimension(String, GSDimensionInfoEncoder)} this method will be removed soon
     * @param key
     * @param dimensionInfo
     */
    public void setMetadata(String key, GSDimensionInfoEncoder dimensionInfo) {
        super.setMetadata(key, dimensionInfo);
    }

    /**
     * Add the 'nativeFormat' node with a text value
     */
    public void setNativeFormat(String format) {
        set(NATIVE_FORMAT, format);
    }
	
    /**
     * Add the 'supportedFormat' node with a text value
     */
    public void addSupportedFormats(String format) {
        final Element el = new Element("string");
        el.setText(format);
        supportedFormatsListEncoder.addContent(el);
    }
    
    /**
     * Add the 'requestSRS' node with a text value
     */
    public void setRequestSRS(String srs) {
        final Element el = new Element("string");
        el.setText(srs);
        set(REQUEST_SRS, el);
    }

    /**
     * Add the 'responseSRS' node with a text value
     */
    public void setResponseSRS(String srs) {
        final Element el = new Element("string");
        el.setText(srs);
        set(RESPONSE_SRS, el);
    }
    
    /**
     * Adds a CoverageDimensionInfo to the GeoServer Resource
     * 
     * @param coverageDimensionInfo
     * 
     */
    public void addCoverageDimensionInfo(GSCoverageDimensionEncoder coverageDimensionInfo) {
        if (ElementUtils.contains(getRoot(), DIMENSIONS) == null)
            addContent(dimensionsEncoder);
        dimensionsEncoder.addContent(coverageDimensionInfo.getRoot());
    }

    /**
     * Adds quickly a CoverageDimensionInfo to the GeoServer Resource
     * 
     * @param name
     * @param description
     * @param rangeMin
     * @param rangeMax
     * @param unit
     * @param dimensionType
     */
    public void addCoverageDimensionInfo(String name, String description, String rangeMin,
            String rangeMax, String unit, String dimensionType) {
        final GSCoverageDimensionEncoder coverageDimensionEncoder = new GSCoverageDimensionEncoder(
                name, description, rangeMin, rangeMax, unit, dimensionType);
        addCoverageDimensionInfo(coverageDimensionEncoder);
    }

    /**
     * Deletes a CoverageDimensionInfo from the list using the CoverageDimension Name (CoverageDimensionInfo content)
     * 
     * @param coverageDimensionName
     * @return true if something is removed, false otherwise
     */
    public boolean delCoverageDimensionInfo(final String coverageDimensionName) {
        return (dimensionsEncoder.removeContent(GSCoverageDimensionEncoder
                .getFilterByContent(coverageDimensionName))).size() == 0 ? false : true;
    }

    /**
     * Add the 'nativeCoverageName' node with a text value from 'name'
     * 
     * 
     */
    public void addNativeCoverageName(final String nativeCoverageName) {
        add(NATIVECOVERAGENAME, nativeCoverageName);
    }

    /**
     * Set the 'nativeCoverageName' node with a text value from 'name'
     * 
     * 
     */
    public void setNativeCoverageName(final String nativeCoverageName) {
        set(NATIVECOVERAGENAME, nativeCoverageName);
    }

    /**
     * Get the nativeCoverageName
     * 
     * @return
     */
    public String getNativeCoverageName() {
        final Element nativeCoverageNameNode = ElementUtils.contains(getRoot(), NATIVECOVERAGENAME, 1);
        if (nativeCoverageNameNode != null)
            return nativeCoverageNameNode.getText();
        else
            return null;
    }

}
