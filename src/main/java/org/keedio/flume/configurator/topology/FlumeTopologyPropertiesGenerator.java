package org.keedio.flume.configurator.topology;

import org.apache.log4j.PropertyConfigurator;
import org.keedio.flume.configurator.constants.FlumeConfiguratorConstants;
import org.keedio.flume.configurator.exceptions.FlumeConfiguratorException;
import org.keedio.flume.configurator.structures.FlumeTopology;
import org.keedio.flume.configurator.structures.LinkedProperties;
import org.keedio.flume.configurator.utils.FlumeConfiguratorTopologyUtils;
import org.keedio.flume.configurator.utils.FlumeConfiguratorUtils;
import org.slf4j.LoggerFactory;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class FlumeTopologyPropertiesGenerator {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FlumeTopologyPropertiesGenerator.class);

    private static String  pathJSONTopology;
    private static String  pathPropertiesGeneratedFile;

    private byte[] flumeJSONTopology;
    private List<FlumeTopology> flumeTopologyList;
    private Map<String, FlumeTopology> mapTopology;
    private List<FlumeTopology> listTopologySources;
    private List<FlumeTopology> listTopologyConnections;
    private List<FlumeTopology> listTopologyInterceptors;
    private Map<String, DefaultMutableTreeNode> flumeTopologyTree = new LinkedHashMap<>();
    //private GraphStructure; TODO: Choose a library for graphs
    private boolean isTreeCompliant = true;
    private static Properties flumeConfigurationProperties = new LinkedProperties();


    /**
     * Load the properties file
     * @throws IOException
     */
    private void loadJSONTopologyFile() throws IOException {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN loadJSONTopologyFile");
        }

        //byte[] jsonTopology = Files.readAllBytes(Paths.get(pathJSONTopology));,

        flumeJSONTopology = Files.readAllBytes(Paths.get("src/main/resources/example-topology/FlumeTopology.json"));

        flumeTopologyList =  Arrays.asList(JSONStringSerializer.fromBytes(flumeJSONTopology, FlumeTopology[].class));

        logger.debug(flumeTopologyList.toString());
    }


    private void createInitialStructures() {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN createInitialStructures");
        }

        mapTopology = new LinkedHashMap<>();
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

            if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE.equals(topologyType)) {
                listTopologySources.add(flumeTopology);
            } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR.equals(topologyType)) {
                listTopologyInterceptors.add(flumeTopology);
            } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION.equals(topologyType)) {
                listTopologyConnections.add(flumeTopology);
            }

        }

        //Detect if the topology is a tree or is a graph from connections list
        isTreeCompliant = FlumeConfiguratorTopologyUtils.isTreeCompliant(listTopologyConnections);

        if (isTreeCompliant) {

            //Create a tree with structure described in topology
            createTopologyTree();

            Set<String> keySet= flumeTopologyTree.keySet();
            Set<Map.Entry<String,DefaultMutableTreeNode>> entrySet = flumeTopologyTree.entrySet();
            Collection<DefaultMutableTreeNode> collectionValues = flumeTopologyTree.values();

            System.out.println(FlumeConfiguratorTopologyUtils.renderFlumeTopology(flumeTopologyTree.values()));

        } else {

            //Create a graph with structure described in topology
        }




    }


    private void createTopologyTree() {

        Set<String> agentsList = new LinkedHashSet<>();
        Set<DefaultMutableTreeNode> rootNodesSet = new HashSet<>();
        List<DefaultMutableTreeNode> flumeTopologyNodeList = new ArrayList<>();


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

            DefaultMutableTreeNode flumeTopologyAgentRootNode = new DefaultMutableTreeNode(flumeTopologyAgentRoot);

            flumeTopologyTree.put(agentName, flumeTopologyAgentRootNode);

            rootNodesSet.add(flumeTopologyAgentRootNode);
        }

        //Create nodes for all elements in topology. Sources nodes will be link to their agent node.
        for (String flumeTopologyElementId : mapTopology.keySet()) {
            FlumeTopology flumeTopologyElement = mapTopology.get(flumeTopologyElementId);

            String flumeTopologyElementType =  flumeTopologyElement.getType();

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

                String propertyKey = FlumeConfiguratorConstants.SOURCES_LIST_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + agentName;
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

                String propertyKey = FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + agentName;
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

                String propertyKey = FlumeConfiguratorConstants.SINKS_LIST_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + agentName;
                flumeConfigurationProperties = FlumeConfiguratorTopologyUtils.addTopologyProperty(flumeConfigurationProperties, propertyKey, agentSinksList,
                        FlumeConfiguratorConstants.PROPERTY_SEPARATOR_DEFAULT);
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

        System.out.println(FlumeConfiguratorTopologyUtils.getPropertyAsString(flumeConfigurationProperties));
        System.out.println(FlumeConfiguratorTopologyUtils.getFlumePropertiesAsString(flumeConfigurationProperties));

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
            generateSourcesListProperties();

            //Generate channels list properties
            generateChannelsListProperties();

            //Generate sinks list properties
            generateSinksListProperties();

            //Generate interceptors list properties
            generateInterceptorsListProperties();





            logger.info("The process has ended correctly. The output file is in " + pathPropertiesGeneratedFile);
            logger.info("******* END FLUME PROPERTIES GENERATOR PROCESS *****************");
            return true;

        } catch (FileNotFoundException e) {
            logger.error("JSON topoloty file not found", e);
            return false;
        } catch (IOException e) {
            logger.error("An error has occurred on the load of JSON topology file", e);
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


            //if (args.length == 2) {
                //pathJSONTopology = args[0];
                //pathPropertiesGeneratedFile = args[1];

                logger.info("Parameter pathJSONTopology: " + pathJSONTopology);
                logger.info("Parameter pathPropertiesGeneratedFile: " + pathPropertiesGeneratedFile);

                FlumeTopologyPropertiesGenerator flumeTopologyPropertiesGenerator = new FlumeTopologyPropertiesGenerator();

                flumeTopologyPropertiesGenerator.generateInputProperties();


            //} else {
            //    logger.error("Incorrect parameters number. Parameters are: pathJSONTopology pathPropertiesGeneratedFile");
            //    logger.info("******* END FLUME PROPERTIES GENERATOR PROCESS *****************");

            //}


        } catch (Exception e) {
            logger.error("An error has occurred in Flume configurator. Check the properties configuration file and the generated logs", e);
            logger.info("******* END FLUME PROPERTIES GENERATOR PROCESS *****************");

        }
    }

}
