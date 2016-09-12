package org.keedio.flume.configurator.structures;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class FlumeTopology {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FlumeTopology.class);

    private String type;
    private String id;
    private Map<String, String> data;
    private String sourceConnection;
    private String targetConnection;


    @JsonCreator
    public FlumeTopology(@JsonProperty("type") String type,
                         @JsonProperty("id") String id,
                         @JsonProperty("data") Map<String, String> data,
                         @JsonProperty("source") String sourceConnection,
                         @JsonProperty("target") String targetConnection) {
        this.type = type;
        this.id = id;
        if (data == null) {
            data = new HashMap<String, String>();
        } else {
            this.data = data;
        }
        this.sourceConnection = sourceConnection;
        this.targetConnection = targetConnection;
    }

    public FlumeTopology() {

        data = new HashMap<String, String>();
    }


    public FlumeTopology(String type, String id) {

        this.type = type;
        this.id = id;
        data = new HashMap<String, String>();
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
}
