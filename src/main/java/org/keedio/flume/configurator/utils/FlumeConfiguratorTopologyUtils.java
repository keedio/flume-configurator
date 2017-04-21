package org.keedio.flume.configurator.utils;

import org.keedio.flume.configurator.constants.FlumeConfiguratorConstants;
import org.keedio.flume.configurator.structures.FlumeTopology;
import org.keedio.flume.configurator.structures.LinkedProperties;
import org.keedio.flume.configurator.structures.TopologyPropertyBean;
import org.keedio.flume.configurator.topology.IGraph;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public class FlumeConfiguratorTopologyUtils {

    private FlumeConfiguratorTopologyUtils() {
        super();
    }

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
                    || (keyPropertyStr.contains(FlumeConfiguratorConstants.INTERCEPTORS_COMMON_PROPERTY_PROPERTIES_PREFIX))
                    || (keyPropertyStr.contains(FlumeConfiguratorConstants.CHANNELS_COMMON_PROPERTY_PROPERTIES_PREFIX))
                    || (keyPropertyStr.contains(FlumeConfiguratorConstants.SINKS_COMMON_PROPERTY_PROPERTIES_PREFIX)))
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

        //Groups list
        getPartialFlumePropertiesAsString(printWriter, "Groups list", properties, FlumeConfiguratorConstants.GROUPS_LIST_PROPERTIES_PREFIX);

        //Sources common properties
        getPartialFlumePropertiesAsString(printWriter, "Sources common properties list (Common to all sources from all agents)", properties,
                FlumeConfiguratorConstants.SOURCES_COMMON_PROPERTY_PROPERTIES_PREFIX);

        //Sources partial properties
        getPartialFlumePropertiesAsString(printWriter, "Sources partial properties list", properties,
                FlumeConfiguratorConstants.SOURCES_PARTIAL_PROPERTY_PROPERTIES_PREFIX);

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

}
