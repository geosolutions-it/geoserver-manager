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

package it.geosolutions.geoserver.rest.decoder.gwc;

import it.geosolutions.geoserver.rest.Util;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.geosolutions.geoserver.rest.decoder.utils.JDOMBuilder;

import org.jdom.Element;

/**
 * Parse <TT>wmsLayer</TT>s returned as XML REST objects.
 *
 * <P>This is the XML REST representation:
 * <PRE>
 * {@code
    <wmsLayer>
      <metaInformation>
      <name>img states</name>
        <title>Nicer title for Image States</title>
        <description>This is a description. Fascinating.</description>
      </metaInformation>
      <mimeFormats>
        <string>image/gif</string>
        <string>image/jpeg</string>
        <string>image/png</string>
        <string>image/png8</string>
      </mimeFormats>
      <gridSubsets>
        <gridSubset>
          <gridSetName>EPSG:4326</gridSetName>
          <extent>
            <coords>
              <double>-129.6</double>
              <double>3.45</double>
              <double>-62.1</double>
              <double>70.9</double>
            </coords>
          </extent>
        </gridSubset>
      </gridSubsets>
      <expireCacheList>
        <expirationRule minZoom="0" expiration="60" />
      </expireCacheList>
      <expireClientsList>
        <expirationRule minZoom="0" expiration="500" />
      </expireClientsList>
      <wmsUrl>
        <string>http://demo.opengeo.org/geoserver/wms</string>
      </wmsUrl>
      <wmsLayers>nurc:Img_Sample,topp:states</wmsLayers>
      <transparent>false</transparent>
      <bgColor>0x0066FF</bgColor>
    </wmsLayer>
    This is the XML Schema representation:
    <xs:complexType name="WmsLayer">
     <xs:complexContent>
         <xs:sequence>
                   <xs:element minOccurs="0" name="blobStoreId" type="xs:string">
                   </xs:element>
                   <xs:element minOccurs="0" name="enabled" type="xs:boolean">
                   </xs:element>
                   <xs:element name="name" type="xs:string">
                   </xs:element>
                   <xs:element minOccurs="0" name="metaInformation" type="gwc:LayerMetaInformation">
                   </xs:element>
                   <xs:element minOccurs="0" name="mimeFormats" type="gwc:MimeFormats">
                   </xs:element>
                   <xs:element minOccurs="0" name="infoMimeFormats" type="gwc:MimeFormats">
                   </xs:element>
                   <xs:element minOccurs="0" name="formatModifiers" type="gwc:formatModifiers">
                   </xs:element>
                   <xs:element minOccurs="0" name="grids" type="gwc:DEPRECATEDgrids">
                   </xs:element>
                   <xs:element minOccurs="0" name="gridSubsets" type="gwc:GridSubsets">
                   </xs:element>
                   <xs:element minOccurs="0" name="updateSources" type="gwc:UpdateSources">
                   </xs:element>
                   <xs:element minOccurs="0" name="requestFilters" type="gwc:RequestFilters">
                   </xs:element>
                   <xs:element minOccurs="0" name="useETags" type="xs:boolean">
                   </xs:element>
                   <xs:element minOccurs="0" name="metaWidthHeight" type="gwc:MetaWidthHeight">
                   </xs:element>
                   <xs:element minOccurs="0" name="expireCache" type="xs:integer">
                   </xs:element>
                   <xs:element minOccurs="0" name="expireCacheList" type="gwc:ExpireList">
                   </xs:element>
                   <xs:element minOccurs="0" name="expireClients" type="xs:integer">
                   </xs:element>
                   <xs:element minOccurs="0" name="expireClientsList" type="gwc:ExpireList">
                   </xs:element>
                   <xs:element minOccurs="0" name="backendTimeout" type="xs:integer">
                   </xs:element>
                   <xs:element minOccurs="0" name="cacheBypassAllowed" type="xs:boolean">
                   </xs:element>
                   <xs:element minOccurs="0" name="queryable" type="xs:boolean">
                   </xs:element>
                   <xs:element minOccurs="0" name="wmsQueryLayers" type="xs:string">
                   </xs:element>
                   <xs:element minOccurs="0" name="parameterFilters" type="gwc:ParameterFilters">
                   </xs:element>
           <xs:element minOccurs="0" name="wmsUrl" type="gwc:WmsUrl">
           </xs:element>
           <xs:element minOccurs="0" name="wmsLayers" type="xs:string">
           </xs:element>
           <xs:element minOccurs="0" name="wmsStyles" type="xs:string">
           </xs:element>
           <xs:element minOccurs="0" name="gutter" type="xs:integer">
           </xs:element>
           <xs:element minOccurs="0" name="errorMime" type="xs:string">
           </xs:element>
           <xs:element minOccurs="0" name="wmsVersion">
             <xs:simpleType>
               <xs:restriction base="xs:string">
                 <xs:enumeration value="1.0.0"/>
                 <xs:enumeration value="1.1.0"/>
                 <xs:enumeration value="1.1.1"/>
               </xs:restriction>
             </xs:simpleType>
           </xs:element>
           <xs:element minOccurs="0" name="httpUsername" type="xs:string">
           </xs:element>
           <xs:element minOccurs="0" name="httpPassword" type="xs:string">
           </xs:element>
           <xs:element minOccurs="0" name="proxyUrl" type="xs:string"/>
           <xs:element minOccurs="0" name="tiled" type="xs:boolean">
           </xs:element>
           <xs:element minOccurs="0" name="transparent" type="xs:boolean">
           </xs:element>
           <xs:element minOccurs="0" name="bgColor" type="xs:string">
           </xs:element>
           <xs:element minOccurs="0" name="palette" type="xs:string">
           </xs:element>
           <xs:element minOccurs="0" name="vendorParameters" type="xs:string">
           </xs:element>
           <xs:element minOccurs="0" name="cachePrefix" type="xs:string">
           </xs:element>
           <xs:element minOccurs="0" name="concurrency" type="xs:positiveInteger">
           </xs:element>
         </xs:sequence>
     </xs:complexContent>
   </xs:complexType>
 * }</PRE>
 * @author Nazzareno Sileno
 */
