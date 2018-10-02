package org.keedio.flume.configurator.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.keedio.flume.configurator.constants.FlumeConfiguratorConstants;
import org.keedio.flume.configurator.structures.LinkedProperties;
import org.keedio.flume.configurator.utils.FlumeConfiguratorUtils;
import org.slf4j.LoggerFactory;

public class BaseConfigurationValidator {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BaseConfigurationValidator.class);

    private Properties baseConfigurationProperties;
    private StringBuilder sbCheckErrors;
    private boolean isPropertiesFileOK;
    private String elementsCharacterSeparator;


    public BaseConfigurationValidator(Properties flumeConfigurationProperties, String elementsCharacterSeparator) {
        this.baseConfigurationProperties = flumeConfigurationProperties;
        sbCheckErrors = new StringBuilder();
        isPropertiesFileOK = true;
        this.elementsCharacterSeparator = elementsCharacterSeparator;
    }


    public StringBuilder getSbCheckErrors() {
        return sbCheckErrors;
    }



    public boolean isPropertiesFileOK() {
        return isPropertiesFileOK;
    }



    /**
     * Check sources, channels and sinks lists are correct
     * @param prefixProperty String with the property type to check (sources, channels, sinks)
     * @param agentsList List with the agents list information
     * @return boolean true if the checked lists are correct false otherwise
     */
    private boolean checkPropertiesFileSourcesChannelsSinksList(String prefixProperty, List<String> agentsList) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN checkPropertiesFileSourcesChannelsSinksList");
        }

        boolean isPropertiesCheckFileOK = true;

        //CHECK elements list
        LinkedProperties elementsListProperties = FlumeConfiguratorUtils.matchingSubset(baseConfigurationProperties, prefixProperty, true);

        if (elementsListProperties.size() == 0) {
            isPropertiesCheckFileOK = false;
            sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("There is no ").append(prefixProperty).append(" property").append(FlumeConfiguratorConstants.NEW_LINE);
        } else {

            //Check all agents have .list property for specified type of property (.sources.list, .channels.list, sink.list)
            for (String agentNameList : agentsList) {

                String keySubsetFind = prefixProperty + FlumeConfiguratorConstants.DOT_SEPARATOR + agentNameList;

                if (FlumeConfiguratorUtils.matchingSubset(baseConfigurationProperties, keySubsetFind, true).size() == 0) {
                    isPropertiesCheckFileOK = false;
                    sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("There is no ").append(prefixProperty).append(" property for agent [").append(agentNameList).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                }

            }

            for (Object keyObject : elementsListProperties.keySet()) {

                String keyProperty = (String) keyObject;
                String valuesProperty = elementsListProperties.getProperty(keyProperty);

                if ("".equals(valuesProperty)) {
                    isPropertiesCheckFileOK = false;
                    sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("The property [").append(keyProperty).append("] has an empty value").append(FlumeConfiguratorConstants.NEW_LINE);
                } else {
                    //Check referenced agent exists
                    String[] keyPropertyArray = keyProperty.split(FlumeConfiguratorConstants.DOT_REGEX);

                    String agentName = keyPropertyArray[2];

                    if (!agentsList.contains(agentName)) {
                        isPropertiesCheckFileOK = false;
                        sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("The property [").append(keyProperty).append("] references an non declared agent [").append(agentName).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                    }
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END checkPropertiesFileSourcesChannelsSinksList");
        }

        return isPropertiesCheckFileOK;

    }


    /**
     * Check the information about sink groups
     * @param agentsList List with the agents list information
     * @return boolean true if the information about sink groups is correct false otherwise
     */
    private boolean checkPropertiesFileSinkGroupsList(List<String> agentsList) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN checkPropertiesFileSinkGroupsList");
        }

        boolean isPropertiesCheckFileOK = true;

        LinkedProperties sinkGroupsListProperties = FlumeConfiguratorUtils.matchingSubset(baseConfigurationProperties, FlumeConfiguratorConstants.SINKGROUPS_LIST_PROPERTIES_PREFIX, true);


        for (Object keyObject : sinkGroupsListProperties.keySet()) {

            String keyProperty = (String) keyObject;
            String valuesProperty = sinkGroupsListProperties.getProperty(keyProperty);

            if ("".equals(valuesProperty)) {
                isPropertiesCheckFileOK = false;
                sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("sinkGroups.list property [").append(keyProperty).append("] has an empty value").append(FlumeConfiguratorConstants.NEW_LINE);
            } else {
                //Check referenced agent exists
                String[] keyPropertyArray = keyProperty.split(FlumeConfiguratorConstants.DOT_REGEX);

                String agentName = keyPropertyArray[2];

                if (!agentsList.contains(agentName)) {
                    isPropertiesCheckFileOK = false;
                    sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("The property [").append(keyProperty).append("] references an non declared agent [").append(agentName).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END checkPropertiesFileSinkGroupsList");
        }

        return isPropertiesCheckFileOK;
    }

    /**
     * Check the information about groups
     * @param agentsList List with the agents list information
     * @return boolean true if the information about groups is correct false otherwise
     */
    private boolean checkPropertiesFileGroupsList(List<String> agentsList) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN checkPropertiesFileGroupsList");
        }

        boolean isPropertiesCheckFileOK = true;
        Map<String, List<String>> mapAgentsElements = new HashMap<>();

        LinkedProperties groupsListProperties = FlumeConfiguratorUtils.matchingSubset(baseConfigurationProperties, FlumeConfiguratorConstants.GROUPS_LIST_PROPERTIES_PREFIX, true);

        if (groupsListProperties.size() == 0) {
            isPropertiesCheckFileOK = false;
            sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("There is no groups.list property").append(FlumeConfiguratorConstants.NEW_LINE);
        } else {
            for (Object keyObject : groupsListProperties.keySet()) {

                String keyProperty = (String) keyObject;
                String valuesProperty = groupsListProperties.getProperty(keyProperty);

                if ("".equals(valuesProperty)) {
                    isPropertiesCheckFileOK = false;
                    sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("groups.list property [").append(keyProperty).append("] has an empty value").append(FlumeConfiguratorConstants.NEW_LINE);
                } else {
                    //Check referenced agent exists
                    String[] keyPropertyArray = keyProperty.split(FlumeConfiguratorConstants.DOT_REGEX);

                    String agentName = keyPropertyArray[2];

                    if (!agentsList.contains(agentName)) {
                        isPropertiesCheckFileOK = false;
                        sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("groups.list property [").append(keyProperty).append("] references an non declared agent").append(FlumeConfiguratorConstants.NEW_LINE);
                    }

                    //Check property value references declared elements for the agent
                    List<String> listValuesProperty = Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(valuesProperty,true,elementsCharacterSeparator));

                    //Generate a list with all sources, channels and sinks for the agent
                    String sourcesAgent = baseConfigurationProperties.getProperty( FlumeConfiguratorConstants.SOURCES_LIST_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + agentName);
                    String channelsAgent = baseConfigurationProperties.getProperty( FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + agentName);
                    String sinksAgent = baseConfigurationProperties.getProperty( FlumeConfiguratorConstants.SINKS_LIST_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + agentName);
                    String sinkGroupsAgent = baseConfigurationProperties.getProperty( FlumeConfiguratorConstants.SINKGROUPS_LIST_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + agentName);

                    List<String> listSourcesChannelsSinksAgent = new ArrayList<>();

                    if (sourcesAgent != null) {
                        listSourcesChannelsSinksAgent.addAll(Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(sourcesAgent,true,elementsCharacterSeparator)));
                    }
                    if (channelsAgent != null) {
                        listSourcesChannelsSinksAgent.addAll(Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(channelsAgent,true,elementsCharacterSeparator)));
                    }
                    if (sinksAgent != null) {
                        listSourcesChannelsSinksAgent.addAll(Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(sinksAgent,true,elementsCharacterSeparator)));
                    }
                    if (sinkGroupsAgent != null) {
                        listSourcesChannelsSinksAgent.addAll(Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(sinkGroupsAgent,true,elementsCharacterSeparator)));
                    }
                    for (Object valuePropertyObj : listValuesProperty) {
                        String valueProperty = (String) valuePropertyObj;

                        if (!listSourcesChannelsSinksAgent.contains(valueProperty)) {
                            isPropertiesCheckFileOK = false;
                            sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("groups.list property [").append(keyProperty).append("] references a non declared source/channel/sink/sinkgroup [").append(valueProperty).append("] for the agent [").append(agentName).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                        }

                        //Check the element belongs to more than one group for the same agent
                        if (mapAgentsElements.get(agentName) != null) {
                            if (mapAgentsElements.get(agentName).contains(valueProperty)) {
                                isPropertiesCheckFileOK = false;
                                sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("groups.list property [").append(keyProperty).append("] references a source/channel/sink/sinkgroup [").append(valueProperty).append("] that already belongs to another group of the same agent [").append(agentName).append("]").append(FlumeConfiguratorConstants.NEW_LINE);

                            } else {
                                mapAgentsElements.get(agentName).add(valueProperty);
                            }

                        } else {
                            List<String> listElementsAgent = new ArrayList<>();
                            listElementsAgent.add(valueProperty);
                            mapAgentsElements.put(agentName, listElementsAgent);
                        }

                    }
                }
            }

            //For every agent, check all his elements have been assigned
            for (String agentName : mapAgentsElements.keySet()) {

                String sourcesAgent = baseConfigurationProperties.getProperty( FlumeConfiguratorConstants.SOURCES_LIST_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + agentName);
                String channelsAgent = baseConfigurationProperties.getProperty( FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + agentName);
                String sinksAgent = baseConfigurationProperties.getProperty( FlumeConfiguratorConstants.SINKS_LIST_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + agentName);
                String sinkGroupsAgent = baseConfigurationProperties.getProperty( FlumeConfiguratorConstants.SINKGROUPS_LIST_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + agentName);
                int sourcesNum = 0;
                int channelsNum = 0;
                int sinksNum = 0;
                int sinkGroupsNum = 0;
                int totalElements;

                if (sourcesAgent != null) {
                    sourcesNum = FlumeConfiguratorUtils.splitWithoutSpacesOptional(sourcesAgent,true,elementsCharacterSeparator).length;
                }
                if (channelsAgent != null) {
                    channelsNum = FlumeConfiguratorUtils.splitWithoutSpacesOptional(channelsAgent,true,elementsCharacterSeparator).length;
                }
                if (sinksAgent != null) {
                    sinksNum = FlumeConfiguratorUtils.splitWithoutSpacesOptional(sinksAgent,true,elementsCharacterSeparator).length;
                }
                if (sinkGroupsAgent != null) {
                    sinkGroupsNum = FlumeConfiguratorUtils.splitWithoutSpacesOptional(sinkGroupsAgent,true,elementsCharacterSeparator).length;
                }

                totalElements = sourcesNum + channelsNum + sinksNum + sinkGroupsNum;

                if (mapAgentsElements.get(agentName).size() != totalElements) {
                    isPropertiesCheckFileOK = false;
                    sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("There are elements of the agent [").append(agentName).append("] that don't belong to the declared groups for the agent").append(FlumeConfiguratorConstants.NEW_LINE);

                }
            }

        }

        if (logger.isDebugEnabled()) {
            logger.debug("END checkPropertiesFileGroupsList");
        }

        return isPropertiesCheckFileOK;
    }


    /**
     * Check the information about selectors
     * @param agentsList List with the agents list information
     * @return boolean true if the information about selectors is correct false otherwise
     */
    private boolean checkPropertiesFileSelectorsList(List<String> agentsList) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN checkPropertiesFileSelectorsList");
        }

        boolean isPropertiesCheckFileOK = true;

        LinkedProperties sourcesWithSelectorsListProperties = FlumeConfiguratorUtils.matchingSubset(baseConfigurationProperties, FlumeConfiguratorConstants.SELECTORS_LIST_PROPERTIES_PREFIX, true);


        for (Object keyObject : sourcesWithSelectorsListProperties.keySet()) {

            String keyProperty = (String) keyObject;
            String valuesProperty = sourcesWithSelectorsListProperties.getProperty(keyProperty);

            if ("".equals(valuesProperty)) {
                isPropertiesCheckFileOK = false;
                sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("sourcesWithSelector.list property [").append(keyProperty).append("] has an empty value").append(FlumeConfiguratorConstants.NEW_LINE);
            } else {
                //Check referenced agent exists
                String[] keyPropertyArray = keyProperty.split(FlumeConfiguratorConstants.DOT_REGEX);

                String agentName = keyPropertyArray[2];

                if (!agentsList.contains(agentName)) {
                    isPropertiesCheckFileOK = false;
                    sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("sourcesWithSelector.list property [").append(keyProperty).append("] references a non declared agent [").append(agentName).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                } else {
                    //Check sources belong to agent
                    List<String> listSourcesAgent = new ArrayList<>();

                    //Get all sources from the agent
                    String sourcesAgent = baseConfigurationProperties.getProperty( FlumeConfiguratorConstants.SOURCES_LIST_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + agentName);
                    if (sourcesAgent != null) {
                        listSourcesAgent.addAll(Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(sourcesAgent,true,elementsCharacterSeparator)));
                    }

                    List<String> valuesPropertyList = Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(valuesProperty,true,elementsCharacterSeparator));

                    for (String valueProperty : valuesPropertyList) {
                        if (!listSourcesAgent.contains(valueProperty)) {
                            isPropertiesCheckFileOK = false;
                            sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property references a non declared source [").append(valueProperty).append("]").append(" for the agent [").append(agentName).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                        }

                        //Check source with selector have multiple channels
                        List<String> sourceChannelsList = FlumeConfiguratorUtils.getSourceChannels(baseConfigurationProperties, valueProperty, elementsCharacterSeparator);

                        if (sourceChannelsList.size() <= 1) {
                            isPropertiesCheckFileOK = false;
                            sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property references a source [").append(valueProperty).append("]").append(" without defined multiple channels").append(FlumeConfiguratorConstants.NEW_LINE);
                        }

                    }
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END checkPropertiesFileSelectorsList");
        }

        return isPropertiesCheckFileOK;
    }


    /**
     * Check the information about interceptors
     * @param agentsList List with the agents list information
     * @return boolean true if the information about interceptors is correct false otherwise
     */
    private boolean checkPropertiesFileInterceptorsList(List<String> agentsList) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN checkPropertiesFileInterceptorsList");
        }

        boolean isPropertiesCheckFileOK = true;

        LinkedProperties interceptorsListProperties = FlumeConfiguratorUtils.matchingSubset(baseConfigurationProperties, FlumeConfiguratorConstants.INTERCEPTORS_LIST_PROPERTIES_PREFIX, true);

        //Generate a list with all sources from all agents
        List<String> listSourcesAllAgents = new ArrayList<>();
        for (String agentNameList : agentsList) {
            String sourcesAgent = baseConfigurationProperties.getProperty( FlumeConfiguratorConstants.SOURCES_LIST_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + agentNameList);
            if (sourcesAgent != null) {
                listSourcesAllAgents.addAll(Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(sourcesAgent,true,elementsCharacterSeparator)));
            }
        }

        for (Object keyObject : interceptorsListProperties.keySet()) {

            String keyProperty = (String) keyObject;
            String valuesProperty = interceptorsListProperties.getProperty(keyProperty);

            if ("".equals(valuesProperty)) {
                isPropertiesCheckFileOK = false;
                sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("interceptors.list property [").append(keyProperty).append("] has an empty value").append(FlumeConfiguratorConstants.NEW_LINE);
            } else {
                //Check referenced source exists
                String[] keyPropertyArray = keyProperty.split(FlumeConfiguratorConstants.DOT_REGEX);

                String sourceName = keyPropertyArray[2];

                if (!listSourcesAllAgents.contains(sourceName)) {
                    isPropertiesCheckFileOK = false;
                    sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("interceptors.list property [").append(keyProperty).append("] references a non declared source [").append(sourceName).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                }

            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END checkPropertiesFileInterceptorsList");
        }

        return isPropertiesCheckFileOK;
    }


    /**
     * Check the information about common properties
     * @param prefixProperty String with the common property type to check (sources, channels, sinks, interceptors)
     * @param agentsList List with the agents list information
     * @return boolean true if the information about common properties is correct false otherwise
     */
    private boolean checkPropertiesFileCommonProperties(String prefixProperty, List<String> agentsList) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN checkPropertiesFileCommonProperties");
        }

        boolean isPropertiesCheckFileOK = true;
        List<String> listChannelsAllAgents = new ArrayList<>();
        List<String> listSinksAllAgents = new ArrayList<>();

        for (String agentNameList : agentsList) {
            String channelsAgent = baseConfigurationProperties.getProperty(FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + agentNameList);
            if (channelsAgent != null) {
                listChannelsAllAgents.addAll(Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(channelsAgent,true,elementsCharacterSeparator)));
            }

            String sinksAgent = baseConfigurationProperties.getProperty(FlumeConfiguratorConstants.SINKS_LIST_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + agentNameList);
            if (sinksAgent != null) {
                listSinksAllAgents.addAll(Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(sinksAgent,true,elementsCharacterSeparator)));
            }
        }



        LinkedProperties commonPropertiesList = FlumeConfiguratorUtils.matchingSubset(baseConfigurationProperties, prefixProperty, true);

        for (Object keyObject : commonPropertiesList.keySet()) {

            String keyProperty = (String) keyObject;
            String valuesProperty = commonPropertiesList.getProperty(keyProperty);

            //Get the name of the property from the key of the property
            String propertyName = keyProperty.substring(prefixProperty.length() + 1);

            //Check comment references a property that exists
            if (propertyName.startsWith(FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX)) {

                String propertyReference = propertyName.substring(FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX.length());
                String valuesPropertyReference = baseConfigurationProperties.getProperty(prefixProperty + propertyReference);

                if (valuesPropertyReference == null) {
                    isPropertiesCheckFileOK = false;
                    sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] comment property references a non declared property [").append(propertyReference).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                }

            } else {
                if ("".equals(valuesProperty)) {
                    isPropertiesCheckFileOK = false;
                    sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property has an empty value").append(FlumeConfiguratorConstants.NEW_LINE);
                } else {

                    //Check sources common property (.channels) reference declared elements
                    if (FlumeConfiguratorUtils.isSourceChannelsProperty(keyProperty)) {

                        //Get the list of agents of the channel(s) (the channels of a source can be multiple and separated by white spaces)
                        List<String> listChannels = Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(valuesProperty,false,FlumeConfiguratorConstants.WHITE_SPACE_REGEX));
                        for (String channel : listChannels) {
                            List<String> listAgentsChannelPartial = FlumeConfiguratorUtils.getElementsAgents(baseConfigurationProperties, FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX, channel);
                            if (listAgentsChannelPartial.isEmpty()) {
                                //There is no agent for the channel
                                isPropertiesCheckFileOK = false;
                                sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property has a channel value [").append(channel).append("] whose agent is not declared").append(FlumeConfiguratorConstants.NEW_LINE);
                            }

                        }

                        //Check if the list of properties contains all properties of all agents. If that is not the case we are setting as channels for every source a subset of posible channels.
                        //and that could be an error (if a partial property doesn't fix it)
                        if(!listChannels.containsAll(listChannelsAllAgents)) {
                            logger.warn("A common property has been declared [" + keyProperty + "] that affects to channel(s). There are declared channels not affected by the property");
                        }

                    //Check sinks common property (.channel) reference declared elements
                    } else if (FlumeConfiguratorUtils.isSinkChannelProperty(keyProperty)) {


                        List<String> valuesPropertyList = Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(valuesProperty,true,elementsCharacterSeparator));

                        for (String valueProperty : valuesPropertyList) {

                            if (!listChannelsAllAgents.contains(valueProperty)) {
                                isPropertiesCheckFileOK = false;
                                sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property references a non declared channel [").append(valueProperty).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                            }

                        }

                        //Check if the list of properties contains all properties of all agents. If that is not the case we are setting as channels for every source a subset of posible channels.
                        //and that could be an error (if a partial property doesn't fix it)
                        if(!valuesPropertyList.containsAll(listChannelsAllAgents)) {
                            logger.warn("A common property has been declared [" + keyProperty + "]that affects to channel(s). There are declared channels not affected by the property");
                        }

                    //Check sinkgroups common property (.sinks) reference declared elements
                    } else if (FlumeConfiguratorUtils.isSinkGroupsSinksProperty(keyProperty)) {

                        //Get the list of agents of the sink(s) (the sinks of a sinkgroup can be multiple and separated by white spaces)
                        List<String> listSinks = Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(valuesProperty,false,FlumeConfiguratorConstants.WHITE_SPACE_REGEX));
                        for (String sink : listSinks) {
                            List<String> listAgentsSinkPartial = FlumeConfiguratorUtils.getElementsAgents(baseConfigurationProperties, FlumeConfiguratorConstants.SINKS_LIST_PROPERTIES_PREFIX, sink);
                            if (listAgentsSinkPartial.isEmpty()) {
                                //There is no agent for the sink
                                isPropertiesCheckFileOK = false;
                                sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property has a sink value [").append(sink).append("] whose agent is not declared").append(FlumeConfiguratorConstants.NEW_LINE);
                            }

                        }

                    //Check selector common property that references channels (selector.mapping..., selector.optional... or selector.default) reference declared elements
                    } else if (FlumeConfiguratorUtils.isSelectorChannelReferenceBaseConfigurationProperty(keyProperty)) {

                        //Get the list of agents of the channel(s) (the channels of this kind of properties can be multiple and separated by white spaces)
                        List<String> listChannels = Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(valuesProperty,false,FlumeConfiguratorConstants.WHITE_SPACE_REGEX));
                        for (String channel : listChannels) {
                            List<String> listAgentsChannelPartial = FlumeConfiguratorUtils.getElementsAgents(baseConfigurationProperties, FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX, channel);
                            if (listAgentsChannelPartial.isEmpty()) {
                                //There is no agent for the channel
                                isPropertiesCheckFileOK = false;
                                sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property has a channel value [").append(channel).append("] whose agent is not declared").append(FlumeConfiguratorConstants.NEW_LINE);
                            }

                        }

                        //Check all sources with selector of all agents have these channels as channels property

                        //Get all sources with Selector of all agents
                        LinkedProperties agentsSourcesWithSelectorList = FlumeConfiguratorUtils.matchingSubset(baseConfigurationProperties, FlumeConfiguratorConstants.SELECTORS_LIST_PROPERTIES_PREFIX, true);

                        for (Object keySelectorObject : agentsSourcesWithSelectorList.keySet()) {

                            String keySourceWithSelectorProperty = (String) keySelectorObject;
                            String valuesSourceWithSelectorProperty = agentsSourcesWithSelectorList.getProperty(keySourceWithSelectorProperty);
                            List<String> listValuesSourceWithSelectorProperty = Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptionalKeepInternalSpaces(valuesSourceWithSelectorProperty, true, elementsCharacterSeparator));

                            for (String sourceWithSelector : listValuesSourceWithSelectorProperty) {

                                //Get all channels of the source with Selector
                                List<String> sourceChannelsList = FlumeConfiguratorUtils.getSourceChannels(baseConfigurationProperties, sourceWithSelector, elementsCharacterSeparator);

                                //Check list of channels (propertyValue) is a subset of list of channels of the source
                                if (!sourceChannelsList.containsAll(listChannels)) {
                                    isPropertiesCheckFileOK = false;
                                    sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property has a channel(s) value [").append(valuesProperty).append("] with channels that are not declared as channels of the selector source [").append(sourceWithSelector).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END checkPropertiesFileCommonProperties");
        }

        return isPropertiesCheckFileOK;
    }

    /**
     * Check the information about partial properties
     * @param prefixProperty String with the partial property type to check (sources, channels, sinks, interceptors)
     * @param agentsList List with the agents list information
     * @return boolean true if the information about partial properties is correct false otherwise
     */
    private boolean checkPropertiesFilePartialProperties(String prefixProperty, List<String> agentsList) {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN checkPropertiesFilePartialProperties");
        }

        boolean isPropertiesCheckFileOK = true;

        //Generata a list with all sources of all agents
        List<String> listSourcesAllAgents = new ArrayList<>();
        List<String> listAgentsSource;
        List<String> listChannelsAllAgents = new ArrayList<>();
        List<String> listAgentsChannel = new ArrayList<>();
        List<String> listSinksAllAgents = new ArrayList<>();
        List<String> listAgentsSink = new ArrayList<>();
        List<String> listSourcesWithSelectorAllAgents = new ArrayList<>();
        List<String> listInterceptorsAllSources = new ArrayList<>();
        List<String> listSinkGroupsAllAgents = new ArrayList<>();
        List<String> listAgentsSinkGroup = new ArrayList<>();

        for (String agentNameList : agentsList) {
            String sourcesAgent = baseConfigurationProperties.getProperty(FlumeConfiguratorConstants.SOURCES_LIST_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + agentNameList);
            if (sourcesAgent != null) {
                listSourcesAllAgents.addAll(Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(sourcesAgent,true,elementsCharacterSeparator)));
            }
        }

        for (String agentNameList : agentsList) {
            String channelsAgent = baseConfigurationProperties.getProperty(FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + agentNameList);
            if (channelsAgent != null) {
                listChannelsAllAgents.addAll(Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(channelsAgent,true,elementsCharacterSeparator)));
            }
        }

        for (String agentNameList : agentsList) {
            String sinksAgent = baseConfigurationProperties.getProperty(FlumeConfiguratorConstants.SINKS_LIST_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + agentNameList);
            if (sinksAgent != null) {
                listSinksAllAgents.addAll(Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(sinksAgent,true,elementsCharacterSeparator)));
            }
        }

        for (String agentNameList : agentsList) {
            String sinkGroupsAgent = baseConfigurationProperties.getProperty(FlumeConfiguratorConstants.SINKGROUPS_LIST_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + agentNameList);
            if (sinkGroupsAgent != null) {
                listSinkGroupsAllAgents.addAll(Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(sinkGroupsAgent,true,elementsCharacterSeparator)));
            }
        }

        for (String agentNameList : agentsList) {
            String sourcesWithSelectorAgent = baseConfigurationProperties.getProperty(FlumeConfiguratorConstants.SELECTORS_LIST_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + agentNameList);
            if (sourcesWithSelectorAgent != null) {
                listSourcesWithSelectorAllAgents.addAll(Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(sourcesWithSelectorAgent,true,elementsCharacterSeparator)));
            }
        }

        for (String sourceNameList : listSourcesAllAgents) {
            String interceptorsSource = baseConfigurationProperties.getProperty(FlumeConfiguratorConstants.INTERCEPTORS_LIST_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + sourceNameList);
            if (interceptorsSource != null) {
                listInterceptorsAllSources.addAll(Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(interceptorsSource,true,elementsCharacterSeparator)));
            }
        }



        LinkedProperties partialPropertiesList = FlumeConfiguratorUtils.matchingSubset(baseConfigurationProperties, prefixProperty, true);

        for (Object keyObject : partialPropertiesList.keySet()) {

            String keyProperty = (String) keyObject;
            String valuesProperty = partialPropertiesList.getProperty(keyProperty);
            List<String> listValuesProperty = Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptionalKeepInternalSpaces(valuesProperty,true,elementsCharacterSeparator));

            //Get the name of the property from the key of the property
            String propertyName = keyProperty.substring(prefixProperty.length() + 1);

            if (propertyName.startsWith(FlumeConfiguratorConstants.PARTIAL_PROPERTY_APPLIED_ELEMENTS_PROPERTIES_PREFIX)) {

                //Check property value is not empty
                if ("".equals(valuesProperty)) {
                    isPropertiesCheckFileOK = false;
                    sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property has an empty value").append(FlumeConfiguratorConstants.NEW_LINE);
                } else {

                    //Check propertyValues property exists
                    String propertyReference = propertyName.substring(FlumeConfiguratorConstants.PARTIAL_PROPERTY_APPLIED_ELEMENTS_PROPERTIES_PREFIX.length());
                    String valuesPropertyReference = baseConfigurationProperties.getProperty(prefixProperty + FlumeConfiguratorConstants.DOT_SEPARATOR + FlumeConfiguratorConstants.PARTIAL_PROPERTY_PROPERTY_VALUES_PROPERTIES_PREFIX + propertyReference);

                    if (valuesPropertyReference == null) {
                        isPropertiesCheckFileOK = false;
                        sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] hasn't ").append(FlumeConfiguratorConstants.PARTIAL_PROPERTY_PROPERTY_VALUES_PROPERTIES_PREFIX).append(" property [").append(propertyReference).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                    }


                    //Check value property references declared elements
                    for (Object valuePropertyObj : listValuesProperty) {
                        String valueProperty = (String) valuePropertyObj;

                        if (!listSourcesAllAgents.contains(valueProperty) && !listChannelsAllAgents.contains(valueProperty)
                                && !listSinksAllAgents.contains(valueProperty) && !listInterceptorsAllSources.contains(valueProperty)
                                && !listSinkGroupsAllAgents.contains(valueProperty)) {
                            isPropertiesCheckFileOK = false;
                            sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] references a non declared source/channel/sink/sinkgroup/interceptor [").append(valueProperty).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                        }

                    }

                    //Check value property reference a defined source with selector
                    if (FlumeConfiguratorConstants.SELECTORS_PARTIAL_PROPERTY_PROPERTIES_PREFIX.equals(prefixProperty)) {

                        for (Object valuePropertyObj : listValuesProperty) {
                            String valueProperty = (String) valuePropertyObj;

                            if (!listSourcesWithSelectorAllAgents.contains(valueProperty)) {
                                isPropertiesCheckFileOK = false;
                                sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] references a non declared source with selector[").append(valueProperty).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                            }

                        }
                    }

                }

            } else if (propertyName.startsWith(FlumeConfiguratorConstants.PARTIAL_PROPERTY_PROPERTY_VALUES_PROPERTIES_PREFIX)) {

                //Check property value is not empty
                if ("".equals(valuesProperty)) {
                    isPropertiesCheckFileOK = false;
                    sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property has an empty value").append(FlumeConfiguratorConstants.NEW_LINE);
                } else {

                    //Check appliedElements property exists
                    String propertyReference = propertyName.substring(FlumeConfiguratorConstants.PARTIAL_PROPERTY_PROPERTY_VALUES_PROPERTIES_PREFIX.length());
                    String valuesPropertyReference = baseConfigurationProperties.getProperty(prefixProperty + FlumeConfiguratorConstants.DOT_SEPARATOR + FlumeConfiguratorConstants.PARTIAL_PROPERTY_APPLIED_ELEMENTS_PROPERTIES_PREFIX + propertyReference);

                    if (valuesPropertyReference == null) {
                        isPropertiesCheckFileOK = false;
                        sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property hasn't ").append(FlumeConfiguratorConstants.PARTIAL_PROPERTY_APPLIED_ELEMENTS_PROPERTIES_PREFIX).append(" property [").append(propertyReference).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                    } else {

                        //Check value of the property has a correct dimension
                        List<String> listValuesPropertyReference = Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(valuesPropertyReference,true,elementsCharacterSeparator));

                        if (listValuesProperty.size() != 1 && listValuesProperty.size() != listValuesPropertyReference.size()) {
                            isPropertiesCheckFileOK = false;
                            sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property has an incorrect dimension").append(FlumeConfiguratorConstants.NEW_LINE);
                        }


                        //Check partial property of the source (.channels) references declared elements
                        if (FlumeConfiguratorUtils.isSourceChannelsProperty(keyProperty)) {
                            //Source (.channels) partial property

                            //Depends if the values of the property (propertyValues) is setted for all elements of applidaElements property) or is setted one single time (and replicated after)
                            if (listValuesProperty.size() == 1) {
                                //Get the list of agents of the channel(s) (the channels of a source can be multiple and separated by white spaces)
                                List<String> listChannels = Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(valuesProperty,false, FlumeConfiguratorConstants.WHITE_SPACE_REGEX));
                                for (String channel : listChannels) {
                                    List<String> listAgentsChannelPartial = FlumeConfiguratorUtils.getElementsAgents(baseConfigurationProperties, FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX, channel);
                                    if (listAgentsChannelPartial.isEmpty()) {
                                        //There is no agent for the channel
                                        isPropertiesCheckFileOK = false;
                                        sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property has a channel value [").append(channel).append("] whose agent is not declared").append(FlumeConfiguratorConstants.NEW_LINE);
                                    }

                                    for (String agentChannel : listAgentsChannelPartial) {
                                        listAgentsChannel.add(agentChannel);
                                    }
                                }

                                for (String valuePropertyReference : listValuesPropertyReference) {
                                    //Get the list of agents for every source and compare with the list of agents of the channel(s)
                                    listAgentsSource = FlumeConfiguratorUtils.getElementsAgents(baseConfigurationProperties, FlumeConfiguratorConstants.SOURCES_LIST_PROPERTIES_PREFIX, valuePropertyReference);

                                    if (!listAgentsSource.containsAll(listAgentsChannel)) {
                                        //The list of agents of the channel contains agents that are not declared for the source
                                        isPropertiesCheckFileOK = false;
                                        sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property has a channel value [").append(listValuesProperty.get(0)).append("] whose agent is not declared for the source [").append(valuePropertyReference).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                                    }

                                }

                            } else {
                                //The values list size is greater than 1. Check every source with every channel value in order to determinate if are applied to the same agent
                                for (int index = 0; index < listValuesPropertyReference.size(); index++) {
                                    String valuePropertyReference = listValuesPropertyReference.get(index);
                                    valuesProperty = listValuesProperty.get(index);

                                    listAgentsSource = FlumeConfiguratorUtils.getElementsAgents(baseConfigurationProperties, FlumeConfiguratorConstants.SOURCES_LIST_PROPERTIES_PREFIX, valuePropertyReference);
                                    listAgentsChannel.clear();

                                    //Get the list of agents of the specified channel (or channels)
                                    List<String> listChannels = Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(valuesProperty,false, FlumeConfiguratorConstants.WHITE_SPACE_REGEX));
                                    for (String channel : listChannels) {
                                        List<String> listAgentsChannelPartial = FlumeConfiguratorUtils.getElementsAgents(baseConfigurationProperties, FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX, channel);
                                        if (listAgentsChannelPartial.isEmpty()) {
                                            //There is no agent for the channel
                                            isPropertiesCheckFileOK = false;
                                            sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property has a channel value [").append(channel).append("] whose agent is not declared for the source [").append(valuePropertyReference).append("]").append(FlumeConfiguratorConstants.NEW_LINE);

                                        }
                                        for (String agentChannel : listAgentsChannelPartial) {
                                            listAgentsChannel.add(agentChannel);
                                        }
                                    }

                                    if (!listAgentsSource.containsAll(listAgentsChannel)) {
                                        //The list of agents of the specified channel contains non defined agents for the source
                                        isPropertiesCheckFileOK = false;
                                        sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property has a channel value [").append(valuesProperty).append("] whose agent is not declared for the source [").append(valuePropertyReference).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                                    }

                                }
                            }

                        //Check partial property of the sink (.channel) references declared elements
                        } else if (FlumeConfiguratorUtils.isSinkChannelProperty(keyProperty)) {
                            //Sink (.channel) partial property

                            //Depends if the values of the property (propertyValues) is setted for all elements of applidaElements property) or is setted one single time (and replicated after)
                            if (listValuesProperty.size() == 1) {
                                //Get the list of agents of the xpecified channel
                                List<String> listChannels = Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(valuesProperty,true,elementsCharacterSeparator));
                                for (String channel : listChannels) {
                                    List<String> listAgentsChannelPartial = FlumeConfiguratorUtils.getElementsAgents(baseConfigurationProperties, FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX, channel);
                                    if (listAgentsChannelPartial.isEmpty()) {
                                        //There is no agent for the channel
                                        isPropertiesCheckFileOK = false;
                                        sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property has a channel value [").append(channel).append("] whose agent is not declared").append(FlumeConfiguratorConstants.NEW_LINE);
                                    }

                                    for (String agentChannel : listAgentsChannelPartial) {
                                        listAgentsChannel.add(agentChannel);
                                    }
                                }

                                for (String valuePropertyReference : listValuesPropertyReference) {
                                    //Get the list of agents for every source and compare with the list of agents of the channel
                                    listAgentsSink = FlumeConfiguratorUtils.getElementsAgents(baseConfigurationProperties, FlumeConfiguratorConstants.SINKS_LIST_PROPERTIES_PREFIX, valuePropertyReference);

                                    if (!listAgentsSink.containsAll(listAgentsChannel)) {
                                        //The list of agents of the specified channel contains non defined agents for the sources
                                        isPropertiesCheckFileOK = false;
                                        sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property has a channel value [").append(listValuesProperty.get(0)).append("] whose agent is not declared for the sink [").append(valuePropertyReference).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                                    }

                                }

                            } else {
                                //The values list size is greater than 1. Check every sink with every channel value in order to determinate if are applied to the same agent
                                for (int index = 0; index < listValuesPropertyReference.size(); index++) {
                                    String valuePropertyReference = listValuesPropertyReference.get(index);
                                    valuesProperty = listValuesProperty.get(index);

                                    listAgentsSink = FlumeConfiguratorUtils.getElementsAgents(baseConfigurationProperties, FlumeConfiguratorConstants.SINKS_LIST_PROPERTIES_PREFIX, valuePropertyReference);
                                    listAgentsChannel.clear();

                                    //Get the list of agents of the specified channel
                                    List<String> listChannels = Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(valuesProperty,true,elementsCharacterSeparator));
                                    for (String channel : listChannels) {
                                        List<String> listAgentsChannelPartial = FlumeConfiguratorUtils.getElementsAgents(baseConfigurationProperties, FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX, channel);
                                        if (listAgentsChannelPartial.isEmpty()) {
                                            //There is no agent for the channel
                                            isPropertiesCheckFileOK = false;
                                            sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property has a channel value [").append(channel).append("] whose agent is not declared for the sink [").append(valuePropertyReference).append("]").append(FlumeConfiguratorConstants.NEW_LINE);

                                        }
                                        for (String agentChannel : listAgentsChannelPartial) {
                                            listAgentsChannel.add(agentChannel);
                                        }
                                    }

                                    if (!listAgentsSink.containsAll(listAgentsChannel)) {
                                        //The list of agents of the specified channel contains non defined agents for the sink
                                        isPropertiesCheckFileOK = false;
                                        sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property has a channel value [").append(valuesProperty).append("] whose agent is not declared for the sink [").append(valuePropertyReference).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                                    }

                                }
                            }

                        //Check partial property of the sinkgroup (.sinks) references declared elements
                        } else if (FlumeConfiguratorUtils.isSinkGroupsSinksProperty(keyProperty)) {
                            //Sinkgroup (.sinks) partial property

                            //Depends if the values of the property (propertyValues) is setted for all elements of applidaElements property) or is setted one single time (and replicated after)
                            if (listValuesProperty.size() == 1) {
                                //Get the list of agents of the sink(s) (the sinks of a sinkgroup can be multiple and separated by white spaces)
                                List<String> listSinks = Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(valuesProperty, false, FlumeConfiguratorConstants.WHITE_SPACE_REGEX));
                                for (String sink : listSinks) {
                                    List<String> listAgentsSinkPartial = FlumeConfiguratorUtils.getElementsAgents(baseConfigurationProperties, FlumeConfiguratorConstants.SINKS_LIST_PROPERTIES_PREFIX, sink);
                                    if (listAgentsSinkPartial.isEmpty()) {
                                        //There is no agent for the sink
                                        isPropertiesCheckFileOK = false;
                                        sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property has a sink value [").append(sink).append("] whose agent is not declared").append(FlumeConfiguratorConstants.NEW_LINE);
                                    }

                                    for (String agentSink : listAgentsSinkPartial) {
                                        listAgentsSink.add(agentSink);
                                    }
                                }

                                for (String valuePropertyReference : listValuesPropertyReference) {
                                    //Get the list of agents for every sinkgroup and compare with the list of agents of the sink(s)
                                    listAgentsSinkGroup = FlumeConfiguratorUtils.getElementsAgents(baseConfigurationProperties, FlumeConfiguratorConstants.SINKGROUPS_LIST_PROPERTIES_PREFIX, valuePropertyReference);

                                    if (!listAgentsSinkGroup.containsAll(listAgentsSink)) {
                                        //The list of agents of the sink contains agents that are not declared for the sink group
                                        isPropertiesCheckFileOK = false;
                                        sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property has a sink value [").append(listValuesProperty.get(0)).append("] whose agent is not declared for the sink group [").append(valuePropertyReference).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                                    }

                                }

                            } else {
                                //The values list size is greater than 1. Check every sink group with every sink value in order to determinate if are applied to the same agent
                                for (int index = 0; index < listValuesPropertyReference.size(); index++) {
                                    String valuePropertyReference = listValuesPropertyReference.get(index);
                                    valuesProperty = listValuesProperty.get(index);

                                    listAgentsSinkGroup = FlumeConfiguratorUtils.getElementsAgents(baseConfigurationProperties, FlumeConfiguratorConstants.SINKGROUPS_LIST_PROPERTIES_PREFIX, valuePropertyReference);
                                    listAgentsSink.clear();

                                    //Get the list of agents of the specified sink (or sinks)
                                    List<String> listSinks = Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(valuesProperty, false, FlumeConfiguratorConstants.WHITE_SPACE_REGEX));
                                    for (String sink : listSinks) {
                                        List<String> listAgentsSinkPartial = FlumeConfiguratorUtils.getElementsAgents(baseConfigurationProperties, FlumeConfiguratorConstants.SINKS_LIST_PROPERTIES_PREFIX, sink);
                                        if (listAgentsSinkPartial.isEmpty()) {
                                            //There is no agent for the channel
                                            isPropertiesCheckFileOK = false;
                                            sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property has a channel value [").append(sink).append("] whose agent is not declared for the sink group [").append(valuePropertyReference).append("]").append(FlumeConfiguratorConstants.NEW_LINE);

                                        }
                                        for (String agentSink : listAgentsSinkPartial) {
                                            listAgentsSink.add(agentSink);
                                        }
                                    }

                                    if (!listAgentsSinkGroup.containsAll(listAgentsSink)) {
                                        //The list of agents of the specified sink contains non defined agents for the sink group
                                        isPropertiesCheckFileOK = false;
                                        sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property has a sink value [").append(valuesProperty).append("] whose agent is not declared for the  sink group [").append(valuePropertyReference).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                                    }

                                }
                            }

                        //Check partial property of the selectors (property that reference channels) references declared elements
                        } else if (FlumeConfiguratorUtils.isSelectorChannelReferenceBaseConfigurationProperty(keyProperty)) {
                            //Selector (property that reference channels)

                            //Depends if the values of the property (propertyValues) is setted for all elements of applidaElements property) or is setted one single time (and replicated after)
                            if (listValuesProperty.size() == 1) {

                                //Get the list of agents of the channel(s) (the channels of a source selector can be multiple and separated by white spaces)
                                List<String> listChannels = Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(valuesProperty, false, FlumeConfiguratorConstants.WHITE_SPACE_REGEX));
                                for (String channel : listChannels) {
                                    List<String> listAgentsChannelPartial = FlumeConfiguratorUtils.getElementsAgents(baseConfigurationProperties, FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX, channel);
                                    if (listAgentsChannelPartial.isEmpty()) {
                                        //There is no agent for the channel
                                        isPropertiesCheckFileOK = false;
                                        sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property has a channel value [").append(channel).append("] whose agent is not declared").append(FlumeConfiguratorConstants.NEW_LINE);
                                    }

                                    for (String agentChannel : listAgentsChannelPartial) {
                                        listAgentsChannel.add(agentChannel);
                                    }
                                }

                                for (String valuePropertyReference : listValuesPropertyReference) {

                                    //Get all channels of the source
                                    List<String> sourceChannelsList = FlumeConfiguratorUtils.getSourceChannels(baseConfigurationProperties, valuePropertyReference, elementsCharacterSeparator);

                                    //Get the list of agents for every selector source and compare with the list of agents of the channel(s)
                                    listAgentsSource = FlumeConfiguratorUtils.getElementsAgents(baseConfigurationProperties, FlumeConfiguratorConstants.SOURCES_LIST_PROPERTIES_PREFIX, valuePropertyReference);

                                    if (!listAgentsSource.containsAll(listAgentsChannel)) {
                                        //The list of agents of the channel contains agents that are not declared for the selector source
                                        isPropertiesCheckFileOK = false;
                                        sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property has a channel value [").append(listValuesProperty.get(0)).append("] whose agent is not declared for the selector source [").append(valuePropertyReference).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                                    }

                                    //Check list of channels (propertyValue) is a subset of list of channels of the source
                                    if (!sourceChannelsList.containsAll(listChannels)) {
                                        isPropertiesCheckFileOK = false;
                                        sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property has a channel(s) value [").append(valuesProperty).append("] with channels that are not declared as channels of the selector source [").append(valuePropertyReference).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                                    }

                                }

                            } else {
                                //The values list size is greater than 1. Check every source with every channel value in order to determinate if are applied to the same agent
                                for (int index = 0; index < listValuesPropertyReference.size(); index++) {
                                    String valuePropertyReference = listValuesPropertyReference.get(index);
                                    valuesProperty = listValuesProperty.get(index);

                                    listAgentsSource = FlumeConfiguratorUtils.getElementsAgents(baseConfigurationProperties, FlumeConfiguratorConstants.SOURCES_LIST_PROPERTIES_PREFIX, valuePropertyReference);
                                    listAgentsChannel.clear();

                                    //Get all channels of the source
                                    List<String> sourceChannelsList = FlumeConfiguratorUtils.getSourceChannels(baseConfigurationProperties, valuePropertyReference, elementsCharacterSeparator);

                                    //Get the list of agents of the specified channel (or channels)
                                    List<String> listChannels = Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(valuesProperty, false, FlumeConfiguratorConstants.WHITE_SPACE_REGEX));
                                    for (String channel : listChannels) {
                                        List<String> listAgentsChannelPartial = FlumeConfiguratorUtils.getElementsAgents(baseConfigurationProperties, FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX, channel);
                                        if (listAgentsChannelPartial.isEmpty()) {
                                            //There is no agent for the channel
                                            isPropertiesCheckFileOK = false;
                                            sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property has a channel value [").append(channel).append("] whose agent is not declared for the selector source [").append(valuePropertyReference).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                                        }

                                        for (String agentChannel : listAgentsChannelPartial) {
                                            listAgentsChannel.add(agentChannel);
                                        }
                                    }

                                    if (!listAgentsSource.containsAll(listAgentsChannel)) {
                                        //The list of agents of the specified channel contains non defined agents for the source
                                        isPropertiesCheckFileOK = false;
                                        sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property has a channel value [").append(valuesProperty).append("] whose agent is not declared for the selector source [").append(valuePropertyReference).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                                    }


                                    //Check list of channels (propertyValue) is a subset of list of channels of the source
                                    if (!sourceChannelsList.containsAll(listChannels)) {
                                        isPropertiesCheckFileOK = false;
                                        sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property has a channel(s) value [").append(valuesProperty).append("] with channels that are not declared as channels of the selector source  [").append(valuePropertyReference).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                                    }
                                }
                            }
                        }
                    }
                }

            } else if (propertyName.startsWith(FlumeConfiguratorConstants.PARTIAL_PROPERTY_COMMENT_PROPERTIES_PREFIX)) {

                //Check appliedElements property exists
                String propertyReference = propertyName.substring(FlumeConfiguratorConstants.PARTIAL_PROPERTY_COMMENT_PROPERTIES_PREFIX.length());
                String valuesPropertyReference = baseConfigurationProperties.getProperty(prefixProperty + FlumeConfiguratorConstants.DOT_SEPARATOR + FlumeConfiguratorConstants.PARTIAL_PROPERTY_APPLIED_ELEMENTS_PROPERTIES_PREFIX + propertyReference);

                if (valuesPropertyReference == null) {
                    isPropertiesCheckFileOK = false;
                    sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property hasn't ").append(FlumeConfiguratorConstants.PARTIAL_PROPERTY_APPLIED_ELEMENTS_PROPERTIES_PREFIX).append(" property [").append(propertyReference).append("]").append(FlumeConfiguratorConstants.NEW_LINE);
                } else {

                    //Check value of the property has a correct dimension
                    List<String> listValuesPropertyReference = Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(valuesPropertyReference,true,elementsCharacterSeparator));

                    if (!listValuesProperty.isEmpty() && listValuesProperty.size() != 1 && listValuesProperty.size() != listValuesPropertyReference.size()) {
                        isPropertiesCheckFileOK = false;
                        sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] property has an incorrect dimension").append(FlumeConfiguratorConstants.NEW_LINE);
                    }

                }

            } else {
                //The property is not one of the valid property types
                isPropertiesCheckFileOK = false;
                sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(keyProperty).append("] partial property is not one of the allowed types for partial properties").append(FlumeConfiguratorConstants.NEW_LINE);

            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("END checkPropertiesFilePartialProperties");
        }

        return isPropertiesCheckFileOK;
    }


    /**
     * Check if the properties configuration file is correct. Store the found errors
     */
    public void validateBaseConfiguration() {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN validateBaseConfiguration");
        }

        List<String> agentsList = new ArrayList<>();


        //Check all properties begin correctly
        for (Object flumeConfigurationProperty : baseConfigurationProperties.keySet()) {

            String flumeConfigurationPropertyString = (String) flumeConfigurationProperty;

            if (!flumeConfigurationPropertyString.startsWith(FlumeConfiguratorConstants.AGENTS_LIST_PROPERTIES_PREFIX)
                    && !flumeConfigurationPropertyString.startsWith(FlumeConfiguratorConstants.SOURCES_LIST_PROPERTIES_PREFIX)
                    && !flumeConfigurationPropertyString.startsWith(FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX)
                    && !flumeConfigurationPropertyString.startsWith(FlumeConfiguratorConstants.SINKS_LIST_PROPERTIES_PREFIX)
                    && !flumeConfigurationPropertyString.startsWith(FlumeConfiguratorConstants.SINKGROUPS_LIST_PROPERTIES_PREFIX)
                    && !flumeConfigurationPropertyString.startsWith(FlumeConfiguratorConstants.GROUPS_LIST_PROPERTIES_PREFIX)
                    && !flumeConfigurationPropertyString.startsWith(FlumeConfiguratorConstants.SOURCES_COMMON_PROPERTY_PROPERTIES_PREFIX)
                    && !flumeConfigurationPropertyString.startsWith(FlumeConfiguratorConstants.SOURCES_PARTIAL_PROPERTY_PROPERTIES_PREFIX)
                    && !flumeConfigurationPropertyString.startsWith(FlumeConfiguratorConstants.SELECTORS_LIST_PROPERTIES_PREFIX)
                    && !flumeConfigurationPropertyString.startsWith(FlumeConfiguratorConstants.SELECTORS_COMMON_PROPERTY_PROPERTIES_PREFIX)
                    && !flumeConfigurationPropertyString.startsWith(FlumeConfiguratorConstants.SELECTORS_PARTIAL_PROPERTY_PROPERTIES_PREFIX)
                    && !flumeConfigurationPropertyString.startsWith(FlumeConfiguratorConstants.INTERCEPTORS_LIST_PROPERTIES_PREFIX)
                    && !flumeConfigurationPropertyString.startsWith(FlumeConfiguratorConstants.INTERCEPTORS_COMMON_PROPERTY_PROPERTIES_PREFIX)
                    && !flumeConfigurationPropertyString.startsWith(FlumeConfiguratorConstants.INTERCEPTORS_PARTIAL_PROPERTY_PROPERTIES_PREFIX)
                    && !flumeConfigurationPropertyString.startsWith(FlumeConfiguratorConstants.CHANNELS_COMMON_PROPERTY_PROPERTIES_PREFIX)
                    && !flumeConfigurationPropertyString.startsWith(FlumeConfiguratorConstants.CHANNELS_PARTIAL_PROPERTY_PROPERTIES_PREFIX)
                    && !flumeConfigurationPropertyString.startsWith(FlumeConfiguratorConstants.SINKS_COMMON_PROPERTY_PROPERTIES_PREFIX)
                    && !flumeConfigurationPropertyString.startsWith(FlumeConfiguratorConstants.SINKS_PARTIAL_PROPERTY_PROPERTIES_PREFIX)
                    && !flumeConfigurationPropertyString.startsWith(FlumeConfiguratorConstants.SINKGROUPS_COMMON_PROPERTY_PROPERTIES_PREFIX)
                    && !flumeConfigurationPropertyString.startsWith(FlumeConfiguratorConstants.SINKGROUPS_PARTIAL_PROPERTY_PROPERTIES_PREFIX)) {

                isPropertiesFileOK = false;
                sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("[").append(flumeConfigurationPropertyString).append("] property is not allowed").append(FlumeConfiguratorConstants.NEW_LINE);

            }
        }




        //Check agents.list
        String agentList = baseConfigurationProperties.getProperty(FlumeConfiguratorConstants.AGENTS_LIST_PROPERTIES_PREFIX);

        if (agentList == null) {
            isPropertiesFileOK = false;
            sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("There is no agent.list property").append(FlumeConfiguratorConstants.NEW_LINE);
        } else {
            if ("".equals(agentList)) {
                isPropertiesFileOK = false;
                sbCheckErrors.append(FlumeConfiguratorConstants.CONFIGURATION_ERROR).append("agent.list property has an empty value").append(FlumeConfiguratorConstants.NEW_LINE);
            } else {
                //Property agent.list OK
                agentsList = Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(agentList,true,elementsCharacterSeparator));
            }
        }


        //Check sources.list
        isPropertiesFileOK = checkPropertiesFileSourcesChannelsSinksList(FlumeConfiguratorConstants.SOURCES_LIST_PROPERTIES_PREFIX, agentsList) && isPropertiesFileOK;

        //Check channels.list
        isPropertiesFileOK = checkPropertiesFileSourcesChannelsSinksList(FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX, agentsList) && isPropertiesFileOK;

        //Check sinks.list
        isPropertiesFileOK = checkPropertiesFileSourcesChannelsSinksList(FlumeConfiguratorConstants.SINKS_LIST_PROPERTIES_PREFIX, agentsList) && isPropertiesFileOK;

        //Check sinkGroups.list
        isPropertiesFileOK = isPropertiesFileOK && checkPropertiesFileSinkGroupsList(agentsList);

        //Check groups.list
        isPropertiesFileOK = isPropertiesFileOK && checkPropertiesFileGroupsList(agentsList);

        //Check sourcesWithSelector.list
        isPropertiesFileOK = isPropertiesFileOK && checkPropertiesFileSelectorsList(agentsList);

        //Check interceptors.list
        isPropertiesFileOK = isPropertiesFileOK && checkPropertiesFileInterceptorsList(agentsList);

        //Check sources.commonProperty
        isPropertiesFileOK = isPropertiesFileOK && checkPropertiesFileCommonProperties(FlumeConfiguratorConstants.SOURCES_COMMON_PROPERTY_PROPERTIES_PREFIX, agentsList);

        //Check sources.partialProperty
        isPropertiesFileOK = isPropertiesFileOK && checkPropertiesFilePartialProperties(FlumeConfiguratorConstants.SOURCES_PARTIAL_PROPERTY_PROPERTIES_PREFIX, agentsList);

        //Check selectors.commonProperty
        isPropertiesFileOK = isPropertiesFileOK && checkPropertiesFileCommonProperties(FlumeConfiguratorConstants.SELECTORS_COMMON_PROPERTY_PROPERTIES_PREFIX, agentsList);

        //Check selectors.partialProperty
        isPropertiesFileOK = isPropertiesFileOK && checkPropertiesFilePartialProperties(FlumeConfiguratorConstants.SELECTORS_PARTIAL_PROPERTY_PROPERTIES_PREFIX, agentsList);

        //Check interceptors.commonProperty
        isPropertiesFileOK = isPropertiesFileOK && checkPropertiesFileCommonProperties(FlumeConfiguratorConstants.INTERCEPTORS_COMMON_PROPERTY_PROPERTIES_PREFIX, agentsList);

        //Check interceptors.partialProperty
        isPropertiesFileOK = isPropertiesFileOK && checkPropertiesFilePartialProperties(FlumeConfiguratorConstants.INTERCEPTORS_PARTIAL_PROPERTY_PROPERTIES_PREFIX, agentsList);

        //Check channels.commonProperty
        isPropertiesFileOK = isPropertiesFileOK && checkPropertiesFileCommonProperties(FlumeConfiguratorConstants.CHANNELS_COMMON_PROPERTY_PROPERTIES_PREFIX, agentsList);

        //Check channels.partialProperty
        isPropertiesFileOK = isPropertiesFileOK && checkPropertiesFilePartialProperties(FlumeConfiguratorConstants.CHANNELS_PARTIAL_PROPERTY_PROPERTIES_PREFIX, agentsList);

        //Check sinks.commonProperty
        isPropertiesFileOK = isPropertiesFileOK && checkPropertiesFileCommonProperties(FlumeConfiguratorConstants.SINKS_COMMON_PROPERTY_PROPERTIES_PREFIX, agentsList);

        //Check sinks.partialProperty
        isPropertiesFileOK = isPropertiesFileOK && checkPropertiesFilePartialProperties(FlumeConfiguratorConstants.SINKS_PARTIAL_PROPERTY_PROPERTIES_PREFIX, agentsList);

        //Check sinkgroups.commonProperty
        isPropertiesFileOK = isPropertiesFileOK && checkPropertiesFileCommonProperties(FlumeConfiguratorConstants.SINKGROUPS_COMMON_PROPERTY_PROPERTIES_PREFIX, agentsList);

        //Check sinkgroups.partialProperty
        isPropertiesFileOK = isPropertiesFileOK && checkPropertiesFilePartialProperties(FlumeConfiguratorConstants.SINKGROUPS_PARTIAL_PROPERTY_PROPERTIES_PREFIX, agentsList);

        if (logger.isDebugEnabled()) {
            logger.debug("END validateBaseConfiguration");
        }
    }

}
