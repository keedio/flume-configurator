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
public class FlumeTopologyConnection {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FlumeTopologyConnection.class);

    private String type;
    private String id;
    private String x;
    private String y;
    private String width;
    private String height;
    private Map<String, String> userData;
    private String cssClass;
    private String bgColor;
    private String color;
    private String stroke;
    private String alpha;
    private String radius;
    private FlumeTopologyNodeConnection sourceConnection;
    private FlumeTopologyNodeConnection targetConnection;


    public FlumeTopologyConnection(FlumeTopology flumeTopology, FlumeTopologyNodeConnection sourceConnection, FlumeTopologyNodeConnection targetConnection) {

        this.type = flumeTopology.getType();
        this.id = flumeTopology.getId();
        this.x = flumeTopology.getX();
        this.y = flumeTopology.getY();
        this.width = flumeTopology.getWidth();
        this.height = flumeTopology.getHeight();
        this.userData = flumeTopology.getData();
        this.cssClass = flumeTopology.getCssClass();
        this.bgColor = flumeTopology.getBgColor();
        this.color = flumeTopology.getColor();
        this.stroke = flumeTopology.getStroke();
        this.alpha = flumeTopology.getAlpha();
        this.radius = flumeTopology.getRadius();
        this.sourceConnection = sourceConnection;
        this.targetConnection = targetConnection;
    }

    public FlumeTopologyConnection(FlumeTopology flumeTopology) {

        this.type = flumeTopology.getType();
        this.id = flumeTopology.getId();
        this.x = flumeTopology.getX();
        this.y = flumeTopology.getY();
        this.width = flumeTopology.getWidth();
        this.height = flumeTopology.getHeight();
        this.userData = flumeTopology.getData();
        this.cssClass = flumeTopology.getCssClass();
        this.bgColor = flumeTopology.getBgColor();
        this.color = flumeTopology.getColor();
        this.stroke = flumeTopology.getStroke();
        this.alpha = flumeTopology.getAlpha();
        this.radius = flumeTopology.getRadius();

        if (flumeTopology.getSourceConnection() != null && !flumeTopology.getSourceConnection().isEmpty()) {
            FlumeTopologyNodeConnection flumeTopologySourceConnection = new FlumeTopologyNodeConnection(flumeTopology.getSourceConnection(), FlumeConfiguratorConstants.OUTPUT_PORT);
            this.sourceConnection = flumeTopologySourceConnection;
        }

        if (flumeTopology.getTargetConnection() != null && !flumeTopology.getTargetConnection().isEmpty()) {
            FlumeTopologyNodeConnection flumeTopologyTargetConnection = new FlumeTopologyNodeConnection(flumeTopology.getTargetConnection(), FlumeConfiguratorConstants.INTPUT_PORT);
            this.targetConnection = flumeTopologyTargetConnection;
        }
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

    public Map<String, String> getUserData() {
        return userData;
    }

    public void setUserData(Map<String, String> userData) {
        this.userData = userData;
    }


    @JsonProperty("source")
    public FlumeTopologyNodeConnection getSourceConnection() {
        return sourceConnection;
    }

    public void setSourceConnection(FlumeTopologyNodeConnection sourceConnection) {
        this.sourceConnection = sourceConnection;
    }

    @JsonProperty("target")
    public FlumeTopologyNodeConnection getTargetConnection() {
        return targetConnection;
    }

    public void setTargetConnection(FlumeTopologyNodeConnection targetConnection) {
        this.targetConnection = targetConnection;
    }


    public String toString() {
        String topologyName =  this.getUserData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

        return "\"[" + this.getType().toUpperCase() + "]\\n" + topologyName + "\\n (" + id + ")" + "\"" ;
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
