package it.geosolutions.geoserver.rest.encoder.service;

import it.geosolutions.geoserver.rest.encoder.utils.PropertyXMLEncoder;
import org.jdom.Element;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nijhur
 * Date: 9-10-14
 * Time: 8:46
 */
public class GSServiceSettingsEncoder extends PropertyXMLEncoder {

    public final static String WORKSPACESTRING = "workspace";
    public final static String NAMESTRING = "name";
    public final static String KEYWORDS = "keywords";
    public final static String METADATA = "metadata";


    private final static String MAINTAINER = "http://www.geonovum.nl/dossiers/pdok";
	private final static String SCHEMABASEURL = "http://schemas.opengis.net";

    final private Element workspaceElement = new Element(WORKSPACESTRING);
    final private Element nameElement = new Element(NAMESTRING);
    final private Element keywordsElement = new Element(KEYWORDS);
    final private Element metadata = new Element(METADATA);



    public GSServiceSettingsEncoder(String type, String serviceMetadataId) {
        super(type.toLowerCase());
        addEnabled();
        addContent(workspaceElement);
        nameElement.setText(type.toUpperCase());
        addContent(nameElement);
        addContent(keywordsElement);
        addContent(metadata);
        if ( "WMS".equals(type.toUpperCase())){
            addWmsMetadata(serviceMetadataId);
            addWmsDefaultFields();

        } else if ("WFS".equals(type.toUpperCase())){
            addWfsMetadata(serviceMetadataId);
            addWfsDefaultFields();
        }
    }

    public void addWfsGmlSettings(){
        String[][] data = new String[][] { { "V_11", "URN", "false" }, {"V_20", "URN2", "false"}, {"V_10", "XML", "true"} };
        Element root = new Element("gml");
        for(String[] entryData : data ){
            Element entry = new Element("entry");

            Element version = new Element("version");
            version.setText(entryData[0]);
            entry.addContent(version);

            Element gml = new Element("gml");

            Element style = new Element("srsNameStyle");
            style.setText(entryData[1]);
            gml.addContent(style);

            Element override = new Element("overrideGMLAttributes");
            override.setText(entryData[2]);
            gml.addContent(override);

            entry.addContent(gml);

            root.addContent(entry);
        }

        addContent(root);

    }


    private void addWmsMetadata(String wmsMetadataId){
        String ENTRY = "entry";
        String KEY = "key";
        metadata.addContent( createElementWithAttribute(ENTRY, "20", KEY, "jpegCompression"));
        metadata.addContent( createElementWithAttribute(ENTRY, "100", KEY, "pngCompression"));
        metadata.addContent( createElementWithAttribute(ENTRY, "auto", KEY, "kmlSuperoverlayMode"));
        metadata.addContent( createElementWithAttribute(ENTRY, "1000", KEY, "framesDelay"));
        metadata.addContent( createElementWithAttribute(ENTRY, "refresh", KEY, "kmlReflectorMode"));
        metadata.addContent( createElementWithAttribute(ENTRY, "", KEY, "inspire.spatialDatasetIdentifier"));
        metadata.addContent( createElementWithAttribute(ENTRY, "dut", KEY, "inspire.language"));
        metadata.addContent( createElementWithAttribute(ENTRY, "40", KEY, "kmlKmscore"));
        metadata.addContent( createElementWithAttribute(ENTRY, "true", KEY, "svgAntiAlias"));
        metadata.addContent( createElementWithAttribute(ENTRY, "http://www.nationaalgeoregister.nl/geonetwork/srv/eng/csw?service=CSW&version=2.0.2&request=GetRecordById&outputschema=http://www.isotc211.org/2005/gmd&elementsetname=full&id=" + wmsMetadataId, KEY, "inspire.metadataURL"));
        metadata.addContent( createElementWithAttribute(ENTRY, "false", KEY, "kmlPlacemark"));
        metadata.addContent( createElementWithAttribute(ENTRY, "true", KEY, "kmlAttr"));
        metadata.addContent( createElementWithAttribute(ENTRY, "2147483647", KEY, "maxAllowedFrames"));
        metadata.addContent( createElementWithAttribute(ENTRY, "[{'name': 'PDOK', 'href' : 'http://www.pdok.nl", KEY, "authorityURLs'"));
        metadata.addContent( createElementWithAttribute(ENTRY, "application/vnd.ogc.csw.GetRecordByIdResponse_xml", KEY, "inspire.metadataURLType"));
        metadata.addContent( createElementWithAttribute(ENTRY, "false", KEY, "loopContinuously"));
        metadata.addContent( createElementWithAttribute(ENTRY, "Batik", KEY, "svgRenderer"));
    }