public class GWCRESTWMSLayer {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(GWCRESTWMSLayer.class);
    
    private final Element wmsLayerElem;
    
    public enum WMSVersion {

        V1_0_0("1.0.0"),
        V1_1_0("1.1.0"),
        V1_1_1("1.1.1"),
        UNKNOWN(null);

        private final String restVersion;

        private WMSVersion(String restVersion) {
            this.restVersion = restVersion;
        }

        public static WMSVersion get(String restName) {
            for (WMSVersion value : values()) {
                if (value == UNKNOWN) {
                    continue;
                }
                if (value.restVersion.equals(restName)) {
                    return value;
                }
            }
            return UNKNOWN;
        }
    };

    public static GWCRESTWMSLayer build(String response) {
        if (response == null) {
            return null;
        }

        Element pb = JDOMBuilder.buildElement(response);
        if (pb != null) {
            return new GWCRESTWMSLayer(pb);
        } else {
            return null;
        }
    }

    public GWCRESTWMSLayer(Element wmsLayerElem) {
        this.wmsLayerElem = wmsLayerElem;
    }

    public String getBlobStoreId() {
        return wmsLayerElem.getChildText("blobStoreId");
    }

    public Boolean getEnabled() {
        return Util.getBooleanValueFromText(wmsLayerElem.getChildText("enabled"));
    }

    public String getName() {
        return wmsLayerElem.getChildText("name");
    }

    public GWCLayerMetaInformation getMetaInformation() {
        GWCLayerMetaInformation layerMetaInformation = null;
        final Element keywordsRoot = wmsLayerElem.getChild("keywords");
        if(keywordsRoot != null){
            layerMetaInformation = new GWCLayerMetaInformation(keywordsRoot);
        }
        return layerMetaInformation;
    }

