package org.keedio.flume.configurator.validator;

import java.util.*;

import org.keedio.flume.configurator.constants.FlumeConfiguratorConstants;
import org.keedio.flume.configurator.structures.LinkedProperties;
import org.keedio.flume.configurator.utils.FlumeConfiguratorTopologyUtils;
import org.keedio.flume.configurator.utils.FlumeConfiguratorUtils;
import org.slf4j.LoggerFactory;

public class FlumeConfigurationValidator {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FlumeConfigurationValidator.class);

    private Properties flumeConfigurationProperties;
    private StringBuilder sbCheckErrors;
    private boolean isPropertiesFileOK;

    public static Map<String, List<String>> mapSources;
    public static Map<String, List<String>> mapChannels;
    public static Map<String, List<String>> mapSinks;
    public static Map<String, List<String>> mapSinkgroups;
    public static Map<String, Map<String,List<String>>> mapInterceptors;
    public static Set<String> allNamesSet;


    public StringBuilder getSbCheckErrors() {
        return sbCheckErrors;
    }

    public boolean isPropertiesFileOK() {
        return isPropertiesFileOK;
    }


    public FlumeConfigurationValidator(Properties flumeConfigurationProperties) {
        this.flumeConfigurationProperties = flumeConfigurationProperties;
        sbCheckErrors = new StringBuilder();
        isPropertiesFileOK = true;

        mapSources = new LinkedHashMap<>();
        mapChannels = new LinkedHashMap<>();
        mapSinks = new LinkedHashMap<>();
        mapSinkgroups = new LinkedHashMap<>();
        mapInterceptors = new LinkedHashMap<>();
        allNamesSet = new HashSet<>();
    }

    /**
     * Create initial structures used on validation process
     * @param agentsList List with the agents list information
     */
    public void createInitialStructures(List<String> agentsList) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN createInitialStructures");
        }

        createInitialStructureType(agentsList, FlumeConfiguratorConstants.SOURCES_PROPERTY, mapSources);
        createInitialStructureType(agentsList, FlumeConfiguratorConstants.CHANNELS_PROPERTY, mapChannels);
        createInitialStructureType(agentsList, FlumeConfiguratorConstants.SINKS_PROPERTY, mapSinks);
        createInitialStructureType(agentsList, FlumeConfiguratorConstants.SINKGROUPS_PROPERTY, mapSinkgroups);

        //Get interceptors list properties for all agents and sources
        LinkedProperties interceptorsListProperties = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(flumeConfigurationProperties, FlumeConfiguratorConstants.INTERCEPTORS_PROPERTY,
                FlumeConfiguratorConstants.INTERCEPTORS_LIST_PROPERTY_PART_INDEX, true);

        for (Object keyObject : interceptorsListProperties.keySet()) {

            String keyProperty = (String) keyObject;
            String valuesProperty = flumeConfigurationProperties.getProperty(keyProperty);

            if (valuesProperty != null) {
                //Check interceptor belongs to a source of the agent
                String interceptorAgent = FlumeConfiguratorTopologyUtils.getPropertyPart(keyProperty, FlumeConfiguratorConstants.AGENTS_PROPERTY_PART_INDEX);
                String interceptorSource = FlumeConfiguratorTopologyUtils.getPropertyPart(keyProperty, FlumeConfiguratorConstants.ELEMENT_PROPERTY_PART_INDEX);

                String[] valueArray = valuesProperty.split(FlumeConfiguratorConstants.WHITE_SPACE_REGEX);

                //Populate agent information
                for (String propertyValue : valueArray) {
                    Map<String, List<String>> sourcesInterceptorsMap = mapInterceptors.get(interceptorAgent);
                    if (sourcesInterceptorsMap == null) {
                        List<String> sourceInterceptorsList = new ArrayList<>();
                        sourceInterceptorsList.add(propertyValue);
                        sourcesInterceptorsMap = new LinkedHashMap<>();
                        sourcesInterceptorsMap.put(interceptorSource, sourceInterceptorsList);
                        mapInterceptors.put(interceptorAgent, sourcesInterceptorsMap);
                    } else {
                        List<String> interceptorsList = sourcesInterceptorsMap.get(interceptorSource);
                        if (interceptorsList == null) {
                            interceptorsList = new ArrayList<>();
                            interceptorsList.add(propertyValue);
                            sourcesInterceptorsMap.put(interceptorSource, interceptorsList);
                        } else {
                            interceptorsList.add(propertyValue);
                        }
                    }
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END createInitialStructures");
        }
    }

    /**
     * Create initial structure information for the indicated type
     * @param agentsList List with the agents list information
     * @param propertyPrefix String with the property type to check (sources, channels, sinks, sinkgroups)
     * @param mapAgentsElements map with relation between agent and elements of the indicated type
     */
    private void createInitialStructureType(List<String> agentsList, String propertyPrefix, Map<String,List<String>> mapAgentsElements) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN createInitialStructureType");
        }

        for (String agentName : agentsList) {

            String keySubsetFind = agentName + FlumeConfiguratorConstants.DOT_SEPARATOR + propertyPrefix;

            String valuesProperty = flumeConfigurationProperties.getProperty(keySubsetFind);

            if (valuesProperty != null) {
                String[] valueArray = valuesProperty.split(FlumeConfiguratorConstants.WHITE_SPACE_REGEX);

                for (String propertyValue : valueArray) {

                    //Populate agent information
                    if (mapAgentsElements.get(agentName) == null) {
                        List<String> agentElementsList = new ArrayList<>();
                        agentElementsList.add(propertyValue);
                        mapAgentsElements.put(agentName, agentElementsList);
                    } else {
                        List<String> agentElementsList = mapAgentsElements.get(agentName);
                        if (agentElementsList == null) {
                            agentElementsList = new ArrayList<>();
                            agentElementsList.add(propertyValue);
                        } else {
                            agentElementsList.add(propertyValue);
                        }
                    }
                }
            }
        }


        if (logger.isDebugEnabled()) {
            logger.debug("END createInitialStructureType");
        }
    }

    /**
     * Check all names for the elements are unique
     * @param agentsList List with the agents list information
     * @param strictValidation true forces validation error if are not unique names. false only show warnings
     * @return boolean if validation proccess  (namespaces uniqueness) is correct, false otherwise.
     */
    private boolean checkNamespacesUniqueness(List<String> agentsList, boolean strictValidation) {

        if (logger.isDebugEnabled()) {
            logger.debug("END checkNamespacesUniqueness");
        }

        boolean isPropertiesCheckFileOK = true;

        //Check agents names
        for (String agentName: agentsList) {
            isPropertiesCheckFileOK = isPropertiesCheckFileOK && checkElementNamespacesUniqueness(agentName, true);
        }

        //Check sources names
        for (String agentName : mapSources.keySet()) {
            List<String> sourcesList = mapSources.get(agentName);
            for (String sourceName : sourcesList) {
                isPropertiesCheckFileOK = isPropertiesCheckFileOK && checkElementNamespacesUniqueness(sourceName, strictValidation);
            }
        }

        //Check channels names
        for (String agentName : mapChannels.keySet()) {
            List<String> channelsList = mapChannels.get(agentName);
            for (String channelName : channelsList) {
                isPropertiesCheckFileOK = isPropertiesCheckFileOK && checkElementNamespacesUniqueness(channelName, strictValidation);
            }
        }

        //Check sinks names
        for (String agentName : mapSinks.keySet()) {
            List<String> sinksList = mapSinks.get(agentName);
            for (String sinkName : sinksList) {
                isPropertiesCheckFileOK = isPropertiesCheckFileOK && checkElementNamespacesUniqueness(sinkName, strictValidation);
            }
        }

        //Check sinks names
        for (String agentName : mapSinkgroups.keySet()) {
            List<String> sinkgroupsList = mapSinkgroups.get(agentName);
            for (String sinkgroupName : sinkgroupsList) {
                isPropertiesCheckFileOK = isPropertiesCheckFileOK && checkElementNamespacesUniqueness(sinkgroupName, strictValidation);
            }
        }

        //Check interceptors names
        for (String agentName : mapInterceptors.keySet()) {
            Map<String, List<String>> mapSourcesInterceptorsAgentRelation = mapInterceptors.get(agentName);
            for (String sourceName : mapSourcesInterceptorsAgentRelation.keySet()) {
                List<String> sourceInterceptorsList = mapSourcesInterceptorsAgentRelation.get(sourceName);
                for (String interceptorName : sourceInterceptorsList) {
                    isPropertiesCheckFileOK = isPropertiesCheckFileOK && checkElementNamespacesUniqueness(interceptorName, strictValidation);
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END checkNamespacesUniqueness");
        }

        return isPropertiesCheckFileOK;
    }

    /**
     * Check if the element name is unique
     * @param elementName element name
     * @param strictValidation true forces validation error if are not unique names. false only show warnings
     * @return boolean if validation proccess (namespaces uniqueness) is correct, false otherwise.
     */
    private boolean checkElementNamespacesUniqueness(String elementName, boolean strictValidation) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN checkElementNamespacesUniqueness");
        }

        boolean isPropertiesCheckFileOK = true;

        if (allNamesSet != null) {
            if (!allNamesSet.contains(elementName)) {
                allNamesSet.add(elementName);
            } else {
                if (strictValidation) {
                    isPropertiesCheckFileOK = false;
                    sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("There is several elements with the same name (").append(elementName).append(")").append(FlumeConfiguratorConstants.NEW_LINE);
                } else {
                    if (logger.isWarnEnabled()) {
                        logger.warn("There is several elements with the same name (" + elementName + ")");
                    }
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END checkElementNamespacesUniqueness");
        }

        return isPropertiesCheckFileOK;
    }

    /**
     * Check sources, channels, sinks and sinkgroups properties listsare correct for all agents
     * @param propertyPrefix String with the property type to check (sources, channels, sinks, sinkgroups)
     * @param agentsList List with the agents list information
     * @param mandatoryProperty true if property is mandatory, false otherwise
     * @return boolean true if the checked lists are correct false otherwise
     */
    private boolean checkPropertiesFilePropertiesList(String propertyPrefix, List<String> agentsList, boolean mandatoryProperty) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN checkPropertiesFilePropertiesList");
        }

        boolean isPropertiesCheckFileOK = true;

        if (agentsList.size() == 0) {
            isPropertiesCheckFileOK = false;
            sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("There is no defined agents on properties").append(FlumeConfiguratorConstants.NEW_LINE);
        } else {

            for (String agentName : agentsList) {

                String keySubsetFind = agentName + FlumeConfiguratorConstants.DOT_SEPARATOR + propertyPrefix;

                String valuesProperty = flumeConfigurationProperties.getProperty(keySubsetFind);

                if (valuesProperty == null) {
                    if (mandatoryProperty) {
                        isPropertiesCheckFileOK = false;
                        sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("There is no [").append(propertyPrefix).append("] property for agent [").append(agentName).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                    }
                } else {

                    if ("".equals(valuesProperty)) {
                        isPropertiesCheckFileOK = false;
                        sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("The property [").append(propertyPrefix).append("] has an empty value for agent [").append(agentName).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                    } else {
                        String[] valueArray = valuesProperty.split(FlumeConfiguratorConstants.WHITE_SPACE_REGEX);

                        for (String propertyValue : valueArray) {

                            String keyElementSubsetFind;
                            if (FlumeConfiguratorConstants.SOURCES_PROPERTY.equals(propertyPrefix) ||
                                FlumeConfiguratorConstants.CHANNELS_PROPERTY.equals(propertyPrefix) ||
                                FlumeConfiguratorConstants.SINKS_PROPERTY.equals(propertyPrefix)) {

                                //Check "type" property existence for the element
                                keyElementSubsetFind = FlumeConfiguratorUtils.constructFullPropertyName(agentName, propertyPrefix, propertyValue, false, FlumeConfiguratorConstants.TYPE_PROPERTY);
                                if (flumeConfigurationProperties.getProperty(keyElementSubsetFind) == null) {
                                    isPropertiesCheckFileOK = false;
                                    sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("There is no defined \"type\" property for element [").append(propertyValue).append("] of the agent [").append(agentName).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                                }
                            } else if (FlumeConfiguratorConstants.SINKGROUPS_PROPERTY.equals(propertyPrefix)) {
                                keyElementSubsetFind = FlumeConfiguratorUtils.constructFullPropertyName(agentName, propertyPrefix, propertyValue, false, FlumeConfiguratorConstants.PROCESSOR_TYPE_PROPERTY);
                                if (flumeConfigurationProperties.getProperty(keyElementSubsetFind) == null) {
                                    isPropertiesCheckFileOK = false;
                                    sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("There is no defined \"processor.type\" property for sinkgroup [").append(propertyValue).append("] of the agent [").append(agentName).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                                }
                            }

                            if (FlumeConfiguratorConstants.SOURCES_PROPERTY.equals(propertyPrefix)) {
                                //Check "channels" property existence for the element
                                keyElementSubsetFind = FlumeConfiguratorUtils.constructFullPropertyName(agentName, propertyPrefix, propertyValue, false, FlumeConfiguratorConstants.CHANNELS_PROPERTY);
                                if (flumeConfigurationProperties.getProperty(keyElementSubsetFind) == null) {
                                    isPropertiesCheckFileOK = false;
                                    sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("There is no defined \"channels\" property for source [").append(propertyValue).append("] of the agent [").append(agentName).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                                }
                            } else if (FlumeConfiguratorConstants.SINKS_PROPERTY.equals(propertyPrefix)) {
                                //Check "channel" property existence for the element
                                keyElementSubsetFind = FlumeConfiguratorUtils.constructFullPropertyName(agentName, propertyPrefix, propertyValue, false, FlumeConfiguratorConstants.CHANNEL_PROPERTY);
                                if (flumeConfigurationProperties.getProperty(keyElementSubsetFind) == null) {
                                    isPropertiesCheckFileOK = false;
                                    sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("There is no defined \"channel\" property for sink [").append(propertyValue).append("] of the agent [").append(agentName).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                                }
                            } else if (FlumeConfiguratorConstants.SINKGROUPS_PROPERTY.equals(propertyPrefix)) {
                                //Check "sinks" property existence for the element
                                keyElementSubsetFind = FlumeConfiguratorUtils.constructFullPropertyName(agentName, propertyPrefix, propertyValue, false, FlumeConfiguratorConstants.SINKS_PROPERTY);
                                if (flumeConfigurationProperties.getProperty(keyElementSubsetFind) == null) {
                                    isPropertiesCheckFileOK = false;
                                    sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("There is no defined \"sinks\" property for sinkgroup [").append(propertyValue).append("] of the agent [").append(agentName).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END checkPropertiesFilePropertiesList");
        }

        return isPropertiesCheckFileOK;

    }


    /**
     * Check interceptors properties list are correct for all agents
     * @param agentsList List with the agents list information
     * @return boolean true if the checked lists are correct false otherwise
     */
    private boolean checkPropertiesFileInterceptorList(List<String> agentsList) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN checkPropertiesFileInterceptorList");
        }

        boolean isPropertiesCheckFileOK = true;

        if (agentsList.size() == 0) {
            isPropertiesCheckFileOK = false;
            sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("There is no defined agents on properties").append(FlumeConfiguratorConstants.NEW_LINE);
        } else {

            //Get interceptors list properties for all agents and sources
            LinkedProperties interceptorsListProperties = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(flumeConfigurationProperties, FlumeConfiguratorConstants.INTERCEPTORS_PROPERTY,
                    FlumeConfiguratorConstants.INTERCEPTORS_LIST_PROPERTY_PART_INDEX, true);

            for (Object keyObject : interceptorsListProperties.keySet()) {

                String keyProperty = (String) keyObject;
                String valuesProperty = flumeConfigurationProperties.getProperty(keyProperty);

                //Check interceptor belongs to a source of the agent
                String interceptorAgent = FlumeConfiguratorTopologyUtils.getPropertyPart(keyProperty, FlumeConfiguratorConstants.AGENTS_PROPERTY_PART_INDEX);
                String interceptorSource = FlumeConfiguratorTopologyUtils.getPropertyPart(keyProperty, FlumeConfiguratorConstants.ELEMENT_PROPERTY_PART_INDEX);

                if (!agentsList.contains(interceptorAgent)) {
                    isPropertiesCheckFileOK = false;
                    sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("The property [").append(keyProperty).append("] references a non declared agent [").append(interceptorAgent).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                } else {
                    List<String> listSourcesAgent = mapSources.get(interceptorAgent);
                    if (listSourcesAgent == null) {
                        isPropertiesCheckFileOK = false;
                        sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("The property [").append(keyProperty).append("] references a non declared agent [").append(interceptorAgent).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                    } else {
                        if (!listSourcesAgent.contains(interceptorSource)) {
                            isPropertiesCheckFileOK = false;
                            sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("The property [").append(keyProperty).append("] references a non declared source [").append(interceptorSource).append("]").append(" for the agent [").append(interceptorAgent).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                        } else {
                            if ("".equals(valuesProperty)) {
                                isPropertiesCheckFileOK = false;
                                sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("There is interceptors empty list defined for source [").append(interceptorSource).append("] of the agent [").append(interceptorAgent).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                            } else {
                                String[] valueArray = valuesProperty.split(FlumeConfiguratorConstants.WHITE_SPACE_REGEX);

                                for (String propertyValue : valueArray) {

                                    //Check "type" property existence for the element
                                    String keyElementSubsetFind = FlumeConfiguratorUtils.constructFullInterceptorPropertyName(interceptorAgent, interceptorSource, propertyValue, false, FlumeConfiguratorConstants.TYPE_PROPERTY);
                                    if (flumeConfigurationProperties.getProperty(keyElementSubsetFind) == null) {
                                        isPropertiesCheckFileOK = false;
                                        sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("There is no defined \"type\" property for interceptor [").append(propertyValue).append("] of the source [").append(interceptorSource).append("] of the agent [").append(interceptorAgent).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END checkPropertiesFileInterceptorList");
        }

        return isPropertiesCheckFileOK;

    }

    /**
     * Check element properties (not interceptors properties)
     * @param agentsList List with the agents list information
     * @param propertyPrefix String with the property type to check (sources, channels, sinks, sinkgroups)
     * @param mapAgentsElements map with relation between agent and elements of the indicated type
     * @return boolean true if the element properties are correct false otherwise
     */
    private boolean checkPropertiesFileElementsProperties(List<String> agentsList, String propertyPrefix, Map<String,List<String>> mapAgentsElements) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN checkPropertiesFileElementsProperties");
        }

        boolean isPropertiesCheckFileOK = true;

        if (agentsList.size() == 0) {
            isPropertiesCheckFileOK = false;
            sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("There is no defined agents on properties").append(FlumeConfiguratorConstants.NEW_LINE);
        } else {
            //Get all properties of the required type
            LinkedProperties prefixPropertiesList = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(flumeConfigurationProperties, propertyPrefix, FlumeConfiguratorConstants.AGENTS_PROPERTY_PART_INDEX + 1, false);

            for (Object keyObject : prefixPropertiesList.keySet()) {

                String keyProperty = (String) keyObject;
                String valuesProperty = prefixPropertiesList.getProperty(keyProperty);

                boolean checkProperty = true;
                //The elements lists properties have been checked before
                if (FlumeConfiguratorTopologyUtils.getPropertyPartsNumber(keyProperty) == 2) {
                    checkProperty = false;
                }

                //The interceptors and selectors properties will be checked by another method
                if ((FlumeConfiguratorTopologyUtils.getPropertyPartsNumber(keyProperty) >= 4) &&
                        (FlumeConfiguratorConstants.INTERCEPTORS_PROPERTY.equals(FlumeConfiguratorTopologyUtils.getPropertyPart(keyProperty, FlumeConfiguratorConstants.SOURCE_INTERCEPTORS_PART_INDEX)) ||
                         FlumeConfiguratorConstants.SELECTOR_PROPERTY.equals(FlumeConfiguratorTopologyUtils.getPropertyPart(keyProperty, FlumeConfiguratorConstants.SOURCE_SELECTOR_PROPERTY_PART_INDEX)))){
                    checkProperty = false;
                }

                if (checkProperty) {

                    if (logger.isDebugEnabled()) {
                        logger.debug("CHECK property [" + keyProperty + "]");
                    }

                    String elementAgent = FlumeConfiguratorTopologyUtils.getPropertyPart(keyProperty, FlumeConfiguratorConstants.AGENTS_PROPERTY_PART_INDEX);
                    String elementName = FlumeConfiguratorTopologyUtils.getPropertyPart(keyProperty, FlumeConfiguratorConstants.ELEMENT_PROPERTY_PART_INDEX);

                    //Check element agent
                    if (!agentsList.contains(elementAgent)) {
                        isPropertiesCheckFileOK = false;
                        sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("The property [").append(keyProperty).append("] references a non declared agent [").append(elementAgent).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                    } else {
                        //Check if the element references a correct element for the agent
                        List<String> listElementsTypeAgent = mapAgentsElements.get(elementAgent);
                        if (listElementsTypeAgent == null || !listElementsTypeAgent.contains(elementName)) {
                            isPropertiesCheckFileOK = false;
                            sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("The property [").append(keyProperty).append("] references a non declared element [").append(elementName).append("]").append(" for the agent [").append(elementAgent).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                        } else {
                            //Check empty value for property
                            if ("".equals(valuesProperty)) {
                                isPropertiesCheckFileOK = false;
                                sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("The property [").append(keyProperty).append("] has an empty value").append(FlumeConfiguratorConstants.NEW_LINE);
                            } else {
                                //Check values for .channels (SOURCES), .channel (SINKS), .sinks (SINKGROUPS)
                                if (FlumeConfiguratorConstants.SOURCES_PROPERTY.equals(propertyPrefix) &&
                                        FlumeConfiguratorConstants.CHANNELS_PROPERTY.equals(FlumeConfiguratorTopologyUtils.getPropertyPart(keyProperty, FlumeConfiguratorConstants.SOURCE_CHANNELS_PART_INDEX))) {

                                    if (logger.isDebugEnabled()) {
                                        logger.debug("CHECK .channels sources property [" + keyProperty + "]");
                                    }

                                    String[] valueArray = valuesProperty.split(FlumeConfiguratorConstants.WHITE_SPACE_REGEX);

                                    for (String propertyValue : valueArray) {
                                        //Check channel is correct for agent
                                        if (mapChannels.get(elementAgent) == null || !mapChannels.get(elementAgent).contains(propertyValue)) {
                                            isPropertiesCheckFileOK = false;
                                            sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("The sources .channels property [").append(keyProperty).append("] has a channel value [").append(propertyValue).append("] that it is not defined for agent [").append(elementAgent).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                                        }
                                    }

                                } else if (FlumeConfiguratorConstants.SINKS_PROPERTY.equals(propertyPrefix) &&
                                        FlumeConfiguratorConstants.CHANNEL_PROPERTY.equals(FlumeConfiguratorTopologyUtils.getPropertyPart(keyProperty, FlumeConfiguratorConstants.SINK_CHANNEL_PART_INDEX))) {

                                    if (logger.isDebugEnabled()) {
                                        logger.debug("CHECK .channel sinks property [" + keyProperty + "]");
                                    }

                                    String[] valueArray = valuesProperty.split(FlumeConfiguratorConstants.WHITE_SPACE_REGEX);

                                    for (String propertyValue : valueArray) {
                                        //Check channel is correct for agent
                                        if (mapChannels.get(elementAgent) == null || !mapChannels.get(elementAgent).contains(propertyValue)) {
                                            isPropertiesCheckFileOK = false;
                                            sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("The sinks .channel property [").append(keyProperty).append("] has a channel value [").append(propertyValue).append("] that it is not defined for agent [").append(elementAgent).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                                        }
                                    }

                                } else if (FlumeConfiguratorConstants.SINKGROUPS_PROPERTY.equals(propertyPrefix) &&
                                        FlumeConfiguratorConstants.SINKS_PROPERTY.equals(FlumeConfiguratorTopologyUtils.getPropertyPart(keyProperty, FlumeConfiguratorConstants.SINKGROUP_SINKS_PART_INDEX))) {

                                    if (logger.isDebugEnabled()) {
                                        logger.debug("CHECK .sinks sinkgroups property [" + keyProperty + "]");
                                    }

                                    String[] valueArray = valuesProperty.split(FlumeConfiguratorConstants.WHITE_SPACE_REGEX);

                                    for (String propertyValue : valueArray) {
                                        //Check sink is correct for agent
                                        if (mapSinks.get(elementAgent) == null || !mapSinks.get(elementAgent).contains(propertyValue)) {
                                            isPropertiesCheckFileOK = false;
                                            sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("The sinkgroups .sinks property [").append(keyProperty).append("] has a sink value [").append(propertyValue).append("] that it is not defined for agent [").append(elementAgent).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END checkPropertiesFileElementsProperties");
        }

        return isPropertiesCheckFileOK;
    }


    /**
     * Check selectors properties
     * @param agentsList List with the agents list information
     * @return boolean true if the selectors properties are correct false otherwise
     */
    private boolean checkPropertiesFileSelectorsProperties(List<String> agentsList) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN checkPropertiesFileSelectorsProperties");
        }

        boolean isPropertiesCheckFileOK = true;

        if (agentsList.size() == 0) {
            isPropertiesCheckFileOK = false;
            sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("There is no defined agents on properties").append(FlumeConfiguratorConstants.NEW_LINE);
        } else {
            //Get all selectors properties
            LinkedProperties selectorsPropertiesList = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(flumeConfigurationProperties, FlumeConfiguratorConstants.SELECTOR_PROPERTY, FlumeConfiguratorConstants.SOURCE_SELECTOR_PROPERTY_PART_INDEX, false);

            for (Object keyObject : selectorsPropertiesList.keySet()) {

                String keyProperty = (String) keyObject;
                String valuesProperty = selectorsPropertiesList.getProperty(keyProperty);

                if (logger.isDebugEnabled()) {
                    logger.debug("CHECK property [" + keyProperty + "]");
                }

                String selectorAgent = FlumeConfiguratorTopologyUtils.getPropertyPart(keyProperty, FlumeConfiguratorConstants.AGENTS_PROPERTY_PART_INDEX);
                String selectorSource = FlumeConfiguratorTopologyUtils.getPropertyPart(keyProperty, FlumeConfiguratorConstants.ELEMENT_PROPERTY_PART_INDEX);

                if (FlumeConfiguratorTopologyUtils.getPropertyPartsNumber(keyProperty) == 4) {
                    isPropertiesCheckFileOK = false;
                    sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("The property [").append(keyProperty).append("] is not a valid selector property [").append(selectorAgent).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                } else {
                    //Check selector agent
                    if (!agentsList.contains(selectorAgent)) {
                        isPropertiesCheckFileOK = false;
                        sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("The property [").append(keyProperty).append("] references a non declared agent [").append(selectorAgent).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                    } else {
                        //Check if the source references a correct source for the agent
                        List<String> agentSourcesList = mapSources.get(selectorAgent);

                        if (!agentSourcesList.contains(selectorSource)) {
                            isPropertiesCheckFileOK = false;
                            sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("The property [").append(keyProperty).append("] references a non declared source [").append(selectorSource).append("] for the agent [").append(selectorAgent).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                        } else {

                            //Check empty value for property
                            if ("".equals(valuesProperty)) {
                                isPropertiesCheckFileOK = false;
                                sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("The property [").append(keyProperty).append("] has an empty value").append(FlumeConfiguratorConstants.NEW_LINE);
                            } else if (FlumeConfiguratorTopologyUtils.isSelectorChannelReferenceFlumeConfigurationProperty(keyProperty)) {

                                if (logger.isDebugEnabled()) {
                                    logger.debug("CHECK channel reference selector property [" + keyProperty + "]");
                                }

                                String keyElementSubsetFind = FlumeConfiguratorUtils.constructFullPropertyName(selectorAgent, FlumeConfiguratorConstants.SOURCES_PROPERTY, selectorSource, false, FlumeConfiguratorConstants.CHANNELS_PROPERTY);

                                //Check source with selector have multiple channels
                                String sourceChannelsListStr = flumeConfigurationProperties.getProperty(keyElementSubsetFind);

                                if (sourceChannelsListStr == null) {
                                    isPropertiesCheckFileOK = false;
                                    sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("The .channels property of the source [").append(selectorSource).append("]").append(" is not defihed").append(FlumeConfiguratorConstants.NEW_LINE);
                                } else {
                                    List<String> sourceChannelsList = Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptionalKeepInternalSpaces(sourceChannelsListStr, true, FlumeConfiguratorConstants.WHITE_SPACE_REGEX));

                                    if (sourceChannelsList.size() <= 1) {
                                        isPropertiesCheckFileOK = false;
                                        sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("The selector property [").append(keyProperty).append("] references a source [").append(selectorSource).append("]").append(" without defined multiple channels").append(FlumeConfiguratorConstants.NEW_LINE);
                                    } else {

                                        String[] valueArray = valuesProperty.split(FlumeConfiguratorConstants.WHITE_SPACE_REGEX);

                                        for (String propertyValue : valueArray) {
                                            //Check channel is correct for agent
                                            if (mapChannels.get(selectorAgent) == null || !mapChannels.get(selectorAgent).contains(propertyValue)) {
                                                isPropertiesCheckFileOK = false;
                                                sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("The selector channel reference property [").append(keyProperty).append("] has a channel value [").append(propertyValue).append("] that it is not defined for agent [").append(selectorAgent).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                                            }

                                            if (!sourceChannelsList.contains(propertyValue)) {
                                                isPropertiesCheckFileOK = false;
                                                sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("The selector channel reference property [").append(keyProperty).append("] has a channel value [").append(propertyValue).append("] that is not declared as channels of the selector source  [").append(selectorSource).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        if (logger.isDebugEnabled()) {
            logger.debug("END checkPropertiesFileSelectorsProperties");
        }

        return isPropertiesCheckFileOK;
    }


    /**
     * Check interceptors properties
     * @param agentsList List with the agents list information
     * @return boolean true if the interceptors properties are correct false otherwise
     */
    private boolean checkPropertiesFileInterceptorsProperties(List<String> agentsList) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN checkPropertiesFileInterceptorsProperties");
        }

        boolean isPropertiesCheckFileOK = true;

        if (agentsList.size() == 0) {
            isPropertiesCheckFileOK = false;
            sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("There is no defined agents on properties").append(FlumeConfiguratorConstants.NEW_LINE);
        } else {
            //Get all interceptors properties
            LinkedProperties interceptorsPropertiesList = FlumeConfiguratorTopologyUtils.getPropertiesWithPart(flumeConfigurationProperties, FlumeConfiguratorConstants.INTERCEPTORS_PROPERTY, FlumeConfiguratorConstants.INTERCEPTORS_LIST_PROPERTY_PART_INDEX, false);

            for (Object keyObject : interceptorsPropertiesList.keySet()) {

                String keyProperty = (String) keyObject;
                String valuesProperty = interceptorsPropertiesList.getProperty(keyProperty);

                boolean checkProperty = true;
                //The interceptors lists properties have been checked before
                if (FlumeConfiguratorTopologyUtils.getPropertyPartsNumber(keyProperty) == 4) {
                    checkProperty = false;
                }

                if (checkProperty) {

                    if (logger.isDebugEnabled()) {
                        logger.debug("CHECK property [" + keyProperty + "]");
                    }

                    String interceptorAgent = FlumeConfiguratorTopologyUtils.getPropertyPart(keyProperty, FlumeConfiguratorConstants.AGENTS_PROPERTY_PART_INDEX);
                    String interceptorSource = FlumeConfiguratorTopologyUtils.getPropertyPart(keyProperty, FlumeConfiguratorConstants.ELEMENT_PROPERTY_PART_INDEX);
                    String interceptorName = FlumeConfiguratorTopologyUtils.getPropertyPart(keyProperty, FlumeConfiguratorConstants.INTERCEPTOR_PROPERTY_PART_INDEX);

                    //Check interceptor agent
                    if (!agentsList.contains(interceptorAgent)) {
                        isPropertiesCheckFileOK = false;
                        sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("The property [").append(keyProperty).append("] references a non declared agent [").append(interceptorAgent).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                    } else {
                        //Check if the element references a correct element for the agent
                        Map<String, List<String>> mapSourcesInterceptorsRelationAgent = mapInterceptors.get(interceptorAgent);

                        if (mapSourcesInterceptorsRelationAgent ==  null) {
                            isPropertiesCheckFileOK = false;
                            sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("The property [").append(keyProperty).append("] references a non declared agent [").append(interceptorAgent).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                        } else {
                            List<String> interceptorsSourceAgentList = mapSourcesInterceptorsRelationAgent.get(interceptorSource);

                            if (interceptorsSourceAgentList == null || !interceptorsSourceAgentList.contains(interceptorName)) {
                                isPropertiesCheckFileOK = false;
                                sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("The property [").append(keyProperty).append("] references an interceptor [").append(interceptorName).append("] from a source [").append(interceptorSource).append("] that it has not been declared for the agent [").append(interceptorAgent).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                            } else {
                                //Check empty value for property
                                if ("".equals(valuesProperty)) {
                                    isPropertiesCheckFileOK = false;
                                    sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("The property [").append(keyProperty).append("] has an empty value").append(FlumeConfiguratorConstants.NEW_LINE);
                                }
                            }
                        }
                    }
                }
            }
        }


        if (logger.isDebugEnabled()) {
            logger.debug("END checkPropertiesFileInterceptorsProperties");
        }

        return isPropertiesCheckFileOK;
    }

    /**
     * Check if the properties configuration file is correct. Store the found errors
     */
    public void validateFlumeConfiguration() {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN validateFlumeConfiguration");
        }

        List<String> agentsList;

        Set<String> agentNames = FlumeConfiguratorTopologyUtils.getSetFirstPartProperties(flumeConfigurationProperties);
        agentsList = new ArrayList<>(agentNames);

        //Check all properties are sources,channels,sinks or sinkgroups properties
        for (Object flumeConfigurationProperty : flumeConfigurationProperties.keySet()) {

            String flumeConfigurationPropertyString = (String) flumeConfigurationProperty;

            String flumeConfigurationPropertyPart = FlumeConfiguratorTopologyUtils.getPropertyPart(flumeConfigurationPropertyString,FlumeConfiguratorConstants.AGENTS_PROPERTY_PART_INDEX + 1);

            if (!flumeConfigurationPropertyPart.startsWith(FlumeConfiguratorConstants.SOURCES_PROPERTY)
                    && !flumeConfigurationPropertyPart.startsWith(FlumeConfiguratorConstants.CHANNELS_PROPERTY)
                    && !flumeConfigurationPropertyPart.startsWith(FlumeConfiguratorConstants.SINKS_PROPERTY)
                    && !flumeConfigurationPropertyPart.startsWith(FlumeConfiguratorConstants.SINKGROUPS_PROPERTY)) {

                isPropertiesFileOK = false;
                sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(flumeConfigurationPropertyString).append("] property is not allowed").append(FlumeConfiguratorConstants.NEW_LINE);

            }
        }

        //Create initial structures
        createInitialStructures(agentsList);

        //Check namespaces uniqueness
        isPropertiesFileOK = isPropertiesFileOK && checkNamespacesUniqueness(agentsList, true);

        //Check sources list properties
        isPropertiesFileOK = isPropertiesFileOK && checkPropertiesFilePropertiesList(FlumeConfiguratorConstants.SOURCES_PROPERTY, agentsList, true);

        //Check channels list properties
        isPropertiesFileOK = isPropertiesFileOK && checkPropertiesFilePropertiesList(FlumeConfiguratorConstants.CHANNELS_PROPERTY, agentsList, true);

        //Check sinks list properties
        isPropertiesFileOK = isPropertiesFileOK && checkPropertiesFilePropertiesList(FlumeConfiguratorConstants.SINKS_PROPERTY, agentsList, true);

        //Check sinksgroups list properties
        isPropertiesFileOK = isPropertiesFileOK && checkPropertiesFilePropertiesList(FlumeConfiguratorConstants.SINKGROUPS_PROPERTY, agentsList, false);

        //Check interceptors list properties
        isPropertiesFileOK = isPropertiesFileOK && checkPropertiesFileInterceptorList(agentsList);

        //Check sources properties
        isPropertiesFileOK = isPropertiesFileOK && checkPropertiesFileElementsProperties(agentsList, FlumeConfiguratorConstants.SOURCES_PROPERTY, mapSources);

        // Check channels properties
        isPropertiesFileOK = isPropertiesFileOK && checkPropertiesFileElementsProperties(agentsList, FlumeConfiguratorConstants.CHANNELS_PROPERTY, mapChannels);

        // Check sinks properties
        isPropertiesFileOK = isPropertiesFileOK && checkPropertiesFileElementsProperties(agentsList, FlumeConfiguratorConstants.SINKS_PROPERTY, mapSinks);

        // Check sinkgroups properties
        isPropertiesFileOK = isPropertiesFileOK && checkPropertiesFileElementsProperties(agentsList, FlumeConfiguratorConstants.SINKGROUPS_PROPERTY, mapSinkgroups);

        //Check selectors properties
        isPropertiesFileOK = isPropertiesFileOK && checkPropertiesFileSelectorsProperties(agentsList);

        //Check interceptors properties
        isPropertiesFileOK = isPropertiesFileOK && checkPropertiesFileInterceptorsProperties(agentsList);

        if (logger.isDebugEnabled()) {
            logger.debug("END validateFlumeConfiguration");
        }
    }
}
