package org.keedio.flume.configurator.utils;

import java.text.SimpleDateFormat;
import java.util.*;

import org.keedio.flume.configurator.constants.FlumeConfiguratorConstants;
import org.keedio.flume.configurator.structures.AgentConfigurationGroupProperties;
import org.keedio.flume.configurator.structures.AgentConfigurationProperties;
import org.keedio.flume.configurator.structures.LinkedProperties;
import org.keedio.flume.configurator.structures.PartialProperties;
import org.keedio.flume.configurator.structures.FlumeTopology;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

public class FlumeConfiguratorUtils {


    private FlumeConfiguratorUtils() {
        super();
    }


    /**
     * Get the list of agents that reference a specified element (source, channel, sink)
     * @param mapAgentElements Map with the information of the type of elements (source, channel, sink) referenced for the agents
     * @param elementName String with the name of the element
     * @return List with the list of agents that reference the specified element
     */
    public static List<String> getElementsAgents(Map<String, List<String>> mapAgentElements, String elementName) {

        List<String> agentsList = new ArrayList<>();

        for (String agentName : mapAgentElements.keySet()) {
            if (mapAgentElements.get(agentName).contains(elementName)) {
                agentsList.add(agentName);
            }
        }
        return agentsList;
    }


    /**
     *
     * Get the list of agents that reference a specified element (source, channel, sink)
     * @param flumeConfigurationProperties Properties with the configuration file
     * @param prefixProperty String with the type of properties searched
     * @param elementName String with the name of element
     * @return List with the list of agents that reference the specified element
     */
    public static List<String> getElementsAgents(Properties flumeConfigurationProperties, String prefixProperty, String elementName) {

        List<String> agentsList = new ArrayList<>();

        LinkedProperties elementPropertiesList = FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties, prefixProperty, true);

        for (Object keyObject : elementPropertiesList.keySet()) {

            String keyProperty = (String) keyObject;
            String valuesProperty = elementPropertiesList.getProperty(keyProperty);

            if (valuesProperty.contains(elementName)) {
                //The elements list contains the searched element. Get the name of the agent from the name of the property

                String agentName = keyProperty.substring(prefixProperty.length() + 1);

                agentsList.add(agentName);
            }
        }


