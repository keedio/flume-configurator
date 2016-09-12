package org.keedio.flume.configurator.utils;

import org.keedio.flume.configurator.constants.FlumeConfiguratorConstants;
import org.keedio.flume.configurator.structures.FlumeTopology;
import org.keedio.flume.configurator.structures.LinkedProperties;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

public class FlumeConfiguratorTopologyUtils {

    public static boolean isTreeCompliant(List<FlumeTopology> listFlumeTopology) {

        boolean isTreeCompliant = true;
        Map<String, FlumeTopology> mapTargetConnecion = new HashMap<>();

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
        if (elementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT)) {
            result.add(new StringBuilder().append(elementId));
        } else {
            result.add(new StringBuilder().append(elementType).append(" ").append(elementName).append(" (").append(elementId).append(")"));
        }


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



    public static String getPropertyAsString(Properties properties) {
        StringWriter writer = new StringWriter();
        properties.list(new PrintWriter(writer));
        return writer.getBuffer().toString();
    }


    public static String getFlumePropertiesAsString(Properties properties) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        String newline = System.getProperty("line.separator");


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
        LinkedProperties sourcesListProperty = FlumeConfiguratorUtils.matchingSubset(properties, FlumeConfiguratorConstants.SOURCES_LIST_PROPERTIES_PREFIX, true);

        for (Object keyProperty  : sourcesListProperty.keySet()) {
            String valueProperty = sourcesListProperty.getProperty((String) keyProperty);
            printWriter.write((String) keyProperty);
            printWriter.write("=");
            printWriter.write(valueProperty);
            printWriter.write(newline);
        }
        printWriter.write(newline);
        printWriter.write(newline);


        //Channels list
        printWriter.write(FlumeConfiguratorUtils.getHeaderAgentConfiguration(new StringBuilder("Channels per agent list"), 1, 0, FlumeConfiguratorConstants.HASH));
        LinkedProperties channelsListProperty = FlumeConfiguratorUtils.matchingSubset(properties, FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX, true);
        for (Object keyProperty  : channelsListProperty.keySet()) {
            String valueProperty = channelsListProperty.getProperty((String) keyProperty);
            printWriter.write((String) keyProperty);
            printWriter.write("=");
            printWriter.write(valueProperty);
            printWriter.write(newline);
        }
        printWriter.write(newline);
        printWriter.write(newline);


        //Sinks list
        printWriter.write(FlumeConfiguratorUtils.getHeaderAgentConfiguration(new StringBuilder("Sinks per agent list"), 1, 0, FlumeConfiguratorConstants.HASH));
        LinkedProperties sinksListProperty = FlumeConfiguratorUtils.matchingSubset(properties, FlumeConfiguratorConstants.SINKS_LIST_PROPERTIES_PREFIX, true);
        for (Object keyProperty  : sinksListProperty.keySet()) {
            String valueProperty = sinksListProperty.getProperty((String) keyProperty);
            printWriter.write((String) keyProperty);
            printWriter.write("=");
            printWriter.write(valueProperty);
            printWriter.write(newline);
        }
        printWriter.write(newline);
        printWriter.write(newline);


        //Sinks list
        printWriter.write(FlumeConfiguratorUtils.getHeaderAgentConfiguration(new StringBuilder("Interceptors per source list"), 1, 0, FlumeConfiguratorConstants.HASH));
        LinkedProperties interceptorsListProperty = FlumeConfiguratorUtils.matchingSubset(properties, FlumeConfiguratorConstants.INTERCEPTORS_LIST_PROPERTIES_PREFIX, true);
        for (Object keyProperty  : interceptorsListProperty.keySet()) {
            String valueProperty = interceptorsListProperty.getProperty((String) keyProperty);
            printWriter.write((String) keyProperty);
            printWriter.write("=");
            printWriter.write(valueProperty);
            printWriter.write(newline);
        }
        printWriter.write(newline);
        printWriter.write(newline);

        return writer.getBuffer().toString();

    }


}
