package org.keedio.flume.configurator.utils;

import org.keedio.flume.configurator.constants.FlumeConfiguratorConstants;
import org.keedio.flume.configurator.structures.FlumeTopology;
import org.keedio.flume.configurator.structures.LinkedProperties;
import org.keedio.flume.configurator.structures.TopologyPropertyBean;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

public class FlumeConfiguratorTopologyUtils {

    public static boolean isTreeCompliant(boolean withAgentNodes, List<FlumeTopology> listFlumeTopology, int nodesNumber, int sourcesNumber, int agentsNumber) {

        boolean isTreeCompliant = true;
        Map<String, FlumeTopology> mapTargetConnecion = new HashMap<>();

        int connectionsNumber = listFlumeTopology.size(); // arches number

        System.out.println("Nodes number:" + nodesNumber);
        System.out.println("Sources number: " + sourcesNumber);
        System.out.println("Arches number: " + connectionsNumber);

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


    public static DefaultMutableTreeNode searchTreeNode(String id, Set<DefaultMutableTreeNode> rootNodesSet) {

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



    public static String renderFlumeTopologyTree(DefaultMutableTreeNode tree) {
        List<StringBuilder> lines = renderFlumeTopologyTreeLines(tree);
        String newline = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder(lines.size() * 20);
        for (StringBuilder line : lines) {
            sb.append(line);
            sb.append(newline);
        }
        return sb.toString();
    }

    public static List<StringBuilder> renderFlumeTopologyTreeLines(DefaultMutableTreeNode tree) {
        List<StringBuilder> result = new LinkedList<>();
        FlumeTopology flumeTopology = (FlumeTopology) tree.getUserObject();
        String elementId = flumeTopology.getId();
        String elementName = flumeTopology.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);
        String elementType = flumeTopology.getType();
        //if (elementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT)) {
        //    result.add(new StringBuilder().append(elementId));
        //} else {
            result.add(new StringBuilder().append(elementType).append(" ").append(elementName).append(" (").append(elementId).append(")"));
        //}


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

    private static void addSubtree(List<StringBuilder> result, List<StringBuilder> subtree) {
        Iterator<StringBuilder> iterator = subtree.iterator();
        //subtree generated by renderDirectoryTreeLines has at least one line which is tree.getData()
        result.add(iterator.next().insert(0, "├── "));
        while (iterator.hasNext()) {
            result.add(iterator.next().insert(0, "│   "));
        }
    }

    private static void addLastSubtree(List<StringBuilder> result, List<StringBuilder> subtree) {
        Iterator<StringBuilder> iterator = subtree.iterator();
        //subtree generated by renderDirectoryTreeLines has at least one line which is tree.getData()
        result.add(iterator.next().insert(0, "└── "));
        while (iterator.hasNext()) {
            result.add(iterator.next().insert(0, "    "));
        }
    }


    public static String getKeyPropertyString(String... propertyNameParts) {

        StringBuilder sb = new StringBuilder(100);

        for (String propertyNamePart : propertyNameParts) {
            sb.append(propertyNamePart);
        }

        return sb.toString();
    }


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

    public static Properties addTopologyProperty(Properties properties, String propertyName, String propertyValue) {

        if ((properties != null) && (propertyName != null) && (!"".equals(propertyName)) && (propertyValue != null) && !"".equals(propertyValue)) {

            properties.setProperty(propertyName, propertyValue);
        }

        return properties;

    }



    public static String getPropertyAsString(Properties properties) {
        StringWriter writer = new StringWriter();
        properties.list(new PrintWriter(writer));
        return writer.getBuffer().toString();
    }


    public static void getPartialFlumePropertiesAsString(PrintWriter printWriter, String headerText, Properties properties, String prefixProperties) {

        String newline = System.getProperty("line.separator");

        printWriter.write(FlumeConfiguratorUtils.getHeaderAgentConfiguration(new StringBuilder(headerText), 1, 0, FlumeConfiguratorConstants.HASH));
        LinkedProperties elementsProperties = FlumeConfiguratorUtils.matchingSubset(properties, prefixProperties, true);

        for (Object keyProperty  : elementsProperties.keySet()) {
            String valueProperty = elementsProperties.getProperty((String) keyProperty);
            printWriter.write((String) keyProperty);
            printWriter.write("=");
            printWriter.write(valueProperty);
            printWriter.write(newline);
        }
        printWriter.write(newline);
        printWriter.write(newline);

    }


    public static String getFlumePropertiesAsString(Properties properties) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        String newline = System.getProperty("line.separator");

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
/*

        //Agent list
        printWriter.write(FlumeConfiguratorUtils.getHeaderAgentConfiguration(new StringBuilder("Agents List"), 1, 0, FlumeConfiguratorConstants.HASH));
        LinkedProperties agentListProperty = FlumeConfiguratorUtils.matchingSubset(properties, FlumeConfiguratorConstants.AGENTS_LIST_PROPERTIES_PREFIX, true);

        for (Object keyProperty  : agentListProperty.keySet()) {
            String valueProperty = agentListProperty.getProperty((String) keyProperty);
            printWriter.write((String) keyProperty);
            printWriter.write("=");
            printWriter.write(valueProperty);
            printWriter.write(newline);
        }
        printWriter.write(newline);
        printWriter.write(newline);


        //Sources list
        printWriter.write(FlumeConfiguratorUtils.getHeaderAgentConfiguration(new StringBuilder("Sources per agent list"), 1, 0, FlumeConfiguratorConstants.HASH));
        LinkedProperties sourcesListProperties = FlumeConfiguratorUtils.matchingSubset(properties, FlumeConfiguratorConstants.SOURCES_LIST_PROPERTIES_PREFIX, true);

        for (Object keyProperty  : sourcesListProperties.keySet()) {
            String valueProperty = sourcesListProperties.getProperty((String) keyProperty);
            printWriter.write((String) keyProperty);
            printWriter.write("=");
            printWriter.write(valueProperty);
            printWriter.write(newline);
        }
        printWriter.write(newline);
        printWriter.write(newline);


        //Channels list
        printWriter.write(FlumeConfiguratorUtils.getHeaderAgentConfiguration(new StringBuilder("Channels per agent list"), 1, 0, FlumeConfiguratorConstants.HASH));
        LinkedProperties channelsListProperties = FlumeConfiguratorUtils.matchingSubset(properties, FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX, true);
        for (Object keyProperty  : channelsListProperties.keySet()) {
            String valueProperty = channelsListProperties.getProperty((String) keyProperty);
            printWriter.write((String) keyProperty);
            printWriter.write("=");
            printWriter.write(valueProperty);
            printWriter.write(newline);
        }
        printWriter.write(newline);
        printWriter.write(newline);


        //Sinks list
        printWriter.write(FlumeConfiguratorUtils.getHeaderAgentConfiguration(new StringBuilder("Sinks per agent list"), 1, 0, FlumeConfiguratorConstants.HASH));
        LinkedProperties sinksListProperties = FlumeConfiguratorUtils.matchingSubset(properties, FlumeConfiguratorConstants.SINKS_LIST_PROPERTIES_PREFIX, true);
        for (Object keyProperty  : sinksListProperties.keySet()) {
            String valueProperty = sinksListProperties.getProperty((String) keyProperty);
            printWriter.write((String) keyProperty);
            printWriter.write("=");
            printWriter.write(valueProperty);
            printWriter.write(newline);
        }
        printWriter.write(newline);
        printWriter.write(newline);


        //Groups list
        printWriter.write(FlumeConfiguratorUtils.getHeaderAgentConfiguration(new StringBuilder("Groups list"), 1, 0, FlumeConfiguratorConstants.HASH));
        LinkedProperties groupsListProperties = FlumeConfiguratorUtils.matchingSubset(properties, FlumeConfiguratorConstants.GROUPS_LIST_PROPERTIES_PREFIX, true);
        for (Object keyProperty  : groupsListProperties.keySet()) {
            String valueProperty = groupsListProperties.getProperty((String) keyProperty);
            printWriter.write((String) keyProperty);
            printWriter.write("=");
            printWriter.write(valueProperty);
            printWriter.write(newline);
        }
        printWriter.write(newline);
        printWriter.write(newline);


        //Sources common properties
        printWriter.write(FlumeConfiguratorUtils.getHeaderAgentConfiguration(new StringBuilder("Sources common properties list (Common to all sources from all agents)"), 1, 0, FlumeConfiguratorConstants.HASH));
        LinkedProperties commonSourcesProperties = FlumeConfiguratorUtils.matchingSubset(properties, FlumeConfiguratorConstants.SOURCES_COMMON_PROPERTY_PROPERTIES_PREFIX, true);
        for (Object keyProperty  : commonSourcesProperties.keySet()) {
            String valueProperty = commonSourcesProperties.getProperty((String) keyProperty);
            printWriter.write((String) keyProperty);
            printWriter.write("=");
            printWriter.write(valueProperty);
            printWriter.write(newline);
        }
        printWriter.write(newline);
        printWriter.write(newline);


        //Interceptors list
        printWriter.write(FlumeConfiguratorUtils.getHeaderAgentConfiguration(new StringBuilder("Interceptors per source list"), 1, 0, FlumeConfiguratorConstants.HASH));
        LinkedProperties interceptorsListProperties = FlumeConfiguratorUtils.matchingSubset(properties, FlumeConfiguratorConstants.INTERCEPTORS_LIST_PROPERTIES_PREFIX, true);
        for (Object keyProperty  : interceptorsListProperties.keySet()) {
            String valueProperty = interceptorsListProperties.getProperty((String) keyProperty);
            printWriter.write((String) keyProperty);
            printWriter.write("=");
            printWriter.write(valueProperty);
            printWriter.write(newline);
        }
        printWriter.write(newline);
        printWriter.write(newline);


        //Interceptors common properties
        printWriter.write(FlumeConfiguratorUtils.getHeaderAgentConfiguration(new StringBuilder("Interceptors common properties list (Common to all interceptors from all agents)"), 1, 0, FlumeConfiguratorConstants.HASH));
        LinkedProperties commonInterceptorsProperties = FlumeConfiguratorUtils.matchingSubset(properties, FlumeConfiguratorConstants.INTERCEPTORS_COMMON_PROPERTY_PROPERTIES_PREFIX, true);
        for (Object keyProperty  : commonInterceptorsProperties.keySet()) {
            String valueProperty = commonInterceptorsProperties.getProperty((String) keyProperty);
            printWriter.write((String) keyProperty);
            printWriter.write("=");
            printWriter.write(valueProperty);
            printWriter.write(newline);
        }
        printWriter.write(newline);
        printWriter.write(newline);


        //Channels common properties
        printWriter.write(FlumeConfiguratorUtils.getHeaderAgentConfiguration(new StringBuilder("Channels common properties list (Common to all channels from all agents)"), 1, 0, FlumeConfiguratorConstants.HASH));
        LinkedProperties commonChannelsProperties = FlumeConfiguratorUtils.matchingSubset(properties, FlumeConfiguratorConstants.CHANNELS_COMMON_PROPERTY_PROPERTIES_PREFIX, true);
        for (Object keyProperty  : commonChannelsProperties.keySet()) {
            String valueProperty = commonChannelsProperties.getProperty((String) keyProperty);
            printWriter.write((String) keyProperty);
            printWriter.write("=");
            printWriter.write(valueProperty);
            printWriter.write(newline);
        }
        printWriter.write(newline);
        printWriter.write(newline);


        //Sinks common properties
        printWriter.write(FlumeConfiguratorUtils.getHeaderAgentConfiguration(new StringBuilder("Sinks common properties list (Common to all sinks from all agents)"), 1, 0, FlumeConfiguratorConstants.HASH));
        LinkedProperties commonSinksProperties = FlumeConfiguratorUtils.matchingSubset(properties, FlumeConfiguratorConstants.SINKS_COMMON_PROPERTY_PROPERTIES_PREFIX, true);
        for (Object keyProperty  : commonSinksProperties.keySet()) {
            String valueProperty = commonSinksProperties.getProperty((String) keyProperty);
            printWriter.write((String) keyProperty);
            printWriter.write("=");
            printWriter.write(valueProperty);
            printWriter.write(newline);
        }
        printWriter.write(newline);
        printWriter.write(newline);
*/
        return writer.getBuffer().toString();

    }


    public static boolean isSpecialProperty(String property) {

        boolean isSpecialProperty = false;

        String agenNameCommentProperty = FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_AGENT_NAME + FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_COMMENT_SUFIX;
        String elementTopologyNameCommentProperty = FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME + FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_COMMENT_SUFIX;

        if ((property.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_AGENT_NAME)) || (property.equals(agenNameCommentProperty))
                || (property.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME)) || (property.equals(elementTopologyNameCommentProperty))) {
            isSpecialProperty = true;
        }

        return isSpecialProperty;
    }


    public static boolean isCommentProperty(String property) {

        boolean isCommentProperty = false;

        if (property.endsWith(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_COMMENT_SUFIX)) {
            isCommentProperty = true;
        }

        return isCommentProperty;
    }


    public static String getCommentPropertyName(String property) {

        String commentPropertyName = null;

        if (isCommentProperty(property)) {
            commentPropertyName = property;
        } else {
            commentPropertyName = property + FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_COMMENT_SUFIX;
        }

        return commentPropertyName;
    }


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


    public static String listToString(List<String> list, String separatorCharacter) {

        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s);
            sb.append(separatorCharacter);
        }

        sb.setLength(sb.length()-separatorCharacter.length());

        return sb.toString();
    }


    public static String appendString(String cadenaOriginal, String appendText, String separatorCharacter) {

        StringBuilder sb = new StringBuilder();
        if (cadenaOriginal != null) {
            sb.append(cadenaOriginal);
        }
        if ((appendText != null) && (separatorCharacter != null) && (!"".equals(separatorCharacter))) {
            sb.append(separatorCharacter).append(appendText);
        }

        return sb.toString();
    }


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




}
