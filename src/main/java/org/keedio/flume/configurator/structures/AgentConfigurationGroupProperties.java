package org.keedio.flume.configurator.structures;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AgentConfigurationGroupProperties {


    private List listSourceProperties;
    private List listChannelProperties;
    private List listSinkProperties;
    private Map<String,List<String>> mapSourceInterceptorProperties;



    public AgentConfigurationGroupProperties() {
        super();

        listSourceProperties = new ArrayList();
        listChannelProperties = new ArrayList();
        listSinkProperties = new ArrayList();
        mapSourceInterceptorProperties = new LinkedHashMap<>();

    }


    public List getListSourceProperties() {
        return listSourceProperties;
    }


    public void setListSourceProperties(List<Map.Entry<String,String>> listSourceProperties) {
        this.listSourceProperties = listSourceProperties;
    }


    public List getListChannelProperties() {
        return listChannelProperties;
    }


    public void setListChannelProperties(List listChannelProperties) {
        this.listChannelProperties = listChannelProperties;
    }


    public List getListSinkProperties() {
        return listSinkProperties;
    }


    public void setListSinkProperties(List listSinkProperties) {
        this.listSinkProperties = listSinkProperties;
    }


    public Map<String, List<String>> getMapSourceInterceptorProperties() {
        return mapSourceInterceptorProperties;
    }

    public void setMapSourceInterceptorProperties(Map<String, List<String>> mapSourceInterceptorProperties) {
        this.mapSourceInterceptorProperties = mapSourceInterceptorProperties;
    }

}
