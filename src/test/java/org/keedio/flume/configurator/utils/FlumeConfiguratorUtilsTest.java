package org.keedio.flume.configurator.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.keedio.flume.configurator.constants.FlumeConfiguratorConstants;
import org.keedio.flume.configurator.structures.LinkedProperties;
import org.keedio.flume.configurator.structures.PartialProperties;
import org.slf4j.LoggerFactory;


public class FlumeConfiguratorUtilsTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FlumeConfiguratorUtilsTest.class);

    private static final String DEFAULT_SEPARATOR = ";";

    private static Properties flumeConfigurationProperties = new LinkedProperties();

    @BeforeClass
    public static void loadPropertiesFile() throws IOException {
        FileInputStream fis = new FileInputStream("src/test/resources/FlumeConfigurationExample.properties");
        flumeConfigurationProperties.load(fis);
    }


    
    @Test
    public void testGetElementsAgentsFromMap() {

        String sourceName = "source1_2";
        String sourceAgentExpected = "agent1";
        String channelName = "channel3_2";
        String channelAgentExpected = "agent3";
        String sinkName = "sink2_3";
        String sinkAgentExpected = "agent2";
        String interceptorName = "interceptor1_1b";
        String interceptorSourceExpected = "source1_1";
        
        try {

            //Check source agents
            Map<String, List<String>> mapAgentsSourcesList = FlumeConfiguratorUtils.getAgentElementsMapFromProperties(flumeConfigurationProperties, FlumeConfiguratorConstants.SOURCES_LIST_PROPERTIES_PREFIX, DEFAULT_SEPARATOR);
            List<String> agentList = FlumeConfiguratorUtils.getElementsAgents(mapAgentsSourcesList, sourceName);
            Assert.assertEquals("The agent of the source is not correct", agentList.size() , 1);
            Assert.assertEquals("The agent of the source is not correct", agentList.get(0) , sourceAgentExpected);

            //Check channel agents
            Map<String, List<String>> mapAgentsChannelsList = FlumeConfiguratorUtils.getAgentElementsMapFromProperties(flumeConfigurationProperties, FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX, DEFAULT_SEPARATOR);
            agentList = FlumeConfiguratorUtils.getElementsAgents(mapAgentsChannelsList, channelName);
            Assert.assertEquals("The agent of the channel is not correct", agentList.size() , 1);
            Assert.assertEquals("The agent of the channel is not correct", agentList.get(0) , channelAgentExpected);

            //Check sink agents
            Map<String, List<String>> mapAgentsSinksList = FlumeConfiguratorUtils.getAgentElementsMapFromProperties(flumeConfigurationProperties, FlumeConfiguratorConstants.SINKS_LIST_PROPERTIES_PREFIX, DEFAULT_SEPARATOR);
            agentList = FlumeConfiguratorUtils.getElementsAgents(mapAgentsSinksList, sinkName);
            Assert.assertEquals("The agent of the sink is not correct", agentList.size() , 1);
            Assert.assertEquals("The agent of the sink is not correct", agentList.get(0) , sinkAgentExpected);

            //Check interceptor sources
            Map<String, List<String>> mapSourcesInterceptorsList = FlumeConfiguratorUtils.getAgentElementsMapFromProperties(flumeConfigurationProperties, FlumeConfiguratorConstants.INTERCEPTORS_LIST_PROPERTIES_PREFIX, DEFAULT_SEPARATOR);
            List<String> sourceList = FlumeConfiguratorUtils.getElementsAgents(mapSourcesInterceptorsList, interceptorName);
            Assert.assertEquals("The source of the interceptor is not correct", sourceList.size() , 1);
            Assert.assertEquals("The source of the interceptor is not correct", sourceList.get(0) , interceptorSourceExpected);

            
            
        } catch (Exception e) {
            Assert.fail("An error has occurred [testGetElementsAgentsFromMap] method");
            logger.error("An error has occurred [testGetElementsAgentsFromMap] method", e);
        }
    }
    
    
    @Test
    public void testGetElementsAgentsFromProperties() {

        String sourceName = "source1_2";
        String sourceAgentExpected = "agent1";
        String channelName = "channel3_2";
        String channelAgentExpected = "agent3";
        String sinkName = "sink2_3";
        String sinkAgentExpected = "agent2";
        String interceptorName = "interceptor1_1b";
        String interceptorSourceExpected = "source1_1";
        
        try {

            //Check source agents
            List<String> agentList = FlumeConfiguratorUtils.getElementsAgents(flumeConfigurationProperties, FlumeConfiguratorConstants.SOURCES_LIST_PROPERTIES_PREFIX, sourceName);
            Assert.assertEquals("The agent of the source is not correct", agentList.size() , 1);
            Assert.assertEquals("The agent of the source is not correct", agentList.get(0) , sourceAgentExpected);

            //Check channel agents
            agentList = FlumeConfiguratorUtils.getElementsAgents(flumeConfigurationProperties, FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX, channelName);
            Assert.assertEquals("The agent of the channel is not correct", agentList.size() , 1);
            Assert.assertEquals("The agent of the channel is not correct", agentList.get(0) , channelAgentExpected);

            //Check sink agents
            agentList = FlumeConfiguratorUtils.getElementsAgents(flumeConfigurationProperties, FlumeConfiguratorConstants.SINKS_LIST_PROPERTIES_PREFIX, sinkName);
            Assert.assertEquals("The agent of the sink is not correct", agentList.size() , 1);
            Assert.assertEquals("The agent of the sink is not correct", agentList.get(0) , sinkAgentExpected);

            //Check interceptor sources
            List<String> sourceList = FlumeConfiguratorUtils.getElementsAgents(flumeConfigurationProperties, FlumeConfiguratorConstants.INTERCEPTORS_LIST_PROPERTIES_PREFIX, interceptorName);
            Assert.assertEquals("The source of the interceptor is not correct", sourceList.size() , 1);
            Assert.assertEquals("The source of the interceptor is not correct", sourceList.get(0) , interceptorSourceExpected);

            
            
        } catch (Exception e) {
            Assert.fail("An error has occurred [testGetElementsAgentsFromProperties] method");
            logger.error("An error has occurred [testGetElementsAgentsFromProperties] method", e);
        }
    }    
    
    
    
    @Test
    public void testGetGroupFromElement() {

        String elementName = "sink3_2";
        String elemementGroupExpected = "GROUP_3_2";
        
        try {

            Map<String, Map<String, String>> agentConfigurationGroupsMap = FlumeConfiguratorUtils.getAgentConfigurationGroupsMapFromProperties(flumeConfigurationProperties, DEFAULT_SEPARATOR);
            String group = FlumeConfiguratorUtils.getGroupFromElement(agentConfigurationGroupsMap, elementName);
            Assert.assertEquals("The group of the element is not correct", group , elemementGroupExpected);

            
        } catch (Exception e) {
            Assert.fail("An error has occurred [testGetGroupFromElement] method");
            logger.error("An error has occurred [testGetGroupFromElement] method", e);
        }
    }    
    
    
    
    @Test
    public void testGetAgentGroupsSet() {

        String elementName = "agent2";
        String agentGroupsSetExpected = "GROUP_2_1;GROUP_2_2;GROUP_2_3;SINKGROUP_G2";
        
        try {

            List<String> agentGroupsSetExpectedList = Arrays.asList(agentGroupsSetExpected.split(DEFAULT_SEPARATOR));

            Map<String, Map<String, String>> agentConfigurationGroupsMap = FlumeConfiguratorUtils.getAgentConfigurationGroupsMapFromProperties(flumeConfigurationProperties, DEFAULT_SEPARATOR);
            Set<String> groupsSet = FlumeConfiguratorUtils.getAgentGroupsSet(agentConfigurationGroupsMap, elementName);
            Assert.assertEquals("The groups of the agent are not correct", agentGroupsSetExpectedList.size() , groupsSet.size());

            Assert.assertTrue("The groups of the agent are not correct", groupsSet.containsAll(agentGroupsSetExpectedList));
            
        } catch (Exception e) {
            Assert.fail("An error has occurred [testGetAgentGroupsSet] method");
            logger.error("An error has occurred [testGetAgentGroupsSet] method", e);
        }
    }       
        
    
    

    @Test
    public void testMatchingSubset() {

        try {
            LinkedProperties agentsListProperties = FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties, FlumeConfiguratorConstants.AGENTS_LIST_PROPERTIES_PREFIX, true);
            LinkedProperties sourcesListProperties = FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties, FlumeConfiguratorConstants.SOURCES_LIST_PROPERTIES_PREFIX, true);
            LinkedProperties channelsListProperties = FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties, FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX, true);
            LinkedProperties sinksListProperties = FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties, FlumeConfiguratorConstants.SINKS_LIST_PROPERTIES_PREFIX, true);
            LinkedProperties groupsListProperties = FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties, FlumeConfiguratorConstants.GROUPS_LIST_PROPERTIES_PREFIX, true);
            LinkedProperties interceptorsListProperties = FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties, FlumeConfiguratorConstants.INTERCEPTORS_LIST_PROPERTIES_PREFIX, true);

            LinkedProperties sourcesCommonPropertiesList = FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties, FlumeConfiguratorConstants.SOURCES_COMMON_PROPERTY_PROPERTIES_PREFIX, true);
            LinkedProperties sourcesPartialPropertiesList = FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties, FlumeConfiguratorConstants.SOURCES_PARTIAL_PROPERTY_PROPERTIES_PREFIX, true);
            LinkedProperties interceptorsCommonPropertiesList = FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties, FlumeConfiguratorConstants.INTERCEPTORS_COMMON_PROPERTY_PROPERTIES_PREFIX, true);
            LinkedProperties interceptorsCommonPartialropertiesList = FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties, FlumeConfiguratorConstants.INTERCEPTORS_PARTIAL_PROPERTY_PROPERTIES_PREFIX, true);
            LinkedProperties channelsCommonPropertiesList = FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties, FlumeConfiguratorConstants.CHANNELS_COMMON_PROPERTY_PROPERTIES_PREFIX, true);
            LinkedProperties channelsPartialPropertiesList = FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties, FlumeConfiguratorConstants.CHANNELS_PARTIAL_PROPERTY_PROPERTIES_PREFIX, true);
            LinkedProperties sinksCommonPropertiesList = FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties, FlumeConfiguratorConstants.SINKS_COMMON_PROPERTY_PROPERTIES_PREFIX, true);
            LinkedProperties sinksPartialPropertiesList = FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties, FlumeConfiguratorConstants.SINKS_PARTIAL_PROPERTY_PROPERTIES_PREFIX, true);


            //Test objects are not null and are not empty
            Assert.assertNotNull(agentsListProperties);
            Assert.assertFalse("The object is empty", agentsListProperties.isEmpty());

            Assert.assertNotNull(sourcesListProperties);
            Assert.assertFalse("The object is empty", sourcesListProperties.isEmpty());

            Assert.assertNotNull(channelsListProperties);
            Assert.assertFalse("The object is empty", channelsListProperties.isEmpty());

            Assert.assertNotNull(sinksListProperties);
            Assert.assertFalse("The object is empty", sinksListProperties.isEmpty());

            Assert.assertNotNull(groupsListProperties);
            Assert.assertFalse("The object is empty", groupsListProperties.isEmpty());

            Assert.assertNotNull(interceptorsListProperties);
            Assert.assertFalse("The object is empty", interceptorsListProperties.isEmpty());

            Assert.assertNotNull(sourcesCommonPropertiesList);
            Assert.assertFalse("The object is empty", sourcesCommonPropertiesList.isEmpty());

            Assert.assertNotNull(sourcesPartialPropertiesList);
            Assert.assertFalse("The object is empty", sourcesPartialPropertiesList.isEmpty());

            Assert.assertNotNull(interceptorsCommonPropertiesList);
            Assert.assertFalse("The object is empty", interceptorsCommonPropertiesList.isEmpty());

            Assert.assertNotNull(interceptorsCommonPartialropertiesList);
            Assert.assertFalse("The object is empty", interceptorsCommonPartialropertiesList.isEmpty());

            Assert.assertNotNull(channelsCommonPropertiesList);
            Assert.assertFalse("The object is empty", channelsCommonPropertiesList.isEmpty());

            Assert.assertNotNull(channelsPartialPropertiesList);
            Assert.assertFalse("The object is empty", channelsPartialPropertiesList.isEmpty());

            Assert.assertNotNull(sinksCommonPropertiesList);
            Assert.assertFalse("The object is empty", sinksCommonPropertiesList.isEmpty());

            Assert.assertNotNull(sinksPartialPropertiesList);
            Assert.assertFalse("The object is empty", sinksPartialPropertiesList.isEmpty());

        } catch (Exception e) {
            Assert.fail("An error has occurred [testMatchingSubset] method");
            logger.error("An error has occurred [testMatchingSubset] method", e);
        }
    }


    @Test
    public void testGetAgentsListFromProperties() {

        try {

            String nameAgent1 = "agent1";
            String nameAgent2 = "agent2";
            String nameAgent3 = "agent3";


            List<String> agentsList =  FlumeConfiguratorUtils.getAgentsListFromProperties(flumeConfigurationProperties, DEFAULT_SEPARATOR);

            Assert.assertTrue("The size of the agent list is not correct", agentsList.size() == 3);

            //The order has preserved
            for (int i=0; i<agentsList.size(); i++)  {
                if (i==0) {
                    Assert.assertEquals("The agents list is not correctly loaded", agentsList.get(i), nameAgent1);
                } else if (i==1) {
                    Assert.assertEquals("The agents list is not correctly loaded", agentsList.get(i), nameAgent2);
                } else if (i==2) {
                    Assert.assertEquals("The agents list is not correctly loaded", agentsList.get(i), nameAgent3);
                }
            }

        } catch (Exception e) {
            Assert.fail("An error has occurred [testGetAgentsListFromProperties] method");
            logger.error("An error has occurred [testGetAgentsListFromProperties] method", e);
        }
    }


    @Test
    public void testGetAgentElementsMapFromProperties() {

        try {

            int numberAgentsProperties = 3;
            int numberSourcesWithInterceptorsProperties = 4;

            //Check sources list
            Map<String, List<String>> mapAgentsSourcesList = FlumeConfiguratorUtils.getAgentElementsMapFromProperties(flumeConfigurationProperties, FlumeConfiguratorConstants.SOURCES_LIST_PROPERTIES_PREFIX, DEFAULT_SEPARATOR);

            Assert.assertTrue("The number of agents is not correct", mapAgentsSourcesList.size() == numberAgentsProperties);

            String expectedNamePrefix = "source";
            //Check order elements in list
            for (String agentName : mapAgentsSourcesList.keySet()) {
                String indexAgent = agentName.substring(agentName.length()-1);
                //Get List elements
                List<String> agentSourcesList = mapAgentsSourcesList.get(agentName);

                for (int i=0; i<agentSourcesList.size(); i++) {
                    String sourceName = agentSourcesList.get(i);
                    int index = i+1;
                    String expectedName = expectedNamePrefix+indexAgent+"_"+index;
                    Assert.assertEquals("The source is not correct",expectedName, sourceName);
                }

            }


            //Check Channels list
            Map<String, List<String>> mapAgentsChannelsList = FlumeConfiguratorUtils.getAgentElementsMapFromProperties(flumeConfigurationProperties, FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX, DEFAULT_SEPARATOR);

            Assert.assertTrue("The number of agents is not correct", mapAgentsChannelsList.size() == numberAgentsProperties);

            expectedNamePrefix = "channel";
            //Check order elements in list
            for (String agentName : mapAgentsChannelsList.keySet()) {
                String indexAgent = agentName.substring(agentName.length()-1);
                //Get List elements
                List<String> agentChannelsList = mapAgentsChannelsList.get(agentName);

                for (int i=0; i<agentChannelsList.size(); i++) {
                    String channelName = agentChannelsList.get(i);
                    int index = i+1;
                    String expectedName = expectedNamePrefix+indexAgent+"_"+index;
                    Assert.assertEquals("The channel is not correct",expectedName, channelName);
                }

            }


            //Check Sinks list
            Map<String, List<String>> mapAgentsSinksList = FlumeConfiguratorUtils.getAgentElementsMapFromProperties(flumeConfigurationProperties, FlumeConfiguratorConstants.SINKS_LIST_PROPERTIES_PREFIX, DEFAULT_SEPARATOR);

            Assert.assertTrue("The number of agents is not correct", mapAgentsSinksList.size() == numberAgentsProperties);

            expectedNamePrefix = "sink";
            //Check order elements in list
            for (String agentName : mapAgentsSinksList.keySet()) {
                String indexAgent = agentName.substring(agentName.length()-1);
                //Get List elements
                List<String> agentSinksList = mapAgentsSinksList.get(agentName);

                for (int i=0; i<agentSinksList.size(); i++) {
                    String sinkName = agentSinksList.get(i);
                    int index = i+1;
                    String expectedName = expectedNamePrefix+indexAgent+"_"+index;
                    Assert.assertEquals("The sink is not correct",expectedName, sinkName);
                }

            }


            //Check Interceptor list
            Map<String, List<String>> mapSourcesInterceptorsList = FlumeConfiguratorUtils.getAgentElementsMapFromProperties(flumeConfigurationProperties, FlumeConfiguratorConstants.INTERCEPTORS_LIST_PROPERTIES_PREFIX, DEFAULT_SEPARATOR);

            Assert.assertTrue("The number of sources is not correct", mapSourcesInterceptorsList.size() == numberSourcesWithInterceptorsProperties);

            for (String sourceName : mapSourcesInterceptorsList.keySet()) {
                //Get List elements
                List<String> sourceInterceptorsList = mapSourcesInterceptorsList.get(sourceName);
                Assert.assertFalse("The interceptor is not correct", sourceInterceptorsList.isEmpty());

            }

        } catch (Exception e) {
            Assert.fail("An error has occurred [testGetAgentElementsMapFromProperties] method");
            logger.error("An error has occurred [testGetAgentElementsMapFromProperties] method", e);
        }
    }


    @Test
    public void testGetMeasuredPropertyString() {

        try {

            String propertyWithDimension = "property1;property2;property3";
            String propertyWithoutDimension = "value1";


            //Is a private method. Access by reflection
            Class<?>[] args1 = new Class[3];
            args1[0] = String.class;
            args1[1] = String.class;
            args1[2] = String.class;


            Method privateMethodGetMeasuredPropertyString = FlumeConfiguratorUtils.class.getDeclaredMethod("getMeasuredPropertyString", args1);
            privateMethodGetMeasuredPropertyString.setAccessible(true);
            String propertyResult = (String) privateMethodGetMeasuredPropertyString.invoke(null, propertyWithDimension, propertyWithoutDimension, DEFAULT_SEPARATOR);

            String[] propertyResultArray = propertyResult.split(DEFAULT_SEPARATOR);

            Assert.assertTrue("The property dimension is not correct", propertyResultArray.length == propertyWithDimension.split(DEFAULT_SEPARATOR).length);

            for(String propertyResultArrayElement : propertyResultArray) {
                Assert.assertTrue("The property value is not correct", propertyResultArrayElement.equals(propertyWithoutDimension));
            }

        } catch (Exception e) {
            Assert.fail("An error has occurred [testGetMeasuredPropertyString] method");
            logger.error("An error has occurred [testGetMeasuredPropertyString] method", e);
        }

    }
    
    
    
    @Test
    public void testGetElementsCommonPropertiesFromProperties() {

        int numberSourcesCommonProperties = 6;  //3 properties + their comment properties
        int numberChannelsCommonProperties = 4; //2 properties + their comment properties
        int numberSinksCommonProperties = 20; //10 properties + their comment properties
        int numberInterceptorsCommonProperties = 14; //7 properties + their comment properties

        try { 

            //Check sources common properties list
            LinkedProperties sourcesCommonPropertiesList = FlumeConfiguratorUtils.getElementsCommonPropertiesFromProperties(flumeConfigurationProperties, FlumeConfiguratorConstants.SOURCES_COMMON_PROPERTY_PROPERTIES_PREFIX);
            Assert.assertEquals("The number of sources common properties is not correct", sourcesCommonPropertiesList.size(), numberSourcesCommonProperties);
            for (Object key : sourcesCommonPropertiesList.keySet()) {
                String propertyName = (String) key;
                Assert.assertNotNull("The sources common properties are not correct",sourcesCommonPropertiesList.get(key));

                if (propertyName.startsWith(FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX)) {
                    String propertyReference = propertyName.substring(FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX.length() + 1);
                    Assert.assertNotNull("The sources common properties are not correct", sourcesCommonPropertiesList.get(propertyReference));
                } else {
                    String propertyCommentReference = FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + propertyName;
                    Assert.assertNotNull("The sources common properties are not correct", sourcesCommonPropertiesList.get(propertyCommentReference));
                }
            }
            
            

            //Check channels common properties list
            LinkedProperties channelsCommonPropertiesList = FlumeConfiguratorUtils.getElementsCommonPropertiesFromProperties(flumeConfigurationProperties, FlumeConfiguratorConstants.CHANNELS_COMMON_PROPERTY_PROPERTIES_PREFIX);
            Assert.assertEquals("The number of channels common properties is not correct", channelsCommonPropertiesList.size(), numberChannelsCommonProperties);
            for (Object key : channelsCommonPropertiesList.keySet()) {
                String propertyName = (String) key;
                Assert.assertNotNull("The channels common properties are not correct",channelsCommonPropertiesList.get(key));

                if (propertyName.startsWith(FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX)) {
                    String propertyReference = propertyName.substring(FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX.length() + 1);
                    Assert.assertNotNull("The channels common properties are not correct", channelsCommonPropertiesList.get(propertyReference));
                } else {
                    String propertyCommentReference = FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + propertyName;
                    Assert.assertNotNull("The sources common properties are not correct", channelsCommonPropertiesList.get(propertyCommentReference));
                }
            }
            
            
            
            //Check sinks common properties list
            LinkedProperties sinksCommonPropertiesList = FlumeConfiguratorUtils.getElementsCommonPropertiesFromProperties(flumeConfigurationProperties, FlumeConfiguratorConstants.SINKS_COMMON_PROPERTY_PROPERTIES_PREFIX);
            Assert.assertEquals("The number of sinks common properties is not correct", sinksCommonPropertiesList.size(), numberSinksCommonProperties);
            for (Object key : sinksCommonPropertiesList.keySet()) {
                String propertyName = (String) key;
                Assert.assertNotNull("The sinks common properties are not correct",sinksCommonPropertiesList.get(key));

                if (propertyName.startsWith(FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX)) {
                    String propertyReference = propertyName.substring(FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX.length() + 1);
                    Assert.assertNotNull("The sinks common properties are not correct", sinksCommonPropertiesList.get(propertyReference));
                } else {
                    String propertyCommentReference = FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + propertyName;
                    Assert.assertNotNull("The sinks common properties are not correct", sinksCommonPropertiesList.get(propertyCommentReference));
                }
            }
            
            
            
            //Check interceptors common properties list
            LinkedProperties interceptorsCommonPropertiesList = FlumeConfiguratorUtils.getElementsCommonPropertiesFromProperties(flumeConfigurationProperties, FlumeConfiguratorConstants.INTERCEPTORS_COMMON_PROPERTY_PROPERTIES_PREFIX);
            Assert.assertEquals("The number of sinks common properties is not correct", interceptorsCommonPropertiesList.size(), numberInterceptorsCommonProperties);
            for (Object key : interceptorsCommonPropertiesList.keySet()) {
                String propertyName = (String) key;
                Assert.assertNotNull("The interceptors common properties are not correct",interceptorsCommonPropertiesList.get(key));

                if (propertyName.startsWith(FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX)) {
                    String propertyReference = propertyName.substring(FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX.length() + 1);
                    Assert.assertNotNull("The interceptors common properties are not correct", interceptorsCommonPropertiesList.get(propertyReference));
                } else {
                    String propertyCommentReference = FlumeConfiguratorConstants.COMMENT_PROPERTY_PREFIX + FlumeConfiguratorConstants.DOT_SEPARATOR + propertyName;
                    Assert.assertNotNull("The interceptors common properties are not correct", interceptorsCommonPropertiesList.get(propertyCommentReference));
                }
            }             
            
            
        } catch (Exception e) {
            Assert.fail("An error has occurred [testGetElementsCommonPropertiesFromProperties] method");
            logger.error("An error has occurred [testGetElementsCommonPropertiesFromProperties] method", e);
        }

    }
    
    
    
    @Test
    public void testGetElementsPartialPropertiesFromProperties() {

        int numberSourcesPartialProperties = 9;  //3 appliedElements properties  + their propertyValues properties + their comment properties
        int numberChannelsPartialProperties = 6; //2 appliedElements properties  + their propertyValues properties + their comment properties
        int numberSinksPartialProperties = 6; //2 appliedElements properties  + their propertyValues properties + their comment properties
        int numberInterceptorsPartialProperties = 6; //2 appliedElements properties  + their propertyValues properties + their comment properties


        try {  

            //Check sources partial properties list
            PartialProperties sourcesPartialPropertiesList = FlumeConfiguratorUtils.getElementsPartialPropertiesFromProperties(flumeConfigurationProperties, FlumeConfiguratorConstants.SOURCES_PARTIAL_PROPERTY_PROPERTIES_PREFIX, DEFAULT_SEPARATOR);

            LinkedProperties appliedElements = sourcesPartialPropertiesList.getAppliedElements();
            LinkedProperties elementsPropertiesValues = sourcesPartialPropertiesList.getPropertiesValues();
            LinkedProperties elementsPropertiesComments = sourcesPartialPropertiesList.getPropertiesComments();

            Assert.assertNotNull("The sources partial properties are not correct",appliedElements);
            Assert.assertNotNull("The sources partial properties are not correct",elementsPropertiesValues);
            Assert.assertNotNull("The sources partial properties are not correct",elementsPropertiesComments);

            int propertiesNumber = appliedElements.size() + elementsPropertiesValues.size() + elementsPropertiesComments.size();
            Assert.assertEquals("The number of sources partial properties is not correct", propertiesNumber, numberSourcesPartialProperties);

            Assert.assertTrue("The sources partial properties are not correct", appliedElements.keySet().equals(elementsPropertiesValues.keySet()));
            Assert.assertTrue("The sources partial properties are not correct", elementsPropertiesValues.keySet().equals(elementsPropertiesComments.keySet()));

 
            //Check sources partial properties list
            PartialProperties channelsPartialPropertiesList = FlumeConfiguratorUtils.getElementsPartialPropertiesFromProperties(flumeConfigurationProperties, FlumeConfiguratorConstants.CHANNELS_PARTIAL_PROPERTY_PROPERTIES_PREFIX, DEFAULT_SEPARATOR);

            appliedElements = channelsPartialPropertiesList.getAppliedElements();
            elementsPropertiesValues = channelsPartialPropertiesList.getPropertiesValues();
            elementsPropertiesComments = channelsPartialPropertiesList.getPropertiesComments();

            Assert.assertNotNull("The channels partial properties are not correct",appliedElements);
            Assert.assertNotNull("The channels partial properties are not correct",elementsPropertiesValues);
            Assert.assertNotNull("The channels partial properties are not correct",elementsPropertiesComments);

            propertiesNumber = appliedElements.size() + elementsPropertiesValues.size() + elementsPropertiesComments.size();
            Assert.assertEquals("The number of channels partial properties is not correct", propertiesNumber, numberChannelsPartialProperties);

            Assert.assertTrue("The channels partial properties are not correct", appliedElements.keySet().equals(elementsPropertiesValues.keySet()));
            Assert.assertTrue("The channels partial properties are not correct", elementsPropertiesValues.keySet().equals(elementsPropertiesComments.keySet()));

 
            //Check sinks partial properties list
            PartialProperties sinksPartialPropertiesList = FlumeConfiguratorUtils.getElementsPartialPropertiesFromProperties(flumeConfigurationProperties, FlumeConfiguratorConstants.SINKS_PARTIAL_PROPERTY_PROPERTIES_PREFIX, DEFAULT_SEPARATOR);

            appliedElements = sinksPartialPropertiesList.getAppliedElements();
            elementsPropertiesValues = sinksPartialPropertiesList.getPropertiesValues();
            elementsPropertiesComments = sinksPartialPropertiesList.getPropertiesComments();

            Assert.assertNotNull("The sinks partial properties are not correct",appliedElements);
            Assert.assertNotNull("The sinks partial properties are not correct",elementsPropertiesValues);
            Assert.assertNotNull("The sinks partial properties are not correct",elementsPropertiesComments);

            propertiesNumber = appliedElements.size() + elementsPropertiesValues.size() + elementsPropertiesComments.size();
            Assert.assertEquals("The number of sinks partial properties is not correct", propertiesNumber, numberSinksPartialProperties);

            Assert.assertTrue("The sinks partial properties are not correct", appliedElements.keySet().equals(elementsPropertiesValues.keySet()));
            Assert.assertTrue("The sinks partial properties are not correct", elementsPropertiesValues.keySet().equals(elementsPropertiesComments.keySet()));

 
            //Check interceptors partial properties list
            PartialProperties interceptorsPartialPropertiesList = FlumeConfiguratorUtils.getElementsPartialPropertiesFromProperties(flumeConfigurationProperties, FlumeConfiguratorConstants.INTERCEPTORS_PARTIAL_PROPERTY_PROPERTIES_PREFIX, DEFAULT_SEPARATOR);

            appliedElements = interceptorsPartialPropertiesList.getAppliedElements();
            elementsPropertiesValues = interceptorsPartialPropertiesList.getPropertiesValues();
            elementsPropertiesComments = interceptorsPartialPropertiesList.getPropertiesComments();

            Assert.assertNotNull("The interceptors partial properties are not correct",appliedElements);
            Assert.assertNotNull("The interceptors partial properties are not correct",elementsPropertiesValues);
            Assert.assertNotNull("The interceptors partial properties are not correct",elementsPropertiesComments);

            propertiesNumber = appliedElements.size() + elementsPropertiesValues.size() + elementsPropertiesComments.size();
            Assert.assertEquals("The number of interceptors partial properties is not correct", propertiesNumber, numberInterceptorsPartialProperties);

            Assert.assertTrue("The interceptors partial properties are not correct", appliedElements.keySet().equals(elementsPropertiesValues.keySet()));
            Assert.assertTrue("The interceptors partial properties are not correct", elementsPropertiesValues.keySet().equals(elementsPropertiesComments.keySet()));


        } catch (Exception e) {
            Assert.fail("An error has occurred [testGetElementsPartialPropertiesFromProperties] method");
            logger.error("An error has occurred [testGetElementsPartialPropertiesFromProperties] method", e);
        }

    }
    
    
    
    @Test
    public void testGetAgentConfigurationGroupsMapFromProperties() {

        int agentsNumber = 3;

        String elementsAgent1 = "source1_1;channel1_1;sink1_1;source1_2;channel1_2;sink1_2;source1_3;channel1_3;sink1_3;g1";
        String elementsAgent2 = "source2_1;channel2_1;sink2_1;source2_2;channel2_2;sink2_2;source2_3;channel2_3;sink2_3;g2";
        String elementsAgent3 = "source3_1;channel3_1;sink3_1;source3_2;channel3_2;sink3_2;source3_3;channel3_3;sink3_3";

        try {   

            //Check groups properties list
            Map<String,  Map<String, String>> mapAgentConfigurationGroups = FlumeConfiguratorUtils.getAgentConfigurationGroupsMapFromProperties(flumeConfigurationProperties, DEFAULT_SEPARATOR);

            Assert.assertEquals("The groups configuration is not correct", mapAgentConfigurationGroups.keySet().size(), agentsNumber);

            Set<String> elementsAgent1Set = new HashSet<>(Arrays.asList(elementsAgent1.split(DEFAULT_SEPARATOR)));
            Set<String> elementsAgent2Set = new HashSet<>(Arrays.asList(elementsAgent2.split(DEFAULT_SEPARATOR)));
            Set<String> elementsAgent3Set = new HashSet<>(Arrays.asList(elementsAgent3.split(DEFAULT_SEPARATOR)));

            for (String agentName : mapAgentConfigurationGroups.keySet()) {
                Map<String, String> groupsAgent = mapAgentConfigurationGroups.get(agentName);

                if ("agent1".equals(agentName)) {
                    Assert.assertEquals("The groups configuration is not correct", groupsAgent.keySet().size(), elementsAgent1Set.size());
                    Assert.assertTrue("The groups configuration is not correct",  groupsAgent.keySet().equals(elementsAgent1Set) );
                } else if ("agent2".equals(agentName)) {
                    Assert.assertEquals("The groups configuration is not correct", groupsAgent.keySet().size(), elementsAgent2Set.size());
                    Assert.assertTrue("The groups configuration is not correct",  groupsAgent.keySet().equals(elementsAgent2Set) );
                } else if ("agent3".equals(agentName)) {
                    Assert.assertEquals("The groups configuration is not correct", groupsAgent.keySet().size(), elementsAgent3Set.size());
                    Assert.assertTrue("The groups configuration is not correct",  groupsAgent.keySet().equals(elementsAgent3Set) );
                }

            }


        } catch (Exception e) {
            Assert.fail("An error has occurred [testGetAgentConfigurationGroupsMapFromProperties] method");
            logger.error("An error has occurred [testGetAgentConfigurationGroupsMapFromProperties] method", e);
        }

    }
    
    
    @Test
    public void testSplitWithoutSpacesOptional() {

        String value = " cadena1  cadena1b  ; cadena2 ;   cadena3";

        String splittedValueWithRemoveSpacesItem1Expected = "cadena1cadena1b";
        String splittedValueWithRemoveSpacesItem2Expected = "cadena2";
        String splittedValueWithRemoveSpacesItem3Expected = "cadena3";

        String splittedValueWithoutRemoveSpacesItem1Expected = " cadena1  cadena1b  ";
        String splittedValueWithoutRemoveSpacesItem2Expected = " cadena2 ";
        String splittedValueWithoutRemoveSpacesItem3Expected = "   cadena3";

        try { 
            Assert.assertTrue("The generated text is not correct", FlumeConfiguratorUtils.splitWithoutSpacesOptional(null, true, DEFAULT_SEPARATOR).length == 0);

            String[] splittedValueWithRemoveSpaces = FlumeConfiguratorUtils.splitWithoutSpacesOptional(value, true, DEFAULT_SEPARATOR);
            Assert.assertEquals("The generated text is not correct", splittedValueWithRemoveSpaces.length, 3);

            for (int i=0; i<splittedValueWithRemoveSpaces.length; i++) {
                if (i==0) {
                    Assert.assertEquals("The generated text is not correct", splittedValueWithRemoveSpaces[i], splittedValueWithRemoveSpacesItem1Expected);
                } else if (i==1) {
                    Assert.assertEquals("The generated text is not correct", splittedValueWithRemoveSpaces[i], splittedValueWithRemoveSpacesItem2Expected);
                } else if (i==2) {
                    Assert.assertEquals("The generated text is not correct", splittedValueWithRemoveSpaces[i], splittedValueWithRemoveSpacesItem3Expected);
                }
            }


            String[] splittedValueWithoutRemoveSpaces = FlumeConfiguratorUtils.splitWithoutSpacesOptional(value, false, DEFAULT_SEPARATOR);
            Assert.assertEquals("The generated text is not correct", splittedValueWithRemoveSpaces.length, 3);

            for (int i=0; i<splittedValueWithRemoveSpaces.length; i++) {
                if (i==0) {
                    Assert.assertEquals("The generated text is not correct", splittedValueWithoutRemoveSpaces[i], splittedValueWithoutRemoveSpacesItem1Expected);
                } else if (i==1) {
                    Assert.assertEquals("The generated text is not correct", splittedValueWithoutRemoveSpaces[i], splittedValueWithoutRemoveSpacesItem2Expected);
                } else if (i==2) {
                    Assert.assertEquals("The generated text is not correct", splittedValueWithoutRemoveSpaces[i], splittedValueWithoutRemoveSpacesItem3Expected);
                }
            }


        } catch (Exception e) {
            Assert.fail("An error has occurred [testSplitWithoutSpacesOptional] method");
            logger.error("An error has occurred [testSplitWithoutSpacesOptional] method", e);
        }
    }
    
    
    @Test
    public void testSplitWithoutSpacesOptionalKeepInternalSpaces() {

        String value = " cadena1  cadena1b  ; cadena2 ;   cadena3";

        String splittedValueWithRemoveSpacesItem1Expected = "cadena1  cadena1b";
        String splittedValueWithRemoveSpacesItem2Expected = "cadena2";
        String splittedValueWithRemoveSpacesItem3Expected = "cadena3";

        String splittedValueWithoutRemoveSpacesItem1Expected = " cadena1  cadena1b  ";
        String splittedValueWithoutRemoveSpacesItem2Expected = " cadena2 ";
        String splittedValueWithoutRemoveSpacesItem3Expected = "   cadena3";

        try { 
            Assert.assertTrue("The generated text is not correct", FlumeConfiguratorUtils.splitWithoutSpacesOptionalKeepInternalSpaces(null, true, DEFAULT_SEPARATOR).length == 0);

            String[] splittedValueWithRemoveSpaces = FlumeConfiguratorUtils.splitWithoutSpacesOptionalKeepInternalSpaces(value, true, DEFAULT_SEPARATOR);
            Assert.assertEquals("The generated text is not correct", splittedValueWithRemoveSpaces.length, 3);

            for (int i=0; i<splittedValueWithRemoveSpaces.length; i++) {
                if (i==0) {
                    Assert.assertEquals("The generated text is not correct", splittedValueWithRemoveSpaces[i], splittedValueWithRemoveSpacesItem1Expected);
                } else if (i==1) {
                    Assert.assertEquals("The generated text is not correct", splittedValueWithRemoveSpaces[i], splittedValueWithRemoveSpacesItem2Expected);
                } else if (i==2) {
                    Assert.assertEquals("The generated text is not correct", splittedValueWithRemoveSpaces[i], splittedValueWithRemoveSpacesItem3Expected);
                }
            }


            String[] splittedValueWithoutRemoveSpaces = FlumeConfiguratorUtils.splitWithoutSpacesOptionalKeepInternalSpaces(value, false, DEFAULT_SEPARATOR);
            Assert.assertEquals("The generated text is not correct", splittedValueWithRemoveSpaces.length, 3);

            for (int i=0; i<splittedValueWithRemoveSpaces.length; i++) {
                if (i==0) {
                    Assert.assertEquals("The generated text is not correct", splittedValueWithoutRemoveSpaces[i], splittedValueWithoutRemoveSpacesItem1Expected);
                } else if (i==1) {
                    Assert.assertEquals("The generated text is not correct", splittedValueWithoutRemoveSpaces[i], splittedValueWithoutRemoveSpacesItem2Expected);
                } else if (i==2) {
                    Assert.assertEquals("The generated text is not correct", splittedValueWithoutRemoveSpaces[i], splittedValueWithoutRemoveSpacesItem3Expected);
                }
            }


        } catch (Exception e) {
            Assert.fail("An error has occurred [testSplitWithoutSpacesOptionalKeepInternalSpaces] method");
            logger.error("An error has occurred [testSplitWithoutSpacesOptionalKeepInternalSpaces] method", e);
        }
    }    
}
