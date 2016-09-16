package org.keedio.flume.configurator.topology;

import org.apache.log4j.PropertyConfigurator;
import org.keedio.flume.configurator.builder.ConfigurationBuilder;
import org.keedio.flume.configurator.constants.FlumeConfiguratorConstants;
import org.keedio.flume.configurator.exceptions.FlumeConfiguratorException;
import org.keedio.flume.configurator.structures.FlumeTopology;
import org.keedio.flume.configurator.structures.LinkedProperties;
import org.keedio.flume.configurator.structures.TopologyPropertyBean;
import org.keedio.flume.configurator.utils.FlumeConfiguratorTopologyUtils;
import org.slf4j.LoggerFactory;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.*;

public class FlumeTopologyPropertiesGenerator {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FlumeTopologyPropertiesGenerator.class);

    private static String  pathJSONTopology;
    private static boolean generateFlumeConfigurationFiles;
    private static String  pathPropertiesGeneratedFile;

    private byte[] flumeJSONTopology;
    private List<FlumeTopology> flumeTopologyList;
    private Map<String, FlumeTopology> mapTopology;
    private List<FlumeTopology> listTopologyAgents;
    private List<FlumeTopology> listTopologySources;
    private List<FlumeTopology> listTopologyConnections;
    private List<FlumeTopology> listTopologyInterceptors;
    private Map<String, DefaultMutableTreeNode> flumeTopologyTree = new LinkedHashMap<>();
    //private GraphStructure; TODO: Choose a library for graphs
    private boolean isTreeCompliant = true;


    private int agentsNumber = 0;
    private int sourcesNumber = 0;
    private int interceptorsNumber = 0;
    private int channelsNumber = 0;
    private int sinksNumber = 0;

    private Properties flumeConfigurationProperties = new LinkedProperties();


    private Map<String, List<TopologyPropertyBean>> sourcesPropertiesMap =  new LinkedHashMap<>();
    private Map<String, List<TopologyPropertyBean>> interceptorsPropertiesMap =  new LinkedHashMap<>();
    private Map<String, List<TopologyPropertyBean>> channelsPropertiesMap =  new LinkedHashMap<>();
    private Map<String, List<TopologyPropertyBean>> sinksPropertiesMap =  new LinkedHashMap<>();

    private Map<String, TopologyPropertyBean> sourcesCommonPropertiesMap = new LinkedHashMap<>();
    private Map<String, TopologyPropertyBean> interceptorsCommonPropertiesMap = new LinkedHashMap<>();
    private Map<String, TopologyPropertyBean> channelsCommonPropertiesMap = new LinkedHashMap<>();
    private Map<String, TopologyPropertyBean> sinksCommonPropertiesMap = new LinkedHashMap<>();




    /**
     * Load the properties file
     * @throws IOException
     */
    private void loadJSONTopologyFile() throws IOException {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN loadJSONTopologyFile");
        }

        flumeJSONTopology = Files.readAllBytes(Paths.get(pathJSONTopology));

        //flumeJSONTopology = Files.readAllBytes(Paths.get("src/main/resources/example-topology/FlumeTopologyWithComments.json"));
        //flumeJSONTopology = Files.readAllBytes(Paths.get("src/main/resources/example-topology/FlumeTopologyWithComments_with1Agent.json"));
        //flumeJSONTopology = Files.readAllBytes(Paths.get("src/main/resources/example-topology/FlumeTopologyWithComments_with2Agent.json"));

        flumeTopologyList =  Arrays.asList(JSONStringSerializer.fromBytes(flumeJSONTopology, FlumeTopology[].class));

        logger.debug(flumeTopologyList.toString());
    }


    private void createInitialStructures() {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN createInitialStructures");
        }

        boolean withAgentNodes = false;

        mapTopology = new LinkedHashMap<>();
        listTopologyAgents = new ArrayList<>();
        listTopologySources = new ArrayList<>();
        listTopologyConnections = new ArrayList<>();
        listTopologyInterceptors = new ArrayList<>();

        for (FlumeTopology flumeTopology : flumeTopologyList) {

            String topologyID = flumeTopology.getId();
            String topologyType = flumeTopology.getType();
            if (!FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION.equals(topologyType)) {
                //Add all elements from topology but connections
                mapTopology.put(topologyID, flumeTopology);
            }

            if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT.equals(topologyType)) {
                listTopologyAgents.add(flumeTopology);
                withAgentNodes = true;
            } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE.equals(topologyType)) {
                listTopologySources.add(flumeTopology);
            } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR.equals(topologyType)) {
                listTopologyInterceptors.add(flumeTopology);
            } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION.equals(topologyType)) {
                listTopologyConnections.add(flumeTopology);
            }

        }

        //Detect if the topology is a tree or is a graph from connections list
        isTreeCompliant = FlumeConfiguratorTopologyUtils.isTreeCompliant(withAgentNodes, listTopologyConnections, mapTopology.size(), listTopologySources.size(), listTopologyAgents.size());

        if (isTreeCompliant) {

            //Create a tree with structure described in topology
            createTopologyTree(withAgentNodes);

            Set<String> keySet= flumeTopologyTree.keySet();
            Set<Map.Entry<String,DefaultMutableTreeNode>> entrySet = flumeTopologyTree.entrySet();
            Collection<DefaultMutableTreeNode> collectionValues = flumeTopologyTree.values();

            System.out.println(FlumeConfiguratorTopologyUtils.renderFlumeTopology(flumeTopologyTree.values()));

            getElementsNumber();

            setSourcesChannelsProperty();

            setSinksChannelProperty();



        } else {

            //Create a graph with structure described in topology
        }




    }


    private void createTopologyTree(boolean withAgentNodes) {

        Set<String> agentsList = new LinkedHashSet<>();
        //Set<DefaultMutableTreeNode> rootNodesSet = new HashSet<>();
        List<DefaultMutableTreeNode> flumeTopologyNodeList = new ArrayList<>();


        if (!withAgentNodes) {
            //Get agents list from sources list
            for (FlumeTopology source : listTopologySources) {
                //Check if the source's agent name is already in tree
                String agentName = source.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_AGENT_NAME).toLowerCase();
                if ((agentName != null) && (!"".equals(agentName))) {
                    if (!agentsList.contains(agentName)) {
                        agentsList.add(agentName);
                    }
                } else {
                    throw new FlumeConfiguratorException("Topology sources must be filled 'agentName' property");
                }
            }

            //Create root nodes
            for (String agentName : agentsList) {

                FlumeTopology flumeTopologyAgentRoot = new FlumeTopology(FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT, agentName);
                flumeTopologyAgentRoot.getData().put(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME, agentName);

                DefaultMutableTreeNode flumeTopologyAgentRootNode = new DefaultMutableTreeNode(flumeTopologyAgentRoot);

                flumeTopologyTree.put(agentName, flumeTopologyAgentRootNode);

                //rootNodesSet.add(flumeTopologyAgentRootNode);
            }

            //Create nodes for all elements in topology. Sources nodes will be link to their agent node.
            for (String flumeTopologyElementId : mapTopology.keySet()) {
                FlumeTopology flumeTopologyElement = mapTopology.get(flumeTopologyElementId);

                String flumeTopologyElementType = flumeTopologyElement.getType();

                //Create node from topology element
                DefaultMutableTreeNode flumeTopologyNode = new DefaultMutableTreeNode(flumeTopologyElement);
                flumeTopologyNodeList.add(flumeTopologyNode);

                //Link source node with their agent node
                if (flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE)) {
                    String agentName = flumeTopologyElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_AGENT_NAME).toLowerCase();

                    DefaultMutableTreeNode flumeTopologyAgentRootNode = flumeTopologyTree.get(agentName);

                    if (flumeTopologyAgentRootNode != null) {
                        flumeTopologyAgentRootNode.add(flumeTopologyNode);
                    } else {
                        throw new FlumeConfiguratorException("flumeTopologyTree doesn't contain root node for agent " + agentName);
                    }

                }
            }
        } else {

            //Create nodes for all elements in topology (including agents).
            for (String flumeTopologyElementId : mapTopology.keySet()) {
                FlumeTopology flumeTopologyElement = mapTopology.get(flumeTopologyElementId);

                String flumeTopologyElementType = flumeTopologyElement.getType();

                //Create node from topology element
                DefaultMutableTreeNode flumeTopologyNode = new DefaultMutableTreeNode(flumeTopologyElement);
                flumeTopologyNodeList.add(flumeTopologyNode);

                //Link source node with their agent node
                if (flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT)) {
                    String agentName = flumeTopologyElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME).toLowerCase();

                    flumeTopologyTree.put(agentName, flumeTopologyNode);
                }
            }

        }

        for (FlumeTopology connection : listTopologyConnections) {
            String sourceConnection = connection.getSourceConnection();
            String targetConnection = connection.getTargetConnection();

            //Get de nodes and link
            DefaultMutableTreeNode sourceConnectionNode = FlumeConfiguratorTopologyUtils.searchNode(sourceConnection, flumeTopologyNodeList);
            DefaultMutableTreeNode targetConnectionNode = FlumeConfiguratorTopologyUtils.searchNode(targetConnection, flumeTopologyNodeList);


            //All nodes must be present
            if ((sourceConnectionNode == null) || (targetConnectionNode == null)) {
                throw new FlumeConfiguratorException("sourceConnectionNode " + sourceConnection + " or targetConnectionNode  " + targetConnection + " are not present on node's pull");
            } else {
                //Link sourceNode with targetNode
                sourceConnectionNode.add(targetConnectionNode);
            }

        }

    }


    private void getElementsNumber() {

        if (isTreeCompliant) {
            agentsNumber = flumeTopologyTree.size();

            for (String agentName : flumeTopologyTree.keySet()) {

                DefaultMutableTreeNode agentRootNode = flumeTopologyTree.get(agentName);
                Enumeration agentTreeNodes = agentRootNode.preorderEnumeration();

                while (agentTreeNodes.hasMoreElements()) {
                    DefaultMutableTreeNode agentTreeNode = (DefaultMutableTreeNode) agentTreeNodes.nextElement();
                    FlumeTopology flumeTopologyElement = (FlumeTopology) agentTreeNode.getUserObject();

                    if (flumeTopologyElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE)) {
                        sourcesNumber++;
                    } else if (flumeTopologyElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR)) {
                        interceptorsNumber++;
                    } else if (flumeTopologyElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL)) {
                        channelsNumber++;
                    } else if (flumeTopologyElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK)) {
                        sinksNumber++;
                    }
                }
            }

        } else {


        }

        System.out.println("Agents number: " + agentsNumber);
        System.out.println("Sources number: " + sourcesNumber);
        System.out.println("Interceptors number: " + interceptorsNumber);
        System.out.println("Channels number: " + channelsNumber);
        System.out.println("Sinks number: " + sinksNumber);


    }


    private void setSourcesChannelsProperty() {

        if (isTreeCompliant) {


            for (String agentName : flumeTopologyTree.keySet()) {

                DefaultMutableTreeNode agentRootNode = flumeTopologyTree.get(agentName);
                Enumeration sourcesTreeNodes = agentRootNode.children();

                while (sourcesTreeNodes.hasMoreElements()) {
                    DefaultMutableTreeNode sourceTreeNode = (DefaultMutableTreeNode) sourcesTreeNodes.nextElement();
                    FlumeTopology flumeTopologyElement = (FlumeTopology) sourceTreeNode.getUserObject();

                    List<String> sourceChannelsList = new ArrayList<>();
                    if (flumeTopologyElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE)) {
                        Map<String,String> sourceProperties = flumeTopologyElement.getData();

                        Enumeration childrenTreeNodes = sourceTreeNode.preorderEnumeration();

                        while (childrenTreeNodes.hasMoreElements()) {
                            DefaultMutableTreeNode childTreeNode = (DefaultMutableTreeNode) childrenTreeNodes.nextElement();
                            FlumeTopology flumeTopologyChildElement = (FlumeTopology) childTreeNode.getUserObject();

                            if (flumeTopologyChildElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL)) {
                                String channelName = flumeTopologyChildElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);
                                sourceChannelsList.add(channelName);
                            }
                        }

                        //Generate channels values
                        sourceProperties.put(FlumeConfiguratorConstants.CHANNELS_PROPERTY,FlumeConfiguratorTopologyUtils.listToString(sourceChannelsList, FlumeConfiguratorConstants.WHITE_SPACE));
                    }


                }
            }




        } else {

        }
    }



    private void setSinksChannelProperty() {

        if (isTreeCompliant) {


            for (String agentName : flumeTopologyTree.keySet()) {

                DefaultMutableTreeNode agentRootNode = flumeTopologyTree.get(agentName);
                Enumeration agentTreeNodes = agentRootNode.preorderEnumeration();

                while (agentTreeNodes.hasMoreElements()) {
                    DefaultMutableTreeNode agentTreeNode = (DefaultMutableTreeNode) agentTreeNodes.nextElement();
                    FlumeTopology flumeTopologyElement = (FlumeTopology) agentTreeNode.getUserObject();


                    if (flumeTopologyElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK)) {
                        Map<String,String> sinkProperties = flumeTopologyElement.getData();

                        DefaultMutableTreeNode channelParentNode = (DefaultMutableTreeNode) agentTreeNode.getParent();
                        FlumeTopology flumeTopologyParentElement = (FlumeTopology) channelParentNode.getUserObject();

                        if (flumeTopologyParentElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL)) {
                            String channelName = flumeTopologyParentElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                            //Generate channels values
                            sinkProperties.put(FlumeConfiguratorConstants.CHANNEL_PROPERTY,channelName);

                        }


                    }


                }
            }




        } else {

        }
    }



    private void generateAgentListProperty() {

        if (isTreeCompliant) {

            ArrayList<String> agentsList = new ArrayList<>();
            for (String agentName : flumeTopologyTree.keySet()) {
                agentsList.add(agentName);
            }

            flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, FlumeConfiguratorConstants.AGENTS_LIST_PROPERTIES_PREFIX, agentsList,
                    FlumeConfiguratorConstants.PROPERTY_SEPARATOR_DEFAULT);


        } else {

        }

    }


    private void generateSourcesListProperties() {

        if (isTreeCompliant) {

            for (String agentName : flumeTopologyTree.keySet()) {

                DefaultMutableTreeNode agentRootNode = flumeTopologyTree.get(agentName);
                Enumeration agentSources = agentRootNode.children();
                List<String> agentSourcesList = new ArrayList<>();
                while (agentSources.hasMoreElements()) {
                    DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode) agentSources.nextElement();
                    FlumeTopology flumeTopologySource = (FlumeTopology) sourceNode.getUserObject();
                    String sourceName = flumeTopologySource.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);
                    agentSourcesList.add(sourceName);
                }

                String propertyKey = FlumeConfiguratorTopologyUtils.getKeyPropertyString(FlumeConfiguratorConstants.SOURCES_LIST_PROPERTIES_PREFIX, FlumeConfiguratorConstants.DOT_SEPARATOR, agentName);
                flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, propertyKey, agentSourcesList,
                        FlumeConfiguratorConstants.PROPERTY_SEPARATOR_DEFAULT);



            }


        } else {

        }

    }



    private void generateChannelsListProperties() {

        if (isTreeCompliant) {

            for (String agentName : flumeTopologyTree.keySet()) {

                DefaultMutableTreeNode agentRootNode = flumeTopologyTree.get(agentName);
                Enumeration agentTreeNodes = agentRootNode.preorderEnumeration();
                List<String> agentChannelsList = new ArrayList<>();
                while (agentTreeNodes.hasMoreElements()) {
                    DefaultMutableTreeNode agentTreeNode = (DefaultMutableTreeNode) agentTreeNodes.nextElement();
                    FlumeTopology flumeTopologyElement = (FlumeTopology) agentTreeNode.getUserObject();
                    String channelName = flumeTopologyElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                    if (flumeTopologyElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL)) {
                        agentChannelsList.add(channelName);
                    }
                }

                String propertyKey = FlumeConfiguratorTopologyUtils.getKeyPropertyString(FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX, FlumeConfiguratorConstants.DOT_SEPARATOR,
                                    agentName);
                flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, propertyKey, agentChannelsList,
                        FlumeConfiguratorConstants.PROPERTY_SEPARATOR_DEFAULT);
            }


        } else {

        }

    }


    private void generateSinksListProperties() {

        if (isTreeCompliant) {

            for (String agentName : flumeTopologyTree.keySet()) {

                DefaultMutableTreeNode agentRootNode = flumeTopologyTree.get(agentName);
                Enumeration agentTreeNodes = agentRootNode.preorderEnumeration();
                List<String> agentSinksList = new ArrayList<>();
                while (agentTreeNodes.hasMoreElements()) {
                    DefaultMutableTreeNode agentTreeNode = (DefaultMutableTreeNode) agentTreeNodes.nextElement();
                    FlumeTopology flumeTopologyElement = (FlumeTopology) agentTreeNode.getUserObject();
                    String sinkName = flumeTopologyElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                    if (flumeTopologyElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK)) {
                        agentSinksList.add(sinkName);
                    }
                }

                String propertyKey = FlumeConfiguratorTopologyUtils.getKeyPropertyString(FlumeConfiguratorConstants.SINKS_LIST_PROPERTIES_PREFIX, FlumeConfiguratorConstants.DOT_SEPARATOR,
                                    agentName);
                flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, propertyKey, agentSinksList,
                        FlumeConfiguratorConstants.PROPERTY_SEPARATOR_DEFAULT);
            }


        } else {

        }

    }


    private void generateElementsListProperties(String topologyType, String elementsListPropertiesPrefix) {

        if (isTreeCompliant) {

            for (String agentName : flumeTopologyTree.keySet()) {

                DefaultMutableTreeNode agentRootNode = flumeTopologyTree.get(agentName);
                Enumeration agentTreeNodes = agentRootNode.preorderEnumeration();
                List<String> agentElementsList = new ArrayList<>();
                while (agentTreeNodes.hasMoreElements()) {
                    DefaultMutableTreeNode agentTreeNode = (DefaultMutableTreeNode) agentTreeNodes.nextElement();
                    FlumeTopology flumeTopologyElement = (FlumeTopology) agentTreeNode.getUserObject();
                    String elementName = flumeTopologyElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                    if (flumeTopologyElement.getType().equals(topologyType)) {
                        agentElementsList.add(elementName);
                    }
                }

                String propertyKey = FlumeConfiguratorTopologyUtils.getKeyPropertyString(elementsListPropertiesPrefix, FlumeConfiguratorConstants.DOT_SEPARATOR,
                        agentName);
                flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, propertyKey, agentElementsList,
                        FlumeConfiguratorConstants.PROPERTY_SEPARATOR_DEFAULT);
            }


        } else {

        }

    }


    private void generateGroupsListProperties(boolean groupPerAgentSource) {

        if (isTreeCompliant) {

            for (String agentName : flumeTopologyTree.keySet()) {

                DefaultMutableTreeNode agentRootNode = flumeTopologyTree.get(agentName);

                if (groupPerAgentSource) {
                    //Group per agent source
                    Enumeration agentSources = agentRootNode.children();

                    while (agentSources.hasMoreElements()) {
                        DefaultMutableTreeNode sourceTreeNode = (DefaultMutableTreeNode) agentSources.nextElement();

                        FlumeTopology flumeTopologySource = (FlumeTopology) sourceTreeNode.getUserObject();
                        String sourceName = flumeTopologySource.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);
                        Enumeration elementTreeNodes = sourceTreeNode.preorderEnumeration();

                        List<String> sourceGroupsList = new ArrayList<>();

                        while (elementTreeNodes.hasMoreElements()) {
                            DefaultMutableTreeNode agentTreeNode = (DefaultMutableTreeNode) elementTreeNodes.nextElement();
                            FlumeTopology flumeTopologyElement = (FlumeTopology) agentTreeNode.getUserObject();
                            String elementName = flumeTopologyElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                            if ((flumeTopologyElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE)) ||
                                    (flumeTopologyElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL)) ||
                                    (flumeTopologyElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK))){
                                sourceGroupsList.add(elementName);
                            }

                        }

                        String propertyKey = FlumeConfiguratorTopologyUtils.getKeyPropertyString(FlumeConfiguratorConstants.GROUPS_LIST_PROPERTIES_PREFIX, FlumeConfiguratorConstants.DOT_SEPARATOR,
                                            agentName, FlumeConfiguratorConstants.DOT_SEPARATOR, FlumeConfiguratorConstants.FLUME_TOPOLOGY_GROUP_NAME_DEFAULT,
                                            FlumeConfiguratorConstants.UNDERSCORE_SEPARATOR, sourceName.toUpperCase());
                        flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, propertyKey, sourceGroupsList,
                                FlumeConfiguratorConstants.PROPERTY_SEPARATOR_DEFAULT);

                    }




                } else {
                    //Group per agent
                    Enumeration agentTreeNodes = agentRootNode.preorderEnumeration();
                    List<String> agentGroupsList = new ArrayList<>();
                    while (agentTreeNodes.hasMoreElements()) {
                        DefaultMutableTreeNode agentTreeNode = (DefaultMutableTreeNode) agentTreeNodes.nextElement();
                        FlumeTopology flumeTopologyElement = (FlumeTopology) agentTreeNode.getUserObject();
                        String elementName = flumeTopologyElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                        if ((flumeTopologyElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE)) ||
                                (flumeTopologyElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL)) ||
                                (flumeTopologyElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK))){
                            agentGroupsList.add(elementName);
                        }
                    }

                    String propertyKey = FlumeConfiguratorTopologyUtils.getKeyPropertyString(FlumeConfiguratorConstants.GROUPS_LIST_PROPERTIES_PREFIX, FlumeConfiguratorConstants.DOT_SEPARATOR,
                                        agentName,  FlumeConfiguratorConstants.DOT_SEPARATOR, FlumeConfiguratorConstants.FLUME_TOPOLOGY_GROUP_NAME_DEFAULT,
                                        FlumeConfiguratorConstants.UNDERSCORE_SEPARATOR, agentName.toUpperCase());
                    flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, propertyKey, agentGroupsList,
                            FlumeConfiguratorConstants.PROPERTY_SEPARATOR_DEFAULT);
                }


            }


        } else {

        }


    }

    private void generateInterceptorsListProperties() {

        if (isTreeCompliant) {

            for (String agentName : flumeTopologyTree.keySet()) {

                DefaultMutableTreeNode agentRootNode = flumeTopologyTree.get(agentName);
                Enumeration agentSources = agentRootNode.children();

                while (agentSources.hasMoreElements()) {

                    DefaultMutableTreeNode agentSourceNode = (DefaultMutableTreeNode) agentSources.nextElement();
                    FlumeTopology flumeTopologySource = (FlumeTopology) agentSourceNode.getUserObject();
                    String sourceName = flumeTopologySource.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                    Enumeration sourceTreeNodes = agentSourceNode.preorderEnumeration();

                    List<String> sourceInterceptorsList = new ArrayList<>();
                    while (sourceTreeNodes.hasMoreElements()) {

                        DefaultMutableTreeNode sourceTreeNode = (DefaultMutableTreeNode) sourceTreeNodes.nextElement();
                        FlumeTopology flumeTopologyElement = (FlumeTopology) sourceTreeNode.getUserObject();
                        String interceptorName = flumeTopologyElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                        if (flumeTopologyElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR)) {
                            sourceInterceptorsList.add(interceptorName);
                        }
                    }

                    String propertyKey = FlumeConfiguratorConstants.INTERCEPTORS_LIST_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + sourceName;
                    flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, propertyKey, sourceInterceptorsList,
                            FlumeConfiguratorConstants.PROPERTY_SEPARATOR_DEFAULT);
                }
            }


        } else {

        }


    }

