package org.keedio.flume.configurator.builder;

import java.io.*;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.util.*;

import org.apache.log4j.PropertyConfigurator;
import org.keedio.flume.configurator.constants.FlumeConfiguratorConstants;
import org.keedio.flume.configurator.structures.AgentConfigurationGroupProperties;
import org.keedio.flume.configurator.structures.AgentConfigurationProperties;
import org.keedio.flume.configurator.structures.LinkedProperties;
import org.keedio.flume.configurator.structures.PartialProperties;
import org.keedio.flume.configurator.utils.FlumeConfiguratorUtils;
import org.keedio.flume.configurator.validator.BaseConfigurationValidator;
import org.slf4j.LoggerFactory;

public class FlumePropertiesGenerator {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FlumePropertiesGenerator.class);

    private static String  pathBaseConfigurationProperties;
    private static String elementsCharacterSeparator;
    private static boolean multipleAgentConfigurationFiles = false;
    private static boolean addComments = true;
    private static String pathConfigurationGeneratedFile;

    private static Properties flumeConfigurationProperties = new LinkedProperties();
    private Map<String,LinkedProperties> configurationInitialMap = new LinkedHashMap<>();
    private Map<String, AgentConfigurationProperties> configurationFinalMap = new LinkedHashMap<>();
    private List<String> agentsList;
    private Map<String, List<String>> mapAgentSources;
    private Map<String, List<String>> mapSourcesInterceptors;



    /**
     * @param pathBaseConfigurationProperties the pathBaseConfigurationProperties to set
     */
    static void setPathBaseConfigurationProperties(
            String pathBaseConfigurationProperties) {
        FlumePropertiesGenerator.pathBaseConfigurationProperties = pathBaseConfigurationProperties;
    }



    /**
     * @param elementsCharacterSeparator the elementsCharacterSeparator to set
     */
    static void setElementsCharacterSeparator(
            String elementsCharacterSeparator) {
        FlumePropertiesGenerator.elementsCharacterSeparator = elementsCharacterSeparator;
    }




    /**
     * @param multipleAgentConfigurationFiles the multipleAgentConfigurationFiles to set
     */
    static void setMultipleAgentConfigurationFiles(
            boolean multipleAgentConfigurationFiles) {
        FlumePropertiesGenerator.multipleAgentConfigurationFiles = multipleAgentConfigurationFiles;
    }



    /**
     * @param addComments the addComments to set
     */
    static void setAddComments(boolean addComments) {
        FlumePropertiesGenerator.addComments = addComments;
    }



    /**
     * @param pathConfigurationGeneratedFile the pathConfigurationGeneratedFile to set
     */
    static void setPathConfigurationGeneratedFile(
            String pathConfigurationGeneratedFile) {
        FlumePropertiesGenerator.pathConfigurationGeneratedFile = pathConfigurationGeneratedFile;
    }



    /**
     * @return the flumeConfigurationProperties
     */
    static Properties getFlumeConfigurationProperties() {
        return flumeConfigurationProperties;
    }




    /**
     * @return the configurationInitialMap
     */
    Map<String, LinkedProperties> getConfigurationInitialMap() {
        return configurationInitialMap;
    }



    /**
     * @return the configurationFinalMap
     */
    Map<String, AgentConfigurationProperties> getConfigurationFinalMap() {
        return configurationFinalMap;
    }



    /**
     * @return the agentsList
     */
    List<String> getAgentsList() {
        return agentsList;
    }


    /**
     * @param mapAgentSources the mapAgentSources to set
     */
    void setMapAgentSources(Map<String, List<String>> mapAgentSources) {
        this.mapAgentSources = mapAgentSources;
    }


    /**
     * @return the mapAgentSources
     */
    public Map<String, List<String>> getMapAgentSources() {
        return mapAgentSources;
    }

    /**
     * @return the mapSourcesInterceptors
     */
    Map<String, List<String>> getMapSourcesInterceptors() {
        return mapSourcesInterceptors;
    }


    /**
     * Create initial structures
     */
    private void createInitialStructures() {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN createInitialStructures");
        }

        flumeConfigurationProperties = new LinkedProperties();
        configurationInitialMap = new LinkedHashMap<>();
        configurationFinalMap = new LinkedHashMap<>();
        agentsList = new ArrayList<>();
        mapAgentSources = new HashMap<>();
        mapSourcesInterceptors = new HashMap<>();

        if (logger.isDebugEnabled()) {
            logger.debug("END createInitialStructures");
        }
    }


    /**
     * Load the properties file
     * @throws IOException
     */
    private void loadPropertiesFile() throws IOException {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN loadPropertiesFile");
        }

        FileInputStream fis = new FileInputStream(pathBaseConfigurationProperties);
        flumeConfigurationProperties.clear();
        flumeConfigurationProperties.load(fis);

        if (logger.isDebugEnabled()) {
            logger.debug("END loadPropertiesFile");
        }

    }



    /**
     * Generate the initial structure with the information of the list of agents
     */
    private void generateAgentsList() {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN generateAgentsList");
        }

        //Set agents list
        agentsList = FlumeConfiguratorUtils.getAgentsListFromProperties(flumeConfigurationProperties, elementsCharacterSeparator);

        //Set agents to mapConfiguration
        for (String agentName : agentsList) {
            configurationInitialMap.put(agentName, new LinkedProperties());
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END generateAgentsList");
        }
    }



    /**
     * Generate the initial structure with the information of the elements of the agents
     *
     * @param prefixElementListProperty String with the prefix of the elements list processed
     * @param prefixElement String with the prefix of the elements processed
     * @return Map with the initial structure with the information of the desired elements of the agents
     */
    private Map<String, List<String>> generateAgentElements(String prefixElementListProperty, String prefixElement) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN generateAgentElements");
        }

        //Set agents Elements
        Map<String, List<String>> mapAgentElements = FlumeConfiguratorUtils.getAgentElementsMapFromProperties(flumeConfigurationProperties, prefixElementListProperty, elementsCharacterSeparator);

        for (String agentName : mapAgentElements.keySet()) {

            List<String> agentElements = mapAgentElements.get(agentName);

            StringBuilder sb = new StringBuilder();

            //Create string with the elements
            for (String agentElement : agentElements) {
                sb.append(agentElement);
                sb.append(FlumeConfiguratorConstants.WHITE_SPACE);
            }
            sb.setLength(sb.length() - 1);

            String propertyName = agentName + FlumeConfiguratorConstants.DOT_SEPARATOR + prefixElement;
            configurationInitialMap.get(agentName).put(propertyName, sb.toString());
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END generateAgentElements");
        }

        return mapAgentElements;
    }



    /**
     * Generate the initial structure with the information of interceptors of the sources
     */
    private void generateSourcesInterceptors() {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN generateSourcesInterceptors");
        }

        //Set interceptors
        mapSourcesInterceptors = FlumeConfiguratorUtils.getAgentElementsMapFromProperties(flumeConfigurationProperties, FlumeConfiguratorConstants.INTERCEPTORS_LIST_PROPERTIES_PREFIX, elementsCharacterSeparator);

        for (String agentName : mapAgentSources.keySet()) {
            List<String> agentSources = mapAgentSources.get(agentName);

            //Create string with the sources
            for (String agentSource : agentSources) {

                List<String> sourceInterceptors = mapSourcesInterceptors.get(agentSource);
                if (sourceInterceptors != null) {

                    StringBuilder sbListInterceptors = new StringBuilder();
                    for(String interceptor : sourceInterceptors) {
                        sbListInterceptors.append(interceptor);
                        sbListInterceptors.append(FlumeConfiguratorConstants.WHITE_SPACE);
                    }
                    sbListInterceptors.setLength(sbListInterceptors.length() - 1);

                    String fullInterceptorsPropertyName = FlumeConfiguratorUtils.constructFullPropertyName(agentName, FlumeConfiguratorConstants.SOURCES_PROPERTY, agentSource, false, FlumeConfiguratorConstants.INTERCEPTORS_PROPERTY);

                    configurationInitialMap.get(agentName).put(fullInterceptorsPropertyName, sbListInterceptors.toString());
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END generateSourcesInterceptors");
        }
    }


    /**
     * Generate the initial structure with the information of the common properties of the interceptors
     */
    private void generateInterceptorsCommonProperties() {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN generateInterceptorsCommonProperties");
        }

        //Set sources interceptors common properties
        LinkedProperties sourcesInterceptorsCommonProperties = FlumeConfiguratorUtils.getElementsCommonPropertiesFromProperties(flumeConfigurationProperties, FlumeConfiguratorConstants.INTERCEPTORS_COMMON_PROPERTY_PROPERTIES_PREFIX);

        for (String agentName : mapAgentSources.keySet()) {
            List<String> agentSources = mapAgentSources.get(agentName);

            //Create string with the sources
            for (String agentSource : agentSources) {

                List<String> listSourceInterceptors = mapSourcesInterceptors.get(agentSource);
                if (listSourceInterceptors != null) {
                    for (String interceptorName : listSourceInterceptors) {

                        for (Object sourceInterceptorCommonProperty : sourcesInterceptorsCommonProperties.keySet()) {
                            StringBuilder sbInterceptorPropertyName = new StringBuilder();
                            Object interceptorPropertyValue = sourcesInterceptorsCommonProperties.get(sourceInterceptorCommonProperty);

                            sbInterceptorPropertyName.append(agentName);
                            sbInterceptorPropertyName.append(FlumeConfiguratorConstants.DOT_SEPARATOR);
                            sbInterceptorPropertyName.append(FlumeConfiguratorConstants.SOURCES_PROPERTY);
                            sbInterceptorPropertyName.append(FlumeConfiguratorConstants.DOT_SEPARATOR);
                            sbInterceptorPropertyName.append(agentSource);
                            sbInterceptorPropertyName.append(FlumeConfiguratorConstants.DOT_SEPARATOR);
                            sbInterceptorPropertyName.append(FlumeConfiguratorConstants.INTERCEPTORS_PROPERTY);
                            sbInterceptorPropertyName.append(FlumeConfiguratorConstants.DOT_SEPARATOR);
                            sbInterceptorPropertyName.append(interceptorName);
                            sbInterceptorPropertyName.append(FlumeConfiguratorConstants.DOT_SEPARATOR);
                            sbInterceptorPropertyName.append(sourceInterceptorCommonProperty);

                            configurationInitialMap.get(agentName).put(sbInterceptorPropertyName.toString(), interceptorPropertyValue);
                        }
                    }
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END generateInterceptorsCommonProperties");
        }
    }



    /**
     * Generate the initial structure with the information of the partial properties of the interceptors
     */
    private void generateInterceptorsPartialProperties() {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN generateInterceptorsPartialProperties");
        }

        //Set sources interceptors partial properties
        PartialProperties sourcesInterceptorsPartialProperties = FlumeConfiguratorUtils.getElementsPartialPropertiesFromProperties(flumeConfigurationProperties, FlumeConfiguratorConstants.INTERCEPTORS_PARTIAL_PROPERTY_PROPERTIES_PREFIX, elementsCharacterSeparator);

        LinkedProperties appliedInterceptorsElements = sourcesInterceptorsPartialProperties.getAppliedElements();
        LinkedProperties interceptorsPropertiesValues = sourcesInterceptorsPartialProperties.getPropertiesValues();
        LinkedProperties interceptorsPropertiesComments = sourcesInterceptorsPartialProperties.getPropertiesComments();

        for (Object partialProperty : appliedInterceptorsElements.keySet()) {
            String appliedInterceptorsElementsString = (String) appliedInterceptorsElements.get(partialProperty);
            String[] appliedInterceptorsElementsArray = FlumeConfiguratorUtils.splitWithoutSpacesOptional(appliedInterceptorsElementsString,true,elementsCharacterSeparator);

            String interceptorsPropertiesValuesString = (String) interceptorsPropertiesValues.get(partialProperty);
            String[] interceptorsPropertiesValuesArray = FlumeConfiguratorUtils.splitWithoutSpacesOptional(interceptorsPropertiesValuesString,true,elementsCharacterSeparator);

            String interceptorsPropertiesCommentsString = (String) interceptorsPropertiesComments.get(partialProperty);
            String[] interceptorsPropertiesCommentsArray = FlumeConfiguratorUtils.splitWithoutSpacesOptional(interceptorsPropertiesCommentsString,false,elementsCharacterSeparator);

            for (int index=0; index < appliedInterceptorsElementsArray.length; index++) {

                String appliedInterceptor = appliedInterceptorsElementsArray[index];
                String interceptorPropertyValue = interceptorsPropertiesValuesArray[index];
                String interceptorPropertyComment = (interceptorsPropertiesCommentsArray != null)? interceptorsPropertiesCommentsArray[index].trim() : null;

                logger.debug("******** appliedInterceptor: " + appliedInterceptor);

                //Get the source(s) of the interceptor
                List<String> sourcesList = FlumeConfiguratorUtils.getElementsAgents(mapSourcesInterceptors, appliedInterceptor);

                for (String sourceName : sourcesList) {
                    logger.debug("******** sourceName: " + sourceName);

                    //Get the agent(s) of the source
                    List<String> agentsSourceList = FlumeConfiguratorUtils.getElementsAgents(mapAgentSources, sourceName);

                    for (String agentName : agentsSourceList) {
                        logger.debug("******** agentName: " + agentName);

                        if (interceptorPropertyComment != null) {
                            //Construct interceptor property comment name (comments before)
                            String fullInterceptorPropertyCommentName = FlumeConfiguratorUtils.constructFullInterceptorPropertyName(agentName, sourceName, appliedInterceptor, true, (String) partialProperty);
                            logger.debug("******** fullInterceptorPropertyCommentName: " + fullInterceptorPropertyCommentName);

                            //Check if the interceptor property exists in agent configuration
                            if (configurationInitialMap.get(agentName).containsKey(fullInterceptorPropertyCommentName)) {
                                logger.debug("----MODIFY INTERCEPTOR comment property " + fullInterceptorPropertyCommentName + " to [Agent= " + agentName + " , Source= " + sourceName + ", Interceptor= " + appliedInterceptor + "]");

                                //Modify interceptor property comment to agent
                                configurationInitialMap.get(agentName).put(fullInterceptorPropertyCommentName, interceptorPropertyComment);


                            } else {
                                logger.debug("----ADD INTERCEPTOR comment property " + fullInterceptorPropertyCommentName + " to [Agent= " + agentName + " , Source= " + sourceName + ", Interceptor= " + appliedInterceptor + "]");

                                //ADD interceptor property to agent
                                configurationInitialMap.get(agentName).put(fullInterceptorPropertyCommentName, interceptorPropertyComment);

                            }
                        }


                        //Construct interceptor property name
                        String fullInterceptorPropertyName = FlumeConfiguratorUtils.constructFullInterceptorPropertyName(agentName, sourceName, appliedInterceptor, false, (String) partialProperty);
                        logger.debug("******** fullInterceptorPropertyName: " + fullInterceptorPropertyName);

                        //Check if the interceptor property exists in agent configuration
                        if (configurationInitialMap.get(agentName).containsKey(fullInterceptorPropertyName)) {
                            logger.debug("----MODIFY INTERCEPTOR property " + fullInterceptorPropertyName + " to [Agent= " + agentName + " , Source= " + sourceName + ", Interceptor= " + appliedInterceptor + "]");

                            //Modify property to agent
                            configurationInitialMap.get(agentName).put(fullInterceptorPropertyName, interceptorPropertyValue);


                        } else {
                            logger.debug("----ADD INTERCEPTOR property " + fullInterceptorPropertyName + " to [Agent= " + agentName + " , Source= " + sourceName + ", Interceptor= " + appliedInterceptor + "]");

                            //ADD property to agent
                            configurationInitialMap.get(agentName).put(fullInterceptorPropertyName, interceptorPropertyValue);
                        }



                    }
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END generateInterceptorsPartialProperties");
        }
    }



    /**
     * Generate the initial structure with the information of the common properties of the elements of the agents
     *
     * @param prefixCommonProperty String with the prefix of the type of common property processed
     * @param prefixProperty String with the prefix of the type of elements processed
     * @param mapAgentElements Map with the information required of the type of element processed
     *
     */
    private void generateElementsCommonProperties(String prefixCommonProperty, String prefixProperty, Map<String, List<String>> mapAgentElements) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN generateElementsCommonProperties");
        }

        //Set elements common properties
        LinkedProperties elementsCommonProperties = FlumeConfiguratorUtils.getElementsCommonPropertiesFromProperties(flumeConfigurationProperties, prefixCommonProperty);

        for (String agentName : mapAgentElements.keySet()) {
            List<String> agentElements = mapAgentElements.get(agentName);

            //Create string with the elements
            for (String agentElement : agentElements) {

                for (Object elementCommonProperty : elementsCommonProperties.keySet()) {

                    Object elementPropertyValue = elementsCommonProperties.get(elementCommonProperty);

                    String fullElementPropertyName = FlumeConfiguratorUtils.constructFullPropertyName(agentName, prefixProperty, agentElement, false, (String) elementCommonProperty);

                    configurationInitialMap.get(agentName).put(fullElementPropertyName, elementPropertyValue);
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END generateElementsCommonProperties");
        }
    }



    /**
     * Generate the initial structure with the information of the partial properties of the elements of the agents
     * @param prefixPartialProperty String with the prefix of the type of partial property processed
     * @param prefixProperty String with the prefix of the type of elements processed
     * @param mapAgentElements Map with the information required of the type of element processed
     */
    private void generateElementsPartialProperties(String prefixPartialProperty, String prefixProperty, Map<String, List<String>> mapAgentElements) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN generateElementsPartialProperties");
        }

        //Set elements partial properties
        PartialProperties elementsPartialProperties = FlumeConfiguratorUtils.getElementsPartialPropertiesFromProperties(flumeConfigurationProperties, prefixPartialProperty, elementsCharacterSeparator);

        LinkedProperties appliedElements = elementsPartialProperties.getAppliedElements();
        LinkedProperties elementsPropertiesValues = elementsPartialProperties.getPropertiesValues();
        LinkedProperties elementsPropertiesComments = elementsPartialProperties.getPropertiesComments();

        for (Object partialProperty : appliedElements.keySet()) {

            String appliedElementsString = (String) appliedElements.get(partialProperty);
            String[] appliedElementsArray = FlumeConfiguratorUtils.splitWithoutSpacesOptional(appliedElementsString,true,elementsCharacterSeparator);

            String elementsPropertiesValuesString = (String) elementsPropertiesValues.get(partialProperty);
            String[] elementsPropertiesValuesArray = FlumeConfiguratorUtils.splitWithoutSpacesOptionalKeepInternalSpaces(elementsPropertiesValuesString,true,elementsCharacterSeparator);


            String elementsPropertiesCommentsString = (String) elementsPropertiesComments.get(partialProperty);
            String[] elementsPropertiesCommentsArray = FlumeConfiguratorUtils.splitWithoutSpacesOptional(elementsPropertiesCommentsString,false,elementsCharacterSeparator);

            for (int index=0; index < appliedElementsArray.length; index++) {

                String appliedElement = appliedElementsArray[index];
                String elementPropertyValue = elementsPropertiesValuesArray[index];
                String elementPropertyComment = (elementsPropertiesCommentsArray != null)? elementsPropertiesCommentsArray[index] : null;

                logger.debug("******** appliedElement: " + appliedElement);

                //Get the agent of the element
                List<String> agentList = FlumeConfiguratorUtils.getElementsAgents(mapAgentElements, appliedElement);

                for (String agentName : agentList) {
                    logger.debug("******** agentName: " + agentName);

                    if (elementPropertyComment != null) {
                        //Construct element property comment name
                        String fullElementPropertyCommentName = FlumeConfiguratorUtils.constructFullPropertyName(agentName, prefixProperty, appliedElement, true, (String) partialProperty);
                        logger.debug("******** fullElementPropertyCommentName: " + fullElementPropertyCommentName);

                        //Check if the property exists in agent configuration
                        if (configurationInitialMap.get(agentName).containsKey(fullElementPropertyCommentName)) {
                            logger.debug("----MODIFY " + prefixProperty + " comment property [" + fullElementPropertyCommentName + "] to [Agent= " + agentName + " , " + prefixProperty + " = " + appliedElement + "]");

                            //Modify property comment to agent
                            configurationInitialMap.get(agentName).put(fullElementPropertyCommentName, elementPropertyComment);

                        } else {
                            logger.debug("----ADD " + prefixProperty + " comment property [" + fullElementPropertyCommentName + "] to [Agent= " + agentName + " , " + prefixProperty + " = " + appliedElement + "]");

                            //ADD property to agent
                            configurationInitialMap.get(agentName).put(fullElementPropertyCommentName, elementPropertyComment);
                        }
                    }

                    //Construct property name
                    String fullElementPropertyName = FlumeConfiguratorUtils.constructFullPropertyName(agentName, prefixProperty, appliedElement, false, (String) partialProperty);
                    logger.debug("******** fullElementPropertyName: " + fullElementPropertyName);

                    //Check if the property exists in agent configuration
                    if (configurationInitialMap.get(agentName).containsKey(fullElementPropertyName)) {
                        logger.debug("----MODIFY " + prefixProperty + " property [" + fullElementPropertyName + "] to [Agent= " + agentName + " , " + prefixProperty + " = " + appliedElement + "]");

                        //Modify property to agent
                        configurationInitialMap.get(agentName).put(fullElementPropertyName, elementPropertyValue);

                    } else {
                        logger.debug("----ADD " + prefixProperty + " property [" + fullElementPropertyName + "] to [Agent= " + agentName + " , " + prefixProperty + " = " + appliedElement + "]");

                        //ADD property to agent
                        configurationInitialMap.get(agentName).put(fullElementPropertyName, elementPropertyValue);
                    }



                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END generateElementsPartialProperties");
        }
    }



    /**
     * Generate the final structure with the information of the configuration
     */
    @SuppressWarnings("unchecked")
    private void generateFinalStructureMap() {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN generateFinalStructureMap");
        }

        //Get groups structure
        Map<String, Map<String, String>> agentGroupsConfiguration = FlumeConfiguratorUtils.getAgentConfigurationGroupsMapFromProperties(flumeConfigurationProperties, elementsCharacterSeparator);

        //Initialize final structure
        for (String agentName : agentsList) {
            configurationFinalMap.put(agentName, new AgentConfigurationProperties());
            Set<String> setAgentGroups = FlumeConfiguratorUtils.getAgentGroupsSet(agentGroupsConfiguration, agentName);

            for (String agentGroup : setAgentGroups) {
                configurationFinalMap.get(agentName).getMapGroupProperties().put(agentGroup, new AgentConfigurationGroupProperties());
            }
        }

        for (String agentName : configurationInitialMap.keySet()) {
            LinkedProperties agentProperties = configurationInitialMap.get(agentName);

            for (Object agentProperty : agentProperties.entrySet()) {
                Map.Entry<?,?> agentPropertyEntry = (Map.Entry<?,?>) agentProperty;

                String propertyKey = (String) agentPropertyEntry.getKey();

                String[] propertyKeyPartsArray = propertyKey.split(FlumeConfiguratorConstants.DOT_REGEX);

                if ((propertyKeyPartsArray.length == 2) && (propertyKeyPartsArray[1].equals(FlumeConfiguratorConstants.SOURCES_PROPERTY))) {
                    //Sources general property
                    configurationFinalMap.get(agentName).getListGeneralProperties().add(agentPropertyEntry);
                    logger.debug("ADD GENERAL SOURCES PROPERTY");

                } else if ((propertyKeyPartsArray.length == 2) && (propertyKeyPartsArray[1].equals(FlumeConfiguratorConstants.CHANNELS_PROPERTY))) {
                    //Channels general property
                    configurationFinalMap.get(agentName).getListGeneralProperties().add(agentPropertyEntry);
                    logger.debug("ADD GENERAL CHANNELS PROPERTY");

                } else if ((propertyKeyPartsArray.length == 2) && (propertyKeyPartsArray[1].equals(FlumeConfiguratorConstants.SINKS_PROPERTY))) {
                    //Sinks general property
                    configurationFinalMap.get(agentName).getListGeneralProperties().add(agentPropertyEntry);
                    logger.debug("ADD GENERAL SINKS PROPERTY");

                } else if ((propertyKeyPartsArray.length == 2) && (propertyKeyPartsArray[1].equals(FlumeConfiguratorConstants.SINKGROUPS_PROPERTY))) {
                    //Sink groups general property
                    configurationFinalMap.get(agentName).getListGeneralProperties().add(agentPropertyEntry);
                    logger.debug("ADD GENERAL SINK GROUPS PROPERTY");

                } else if ((propertyKeyPartsArray.length > 2) && (propertyKeyPartsArray[1].equals(FlumeConfiguratorConstants.SOURCES_PROPERTY)) && (!propertyKeyPartsArray[3].equals(FlumeConfiguratorConstants.INTERCEPTORS_PROPERTY))) {
                    //Sources properties (no interceptors properties)
                    String sourceName = propertyKeyPartsArray[2];
                    String groupName = FlumeConfiguratorUtils.getGroupFromElement(agentGroupsConfiguration, sourceName);
                    logger.debug("GROUP OF PROPERTY " + sourceName + " = " + groupName);

                    configurationFinalMap.get(agentName).getMapGroupProperties().get(groupName).getListSourceProperties().add(agentPropertyEntry);
                    logger.debug("ADD SOURCE PROPERTY FOR GROUP " + groupName);

                } else if ((propertyKeyPartsArray.length > 2) && (propertyKeyPartsArray[1].equals(FlumeConfiguratorConstants.CHANNELS_PROPERTY))) {
                    //Channels properties
                    String channelName = propertyKeyPartsArray[2];
                    String groupName = FlumeConfiguratorUtils.getGroupFromElement(agentGroupsConfiguration, channelName);
                    logger.debug("GROUP OF PROPERTY " + channelName + " = " + groupName);

                    configurationFinalMap.get(agentName).getMapGroupProperties().get(groupName).getListChannelProperties().add(agentPropertyEntry);
                    logger.debug("ADD Channel Property for Group " + groupName);

                } else if ((propertyKeyPartsArray.length > 2) && (propertyKeyPartsArray[1].equals(FlumeConfiguratorConstants.SINKS_PROPERTY))) {
                    //Sinks properties
                    String sinkName = propertyKeyPartsArray[2];
                    String groupName = FlumeConfiguratorUtils.getGroupFromElement(agentGroupsConfiguration, sinkName);
                    logger.debug("Group of Property " + sinkName + " = " + groupName);

                    configurationFinalMap.get(agentName).getMapGroupProperties().get(groupName).getListSinkProperties().add(agentPropertyEntry);
                    logger.debug("ADD SINK PROPERTY FOR GROUP " + groupName);

                } else if ((propertyKeyPartsArray.length > 2) && (propertyKeyPartsArray[1].equals(FlumeConfiguratorConstants.SINKGROUPS_PROPERTY))) {
                    //Sink groups properties
                    String sinkGroupName = propertyKeyPartsArray[2];
                    String groupName = FlumeConfiguratorUtils.getGroupFromElement(agentGroupsConfiguration, sinkGroupName);
                    logger.debug("Group of Property " + sinkGroupName + " = " + groupName);

                    configurationFinalMap.get(agentName).getMapGroupProperties().get(groupName).getListSinkGroupProperties().add(agentPropertyEntry);
                    logger.debug("ADD SINKGROUP PROPERTY FOR GROUP " + groupName);

                } else if ((propertyKeyPartsArray.length == 4) && (propertyKeyPartsArray[3].equals(FlumeConfiguratorConstants.INTERCEPTORS_PROPERTY))) {
                    //Sources Interceptors
                    String sourceName = propertyKeyPartsArray[2];
                    String groupName = FlumeConfiguratorUtils.getGroupFromElement(agentGroupsConfiguration, sourceName);
                    String interceptorName =  propertyKeyPartsArray[3];
                    logger.debug("Group of Property " + sourceName + " = " + groupName);

                    Map<String, List<String>>  mapInterceptorsListGroup = configurationFinalMap.get(agentName).getMapGroupProperties().get(groupName).getMapSourceInterceptorProperties();

                    List interceptorListGroup = mapInterceptorsListGroup.get(interceptorName);
                    if (interceptorListGroup != null) {
                        interceptorListGroup.add(agentPropertyEntry);
                    } else {
                        interceptorListGroup = new ArrayList<>();
                        interceptorListGroup.add(agentPropertyEntry);
                        mapInterceptorsListGroup.put(interceptorName, interceptorListGroup);
                    }

                    logger.debug("ADD Interceptors for Group: " + groupName);

                } else if ((propertyKeyPartsArray.length > 4) && (propertyKeyPartsArray[3].equals(FlumeConfiguratorConstants.INTERCEPTORS_PROPERTY))) {
                    //Interceptors properties
                    String sourceName = propertyKeyPartsArray[2];
                    String groupName = FlumeConfiguratorUtils.getGroupFromElement(agentGroupsConfiguration, sourceName);
                    String interceptorName =  propertyKeyPartsArray[4];
                    logger.debug("GROUP OF PROPERTY " + sourceName + " = " + groupName);

                    Map<String, List<String>>  mapInterceptorsListGroup = configurationFinalMap.get(agentName).getMapGroupProperties().get(groupName).getMapSourceInterceptorProperties();

                    List interceptorListGroup = mapInterceptorsListGroup.get(interceptorName);
                    if (interceptorListGroup != null) {
                        interceptorListGroup.add(agentPropertyEntry);
                    } else {
                        interceptorListGroup = new ArrayList<>();
                        interceptorListGroup.add(agentPropertyEntry);
                        mapInterceptorsListGroup.put(interceptorName, interceptorListGroup);
                    }

                    logger.debug("ADD Interceptors Property for Group: " + groupName);
                }

            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END generateFinalStructureMap");
        }
    }


    /**
     * Generate the Flume configuration files
     * @throws IOException
     */
    private void writeConfigurationFiles() throws IOException {


        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN writeConfigurationFiles");
        }

        String fileName;
        String fileBaseName;
        BufferedWriter bw;

        if ((pathConfigurationGeneratedFile == null) || ("".equals(pathConfigurationGeneratedFile))) {
            throw new InvalidPathException("", "The path is not valid");
        }

        File configurationGeneratedFile = new File(pathConfigurationGeneratedFile);
        boolean isDirectory = configurationGeneratedFile.isDirectory();

        if (!multipleAgentConfigurationFiles) {

            //Only 1 configuration file is created for all agents
            if (isDirectory) {
                //The path of the file is determinated but not the name of the configuration file
                fileName = pathConfigurationGeneratedFile + File.separator + FlumeConfiguratorConstants.FLUME_FILE_CONFIGURATION_FILE;
            } else {
                //The full path has been determinated
                fileName = pathConfigurationGeneratedFile;
            }

            bw = new BufferedWriter(new FileWriter(fileName));

            //Write the content
            String agentConfiguration;
            boolean isFirstAgent = true;
            for (String agentName : configurationFinalMap.keySet()) {

                AgentConfigurationProperties agentConfigurationProperties = configurationFinalMap.get(agentName);

                if (isFirstAgent) {
                    agentConfiguration = FlumeConfiguratorUtils.getStringAgentConfiguration(agentConfigurationProperties, agentName, addComments, true);
                    isFirstAgent = false;
                } else {
                    agentConfiguration = FlumeConfiguratorUtils.getStringAgentConfiguration(agentConfigurationProperties, agentName, addComments, false);
                }
                logger.info(agentConfiguration);

                bw.write(agentConfiguration);

            }

            bw.flush();
            bw.close();

        } else {

            //A configuration file is generated for each agent
            if (isDirectory) {
                ///The path of the file is determinated but not the name of the configuration file
                fileBaseName = pathConfigurationGeneratedFile + File.separator;
            } else {
                //The full path has been determinated. The full path will be used as base of the final agent file name
                fileBaseName = pathConfigurationGeneratedFile + FlumeConfiguratorConstants. UNDERSCORE_SEPARATOR;
            }

            for (String agentName : configurationFinalMap.keySet()) {

                fileName = fileBaseName + agentName + FlumeConfiguratorConstants. UNDERSCORE_SEPARATOR + FlumeConfiguratorConstants.FLUME_FILE_CONFIGURATION_SUFFIX_DEFAULT;

                bw = new BufferedWriter(new FileWriter(fileName));

                AgentConfigurationProperties agentConfigurationProperties = configurationFinalMap.get(agentName);

                String agentConfiguration = FlumeConfiguratorUtils.getStringAgentConfiguration(agentConfigurationProperties, agentName, addComments, true);

                logger.info(agentConfiguration);

                bw.write(agentConfiguration);

                bw.flush();

                bw.close();
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END writeConfigurationFiles");
        }
    }



    /**
     * Validate the initial properties file information and generate the Flume configuration file(s)
     * @return true if the process is correct, false otherwise
     */
    public boolean buildConfigurationMap() {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN buildConfigurationMap");
        }

        boolean isPropertiesFileOK;
        BaseConfigurationValidator configurationValidator;
        Map<String, List<String>> mapAgentChannels;
        Map<String, List<String>> mapAgentSinks;
        Map<String, List<String>> mapAgentSinkGroups;

        try {

            //Create initial structures
            createInitialStructures();

            //Load the properties file
            loadPropertiesFile();

            //Properties file validation
            configurationValidator = new BaseConfigurationValidator(flumeConfigurationProperties,elementsCharacterSeparator);
            configurationValidator.validateBaseConfiguration();

            isPropertiesFileOK = configurationValidator.isPropertiesFileOK();

            if (isPropertiesFileOK) {
                logger.info("checkPropertiesFile: The properties file is correct");

                //Generate Agents List
                generateAgentsList();

                //Generate Agents Sources
                mapAgentSources = generateAgentElements(FlumeConfiguratorConstants.SOURCES_LIST_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SOURCES_PROPERTY);

                //Generate Agents Channels
                mapAgentChannels = generateAgentElements(FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX, FlumeConfiguratorConstants.CHANNELS_PROPERTY);

                //Generate Agents Sinks
                mapAgentSinks = generateAgentElements(FlumeConfiguratorConstants.SINKS_LIST_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SINKS_PROPERTY);

                //Generate Agents Sink Groups
                mapAgentSinkGroups = generateAgentElements(FlumeConfiguratorConstants.SINKGROUPS_LIST_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SINKGROUPS_PROPERTY);

                //Generate Sources Common Properties
                generateElementsCommonProperties(FlumeConfiguratorConstants.SOURCES_COMMON_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SOURCES_PROPERTY, mapAgentSources);

                //Generate Sources Partial Properties
                generateElementsPartialProperties(FlumeConfiguratorConstants.SOURCES_PARTIAL_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SOURCES_PROPERTY, mapAgentSources);

                //Generate Sources Interceptors
                generateSourcesInterceptors();

                //Generate Interceptors Common Properties
                generateInterceptorsCommonProperties();

                //Generate Interceptors Partial Properties
                generateInterceptorsPartialProperties();

                //Generate Channels Common Properties
                generateElementsCommonProperties(FlumeConfiguratorConstants.CHANNELS_COMMON_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.CHANNELS_PROPERTY, mapAgentChannels);

                //Generate Channels Partial Properties
                generateElementsPartialProperties(FlumeConfiguratorConstants.CHANNELS_PARTIAL_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.CHANNELS_PROPERTY, mapAgentChannels);

                //Generate Sinks Common Properties
                generateElementsCommonProperties(FlumeConfiguratorConstants.SINKS_COMMON_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SINKS_PROPERTY, mapAgentSinks);

                //Generate Sinks Partial Properties
                generateElementsPartialProperties(FlumeConfiguratorConstants.SINKS_PARTIAL_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SINKS_PROPERTY, mapAgentSinks);

                //Generate SinkGroups Common Properties
                generateElementsCommonProperties(FlumeConfiguratorConstants.SINKGROUPS_COMMON_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SINKGROUPS_PROPERTY, mapAgentSinkGroups);

                //Generate SinkGroups Partial Properties
                generateElementsPartialProperties(FlumeConfiguratorConstants.SINKGROUPS_PARTIAL_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SINKGROUPS_PROPERTY, mapAgentSinkGroups);

                //Generate final structure
                generateFinalStructureMap();

                //Write Flume configuration files
                writeConfigurationFiles();

                logger.info("The process has ended correctly. The output files are in " + pathConfigurationGeneratedFile);
                logger.info("******* END FLUME CONFIGURATOR PROCESS *****************");
                return true;


            } else {
                logger.error("[ERROR] The configuration file is not correct");
                logger.error(configurationValidator.getSbCheckErrors().toString());
                return false;
            }




        } catch (FileNotFoundException e) {
            logger.error("Properties file not found", e);
            return false;
        } catch (IOException e) {
            logger.error("An error has occurred on the load of properties file", e);
            return false;
        }

    }

    /**
     * Generate a Flume configuration string from a base (template) configuration properties
     * @param properties Base (template) properties
     * @param characterSeparator Separator character used in base (template) configuration file
     * @param withComments  true -> Property comments are added to Flume configuration file");
                            false -> Property comments are not added to Flume configuration file");
     * @param writeFlumeConfigurationFiles boolean  true -> The Flume configuration file(s) are generated
     *                                              false -> The Flume configuration file(s) aren't generated
     * @param pathFlumeConfigurationGeneratedFile Path of the Flume configuration file(s) (if the file(s) are generated)
     * @param multipleFlumeConfigurationFiles   true -> Every agent has an own configuration file
                                                false -> All agents configuration in one single file
     * @return String with the Flume configuration generated
     */
    public String buildConfigurationMapFromStringProperties(String properties, String characterSeparator, boolean withComments, boolean writeFlumeConfigurationFiles,
                                                            String pathFlumeConfigurationGeneratedFile, boolean multipleFlumeConfigurationFiles) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN buildConfigurationMapFromStringProperties");
        }

        boolean isPropertiesFileOK;
        BaseConfigurationValidator baseConfigurationValidator;
        Map<String, List<String>> mapAgentChannels;
        Map<String, List<String>> mapAgentSinks;
        Map<String, List<String>> mapAgentSinkGroups;

        try {

            //Create initial structures
            createInitialStructures();

            elementsCharacterSeparator = characterSeparator;
            addComments = withComments;

            //Load the properties string
            flumeConfigurationProperties.load(new StringReader(properties));

            //Properties file validation
            baseConfigurationValidator = new BaseConfigurationValidator(flumeConfigurationProperties,elementsCharacterSeparator);
            baseConfigurationValidator.validateBaseConfiguration();

            isPropertiesFileOK = baseConfigurationValidator.isPropertiesFileOK();

            if (isPropertiesFileOK) {
                logger.info("checkPropertiesFile: The properties file is correct");


                //Generate Agents List
                generateAgentsList();

                //Generate Agents Sources
                mapAgentSources = generateAgentElements(FlumeConfiguratorConstants.SOURCES_LIST_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SOURCES_PROPERTY);

                //Generate Agents Channels
                mapAgentChannels = generateAgentElements(FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX, FlumeConfiguratorConstants.CHANNELS_PROPERTY);

                //Generate Agents Sinks
                mapAgentSinks = generateAgentElements(FlumeConfiguratorConstants.SINKS_LIST_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SINKS_PROPERTY);

                //Generate Agents Sink Groups
                mapAgentSinkGroups = generateAgentElements(FlumeConfiguratorConstants.SINKGROUPS_LIST_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SINKGROUPS_PROPERTY);

                //Generate Sources Common Properties
                generateElementsCommonProperties(FlumeConfiguratorConstants.SOURCES_COMMON_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SOURCES_PROPERTY, mapAgentSources);

                //Generate Sources Partial Properties
                generateElementsPartialProperties(FlumeConfiguratorConstants.SOURCES_PARTIAL_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SOURCES_PROPERTY, mapAgentSources);

                //Generate Sources Interceptors
                generateSourcesInterceptors();

                //Generate Interceptors Common Properties
                generateInterceptorsCommonProperties();

                //Generate Interceptors Partial Properties
                generateInterceptorsPartialProperties();

                //Generate Channels Common Properties
                generateElementsCommonProperties(FlumeConfiguratorConstants.CHANNELS_COMMON_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.CHANNELS_PROPERTY, mapAgentChannels);

                //Generate Channels Partial Properties
                generateElementsPartialProperties(FlumeConfiguratorConstants.CHANNELS_PARTIAL_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.CHANNELS_PROPERTY, mapAgentChannels);

                //Generate Sinks Common Properties
                generateElementsCommonProperties(FlumeConfiguratorConstants.SINKS_COMMON_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SINKS_PROPERTY, mapAgentSinks);

                //Generate Sinks Partial Properties
                generateElementsPartialProperties(FlumeConfiguratorConstants.SINKS_PARTIAL_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SINKS_PROPERTY, mapAgentSinks);

                //Generate SinkGroups Common Properties
                generateElementsCommonProperties(FlumeConfiguratorConstants.SINKGROUPS_COMMON_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SINKGROUPS_PROPERTY, mapAgentSinkGroups);

                //Generate SinkGroups Partial Properties
                generateElementsPartialProperties(FlumeConfiguratorConstants.SINKGROUPS_PARTIAL_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SINKGROUPS_PROPERTY, mapAgentSinkGroups);

                //Generate final structure
                generateFinalStructureMap();


                //Get the content
                String agentConfiguration;
                StringBuilder sbAgentConfiguration = new StringBuilder(10000);
                boolean isFirstAgent = true;
                for (String agentName : configurationFinalMap.keySet()) {

                    AgentConfigurationProperties agentConfigurationProperties = configurationFinalMap.get(agentName);

                    if (isFirstAgent) {
                        agentConfiguration = FlumeConfiguratorUtils.getStringAgentConfiguration(agentConfigurationProperties, agentName, addComments, true);
                        isFirstAgent = false;
                    } else {
                        agentConfiguration = FlumeConfiguratorUtils.getStringAgentConfiguration(agentConfigurationProperties, agentName, addComments, false);
                    }
                    //logger.info(agentConfiguration);

                    sbAgentConfiguration.append(agentConfiguration);
                }

                if (writeFlumeConfigurationFiles) {
                    pathConfigurationGeneratedFile = pathFlumeConfigurationGeneratedFile;
                    multipleAgentConfigurationFiles = multipleFlumeConfigurationFiles;

                    //Write Flume configuration files
                    writeConfigurationFiles();
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("END buildConfigurationMapFromStringProperties");
                }

                return sbAgentConfiguration.toString();


            } else {
                logger.error("[ERROR] The configuration file is not correct");
                logger.error(baseConfigurationValidator.getSbCheckErrors().toString());
                return null;
            }


        } catch (IOException e) {
            logger.error("An error has occurred on the load of properties file", e);
            return null;
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
            sb.append("An error has occurred in Flume configurator. Check the properties configuration file and generated logs");
        } else if (errorCode == 2) {
            sb.append("Incorrect parameters number");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("Usage: java -cp jarFile org.keedio.flume.configurator.builder.FlumePropertiesGenerator <parameters>");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("The parameters are:");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("pathBaseConfigurationProperties => Path of the base (template) configuration file");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("elementsCharacterSeparator => Separator character used in base (template) configuration file");
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
            sb.append("pathConfigurationGeneratedFile => Path of the created flume configuration file(s).May be a directory if several");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                                  configuration files are created (the directory must be exist)");
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
            logger.debug("******* BEGIN FLUME PROPERTIES GENERATOR PROCESS *****************");
        }

        try {

            if (args.length == 5) {
                pathBaseConfigurationProperties = args[0];
                elementsCharacterSeparator = args[1];
                multipleAgentConfigurationFiles = Boolean.valueOf(args[2]);
                addComments = Boolean.valueOf(args[3]);
                pathConfigurationGeneratedFile = args[4];

                if (logger.isDebugEnabled()) {
                    logger.debug("Parameter pathBaseConfigurationProperties: " + pathBaseConfigurationProperties);
                    logger.debug("Parameter elementsCharacterSeparator: " + elementsCharacterSeparator);
                    logger.debug("Parameter multipleAgentConfigurationFiles: " + multipleAgentConfigurationFiles);
                    logger.debug("Parameter addComments: " + addComments);
                    logger.debug("Parameter patConfigurationGeneratedFile: " + pathConfigurationGeneratedFile);
                }
                FlumePropertiesGenerator flumePropertiesGenerator = new FlumePropertiesGenerator();

                flumePropertiesGenerator.buildConfigurationMap();

            } else {
                logger.error(getErrorMessage(2));
                if (logger.isDebugEnabled()) {
                    logger.debug("******* END FLUME PROPERTIES GENERATOR PROCESS *****************");
                }
            }

        } catch (Exception e) {
            logger.error(getErrorMessage(1), e);
            if (logger.isDebugEnabled()) {
                logger.debug("******* END FLUME PROPERTIES GENERATOR PROCESS *****************");
            }

        }
    }

}