    private void addWfsMetadata(String wfsMetadataId){
        String ENTRY = "entry";
        String KEY = "key";
        metadata.addContent( createElementWithAttribute(ENTRY, "false", KEY, "SHAPE-ZIP_DEFAULT_PRJ_IS_ESRI"));
        metadata.addContent( createElementWithAttribute(ENTRY, wfsMetadataId, KEY, "inspire.spatialDatasetIdentifier"));
        metadata.addContent( createElementWithAttribute(ENTRY, "application/vnd.ogc.csw.GetRecordByIdResponse_xml", KEY, "inspire.metadataURLType"));
        metadata.addContent( createElementWithAttribute(ENTRY, "http://www.nationaalgeoregister.nl/geonetwork/srv/eng/csw?service=CSW&version=2.0.2&request=GetRecordById&outputschema=http://www.isotc211.org/2005/gmd&elementsetname=full&id=" + wfsMetadataId, KEY, "inspire.metadataURL"));
    }

    private void addWmsDefaultFields(){
        add("maintainer", MAINTAINER);
        add("schemaBaseURL", "http://schemas.opengis.net");
        add("bboxForEachCRS", "true");
        //addWatermark();
        add("maxBuffer", "25");
        add("maxRequestMemory", "270336");
        add("maxRenderingTime", "60");
        add("maxRenderingErrors", "1000");
    }

    private void addWfsDefaultFields(){
		add("schemaBaseURL", SCHEMABASEURL);
        add("serviceLevel", "BASIC");
        add("maxFeatures", "15000");
        add("featureBounding", "false");
        add("canonicalSchemaLocation", "false");
        add("encodeFeatureMember", "false");
        addWfsGmlSettings();
    }

    public void setWorkspace(String workspaceName){
        final Element name = new Element(NAMESTRING);
        name.setText(workspaceName);
        workspaceElement.addContent(name);
    }

    private void addWatermark(){
        Element watermark = new Element("watermark");

        Element enabled = new Element("enabled");
        enabled.setText("false");
        watermark.addContent(enabled);

        Element position = new Element("position");
        enabled.setText("BOT_RIGHT");
        watermark.addContent(position);

        Element transparency = new Element("transparency");
        enabled.setText("0");
        watermark.addContent(transparency);

        addContent(watermark);
    }

    protected void addEnabled(){
        add("enabled","true");
    }

    public void setTitle(String title){
        add("title", title);
    }

    public void setAbstract(String abstractText){
        add("abstrct", abstractText);
    }

    public void setAccessConstraints(String accessConstraints){
        add("accessConstraints", accessConstraints);
    }

    public void setFees(String fees){
        add("fees", fees);
    }

    public void setSrs(List<String> values){
        Element srs = new Element("srs");
        for (String value : values){
            Element valueElement = new Element("string");
            valueElement.setText(value);
            srs.addContent(valueElement);
        }
        addContent(srs);
    }

    public void addKeyword(String keyword){
        Element valueElement = new Element("string");
        valueElement.setText(keyword);
        keywordsElement.addContent(valueElement);
    }

}