    public List<String> getMimeFormats() {
        List<String> mimeFormatsList = null;
        final Element mimeFormatsRoot = wmsLayerElem.getChild("mimeFormats");
        if(mimeFormatsRoot != null){
            mimeFormatsList = Util.getElementsChildrenStringContent(mimeFormatsRoot);
        }
        return mimeFormatsList;
    }

    public List<String> getInfoMimeFormats() {
        List<String> infoMimeFormatsList = null;
        final Element infoMimeFormatsRoot = wmsLayerElem.getChild("infoMimeFormats");
        if(infoMimeFormatsRoot != null){
            infoMimeFormatsList = Util.getElementsChildrenStringContent(infoMimeFormatsRoot);
        }
        return infoMimeFormatsList;
    }

    public List<GWCFormatModifier> getFormatModifiers() {
        List<GWCFormatModifier> formatModifierList = null;
        final Element formatModifiersRoot = wmsLayerElem.getChild("formatModifiers");
        if(formatModifiersRoot != null){
            formatModifierList = new ArrayList<GWCFormatModifier>();
            for (Element listItem : (List<Element>) Util.safeList(formatModifiersRoot.getChildren())) {
                formatModifierList.add(new GWCFormatModifier(listItem));
            }
        }
        return formatModifierList;
    }

    public List<GWCGridSubset> getGridSubsets() {
        List<GWCGridSubset> gridSubsetList = null;
        final Element gridSubsetsRoot = wmsLayerElem.getChild("gridSubsets");
        if(gridSubsetsRoot != null){
            gridSubsetList = new ArrayList<GWCGridSubset>();
            for (Element listItem : (List<Element>) Util.safeList(gridSubsetsRoot.getChildren())) {
                gridSubsetList.add(new GWCGridSubset(listItem));
            }
        }
        return gridSubsetList;
    }

    public List<GWCGeoRssFeed> getUpdateSources() {
        List<GWCGeoRssFeed> updateSourceList = null;
        final Element updateSourcesRoot = wmsLayerElem.getChild("updateSources");
        if(updateSourcesRoot != null){
            updateSourceList = new ArrayList<GWCGeoRssFeed>();
            for (Element listItem : (List<Element>) Util.safeList(updateSourcesRoot.getChildren())) {
                updateSourceList.add(new GWCGeoRssFeed(listItem));
            }
        }
        return updateSourceList;
    }

    public GWCRequestFilters getRequestFilters() {
        GWCRequestFilters requestFilters = null;
        final Element requestFiltersRoot = wmsLayerElem.getChild("requestFilters");
        if(requestFiltersRoot != null){
            requestFilters = new GWCRequestFilters(requestFiltersRoot);
        }
        return requestFilters;
    }
    
    
    public Boolean getUseETags() {
        return Util.getBooleanValueFromText(wmsLayerElem.getChildText("useETags"));
    }

    public List<Integer> getMetaWidthHeight() {
        List<Integer> result = null;
        final Element metaWidthHeightRoot = wmsLayerElem.getChild("metaWidthHeight");
        if(metaWidthHeightRoot != null){
            result = new ArrayList<Integer>(2);
            for(Element listItem : (List<Element>) Util.safeList(metaWidthHeightRoot.getChildren())){
                result.add(Util.getIntValueFromText(listItem.getChildText("int")));
            }
        }
        return result;
    }
    
    public Integer getExpireCache() {
        return Util.getIntValueFromText(wmsLayerElem.getChildText("expireCache"));
    }

    public List<GWCExpirationRule> getExpireCacheList() {
        List<GWCExpirationRule> expireCacheList = null;
        final Element expireCacheListRoot = wmsLayerElem.getChild("expireCacheList");
        if(expireCacheListRoot != null){
            expireCacheList = new ArrayList<GWCExpirationRule>();
            for (Element listItem : (List<Element>) Util.safeList(expireCacheListRoot.getChildren())) {
                expireCacheList.add(new GWCExpirationRule(listItem));
            }
        }
        return expireCacheList;
    }
    
