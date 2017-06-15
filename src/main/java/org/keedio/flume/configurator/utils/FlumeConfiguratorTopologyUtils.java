package org.keedio.flume.configurator.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
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

        boolean isTreeCompliant = true;
        Map<String, FlumeTopology> mapTargetConnecion = new HashMap<>();

        int connectionsNumber = listFlumeTopology.size(); // arches number

        //System.out.println("Nodes number:" + nodesNumber);
        //System.out.println("Sources number: " + sourcesNumber);
        //System.out.println("Arches number: " + connectionsNumber);

        if (withAgentNodes) {
            if (connectionsNumber != (nodesNumber - agentsNumber)) {
                return false;
            }
        } else {
            if (connectionsNumber != (nodesNumber - sourcesNumber)) {
                return false;
            }
        }

        if ((listFlumeTopology != null) && (listFlumeTopology.size() > 0)) {

            for (FlumeTopology flumeTopology : listFlumeTopology) {
                String targetConnection = flumeTopology.getTargetConnection();

                FlumeTopology elementConnection = mapTargetConnecion.get(targetConnection);
                if (elementConnection != null) {
                    //There is another element with the same target (a target has more than one source, so is not a tree)
                    return false;
                } else {
                    mapTargetConnecion.put(targetConnection, elementConnection);
                }
            }
        }

        return isTreeCompliant;
    }


    /**
     * Get the node of the tree with the indicated id
     * @param id id of the searched node
     * @param node ancestor node of the searched node
     * @return the node of the tree with the indicated id
     */
    public static DefaultMutableTreeNode searchTreeNode(String id, DefaultMutableTreeNode node){
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
    public static boolean isSpecialProperty(String propertyName) {

        boolean isSpecialProperty = false;

        String agenNameCommentProperty = FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_AGENT_NAME + FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_COMMENT_SUFIX;
        String elementTopologyNameCommentProperty = FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME + FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_COMMENT_SUFIX;

        if ((propertyName.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_AGENT_NAME)) || (propertyName.equals(agenNameCommentProperty))
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
     * @param property
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
    public static String getInterceptorConnection(String sourceConnectionId, List<FlumeTopology> listTopologyConnections, List<FlumeTopology> flumeTopologyList) {

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

                int interceptorsNumber = 0;
                int maxInterceptorsNumber = 0;
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
     * @param maxYCoordinate
     * @param isFirstElementSlice
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
     * @param list
     * @param searchedElementsSublist
     * @return true if the list contains the sublist in identical order, false otherwise
     */
    public static boolean isCorrectOrderSublist(List<String> list, List<String> searchedElementsSublist) {

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

                    if (mapSharedChannelsSourcesRelation.get(sharedChannelName) == null) {
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
     * Get permutatios that preserve a order determinated by shared channels
     * @param completeSourcesList complete list of shared sources
     * @param mapSharedChannelsSourcesRelation relation between shared channels and sources
     * @return list of permutatios of shared sources that preserve a order determinated by shared channels
     */
    public static Collection<List<String>> getCorrectSourcesPermutations (List<String> completeSourcesList, Map<String, List<String>> mapSharedChannelsSourcesRelation) {

        Collection<List<String>> correctSourcesPermutations = new ArrayList<>();
        Collection<List<String>> sourcesPermutations = new ArrayList<>();

        List<String> sourcesInMap = new ArrayList<>();
        List<String> sourcesNotInMap = new ArrayList<>();
        List<String> sourcesNotInMapWithSourcePermutation = new ArrayList<>();
        boolean isSharedSource = false;

        if (completeSourcesList != null && mapSharedChannelsSourcesRelation != null) {
            if (mapSharedChannelsSourcesRelation.size() > 0) {
                //Detect sources not referencied by map
                for (String sourceName : completeSourcesList) {
                    isSharedSource = false;
                    for (String channelName : mapSharedChannelsSourcesRelation.keySet()) {
                        List<String> channelSourcesList = mapSharedChannelsSourcesRelation.get(channelName);
                        if (channelSourcesList.contains(sourceName)) {
                            if (!sourcesInMap.contains(sourceName)) {
                                sourcesInMap.add(sourceName);
                            }
                            isSharedSource = true;
                        }
                    }

                    if (!isSharedSource) {
                        sourcesNotInMap.add(sourceName);
                    }
                }

                //Get all permutations and correct permutations of the sources
                boolean isCorrectSourcerOrder = false;

                sourcesPermutations = CollectionUtils.permutations(sourcesInMap);

                Iterator<List<String>> itSourcesPermutations = sourcesPermutations.iterator();
                while (itSourcesPermutations.hasNext()) {
                    List<String> sourcePermutation = itSourcesPermutations.next();

                    isCorrectSourcerOrder = true;
                    for (String channelName : mapSharedChannelsSourcesRelation.keySet()) {
                        List<String> channelSourcesList = mapSharedChannelsSourcesRelation.get(channelName);
                        isCorrectSourcerOrder = isCorrectSourcerOrder && FlumeConfiguratorTopologyUtils.isCorrectOrderSublist(sourcePermutation, channelSourcesList);
                    }

                    if (isCorrectSourcerOrder) {
                        //Add permutation from sources not in map + correct permutation of sources in map

                        sourcesNotInMapWithSourcePermutation = new ArrayList(sourcesNotInMap);
                        sourcesNotInMapWithSourcePermutation.addAll(sourcePermutation);
                        correctSourcesPermutations.add(sourcesNotInMapWithSourcePermutation);

                        //correctSourcesPermutations.add(sourcePermutation);
                    }
                }
            } else {
                //It's not neccesary permutations. Any order is OK
                correctSourcesPermutations.add(completeSourcesList);
            }
        }

        return correctSourcesPermutations;
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
                //mapSourcesIndependentChannelsNumberRelation.put(sourceName, sourceIndependentChannelsNumber);
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
     * Get relation between sources and interceptors
     * @param agentGraph graph of the agent
     * @return relation between sources and interceptors
     */
    public static Map<String,List<String>> getMapSourcesInterceptorsRelation(IGraph agentGraph) {

        Map<String,List<String>> mapSourcesInterceptorsRelation = new HashedMap<>();

        if (agentGraph != null) {

            FlumeTopology agentVertex = FlumeConfiguratorTopologyUtils.getAgentVertexFromGraph(agentGraph);
            String agentName = agentVertex.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

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
     * Get optimal sources permutation
     * @param sourcesCorrectPermutations collection of allowed permutations
     * @param mapSourcesIndependentChannelsRelation relation between shared sources and independent channels
     * @param mapSourcesInterceptorsRelation relation between sources and interceptors
     * @return the optimal sources permutation
     */
    public static List<String> getOptimalSourcesPermutation(Collection<List<String>> sourcesCorrectPermutations, Map<String,List<String>> mapSourcesIndependentChannelsRelation, Map<String,List<String>> mapSourcesInterceptorsRelation) {

        List<String> optimalSourcesPermutation = null;

        if (sourcesCorrectPermutations != null && mapSourcesIndependentChannelsRelation != null && mapSourcesInterceptorsRelation != null ) {

            if (sourcesCorrectPermutations.size() == 1) {
                optimalSourcesPermutation = CollectionUtils.get(sourcesCorrectPermutations, 0);

            } else  if (sourcesCorrectPermutations.size() > 1) {

                String firstSource = null;
                String secondSource = null;
                int firstSourceIndependentChannelsNumber = 0;
                int secondSourceIndependentChannelsNumber = 0;
                int firstSourceInterceptorsNumber = 0;
                int secondSourceInterceptorsNumber = 0;

                //boolean isFirstElement = true;
                int elementNumber = 1;
                for (String sourceName : mapSourcesIndependentChannelsRelation.keySet()) {

                    int sourceIndependentChannelsNumber = mapSourcesIndependentChannelsRelation.get(sourceName).size();
                    int sourceInterceptorsNumber = mapSourcesInterceptorsRelation.get(sourceName).size();

                    if (elementNumber == 1) {
                        firstSource = sourceName;
                        firstSourceIndependentChannelsNumber = sourceIndependentChannelsNumber;
                        firstSourceInterceptorsNumber = sourceInterceptorsNumber;
                    } else if (elementNumber == 2) {
                        secondSource = sourceName;
                        secondSourceIndependentChannelsNumber = sourceIndependentChannelsNumber;
                        secondSourceInterceptorsNumber = sourceInterceptorsNumber;
                    }

                    /*
                    if (isFirstElement) {
                        firstSource = sourceName;
                        secondSource = sourceName;
                        firstSourceIndependentChannelsNumber = sourceIndependentChannelsNumber;
                        secondSourceIndependentChannelsNumber = sourceIndependentChannelsNumber;
                        firstSourceInterceptorsNumber = sourceInterceptorsNumber;
                        secondSourceInterceptorsNumber = sourceInterceptorsNumber;
                        isFirstElement = false;
                    }
                    */

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

                boolean foundPermutation = false;
                Iterator<List<String>> itSourcesCorrectPermutations = sourcesCorrectPermutations.iterator();
                while (itSourcesCorrectPermutations.hasNext() && !foundPermutation) {
                    List<String> correctSourcesPermutation = itSourcesCorrectPermutations.next();

                    //Check if permutation is correct

                    if (correctSourcesPermutation.get(correctSourcesPermutation.size() -1).equals(firstSource) &&
                            correctSourcesPermutation.get(0).equals(secondSource)) {

                        optimalSourcesPermutation = correctSourcesPermutation;
                        foundPermutation = true;
                    }
                }
            }


        }

        return optimalSourcesPermutation;
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

                    if (mapSharedSinkGroupsSourcesRelation.get(sharedSinkGroupName) == null) {
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
}
