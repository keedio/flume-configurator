package org.keedio.flume.configurator.utils;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.keedio.flume.configurator.constants.FlumeConfiguratorConstants;
import org.keedio.flume.configurator.structures.FlumeTopology;
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

    private static DefaultMutableTreeNode flumeTopologyTreeRootNode;

    @BeforeClass
    public static void loadPropertiesFile() throws IOException {
        String  pathJSONTopologyTree = "src/test/resources/FlumeTopologyWithComments_with2Agent.json";
        flumeJSONTopologyTree = Files.readAllBytes(Paths.get(pathJSONTopologyTree));
        flumeTopologyListTree =  Arrays.asList(JSONStringSerializer.fromBytes(flumeJSONTopologyTree, FlumeTopology[].class));

        String  pathJSONTopologyGraph = "src/test/resources/FlumeTopologyGraphWithComments_with2Agent.json";
        flumeJSONTopologyGraph = Files.readAllBytes(Paths.get(pathJSONTopologyGraph));
        flumeTopologyListGraph =  Arrays.asList(JSONStringSerializer.fromBytes(flumeJSONTopologyGraph, FlumeTopology[].class));

        //Create tree
        FlumeTopology flumeTopologyA = new FlumeTopology();
        flumeTopologyA.setId("A");
        FlumeTopology flumeTopologyB = new FlumeTopology();
        flumeTopologyB.setId("B");
        FlumeTopology flumeTopologyC = new FlumeTopology();
        flumeTopologyC.setId("C");
        FlumeTopology flumeTopologyD = new FlumeTopology();
        flumeTopologyD.setId("D");

        flumeTopologyTreeRootNode = new DefaultMutableTreeNode(flumeTopologyA);
        DefaultMutableTreeNode flumeTopologyNodeB = new DefaultMutableTreeNode(flumeTopologyB);
        DefaultMutableTreeNode flumeTopologyNodeC = new DefaultMutableTreeNode(flumeTopologyC);
        DefaultMutableTreeNode flumeTopologyNodeD = new DefaultMutableTreeNode(flumeTopologyD);

        flumeTopologyTreeRootNode.add(flumeTopologyNodeB);
        flumeTopologyTreeRootNode.add(flumeTopologyNodeC);
        flumeTopologyNodeB.add(flumeTopologyNodeD);
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


}
