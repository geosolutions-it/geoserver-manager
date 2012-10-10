/*
 *  GeoBatch - Open Source geospatial batch processing system
 *  https://github.com/nfms4redd/nfms-geobatch
 *  Copyright (C) 2007-2012 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 *  GPLv3 + Classpath exception
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.geosolutions.geoserver.rest.decoder;

import org.jdom.Element;

/**
 * @author DamianoG
 *
 */
public class RESTDimensionInfo extends RESTMetadataList.RESTMetadataElement{
    
    private boolean enabled;
    private String presentation;
    private String resolution;
    
    public static final String TIME = "time";
    public static final String ELEVATION = "elevation";
    
    /**
     * @param elem
     */
    public RESTDimensionInfo(Element elem) {
        super(elem);
        if(elem.getChild("dimensionInfo")!=null){
            enabled = Boolean.parseBoolean(elem.getChild("dimensionInfo").getChildText("enabled"));
            presentation = elem.getChild("dimensionInfo").getChildText("presentation");
            resolution = elem.getChild("dimensionInfo").getChildText("resolution");
        }
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the presentation
     */
    public String getPresentation() {
        return presentation;
    }

    /**
     * @param presentation the presentation to set
     */
    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    /**
     * @return the resolution
     */
    public String getResolution() {
        return resolution;
    }

    /**
     * @param resolution the resolution to set
     */
    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
    
    

}