        return agentsList;
    }


    /**
     * Get the group which a element belongs
     * @param agentConfigurationGroupsMap Map with the general information about groups
     * @param elementName String with the name of the element
     * @return String with the name of the group which the specified element belongs
     */
    public static String getGroupFromElement(Map<String, Map<String, String>> agentConfigurationGroupsMap, String elementName) {

        String elementGroup = null;

        for (String agentName : agentConfigurationGroupsMap.keySet()) {

            Map<String, String> mapAgentConfigurationGroups = agentConfigurationGroupsMap.get(agentName);

            for (String elementMap : mapAgentConfigurationGroups.keySet()) {

                if (elementMap.equals(elementName)) {
                    elementGroup = mapAgentConfigurationGroups.get(elementMap);
                }

            }

        }

        return elementGroup;
    }



    /**
     * Get the set of groups of a specified agent
     * @param agentConfigurationGroupsMap Map with the general information about groups
     * @param agentName String  eith the name of agent
     * @return Set with the infomation of the groups of the specified agent
     */
    public static Set<String> getAgentGroupsSet(Map<String, Map<String, String>> agentConfigurationGroupsMap, String agentName) {

        Set<String> groupsTreeSet = new LinkedHashSet<>();

        Map<String, String> mapAgentConfigurationGroups = agentConfigurationGroupsMap.get(agentName);

        for (String elementName : mapAgentConfigurationGroups.keySet()) {

            String groupName = mapAgentConfigurationGroups.get(elementName);

            groupsTreeSet.add(groupName);
        }

        return groupsTreeSet;

    }



    /**
     * Build the full name of a configuration property
     * @param agentName String with the name of the agent of the property
     * @param typeElement String with the type of element of the property (sources, channels, sinks)
     * @param elementName String with the name of the element of the property
     * @param isComment boolean true if is a comment property, false otherwise
     * @param propertyName String with the name of the property
     * @return String with the full name of the property
     */
    public static String constructFullPropertyName(String agentName, String typeElement, String elementName, boolean isComment, String propertyName) {

        StringBuilder sb = new StringBuilder();
        sb.append(agentName);
        sb.append(FlumeConfiguratorConstants.DOT_SEPARATOR);
        sb.append(typeElement);
        sb.append(FlumeConfiguratorConstants.DOT_SEPARATOR);
        sb.append(elementName);
        sb.append(FlumeConfiguratorConstants.DOT_SEPARATOR);
        if (isComment) {
            sb.append(FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX);
            sb.append(FlumeConfiguratorConstants.DOT_SEPARATOR);
        }
        sb.append(propertyName);

        return sb.toString();
    }


    /**
     * Build the full name of a configuration interceptor property
     * @param agentName String with the name of the agent of the property
     * @param sourceName String with the name of the source of the interceptor property
     * @param interceptorName String with the name of the interceptor of the property
     * @param isComment boolean true if is a comment property, false otherwise
     * @param interceptorPropertyName  String with the name of the interceptor property
     * @return String with the full name of the interceptor property
     */
    public static String constructFullInterceptorPropertyName(String agentName, String sourceName, String interceptorName, boolean isComment, String interceptorPropertyName) {

        StringBuilder sb = new StringBuilder();
        sb.append(agentName);
        sb.append(FlumeConfiguratorConstants.DOT_SEPARATOR);
        sb.append(FlumeConfiguratorConstants.SOURCES_PROPERTY);
        sb.append(FlumeConfiguratorConstants.DOT_SEPARATOR);
        sb.append(sourceName);
        sb.append(FlumeConfiguratorConstants.DOT_SEPARATOR);
        sb.append(FlumeConfiguratorConstants.INTERCEPTORS_PROPERTY);
        sb.append(FlumeConfiguratorConstants.DOT_SEPARATOR);
        sb.append(interceptorName);
        sb.append(FlumeConfiguratorConstants.DOT_SEPARATOR);
        if (isComment) {
            sb.append(FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX);
            sb.append(FlumeConfiguratorConstants.DOT_SEPARATOR);
        }
        sb.append(interceptorPropertyName);

        return sb.toString();
    }


    /**
     * Build the full name of a configuration selector property
     * @param agentName String with the name of the agent of the property
     * @param sourceName String with the name of the source of the selector property
     * @param isComment boolean true if is a comment property, false otherwise
     * @param selectorPropertyName  String with the name of the selector property
     * @return String with the full name of the selector property
     */
    public static String constructFullSelectorPropertyName(String agentName, String sourceName, boolean isComment, String selectorPropertyName) {

        StringBuilder sb = new StringBuilder();
        sb.append(agentName);
        sb.append(FlumeConfiguratorConstants.DOT_SEPARATOR);
        sb.append(FlumeConfiguratorConstants.SOURCES_PROPERTY);
        sb.append(FlumeConfiguratorConstants.DOT_SEPARATOR);
        sb.append(sourceName);
        sb.append(FlumeConfiguratorConstants.DOT_SEPARATOR);
        sb.append(FlumeConfiguratorConstants.SELECTOR_PROPERTY);
        sb.append(FlumeConfiguratorConstants.DOT_SEPARATOR);
        if (isComment) {
            sb.append(FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX);
            sb.append(FlumeConfiguratorConstants.DOT_SEPARATOR);
        }
        sb.append(selectorPropertyName);

        return sb.toString();
    }


    /**
     * Build the string with the decorated version of a text
     * @param headerText StringBuilder with the text to decorate
     * @param asteriskLinesNumber int with the number of lines of asterisks to generate
     * @param newLinesAfterHeaderNumber int with the number of new lines to generate after decorated text
     * @param decoratorCharacter String with the decorator character
     * @return String with the decorated text
     */
    public static String getHeaderAgentConfiguration(StringBuilder headerText, int asteriskLinesNumber, int newLinesAfterHeaderNumber, String decoratorCharacter) {

        StringBuilder sbHeader = new StringBuilder();

        int asteriskNumberPrePost = 5;
        int spacesPrePost = 3;

        int headerTextLength = headerText.length();

        int lineSize = (asteriskNumberPrePost * 2) + (spacesPrePost * 2) + headerTextLength;

        //Asterisk Lines Pre
        for (int i=0; i<asteriskLinesNumber; i++) {

            for (int j=0; j<lineSize; j++) {
                sbHeader.append(decoratorCharacter);
            }
            sbHeader.append(FlumeConfiguratorConstants.NEW_LINE);
        }

        for (int i=0; i<asteriskNumberPrePost; i++) {
            sbHeader.append(decoratorCharacter);
        }
        for (int i=0; i<spacesPrePost; i++) {
            sbHeader.append(FlumeConfiguratorConstants.WHITE_SPACE);
        }
        sbHeader.append(headerText);
        for (int i=0; i<spacesPrePost; i++) {
            sbHeader.append(FlumeConfiguratorConstants.WHITE_SPACE);
        }
        for (int i=0; i<asteriskNumberPrePost; i++) {
            sbHeader.append(decoratorCharacter);
        }
        sbHeader.append(FlumeConfiguratorConstants.NEW_LINE);

        //Asterisk Lines Post
        for (int i=0; i<asteriskLinesNumber; i++) {

            for (int j=0; j<lineSize; j++) {
                sbHeader.append(decoratorCharacter);
            }
            sbHeader.append(FlumeConfiguratorConstants.NEW_LINE);
        }

        //New Lines
        for (int i=0; i<newLinesAfterHeaderNumber; i++) {
            sbHeader.append(FlumeConfiguratorConstants.NEW_LINE);
        }

        return sbHeader.toString();

    }

    /**
     * Generate the string with the final Flume configuration for a specified agent
     * @param agentConfigurationProperties AgentConfigurationProperties with the information of the configuration of the agent
     * @param agentName String with the name of the agent
     * @param addComments boolean true if the comments will be added to the generated configuration, false otherwise
     * @param addBuiltDate boolean true if the generation date will be added to the generated configuration, false otherwise
     * @return String with the text of final Flume configuration for the agent
     */
    @SuppressWarnings("unchecked")
    public static String getStringAgentConfiguration(AgentConfigurationProperties agentConfigurationProperties, String agentName, boolean addComments, boolean addBuiltDate) {

        StringBuilder sb = new StringBuilder();
        StringBuilder textHeader;
        sb.append(FlumeConfiguratorConstants.NEW_LINE);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss zzz");

        if (addBuiltDate) {
            textHeader = new StringBuilder().append("Built by Flume Configurator on:  ").append(sdf.format(Calendar.getInstance().getTime()));
            sb.append(getHeaderAgentConfiguration(textHeader,2,2,FlumeConfiguratorConstants.HASH));
        }

        textHeader = new StringBuilder().append("CONFIGURATION AGENT:  ").append(agentName);
        sb.append(getHeaderAgentConfiguration(textHeader,2,2,FlumeConfiguratorConstants.HASH));

        //General Properties
        for (Object generalProperty : agentConfigurationProperties.getListGeneralProperties()) {
            sb.append(generalProperty).append(FlumeConfiguratorConstants.NEW_LINE);
        }

        sb.append(FlumeConfiguratorConstants.NEW_LINE);
        sb.append(FlumeConfiguratorConstants.NEW_LINE);

        //Groups
        Map<String,AgentConfigurationGroupProperties> agentConfigurationGroupPropertiesMap = agentConfigurationProperties.getMapGroupProperties();


        for (String groupName : agentConfigurationGroupPropertiesMap.keySet()) {

            textHeader = new StringBuilder().append("GROUP:  ").append(groupName);
            sb.append(getHeaderAgentConfiguration(textHeader,1,1,FlumeConfiguratorConstants.HASH));

            AgentConfigurationGroupProperties agentConfigurationGroupProperties = agentConfigurationGroupPropertiesMap.get(groupName);

            boolean withProperties = false;

            //Sources properties
            List listSourceProperties = agentConfigurationGroupProperties.getListSourceProperties();

            for (Object sourceProperty : listSourceProperties) {

                Map.Entry sourcePropertyEntry = (Map.Entry) sourceProperty;

                if (!sourcePropertyEntry.getKey().toString().contains(FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX)) {
                    sb.append(sourcePropertyEntry).append(FlumeConfiguratorConstants.NEW_LINE);
                    withProperties = true;
                } else {
                    if (addComments) {
                        String sourcePropertyEntryString = (String) sourcePropertyEntry.getValue();
                        if (!"".equals(sourcePropertyEntryString.trim())) {
                            sb.append(FlumeConfiguratorConstants.COMMENT_PREFIX).append(sourcePropertyEntry.getValue()).append(FlumeConfiguratorConstants.NEW_LINE);
                            withProperties = true;
                        }
                    }
                }
            }

            if (withProperties) {
                sb.append(FlumeConfiguratorConstants.NEW_LINE);
                withProperties = false;
            }


            //Source interceptors properties
            Map<String, List<String>> mapSourceInterceptorProperties = agentConfigurationGroupProperties.getMapSourceInterceptorProperties();

            for (String interceptorName : mapSourceInterceptorProperties.keySet()) {
                List listSourceInterceptorProperties = mapSourceInterceptorProperties.get(interceptorName);

                for (Object sourceInterceptorProperty : listSourceInterceptorProperties) {

                    Map.Entry sourceInterceptorPropertyEntry = (Map.Entry) sourceInterceptorProperty;

                    if (!sourceInterceptorPropertyEntry.getKey().toString().contains(FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX)) {
                        sb.append(sourceInterceptorPropertyEntry).append(FlumeConfiguratorConstants.NEW_LINE);
                        withProperties = true;
                    } else {
                        if (addComments) {
                            String sourceInterceptorPropertyEntryString = (String) sourceInterceptorPropertyEntry.getValue();
                            if (!"".equals(sourceInterceptorPropertyEntryString.trim())) {
                                sb.append(FlumeConfiguratorConstants.COMMENT_PREFIX).append(sourceInterceptorPropertyEntry.getValue()).append(FlumeConfiguratorConstants.NEW_LINE);
                                withProperties = true;
                            }
                        }
                    }
                }
            }

            if (withProperties) {
                sb.append(FlumeConfiguratorConstants.NEW_LINE);
                withProperties = false;
            }

            //Channels properties
            List listChannelProperties = agentConfigurationGroupProperties.getListChannelProperties();

            for (Object channelProperty : listChannelProperties) {

                Map.Entry channelPropertyEntry = (Map.Entry) channelProperty;

                if (!channelPropertyEntry.getKey().toString().contains(FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX)) {
                    sb.append(channelPropertyEntry).append(FlumeConfiguratorConstants.NEW_LINE);
                    withProperties = true;
                } else {
                    if (addComments) {
                        String channelPropertyEntryString = (String) channelPropertyEntry.getValue();
                        if (!"".equals(channelPropertyEntryString.trim())) {
                            sb.append(FlumeConfiguratorConstants.COMMENT_PREFIX).append(channelPropertyEntry.getValue()).append(FlumeConfiguratorConstants.NEW_LINE);
                            withProperties = true;
                        }
                    }
                }
            }

            if (withProperties) {
                sb.append(FlumeConfiguratorConstants.NEW_LINE);
                withProperties = false;
            }

            //Sinks properties
            List listSinkProperties = agentConfigurationGroupProperties.getListSinkProperties();

            for (Object sinkProperty : listSinkProperties) {

                Map.Entry sinkPropertyEntry = (Map.Entry) sinkProperty;

                if (!sinkPropertyEntry.getKey().toString().contains(FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX)) {
                    sb.append(sinkPropertyEntry).append(FlumeConfiguratorConstants.NEW_LINE);
                    withProperties = true;
                } else {
                    if (addComments) {
                        String sinkPropertyEntryString = (String) sinkPropertyEntry.getValue();
                        if (!"".equals(sinkPropertyEntryString.trim())) {
                            sb.append(FlumeConfiguratorConstants.COMMENT_PREFIX).append(sinkPropertyEntry.getValue()).append(FlumeConfiguratorConstants.NEW_LINE);
                            withProperties = true;
                        }
                    }
                }
            }

            if (withProperties) {
                sb.append(FlumeConfiguratorConstants.NEW_LINE);
                withProperties = false;
            }

            //Sink groups properties
            List listSinkGroupsProperties = agentConfigurationGroupProperties.getListSinkGroupProperties();

            for (Object sinkGroupProperty : listSinkGroupsProperties) {

                Map.Entry sinkGroupPropertyEntry = (Map.Entry) sinkGroupProperty;

                if (!sinkGroupPropertyEntry.getKey().toString().contains(FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX)) {
                    sb.append(sinkGroupPropertyEntry).append(FlumeConfiguratorConstants.NEW_LINE);
                    withProperties = true;
                } else {
                    if (addComments) {
                        String sinkGroupPropertyEntryString = (String) sinkGroupPropertyEntry.getValue();
                        if (!"".equals(sinkGroupPropertyEntryString.trim())) {
                            sb.append(FlumeConfiguratorConstants.COMMENT_PREFIX).append(sinkGroupPropertyEntry.getValue()).append(FlumeConfiguratorConstants.NEW_LINE);
                            withProperties = true;
                        }
                    }
                }
            }


            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append(FlumeConfiguratorConstants.NEW_LINE);

        }

        sb.append(FlumeConfiguratorConstants.NEW_LINE);
        sb.append(FlumeConfiguratorConstants.NEW_LINE);


        return sb.toString();

    }



    /**
     * Extracts a specific property key subset from the known properties.
     * The prefix may be removed from the keys in the resulting dictionary,
     * or it may be kept. In the latter case, exact matches on the prefix
     * will also be copied into the resulting dictionary
     * @param properties Properties with the initial set of properties
     * @param prefix String is the key prefix to filter the properties by.
     * @param keepPrefix boolean if true, the key prefix is kept in the resulting
     * dictionary. As side-effect, a key that matches the prefix exactly
     * will also be copied. If false, the resulting dictionary's keys are
     * shortened by the prefix. An exact prefix match will not be copied,
     * as it would result in an empty string key.
     * @return LinkedProperties with a property dictionary matching the filter key. May be
     * an empty dictionary, if no prefix matches were found.
     */
    public static LinkedProperties matchingSubset(Properties properties, String prefix, boolean keepPrefix) {

        LinkedProperties result = new LinkedProperties();

        //sanity check
        if (prefix == null || prefix.length() == 0) {
            return result;
        }

        String prefixMatch; // match prefix strings with this
        String prefixSelf; // match self with this
        if (prefix.charAt(prefix.length() - 1) != '.') {
            //prefix does not end in a dot
            prefixSelf = prefix;
            prefixMatch = prefix + '.';
        } else {
            // prefix does end in one dot, remove for exact matches
            prefixSelf = prefix.substring(0, prefix.length() - 1);
            prefixMatch = prefix;
        }

        // POSTCONDITION: prefixMatch and prefixSelf are initialized!

        // now add all matches into the resulting properties.
        // Remark 1: #propertyNames() will contain the System properties!
        // Remark 2: We need to give priority to System properties. This is done
        // automatically by calling this class's getProperty method.
        String key;

        for (Object keyObject : properties.keySet()) {

            key = (String) keyObject;

            if (keepPrefix) {
                // keep full prefix in result, also copy direct matches
                if (key.startsWith(prefixMatch) || key.equals(prefixSelf)) {
                    result.setProperty(key, properties.getProperty(key));
                }
            } else {
                // remove full prefix in result, dont copy direct matches
                if (key.startsWith(prefixMatch)) {
                    result.setProperty(key.substring(prefixMatch.length()), properties.getProperty(key));
                }
            }
        }

        // done
        return result;
    }


    /**
     * Get the list of agents from a properties configuration
     * @param flumeConfigurationProperties Properties with the information of the configuration
     * @param separatorCharacter String with the separator character of the elements of the configuration
     * @return List with the list of agents specified in the configuration information
     */
    public static List<String> getAgentsListFromProperties(Properties flumeConfigurationProperties, String separatorCharacter) {

        String agentsListProperty = flumeConfigurationProperties.getProperty(FlumeConfiguratorConstants.AGENTS_LIST_PROPERTIES_PREFIX);
        String[] agentsListPropertyArray = FlumeConfiguratorUtils.splitWithoutSpacesOptional(agentsListProperty,true,separatorCharacter);
        return Arrays.asList(agentsListPropertyArray);

    }



    /**
     * Generate the necessary structure to store the information of the elements of configuration (sources, channels, sinks, interceptors) for every agent
     * If the specified elements are interceptor, generate the structure of interceptors for the sources
     * @param flumeConfigurationProperties Properties with the initial configuration
     * @param prefixProperty String with the prefix that indicate the type of elements (sources, channels, sinks, interceptors)
     * @param separatorCharacter String with the separator character between the elements
     * @return Map with the list of specified elements for every egent (or interceptors for every sources)
     */
    public static Map<String, List<String>> getAgentElementsMapFromProperties(Properties flumeConfigurationProperties, String prefixProperty, String separatorCharacter) {

        Map<String, List<String>> mapAgentElements = new HashMap<>();

        LinkedProperties flumeConfigurationPropertiesAgentElements = matchingSubset(flumeConfigurationProperties, prefixProperty, true);


        for (Object keyObject : flumeConfigurationPropertiesAgentElements.keySet()) {

            String keyProperty = (String) keyObject;
            String valueProperty = flumeConfigurationPropertiesAgentElements.getProperty(keyProperty);

            //Get the name of the agent (or the name of the source in case of interceptors) from the key of the property
            String[] keyPropertyArray = keyProperty.split(FlumeConfiguratorConstants.DOT_REGEX);

            String agentName = keyPropertyArray[2];

            //Get the list of sources of the agent from value of the property
            String[] valuePropertyArray = FlumeConfiguratorUtils.splitWithoutSpacesOptional(valueProperty,true,separatorCharacter);
            mapAgentElements.put(agentName,Arrays.asList(valuePropertyArray));

        }

        return mapAgentElements;

    }



    /**
     * Generate a measured property (number of elements) based on the dimension of another property
     * If the property already has the same dimension of the referenced property any transformation will be done.
     * If the property has a smaller dimension that the referenced property, the first element will be replicated until get the desired dimension
     * @param propertyWithDimension String with property whose dimension will be taken as reference
     * @param propertyWithoutDimension String with the property to resize
     * @param separatorCharacter String with the separator character between the elements
     * @return String with the property resized to the same dimension that the property taken as reference
     */
    private static String getMeasuredPropertyString(String propertyWithDimension, String propertyWithoutDimension, String separatorCharacter) {

        String dimensionPropertyString;
        StringBuilder sb = new StringBuilder();

        String[] propertyWithDimensionArray = FlumeConfiguratorUtils.splitWithoutSpacesOptional(propertyWithDimension,false,separatorCharacter);
        int numberDimensionPropertyWithDimension = propertyWithDimensionArray.length;
        String[] propertyWithoutDimensionArray = FlumeConfiguratorUtils.splitWithoutSpacesOptional(propertyWithoutDimension,false,separatorCharacter);
        int numberDimensionPropertyWithoutDimension = propertyWithoutDimensionArray.length;

        if (numberDimensionPropertyWithDimension != numberDimensionPropertyWithoutDimension) {
            if ("".equals(propertyWithoutDimension)) {

                if (numberDimensionPropertyWithDimension == 1) {
                    dimensionPropertyString = "";
                } else {
                    //Construct a string with white spaces between separator characteres in order to a correct funcion of the split
                    for (int i=0; i<numberDimensionPropertyWithDimension; i++) {
                        sb.append(FlumeConfiguratorConstants.WHITE_SPACE);
                        sb.append(separatorCharacter);
                    }
                    //Remove the last aparition of separator character
                    sb.setLength(sb.length() - 1);
                    dimensionPropertyString = sb.toString();
                }


            } else {
                //Replicate the value (the first section) so many times as the required dimension
                String valueClone = propertyWithoutDimensionArray[0];

                for (int i=0; i<numberDimensionPropertyWithDimension; i++) {
                    sb.append(valueClone);
                    sb.append(separatorCharacter);
                }
                //Remove the last aparition of separator character
                sb.setLength(sb.length() - 1);

                dimensionPropertyString = sb.toString();
            }

        } else {
            dimensionPropertyString = propertyWithoutDimension;
        }

        return dimensionPropertyString;
    }




    /**
     * Get a list of common properties for every elements of the specified type from a properties configuration
     * @param flumeConfigurationProperties Properties with the initial configuration
     * @param prefixProperty String with the prefix that indicate the type of common properties elements (sources, channels, sinks, interceptors)
     * @return LinkedProperties with the information of the common properties created
     */
    public static LinkedProperties getElementsCommonPropertiesFromProperties(Properties flumeConfigurationProperties, String prefixProperty) {

        LinkedProperties elementCommonProperties = new LinkedProperties();

        LinkedProperties flumeConfigurationPropertiesAgentElements = matchingSubset(flumeConfigurationProperties, prefixProperty, true);


        for (Object keyObject : flumeConfigurationPropertiesAgentElements.keySet()) {

            String keyProperty = (String) keyObject;
            String valueProperty = flumeConfigurationPropertiesAgentElements.getProperty(keyProperty);

            //Get the name of property from the key of the property
            String propertyName = keyProperty.substring(prefixProperty.length() + 1);

            //Put into the map the value for the property (normal property or comment property)
            elementCommonProperties.put(propertyName, valueProperty);

        }

        return elementCommonProperties;
    }



    /**
     * Get a list of partial properties for every elements of the specified type from a properties configuration
     * @param flumeConfigurationProperties Properties with the initial configuration
     * @param prefixProperty  String with the prefix that indicate the type of partial properties elements (sources, channels, sinks, interceptors)
     * @param separatorCharacter String with the separator character between the elements
     * @return LinkedProperties with the information of the partial properties created
     */
    public static PartialProperties getElementsPartialPropertiesFromProperties(Properties flumeConfigurationProperties, String prefixProperty, String separatorCharacter) {

        PartialProperties elementPartialProperties = new PartialProperties();

        LinkedProperties appliedElements = elementPartialProperties.getAppliedElements();
        LinkedProperties elementsPropertiesValues = elementPartialProperties.getPropertiesValues();
        LinkedProperties elementsPropertiesComments = elementPartialProperties.getPropertiesComments();

        LinkedProperties flumeConfigurationPropertiesAgentElements = matchingSubset(flumeConfigurationProperties, prefixProperty, true);

        for (Object keyObject : flumeConfigurationPropertiesAgentElements.keySet()) {

            String keyProperty = (String) keyObject;
            String valueProperty = flumeConfigurationPropertiesAgentElements.getProperty(keyProperty);

            //Get the property without the prefix
            String propertyWithoutPrefix = keyProperty.substring(prefixProperty.length() + 1);

            if (propertyWithoutPrefix.startsWith(FlumeConfiguratorConstants.PARTIAL_PROPERTY_APPLIED_ELEMENTS_PROPERTIES_PREFIX)) {
                //Is a partial (applied elements) property

                //Get the name of the property
                String propertyName = propertyWithoutPrefix.substring(FlumeConfiguratorConstants.PARTIAL_PROPERTY_APPLIED_ELEMENTS_PROPERTIES_PREFIX.length() + 1);

                //Put the property into the map of applied elements
                appliedElements.put(propertyName, valueProperty);


            } else if (propertyWithoutPrefix.startsWith(FlumeConfiguratorConstants.PARTIAL_PROPERTY_PROPERTY_VALUES_PROPERTIES_PREFIX)) {
                //Is a partial (properties values) property.  Be sure that the dimension of property is the same of the referenced property (applied elements property)

                //Get the name of property
                String propertyName = propertyWithoutPrefix.substring(FlumeConfiguratorConstants.PARTIAL_PROPERTY_PROPERTY_VALUES_PROPERTIES_PREFIX.length() + 1);

                String propertyReference = propertyWithoutPrefix.substring(FlumeConfiguratorConstants.PARTIAL_PROPERTY_PROPERTY_VALUES_PROPERTIES_PREFIX.length());
                String valuePropertyReference = flumeConfigurationPropertiesAgentElements.getProperty(prefixProperty + FlumeConfiguratorConstants.DOT_SEPARATOR + FlumeConfiguratorConstants.PARTIAL_PROPERTY_APPLIED_ELEMENTS_PROPERTIES_PREFIX + propertyReference);

                //Resize the dimension of property to the dimension of the referenced property (applied elements property)
                String valuePropertyMeasured = getMeasuredPropertyString(valuePropertyReference, valueProperty, separatorCharacter);

                //Put the property into the map of values of properties
                elementsPropertiesValues.put(propertyName, valuePropertyMeasured);

            } else if (propertyWithoutPrefix.startsWith(FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX)) {
                //Is a partial (comment) property. Be sure that the dimension of property is the same of the referenced property (applied elements property)

                //Get the name of the property
                String propertyName = propertyWithoutPrefix.substring(FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX.length() + 1);

                String propertyReference = propertyWithoutPrefix.substring(FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX.length());
                String valuePropertyReference = flumeConfigurationPropertiesAgentElements.getProperty(prefixProperty + FlumeConfiguratorConstants.DOT_SEPARATOR + FlumeConfiguratorConstants.PARTIAL_PROPERTY_APPLIED_ELEMENTS_PROPERTIES_PREFIX + propertyReference);

                //Resize the dimension of property to the dimension of the referenced property (applied elements property)
                String valuePropertyMeasured = getMeasuredPropertyString(valuePropertyReference, valueProperty, separatorCharacter);

                //Put the property into the map of comments of properties
                elementsPropertiesComments.put(propertyName, valuePropertyMeasured);
            }

        }


        return elementPartialProperties;
    }


    /**
     * Get the information of the groups of every agent from a properties configuration
     * @param flumeConfigurationProperties Properties with the initial configuration
     * @param separatorCharacter String with the separator character between the elements
     * @return Map with the information of the groups of every agent
     */
    public static Map<String, Map<String, String>> getAgentConfigurationGroupsMapFromProperties(Properties flumeConfigurationProperties, String separatorCharacter) {

        Map<String,  Map<String, String>> mapAgentConfigurationGroups = new LinkedHashMap<>();

        LinkedProperties flumeConfigurationPropertiesAgentGroups = matchingSubset(flumeConfigurationProperties, FlumeConfiguratorConstants.GROUPS_LIST_PROPERTIES_PREFIX, true);

        for (Object keyObject : flumeConfigurationPropertiesAgentGroups.keySet()) {

            String keyProperty = (String) keyObject;
            String valueProperty = flumeConfigurationPropertiesAgentGroups.getProperty(keyProperty);


            String[] keyPropertyArray = keyProperty.split(FlumeConfiguratorConstants.DOT_REGEX);

            //Get the name of the agent from the key of property
            String agentName = keyPropertyArray[2];

            //Get the name of the group from the key of the property
            String groupName = keyPropertyArray[3];

            //Get the map of the agent (if the map doesn't exist it will be initialized)
            Map<String, String> mapAgent = mapAgentConfigurationGroups.get(agentName);

            if (mapAgent == null) {
                mapAgent = new LinkedHashMap<>();
            }

            //Split the values of the property. Each value will be the key in the map and the name of the group will be the value
            String[] valuePropertyArray = FlumeConfiguratorUtils.splitWithoutSpacesOptional(valueProperty,true,separatorCharacter);

            for (String value : valuePropertyArray) {
                mapAgent.put(value, groupName);
            }

            mapAgentConfigurationGroups.put(agentName, mapAgent);

        }

        return mapAgentConfigurationGroups;
    }


    /**
     * Indicate if a property is the channels property of sources
     * @param propertyName String with the name of the property
     * @return true if the property is the channels property of sources, false otherwise
     */
    public static boolean isSourceChannelsProperty(String propertyName) {

        return (propertyName.startsWith(FlumeConfiguratorConstants.SOURCES_COMMON_PROPERTY_PROPERTIES_PREFIX) ||
                propertyName.startsWith(FlumeConfiguratorConstants.SOURCES_PARTIAL_PROPERTY_PROPERTIES_PREFIX)) &&
                propertyName.endsWith(FlumeConfiguratorConstants.CHANNELS_PROPERTY);

    }


    /**
     * Indicate if a property is the channel property of sinks
     * @param propertyName String with the name of the property
     * @return true if the property is the channel property of sinks, false otherwise
     */
    public static boolean isSinkChannelProperty(String propertyName) {

        return (propertyName.startsWith(FlumeConfiguratorConstants.SINKS_COMMON_PROPERTY_PROPERTIES_PREFIX) ||
                propertyName.startsWith(FlumeConfiguratorConstants.SINKS_PARTIAL_PROPERTY_PROPERTIES_PREFIX)) &&
                propertyName.endsWith(FlumeConfiguratorConstants.CHANNEL_PROPERTY);
    }


    /**
     * Indicate if a property is the sinks property of sinkgroups
     * @param propertyName String with the name of the property
     * @return true if the property is the sinks property of sinkgroups, false otherwise
     */
    public static boolean isSinkGroupsSinksProperty(String propertyName) {

        return (propertyName.startsWith(FlumeConfiguratorConstants.SINKGROUPS_COMMON_PROPERTY_PROPERTIES_PREFIX) ||
                propertyName.startsWith(FlumeConfiguratorConstants.SINKGROUPS_PARTIAL_PROPERTY_PROPERTIES_PREFIX)) &&
                propertyName.endsWith(FlumeConfiguratorConstants.SINKS_PROPERTY);

    }

    /**
     * Indicate if a property is a property of selector that references channels
     * @param propertyName String with the name of the property
     * @return true if the property is t a property of selector that references channels, false otherwise
     */
    public static boolean isSelectorChannelReferenceProperty(String propertyName) {

        return (propertyName.startsWith(FlumeConfiguratorConstants.SELECTORS_COMMON_PROPERTY_PROPERTIES_PREFIX) ||
                propertyName.startsWith(FlumeConfiguratorConstants.SELECTORS_PARTIAL_PROPERTY_PROPERTIES_PREFIX)) &&
                (propertyName.contains(FlumeConfiguratorConstants.SELECTOR_PROPERTY) &&
                (propertyName.contains(FlumeConfiguratorConstants.MAPPING_PROPERTY) ||
                propertyName.contains(FlumeConfiguratorConstants.OPTIONAL_PROPERTY) ||
                propertyName.contains(FlumeConfiguratorConstants.DEFAULT_PROPERTY)));

    }

    /**
     * Split the value of a property
     * @param value String with the value of the property to be splitted
     * @param removeSpaces boolean true if the white spaces will be removed before the split, false otherwise
     * @param separatorCharacter String with the separator character between the elements used by split
     * @return String[] with the array of splitted values.
     */
    public static String[] splitWithoutSpacesOptional(String value, boolean removeSpaces, String separatorCharacter) {

        if (value != null) {
            String splittedValue = value;
            if (removeSpaces) {
                splittedValue = value.replaceAll(FlumeConfiguratorConstants.WHITE_SPACE_REGEX, "");
            }
            return splittedValue.split(separatorCharacter);
        } return new String[]{};
    }


    /**
     * Split the value of a property keeping the white spaces in the middle of the values of elements
     * @param value String with the value of the property to be splitted
     * @param removeSpaces boolean true if the white spaces (at beginning and at the end) will be removed before the split, false otherwise
     * @param separatorCharacter tring with the separator character between the elements used by split
     * @return String[] with the array of splitted values.
     */
    public static String[] splitWithoutSpacesOptionalKeepInternalSpaces(String value, boolean removeSpaces, String separatorCharacter) {

        if (value != null) {
            String[] splittedValues = value.split(separatorCharacter);
            if (removeSpaces) {
                for (int index=0; index<splittedValues.length; index++) {
                    splittedValues[index] = splittedValues[index].trim();
                }
            }
            return splittedValues;
        } return new String[]{};
    }


    /**
     *
     * Get the list of channels of a source
     * @param flumeConfigurationProperties Properties with the configuration file
     * @param sourceName String with the name of source
     * @param separatorCharacter String with the separator character between the elements
     * @return List with the list of agents that reference the specified element
     */
    public static List<String> getSourceChannels(Properties flumeConfigurationProperties, String sourceName, String separatorCharacter) {

        List<String> sourceChannelsList = new ArrayList<>();

        //Get channels from the common property
        String valuesProperty = flumeConfigurationProperties.getProperty(FlumeConfiguratorConstants.SOURCES_COMMON_PROPERTY_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + FlumeConfiguratorConstants.CHANNELS_PROPERTY);
        if (valuesProperty != null && !valuesProperty.isEmpty()) {
            List<String> listChannels = Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(valuesProperty, false, FlumeConfiguratorConstants.WHITE_SPACE_REGEX));
            for (String channel : listChannels) {
                if (!sourceChannelsList.contains(channel)) {
                    sourceChannelsList.add(channel);
                }
            }
        }

        //Get channels from the partial property
        String appliedElementsList = flumeConfigurationProperties.getProperty(FlumeConfiguratorConstants.SOURCES_PARTIAL_PROPERTY_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + FlumeConfiguratorConstants.PARTIAL_PROPERTY_APPLIED_ELEMENTS_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + FlumeConfiguratorConstants.CHANNELS_PROPERTY);
        String propertyValuesList = flumeConfigurationProperties.getProperty(FlumeConfiguratorConstants.SOURCES_PARTIAL_PROPERTY_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + FlumeConfiguratorConstants.PARTIAL_PROPERTY_PROPERTY_VALUES_PROPERTIES_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + FlumeConfiguratorConstants.CHANNELS_PROPERTY);

        if (appliedElementsList != null && propertyValuesList != null) {

            String[] appliedElementsArray = FlumeConfiguratorUtils.splitWithoutSpacesOptionalKeepInternalSpaces(appliedElementsList,true,separatorCharacter);
            String[] propertyValuesArray = FlumeConfiguratorUtils.splitWithoutSpacesOptionalKeepInternalSpaces(propertyValuesList,true,separatorCharacter);

            for (int i = 0; i < appliedElementsArray.length; i++) {
                String source = appliedElementsArray[i];
                if (source.equals(sourceName)) {
                    //The common property is overwrite
                    sourceChannelsList.clear();
                    if (propertyValuesArray.length == appliedElementsArray.length) {
                        valuesProperty = propertyValuesArray[i];

                        List<String> listChannels = Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(valuesProperty, false, FlumeConfiguratorConstants.WHITE_SPACE_REGEX));
                        for (String channel : listChannels) {
                            if (!sourceChannelsList.contains(channel)) {
                                sourceChannelsList.add(channel);
                            }
                        }
                    } else if (propertyValuesArray.length == 1) {
                        valuesProperty = propertyValuesArray[0];
                        List<String> listChannels = Arrays.asList(FlumeConfiguratorUtils.splitWithoutSpacesOptional(valuesProperty, false, FlumeConfiguratorConstants.WHITE_SPACE_REGEX));
                        for (String channel : listChannels) {
                            if (!sourceChannelsList.contains(channel)) {
                                sourceChannelsList.add(channel);
                            }
                        }
                    } else {
                        sourceChannelsList.clear();
                    }
                }
            }
        }

        return sourceChannelsList;
    }




}

