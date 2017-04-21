package org.keedio.flume.configurator.structures;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.keedio.flume.configurator.constants.FlumeConfiguratorConstants;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class FlumeTopology implements Comparable {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FlumeTopology.class);

    private String type;
    private String id;
    private Map<String, String> data;
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
                         @JsonProperty("data") Map<String, String> data,
                         @JsonProperty("source") String sourceConnection,
                         @JsonProperty("target") String targetConnection) {
        this.type = type;
        this.id = id;
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

    public String getSourceConnection() {
        return sourceConnection;
    }

    public void setSourceConnection(String sourceConnection) {
        this.sourceConnection = sourceConnection;
    }

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

        return "\"" + topologyName + " (" + id + ")" + "\"" ;
    }


    public String getAgentName() {
        String agentName = null;

        if (this != null) {
            String flumeTopologyElementType = this.getType();
            if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE.equals(flumeTopologyElementType)) {
                Map<String, String> data = this.getData();
                if (data != null) {
                    agentName = data.get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_AGENT_NAME).toLowerCase();
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
}