/*
    private void generateSourcesProperties() {

        if (isTreeCompliant) {

            for (String agentName : flumeTopologyTree.keySet()) {

                DefaultMutableTreeNode agentRootNode = flumeTopologyTree.get(agentName);
                Enumeration agentSources = agentRootNode.children();

                while (agentSources.hasMoreElements()) {
                    DefaultMutableTreeNode agentSourceNode = (DefaultMutableTreeNode) agentSources.nextElement();
                    FlumeTopology flumeTopologyElement = (FlumeTopology) agentSourceNode.getUserObject();
                    String sourceName = flumeTopologyElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                    Map<String, String> originalTopologySourceProperties = flumeTopologyElement.getData();
                    Map<String, String> topologySourceProperties = FlumeConfiguratorTopologyUtils.getValidTopologyProperties(originalTopologySourceProperties);

                    for (String propertyKey : topologySourceProperties.keySet()) {

                        if (!FlumeConfiguratorTopologyUtils.isCommentProperty(propertyKey)) {
                            String propertyValue = topologySourceProperties.get(propertyKey);

                            //Get the property Comment.
                            String propertyCommentKey = FlumeConfiguratorTopologyUtils.getCommentPropertyName(propertyKey);
                            String propertyCommentValue = topologySourceProperties.get(propertyCommentKey);


                            sourcesPropertiesMap = FlumeConfiguratorTopologyUtils.addPropertyBean(sourcesPropertiesMap, propertyKey, propertyCommentValue, sourceName, propertyValue);
                        }

                    }

                }
            }


            //Detect common properties
            for (String propertyKey : sourcesPropertiesMap.keySet()) {
                List<TopologyPropertyBean> listPropertyBeans = sourcesPropertiesMap.get(propertyKey);

                String valueCommonProperty = FlumeConfiguratorTopologyUtils.getValueCommonProperty(listPropertyBeans, sourcesNumber, FlumeConfiguratorConstants.FLUME_TOPOLOGY_COMMON_PROPERTY_RATIO);

                if (valueCommonProperty != null) {
                    //Get the comment from the any member of the list.
                    String propertyComment = listPropertyBeans.get(0).getPropertyComment();
                    TopologyPropertyBean commonProperty = new TopologyPropertyBean(propertyComment, null, valueCommonProperty);

                    sourcesCommonPropertiesMap.put(propertyKey, commonProperty);
                }

            }

            //Write common properties
            for (String sourceCommonPropertyKey : sourcesCommonPropertiesMap.keySet()) {

                TopologyPropertyBean sourceCommonProperty = sourcesCommonPropertiesMap.get(sourceCommonPropertyKey);
                String commentPropertyValue = sourceCommonProperty.getPropertyComment();
                String propertyValue = sourceCommonProperty.getPropertyValue();

                String propertyKey = FlumeConfiguratorConstants.SOURCES_COMMON_PROPERTY_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + sourceCommonPropertyKey;
                String commentPropertyKey = FlumeConfiguratorConstants.SOURCES_COMMON_PROPERTY_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR +
                        FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + sourceCommonPropertyKey;
                flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, commentPropertyKey, commentPropertyValue);
                flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, propertyKey, propertyValue);

            }



        } else {

        }


    }
*/
/*
    private void generateInterceptorsProperties() {

        if (isTreeCompliant) {

            for (String agentName : flumeTopologyTree.keySet()) {

                DefaultMutableTreeNode agentRootNode = flumeTopologyTree.get(agentName);
                Enumeration agentElements = agentRootNode.preorderEnumeration();

                while (agentElements.hasMoreElements()) {
                    DefaultMutableTreeNode agentElementNode = (DefaultMutableTreeNode) agentElements.nextElement();
                    FlumeTopology flumeTopologyElement = (FlumeTopology) agentElementNode.getUserObject();
                    String elementName = flumeTopologyElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                    if (flumeTopologyElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR)) {
                        Map<String, String> originalTopologyInterceptorProperties = flumeTopologyElement.getData();
                        Map<String, String> topologyInterceptorProperties = FlumeConfiguratorTopologyUtils.getValidTopologyProperties(originalTopologyInterceptorProperties);

                        for (String propertyKey : topologyInterceptorProperties.keySet()) {

                            if (!FlumeConfiguratorTopologyUtils.isCommentProperty(propertyKey)) {
                                String propertyValue = topologyInterceptorProperties.get(propertyKey);

                                //Get the property Comment.
                                String propertyCommentKey = FlumeConfiguratorTopologyUtils.getCommentPropertyName(propertyKey);
                                String propertyCommentValue = topologyInterceptorProperties.get(propertyCommentKey);


                                interceptorsPropertiesMap = FlumeConfiguratorTopologyUtils.addPropertyBean(interceptorsPropertiesMap, propertyKey, propertyCommentValue, elementName, propertyValue);

                            }
                        }
                    }

                }
            }

            //Detect common properties
            for (String propertyKey : interceptorsPropertiesMap.keySet()) {
                List<TopologyPropertyBean> listPropertyBeans = interceptorsPropertiesMap.get(propertyKey);

                String valueCommonProperty = FlumeConfiguratorTopologyUtils.getValueCommonProperty(listPropertyBeans, interceptorsNumber, FlumeConfiguratorConstants.FLUME_TOPOLOGY_COMMON_PROPERTY_RATIO);

                if (valueCommonProperty != null) {
                    //Get the comment from the any member of the list.
                    String propertyComment = listPropertyBeans.get(0).getPropertyComment();
                    TopologyPropertyBean commonProperty = new TopologyPropertyBean(propertyComment, null, valueCommonProperty);

                    interceptorsCommonPropertiesMap.put(propertyKey, commonProperty);
                }

            }

            //Write common properties
            for (String interceptorCommonPropertyKey : interceptorsCommonPropertiesMap.keySet()) {

                TopologyPropertyBean interceptorCommonProperty = interceptorsCommonPropertiesMap.get(interceptorCommonPropertyKey);
                String commentPropertyValue = interceptorCommonProperty.getPropertyComment();
                String propertyValue = interceptorCommonProperty.getPropertyValue();

                String propertyKey = FlumeConfiguratorConstants.INTERCEPTORS_COMMON_PROPERTY_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + interceptorCommonPropertyKey;
                String commentPropertyKey = FlumeConfiguratorConstants.INTERCEPTORS_COMMON_PROPERTY_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR +
                        FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + interceptorCommonPropertyKey;
                flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, commentPropertyKey, commentPropertyValue);
                flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, propertyKey, propertyValue);

            }

        } else {

        }


    }
*/
/*
    private void generateChannelsProperties() {

        if (isTreeCompliant) {

            for (String agentName : flumeTopologyTree.keySet()) {

                DefaultMutableTreeNode agentRootNode = flumeTopologyTree.get(agentName);
                Enumeration agentElements = agentRootNode.preorderEnumeration();

                while (agentElements.hasMoreElements()) {
                    DefaultMutableTreeNode agentElementNode = (DefaultMutableTreeNode) agentElements.nextElement();
                    FlumeTopology flumeTopologyElement = (FlumeTopology) agentElementNode.getUserObject();
                    String elementName = flumeTopologyElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                    if (flumeTopologyElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL)) {
                        Map<String, String> originalTopologyChannelProperties = flumeTopologyElement.getData();
                        Map<String, String> topologyChannelProperties = FlumeConfiguratorTopologyUtils.getValidTopologyProperties(originalTopologyChannelProperties);

                        for (String propertyKey : topologyChannelProperties.keySet()) {

                            if (!FlumeConfiguratorTopologyUtils.isCommentProperty(propertyKey)) {
                                String propertyValue = topologyChannelProperties.get(propertyKey);

                                //Get the property Comment.
                                String propertyCommentKey = FlumeConfiguratorTopologyUtils.getCommentPropertyName(propertyKey);
                                String propertyCommentValue = topologyChannelProperties.get(propertyCommentKey);


                                channelsPropertiesMap = FlumeConfiguratorTopologyUtils.addPropertyBean(channelsPropertiesMap, propertyKey, propertyCommentValue, elementName, propertyValue);
                            }

                        }
                    }

                }
            }

            //Detect common properties
            for (String propertyKey : channelsPropertiesMap.keySet()) {
                List<TopologyPropertyBean> listPropertyBeans = channelsPropertiesMap.get(propertyKey);

                String valueCommonProperty = FlumeConfiguratorTopologyUtils.getValueCommonProperty(listPropertyBeans, channelsNumber, FlumeConfiguratorConstants.FLUME_TOPOLOGY_COMMON_PROPERTY_RATIO);

                if (valueCommonProperty != null) {
                    //Get the comment from the any member of the list.
                    String propertyComment = listPropertyBeans.get(0).getPropertyComment();
                    TopologyPropertyBean commonProperty = new TopologyPropertyBean(propertyComment, null, valueCommonProperty);

                    channelsCommonPropertiesMap.put(propertyKey, commonProperty);
                }

            }

            //Write common properties
            for (String channelCommonPropertyKey : channelsCommonPropertiesMap.keySet()) {

                TopologyPropertyBean channelCommonProperty = channelsCommonPropertiesMap.get(channelCommonPropertyKey);
                String commentPropertyValue = channelCommonProperty.getPropertyComment();
                String propertyValue = channelCommonProperty.getPropertyValue();

                String propertyKey = FlumeConfiguratorConstants.CHANNELS_COMMON_PROPERTY_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + channelCommonPropertyKey;
                String commentPropertyKey = FlumeConfiguratorConstants.CHANNELS_COMMON_PROPERTY_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR +
                        FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + channelCommonPropertyKey;
                flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, commentPropertyKey, commentPropertyValue);
                flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, propertyKey, propertyValue);

            }

        } else {

        }


    }
*/
/*
    private void generateSinksProperties() {

        if (isTreeCompliant) {

            for (String agentName : flumeTopologyTree.keySet()) {

                DefaultMutableTreeNode agentRootNode = flumeTopologyTree.get(agentName);
                Enumeration agentElements = agentRootNode.preorderEnumeration();

                while (agentElements.hasMoreElements()) {
                    DefaultMutableTreeNode agentElementNode = (DefaultMutableTreeNode) agentElements.nextElement();
                    FlumeTopology flumeTopologyElement = (FlumeTopology) agentElementNode.getUserObject();
                    String elementName = flumeTopologyElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                    if (flumeTopologyElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK)) {
                        Map<String, String> originalTopologySinkProperties = flumeTopologyElement.getData();
                        Map<String, String> topologySinkProperties = FlumeConfiguratorTopologyUtils.getValidTopologyProperties(originalTopologySinkProperties);

                        for (String propertyKey : topologySinkProperties.keySet()) {

                            if (!FlumeConfiguratorTopologyUtils.isCommentProperty(propertyKey)) {
                                String propertyValue = topologySinkProperties.get(propertyKey);

                                //Get the property Comment.
                                String propertyCommentKey = FlumeConfiguratorTopologyUtils.getCommentPropertyName(propertyKey);
                                String propertyCommentValue = topologySinkProperties.get(propertyCommentKey);


                                sinksPropertiesMap = FlumeConfiguratorTopologyUtils.addPropertyBean(sinksPropertiesMap, propertyKey, propertyCommentValue, elementName, propertyValue);

                            }
                        }
                    }

                }
            }

            //Detect common properties
            for (String propertyKey : sinksPropertiesMap.keySet()) {
                List<TopologyPropertyBean> listPropertyBeans = sinksPropertiesMap.get(propertyKey);

                String valueCommonProperty = FlumeConfiguratorTopologyUtils.getValueCommonProperty(listPropertyBeans, sinksNumber, FlumeConfiguratorConstants.FLUME_TOPOLOGY_COMMON_PROPERTY_RATIO);

                if (valueCommonProperty != null) {
                    //Get the comment from the any member of the list.
                    String propertyComment = listPropertyBeans.get(0).getPropertyComment();
                    TopologyPropertyBean commonProperty = new TopologyPropertyBean(propertyComment, null, valueCommonProperty);

                    sinksCommonPropertiesMap.put(propertyKey, commonProperty);
                }

            }

            //Write common properties
            for (String sinkCommonPropertyKey : sinksCommonPropertiesMap.keySet()) {

                TopologyPropertyBean sinkCommonProperty = sinksCommonPropertiesMap.get(sinkCommonPropertyKey);
                String commentPropertyValue = sinkCommonProperty.getPropertyComment();
                String propertyValue = sinkCommonProperty.getPropertyValue();

                String propertyKey = FlumeConfiguratorConstants.SINKS_COMMON_PROPERTY_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + sinkCommonPropertyKey;
                String commentPropertyKey = FlumeConfiguratorConstants.SINKS_COMMON_PROPERTY_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR +
                        FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + sinkCommonPropertyKey;
                flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, commentPropertyKey, commentPropertyValue);
                flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, propertyKey, propertyValue);

            }


        } else {

        }


    }
*/


    private void generateElementsProperties(String topologyType, String commonPropertiesPrefix, String partialPropertiesPrefix) {

        if (isTreeCompliant) {

            Map<String, List<TopologyPropertyBean>> elementsMap = null;
            Map<String, TopologyPropertyBean> elementsCommonPropertiesMap = null;
            int elementsNumber = 0;

            if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE.equals(topologyType)) {
                elementsMap = sourcesPropertiesMap;
                elementsCommonPropertiesMap = sourcesCommonPropertiesMap;
                elementsNumber = sourcesNumber;
            } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR.equals(topologyType)) {
                elementsMap = interceptorsPropertiesMap;
                elementsCommonPropertiesMap = interceptorsCommonPropertiesMap;
                elementsNumber = interceptorsNumber;
            } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL.equals(topologyType)) {
                elementsMap = channelsPropertiesMap;
                elementsCommonPropertiesMap = channelsCommonPropertiesMap;
                elementsNumber = channelsNumber;
            } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK.equals(topologyType)) {
                elementsMap = sinksPropertiesMap;
                elementsCommonPropertiesMap = sinksCommonPropertiesMap;
                elementsNumber = sinksNumber;
            }


            for (String agentName : flumeTopologyTree.keySet()) {

                DefaultMutableTreeNode agentRootNode = flumeTopologyTree.get(agentName);
                Enumeration agentElements = agentRootNode.preorderEnumeration();

                while (agentElements.hasMoreElements()) {
                    DefaultMutableTreeNode agentElementNode = (DefaultMutableTreeNode) agentElements.nextElement();
                    FlumeTopology flumeTopologyElement = (FlumeTopology) agentElementNode.getUserObject();
                    String elementName = flumeTopologyElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                    if (flumeTopologyElement.getType().equals(topologyType)) {
                        Map<String, String> originalTopologyElementProperties = flumeTopologyElement.getData();
                        Map<String, String> topologyElementProperties = FlumeConfiguratorTopologyUtils.getValidTopologyProperties(originalTopologyElementProperties);

                        for (String propertyKey : topologyElementProperties.keySet()) {

                            if (!FlumeConfiguratorTopologyUtils.isCommentProperty(propertyKey)) {
                                String propertyValue = topologyElementProperties.get(propertyKey);

                                //Get the property Comment.
                                String propertyCommentKey = FlumeConfiguratorTopologyUtils.getCommentPropertyName(propertyKey);
                                String propertyCommentValue = topologyElementProperties.get(propertyCommentKey);


                                elementsMap = FlumeConfiguratorTopologyUtils.addPropertyBean(elementsMap, propertyKey, propertyCommentValue, elementName, propertyValue);

                            }
                        }
                    }

                }
            }

            //Detect common properties
            for (String propertyKey : elementsMap.keySet()) {
                List<TopologyPropertyBean> listPropertyBeans = elementsMap.get(propertyKey);

                String valueCommonProperty = FlumeConfiguratorTopologyUtils.getValueCommonProperty(listPropertyBeans, elementsNumber, FlumeConfiguratorConstants.FLUME_TOPOLOGY_COMMON_PROPERTY_RATIO);

                if (valueCommonProperty != null) {
                    //Get the comment from the any member of the list.
                    String propertyComment = listPropertyBeans.get(0).getPropertyComment();
                    TopologyPropertyBean commonProperty = new TopologyPropertyBean(propertyComment, null, valueCommonProperty);

                    elementsCommonPropertiesMap.put(propertyKey, commonProperty);
                }

            }

            //Write common properties
            for (String elementCommonPropertyKey : elementsCommonPropertiesMap.keySet()) {

                TopologyPropertyBean elementCommonProperty = elementsCommonPropertiesMap.get(elementCommonPropertyKey);
                String commentPropertyValue = elementCommonProperty.getPropertyComment();
                String propertyValue = elementCommonProperty.getPropertyValue();

                String propertyKey = commonPropertiesPrefix + FlumeConfiguratorConstants.DOT_SEPARATOR + elementCommonPropertyKey;
                String commentPropertyKey = commonPropertiesPrefix + FlumeConfiguratorConstants.DOT_SEPARATOR + FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX +
                        FlumeConfiguratorConstants.DOT_SEPARATOR + elementCommonPropertyKey;
                flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, commentPropertyKey, commentPropertyValue);
                flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, propertyKey, propertyValue);

            }

            //Write partial properties
            for (String propertyKey : elementsMap.keySet()) {
                List<TopologyPropertyBean> listPropertyBeans = elementsMap.get(propertyKey);

                TopologyPropertyBean partialProperty = null;

                for (TopologyPropertyBean propertyBean : listPropertyBeans) {
                    boolean existsCommonProperty = FlumeConfiguratorTopologyUtils.existsCommonProperty(propertyBean, elementsCommonPropertiesMap, propertyKey);

                    if (!existsCommonProperty) {
                        String partialPropertyComment = propertyBean.getPropertyComment();
                        String partialPropertyAppliedElement = propertyBean.getAppliedElement();
                        String partialPropertyValue = propertyBean.getPropertyValue();

                        if (partialProperty == null) {
                            partialProperty = new TopologyPropertyBean(partialPropertyComment, partialPropertyAppliedElement, partialPropertyValue);
                        } else {
                            //The comment of the first element will be the comment for all elements

                            partialProperty.setAppliedElement(FlumeConfiguratorTopologyUtils.appendString(partialProperty.getAppliedElement(), partialPropertyAppliedElement,
                                    FlumeConfiguratorConstants.PROPERTY_SEPARATOR_DEFAULT));

                            partialProperty.setPropertyValue(FlumeConfiguratorTopologyUtils.appendString(partialProperty.getPropertyValue(), partialPropertyValue,
                                    FlumeConfiguratorConstants.PROPERTY_SEPARATOR_DEFAULT));
                        }
                    }
                }

                if (partialProperty != null) {
                    String partialPropertyKeyComment = partialPropertiesPrefix + FlumeConfiguratorConstants.DOT_SEPARATOR +
                            FlumeConfiguratorConstants.PARTIAL_PROPERTY_COMMENT_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + propertyKey;
                    String partialPropertyKeyAppliedElements = partialPropertiesPrefix + FlumeConfiguratorConstants.DOT_SEPARATOR +
                            FlumeConfiguratorConstants.PARTIAL_PROPERTY_APPLIED_ELEMENTS_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + propertyKey;
                    String partialPropertyKeyValues = partialPropertiesPrefix + FlumeConfiguratorConstants.DOT_SEPARATOR +
                            FlumeConfiguratorConstants.PARTIAL_PROPERTY_PROPERTY_VALUES_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + propertyKey;

                    flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, partialPropertyKeyComment, partialProperty.getPropertyComment());
                    flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, partialPropertyKeyAppliedElements, partialProperty.getAppliedElement());
                    flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, partialPropertyKeyValues, partialProperty.getPropertyValue());
                }



            }


        } else {

        }


    }


    private void printConfiguration() {
        System.out.println(FlumeConfiguratorTopologyUtils.getPropertyAsString(flumeConfigurationProperties));
        String flumeConfigurationPropertiesString = FlumeConfiguratorTopologyUtils.getFlumePropertiesAsString(flumeConfigurationProperties);
        System.out.println(flumeConfigurationPropertiesString);
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        System.out.println(configurationBuilder.buildConfigurationMapFromStringProperties(flumeConfigurationPropertiesString,FlumeConfiguratorConstants.PROPERTY_SEPARATOR_DEFAULT,true, false, null, false));

    }


    /**
     * Generate the Configuration base properties file
     * @throws IOException
     */
    private void writeBaseConfigurationPropertiesFile(boolean writeFlumeConfigurationFiles) throws IOException {


        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN writeBaseConfigurationPropertiesFile");
        }

        String fileName;
        String fileBaseName;
        BufferedWriter bw;

        if ((pathPropertiesGeneratedFile == null) || ("".equals(pathPropertiesGeneratedFile))) {
            throw new InvalidPathException("", "The path is not valid");
        }

        File propertiesGeneratedFile = new File(pathPropertiesGeneratedFile);
        boolean isDirectory = propertiesGeneratedFile.isDirectory();


        if (isDirectory) {
            //The path of the file is determinated but not the name of the configuration file
            fileName = pathPropertiesGeneratedFile + File.separator + FlumeConfiguratorConstants.CONFIGURATION_BASE_PROPERTIES_FILE;
        } else {
            //The full path has been determinated
            fileName = pathPropertiesGeneratedFile;
        }

        bw = new BufferedWriter(new FileWriter(fileName));

        //Write the content
        String flumeConfigurationPropertiesString = FlumeConfiguratorTopologyUtils.getFlumePropertiesAsString(flumeConfigurationProperties);
        logger.info(flumeConfigurationPropertiesString);
        bw.write(flumeConfigurationPropertiesString);

        bw.flush();
        bw.close();

        if (writeFlumeConfigurationFiles) {
            ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
            configurationBuilder.buildConfigurationMapFromStringProperties(flumeConfigurationPropertiesString,FlumeConfiguratorConstants.PROPERTY_SEPARATOR_DEFAULT,true, true, pathPropertiesGeneratedFile, true);

        }

    }


    /**
     * Generate the input properties to Flume Configurator from a JSON Topology file
     * @return true if the process is correct, false otherwise
     */
    private boolean generateInputProperties() {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN generateInputProperties");
        }

        boolean isProcessOK;


        try {

            //Load the JSON topology file
            loadJSONTopologyFile();

            //Create initial structures
            createInitialStructures();

            //Generate agent list property
            generateAgentListProperty();

            //Generate sources list properties
            //generateSourcesListProperties();
            generateElementsListProperties(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE, FlumeConfiguratorConstants.SOURCES_LIST_PROPERTIES_PREFIX);

            //Generate channels list properties
            //generateChannelsListProperties();
            generateElementsListProperties(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL, FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX);

            //Generate sinks list properties
            //generateSinksListProperties();
            generateElementsListProperties(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK, FlumeConfiguratorConstants.SINKS_LIST_PROPERTIES_PREFIX);

            //Generate group lists properties
            generateGroupsListProperties(true);

            //Generate interceptors list properties
            generateInterceptorsListProperties();

            //Generate sources properties
            //generateSourcesProperties();
            generateElementsProperties(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE, FlumeConfiguratorConstants.SOURCES_COMMON_PROPERTY_PROPERTIES_PREFIX,
                    FlumeConfiguratorConstants.SOURCES_PARTIAL_PROPERTY_PROPERTIES_PREFIX);

            //Generate interceptors properties
            //generateInterceptorsProperties();
            generateElementsProperties(FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR, FlumeConfiguratorConstants.INTERCEPTORS_COMMON_PROPERTY_PROPERTIES_PREFIX,
                    FlumeConfiguratorConstants.INTERCEPTORS_PARTIAL_PROPERTY_PROPERTIES_PREFIX);

            //Generate channels properties
            //generateChannelsProperties();
            generateElementsProperties(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL, FlumeConfiguratorConstants.CHANNELS_COMMON_PROPERTY_PROPERTIES_PREFIX,
                    FlumeConfiguratorConstants.CHANNELS_PARTIAL_PROPERTY_PROPERTIES_PREFIX);

            //Generate sinks properties
            //generateSinksProperties();
            generateElementsProperties(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK, FlumeConfiguratorConstants.SINKS_COMMON_PROPERTY_PROPERTIES_PREFIX,
                    FlumeConfiguratorConstants.SINKS_PARTIAL_PROPERTY_PROPERTIES_PREFIX);

            //printConfiguration();

            //Write properties file
            writeBaseConfigurationPropertiesFile(true);


            logger.info("The process has ended correctly. The output file is in " + pathPropertiesGeneratedFile);
            logger.info("******* END FLUME PROPERTIES GENERATOR PROCESS *****************");
            return true;

        } catch (FileNotFoundException e) {
            logger.error("JSON topoloty file not found", e);
            return false;
        } catch (IOException e) {
            logger.error("An error has occurred on the load of JSON topology file", e);
            return false;
        } catch (Throwable t) {
            t.printStackTrace();
            logger.error("An error has occurred on the process", t);
            return false;
        }

    }


    /**
     * Main method.
     * @param args String array with the parameters of the execution.
     */
    public static void main(String[] args) {

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource("log4j.properties");
        PropertyConfigurator.configure(url);


        logger.info("******* BEGIN FLUME PROPERTIES GENERATOR PROCESS *****************");


        try {


            if (args.length == 3) {
                pathJSONTopology = args[0];
                generateFlumeConfigurationFiles = Boolean.valueOf(args[1]);
                pathPropertiesGeneratedFile = args[2];

                logger.info("Parameter pathJSONTopology: " + pathJSONTopology);
                logger.info("Parameter pathPropertiesGeneratedFile: " + pathPropertiesGeneratedFile);

                FlumeTopologyPropertiesGenerator flumeTopologyPropertiesGenerator = new FlumeTopologyPropertiesGenerator();

                flumeTopologyPropertiesGenerator.generateInputProperties();


            } else {
                logger.error("Incorrect parameters number. Parameters are: pathJSONTopology pathPropertiesGeneratedFile");
                logger.info("******* END FLUME PROPERTIES GENERATOR PROCESS *****************");

            }

        } catch (Exception e) {
            logger.error("An error has occurred in Flume configurator. Check the properties configuration file and the generated logs", e);
            logger.info("******* END FLUME PROPERTIES GENERATOR PROCESS *****************");

        }
    }

}
