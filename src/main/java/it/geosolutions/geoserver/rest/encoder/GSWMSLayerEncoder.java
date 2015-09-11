package it.geosolutions.geoserver.rest.encoder;

import it.geosolutions.geoserver.rest.encoder.feature.FeatureTypeAttribute;
import it.geosolutions.geoserver.rest.encoder.feature.GSAttributeEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSFeatureDimensionInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.virtualtable.GSVirtualTableEncoder;
import org.jdom.Element;

/**
 * Created by drakiko on 02/09/2015.
 */
public class GSWMSLayerEncoder extends GSResourceEncoder {

    public final static String ATTRIBUTES = "attributes";

    final private Element attributes = new Element(ATTRIBUTES);

    public GSWMSLayerEncoder() {
        super("wmsLayer");
    }


    /**
     * @deprecated Use {@link GSResourceEncoder#addMetadataDimension(String, GSDimensionInfoEncoder)} this method will be removed soon
     * @param key
     * @param dimensionInfo
     *
     */
    protected void addMetadata(String key, GSFeatureDimensionInfoEncoder dimensionInfo) {
        super.addMetadata(key, dimensionInfo);
    }


    /**
     * @deprecated Use {@link GSResourceEncoder#setMetadataDimension(String, GSDimensionInfoEncoder)} this method will be removed soon
     * @param key
     * @param dimensionInfo
     *
     */
    public void setMetadata(String key, GSFeatureDimensionInfoEncoder dimensionInfo) {
        super.setMetadata(key, dimensionInfo);
    }

    /**
     * Add a VirtualTable (SQL View feature type)
     *
     * @param virtualtable
     */
    protected void addMetadataVirtualTable(
            final GSVirtualTableEncoder virtualtable) {
        super.addMetadata("JDBC_VIRTUAL_TABLE", virtualtable);
    }

    /**
     * Set a VirtualTable (SQL View feature type)
     *
     * @param virtualtable
     */
    public void setMetadataVirtualTable(final GSVirtualTableEncoder virtualtable) {
        super.setMetadata("JDBC_VIRTUAL_TABLE", virtualtable);
    }

    /**
     * Deletes the VirtualTable metadata
     *
     * @return true if deleted, false otherwise
     */
    public boolean delMetadataVirtualTable(){
        return super.delMetadata("JDB_VIRTUAL_TABLE");
    }

    /**
     * delete a keyword from the list
     *
     * @param keyword
     * @return true if something is removed, false otherwise
     */
    public boolean delAttribute(final String keyword) {
        final Element el = new Element("string");
        el.setText(keyword);
        return (attributes.removeContent(GSAttributeEncoder.getFilterByName(keyword))).size() == 0 ? false
                : true;
    }

    /**
     * @param attribute the attribute to add
     */
    protected void addAttribute(GSAttributeEncoder attribute) {
        attributes.addContent(attribute.getRoot());
    }

    /**
     * @param attribute the attribute to set (overriding an attribute with the same name if present)
     */
    public void setAttribute(GSAttributeEncoder attribute) {
        delAttribute(attribute.getAttribute(FeatureTypeAttribute.name));
        addAttribute(attribute);
    }



}
