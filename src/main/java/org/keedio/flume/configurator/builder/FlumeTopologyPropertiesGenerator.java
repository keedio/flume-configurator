package org.keedio.flume.configurator.builder;

import org.apache.log4j.PropertyConfigurator;

import org.keedio.flume.configurator.constants.FlumeConfiguratorConstants;
import org.keedio.flume.configurator.exceptions.FlumeConfiguratorException;
import org.keedio.flume.configurator.structures.FlumeTopology;
import org.keedio.flume.configurator.structures.LinkedProperties;
import org.keedio.flume.configurator.structures.TopologyPropertyBean;
import org.keedio.flume.configurator.topology.GraphFactory;
import org.keedio.flume.configurator.topology.IGraph;
import org.keedio.flume.configurator.topology.JSONStringSerializer;
import org.keedio.flume.configurator.utils.FlumeConfiguratorTopologyUtils;
import org.slf4j.LoggerFactory;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.*;

public class FlumeTopologyPropertiesGenerator {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FlumeTopologyPropertiesGenerator.class);


    private static String  pathJSONTopology;
    private static boolean generateBaseConfigurationFiles = false;

    private static boolean multipleAgentConfigurationFiles = false;
    private static boolean addComments = true;
    private static boolean computeTreeAsGraph = false;
    private static String pathBasePropertiesGeneratedFile;
    private static String pathConfigurationGeneratedFile;
    private static double ratioCommonProperty = 1.0d;

    private byte[] flumeJSONTopology;
    private List<FlumeTopology> flumeTopologyList;
    private Map<String, FlumeTopology> mapTopology;
    private List<FlumeTopology> listTopologyAgents;
    private List<FlumeTopology> listTopologySources;
    private List<FlumeTopology> listTopologyConnections;
    private List<FlumeTopology> listTopologyInterceptors;
    private Map<String, DefaultMutableTreeNode> flumeTreeTopology = new LinkedHashMap<>();
    private Map<String, IGraph> flumeGraphTopology = new LinkedHashMap<>();

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
     * @param pathJSONTopology the pathJSONTopology to set
     */
    public static void setPathJSONTopology(String pathJSONTopology) {
        FlumeTopologyPropertiesGenerator.pathJSONTopology = pathJSONTopology;
    }

    /**
     * @param generateBaseConfigurationFiles the generateBaseConfigurationFiles to set
     */
    public static void setGenerateBaseConfigurationFiles(boolean generateBaseConfigurationFiles) {
        FlumeTopologyPropertiesGenerator.generateBaseConfigurationFiles = generateBaseConfigurationFiles;
    }

    /**
     * @param multipleAgentConfigurationFiles the multipleAgentConfigurationFiles to set
     */
    public static void setMultipleAgentConfigurationFiles(boolean multipleAgentConfigurationFiles) {
        FlumeTopologyPropertiesGenerator.multipleAgentConfigurationFiles = multipleAgentConfigurationFiles;
    }

    /**
     * @param addComments the addComments to set
     */
    public static void setAddComments(boolean addComments) {
        FlumeTopologyPropertiesGenerator.addComments = addComments;
    }

    /**
     * @param pathBasePropertiesGeneratedFile the pathBasePropertiesGeneratedFile to set
     */
    public static void setPathBasePropertiesGeneratedFile(String pathBasePropertiesGeneratedFile) {
        FlumeTopologyPropertiesGenerator.pathBasePropertiesGeneratedFile = pathBasePropertiesGeneratedFile;
    }

    /**
     * @param pathConfigurationGeneratedFile the pathConfigurationGeneratedFile to set
     */
    public static void setPathConfigurationGeneratedFile(String pathConfigurationGeneratedFile) {
        FlumeTopologyPropertiesGenerator.pathConfigurationGeneratedFile = pathConfigurationGeneratedFile;
    }

    /**
     * @param computeTreeAsGraph the computeTreeAsGraph to set
     */
    public static void setComputeTreeAsGraph(boolean computeTreeAsGraph) {
        FlumeTopologyPropertiesGenerator.computeTreeAsGraph = computeTreeAsGraph;
    }

    /**
     *
     * @return the flumeTopologyList
     */
    public List<FlumeTopology> getFlumeTopologyList() {
        return flumeTopologyList;
    }

    /**
     * @return the mapTopology
     */
    public Map<String, FlumeTopology> getMapTopology() {
        return mapTopology;
    }

    /**
     * @return the listTopologyAgents
     */
    public List<FlumeTopology> getListTopologyAgents() {
        return listTopologyAgents;
    }

    /**
     * @return the listTopologySources
     */
    public List<FlumeTopology> getListTopologySources() {
        return listTopologySources;
    }

    /**
     * @return the listTopologyConnections
     */
    public List<FlumeTopology> getListTopologyConnections() {
        return listTopologyConnections;
    }

    /**
     * @return the listTopologyInterceptors
     */
    public List<FlumeTopology> getListTopologyInterceptors() {
        return listTopologyInterceptors;
    }

    /**
     * @return the flumeTreeTopology
     */
    public Map<String, DefaultMutableTreeNode> getFlumeTreeTopology() {
        return flumeTreeTopology;
    }

    /**
     * @return the flumeGraphTopology
     */
    public Map<String, IGraph> getFlumeGraphTopology() {
        return flumeGraphTopology;
    }

    /**
     * @return the isTreeCompliant
     */
    public boolean isTreeCompliant() {
        return isTreeCompliant;
    }

    /**
     *
     * @return the flumeConfigurationProperties
     */
    public Properties getFlumeConfigurationProperties() {
        return flumeConfigurationProperties;
    }

    /**
     * @param ratioCommonProperty the ratioCommonProperty to set
     */
    public static void setRatioCommonProperty(double ratioCommonProperty) {
        FlumeTopologyPropertiesGenerator.ratioCommonProperty = ratioCommonProperty;
    }



    /**
     * Load the properties file
     * @throws IOException
     */
    private void loadJSONTopologyFile() throws IOException {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN loadJSONTopologyFile");
        }

        flumeJSONTopology = Files.readAllBytes(Paths.get(pathJSONTopology));

        if (logger.isInfoEnabled()) {
            String topologyStr = new String(flumeJSONTopology);
            logger.info(topologyStr);
        }

        flumeTopologyList =  Arrays.asList(JSONStringSerializer.fromBytes(flumeJSONTopology, FlumeTopology[].class));

        if (logger.isDebugEnabled()) {
            logger.debug(flumeTopologyList.toString());
        }


        if (logger.isDebugEnabled()) {
            logger.debug("END loadJSONTopologyFile");
        }
    }

    /**
     * Create the needed structures (tree or graph)
     */
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

        if (computeTreeAsGraph) {
            isTreeCompliant = false;
        } else {
            //Detect if the topology is a tree or is a graph from connections list
            isTreeCompliant = FlumeConfiguratorTopologyUtils.isTreeCompliant(withAgentNodes, listTopologyConnections, mapTopology.size(), listTopologySources.size(), listTopologyAgents.size());
        }


        if (isTreeCompliant) {

            //Check only one agent per node
            if (!FlumeConfiguratorTopologyUtils.checkOnlyOneAgentPerNode(flumeTopologyList, listTopologyConnections)) {
                throw new FlumeConfiguratorException("There are nodes sharing agents");
            }

            //Create a tree with structure described in topology
            createTreeTopology(withAgentNodes);

            //Show tree structure
            if (logger.isInfoEnabled()) {
                logger.info(FlumeConfiguratorTopologyUtils.renderFlumeTopology(flumeTreeTopology.values()));
            }

            getElementsNumber();

            setSourcesChannelsProperty();

            setSinksChannelProperty();


        } else {

            //Check only one agent per node
            if (!FlumeConfiguratorTopologyUtils.checkOnlyOneAgentPerNode(flumeTopologyList, listTopologyConnections)) {
                throw new FlumeConfiguratorException("There are nodes sharing agents");
            }

            //Create a graph with structure described in topology
            createGraphTopology(withAgentNodes);

            validateFlumeTopologyGraphs();


            //Show graph structure
            if (logger.isInfoEnabled()) {
                logger.info(FlumeConfiguratorTopologyUtils.renderFlumeTopologyGraph(flumeGraphTopology.values(), true));
            }

            getElementsNumber();

            setSourcesChannelsProperty();

            setSinksChannelProperty();
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END createInitialStructures");
        }
    }


    /**
     * Create a tree topology for the elements
     * @param withAgentNodes true if there are real agent node(s) , false otherwise
     */
    private void createTreeTopology(boolean withAgentNodes) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN createTreeTopology");
        }

        Set<String> agentsList = new LinkedHashSet<>();
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

                flumeTreeTopology.put(agentName, flumeTopologyAgentRootNode);

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

                    DefaultMutableTreeNode flumeTopologyAgentRootNode = flumeTreeTopology.get(agentName);

                    if (flumeTopologyAgentRootNode != null) {
                        flumeTopologyAgentRootNode.add(flumeTopologyNode);
                    } else {
                        throw new FlumeConfiguratorException("flumeTreeTopology doesn't contain root node for agent " + agentName);
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

                    flumeTreeTopology.put(agentName, flumeTopologyNode);
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
                throw new FlumeConfiguratorException("sourceConnectionNode " + sourceConnection + " or targetConnectionNode  " + targetConnection + " are not present on node's pool");
            } else {
                //Link sourceNode with targetNode
                sourceConnectionNode.add(targetConnectionNode);
            }

        }

        if (logger.isDebugEnabled()) {
            logger.debug("END createTreeTopology");
        }

    }

    /**
     * Create a graph topology for the elements
     * @param withAgentNodes  true if there are real agent node(s), false otherwise
     */
    private void createGraphTopology(boolean withAgentNodes) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN createGraphTopology");
        }

        Set<String> agentsList = new LinkedHashSet<>();

        if (!withAgentNodes) {

            //Get agents list from sources list
            for (FlumeTopology source : listTopologySources) {
                //Check if the source's agent name is already in tree
                String agentName = source.getAgentName();
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

                //Generate graph
                IGraph igraph = GraphFactory.createGraph("jgrapht");

                FlumeTopology flumeTopologyAgentRoot = new FlumeTopology(FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT, agentName);
                flumeTopologyAgentRoot.getData().put(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME, agentName);

                igraph.addGraphVertex(flumeTopologyAgentRoot);

                flumeGraphTopology.put(agentName, igraph);

            }

            //Create nodes for all elements in topology. Sources nodes will be link to their agent node.
            for (String flumeTopologyElementId : mapTopology.keySet()) {
                IGraph graphAgent = null;
                FlumeTopology flumeTopologyElement = mapTopology.get(flumeTopologyElementId);

                String flumeTopologyElementType = flumeTopologyElement.getType();

                //Get the agent graph
                String agentName = FlumeConfiguratorTopologyUtils.getGraphAgentFromConnections(flumeTopologyElementId,listTopologyConnections,flumeTopologyList,withAgentNodes);

                if ((agentName != null) && (!"".equals(agentName))) {

                    graphAgent = flumeGraphTopology.get(agentName);
                    if (graphAgent != null) {
                        graphAgent.addGraphVertex(flumeTopologyElement);
                    } else {
                        throw new FlumeConfiguratorException("flumeGraphTopology doesn't contain root node for agent " + agentName);
                    }
                } else {
                    throw new FlumeConfiguratorException("The agent name can't be obtained for the node id: " + flumeTopologyElementId);
                }


                if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE.equals(flumeTopologyElementType)) {
                    //Add edge between agent and source
                    FlumeTopology agentVertex = graphAgent.getVertex(agentName, withAgentNodes);
                    graphAgent.addGraphEdge(agentVertex, flumeTopologyElement);
                }
            }

        } else {

            //Get the agents
            for (FlumeTopology agent : listTopologyAgents) {

                String agentName = agent.getAgentName();

                //Generate graph
                IGraph igraph = GraphFactory.createGraph("jgrapht");

                igraph.addGraphVertex(agent);

                flumeGraphTopology.put(agentName, igraph);

            }


            //Create nodes for all elements in topology (except Agents).
            for (String flumeTopologyElementId : mapTopology.keySet()) {
                IGraph graphAgent = null;
                FlumeTopology flumeTopologyElement = mapTopology.get(flumeTopologyElementId);

                String flumeTopologyElementType = flumeTopologyElement.getType();

                if (!FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT.equals(flumeTopologyElementType)) {

                    //Get the agent graph
                    String agentName = FlumeConfiguratorTopologyUtils.getGraphAgentFromConnections(flumeTopologyElementId,listTopologyConnections,flumeTopologyList,withAgentNodes);

                    if ((agentName != null) && (!"".equals(agentName))) {

                        graphAgent = flumeGraphTopology.get(agentName);
                        if (graphAgent != null) {
                            graphAgent.addGraphVertex(flumeTopologyElement);
                        } else {
                            throw new FlumeConfiguratorException("flumeGraphTopology doesn't contain root node for agent " + agentName);
                        }
                    } else {
                        throw new FlumeConfiguratorException("The agent name can't be obtained for the node id: " + flumeTopologyElementId);
                    }

                }

            }

        }

        //Create edges for the rest of vertex
        for (FlumeTopology connection : listTopologyConnections) {
            String sourceConnection = connection.getSourceConnection();
            String targetConnection = connection.getTargetConnection();

            String sourceAgentName = FlumeConfiguratorTopologyUtils.getGraphAgentFromConnections(sourceConnection,listTopologyConnections,flumeTopologyList,withAgentNodes);
            String targetAgentName = FlumeConfiguratorTopologyUtils.getGraphAgentFromConnections(targetConnection,listTopologyConnections,flumeTopologyList,withAgentNodes);

            if (sourceAgentName == null || targetAgentName == null) {
                throw new FlumeConfiguratorException("sourceConnectionNode " + sourceConnection + " or targetConnectionNode  " + targetConnection + " are not present on node's pool");
            } else if (!sourceAgentName.equals(targetAgentName)) {
                throw new FlumeConfiguratorException("Edge between nodes from different agents is not possible");
            } else {
                IGraph graphAgent = flumeGraphTopology.get(sourceAgentName);
                if (graphAgent != null) {

                    FlumeTopology sourceVertex = graphAgent.getVertex(sourceConnection, withAgentNodes);
                    FlumeTopology targetVertex = graphAgent.getVertex(targetConnection, withAgentNodes);

                    graphAgent.addGraphEdge(sourceVertex, targetVertex);

                } else {
                    throw new FlumeConfiguratorException("flumeGraphTopology doesn't contain root node for agent " + sourceAgentName);
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END loadJSONTopologyFile");
        }
    }


    /**
     * Validate the graph structure
     */
    private void validateFlumeTopologyGraphs() {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN validateFlumeTopologyGraphs");
        }

        //Detect cycles in the graph
        for (String flumeGraphTopologyElementId : flumeGraphTopology.keySet()) {
            IGraph graphAgent = flumeGraphTopology.get(flumeGraphTopologyElementId);
            boolean withCycles = graphAgent.detectCycles();
            if (withCycles) {
                throw new FlumeConfiguratorException("There are cycles in graph of agent " + flumeGraphTopologyElementId);
            }
        }

        for (String agentName : flumeGraphTopology.keySet()) {
            IGraph agentGraph = flumeGraphTopology.get(agentName);

            Set<FlumeTopology> vertexSet = agentGraph.getVertexSet();

            for (FlumeTopology vertex : vertexSet) {

                String topologyName =  vertex.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                if (vertex.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT)) {
                    if (agentGraph.predecessorListOf(vertex).size() != 0) {
                        throw new FlumeConfiguratorException("The agent vertex have parents " + agentName);
                    }

                } else if (vertex.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE)) {
                    if (agentGraph.predecessorListOf(vertex).size() > 1) {
                        throw new FlumeConfiguratorException("The source vertex have multiple parents " + topologyName);
                    }

                } else if (vertex.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR)) {
                    if (agentGraph.predecessorListOf(vertex).size() > 1) {
                        throw new FlumeConfiguratorException("The interceptor vertex have multiple parents " + topologyName);
                    }

                } else if (vertex.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL)) {
                    if (agentGraph.successorListOf(vertex).size() > 1) {
                        throw new FlumeConfiguratorException("The channel vertex have multiple descendants " + topologyName);
                    }

                } else if (vertex.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK)) {
                    if (agentGraph.predecessorListOf(vertex).size() > 1) {
                        throw new FlumeConfiguratorException("The sink vertex have multiple parents " + topologyName);
                    }
                    if (agentGraph.successorListOf(vertex).size() != 0) {
                        throw new FlumeConfiguratorException("The sink vertex have multiple descendants " + topologyName);
                    }

                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END validateFlumeTopologyGraphs");
        }
    }


    /**
     * Show the number of elements group by type
     */
    private void getElementsNumber() {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN getElementsNumber");
        }

        if (isTreeCompliant) {
            agentsNumber = flumeTreeTopology.size();

            for (String agentName : flumeTreeTopology.keySet()) {

                DefaultMutableTreeNode agentRootNode = flumeTreeTopology.get(agentName);
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
            agentsNumber = flumeGraphTopology.size();

            for (String agentName : flumeGraphTopology.keySet()) {
                IGraph agentGraph = flumeGraphTopology.get(agentName);

                Set<FlumeTopology> vertexSet = agentGraph.getVertexSet();

                for (FlumeTopology agentVertex : vertexSet) {
                    if (agentVertex.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE)) {
                        sourcesNumber++;
                    } else if (agentVertex.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR)) {
                        interceptorsNumber++;
                    } else if (agentVertex.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL)) {
                        channelsNumber++;
                    } else if (agentVertex.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK)) {
                        sinksNumber++;
                    }
                }
            }

        }

        logger.debug("Agents number: " + agentsNumber);
        logger.debug("Sources number: " + sourcesNumber);
        logger.debug("Interceptors number: " + interceptorsNumber);
        logger.debug("Channels number: " + channelsNumber);
        logger.debug("Sinks number: " + sinksNumber);


        if (logger.isDebugEnabled()) {
            logger.debug("END getElementsNumber");
        }


    }

    /**
     * Set the channels property for every source
     */
    private void setSourcesChannelsProperty() {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN setSourcesChannelsProperty");
        }

        if (isTreeCompliant) {


            for (String agentName : flumeTreeTopology.keySet()) {

                DefaultMutableTreeNode agentRootNode = flumeTreeTopology.get(agentName);
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

            for (String agentName : flumeGraphTopology.keySet()) {

                IGraph agentGraph = flumeGraphTopology.get(agentName);

                FlumeTopology agentVertex = FlumeConfiguratorTopologyUtils.getAgentVertexFromGraph(agentGraph);

                //Get the sources of the agent vertex
                List<FlumeTopology> sourcesList = agentGraph.successorListOf(agentVertex);
                Collections.sort(sourcesList);

                //Get the descendants of the source
                for (FlumeTopology source : sourcesList) {

                    List<String> sourceChannelsList = new ArrayList<>();
                    if (source.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE)) {
                        Map<String,String> sourceProperties = source.getData();

                        Set<FlumeTopology> sourceChildren = FlumeConfiguratorTopologyUtils.convetTreeSet(agentGraph.getVertexDescendants(source));
                        Iterator<FlumeTopology> itSourceChildren = sourceChildren.iterator();

                        while (itSourceChildren.hasNext()) {
                            FlumeTopology sourceChild = itSourceChildren.next();

                            if (sourceChild.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL)) {
                                String channelName = sourceChild.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);
                                sourceChannelsList.add(channelName);
                            }
                        }


                        //Generate channels values
                        sourceProperties.put(FlumeConfiguratorConstants.CHANNELS_PROPERTY,FlumeConfiguratorTopologyUtils.listToString(sourceChannelsList, FlumeConfiguratorConstants.WHITE_SPACE));
                    }

                }
            }

        }

        if (logger.isDebugEnabled()) {
            logger.debug("END setSourcesChannelsProperty");
        }
    }


    /**
     * Set the channel property for every sink
     */
    private void setSinksChannelProperty() {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN setSinksChannelProperty");
        }

        if (isTreeCompliant) {


            for (String agentName : flumeTreeTopology.keySet()) {

                DefaultMutableTreeNode agentRootNode = flumeTreeTopology.get(agentName);
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

            for (String agentName : flumeGraphTopology.keySet()) {

                IGraph agentGraph = flumeGraphTopology.get(agentName);

                FlumeTopology agentVertex = FlumeConfiguratorTopologyUtils.getAgentVertexFromGraph(agentGraph);

                //Get the descendants of the agent vertex
                Set<FlumeTopology> agentVertexChildren = FlumeConfiguratorTopologyUtils.convetTreeSet(agentGraph.getVertexDescendants(agentVertex));

                for (FlumeTopology agentVertexChild : agentVertexChildren) {

                    if (agentVertexChild.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK)) {
                        Map<String, String> sinkProperties = agentVertexChild.getData();

                        List<FlumeTopology> channelsList = agentGraph.predecessorListOf(agentVertexChild);
                        Collections.sort(channelsList);

                        for (FlumeTopology channel : channelsList) {

                            if (channel.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL)) {
                                String channelName = channel.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                                //Generate channels values
                                sinkProperties.put(FlumeConfiguratorConstants.CHANNEL_PROPERTY,channelName);
                            }

                        }

                    }
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END setSinksChannelProperty");
        }
    }


    /**
     * Generate agents list property
     */
    private void generateAgentListProperty() {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN generateAgentListProperty");
        }

        if (isTreeCompliant) {

            ArrayList<String> agentsList = new ArrayList<>();
            for (String agentName : flumeTreeTopology.keySet()) {
                agentsList.add(agentName);
            }

            flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, FlumeConfiguratorConstants.AGENTS_LIST_PROPERTIES_PREFIX, agentsList,
                    FlumeConfiguratorConstants.PROPERTY_SEPARATOR_DEFAULT);

        } else {

            ArrayList<String> agentsList = new ArrayList<>();
            for (String agentName : flumeGraphTopology.keySet()) {
                agentsList.add(agentName);
            }

            Collections.sort(agentsList);

            flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, FlumeConfiguratorConstants.AGENTS_LIST_PROPERTIES_PREFIX, agentsList,
                    FlumeConfiguratorConstants.PROPERTY_SEPARATOR_DEFAULT);

        }

        if (logger.isDebugEnabled()) {
            logger.debug("END generateAgentListProperty");
        }

    }



    /**
     * Generate properties for different kinds of elements
     * @param topologyType Type of element (SOURCE, INTERCEPTOR, CHANNEL, SINK)
     * @param elementsListPropertiesPrefix Prefix of the property for the type of element
     */
    private void generateElementsListProperties(String topologyType, String elementsListPropertiesPrefix) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN generateElementsListProperties");
        }

        if (isTreeCompliant) {

            for (String agentName : flumeTreeTopology.keySet()) {

                DefaultMutableTreeNode agentRootNode = flumeTreeTopology.get(agentName);
                Enumeration agentTreeNodes = agentRootNode.preorderEnumeration();
                List<String> agentElementsList = new ArrayList<>();
                while (agentTreeNodes.hasMoreElements()) {
                    DefaultMutableTreeNode agentTreeNode = (DefaultMutableTreeNode) agentTreeNodes.nextElement();
                    FlumeTopology flumeTopologyElement = (FlumeTopology) agentTreeNode.getUserObject();

                    if (flumeTopologyElement.getType().equals(topologyType)) {
                        String elementName = flumeTopologyElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);
                        agentElementsList.add(elementName);
                    }
                }

                String propertyKey = FlumeConfiguratorTopologyUtils.getKeyPropertyString(elementsListPropertiesPrefix, FlumeConfiguratorConstants.DOT_SEPARATOR,
                        agentName);
                flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, propertyKey, agentElementsList,
                        FlumeConfiguratorConstants.PROPERTY_SEPARATOR_DEFAULT);
            }


        } else {

            for (String agentName : flumeGraphTopology.keySet()) {


                IGraph agentGraph = flumeGraphTopology.get(agentName);
                FlumeTopology agentVertex = FlumeConfiguratorTopologyUtils.getAgentVertexFromGraph(agentGraph);

                //Get the descendants of the agent vertex
                Set<FlumeTopology> agentVertexChildren = FlumeConfiguratorTopologyUtils.convetTreeSet(agentGraph.getVertexDescendants(agentVertex));
                List<String> agentElementsList = new ArrayList<>();

                for (FlumeTopology agentVertexChild : agentVertexChildren) {

                    if (agentVertexChild.getType().equals(topologyType)) {
                        String elementName = agentVertexChild.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);
                        agentElementsList.add(elementName);
                    }
                }

                String propertyKey = FlumeConfiguratorTopologyUtils.getKeyPropertyString(elementsListPropertiesPrefix, FlumeConfiguratorConstants.DOT_SEPARATOR,
                        agentName);
                flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, propertyKey, agentElementsList,
                        FlumeConfiguratorConstants.PROPERTY_SEPARATOR_DEFAULT);
            }

        }

        if (logger.isDebugEnabled()) {
            logger.debug("END generateElementsListProperties");
        }

    }


    /**
     * Generate groups properties
     * @param groupPerAgentSource true if we want a group for every source, false
     */
    private void generateGroupsListProperties(boolean groupPerAgentSource) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN generateGroupsListProperties");
        }

        if (isTreeCompliant) {

            for (String agentName : flumeTreeTopology.keySet()) {

                DefaultMutableTreeNode agentRootNode = flumeTreeTopology.get(agentName);

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

                            if ((flumeTopologyElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE)) ||
                                    (flumeTopologyElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL)) ||
                                    (flumeTopologyElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK))){
                                String elementName = flumeTopologyElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);
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

                        if ((flumeTopologyElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE)) ||
                                (flumeTopologyElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL)) ||
                                (flumeTopologyElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK))){
                            String elementName = flumeTopologyElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);
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

            for (String agentName : flumeGraphTopology.keySet()) {

                IGraph agentGraph = flumeGraphTopology.get(agentName);
                FlumeTopology agentVertex = FlumeConfiguratorTopologyUtils.getAgentVertexFromGraph(agentGraph);

                //Detect elements with two sources (group by agent source only is possible when elements have one single source
                Set<FlumeTopology> agentVertexChildren = agentGraph.getVertexDescendants(agentVertex);
                Iterator<FlumeTopology> itAgentVertexChildren = agentVertexChildren.iterator();
                while (itAgentVertexChildren.hasNext()) {
                    FlumeTopology agentVertexChild = itAgentVertexChildren.next();

                    Set<FlumeTopology> agentVertexChildParents = agentGraph.getVertexAncestors(agentVertexChild);
                    Iterator<FlumeTopology> itAgentVertexChildParents = agentVertexChildParents.iterator();

                    int sourcesNumber = 0;
                    while (itAgentVertexChildParents.hasNext()) {
                        FlumeTopology agentVertexChildParent = itAgentVertexChildParents.next();

                        if (agentVertexChildParent.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE)) {
                            sourcesNumber++;
                        }

                    }

                    if (sourcesNumber > 1) {
                        logger.debug("The element has more than one source. Groups must be done by agent not by source");
                        groupPerAgentSource = false;
                    }
                }


                if (groupPerAgentSource) {

                    //Get the sources of the agent vertex
                    List<FlumeTopology> sourcesList = agentGraph.successorListOf(agentVertex);
                    Collections.sort(sourcesList);

                    //Get the descendants of the source
                    for (FlumeTopology source : sourcesList) {

                        String sourceName = source.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);
                        List<String> sourceGroupsList = new ArrayList<>();

                        if (source.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE)) {
                            String elementName = source.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);
                            sourceGroupsList.add(elementName);

                            Set<FlumeTopology> sourceChildren = FlumeConfiguratorTopologyUtils.convetTreeSet(agentGraph.getVertexDescendants(source));
                            Iterator<FlumeTopology> itSourceChildren = sourceChildren.iterator();

                            while (itSourceChildren.hasNext()) {
                                FlumeTopology sourceChild = itSourceChildren.next();

                                if ((sourceChild.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE)) ||
                                        (sourceChild.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL)) ||
                                        (sourceChild.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK))){
                                    elementName = sourceChild.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);
                                    sourceGroupsList.add(elementName);
                                }

                            }


                            String propertyKey = FlumeConfiguratorTopologyUtils.getKeyPropertyString(FlumeConfiguratorConstants.GROUPS_LIST_PROPERTIES_PREFIX, FlumeConfiguratorConstants.DOT_SEPARATOR,
                                    agentName, FlumeConfiguratorConstants.DOT_SEPARATOR, FlumeConfiguratorConstants.FLUME_TOPOLOGY_GROUP_NAME_DEFAULT,
                                    FlumeConfiguratorConstants.UNDERSCORE_SEPARATOR, sourceName.toUpperCase());
                            flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, propertyKey, sourceGroupsList,
                                    FlumeConfiguratorConstants.PROPERTY_SEPARATOR_DEFAULT);

                        }

                    }

                } else {

                    //Get the descendants of the agent vertex
                    agentVertexChildren = agentGraph.getVertexDescendants(agentVertex);
                    List<String> agentElementsList = new ArrayList<>();

                    for (FlumeTopology agentVertexChild : agentVertexChildren) {

                        if ((agentVertexChild.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE)) ||
                                (agentVertexChild.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL)) ||
                                (agentVertexChild.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK))){
                            String elementName = agentVertexChild.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);
                            agentElementsList.add(elementName);
                        }
                    }

                    Collections.sort(agentElementsList);

                    String propertyKey = FlumeConfiguratorTopologyUtils.getKeyPropertyString(FlumeConfiguratorConstants.GROUPS_LIST_PROPERTIES_PREFIX, FlumeConfiguratorConstants.DOT_SEPARATOR,
                            agentName,  FlumeConfiguratorConstants.DOT_SEPARATOR, FlumeConfiguratorConstants.FLUME_TOPOLOGY_GROUP_NAME_DEFAULT,
                            FlumeConfiguratorConstants.UNDERSCORE_SEPARATOR, agentName.toUpperCase());
                    flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, propertyKey, agentElementsList,
                            FlumeConfiguratorConstants.PROPERTY_SEPARATOR_DEFAULT);

                }

            }

        }

        if (logger.isDebugEnabled()) {
            logger.debug("END generateGroupsListProperties");
        }


    }

    /**
     * Generate properties for interceptors
     */
    private void generateInterceptorsListProperties() {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN generateInterceptorsListProperties");
        }

        if (isTreeCompliant) {

            for (String agentName : flumeTreeTopology.keySet()) {

                DefaultMutableTreeNode agentRootNode = flumeTreeTopology.get(agentName);
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

            for (String agentName : flumeGraphTopology.keySet()) {

                IGraph agentGraph = flumeGraphTopology.get(agentName);
                FlumeTopology agentVertex = FlumeConfiguratorTopologyUtils.getAgentVertexFromGraph(agentGraph);

                //Get the sources of the agent vertex
                List<FlumeTopology> sourcesList = agentGraph.successorListOf(agentVertex);
                Collections.sort(sourcesList);

                //Get the descendants of the source
                for (FlumeTopology source : sourcesList) {

                    String sourceName = source.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);
                    List<String> sourceInterceptorsList = new ArrayList<>();

                    if (source.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE)) {

                        Set<FlumeTopology> sourceChildren = FlumeConfiguratorTopologyUtils.convetTreeSet(agentGraph.getVertexDescendants(source));
                        Iterator<FlumeTopology> itSourceChildren = sourceChildren.iterator();

                        while (itSourceChildren.hasNext()) {
                            FlumeTopology sourceChild = itSourceChildren.next();

                            if (sourceChild.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR)) {
                                String interceptorName = sourceChild.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);
                                sourceInterceptorsList.add(interceptorName);
                            }

                        }

                        //Get the interceptors chain ordered
                        List<String> sourceInterceptorsOrderedList = FlumeConfiguratorTopologyUtils.orderSourceInterceptorsFromConnections(sourceInterceptorsList, source.getId(), listTopologyConnections, flumeTopologyList);

                        String propertyKey = FlumeConfiguratorConstants.INTERCEPTORS_LIST_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + sourceName;

                        //flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, propertyKey, sourceInterceptorsList,
                        //        FlumeConfiguratorConstants.PROPERTY_SEPARATOR_DEFAULT);
                        flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, propertyKey, sourceInterceptorsOrderedList,
                                FlumeConfiguratorConstants.PROPERTY_SEPARATOR_DEFAULT);

                    }

                }

            }

        }

        if (logger.isDebugEnabled()) {
            logger.debug("END generateInterceptorsListProperties");
        }


    }



    /**
     * Generate properties for different kinds of elements
     * @param topologyType Type of element
     * @param commonPropertiesPrefix Prefix for common properties
     * @param partialPropertiesPrefix Prefix for partial properties
     */
    private void generateElementsProperties(String topologyType, String commonPropertiesPrefix, String partialPropertiesPrefix) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN generateElementsProperties");
        }

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

        if (isTreeCompliant) {

            for (String agentName : flumeTreeTopology.keySet()) {

                DefaultMutableTreeNode agentRootNode = flumeTreeTopology.get(agentName);
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

        } else {

            for (String agentName : flumeGraphTopology.keySet()) {


                IGraph agentGraph = flumeGraphTopology.get(agentName);
                FlumeTopology agentVertex = FlumeConfiguratorTopologyUtils.getAgentVertexFromGraph(agentGraph);

                //Get the descendants of the agent vertex
                Set<FlumeTopology> agentVertexChildren = agentGraph.getVertexDescendants(agentVertex);

                for (FlumeTopology agentVertexChild : agentVertexChildren) {

                    if (agentVertexChild.getType().equals(topologyType)) {
                        String elementName = agentVertexChild.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);
                        Map<String, String> originalTopologyElementProperties = agentVertexChild.getData();
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
        }

        //Detect common properties
        for (String propertyKey : elementsMap.keySet()) {
            List<TopologyPropertyBean> listPropertyBeans = elementsMap.get(propertyKey);

            String valueCommonProperty = FlumeConfiguratorTopologyUtils.getValueCommonProperty(listPropertyBeans, elementsNumber, ratioCommonProperty);

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
            flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, commentPropertyKey, commentPropertyValue, true);
            flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, propertyKey, propertyValue, false);

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
                        //The comment of the first element (with value) will be the comment for all elements
                        if (partialProperty.getPropertyComment() == null || "".equals(partialProperty.getPropertyComment())) {
                            partialProperty.setPropertyComment(partialPropertyComment);
                        }


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

                flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, partialPropertyKeyComment, partialProperty.getPropertyComment(), true);
                flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, partialPropertyKeyAppliedElements, partialProperty.getAppliedElement(), false);
                flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, partialPropertyKeyValues, partialProperty.getPropertyValue(), false);
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END generateElementsProperties");
        }

    }



    /**
     * Generate the Flume Configuration properties file and optionally the base configuration properties file
     * @throws IOException
     */
    private void writeConfigurationPropertiesFile() throws IOException {


        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN writeConfigurationPropertiesFile");
        }

        String fileName;
        BufferedWriter bw;

        if (pathConfigurationGeneratedFile == null || "".equals(pathConfigurationGeneratedFile)) {
            throw new InvalidPathException("", "The path is not valid");
        }

        //Get the base configuration properties
        String baseConfigurationPropertiesString = FlumeConfiguratorTopologyUtils.getFlumePropertiesAsString(flumeConfigurationProperties);

        logger.info(baseConfigurationPropertiesString);

        //Write Flume configuration properties file
        FlumePropertiesGenerator flumePropertiesGenerator = new FlumePropertiesGenerator();
        flumePropertiesGenerator.buildConfigurationMapFromStringProperties(baseConfigurationPropertiesString,FlumeConfiguratorConstants.PROPERTY_SEPARATOR_DEFAULT, addComments, true, pathConfigurationGeneratedFile, multipleAgentConfigurationFiles);

        if (generateBaseConfigurationFiles) {

            if (pathBasePropertiesGeneratedFile == null || "".equals(pathBasePropertiesGeneratedFile)) {
                throw new InvalidPathException("", "The path is not valid");
            }

            File basePropertiesGeneratedFile = new File(pathBasePropertiesGeneratedFile);
            boolean isDirectory = basePropertiesGeneratedFile.isDirectory();


            if (isDirectory) {
                //The path of the file is determinated but not the name of the configuration file
                fileName = pathBasePropertiesGeneratedFile + File.separator + FlumeConfiguratorConstants.CONFIGURATION_BASE_PROPERTIES_FILE;
            } else {
                //The full path has been determinated
                fileName = pathBasePropertiesGeneratedFile;
            }

            bw = new BufferedWriter(new FileWriter(fileName));

            //Write the content
            bw.write(baseConfigurationPropertiesString);

            bw.flush();
            bw.close();

        }

        if (logger.isDebugEnabled()) {
            logger.debug("END writeConfigurationPropertiesFile");
        }

    }


    /**
     * Generate a Flume configuration from a Draw2D Flume topology (json format)
     * @param jsonTopology String with JSON topology
     * @param generateBaseConfigurationProperties true -> the base configuration properties is generated
     *                                            false -> otherwise
     * @param generateFlumeConfigurationProperties true -> the flume configuration properties is generated,
     *                                             false -> otherwise
     * @return Map with the content of properties generated (base configuration properties and flume configuration properties)
     */
    public Map<String, String> generateInputPropertiesFromDraw2DFlumeTopology(String jsonTopology, boolean generateBaseConfigurationProperties, boolean generateFlumeConfigurationProperties) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN generateInputPropertiesFromDraw2DFlumeTopology");
        }

        String baseConfigurationPropertiesString = null;
        String flumeConfigurationPropertiesString = null;

        List<String> listConfigurationProperties = new ArrayList<>();
        Map<String, String> mapConfigurationProperties = new HashMap<>();

        try {

            flumeJSONTopology = jsonTopology.getBytes();
            flumeTopologyList =  Arrays.asList(JSONStringSerializer.fromBytes(flumeJSONTopology, FlumeTopology[].class));

            logger.debug(flumeTopologyList.toString());


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

            if (logger.isDebugEnabled()) {
                logger.debug(FlumeConfiguratorTopologyUtils.getPropertyAsString(flumeConfigurationProperties));
            }


            //Generation of base configuration properties
            if (generateBaseConfigurationProperties) {
                baseConfigurationPropertiesString = FlumeConfiguratorTopologyUtils.getFlumePropertiesAsString(flumeConfigurationProperties);

                if (logger.isDebugEnabled()) {
                    logger.debug(baseConfigurationPropertiesString);
                }

                //listConfigurationProperties.add(baseConfigurationPropertiesString);
                mapConfigurationProperties.put(FlumeConfiguratorConstants.BASE_CONFIGURATION_KEY, baseConfigurationPropertiesString);
            }

            //Generation of flume configuration properties
            if (generateFlumeConfigurationProperties) {
                FlumePropertiesGenerator flumePropertiesGenerator = new FlumePropertiesGenerator();

                baseConfigurationPropertiesString = FlumeConfiguratorTopologyUtils.getFlumePropertiesAsString(flumeConfigurationProperties);

                flumeConfigurationPropertiesString = flumePropertiesGenerator.buildConfigurationMapFromStringProperties(baseConfigurationPropertiesString,FlumeConfiguratorConstants.PROPERTY_SEPARATOR_DEFAULT,true, false, null, false);

                if (logger.isDebugEnabled()) {
                    logger.debug(flumeConfigurationPropertiesString);
                }

                //listConfigurationProperties.add(flumeConfigurationPropertiesString);
                mapConfigurationProperties.put(FlumeConfiguratorConstants.FLUME_CONFIGURATION_KEY, flumeConfigurationPropertiesString);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("END generateInputPropertiesFromDraw2DFlumeTopology");
            }

            return mapConfigurationProperties;

        } catch (IOException e) {
            logger.error("An error has occurred on the load of JSON topology file", e);
            return null;
        } catch (Throwable t) {
            logger.error("An error has occurred on the process", t);
            return null;
        }
    }


    /**
     * Generate the input properties to Flume Configurator from a JSON Topology file
     * @return true if the process is correct, false otherwise
     */
    public boolean generateInputProperties() {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN generateInputProperties");
        }

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


            if (logger.isDebugEnabled()) {
                logger.debug(FlumeConfiguratorTopologyUtils.getPropertyAsString(flumeConfigurationProperties));
            }


            //Write properties file
            writeConfigurationPropertiesFile();


            logger.info("The process has ended correctly. The output file is in " + pathConfigurationGeneratedFile);
            if (pathBasePropertiesGeneratedFile != null) {
                logger.info("The base configuration properties output file is in " + pathBasePropertiesGeneratedFile);
            }
            logger.info("******* END FLUME PROPERTIES GENERATOR PROCESS *****************");

            if (logger.isDebugEnabled()) {
                logger.debug("END generateInputProperties");
            }

            return true;

        } catch (NoSuchFileException e) {
            logger.error("JSON topology file not found", e);
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
     * Get associated message to an error code
     * @param errorCode error code
     * @return associated message to an error code
     */
    public static String getErrorMessage(int errorCode) {

        String error = "";
        StringBuilder sb = new StringBuilder(10000);

        if (errorCode == 1) {
            sb.append("An error has occurred in Flume Topology properties generator. Check the properties configuration file and generated logs");
        } else if (errorCode == 2) {

            sb.append("Incorrect parameter number");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("Usage: java -cp jarFile org.keedio.flume.configurator.builder.FlumeTopologyPropertiesGenerator <parameters>");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("The parameters are:");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("pathJSONTopology => Path of the Draw2D Flume Topology configuration file");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("multipleAgentConfigurationFiles => (boolean)");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                                     true -> Every agent has an own configuration file");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                                     false -> All agents configuration in one single file");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("addComments => (boolean)");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                 true -> Property comments are added to Flume configuration file");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                 false -> Property comments are not added to Flume configuration file");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("computeTreeAsGraph => (boolean)");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                         true -> The tree topology is processed with a graph library");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                         false -> The tree topology is processed without any graph library");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("ratioCommonProperty => Ratio used for determinate if a property is considered as a common property or not (generation of template configuration file)");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("pathPropertiesGeneratedFile => Path of the created flume configuration file(s).May be a directory if several");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                               configuration files are created (the directory must be exist)");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("pathBasePropertiesGeneratedFile => (Optional parameter) Path of the created base (template) configuration file.May be a directory (the directory must be exist)");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
        }

        return sb.toString();
    }

    /**
     * Main method.
     * @param args String array with the parameters of the execution.
     */
    public static void main(String[] args) {

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource("log4j.properties");
        PropertyConfigurator.configure(url);

        if (logger.isDebugEnabled()) {
            logger.debug("******* BEGIN FLUME TOPOLOGY PROPERTIES GENERATOR PROCESS *****************");
        }

        try {

            if (args.length == 6) {

                pathJSONTopology = args[0];
                multipleAgentConfigurationFiles = Boolean.valueOf(args[1]);
                addComments = Boolean.valueOf(args[2]);
                computeTreeAsGraph = Boolean.valueOf(args[3]);
                ratioCommonProperty = Double.valueOf(args[4]);
                pathConfigurationGeneratedFile = args[5];

                if (logger.isDebugEnabled()) {
                    logger.debug("Parameter pathJSONTopology: " + pathJSONTopology);
                    logger.debug("Parameter multipleAgentConfigurationFiles: " + multipleAgentConfigurationFiles);
                    logger.debug("Parameter addComments: " + addComments);
                    logger.debug("Parameter computeTreeAsGraph: " + computeTreeAsGraph);
                    logger.debug("Parameter ratioCommonProperty: " + ratioCommonProperty);
                    logger.debug("Parameter pathPropertiesGeneratedFile: " + pathConfigurationGeneratedFile);
                }

                FlumeTopologyPropertiesGenerator flumeTopologyPropertiesGenerator = new FlumeTopologyPropertiesGenerator();

                flumeTopologyPropertiesGenerator.generateInputProperties();

            } else if (args.length == 7) {

                pathJSONTopology = args[0];
                multipleAgentConfigurationFiles = Boolean.valueOf(args[1]);
                addComments = Boolean.valueOf(args[2]);
                computeTreeAsGraph = Boolean.valueOf(args[3]);
                ratioCommonProperty = Double.valueOf(args[4]);
                pathConfigurationGeneratedFile = args[5];
                generateBaseConfigurationFiles = true;
                pathBasePropertiesGeneratedFile = args[6];

                if (logger.isDebugEnabled()) {
                    logger.debug("Parameter pathJSONTopology: " + pathJSONTopology);
                    logger.debug("Parameter multipleAgentConfigurationFiles: " + multipleAgentConfigurationFiles);
                    logger.debug("Parameter addComments: " + addComments);
                    logger.debug("Parameter computeTreeAsGraph: " + computeTreeAsGraph);
                    logger.debug("Parameter ratioCommonProperty: " + ratioCommonProperty);
                    logger.debug("Parameter pathPropertiesGeneratedFile: " + pathConfigurationGeneratedFile);
                    logger.debug("Parameter pathBasePropertiesGeneratedFile: " + pathBasePropertiesGeneratedFile);
                }

                FlumeTopologyPropertiesGenerator flumeTopologyPropertiesGenerator = new FlumeTopologyPropertiesGenerator();

                flumeTopologyPropertiesGenerator.generateInputProperties();

            } else {
                logger.error(getErrorMessage(2));
                if (logger.isDebugEnabled()) {
                    logger.debug("******* END FLUME TOPOLOGY PROPERTIES GENERATOR PROCESS *****************");
                }
            }

        } catch (Exception e) {
            logger.error(getErrorMessage(1), e);
            if (logger.isDebugEnabled()) {
                logger.debug("******* END FLUME TOPOLOGY PROPERTIES GENERATOR PROCESS *****************");
            }
        }
    }

}
