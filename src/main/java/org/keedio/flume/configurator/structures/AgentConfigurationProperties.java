package org.keedio.flume.configurator.structures;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AgentConfigurationProperties {

    private List listGeneralProperties;
    private Map<String,AgentConfigurationGroupProperties> mapGroupProperties;



    public AgentConfigurationProperties() {
        super();
        listGeneralProperties = new ArrayList();
        mapGroupProperties = new LinkedHashMap<>();
    }

    public List getListGeneralProperties() {
        return listGeneralProperties;
    }

    public void setListGeneralProperties(List listGeneralProperties) {
        this.listGeneralProperties = listGeneralProperties;
    }

    public Map<String, AgentConfigurationGroupProperties> getMapGroupProperties() {
        return mapGroupProperties;
    }

    public void setMapGroupProperties(
            Map<String, AgentConfigurationGroupProperties> mapGroupProperties) {
        this.mapGroupProperties = mapGroupProperties;
    }

}

