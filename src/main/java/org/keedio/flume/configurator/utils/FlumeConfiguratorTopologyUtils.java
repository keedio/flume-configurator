package org.keedio.flume.configurator.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.keedio.flume.configurator.builder.FlumeTopologyReversePropertiesGenerator;
import org.keedio.flume.configurator.constants.FlumeConfiguratorConstants;
import org.keedio.flume.configurator.exceptions.FlumeConfiguratorException;
import org.keedio.flume.configurator.structures.FlumeTopology;
import org.keedio.flume.configurator.structures.LinkedProperties;
import org.keedio.flume.configurator.structures.TopologyPropertyBean;
import org.keedio.flume.configurator.topology.GraphFactory;
import org.keedio.flume.configurator.topology.IGraph;
import org.slf4j.LoggerFactory;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public class FlumeConfiguratorTopologyUtils {

    private FlumeConfiguratorTopologyUtils() {
        super();
    }

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FlumeTopologyReversePropertiesGenerator.class);

    /**
     * Detect if the structure is a tree
     * @param withAgentNodes true if exists agent nodes
     * @param listFlumeTopology List of FlumeTopology
     * @param nodesNumber number of nodes
     * @param sourcesNumber number of sources
     * @param agentsNumber number of agents
     * @return true if the structure is a tree, false otherwise
     */
    public static boolean isTreeCompliant(boolean withAgentNodes, List<FlumeTopology> listFlumeTopology, int nodesNumber, int sourcesNumber, int agentsNumber) {

        Map<String, FlumeTopology> targetConnectionMap = new HashMap<>();

        int connectionsNumber = listFlumeTopology.size(); // arches number

        if (withAgentNodes) {
            if (connectionsNumber != (nodesNumber - agentsNumber)) {
                return false;
            }
        } else {
            if (connectionsNumber != (nodesNumber - sourcesNumber)) {
                return false;
            }
        }

        if (listFlumeTopology.size() > 0) {

            for (FlumeTopology flumeTopology : listFlumeTopology) {
                String targetConnection = flumeTopology.getTargetConnection();

                FlumeTopology elementConnection = targetConnectionMap.get(targetConnection);
                if (elementConnection != null) {
                    //There is another element with the same target (a target has more than one source, so is not a tree)
                    return false;
                } else {
                    targetConnectionMap.put(targetConnection, null);
                }
            }
        }

        return true;
    }


    /**
     * Get the node of the tree with the indicated id
     * @param id id of the searched node
     * @param node ancestor node of the searched node
     * @return the node of the tree with the indicated id
     */
    static DefaultMutableTreeNode searchTreeNode(String id, DefaultMutableTreeNode node){
        FlumeTopology flumeTopologyNode = (FlumeTopology) node.getUserObject();
        if (flumeTopologyNode.getId().equals(id)) {
            return node;
        }

        Enumeration children = node.children();
        DefaultMutableTreeNode resNode = null;

        while (children.hasMoreElements() && (resNode==null)) {
            resNode = searchTreeNode (id, (DefaultMutableTreeNode) children.nextElement());
        }

        return resNode;
    }


    /**
     * Get the node of several trees with the indicated id
     * @param id id of the searched node
     * @param rootNodesSet root nodes of the trees
     * @return the first node with the indicated id
     */
    private static DefaultMutableTreeNode searchTreeNode(String id, Set<DefaultMutableTreeNode> rootNodesSet) {

        DefaultMutableTreeNode node = null;

        if (rootNodesSet != null) {
            Iterator<DefaultMutableTreeNode> itRootNodes = rootNodesSet.iterator();
            while ((itRootNodes.hasNext() && (node == null))) {
                DefaultMutableTreeNode rootNode = itRootNodes.next();
                node = searchTreeNode(id, rootNode);
            }
        }

        return node;

    }


    /**
     * Get the node with the indicated id from a list of nodes
     * @param id id of the searched node
     * @param nodesList list of nodes
     * @return the node with de indicated id
     */
    public static DefaultMutableTreeNode searchNode(String id, List<DefaultMutableTreeNode> nodesList) {

        DefaultMutableTreeNode resNode = null;

        if ((nodesList != null) && (nodesList.size()>0)) {
            for (DefaultMutableTreeNode node : nodesList) {
                FlumeTopology flumeTopologyNode = (FlumeTopology) node.getUserObject();
                if (flumeTopologyNode.getId().equals(id)) {
                    resNode = node;
                }
            }
        }

        return resNode;

    }



    /**
     * Render a tree topology
     * @param rootNodesCollection collection of root nodes of the trees
     * @return String with the renderized topology
     */
    public static String renderFlumeTopology(Collection<DefaultMutableTreeNode> rootNodesCollection) {

        StringBuilder sb = new StringBuilder();
        String newline = System.getProperty("line.separator");

        sb.append(newline);
        sb.append(newline);
        if (rootNodesCollection != null) {
            Iterator<DefaultMutableTreeNode> itRootNodes = rootNodesCollection.iterator();
            while (itRootNodes.hasNext()) {
                DefaultMutableTreeNode rootNode = itRootNodes.next();

                sb.append("Agent").append(newline);
                sb.append("-----").append(newline).append(newline);
                sb.append(renderFlumeTopologyTree(rootNode));
                sb.append(newline);
                sb.append(newline);
                sb.append(newline);
            }
        }

        return sb.toString();
    }

    /**
     * Render a graph topology
     * @param graphsCollection collection of graphs
     * @param singleGraph true if only one graph description is made, false for multiple graphs description
     * @return description of the graph in dot format
     */
    public static String renderFlumeTopologyGraph(Collection<IGraph> graphsCollection, boolean singleGraph) {

        StringBuilder sb = new StringBuilder();
        String newline = System.getProperty("line.separator");

        sb.append(newline);
        sb.append(newline);
        if (graphsCollection != null) {

            if (singleGraph) {
                boolean firstGraph = true;
                Iterator<IGraph> itGraphs = graphsCollection.iterator();
                while (itGraphs.hasNext()) {
                    IGraph iGraph = itGraphs.next();
                    if (firstGraph) {
                        sb.append(iGraph.exportToDot().replace("}",""));
                        sb.setLength(sb.length() - 1);
                        firstGraph = false;
                    } else {
                        sb.append(iGraph.exportToDot().replace("strict ","").replace("digraph G {", "").replace("}",""));
                        sb.setLength(sb.length() - 1);
                    }
                }
                sb.append("}");
            } else {
                Iterator<IGraph> itGraphs = graphsCollection.iterator();
                while (itGraphs.hasNext()) {
                    IGraph iGraph = itGraphs.next();

                    sb.append("Agent").append(newline);
                    sb.append("-----").append(newline).append(newline);
                    sb.append(iGraph.exportToDot());
                    sb.append(newline);
                    sb.append(newline);
                    sb.append(newline);
                }
            }

        }

        return sb.toString();
    }


    /**
     * Render a tree
     * @param tree Root node of the tree
     * @return String with the renderized tree
     */
    private static String renderFlumeTopologyTree(DefaultMutableTreeNode tree) {
        List<StringBuilder> lines = renderFlumeTopologyTreeLines(tree);
        String newline = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder(lines.size() * 20);
        for (StringBuilder line : lines) {
            sb.append(line);
            sb.append(newline);
        }
        return sb.toString();
    }


    /**
     * Create the representation of the tree
     * @param tree Root node of the tree
     * @return String with de representation of the tree
     */
    private static List<StringBuilder> renderFlumeTopologyTreeLines(DefaultMutableTreeNode tree) {
        List<StringBuilder> result = new LinkedList<>();
        FlumeTopology flumeTopology = (FlumeTopology) tree.getUserObject();
        String elementId = flumeTopology.getId();
        String elementName = flumeTopology.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);
        String elementType = flumeTopology.getType();
        result.add(new StringBuilder().append(elementType).append(" ").append(elementName).append(" (").append(elementId).append(")"));

        Enumeration<DefaultMutableTreeNode> e = tree.children();

        while (e.hasMoreElements()) {
            List<StringBuilder> subtree = renderFlumeTopologyTreeLines(e.nextElement());
            if (e.hasMoreElements()) {
                addSubtree(result,subtree);
            } else {
                addLastSubtree(result, subtree);
            }

        }

        return result;
    }

    /**
     * Add a string representation of the subtree
     * @param result List with the representation of the tree
     * @param subtree List with the representation of the subtree
     */
    private static void addSubtree(List<StringBuilder> result, List<StringBuilder> subtree) {
        Iterator<StringBuilder> iterator = subtree.iterator();
        //subtree generated by renderDirectoryTreeLines has at least one line which is tree.getData()
        result.add(iterator.next().insert(0, "├── "));
        while (iterator.hasNext()) {
            result.add(iterator.next().insert(0, "│   "));
        }
    }

    /**
     * Add a string representation of the last subtree
     * @param result List with the representation of the tree
     * @param subtree  List with the representation of the last subtree
     */
    private static void addLastSubtree(List<StringBuilder> result, List<StringBuilder> subtree) {
        Iterator<StringBuilder> iterator = subtree.iterator();
        //subtree generated by renderDirectoryTreeLines has at least one line which is tree.getData()
        result.add(iterator.next().insert(0, "└── "));
        while (iterator.hasNext()) {
            result.add(iterator.next().insert(0, "    "));
        }
    }


    /**
     * Get the property from parts of the property
     * @param propertyNameParts Strings with the parts of the name of property
     * @return property name
     */
    public static String getKeyPropertyString(String... propertyNameParts) {

        StringBuilder sb = new StringBuilder(100);

        for (String propertyNamePart : propertyNameParts) {
            sb.append(propertyNamePart);
        }

        return sb.toString();
    }


    /**
     * Add a topology property
     * @param properties the properties file
     * @param propertyName the property name
     * @param propertyValues the values of the property
     * @param separatorCharacter the separator character
     * @return properties with the added property
     */
    public static Properties addTopologyProperty(Properties properties, String propertyName, List<String> propertyValues, String separatorCharacter) {

        if ((properties != null) && (propertyName != null) && (!"".equals(propertyName)) && (propertyValues != null) && (propertyValues.size() > 0)) {

            StringBuilder sb = new StringBuilder();

            for (String propertyValue : propertyValues) {
                sb.append(propertyValue);
                sb.append(separatorCharacter);
            }

            //Remove last separator character
            sb.setLength(sb.length()-1);

            properties.setProperty(propertyName, sb.toString());
        }

        return properties;

    }

    /**
     *  Add a topology property
     * @param properties he properties file
     * @param propertyName the property name
     * @param propertyValue the value of the property
     * @param isCommentProperty true if is a comment property, false otherwise
     * @return properties with the added property
     */
    public static Properties addTopologyProperty(Properties properties, String propertyName, String propertyValue, boolean isCommentProperty) {

        if ((properties != null) && (propertyName != null) && (!"".equals(propertyName))) {

            if (!isCommentProperty) {
                if (propertyValue != null && !"".equals(propertyValue)) {
                    properties.setProperty(propertyName, propertyValue);
                }
            } else {
                if (propertyValue == null) {
                    propertyValue = "";
                }
                properties.setProperty(propertyName, propertyValue);
            }
        }

        return properties;

    }


    /**
     * Get the string with the properties values
     * @param properties the Properties
     * @return String with the properties values
     */
    public static String getPropertyAsString(Properties properties) {
        StringWriter writer = new StringWriter();
        properties.list(new PrintWriter(writer));
        return writer.getBuffer().toString();
    }



    /**
     * Get Base configuration properties as string
     * @param printWriter PrintWriter
     * @param headerText the string with the header text
     * @param properties properties
     * @param prefixProperties String with pre prefix properties
     */

    private static void getPartialFlumePropertiesAsString(PrintWriter printWriter, String headerText, Properties properties, String prefixProperties) {

        String newline = System.getProperty("line.separator");

        printWriter.write(FlumeConfiguratorUtils.getHeaderAgentConfiguration(new StringBuilder(headerText), 1, 0, FlumeConfiguratorConstants.HASH));
        LinkedProperties elementsProperties = FlumeConfiguratorUtils.matchingSubset(properties, prefixProperties, true);

        for (Object keyProperty  : elementsProperties.keySet()) {
            String keyPropertyStr = (String) keyProperty;
            String valueProperty = elementsProperties.getProperty(keyPropertyStr);
            printWriter.write(keyPropertyStr);
            printWriter.write("=");

            if(valueProperty.length() > 50) {
                if (valueProperty.contains(FlumeConfiguratorConstants.PROPERTY_SEPARATOR_DEFAULT)) {
                    String[] valuePropertyArray = valueProperty.split(FlumeConfiguratorConstants.PROPERTY_SEPARATOR_DEFAULT);
                    boolean isFirst = true;
                    for (int i=0; i<valuePropertyArray.length; i++) {
                        String elementValuePropertyArray = valuePropertyArray[i];
                        if (!isFirst) {
                            printWriter.write(newline);
                            int tabNumber = ((keyPropertyStr).length()  + 3) / 4;
                            for (int j=0; j<tabNumber;j++) {
                                printWriter.write(FlumeConfiguratorConstants.TABULATOR);
                            }
                        }
                        isFirst=false;
                        printWriter.write(elementValuePropertyArray);
                        printWriter.write(FlumeConfiguratorConstants.PROPERTY_SEPARATOR_DEFAULT);
                        if (i<valuePropertyArray.length-1) {
                            printWriter.write("\\");
                        }
                    }

                } else {
                    printWriter.write(valueProperty);
                }

            } else {
                printWriter.write(valueProperty);
            }

            printWriter.write(newline);

            if (keyPropertyStr.contains(FlumeConfiguratorConstants.PARTIAL_PROPERTY_PROPERTY_VALUES_PROPERTIES_PREFIX)) {
                printWriter.write(newline);
            }

            boolean commonPropertyNotComment = ((keyPropertyStr.contains(FlumeConfiguratorConstants.SOURCES_COMMON_PROPERTY_PROPERTIES_PREFIX))
                    || (keyPropertyStr.contains(FlumeConfiguratorConstants.SELECTORS_COMMON_PROPERTY_PROPERTIES_PREFIX))
                    || (keyPropertyStr.contains(FlumeConfiguratorConstants.INTERCEPTORS_COMMON_PROPERTY_PROPERTIES_PREFIX))
                    || (keyPropertyStr.contains(FlumeConfiguratorConstants.CHANNELS_COMMON_PROPERTY_PROPERTIES_PREFIX))
                    || (keyPropertyStr.contains(FlumeConfiguratorConstants.SINKS_COMMON_PROPERTY_PROPERTIES_PREFIX))
                    || (keyPropertyStr.contains(FlumeConfiguratorConstants.SINKGROUPS_COMMON_PROPERTY_PROPERTIES_PREFIX)))
                    && (!keyPropertyStr.contains(FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX));

            if (commonPropertyNotComment) {
                printWriter.write(newline);
            }

        }
        printWriter.write(newline);
        printWriter.write(newline);

    }


    /**
     * Get the Base Configuration properties string representation
     * @param properties the properties
     * @return String with the properties representation
     */
    public static String getFlumePropertiesAsString(Properties properties) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);

        //Timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss zzz");
        printWriter.write(FlumeConfiguratorUtils.getHeaderAgentConfiguration(new StringBuilder("Built by Flume Configurator on:  " + sdf.format(Calendar.getInstance().getTime())), 2, 2, FlumeConfiguratorConstants.HASH));

        //Agent list
        getPartialFlumePropertiesAsString(printWriter, "Agents List", properties, FlumeConfiguratorConstants.AGENTS_LIST_PROPERTIES_PREFIX);

        //Sources list
        getPartialFlumePropertiesAsString(printWriter, "Sources per agent list", properties, FlumeConfiguratorConstants.SOURCES_LIST_PROPERTIES_PREFIX);

        //Channels list
        getPartialFlumePropertiesAsString(printWriter, "Channels per agent list", properties, FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX);

        //Sinks list
        getPartialFlumePropertiesAsString(printWriter, "Sinks per agent list", properties, FlumeConfiguratorConstants.SINKS_LIST_PROPERTIES_PREFIX);

        //Sink groups list
        getPartialFlumePropertiesAsString(printWriter, "Sink groups per agent list", properties, FlumeConfiguratorConstants.SINKGROUPS_LIST_PROPERTIES_PREFIX);

        //Groups list
        getPartialFlumePropertiesAsString(printWriter, "Groups list", properties, FlumeConfiguratorConstants.GROUPS_LIST_PROPERTIES_PREFIX);

        //Sources common properties
        getPartialFlumePropertiesAsString(printWriter, "Sources common properties list (Common to all sources from all agents)", properties,
                FlumeConfiguratorConstants.SOURCES_COMMON_PROPERTY_PROPERTIES_PREFIX);

        //Sources partial properties
        getPartialFlumePropertiesAsString(printWriter, "Sources partial properties list", properties,
                FlumeConfiguratorConstants.SOURCES_PARTIAL_PROPERTY_PROPERTIES_PREFIX);

        //SourcesWithSelector list
        getPartialFlumePropertiesAsString(printWriter, "Sources with selector list", properties,FlumeConfiguratorConstants.SELECTORS_LIST_PROPERTIES_PREFIX);

        //Selectors common properties
        getPartialFlumePropertiesAsString(printWriter, "Selectors common properties list (Common to all selectors from sources with selector)", properties,
                FlumeConfiguratorConstants.SELECTORS_COMMON_PROPERTY_PROPERTIES_PREFIX);

        //Selectors partial properties
        getPartialFlumePropertiesAsString(printWriter, "Selectors partial properties list", properties,
                FlumeConfiguratorConstants.SELECTORS_PARTIAL_PROPERTY_PROPERTIES_PREFIX);

        //Interceptors list
        getPartialFlumePropertiesAsString(printWriter, "Interceptors per source list", properties,FlumeConfiguratorConstants.INTERCEPTORS_LIST_PROPERTIES_PREFIX);

        //Interceptors common properties
        getPartialFlumePropertiesAsString(printWriter, "Interceptors common properties list (Common to all interceptors from all agents)", properties,
                FlumeConfiguratorConstants.INTERCEPTORS_COMMON_PROPERTY_PROPERTIES_PREFIX);

        //Interceptors partial properties
        getPartialFlumePropertiesAsString(printWriter, "Interceptors partial properties list", properties,
                FlumeConfiguratorConstants.INTERCEPTORS_PARTIAL_PROPERTY_PROPERTIES_PREFIX);

        //Channels common properties
        getPartialFlumePropertiesAsString(printWriter, "Channels common properties list (Common to all channels from all agents)", properties,
                FlumeConfiguratorConstants.CHANNELS_COMMON_PROPERTY_PROPERTIES_PREFIX);

        //Channels partial properties
        getPartialFlumePropertiesAsString(printWriter, "Channels partial properties list", properties,
                FlumeConfiguratorConstants.CHANNELS_PARTIAL_PROPERTY_PROPERTIES_PREFIX);

        //Sinks common properties
        getPartialFlumePropertiesAsString(printWriter, "Sinks common properties list (Common to all sinks from all agents)", properties,
                FlumeConfiguratorConstants.SINKS_COMMON_PROPERTY_PROPERTIES_PREFIX);

        //Sinks partial properties
        getPartialFlumePropertiesAsString(printWriter, "Sinks partial properties list", properties,
                FlumeConfiguratorConstants.SINKS_PARTIAL_PROPERTY_PROPERTIES_PREFIX);

        //Sinkgroups common properties
        getPartialFlumePropertiesAsString(printWriter, "Sinkgroups common common properties list (Common to all sinkgroups from all agents)", properties,
                FlumeConfiguratorConstants.SINKGROUPS_COMMON_PROPERTY_PROPERTIES_PREFIX);

        //Sinkgroups partial properties
        getPartialFlumePropertiesAsString(printWriter, "Sinkgroups partial properties list", properties,
                FlumeConfiguratorConstants.SINKGROUPS_PARTIAL_PROPERTY_PROPERTIES_PREFIX);

        return writer.getBuffer().toString();

    }


    /**
     * Determine if the property is a special property
     * @param propertyName Name of the property
     * @return true if is a special property, false otherwise
     */
    static boolean isSpecialProperty(String propertyName) {

        boolean isSpecialProperty = false;

        String agentNameCommentProperty = FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_AGENT_NAME + FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_COMMENT_SUFIX;
        String elementTopologyNameCommentProperty = FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME + FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_COMMENT_SUFIX;

        if ((propertyName.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_AGENT_NAME)) || (propertyName.equals(agentNameCommentProperty))
                || (propertyName.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME)) || (propertyName.equals(elementTopologyNameCommentProperty))) {
            isSpecialProperty = true;
        }

        return isSpecialProperty;
    }


    /**
     * Determine if the property is a comment property
     * @param property Name of the property
     * @return true if is a comment property, false otherwise
     */
    public static boolean isCommentProperty(String property) {

        boolean isCommentProperty = false;

        if (property.endsWith(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_COMMENT_SUFIX)) {
            isCommentProperty = true;
        }

        return isCommentProperty;
    }


    /**
     * Get the comment property name associated to a property
     * @param property Name of the property
     * @return the name of the comment property associated.
     */
    public static String getCommentPropertyName(String property) {

        String commentPropertyName;

        if (isCommentProperty(property)) {
            commentPropertyName = property;
        } else {
            commentPropertyName = property + FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_COMMENT_SUFIX;
        }

        return commentPropertyName;
    }


    /**
     * Get valid real properties (not special or comment properties) from properties
     * @param originalTopologyProperties Map with the properties
     * @return map with the real valid properties
     */
    public static Map<String, String> getValidTopologyProperties(Map<String, String> originalTopologyProperties) {

        LinkedHashMap<String, String> topologyProperties = new LinkedHashMap<>(originalTopologyProperties);

        for (String propertyKey : originalTopologyProperties.keySet()) {

            if (isSpecialProperty(propertyKey)) {
                topologyProperties.remove(propertyKey);
            } else {
                String propertyValue = topologyProperties.get(propertyKey);

                if ((propertyValue == null) || ("".equals(propertyValue))) {

                    topologyProperties.remove(propertyKey);
                    if (!isCommentProperty(propertyKey)) {
                        //Remove comment property
                        String commentPropertyKey = getCommentPropertyName(propertyKey);
                        topologyProperties.remove(commentPropertyKey);
                    }

                }

            }
        }

        return topologyProperties;
    }

    /**
     * Add a property bean
     * @param propertiesMap Map with the properties
     * @param propertyKey the property key
     * @param propertyComment the property comment
     * @param appliedElement the applied elements for the property
     * @param propertyValue the property value
     * @return the map of properties with the added property bean
     */
    public static Map<String, List<TopologyPropertyBean>> addPropertyBean(Map<String, List<TopologyPropertyBean>> propertiesMap, String propertyKey,
                                                                          String propertyComment, String appliedElement, String propertyValue) {

        if (propertiesMap != null) {

            if (propertiesMap.containsKey(propertyKey)) {

                List<TopologyPropertyBean> topologyPropertyBeanList = propertiesMap.get(propertyKey);

                TopologyPropertyBean topologyPropertyBean = new TopologyPropertyBean(propertyComment, appliedElement, propertyValue);
                topologyPropertyBeanList.add(topologyPropertyBean);

            } else {

                List<TopologyPropertyBean> topologyPropertyBeanList = new ArrayList<>();

                TopologyPropertyBean topologyPropertyBean = new TopologyPropertyBean(propertyComment, appliedElement, propertyValue);
                topologyPropertyBeanList.add(topologyPropertyBean);

                propertiesMap.put(propertyKey, topologyPropertyBeanList);

            }
        }

        return propertiesMap;

    }


    /**
     * Get the common property (if exist)
     * @param topologyPropertyBeanList List of topology property beans
     * @param totalElements number of total elements
     * @param commonRatio ratio indicating when a property can be taken as a common property
     * @return value of the common property
     */
    public static String getValueCommonProperty(List<TopologyPropertyBean> topologyPropertyBeanList, int totalElements, double commonRatio) {

        Map<String,Integer> valuePropertyFrequencyMap = new HashMap<>();

        String valueCommonProperty = null;

        if ((topologyPropertyBeanList != null) && (topologyPropertyBeanList.size() == totalElements)) {

            //Calculate frequency of each value of key
            for (TopologyPropertyBean topologyPropertyBean : topologyPropertyBeanList) {
                String propertyValue = topologyPropertyBean.getPropertyValue();
                if (valuePropertyFrequencyMap.containsKey(propertyValue)) {
                    valuePropertyFrequencyMap.put(propertyValue, valuePropertyFrequencyMap.get(propertyValue)+1);
                } else {
                    valuePropertyFrequencyMap.put(propertyValue,1);
                }
            }

            //Detect property with max frequency
            Map.Entry<String, Integer> maxEntry = null;

            for (Map.Entry<String, Integer> entry : valuePropertyFrequencyMap.entrySet()) {
                if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                    maxEntry = entry;
                }
            }

            //Determine a common property when the max frequency value divide number of elements with that property exceed a ratio
            if ((maxEntry != null) && (totalElements > 0) && ((double) maxEntry.getValue() / (double) totalElements >= commonRatio)) {
                //Is a common property
                valueCommonProperty = maxEntry.getKey();

            }

        }

        return valueCommonProperty;

    }


    /**
     * Get the string representation of a list
     * @param list list
     * @param separatorCharacter the separator character
     * @return String with the representation of the elements of the list separated with the indicated character
     */
    public static String listToString(List<String> list, String separatorCharacter) {

        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s);
            sb.append(separatorCharacter);
        }

        if (list.size() > 0) {
            sb.setLength(sb.length() - separatorCharacter.length());
        }

        return sb.toString();
    }


    /**
     * Add a string to another string
     * @param cadenaOriginal original string
     * @param appendText string to be added
     * @param separatorCharacter the separator character
     * @return the original string with the added string (separated by the separator character)
     */
    public static String appendString(String cadenaOriginal, String appendText, String separatorCharacter) {

        StringBuilder sb = new StringBuilder();
        if (cadenaOriginal != null) {
            sb.append(cadenaOriginal);
        }
        if ((appendText != null) && (!"".equals(appendText)) && (separatorCharacter != null) && (!"".equals(separatorCharacter))) {
            if (sb.length()>0) {
                sb.append(separatorCharacter).append(appendText);
            } else {
                sb.append(appendText);
            }

        }

        return sb.toString();
    }


    /**
     * Detect if exists a common property for a property
     * @param topologyPropertyBean TopologyPropertyBean
     * @param commonPropertiesMap Map with the common properties
     * @param propertyKey Name of the property
     * @return true if a common property with the same name already exists, false otherwise
     */
    public static boolean existsCommonProperty(TopologyPropertyBean topologyPropertyBean, Map<String, TopologyPropertyBean> commonPropertiesMap, String propertyKey) {

        boolean existsCommonProperty = false;

        if ((topologyPropertyBean != null) && (commonPropertiesMap != null) && (propertyKey != null)) {

           if (commonPropertiesMap.containsKey(propertyKey)) {
               TopologyPropertyBean commonProperty = commonPropertiesMap.get(propertyKey);

               if (commonProperty.getPropertyValue().equals(topologyPropertyBean.getPropertyValue())) {
                   existsCommonProperty = true;
               }
           }
        }

        return existsCommonProperty;

    }


    /**
     * Get the list with elements having the indicated ids
     * @param idElements the ids
     * @param listElements list of elements
     * @return a list of elements having the indicated ids
     */
    public static List<FlumeTopology> getSubList(List<String> idElements, List<FlumeTopology> listElements) {

        List<FlumeTopology> sublistElements = new ArrayList<>();

        for (FlumeTopology flumeTopology : listElements) {
            if (idElements.contains(flumeTopology.getId())) {
                sublistElements.add(flumeTopology);
            }
        }

        return sublistElements;
    }


    /**
     * Check if every node belong to a unique agend
     * @param listElements list of elements
     * @param listConnections list of connections
     * @return true if every element belongs to a unique agent, false otherwise
     */
    public static boolean checkOnlyOneAgentPerNode(List<FlumeTopology> listElements, List<FlumeTopology> listConnections) {


        for (FlumeTopology flumeTopology : listElements) {

            if (!flumeTopology.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION)) {

                List<String> listAgents = new ArrayList<>();

                //Get all ancestors of the node
                List<String> listAncestorsID = getGraphAncestorsFromConnections(flumeTopology.getId(), listConnections);
                List<FlumeTopology> listAncestors = FlumeConfiguratorTopologyUtils.getSubList(listAncestorsID, listElements);

                for (FlumeTopology ancestor : listAncestors) {
                    if (ancestor.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT)) {
                        String agentName = ancestor.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                        if ((agentName != null) && (!listAgents.contains(agentName))) {
                            listAgents.add(agentName);
                        }

                    } else if (ancestor.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE)) {
                        String agentName = ancestor.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_AGENT_NAME);

                        if ((agentName != null) && (!listAgents.contains(agentName))) {
                            listAgents.add(agentName);
                        }

                    }
                }

                if (listAgents.size() > 1) {
                    return false;
                }

            }
        }
        return true;
    }


    /**
     * Get the graph ancestors of a node with a indicated id
     * @param idNode id of the node
     * @param listConnections list of connections
     * @return list of ids of ancestors of the indicated node
     */
    private static List<String> getGraphAncestorsFromConnections(String idNode, List<FlumeTopology> listConnections) {

        List<String> listAncestors = new ArrayList<>();

        if ((idNode != null) && (!"".equals(idNode))) {
            for (FlumeTopology flumeTopology : listConnections) {
                if (idNode.equals(flumeTopology.getTargetConnection())) {
                    listAncestors.add(flumeTopology.getSourceConnection());
                    listAncestors.addAll(getGraphAncestorsFromConnections(flumeTopology.getSourceConnection(), listConnections));
                }
            }
        }

        return listAncestors;
    }


    /**
     * Get the name of the agent from a structure
     * @param idNode id of the node
     * @param listConnections list of commenctions
     * @param flumeTopologyList list of Flume Topology
     * @param withAgentNodes true if the structure have agent nodes, false otherwise
     * @return the name of the agent of the indicated node
     */
    public static String getGraphAgentFromConnections(String idNode, List<FlumeTopology> listConnections, List<FlumeTopology> flumeTopologyList, boolean withAgentNodes) {

        String agentName = null;

        if ((idNode != null) && (!"".equals(idNode))) {

            List<String> listAncestorsID = getGraphAncestorsFromConnections(idNode, listConnections);
            List<FlumeTopology> listAncestors = FlumeConfiguratorTopologyUtils.getSubList(listAncestorsID, flumeTopologyList);

            if (listAncestors == null || listAncestors.isEmpty()) {
                //The ancestor is the element
                listAncestorsID = new ArrayList<>();
                listAncestorsID.add(idNode);
                listAncestors = FlumeConfiguratorTopologyUtils.getSubList(listAncestorsID, flumeTopologyList);
                if (listAncestors.size() > 0) {
                    FlumeTopology ancestor = listAncestors.get(0);
                    if (withAgentNodes) {
                        if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT.equals(ancestor.getType())) {
                            agentName = ancestor.getAgentName();
                        }
                    } else {
                        if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE.equals(ancestor.getType())) {
                            agentName = ancestor.getAgentName();
                        }
                    }
                }
            } else {
                for (FlumeTopology ancestor : listAncestors) {
                    if (agentName == null) {
                        if (withAgentNodes) {
                            if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT.equals(ancestor.getType())) {
                                agentName = ancestor.getAgentName();
                            }
                        } else {
                            if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE.equals(ancestor.getType())) {
                                agentName = ancestor.getAgentName();
                            }
                        }
                    }
                }
            }
        }

        return agentName;

    }

    /**
     * Get the agent vertex with the indicated agent name
     * @param flumeGraphTopology map with the graph topology of agents
     * @param agentName name of the agent
     * @return the agent vertex of the agent with the indicated name
     */
    public static FlumeTopology getAgentVertexFromGraph(Map<String, IGraph> flumeGraphTopology, String agentName) {

        FlumeTopology agentVertex = null;

        IGraph agentGraph = flumeGraphTopology.get(agentName);
        Set<FlumeTopology> vertexSet = agentGraph.getVertexSet();

        //Get the agent vertex
        for (FlumeTopology vertex : vertexSet) {
            if (vertex.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT)) {
                agentVertex = vertex;
            }
        }

        return agentVertex;

    }

    /**
     * Get the agent vertex of the graph
     * @param agentGraph IGraph with the graph of the agent
     * @return the vertex agent of the graph
     */
    public static FlumeTopology getAgentVertexFromGraph(IGraph agentGraph) {

        FlumeTopology agentVertex = null;

        Set<FlumeTopology> vertexSet = agentGraph.getVertexSet();

        //Get the agent vertex
        for (FlumeTopology vertex : vertexSet) {
            if (vertex.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT)) {
                agentVertex = vertex;
            }
        }

        return agentVertex;

    }

    /**
     * Convert a Set into a TreeSet (the elements will be ordered)
     * @param hashSet Set of Elements
     * @return TreeSet of elements ordered
     */
    public static TreeSet<FlumeTopology> convetTreeSet(Set<FlumeTopology> hashSet) {
        TreeSet<FlumeTopology> treeSet = new TreeSet<>();

        treeSet.addAll(hashSet);

        return treeSet;
    }



    /**
     * Get the number of parts of the Flume property
     * @param property name of the property
     */
    public static int getPropertyPartsNumber(String property) {

        int partsNumber = 0;
        if (property != null && !"".equals(property)) {
            String[] parts = property.split(FlumeConfiguratorConstants.DOT_REGEX);
            partsNumber = parts.length;
        }

        return partsNumber;
    }


    /**
     * Get the part of the property on the indicated index
     * @param property Name of the property
     * @param partIndex index of part
     * @return Part of the property on the indicated index
     */
    public static String getPropertyPart(String property, int partIndex) {

        String part = "";
        if (property != null && !"".equals(property) && partIndex > 0) {
            int propertyPartsNumber = getPropertyPartsNumber(property);
            if (partIndex <= propertyPartsNumber) {
                String[] parts = property.split(FlumeConfiguratorConstants.DOT_REGEX);
                part = parts[partIndex-1];
            }
        }

        return part;
    }

    /**
     * Get properties with the specified part
     * @param properties properties
     * @param part part to be searched
     * @param partIndex index of the part
     * @param isLastPart true if only properties with the last part is searched
     * @return LinkedProperties with the searched properties
     */
    public static LinkedProperties getPropertiesWithPart(Properties properties, String part, int partIndex, boolean isLastPart) {

        LinkedProperties result = new LinkedProperties();

        String key;
        for (Object keyObject : properties.keySet()) {

            key = (String) keyObject;
            int propertyPartsNumber = getPropertyPartsNumber(key);
            String propertyPart = getPropertyPart(key, partIndex);

            if (!"".equals(propertyPart) && propertyPart.equals(part)) {
                if (isLastPart) {
                    if (partIndex == propertyPartsNumber) {
                        String value = properties.getProperty(key);
                        if (value != null) {
                            result.put(key,value);
                        }
                    }
                } else {
                    String value = properties.getProperty(key);
                    if (value != null) {
                        result.put(key,value);
                    }
                }
            }
        }

        return result;
    }


    /**
     * Obtain set of string with the first part of the properties
     * @param properties properties
     * @return Set of string with the first part of the properties
     */
    public static Set<String> getSetFirstPartProperties(Properties properties) {

        String key;
        Set<String> setFirstPart = new HashSet<>();
        for (Object keyObject : properties.keySet()) {

            key = (String) keyObject;
            String firstPart = getPropertyPart(key,1);
            if (!"".equals(firstPart)) {
                setFirstPart.add(firstPart);
            }
        }

        return setFirstPart;
    }


    /**
     * Get the rest of the name of the property from a specified part index
     * @param propertyName property name
     * @param partIndex index of the parte
     * @return rest of the name of the property from a specified part index
     */
    public static String getTailPartProperty(String propertyName, int partIndex) {

        StringBuilder sb = new StringBuilder();
        if (propertyName != null && !"".equals(propertyName) && partIndex > 0) {
            int propertyPartsNumber = getPropertyPartsNumber(propertyName);
            String[] parts = propertyName.split(FlumeConfiguratorConstants.DOT_REGEX);

            if (propertyPartsNumber > partIndex) {
                for (int i=partIndex; i<propertyPartsNumber; i++) {
                    sb.append(parts[i]);
                    sb.append(FlumeConfiguratorConstants.DOT_SEPARATOR);

                }
            }

            if (sb.length() > 0) {
                sb.setLength(sb.length()-1);
            }
        }
        return sb.toString();
    }


    /**
     * Get the comment of the property if exists
     * @param lines Content of the configuration files
     * @param propertyName property name
     * @return the comment of the property if exists, blank string otherwise
     */
    public static String getPropertyCommentFromText(List<String> lines, String propertyName) {

        String propertyComment = "";

        if (lines != null && propertyName != null && !"".equals(propertyName)) {
            for (int i=1; i<lines.size(); i++) {
                boolean isProperty = false;
                String line = lines.get(i);

                //Get property from line
                line = line.trim();
                int equalIndex = line.indexOf("=");

                if (equalIndex != -1 && !line.startsWith(FlumeConfiguratorConstants.HASH)) {
                    isProperty = true;
                }

                if (isProperty) {
                    String property = line.substring(0, line.indexOf("=")).trim();
                    if (property.equals(propertyName)) {
                        //is the property line. Get the previous line
                        String previousLine = lines.get(i-1);
                        if (previousLine.trim().startsWith(FlumeConfiguratorConstants.HASH)
                                && !previousLine.trim().startsWith("##")
                                && !previousLine.contains("=")) {

                            propertyComment = previousLine.trim().replaceFirst(FlumeConfiguratorConstants.HASH, "").trim();
                        }
                    }
                }
            }
        }

        return propertyComment;
    }

    /**
     * Get id of a FlumeTopology element
     * @param flumeTopologyList List of FlumeTopology elements
     * @param elemName name of the element
     * @return id of the element with the indicated name
     */
    public static String getFlumeTopologyId(List<FlumeTopology> flumeTopologyList, String elemName) {

        String flumeTopologyID = "";

        for (FlumeTopology flumeTopologyElement : flumeTopologyList) {

            String type = flumeTopologyElement.getType();

            if (!FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION.equals(type)) {
                String elementName = flumeTopologyElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                if (elementName != null && !"".equals(elementName) && elemName != null && !"".equals(elemName) && elementName.equals(elemName)) {
                    if (flumeTopologyID.isEmpty()) {
                        flumeTopologyID = flumeTopologyElement.getId();
                    } else {
                        throw new FlumeConfiguratorException("There are more than 1 Flume topology element with the same ID: " + flumeTopologyID);
                    }

                }
            }
        }

        return flumeTopologyID;
    }


    /**
     * Get Flume Topology element with the indicated ID
     * @param flumeTopologyList Flume Topolology elements list
     * @param flumeTopologyId ID of searched Flume Topology element
     * @return Flume Topology element with the indicated ID
     */
    public static FlumeTopology getFlumeTopologyElement(List<FlumeTopology> flumeTopologyList, String flumeTopologyId) {

        FlumeTopology flumeTopology = null;

        for (FlumeTopology flumeTopologyElement : flumeTopologyList) {

            String flumeTopologyElementID = flumeTopologyElement.getId();

            if (flumeTopologyId != null && !"".equals(flumeTopologyId) && flumeTopologyElementID != null &&
                    !"".equals(flumeTopologyElementID) && flumeTopologyId.equals(flumeTopologyElementID)) {

                flumeTopology = flumeTopologyElement;
            }
        }

        return flumeTopology;
    }


    /**
     * Get the ordered list of interceptors from a source. The order is determinated by the connections
     * @param sourceInterceptorsList List of interceptors of the source
     * @param sourceId Od pf tje spurce
     * @param listTopologyConnections List of Flume Topology connections
     * @param flumeTopologyList List of Flume Topology elements
     * @return The ordered list of interceptors
     */
    public static List<String> orderSourceInterceptorsFromConnections(List<String> sourceInterceptorsList, String sourceId,
                                                                      List<FlumeTopology> listTopologyConnections,
                                                                      List<FlumeTopology> flumeTopologyList) {

        List<String> sourceInterceptorsOrderedList = new ArrayList<>();

        if (sourceInterceptorsList == null || sourceInterceptorsList.size() == 1) {
            sourceInterceptorsOrderedList = sourceInterceptorsList;
        } else {
            String interceptorConnectionID = getInterceptorConnection(sourceId, listTopologyConnections, flumeTopologyList);

            while (interceptorConnectionID != null && !interceptorConnectionID.isEmpty()) {

                FlumeTopology flumeTopologyElement = getFlumeTopologyElement(flumeTopologyList, interceptorConnectionID);

                String interceptorName = flumeTopologyElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                sourceInterceptorsOrderedList.add(interceptorName);

                interceptorConnectionID = getInterceptorConnection(interceptorConnectionID, listTopologyConnections, flumeTopologyList);
            }

            if (sourceInterceptorsOrderedList.size() != sourceInterceptorsList.size()) {
                throw new FlumeConfiguratorException("The source interceptor ordered list of source: " + sourceId + "has a different size than unordered list");
            }

            for (String interceptorName : sourceInterceptorsOrderedList) {
                if (!sourceInterceptorsList.contains(interceptorName)) {
                    throw new FlumeConfiguratorException("The source interceptor ordered list of source: " + sourceId + "has different elements than unordered list");
                }
            }
        }

        return sourceInterceptorsOrderedList;
    }


    /**
     * Get the list with all target connections from a source connection
     * @param sourceConnectionId Source connection ID
     * @param listTopologyConnections Flume Topology connections list
     * @return List with all target connections from a source connection
     */
    public static List<String> getAllTargetConnections(String sourceConnectionId, List<FlumeTopology> listTopologyConnections) {

        List<String> targetConnections = new ArrayList<>();

        if (sourceConnectionId != null && !sourceConnectionId.isEmpty() ) {
            for (FlumeTopology connection : listTopologyConnections) {

                String sourceConnection = connection.getSourceConnection();
                if (sourceConnection != null && !sourceConnection.isEmpty() && sourceConnection.equals(sourceConnectionId)) {
                    String targetConnection = connection.getTargetConnection();
                    targetConnections.add(targetConnection);
                }
            }
        }

        return targetConnections;
    }


    /**
     * Get the connection to the first interceptor of a source (interceptor's ID)
     * @param sourceConnectionId Source ID
     * @param listTopologyConnections List of Flume Topology connections
     * @param flumeTopologyList List of Flume Topology elements
     * @return Get the connection to the first interceptor of a source (interceptor's ID)
     */
    static String getInterceptorConnection(String sourceConnectionId, List<FlumeTopology> listTopologyConnections, List<FlumeTopology> flumeTopologyList) {

        String interceptorConnectionID = "";

        List<String> targetConnectionsList = getAllTargetConnections(sourceConnectionId, listTopologyConnections);

        for (String targetConnection : targetConnectionsList) {

            FlumeTopology flumeTopologyElement = getFlumeTopologyElement(flumeTopologyList, targetConnection);

            if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR.equals(flumeTopologyElement.getType())) {
                interceptorConnectionID = targetConnection;
            }
        }

        return interceptorConnectionID;
    }


    /**
     * Get the last interceptor of a specified source (if exists)
     * @param properties properties
     * @param sourceName name of the source
     * @return the last interceptor from the source (if exists)
     */
    public static String getLastInterceptorNameFromSource(Properties properties, String sourceName) {

        String key;
        String lastInterceptorID = "";

        if (properties != null && sourceName != null && !sourceName.isEmpty()) {
            LinkedProperties interceptorsPart = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(properties, FlumeConfiguratorConstants.INTERCEPTORS_PROPERTY, FlumeConfiguratorConstants.INTERCEPTORS_LIST_PROPERTY_PART_INDEX, true);


            for (Object keyObject : interceptorsPart.keySet()) {
                key = (String) keyObject;
                String sourceInterceptorName = FlumeConfiguratorTopologyUtils.getPropertyPart(key, FlumeConfiguratorConstants.ELEMENT_PROPERTY_PART_INDEX);

                if (sourceInterceptorName.equals(sourceName)) {
                    //is the searched source. Get the interceptors
                    String propertyValue = interceptorsPart.getProperty(key);

                    if (propertyValue != null && !"".equals(propertyValue)) {
                        String[] interceptors = propertyValue.split(FlumeConfiguratorConstants.WHITE_SPACE_REGEX);

                        if (lastInterceptorID.isEmpty()) {
                            lastInterceptorID = interceptors[interceptors.length - 1];
                        } else {
                            throw new FlumeConfiguratorException("There are sources with more than 1 interceptors property defined: " + sourceName);
                        }
                    }
                }
            }
        }

        return lastInterceptorID;
    }


    /**
     * Get the number of slices to divide the canvas size
     * @param flumeGraphTopology map with the graph topology of agents
     * @param agentName name of the agent
     * @return number of slices to divide the canvas size
     */
    public static int calculateSlicesNumber(Map<String, IGraph> flumeGraphTopology, String agentName) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN calculateSlicesNumber");
        }

        int maxSlicesNumber = 0;

        if (flumeGraphTopology != null && agentName != null && !agentName.isEmpty()) {

            IGraph agentGraph = flumeGraphTopology.get(agentName);

            if (agentGraph != null) {
                FlumeTopology agentVertex = FlumeConfiguratorTopologyUtils.getAgentVertexFromGraph(agentGraph);

                List<FlumeTopology> sourcesList = agentGraph.successorListOf(agentVertex);

                int interceptorsNumber;
                int maxInterceptorsNumber = 0;
                boolean withSelector = false;
                boolean withSinkGroup = false;

                //Get the descendants of the source
                for (FlumeTopology source : sourcesList) {

                    if (source.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE)) {

                        //Get descendants of source
                        Set<FlumeTopology> sourceChildren = agentGraph.getVertexDescendants(source);
                        Iterator<FlumeTopology> itSourceChildren = sourceChildren.iterator();

                        interceptorsNumber = 0;

                        while (itSourceChildren.hasNext()) {
                            FlumeTopology sourceChild = itSourceChildren.next();

                            if (sourceChild.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR)) {
                                interceptorsNumber ++;
                            }

                            if (sourceChild.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SELECTOR)) {
                                withSelector = true;
                            }

                            if (sourceChild.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP)) {
                                withSinkGroup = true;
                            }
                        }

                        if (interceptorsNumber > maxInterceptorsNumber) {
                            maxInterceptorsNumber = interceptorsNumber;
                        }
                    }
                }

                int fixedSlicesNumber = FlumeConfiguratorConstants.FIXED_SLICES_NUMBER;
                if (withSelector) {
                    fixedSlicesNumber++;
                }
                if (withSinkGroup) {
                    fixedSlicesNumber++;
                }
                maxSlicesNumber = maxInterceptorsNumber + fixedSlicesNumber;

                logger.debug("Max interceptors number agent: " + agentName + " = " + maxInterceptorsNumber);
                logger.debug("Max slices number agent: " + agentName + " = " + maxSlicesNumber);

            }
        }


        if (logger.isDebugEnabled()) {
            logger.debug("END calculateSlicesNumber");
        }

        return maxSlicesNumber;
    }



    /**
     * Get the Y coordinate from the element with the maximum Y coordinate
     * @param flumeTopologyList List of Flume Topology elements
     * @return the Y coordinate from the element with the maximum Y coordinate
     */
    public static int getMaxYCoordinate(List<FlumeTopology> flumeTopologyList) {

        int lastYCoordinate = FlumeConfiguratorConstants.CANVAS_ELEMENTS_HEIGHT_PX_SEPARATION;

        if (flumeTopologyList != null) {
            for (FlumeTopology flumeTopology : flumeTopologyList) {
                if (flumeTopology.getY() != null && !flumeTopology.getY().isEmpty()) {
                    int lastYCoordinateValue = Integer.valueOf(flumeTopology.getY());
                    if (lastYCoordinateValue >  lastYCoordinate) {
                        lastYCoordinate = lastYCoordinateValue;
                    }
                }
            }
        }

        return lastYCoordinate;
    }


    /**
     * Get next Y Coordinate in function of max Y coordinate and first element of slice
     * @param maxYCoordinate the max Y coordinate obtained until now
     * @param isFirstElementSlice true if is the first element of the slice, false otherwise
     * @return next Y Coordinate
     */
    public static int getNextYCoordinate(int maxYCoordinate, boolean isFirstElementSlice) {

        int nextYCoordinate = maxYCoordinate;

        if (!isFirstElementSlice) {
            nextYCoordinate = maxYCoordinate + FlumeConfiguratorConstants.CANVAS_ELEMENT_PX_HEIGHT + FlumeConfiguratorConstants.CANVAS_ELEMENTS_HEIGHT_PX_SEPARATION;
        }

        return nextYCoordinate;
    }


    /**
     * Detect if a list contains a sublist
     * @param list the list
     * @param searchedElementsSublist the sublist
     * @return true if the list contains the sublist in identical order, false otherwise
     */
    static boolean isCorrectOrderSublist(List<String> list, List<String> searchedElementsSublist) {

        boolean hasOrderedSublist = false;

        if (list != null && searchedElementsSublist != null && searchedElementsSublist.size() <= list.size()) {

            int fromIndex = 0;
            while (fromIndex + searchedElementsSublist.size() <= list.size() && !hasOrderedSublist) {

                //Create sublist with the size of the list of searched elements
                List<String> sublist = list.subList(fromIndex, fromIndex + searchedElementsSublist.size());

                //Check all elements sublist are elements of searched list
                hasOrderedSublist = sublist.containsAll(searchedElementsSublist) && searchedElementsSublist.containsAll(sublist);

                fromIndex++;
            }
        }

        return hasOrderedSublist;
    }


    /**
     * Get relation between shared channels and sources
     * @param sharedSources list of shared sources
     * @param sourcesChannelsRelationsMap relation between sources and channels
     * @param agentName Name of the agent
     * @return Relation between shared channels and sources
     */
    public static Map<String, List<String>> getMapSharedChannelsSourcesRelation(List<String> sharedSources, Map<String, Map<String, List<String>>> sourcesChannelsRelationsMap, String agentName) {

        Map<String, List<String>> mapSharedChannelsSourcesRelation = new HashMap<>();

        if (sharedSources != null && sourcesChannelsRelationsMap != null && agentName != null) {
            for(String sharedSourceName : sharedSources) {

                //Get channels of the shared source
                List<String> sharedChannelsList = sourcesChannelsRelationsMap.get(agentName).get(sharedSourceName);

                for(String sharedChannelName : sharedChannelsList) {

                    if (!mapSharedChannelsSourcesRelation.containsKey(sharedChannelName)) {
                        mapSharedChannelsSourcesRelation.put(sharedChannelName, new ArrayList<>());
                    }
                    if (!mapSharedChannelsSourcesRelation.get(sharedChannelName).contains(sharedSourceName)) {
                        mapSharedChannelsSourcesRelation.get(sharedChannelName).add(sharedSourceName);
                    }
                }
            }
        }

        return mapSharedChannelsSourcesRelation;
    }


    /**
     * Get complete list of shared sources from a subset of shared sources
     * @param mapSharedChannelsSourcesRelation relation between shared channels and sources
     * @return complete list of shared sources
     */
    public static List<String> getCompleteSharedSourcesList(Map<String, List<String>> mapSharedChannelsSourcesRelation) {

        //Construct initial order or sources from the larger one
        List<String> greaterSourceList = new ArrayList<>();
        List<String> completeSourcesList = null;

        if (mapSharedChannelsSourcesRelation != null) {
            for (String channelName : mapSharedChannelsSourcesRelation.keySet()) {
                List<String> sourcesList = mapSharedChannelsSourcesRelation.get(channelName);

                if (sourcesList.size() > greaterSourceList.size()) {
                    greaterSourceList = new ArrayList<>(sourcesList);
                }
            }

            //Complete the rest of sources
            completeSourcesList = new ArrayList<>(greaterSourceList);
            for (String channelName : mapSharedChannelsSourcesRelation.keySet()) {
                List<String> sourcesList = mapSharedChannelsSourcesRelation.get(channelName);
                for (String sourceName : sourcesList) {
                    if (!greaterSourceList.contains(sourceName)) {
                        completeSourcesList.add(sourceName);
                    }
                }
            }
        }

        return completeSourcesList;
    }


    /**
     * Get permutations that preserve a order determinated by shared channels
     * @param completeSourcesList complete list of shared sources
     * @param sharedChannelsSourcesRelationMap relation between shared channels and sources
     * @return list of permutatios of shared sources that preserve a order determinated by shared channels
     */
    public static Collection<List<String>> getSharedSourcesPermutations(List<String> completeSourcesList, Map<String, List<String>> sharedChannelsSourcesRelationMap) {

        Collection<List<String>> sourcesPermutations = new ArrayList<>();

        List<String> sourcesReferencedByMap = new ArrayList<>();
        List<String> sourcesNotReferencedByMap = new ArrayList<>();
        boolean isSharedSource;

        if (completeSourcesList != null && sharedChannelsSourcesRelationMap != null) {

            if (sharedChannelsSourcesRelationMap.size() > 0) {

                if (logger.isDebugEnabled()) {
                    logger.debug("Generating sources permutations");
                }

                //Detect sources not referencied by map
                for (String sourceName : completeSourcesList) {
                    isSharedSource = false;
                    for (String channelName : sharedChannelsSourcesRelationMap.keySet()) {
                        List<String> channelSourcesList = sharedChannelsSourcesRelationMap.get(channelName);
                        if (channelSourcesList.contains(sourceName)) {
                            if (!sourcesReferencedByMap.contains(sourceName)) {
                                sourcesReferencedByMap.add(sourceName);
                            }
                            isSharedSource = true;
                        }
                    }

                    if (!isSharedSource) {
                        sourcesNotReferencedByMap.add(sourceName);
                    }
                }


                //Get all permutations and correct permutations of the sources
                if (sourcesReferencedByMap.size() <= FlumeConfiguratorConstants.MAX_NUMBER_SOURCES_FOR_PERMUTATIONS) {
                    sourcesPermutations = CollectionUtils.permutations(sourcesReferencedByMap);
                } else {

                    if (sharedChannelsSourcesRelationMap.size() <= FlumeConfiguratorConstants.MAX_NUMBER_CHANNELS_FOR_PERMUTATIONS) {


                        sourcesPermutations = getSourcesPartialNumberPermutations(sharedChannelsSourcesRelationMap);

                        if (logger.isDebugEnabled()) {
                            logger.debug("Order completeSourcesList: " + Arrays.toString(completeSourcesList.toArray()));
                            if (sourcesPermutations.size() > 0) {
                                logger.debug("Order getSourcesPartialNumberPermutations(0): " + Arrays.toString(sourcesPermutations.iterator().next().toArray()));
                            }
                        }

                        if (sourcesPermutations.size() == 0) {
                            //The permutations doesn't have been calculated (the cartesian product is too big)
                            if (logger.isWarnEnabled()) {
                                logger.warn("There is no permutations calculation (too many elements for any cartesian product)");
                            }
                            sourcesPermutations.add(completeSourcesList);
                        }

                    } else {
                        //There is no optimal sources permutation. Take the complete sources list as optimal permutation
                        if (logger.isWarnEnabled()) {
                            logger.warn("There is no permutations calculation (too many elements) - Num shared sources: " + sourcesReferencedByMap.size() + " Num shared channels: " + sharedChannelsSourcesRelationMap.size() + " Limit shared channels: " + FlumeConfiguratorConstants.MAX_NUMBER_CHANNELS_FOR_PERMUTATIONS);
                        }
                        sourcesPermutations.add(completeSourcesList);
                    }
                }

                sourcesPermutations = addSourcesNotReferencedByMapToPermutation(sourcesPermutations, sourcesNotReferencedByMap);

            } else {
                //It's not neccesary permutations. Any order is OK
                sourcesPermutations.add(completeSourcesList);
            }
        }

        return sourcesPermutations;
    }



    /**
     * Get relation between shared channels and number of sources
     * @param completeSourcesList complete list of shared sources
     * @param agentGraph graph of the agent
     * @param flumeTopologyList list of Flume Topology elements
     * @return relation between shared channels and number of sources
     */
    public static Map<String,Integer> getMapChannelSourcesNumberRelation(List<String> completeSourcesList, IGraph agentGraph,  List<FlumeTopology> flumeTopologyList) {

        Map<String,Integer> mapChannelSourcesNumberRelation = new HashMap<>();

        if (completeSourcesList != null && agentGraph != null && flumeTopologyList != null) {
            for (String sourceName : completeSourcesList) {

                String sourceID = FlumeConfiguratorTopologyUtils.getFlumeTopologyId(flumeTopologyList, sourceName);
                FlumeTopology flumeTopologySourceElement = agentGraph.getVertex(sourceID, true);

                Set<FlumeTopology> sourceChildren = FlumeConfiguratorTopologyUtils.convetTreeSet(agentGraph.getVertexDescendants(flumeTopologySourceElement));
                Iterator<FlumeTopology> itSourceChildren = sourceChildren.iterator();

                while (itSourceChildren.hasNext()) {
                    FlumeTopology sourceChild = itSourceChildren.next();

                    if (sourceChild.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL)) {
                        String channelName = sourceChild.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                        //Get all sources from channel
                        Set<FlumeTopology> channelAncestorsList = agentGraph.getVertexAncestors(sourceChild);

                        int sourcesChannelAncestorNumber = 0;
                        for (FlumeTopology channelAncestor : channelAncestorsList) {

                            if (channelAncestor.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE)) {
                                sourcesChannelAncestorNumber++;
                            }
                        }

                        mapChannelSourcesNumberRelation.put(channelName, sourcesChannelAncestorNumber);
                    }
                }
            }
        }

        return mapChannelSourcesNumberRelation;
    }


    /**
     * Get relation between shared sources and independent channels
     * @param completeSourcesList complete list of shared sources
     * @param agentGraph graph of the agent
     * @param flumeTopologyList list of Flume Topology elements
     * @param mapChannelSourcesNumberRelation relation between shared channels and number of sources
     * @return relation between shared sources and independent channels
     */
    public static Map<String,List<String>> getMapSourcesIndependentChannelsRelation(List<String> completeSourcesList, IGraph agentGraph, List<FlumeTopology> flumeTopologyList,  Map<String,Integer> mapChannelSourcesNumberRelation) {

        Map<String,List<String>> mapSourcesIndependentChannelsRelation = new HashMap<>();

        if (completeSourcesList != null && agentGraph != null && flumeTopologyList != null && mapChannelSourcesNumberRelation != null) {
            for (String sourceName : completeSourcesList) {

                String xourceID = FlumeConfiguratorTopologyUtils.getFlumeTopologyId(flumeTopologyList, sourceName);
                FlumeTopology flumeTopologySourceElement = agentGraph.getVertex(xourceID, true);

                Set<FlumeTopology> sourceChildren = FlumeConfiguratorTopologyUtils.convetTreeSet(agentGraph.getVertexDescendants(flumeTopologySourceElement));
                Iterator<FlumeTopology> itSourceChildren = sourceChildren.iterator();

                List<String> sourceIndependentChannelsList = new ArrayList<>();
                while (itSourceChildren.hasNext()) {
                    FlumeTopology sourceChild = itSourceChildren.next();


                    if (sourceChild.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL)) {
                        String channelName = sourceChild.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                        //check if channel is idependent
                        if (mapChannelSourcesNumberRelation.get(channelName) == 1) {
                            //It's an independent channel
                            sourceIndependentChannelsList.add(channelName);
                        }
                    }

                }

                mapSourcesIndependentChannelsRelation.put(sourceName, sourceIndependentChannelsList);
            }
        }

        return mapSourcesIndependentChannelsRelation;
    }


    /**
     * Get list of interceptors of a source
     * @param flumeTopologySourceElement Flume Topology element of the source
     * @param agentGraph graph of agent of the source
     * @return list of interceptors of the specified source
     */
    public static List<String> getSourceInterceptorsList(FlumeTopology flumeTopologySourceElement, IGraph agentGraph) {

        List<String> sourceInterceptorsList = new ArrayList<>();

        if (flumeTopologySourceElement != null && agentGraph != null ) {

            Set<FlumeTopology> sourceChildren = FlumeConfiguratorTopologyUtils.convetTreeSet(agentGraph.getVertexDescendants(flumeTopologySourceElement));
            Iterator<FlumeTopology> itSourceChildren = sourceChildren.iterator();

            while (itSourceChildren.hasNext()) {
                FlumeTopology sourceChild = itSourceChildren.next();

                if (sourceChild.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR)) {
                    String interceptorName = sourceChild.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);
                    sourceInterceptorsList.add(interceptorName);
                }

            }
        }

        return sourceInterceptorsList;

    }


    /**
     * Get list of descendants of a source with the indicated type
     * @param flumeTopologySourceElement Flume Topology element of the source
     * @param agentGraph graph of agent of the source
     * @param descendantType type of the descendant
     * @return list of descendants of a source with the indicated type
     */
    public static List<String> getSourceDescendantsTypeList(FlumeTopology flumeTopologySourceElement, IGraph agentGraph, String descendantType) {

        List<String> sourceInterceptorsList = new ArrayList<>();

        if (flumeTopologySourceElement != null && agentGraph != null && descendantType != null && !descendantType.isEmpty()) {

            Set<FlumeTopology> sourceChildren = FlumeConfiguratorTopologyUtils.convetTreeSet(agentGraph.getVertexDescendants(flumeTopologySourceElement));
            Iterator<FlumeTopology> itSourceChildren = sourceChildren.iterator();

            while (itSourceChildren.hasNext()) {
                FlumeTopology sourceChild = itSourceChildren.next();

                if (sourceChild.getType().equals(descendantType)) {
                    String interceptorName = sourceChild.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);
                    sourceInterceptorsList.add(interceptorName);
                }

            }
        }

        return sourceInterceptorsList;

    }


    /**
     * Get relation between sources and interceptors
     * @param agentGraph graph of the agent
     * @return relation between sources and interceptors
     */
    public static Map<String,List<String>> getMapSourcesInterceptorsRelation(IGraph agentGraph) {

        Map<String,List<String>> mapSourcesInterceptorsRelation = new HashedMap<>();

        if (agentGraph != null) {

            FlumeTopology agentVertex = FlumeConfiguratorTopologyUtils.getAgentVertexFromGraph(agentGraph);
            List<FlumeTopology> sourcesList = agentGraph.successorListOf(agentVertex);

            //Get the descendants of the source
            for (FlumeTopology source : sourcesList) {

                if (source.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE)) {

                    String sourceName = source.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                    List<String> sourceInterceptorsList = getSourceInterceptorsList(source, agentGraph);
                    mapSourcesInterceptorsRelation.put(sourceName, sourceInterceptorsList);
                }
            }
        }

        return mapSourcesInterceptorsRelation;
    }


    /**
     * Detect existence of a sinkgroup slice
     * @param flumeGraphTopology map with the graph topology of agents
     * @param agentName name of the agent
     * @return true if the agent has a sinkgroup element, false otherwise
     */
    public static boolean existSinkGroupSlice(Map<String, IGraph> flumeGraphTopology, String agentName) {

        boolean existSinkGroupSlice = false;

        if (flumeGraphTopology != null && agentName != null && !agentName.isEmpty()) {

            IGraph agentGraph = flumeGraphTopology.get(agentName);

            if (agentGraph != null) {
                FlumeTopology agentVertex = FlumeConfiguratorTopologyUtils.getAgentVertexFromGraph(agentGraph);

                Set<FlumeTopology> agentChildren = agentGraph.getVertexDescendants(agentVertex);
                Iterator<FlumeTopology> itAgentChildren = agentChildren.iterator();

                while (itAgentChildren.hasNext()) {
                    FlumeTopology agentChild = itAgentChildren.next();

                    if (agentChild.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP)) {
                        existSinkGroupSlice = true;
                    }
                }
            }
        }

        return existSinkGroupSlice;
    }

    /**
     * Detect existence of a slice of the indicated type
     * @param flumeGraphTopology map with the graph topology of agents
     * @param agentName name of the agent
     * @param sliceType type of the slice
     * @return true if the agent has a slice of the indicated type, false otherwise
     */
    public static boolean existSliceType(Map<String, IGraph> flumeGraphTopology, String agentName, String sliceType) {

        boolean existSliceType = false;

        if (flumeGraphTopology != null && agentName != null && !agentName.isEmpty()) {

            IGraph agentGraph = flumeGraphTopology.get(agentName);

            if (agentGraph != null) {
                FlumeTopology agentVertex = FlumeConfiguratorTopologyUtils.getAgentVertexFromGraph(agentGraph);

                Set<FlumeTopology> agentChildren = agentGraph.getVertexDescendants(agentVertex);
                Iterator<FlumeTopology> itAgentChildren = agentChildren.iterator();

                while (itAgentChildren.hasNext()) {
                    FlumeTopology agentChild = itAgentChildren.next();

                    if (agentChild.getType().equals(sliceType)) {
                        existSliceType = true;
                    }
                }
            }
        }

        return existSliceType;
    }


    /**
     * Get a list with the Flume topology elements of the searched type for an agent
     * @param flumeGraphTopology map with the graph topology of agents
     * @param agentName name of the agent
     * @param elementType Type of the searched elements
     * @return a list with the Flume topology elements of the searched type for an agent
     */
    public static List<FlumeTopology> getListFlumeTopologyByType(Map<String, IGraph> flumeGraphTopology, String agentName, String elementType) {


        List<FlumeTopology> listFlumeTopologyByType = new ArrayList<>();

        if (flumeGraphTopology != null && agentName != null && !agentName.isEmpty() && elementType != null && !elementType.isEmpty()) {

            IGraph agentGraph = flumeGraphTopology.get(agentName);

            if (agentGraph != null) {

                FlumeTopology agentVertex = FlumeConfiguratorTopologyUtils.getAgentVertexFromGraph(agentGraph);

                if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT.equals(elementType)) {
                    listFlumeTopologyByType.add(agentVertex);
                } else {

                    Set<FlumeTopology> agentChildren = agentGraph.getVertexDescendants(agentVertex);
                    Iterator<FlumeTopology> itAgentChildren = agentChildren.iterator();

                    while (itAgentChildren.hasNext()) {
                        FlumeTopology agentChild = itAgentChildren.next();

                        if (agentChild.getType().equals(elementType)) {
                            listFlumeTopologyByType.add(agentChild);
                        }
                    }
                }
            }
        }

        return listFlumeTopologyByType;
    }


    /**
     * Get relation between shared sinkgroups and sources
     * @param sharedSources list of shared sources
     * @param sourcesSinkGroupsRelationsMap relation between sources and sinkgroups
     * @return Relation between shared sinkgroups and sources
     */
    public static Map<String, List<String>> getMapSharedSinkGroupsSourcesRelation(List<String> sharedSources, Map<String, List<String>> sourcesSinkGroupsRelationsMap) {

        Map<String, List<String>> mapSharedSinkGroupsSourcesRelation = new HashMap<>();

        if (sharedSources != null && sourcesSinkGroupsRelationsMap != null) {
            for(String sharedSourceName : sharedSources) {

                //Get sinkgroups of the shared source
                List<String> sharedSinkGroupsList = sourcesSinkGroupsRelationsMap.get(sharedSourceName);

                for(String sharedSinkGroupName : sharedSinkGroupsList) {

                    if (!mapSharedSinkGroupsSourcesRelation.containsKey(sharedSinkGroupName)) {
                        mapSharedSinkGroupsSourcesRelation.put(sharedSinkGroupName, new ArrayList<>());
                    }
                    if (!mapSharedSinkGroupsSourcesRelation.get(sharedSinkGroupName).contains(sharedSourceName)) {
                        mapSharedSinkGroupsSourcesRelation.get(sharedSinkGroupName).add(sharedSourceName);
                    }
                }
            }
        }

        return mapSharedSinkGroupsSourcesRelation;
    }



    /**
     * Get first ancestor with indicated type of the node
     * @param node Node of the tree
     * @param elementType type of the searched ancestor
     * @return first ancestor with the indicated type
     */
    public static DefaultMutableTreeNode getFirstAncestorByType(DefaultMutableTreeNode node, String elementType) {

        DefaultMutableTreeNode ancestorNode = null;

        if (node != null) {
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
            FlumeTopology flumeTopologyParentElement = (FlumeTopology) parentNode.getUserObject();
            while (parentNode != null && !flumeTopologyParentElement.getType().equals(elementType)) {
                parentNode = (DefaultMutableTreeNode) parentNode.getParent();
                if (parentNode != null) {
                    flumeTopologyParentElement = (FlumeTopology) parentNode.getUserObject();
                }

            }

            ancestorNode = parentNode;
        }

        return ancestorNode;
    }


    /**
     * Indicate if a property (flume configuration) is a property of selector that references channels
     * @param propertyName String with the name of the property
     * @return true if the property is t a property of selector that references channels, false otherwise
     */
    public static boolean isSelectorChannelReferenceFlumeConfigurationProperty(String propertyName) {

        return (FlumeConfiguratorTopologyUtils.getPropertyPartsNumber(propertyName) >= 4 &&
            FlumeConfiguratorConstants.SOURCES_PROPERTY.equals(FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, FlumeConfiguratorConstants.SOURCES_LIST_PROPERTY_PART_INDEX)) &&
            FlumeConfiguratorConstants.SELECTOR_PROPERTY.equals(FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, FlumeConfiguratorConstants.SOURCE_SELECTOR_PROPERTY_PART_INDEX)) &&
            (FlumeConfiguratorConstants.MAPPING_PROPERTY.equals(FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, FlumeConfiguratorConstants.SOURCE_SELECTOR_PROPERTY_PART_INDEX + 1)) ||
             FlumeConfiguratorConstants.OPTIONAL_PROPERTY.equals(FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, FlumeConfiguratorConstants.SOURCE_SELECTOR_PROPERTY_PART_INDEX + 1)) ||
             FlumeConfiguratorConstants.DEFAULT_PROPERTY.equals(FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, FlumeConfiguratorConstants.SOURCE_SELECTOR_PROPERTY_PART_INDEX + 1))));
    }


    /**
     * Get the free average Y coordinate position for an flume topology element relationated with another elements connected to it
     * @param agentGraph graph of the agent
     * @param flumeTopologyList list of Flume Topology elements
     * @param flumeTopologyElement Flume Topology element
     * @param referenceTypeElement type of elements connected to it
     * @return free average Y coordinate position
     */
    public static int getElementAveragePosition(IGraph agentGraph, List<FlumeTopology> flumeTopologyList, FlumeTopology flumeTopologyElement, String referenceTypeElement) {
        int average_Y_Coordinate;
        int min_Y_coordinate = -1;
        int max_Y_coordinate = -1;

        //Get descendants of the element
        String elementName = flumeTopologyElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

        boolean isAgentWithSource = FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT.equals(flumeTopologyElement.getType()) && FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE.equals(referenceTypeElement);
        boolean isSourceWithChannel = FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE.equals(flumeTopologyElement.getType()) && FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL.equals(referenceTypeElement);
        boolean isChannelWithSink = FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL.equals(flumeTopologyElement.getType()) && FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK.equals(referenceTypeElement);
        boolean isChannelWithSource = FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL.equals(flumeTopologyElement.getType()) && FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE.equals(referenceTypeElement);
        boolean isSinkgroupWithSink = FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP.equals(flumeTopologyElement.getType()) && FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK.equals(referenceTypeElement);

        logger.debug("Calculating average position of element [" + elementName + " relation with its [" + referenceTypeElement + "]");

        if (isAgentWithSource || isSourceWithChannel || isChannelWithSink) {

            Set<FlumeTopology> flumeTopologyElementChildren = agentGraph.getVertexDescendants(flumeTopologyElement);
            Iterator<FlumeTopology> itFlumeTopologyElementChildren = flumeTopologyElementChildren.iterator();

            while (itFlumeTopologyElementChildren.hasNext()) {

                FlumeTopology flumeTopologyElementChild = itFlumeTopologyElementChildren.next();

                if (flumeTopologyElementChild.getType().equals(referenceTypeElement)) {
                    if (flumeTopologyElementChild.getY() != null && !flumeTopologyElementChild.getY().isEmpty() && Integer.valueOf(flumeTopologyElementChild.getY()) > max_Y_coordinate) {
                        max_Y_coordinate =  Integer.valueOf(flumeTopologyElementChild.getY());
                    }

                    if (min_Y_coordinate == -1) {
                        if (flumeTopologyElementChild.getY() != null && !flumeTopologyElementChild.getY().isEmpty()) {
                            min_Y_coordinate = Integer.valueOf(flumeTopologyElementChild.getY());
                        }
                    } else {
                        if (flumeTopologyElementChild.getY() != null && !flumeTopologyElementChild.getY().isEmpty() && Integer.valueOf(flumeTopologyElementChild.getY()) < min_Y_coordinate) {
                            min_Y_coordinate = Integer.valueOf(flumeTopologyElementChild.getY());
                        }
                    }
                }
            }
        } else if (isChannelWithSource || isSinkgroupWithSink) {

            Set<FlumeTopology> flumeTopologyElementAncestors = agentGraph.getVertexAncestors(flumeTopologyElement);
            Iterator<FlumeTopology> itFlumeTopologyElementAncestors = flumeTopologyElementAncestors.iterator();

            while (itFlumeTopologyElementAncestors.hasNext()) {
                FlumeTopology flumeTopologyElementAncestor = itFlumeTopologyElementAncestors.next();

                if (flumeTopologyElementAncestor.getType().equals(referenceTypeElement)) {
                    if (flumeTopologyElementAncestor.getY() != null && !flumeTopologyElementAncestor.getY().isEmpty() && Integer.valueOf(flumeTopologyElementAncestor.getY()) > max_Y_coordinate) {
                        max_Y_coordinate =  Integer.valueOf(flumeTopologyElementAncestor.getY());
                    }

                    if (min_Y_coordinate == -1) {
                        if (flumeTopologyElementAncestor.getY() != null && !flumeTopologyElementAncestor.getY().isEmpty()) {
                            min_Y_coordinate = Integer.valueOf(flumeTopologyElementAncestor.getY());
                        }
                    } else {
                        if (flumeTopologyElementAncestor.getY() != null && !flumeTopologyElementAncestor.getY().isEmpty() && Integer.valueOf(flumeTopologyElementAncestor.getY()) < min_Y_coordinate) {
                            min_Y_coordinate = Integer.valueOf(flumeTopologyElementAncestor.getY());
                        }
                    }
                }
            }

        }


        logger.debug("min_Y_coordinate descendants / ancestors element [ " + elementName + "]: " + min_Y_coordinate);
        logger.debug("max_Y_coordinate  descendants / ancestors element [ " + elementName + "]: " + max_Y_coordinate);

        if (max_Y_coordinate == min_Y_coordinate) {
            average_Y_Coordinate = min_Y_coordinate;
        } else {
            average_Y_Coordinate = min_Y_coordinate + (max_Y_coordinate - min_Y_coordinate) / 2;
        }

        logger.debug("average_Y_Coordinate (PRE isEmptyPosition) [ " + elementName + "]: " + average_Y_Coordinate);

        while (!isEmptyPosition(flumeTopologyList, flumeTopologyElement, average_Y_Coordinate)) {
            logger.debug("Element " + elementName + " has a non valid recalculated Y-coordinate position : " + average_Y_Coordinate);
            average_Y_Coordinate = getNextYCoordinate(average_Y_Coordinate, false);
            logger.debug("New proposed Y-coordinate position for element"  + elementName + " : " + average_Y_Coordinate);
        }

        logger.debug("average_Y_Coordinate element (POST isEmptyPosition) [ " + elementName + "]: " + average_Y_Coordinate);
        return average_Y_Coordinate;
    }


    /**
     * Return if the position is a empty position or there are another element
     * @param flumeTopologyList list of Flume Topology elements
     * @param flumeTopologyElement Flume topology element
     * @param position_Y_coordinate Y-coordinate position
     * @return true if the Y-coordinate position is free
     */
    private static boolean isEmptyPosition(List<FlumeTopology> flumeTopologyList, FlumeTopology flumeTopologyElement, int position_Y_coordinate) {

        boolean isEmptyPosition = true;

        for (FlumeTopology flumeTopologyListElement : flumeTopologyList) {

            if (flumeTopologyListElement.getType().equals(flumeTopologyElement.getType()) && !flumeTopologyListElement.equals(flumeTopologyElement)) {
                if (flumeTopologyListElement.getY() != null && !flumeTopologyListElement.getY().isEmpty()) {
                    int element_Y_coordinate = Integer.valueOf(flumeTopologyListElement.getY());
                    int separation = FlumeConfiguratorConstants.CANVAS_ELEMENTS_HEIGHT_PX_SEPARATION * 2;

                    logger.debug("Comparing position_Y_coordinate " + position_Y_coordinate + " with position of list element with id " + flumeTopologyListElement.getId() + " (y_coordinate:  " + element_Y_coordinate + ")");

                    if (Math.abs(element_Y_coordinate - position_Y_coordinate) < separation) {
                        isEmptyPosition = false;
                    }

                    logger.debug("isEmptyPosition:" + isEmptyPosition);

                }
            }
        }

        return isEmptyPosition;
    }

    /***
     * Insert element in a correct position of an ordered list (in function of values of maps of min and max Y coordinates)
     * @param orderedList ordered List
     * @param element element to be inserted
     * @param map_min_Y_Source_Coordinate Map with min Y coordinate of the sources of the element
     * @param map_max_Y_Source_Coordinate Map with max Y coordinate of the sources of the element
     * @return ordered list with the element inserted on correct position
     */
    public static List<String> insertOrderedList(List<String> orderedList, String element, Map<String, Integer> map_min_Y_Source_Coordinate, Map<String, Integer> map_max_Y_Source_Coordinate) {
        if (orderedList.isEmpty()) {
            orderedList.add(element);
        } else {

            boolean elementAdded = false;
            for (int i=0; i<orderedList.size(); i++) {
                String elementListName = orderedList.get(i);
                if (map_min_Y_Source_Coordinate.get(element) < map_min_Y_Source_Coordinate.get(elementListName)) {
                    orderedList.add(i, element);
                    elementAdded = true;
                    break;
                } else if (map_min_Y_Source_Coordinate.get(element) == map_min_Y_Source_Coordinate.get(elementListName)) {
                    if (map_max_Y_Source_Coordinate.get(element) < map_max_Y_Source_Coordinate.get(elementListName)) {
                        orderedList.add(i, element);
                        elementAdded = true;
                        break;
                    }
                }
            }

            if (!elementAdded) {
                orderedList.add(element);
            }
        }

        return orderedList;
    }

    /**
     * Get the elements that belong to a set or the elements than belong to intersection between 2 sets
     * @param mapSharedChannelsSourcesRelation map with the sets of elements
     * @param channel1Name name of the set 1
     * @param channel2Name name of the set 2
     * @param onlyInSet1 true if we want the elements belong to set 1, false if we want the elements that belong to intersection between set 1 and set 3
     * @return list with the elements that belong to a set or the elements than belong to intersection between 2 sets
     */
    private static List<String> getElementsBelongToSet(Map<String, List<String>> mapSharedChannelsSourcesRelation, String channel1Name, String channel2Name, boolean onlyInSet1) {

        List<String> elementsBelongToSet = new ArrayList<>();
        if (mapSharedChannelsSourcesRelation != null && channel1Name != null && !channel1Name.isEmpty()) {

            if (onlyInSet1) {
                //Return elements that only belong to set1
                Collection<String> channel1Elements = mapSharedChannelsSourcesRelation.get(channel1Name);
                for (String channelName : mapSharedChannelsSourcesRelation.keySet()) {
                    if (!channelName.equals(channel1Name)) {
                        Collection<String> channelElements = mapSharedChannelsSourcesRelation.get(channelName);
                        channel1Elements = CollectionUtils.subtract(channel1Elements, channelElements);
                    }
                }

                elementsBelongToSet = new ArrayList<>(channel1Elements);
            } else {

                if (channel2Name != null && !channel2Name.isEmpty()) {
                    Collection<String> channel1Elements = mapSharedChannelsSourcesRelation.get(channel1Name);
                    Collection<String> channel2Elements = mapSharedChannelsSourcesRelation.get(channel2Name);

                    elementsBelongToSet = new ArrayList<>(CollectionUtils.intersection(channel1Elements, channel2Elements));
                }
            }
        }

        return elementsBelongToSet;
    }


    /**
     * Get cartesian product between elements or several lists
     * @param lists list with the lists of elements to get the cartesian product
     * @return cartesian product between elements or several lists
     */
    private static <T> List<List<T>> getCartesianProduct(List<List<T>> lists) {
        List<List<T>> resultLists = new ArrayList<>();
        if (lists.size() == 0) {
            resultLists.add(new ArrayList<T>());
            return resultLists;
        } else {
            List<T> firstList = lists.get(0);
            List<List<T>> remainingLists = getCartesianProduct(lists.subList(1, lists.size()));
            for (T condition : firstList) {
                for (List<T> remainingList : remainingLists) {
                    ArrayList<T> resultList = new ArrayList<>();
                    resultList.add(condition);
                    resultList.addAll(remainingList);
                    resultLists.add(resultList);
                }
            }
        }
        return resultLists;
    }

    /**
     * Get permutations of sources based on cartesian products of sources of each channel
     * @param mapSharedChannelsSourcesRelation map with relation between channels and sources
     * @return permutations of sources based on cartesian products of sources of each channel
     */
    private static Collection<List<String>> getSourcesPartialNumberPermutations(Map<String, List<String>> mapSharedChannelsSourcesRelation) {

        Collection<List<String>> sourcesPartialPermutations = new ArrayList<>();

        if (mapSharedChannelsSourcesRelation != null) {
            Collection<List<String>> channelsPermutations =
                    CollectionUtils.permutations(mapSharedChannelsSourcesRelation.keySet());
            if (logger.isDebugEnabled()) {
                logger.debug("Generating channels permutations of size: " + mapSharedChannelsSourcesRelation.keySet().size());
            }

            for (List<String> channelPermutation : channelsPermutations) {

                if (sourcesPartialPermutations.size() > FlumeConfiguratorConstants.MAX_NUMBER_CARTESIAN_PRODUCT_NUMBER) {
                    //The number of partial permutations is two big
                    if (logger.isWarnEnabled()) {
                        logger.warn("The number of partial permutations obtained until now is two big. Don't continue getting new permutations. Number of partial permutations : " + sourcesPartialPermutations.size() + " Limit cartesianProductNumber: " + FlumeConfiguratorConstants.MAX_NUMBER_CARTESIAN_PRODUCT_NUMBER);
                    }
                    break;
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("The number of partial permutations obtained until now: " + sourcesPartialPermutations.size());
                }


                List<List<String>> channelSourcesLists = new ArrayList<>();
                List<Collection<List<String>>> channelSourcesPermutationLists = new ArrayList<>();
                List<List<Integer>> permutationsSizesLists = new ArrayList<>();


                //Process a permutation. Build a list of list of sources
                for (int i = 0; i < channelPermutation.size(); i++) {
                    String channelName = channelPermutation.get(i);
                    //Get elements that only belong to this channel
                    List<String> channelNameExclusiveElements = getElementsBelongToSet(mapSharedChannelsSourcesRelation, channelName, null, true);

                    channelSourcesLists.add(channelNameExclusiveElements);
                    for (int j = i + 1; j < channelPermutation.size(); j++) {
                        String referenceChannelName = channelPermutation.get(j);
                        List<String> channelNameSharedElements = getElementsBelongToSet(mapSharedChannelsSourcesRelation, channelName, referenceChannelName, false);

                        //Add shared elements (only if they don't belong to list)
                        List<String> channelNameSharedElementsList = new ArrayList<>();
                        for (String channelNameSharedElement : channelNameSharedElements) {
                            boolean existSource = false;
                            for (List<String> predecessorList : channelSourcesLists) {
                                if (predecessorList.contains(channelNameSharedElement)) {
                                    existSource = true;
                                }
                            }
                            if (!existSource) {
                                channelNameSharedElementsList.add(channelNameSharedElement);
                            }
                        }
                        channelSourcesLists.add(channelNameSharedElementsList);
                    }
                }

                //Calculate product cartesian number
                long cartesianProductNumber = 1;
                for (List<String> channelSourcesList : channelSourcesLists) {
                    cartesianProductNumber = cartesianProductNumber * CombinatoricsUtils.factorial(channelSourcesList.size());
                }

                if (cartesianProductNumber > FlumeConfiguratorConstants.MAX_NUMBER_CARTESIAN_PRODUCT_NUMBER) {
                    //The cartesian product is too big
                    if (logger.isWarnEnabled()) {
                        logger.warn("There cartesian product will not be calculated (too many elements) - cartesianProductNumber: " + cartesianProductNumber + " Limit cartesianProductNumber: " + FlumeConfiguratorConstants.MAX_NUMBER_CARTESIAN_PRODUCT_NUMBER);
                    }

                    continue;
                }

                //Generate permutations of the lists of the list
                Collection<List<String>> permutationsList;
                for (List<String> channelSourcesList : channelSourcesLists) {
                    if (channelSourcesList.size() > 0) {
                        permutationsList = CollectionUtils.permutations(channelSourcesList);
                    } else {
                        permutationsList = Collections.EMPTY_LIST;
                    }
                    channelSourcesPermutationLists.add(permutationsList);
                    List<Integer> permutationsSizesList = generateSizesList(permutationsList);
                    permutationsSizesLists.add(permutationsSizesList);
                }

                //Cartesian product
                List<List<Integer>> cartesianProductPermutationSizesLists = getCartesianProduct(permutationsSizesLists);

                for (List<Integer> cartesianProductPermutationSizesElement : cartesianProductPermutationSizesLists) {
                    List<String> totalSourcesList = new ArrayList<>();
                    for(int i=0; i<cartesianProductPermutationSizesElement.size(); i++) {
                        //Get the permutation on index
                        Integer indexPermutation = cartesianProductPermutationSizesElement.get(i);
                        if (indexPermutation > 0) {
                            //Index base 1
                            indexPermutation = indexPermutation - 1;

                            //Get the permutation (list (i) - permutation (indexPermutation))
                            List<String> partialSourcesList = CollectionUtils.get(channelSourcesPermutationLists.get(i), indexPermutation);

                            totalSourcesList.addAll(partialSourcesList);
                        }
                    }
                    sourcesPartialPermutations.add(totalSourcesList);
                }
            }
        }

        return sourcesPartialPermutations;

    }


    /**
     * Generate a list with the sizes of the lists of the collection
     * @param collection Collection with the lists
     * @return list with the sizes of the lists of the collection
     */
    private static List<Integer> generateSizesList(Collection<?> collection) {

        List<Integer> sizesList = new ArrayList<>();

        if (collection != null) {
            int size = collection.size();

            if (size == 0) {
                sizesList.add(0);
            } else {
                for (int i=1; i<=size; i++) {
                    sizesList.add(i);
                }
            }
        }

        return sizesList;
    }


    /**
     * Get groups of shared sources than share any channel with other source(s) of the group. The groups
     * @param mapSharedChannelsSourcesRelation
     * @return
     */
    public static Map<String, List<String>> getSharedSourcesGroups(Map<String, List<String>> mapSharedChannelsSourcesRelation) {

        Map<String, List<String>> sharedSourcesGroups = new HashedMap<>();
        Map<String, List<String>> sharedChannelsGroups = new HashedMap<>();
        List<Set> listSharedSourcesChannels;

        if (mapSharedChannelsSourcesRelation != null) {

            Set<String> keySet = mapSharedChannelsSourcesRelation.keySet();

            String[] keySetArray = keySet.toArray(new String[keySet.size()]);

            for (int i=0; i<keySetArray.length; i++) {

                for (int j=0; j<keySetArray.length; j++) {

                    String channel_i_name = keySetArray[i];
                    String channel_j_name = keySetArray[j];

                    if (!sharedChannelsGroups.containsKey(channel_i_name)) {
                        sharedChannelsGroups.put(channel_i_name, new ArrayList<>());
                    }

                    List<String> sourcesChannel_i = mapSharedChannelsSourcesRelation.get(keySetArray[i]);
                    List<String> sourcesChannel_j = mapSharedChannelsSourcesRelation.get(keySetArray[j]);

                    if (CollectionUtils.containsAny(sourcesChannel_i, sourcesChannel_j)) {
                        sharedChannelsGroups.get(channel_i_name).add(channel_j_name);
                    }
                }
            }

            //Create graph
            IGraph channelsRelationGraph = GraphFactory.createDefaultDirectedGraph("jgrapht");

            //Add vertex
            for (String channelName : sharedChannelsGroups.keySet()) {
                FlumeTopology channelFT = new FlumeTopology();
                channelFT.setId(channelName);
                channelsRelationGraph.addGraphVertex(channelFT);
            }

            //Add edges
            for (String channelName : sharedChannelsGroups.keySet()) {
                FlumeTopology nodeSource = channelsRelationGraph.getVertex(channelName, false);
                List<String> sharedChannels = sharedChannelsGroups.get(channelName);

                for(String sharedChannelName : sharedChannels) {
                    FlumeTopology nodeTarget = channelsRelationGraph.getVertex(sharedChannelName, false);
                    channelsRelationGraph.addGraphEdge(nodeSource, nodeTarget);
                }
            }

            //Get subgroups
            listSharedSourcesChannels = channelsRelationGraph.getConnectedSets();

            int i=1;

            //Create shared sources groups
            for (Set sharedSourcesChannel : listSharedSourcesChannels) {
                List<String> sourcesList = new ArrayList<>();
                for (Object sharedChannelElement : sharedSourcesChannel) {
                    FlumeTopology sharedChannelElementFT = (FlumeTopology) sharedChannelElement;
                    String channelElement = sharedChannelElementFT.getId();
                    List<String> sourcesChannelList = mapSharedChannelsSourcesRelation.get(channelElement);
                    sourcesList.addAll(sourcesChannelList);
                }
                //Remove duplicates
                List<String> sourcesListWithoutDuplicates = new ArrayList<>(new HashSet<>(sourcesList));
                //Put in map
                sharedSourcesGroups.put(FlumeConfiguratorConstants.SOURCES_GROUP_PREFIX_NAME+i, sourcesListWithoutDuplicates);
                i++;
            }

        }
        return sharedSourcesGroups;
    }


    /**
     * Get number of sources of a list referenced by a map
     * @param completeSourcesList list of sources
     * @param mapSharedChannelsSourcesRelation map with relation between channels and sources
     * @return number of sources of a list referenced by a map
     */
    public static int getSourcesNumberReferencedByMap(List<String> completeSourcesList, Map<String,List<String>> mapSharedChannelsSourcesRelation) {

        List<String> sourcesReferencedByMap = new ArrayList<>();
        int sourcesNumberReferencedByMap = 0;

        if (completeSourcesList != null && mapSharedChannelsSourcesRelation != null) {
            //Detect sources not referencied by map
            for (String sourceName : completeSourcesList) {
                for (String channelName : mapSharedChannelsSourcesRelation.keySet()) {
                    List<String> channelSourcesList = mapSharedChannelsSourcesRelation.get(channelName);
                    if (channelSourcesList.contains(sourceName)) {
                        if (!sourcesReferencedByMap.contains(sourceName)) {
                            sourcesReferencedByMap.add(sourceName);
                            sourcesNumberReferencedByMap++;
                        }
                    }
                }
            }
        }

        return sourcesNumberReferencedByMap;
    }


    /**
     * Add sources from a list to all permutations from the collection
     * @param sourcesPermutations Collection with the permutations of sources
     * @param sourcesNotReferencedByMap list of sources to add to the permutations
     * @return Collection of permutations with the sources added
     */
    private static Collection<List<String>> addSourcesNotReferencedByMapToPermutation(Collection<List<String>> sourcesPermutations,
                                                                         List<String> sourcesNotReferencedByMap) {

        Collection<List<String>> enrichedSourcesPermutations = new ArrayList<>();
        List<String> sourcesNotInMapWithSourcePermutation;

        if (sourcesPermutations != null && sourcesNotReferencedByMap != null) {


            Iterator<List<String>> itSourcesPermutations = sourcesPermutations.iterator();

            while (itSourcesPermutations.hasNext()) {
                List<String> sourcePermutation = itSourcesPermutations.next();

                //Add permutation from sources not in map + permutation of sources in map
                sourcesNotInMapWithSourcePermutation = new ArrayList<>(sourcesNotReferencedByMap);
                sourcesNotInMapWithSourcePermutation.addAll(sourcePermutation);
                enrichedSourcesPermutations.add(sourcesNotInMapWithSourcePermutation);
            }
        }

        return enrichedSourcesPermutations;
    }

    /**
     * Detect if exists a relation (ancestor or descendant) between two elements
     * @param igraph Graph of the agent
     * @param flumeTopologyList list of Flume Topology elements
     * @param element1_Name name of the first element
     * @param element2_Name name of the second element
     * @return true if exists a relation (ancestor or descendant) between elements, false otherwise
     */
    private static boolean existRelation(IGraph igraph, List<FlumeTopology> flumeTopologyList, String element1_Name, String element2_Name) {

        boolean existRelation = false;

        if (igraph != null && flumeTopologyList != null && element1_Name != null && !element1_Name.isEmpty() && element2_Name != null && !element2_Name.isEmpty()) {


            String element1_ID = FlumeConfiguratorTopologyUtils.getFlumeTopologyId(flumeTopologyList, element1_Name);
            String element2_ID = FlumeConfiguratorTopologyUtils.getFlumeTopologyId(flumeTopologyList, element2_Name);

            FlumeTopology element1_FlumeTopology = igraph.getVertex(element1_ID, true);
            FlumeTopology element2_FlumeTopology = igraph.getVertex(element2_ID, true);

            Set<FlumeTopology> element1_Descendants = igraph.getVertexDescendants(element1_FlumeTopology);
            for (FlumeTopology element1_Descendant : element1_Descendants) {
                if (element1_Descendant.equals(element2_FlumeTopology)) {
                    existRelation = true;
                }
            }

            if (!existRelation) {
                Set<FlumeTopology> element1_Ancestors = igraph.getVertexAncestors(element1_FlumeTopology);
                for (FlumeTopology element1_Ancestor : element1_Ancestors) {
                    if (element1_Ancestor.equals(element2_FlumeTopology)) {
                        existRelation = true;
                    }
                }
            }
        }

        return existRelation;

    }

    /**
     * Get degree of grouping of the sources of the channels from a permutation. The degree will be
     * 0 when all sources of a channel are together. The number of sources than don't belong to
     * the channel between the first source than belong to the channel and last one will be the
     * channel grouping degree.
     * @param igraph graph of the agent
     * @param flumeTopologyList list of Flume Topology elements
     * @param sourcesPermutation permutation of the sources
     * @param sharedChannelsSourcesRelationMap map with relation between sources and channels
     * @return the degree of grouping of the sources of the channels from a permutation
     */
    private static int getPermutationChannelGroupingDegree(IGraph igraph, List<FlumeTopology> flumeTopologyList, List<String> sourcesPermutation, Map<String, List<String>> sharedChannelsSourcesRelationMap) {

        int permutationGroupingDegree = 0;

        if (igraph != null && flumeTopologyList != null && sourcesPermutation != null && sharedChannelsSourcesRelationMap != null) {
            for (String channelName : sharedChannelsSourcesRelationMap.keySet()) {
                //Get permutation grouping degree for the channel
                boolean firstElementFound = false;
                int noChannelBlockSourcesNumber = 0;
                int noChannelSourcesNumber = 0;
                for (String sourceName : sourcesPermutation) {
                    boolean existRelation = existRelation(igraph, flumeTopologyList, sourceName, channelName);
                    if (existRelation) {
                        if (!firstElementFound) {
                            firstElementFound = true;
                        } else {
                            noChannelSourcesNumber = noChannelSourcesNumber + noChannelBlockSourcesNumber;
                            noChannelBlockSourcesNumber = 0;
                        }
                    } else {
                        if (firstElementFound) {
                            noChannelBlockSourcesNumber++;
                        }
                    }
                }

                permutationGroupingDegree = permutationGroupingDegree + noChannelSourcesNumber;

            }
        }

        return permutationGroupingDegree * FlumeConfiguratorConstants.FACTOR_CHANNEL_GROUPING_DEGREE;
    }


    /**
     * Get degree of grouping of the sources of the sinkgroups from a permutation. The degree will be
     * 0 when all sources of a sinkgroup are together. The number of sources than don't belong to
     * the sinkgroup between the first source than belong to the sinkgroup and last one will be the
     * sinkgroup grouping degree.
     * @param igraph igraph graph of the agent
     * @param flumeTopologyList list of Flume Topology elements
     * @param sourcesPermutation permutation of the sources
     * @param mapSharedSinkgroupsSourcesRelation map with relation between sources and sinkgroups
     * @return the degree of grouping of the sources of the sinkgroups from a permutation
     */
    private static int getPermutationSinkgroupGroupingDegree(IGraph igraph, List<FlumeTopology> flumeTopologyList, List<String> sourcesPermutation, Map<String, List<String>> mapSharedSinkgroupsSourcesRelation) {

        int permutationGroupingDegree = 0;

        for (String sinkgroupName : mapSharedSinkgroupsSourcesRelation.keySet()) {
            //Get permutation grouping degree for the channel
            boolean firstElementFound = false;
            int noSinkgroupBlockSourcesNumber = 0;
            int noSinkgroupSourcesNumber = 0;
            for (String sourceName : sourcesPermutation) {
                boolean existRelation = mapSharedSinkgroupsSourcesRelation.get(sinkgroupName).contains(sourceName);
                if (existRelation) {
                    if (!firstElementFound) {
                        firstElementFound = true;
                    } else {
                        noSinkgroupSourcesNumber = noSinkgroupSourcesNumber + noSinkgroupBlockSourcesNumber;
                        noSinkgroupBlockSourcesNumber = 0;
                    }
                } else {
                    if (firstElementFound) {
                        noSinkgroupBlockSourcesNumber++;
                    }
                }
            }

            permutationGroupingDegree = permutationGroupingDegree + noSinkgroupSourcesNumber;

        }

        return permutationGroupingDegree * FlumeConfiguratorConstants.FACTOR_SINKGROUP_GROUPING_DEGREE;
    }


    /**
     * Get degree of outsiding of the sources from a permutation. The degree will be
     * 0 when the two sources with maximum number of independent channels and interceptors are first and last source of the permutation. The distance
     * between the position of the two sources with maximum number of independent channels/indicators and the first or last position of the permutation
     * will be the degree of outsiding.
     * @param sourcesPermutation permutation of the sources
     * @param sourcesIndependentChannelsRelationMap map with relation between sources and their independent channels
     * @param sourcesInterceptorsRelationMap map with relation between sources and their interceptors
     * @return the degree of outsiding of the sources from a permutation
     */
    private static int getPermutationOutsidingDegree(List<String> sourcesPermutation, Map<String,List<String>> sourcesIndependentChannelsRelationMap, Map<String,List<String>> sourcesInterceptorsRelationMap) {

        int permutationOutsidingDegree = 0;

        if (sourcesPermutation != null && sourcesIndependentChannelsRelationMap != null && sourcesInterceptorsRelationMap != null ) {

            //Get the 2 best options
            String firstSource = null;
            String secondSource = null;
            int firstSourceIndependentChannelsNumber = 0;
            int secondSourceIndependentChannelsNumber = 0;
            int firstSourceInterceptorsNumber = 0;
            int secondSourceInterceptorsNumber = 0;

            //boolean isFirstElement = true;
            int elementNumber = 1;
            for (String sourceName : sourcesIndependentChannelsRelationMap.keySet()) {

                if (sourcesPermutation.contains(sourceName)) {
                    int sourceIndependentChannelsNumber = sourcesIndependentChannelsRelationMap.get(sourceName).size();
                    int sourceInterceptorsNumber = sourcesInterceptorsRelationMap.get(sourceName).size();

                    if (elementNumber == 1) {
                        firstSource = sourceName;
                        firstSourceIndependentChannelsNumber = sourceIndependentChannelsNumber;
                        firstSourceInterceptorsNumber = sourceInterceptorsNumber;
                    } else if (elementNumber == 2) {
                        secondSource = sourceName;
                        secondSourceIndependentChannelsNumber = sourceIndependentChannelsNumber;
                        secondSourceInterceptorsNumber = sourceInterceptorsNumber;
                    }

                    if (sourceIndependentChannelsNumber > firstSourceIndependentChannelsNumber) {
                        secondSource = firstSource;
                        secondSourceIndependentChannelsNumber = firstSourceIndependentChannelsNumber;
                        firstSource = sourceName;
                        firstSourceIndependentChannelsNumber = sourceIndependentChannelsNumber;
                    } else if (sourceIndependentChannelsNumber == firstSourceIndependentChannelsNumber) {
                        if (sourceInterceptorsNumber >= firstSourceInterceptorsNumber) {
                            secondSource = firstSource;
                            secondSourceIndependentChannelsNumber = firstSourceIndependentChannelsNumber;
                            secondSourceInterceptorsNumber = firstSourceInterceptorsNumber;
                            firstSource = sourceName;
                            firstSourceIndependentChannelsNumber = sourceIndependentChannelsNumber;
                            firstSourceInterceptorsNumber = sourceInterceptorsNumber;

                        } else if (sourceInterceptorsNumber >= secondSourceInterceptorsNumber) {
                            secondSource = sourceName;
                            secondSourceIndependentChannelsNumber = sourceIndependentChannelsNumber;
                            secondSourceInterceptorsNumber = sourceInterceptorsNumber;
                        }
                    } else if (sourceIndependentChannelsNumber > secondSourceIndependentChannelsNumber) {
                        secondSource = sourceName;
                        secondSourceIndependentChannelsNumber = sourceIndependentChannelsNumber;
                        secondSourceInterceptorsNumber = sourceInterceptorsNumber;
                    } else if (sourceIndependentChannelsNumber == secondSourceIndependentChannelsNumber) {
                        if (sourceInterceptorsNumber >= secondSourceInterceptorsNumber) {
                            secondSource = sourceName;
                            secondSourceIndependentChannelsNumber = sourceIndependentChannelsNumber;
                            secondSourceInterceptorsNumber = sourceInterceptorsNumber;
                        }
                    }

                    elementNumber++;
                }
            }

            //Calculate distance between position and idoneal position of two best elements
            int distanceFirstSource;
            int distanceSecondSource;
            int posFirstSource = sourcesPermutation.indexOf(firstSource);
            int posSecondSource = sourcesPermutation.indexOf(secondSource);


            String source_permutation_begin = sourcesPermutation.get(0);
            String source_permutation_end = sourcesPermutation.get(sourcesPermutation.size()-1);

            int source_permutation_begin_IndependentChannelsNumber = sourcesIndependentChannelsRelationMap.get(source_permutation_begin).size();
            int source_permutation_begin_InterceptorsNumber = sourcesInterceptorsRelationMap.get(source_permutation_begin).size();

            int source_permutation_end_IndependentChannelsNumber = sourcesIndependentChannelsRelationMap.get(source_permutation_end).size();
            int source_permutation_end_InterceptorsNumber = sourcesInterceptorsRelationMap.get(source_permutation_end).size();


            if (source_permutation_begin_IndependentChannelsNumber == firstSourceIndependentChannelsNumber && source_permutation_begin_InterceptorsNumber == firstSourceInterceptorsNumber) {
                //Source at index 0 is as good as firstSource
                posFirstSource = 0;

                if (source_permutation_end_IndependentChannelsNumber == secondSourceIndependentChannelsNumber && source_permutation_end_InterceptorsNumber == secondSourceInterceptorsNumber) {
                    posSecondSource = sourcesPermutation.size()-1;
                }

            } else if (source_permutation_end_IndependentChannelsNumber == firstSourceIndependentChannelsNumber && source_permutation_end_InterceptorsNumber == firstSourceInterceptorsNumber) {
                //Source at index n-1 is as good as firstSource
                posFirstSource = sourcesPermutation.size()-1;

                if (source_permutation_begin_IndependentChannelsNumber == secondSourceIndependentChannelsNumber && source_permutation_begin_InterceptorsNumber == secondSourceInterceptorsNumber) {
                    posSecondSource = 0;
                }
            }


            int distanceToEndFirstSource = Math.abs(sourcesPermutation.size() - 1 - posFirstSource);

            if (distanceToEndFirstSource <= posFirstSource) {
                distanceFirstSource = distanceToEndFirstSource;
                distanceSecondSource = posSecondSource;
            } else {
                distanceFirstSource = posFirstSource;
                distanceSecondSource = Math.abs(sourcesPermutation.size() - 1 - posSecondSource);
            }

            permutationOutsidingDegree = distanceFirstSource + distanceSecondSource;

        }


        return permutationOutsidingDegree * FlumeConfiguratorConstants.FACTOR_OUTSIDIND_DEGREE;
    }



    public static List<String> getOptimalSharedSourcesPermutation(IGraph igraph, List<FlumeTopology> flumeTopologyList,
                                                            Collection<List<String>> sourcesPermutations,
                                                            Map<String, List<String>> sharedChannelsSourcesRelationMap,
                                                            Map<String,List<String>> sourcesIndependentChannelsRelationMap,
                                                            Map<String,List<String>> sourcesInterceptorsRelationMap,
                                                            Map<String,List<String>> sharedSinkGroupsSourcesRelationsMap,
                                                            List<Integer> alternativeOptimizationPermutationAgentList,
                                                            int agentIndex) {

        List<String> optimalSourcesPermutation = null;

        if (alternativeOptimizationPermutationAgentList == null || alternativeOptimizationPermutationAgentList.size() == 0 || alternativeOptimizationPermutationAgentList.size() <= agentIndex) {
            //The alternative number 1 is choosen
            optimalSourcesPermutation = getOptimalSharedSourcesPermutationWithAlternatives(igraph, flumeTopologyList, sourcesPermutations, sharedChannelsSourcesRelationMap, sourcesIndependentChannelsRelationMap,
                    sourcesInterceptorsRelationMap, sharedSinkGroupsSourcesRelationsMap, 1);
        } else {
            //The alternative indicated by list for the agent
            int alternativeOptimizationPermutationAgent = alternativeOptimizationPermutationAgentList.get(agentIndex);
            optimalSourcesPermutation = getOptimalSharedSourcesPermutationWithAlternatives(igraph, flumeTopologyList, sourcesPermutations, sharedChannelsSourcesRelationMap, sourcesIndependentChannelsRelationMap,
                    sourcesInterceptorsRelationMap, sharedSinkGroupsSourcesRelationsMap, alternativeOptimizationPermutationAgent);
        }

        return optimalSourcesPermutation;

    }



    public static List<String> getOptimalSharedSourcesPermutationWithAlternatives(IGraph igraph, List<FlumeTopology> flumeTopologyList,
                                                                  Collection<List<String>> sourcesPermutations,
                                                                  Map<String, List<String>> sharedChannelsSourcesRelationMap,
                                                                  Map<String,List<String>> sourcesIndependentChannelsRelationMap,
                                                                  Map<String,List<String>> sourcesInterceptorsRelationMap,
                                                                  Map<String,List<String>> sharedSinkGroupsSourcesRelationsMap,
                                                                  int alternativesNumber) {

        List<String> optimalSourcesPermutation = null;

        if (igraph != null && flumeTopologyList != null && sourcesPermutations != null && sharedChannelsSourcesRelationMap != null && sourcesIndependentChannelsRelationMap != null && sourcesInterceptorsRelationMap != null) {

            if (logger.isDebugEnabled()) {
                logger.debug("Calculating shared sources optimal permutation. Number of alternative: " + alternativesNumber);
            }
            if (sourcesPermutations.size() == 1) {
                optimalSourcesPermutation = CollectionUtils.get(sourcesPermutations, 0);

            } else if (sourcesPermutations.size() > 1) {

                int minPermutationOptimalDegreeValue = Integer.MAX_VALUE;
                int sourcesPermutationChannelGroupingDegree = 0;
                int sourcesPermutationOutsidingDegree = 0;
                int sourcesPermutationSinkgroupGroupingDegree = 0;
                int sourcesPermutationOptimalDegree= 0;

                int optimalSourcesPermutationIterNumber = 1;
                int iterNumber = 1;

                //Creation of a queue
                LinkedList<List<String>> alternativesQueue = new LinkedList<>();
                LinkedList<List<String>> alternativesZeroDegreeQueue = new LinkedList<>();


                Iterator<List<String>> itSourcesPermutations = sourcesPermutations.iterator();


               // while (itSourcesPermutations.hasNext() && minPermutationOptimalDegreeValue > 0) {
                while (itSourcesPermutations.hasNext() && alternativesZeroDegreeQueue.size() < alternativesNumber) {

                    if (iterNumber % 100000 == 0) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Permutations processed number: (" + iterNumber + " / " + sourcesPermutations.size() + ")");
                        }
                    }
                    List<String> sourcesPermutation = itSourcesPermutations.next();

                    sourcesPermutationChannelGroupingDegree = getPermutationChannelGroupingDegree(igraph, flumeTopologyList, sourcesPermutation, sharedChannelsSourcesRelationMap);
                    sourcesPermutationOutsidingDegree = getPermutationOutsidingDegree(sourcesPermutation, sourcesIndependentChannelsRelationMap, sourcesInterceptorsRelationMap);
                    sourcesPermutationSinkgroupGroupingDegree = getPermutationSinkgroupGroupingDegree(igraph, flumeTopologyList, sourcesPermutation, sharedSinkGroupsSourcesRelationsMap);

                    sourcesPermutationOptimalDegree = sourcesPermutationChannelGroupingDegree + sourcesPermutationOutsidingDegree + sourcesPermutationSinkgroupGroupingDegree;

                    if (sourcesPermutationOptimalDegree < minPermutationOptimalDegreeValue || sourcesPermutationOptimalDegree == 0) {

                        if (alternativesQueue.size() < alternativesNumber) {
                            alternativesQueue.add(sourcesPermutation);
                        } else {
                            alternativesQueue.remove();
                            alternativesQueue.add(sourcesPermutation);
                        }

                        if (sourcesPermutationOptimalDegree == 0) {
                            alternativesZeroDegreeQueue.add(sourcesPermutation);
                        }

                        if (logger.isDebugEnabled()) {
                            logger.debug("New optimal sources permutation found. Optimization function value: " +  sourcesPermutationOptimalDegree + ". Iteration number: " + iterNumber + " / " + sourcesPermutations.size());
                        }
                        minPermutationOptimalDegreeValue = sourcesPermutationOptimalDegree;
                        optimalSourcesPermutationIterNumber = iterNumber;
                    }

                    iterNumber++;

                }

                if (logger.isDebugEnabled()) {
                    iterNumber--;
                    logger.debug("Optimal sources permutation found at iteration number " + optimalSourcesPermutationIterNumber + " with " + iterNumber + " permutations processed of " + sourcesPermutations.size());
                    logger.debug("sourcesPermutationOptimalDegree: " +  sourcesPermutationOptimalDegree + " (sourcesPermutationChannelGroupingDegree: " + sourcesPermutationChannelGroupingDegree + " / sourcesPermutationOutsidingDegree: " + sourcesPermutationOutsidingDegree + " sourcesPermutationSinkgroupGroupingDegree: " + sourcesPermutationSinkgroupGroupingDegree + ")");
                }

                //Log alternatives
                if (logger.isDebugEnabled()) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i <= alternativesQueue.size(); i++) {
                       sb.setLength(0);
                        List<String> alternative = alternativesQueue.get(i-1);
                        sb.append("Alternative number: ").append(i).append(" Sources");
                        sb.append(Arrays.toString(alternative.toArray()));
                        logger.debug(sb.toString());

                    }
                }

                ////Get the chosen optimal alternative
                if (alternativesNumber > alternativesZeroDegreeQueue.size()) {
                    int index = 0;
                    optimalSourcesPermutation = alternativesQueue.get(index);
                    int alternativeIndex = index + 1;
                    if (logger.isDebugEnabled()) {
                        logger.debug("optimalSourcesPermutation (alternative " + alternativeIndex + ") Sources: " + Arrays.toString(optimalSourcesPermutation.toArray()));
                    }
                } else {
                    int index = alternativesQueue.size() - 1;
                    optimalSourcesPermutation = alternativesQueue.get(index);
                    int alternativeIndex = index + 1;
                    if (logger.isDebugEnabled()) {
                        logger.debug("optimalSourcesPermutation (alternative " + alternativeIndex + ") Sources: " + Arrays.toString(optimalSourcesPermutation.toArray()));
                    }
                }


            }
        }

        return optimalSourcesPermutation;
    }





    public static Map<String, List<String>> getMapSharedSinkGroupsSourcesRelation(IGraph agentGraph, List<FlumeTopology> flumeTopologyList, List<String> sourcesList) {

        Map<String, List<String>> mapSharedSinkGroupsSourcesRelation = new HashedMap<>();
        Map<String, List<String>> sourcesSharedSinkGroupsRelationsMap = new HashMap<>();
        List<String> sharedSinkGroupSources = new ArrayList<>();

        if (agentGraph != null && flumeTopologyList != null && sourcesList != null) {
            //Create relation between sources and shared sinkgroups
            for (String sourceName : sourcesList) {

                String sourceID = FlumeConfiguratorTopologyUtils.getFlumeTopologyId(flumeTopologyList, sourceName);
                FlumeTopology flumeTopologySourceElement = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyList, sourceID);

                if (flumeTopologySourceElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE)) {

                    sourcesSharedSinkGroupsRelationsMap.put(sourceName, new ArrayList<>());

                    //Get descendants of source
                    Set<FlumeTopology> sourceChildren = agentGraph.getVertexDescendants(flumeTopologySourceElement);
                    Iterator<FlumeTopology> itSourceChildren = sourceChildren.iterator();

                    while (itSourceChildren.hasNext()) {
                        FlumeTopology sourceChild = itSourceChildren.next();

                        if (sourceChild.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP)) {

                            String sinkGroupName = sourceChild.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                            //Get all sources from sink group
                            Set<FlumeTopology> sinkGroupAncestorsList = agentGraph.getVertexAncestors(sourceChild);

                            for (FlumeTopology sinkGroupAncestor : sinkGroupAncestorsList) {

                                if (sinkGroupAncestor.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE)) {
                                    String sinkGroupAncestorSourceName = sinkGroupAncestor.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                                    if (!sinkGroupName.equals(sinkGroupAncestorSourceName)) {
                                        logger.debug("The source " + sourceName + " has a sink group with a different source: " + sinkGroupAncestorSourceName);
                                        //The channel has a different source
                                        //Add the shared sinkGroup
                                        sourcesSharedSinkGroupsRelationsMap.get(sourceName).add(sinkGroupName);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //Get list of fully independent sources and independent sources that share sinkgroup
            for (String sourceName : sourcesSharedSinkGroupsRelationsMap.keySet()) {

                List<String> sharedSinkGroupList = sourcesSharedSinkGroupsRelationsMap.get(sourceName);
                if (sharedSinkGroupList.size() > 0) {
                    //Source share sinkGroups with other sources
                    sharedSinkGroupSources.add(sourceName);
                }
            }

            mapSharedSinkGroupsSourcesRelation = getMapSharedSinkGroupsSourcesRelation(sharedSinkGroupSources, sourcesSharedSinkGroupsRelationsMap);

        }

        return mapSharedSinkGroupsSourcesRelation;

    }


}