    public Integer getExpireClients() {
        return Util.getIntValueFromText(wmsLayerElem.getChildText("expireClients"));
    }
    
    public List<GWCExpirationRule> getExpireClientsList() {
        List<GWCExpirationRule> expireCacheList = null;
        final Element expireCacheListRoot = wmsLayerElem.getChild("expireClientsList");
        if(expireCacheListRoot != null){
            expireCacheList = new ArrayList<GWCExpirationRule>();
            for (Element listItem : (List<Element>) Util.safeList(expireCacheListRoot.getChildren())) {
                expireCacheList.add(new GWCExpirationRule(listItem));
            }
        }
        return expireCacheList;
    }
    
    public Integer getBackendTimeout() {
        return Util.getIntValueFromText(wmsLayerElem.getChildText("backendTimeout"));
    }
    
    public Boolean getCacheBypassAllowed() {
        return Util.getBooleanValueFromText(wmsLayerElem.getChildText("cacheBypassAllowed"));
    }

    public Boolean getQueryable() {
        return Util.getBooleanValueFromText(wmsLayerElem.getChildText("queryable"));
    }

    public String getWMSQueryLayers() {
        return wmsLayerElem.getChildText("wmsQueryLayers");
    }

    public GWCParameterFilters getParameterFilters() {
        GWCParameterFilters parameterFilters = null;
        final Element parameterFiltersRoot = wmsLayerElem.getChild("parameterFilters");
        if(parameterFiltersRoot != null){
            parameterFilters = new GWCParameterFilters(parameterFiltersRoot);
        }
        return parameterFilters;
    }

    public List<String> getWmsUrl() {
        List<String> wmsUrlList = null;
        final Element wmsUrlRoot = wmsLayerElem.getChild("wmsUrl");
        if(wmsUrlRoot != null){
            wmsUrlList = Util.getElementsChildrenStringContent(wmsUrlRoot);
        }
        return wmsUrlList;
    }

    public String getWMSLayers() {
        return wmsLayerElem.getChildText("wmsLayers");
    }

    public String getWMSStyles() {
        return wmsLayerElem.getChildText("wmsStyles");
    }
    
    public Integer getGutter() {
        return Util.getIntValueFromText(wmsLayerElem.getChildText("gutter"));
    }

    public String getErrorMime() {
        return wmsLayerElem.getChildText("errorMime");
    }
    
    public WMSVersion getWMSVersion(){
        return WMSVersion.get(wmsLayerElem.getChildText("wmsVersion"));
    }
    
    public String getHTTPUsername() {
        return wmsLayerElem.getChildText("httpUsername");
    }

    public String getHTTPPassword() {
        return wmsLayerElem.getChildText("httpPassword");
    }

    public String getProxyUrl() {
        return wmsLayerElem.getChildText("proxyUrl");
    }

    public Boolean getTiled() {
        return Util.getBooleanValueFromText(wmsLayerElem.getChildText("tiled"));
    }
    
    public Boolean getTransparent() {
        return Util.getBooleanValueFromText(wmsLayerElem.getChildText("transparent"));
    }
    
    public String getBGColor() {
        return wmsLayerElem.getChildText("bgColor");
    }

    public String getPalette() {
        return wmsLayerElem.getChildText("palette");
    }

    public String getVendorParameters() {
        return wmsLayerElem.getChildText("vendorParameters");
    }

    public String getCachePrefix() {
        return wmsLayerElem.getChildText("cachePrefix");
    }

    public String getConcurrency() {
        return wmsLayerElem.getChildText("concurrency");
    }
    
    public String getTitle() {
        Element metaInformation = wmsLayerElem.getChild("metaInformation");
        return metaInformation.getChildText("title");
    }

    public String getDescription() {
        Element metaInformation = wmsLayerElem.getChild("metaInformation");
        return metaInformation.getChildText("description");
    }

//    @Deprecated
//    public DEPRECATEDgrids getGrids() {
//        Element deprecatedGrids = wmsLayerElem.getChild("grids");
//    }
}
