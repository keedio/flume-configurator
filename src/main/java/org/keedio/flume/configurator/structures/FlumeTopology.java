package org.keedio.flume.configurator.structures;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.keedio.flume.configurator.constants.FlumeConfiguratorConstants;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlumeTopology implements Comparable {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FlumeTopology.class);

    private String type;
    private String id;
    private String x;
    private String y;
    private String width;
    private String height;
    private Map<String, String> data;
    private String cssClass;
    private String bgColor;
    private String color;
    private String stroke;
    private String alpha;
    private String radius;
    private String sourceConnection;
    private String targetConnection;
    private static ArrayList<String> orderTypeArrayList;

    static {

        orderTypeArrayList = new ArrayList<String>();
        orderTypeArrayList.add(FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT);
        orderTypeArrayList.add(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE);
        orderTypeArrayList.add(FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR);
        orderTypeArrayList.add(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL);
        orderTypeArrayList.add(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK);
        orderTypeArrayList.add(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION);

    }


    @JsonCreator
    public FlumeTopology(@JsonProperty("type") String type,
                         @JsonProperty("id") String id,
                         @JsonProperty("x") String x,
                         @JsonProperty("y") String y,
                         @JsonProperty("width") String width,
                         @JsonProperty("height") String height,
                         @JsonProperty("data") Map<String, String> data,
                         @JsonProperty("cssClass") String cssClass,
                         @JsonProperty("bgColor") String bgColor,
                         @JsonProperty("color") String color,
                         @JsonProperty("stroke") String stroke,
                         @JsonProperty("alpha") String alpha,
                         @JsonProperty("radius") String radius,
                         @JsonProperty("source") String sourceConnection,
                         @JsonProperty("target") String targetConnection) {
        this.type = type;
        this.id = id;
        this.x = x;
        this.y = y;
        if (data == null) {
            data = new LinkedHashMap<String, String>();
        } else {
            this.data = data;
        }
        this.sourceConnection = sourceConnection;
        this.targetConnection = targetConnection;
    }

    public FlumeTopology() {

        data = new LinkedHashMap<String, String>();
    }


    public FlumeTopology(String type, String id) {

        this.type = type;
        this.id = id;
        data = new LinkedHashMap<String, String>();
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }


    @JsonProperty("source")
    public String getSourceConnection() {
        return sourceConnection;
    }

    public void setSourceConnection(String sourceConnection) {
        this.sourceConnection = sourceConnection;
    }

    @JsonProperty("target")
    public String getTargetConnection() {
        return targetConnection;
    }

    public void setTargetConnection(String targetConnection) {
        this.targetConnection = targetConnection;
    }

/*
    public String toString() {
        StringBuilder sb = new StringBuilder(1000);
        sb.append("type: ").append(type).append("\n");
        sb.append("id: ").append(id).append("\n");
        for (String keyProperty : data.keySet()) {
            String propertyValue = data.get(keyProperty);
            sb.append("\t").append(keyProperty).append("=").append(propertyValue).append("\n");
        }
        sb.append("sourceConnection: ").append(sourceConnection).append("\n");
        sb.append("targetConnection: ").append(targetConnection).append("\n");

        return sb.toString();
    }
*/

    public String toString() {
        String topologyName =  this.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

        return "\"[" + this.getType().toUpperCase() + "]\\n" + topologyName + "\\n (" + id + ")" + "\"" ;
    }


    @JsonIgnore
    public String getAgentName() {
        String agentName = null;

        if (this != null) {
            String flumeTopologyElementType = this.getType();
            if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE.equals(flumeTopologyElementType)) {
                Map<String, String> data = this.getData();
                if (data != null) {
                    if (data.containsKey(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_AGENT_NAME)) {
                        agentName = data.get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_AGENT_NAME).toLowerCase();
                    }
                }
            } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT.equals(flumeTopologyElementType)) {
                Map<String, String> data = this.getData();
                if (data != null) {
                    agentName = data.get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME).toLowerCase();
                }
            }
        }

        return agentName;
    }

    public int compareTo(Object anotherFlumeTopology) {

        FlumeTopology theAnotherFlumeTopology = (FlumeTopology) anotherFlumeTopology;

        String topologyType = this.getType();
        String anotherTopolotyType = theAnotherFlumeTopology.getType();
        String elementName = this.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);
        String anotherElementName = theAnotherFlumeTopology.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);


        if (topologyType.equals(anotherTopolotyType)) {
            return elementName.compareTo(anotherElementName);
        } else {
            int indexTopologyType = orderTypeArrayList.indexOf(topologyType);
            int indexAnotherTopolotyType = orderTypeArrayList.indexOf(anotherTopolotyType);

            return  indexTopologyType - indexAnotherTopolotyType;
        }
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getStroke() {
        return stroke;
    }

    public void setStroke(String stroke) {
        this.stroke = stroke;
    }

    public String getAlpha() {
        return alpha;
    }

    public void setAlpha(String alpha) {
        this.alpha = alpha;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }
}
