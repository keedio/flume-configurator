package org.keedio.flume.configurator.validator;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.keedio.flume.configurator.constants.FlumeConfiguratorConstants;
import org.slf4j.LoggerFactory;

public class ConfigurationValidatorTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ConfigurationValidatorTest.class);


    private static final String AGENT1_NAME = "agent1";
    private static final String AGENT2_NAME = "agent2";
    private static final String AGENT3_NAME = "agent3";

    private static final String DEFAULT_SEPARATOR = ";";

    private static Properties flumeConfigurationProperties = new Properties();
    private static List<String> agentList = new ArrayList<>();

    private static ConfigurationValidator configurationValidator = new ConfigurationValidator(flumeConfigurationProperties, DEFAULT_SEPARATOR);


    @Rule public TestName testName = new TestName();

    @BeforeClass
    public static void loadPropertiesFile() throws IOException {
        FileInputStream fis = new FileInputStream("src/test/resources/FlumeConfigurationExample.properties");
        flumeConfigurationProperties.clear();
        flumeConfigurationProperties.load(fis);
        agentList.add(AGENT1_NAME);
        agentList.add(AGENT2_NAME);
        agentList.add(AGENT3_NAME);


    }


    @Before
    public void resetConfigurationValidator() throws IOException {
        configurationValidator = new ConfigurationValidator(flumeConfigurationProperties, DEFAULT_SEPARATOR);
    }


    private void showCheckErrors() {
        logger.error("[Test " + testName.getMethodName() + "] " + configurationValidator.getSbCheckErrors().toString());
    }


    @Test
    public void testCheckPropertiesFileSourcesChannelsSinksListSourcesCheck() {

        String prefixProperty = FlumeConfiguratorConstants.SOURCES_LIST_PROPERTIES_PREFIX;
        boolean isPropertiesFileOK;

        try {

            //Is a private method. Access by reflection
            Class<?>[] args1 = new Class[2];
            args1[0] = String.class;
            args1[1] = List.class;

            Method checkPropertiesFileSourcesChannelsSinksListMethod = ConfigurationValidator.class.getDeclaredMethod("checkPropertiesFileSourcesChannelsSinksList", args1);
            checkPropertiesFileSourcesChannelsSinksListMethod.setAccessible(true);


            //Check sources OK
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertTrue("The sources validation is not correct", isPropertiesFileOK);

            //Check sources without properties
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("sources.list.agent1");
            flumeConfigurationProperties.remove("sources.list.agent2");
            flumeConfigurationProperties.remove("sources.list.agent3");
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check agent without sources.list
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("sources.list.agent1");
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.list empty
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sources.list.agent1","");
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check non existing agent
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sources.list.agent4","source4");
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources validation is not correct", isPropertiesFileOK);
            showCheckErrors();

        } catch (Exception e) {
            Assert.fail("An error has occurred [testCheckPropertiesFileSourcesChannelsSinksListSourcesCheck] method");
            logger.error("An error has occurred [testCheckPropertiesFileSourcesChannelsSinksListSourcesCheck] method", e);
        }
    }



    @Test
    public void testCheckPropertiesFileSourcesChannelsSinksListChannelsCheck() {

        String prefixProperty = FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX;
        boolean isPropertiesFileOK;

        try {

            //Is a private method. Access by reflection
            Class<?>[] args1 = new Class[2];
            args1[0] = String.class;
            args1[1] = List.class;

            Method checkPropertiesFileSourcesChannelsSinksListMethod = ConfigurationValidator.class.getDeclaredMethod("checkPropertiesFileSourcesChannelsSinksList", args1);
            checkPropertiesFileSourcesChannelsSinksListMethod.setAccessible(true);


            //Check channels OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertTrue("The channels validation is not correct", isPropertiesFileOK);


            //Check channels without properties
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("channels.list.agent1");
            flumeConfigurationProperties.remove("channels.list.agent2");
            flumeConfigurationProperties.remove("channels.list.agent3");
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The channels validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check agent without channels.list
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("channels.list.agent1");
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The channels validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check channels.list empty
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("channels.list.agent1","");
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The channels validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check non existing agent
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("channels.list.agent4","channel4");
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The channels validation is not correct", isPropertiesFileOK);
            showCheckErrors();


        } catch (Exception e) {
            Assert.fail("An error has occurred [testCheckPropertiesFileSourcesChannelsSinksListChannelsCheck] method");
            logger.error("An error has occurred [testCheckPropertiesFileSourcesChannelsSinksListChannelsCheck] method", e);
        }
    }




    @Test
    public void testCheckPropertiesFileSourcesChannelsSinksListSinksCheck() {

        String prefixProperty = FlumeConfiguratorConstants.SINKS_LIST_PROPERTIES_PREFIX;
        boolean isPropertiesFileOK;

        try {

            //Is a private method. Access by reflection
            Class<?>[] args1 = new Class[2];
            args1[0] = String.class;
            args1[1] = List.class;

            Method checkPropertiesFileSourcesChannelsSinksListMethod = ConfigurationValidator.class.getDeclaredMethod("checkPropertiesFileSourcesChannelsSinksList", args1);
            checkPropertiesFileSourcesChannelsSinksListMethod.setAccessible(true);


            //Check sinks OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertTrue("The sinks validation is not correct", isPropertiesFileOK);


            //Check sinks without properties
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("sinks.list.agent1");
            flumeConfigurationProperties.remove("sinks.list.agent2");
            flumeConfigurationProperties.remove("sinks.list.agent3");
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check agent without sinks.list
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("sinks.list.agent1");
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.list empty
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sinks.list.agent1","");
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check non existing agent
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sinks.list.agent4","channel4");
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks validation is not correct", isPropertiesFileOK);
            showCheckErrors();

        } catch (Exception e) {
            Assert.fail("An error has occurred [testCheckPropertiesFileSourcesChannelsSinksListSinksCheck] method");
            logger.error("An error has occurred [testCheckPropertiesFileSourcesChannelsSinksListSinksCheck] method", e);
        }
    }



    @Test
    public void testCheckPropertiesFileGroupsList() {

        try {

            //Is a private method. Access by reflection
            Class<?>[] args1 = new Class[1];
            args1[0] = List.class;

            Method checkPropertiesFileGroupsListMethod = ConfigurationValidator.class.getDeclaredMethod("checkPropertiesFileGroupsList", args1);
            checkPropertiesFileGroupsListMethod.setAccessible(true);

            boolean isPropertiesFileOK;

            //Check groups OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFileGroupsListMethod.invoke(configurationValidator, agentList);
            Assert.assertTrue("The groups validation is not correct", isPropertiesFileOK);


            //Check groups without properties
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("groups.list.agent1.GROUP_1_1");
            flumeConfigurationProperties.remove("groups.list.agent1.GROUP_1_2");
            flumeConfigurationProperties.remove("groups.list.agent1.GROUP_1_3");
            flumeConfigurationProperties.remove("groups.list.agent2.GROUP_2_1");
            flumeConfigurationProperties.remove("groups.list.agent2.GROUP_2_2");
            flumeConfigurationProperties.remove("groups.list.agent2.GROUP_2_3");
            flumeConfigurationProperties.remove("groups.list.agent3.GROUP_3_1");
            flumeConfigurationProperties.remove("groups.list.agent3.GROUP_3_2");
            flumeConfigurationProperties.remove("groups.list.agent3.GROUP_3_3");
            isPropertiesFileOK = (boolean) checkPropertiesFileGroupsListMethod.invoke(configurationValidator, agentList);
            Assert.assertFalse("The groups validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check groups.list empty
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("groups.list.agent1.GROUP_1_1","");
            isPropertiesFileOK = (boolean) checkPropertiesFileGroupsListMethod.invoke(configurationValidator, agentList);
            Assert.assertFalse("The groups validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check non existing agent
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("groups.list.agent4.GROUP_4_1","group4");
            isPropertiesFileOK = (boolean) checkPropertiesFileGroupsListMethod.invoke(configurationValidator, agentList);
            Assert.assertFalse("The groups validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check value property belong to agent of the group
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("groups.list.agent1.GROUP_1_1","source1_1;channel3_1;sink1_1");
            isPropertiesFileOK = (boolean) checkPropertiesFileGroupsListMethod.invoke(configurationValidator, agentList);
            Assert.assertFalse("The groups validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check element belongs more than one group of the same agent
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("groups.list.agent1.GROUP_1_1","source1_1;channel1_1;sink1_1");
            flumeConfigurationProperties.put("groups.list.agent1.GROUP_1_2","source1_2;channel1_1;sink1_2");
            isPropertiesFileOK = (boolean) checkPropertiesFileGroupsListMethod.invoke(configurationValidator, agentList);
            Assert.assertFalse("The groups validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check all elements of the agent belong to one group
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("groups.list.agent1.GROUP_1_1","source1_1;channel1_1");
            isPropertiesFileOK = (boolean) checkPropertiesFileGroupsListMethod.invoke(configurationValidator, agentList);
            Assert.assertFalse("The groups validation is not correct", isPropertiesFileOK);
            showCheckErrors();

        } catch (Exception e) {
            Assert.fail("An error has occurred [testCheckPropertiesFileGroupsList] method");
            logger.error("An error has occurred [testCheckPropertiesFileGroupsList] method", e);
        }
    }


    @Test
    public void testCheckPropertiesFileInterceptorsList() {

        try {

            //Is a private method. Access by reflection
            Class<?>[] args1 = new Class[1];
            args1[0] = List.class;

            Method checkPropertiesFileInterceptorsListMethod = ConfigurationValidator.class.getDeclaredMethod("checkPropertiesFileInterceptorsList", args1);
            checkPropertiesFileInterceptorsListMethod.setAccessible(true);

            boolean isPropertiesFileOK;


            //Check interceptors OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFileInterceptorsListMethod.invoke(configurationValidator, agentList);
            Assert.assertTrue("The interceptors validation is not correct", isPropertiesFileOK);


            //Check interceptors.list empty
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("interceptors.list.source1_1","");
            isPropertiesFileOK = (boolean) checkPropertiesFileInterceptorsListMethod.invoke(configurationValidator, agentList);
            Assert.assertFalse("The interceptors validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check Non existing source of the interceptor
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("interceptors.list.source4_1","interceptor4_1");
            isPropertiesFileOK = (boolean) checkPropertiesFileInterceptorsListMethod.invoke(configurationValidator, agentList);
            Assert.assertFalse("The interceptors validation is not correct", isPropertiesFileOK);
            showCheckErrors();

        } catch (Exception e) {
            Assert.fail("An error has occurred [testCheckPropertiesFileInterceptorsList] method");
            logger.error("An error has occurred [testCheckPropertiesFileInterceptorsList] method", e);
        }
    }



    @Test
    public void testCheckPropertiesFileCommonPropertiesSourcesCheck() {

        String prefixProperty = FlumeConfiguratorConstants.SOURCES_COMMON_PROPERTY_PROPERTIES_PREFIX;
        boolean isPropertiesFileOK;

        try {

            //Is a private method. Access by reflection
            Class<?>[] args1 = new Class[2];
            args1[0] = String.class;
            args1[1] = List.class;

            Method checkPropertiesFileCommonPropertiesMethod = ConfigurationValidator.class.getDeclaredMethod("checkPropertiesFileCommonProperties", args1);
            checkPropertiesFileCommonPropertiesMethod.setAccessible(true);


            //Check sources common properties OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertTrue("The sources common properties validation is not correct", isPropertiesFileOK);


            //Check sources.commonProperty.comment property references a property that not exists
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sources.commonProperty.comment.fakeproperty","Comentario .fakeproperty");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources common properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.commonProperty empty
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sources.commonProperty.type","");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources common properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.commonProperty.channels references channels that don't belong to any agent
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sources.commonProperty.channels","fakeChannel");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources common properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.commonProperty.channels doesn't references all channels of all agents (WARNING)
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sources.commonProperty.channels","channel1_1");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertTrue("The sources common properties validation is not correct", isPropertiesFileOK);

        } catch (Exception e) {
            Assert.fail("An error has occurred [testCheckPropertiesFileCommonPropertiesSourcesCheck] method");
            logger.error("An error has occurred [testCheckPropertiesFileCommonPropertiesSourcesCheck] method", e);
        }
    }



    @Test
    public void testCheckPropertiesFileCommonPropertiesChannelsCheck() {

        String prefixProperty = FlumeConfiguratorConstants.CHANNELS_COMMON_PROPERTY_PROPERTIES_PREFIX;
        boolean isPropertiesFileOK;

        try {

            //Is a private method. Access by reflection
            Class<?>[] args1 = new Class[2];
            args1[0] = String.class;
            args1[1] = List.class;

            Method checkPropertiesFileCommonPropertiesMethod = ConfigurationValidator.class.getDeclaredMethod("checkPropertiesFileCommonProperties", args1);
            checkPropertiesFileCommonPropertiesMethod.setAccessible(true);


            //Check channels common properties OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertTrue("The channels common properties validation is not correct", isPropertiesFileOK);


            //Check channels.commonProperty.comment property references a property that not exists
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("channels.commonProperty.comment.fakeproperty","Comentario .fakeproperty");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The channels common properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check channels.commonProperty empty
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("channels.commonProperty.type","");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The channels common properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

        } catch (Exception e) {
            Assert.fail("An error has occurred [testCheckPropertiesFileCommonPropertiesChannelsCheck] method");
            logger.error("An error has occurred [testCheckPropertiesFileCommonPropertiesChannelsCheck] method", e);
        }
    }




    @Test
    public void testCheckPropertiesFileCommonPropertiesSinksCheck() {

        String prefixProperty = FlumeConfiguratorConstants.SINKS_COMMON_PROPERTY_PROPERTIES_PREFIX;
        boolean isPropertiesFileOK;

        try {

            //Is a private method. Access by reflection
            Class<?>[] args1 = new Class[2];
            args1[0] = String.class;
            args1[1] = List.class;

            Method checkPropertiesFileCommonPropertiesMethod = ConfigurationValidator.class.getDeclaredMethod("checkPropertiesFileCommonProperties", args1);
            checkPropertiesFileCommonPropertiesMethod.setAccessible(true);


            //Check channels common properties OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertTrue("The sinks common properties validation is not correct", isPropertiesFileOK);


            //Check sinks.commonProperty.comment property references a property that not exists
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sinks.commonProperty.comment.fakeproperty","Comentario .fakeproperty");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks common properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.commonProperty empty
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sinks.commonProperty.type","");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks common properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.commonProperty.channel references channel that don't belong to any agent
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sinks.commonProperty.channel","fakeChannel");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks common properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.commonProperty.channel doesn't references all channels of all agents (WARNING)
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sinks.commonProperty.channel","channel1_1");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertTrue("The sinks common properties validation is not correct", isPropertiesFileOK);


        } catch (Exception e) {
            Assert.fail("An error has occurred [testCheckPropertiesFileCommonPropertiesSinksCheck] method");
            logger.error("An error has occurred [testCheckPropertiesFileCommonPropertiesSinksCheck] method", e);
        }
    }




    @Test
    public void testCheckPropertiesFileCommonPropertiesInterceptorsCheck() {

        String prefixProperty = FlumeConfiguratorConstants.INTERCEPTORS_COMMON_PROPERTY_PROPERTIES_PREFIX;
        boolean isPropertiesFileOK;

        try {

            //Is a private method. Access by reflection
            Class<?>[] args1 = new Class[2];
            args1[0] = String.class;
            args1[1] = List.class;

            Method checkPropertiesFileCommonPropertiesMethod = ConfigurationValidator.class.getDeclaredMethod("checkPropertiesFileCommonProperties", args1);
            checkPropertiesFileCommonPropertiesMethod.setAccessible(true);


            //Check interceptors common properties OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertTrue("The interceptors common properties validation is not correct", isPropertiesFileOK);


            //Check interceptors.commonProperty.comment property references a property that not exists
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("interceptors.commonProperty.comment.fakeproperty","Comentario .fakeproperty");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The interceptors common properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check interceptors.commonProperty empty
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("interceptors.commonProperty.type","");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The interceptors common properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();
        } catch (Exception e) {
            Assert.fail("An error has occurred [testCheckPropertiesFileCommonPropertiesInterceptorsCheck] method");
            logger.error("An error has occurred [testCheckPropertiesFileCommonPropertiesInterceptorsCheck] method", e);
        }
    }



    @Test
    public void testCheckPropertiesFilePartialPropertiesSourcesCheck() {

        String prefixProperty = FlumeConfiguratorConstants.SOURCES_PARTIAL_PROPERTY_PROPERTIES_PREFIX;
        boolean isPropertiesFileOK;

        try {

            //Is a private method. Access by reflection
            Class<?>[] args1 = new Class[2];
            args1[0] = String.class;
            args1[1] = List.class;

            Method checkPropertiesFilePartialPropertiesMethod = ConfigurationValidator.class.getDeclaredMethod("checkPropertiesFilePartialProperties", args1);
            checkPropertiesFilePartialPropertiesMethod.setAccessible(true);


            //Check sources partial properties OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertTrue("The sources partial properties validation is not correct", isPropertiesFileOK);


            //Check sources.partialProperty.appliedElements empty
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sources.partialProperty.appliedElements.topic","");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.partialProperty.appliedElements existence propertyValues property
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("sources.partialProperty.propertyValues.topic");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.partialProperty.appliedElements value references existing elements
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sources.partialProperty.appliedElements.topic", "sourceFake");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.partialProperty.propertyValues empty
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sources.partialProperty.propertyValues.topic","");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.partialProperty.propertyValues existence appliedElements property
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("sources.partialProperty.appliedElements.topic");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.partialProperty.propertyValues correct dimension
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sources.partialProperty.propertyValues.topic", "topic1;topic2;topic3");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.partialProperty.propertyValues.channels (1 value) non existing channel
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sources.partialProperty.propertyValues.channels", "channelFake");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.partialProperty.propertyValues.channels (1 value) channel belongs agent of the source
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sources.partialProperty.propertyValues.channels", "channel3_1");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.partialProperty.propertyValues.channels (n values) non existing channel
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sources.partialProperty.propertyValues.channels", "channel1_1;channelFake");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.partialProperty.propertyValues.channels (n value) channel belongs agent of the source
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sources.partialProperty.propertyValues.channels", "channel1_1;channel3_1");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.partialProperty.comment existence appliedElements property
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("sources.partialProperty.appliedElements.topic");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.partialProperty.comment correct dimension
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sources.partialProperty.comment.topic", "Item 1 Comment;Item 2 Comment;Item 3 Comment");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check unknown type property
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sources.partialProperty.unknown", "Unknown value");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

        } catch (Exception e) {
            Assert.fail("An error has occurred [testCheckPropertiesFilePartialPropertiesSourcesCheck] method");
            logger.error("An error has occurred [testCheckPropertiesFilePartialPropertiesSourcesCheck] method", e);
        }
    }



    @Test
    public void testCheckPropertiesFilePartialPropertiesChannelsCheck() {

        String prefixProperty = FlumeConfiguratorConstants.CHANNELS_PARTIAL_PROPERTY_PROPERTIES_PREFIX;
        boolean isPropertiesFileOK;

        try {

            //Is a private method. Access by reflection
            Class<?>[] args1 = new Class[2];
            args1[0] = String.class;
            args1[1] = List.class;

            Method checkPropertiesFilePartialPropertiesMethod = ConfigurationValidator.class.getDeclaredMethod("checkPropertiesFilePartialProperties", args1);
            checkPropertiesFilePartialPropertiesMethod.setAccessible(true);


            //Check channels partial properties OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertTrue("The channels partial properties validation is not correct", isPropertiesFileOK);


            //Check channels.partialProperty.appliedElements empty
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("channels.partialProperty.appliedElements.type","");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The channels partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check channels.partialProperty.appliedElements existence propertyValues property
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("channels.partialProperty.propertyValues.type");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The channels partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check channels.partialProperty.appliedElements value references existing elements
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("channels.partialProperty.appliedElements.type", "channelFake");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The channels partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check channels.partialProperty.propertyValues empty
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("channels.partialProperty.propertyValues.type","");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The channels partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check channels.partialProperty.propertyValues existence appliedElements property
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("channels.partialProperty.appliedElements.type");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The channels partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check channels.partialProperty.propertyValues correct dimension
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("channels.partialProperty.propertyValues.type", "disk;disk;disk");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The channels partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check channels.partialProperty.comment existence appliedElements property
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("channels.partialProperty.appliedElements.type");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The channels partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check channels.partialProperty.comment correct dimension
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("channels.partialProperty.comment.type", "Item 1 Comment;Item 2 Comment;Item 3 Comment");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The channels partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check unknown type property
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("channels.partialProperty.unknown", "Unknown value");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The channels partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

        } catch (Exception e) {
            Assert.fail("An error has occurred [testCheckPropertiesFilePartialPropertiesChannelsCheck] method");
            logger.error("An error has occurred [testCheckPropertiesFilePartialPropertiesChannelsCheck] method", e);
        }
    }



    @Test
    public void testCheckPropertiesFilePartialPropertiesSinksCheck() {

        String prefixProperty = FlumeConfiguratorConstants.SINKS_PARTIAL_PROPERTY_PROPERTIES_PREFIX;
        boolean isPropertiesFileOK;

        try {

            //Is a private method. Access by reflection
            Class<?>[] args1 = new Class[2];
            args1[0] = String.class;
            args1[1] = List.class;

            Method checkPropertiesFilePartialPropertiesMethod = ConfigurationValidator.class.getDeclaredMethod("checkPropertiesFilePartialProperties", args1);
            checkPropertiesFilePartialPropertiesMethod.setAccessible(true);


            //Check sinks partial properties OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertTrue("The sinks partial properties validation is not correct", isPropertiesFileOK);


            //Check sinks.partialProperty.appliedElements empty
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sinks.partialProperty.appliedElements.hdfs.filePrefix","");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.partialProperty.appliedElements existence propertyValues property
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("sinks.partialProperty.propertyValues.hdfs.filePrefix");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.partialProperty.appliedElements value references existing elements
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sinks.partialProperty.appliedElements.hdfs.filePrefix", "sinkFake");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.partialProperty.propertyValues empty
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sinks.partialProperty.propertyValues.hdfs.filePrefix","");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.partialProperty.propertyValues existence appliedElements property
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("sinks.partialProperty.appliedElements.hdfs.filePrefix");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.partialProperty.propertyValues correct dimension
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sinks.partialProperty.propertyValues.hdfs.filePrefix", "pnl1;pnl2");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.partialProperty.propertyValues.channels (1 value) non existing channel
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sinks.partialProperty.propertyValues.channel", "channelFake");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.partialProperty.propertyValues.channels (1 value) channel belongs agent of the source
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sinks.partialProperty.propertyValues.channel", "channel3_1");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.partialProperty.propertyValues.channels (n values) non existing channel
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sinks.partialProperty.propertyValues.channel", " channel1_1;channel1_2;channelFake");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.partialProperty.propertyValues.channels (n values) channel belongs agent of the source
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sinks.partialProperty.propertyValues.channel", "channel1_1;channel1_2;channel3_1");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.partialProperty.comment existence appliedElements property
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("sinks.partialProperty.appliedElements.hdfs.filePrefix");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.partialProperty.comment correct dimension
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sinks.partialProperty.comment.hdfs.filePrefix", "Item 1 Comment;Item 2 Comment");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check unknown type property
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("sinks.partialProperty.unknown", "Unknown value");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

        } catch (Exception e) {
            Assert.fail("An error has occurred [testCheckPropertiesFilePartialPropertiesSinksCheck] method");
            logger.error("An error has occurred [testCheckPropertiesFilePartialPropertiesSinksCheck] method", e);
        }
    }



    @Test
    public void testCheckPropertiesFilePartialPropertiesInterceptorsCheck() {

        String prefixProperty = FlumeConfiguratorConstants.INTERCEPTORS_PARTIAL_PROPERTY_PROPERTIES_PREFIX;
        boolean isPropertiesFileOK;

        try {

            //Is a private method. Access by reflection
            Class<?>[] args1 = new Class[2];
            args1[0] = String.class;
            args1[1] = List.class;

            Method checkPropertiesFilePartialPropertiesMethod = ConfigurationValidator.class.getDeclaredMethod("checkPropertiesFilePartialProperties", args1);
            checkPropertiesFilePartialPropertiesMethod.setAccessible(true);


            //Check interceptors partial properties OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertTrue("The interceptors partial properties validation is not correct", isPropertiesFileOK);


            //Check interceptors.partialProperty.appliedElements empty
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("interceptors.partialProperty.appliedElements.filename","");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The interceptors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check interceptors.partialProperty.appliedElements existence propertyValues property
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("interceptors.partialProperty.propertyValues.filename");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The interceptors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check interceptors.partialProperty.appliedElements value references existing elements
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("interceptors.partialProperty.appliedElements.filename", "interceptorFake");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The interceptors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check interceptors.partialProperty.propertyValues empty
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("interceptors.partialProperty.propertyValues.filename","");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The interceptors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check interceptors.partialProperty.propertyValues existence appliedElements property
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("interceptors.partialProperty.appliedElements.filename");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The interceptors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check interceptors.partialProperty.propertyValues correct dimension
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("interceptors.partialProperty.propertyValues.filename", "value1;value2");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The interceptors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check interceptors.partialProperty.comment existence appliedElements property
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("interceptors.partialProperty.appliedElements.filename");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The interceptors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check interceptors.partialProperty.comment correct dimension
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("interceptors.partialProperty.comment.filename", "Item 1 Comment;Item 2 Comment");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The interceptors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check unknown type property
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("interceptors.partialProperty.unknown", "Unknown value");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(configurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The interceptors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

        } catch (Exception e) {
            Assert.fail("An error has occurred [testCheckPropertiesFilePartialPropertiesInterceptorsCheck] method");
            logger.error("An error has occurred [testCheckPropertiesFilePartialPropertiesInterceptorsCheck] method", e);
        }
    }



    @Test
    public void testValidateConfiguration() {

        boolean isPropertiesFileOK;

        try {

            //Check validation OK
            loadPropertiesFile();
            resetConfigurationValidator();
            configurationValidator.validateConfiguration();
            isPropertiesFileOK = configurationValidator.isPropertiesFileOK();
            Assert.assertTrue("The validation of the configuration is not correct", isPropertiesFileOK);
 

            //Check property unknown
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("unknown.property", "Unknown value");
            configurationValidator.validateConfiguration();
            isPropertiesFileOK = configurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the configuration is not correct", isPropertiesFileOK);


            //Check agents.list property existence
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("agents.list");
            configurationValidator.validateConfiguration();
            isPropertiesFileOK = configurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the configuration is not correct", isPropertiesFileOK);


            //Check agents.list property empty
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("agents.list", "");
            configurationValidator.validateConfiguration();
            isPropertiesFileOK = configurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the configuration is not correct", isPropertiesFileOK);


            //Check sources.list error
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("sources.list.agent1");
            configurationValidator.validateConfiguration();
            isPropertiesFileOK = configurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the configuration is not correct", isPropertiesFileOK);


            //Check channels.list error
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("channels.list.agent1");
            configurationValidator.validateConfiguration();
            isPropertiesFileOK = configurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the configuration is not correct", isPropertiesFileOK);


            //Check sinks.list error
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("sinks.list.agent1");
            configurationValidator.validateConfiguration();
            isPropertiesFileOK = configurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the configuration is not correct", isPropertiesFileOK);


            //Check groups.list error
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("groups.list.agent1.GROUP_1_1");
            configurationValidator.validateConfiguration();
            isPropertiesFileOK = configurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the configuration is not correct", isPropertiesFileOK);


            //Check interceptors.list error
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("interceptors.list.source1_4", "interceptor1_4");
            configurationValidator.validateConfiguration();
            isPropertiesFileOK = configurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the configuration is not correct", isPropertiesFileOK);


            //Check sources.commonProperty error
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("sources.commonProperty.type");
            configurationValidator.validateConfiguration();
            isPropertiesFileOK = configurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the configuration is not correct", isPropertiesFileOK);


            //Check sources.partialProperty error
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("sources.partialProperty.propertyValues.topic");
            configurationValidator.validateConfiguration();
            isPropertiesFileOK = configurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the configuration is not correct", isPropertiesFileOK);


            //Check interceptors.commonProperty error
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("interceptors.commonProperty.serializer.type");
            configurationValidator.validateConfiguration();
            isPropertiesFileOK = configurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the configuration is not correct", isPropertiesFileOK);


            //Check interceptors.partialProperty error
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("interceptors.partialProperty.appliedElements.filename");
            configurationValidator.validateConfiguration();
            isPropertiesFileOK = configurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the configuration is not correct", isPropertiesFileOK);
 

            //Check channels.commonProperty error
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("channels.commonProperty.type");
            configurationValidator.validateConfiguration();
            isPropertiesFileOK = configurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the configuration is not correct", isPropertiesFileOK);
 

            //Check channels.partialProperty error
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("channels.partialProperty.appliedElements.type");
            configurationValidator.validateConfiguration();
            isPropertiesFileOK = configurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the configuration is not correct", isPropertiesFileOK);
 

            //Check sinks.commonProperty error
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("sinks.commonProperty.type");
            configurationValidator.validateConfiguration();
            isPropertiesFileOK = configurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the configuration is not correct", isPropertiesFileOK);


            //Check sinks.partialProperty error
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("sinks.partialProperty.appliedElements.hdfs.filePrefix");
            configurationValidator.validateConfiguration();
            isPropertiesFileOK = configurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the configuration is not correct", isPropertiesFileOK);

        } catch (Exception e) {
            Assert.fail("An error has occurred [testCheckPropertiesFilePartialPropertiesInterceptorsCheck] method");
            logger.error("An error has occurred [testCheckPropertiesFilePartialPropertiesInterceptorsCheck] method", e);
        }
    }


}
