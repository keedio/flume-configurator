package org.keedio.flume.configurator.utils;

import org.apache.commons.collections4.map.HashedMap;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.keedio.flume.configurator.constants.FlumeConfiguratorConstants;
import org.keedio.flume.configurator.exceptions.FlumeConfiguratorException;
import org.keedio.flume.configurator.structures.FlumeTopology;
import org.keedio.flume.configurator.structures.LinkedProperties;
import org.keedio.flume.configurator.structures.TopologyPropertyBean;
import org.keedio.flume.configurator.topology.GraphFactory;
import org.keedio.flume.configurator.topology.IGraph;
import org.keedio.flume.configurator.topology.JSONStringSerializer;
import org.slf4j.LoggerFactory;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class FlumeConfiguratorTopologyUtilsTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FlumeConfiguratorUtilsTest.class);

    private static byte[] flumeJSONTopologyTree;
    private static List<FlumeTopology> flumeTopologyListTree;

    private static byte[] flumeJSONTopologyGraph;
    private static List<FlumeTopology> flumeTopologyListGraph;

    private static List<String> listSharedSourcesGraphAgent1;
    private static List<String> listSharedSourcesGraphAgent2;
    private static Map<String, Map<String,List<String>>> sourcesChannelsRelationsMap;

    private static Map<String, IGraph> flumeGraphTopology;
    private static DefaultMutableTreeNode flumeTopologyTreeRootNode;

    private static DefaultMutableTreeNode flumeTopologyNodeE;

    @BeforeClass
    public static void loadPropertiesFile() throws IOException {
        String  pathJSONTopologyTree = "src/test/resources/FlumeTopologyWithComments_with2Agent.json";
        flumeJSONTopologyTree = Files.readAllBytes(Paths.get(pathJSONTopologyTree));
        flumeTopologyListTree =  Arrays.asList(JSONStringSerializer.fromBytes(flumeJSONTopologyTree, FlumeTopology[].class));

        //String  pathJSONTopologyGraph = "src/test/resources/FlumeTopologyGraphWithComments_with2Agent.json";
        String  pathJSONTopologyGraph = "src/test/resources/FlumeTopologyGraphSinkGroups_with2Agent.json";
        flumeJSONTopologyGraph = Files.readAllBytes(Paths.get(pathJSONTopologyGraph));
        flumeTopologyListGraph =  Arrays.asList(JSONStringSerializer.fromBytes(flumeJSONTopologyGraph, FlumeTopology[].class));

        //Create tree
        FlumeTopology flumeTopologyA = new FlumeTopology();
        flumeTopologyA.setId("A");
        flumeTopologyA.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT);
        FlumeTopology flumeTopologyB = new FlumeTopology();
        flumeTopologyB.setId("B");
        flumeTopologyB.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE);
        FlumeTopology flumeTopologyC = new FlumeTopology();
        flumeTopologyC.setId("C");
        flumeTopologyC.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE);
        FlumeTopology flumeTopologyD = new FlumeTopology();
        flumeTopologyD.setId("D");
        flumeTopologyD.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL);
        FlumeTopology flumeTopologyE = new FlumeTopology();
        flumeTopologyE.setId("E");
        flumeTopologyE.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK);

        flumeTopologyTreeRootNode = new DefaultMutableTreeNode(flumeTopologyA);
        DefaultMutableTreeNode flumeTopologyNodeB = new DefaultMutableTreeNode(flumeTopologyB);
        DefaultMutableTreeNode flumeTopologyNodeC = new DefaultMutableTreeNode(flumeTopologyC);
        DefaultMutableTreeNode flumeTopologyNodeD = new DefaultMutableTreeNode(flumeTopologyD);
        flumeTopologyNodeE = new DefaultMutableTreeNode(flumeTopologyE);

        flumeTopologyTreeRootNode.add(flumeTopologyNodeB);
        flumeTopologyTreeRootNode.add(flumeTopologyNodeC);
        flumeTopologyNodeB.add(flumeTopologyNodeD);
        flumeTopologyNodeD.add(flumeTopologyNodeE);

        listSharedSourcesGraphAgent1 = new ArrayList<>();
        listSharedSourcesGraphAgent1.add("source1");
        listSharedSourcesGraphAgent1.add("source2");

        listSharedSourcesGraphAgent2 = new ArrayList<>();
        listSharedSourcesGraphAgent2.add("source4");
        listSharedSourcesGraphAgent2.add("source5");

        sourcesChannelsRelationsMap = new HashMap<>();
        Map<String,List<String>> sourcesChannelsRelationsMap_Agent1 = new HashMap<>();
        Map<String,List<String>> sourcesChannelsRelationsMap_Agent2= new HashMap<>();

        List<String> listChannels_Source1 = new ArrayList<>();
        List<String> listChannels_Source2 = new ArrayList<>();
        List<String> listChannels_Source3 = new ArrayList<>();
        List<String> listChannels_Source4 = new ArrayList<>();
        List<String> listChannels_Source5 = new ArrayList<>();
        List<String> listChannels_Source6 = new ArrayList<>();

        listChannels_Source1.add("channel1");
        listChannels_Source2.add("channel1");
        listChannels_Source3.add("channel2");
        listChannels_Source4.add("channel3");
        listChannels_Source5.add("channel3");
        listChannels_Source6.add("channel4");

        sourcesChannelsRelationsMap_Agent1.put("source1",listChannels_Source1);
        sourcesChannelsRelationsMap_Agent1.put("source2",listChannels_Source2);
        sourcesChannelsRelationsMap_Agent1.put("source3",listChannels_Source3);
        sourcesChannelsRelationsMap_Agent2.put("source4",listChannels_Source4);
        sourcesChannelsRelationsMap_Agent2.put("source5",listChannels_Source5);
        sourcesChannelsRelationsMap_Agent2.put("source6",listChannels_Source6);

        sourcesChannelsRelationsMap.put("agent1", sourcesChannelsRelationsMap_Agent1);
        sourcesChannelsRelationsMap.put("agent2", sourcesChannelsRelationsMap_Agent2);

        //Create agents graphs
        flumeGraphTopology = new LinkedHashMap<>();

        IGraph igraph_Agent1 = GraphFactory.createGraph("jgrapht");
        IGraph igraph_Agent2 = GraphFactory.createGraph("jgrapht");

        FlumeTopology agent1_Vertex = null;
        FlumeTopology agent1_source1_Vertex = null;
        FlumeTopology agent1_source2_Vertex = null;
        FlumeTopology agent1_source3_Vertex = null;
        FlumeTopology agent1_interceptor1_Vertex = null;
        FlumeTopology agent1_interceptor2_Vertex = null;
        FlumeTopology agent1_interceptor3_Vertex = null;
        FlumeTopology agent1_channel1_Vertex = null;
        FlumeTopology agent1_channel2_Vertex = null;
        FlumeTopology agent1_sink1_Vertex = null;
        FlumeTopology agent1_sink2_Vertex = null;
        FlumeTopology agent1_sinkgroup1_Vertex = null;

        FlumeTopology agent2_Vertex = null;
        FlumeTopology agent2_source4_Vertex = null;
        FlumeTopology agent2_source5_Vertex = null;
        FlumeTopology agent2_source6_Vertex = null;
        FlumeTopology agent2_interceptor4_Vertex = null;
        FlumeTopology agent2_interceptor5_Vertex = null;
        FlumeTopology agent2_channel3_Vertex = null;
        FlumeTopology agent2_channel4_Vertex = null;
        FlumeTopology agent2_sink3_Vertex = null;
        FlumeTopology agent2_sink4_Vertex = null;
        FlumeTopology agent2_sinkgroup2_Vertex = null;

        for (FlumeTopology flumeTopologyElement : flumeTopologyListGraph) {

            if (!flumeTopologyElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION)) {
                String flumeTopologyElementName = flumeTopologyElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                //String flumeTopologyElementID = flumeTopologyElement.getId();
                //FlumeTopology flumeTopologyElementFromID = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyListGraph, flumeTopologyElementID);

                switch (flumeTopologyElementName) {
                    case "agent1":
                        agent1_Vertex = flumeTopologyElement;
                        break;
                    case "source1":
                        agent1_source1_Vertex = flumeTopologyElement;
                        break;
                    case "source2":
                        agent1_source2_Vertex = flumeTopologyElement;
                        break;
                    case "source3":
                        agent1_source3_Vertex = flumeTopologyElement;
                        break;
                    case "i1":
                        agent1_interceptor1_Vertex = flumeTopologyElement;
                        break;
                    case "i2":
                        agent1_interceptor2_Vertex = flumeTopologyElement;
                        break;
                    case "i3":
                        agent1_interceptor3_Vertex = flumeTopologyElement;
                        break;
                    case "channel1":
                        agent1_channel1_Vertex = flumeTopologyElement;
                        break;
                    case "channel2":
                        agent1_channel2_Vertex = flumeTopologyElement;
                        break;
                    case "sink1":
                        agent1_sink1_Vertex = flumeTopologyElement;
                        break;
                    case "sink2":
                        agent1_sink2_Vertex = flumeTopologyElement;
                        break;
                    case "sinkgroup1":
                        agent1_sinkgroup1_Vertex = flumeTopologyElement;
                        break;
                    case "agent2":
                        agent2_Vertex = flumeTopologyElement;
                        break;
                    case "source4":
                        agent2_source4_Vertex = flumeTopologyElement;
                        break;
                    case "source5":
                        agent2_source5_Vertex = flumeTopologyElement;
                        break;
                    case "source6":
                        agent2_source6_Vertex = flumeTopologyElement;
                        break;
                    case "i4":
                        agent2_interceptor4_Vertex = flumeTopologyElement;
                        break;
                    case "i5":
                        agent2_interceptor5_Vertex = flumeTopologyElement;
                        break;
                    case "channel3":
                        agent2_channel3_Vertex = flumeTopologyElement;
                        break;
                    case "channel4":
                        agent2_channel4_Vertex = flumeTopologyElement;
                        break;
                    case "sink3":
                        agent2_sink3_Vertex = flumeTopologyElement;
                        break;
                    case "sink4":
                        agent2_sink4_Vertex = flumeTopologyElement;
                        break;
                    case "sinkgroup2":
                        agent2_sinkgroup2_Vertex = flumeTopologyElement;
                        break;
                    default:
                        throw new IOException("There is unexpected elements in graph");
                }
            }
        }

        igraph_Agent1.addGraphVertex(agent1_Vertex);
        igraph_Agent1.addGraphVertex(agent1_source1_Vertex);
        igraph_Agent1.addGraphVertex(agent1_source2_Vertex);
        igraph_Agent1.addGraphVertex(agent1_source3_Vertex);
        igraph_Agent1.addGraphVertex(agent1_interceptor1_Vertex);
        igraph_Agent1.addGraphVertex(agent1_interceptor2_Vertex);
        igraph_Agent1.addGraphVertex(agent1_interceptor3_Vertex);
        igraph_Agent1.addGraphVertex(agent1_channel1_Vertex);
        igraph_Agent1.addGraphVertex(agent1_channel2_Vertex);
        igraph_Agent1.addGraphVertex(agent1_sink1_Vertex);
        igraph_Agent1.addGraphVertex(agent1_sink2_Vertex);
        igraph_Agent1.addGraphVertex(agent1_sinkgroup1_Vertex);

        igraph_Agent2.addGraphVertex(agent2_Vertex);
        igraph_Agent2.addGraphVertex(agent2_source4_Vertex);
        igraph_Agent2.addGraphVertex(agent2_source5_Vertex);
        igraph_Agent2.addGraphVertex(agent2_source6_Vertex);
        igraph_Agent2.addGraphVertex(agent2_interceptor4_Vertex);
        igraph_Agent2.addGraphVertex(agent2_interceptor5_Vertex);
        igraph_Agent2.addGraphVertex(agent2_channel3_Vertex);
        igraph_Agent2.addGraphVertex(agent2_channel4_Vertex);
        igraph_Agent2.addGraphVertex(agent2_sink3_Vertex);
        igraph_Agent2.addGraphVertex(agent2_sink4_Vertex);
        igraph_Agent2.addGraphVertex(agent2_sinkgroup2_Vertex);

        igraph_Agent1.addGraphEdge(agent1_Vertex, agent1_source1_Vertex);
        igraph_Agent1.addGraphEdge(agent1_Vertex, agent1_source2_Vertex);
        igraph_Agent1.addGraphEdge(agent1_Vertex, agent1_source3_Vertex);
        igraph_Agent1.addGraphEdge(agent1_source1_Vertex, agent1_interceptor1_Vertex);
        igraph_Agent1.addGraphEdge(agent1_source2_Vertex, agent1_interceptor3_Vertex);
        igraph_Agent1.addGraphEdge(agent1_source3_Vertex, agent1_channel2_Vertex);
        igraph_Agent1.addGraphEdge(agent1_interceptor1_Vertex, agent1_interceptor2_Vertex);
        igraph_Agent1.addGraphEdge(agent1_interceptor2_Vertex, agent1_channel1_Vertex);
        igraph_Agent1.addGraphEdge(agent1_interceptor3_Vertex, agent1_channel1_Vertex);
        igraph_Agent1.addGraphEdge(agent1_channel1_Vertex, agent1_sink1_Vertex);
        igraph_Agent1.addGraphEdge(agent1_channel2_Vertex, agent1_sink2_Vertex);
        igraph_Agent1.addGraphEdge(agent1_sink1_Vertex, agent1_sinkgroup1_Vertex);
        igraph_Agent1.addGraphEdge(agent1_sink2_Vertex, agent1_sinkgroup1_Vertex);


        igraph_Agent2.addGraphEdge(agent2_Vertex, agent2_source4_Vertex);
        igraph_Agent2.addGraphEdge(agent2_Vertex, agent2_source5_Vertex);
        igraph_Agent2.addGraphEdge(agent2_Vertex, agent2_source6_Vertex);
        igraph_Agent2.addGraphEdge(agent2_source4_Vertex, agent2_interceptor4_Vertex);
        igraph_Agent2.addGraphEdge(agent2_source5_Vertex, agent2_interceptor5_Vertex);
        igraph_Agent2.addGraphEdge(agent2_source6_Vertex, agent2_channel4_Vertex);
        igraph_Agent2.addGraphEdge(agent2_interceptor4_Vertex, agent2_channel3_Vertex);
        igraph_Agent2.addGraphEdge(agent2_interceptor5_Vertex, agent2_channel3_Vertex);
        igraph_Agent2.addGraphEdge(agent2_channel3_Vertex, agent2_sink3_Vertex);
        igraph_Agent2.addGraphEdge(agent2_channel4_Vertex, agent2_sink4_Vertex);
        igraph_Agent2.addGraphEdge(agent2_sink3_Vertex, agent2_sinkgroup2_Vertex);
        igraph_Agent2.addGraphEdge(agent2_sink4_Vertex, agent2_sinkgroup2_Vertex);

        flumeGraphTopology.put("agent1", igraph_Agent1);
        flumeGraphTopology.put("agent2", igraph_Agent2);
    }


    @Test
    public void testIsTreeCompliant() {

        int treeNodesNumber = 13;
        int treeSourcesNumber = 2;
        int treeAgentsNumber = 2;
        int graphNodesNumber = 15;
        int graphSourcesNumber = 4;
        int graphAgentNumber = 2;

        //Check tree
        List<FlumeTopology> flumeTopologyListConnectionsTree = new ArrayList<>();
        //Get connections
        for (FlumeTopology flumeTopologyElementTree : flumeTopologyListTree) {
            if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION.equals(flumeTopologyElementTree.getType())) {
                flumeTopologyListConnectionsTree.add(flumeTopologyElementTree);
            }
        }

        boolean isTreeCompliantTree = FlumeConfiguratorTopologyUtils.isTreeCompliant(true, flumeTopologyListConnectionsTree, treeNodesNumber, treeSourcesNumber, treeAgentsNumber);
        Assert.assertTrue("The result of the isTreeCompliant method is not correct", isTreeCompliantTree);


        //Check graph
        List<FlumeTopology> flumeTopologyListConnectionsGraph = new ArrayList<>();
        //Get connections
        for (FlumeTopology flumeTopologyElementGraph : flumeTopologyListGraph) {
            if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION.equals(flumeTopologyElementGraph.getType())) {
                flumeTopologyListConnectionsGraph.add(flumeTopologyElementGraph);
            }
        }

        boolean isTreeCompliantGraph = FlumeConfiguratorTopologyUtils.isTreeCompliant(true, flumeTopologyListGraph, graphNodesNumber, graphSourcesNumber, graphAgentNumber);
        Assert.assertFalse("The result of the isTreeCompliant method is not correct", isTreeCompliantGraph);

    }

    @Test
    public void testSearchTreeNode() {

        String existsID = "D";
        String nonExistsID = "X";

        //Searh an existing node
        DefaultMutableTreeNode searchNode = FlumeConfiguratorTopologyUtils.searchTreeNode(existsID, flumeTopologyTreeRootNode);
        Assert.assertNotNull("The result of the searchTreeNode method is not correct", searchNode);
        FlumeTopology element = (FlumeTopology) searchNode.getUserObject();
        Assert.assertEquals("The result of the searchTreeNode method is not correct", element.getId(), existsID);

        //Search an non existing node
        searchNode = FlumeConfiguratorTopologyUtils.searchTreeNode(nonExistsID, flumeTopologyTreeRootNode);
        Assert.assertNull("The result of the searchTreeNode method is not correct", searchNode);

    }

    @Test
    public void testSearchNode() {

        String existsID = "D";
        String nonExistsID = "X";

        //Create list of nodes of the tree
        List<DefaultMutableTreeNode> nodesList = new ArrayList<>();
        Enumeration agentTreeNodes = flumeTopologyTreeRootNode.preorderEnumeration();

        while (agentTreeNodes.hasMoreElements()) {
            DefaultMutableTreeNode agentTreeNode = (DefaultMutableTreeNode) agentTreeNodes.nextElement();
            nodesList.add(agentTreeNode);
        }

        //Search an existing node
        DefaultMutableTreeNode searchNode = FlumeConfiguratorTopologyUtils.searchNode(existsID, nodesList);
        Assert.assertNotNull("The result of the searchNode method is not correct", searchNode);
        FlumeTopology element = (FlumeTopology) searchNode.getUserObject();
        Assert.assertEquals("The result of the searchNode method is not correct", element.getId(), existsID);

        //Search an non existing node
        searchNode = FlumeConfiguratorTopologyUtils.searchNode(nonExistsID, nodesList);
        Assert.assertNull("The result of the searchNode method is not correct", searchNode);
    }


    @Test
    public void testGetKeyPropertyString() {

        String propertyPart1 = "propertyPart1";
        String propertyPart2 = ".";
        String propertyPart3 = "propertyPart3";

        String property = FlumeConfiguratorTopologyUtils.getKeyPropertyString(propertyPart1, propertyPart2, propertyPart3);
        Assert.assertEquals("The result of the getKeyPropertyString method is not correct", property.length(), propertyPart1.length() + propertyPart2.length() + propertyPart3.length());
        Assert.assertEquals("The result of the getKeyPropertyString method is not correct", property, propertyPart1 + propertyPart2 + propertyPart3);

    }


    @Test
    public void testAddTopologyProperty() {

        String propertyName = "propertyName";
        List<String> propertyValues = new ArrayList<>();
        String value1 = "value1";
        String value2 = "value2";
        String value3 = "value3";
        String separatorCharacter = ";";


        propertyValues.add(value1);
        propertyValues.add(value2);
        propertyValues.add(value3);

        Properties properties = new Properties();
        Assert.assertTrue("The result of the addTopologyProperty method is not correct", properties.isEmpty());


        properties = FlumeConfiguratorTopologyUtils.addTopologyProperty(properties, propertyName, propertyValues, separatorCharacter);
        Assert.assertFalse("The result of the addTopologyProperty method is not correct", properties.isEmpty());
        Assert.assertEquals("The result of the addTopologyProperty method is not correct", properties.size(), 1);
        String propertyValue = (String) properties.get(propertyName);
        Assert.assertNotNull("The result of the addTopologyProperty method is not correct", propertyValue);
        String expectedValue = value1 + separatorCharacter + value2 + separatorCharacter + value3;
        Assert.assertEquals("The result of the addTopologyProperty method is not correct", propertyValue, expectedValue);

        properties = new Properties();

        properties = FlumeConfiguratorTopologyUtils.addTopologyProperty(properties, propertyName, value1, false);
        Assert.assertFalse("The result of the addTopologyProperty method is not correct", properties.isEmpty());
        Assert.assertEquals("The result of the addTopologyProperty method is not correct", properties.size(), 1);
        propertyValue = (String) properties.get(propertyName);
        Assert.assertNotNull("The result of the addTopologyProperty method is not correct", propertyValue);
        expectedValue = value1;
        Assert.assertEquals("The result of the addTopologyProperty method is not correct", propertyValue, expectedValue);

        properties = new Properties();

        properties = FlumeConfiguratorTopologyUtils.addTopologyProperty(properties, propertyName, null, true);
        Assert.assertFalse("The result of the addTopologyProperty method is not correct", properties.isEmpty());
        Assert.assertEquals("The result of the addTopologyProperty method is not correct", properties.size(), 1);
        propertyValue = (String) properties.get(propertyName);
        Assert.assertNotNull("The result of the addTopologyProperty method is not correct", propertyValue);
        expectedValue = "";
        Assert.assertEquals("The result of the addTopologyProperty method is not correct", propertyValue, expectedValue);

    }


    @Test
    public void testIsSpecialProperty() {

        String propertyAgentName = FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_AGENT_NAME;
        String propertyAgentNameComment = propertyAgentName + FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_COMMENT_SUFIX;
        String propertyElementTopologytName = FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME;
        String propertyElementTopologytNameComment = propertyElementTopologytName + FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_COMMENT_SUFIX;
        String propertyName = "propertyName";

        Assert.assertTrue("The result of the isSpecialProperty method is not correct",
                FlumeConfiguratorTopologyUtils.isSpecialProperty(propertyAgentName));
        Assert.assertTrue("The result of the isSpecialProperty method is not correct",
                FlumeConfiguratorTopologyUtils.isSpecialProperty(propertyAgentNameComment));
        Assert.assertTrue("The result of the isSpecialProperty method is not correct",
                FlumeConfiguratorTopologyUtils.isSpecialProperty(propertyElementTopologytName));
        Assert.assertTrue("The result of the isSpecialProperty method is not correct",
                FlumeConfiguratorTopologyUtils.isSpecialProperty(propertyElementTopologytNameComment));
        Assert.assertFalse("The result of the isSpecialProperty method is not correct",
                FlumeConfiguratorTopologyUtils.isSpecialProperty(propertyName));

    }


    @Test
    public void testIsCommentProperty() {

        String propertyName = "propertyName";
        String propertyNameComment = propertyName + FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_COMMENT_SUFIX;

        Assert.assertFalse("The result of the isCommentProperty method is not correct",
                FlumeConfiguratorTopologyUtils.isCommentProperty(propertyName));
        Assert.assertTrue("The result of the isCommentProperty method is not correct",
                FlumeConfiguratorTopologyUtils.isCommentProperty(propertyNameComment));

    }


    @Test
    public void testGetValidTopologyProperties() {

        String propertyAgentName = FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_AGENT_NAME;
        String propertyAgentValue = "agent1";
        String propertyName = "propertyName";
        String propertyValue = "propertyValue";
        String propertyName2 = "propertyName2";
        String propertyName3 = "propertyName3";
        String propertyName3Comment = "propertyName3_comment";
        String propertyName3CommentValue = "propertyName3 value";

        Map<String, String> originalTopologyProperties = new HashMap<>();
        originalTopologyProperties.put(propertyAgentName, propertyAgentValue);
        originalTopologyProperties.put(propertyName, propertyValue);

        //Special property
        Map<String, String> topologyProperties = FlumeConfiguratorTopologyUtils.getValidTopologyProperties(originalTopologyProperties);
        Assert.assertEquals("The result of the getValidTopologyProperties method is not correct",originalTopologyProperties.size(), 2);
        Assert.assertEquals("The result of the getValidTopologyProperties method is not correct",topologyProperties.size(), 1);

        //Without value
        originalTopologyProperties.put(propertyName2, "");
        topologyProperties = FlumeConfiguratorTopologyUtils.getValidTopologyProperties(originalTopologyProperties);
        Assert.assertEquals("The result of the getValidTopologyProperties method is not correct",originalTopologyProperties.size(), 3);
        Assert.assertEquals("The result of the getValidTopologyProperties method is not correct",topologyProperties.size(), 1);

        //Without value with comment property
        originalTopologyProperties.put(propertyName3, "");
        originalTopologyProperties.put(propertyName3Comment, propertyName3CommentValue);
        topologyProperties = FlumeConfiguratorTopologyUtils.getValidTopologyProperties(originalTopologyProperties);
        Assert.assertEquals("The result of the getValidTopologyProperties method is not correct",originalTopologyProperties.size(), 5);
        Assert.assertEquals("The result of the getValidTopologyProperties method is not correct",topologyProperties.size(), 1);

    }


    @Test
    public void testAddPropertyBean() {

        String propertyName1 = "propertyName1";
        String propertyComment1 = "propertyComment1";
        String appliedElement1 = "appliedElement1";
        String propertyValue1 = "propertyValue1";
        String propertyComment1_2 = "propertyComment1_2";
        String appliedElement1_2 = "appliedElement1_2";
        String propertyValue1_2 = "propertyValue1_2";
        String propertyName2 = "propertyName2";
        String propertyComment2 = "propertyComment2";
        String appliedElement2 = "appliedElement2";
        String propertyValue2 = "propertyValue2";

        Map<String, List<TopologyPropertyBean>> propertiesMap = new HashMap<>();
        List<TopologyPropertyBean> topologyPropertyBeanList = new ArrayList<>();
        TopologyPropertyBean topologyPropertyBean = new TopologyPropertyBean(propertyComment1, appliedElement1, propertyValue1);
        topologyPropertyBeanList.add(topologyPropertyBean);
        propertiesMap.put(propertyName1, topologyPropertyBeanList);

        List<TopologyPropertyBean> topologyPropertyBeanList1 = propertiesMap.get(propertyName1);
        Assert.assertEquals("The result of the addPropertyBean method is not correct",topologyPropertyBeanList1.size(), 1);

        propertiesMap = FlumeConfiguratorTopologyUtils.addPropertyBean(propertiesMap, propertyName1, propertyComment1_2, appliedElement1_2, propertyValue1_2);
        topologyPropertyBeanList1 = propertiesMap.get(propertyName1);
        Assert.assertEquals("The result of the addPropertyBean method is not correct",topologyPropertyBeanList1.size(), 2);

        topologyPropertyBeanList1 = propertiesMap.get(propertyName2);
        Assert.assertNull("The result of the addPropertyBean method is not correct",topologyPropertyBeanList1);
        propertiesMap = FlumeConfiguratorTopologyUtils.addPropertyBean(propertiesMap, propertyName2, propertyComment2, appliedElement2, propertyValue2);
        topologyPropertyBeanList1 = propertiesMap.get(propertyName2);
        Assert.assertEquals("The result of the addPropertyBean method is not correct",topologyPropertyBeanList1.size(), 1);

    }


    @Test
    public void testGetValueCommonProperty() {

        String propertyComment1 = "propertyComment1";
        String appliedElement1 = "appliedElement1";
        String propertyValue1 = "propertyValue1";
        String propertyComment2 = "propertyComment2";
        String appliedElement2 = "appliedElement2";
        String propertyValue2 = "propertyValue2";
        double ratio = 0.75d;

        List<TopologyPropertyBean> topologyPropertyBeanList = new ArrayList<>();
        TopologyPropertyBean topologyPropertyBean1 = new TopologyPropertyBean(propertyComment1, appliedElement1, propertyValue1);
        TopologyPropertyBean topologyPropertyBean2 = new TopologyPropertyBean(propertyComment1, appliedElement1, propertyValue1);
        TopologyPropertyBean topologyPropertyBean3 = new TopologyPropertyBean(propertyComment2, appliedElement2, propertyValue2);
        topologyPropertyBeanList.add(topologyPropertyBean1);
        topologyPropertyBeanList.add(topologyPropertyBean2);
        topologyPropertyBeanList.add(topologyPropertyBean3);

        String commonValue = FlumeConfiguratorTopologyUtils.getValueCommonProperty(topologyPropertyBeanList, topologyPropertyBeanList.size(), ratio);
        Assert.assertNull("The result of the getValueCommonProperty method is not correct",commonValue);

        ratio = 0.5d;
        commonValue = FlumeConfiguratorTopologyUtils.getValueCommonProperty(topologyPropertyBeanList, topologyPropertyBeanList.size(), ratio);
        Assert.assertEquals("The result of the getValueCommonProperty method is not correct",commonValue, propertyValue1);

    }


    @Test
    public void testListToString() {

        String cad1 = "cad1";
        String cad2 = "cad2";
        String cad3 = "cad3";
        String separatorChar = ";";

        List<String> cadList = new ArrayList<>();
        cadList.add(cad1);
        cadList.add(cad2);
        cadList.add(cad3);

        String result = FlumeConfiguratorTopologyUtils.listToString(cadList, separatorChar);
        String expectedResult = cad1 + separatorChar + cad2 + separatorChar + cad3;
        Assert.assertEquals("The result of the listToString method is not correct",result, expectedResult);

        cadList = new ArrayList<>();
        result = FlumeConfiguratorTopologyUtils.listToString(cadList, separatorChar);
        expectedResult = "";
        Assert.assertEquals("The result of the listToString method is not correct",result, expectedResult);

    }


    @Test
    public void testAppendString() {

        String cad1 = "cad1";
        String cad2 = "cad2";
        String separatorChar = ";";

        String result = FlumeConfiguratorTopologyUtils.appendString(cad1, cad2, separatorChar);
        String expectedResult = cad1 + separatorChar + cad2;
        Assert.assertEquals("The result of the appendString method is not correct",result, expectedResult);

        result = FlumeConfiguratorTopologyUtils.appendString("", cad2, separatorChar);
        expectedResult = cad2;
        Assert.assertEquals("The result of the appendString method is not correct",result, expectedResult);

        result = FlumeConfiguratorTopologyUtils.appendString(null, cad2, separatorChar);
        expectedResult = cad2;
        Assert.assertEquals("The result of the appendString method is not correct",result, expectedResult);

        result = FlumeConfiguratorTopologyUtils.appendString(cad1, "", separatorChar);
        expectedResult = cad1;
        Assert.assertEquals("The result of the appendString method is not correct",result, expectedResult);

    }

    @Test
    public void testExistsCommonProperty() {

        String propertyKey1 = "propertyKey1";
        String propertyKey2 = "propertyKey2";
        String propertyCommonComment1 = "propertyCommonComment1";
        String appliedCommonElement1 = "appliedCommonElement1";
        String propertyCommonValue1 = "propertyCommonValue1";
        String propertyComment2 = "propertyComment2";
        String appliedElement2 = "appliedElement2";
        String propertyValue2 = "propertyValue2";

        TopologyPropertyBean topologyCommonPropertyBean = new TopologyPropertyBean(propertyCommonComment1, appliedCommonElement1, propertyCommonValue1);
        Map<String, TopologyPropertyBean> commonPropertiesMap = new HashMap<>();
        commonPropertiesMap.put(propertyKey1, topologyCommonPropertyBean);

        TopologyPropertyBean topologyPropertyBean = new TopologyPropertyBean(propertyComment2, appliedElement2, propertyValue2);
        Assert.assertFalse("The result of the existsCommonProperty method is not correct",FlumeConfiguratorTopologyUtils.existsCommonProperty(topologyPropertyBean, commonPropertiesMap, propertyKey2));
        Assert.assertFalse("The result of the existsCommonProperty method is not correct",FlumeConfiguratorTopologyUtils.existsCommonProperty(topologyPropertyBean, commonPropertiesMap, propertyKey1));

        topologyPropertyBean = new TopologyPropertyBean(propertyComment2, appliedElement2, propertyCommonValue1);
        Assert.assertFalse("The result of the existsCommonProperty method is not correct",FlumeConfiguratorTopologyUtils.existsCommonProperty(topologyPropertyBean, commonPropertiesMap, propertyKey2));
        Assert.assertTrue("The result of the existsCommonProperty method is not correct",FlumeConfiguratorTopologyUtils.existsCommonProperty(topologyPropertyBean, commonPropertiesMap, propertyKey1));

    }


    @Test
    public void testGetSubList() {

        String id1 = "id1";
        String id2 = "id2";
        String id3 = "id3";

        FlumeTopology ft1 = new FlumeTopology();
        ft1.setId(id1);
        FlumeTopology ft2 = new FlumeTopology();
        ft2.setId(id2);
        FlumeTopology ft3 = new FlumeTopology();
        ft3.setId(id1);

        List<FlumeTopology> listElements = new ArrayList<>();
        listElements.add(ft1);
        listElements.add(ft2);
        listElements.add(ft3);

        List<String> listSearchId = new ArrayList<>();
        listSearchId.add(id1);

        List<FlumeTopology> listResult = FlumeConfiguratorTopologyUtils.getSubList(listSearchId, listElements);
        Assert.assertEquals("The result of the getSubList method is not correct", listResult.size(), 2);

        listSearchId.add(id2);
        listResult = FlumeConfiguratorTopologyUtils.getSubList(listSearchId, listElements);
        Assert.assertEquals("The result of the getSubList method is not correct", listResult.size(), 3);

        listSearchId = new ArrayList<>();
        listSearchId.add(id3);
        listResult = FlumeConfiguratorTopologyUtils.getSubList(listSearchId, listElements);
        Assert.assertEquals("The result of the getSubList method is not correct", listResult.size(), 0);

    }


    @Test
    public void testCheckOnlyOneAgentPerNode() {

        String connectionIDTree = "connectionIDTree";
        String connectionSourceTree = "4ba63761-5d62-9aa1-0986-0e9d8cfeed21";
        String connectionTargetTree = "38d0a869-d5f9-4cce-bd13-ed01e04ae15e";

        String connectionIDGraph = "connectionIDGraph";
        String connectionSourceGraph = "6cfae647-d552-5642-7947-547181a67a3c";
        String connectionTargetGraph = "f89d13d8-27bf-9c97-91d3-9505e894a2d3";

        List<FlumeTopology> flumeTopologyListConnectionsTree = new ArrayList<>();
        //Get connections
        for (FlumeTopology flumeTopologyElementTree : flumeTopologyListTree) {
            if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION.equals(flumeTopologyElementTree.getType())) {
                flumeTopologyListConnectionsTree.add(flumeTopologyElementTree);
            }
        }

        Assert.assertTrue("The result of the checkOnlyOneAgentPerNode method is not correct",FlumeConfiguratorTopologyUtils.checkOnlyOneAgentPerNode(flumeTopologyListTree, flumeTopologyListConnectionsTree));

        //Check graph
        List<FlumeTopology> flumeTopologyListConnectionsGraph = new ArrayList<>();
        //Get connections
        for (FlumeTopology flumeTopologyElementGraph : flumeTopologyListGraph) {
            if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION.equals(flumeTopologyElementGraph.getType())) {
                flumeTopologyListConnectionsGraph.add(flumeTopologyElementGraph);
            }
        }

        Assert.assertTrue("The result of the checkOnlyOneAgentPerNode method is not correct",FlumeConfiguratorTopologyUtils.checkOnlyOneAgentPerNode(flumeTopologyListGraph, flumeTopologyListConnectionsGraph));


        //Create a connection between 1 source from agent1 to a node from agent2 (Tree)
        FlumeTopology flumeTopologyConnectionTree = new FlumeTopology();
        flumeTopologyConnectionTree.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION);
        flumeTopologyConnectionTree.setId(connectionIDTree);
        flumeTopologyConnectionTree.setSourceConnection(connectionSourceTree);
        flumeTopologyConnectionTree.setTargetConnection(connectionTargetTree);

        flumeTopologyListConnectionsTree.add(flumeTopologyConnectionTree);
        Assert.assertFalse("The result of the checkOnlyOneAgentPerNode method is not correct",FlumeConfiguratorTopologyUtils.checkOnlyOneAgentPerNode(flumeTopologyListTree, flumeTopologyListConnectionsTree));



        //Create a connection between 1 source from agent1 to a node from agent2 (Graph)
        FlumeTopology flumeTopologyConnectionGraph = new FlumeTopology();
        flumeTopologyConnectionGraph.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION);
        flumeTopologyConnectionGraph.setId(connectionIDGraph);
        flumeTopologyConnectionGraph.setSourceConnection(connectionSourceGraph);
        flumeTopologyConnectionGraph.setTargetConnection(connectionTargetGraph);

        flumeTopologyListConnectionsGraph.add(flumeTopologyConnectionGraph);
        Assert.assertFalse("The result of the checkOnlyOneAgentPerNode method is not correct",FlumeConfiguratorTopologyUtils.checkOnlyOneAgentPerNode(flumeTopologyListGraph, flumeTopologyListConnectionsGraph));

    }


    @Test
    public void testGetGraphAgentFromConnections() {

        String searchIDTree = "2271cb95-565a-5bc2-b0b1-bc8b68919337";
        String agentIDTree = "cbbd2d42-b905-4c17-b385-dbd33bb9871e";
        String expectedAgentNameTree = "agent1";
        String searchIDGraph = "2271cb95-565a-5bc2-b0b1-bc8b68919337";
        String agentIDGraph = "2e6fce4a-51c2-4bd4-b5d6-55b2b1375081";
        String expectedAgentNameGraph = "agent2";

        String nonExistID = "nonExistID";


        List<FlumeTopology> flumeTopologyListConnectionsTree = new ArrayList<>();
        //Get connections
        for (FlumeTopology flumeTopologyElementTree : flumeTopologyListTree) {
            if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION.equals(flumeTopologyElementTree.getType())) {
                flumeTopologyListConnectionsTree.add(flumeTopologyElementTree);
            }
        }

        Assert.assertEquals("The result of the getGraphAgentFromConnections method is not correct",
                FlumeConfiguratorTopologyUtils.getGraphAgentFromConnections(searchIDTree, flumeTopologyListConnectionsTree, flumeTopologyListTree, true), expectedAgentNameTree);

        Assert.assertEquals("The result of the getGraphAgentFromConnections method is not correct",
                FlumeConfiguratorTopologyUtils.getGraphAgentFromConnections(agentIDTree, flumeTopologyListConnectionsTree, flumeTopologyListTree, true), expectedAgentNameTree);

        Assert.assertNull("The result of the getGraphAgentFromConnections method is not correct",
                FlumeConfiguratorTopologyUtils.getGraphAgentFromConnections(nonExistID, flumeTopologyListConnectionsTree, flumeTopologyListTree, true));


        List<FlumeTopology> flumeTopologyListConnectionsGraph = new ArrayList<>();
        //Get connections
        for (FlumeTopology flumeTopologyElementGraph : flumeTopologyListGraph) {
            if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION.equals(flumeTopologyElementGraph.getType())) {
                flumeTopologyListConnectionsGraph.add(flumeTopologyElementGraph);
            }
        }

        Assert.assertEquals("The result of the getGraphAgentFromConnections method is not correct",
                FlumeConfiguratorTopologyUtils.getGraphAgentFromConnections(searchIDGraph, flumeTopologyListConnectionsGraph, flumeTopologyListGraph, true), expectedAgentNameGraph);

        Assert.assertEquals("The result of the getGraphAgentFromConnections method is not correct",
                FlumeConfiguratorTopologyUtils.getGraphAgentFromConnections(agentIDGraph, flumeTopologyListConnectionsGraph, flumeTopologyListGraph, true), expectedAgentNameGraph);

        Assert.assertNull("The result of the getGraphAgentFromConnections method is not correct",
                FlumeConfiguratorTopologyUtils.getGraphAgentFromConnections(nonExistID, flumeTopologyListConnectionsGraph, flumeTopologyListGraph, true));

    }


    @Test
    public void testGetAgentVertexFromGraph() {

        String agent1 = "agent1";
        String source1 = "source1";
        String source2 = "source2";
        String channel = "channel";
        String sink = "sink";

        FlumeTopology ftAgent1 = new FlumeTopology();
        ftAgent1.setId(agent1);
        ftAgent1.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT);

        FlumeTopology ftSource1 = new FlumeTopology();
        ftSource1.setId(source1);
        ftSource1.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE);

        FlumeTopology ftSource2 = new FlumeTopology();
        ftSource2.setId(source2);
        ftSource2.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE);

        FlumeTopology ftChannel = new FlumeTopology();
        ftChannel.setId(channel);
        ftChannel.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL);

        FlumeTopology ftSink = new FlumeTopology();
        ftSink.setId(sink);
        ftSink.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK);

        IGraph igraph = GraphFactory.createGraph("jgrapht");
        igraph.addGraphVertex(ftAgent1);
        igraph.addGraphVertex(ftSource1);
        igraph.addGraphVertex(ftSource2);
        igraph.addGraphVertex(ftChannel);
        igraph.addGraphVertex(ftSink);


        Assert.assertEquals("The result of the getAgentVertexFromGraph method is not correct",
                FlumeConfiguratorTopologyUtils.getAgentVertexFromGraph(igraph).getId(), agent1);


        igraph = GraphFactory.createGraph("jgrapht");
        Assert.assertNull("The result of the getAgentVertexFromGraph method is not correct",
                FlumeConfiguratorTopologyUtils.getAgentVertexFromGraph(igraph));

    }


    @Test
    public void testConvetTreeSet() {

        //Remove connections
        List<FlumeTopology> flumeTopologyListNoConnectionsTree = new ArrayList<>();
        for (FlumeTopology flumeTopologyElementTree : flumeTopologyListTree) {
            if (!FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION.equals(flumeTopologyElementTree.getType())) {
                flumeTopologyListNoConnectionsTree.add(flumeTopologyElementTree);
            }
        }

        Set<FlumeTopology> flumeTopologySetTree = new HashSet<>(flumeTopologyListNoConnectionsTree);

        TreeSet<FlumeTopology> flumeTopologyTreeSetTree = FlumeConfiguratorTopologyUtils.convetTreeSet(flumeTopologySetTree);
        Assert.assertEquals("The result of the convetTreeSet method is not correct",
                flumeTopologyTreeSetTree.size(), flumeTopologySetTree.size());


        Iterator<FlumeTopology> itFlumeTopologyTreeSetTree = flumeTopologyTreeSetTree.iterator();
        FlumeTopology ft1 = null;
        FlumeTopology ft2 = null;
        while (itFlumeTopologyTreeSetTree.hasNext()) {
            if (ft1 == null) {
                ft1 = itFlumeTopologyTreeSetTree.next();
            } else {
                ft1 = ft2;
            }


            if (itFlumeTopologyTreeSetTree.hasNext()) {
                ft2 = itFlumeTopologyTreeSetTree.next();
            } else {
                ft2 = ft1;
            }

            Assert.assertTrue("The result of the convetTreeSet method is not correct", ft1.compareTo(ft2) < 1);
        }
    }


    @Test
    public void testGetPropertyPartsNumber() {

        String propertyName = null;
        int partsNumber = FlumeConfiguratorTopologyUtils.getPropertyPartsNumber(propertyName);
        Assert.assertEquals("The result of the getPropertyPartsNumber method is not correct", partsNumber, 0);

        propertyName = "";
        partsNumber = FlumeConfiguratorTopologyUtils.getPropertyPartsNumber(propertyName);
        Assert.assertEquals("The result of the getPropertyPartsNumber method is not correct", partsNumber, 0);

        propertyName = "part1";
        partsNumber = FlumeConfiguratorTopologyUtils.getPropertyPartsNumber(propertyName);
        Assert.assertEquals("The result of the getPropertyPartsNumber method is not correct", partsNumber, 1);

        propertyName = "part1.part2";
        partsNumber = FlumeConfiguratorTopologyUtils.getPropertyPartsNumber(propertyName);
        Assert.assertEquals("The result of the getPropertyPartsNumber method is not correct", partsNumber, 2);

        propertyName = "part1.part2 ;,part2";
        partsNumber = FlumeConfiguratorTopologyUtils.getPropertyPartsNumber(propertyName);
        Assert.assertEquals("The result of the getPropertyPartsNumber method is not correct", partsNumber, 2);

    }


    @Test
    public void testGetPropertyPart() {

        String propertyName = null;
        String expectedPropertyPart  = "";
        int indexPart = 1;
        String propertyPart = FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, indexPart);
        Assert.assertEquals("The result of the getPropertyPart method is not correct", expectedPropertyPart, propertyPart);

        propertyName = "";
        expectedPropertyPart  = "";
        indexPart = 1;
        propertyPart = FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, indexPart);
        Assert.assertEquals("The result of the getPropertyPart method is not correct", expectedPropertyPart, propertyPart);

        propertyName = "part1";
        expectedPropertyPart  = "part1";
        indexPart = 1;
        propertyPart = FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, indexPart);
        Assert.assertEquals("The result of the getPropertyPart method is not correct", expectedPropertyPart, propertyPart);

        propertyName = "part1";
        expectedPropertyPart  = "";
        indexPart = 2;
        propertyPart = FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, indexPart);
        Assert.assertEquals("The result of the getPropertyPart method is not correct", expectedPropertyPart, propertyPart);

        propertyName = "part1";
        expectedPropertyPart  = "";
        indexPart = 0;
        propertyPart = FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, indexPart);
        Assert.assertEquals("The result of the getPropertyPart method is not correct", expectedPropertyPart, propertyPart);

        propertyName = "part1.part2";
        expectedPropertyPart  = "part1";
        indexPart = 1;
        propertyPart = FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, indexPart);
        Assert.assertEquals("The result of the getPropertyPart method is not correct", expectedPropertyPart, propertyPart);

        propertyName = "part1.part2";
        expectedPropertyPart  = "part2";
        indexPart = 2;
        propertyPart = FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, indexPart);
        Assert.assertEquals("The result of the getPropertyPart method is not correct", expectedPropertyPart, propertyPart);

    }


    @Test
    public void testGetPropertiesWithPart() {

        Properties properties = new Properties();
        String part = "part1";
        int partIndex = 1;
        boolean isLastPart = false;

        LinkedProperties linkedProperties = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(properties, part, partIndex, isLastPart);
        Assert.assertTrue("The result of the getPropertiesWithPart method is not correct", linkedProperties.isEmpty());


        String propertyName1 = "part1.part2";
        String propertyValue1 = "value1";
        String propertyName2 = "part1.part2.part1";
        String propertyValue2 = "value2";
        String propertyName3 = "part1.part2.part1.part3";
        String propertyValue3 = "value2";

        properties.put(propertyName1, propertyValue1);
        properties.put(propertyName2, propertyValue2);
        properties.put(propertyName3, propertyValue3);

        linkedProperties = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(properties, part, partIndex, isLastPart);
        Assert.assertFalse("The result of the getPropertiesWithPart method is not correct", linkedProperties.isEmpty());
        Assert.assertEquals("The result of the getPropertiesWithPart method is not correct", linkedProperties.size(), 3);
        Assert.assertNotNull("The result of the getPropertiesWithPart method is not correct", linkedProperties.getProperty(propertyName1));
        Assert.assertNotNull("The result of the getPropertiesWithPart method is not correct", linkedProperties.getProperty(propertyName2));
        Assert.assertNotNull("The result of the getPropertiesWithPart method is not correct", linkedProperties.getProperty(propertyName3));

        partIndex = 3;
        linkedProperties = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(properties, part, partIndex, isLastPart);
        Assert.assertFalse("The result of the getPropertiesWithPart method is not correct", linkedProperties.isEmpty());
        Assert.assertEquals("The result of the getPropertiesWithPart method is not correct", linkedProperties.size(), 2);
        Assert.assertNotNull("The result of the getPropertiesWithPart method is not correct", linkedProperties.getProperty(propertyName2));
        Assert.assertNotNull("The result of the getPropertiesWithPart method is not correct", linkedProperties.getProperty(propertyName3));

        isLastPart = true;
        partIndex = 3;
        linkedProperties = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(properties, part, partIndex, isLastPart);
        Assert.assertFalse("The result of the getPropertiesWithPart method is not correct", linkedProperties.isEmpty());
        Assert.assertEquals("The result of the getPropertiesWithPart method is not correct", linkedProperties.size(), 1);
        Assert.assertNotNull("The result of the getPropertiesWithPart method is not correct", linkedProperties.getProperty(propertyName2));

        part = "partX";
        partIndex = 1;
        linkedProperties = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(properties, part, partIndex, isLastPart);
        Assert.assertTrue("The result of the getPropertiesWithPart method is not correct", linkedProperties.isEmpty());

    }

    @Test
    public void testGetTailPartProperty() {

        String propertyName = "";
        int partIndex = 1;
        String tailPart = FlumeConfiguratorTopologyUtils.getTailPartProperty(propertyName, partIndex);
        Assert.assertTrue("The result of the getTailPartProperty method is not correct", tailPart.isEmpty());

        propertyName = "part1";
        partIndex = 0;
        tailPart = FlumeConfiguratorTopologyUtils.getTailPartProperty(propertyName, partIndex);
        Assert.assertTrue("The result of the getTailPartProperty method is not correct", tailPart.isEmpty());

        propertyName = "part1";
        partIndex = 1;
        tailPart = FlumeConfiguratorTopologyUtils.getTailPartProperty(propertyName, partIndex);
        Assert.assertTrue("The result of the getTailPartProperty method is not correct", tailPart.isEmpty());

        propertyName = "part1";
        partIndex = 2;
        tailPart = FlumeConfiguratorTopologyUtils.getTailPartProperty(propertyName, partIndex);
        Assert.assertTrue("The result of the getTailPartProperty method is not correct", tailPart.isEmpty());

        propertyName = "part1.part2";
        partIndex = 1;
        String expectedPart = "part2";
        tailPart = FlumeConfiguratorTopologyUtils.getTailPartProperty(propertyName, partIndex);
        Assert.assertEquals("The result of the getTailPartProperty method is not correct", tailPart, expectedPart);

        propertyName = "part1.part2";
        partIndex = 2;
        expectedPart = "";
        tailPart = FlumeConfiguratorTopologyUtils.getTailPartProperty(propertyName, partIndex);
        Assert.assertEquals("The result of the getTailPartProperty method is not correct", tailPart, expectedPart);

    }


    @Test
    public void testGetPropertyCommentFromText() {

        String property1CommentLine = " # Comment property 1 line  ";
        String property1Line = "part1_1.part1_2.part1_3 = value1";
        String property2CommentLine = " #Comment property 2 line";
        String property2Line = "part2_1.part2_2.part2_3 = value2";
        String noPropertyComment = "### NO COMMENT";
        String property3Line = "part3_1.part3_2.part3_3 = value3";

        String property4CommentLine = " # Comment property 4 line  ";
        String property4Line = "part1.part2.part3  = value4";
        String property5CommentLine = " # Comment property 5 line  ";
        String property5Line = "part1.part2=value5";


        List<String> lines = new ArrayList<>();
        lines.add(property1CommentLine);
        lines.add(property1Line);
        lines.add(property2CommentLine);
        lines.add(property2Line);
        lines.add(noPropertyComment);
        lines.add(property3Line);


        String propertyComment = FlumeConfiguratorTopologyUtils.getPropertyCommentFromText(lines,"");
        Assert.assertTrue("The result of the getPropertyCommentFromText method is not correct", propertyComment.isEmpty());

        String expectedPropertyComment = "Comment property 1 line";
        String propertyName = "part1_1.part1_2.part1_3";
        propertyComment = FlumeConfiguratorTopologyUtils.getPropertyCommentFromText(lines, propertyName);
        Assert.assertFalse("The result of the getPropertyCommentFromText method is not correct", propertyComment.isEmpty());
        Assert.assertEquals("The result of the getPropertyCommentFromText method is not correct", propertyComment, expectedPropertyComment);

        expectedPropertyComment = "Comment property 2 line";
        propertyName = "part2_1.part2_2.part2_3";
        propertyComment = FlumeConfiguratorTopologyUtils.getPropertyCommentFromText(lines, propertyName);
        Assert.assertFalse("The result of the getPropertyCommentFromText method is not correct", propertyComment.isEmpty());
        Assert.assertEquals("The result of the getPropertyCommentFromText method is not correct", propertyComment, expectedPropertyComment);

        propertyName = "part3_1.part3_2.part3_3";
        propertyComment = FlumeConfiguratorTopologyUtils.getPropertyCommentFromText(lines, propertyName);
        Assert.assertTrue("The result of the getPropertyCommentFromText method is not correct", propertyComment.isEmpty());

        lines = new ArrayList<>();
        lines.add(property4CommentLine);
        lines.add(property4Line);
        lines.add(property5CommentLine);
        lines.add(property5Line);


        expectedPropertyComment = "Comment property 5 line";
        propertyName = "part1.part2";
        propertyComment = FlumeConfiguratorTopologyUtils.getPropertyCommentFromText(lines, propertyName);
        Assert.assertFalse("The result of the getPropertyCommentFromText method is not correct", propertyComment.isEmpty());
        Assert.assertEquals("The result of the getPropertyCommentFromText method is not correct", propertyComment, expectedPropertyComment);


        lines = new ArrayList<>();
        lines.add(property5CommentLine);
        lines.add(property5Line);
        lines.add(property4CommentLine);
        lines.add(property4Line);


        expectedPropertyComment = "Comment property 5 line";
        propertyName = "part1.part2";
        propertyComment = FlumeConfiguratorTopologyUtils.getPropertyCommentFromText(lines, propertyName);
        Assert.assertFalse("The result of the getPropertyCommentFromText method is not correct", propertyComment.isEmpty());
        Assert.assertEquals("The result of the getPropertyCommentFromText method is not correct", propertyComment, expectedPropertyComment);

        String property6CommentLine = " # Comment property 6 line  ";
        String property6Line = "#part6_1.part6_2.part61_3 = value6";

        lines = new ArrayList<>();
        lines.add(property6CommentLine);
        lines.add(property6Line);

        propertyName = "part6_1.part6_2.part61_3";
        propertyComment = FlumeConfiguratorTopologyUtils.getPropertyCommentFromText(lines, propertyName);
        Assert.assertTrue("The result of the getPropertyCommentFromText method is not correct", propertyComment.isEmpty());

    }

    @Test
    public void testGetFlumeTopologyId() {

        String existComponent = "i3";
        String existComponentID = "38d0a869-d5f9-4cce-bd13-ed01e04ae15e";
        String nonExistComponent = "XX";

        String componentID = FlumeConfiguratorTopologyUtils.getFlumeTopologyId(flumeTopologyListGraph, nonExistComponent);
        Assert.assertTrue("The result of the getFlumeTopologyId method is not correct", componentID.isEmpty());

        componentID = FlumeConfiguratorTopologyUtils.getFlumeTopologyId(flumeTopologyListGraph, existComponent);
        Assert.assertFalse("The result of the getFlumeTopologyId method is not correct", componentID.isEmpty());
        Assert.assertEquals("The result of the getFlumeTopologyId method is not correct", componentID, existComponentID);

    }

    @Test
    public void testGetFlumeTopologyElement() {

        String agentSearchedID = "cbbd2d42-b905-4c17-b385-dbd33bb9871e";
        String agentSearchedName = "agent1";
        String sourceSearchedID = "4ba63761-5d62-9aa1-0986-0e9d8cfeed21";
        String sourceSearchedName = "source1";
        String interceptorSearchedID = "ae1cd432-12b1-2a45-2a2c-0432baec1aae";
        String interceptorSearchedName = "i2";
        String channelSearchedID = "4a514b0e-83b6-ed99-66f8-36bcf20b5f95";
        String channelSearchedName = "channel1";
        String sinkSearchedID = "dc5de583-21d6-4a28-5b7a-0223aefb1ffd";
        String sinkSearchedName = "sink1";
        String nonExistID = "xxxxxxx";

        FlumeTopology flumeTopologyElement = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyListGraph, null);
        Assert.assertNull("The result of the getFlumeTopologyElement method is not correct",flumeTopologyElement);

        flumeTopologyElement = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyListGraph, "");
        Assert.assertNull("The result of the getFlumeTopologyElement method is not correct",flumeTopologyElement);

        flumeTopologyElement = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyListGraph, nonExistID);
        Assert.assertNull("The result of the getFlumeTopologyElement method is not correct",flumeTopologyElement);

        flumeTopologyElement = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyListGraph, agentSearchedID);
        Assert.assertNotNull("The result of the getFlumeTopologyElement method is not correct",flumeTopologyElement);
        Assert.assertEquals("The result of the getFlumeTopologyElement method is not correct", flumeTopologyElement.getId(), agentSearchedID);
        Assert.assertEquals("The result of the getFlumeTopologyElement method is not correct", flumeTopologyElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME), agentSearchedName);

        flumeTopologyElement = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyListGraph, sourceSearchedID);
        Assert.assertNotNull("The result of the getFlumeTopologyElement method is not correct",flumeTopologyElement);
        Assert.assertEquals("The result of the getFlumeTopologyElement method is not correct", flumeTopologyElement.getId(), sourceSearchedID);
        Assert.assertEquals("The result of the getFlumeTopologyElement method is not correct", flumeTopologyElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME), sourceSearchedName);

        flumeTopologyElement = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyListGraph, interceptorSearchedID);
        Assert.assertNotNull("The result of the getFlumeTopologyElement method is not correct",flumeTopologyElement);
        Assert.assertEquals("The result of the getFlumeTopologyElement method is not correct", flumeTopologyElement.getId(), interceptorSearchedID);
        Assert.assertEquals("The result of the getFlumeTopologyElement method is not correct", flumeTopologyElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME), interceptorSearchedName);

        flumeTopologyElement = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyListGraph, channelSearchedID);
        Assert.assertNotNull("The result of the getFlumeTopologyElement method is not correct",flumeTopologyElement);
        Assert.assertEquals("The result of the getFlumeTopologyElement method is not correct", flumeTopologyElement.getId(), channelSearchedID);
        Assert.assertEquals("The result of the getFlumeTopologyElement method is not correct", flumeTopologyElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME), channelSearchedName);

        flumeTopologyElement = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyListGraph, sinkSearchedID);
        Assert.assertNotNull("The result of the getFlumeTopologyElement method is not correct",flumeTopologyElement);
        Assert.assertEquals("The result of the getFlumeTopologyElement method is not correct", flumeTopologyElement.getId(), sinkSearchedID);
        Assert.assertEquals("The result of the getFlumeTopologyElement method is not correct", flumeTopologyElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME), sinkSearchedName);

    }

    @Test
    public void testOrderSourceInterceptorsFromConnections() {

        String source_With_Multiple_Interceptors_ID = "4ba63761-5d62-9aa1-0986-0e9d8cfeed21";
        String interceptor_1_ID = "ec2dc456-24a3-5e12-7c2c-0213eabf2ddc";
        String interceptor_1_Name = "i1";
        String interceptor_2_ID = "ae1cd432-12b1-2a45-2a2c-0432baec1aae";
        String interceptor_2_Name = "i2";
        String source_With_Single_Interceptor_ID = "4ba63761-5d62-9aa1-0986-0e9d8cfeed21";
        String interceptor_3_ID = "38d0a869-d5f9-4cce-bd13-ed01e04ae15e";
        String interceptor_3_Name = "i3";

        List<FlumeTopology> flumeTopologyListConnections = new ArrayList<>();
        for (FlumeTopology flumeTopologyElement : flumeTopologyListGraph) {
            if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION.equals(flumeTopologyElement.getType())) {
                flumeTopologyListConnections.add(flumeTopologyElement);
            }
        }

        List<String> unorderedInterceptorListSourceMultipleInterceptors = new ArrayList<>();
        unorderedInterceptorListSourceMultipleInterceptors.add(interceptor_2_Name);
        unorderedInterceptorListSourceMultipleInterceptors.add(interceptor_1_Name);

        List<String> orderedInterceptorListSourceMultipleInterceptors = new ArrayList<>();
        orderedInterceptorListSourceMultipleInterceptors.add(interceptor_1_Name);
        orderedInterceptorListSourceMultipleInterceptors.add(interceptor_2_Name);

        List<String> unorderedInterceptorListSourceSingleInterceptor = new ArrayList<>();
        unorderedInterceptorListSourceSingleInterceptor.add(interceptor_3_Name);

        List<String> orderedInterceptorsList = FlumeConfiguratorTopologyUtils.orderSourceInterceptorsFromConnections(null, source_With_Multiple_Interceptors_ID, flumeTopologyListConnections, flumeTopologyListGraph);
        Assert.assertNull("The result of the orderSourceInterceptorsFromConnections method is not correct",orderedInterceptorsList);

        orderedInterceptorsList = FlumeConfiguratorTopologyUtils.orderSourceInterceptorsFromConnections(unorderedInterceptorListSourceSingleInterceptor, source_With_Single_Interceptor_ID, flumeTopologyListConnections, flumeTopologyListGraph);
        Assert.assertNotNull("The result of the orderSourceInterceptorsFromConnections method is not correct",orderedInterceptorsList);
        Assert.assertEquals("The result of the orderSourceInterceptorsFromConnections method is not correct", orderedInterceptorsList.size(), 1);
        for (int i=0; i< orderedInterceptorsList.size(); i++) {
            String interceptorName = orderedInterceptorsList.get(i);
            Assert.assertEquals("The result of the getFlumeTopologyId method is not correct", interceptorName, unorderedInterceptorListSourceSingleInterceptor.get(i));
        }

        orderedInterceptorsList = FlumeConfiguratorTopologyUtils.orderSourceInterceptorsFromConnections(unorderedInterceptorListSourceMultipleInterceptors, source_With_Multiple_Interceptors_ID, flumeTopologyListConnections, flumeTopologyListGraph);
        Assert.assertNotNull("The result of the orderSourceInterceptorsFromConnections method is not correct",orderedInterceptorsList);
        Assert.assertEquals("The result of the orderSourceInterceptorsFromConnections method is not correct", orderedInterceptorsList.size(), unorderedInterceptorListSourceMultipleInterceptors.size());
        Assert.assertEquals("The result of the orderSourceInterceptorsFromConnections method is not correct", orderedInterceptorsList.get(0), interceptor_1_Name);
        Assert.assertEquals("The result of the orderSourceInterceptorsFromConnections method is not correct", orderedInterceptorsList.get(1), interceptor_2_Name);

        orderedInterceptorsList = FlumeConfiguratorTopologyUtils.orderSourceInterceptorsFromConnections(orderedInterceptorListSourceMultipleInterceptors, source_With_Multiple_Interceptors_ID, flumeTopologyListConnections, flumeTopologyListGraph);
        Assert.assertNotNull("The result of the orderSourceInterceptorsFromConnections method is not correct",orderedInterceptorsList);
        Assert.assertEquals("The result of the orderSourceInterceptorsFromConnections method is not correct", orderedInterceptorsList.size(), orderedInterceptorListSourceMultipleInterceptors.size());
        Assert.assertEquals("The result of the orderSourceInterceptorsFromConnections method is not correct", orderedInterceptorsList.get(0), interceptor_1_Name);
        Assert.assertEquals("The result of the orderSourceInterceptorsFromConnections method is not correct", orderedInterceptorsList.get(1), interceptor_2_Name);
    }



    @Test
    public void testGetAllTargetConnections() {


        String agent1_ID = "cbbd2d42-b905-4c17-b385-dbd33bb9871e";
        String source1_ID = "4ba63761-5d62-9aa1-0986-0e9d8cfeed21";
        String source2_ID = "6cfae647-d552-5642-7947-547181a67a3c";
        String source3_ID = "6d3f87eb-7483-472a-87e2-17f9712047eb";
        String interceptor_i1_ID = "ec2dc456-24a3-5e12-7c2c-0213eabf2ddc";
        String interceptor_i2_ID = "ae1cd432-12b1-2a45-2a2c-0432baec1aae";
        String sink1_ID = "dc5de583-21d6-4a28-5b7a-0223aefb1ffd";
        String sinkgroup1_ID = "70c8433e-ab52-44c3-9894-72fa4fef53f6";
        String nonExistElementID = "xxxxxxxxxxx";


        List<FlumeTopology> flumeTopologyListConnections = new ArrayList<>();
        for (FlumeTopology flumeTopologyElement : flumeTopologyListGraph) {
            if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION.equals(flumeTopologyElement.getType())) {
                flumeTopologyListConnections.add(flumeTopologyElement);
            }
        }

        List<String> allTargetConnections = FlumeConfiguratorTopologyUtils.getAllTargetConnections(null,flumeTopologyListConnections);
        Assert.assertNotNull("The result of the getAllTargetConnections method is not correct",allTargetConnections);
        Assert.assertTrue("The result of the getAllTargetConnections method is not correct",allTargetConnections.isEmpty());

        allTargetConnections = FlumeConfiguratorTopologyUtils.getAllTargetConnections("",flumeTopologyListConnections);
        Assert.assertNotNull("The result of the getAllTargetConnections method is not correct",allTargetConnections);
        Assert.assertTrue("The result of the getAllTargetConnections method is not correct",allTargetConnections.isEmpty());

        allTargetConnections = FlumeConfiguratorTopologyUtils.getAllTargetConnections(nonExistElementID,flumeTopologyListConnections);
        Assert.assertNotNull("The result of the getAllTargetConnections method is not correct",allTargetConnections);
        Assert.assertTrue("The result of the getAllTargetConnections method is not correct",allTargetConnections.isEmpty());

        allTargetConnections = FlumeConfiguratorTopologyUtils.getAllTargetConnections(sink1_ID,flumeTopologyListConnections);
        Assert.assertNotNull("The result of the getAllTargetConnections method is not correct",allTargetConnections);
        Assert.assertFalse("The result of the getAllTargetConnections method is not correct",allTargetConnections.isEmpty());
        Assert.assertEquals("The result of the getAllTargetConnections method is not correct",allTargetConnections.size(), 1);
        Assert.assertEquals("The result of the getAllTargetConnections method is not correct",allTargetConnections.get(0), sinkgroup1_ID);

        allTargetConnections = FlumeConfiguratorTopologyUtils.getAllTargetConnections(sinkgroup1_ID,flumeTopologyListConnections);
        Assert.assertNotNull("The result of the getAllTargetConnections method is not correct",allTargetConnections);
        Assert.assertTrue("The result of the getAllTargetConnections method is not correct",allTargetConnections.isEmpty());

        allTargetConnections = FlumeConfiguratorTopologyUtils.getAllTargetConnections(interceptor_i1_ID,flumeTopologyListConnections);
        Assert.assertNotNull("The result of the getAllTargetConnections method is not correct",allTargetConnections);
        Assert.assertFalse("The result of the getAllTargetConnections method is not correct",allTargetConnections.isEmpty());
        Assert.assertEquals("The result of the getAllTargetConnections method is not correct",allTargetConnections.size(), 1);
        Assert.assertEquals("The result of the getAllTargetConnections method is not correct",allTargetConnections.get(0), interceptor_i2_ID);

        allTargetConnections = FlumeConfiguratorTopologyUtils.getAllTargetConnections(agent1_ID,flumeTopologyListConnections);
        Assert.assertNotNull("The result of the getAllTargetConnections method is not correct",allTargetConnections);
        Assert.assertFalse("The result of the getAllTargetConnections method is not correct",allTargetConnections.isEmpty());
        Assert.assertEquals("The result of the getAllTargetConnections method is not correct",allTargetConnections.size(), 3);
        for (String targetConnection : allTargetConnections) {
            Assert.assertTrue("The result of the getAllTargetConnections method is not correct",targetConnection.equals(source1_ID) || targetConnection.equals(source2_ID) || targetConnection.equals(source3_ID));
        }

    }


    @Test
    public void testGetInterceptorConnection() {

        String source_ID = "4ba63761-5d62-9aa1-0986-0e9d8cfeed21";
        String expected_first_interceptor_ID = "ec2dc456-24a3-5e12-7c2c-0213eabf2ddc";
        String sink1_ID = "dc5de583-21d6-4a28-5b7a-0223aefb1ffd";
        String nonExistElementID = "xxxxxxxxxxx";

        List<FlumeTopology> flumeTopologyListConnections = new ArrayList<>();
        for (FlumeTopology flumeTopologyElement : flumeTopologyListGraph) {
            if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION.equals(flumeTopologyElement.getType())) {
                flumeTopologyListConnections.add(flumeTopologyElement);
            }
        }

        String firstInterceptorID = FlumeConfiguratorTopologyUtils.getInterceptorConnection(null, flumeTopologyListConnections, flumeTopologyListGraph);
        Assert.assertNotNull("The result of the getInterceptorConnection method is not correct",firstInterceptorID);
        Assert.assertTrue("The result of the getInterceptorConnection method is not correct",firstInterceptorID.isEmpty());

        firstInterceptorID = FlumeConfiguratorTopologyUtils.getInterceptorConnection("", flumeTopologyListConnections, flumeTopologyListGraph);
        Assert.assertNotNull("The result of the getInterceptorConnection method is not correct",firstInterceptorID);
        Assert.assertTrue("The result of the getInterceptorConnection method is not correct",firstInterceptorID.isEmpty());

        firstInterceptorID = FlumeConfiguratorTopologyUtils.getInterceptorConnection(nonExistElementID, flumeTopologyListConnections, flumeTopologyListGraph);
        Assert.assertNotNull("The result of the getInterceptorConnection method is not correct",firstInterceptorID);
        Assert.assertTrue("The result of the getInterceptorConnection method is not correct",firstInterceptorID.isEmpty());

        firstInterceptorID = FlumeConfiguratorTopologyUtils.getInterceptorConnection(sink1_ID, flumeTopologyListConnections, flumeTopologyListGraph);
        Assert.assertNotNull("The result of the getInterceptorConnection method is not correct",firstInterceptorID);
        Assert.assertTrue("The result of the getInterceptorConnection method is not correct",firstInterceptorID.isEmpty());

        firstInterceptorID = FlumeConfiguratorTopologyUtils.getInterceptorConnection(source_ID, flumeTopologyListConnections, flumeTopologyListGraph);
        Assert.assertNotNull("The result of the getInterceptorConnection method is not correct",firstInterceptorID);
        Assert.assertFalse("The result of the getInterceptorConnection method is not correct",firstInterceptorID.isEmpty());
        Assert.assertEquals("The result of the getInterceptorConnection method is not correct",firstInterceptorID, expected_first_interceptor_ID);

    }


    @Test
    public void testGetLastInterceptorNameFromSource() {

        String property1_Name = "agent.sources.source1.interceptors";
        String property1_Value = "   i1 i2 i3   ";
        String expected_last_interceptor_name_source1 = "i3";
        String source1_Name = "source1";

        String property2_Name = "agent.sources.source2.interceptors";
        String property2_Value = "i4";
        String expected_last_interceptor_name_source2 = "i4";
        String source2_Name = "source2";

        String nonExistSource = "xxxxxxxxxx";

        Properties properties = new Properties();
        properties.put(property1_Name, property1_Value);
        properties.put(property2_Name, property2_Value);

        String lastInterceptorName = FlumeConfiguratorTopologyUtils.getLastInterceptorNameFromSource(null,  null);
        Assert.assertNotNull("The result of the getLastInterceptorNameFromSource method is not correct",lastInterceptorName);
        Assert.assertTrue("The result of the getLastInterceptorNameFromSource method is not correct",lastInterceptorName.isEmpty());

        lastInterceptorName = FlumeConfiguratorTopologyUtils.getLastInterceptorNameFromSource(null,  source1_Name);
        Assert.assertNotNull("The result of the getLastInterceptorNameFromSource method is not correct",lastInterceptorName);
        Assert.assertTrue("The result of the getLastInterceptorNameFromSource method is not correct",lastInterceptorName.isEmpty());

        lastInterceptorName = FlumeConfiguratorTopologyUtils.getLastInterceptorNameFromSource(properties,  null);
        Assert.assertNotNull("The result of the getLastInterceptorNameFromSource method is not correct",lastInterceptorName);
        Assert.assertTrue("The result of the getLastInterceptorNameFromSource method is not correct",lastInterceptorName.isEmpty());

        lastInterceptorName = FlumeConfiguratorTopologyUtils.getLastInterceptorNameFromSource(properties,  nonExistSource);
        Assert.assertNotNull("The result of the getLastInterceptorNameFromSource method is not correct",lastInterceptorName);
        Assert.assertTrue("The result of the getLastInterceptorNameFromSource method is not correct",lastInterceptorName.isEmpty());

        lastInterceptorName = FlumeConfiguratorTopologyUtils.getLastInterceptorNameFromSource(properties,  source1_Name);
        Assert.assertNotNull("The result of the getLastInterceptorNameFromSource method is not correct",lastInterceptorName);
        Assert.assertFalse("The result of the getLastInterceptorNameFromSource method is not correct",lastInterceptorName.isEmpty());
        Assert.assertEquals("The result of the getLastInterceptorNameFromSource method is not correct",lastInterceptorName, expected_last_interceptor_name_source1);

        lastInterceptorName = FlumeConfiguratorTopologyUtils.getLastInterceptorNameFromSource(properties,  source2_Name);
        Assert.assertNotNull("The result of the getLastInterceptorNameFromSourc emethod is not correct",lastInterceptorName);
        Assert.assertFalse("The result of the getLastInterceptorNameFromSource method is not correct",lastInterceptorName.isEmpty());
        Assert.assertEquals("The result of the getLastInterceptorNameFromSource method is not correct",lastInterceptorName, expected_last_interceptor_name_source2);


        try {
            String property3_Name = "agent2.sources.source2.interceptors";
            String property3_Value = "i6";
            properties.put(property3_Name, property3_Value);

            FlumeConfiguratorTopologyUtils.getLastInterceptorNameFromSource(properties,  source2_Name);

            //The exception must be thrown
            Assert.fail("The result of the getLastInterceptorNameFromSource method is not correct");
        } catch (Exception ex) {
            if (!(ex instanceof FlumeConfiguratorException)) {
                Assert.fail("The result of the getLastInterceptorNameFromSource method is not correct");
            }
        }

    }


    @Test
    public void testCalculateSlicesNumber() {

        String agent1 = "agent1";
        String source1 = "source1";
        String source2 = "source2";
        String channel = "channel";
        String sink = "sink";
        String nonExistAgent = "xxxxxx";

        FlumeTopology ftAgent1 = new FlumeTopology();
        ftAgent1.setId(agent1);
        ftAgent1.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT);

        FlumeTopology ftSource1 = new FlumeTopology();
        ftSource1.setId(source1);
        ftSource1.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE);

        FlumeTopology ftSource2 = new FlumeTopology();
        ftSource2.setId(source2);
        ftSource2.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE);

        FlumeTopology ftChannel = new FlumeTopology();
        ftChannel.setId(channel);
        ftChannel.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL);

        FlumeTopology ftSink = new FlumeTopology();
        ftSink.setId(sink);
        ftSink.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK);

        IGraph igraph = GraphFactory.createGraph("jgrapht");
        igraph.addGraphVertex(ftAgent1);
        igraph.addGraphVertex(ftSource1);
        igraph.addGraphVertex(ftSource2);
        igraph.addGraphVertex(ftChannel);
        igraph.addGraphVertex(ftSink);

        igraph.addGraphEdge(ftAgent1, ftSource1);
        igraph.addGraphEdge(ftAgent1, ftSource2);


        Map<String, IGraph> flumeGraphTopology = new HashedMap<>();
        flumeGraphTopology.put(agent1, igraph);

        int slicesNumber = FlumeConfiguratorTopologyUtils.calculateSlicesNumber(null, null);
        Assert.assertEquals("The result of the calculateSlicesNumber method is not correct",slicesNumber, 0);

        slicesNumber = FlumeConfiguratorTopologyUtils.calculateSlicesNumber(flumeGraphTopology, null);
        Assert.assertEquals("The result of the calculateSlicesNumber method is not correct",slicesNumber, 0);

        slicesNumber = FlumeConfiguratorTopologyUtils.calculateSlicesNumber(null, agent1);
        Assert.assertEquals("The result of the calculateSlicesNumber method is not correct",slicesNumber, 0);

        slicesNumber = FlumeConfiguratorTopologyUtils.calculateSlicesNumber(flumeGraphTopology, nonExistAgent);
        Assert.assertEquals("The result of the calculateSlicesNumber method is not correct",slicesNumber, 0);

        slicesNumber = FlumeConfiguratorTopologyUtils.calculateSlicesNumber(flumeGraphTopology, agent1);
        Assert.assertEquals("The result of the calculateSlicesNumber method is not correct",slicesNumber, 4);

        String interceptor1 = "interceptor1";
        FlumeTopology ftInterceptor1 = new FlumeTopology();
        ftInterceptor1.setId(interceptor1);
        ftInterceptor1.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR);
        igraph.addGraphVertex(ftInterceptor1);
        igraph.addGraphEdge(ftSource1, ftInterceptor1);

        slicesNumber = FlumeConfiguratorTopologyUtils.calculateSlicesNumber(flumeGraphTopology, agent1);
        Assert.assertEquals("The result of the calculateSlicesNumber method is not correct",slicesNumber, 5);


        String interceptor2 = "interceptor2";
        FlumeTopology ftInterceptor2 = new FlumeTopology();
        ftInterceptor2.setId(interceptor2);
        ftInterceptor2.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR);
        igraph.addGraphVertex(ftInterceptor2);
        igraph.addGraphEdge(ftInterceptor1, ftInterceptor2);

        slicesNumber = FlumeConfiguratorTopologyUtils.calculateSlicesNumber(flumeGraphTopology, agent1);
        Assert.assertEquals("The result of the calculateSlicesNumber method is not correct",slicesNumber, 6);


        String interceptor3 = "interceptor3";
        FlumeTopology ftInterceptor3 = new FlumeTopology();
        ftInterceptor3.setId(interceptor3);
        ftInterceptor3.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR);
        igraph.addGraphVertex(ftInterceptor3);
        igraph.addGraphEdge(ftSource2, ftInterceptor3);

        slicesNumber = FlumeConfiguratorTopologyUtils.calculateSlicesNumber(flumeGraphTopology, agent1);
        Assert.assertEquals("The result of the calculateSlicesNumber method is not correct",slicesNumber, 6);

    }


    @Test
    public void testGetMaxYCoordinate() {

        String agent1 = "agent1";
        String agent1_Y_coordinate = "50";
        String source1 = "source1";
        String source1_Y_coordinate = "150";
        String source2 = "source2";
        String source2_Y_coordinate = "250";
        int expected_max_Y_coordinate = 250;

        FlumeTopology ftAgent1 = new FlumeTopology();
        ftAgent1.setId(agent1);
        ftAgent1.setY(agent1_Y_coordinate);
        ftAgent1.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT);

        FlumeTopology ftSource1 = new FlumeTopology();
        ftSource1.setId(source1);
        ftSource1.setY(source1_Y_coordinate);
        ftSource1.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE);

        FlumeTopology ftSource2 = new FlumeTopology();
        ftSource2.setId(source2);
        ftSource2.setY(source2_Y_coordinate);
        ftSource2.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE);

        List<FlumeTopology> flumeTopologyList = new ArrayList<>();


        int max_Y_coordinate = FlumeConfiguratorTopologyUtils.getMaxYCoordinate(null);
        Assert.assertEquals("The result of the getMaxYCoordinate method is not correct",max_Y_coordinate, FlumeConfiguratorConstants.CANVAS_ELEMENTS_HEIGHT_PX_SEPARATION);

        max_Y_coordinate = FlumeConfiguratorTopologyUtils.getMaxYCoordinate(flumeTopologyList);
        Assert.assertEquals("The result of the getMaxYCoordinate method is not correct",max_Y_coordinate, FlumeConfiguratorConstants.CANVAS_ELEMENTS_HEIGHT_PX_SEPARATION);

        flumeTopologyList.add(ftAgent1);
        flumeTopologyList.add(ftSource1);
        flumeTopologyList.add(ftSource2);

        max_Y_coordinate = FlumeConfiguratorTopologyUtils.getMaxYCoordinate(flumeTopologyList);
        Assert.assertEquals("The result of the getMaxYCoordinate method is not correct",max_Y_coordinate, expected_max_Y_coordinate);

    }


    @Test
    public void testGetNextYCoordinate() {

        int max_Y_coordinate = 400;
        boolean isFirstElementSlice = true;
        int expected_next_Y_coordinate = max_Y_coordinate + FlumeConfiguratorConstants.CANVAS_ELEMENT_PX_HEIGHT + FlumeConfiguratorConstants.CANVAS_ELEMENTS_HEIGHT_PX_SEPARATION;

        int next_Y_coordinate = FlumeConfiguratorTopologyUtils.getNextYCoordinate(max_Y_coordinate, isFirstElementSlice);
        Assert.assertEquals("The result of the getNextYCoordinate method is not correct",next_Y_coordinate, max_Y_coordinate);

        isFirstElementSlice = false;
        next_Y_coordinate = FlumeConfiguratorTopologyUtils.getNextYCoordinate(max_Y_coordinate, isFirstElementSlice);
        Assert.assertEquals("The result of the getNextYCoordinate method is not correct",next_Y_coordinate, expected_next_Y_coordinate);

    }


    @Test
    public void testIsCorrectOrderSublist() {

        List<String> list = new ArrayList<>();
        list.add("elem1");
        list.add("elem2");
        list.add("elem3");
        list.add("elem4");
        list.add("elem5");

        List<String> correctSublist = new ArrayList<>();
        correctSublist.add("elem1");
        correctSublist.add("elem2");

        List<String> incorrectSublist = new ArrayList<>();
        incorrectSublist.add("elem2");
        incorrectSublist.add("elem4");

        boolean isCorrectOrderSublist = FlumeConfiguratorTopologyUtils.isCorrectOrderSublist(null, null);
        Assert.assertFalse("The result of the isCorrectOrderSublist method is not correct",isCorrectOrderSublist);

        isCorrectOrderSublist = FlumeConfiguratorTopologyUtils.isCorrectOrderSublist(list, null);
        Assert.assertFalse("The result of the isCorrectOrderSublist method is not correct",isCorrectOrderSublist);

        isCorrectOrderSublist = FlumeConfiguratorTopologyUtils.isCorrectOrderSublist(null, correctSublist);
        Assert.assertFalse("The result of the isCorrectOrderSublist method is not correct",isCorrectOrderSublist);

        isCorrectOrderSublist = FlumeConfiguratorTopologyUtils.isCorrectOrderSublist(correctSublist, list);
        Assert.assertFalse("The result of the isCorrectOrderSublist method is not correct",isCorrectOrderSublist);

        isCorrectOrderSublist = FlumeConfiguratorTopologyUtils.isCorrectOrderSublist(list, incorrectSublist);
        Assert.assertFalse("The result of the isCorrectOrderSublist method is not correct",isCorrectOrderSublist);

        isCorrectOrderSublist = FlumeConfiguratorTopologyUtils.isCorrectOrderSublist(list, correctSublist);
        Assert.assertTrue("The result of the isCorrectOrderSublist method is not correct",isCorrectOrderSublist);

    }


    @Test
    public void testGetMapSharedChannelsSourcesRelation() {

        String agent1_Name = "agent1";
        String agent2_Name = "agent2";
        String agent1_shared_channel = "channel1";
        String agent2_shared_channel = "channel3";
        String agent1_shared_channel_source1 = "source1";
        String agent1_shared_channel_source2 = "source2";
        String agent2_shared_channel_source4 = "source4";
        String agent2_shared_channel_source5 = "source5";


        Map<String, List<String>> mapSharedChannelsSourcesRelation = FlumeConfiguratorTopologyUtils.getMapSharedChannelsSourcesRelation(null, null, null);
        Assert.assertNotNull("The result of the getMapSharedChannelsSourcesRelation method is not correct",mapSharedChannelsSourcesRelation);
        Assert.assertTrue("The result of the getMapSharedChannelsSourcesRelation method is not correct",mapSharedChannelsSourcesRelation.isEmpty());

        mapSharedChannelsSourcesRelation = FlumeConfiguratorTopologyUtils.getMapSharedChannelsSourcesRelation(null, sourcesChannelsRelationsMap, agent1_Name);
        Assert.assertNotNull("The result of the getMapSharedChannelsSourcesRelation method is not correct",mapSharedChannelsSourcesRelation);
        Assert.assertTrue("The result of the getMapSharedChannelsSourcesRelation method is not correct",mapSharedChannelsSourcesRelation.isEmpty());

        mapSharedChannelsSourcesRelation = FlumeConfiguratorTopologyUtils.getMapSharedChannelsSourcesRelation(listSharedSourcesGraphAgent1, null, agent1_Name);
        Assert.assertNotNull("The result of the getMapSharedChannelsSourcesRelation method is not correct",mapSharedChannelsSourcesRelation);
        Assert.assertTrue("The result of the getMapSharedChannelsSourcesRelation method is not correct",mapSharedChannelsSourcesRelation.isEmpty());

        mapSharedChannelsSourcesRelation = FlumeConfiguratorTopologyUtils.getMapSharedChannelsSourcesRelation(listSharedSourcesGraphAgent1, sourcesChannelsRelationsMap, null);
        Assert.assertNotNull("The result of the getMapSharedChannelsSourcesRelation method is not correct",mapSharedChannelsSourcesRelation);
        Assert.assertTrue("The result of the getMapSharedChannelsSourcesRelation method is not correct",mapSharedChannelsSourcesRelation.isEmpty());

        mapSharedChannelsSourcesRelation = FlumeConfiguratorTopologyUtils.getMapSharedChannelsSourcesRelation(listSharedSourcesGraphAgent1, sourcesChannelsRelationsMap, agent1_Name);
        Assert.assertNotNull("The result of the getMapSharedChannelsSourcesRelation method is not correct",mapSharedChannelsSourcesRelation);
        Assert.assertFalse("The result of the getMapSharedChannelsSourcesRelation method is not correct",mapSharedChannelsSourcesRelation.isEmpty());
        Assert.assertEquals("The result of the getMapSharedChannelsSourcesRelation method is not correct",mapSharedChannelsSourcesRelation.size(), 1);

        List<String> agent1_shared_channel_sources_list =  mapSharedChannelsSourcesRelation.get(agent1_shared_channel);

        Assert.assertEquals("The result of the getMapSharedChannelsSourcesRelation method is not correct",agent1_shared_channel_sources_list.size(), 2);
        for (String sourceName : agent1_shared_channel_sources_list) {
            Assert.assertTrue("The result of the getMapSharedChannelsSourcesRelation method is not correct",sourceName.equals(agent1_shared_channel_source1) || sourceName.equals(agent1_shared_channel_source2) );
        }

        mapSharedChannelsSourcesRelation = FlumeConfiguratorTopologyUtils.getMapSharedChannelsSourcesRelation(listSharedSourcesGraphAgent2, sourcesChannelsRelationsMap, agent2_Name);
        Assert.assertNotNull("The result of the getMapSharedChannelsSourcesRelation method is not correct",mapSharedChannelsSourcesRelation);
        Assert.assertFalse("The result of the getMapSharedChannelsSourcesRelation method is not correct",mapSharedChannelsSourcesRelation.isEmpty());
        Assert.assertEquals("The result of the getMapSharedChannelsSourcesRelation method is not correct",mapSharedChannelsSourcesRelation.size(), 1);

        List<String> agent2_shared_channel_sources_list =  mapSharedChannelsSourcesRelation.get(agent2_shared_channel);

        Assert.assertEquals("The result of the getMapSharedChannelsSourcesRelation method is not correct",agent2_shared_channel_sources_list.size(), 2);
        for (String sourceName : agent2_shared_channel_sources_list) {
            Assert.assertTrue("The result of the getMapSharedChannelsSourcesRelation method is not correct",sourceName.equals(agent2_shared_channel_source4) || sourceName.equals(agent2_shared_channel_source5) );
        }
    }


    @Test
    public void testGetCompleteSharedSourcesList() {

        String agent1_Name = "agent1";
        String agent2_Name = "agent2";
        String agent1_shared_channel_source1 = "source1";
        String agent1_shared_channel_source2 = "source2";
        String agent2_shared_channel_source4 = "source4";
        String agent2_shared_channel_source5 = "source5";

        Map<String, List<String>> mapSharedChannelsSourcesRelation_Agent1 = FlumeConfiguratorTopologyUtils.getMapSharedChannelsSourcesRelation(listSharedSourcesGraphAgent1, sourcesChannelsRelationsMap, agent1_Name);
        Map<String, List<String>> mapSharedChannelsSourcesRelation_Agent2 = FlumeConfiguratorTopologyUtils.getMapSharedChannelsSourcesRelation(listSharedSourcesGraphAgent2, sourcesChannelsRelationsMap, agent2_Name);

        List<String> completeSharedSourcesList = FlumeConfiguratorTopologyUtils.getCompleteSharedSourcesList(null);
        Assert.assertNull("The result of the getCompleteSharedSourcesList method is not correct",completeSharedSourcesList);

        completeSharedSourcesList = FlumeConfiguratorTopologyUtils.getCompleteSharedSourcesList(mapSharedChannelsSourcesRelation_Agent1);
        Assert.assertNotNull("The result of the getCompleteSharedSourcesList method is not correct",completeSharedSourcesList);
        Assert.assertEquals("The result of the getCompleteSharedSourcesList method is not correct",completeSharedSourcesList.size(), 2);

        for (String sourceName : completeSharedSourcesList) {
            Assert.assertTrue("The result of the getCompleteSharedSourcesList method is not correct",sourceName.equals(agent1_shared_channel_source1) || sourceName.equals(agent1_shared_channel_source2) );
        }

        completeSharedSourcesList = FlumeConfiguratorTopologyUtils.getCompleteSharedSourcesList(mapSharedChannelsSourcesRelation_Agent2);
        Assert.assertNotNull("The result of the getCompleteSharedSourcesList method is not correct",completeSharedSourcesList);
        Assert.assertEquals("The result of the getCompleteSharedSourcesList method is not correct",completeSharedSourcesList.size(), 2);

        for (String sourceName : completeSharedSourcesList) {
            Assert.assertTrue("The result of the getCompleteSharedSourcesList method is not correct",sourceName.equals(agent2_shared_channel_source4) || sourceName.equals(agent2_shared_channel_source5) );
        }
    }


    @Test
    public void testGetCorrectSourcesPermutations() {

        String agent1_Name = "agent1";
        String agent2_Name = "agent2";
        String agent1_shared_channel_source1 = "source1";
        String agent1_shared_channel_source2 = "source2";
        String agent2_shared_channel_source4 = "source4";
        String agent2_shared_channel_source5 = "source5";

        Map<String, List<String>> mapSharedChannelsSourcesRelation_Agent1 = FlumeConfiguratorTopologyUtils.getMapSharedChannelsSourcesRelation(listSharedSourcesGraphAgent1, sourcesChannelsRelationsMap, agent1_Name);
        Map<String, List<String>> mapSharedChannelsSourcesRelation_Agent2 = FlumeConfiguratorTopologyUtils.getMapSharedChannelsSourcesRelation(listSharedSourcesGraphAgent2, sourcesChannelsRelationsMap, agent2_Name);

        List<String> completeSharedSourcesList_Agent1 = FlumeConfiguratorTopologyUtils.getCompleteSharedSourcesList(mapSharedChannelsSourcesRelation_Agent1);
        List<String> completeSharedSourcesList_Agent2 = FlumeConfiguratorTopologyUtils.getCompleteSharedSourcesList(mapSharedChannelsSourcesRelation_Agent2);

        Collection<List<String>> correctSourcesPermutations = FlumeConfiguratorTopologyUtils.getCorrectSourcesPermutations (null, null);
        Assert.assertNotNull("The result of the getCorrectSourcesPermutations method is not correct",correctSourcesPermutations);
        Assert.assertTrue("The result of the getCorrectSourcesPermutations method is not correct",correctSourcesPermutations.isEmpty());

        correctSourcesPermutations = FlumeConfiguratorTopologyUtils.getCorrectSourcesPermutations (completeSharedSourcesList_Agent1, null);
        Assert.assertNotNull("The result of the getCorrectSourcesPermutations method is not correct",correctSourcesPermutations);
        Assert.assertTrue("The result of the getCorrectSourcesPermutations method is not correct",correctSourcesPermutations.isEmpty());

        correctSourcesPermutations = FlumeConfiguratorTopologyUtils.getCorrectSourcesPermutations (null, mapSharedChannelsSourcesRelation_Agent1);
        Assert.assertNotNull("The result of the getCorrectSourcesPermutations method is not correct",correctSourcesPermutations);
        Assert.assertTrue("The result of the getCorrectSourcesPermutations method is not correct",correctSourcesPermutations.isEmpty());

        correctSourcesPermutations = FlumeConfiguratorTopologyUtils.getCorrectSourcesPermutations (completeSharedSourcesList_Agent1, mapSharedChannelsSourcesRelation_Agent1);
        Assert.assertNotNull("The result of the getCorrectSourcesPermutations method is not correct",correctSourcesPermutations);
        Assert.assertFalse("The result of the getCorrectSourcesPermutations method is not correct",correctSourcesPermutations.isEmpty());
        Assert.assertEquals("The result of the getCorrectSourcesPermutations method is not correct",correctSourcesPermutations.size(), 2);

        for (List<String> sourcePermutation : correctSourcesPermutations) {
            Assert.assertEquals("The result of the getCorrectSourcesPermutations method is not correct",sourcePermutation.size(), 2);
            for(String sourceName : sourcePermutation) {
                Assert.assertTrue("The result of the getCorrectSourcesPermutations method is not correct",sourceName.equals(agent1_shared_channel_source1) || sourceName.equals(agent1_shared_channel_source2) );
            }
        }

        correctSourcesPermutations = FlumeConfiguratorTopologyUtils.getCorrectSourcesPermutations (completeSharedSourcesList_Agent2, mapSharedChannelsSourcesRelation_Agent2);
        Assert.assertNotNull("The result of the getCorrectSourcesPermutations method is not correct",correctSourcesPermutations);
        Assert.assertFalse("The result of the getCorrectSourcesPermutations method is not correct",correctSourcesPermutations.isEmpty());
        Assert.assertEquals("The result of the getCorrectSourcesPermutations method is not correct",correctSourcesPermutations.size(), 2);

        for (List<String> sourcePermutation : correctSourcesPermutations) {
            Assert.assertEquals("The result of the getCorrectSourcesPermutations method is not correct",sourcePermutation.size(), 2);
            for(String sourceName : sourcePermutation) {
                Assert.assertTrue("The result of the getCorrectSourcesPermutations method is not correct",sourceName.equals(agent2_shared_channel_source4) || sourceName.equals(agent2_shared_channel_source5) );
            }
        }
    }



    @Test
    public void testGetMapChannelSourcesNumberRelation() {

        String agent1_Name = "agent1";
        String agent2_Name = "agent2";

        String agent1_channel1Name = "channel1";
        String agent2_channel3Name = "channel3";


        IGraph agent1_graph = flumeGraphTopology.get(agent1_Name);
        IGraph agent2_graph = flumeGraphTopology.get(agent2_Name);

        Map<String, List<String>> mapSharedChannelsSourcesRelation_Agent1 = FlumeConfiguratorTopologyUtils.getMapSharedChannelsSourcesRelation(listSharedSourcesGraphAgent1, sourcesChannelsRelationsMap, agent1_Name);
        Map<String, List<String>> mapSharedChannelsSourcesRelation_Agent2 = FlumeConfiguratorTopologyUtils.getMapSharedChannelsSourcesRelation(listSharedSourcesGraphAgent2, sourcesChannelsRelationsMap, agent2_Name);

        List<String> completeSharedSourcesList_Agent1 = FlumeConfiguratorTopologyUtils.getCompleteSharedSourcesList(mapSharedChannelsSourcesRelation_Agent1);
        List<String> completeSharedSourcesList_Agent2 = FlumeConfiguratorTopologyUtils.getCompleteSharedSourcesList(mapSharedChannelsSourcesRelation_Agent2);

        Map<String,Integer> mapChannelSourcesNumberRelation = FlumeConfiguratorTopologyUtils.getMapChannelSourcesNumberRelation(null, null, null);
        Assert.assertNotNull("The result of the getMapChannelSourcesNumberRelation method is not correct",mapChannelSourcesNumberRelation);
        Assert.assertTrue("The result of the getMapChannelSourcesNumberRelation method is not correct",mapChannelSourcesNumberRelation.isEmpty());

        mapChannelSourcesNumberRelation = FlumeConfiguratorTopologyUtils.getMapChannelSourcesNumberRelation(null, agent1_graph, flumeTopologyListGraph);
        Assert.assertNotNull("The result of the getMapChannelSourcesNumberRelation method is not correct",mapChannelSourcesNumberRelation);
        Assert.assertTrue("The result of the getMapChannelSourcesNumberRelation method is not correct",mapChannelSourcesNumberRelation.isEmpty());

        mapChannelSourcesNumberRelation = FlumeConfiguratorTopologyUtils.getMapChannelSourcesNumberRelation(completeSharedSourcesList_Agent1, null, flumeTopologyListGraph);
        Assert.assertNotNull("The result of the getMapChannelSourcesNumberRelation method is not correct",mapChannelSourcesNumberRelation);
        Assert.assertTrue("The result of the getMapChannelSourcesNumberRelation method is not correct",mapChannelSourcesNumberRelation.isEmpty());

        mapChannelSourcesNumberRelation = FlumeConfiguratorTopologyUtils.getMapChannelSourcesNumberRelation(completeSharedSourcesList_Agent1, agent1_graph, null);
        Assert.assertNotNull("The result of the getMapChannelSourcesNumberRelation method is not correct",mapChannelSourcesNumberRelation);
        Assert.assertTrue("The result of the getMapChannelSourcesNumberRelation method is not correct",mapChannelSourcesNumberRelation.isEmpty());

        mapChannelSourcesNumberRelation = FlumeConfiguratorTopologyUtils.getMapChannelSourcesNumberRelation(completeSharedSourcesList_Agent1, agent1_graph, flumeTopologyListGraph);
        Assert.assertNotNull("The result of the getMapChannelSourcesNumberRelation method is not correct",mapChannelSourcesNumberRelation);
        Assert.assertFalse("The result of the getMapChannelSourcesNumberRelation method is not correct",mapChannelSourcesNumberRelation.isEmpty());
        Assert.assertEquals("The result of the getMapChannelSourcesNumberRelation method is not correct",mapChannelSourcesNumberRelation.size(), 1);

        Integer agent1_channel1_sourcesNumber = mapChannelSourcesNumberRelation.get(agent1_channel1Name);
        Assert.assertEquals("The result of the getMapChannelSourcesNumberRelation method is not correct", agent1_channel1_sourcesNumber, new Integer(2));

        mapChannelSourcesNumberRelation = FlumeConfiguratorTopologyUtils.getMapChannelSourcesNumberRelation(completeSharedSourcesList_Agent2, agent2_graph, flumeTopologyListGraph);
        Assert.assertNotNull("The result of the getMapChannelSourcesNumberRelation method is not correct",mapChannelSourcesNumberRelation);
        Assert.assertFalse("The result of the getMapChannelSourcesNumberRelation method is not correct",mapChannelSourcesNumberRelation.isEmpty());
        Assert.assertEquals("The result of the getMapChannelSourcesNumberRelation method is not correct",mapChannelSourcesNumberRelation.size(), 1);

        Integer agent2_channel3_sourcesNumber = mapChannelSourcesNumberRelation.get(agent2_channel3Name);
        Assert.assertEquals("The result of the getMapChannelSourcesNumberRelation method is not correct", agent2_channel3_sourcesNumber, new Integer(2));

    }


    @Test
    public void testGetMapSourcesIndependentChannelsRelation() {

        String agent1_Name = "agent1";
        String agent2_Name = "agent2";

        IGraph agent1_graph = flumeGraphTopology.get(agent1_Name);
        IGraph agent2_graph = flumeGraphTopology.get(agent2_Name);

        Map<String, List<String>> mapSharedChannelsSourcesRelation_Agent1 = FlumeConfiguratorTopologyUtils.getMapSharedChannelsSourcesRelation(listSharedSourcesGraphAgent1, sourcesChannelsRelationsMap, agent1_Name);
        Map<String, List<String>> mapSharedChannelsSourcesRelation_Agent2 = FlumeConfiguratorTopologyUtils.getMapSharedChannelsSourcesRelation(listSharedSourcesGraphAgent2, sourcesChannelsRelationsMap, agent2_Name);

        List<String> completeSharedSourcesList_Agent1 = FlumeConfiguratorTopologyUtils.getCompleteSharedSourcesList(mapSharedChannelsSourcesRelation_Agent1);
        List<String> completeSharedSourcesList_Agent2 = FlumeConfiguratorTopologyUtils.getCompleteSharedSourcesList(mapSharedChannelsSourcesRelation_Agent2);

        Map<String,Integer> mapChannelSourcesNumberRelation_Agent1 = FlumeConfiguratorTopologyUtils.getMapChannelSourcesNumberRelation(completeSharedSourcesList_Agent1, agent1_graph, flumeTopologyListGraph);
        Map<String,Integer> mapChannelSourcesNumberRelation_Agent2 = FlumeConfiguratorTopologyUtils.getMapChannelSourcesNumberRelation(completeSharedSourcesList_Agent2, agent2_graph, flumeTopologyListGraph);

        Map<String,List<String>> mapSourcesIndependentChannelsRelation = FlumeConfiguratorTopologyUtils.getMapSourcesIndependentChannelsRelation(null, null, null, null);
        Assert.assertNotNull("The result of the getMapSourcesIndependentChannelsRelation method is not correct",mapSourcesIndependentChannelsRelation);
        Assert.assertTrue("The result of the getMapSourcesIndependentChannelsRelation method is not correct",mapSourcesIndependentChannelsRelation.isEmpty());

        mapSourcesIndependentChannelsRelation = FlumeConfiguratorTopologyUtils.getMapSourcesIndependentChannelsRelation(null, agent1_graph, flumeTopologyListGraph, mapChannelSourcesNumberRelation_Agent1);
        Assert.assertNotNull("The result of the getMapSourcesIndependentChannelsRelation method is not correct",mapSourcesIndependentChannelsRelation);
        Assert.assertTrue("The result of the getMapSourcesIndependentChannelsRelation method is not correct",mapSourcesIndependentChannelsRelation.isEmpty());

        mapSourcesIndependentChannelsRelation = FlumeConfiguratorTopologyUtils.getMapSourcesIndependentChannelsRelation(completeSharedSourcesList_Agent1, null, flumeTopologyListGraph, mapChannelSourcesNumberRelation_Agent1);
        Assert.assertNotNull("The result of the getMapSourcesIndependentChannelsRelation method is not correct",mapSourcesIndependentChannelsRelation);
        Assert.assertTrue("The result of the getMapSourcesIndependentChannelsRelation method is not correct",mapSourcesIndependentChannelsRelation.isEmpty());

        mapSourcesIndependentChannelsRelation = FlumeConfiguratorTopologyUtils.getMapSourcesIndependentChannelsRelation(completeSharedSourcesList_Agent1, agent1_graph, null, mapChannelSourcesNumberRelation_Agent1);
        Assert.assertNotNull("The result of the getMapSourcesIndependentChannelsRelation method is not correct",mapSourcesIndependentChannelsRelation);
        Assert.assertTrue("The result of the getMapSourcesIndependentChannelsRelation method is not correct",mapSourcesIndependentChannelsRelation.isEmpty());

        mapSourcesIndependentChannelsRelation = FlumeConfiguratorTopologyUtils.getMapSourcesIndependentChannelsRelation(completeSharedSourcesList_Agent1, agent1_graph, flumeTopologyListGraph, null);
        Assert.assertNotNull("The result of the getMapSourcesIndependentChannelsRelation method is not correct",mapSourcesIndependentChannelsRelation);
        Assert.assertTrue("The result of the getMapSourcesIndependentChannelsRelation method is not correct",mapSourcesIndependentChannelsRelation.isEmpty());

        mapSourcesIndependentChannelsRelation = FlumeConfiguratorTopologyUtils.getMapSourcesIndependentChannelsRelation(completeSharedSourcesList_Agent1, agent1_graph, flumeTopologyListGraph, mapChannelSourcesNumberRelation_Agent1);
        Assert.assertNotNull("The result of the getMapSourcesIndependentChannelsRelation method is not correct",mapSourcesIndependentChannelsRelation);
        Assert.assertFalse("The result of the getMapSourcesIndependentChannelsRelation method is not correct",mapSourcesIndependentChannelsRelation.isEmpty());
        Assert.assertEquals("The result of the getMapSourcesIndependentChannelsRelation method is not correct",mapSourcesIndependentChannelsRelation.size(), 2);

        for (String sourceName : mapSourcesIndependentChannelsRelation.keySet()) {
            List<String> sourceIndependentChannelsList = mapSourcesIndependentChannelsRelation.get(sourceName);
            Assert.assertTrue("The result of the getMapSourcesIndependentChannelsRelation method is not correct",sourceIndependentChannelsList.isEmpty());
        }

        mapSourcesIndependentChannelsRelation = FlumeConfiguratorTopologyUtils.getMapSourcesIndependentChannelsRelation(completeSharedSourcesList_Agent2, agent2_graph, flumeTopologyListGraph, mapChannelSourcesNumberRelation_Agent2);
        Assert.assertNotNull("The result of the getMapSourcesIndependentChannelsRelation method is not correct",mapSourcesIndependentChannelsRelation);
        Assert.assertFalse("The result of the getMapSourcesIndependentChannelsRelation method is not correct",mapSourcesIndependentChannelsRelation.isEmpty());
        Assert.assertEquals("The result of the getMapSourcesIndependentChannelsRelation method is not correct",mapSourcesIndependentChannelsRelation.size(), 2);

        for (String sourceName : mapSourcesIndependentChannelsRelation.keySet()) {
            List<String> sourceIndependentChannelsList = mapSourcesIndependentChannelsRelation.get(sourceName);
            Assert.assertTrue("The result of the getMapSourcesIndependentChannelsRelation method is not correct",sourceIndependentChannelsList.isEmpty());
        }
    }



    @Test
    public void testGetSourceInterceptorsList() {

        String agent1_Name = "agent1";
        String agent2_Name = "agent2";
        String source1_ID = "4ba63761-5d62-9aa1-0986-0e9d8cfeed21";
        String sourcd2_ID = "6cfae647-d552-5642-7947-547181a67a3c";
        String source1_interceptor1_name = "i1";
        String source1_interceptor2_name = "i2";
        String source2_interceptor3_name = "i3";

        IGraph agent1_graph = flumeGraphTopology.get(agent1_Name);
        IGraph agent2_graph = flumeGraphTopology.get(agent2_Name);

        FlumeTopology source1_flume_topology_element = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyListGraph, source1_ID);
        FlumeTopology source2_flume_topology_element = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyListGraph, sourcd2_ID);

        List<String> sourceInterceptorsList = FlumeConfiguratorTopologyUtils.getSourceInterceptorsList(null, null);
        Assert.assertNotNull("The result of the getSourceInterceptorsList method is not correct",sourceInterceptorsList);
        Assert.assertTrue("The result of the getSourceInterceptorsList method is not correct",sourceInterceptorsList.isEmpty());

        sourceInterceptorsList = FlumeConfiguratorTopologyUtils.getSourceInterceptorsList(null, agent1_graph);
        Assert.assertNotNull("The result of the getSourceInterceptorsList method is not correct",sourceInterceptorsList);
        Assert.assertTrue("The result of the getSourceInterceptorsList method is not correct",sourceInterceptorsList.isEmpty());

        sourceInterceptorsList = FlumeConfiguratorTopologyUtils.getSourceInterceptorsList(source1_flume_topology_element, null);
        Assert.assertNotNull("The result of the getSourceInterceptorsList method is not correct",sourceInterceptorsList);
        Assert.assertTrue("The result of the getSourceInterceptorsList method is not correct",sourceInterceptorsList.isEmpty());

        sourceInterceptorsList = FlumeConfiguratorTopologyUtils.getSourceInterceptorsList(source1_flume_topology_element , agent1_graph);
        Assert.assertNotNull("The result of the getSourceInterceptorsList method is not correct",sourceInterceptorsList);
        Assert.assertFalse("The result of the getSourceInterceptorsList method is not correct",sourceInterceptorsList.isEmpty());
        Assert.assertEquals("The result of the getSourceInterceptorsList method is not correct",sourceInterceptorsList.size(), 2);
        for (String interceptorName : sourceInterceptorsList) {
            Assert.assertTrue("The result of the getSourceInterceptorsList method is not correct",interceptorName.equals(source1_interceptor1_name) || interceptorName.equals(source1_interceptor2_name));
        }

        sourceInterceptorsList = FlumeConfiguratorTopologyUtils.getSourceInterceptorsList(source2_flume_topology_element , agent1_graph);
        Assert.assertNotNull("The result of the getSourceInterceptorsList method is not correct",sourceInterceptorsList);
        Assert.assertFalse("The result of the getSourceInterceptorsList method is not correct",sourceInterceptorsList.isEmpty());
        Assert.assertEquals("The result of the getSourceInterceptorsList method is not correct",sourceInterceptorsList.size(), 1);
        for (String interceptorName : sourceInterceptorsList) {
            Assert.assertTrue("The result of the getSourceInterceptorsList method is not correct",interceptorName.equals(source2_interceptor3_name));
        }
    }



    @Test
    public void testGetMapSourcesInterceptorsRelation() {

        String agent1_Name = "agent1";
        String agent2_Name = "agent2";
        String source1_Name = "source1";
        String source2_Name = "source2";
        String source3_Name = "source3";
        String source4_Name = "source4";
        String source5_Name = "source5";
        String source6_Name = "source6";
        String interceptor1_Name = "i1";
        String interceptor2_Name = "i2";
        String interceptor3_Name = "i3";
        String interceptor4_Name = "i4";
        String interceptor5_Name = "i5";

        IGraph agent1_graph = flumeGraphTopology.get(agent1_Name);
        IGraph agent2_graph = flumeGraphTopology.get(agent2_Name);

        Map<String,List<String>> mapSourcesInterceptorsRelation = FlumeConfiguratorTopologyUtils.getMapSourcesInterceptorsRelation(null);
        Assert.assertNotNull("The result of the getMapSourcesInterceptorsRelation method is not correct",mapSourcesInterceptorsRelation);
        Assert.assertTrue("The result of the getMapSourcesInterceptorsRelation method is not correct",mapSourcesInterceptorsRelation.isEmpty());

        mapSourcesInterceptorsRelation = FlumeConfiguratorTopologyUtils.getMapSourcesInterceptorsRelation(agent1_graph);
        Assert.assertNotNull("The result of the getMapSourcesInterceptorsRelation method is not correct",mapSourcesInterceptorsRelation);
        Assert.assertFalse("The result of the getMapSourcesInterceptorsRelation method is not correct",mapSourcesInterceptorsRelation.isEmpty());
        Assert.assertEquals("The result of the getMapSourcesInterceptorsRelation method is not correct",mapSourcesInterceptorsRelation.size(), 3);
        for (String sourceName : mapSourcesInterceptorsRelation.keySet()) {
            Assert.assertTrue("The result of the getMapSourcesInterceptorsRelation method is not correct",sourceName.equals(source1_Name) || sourceName.equals(source2_Name) || sourceName.equals(source3_Name));
            List<String> sourceInterceptorsList = mapSourcesInterceptorsRelation.get(sourceName);

            if (sourceName.equals(source1_Name)) {
                Assert.assertEquals("The result of the getMapSourcesInterceptorsRelation method is not correct",sourceInterceptorsList.size(), 2);
                for(String interceptorName : sourceInterceptorsList) {
                    Assert.assertTrue("The result of the getMapSourcesInterceptorsRelation method is not correct",interceptorName.equals(interceptor1_Name) || interceptorName.equals(interceptor2_Name));
                }
            } else if (sourceName.equals(source2_Name)) {
                Assert.assertEquals("The result of the getMapSourcesInterceptorsRelation method is not correct",sourceInterceptorsList.size(), 1);
                Assert.assertEquals("The result of the getMapSourcesInterceptorsRelation method is not correct",sourceInterceptorsList.get(0), interceptor3_Name);
            } else if (sourceName.equals(source3_Name)) {
                Assert.assertEquals("The result of the getMapSourcesInterceptorsRelation method is not correct",sourceInterceptorsList.size(), 0);
            }
        }

        mapSourcesInterceptorsRelation = FlumeConfiguratorTopologyUtils.getMapSourcesInterceptorsRelation(agent2_graph);
        Assert.assertNotNull("The result of the getMapSourcesInterceptorsRelation method is not correct",mapSourcesInterceptorsRelation);
        Assert.assertFalse("The result of the getMapSourcesInterceptorsRelation method is not correct",mapSourcesInterceptorsRelation.isEmpty());
        Assert.assertEquals("The result of the getMapSourcesInterceptorsRelation method is not correct",mapSourcesInterceptorsRelation.size(), 3);
        for (String sourceName : mapSourcesInterceptorsRelation.keySet()) {
            Assert.assertTrue("The result of the getMapSourcesInterceptorsRelation method is not correct",sourceName.equals(source4_Name) || sourceName.equals(source5_Name) || sourceName.equals(source6_Name));
            List<String> sourceInterceptorsList = mapSourcesInterceptorsRelation.get(sourceName);

            if (sourceName.equals(source4_Name)) {
                Assert.assertEquals("The result of the getMapSourcesInterceptorsRelation method is not correct",sourceInterceptorsList.size(), 1);
                Assert.assertEquals("The result of the getMapSourcesInterceptorsRelation method is not correct",sourceInterceptorsList.get(0), interceptor4_Name);
            } else if (sourceName.equals(source5_Name)) {
                Assert.assertEquals("The result of the getMapSourcesInterceptorsRelation method is not correct",sourceInterceptorsList.size(), 1);
                Assert.assertEquals("The result of the getMapSourcesInterceptorsRelation method is not correct",sourceInterceptorsList.get(0), interceptor5_Name);
            } else if (sourceName.equals(source6_Name)) {
                Assert.assertEquals("The result of the getMapSourcesInterceptorsRelation method is not correct",sourceInterceptorsList.size(), 0);
            }
        }
    }


    @Test
    public void testGetOptimalSourcesPermutation() {

        String agent1_Name = "agent1";
        String agent2_Name = "agent2";
        String agent1_shared_channel_source1 = "source1";
        String agent1_shared_channel_source2 = "source2";
        String agent2_shared_channel_source4 = "source4";
        String agent2_shared_channel_source5 = "source5";

        IGraph agent1_graph = flumeGraphTopology.get(agent1_Name);
        IGraph agent2_graph = flumeGraphTopology.get(agent2_Name);

        Map<String, List<String>> mapSharedChannelsSourcesRelation_Agent1 = FlumeConfiguratorTopologyUtils.getMapSharedChannelsSourcesRelation(listSharedSourcesGraphAgent1, sourcesChannelsRelationsMap, agent1_Name);
        Map<String, List<String>> mapSharedChannelsSourcesRelation_Agent2 = FlumeConfiguratorTopologyUtils.getMapSharedChannelsSourcesRelation(listSharedSourcesGraphAgent2, sourcesChannelsRelationsMap, agent2_Name);

        List<String> completeSharedSourcesList_Agent1 = FlumeConfiguratorTopologyUtils.getCompleteSharedSourcesList(mapSharedChannelsSourcesRelation_Agent1);
        List<String> completeSharedSourcesList_Agent2 = FlumeConfiguratorTopologyUtils.getCompleteSharedSourcesList(mapSharedChannelsSourcesRelation_Agent2);

        Collection<List<String>> correctSourcesPermutations_Agent1 = FlumeConfiguratorTopologyUtils.getCorrectSourcesPermutations (completeSharedSourcesList_Agent1, mapSharedChannelsSourcesRelation_Agent1);
        Collection<List<String>> correctSourcesPermutations_Agent2 = FlumeConfiguratorTopologyUtils.getCorrectSourcesPermutations (completeSharedSourcesList_Agent2, mapSharedChannelsSourcesRelation_Agent2);

        Map<String,Integer> mapChannelSourcesNumberRelation_Agent1 = FlumeConfiguratorTopologyUtils.getMapChannelSourcesNumberRelation(completeSharedSourcesList_Agent1, agent1_graph, flumeTopologyListGraph);
        Map<String,Integer> mapChannelSourcesNumberRelation_Agent2 = FlumeConfiguratorTopologyUtils.getMapChannelSourcesNumberRelation(completeSharedSourcesList_Agent2, agent2_graph, flumeTopologyListGraph);

        Map<String,List<String>> mapSourcesIndependentChannelsRelation_Agent1 = FlumeConfiguratorTopologyUtils.getMapSourcesIndependentChannelsRelation(completeSharedSourcesList_Agent1, agent1_graph, flumeTopologyListGraph, mapChannelSourcesNumberRelation_Agent1);
        Map<String,List<String>> mapSourcesIndependentChannelsRelation_Agent2 = FlumeConfiguratorTopologyUtils.getMapSourcesIndependentChannelsRelation(completeSharedSourcesList_Agent2, agent2_graph, flumeTopologyListGraph, mapChannelSourcesNumberRelation_Agent2);

        Map<String,List<String>> mapSourcesInterceptorsRelation_Agent1 = FlumeConfiguratorTopologyUtils.getMapSourcesInterceptorsRelation(agent1_graph);
        Map<String,List<String>> mapSourcesInterceptorsRelation_Agent2 = FlumeConfiguratorTopologyUtils.getMapSourcesInterceptorsRelation(agent2_graph);

        List<String> optimalSourcesPermutation = FlumeConfiguratorTopologyUtils.getOptimalSourcesPermutation(null, null, null);
        Assert.assertNull("The result of the getMapSourcesInterceptorsRelation method is not correct",optimalSourcesPermutation);

        optimalSourcesPermutation = FlumeConfiguratorTopologyUtils.getOptimalSourcesPermutation(null, mapSourcesIndependentChannelsRelation_Agent1, mapSourcesInterceptorsRelation_Agent1);
        Assert.assertNull("The result of the getMapSourcesInterceptorsRelation method is not correct",optimalSourcesPermutation);

        optimalSourcesPermutation = FlumeConfiguratorTopologyUtils.getOptimalSourcesPermutation(correctSourcesPermutations_Agent1, null, mapSourcesInterceptorsRelation_Agent1);
        Assert.assertNull("The result of the getMapSourcesInterceptorsRelation method is not correct",optimalSourcesPermutation);

        optimalSourcesPermutation = FlumeConfiguratorTopologyUtils.getOptimalSourcesPermutation(correctSourcesPermutations_Agent1, mapSourcesIndependentChannelsRelation_Agent1, null);
        Assert.assertNull("The result of the getMapSourcesInterceptorsRelation method is not correct",optimalSourcesPermutation);

        optimalSourcesPermutation = FlumeConfiguratorTopologyUtils.getOptimalSourcesPermutation(correctSourcesPermutations_Agent1, mapSourcesIndependentChannelsRelation_Agent1, mapSourcesInterceptorsRelation_Agent1);
        Assert.assertNotNull("The result of the getMapSourcesInterceptorsRelation method is not correct",optimalSourcesPermutation);
        Assert.assertEquals("The result of the getMapSourcesInterceptorsRelation method is not correct",optimalSourcesPermutation.size(), 2);
        Assert.assertEquals("The result of the getMapSourcesInterceptorsRelation method is not correct",optimalSourcesPermutation.get(1), agent1_shared_channel_source1);
        Assert.assertEquals("The result of the getMapSourcesInterceptorsRelation method is not correct",optimalSourcesPermutation.get(0), agent1_shared_channel_source2);

        optimalSourcesPermutation = FlumeConfiguratorTopologyUtils.getOptimalSourcesPermutation(correctSourcesPermutations_Agent2, mapSourcesIndependentChannelsRelation_Agent2, mapSourcesInterceptorsRelation_Agent2);
        Assert.assertNotNull("The result of the getMapSourcesInterceptorsRelation method is not correct",optimalSourcesPermutation);
        Assert.assertEquals("The result of the getMapSourcesInterceptorsRelation method is not correct",optimalSourcesPermutation.size(), 2);
        Assert.assertTrue("The result of the getMapSourcesInterceptorsRelation method is not correct",optimalSourcesPermutation.get(1).equals(agent2_shared_channel_source4) || optimalSourcesPermutation.get(1).equals(agent2_shared_channel_source5));
        Assert.assertTrue("The result of the getMapSourcesInterceptorsRelation method is not correct",optimalSourcesPermutation.get(0).equals(agent2_shared_channel_source4) || optimalSourcesPermutation.get(0).equals(agent2_shared_channel_source5));

    }

    @Test
    public void testExistSinkGroupSlice() {

        String agent1_Name = "agent1";
        String agent2_Name = "agent2";

        boolean existSinkGroupSlice_Agent1 = FlumeConfiguratorTopologyUtils.existSinkGroupSlice(flumeGraphTopology, agent1_Name);
        boolean existSinkGroupSlice_Agent2 = FlumeConfiguratorTopologyUtils.existSinkGroupSlice(flumeGraphTopology, agent2_Name);

        Assert.assertTrue("The result of the existSinkGroupSlice method is not correct",existSinkGroupSlice_Agent1);
        Assert.assertTrue("The result of the existSinkGroupSlice method is not correct",existSinkGroupSlice_Agent2);

    }


    @Test
    public void testGetListFlumeTopologyByType() {

        String agent1_Name = "agent1";

        List<FlumeTopology> listFlumeTopologyByType = FlumeConfiguratorTopologyUtils.getListFlumeTopologyByType(null, null, null);
        Assert.assertNotNull("The result of the getListFlumeTopologyByType method is not correct",listFlumeTopologyByType);
        Assert.assertEquals("The result of the getListFlumeTopologyByType method is not correct",listFlumeTopologyByType.size(), 0);

        listFlumeTopologyByType = FlumeConfiguratorTopologyUtils.getListFlumeTopologyByType(null, agent1_Name, FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT);
        Assert.assertNotNull("The result of the getListFlumeTopologyByType method is not correct",listFlumeTopologyByType);
        Assert.assertEquals("The result of the getListFlumeTopologyByType method is not correct",listFlumeTopologyByType.size(), 0);

        listFlumeTopologyByType = FlumeConfiguratorTopologyUtils.getListFlumeTopologyByType(flumeGraphTopology, null, FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT);
        Assert.assertNotNull("The result of the getListFlumeTopologyByType method is not correct",listFlumeTopologyByType);
        Assert.assertEquals("The result of the getListFlumeTopologyByType method is not correct",listFlumeTopologyByType.size(), 0);

        listFlumeTopologyByType = FlumeConfiguratorTopologyUtils.getListFlumeTopologyByType(flumeGraphTopology, agent1_Name, null);
        Assert.assertNotNull("The result of the getListFlumeTopologyByType method is not correct",listFlumeTopologyByType);
        Assert.assertEquals("The result of the getListFlumeTopologyByType method is not correct",listFlumeTopologyByType.size(), 0);

        //AGENT1
        String elementType = FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT;
        listFlumeTopologyByType = FlumeConfiguratorTopologyUtils.getListFlumeTopologyByType(flumeGraphTopology, agent1_Name, elementType);
        Assert.assertNotNull("The result of the getListFlumeTopologyByType method is not correct",listFlumeTopologyByType);
        Assert.assertTrue("The result of the getListFlumeTopologyByType method is not correct",listFlumeTopologyByType.size() > 0);
        for (FlumeTopology flumeTopologyElement : listFlumeTopologyByType) {
            Assert.assertEquals("The result of the getListFlumeTopologyByType method is not correct",flumeTopologyElement.getType(), elementType);
        }

        elementType = FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE;
        listFlumeTopologyByType = FlumeConfiguratorTopologyUtils.getListFlumeTopologyByType(flumeGraphTopology, agent1_Name, elementType);
        Assert.assertNotNull("The result of the getListFlumeTopologyByType method is not correct",listFlumeTopologyByType);
        Assert.assertTrue("The result of the getListFlumeTopologyByType method is not correct",listFlumeTopologyByType.size() > 0);
        for (FlumeTopology flumeTopologyElement : listFlumeTopologyByType) {
            Assert.assertEquals("The result of the getListFlumeTopologyByType method is not correct",flumeTopologyElement.getType(), elementType);
        }

        elementType = FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR;
        listFlumeTopologyByType = FlumeConfiguratorTopologyUtils.getListFlumeTopologyByType(flumeGraphTopology, agent1_Name, elementType);
        Assert.assertNotNull("The result of the getListFlumeTopologyByType method is not correct",listFlumeTopologyByType);
        Assert.assertTrue("The result of the getListFlumeTopologyByType method is not correct",listFlumeTopologyByType.size() > 0);
        for (FlumeTopology flumeTopologyElement : listFlumeTopologyByType) {
            Assert.assertEquals("The result of the getListFlumeTopologyByType method is not correct",flumeTopologyElement.getType(), elementType);
        }

        elementType = FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL;
        listFlumeTopologyByType = FlumeConfiguratorTopologyUtils.getListFlumeTopologyByType(flumeGraphTopology, agent1_Name, elementType);
        Assert.assertNotNull("The result of the getListFlumeTopologyByType method is not correct",listFlumeTopologyByType);
        Assert.assertTrue("The result of the getListFlumeTopologyByType method is not correct",listFlumeTopologyByType.size() > 0);
        for (FlumeTopology flumeTopologyElement : listFlumeTopologyByType) {
            Assert.assertEquals("The result of the getListFlumeTopologyByType method is not correct",flumeTopologyElement.getType(), elementType);
        }

        elementType = FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK;
        listFlumeTopologyByType = FlumeConfiguratorTopologyUtils.getListFlumeTopologyByType(flumeGraphTopology, agent1_Name, elementType);
        Assert.assertNotNull("The result of the getListFlumeTopologyByType method is not correct",listFlumeTopologyByType);
        Assert.assertTrue("The result of the getListFlumeTopologyByType method is not correct",listFlumeTopologyByType.size() > 0);
        for (FlumeTopology flumeTopologyElement : listFlumeTopologyByType) {
            Assert.assertEquals("The result of the getListFlumeTopologyByType method is not correct",flumeTopologyElement.getType(), elementType);
        }

        elementType = FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP;
        listFlumeTopologyByType = FlumeConfiguratorTopologyUtils.getListFlumeTopologyByType(flumeGraphTopology, agent1_Name, elementType);
        Assert.assertNotNull("The result of the getListFlumeTopologyByType method is not correct",listFlumeTopologyByType);
        Assert.assertTrue("The result of the getListFlumeTopologyByType method is not correct",listFlumeTopologyByType.size() > 0);
        for (FlumeTopology flumeTopologyElement : listFlumeTopologyByType) {
            Assert.assertEquals("The result of the getListFlumeTopologyByType method is not correct",flumeTopologyElement.getType(), elementType);
        }
    }


    @Test
    public void testGetFirstAncestorByType() {

        try {
            String elementType = FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL;
            DefaultMutableTreeNode ancestorNode = FlumeConfiguratorTopologyUtils.getFirstAncestorByType(flumeTopologyNodeE, elementType);
            Assert.assertNotNull("The result of the getFirstAncestorByType method is not correct",ancestorNode);
            FlumeTopology flumeTopologyAncestorElement = (FlumeTopology) ancestorNode.getUserObject();
            Assert.assertEquals("The result of the getFirstAncestorByType method is not correct", flumeTopologyAncestorElement.getType(), elementType);

            elementType = FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE;
            ancestorNode = FlumeConfiguratorTopologyUtils.getFirstAncestorByType(flumeTopologyNodeE, elementType);
            Assert.assertNotNull("The result of the getFirstAncestorByType method is not correct",ancestorNode);
            flumeTopologyAncestorElement = (FlumeTopology) ancestorNode.getUserObject();
            Assert.assertEquals("The result of the getFirstAncestorByType method is not correct", flumeTopologyAncestorElement.getType(), elementType);

            elementType = FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT;
            ancestorNode = FlumeConfiguratorTopologyUtils.getFirstAncestorByType(flumeTopologyNodeE, elementType);
            Assert.assertNotNull("The result of the getFirstAncestorByType method is not correct",ancestorNode);
            flumeTopologyAncestorElement = (FlumeTopology) ancestorNode.getUserObject();
            Assert.assertEquals("The result of the getFirstAncestorByType method is not correct", flumeTopologyAncestorElement.getType(), elementType);

            elementType = FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK; //Non exist ancestor of this type
            ancestorNode = FlumeConfiguratorTopologyUtils.getFirstAncestorByType(flumeTopologyNodeE, elementType);
            Assert.assertNull("The result of the getFirstAncestorByType method is not correct",ancestorNode);

            elementType = FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE;
            ancestorNode = FlumeConfiguratorTopologyUtils.getFirstAncestorByType(null, elementType);
            Assert.assertNull("The result of the getFirstAncestorByType method is not correct",ancestorNode);

        } catch (Exception e) {
            Assert.fail("An error has occurred [testGetFirstAncestorByType] method");
            logger.error("An error has occurred [testGetFirstAncestorByType] method", e);
        }
    }
}
