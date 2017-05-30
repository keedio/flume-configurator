package org.keedio.flume.configurator.structures;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.LoggerFactory;



public class FlumeTopologyNodeConnection {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FlumeTopology.class);

    private String node;
    private String port;


    @JsonCreator
    public FlumeTopologyNodeConnection(@JsonProperty("node") String node,
                                       @JsonProperty("port") String port) {
        this.node = node;
        this.port = port;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
