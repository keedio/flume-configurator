package org.keedio.flume.configurator.builder;

import org.apache.log4j.PropertyConfigurator;
import org.keedio.flume.configurator.constants.FlumeConfiguratorConstants;
import org.keedio.flume.configurator.exceptions.FlumeConfiguratorException;
import org.keedio.flume.configurator.structures.*;
import org.keedio.flume.configurator.topology.GraphFactory;
import org.keedio.flume.configurator.topology.IGraph;
import org.keedio.flume.configurator.topology.JSONStringSerializer;
import org.keedio.flume.configurator.utils.FlumeConfiguratorTopologyUtils;
import org.keedio.flume.configurator.utils.FlumeConfiguratorUtils;
import org.keedio.flume.configurator.validator.FlumeConfigurationValidator;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class FlumeTopologyReversePropertiesGenerator {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FlumeTopologyReversePropertiesGenerator.class);

    private static String pathFlumeProperties;
    private static String pathDraw2DFlumeTopologyGeneratedFile;
    private static boolean withComments = false;
    private static boolean generatePositionCoordinates = true;


    private Map<String, FlumeLinesProperties> mapFlumeLinesProperties = new LinkedHashMap<>();
    private FlumeLinesProperties flumeLinesProperties = new FlumeLinesProperties();
    private List<FlumeTopology> flumeTopologyList = new ArrayList<>();
    private List<FlumeTopologyConnection> flumeTopologyConnectionList = new ArrayList<>();

    private List<FlumeTopology> listTopologyConnections = new ArrayList<>();
    private Map<String, IGraph> flumeGraphTopology = new LinkedHashMap<>();


    boolean existIndependentSources = false;

    /**
     * @param pathFlumeProperties the pathFlumeProperties to set
     */
    public static void setPathFlumeProperties(String pathFlumeProperties) {
        FlumeTopologyReversePropertiesGenerator.pathFlumeProperties = pathFlumeProperties;
    }

    /**
     * @param pathDraw2DFlumeTopologyGeneratedFile the pathDraw2DFlumeTopologyGeneratedFile to set
     */
    public static void setPathDraw2DFlumeTopologyGeneratedFile(String pathDraw2DFlumeTopologyGeneratedFile) {
        FlumeTopologyReversePropertiesGenerator.pathDraw2DFlumeTopologyGeneratedFile = pathDraw2DFlumeTopologyGeneratedFile;
    }

    /**
     * @param withComments the withComments to set
     */
    public static void setWithComments(boolean withComments) {
        FlumeTopologyReversePropertiesGenerator.withComments = withComments;
    }

    /**
     * @return generatePositionCoordinates
     */
    public static boolean isGeneratePositionCoordinates() {
        return generatePositionCoordinates;
    }

    /**
     * @param generatePositionCoordinates the generatePositionCoordinates to set
     */
    public static void setGeneratePositionCoordinates(boolean generatePositionCoordinates) {
        FlumeTopologyReversePropertiesGenerator.generatePositionCoordinates = generatePositionCoordinates;
    }


    /**
     * @return mapFlumeLinesProperties
     */
    public Map<String, FlumeLinesProperties> getMapFlumeLinesProperties() {
        return mapFlumeLinesProperties;
    }

    /**
     * @return flumeLinesProperties
     */
    public FlumeLinesProperties getFlumeLinesProperties() {
        return flumeLinesProperties;
    }

    /**
     * @return flumeTopologyList
     */
    public List<FlumeTopology> getFlumeTopologyList() {
        return flumeTopologyList;
    }

    /**
     * @return listTopologyConnections
     */
    public List<FlumeTopology> getListTopologyConnections() {
        return listTopologyConnections;
    }

    /**
     * @return flumeTopologyConnectionList
     */
    public List<FlumeTopologyConnection> getFlumeTopologyConnectionList() {
        return flumeTopologyConnectionList;
    }

    /**
     * @return flumeGraphTopology
     */
    public Map<String, IGraph> getFlumeGraphTopology() {
        return flumeGraphTopology;
    }

    /**
     * Create the initisl structures
     */
    private void createInitialStructures() {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN createInitialStructures");
        }

        mapFlumeLinesProperties = new LinkedHashMap<>();
        flumeLinesProperties = new FlumeLinesProperties();
        flumeTopologyList = new ArrayList<>();
        flumeTopologyConnectionList = new ArrayList<>();
        listTopologyConnections = new ArrayList<>();
        flumeGraphTopology = new LinkedHashMap<>();


        if (logger.isDebugEnabled()) {
            logger.debug("END createInitialStructures");
        }
    }

    /**
     * Load the properties file(s)
     *
     * @throws IOException
     */
    private void loadFlumePropertiesFile() throws IOException {


        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN loadFlumePropertiesFile");
        }

        if (logger.isDebugEnabled()) {
            logger.debug(pathFlumeProperties);
        }

        File flumePropertiesFile = new File(pathFlumeProperties);
        boolean isDirectory = flumePropertiesFile.isDirectory();

        if (isDirectory) {

            Stream<Path> filesList = Files.list(Paths.get(pathFlumeProperties));
            Iterator<Path> filesIterator = filesList.iterator();
            while (filesIterator.hasNext()) {
                List<String> lines = null;
                FlumeLinesProperties flumeLinesProperties = new FlumeLinesProperties();
                Path filePath = filesIterator.next();

                //Avoid hidden files
                if (!filePath.getFileName().toString().startsWith(FlumeConfiguratorConstants.DOT_SEPARATOR)) {

                    FileInputStream fis = new FileInputStream(filePath.toString());
                    LinkedProperties properties = new LinkedProperties();
                    properties.load(fis);

                    if (withComments) {
                        //Obtain lines of the file
                        try (Stream<String> stream = Files.lines(filePath)) {
                            //stream.forEach(System.out::println);
                            lines = stream.collect(Collectors.toCollection(ArrayList::new));
                        }
                    }

                    flumeLinesProperties.setProperties(properties);
                    flumeLinesProperties.setLines(lines);

                    mapFlumeLinesProperties.put(filePath.getFileName().toString(), flumeLinesProperties);
                }
            }

        } else {

            List<String> lines = null;

            //Obtain properties of the file
            FileInputStream fis = new FileInputStream(pathFlumeProperties);
            LinkedProperties properties = new LinkedProperties();
            properties.load(fis);

            if (withComments) {
                //Obtain lines of the file
                try (Stream<String> stream = Files.lines(Paths.get(pathFlumeProperties))) {
                    lines = stream.collect(Collectors.toCollection(ArrayList::new));
                }
            }

            flumeLinesProperties.setProperties(properties);
            flumeLinesProperties.setLines(lines);

            mapFlumeLinesProperties.put(flumePropertiesFile.getName(), flumeLinesProperties);

        }

        if (logger.isDebugEnabled()) {
            logger.debug("END loadFlumePropertiesFile");
        }
    }


    /**
     * Generate a single FlumeLinesProperties from the information of the loaded file(s)
     */
    private void generateSingleLinesProperties() {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN generateSingleLinesProperties");
        }

        flumeLinesProperties = new FlumeLinesProperties();
        List<String> lines = flumeLinesProperties.getLines();
        LinkedProperties properties = flumeLinesProperties.getProperties();
        LinkedProperties propertiesCheck = new LinkedProperties();

        Set<String> setAgentNames = new HashSet<>();
        Set<String> setSourceNames = new HashSet<>();
        Set<String> setInterceptorNames = new HashSet<>();
        Set<String> setChannelNames = new HashSet<>();
        Set<String> setSinkNames = new HashSet<>();
        Set<String> setSinkGroupNames = new HashSet<>();
        Set<String> setAllNames = new HashSet<>();


        for (String agentKey : mapFlumeLinesProperties.keySet()) {
            FlumeLinesProperties flumeLinesPropertiesAgent = mapFlumeLinesProperties.get(agentKey);

            List<String> linesAgent = flumeLinesPropertiesAgent.getLines();
            LinkedProperties propertiesAgent = flumeLinesPropertiesAgent.getProperties();

            if (withComments && linesAgent != null) {
                //Add lines of agent to
                flumeLinesProperties.getLines().addAll(linesAgent);
            }

            //Detect unique names for agents
            Set<String> agentNames = FlumeConfiguratorTopologyUtils.getSetFirstPartProperties(propertiesAgent);
            for (String agentName : agentNames) {
                if (!setAgentNames.contains(agentName)) {
                    setAgentNames.add(agentName);
                } else {
                    //There is another agent with the same name
                    throw new FlumeConfiguratorException("There is another agent with the same name (" + agentName + ") File: " + agentKey);
                }

                if (!setAllNames.contains(agentName)) {
                    setAllNames.add(agentName);
                } else {
                    //There is two elements with the same name
                    throw new FlumeConfiguratorException("There is another element with the same name (" + agentName + ") File: " + agentKey);
                }

            }

            //Detect unique names for sources
            LinkedProperties sourcesListProperties = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(propertiesAgent, FlumeConfiguratorConstants.SOURCES_PROPERTY, FlumeConfiguratorConstants.SOURCES_LIST_PROPERTY_PART_INDEX, true);
            for (Object sourceNameProperty : sourcesListProperties.keySet()) {
                String sourceNames = (String) sourcesListProperties.get(sourceNameProperty);
                String[] sourceNamesArray = sourceNames.split(FlumeConfiguratorConstants.WHITE_SPACE_REGEX);

                for (String sourceName : sourceNamesArray) {
                    if (!setSourceNames.contains(sourceName)) {
                        setSourceNames.add(sourceName);
                    } else {
                        //There is another source with the same name
                        throw new FlumeConfiguratorException("There is another source with the same name (" + sourceName + ") File: " + agentKey);
                    }

                    if (!setAllNames.contains(sourceName)) {
                        setAllNames.add(sourceName);
                    } else {
                        //There is two elements with the same name
                        throw new FlumeConfiguratorException("There is another element with the same name (" + sourceName + ") File: " + agentKey);
                    }
                }
            }

            //Detect unique names for interceptors
            LinkedProperties interceptorsListProperties = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(propertiesAgent, FlumeConfiguratorConstants.INTERCEPTORS_PROPERTY, FlumeConfiguratorConstants.INTERCEPTORS_LIST_PROPERTY_PART_INDEX, true);
            for (Object interceptorNameProperty : interceptorsListProperties.keySet()) {
                String interceptorNames = (String) interceptorsListProperties.get(interceptorNameProperty);
                String[] interceptorNamesArray = interceptorNames.split(FlumeConfiguratorConstants.WHITE_SPACE_REGEX);

                for (String interceptorName : interceptorNamesArray) {
                    if (!setInterceptorNames.contains(interceptorName)) {
                        setInterceptorNames.add(interceptorName);
                    } else {
                        //There is another interceptor with the same name
                        throw new FlumeConfiguratorException("There is another interceptor with the same name (" + interceptorName + ") File: " + agentKey);
                    }

                    if (!setAllNames.contains(interceptorName)) {
                        setAllNames.add(interceptorName);
                    } else {
                        //There is two elements with the same name
                        throw new FlumeConfiguratorException("There is another element with the same name  (" + interceptorName + ") File: " + agentKey);
                    }
                }
            }


            //Detect unique names for channels
            LinkedProperties channelsListProperties = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(propertiesAgent, FlumeConfiguratorConstants.CHANNELS_PROPERTY, FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTY_PART_INDEX, true);
            for (Object channelNameProperty : channelsListProperties.keySet()) {
                String channelNames = (String) channelsListProperties.get(channelNameProperty);
                String[] channelNamesArray = channelNames.split(FlumeConfiguratorConstants.WHITE_SPACE_REGEX);

                for (String channelName : channelNamesArray) {
                    if (!setChannelNames.contains(channelName)) {
                        setChannelNames.add(channelName);
                    } else {
                        //There is another channel with the same name
                        throw new FlumeConfiguratorException("There is another channel with the same name (" + channelName + ") File: " + agentKey);
                    }

                    if (!setAllNames.contains(channelName)) {
                        setAllNames.add(channelName);
                    } else {
                        //There is two elements with the same name
                        throw new FlumeConfiguratorException("There is another element with the same name (" + channelName + ") File: " + agentKey);
                    }
                }
            }


            //Detect unique names for sinks
            LinkedProperties sinksListProperties = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(propertiesAgent, FlumeConfiguratorConstants.SINKS_PROPERTY, FlumeConfiguratorConstants.SINKS_LIST_PROPERTY_PART_INDEX, true);
            for (Object sinkNameProperty : sinksListProperties.keySet()) {
                String sinkNames = (String) sinksListProperties.get(sinkNameProperty);
                String[] sinkNamesArray = sinkNames.split(FlumeConfiguratorConstants.WHITE_SPACE_REGEX);

                for (String sinkName : sinkNamesArray) {
                    if (!setSinkNames.contains(sinkName)) {
                        setSinkNames.add(sinkName);
                    } else {
                        //There is another sink with the same name
                        throw new FlumeConfiguratorException("There is another sink with the same name (" + sinkName + ") File: " + agentKey);
                    }

                    if (!setAllNames.contains(sinkName)) {
                        setAllNames.add(sinkName);
                    } else {
                        //There is two elements with the same name
                        throw new FlumeConfiguratorException("There is another element with the same name (" + sinkName + ") File: " + agentKey);
                    }
                }
            }


            //Detect unique names for sinkgroups
            LinkedProperties sinkGroupsListProperties = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(propertiesAgent, FlumeConfiguratorConstants.SINKGROUPS_PROPERTY, FlumeConfiguratorConstants.SINKGROUPS_LIST_PROPERTY_PART_INDEX, true);
            for (Object sinkGroupNameProperty : sinkGroupsListProperties.keySet()) {
                String sinkGroupNames = (String) sinkGroupsListProperties.get(sinkGroupNameProperty);
                String[] sinkGroupNamesArray = sinkGroupNames.split(FlumeConfiguratorConstants.WHITE_SPACE_REGEX);

                for (String sinkGroupName : sinkGroupNamesArray) {
                    if (!setSinkGroupNames.contains(sinkGroupName)) {
                        setSinkNames.add(sinkGroupName);
                    } else {
                        //There is another sinkgroup with the same name
                        throw new FlumeConfiguratorException("There is another sinkgroup with the same name (" + sinkGroupName + ") File: " + agentKey);
                    }

                    if (!setAllNames.contains(sinkGroupName)) {
                        setAllNames.add(sinkGroupName);
                    } else {
                        //There is two elements with the same name
                        throw new FlumeConfiguratorException("There is another element with the same name (" + sinkGroupName + ") File: " + agentKey);
                    }
                }
            }

            //Detect if the property already exists for another agent
            for (Enumeration e = propertiesAgent.propertyNames(); e.hasMoreElements(); ) {
                String propertyName = (String) e.nextElement();
                String propertyValue = propertiesAgent.getProperty(propertyName);
                String singlePropertyValue = propertiesCheck.getProperty(propertyName);

                if (singlePropertyValue == null) {
                    //The property deesn't exist
                    propertiesCheck.put(propertyName, propertyValue);
                } else {
                    if (propertyValue.equals(singlePropertyValue)) {
                        //The property exists (with the same value)
                        propertiesCheck.put(propertyName, propertyValue);
                    } else {
                        //The property exists with a different value
                        throw new FlumeConfiguratorException("There are properties with same name and different values: Property: " + propertyName + " File: " + agentKey);
                    }
                }
            }

            //Add agent properties to general properties
            properties.putAll(propertiesAgent);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN generateSingleLinesProperties");
        }
    }

    /**
     * Generate agents Flume topology elements
     */
    private void generateAgentsFlumeTopology() {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN generateAgentsFlumeTopology");
        }

        LinkedProperties properties = flumeLinesProperties.getProperties();
        Set<String> agentNames = FlumeConfiguratorTopologyUtils.getSetFirstPartProperties(properties);

        for (String agentName : agentNames) {
            FlumeTopology flumeTopologyAgent = new FlumeTopology();
            flumeTopologyAgent.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT);
            flumeTopologyAgent.setId(UUID.randomUUID().toString());
            //Add flumeType property
            flumeTopologyAgent.getData().put(FlumeConfiguratorConstants.FLUME_TYPE_PROPERTY, FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT);
            flumeTopologyAgent.getData().put(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME, agentName);
            if (withComments) {
                flumeTopologyAgent.getData().put(FlumeConfiguratorTopologyUtils.getCommentPropertyName(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME), "");
            }
            flumeTopologyList.add(flumeTopologyAgent);
        }


        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN generateAgentsFlumeTopology");
        }

    }


    /**
     * Generate the different elementos of Flume topology (SOURCES, INTERCEPTORS, CHANNELS, SINKS)
     *
     * @param topologyType  The type of the topology to create (SOURCE, INTERCEPTOR, CHANNEL, SINK)
     * @param propertyPart  part of the property to search the elements of the topology
     * @param propertyIndex index of the searched part in the property
     */
    private void generateFlumeTopologyElements(String topologyType, String propertyPart, int propertyIndex) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN generateFlumeTopologyElements");
        }

        LinkedProperties properties = flumeLinesProperties.getProperties();

        LinkedProperties propertiesWithPart = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(properties, propertyPart, propertyIndex, true);

        String key;
        for (Object keyObject : propertiesWithPart.keySet()) {
            key = (String) keyObject;
            String propertyValue = propertiesWithPart.getProperty(key);
            if (propertyValue != null && !"".equals(propertyValue)) {
                String[] elements = propertyValue.split(FlumeConfiguratorConstants.WHITE_SPACE_REGEX);
                for (String elementName : elements) {
                    FlumeTopology flumeTopologyAgent = new FlumeTopology();
                    flumeTopologyAgent.setType(topologyType);
                    flumeTopologyAgent.setId(UUID.randomUUID().toString());

                    //Add flumeType property
                    flumeTopologyAgent.getData().put(FlumeConfiguratorConstants.FLUME_TYPE_PROPERTY, topologyType);
                    //Add elementTopologyName property
                    flumeTopologyAgent.getData().put(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME, elementName);
                    if (withComments) {
                        flumeTopologyAgent.getData().put(FlumeConfiguratorTopologyUtils.getCommentPropertyName(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME), "");
                    }

                    flumeTopologyList.add(flumeTopologyAgent);
                }
            }

        }


        if (logger.isDebugEnabled()) {
            logger.debug("END generateFlumeTopologyElements");
        }

    }


    /**
     * Generate the different properties for the elements of the Flume topology
     */
    private void generateElementsProperties() {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN generateElementsProperties");
        }

        LinkedProperties properties = flumeLinesProperties.getProperties();

        for (FlumeTopology flumeTopologyElement : flumeTopologyList) {
            String type = flumeTopologyElement.getType();
            String elementName = flumeTopologyElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

            if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE.equals(type)) {

                LinkedProperties elementProperties = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(properties, elementName, FlumeConfiguratorConstants.ELEMENT_PROPERTY_PART_INDEX, false);

                //Add type property
                LinkedProperties elementTypeProperty = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(elementProperties, FlumeConfiguratorConstants.TYPE_PROPERTY, FlumeConfiguratorConstants.ELEMENT_PROPERTY_PART_INDEX + 1, true);

                String propertyName;
                for (Object keyObject : elementTypeProperty.keySet()) {
                    propertyName = (String) keyObject;

                    String sourcesPart = FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, FlumeConfiguratorConstants.SOURCES_LIST_PROPERTY_PART_INDEX);
                    String sourcesInterceptorsPart = FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, FlumeConfiguratorConstants.SOURCE_INTERCEPTORS_PART_INDEX);
                    String sourcesChannelsPart = FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, FlumeConfiguratorConstants.SOURCE_CHANNELS_PART_INDEX);

                    if (FlumeConfiguratorConstants.SOURCES_PROPERTY.equals(sourcesPart)
                            && !FlumeConfiguratorConstants.INTERCEPTORS_PROPERTY.equals(sourcesInterceptorsPart)
                            && !FlumeConfiguratorConstants.CHANNELS_PROPERTY.equals(sourcesChannelsPart)) {
                        //The property is valid

                        addElementProperty(flumeTopologyElement, propertyName, FlumeConfiguratorConstants.ELEMENT_PROPERTY_PART_INDEX);
                    }
                }

                //Add rest of properties
                for (Object keyObject : elementProperties.keySet()) {
                    propertyName = (String) keyObject;

                    String sourcesPart = FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, FlumeConfiguratorConstants.SOURCES_LIST_PROPERTY_PART_INDEX);
                    String sourcesInterceptorsPart = FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, FlumeConfiguratorConstants.SOURCE_INTERCEPTORS_PART_INDEX);
                    String sourcesChannelsPart = FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, FlumeConfiguratorConstants.SOURCE_CHANNELS_PART_INDEX);

                    if (FlumeConfiguratorConstants.SOURCES_PROPERTY.equals(sourcesPart)
                            && !FlumeConfiguratorConstants.INTERCEPTORS_PROPERTY.equals(sourcesInterceptorsPart)
                            && !FlumeConfiguratorConstants.CHANNELS_PROPERTY.equals(sourcesChannelsPart)) {

                        //The property is valid
                        addElementProperty(flumeTopologyElement, propertyName, FlumeConfiguratorConstants.ELEMENT_PROPERTY_PART_INDEX);
                    }
                }

            } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL.equals(type)) {

                LinkedProperties elementProperties = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(properties, elementName, FlumeConfiguratorConstants.ELEMENT_PROPERTY_PART_INDEX, false);

                //Add type property first
                LinkedProperties elementTypeProperty = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(elementProperties, FlumeConfiguratorConstants.TYPE_PROPERTY, FlumeConfiguratorConstants.ELEMENT_PROPERTY_PART_INDEX + 1, true);

                String propertyName;
                for (Object keyObject : elementTypeProperty.keySet()) {
                    propertyName = (String) keyObject;

                    String channelsPart = FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTY_PART_INDEX);

                    if (FlumeConfiguratorConstants.CHANNELS_PROPERTY.equals(channelsPart)) {
                        //The property is valid
                        addElementProperty(flumeTopologyElement, propertyName, FlumeConfiguratorConstants.ELEMENT_PROPERTY_PART_INDEX);
                    }
                }

                //Add rest of properties
                for (Object keyObject : elementProperties.keySet()) {
                    propertyName = (String) keyObject;

                    String channelsPart = FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTY_PART_INDEX);

                    if (FlumeConfiguratorConstants.CHANNELS_PROPERTY.equals(channelsPart)) {
                        //The property is valid
                        addElementProperty(flumeTopologyElement, propertyName, FlumeConfiguratorConstants.ELEMENT_PROPERTY_PART_INDEX);
                    }
                }

            } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK.equals(type)) {

                LinkedProperties elementProperties = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(properties, elementName, FlumeConfiguratorConstants.ELEMENT_PROPERTY_PART_INDEX, false);

                //Add type property first
                LinkedProperties elementTypeProperty = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(elementProperties, FlumeConfiguratorConstants.TYPE_PROPERTY, FlumeConfiguratorConstants.ELEMENT_PROPERTY_PART_INDEX + 1, true);

                String propertyName;
                for (Object keyObject : elementTypeProperty.keySet()) {
                    propertyName = (String) keyObject;

                    String sinksPart = FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, FlumeConfiguratorConstants.SINKS_LIST_PROPERTY_PART_INDEX);
                    String sinksChannelPart = FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, FlumeConfiguratorConstants.SINK_CHANNEL_PART_INDEX);

                    if (FlumeConfiguratorConstants.SINKS_PROPERTY.equals(sinksPart)
                            && !FlumeConfiguratorConstants.CHANNEL_PROPERTY.equals(sinksChannelPart)) {

                        //The property is valid
                        addElementProperty(flumeTopologyElement, propertyName, FlumeConfiguratorConstants.ELEMENT_PROPERTY_PART_INDEX);
                    }
                }

                //Add rest of properties
                for (Object keyObject : elementProperties.keySet()) {
                    propertyName = (String) keyObject;

                    String sinksPart = FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, FlumeConfiguratorConstants.SINKS_LIST_PROPERTY_PART_INDEX);
                    String sinksChannelPart = FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, FlumeConfiguratorConstants.SINK_CHANNEL_PART_INDEX);

                    if (FlumeConfiguratorConstants.SINKS_PROPERTY.equals(sinksPart)
                            && !FlumeConfiguratorConstants.CHANNEL_PROPERTY.equals(sinksChannelPart)) {

                        //The property is valid
                        addElementProperty(flumeTopologyElement, propertyName, FlumeConfiguratorConstants.ELEMENT_PROPERTY_PART_INDEX);
                    }
                }

            } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP.equals(type)) {

                LinkedProperties elementProperties = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(properties, elementName, FlumeConfiguratorConstants.ELEMENT_PROPERTY_PART_INDEX, false);

                //Add processor.type property first
                LinkedProperties elementProcessorTypeProperty = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(elementProperties, FlumeConfiguratorConstants.TYPE_PROPERTY, FlumeConfiguratorConstants.ELEMENT_PROPERTY_PART_INDEX + 2, true);

                String propertyName;
                for (Object keyObject : elementProcessorTypeProperty.keySet()) {
                    propertyName = (String) keyObject;

                    String sinkGroupsPart = FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, FlumeConfiguratorConstants.SINKGROUPS_LIST_PROPERTY_PART_INDEX);
                    String sinkGroupsProcessorPart = FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, FlumeConfiguratorConstants.SINKGROUP_PROCESSOR_PART_INDEX);
                    String sinkGroupsSinksPart = FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, FlumeConfiguratorConstants.SINKGROUP_SINKS_PART_INDEX);

                    if (FlumeConfiguratorConstants.SINKGROUPS_PROPERTY.equals(sinkGroupsPart)
                            && FlumeConfiguratorConstants.PROCESSOR_PROPERTY.equals(sinkGroupsProcessorPart)
                            && !FlumeConfiguratorConstants.SINKS_PROPERTY.equals(sinkGroupsSinksPart)) {

                        //The property is valid
                        addElementProperty(flumeTopologyElement, propertyName, FlumeConfiguratorConstants.ELEMENT_PROPERTY_PART_INDEX);
                    }
                }

                //Add rest of properties
                for (Object keyObject : elementProperties.keySet()) {
                    propertyName = (String) keyObject;

                    String sinkGroupsPart = FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, FlumeConfiguratorConstants.SINKGROUPS_LIST_PROPERTY_PART_INDEX);
                    String sinkGroupsProcessorPart = FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, FlumeConfiguratorConstants.SINKGROUP_PROCESSOR_PART_INDEX);
                    String sinkGroupsSinksPart = FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, FlumeConfiguratorConstants.SINKGROUP_SINKS_PART_INDEX);

                    if (FlumeConfiguratorConstants.SINKGROUPS_PROPERTY.equals(sinkGroupsPart)
                            && FlumeConfiguratorConstants.PROCESSOR_PROPERTY.equals(sinkGroupsProcessorPart)
                            && !FlumeConfiguratorConstants.SINKS_PROPERTY.equals(sinkGroupsSinksPart)) {

                        //The property is valid
                        addElementProperty(flumeTopologyElement, propertyName, FlumeConfiguratorConstants.ELEMENT_PROPERTY_PART_INDEX);
                    }
                }

            } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR.equals(type)) {

                LinkedProperties elementProperties = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(properties, elementName, FlumeConfiguratorConstants.INTERCEPTOR_PROPERTY_PART_INDEX, false);

                //Add type property first
                LinkedProperties elementTypeProperty = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(elementProperties, FlumeConfiguratorConstants.TYPE_PROPERTY, FlumeConfiguratorConstants.INTERCEPTOR_PROPERTY_PART_INDEX + 1, true);

                String propertyName;
                for (Object keyObject : elementTypeProperty.keySet()) {
                    propertyName = (String) keyObject;

                    String sourcesPart = FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, FlumeConfiguratorConstants.SOURCES_LIST_PROPERTY_PART_INDEX);
                    String interceptorsPart = FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, FlumeConfiguratorConstants.INTERCEPTORS_LIST_PROPERTY_PART_INDEX);

                    if (FlumeConfiguratorConstants.SOURCES_PROPERTY.equals(sourcesPart)
                            && FlumeConfiguratorConstants.INTERCEPTORS_PROPERTY.equals(interceptorsPart)) {

                        //The property is valid
                        addElementProperty(flumeTopologyElement, propertyName, FlumeConfiguratorConstants.INTERCEPTOR_PROPERTY_PART_INDEX);
                    }
                }

                for (Object keyObject : elementProperties.keySet()) {
                    propertyName = (String) keyObject;

                    String sourcesPart = FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, FlumeConfiguratorConstants.SOURCES_LIST_PROPERTY_PART_INDEX);
                    String interceptorsPart = FlumeConfiguratorTopologyUtils.getPropertyPart(propertyName, FlumeConfiguratorConstants.INTERCEPTORS_LIST_PROPERTY_PART_INDEX);

                    if (FlumeConfiguratorConstants.SOURCES_PROPERTY.equals(sourcesPart)
                            && FlumeConfiguratorConstants.INTERCEPTORS_PROPERTY.equals(interceptorsPart)) {

                        //The property is valid
                        addElementProperty(flumeTopologyElement, propertyName, FlumeConfiguratorConstants.INTERCEPTOR_PROPERTY_PART_INDEX);
                    }
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END generateElementsProperties");
        }
    }


    /**
     * Add the property to the flume topology element
     *
     * @param flumeTopologyElement FlumeTopology element
     * @param propertyName         Name of the property
     * @param partIndex            index of the part of the property
     */
    private void addElementProperty(FlumeTopology flumeTopologyElement, String propertyName, int partIndex) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN addElementProperty");
        }

        LinkedProperties properties = flumeLinesProperties.getProperties();
        List<String> lines = flumeLinesProperties.getLines();

        String elementName = flumeTopologyElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);
        LinkedProperties elementProperties = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(properties, elementName, partIndex, false);
        Map<String, String> flumeTopologyElementProperties = flumeTopologyElement.getData();

        //Get the property
        String flumeTopologyPropertyName = FlumeConfiguratorTopologyUtils.getTailPartProperty(propertyName, partIndex);
        String flumeTopologyPropertyValue = elementProperties.getProperty(propertyName);

        //Add property
        if (flumeTopologyPropertyName != null && !"".equals(flumeTopologyPropertyName)) {

            if (flumeTopologyPropertyValue != null && !"".equals(flumeTopologyPropertyValue)) {
                flumeTopologyElementProperties.put(flumeTopologyPropertyName, flumeTopologyPropertyValue);
            } else {
                flumeTopologyElementProperties.put(flumeTopologyPropertyName, "");
            }

            if (withComments) {
                //Add comment property name
                String flumeTopologyPropertyCommentName = FlumeConfiguratorTopologyUtils.getCommentPropertyName(flumeTopologyPropertyName);
                String flumeTopologyPropertyCommentValue = FlumeConfiguratorTopologyUtils.getPropertyCommentFromText(lines, propertyName);

                flumeTopologyElementProperties.put(flumeTopologyPropertyCommentName, flumeTopologyPropertyCommentValue);
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END addElementProperty");
        }
    }


    /**
     * Generate the Flume topology connections elements
     */
    private void generateFlumeTopologyConnections() {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN generateFlumeTopologyConnections");
        }

        String key;
        LinkedProperties properties = flumeLinesProperties.getProperties();


        if (logger.isDebugEnabled()) {
            logger.debug("Create agents to sources connections");
        }

        //AGENTS TO SOURCES CONNECTIONS
        LinkedProperties sourcesPart = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(properties, FlumeConfiguratorConstants.SOURCES_PROPERTY, FlumeConfiguratorConstants.SOURCES_LIST_PROPERTY_PART_INDEX, true);

        for (Object keyObject : sourcesPart.keySet()) {
            key = (String) keyObject;
            String propertyValue = sourcesPart.getProperty(key);

            if (propertyValue != null && !"".equals(propertyValue)) {
                //Get the agent name from property
                String agentName = FlumeConfiguratorTopologyUtils.getPropertyPart(key, FlumeConfiguratorConstants.AGENTS_PROPERTY_PART_INDEX);
                String agentFlumeTopologyID = FlumeConfiguratorTopologyUtils.getFlumeTopologyId(flumeTopologyList, agentName);

                //Get the sources of the agent
                String[] sources = propertyValue.split(FlumeConfiguratorConstants.WHITE_SPACE_REGEX);
                for (String sourceName : sources) {

                    String sourceFlumeTopologyID = FlumeConfiguratorTopologyUtils.getFlumeTopologyId(flumeTopologyList, sourceName);

                    //Create connection
                    FlumeTopology flumeTopologyConnection = new FlumeTopology();
                    flumeTopologyConnection.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION);
                    flumeTopologyConnection.setId(UUID.randomUUID().toString());
                    flumeTopologyConnection.setSourceConnection(agentFlumeTopologyID);
                    flumeTopologyConnection.setTargetConnection(sourceFlumeTopologyID);

                    flumeTopologyList.add(flumeTopologyConnection);
                }
            }
        }


        if (logger.isDebugEnabled()) {
            logger.debug("Create sources to interceptors connections");
        }

        //SOURCES TO INTERCEPTORS CONNECTIONS
        LinkedProperties interceptorsPart = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(properties, FlumeConfiguratorConstants.INTERCEPTORS_PROPERTY, FlumeConfiguratorConstants.INTERCEPTORS_LIST_PROPERTY_PART_INDEX, true);

        for (Object keyObject : interceptorsPart.keySet()) {
            key = (String) keyObject;
            String propertyValue = interceptorsPart.getProperty(key);

            if (propertyValue != null && !"".equals(propertyValue)) {
                //Get the source name from property
                String sourceName = FlumeConfiguratorTopologyUtils.getPropertyPart(key, FlumeConfiguratorConstants.ELEMENT_PROPERTY_PART_INDEX);
                String sourceFlumeTopologyID = FlumeConfiguratorTopologyUtils.getFlumeTopologyId(flumeTopologyList, sourceName);

                //Get the interceptors of the source (Each interceptor will be connected to the next interceptor. The first, to the source)
                String[] interceptors = propertyValue.split(FlumeConfiguratorConstants.WHITE_SPACE_REGEX);
                boolean isFirstInterceptor = true;
                String previousInterceptorID = "";
                for (String interceptorName : interceptors) {

                    String interceptorFlumeTopologyID = FlumeConfiguratorTopologyUtils.getFlumeTopologyId(flumeTopologyList, interceptorName);

                    if (isFirstInterceptor) {
                        //Create connection
                        FlumeTopology flumeTopologyConnection = new FlumeTopology();
                        flumeTopologyConnection.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION);
                        flumeTopologyConnection.setId(UUID.randomUUID().toString());
                        flumeTopologyConnection.setSourceConnection(sourceFlumeTopologyID);
                        flumeTopologyConnection.setTargetConnection(interceptorFlumeTopologyID);

                        flumeTopologyList.add(flumeTopologyConnection);

                        previousInterceptorID = interceptorFlumeTopologyID;
                        isFirstInterceptor = false;
                    } else {
                        //Create connection
                        FlumeTopology flumeTopologyConnection = new FlumeTopology();
                        flumeTopologyConnection.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION);
                        flumeTopologyConnection.setId(UUID.randomUUID().toString());
                        flumeTopologyConnection.setSourceConnection(previousInterceptorID);
                        flumeTopologyConnection.setTargetConnection(interceptorFlumeTopologyID);

                        flumeTopologyList.add(flumeTopologyConnection);

                        previousInterceptorID = interceptorFlumeTopologyID;
                    }
                }
            }
        }


        if (logger.isDebugEnabled()) {
            logger.debug("Create sources to channels / interceptors to channels connections");
        }

        //SOURCES TO CHANNELS CONNECTIONS / INTERCEPTORS TO CHANNELS CONNECTIONS
        LinkedProperties channelsPart = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(properties, FlumeConfiguratorConstants.CHANNELS_PROPERTY, FlumeConfiguratorConstants.SOURCE_CHANNELS_PART_INDEX, true);

        for (Object keyObject : channelsPart.keySet()) {
            key = (String) keyObject;
            String propertyValue = channelsPart.getProperty(key);

            if (propertyValue != null && !"".equals(propertyValue)) {
                //Get the source name from property
                String sourceName = FlumeConfiguratorTopologyUtils.getPropertyPart(key, FlumeConfiguratorConstants.ELEMENT_PROPERTY_PART_INDEX);
                String sourceFlumeTopologyID = FlumeConfiguratorTopologyUtils.getFlumeTopologyId(flumeTopologyList, sourceName);

                //Get the last interceptor or the source (if exists)
                String lastInterceptorName = FlumeConfiguratorTopologyUtils.getLastInterceptorNameFromSource(properties, sourceName);
                String lastInterceptorFlumeTopologyID = FlumeConfiguratorTopologyUtils.getFlumeTopologyId(flumeTopologyList, lastInterceptorName);


                //Get the channels of the source
                String[] channels = propertyValue.split(FlumeConfiguratorConstants.WHITE_SPACE_REGEX);

                for (String channelName : channels) {

                    String channelFlumeTopologyID = FlumeConfiguratorTopologyUtils.getFlumeTopologyId(flumeTopologyList, channelName);

                    if (lastInterceptorFlumeTopologyID.isEmpty()) {
                        //Connect the source with the channel
                        FlumeTopology flumeTopologyConnection = new FlumeTopology();
                        flumeTopologyConnection.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION);
                        flumeTopologyConnection.setId(UUID.randomUUID().toString());
                        flumeTopologyConnection.setSourceConnection(sourceFlumeTopologyID);
                        flumeTopologyConnection.setTargetConnection(channelFlumeTopologyID);

                        flumeTopologyList.add(flumeTopologyConnection);
                    } else {
                        //Connect the last interceptor of the source with the channel
                        FlumeTopology flumeTopologyConnection = new FlumeTopology();
                        flumeTopologyConnection.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION);
                        flumeTopologyConnection.setId(UUID.randomUUID().toString());
                        flumeTopologyConnection.setSourceConnection(lastInterceptorFlumeTopologyID);
                        flumeTopologyConnection.setTargetConnection(channelFlumeTopologyID);

                        flumeTopologyList.add(flumeTopologyConnection);
                    }

                }
            }
        }


        if (logger.isDebugEnabled()) {
            logger.debug("Create channels to sinks connections");
        }

        //CHANNELS TO SINKS CONNECTIONS
        LinkedProperties channelPart = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(properties, FlumeConfiguratorConstants.CHANNEL_PROPERTY, FlumeConfiguratorConstants.SINK_CHANNEL_PART_INDEX, true);

        for (Object keyObject : channelPart.keySet()) {
            key = (String) keyObject;
            String propertyValue = channelPart.getProperty(key);

            if (propertyValue != null && !"".equals(propertyValue)) {
                //Get the sink name from property
                String sinkName = FlumeConfiguratorTopologyUtils.getPropertyPart(key, FlumeConfiguratorConstants.ELEMENT_PROPERTY_PART_INDEX);
                String sinkFlumeTopologyID = FlumeConfiguratorTopologyUtils.getFlumeTopologyId(flumeTopologyList, sinkName);

                //Get the channel of the sink
                String channelName = propertyValue.trim();

                String channelFlumeTopologyID = FlumeConfiguratorTopologyUtils.getFlumeTopologyId(flumeTopologyList, channelName);

                //Create connection
                FlumeTopology flumeTopologyConnection = new FlumeTopology();
                flumeTopologyConnection.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION);
                flumeTopologyConnection.setId(UUID.randomUUID().toString());
                flumeTopologyConnection.setSourceConnection(channelFlumeTopologyID);
                flumeTopologyConnection.setTargetConnection(sinkFlumeTopologyID);

                flumeTopologyList.add(flumeTopologyConnection);
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Create sinks to sinkgroups connections");
        }

        //SINKS TO SINKGROUPS CONNECTIONS
        LinkedProperties sinksPart = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(properties, FlumeConfiguratorConstants.SINKS_PROPERTY, FlumeConfiguratorConstants.SINKGROUP_SINKS_PART_INDEX, true);

        for (Object keyObject : sinksPart.keySet()) {
            key = (String) keyObject;
            String propertyValue = sinksPart.getProperty(key);

            if (propertyValue != null && !"".equals(propertyValue)) {
                //Get the sinkgroup name from property
                String sinkGroupName = FlumeConfiguratorTopologyUtils.getPropertyPart(key, FlumeConfiguratorConstants.ELEMENT_PROPERTY_PART_INDEX);
                String sinkGroupFlumeTopologyID = FlumeConfiguratorTopologyUtils.getFlumeTopologyId(flumeTopologyList, sinkGroupName);

                //Get the sinks of the sinkgroup
                String[] sinks = propertyValue.split(FlumeConfiguratorConstants.WHITE_SPACE_REGEX);

                for (String sinkName : sinks) {

                    String sinkFlumeTopologyID = FlumeConfiguratorTopologyUtils.getFlumeTopologyId(flumeTopologyList, sinkName);

                    //Create connection
                    FlumeTopology flumeTopologyConnection = new FlumeTopology();
                    flumeTopologyConnection.setType(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION);
                    flumeTopologyConnection.setId(UUID.randomUUID().toString());
                    flumeTopologyConnection.setSourceConnection(sinkFlumeTopologyID);
                    flumeTopologyConnection.setTargetConnection(sinkGroupFlumeTopologyID);

                    flumeTopologyList.add(flumeTopologyConnection);
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END generateFlumeTopologyConnections");
        }

    }


    /**
     * Generate a graph of the topology and export it (dot format)
     */
    public void generateGraph() {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN generateGraph");
        }

        String graphDotFormat = null;

        boolean withAgentNodes = false;
        boolean isTreeCompliant = false;
        Map<String, FlumeTopology> mapTopology = new LinkedHashMap<>();
        List<FlumeTopology> listTopologyAgents = new ArrayList<>();
        List<FlumeTopology> listTopologySources = new ArrayList<>();
        List<FlumeTopology> listTopologyInterceptors = new ArrayList<>();
        Set<String> agentsList = new LinkedHashSet<>();


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
            String flumeTopologyElementName = flumeTopologyElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

            String flumeTopologyElementType = flumeTopologyElement.getType();

            if (!FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT.equals(flumeTopologyElementType)) {

                //Get the agent graph
                String agentName = FlumeConfiguratorTopologyUtils.getGraphAgentFromConnections(flumeTopologyElementId, listTopologyConnections, flumeTopologyList, withAgentNodes);

                if ((agentName != null) && (!"".equals(agentName))) {

                    graphAgent = flumeGraphTopology.get(agentName);
                    if (graphAgent != null) {
                        graphAgent.addGraphVertex(flumeTopologyElement);
                    } else {
                        throw new FlumeConfiguratorException("flumeGraphTopology doesn't contain root node for agent " + agentName);
                    }
                } else {
                    throw new FlumeConfiguratorException("The agent name can't be obtained for the node id: " + flumeTopologyElementId + "[" + flumeTopologyElementName + "]");
                }

            }

        }

        //Create edges for the rest of vertex
        for (FlumeTopology connection : listTopologyConnections) {
            String sourceConnection = connection.getSourceConnection();
            String targetConnection = connection.getTargetConnection();

            FlumeTopology flumeTopologySourceConnectionElement = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyList, sourceConnection);
            FlumeTopology flumeTopologyTargetConnectionElement = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyList, sourceConnection);


            String sourceAgentName = FlumeConfiguratorTopologyUtils.getGraphAgentFromConnections(sourceConnection, listTopologyConnections, flumeTopologyList, withAgentNodes);
            String targetAgentName = FlumeConfiguratorTopologyUtils.getGraphAgentFromConnections(targetConnection, listTopologyConnections, flumeTopologyList, withAgentNodes);

            if (sourceAgentName == null || targetAgentName == null) {
                String sourceName = "";
                String targetName = "";
                if (flumeTopologySourceConnectionElement != null) {
                    sourceName = flumeTopologySourceConnectionElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);
                }
                if (flumeTopologyTargetConnectionElement != null) {
                    targetName = flumeTopologyTargetConnectionElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);
                }

                throw new FlumeConfiguratorException("sourceConnectionNode " + sourceName + " [" + sourceConnection + "] or targetConnectionNode  " + targetName + " [" + targetConnection + "] are not present on node's pool");
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

        graphDotFormat = FlumeConfiguratorTopologyUtils.renderFlumeTopologyGraph(flumeGraphTopology.values(), true);

        logger.info(graphDotFormat);

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN generateGraph");
        }

    }


    /**
     * Generate specific propertys for presentation (Draw2D library)
     */
    private void generatePropertiesDraw2D() {


        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN generatePropertiesDraw2D");
        }

        Map<String, Map<String, List<String>>> sourcesRelationsMap = new HashMap<>();
        Map<String, List<String>> independentSourcesMap = new HashMap<>();
        Map<String, List<String>> sharedSourcesMap = new HashMap<>();
        Map<String, List<FlumeTopology>> sinkGroupsMap = new HashMap<>();
        Set<String> sourcesSharedSinkGroupWithSharedSourceSet = new HashSet<>();
        List<String> sourcesSharedSinkGroupWithSharedSourceList = new ArrayList<>();

        if (generatePositionCoordinates) {
            //Detect relations between sources
            for (String agentName : flumeGraphTopology.keySet()) {

                sourcesRelationsMap.put(agentName, new HashMap());

                IGraph agentGraph = flumeGraphTopology.get(agentName);

                FlumeTopology agentVertex = FlumeConfiguratorTopologyUtils.getAgentVertexFromGraph(agentGraph);

                List<FlumeTopology> sourcesList = agentGraph.successorListOf(agentVertex);

                List<FlumeTopology> sinkGroupsList = FlumeConfiguratorTopologyUtils.getListFlumeTopologyByType(flumeGraphTopology, agentName, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP);
                sinkGroupsMap.put(agentName, sinkGroupsList);

                //Get the descendants of the source
                for (FlumeTopology source : sourcesList) {

                    if (source.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE)) {

                        String sourceName = source.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);
                        sourcesRelationsMap.get(agentName).put(sourceName, new ArrayList<>());

                        //Get descendants of source
                        Set<FlumeTopology> sourceChildren = agentGraph.getVertexDescendants(source);
                        Iterator<FlumeTopology> itSourceChildren = sourceChildren.iterator();

                        while (itSourceChildren.hasNext()) {
                            FlumeTopology sourceChild = itSourceChildren.next();
                            String channelName = sourceChild.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                            if (sourceChild.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL)) {

                                //Get all sources from channel
                                Set<FlumeTopology> channelAncestorsList = agentGraph.getVertexAncestors(sourceChild);

                                for (FlumeTopology channelAncestor : channelAncestorsList) {

                                    if (channelAncestor.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE)) {
                                        String channelAncestorSourceName = channelAncestor.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                                        if (!sourceName.equals(channelAncestorSourceName)) {
                                            logger.debug("The source " + sourceName + " has a channel with a different source: " + channelAncestorSourceName);
                                            //The channel has a different source
                                            //Add the shared channel
                                            //sourcesRelationsMap.get(agentName).get(sourceName).add(channelAncestorSourceName);
                                            sourcesRelationsMap.get(agentName).get(sourceName).add(channelName);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //Detect independent sources
            for (String agentName : sourcesRelationsMap.keySet()) {

                independentSourcesMap.put(agentName, new ArrayList<>());
                sharedSourcesMap.put(agentName, new ArrayList<>());

                for (String sourceName : sourcesRelationsMap.get(agentName).keySet()) {

                    List<String> sharedChannelsList = sourcesRelationsMap.get(agentName).get(sourceName);
                    if (sharedChannelsList.size() == 0) {
                        //Source is independent
                        independentSourcesMap.get(agentName).add(sourceName);
                    } else {
                        //Source share elements with other sources
                        sharedSourcesMap.get(agentName).add(sourceName);
                    }
                }
            }


            //Draw elements
            boolean isFirstAgent = true;
            for (String agentName : independentSourcesMap.keySet()) {

                IGraph agentGraph = flumeGraphTopology.get(agentName);

                int numberSlicesAgent = FlumeConfiguratorTopologyUtils.calculateSlicesNumber(flumeGraphTopology, agentName);
                int sliceWidthAgent = (FlumeConfiguratorConstants.CANVAS_PX_WIDTH - FlumeConfiguratorConstants.CANVAS_HORIZONTAL_MARGIN_PX * 2) / numberSlicesAgent;
                int fixedSlicesNumber = FlumeConfiguratorConstants.FIXED_SLICES_NUMBER;
                if (FlumeConfiguratorTopologyUtils.existSinkGroupSlice(flumeGraphTopology, agentName)) {
                    fixedSlicesNumber++;
                }
                int maxInterceptorsNumber = numberSlicesAgent - fixedSlicesNumber;

                logger.debug("Slice size agent: " + agentName + " = " + sliceWidthAgent + "px");

                int agentsSlice = FlumeConfiguratorConstants.CANVAS_HORIZONTAL_MARGIN_PX;
                int sourcesSlice = agentsSlice + sliceWidthAgent;
                int firstInterceptorSlice = sourcesSlice + sliceWidthAgent;
                int channelSlice = firstInterceptorSlice + (maxInterceptorsNumber * sliceWidthAgent);
                int sinkSlice = channelSlice + sliceWidthAgent;
                int sinkGroupSlice = sinkSlice + sliceWidthAgent;

                logger.debug("Agents slice (y coordinate) agent: " + agentName + " = " + agentsSlice + "px");
                logger.debug("Sources slice (y coordinate) agent: " + agentName + " = " + sourcesSlice + "px");
                logger.debug("First Interceptor slice (y coordinate) agent: " + agentName + " = " + firstInterceptorSlice + "px");
                logger.debug("Channels slice (y coordinate) agent: " + agentName + " = " + channelSlice + "px");
                logger.debug("Sinks slice (y coordinate) agent: " + agentName + " = " + sinkSlice + "px");
                logger.debug("Sinkgroups slice (y coordinate) agent: " + agentName + " = " + sinkGroupSlice + "px");


                int initial_Agent_Y_coordinate = FlumeConfiguratorTopologyUtils.getNextYCoordinate(FlumeConfiguratorTopologyUtils.getMaxYCoordinate(flumeTopologyList), isFirstAgent)
                        + FlumeConfiguratorConstants.CANVAS_AGENTS_HEIGHT_PX_SEPARATION;

                FlumeTopology flumeTopologyAgentElement = FlumeConfiguratorTopologyUtils.getAgentVertexFromGraph(agentGraph);

                //Draw Agent
                assignFlumeTopologyElementPositionCoordinates(agentName, FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT, agentsSlice, sourcesSlice, firstInterceptorSlice,
                        channelSlice, sinkSlice, sinkGroupSlice, isFirstAgent, 0, sliceWidthAgent);

                isFirstAgent = false;


                //Process fully independent sources
                sourcesSharedSinkGroupWithSharedSourceSet = processIndependentSources(agentGraph, independentSourcesMap, agentName, agentsSlice, sourcesSlice, firstInterceptorSlice, channelSlice, sinkSlice, sinkGroupSlice, sliceWidthAgent);

                //Process shared sources
                processSharedSources(agentGraph, sourcesRelationsMap, sharedSourcesMap, agentName, agentsSlice, sourcesSlice, firstInterceptorSlice, channelSlice, sinkSlice, sinkGroupSlice, sliceWidthAgent);

                //Convert set (independent sources that share sink groups with shared sources) to a list
                sourcesSharedSinkGroupWithSharedSourceList = new ArrayList<String>(sourcesSharedSinkGroupWithSharedSourceSet);

                //Draw independent sources with shared sink group with shared sources
                drawIndependentSources(agentGraph, sourcesSharedSinkGroupWithSharedSourceList, agentsSlice, sourcesSlice, firstInterceptorSlice,
                                        channelSlice, sinkSlice, sinkGroupSlice, sliceWidthAgent, false);

                //Relocate sink groups position
                relocateSinkGroups(agentGraph, agentName, sinkGroupsMap);

                int final_Agent_Y_coordinate = FlumeConfiguratorTopologyUtils.getMaxYCoordinate(flumeTopologyList);

                //Reassign Y coordinate to agent
                int average_Agent_Y_Coordinate = initial_Agent_Y_coordinate + ((final_Agent_Y_coordinate - initial_Agent_Y_coordinate) / 2);
                flumeTopologyAgentElement.setY(String.valueOf(average_Agent_Y_Coordinate));

                if (logger.isDebugEnabled()) {
                    logger.debug("Flume topology element " + agentName + " NEW coordinates: {" + flumeTopologyAgentElement.getX() + "," + flumeTopologyAgentElement.getY() + "}");
                }
            }
        }


        createCommonDraw2dProperties(generatePositionCoordinates);

        convertToDraw2DTopologyFormat();

        if (logger.isDebugEnabled()) {
            logger.debug("END generatePropertiesDraw2D");
        }
    }


    /**
     * Process fully independent sources (channels belong to only one source and there isn't a shared source that shares sink group with the source)
     *
     * @param agentGraph            graph of the agent
     * @param independentSourcesMap relation between agents and independent sources
     * @param agentName             name of the agent
     * @param agentsSlice           X-coordinate of the agent's slice
     * @param sourcesSlice          X-coordinate of the sources's slice
     * @param firstInterceptorSlice X-coordinate of the first interceptor slice
     * @param channelSlice          X-coordinate of the channel's slice
     * @param sinkSlice             X-coordinate of the sink's slice
     * @param sinkGroupSlice        X-coordinate of the sinkgroup's slice
     * @param sliceWidthAgent       width of the slices
     * @return Set of independent sources that share sink group with a shared source
     */
    private Set<String> processIndependentSources(IGraph agentGraph, Map<String, List<String>> independentSourcesMap, String agentName, int agentsSlice, int sourcesSlice, int firstInterceptorSlice,
                                                   int channelSlice, int sinkSlice, int sinkGroupSlice, int sliceWidthAgent) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN processIndependentSources");
        }

        Map<String, List<String>> sourcesSharedSinkGroupsRelationsMap = new HashMap<>();
        Map<String, List<String>> sharedSinkGroupsSourcesRelationsMap = new HashMap<>();
        List<String> sharedSinkGroupSources = new ArrayList<>();
        Collection<List<String>> sourcesCorrectPermutations = null;
        Set<String> sourcesSharedSinkGroupWithSharedSource = new HashSet<>();

        //Draw Sources
        List<String> independentSources = independentSourcesMap.get(agentName);

        if (independentSources.size() > 0) {
            existIndependentSources = true;
        }

        //Create relation between independent sources and shared sinkgroups
        for (String sourceName : independentSources) {

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


        //Get shared sinkgroups and independent sources relation
        sharedSinkGroupsSourcesRelationsMap = FlumeConfiguratorTopologyUtils.getMapSharedSinkGroupsSourcesRelation(sharedSinkGroupSources, sourcesSharedSinkGroupsRelationsMap);

        //Get correct sources permutations
        sourcesCorrectPermutations = FlumeConfiguratorTopologyUtils.getCorrectSourcesPermutations(independentSources, sharedSinkGroupsSourcesRelationsMap);

        //Get one of the correct permutations (the first)
        List<String> optimalPermutationIndependentSources = sourcesCorrectPermutations.iterator().next();

        //Remove independent sources that share sink group with a shared (channel) source
        for (String independentSourceName : optimalPermutationIndependentSources) {
            //Shared sink group
            List<String> sharedSinkGroupList = sourcesSharedSinkGroupsRelationsMap.get(independentSourceName);

            if (sharedSinkGroupList != null && sharedSinkGroupList.size() > 0) {
                //Get all ancestors sources of sink group
                for (String sharedSinkGroupName : sharedSinkGroupList) {
                    String sharedSinkGroupID = FlumeConfiguratorTopologyUtils.getFlumeTopologyId(flumeTopologyList, sharedSinkGroupName);
                    FlumeTopology flumeTopologySharedSinkGroupElement = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyList, sharedSinkGroupID);

                    Set<FlumeTopology> sharedSinkGroupAncestors = agentGraph.getVertexAncestors(flumeTopologySharedSinkGroupElement);
                    for (FlumeTopology sharedSinkGroupAncestor : sharedSinkGroupAncestors) {
                        if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE.equals(sharedSinkGroupAncestor.getType())) {
                            String sourceName = sharedSinkGroupAncestor.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);
                            if (!independentSources.contains(sourceName)) {
                                //The independent source shares a sink group with a source not independent (shared source)
                                if (logger.isDebugEnabled()) {
                                    logger.debug("The source " + independentSourceName + " share a sink group (" + sharedSinkGroupName + ") with a shared (non independent) source (" + sourceName + ")");
                                }
                                sourcesSharedSinkGroupWithSharedSource.add(independentSourceName);
                            }
                        }
                    }
                }
            }
        }

        //Remove sources that shares a sink group with a source not independent (shared source)
        Iterator<String> iterOptimalPermutationIndependentSources = optimalPermutationIndependentSources.iterator();
        while (iterOptimalPermutationIndependentSources.hasNext()) {
            String optimalPermutationIndependentSource = iterOptimalPermutationIndependentSources.next();
            if (sourcesSharedSinkGroupWithSharedSource.contains(optimalPermutationIndependentSource)) {
                iterOptimalPermutationIndependentSources.remove();
            }
        }


        drawIndependentSources(agentGraph, optimalPermutationIndependentSources, agentsSlice, sourcesSlice, firstInterceptorSlice, channelSlice,
                sinkSlice, sinkGroupSlice, sliceWidthAgent, true);

        if (logger.isDebugEnabled()) {
            logger.debug("END processIndependentSources");
        }

        return sourcesSharedSinkGroupWithSharedSource;

    }



    /**
     * Assign the coordinates for the independent sources of the list
     *
     * @param agentGraph            graph of the agent
     * @param sourcesList           list of independent sources
     * @param agentsSlice           X-coordinate of the agent's slice
     * @param sourcesSlice          X-coordinate of the sources's slice
     * @param firstInterceptorSlice X-coordinate of the first interceptor slice
     * @param channelSlice          X-coordinate of the channel's slice
     * @param sinkSlice             X-coordinate of the sink's slice
     * @param sinkGroupSlice        X-coordinate of the sinkgroup's slice
     * @param sliceWidthAgent       width of the slices
     * @param isFirstSource         true if the source is the first of the slica, false otherwise
     */
    private void drawIndependentSources(IGraph agentGraph, List<String> sourcesList, int agentsSlice, int sourcesSlice, int firstInterceptorSlice,
                                        int channelSlice, int sinkSlice, int sinkGroupSlice, int sliceWidthAgent, boolean isFirstSource) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN drawIndependentSources");
        }

        for (String independentSourceName : sourcesList) {

            int initial_Source_Y_coordinate = FlumeConfiguratorTopologyUtils.getNextYCoordinate(FlumeConfiguratorTopologyUtils.getMaxYCoordinate(flumeTopologyList), isFirstSource);
            logger.debug("******* source initial_Source_Y_coordinate : " + independentSourceName + "= " + initial_Source_Y_coordinate);

            assignFlumeTopologyElementPositionCoordinates(independentSourceName, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE, agentsSlice, sourcesSlice,
                    firstInterceptorSlice, channelSlice, sinkSlice, sinkGroupSlice, isFirstSource, 0, sliceWidthAgent);

            isFirstSource = false;

            //Get interceptors of the source
            String independentSourceID = FlumeConfiguratorTopologyUtils.getFlumeTopologyId(flumeTopologyList, independentSourceName);
            FlumeTopology flumeTopologySourceElement = agentGraph.getVertex(independentSourceID, true);

            List<String> sourceInterceptorsList = FlumeConfiguratorTopologyUtils.getSourceInterceptorsList(flumeTopologySourceElement, agentGraph);

            //Get the interceptors chain ordered
            List<String> sourceInterceptorsOrderedList = FlumeConfiguratorTopologyUtils.orderSourceInterceptorsFromConnections(sourceInterceptorsList, flumeTopologySourceElement.getId(), listTopologyConnections, flumeTopologyList);

            //Draw interceptors
            boolean isFirstInterceptor = true;
            int interceptorNumber = 1;
            String lastInterceptorName = "";
            for (String interceptorName : sourceInterceptorsOrderedList) {

                assignFlumeTopologyElementPositionCoordinates(interceptorName, FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR, agentsSlice, sourcesSlice,
                        firstInterceptorSlice, channelSlice, sinkSlice, sinkGroupSlice, isFirstInterceptor, interceptorNumber, sliceWidthAgent);

                isFirstInterceptor = false;
                interceptorNumber++;
                lastInterceptorName = interceptorName;
            }


            List<String> channelsIDs = new ArrayList<String>();
            //Draw channels
            if (sourceInterceptorsOrderedList.size() == 0) {
                //Source without interceptors
                channelsIDs = FlumeConfiguratorTopologyUtils.getAllTargetConnections(independentSourceID, listTopologyConnections);
            } else {
                //Source with interceptors

                String lastInterceptorID = FlumeConfiguratorTopologyUtils.getFlumeTopologyId(flumeTopologyList, lastInterceptorName);
                channelsIDs = FlumeConfiguratorTopologyUtils.getAllTargetConnections(lastInterceptorID, listTopologyConnections);
            }


            boolean isFirstChannel = true;
            for (String channelID : channelsIDs) {

                FlumeTopology flumeTopologyChannelElement = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyList, channelID);

                if (flumeTopologyChannelElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL)) {

                    int initial_Channel_Y_coordinate = FlumeConfiguratorTopologyUtils.getNextYCoordinate(FlumeConfiguratorTopologyUtils.getMaxYCoordinate(flumeTopologyList), isFirstChannel);

                    String channelName = flumeTopologyChannelElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                    assignFlumeTopologyElementPositionCoordinates(channelName, FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL, agentsSlice, sourcesSlice,
                            firstInterceptorSlice, channelSlice, sinkSlice, sinkGroupSlice, isFirstChannel, interceptorNumber, sliceWidthAgent);

                    isFirstChannel = false;


                    //Draw sinks
                    List<String> sinksIDs = new ArrayList<String>();
                    sinksIDs = FlumeConfiguratorTopologyUtils.getAllTargetConnections(channelID, listTopologyConnections);

                    boolean isFirstSink = true;
                    for (String sinkID : sinksIDs) {

                        FlumeTopology flumeTopologySinkElement = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyList, sinkID);

                        if (flumeTopologySinkElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK)) {

                            String sinkName = flumeTopologySinkElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                            assignFlumeTopologyElementPositionCoordinates(sinkName, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK, agentsSlice, sourcesSlice,
                                    firstInterceptorSlice, channelSlice, sinkSlice, sinkGroupSlice, isFirstSink, interceptorNumber, sliceWidthAgent);

                            isFirstSink = false;


                            //Draw sinkgroups
                            List<String> sinkGroupsIDs = new ArrayList<String>();
                            sinkGroupsIDs = FlumeConfiguratorTopologyUtils.getAllTargetConnections(sinkID, listTopologyConnections);

                            boolean isFirstSinkGroup = true;
                            for (String sinkGroupID : sinkGroupsIDs) {

                                FlumeTopology flumeTopologySinkGroupElement = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyList, sinkGroupID);

                                if (flumeTopologySinkGroupElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP)) {

                                    String sinkGroupName = flumeTopologySinkGroupElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                                    assignFlumeTopologyElementPositionCoordinates(sinkGroupName, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP, agentsSlice, sourcesSlice,
                                            firstInterceptorSlice, channelSlice, sinkSlice, sinkGroupSlice, isFirstSinkGroup, interceptorNumber, sliceWidthAgent);

                                    isFirstSinkGroup = false;
                                }
                            }
                        }
                    }

                    int final_Channel_Y_coordinate = FlumeConfiguratorTopologyUtils.getMaxYCoordinate(flumeTopologyList);

                    //Reassign Y coordinate to channel
                    int average_Channel_Y_Coordinate = initial_Channel_Y_coordinate + ((final_Channel_Y_coordinate - initial_Channel_Y_coordinate) / 2);
                    flumeTopologyChannelElement.setY(String.valueOf(average_Channel_Y_Coordinate));

                    if (logger.isDebugEnabled()) {
                        logger.debug("Flume topology element " + channelName + " NEW coordinates: {" + flumeTopologyChannelElement.getX() + "," + flumeTopologyChannelElement.getY() + "}");
                    }
                }
            }

            int final_Source_Y_coordinate = FlumeConfiguratorTopologyUtils.getMaxYCoordinate(flumeTopologyList);
            logger.debug("******* source final_Source_Y_coordinate: " + independentSourceName + "= " + final_Source_Y_coordinate);

            //Reassign Y coordinate to source
            int average_Source_Y_Coordinate = initial_Source_Y_coordinate + ((final_Source_Y_coordinate - initial_Source_Y_coordinate) / 2);
            flumeTopologySourceElement.setY(String.valueOf(average_Source_Y_Coordinate));

            if (logger.isDebugEnabled()) {
                logger.debug("Flume topology element " + independentSourceName + " NEW coordinates: {" + flumeTopologySourceElement.getX() + "," + flumeTopologySourceElement.getY() + "}");
            }

            //Assign the Y coordinates for all interceptors of the source
            for (String interceptorName : sourceInterceptorsOrderedList) {

                String interceptorID = FlumeConfiguratorTopologyUtils.getFlumeTopologyId(flumeTopologyList, interceptorName);
                FlumeTopology flumeTopologyInterceptorElement = agentGraph.getVertex(interceptorID, true);

                flumeTopologyInterceptorElement.setY(String.valueOf(average_Source_Y_Coordinate));

            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END drawIndependentSources");
        }

    }




    /**
     * Process shared sources (channels belong to several sources)
     * @param agentGraph graph of the agent
     * @param sourcesRelationsMap relation between sources and shared channels (for every agent)
     * @param sharedSourcesMap relation between agents and shared sources
     * @param agentName name of the agent
     * @param agentsSlice X-coordinate of the agent's slice
     * @param sourcesSlice X-coordinate of the sources's slice
     * @param firstInterceptorSlice X-coordinate of the first interceptor slice
     * @param channelSlice X-coordinate of the channel's slice
     * @param sinkSlice X-coordinate of the sink's slice
     * @param sinkGroupSlice X-coordinate of the sinkgroup's slice
     * @param sliceWidthAgent width of the slices
     */
    private void processSharedSources(IGraph agentGraph, Map<String, Map<String, List<String>>> sourcesRelationsMap , Map<String, List<String>> sharedSourcesMap, String agentName,
                                           int agentsSlice, int sourcesSlice, int firstInterceptorSlice, int channelSlice, int sinkSlice, int sinkGroupSlice, int sliceWidthAgent) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN processSharedSources");
        }

        Map<String, List<String>> mapSharedChannelsSourcesRelation = new HashMap<>();
        List<String> completeSourcesList = new ArrayList<>();
        Collection<List<String>> sourcesCorrectPermutations = null;
        Map<String,Integer> mapChannelSourcesNumberRelation = null;
        Map<String,List<String>> mapSourcesIndependentChannelsRelation = null;
        Map<String,List<String>> mapSourcesInterceptorsRelation = null;

        //Get shared sources
        List<String> sharedSources = sharedSourcesMap.get(agentName);

        if (sharedSources.size() > 0) {

            //Get shared channels and sources relation
            mapSharedChannelsSourcesRelation = FlumeConfiguratorTopologyUtils.getMapSharedChannelsSourcesRelation(sharedSources, sourcesRelationsMap, agentName);

            //Print map relations
            for (String channelName : mapSharedChannelsSourcesRelation.keySet()) {
                List<String> sourcesList = mapSharedChannelsSourcesRelation.get(channelName);
                StringBuilder sb = new StringBuilder();
                sb.append("Sources of channel: " + channelName + " {");


                Iterator<String> sourcesIt = sourcesList.iterator();
                while (sourcesIt.hasNext()) {
                    String source = sourcesIt.next();
                    sb.append(source);
                    sb.append(",");
                }

                sb.setLength(sb.length() - 1);
                sb.append("}");

                logger.debug(sb.toString());
            }


            //Get complete shared sources list (from greatest size)
            completeSourcesList = FlumeConfiguratorTopologyUtils.getCompleteSharedSourcesList(mapSharedChannelsSourcesRelation);

/*
            //Chack correct order
            boolean isCorrectSourcerOrder = true;
            for (String channelName : mapSharedChannelsSourcesRelation.keySet()) {
                List<String> channelSourcesList = mapSharedChannelsSourcesRelation.get(channelName);
                isCorrectSourcerOrder = isCorrectSourcerOrder && FlumeConfiguratorTopologyUtils.isCorrectOrderSublist(completeSourcesList, channelSourcesList);
            }
*/

            //Get correct sources permutations
            sourcesCorrectPermutations = FlumeConfiguratorTopologyUtils.getCorrectSourcesPermutations(completeSourcesList, mapSharedChannelsSourcesRelation);

            //Get channel sources number relation
            mapChannelSourcesNumberRelation = FlumeConfiguratorTopologyUtils.getMapChannelSourcesNumberRelation(completeSourcesList, agentGraph, flumeTopologyList);

            //Get sources independent channels relation
            mapSourcesIndependentChannelsRelation = FlumeConfiguratorTopologyUtils.getMapSourcesIndependentChannelsRelation(completeSourcesList, agentGraph, flumeTopologyList, mapChannelSourcesNumberRelation);

            //Get sources interceptors relation
            mapSourcesInterceptorsRelation = FlumeConfiguratorTopologyUtils.getMapSourcesInterceptorsRelation(agentGraph);

            //Get optimal source permutation
            List<String> optimalSourcesPermutation = FlumeConfiguratorTopologyUtils.getOptimalSourcesPermutation(sourcesCorrectPermutations, mapSourcesIndependentChannelsRelation, mapSourcesInterceptorsRelation);

            //Draw Sources
            drawSharedSources(agentGraph, optimalSourcesPermutation, mapSharedChannelsSourcesRelation, mapSourcesIndependentChannelsRelation,
                    agentsSlice, sourcesSlice, firstInterceptorSlice, channelSlice, sinkSlice, sinkGroupSlice, sliceWidthAgent, !existIndependentSources);

        }


        if (logger.isDebugEnabled()) {
            logger.debug("END processSharedSources");
        }
    }



    /**
     * Assign the coordinates for the shared sources of the list
     *
     * @param agentGraph graph of the agent
     * @param optimalSourcesPermutation optimal permutation of the shared sources
     * @param mapSharedChannelsSourcesRelation relation between shared channels and sources of the agent
     * @param mapSourcesIndependentChannelsRelation relation between shared sources and independent channels of the agent
     * @param agentsSlice X-coordinate of the agent's slice
     * @param sourcesSlice X-coordinate of the sources's slice
     * @param firstInterceptorSlice X-coordinate of the first interceptor slice
     * @param channelSlice X-coordinate of the channel's slice
     * @param sinkSlice X-coordinate of the sink's slice
     * @param sinkGroupSlice X-coordinate of the sinkgroup's slice
     * @param sliceWidthAgent width of the slices
     * @param isFirstSharedSource true if is the first source of the slice, false otherwise
     */
    private void drawSharedSources(IGraph agentGraph, List<String> optimalSourcesPermutation, Map<String, List<String>> mapSharedChannelsSourcesRelation,
                                   Map<String,List<String>> mapSourcesIndependentChannelsRelation, int agentsSlice, int sourcesSlice, int firstInterceptorSlice,
                                   int channelSlice, int sinkSlice, int sinkGroupSlice, int sliceWidthAgent, boolean isFirstSharedSource) {


        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN drawSharedSources");
        }

        List<String> channelsRecalculatedCoordinatesList = new ArrayList<>();

        for (int i = 0; i < optimalSourcesPermutation.size(); i++) {

            String sharedSourceName = optimalSourcesPermutation.get(i);


            int initial_Source_Y_coordinate = FlumeConfiguratorTopologyUtils.getNextYCoordinate(FlumeConfiguratorTopologyUtils.getMaxYCoordinate(flumeTopologyList), isFirstSharedSource);
            logger.debug("******* source initial_Source_Y_coordinate : " + sharedSourceName + "= " + initial_Source_Y_coordinate);

            assignFlumeTopologyElementPositionCoordinates(sharedSourceName, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE, agentsSlice, sourcesSlice,
                    firstInterceptorSlice, channelSlice, sinkSlice, sinkGroupSlice, isFirstSharedSource, 0, sliceWidthAgent);

            isFirstSharedSource = false;

            //Get interceptors of the source
            String sharedSourceID = FlumeConfiguratorTopologyUtils.getFlumeTopologyId(flumeTopologyList, sharedSourceName);
            FlumeTopology flumeTopologySourceElement = agentGraph.getVertex(sharedSourceID, true);

            List<String> sourceInterceptorsList = FlumeConfiguratorTopologyUtils.getSourceInterceptorsList(flumeTopologySourceElement, agentGraph);

            //Get the interceptors chain ordered
            List<String> sourceInterceptorsOrderedList = FlumeConfiguratorTopologyUtils.orderSourceInterceptorsFromConnections(sourceInterceptorsList, flumeTopologySourceElement.getId(), listTopologyConnections, flumeTopologyList);

            //Draw interceptors
            boolean isFirstInterceptor = true;
            int interceptorNumber = 1;
            String lastInterceptorName = "";
            for (String interceptorName : sourceInterceptorsOrderedList) {

                assignFlumeTopologyElementPositionCoordinates(interceptorName, FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR, agentsSlice, sourcesSlice,
                        firstInterceptorSlice, channelSlice, sinkSlice, sinkGroupSlice, isFirstInterceptor, interceptorNumber, sliceWidthAgent);

                isFirstInterceptor = false;
                interceptorNumber++;
                lastInterceptorName = interceptorName;
            }

            //Get all channels
            List<String> channelsIDs = new ArrayList<String>();
            //Draw channels
            if (sourceInterceptorsOrderedList.size() == 0) {
                //Source without interceptors
                channelsIDs = FlumeConfiguratorTopologyUtils.getAllTargetConnections(sharedSourceID, listTopologyConnections);
            } else {
                //Source with interceptors

                String lastInterceptorID = FlumeConfiguratorTopologyUtils.getFlumeTopologyId(flumeTopologyList, lastInterceptorName);
                channelsIDs = FlumeConfiguratorTopologyUtils.getAllTargetConnections(lastInterceptorID, listTopologyConnections);
            }


            //Get independent/shared channels lists
            List<String> sharedChannelList = new ArrayList<>();
            List<String> independentChannelList = new ArrayList();
            for (String channelID : channelsIDs) {
                FlumeTopology flumeTopologyChannelElement = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyList, channelID);
                String channelName = flumeTopologyChannelElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                boolean isIndependenChannel = mapSourcesIndependentChannelsRelation.get(sharedSourceName).contains(channelName);

                if (isIndependenChannel) {
                    independentChannelList.add(channelID);
                } else {
                    sharedChannelList.add(channelID);
                }
            }


            if (i < optimalSourcesPermutation.size() - 1) {
                //Independent channels first
                boolean isFirstChannel = true;
                for (String channelID : independentChannelList) {

                    FlumeTopology flumeTopologyChannelElement = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyList, channelID);
                    String channelName = flumeTopologyChannelElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                    if (flumeTopologyChannelElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL)) {

                        int initial_Channel_Y_coordinate = FlumeConfiguratorTopologyUtils.getNextYCoordinate(FlumeConfiguratorTopologyUtils.getMaxYCoordinate(flumeTopologyList), isFirstChannel);

                        assignFlumeTopologyElementPositionCoordinates(channelName, FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL, agentsSlice, sourcesSlice,
                                firstInterceptorSlice, channelSlice, sinkSlice, sinkGroupSlice, isFirstChannel, interceptorNumber, sliceWidthAgent);

                        isFirstChannel = false;

                        //Draw sinks
                        List<String> sinksIDs = new ArrayList<String>();
                        sinksIDs = FlumeConfiguratorTopologyUtils.getAllTargetConnections(channelID, listTopologyConnections);

                        boolean isFirstSink = true;
                        for (String sinkID : sinksIDs) {

                            FlumeTopology flumeTopologySinkElement = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyList, sinkID);

                            if (flumeTopologySinkElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK)) {

                                String sinkName = flumeTopologySinkElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                                assignFlumeTopologyElementPositionCoordinates(sinkName, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK, agentsSlice, sourcesSlice,
                                        firstInterceptorSlice, channelSlice, sinkSlice, sinkGroupSlice, isFirstSink, interceptorNumber, sliceWidthAgent);

                                isFirstSink = false;

                                //Draw sinkgroups
                                List<String> sinkGroupsIDs = new ArrayList<String>();
                                sinkGroupsIDs = FlumeConfiguratorTopologyUtils.getAllTargetConnections(sinkID, listTopologyConnections);

                                boolean isFirstSinkGroup = true;
                                for (String sinkGroupID: sinkGroupsIDs) {

                                    FlumeTopology flumeTopologySinkGroupElement = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyList, sinkGroupID);

                                    if (flumeTopologySinkGroupElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP)) {

                                        String sinkGroupName = flumeTopologySinkGroupElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                                        assignFlumeTopologyElementPositionCoordinates(sinkGroupName, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP, agentsSlice, sourcesSlice,
                                                firstInterceptorSlice, channelSlice, sinkSlice, sinkGroupSlice, isFirstSinkGroup, interceptorNumber, sliceWidthAgent);

                                        isFirstSinkGroup = false;
                                    }
                                }
                            }
                        }

                        int final_Channel_Y_coordinate = FlumeConfiguratorTopologyUtils.getMaxYCoordinate(flumeTopologyList);

                        //Reassign Y coordinate to channel

                        if (!channelsRecalculatedCoordinatesList.contains(channelName)) {
                            int average_Channel_Y_Coordinate = initial_Channel_Y_coordinate + ((final_Channel_Y_coordinate - initial_Channel_Y_coordinate) / 2);
                            flumeTopologyChannelElement.setY(String.valueOf(average_Channel_Y_Coordinate));
                            channelsRecalculatedCoordinatesList.add(channelName);
                            if (logger.isDebugEnabled()) {
                                logger.debug("Flume topology element " + channelName + " NEW coordinates: {" + flumeTopologyChannelElement.getX() + "," + flumeTopologyChannelElement.getY() + "}");
                            }
                        }
                    }
                }

                //Shared Channel
                for (String channelID : sharedChannelList) {

                    FlumeTopology flumeTopologyChannelElement = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyList, channelID);
                    String channelName = flumeTopologyChannelElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                    if (flumeTopologyChannelElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL)) {

                        int initial_Channel_Y_coordinate = FlumeConfiguratorTopologyUtils.getNextYCoordinate(FlumeConfiguratorTopologyUtils.getMaxYCoordinate(flumeTopologyList), isFirstChannel);

                        assignFlumeTopologyElementPositionCoordinates(channelName, FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL, agentsSlice, sourcesSlice,
                                firstInterceptorSlice, channelSlice, sinkSlice, sinkGroupSlice, isFirstChannel, interceptorNumber, sliceWidthAgent);

                        isFirstChannel = false;

                        //Draw sinks
                        List<String> sinksIDs = new ArrayList<String>();
                        sinksIDs = FlumeConfiguratorTopologyUtils.getAllTargetConnections(channelID, listTopologyConnections);

                        boolean isFirstSink = true;
                        for (String sinkID : sinksIDs) {

                            FlumeTopology flumeTopologySinkElement = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyList, sinkID);

                            if (flumeTopologySinkElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK)) {

                                String sinkName = flumeTopologySinkElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                                assignFlumeTopologyElementPositionCoordinates(sinkName, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK, agentsSlice, sourcesSlice,
                                        firstInterceptorSlice, channelSlice, sinkSlice, sinkGroupSlice, isFirstSink, interceptorNumber, sliceWidthAgent);

                                isFirstSink = false;

                                //Draw sinkgroups
                                List<String> sinkGroupsIDs = new ArrayList<String>();
                                sinkGroupsIDs = FlumeConfiguratorTopologyUtils.getAllTargetConnections(sinkID, listTopologyConnections);

                                boolean isFirstSinkGroup = true;
                                for (String sinkGroupID: sinkGroupsIDs) {

                                    FlumeTopology flumeTopologySinkGroupElement = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyList, sinkGroupID);

                                    if (flumeTopologySinkGroupElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP)) {

                                        String sinkGroupName = flumeTopologySinkGroupElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                                        assignFlumeTopologyElementPositionCoordinates(sinkGroupName, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP, agentsSlice, sourcesSlice,
                                                firstInterceptorSlice, channelSlice, sinkSlice, sinkGroupSlice, isFirstSinkGroup, interceptorNumber, sliceWidthAgent);

                                        isFirstSinkGroup = false;
                                    }
                                }
                            }
                        }

                        int final_Channel_Y_coordinate = FlumeConfiguratorTopologyUtils.getMaxYCoordinate(flumeTopologyList);

                        //Reassign Y coordinate to channel

                        if (!channelsRecalculatedCoordinatesList.contains(channelName)) {
                            int average_Channel_Y_Coordinate = initial_Channel_Y_coordinate + ((final_Channel_Y_coordinate - initial_Channel_Y_coordinate) / 2);
                            flumeTopologyChannelElement.setY(String.valueOf(average_Channel_Y_Coordinate));
                            channelsRecalculatedCoordinatesList.add(channelName);
                            if (logger.isDebugEnabled()) {
                                logger.debug("Flume topology element " + channelName + " NEW coordinates: {" + flumeTopologyChannelElement.getX() + "," + flumeTopologyChannelElement.getY() + "}");
                            }
                        }
                    }
                }


            } else {
                //Is last source (independent channels last)
                boolean isFirstChannel = true;

                //Shared Channel
                for (String channelID : sharedChannelList) {

                    FlumeTopology flumeTopologyChannelElement = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyList, channelID);
                    String channelName = flumeTopologyChannelElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                    if (flumeTopologyChannelElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL)) {

                        int initial_Channel_Y_coordinate = FlumeConfiguratorTopologyUtils.getNextYCoordinate(FlumeConfiguratorTopologyUtils.getMaxYCoordinate(flumeTopologyList), isFirstChannel);

                        assignFlumeTopologyElementPositionCoordinates(channelName, FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL, agentsSlice, sourcesSlice,
                                firstInterceptorSlice, channelSlice, sinkSlice, sinkGroupSlice, isFirstChannel, interceptorNumber, sliceWidthAgent);

                        isFirstChannel = false;

                        //Draw sinks
                        List<String> sinksIDs = new ArrayList<String>();
                        sinksIDs = FlumeConfiguratorTopologyUtils.getAllTargetConnections(channelID, listTopologyConnections);

                        boolean isFirstSink = true;
                        for (String sinkID : sinksIDs) {

                            FlumeTopology flumeTopologySinkElement = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyList, sinkID);

                            if (flumeTopologySinkElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK)) {

                                String sinkName = flumeTopologySinkElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                                assignFlumeTopologyElementPositionCoordinates(sinkName, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK, agentsSlice, sourcesSlice,
                                        firstInterceptorSlice, channelSlice, sinkSlice, sinkGroupSlice, isFirstSink, interceptorNumber, sliceWidthAgent);

                                isFirstSink = false;

                                //Draw sinkgroups
                                List<String> sinkGroupsIDs = new ArrayList<String>();
                                sinkGroupsIDs = FlumeConfiguratorTopologyUtils.getAllTargetConnections(sinkID, listTopologyConnections);

                                boolean isFirstSinkGroup = true;
                                for (String sinkGroupID: sinkGroupsIDs) {

                                    FlumeTopology flumeTopologySinkGroupElement = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyList, sinkGroupID);

                                    if (flumeTopologySinkGroupElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP)) {

                                        String sinkGroupName = flumeTopologySinkGroupElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                                        assignFlumeTopologyElementPositionCoordinates(sinkGroupName, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP, agentsSlice, sourcesSlice,
                                                firstInterceptorSlice, channelSlice, sinkSlice, sinkGroupSlice, isFirstSinkGroup, interceptorNumber, sliceWidthAgent);

                                        isFirstSinkGroup = false;
                                    }
                                }
                            }
                        }

                        int final_Channel_Y_coordinate = FlumeConfiguratorTopologyUtils.getMaxYCoordinate(flumeTopologyList);

                        //Reassign Y coordinate to channel

                        if (!channelsRecalculatedCoordinatesList.contains(channelName)) {
                            int average_Channel_Y_Coordinate = initial_Channel_Y_coordinate + ((final_Channel_Y_coordinate - initial_Channel_Y_coordinate) / 2);
                            flumeTopologyChannelElement.setY(String.valueOf(average_Channel_Y_Coordinate));
                            channelsRecalculatedCoordinatesList.add(channelName);
                            if (logger.isDebugEnabled()) {
                                logger.debug("Flume topology element " + channelName + " NEW coordinates: {" + flumeTopologyChannelElement.getX() + "," + flumeTopologyChannelElement.getY() + "}");
                            }
                        }
                    }
                }

                for (String channelID : independentChannelList) {

                    FlumeTopology flumeTopologyChannelElement = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyList, channelID);
                    String channelName = flumeTopologyChannelElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                    if (flumeTopologyChannelElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL)) {

                        int initial_Channel_Y_coordinate = FlumeConfiguratorTopologyUtils.getNextYCoordinate(FlumeConfiguratorTopologyUtils.getMaxYCoordinate(flumeTopologyList), isFirstChannel);

                        assignFlumeTopologyElementPositionCoordinates(channelName, FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL, agentsSlice, sourcesSlice,
                                firstInterceptorSlice, channelSlice, sinkSlice, sinkGroupSlice, isFirstChannel, interceptorNumber, sliceWidthAgent);

                        isFirstChannel = false;

                        //Draw sinks
                        List<String> sinksIDs = new ArrayList<String>();
                        sinksIDs = FlumeConfiguratorTopologyUtils.getAllTargetConnections(channelID, listTopologyConnections);

                        boolean isFirstSink = true;
                        for (String sinkID : sinksIDs) {

                            FlumeTopology flumeTopologySinkElement = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyList, sinkID);

                            if (flumeTopologySinkElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK)) {

                                String sinkName = flumeTopologySinkElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                                assignFlumeTopologyElementPositionCoordinates(sinkName, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK, agentsSlice, sourcesSlice,
                                        firstInterceptorSlice, channelSlice, sinkSlice, sinkGroupSlice, isFirstSink, interceptorNumber, sliceWidthAgent);

                                isFirstSink = false;

                                //Draw sinkgroups
                                List<String> sinkGroupsIDs = new ArrayList<String>();
                                sinkGroupsIDs = FlumeConfiguratorTopologyUtils.getAllTargetConnections(sinkID, listTopologyConnections);

                                boolean isFirstSinkGroup = true;
                                for (String sinkGroupID: sinkGroupsIDs) {

                                    FlumeTopology flumeTopologySinkGroupElement = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyList, sinkGroupID);

                                    if (flumeTopologySinkGroupElement.getType().equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP)) {

                                        String sinkGroupName = flumeTopologySinkGroupElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);

                                        assignFlumeTopologyElementPositionCoordinates(sinkGroupName, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP, agentsSlice, sourcesSlice,
                                                firstInterceptorSlice, channelSlice, sinkSlice, sinkGroupSlice, isFirstSinkGroup, interceptorNumber, sliceWidthAgent);

                                        isFirstSinkGroup = false;
                                    }
                                }
                            }
                        }

                        int final_Channel_Y_coordinate = FlumeConfiguratorTopologyUtils.getMaxYCoordinate(flumeTopologyList);

                        //Reassign Y coordinate to channel

                        if (!channelsRecalculatedCoordinatesList.contains(channelName)) {
                            int average_Channel_Y_Coordinate = initial_Channel_Y_coordinate + ((final_Channel_Y_coordinate - initial_Channel_Y_coordinate) / 2);
                            flumeTopologyChannelElement.setY(String.valueOf(average_Channel_Y_Coordinate));
                            channelsRecalculatedCoordinatesList.add(channelName);
                            if (logger.isDebugEnabled()) {
                                logger.debug("Flume topology element " + channelName + " NEW coordinates: {" + flumeTopologyChannelElement.getX() + "," + flumeTopologyChannelElement.getY() + "}");
                            }
                        }
                    }
                }
            }

            int final_Source_Y_coordinate = FlumeConfiguratorTopologyUtils.getMaxYCoordinate(flumeTopologyList);
            logger.debug("******* source final_Source_Y_coordinate: " + sharedSourceName + "= " + final_Source_Y_coordinate);

            //Reassign Y coordinate to channel
            int average_Source_Y_Coordinate = initial_Source_Y_coordinate + ((final_Source_Y_coordinate - initial_Source_Y_coordinate) / 2);
            flumeTopologySourceElement.setY(String.valueOf(average_Source_Y_Coordinate));

            if (logger.isDebugEnabled()) {
                logger.debug("Flume topology element " + sharedSourceName + " NEW coordinates: {" + flumeTopologySourceElement.getX() + "," + flumeTopologySourceElement.getY() + "}");
            }

            //Assign the Y coordinates for all interceptors of the source
            for (String interceptorName : sourceInterceptorsOrderedList) {

                String interceptorID = FlumeConfiguratorTopologyUtils.getFlumeTopologyId(flumeTopologyList, interceptorName);
                FlumeTopology flumeTopologyInterceptorElement = agentGraph.getVertex(interceptorID, true);

                flumeTopologyInterceptorElement.setY(String.valueOf(average_Source_Y_Coordinate));

            }

        }


        //Relocate shared channels
        for (String channelName: mapSharedChannelsSourcesRelation.keySet()) {

            String channelID = FlumeConfiguratorTopologyUtils.getFlumeTopologyId(flumeTopologyList, channelName);
            FlumeTopology flumeTopologyChannelElement = agentGraph.getVertex(channelID, true);

            List<String> channelSourcesList = mapSharedChannelsSourcesRelation.get(channelName);

            int min_source_Y_coordinate = 0;
            int max_source_Y_coordinate = 0;
            boolean isFirstSource = true;
            for (String channelSourceName: channelSourcesList) {

                String channelSourceID = FlumeConfiguratorTopologyUtils.getFlumeTopologyId(flumeTopologyList, channelSourceName);
                FlumeTopology flumeTopologySourceElement = agentGraph.getVertex(channelSourceID, true);
                int source_Y_coordinate = Integer.valueOf(flumeTopologySourceElement.getY());

                if (isFirstSource) {
                    min_source_Y_coordinate = source_Y_coordinate;
                    max_source_Y_coordinate = source_Y_coordinate;
                    isFirstSource = false;
                } else {
                    if (source_Y_coordinate <= min_source_Y_coordinate) {
                        min_source_Y_coordinate = source_Y_coordinate;
                    } else if (source_Y_coordinate >= max_source_Y_coordinate) {
                        max_source_Y_coordinate = source_Y_coordinate;
                    }
                }
            }

            int average_Channel_Y_Coordinate = min_source_Y_coordinate + ((max_source_Y_coordinate - min_source_Y_coordinate) / 2);
            flumeTopologyChannelElement.setY(String.valueOf(average_Channel_Y_Coordinate));
            if (logger.isDebugEnabled()) {
                logger.debug("Flume topology element " + channelName + " NEW coordinates: {" + flumeTopologyChannelElement.getX() + "," + flumeTopologyChannelElement.getY() + "}");
            }

            List<String> sinksIDs = new ArrayList<String>();
            sinksIDs = FlumeConfiguratorTopologyUtils.getAllTargetConnections(channelID, listTopologyConnections);

            if (sinksIDs.size() % 2 == 0) {
                //Odd number of sinks
                int centralSupIndex = sinksIDs.size() / 2;
                int centralInfIndex = centralSupIndex - 1;

                int next_Y_Coordinate = FlumeConfiguratorTopologyUtils.getNextYCoordinate(average_Channel_Y_Coordinate, false);
                int gap_Y_Coordinates = next_Y_Coordinate - average_Channel_Y_Coordinate;

                int inital_Central_Sup_Y_Coordinate = average_Channel_Y_Coordinate + (gap_Y_Coordinates / 2);
                int inital_Central_Inf_Y_Coordinate = average_Channel_Y_Coordinate - (gap_Y_Coordinates / 2);

                for (int i = centralSupIndex; i < sinksIDs.size() ; i++) {

                    String centralSupSinkID = sinksIDs.get(i);
                    FlumeTopology flumeTopologyCentralSupSinkElement = agentGraph.getVertex(centralSupSinkID, true);

                    int sink_Central_Sup_Y_Coordinate = inital_Central_Sup_Y_Coordinate + (gap_Y_Coordinates * (i - centralSupIndex));
                    flumeTopologyCentralSupSinkElement.setY(String.valueOf(sink_Central_Sup_Y_Coordinate));
                    if (logger.isDebugEnabled()) {
                        logger.debug("Flume topology element " + channelName + " NEW coordinates: {" + flumeTopologyCentralSupSinkElement.getX() + "," + flumeTopologyCentralSupSinkElement.getY() + "}");
                    }

                    String centralInfSinkID = sinksIDs.get(sinksIDs.size() - i - 1);
                    FlumeTopology flumeTopologyCentralInfSinkElement = agentGraph.getVertex(centralInfSinkID, true);

                    int sink_Central_Inf_Y_Coordinate = inital_Central_Inf_Y_Coordinate - (gap_Y_Coordinates * (i - centralSupIndex));
                    flumeTopologyCentralInfSinkElement.setY(String.valueOf(sink_Central_Inf_Y_Coordinate));
                    if (logger.isDebugEnabled()) {
                        logger.debug("Flume topology element " + channelName + " NEW coordinates: {" + flumeTopologyCentralInfSinkElement.getX() + "," + flumeTopologyCentralInfSinkElement.getY() + "}");
                    }

                }

            } else {
                //Even number of sinks

                int centralIndex = sinksIDs.size() / 2;
                String centralSinkID = sinksIDs.get(centralIndex);
                FlumeTopology flumeTopologyCentralSinkElement = agentGraph.getVertex(centralSinkID, true);

                flumeTopologyCentralSinkElement.setY(flumeTopologyChannelElement.getY());
                if (logger.isDebugEnabled()) {
                    logger.debug("Flume topology element " + channelName + " NEW coordinates: {" + flumeTopologyCentralSinkElement.getX() + "," + flumeTopologyCentralSinkElement.getY() + "}");
                }

                int next_Y_Coordinate = FlumeConfiguratorTopologyUtils.getNextYCoordinate(average_Channel_Y_Coordinate, false);
                int gap_Y_Coordinates = next_Y_Coordinate - average_Channel_Y_Coordinate;
                for (int i = centralIndex + 1; i < sinksIDs.size() ; i++) {
                    String afterCentralSinkID = sinksIDs.get(i);
                    FlumeTopology flumeTopologyAfterCentralSinkElement = agentGraph.getVertex(afterCentralSinkID, true);

                    int sink_After_Central_Y_Coordinate = average_Channel_Y_Coordinate + (gap_Y_Coordinates * (i - centralIndex));
                    flumeTopologyAfterCentralSinkElement.setY(String.valueOf(sink_After_Central_Y_Coordinate));
                    if (logger.isDebugEnabled()) {
                        logger.debug("Flume topology element " + channelName + " NEW coordinates: {" + flumeTopologyAfterCentralSinkElement.getX() + "," + flumeTopologyAfterCentralSinkElement.getY() + "}");
                    }

                    String beforeCentralSinkID = sinksIDs.get(sinksIDs.size() - i - 1);
                    FlumeTopology flumeTopologyBeforeCentralSinkElement = agentGraph.getVertex(beforeCentralSinkID, true);

                    int sink_Before_Central_Y_Coordinate = average_Channel_Y_Coordinate - (gap_Y_Coordinates * (i - centralIndex));
                    flumeTopologyBeforeCentralSinkElement.setY(String.valueOf(sink_Before_Central_Y_Coordinate));
                    if (logger.isDebugEnabled()) {
                        logger.debug("Flume topology element " + channelName + " NEW coordinates: {" + flumeTopologyBeforeCentralSinkElement.getX() + "," + flumeTopologyBeforeCentralSinkElement.getY() + "}");
                    }
                }
            }
        }


        if (logger.isDebugEnabled()) {
            logger.debug("END drawSharedSources");
        }

    }


    /**
     * Relocate position coordinates of the sink groups.
     * @param agentGraph graph of the agent
     * @param agentName name of the agent
     * @param sinkGroupsMap Relation between agents and sink groups
     */
    private void relocateSinkGroups(IGraph agentGraph, String agentName, Map<String, List<FlumeTopology>> sinkGroupsMap) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN relocateSinkGroups");
        }

        if (agentGraph != null && agentName != null && !agentName.isEmpty() && sinkGroupsMap != null) {

            List<FlumeTopology> sinkGroupsList = sinkGroupsMap.get(agentName);

            if (sinkGroupsList != null) {

                for (FlumeTopology flumeTopologySinkGroupElement : sinkGroupsList) {

                    String sinkGroupName = flumeTopologySinkGroupElement.getData().get(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME);
                    List<FlumeTopology> listSinkGroupSinks = agentGraph.predecessorListOf(flumeTopologySinkGroupElement);

                    int min_sink_Y_coordinate = 0;
                    int max_sink_Y_coordinate = 0;
                    boolean isFirstSink = true;
                    for (FlumeTopology sinkGroupSinkName : listSinkGroupSinks) {

                        int sink_Y_coordinate = Integer.valueOf(sinkGroupSinkName.getY());

                        if (isFirstSink) {
                            min_sink_Y_coordinate = sink_Y_coordinate;
                            max_sink_Y_coordinate = sink_Y_coordinate;
                            isFirstSink = false;
                        } else {
                            if (sink_Y_coordinate <= min_sink_Y_coordinate) {
                                min_sink_Y_coordinate = sink_Y_coordinate;
                            } else if (sink_Y_coordinate >= max_sink_Y_coordinate) {
                                max_sink_Y_coordinate = sink_Y_coordinate;
                            }
                        }
                    }

                    int average_SinkGroup_Y_Coordinate = min_sink_Y_coordinate + ((max_sink_Y_coordinate - min_sink_Y_coordinate) / 2);
                    flumeTopologySinkGroupElement.setY(String.valueOf(average_SinkGroup_Y_Coordinate));
                    if (logger.isDebugEnabled()) {
                        logger.debug("Flume topology element " + sinkGroupName + " NEW coordinates: {" + flumeTopologySinkGroupElement.getX() + "," + flumeTopologySinkGroupElement.getY() + "}");
                    }

                }

            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END relocateSinkGroups");
        }
    }

    /**
     * Assign position coordinates for flume topology elements
     * @param elementName name of the topology element
     * @param flumeTopologyType type of the element
     * @param agentsSlice X-coordinate of the agent's slice
     * @param sourcesSlice X-coordinate of the source's slice
     * @param firstInterceptorSlice X-coordinate of the first interceptor slice
     * @param channelSlice X-coordinate of the channel's slice
     * @param sinkSlice X-coordinate of the sink's slice
     * @param sinkGroupSlice X-coordinate of the sinkgroup's slice
     * @param isFirstElementSlice boolean indicating if the element is the first element for the slice
     * @param interceptorNumberSlice number of interceptors slices
     * @param sliceWidth width of the slices
     */
    private void assignFlumeTopologyElementPositionCoordinates(String elementName, String flumeTopologyType, int agentsSlice, int sourcesSlice,
                                                       int firstInterceptorSlice,  int channelSlice, int sinkSlice, int sinkGroupSlice,
                                                       boolean isFirstElementSlice, int interceptorNumberSlice, int sliceWidth) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN assignFlumeTopologyElementPositionCoordinates");
        }

        int element_X_Coordinate = 0;
        int element_Y_Coordinate = 0;

        //Get Max Y coordinate
        int maxYCoordinate = FlumeConfiguratorTopologyUtils.getMaxYCoordinate(flumeTopologyList);
        logger.debug("Max Y coordinate:  " + elementName + " = " + maxYCoordinate + "px");

        if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT.equals(flumeTopologyType)) {
            element_X_Coordinate = agentsSlice;
        } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE.equals(flumeTopologyType)) {
            element_X_Coordinate = sourcesSlice;
        } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR.equals(flumeTopologyType)) {
            element_X_Coordinate = firstInterceptorSlice + ((interceptorNumberSlice - 1) * sliceWidth);
        } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL.equals(flumeTopologyType)) {
            element_X_Coordinate = channelSlice;
        } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK.equals(flumeTopologyType)) {
            element_X_Coordinate = sinkSlice;
        } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP.equals(flumeTopologyType)) {
            element_X_Coordinate = sinkGroupSlice;
        }


        if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT.equals(flumeTopologyType)) {
            element_Y_Coordinate = FlumeConfiguratorTopologyUtils.getNextYCoordinate(maxYCoordinate, isFirstElementSlice) + FlumeConfiguratorConstants.CANVAS_AGENTS_HEIGHT_PX_SEPARATION;
        } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE.equals(flumeTopologyType)) {
            element_Y_Coordinate = FlumeConfiguratorTopologyUtils.getNextYCoordinate(maxYCoordinate, isFirstElementSlice);
        } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR.equals(flumeTopologyType)) {
            element_Y_Coordinate = maxYCoordinate;
        } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL.equals(flumeTopologyType)) {
            element_Y_Coordinate = FlumeConfiguratorTopologyUtils.getNextYCoordinate(maxYCoordinate, isFirstElementSlice);
        } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK.equals(flumeTopologyType)) {
            element_Y_Coordinate = FlumeConfiguratorTopologyUtils.getNextYCoordinate(maxYCoordinate, isFirstElementSlice);
        } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP.equals(flumeTopologyType)) {
            element_Y_Coordinate = FlumeConfiguratorTopologyUtils.getNextYCoordinate(maxYCoordinate, isFirstElementSlice);
        }


        String elementID = FlumeConfiguratorTopologyUtils.getFlumeTopologyId(flumeTopologyList, elementName);
        FlumeTopology flumeTopology = FlumeConfiguratorTopologyUtils.getFlumeTopologyElement(flumeTopologyList, elementID);

        boolean coordinatesAssigned = false;

        if(flumeTopology.getX() == null || flumeTopology.getX().isEmpty()) {
            flumeTopology.setX(String.valueOf(element_X_Coordinate));
            coordinatesAssigned = true;
        }

        if (flumeTopology.getY() == null || flumeTopology.getY().isEmpty()) {
            flumeTopology.setY(String.valueOf(element_Y_Coordinate));
            coordinatesAssigned = true;
        }

        if (coordinatesAssigned) {
            if (logger.isDebugEnabled()) {
                logger.debug("Flume topology element " + elementName + " coordinates: {" + element_X_Coordinate + "," + element_Y_Coordinate + "}");
            }
        }


        if (logger.isDebugEnabled()) {
            logger.debug("END assignFlumeTopologyElementPositionCoordinates");
        }
    }


    /**
     * Generate common properties for Draw 2D elements
     * @param generatePositionCoordinates boolean indicating if position coordinates have been added
     */
    private void createCommonDraw2dProperties(boolean generatePositionCoordinates) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN createCommonDraw2dProperties");
        }

        //Create list of agents
        String[] agentsArray = new String[flumeGraphTopology.keySet().size()];
        agentsArray = flumeGraphTopology.keySet().toArray(agentsArray);

        for (FlumeTopology flumeTopology : flumeTopologyList) {
            String flumeType = flumeTopology.getType();

            if (!FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION.equals(flumeType)) {
                flumeTopology.setWidth(String.valueOf(FlumeConfiguratorConstants.CANVAS_ELEMENT_PX_WIDTH));
                flumeTopology.setHeight(String.valueOf(FlumeConfiguratorConstants.CANVAS_ELEMENT_PX_HEIGHT));


                if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT.equals(flumeType)) {
                    flumeTopology.setBgColor(FlumeConfiguratorConstants.CANVAS_AGENT_BGCOLOR);
                } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE.equals(flumeType)) {
                    flumeTopology.setBgColor(FlumeConfiguratorConstants.CANVAS_SOURCE_BGCOLOR);
                } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR.equals(flumeType)) {
                    flumeTopology.setBgColor(FlumeConfiguratorConstants.CANVAS_INTERCEPTOR_BGCOLOR);
                } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL.equals(flumeType)) {
                    flumeTopology.setBgColor(FlumeConfiguratorConstants.CANVAS_CHANNEL_BGCOLOR);
                } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK.equals(flumeType)) {
                    flumeTopology.setBgColor(FlumeConfiguratorConstants.CANVAS_SINK_BGCOLOR);
                } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP.equals(flumeType)) {
                    flumeTopology.setBgColor(FlumeConfiguratorConstants.CANVAS_SINKGROUP_BGCOLOR);
                }


                flumeTopology.setColor(FlumeConfiguratorConstants.CANVAS_ELEMENT_COLOR);
                flumeTopology.setStroke(FlumeConfiguratorConstants.CANVAS_ELEMENT_STROKE);
                flumeTopology.setAlpha(FlumeConfiguratorConstants.CANVAS_ELEMENT_ALPHA);
                flumeTopology.setRadius(FlumeConfiguratorConstants.CANVAS_ELEMENT_RADIUS);


                //Create agentGroup property
                String agentName = FlumeConfiguratorTopologyUtils.getGraphAgentFromConnections(flumeTopology.getId(), listTopologyConnections, flumeTopologyList, true);

                //Get index of agent
                int agentIndex = -1;
                for (int i=0; i<agentsArray.length; i++) {
                    if(agentName.equals(agentsArray[i])) {
                        agentIndex = i;
                    }
                }

                if (agentIndex == -1) {
                    throw new FlumeConfiguratorException("The group agent is not found for agent name: " + agentName);
                }

                //Set agentGroup property. Without position coordinates agent group property must be created
                if (!generatePositionCoordinates) {
                    flumeTopology.getData().put(FlumeConfiguratorConstants.AGENT_GROUP_PROPERTY, String.valueOf(agentIndex));
                }


            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END createCommonDraw2dProperties");
        }
    }


    /**
     * Convert topology elements in Draw 2D format
     */
    private void convertToDraw2DTopologyFormat() {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN convertToDraw2DTopologyFormat");
        }

        for (FlumeTopology flumeTopology : flumeTopologyList) {
            String flumeType = flumeTopology.getType();

            if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT.equals(flumeType)) {
                flumeTopology.setType(FlumeConfiguratorConstants.DRAW2D_START_TYPE);
                flumeTopology.setCssClass(FlumeConfiguratorConstants.DRAW2D_START_CSS_CLASS);
                //flumeTopology.setType(FlumeConfiguratorConstants.DRAW2D_AGENT_TYPE);
            } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE.equals(flumeType)) {
                flumeTopology.setType(FlumeConfiguratorConstants.DRAW2D_BETWEEN_TYPE);
                flumeTopology.setCssClass(FlumeConfiguratorConstants.DRAW2D_BETWEEN_CSS_CLASS);
                //flumeTopology.setType(FlumeConfiguratorConstants.DRAW2D_SOURCE_TYPE);
            } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR.equals(flumeType)) {
                flumeTopology.setType(FlumeConfiguratorConstants.DRAW2D_BETWEEN_TYPE);
                flumeTopology.setCssClass(FlumeConfiguratorConstants.DRAW2D_BETWEEN_CSS_CLASS);
                //flumeTopology.setType(FlumeConfiguratorConstants.DRAW2D_INTERCEPTOR_TYPE);
            } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL.equals(flumeType)) {
                flumeTopology.setType(FlumeConfiguratorConstants.DRAW2D_BETWEEN_TYPE);
                flumeTopology.setCssClass(FlumeConfiguratorConstants.DRAW2D_BETWEEN_CSS_CLASS);
                //flumeTopology.setType(FlumeConfiguratorConstants.DRAW2D_CHANNEL_TYPE);
            } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK.equals(flumeType)) {
                flumeTopology.setType(FlumeConfiguratorConstants.DRAW2D_BETWEEN_TYPE);
                flumeTopology.setCssClass(FlumeConfiguratorConstants.DRAW2D_BETWEEN_CSS_CLASS);
                //flumeTopology.setType(FlumeConfiguratorConstants.DRAW2D_SINK_TYPE);
            } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP.equals(flumeType)) {
                flumeTopology.setType(FlumeConfiguratorConstants.DRAW2D_END_TYPE);
                flumeTopology.setCssClass(FlumeConfiguratorConstants.DRAW2D_END_CSS_CLASS);
                //flumeTopology.setType(FlumeConfiguratorConstants.DRAW2D_SINKGROUP_TYPE);
            } else if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION.equals(flumeType)) {
                flumeTopology.setType(FlumeConfiguratorConstants.DRAW2D_CONNECTION_TYPE);
                flumeTopology.setCssClass(FlumeConfiguratorConstants.DRAW2D_CONNECTION_CSS_CLASS);
            }

            flumeTopologyConnectionList.add(new FlumeTopologyConnection(flumeTopology));
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END convertToDraw2DTopologyFormat");
        }
    }




    /**
     * Write the Draw2D Flume topology file generated
     * @throws IOException
     */
    private void writeDraw2DFlumeTopologyFile() throws IOException {


        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN writeDraw2DFlumeTopologyFile");
        }

        String fileName;
        BufferedWriter bw;

        if (pathDraw2DFlumeTopologyGeneratedFile == null || "".equals(pathDraw2DFlumeTopologyGeneratedFile)) {
            throw new InvalidPathException("", "The path is not valid");
        }

        File draw2DFlumeTopologyGeneratedFile = new File(pathDraw2DFlumeTopologyGeneratedFile);
        boolean isDirectory = draw2DFlumeTopologyGeneratedFile.isDirectory();

        if (isDirectory) {
            //The path of the file is determinated but not the name of the configuration file
            fileName = pathDraw2DFlumeTopologyGeneratedFile + File.separator + FlumeConfiguratorConstants.DRAW2D_FLUME_TOPOLOGY_FILE;
        } else {
            //The full path has been determinated
            fileName = pathDraw2DFlumeTopologyGeneratedFile;
        }

        bw = new BufferedWriter(new FileWriter(fileName));


        //Create Draw2D Flume Topology content
        //ObjectMapper objectMapper = new ObjectMapper();
        //String draw2DTopology = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(flumeTopologyList);

        String draw2DTopology = JSONStringSerializer.toJSONString(flumeTopologyConnectionList);

        logger.info(draw2DTopology);

        //Write the content
        bw.write(draw2DTopology);

        bw.flush();
        bw.close();

        if (logger.isDebugEnabled()) {
            logger.debug("END writeDraw2DFlumeTopologyFile");
        }


    }


    /**
     * Generate Draw 2D Flume Topology from a flume properties
     * @param flumeProperties Flume properties
     * @return String with the json representation of the flume topology
     */
    public String generateDraw2DFlumeTopologyFromProperties(Properties flumeProperties) {

        try {

            if (logger.isDebugEnabled()) {
                logger.debug("BEGIN generateDraw2DFlumeTopologyFromProperties");
            }

            boolean isPropertiesFileOK;
            FlumeConfigurationValidator flumeConfigurationValidator;

            //Create initial structures
            createInitialStructures();

            //Get the properties content
            flumeLinesProperties = new FlumeLinesProperties();
            LinkedProperties linkedProperties = new LinkedProperties();
            linkedProperties.populateFromProperties(flumeProperties);

            flumeLinesProperties.setProperties(linkedProperties);

            //Flume properties file validation
            flumeConfigurationValidator = new FlumeConfigurationValidator(flumeLinesProperties.getProperties());
            flumeConfigurationValidator.validateFlumeConfiguration();

            isPropertiesFileOK = flumeConfigurationValidator.isPropertiesFileOK();

            if (isPropertiesFileOK) {

                //Generate Agents Flume topology elements
                generateAgentsFlumeTopology();

                //Generate Sources Flume topology elements
                generateFlumeTopologyElements(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE, FlumeConfiguratorConstants.SOURCES_PROPERTY, FlumeConfiguratorConstants.SOURCES_LIST_PROPERTY_PART_INDEX);

                //Generate Channels Flume topology elements
                generateFlumeTopologyElements(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL, FlumeConfiguratorConstants.CHANNELS_PROPERTY, FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTY_PART_INDEX);

                //Generate Sinks Flume topology elements
                generateFlumeTopologyElements(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK, FlumeConfiguratorConstants.SINKS_PROPERTY, FlumeConfiguratorConstants.SINKS_LIST_PROPERTY_PART_INDEX);

                //Generate Sinkgroups Flume topology elements
                generateFlumeTopologyElements(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP, FlumeConfiguratorConstants.SINKGROUPS_PROPERTY, FlumeConfiguratorConstants.SINKGROUPS_LIST_PROPERTY_PART_INDEX);

                //Generate Interceptors Flume topology elements
                generateFlumeTopologyElements(FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR, FlumeConfiguratorConstants.INTERCEPTORS_PROPERTY, FlumeConfiguratorConstants.INTERCEPTORS_LIST_PROPERTY_PART_INDEX);

                //Generate Flume topology elements properties
                generateElementsProperties();

                //Generate Flume topology connections
                generateFlumeTopologyConnections();

                //Generate graph
                generateGraph();

                //Generate internal property flumeType and type according Draw2D
                generatePropertiesDraw2D();

                //Generate json flume topology
                String draw2DFlumeTopology = JSONStringSerializer.toJSONString(flumeTopologyConnectionList);

                if (logger.isDebugEnabled()) {
                    logger.debug(draw2DFlumeTopology);
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("END generateDraw2DFlumeTopologyFromProperties");
                }

                return draw2DFlumeTopology;

            } else {
                logger.error("[ERROR] The Flume configuration file is not correct");
                logger.error(flumeConfigurationValidator.getSbCheckErrors().toString());
                return null;
            }



        } catch (IOException e) {
            logger.error("An error has occurred on serialization of Flume topology", e);
            return null;
        } catch (Throwable t) {
            t.printStackTrace();
            logger.error("An error has occurred on the process", t);
            return null;
        }
    }


    /**
     * Generate Draw 2D Flume Topology (json format) from a Flume configuration string
     * @param flumePropertiesString Flume properties string
     * @return String with the json representation of the flume topology
     */
    public String generateDraw2DFlumeTopologyFromPropertiesString(String flumePropertiesString) {

        try {

            if (logger.isDebugEnabled()) {
                logger.debug("BEGIN generateDraw2DFlumeTopologyFromPropertiesString");
            }

            LinkedProperties linkedProperties = new LinkedProperties();
            List<String> lines;

            boolean isPropertiesFileOK;
            FlumeConfigurationValidator flumeConfigurationValidator;

            //Create initial structures
            createInitialStructures();

            //Get the properties content
            linkedProperties.load(new StringReader(flumePropertiesString));
            flumeLinesProperties.setProperties(linkedProperties);

            if (withComments) {
                String[] linesArray = flumePropertiesString.split(System.getProperty("line.separator"));
                lines = new ArrayList(Arrays.asList(linesArray));
                flumeLinesProperties.setLines(lines);
            }

            //Flume properties file validation
            flumeConfigurationValidator = new FlumeConfigurationValidator(flumeLinesProperties.getProperties());
            flumeConfigurationValidator.validateFlumeConfiguration();

            isPropertiesFileOK = flumeConfigurationValidator.isPropertiesFileOK();

            if (isPropertiesFileOK) {
                logger.info("check Flume configuration file: The Flume configuration file is correct");

                //Generate Agents Flume topology elements
                generateAgentsFlumeTopology();

                //Generate Sources Flume topology elements
                generateFlumeTopologyElements(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE, FlumeConfiguratorConstants.SOURCES_PROPERTY, FlumeConfiguratorConstants.SOURCES_LIST_PROPERTY_PART_INDEX);

                //Generate Channels Flume topology elements
                generateFlumeTopologyElements(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL, FlumeConfiguratorConstants.CHANNELS_PROPERTY, FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTY_PART_INDEX);

                //Generate Sinks Flume topology elements
                generateFlumeTopologyElements(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK, FlumeConfiguratorConstants.SINKS_PROPERTY, FlumeConfiguratorConstants.SINKS_LIST_PROPERTY_PART_INDEX);

                //Generate Sinkgroups Flume topology elements
                generateFlumeTopologyElements(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP, FlumeConfiguratorConstants.SINKGROUPS_PROPERTY, FlumeConfiguratorConstants.SINKGROUPS_LIST_PROPERTY_PART_INDEX);

                //Generate Interceptors Flume topology elements
                generateFlumeTopologyElements(FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR, FlumeConfiguratorConstants.INTERCEPTORS_PROPERTY, FlumeConfiguratorConstants.INTERCEPTORS_LIST_PROPERTY_PART_INDEX);

                //Generate Flume topology elements properties
                generateElementsProperties();

                //Generate Flume topology connections
                generateFlumeTopologyConnections();

                //Generate graph
                generateGraph();

                //Generate internal property flumeType and type according Draw2D
                generatePropertiesDraw2D();

                //Generate Draw2D Flume topology
                String draw2DFlumeTopology = JSONStringSerializer.toJSONString(flumeTopologyConnectionList);

                if (logger.isDebugEnabled()) {
                    logger.debug(draw2DFlumeTopology);
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("END generateDraw2DFlumeTopologyFromPropertiesString");
                }

                return draw2DFlumeTopology;

            } else {
                logger.error("[ERROR] The Flume configuration file is not correct");
                logger.error(flumeConfigurationValidator.getSbCheckErrors().toString());
                return null;
            }


        } catch (IOException e) {
            logger.error("An error has occurred on serialization of Flume topology", e);
            return null;
        } catch (Throwable t) {
            t.printStackTrace();
            logger.error("An error has occurred on the process", t);
            return null;
        }
    }



    /**
     * Generate the a Draw2D Flume Topology configuration from a Flume configuration file
     * @return true if the process is correct, false otherwise
     */
    public boolean generateDraw2DFlumeTopology() {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN generateDraw2DFlumeTopology");
        }

        boolean isPropertiesFileOK;
        FlumeConfigurationValidator flumeConfigurationValidator;

        try {

            //Create initial structures
            createInitialStructures();

            //Load the Flume properties file
            loadFlumePropertiesFile();

            //Generate a single information unit
            generateSingleLinesProperties();

            //Flume properties file validation
            flumeConfigurationValidator = new FlumeConfigurationValidator(flumeLinesProperties.getProperties());
            flumeConfigurationValidator.validateFlumeConfiguration();

            isPropertiesFileOK = flumeConfigurationValidator.isPropertiesFileOK();

            if (isPropertiesFileOK) {
                logger.info("check Flume configuration file: The Flume configuration file is correct");

                //Generate Agents Flume topology elements
                generateAgentsFlumeTopology();

                //Generate Sources Flume topology elements
                generateFlumeTopologyElements(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE, FlumeConfiguratorConstants.SOURCES_PROPERTY, FlumeConfiguratorConstants.SOURCES_LIST_PROPERTY_PART_INDEX);

                //Generate Channels Flume topology elements
                generateFlumeTopologyElements(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL, FlumeConfiguratorConstants.CHANNELS_PROPERTY, FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTY_PART_INDEX);

                //Generate Sinks Flume topology elements
                generateFlumeTopologyElements(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK, FlumeConfiguratorConstants.SINKS_PROPERTY, FlumeConfiguratorConstants.SINKS_LIST_PROPERTY_PART_INDEX);

                //Generate Sinkgroups Flume topology elements
                generateFlumeTopologyElements(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP, FlumeConfiguratorConstants.SINKGROUPS_PROPERTY, FlumeConfiguratorConstants.SINKGROUPS_LIST_PROPERTY_PART_INDEX);

                //Generate Interceptors Flume topology elements
                generateFlumeTopologyElements(FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR, FlumeConfiguratorConstants.INTERCEPTORS_PROPERTY, FlumeConfiguratorConstants.INTERCEPTORS_LIST_PROPERTY_PART_INDEX);

                //Generate Flume topology elements properties
                generateElementsProperties();

                //Generate Flume topology connections
                generateFlumeTopologyConnections();

                //Generate graph
                generateGraph();

                //Generate internal property flumeType and type according Draw2D
                generatePropertiesDraw2D();

                //Write the Draw2D Flume topology file
                writeDraw2DFlumeTopologyFile();

                logger.info("The process has ended correctly. The output file is in " + pathDraw2DFlumeTopologyGeneratedFile);

                logger.info("******* END DRAW2D FLUME TOPOLOGY GENERATOR PROCESS *****************");

                if (logger.isDebugEnabled()) {
                    logger.debug("END generateDraw2DFlumeTopology");
                }

                return true;

            } else {
                logger.error("[ERROR] The Flume configuration file is not correct");
                logger.error(flumeConfigurationValidator.getSbCheckErrors().toString());
                return false;
            }

        } catch (NoSuchFileException e) {
            logger.error("Flume properties file not found", e);
            return false;
        } catch (IOException e) {
            logger.error("An error has occurred on the load of Flume Properties file", e);
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
            sb.append("An error has occurred in Draw2D Flume Topology properties generator. Check the properties configuration file and generated logs");
        } else if (errorCode == 2) {
            sb.append("Incorrect parameter number");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("Usage: java -cp jarFile org.keedio.flume.configurator.builder.FlumeTopologyReversePropertiesGenerator <parameters>");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("The parameters are:");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("pathFlumeProperties => Path of the Flume configuration file");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("withComments => (boolean)");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                 true -> Property comments are added to Draw2D Flume topology configuration file");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                 false -> Property comments are not added to Draw2D Flume topology configuration file");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("generatePositionCoordinates => (boolean)");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                                 true -> The created Draw2D Flume topology configuration file includes position coordinates for its elements");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                                 false -> The created Draw2D Flume topology configuration file doesn't include position coordinates for its elements");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("pathDraw2DFlumeTopologyGeneratedFile => Path of the created Draw2D Flume topology configuration file.May be a directory (the directory must be exist)");
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
            logger.debug("******* BEGIN DRAW2D FLUME TOPOLOGY GENERATOR PROCESS *****************");
        }

        try {


            if (args.length == 4) {
                pathFlumeProperties = args[0];
                withComments = Boolean.valueOf(args[1]);
                generatePositionCoordinates = Boolean.valueOf(args[2]);
                pathDraw2DFlumeTopologyGeneratedFile = args[3];

                if (logger.isDebugEnabled()) {
                    logger.debug("Parameter pathFlumeProperties: " + pathFlumeProperties);
                    logger.debug("Parameter withComments: " + withComments);
                    logger.debug("Parameter generatePositionCoordinates: " + generatePositionCoordinates);
                    logger.debug("Parameter pathDraw2DFlumeTopologyGeneratedFile: " + pathDraw2DFlumeTopologyGeneratedFile);
                }

                FlumeTopologyReversePropertiesGenerator flumeTopologyReversePropertiesGenerator = new FlumeTopologyReversePropertiesGenerator();

                flumeTopologyReversePropertiesGenerator.generateDraw2DFlumeTopology();

            } else {
                logger.error(getErrorMessage(2));
                if (logger.isDebugEnabled()) {
                    logger.debug("******* DRAW2D FLUME TOPOLOGY GENERATOR PROCESS  *****************");
                }

            }

        } catch (Exception e) {
            logger.error(getErrorMessage(1), e);
            if (logger.isDebugEnabled()) {
                logger.debug("******* END DRAW2D FLUME TOPOLOGY GENERATOR PROCESS  *****************");
            }
        }
    }
}
