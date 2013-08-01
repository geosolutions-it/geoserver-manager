/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *  
 *  Copyright (C) 2007,2012 GeoSolutions S.A.S.
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
package it.geosolutions.geoserver.rest.encoder;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher.StoreType;
import it.geosolutions.geoserver.rest.encoder.utils.ElementUtils;
import it.geosolutions.geoserver.rest.encoder.utils.PropertyXMLEncoder;

import org.jdom.Element;

/**
 * Generic Store encoder.
 * 
 * Provides getters and setters for parameters common to all CoverageStore.
 * 
 * @author Carlo Cancellieri - GeoSolutions
 */
public abstract class GSAbstractStoreEncoder extends PropertyXMLEncoder {

    private final GeoServerRESTPublisher.StoreType type;

    protected GSAbstractStoreEncoder(GeoServerRESTPublisher.StoreType type, String storeName) {
        super(type.getType());
        this.type=type;
    }
    
    public StoreType getStoreType() {
        return this.type;
    }
    
    public void setType(String type) {
        set("type", type);
    }

    public String getType() {
        return ElementUtils.contains(getRoot(), "name").getTextTrim();
    }

    public void setName(String name) {
        ensureValidName(name);
        set("name", name);
    }

    public String getName() {
        Element e = ElementUtils.contains(getRoot(), "name");
        return e!=null?e.getTextTrim():null;
    }

    public void setDescription(String description) {
        set("description", description);
    }

    public String getDescription() {
        Element e = ElementUtils.contains(getRoot(), "description");
        return e!=null?e.getTextTrim():null;
    }

    public void setEnabled(boolean enabled) {
        set("enabled", Boolean.toString(enabled));
    }

    public boolean getEnabled() {
        Element e = ElementUtils.contains(getRoot(), "name");
        if (e!=null)
            return Boolean.parseBoolean(e.getTextTrim());
        else
            return false;
    }

    /**
     * Check name validity.
     * 
     * @param name the name
     * @throws IllegalArgumentException if name is null or empty
     */
    protected void ensureValidName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Store name cannot be null or empty");
        }
    }

    /**
     * Check type validity.
     * 
     * @param type the type.
     * @throws IllegalArgumentException if type is not {@value #TYPE}
     */
    protected void ensureValidType(String type) {
        if (!type.equals(getValidType())) {
            throw new IllegalArgumentException("The store type '" + type + "' is not valid");
        }
    }

    /**
     * The type of the implementing store.
     */
    protected abstract String getValidType();
}
