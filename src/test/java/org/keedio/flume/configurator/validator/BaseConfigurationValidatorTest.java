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

public class BaseConfigurationValidatorTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BaseConfigurationValidatorTest.class);


    private static final String AGENT1_NAME = "agent1";
    private static final String AGENT2_NAME = "agent2";
    private static final String AGENT3_NAME = "agent3";

    private static final String DEFAULT_SEPARATOR = ";";

    private static Properties baseConfigurationProperties = new Properties();
    private static List<String> agentList = new ArrayList<>();

    private static BaseConfigurationValidator baseConfigurationValidator = new BaseConfigurationValidator(baseConfigurationProperties, DEFAULT_SEPARATOR);


    @Rule public TestName testName = new TestName();

    @BeforeClass
    public static void loadPropertiesFile() throws IOException {
        FileInputStream fis = new FileInputStream("src/test/resources/FlumeConfigurationExample.properties");
        baseConfigurationProperties.clear();
        baseConfigurationProperties.load(fis);
        agentList = new ArrayList<>();
        agentList.add(AGENT1_NAME);
        agentList.add(AGENT2_NAME);
        agentList.add(AGENT3_NAME);


    }


    @Before
    public void resetConfigurationValidator() throws IOException {
        baseConfigurationValidator = new BaseConfigurationValidator(baseConfigurationProperties, DEFAULT_SEPARATOR);
    }


    private void showCheckErrors() {
        logger.error("[Test " + testName.getMethodName() + "] " + baseConfigurationValidator.getSbCheckErrors().toString());
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

            Method checkPropertiesFileSourcesChannelsSinksListMethod = BaseConfigurationValidator.class.getDeclaredMethod("checkPropertiesFileSourcesChannelsSinksList", args1);
            checkPropertiesFileSourcesChannelsSinksListMethod.setAccessible(true);


            //Check sources OK
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertTrue("The sources validation is not correct", isPropertiesFileOK);

            //Check sources without properties
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("sources.list.agent1");
            baseConfigurationProperties.remove("sources.list.agent2");
            baseConfigurationProperties.remove("sources.list.agent3");
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check agent without sources.list
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("sources.list.agent1");
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.list empty
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sources.list.agent1","");
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check non existing agent
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sources.list.agent4","source4");
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
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

            Method checkPropertiesFileSourcesChannelsSinksListMethod = BaseConfigurationValidator.class.getDeclaredMethod("checkPropertiesFileSourcesChannelsSinksList", args1);
            checkPropertiesFileSourcesChannelsSinksListMethod.setAccessible(true);


            //Check channels OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertTrue("The channels validation is not correct", isPropertiesFileOK);


            //Check channels without properties
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("channels.list.agent1");
            baseConfigurationProperties.remove("channels.list.agent2");
            baseConfigurationProperties.remove("channels.list.agent3");
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The channels validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check agent without channels.list
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("channels.list.agent1");
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The channels validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check channels.list empty
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("channels.list.agent1","");
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The channels validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check non existing agent
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("channels.list.agent4","channel4");
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
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

            Method checkPropertiesFileSourcesChannelsSinksListMethod = BaseConfigurationValidator.class.getDeclaredMethod("checkPropertiesFileSourcesChannelsSinksList", args1);
            checkPropertiesFileSourcesChannelsSinksListMethod.setAccessible(true);


            //Check sinks OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertTrue("The sinks validation is not correct", isPropertiesFileOK);


            //Check sinks without properties
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("sinks.list.agent1");
            baseConfigurationProperties.remove("sinks.list.agent2");
            baseConfigurationProperties.remove("sinks.list.agent3");
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check agent without sinks.list
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("sinks.list.agent1");
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.list empty
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinks.list.agent1","");
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check non existing agent
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinks.list.agent4","channel4");
            isPropertiesFileOK = (boolean) checkPropertiesFileSourcesChannelsSinksListMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks validation is not correct", isPropertiesFileOK);
            showCheckErrors();

        } catch (Exception e) {
            Assert.fail("An error has occurred [testCheckPropertiesFileSourcesChannelsSinksListSinksCheck] method");
            logger.error("An error has occurred [testCheckPropertiesFileSourcesChannelsSinksListSinksCheck] method", e);
        }
    }


    @Test
    public void testCheckPropertiesFileSinkGroupsList() {

        boolean isPropertiesFileOK;

        try {

            //Is a private method. Access by reflection
            Class<?>[] args1 = new Class[1];
            args1[0] = List.class;

            Method checkPropertiesFileSinkGroupsListMethod = BaseConfigurationValidator.class.getDeclaredMethod("checkPropertiesFileSinkGroupsList", args1);
            checkPropertiesFileSinkGroupsListMethod.setAccessible(true);


            //Check sinkgroups OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFileSinkGroupsListMethod.invoke(baseConfigurationValidator, agentList);
            Assert.assertTrue("The sinkgroups validation is not correct", isPropertiesFileOK);

            //Check sinks.list empty
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinkgroups.list.agent1","");
            isPropertiesFileOK = (boolean) checkPropertiesFileSinkGroupsListMethod.invoke(baseConfigurationValidator, agentList);
            Assert.assertFalse("The sinks sinkgroups is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check non existing agent
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinkgroups.list.agent4","g4");
            isPropertiesFileOK = (boolean) checkPropertiesFileSinkGroupsListMethod.invoke(baseConfigurationValidator, agentList);
            Assert.assertFalse("The sinks validation is not correct", isPropertiesFileOK);
            showCheckErrors();

        } catch (Exception e) {
            Assert.fail("An error has occurred [testCheckPropertiesFileSinkGroupsList] method");
            logger.error("An error has occurred [testCheckPropertiesFileSinkGroupsList] method", e);
        }
    }



    @Test
    public void testCheckPropertiesFileGroupsList() {

        try {

            //Is a private method. Access by reflection
            Class<?>[] args1 = new Class[1];
            args1[0] = List.class;

            Method checkPropertiesFileGroupsListMethod = BaseConfigurationValidator.class.getDeclaredMethod("checkPropertiesFileGroupsList", args1);
            checkPropertiesFileGroupsListMethod.setAccessible(true);

            boolean isPropertiesFileOK;

            //Check groups OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFileGroupsListMethod.invoke(baseConfigurationValidator, agentList);
            Assert.assertTrue("The groups validation is not correct", isPropertiesFileOK);


            //Check groups without properties
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("groups.list.agent1.GROUP_1_1");
            baseConfigurationProperties.remove("groups.list.agent1.GROUP_1_2");
            baseConfigurationProperties.remove("groups.list.agent1.GROUP_1_3");
            baseConfigurationProperties.remove("groups.list.agent2.GROUP_2_1");
            baseConfigurationProperties.remove("groups.list.agent2.GROUP_2_2");
            baseConfigurationProperties.remove("groups.list.agent2.GROUP_2_3");
            baseConfigurationProperties.remove("groups.list.agent3.GROUP_3_1");
            baseConfigurationProperties.remove("groups.list.agent3.GROUP_3_2");
            baseConfigurationProperties.remove("groups.list.agent3.GROUP_3_3");
            isPropertiesFileOK = (boolean) checkPropertiesFileGroupsListMethod.invoke(baseConfigurationValidator, agentList);
            Assert.assertFalse("The groups validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check groups.list empty
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("groups.list.agent1.GROUP_1_1","");
            isPropertiesFileOK = (boolean) checkPropertiesFileGroupsListMethod.invoke(baseConfigurationValidator, agentList);
            Assert.assertFalse("The groups validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check non existing agent
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("groups.list.agent4.GROUP_4_1","group4");
            isPropertiesFileOK = (boolean) checkPropertiesFileGroupsListMethod.invoke(baseConfigurationValidator, agentList);
            Assert.assertFalse("The groups validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check value property belong to agent of the group
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("groups.list.agent1.GROUP_1_1","source1_1;channel3_1;sink1_1");
            isPropertiesFileOK = (boolean) checkPropertiesFileGroupsListMethod.invoke(baseConfigurationValidator, agentList);
            Assert.assertFalse("The groups validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check element belongs more than one group of the same agent
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("groups.list.agent1.GROUP_1_1","source1_1;channel1_1;sink1_1");
            baseConfigurationProperties.put("groups.list.agent1.GROUP_1_2","source1_2;channel1_1;sink1_2");
            isPropertiesFileOK = (boolean) checkPropertiesFileGroupsListMethod.invoke(baseConfigurationValidator, agentList);
            Assert.assertFalse("The groups validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check all elements of the agent belong to one group
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("groups.list.agent1.GROUP_1_1","source1_1;channel1_1");
            isPropertiesFileOK = (boolean) checkPropertiesFileGroupsListMethod.invoke(baseConfigurationValidator, agentList);
            Assert.assertFalse("The groups validation is not correct", isPropertiesFileOK);
            showCheckErrors();

        } catch (Exception e) {
            Assert.fail("An error has occurred [testCheckPropertiesFileGroupsList] method");
            logger.error("An error has occurred [testCheckPropertiesFileGroupsList] method", e);
        }
    }


    @Test
    public void testCheckPropertiesFileSelectorsList() {

        try {

            //Is a private method. Access by reflection
            Class<?>[] args1 = new Class[1];
            args1[0] = List.class;

            Method checkPropertiesFileSelectorsListMethod = BaseConfigurationValidator.class.getDeclaredMethod("checkPropertiesFileSelectorsList", args1);
            checkPropertiesFileSelectorsListMethod.setAccessible(true);

            boolean isPropertiesFileOK;

            //Check interceptors OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFileSelectorsListMethod.invoke(baseConfigurationValidator, agentList);
            Assert.assertTrue("The selectors validation is not correct", isPropertiesFileOK);


            //Check sourcesWithSelector.list empty
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sourcesWithSelector.list.agent1","");
            isPropertiesFileOK = (boolean) checkPropertiesFileSelectorsListMethod.invoke(baseConfigurationValidator, agentList);
            Assert.assertFalse("The selectors validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check non exist agent
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sourcesWithSelector.list.agent4","source4_1");
            isPropertiesFileOK = (boolean) checkPropertiesFileSelectorsListMethod.invoke(baseConfigurationValidator, agentList);
            Assert.assertFalse("The selectors validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check source not belong to agent
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sourcesWithSelector.list.agent1","source2_2");
            isPropertiesFileOK = (boolean) checkPropertiesFileSelectorsListMethod.invoke(baseConfigurationValidator, agentList);
            Assert.assertFalse("The selectors validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check source without multiple channels
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sourcesWithSelector.list.agent1","source1_2");
            isPropertiesFileOK = (boolean) checkPropertiesFileSelectorsListMethod.invoke(baseConfigurationValidator, agentList);
            Assert.assertFalse("The selectors validation is not correct", isPropertiesFileOK);
            showCheckErrors();


        } catch (Exception e) {
            Assert.fail("An error has occurred [testCheckPropertiesFileSelectorsList] method");
            logger.error("An error has occurred [testCheckPropertiesFileSelectorsList] method", e);
        }
    }


    @Test
    public void testCheckPropertiesFileInterceptorsList() {

        try {

            //Is a private method. Access by reflection
            Class<?>[] args1 = new Class[1];
            args1[0] = List.class;

            Method checkPropertiesFileInterceptorsListMethod = BaseConfigurationValidator.class.getDeclaredMethod("checkPropertiesFileInterceptorsList", args1);
            checkPropertiesFileInterceptorsListMethod.setAccessible(true);

            boolean isPropertiesFileOK;


            //Check interceptors OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFileInterceptorsListMethod.invoke(baseConfigurationValidator, agentList);
            Assert.assertTrue("The interceptors validation is not correct", isPropertiesFileOK);


            //Check interceptors.list empty
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("interceptors.list.source1_1","");
            isPropertiesFileOK = (boolean) checkPropertiesFileInterceptorsListMethod.invoke(baseConfigurationValidator, agentList);
            Assert.assertFalse("The interceptors validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check Non existing source of the interceptor
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("interceptors.list.source4_1","interceptor4_1");
            isPropertiesFileOK = (boolean) checkPropertiesFileInterceptorsListMethod.invoke(baseConfigurationValidator, agentList);
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

            Method checkPropertiesFileCommonPropertiesMethod = BaseConfigurationValidator.class.getDeclaredMethod("checkPropertiesFileCommonProperties", args1);
            checkPropertiesFileCommonPropertiesMethod.setAccessible(true);


            //Check sources common properties OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertTrue("The sources common properties validation is not correct", isPropertiesFileOK);


            //Check sources.commonProperty.comment property references a property that not exists
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sources.commonProperty.comment.fakeproperty","Comentario .fakeproperty");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources common properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.commonProperty empty
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sources.commonProperty.type","");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources common properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.commonProperty.channels references channels that don't belong to any agent
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sources.commonProperty.channels","fakeChannel");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources common properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.commonProperty.channels doesn't references all channels of all agents (WARNING)
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sources.commonProperty.channels","channel1_1");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
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

            Method checkPropertiesFileCommonPropertiesMethod = BaseConfigurationValidator.class.getDeclaredMethod("checkPropertiesFileCommonProperties", args1);
            checkPropertiesFileCommonPropertiesMethod.setAccessible(true);


            //Check channels common properties OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertTrue("The channels common properties validation is not correct", isPropertiesFileOK);


            //Check channels.commonProperty.comment property references a property that not exists
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("channels.commonProperty.comment.fakeproperty","Comentario .fakeproperty");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The channels common properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check channels.commonProperty empty
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("channels.commonProperty.type","");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
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

            Method checkPropertiesFileCommonPropertiesMethod = BaseConfigurationValidator.class.getDeclaredMethod("checkPropertiesFileCommonProperties", args1);
            checkPropertiesFileCommonPropertiesMethod.setAccessible(true);


            //Check channels common properties OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertTrue("The sinks common properties validation is not correct", isPropertiesFileOK);


            //Check sinks.commonProperty.comment property references a property that not exists
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinks.commonProperty.comment.fakeproperty","Comentario .fakeproperty");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks common properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.commonProperty empty
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinks.commonProperty.type","");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks common properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.commonProperty.channel references channel that don't belong to any agent
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinks.commonProperty.channel","fakeChannel");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks common properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.commonProperty.channel doesn't references all channels of all agents (WARNING)
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinks.commonProperty.channel","channel1_1");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertTrue("The sinks common properties validation is not correct", isPropertiesFileOK);


        } catch (Exception e) {
            Assert.fail("An error has occurred [testCheckPropertiesFileCommonPropertiesSinksCheck] method");
            logger.error("An error has occurred [testCheckPropertiesFileCommonPropertiesSinksCheck] method", e);
        }
    }


    @Test
    public void testCheckPropertiesFileCommonPropertiesSinkgroupsCheck() {

        String prefixProperty = FlumeConfiguratorConstants.SINKGROUPS_COMMON_PROPERTY_PROPERTIES_PREFIX;
        boolean isPropertiesFileOK;

        try {

            //Is a private method. Access by reflection
            Class<?>[] args1 = new Class[2];
            args1[0] = String.class;
            args1[1] = List.class;

            Method checkPropertiesFileCommonPropertiesMethod = BaseConfigurationValidator.class.getDeclaredMethod("checkPropertiesFileCommonProperties", args1);
            checkPropertiesFileCommonPropertiesMethod.setAccessible(true);


            //Check sinkgroups common properties OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertTrue("The sinkgroups common properties validation is not correct", isPropertiesFileOK);


            //Check sinkgroups.commonProperty.comment property references a property that not exists
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinkgroups.commonProperty.comment.fakeproperty","Comentario .fakeproperty");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinkgroups common properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinkgroups.commonProperty empty
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinkgroups.commonProperty.processor.type","");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinkgroups common properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinkgroups.commonProperty.sinks references sink that don't belong to any agent
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinkgroups.commonProperty.sinks","fakeSink");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinkgroups common properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

        } catch (Exception e) {
            Assert.fail("An error has occurred [testCheckPropertiesFileCommonPropertiesSinkgroupsCheck] method");
            logger.error("An error has occurred [testCheckPropertiesFileCommonPropertiesSinkgroupsCheck] method", e);
        }
    }


    @Test
    public void testCheckPropertiesFileCommonPropertiesSelectorsCheck() {

        String prefixProperty = FlumeConfiguratorConstants.SELECTORS_COMMON_PROPERTY_PROPERTIES_PREFIX;
        boolean isPropertiesFileOK;

        try {

            //Is a private method. Access by reflection
            Class<?>[] args1 = new Class[2];
            args1[0] = String.class;
            args1[1] = List.class;

            Method checkPropertiesFileCommonPropertiesMethod = BaseConfigurationValidator.class.getDeclaredMethod("checkPropertiesFileCommonProperties", args1);
            checkPropertiesFileCommonPropertiesMethod.setAccessible(true);


            //Check selectors common properties OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertTrue("The selectors common properties validation is not correct", isPropertiesFileOK);


            //Check selectors.commonProperty.comment property references a property that not exists
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("selectors.commonProperty.comment.fakeproperty","Comentario .fakeproperty");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The selectors common properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check selectors.commonProperty empty
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("selectors.commonProperty.type","");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The selectors common properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check selectors.commonProperty.mapping... references channel that don't belong to any agent
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("selectors.commonProperty.mapping.XX","fakeChannel");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The selectors common properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check selectors.commonProperty.optional... references channel that don't belong to any agent
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("selectors.commonProperty.optional.XX","fakeChannel");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The selectors common properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check selectors.commonProperty.default... references channel that don't belong to any agent
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("selectors.commonProperty.default","fakeChannel");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The selectors common properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check selectors.commonProperty.<propertyReferencesChannel>... references channel(s) that don't belong to the channels of all sources with selector
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("selectors.commonProperty.default","channel1_1");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The selectors common properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


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

            Method checkPropertiesFileCommonPropertiesMethod = BaseConfigurationValidator.class.getDeclaredMethod("checkPropertiesFileCommonProperties", args1);
            checkPropertiesFileCommonPropertiesMethod.setAccessible(true);


            //Check interceptors common properties OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertTrue("The interceptors common properties validation is not correct", isPropertiesFileOK);


            //Check interceptors.commonProperty.comment property references a property that not exists
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("interceptors.commonProperty.comment.fakeproperty","Comentario .fakeproperty");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The interceptors common properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check interceptors.commonProperty empty
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("interceptors.commonProperty.type","");
            isPropertiesFileOK = (boolean) checkPropertiesFileCommonPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
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

            Method checkPropertiesFilePartialPropertiesMethod = BaseConfigurationValidator.class.getDeclaredMethod("checkPropertiesFilePartialProperties", args1);
            checkPropertiesFilePartialPropertiesMethod.setAccessible(true);


            //Check sources partial properties OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertTrue("The sources partial properties validation is not correct", isPropertiesFileOK);


            //Check sources.partialProperty.appliedElements empty
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sources.partialProperty.appliedElements.topic","");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.partialProperty.appliedElements existence propertyValues property
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("sources.partialProperty.propertyValues.topic");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.partialProperty.appliedElements value references existing elements
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sources.partialProperty.appliedElements.topic", "sourceFake;source1_2");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.partialProperty.propertyValues empty
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sources.partialProperty.propertyValues.topic","");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.partialProperty.propertyValues existence appliedElements property
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("sources.partialProperty.appliedElements.topic");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.partialProperty.propertyValues correct dimension
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sources.partialProperty.propertyValues.topic", "topic1;topic2;topic3");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.partialProperty.propertyValues.channels (1 value) non existing channel
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sources.partialProperty.propertyValues.channels", "channelFake");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.partialProperty.propertyValues.channels (1 value) channel doesn't belong to the agent of the source
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sources.partialProperty.propertyValues.channels", "channel3_1");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.partialProperty.propertyValues.channels (n values) non existing channel
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sources.partialProperty.propertyValues.channels", "channel1_1 channelFake channel1_3;channel1_2;channel1_1 channel1_2 channel1_3;channel2_1;channel2_2;channel2_3;channel3_1;channel3_2 channel3_3;channel3_3");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.partialProperty.propertyValues.channels (n value) channel doesn't belong to the agent of the source
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sources.partialProperty.propertyValues.channels", "channel1_1 channel3_2 channel1_3;channel1_2;channel1_1 channel1_2 channel1_3;channel2_1;channel2_2;channel2_3;channel3_1;channel3_2 channel3_3;channel3_3");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.partialProperty.comment existence appliedElements property
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("sources.partialProperty.appliedElements.topic");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sources.partialProperty.comment correct dimension
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sources.partialProperty.comment.topic", "Item 1 Comment;Item 2 Comment;Item 3 Comment");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sources partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check unknown type property
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sources.partialProperty.unknown", "Unknown value");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
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

            Method checkPropertiesFilePartialPropertiesMethod = BaseConfigurationValidator.class.getDeclaredMethod("checkPropertiesFilePartialProperties", args1);
            checkPropertiesFilePartialPropertiesMethod.setAccessible(true);


            //Check channels partial properties OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertTrue("The channels partial properties validation is not correct", isPropertiesFileOK);


            //Check channels.partialProperty.appliedElements empty
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("channels.partialProperty.appliedElements.type","");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The channels partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check channels.partialProperty.appliedElements existence propertyValues property
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("channels.partialProperty.propertyValues.type");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The channels partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check channels.partialProperty.appliedElements value references existing elements
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("channels.partialProperty.appliedElements.type", "channelFake");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The channels partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check channels.partialProperty.propertyValues empty
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("channels.partialProperty.propertyValues.type","");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The channels partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check channels.partialProperty.propertyValues existence appliedElements property
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("channels.partialProperty.appliedElements.type");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The channels partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check channels.partialProperty.propertyValues correct dimension
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("channels.partialProperty.propertyValues.type", "disk;disk;disk");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The channels partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check channels.partialProperty.comment existence appliedElements property
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("channels.partialProperty.appliedElements.type");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The channels partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check channels.partialProperty.comment correct dimension
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("channels.partialProperty.comment.type", "Item 1 Comment;Item 2 Comment;Item 3 Comment");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The channels partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check unknown type property
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("channels.partialProperty.unknown", "Unknown value");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
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

            Method checkPropertiesFilePartialPropertiesMethod = BaseConfigurationValidator.class.getDeclaredMethod("checkPropertiesFilePartialProperties", args1);
            checkPropertiesFilePartialPropertiesMethod.setAccessible(true);


            //Check sinks partial properties OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertTrue("The sinks partial properties validation is not correct", isPropertiesFileOK);


            //Check sinks.partialProperty.appliedElements empty
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinks.partialProperty.appliedElements.hdfs.filePrefix","");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.partialProperty.appliedElements existence propertyValues property
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("sinks.partialProperty.propertyValues.hdfs.filePrefix");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.partialProperty.appliedElements value references existing elements
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinks.partialProperty.appliedElements.hdfs.filePrefix", "sinkFake");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.partialProperty.propertyValues empty
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinks.partialProperty.propertyValues.hdfs.filePrefix","");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.partialProperty.propertyValues existence appliedElements property
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("sinks.partialProperty.appliedElements.hdfs.filePrefix");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.partialProperty.propertyValues correct dimension
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinks.partialProperty.propertyValues.hdfs.filePrefix", "pnl1;pnl2");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.partialProperty.propertyValues.channels (1 value) non existing channel
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinks.partialProperty.propertyValues.channel", "channelFake");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.partialProperty.propertyValues.channels (1 value) channel belongs agent of the source
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinks.partialProperty.propertyValues.channel", "channel3_1");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.partialProperty.propertyValues.channels (n values) non existing channel
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinks.partialProperty.propertyValues.channel", " channel1_1;channel1_2;channelFake");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.partialProperty.propertyValues.channels (n values) channel belongs agent of the source
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinks.partialProperty.propertyValues.channel", "channel1_1;channel1_2;channel3_1");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.partialProperty.comment existence appliedElements property
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("sinks.partialProperty.appliedElements.hdfs.filePrefix");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinks.partialProperty.comment correct dimension
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinks.partialProperty.comment.hdfs.filePrefix", "Item 1 Comment;Item 2 Comment");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check unknown type property
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinks.partialProperty.unknown", "Unknown value");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinks partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

        } catch (Exception e) {
            Assert.fail("An error has occurred [testCheckPropertiesFilePartialPropertiesSinksCheck] method");
            logger.error("An error has occurred [testCheckPropertiesFilePartialPropertiesSinksCheck] method", e);
        }
    }



    @Test
    public void testCheckPropertiesFilePartialPropertiesSinkgroupsCheck() {

        String prefixProperty = FlumeConfiguratorConstants.SINKGROUPS_PARTIAL_PROPERTY_PROPERTIES_PREFIX;
        boolean isPropertiesFileOK;

        try {

            //Is a private method. Access by reflection
            Class<?>[] args1 = new Class[2];
            args1[0] = String.class;
            args1[1] = List.class;

            Method checkPropertiesFilePartialPropertiesMethod = BaseConfigurationValidator.class.getDeclaredMethod("checkPropertiesFilePartialProperties", args1);
            checkPropertiesFilePartialPropertiesMethod.setAccessible(true);


            //Check sinkgroups partial properties OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertTrue("The sinkgroups partial properties validation is not correct", isPropertiesFileOK);


            //Check sinkgroups.partialProperty.appliedElements empty
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinkgroups.partialProperty.appliedElements.processor.type","");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinkgroups partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinkgroups.partialProperty.appliedElements existence propertyValues property
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("sinkgroups.partialProperty.propertyValues.processor.type");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinkgroups partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinkgroups.partialProperty.appliedElements value references existing elements
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinkgroups.partialProperty.appliedElements.processor.type", "sinkgroupFake");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinkgroups partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinkgroups.partialProperty.propertyValues empty
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinkgroups.partialProperty.propertyValues.processor.type","");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinkgroups partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinkgroups.partialProperty.propertyValues existence appliedElements property
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("sinkgroups.partialProperty.appliedElements.processor.type");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinkgroups partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinkgroups.partialProperty.propertyValues correct dimension
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinkgroups.partialProperty.propertyValues.processor.type", "load_balance;load_balance;load_balance");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinkgroups partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinkgroups.partialProperty.propertyValues.sinks (1 value) non existing sink
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinkgroups.partialProperty.propertyValues.sinks", "sinkFake");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinkgroups partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinkgroups.partialProperty.propertyValues.sinks (1 value) sink doesn't belong to the agent of the sinkgroup
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinkgroups.partialProperty.propertyValues.sinks", "sink1_1");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinkgroups partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinkgroups.partialProperty.propertyValues.sinks (n values) non existing sink
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinkgroups.partialProperty.propertyValues.sinks", "sink1_1 sinkFake;sink2_1 sink2_2");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinkgroups partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinkgroups.partialProperty.propertyValues.sinks (n value) sink doesn't belong to the agent of the sinkgroup
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinkgroups.partialProperty.propertyValues.sinks", "sink1_1 sink2_1;sink2_1 sink2_2");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinkgroups partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinkgroups.partialProperty.comment existence appliedElements property
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("sinkgroups.partialProperty.appliedElements.processor.type");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinkgroups partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check sinkgroups.partialProperty.comment correct dimension
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinkgroups.partialProperty.comment.processor.type", "Item 1 Comment;Item 2 Comment;Item 3 Comment");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinkgroups partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check unknown type property
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sinkgroups.partialProperty.unknown", "Unknown value");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The sinkgroups partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

        } catch (Exception e) {
            Assert.fail("An error has occurred [testCheckPropertiesFilePartialPropertiesSinkgroupsCheck] method");
            logger.error("An error has occurred [testCheckPropertiesFilePartialPropertiesSinkgroupsCheck] method", e);
        }
    }



    @Test
    public void testCheckPropertiesFilePartialPropertiesSelectorsCheck() {

        String prefixProperty = FlumeConfiguratorConstants.SELECTORS_PARTIAL_PROPERTY_PROPERTIES_PREFIX;
        boolean isPropertiesFileOK;

        try {

            //Is a private method. Access by reflection
            Class<?>[] args1 = new Class[2];
            args1[0] = String.class;
            args1[1] = List.class;

            Method checkPropertiesFilePartialPropertiesMethod = BaseConfigurationValidator.class.getDeclaredMethod("checkPropertiesFilePartialProperties", args1);
            checkPropertiesFilePartialPropertiesMethod.setAccessible(true);


            //Check selectors partial properties OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertTrue("The selectors partial properties validation is not correct", isPropertiesFileOK);


            //Check selectors.partialProperty.appliedElements empty
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("selectors.partialProperty.appliedElements.default","");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The selectors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check selectors.partialProperty.appliedElements existence propertyValues property
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("selectors.partialProperty.propertyValues.default");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The selectors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check selectors.partialProperty.appliedElements value references existing elements
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("selectors.partialProperty.appliedElements.default", "source1_1;sourceFake;source3_2");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The selectors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check selectors.partialProperty.propertyValues empty
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("selectors.partialProperty.propertyValues.default","");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The selectors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check selectors.partialProperty.propertyValues existence appliedElements property
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("selectors.partialProperty.appliedElements.default");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The selectors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check selectors.partialProperty.propertyValues correct dimension
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("selectors.partialProperty.propertyValues.default", "channel1_1;channel1_3;channel3_2;channel3_3");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The selectors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check selectors.partialProperty.propertyValues.<propertyReferencesChannel> (1 value) non existing channel
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("selectors.partialProperty.propertyValues.mapping.CA", "channelFake");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The selectors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check selectors.partialProperty.propertyValues.<propertyReferencesChannel> (1 value) channel doesn't belong to the agent of the source selector
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("selectors.partialProperty.propertyValues.mapping.CA", "channel2_1");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The selectors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check selectors.partialProperty.propertyValues.<propertyReferencesChannel> (1 value) channel doesn't belong to the channels of the source selector
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("selectors.partialProperty.appliedElements.mapping.CA", "source3_2");
            baseConfigurationProperties.put("selectors.partialProperty.propertyValues.mapping.CA", "channel3_1");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The selectors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check selectors.partialProperty.propertyValues.<propertyReferencesChannel> (n values) non existing channel
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("selectors.partialProperty.propertyValues.mapping.CA", "channel1_1;channelFake;channel3_2");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The selectors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check selectors.partialProperty.propertyValues.<propertyReferencesChannel> (n value) sink doesn't belong to the agent  of the source selector
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("selectors.partialProperty.propertyValues.mapping.CA", "channel1_1;channel2_2;channel3_2");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The selectors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check selectors.partialProperty.propertyValues.<propertyReferencesChannel> (n value) channel doesn't belong to the channels of the source selector
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("selectors.partialProperty.appliedElements.mapping.CA", "source1_1;source1_3;source3_2");
            baseConfigurationProperties.put("selectors.partialProperty.propertyValues.mapping.CA", "channel1_1;channel1_1;channel3_1");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The selectors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check selectors.partialProperty.comment existence appliedElements property
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("selectors.partialProperty.appliedElements.mapping.CA");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The selectors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check selectors.partialProperty.comment correct dimension
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("selectors.partialProperty.comment.mapping.CA", "Item 1 Comment;Item 2 Comment");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The selectors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check unknown type property
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("selectors.partialProperty.unknown", "Unknown value");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The selectors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

        } catch (Exception e) {
            Assert.fail("An error has occurred [testCheckPropertiesFilePartialPropertiesSelectorsCheck] method");
            logger.error("An error has occurred [testCheckPropertiesFilePartialPropertiesSelectorsCheck] method", e);
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

            Method checkPropertiesFilePartialPropertiesMethod = BaseConfigurationValidator.class.getDeclaredMethod("checkPropertiesFilePartialProperties", args1);
            checkPropertiesFilePartialPropertiesMethod.setAccessible(true);


            //Check interceptors partial properties OK
            loadPropertiesFile();
            resetConfigurationValidator();
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertTrue("The interceptors partial properties validation is not correct", isPropertiesFileOK);


            //Check interceptors.partialProperty.appliedElements empty
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("interceptors.partialProperty.appliedElements.filename","");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The interceptors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check interceptors.partialProperty.appliedElements existence propertyValues property
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("interceptors.partialProperty.propertyValues.filename");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The interceptors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check interceptors.partialProperty.appliedElements value references existing elements
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("interceptors.partialProperty.appliedElements.filename", "interceptorFake");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The interceptors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check interceptors.partialProperty.propertyValues empty
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("interceptors.partialProperty.propertyValues.filename","");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The interceptors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check interceptors.partialProperty.propertyValues existence appliedElements property
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("interceptors.partialProperty.appliedElements.filename");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The interceptors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check interceptors.partialProperty.propertyValues correct dimension
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("interceptors.partialProperty.propertyValues.filename", "value1;value2");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The interceptors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check interceptors.partialProperty.comment existence appliedElements property
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("interceptors.partialProperty.appliedElements.filename");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The interceptors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check interceptors.partialProperty.comment correct dimension
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("interceptors.partialProperty.comment.filename", "Item 1 Comment;Item 2 Comment");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The interceptors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();


            //Check unknown type property
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("interceptors.partialProperty.unknown", "Unknown value");
            isPropertiesFileOK = (boolean) checkPropertiesFilePartialPropertiesMethod.invoke(baseConfigurationValidator, prefixProperty, agentList);
            Assert.assertFalse("The interceptors partial properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

        } catch (Exception e) {
            Assert.fail("An error has occurred [testCheckPropertiesFilePartialPropertiesInterceptorsCheck] method");
            logger.error("An error has occurred [testCheckPropertiesFilePartialPropertiesInterceptorsCheck] method", e);
        }
    }



    @Test
    public void testValidateBaseConfiguration() {

        boolean isPropertiesFileOK;

        try {

            //Check validation OK
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationValidator.validateBaseConfiguration();
            isPropertiesFileOK = baseConfigurationValidator.isPropertiesFileOK();
            Assert.assertTrue("The validation of the base configuration is not correct", isPropertiesFileOK);
 

            //Check property unknown
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("unknown.property", "Unknown value");
            baseConfigurationValidator.validateBaseConfiguration();
            isPropertiesFileOK = baseConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the base configuration is not correct", isPropertiesFileOK);


            //Check agents.list property existence
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("agents.list");
            baseConfigurationValidator.validateBaseConfiguration();
            isPropertiesFileOK = baseConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the base configuration is not correct", isPropertiesFileOK);


            //Check agents.list property empty
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("agents.list", "");
            baseConfigurationValidator.validateBaseConfiguration();
            isPropertiesFileOK = baseConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the base configuration is not correct", isPropertiesFileOK);


            //Check sources.list error
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("sources.list.agent1");
            baseConfigurationValidator.validateBaseConfiguration();
            isPropertiesFileOK = baseConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the base configuration is not correct", isPropertiesFileOK);


            //Check channels.list error
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("channels.list.agent1");
            baseConfigurationValidator.validateBaseConfiguration();
            isPropertiesFileOK = baseConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the base configuration is not correct", isPropertiesFileOK);


            //Check sinks.list error
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("sinks.list.agent1");
            baseConfigurationValidator.validateBaseConfiguration();
            isPropertiesFileOK = baseConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the base configuration is not correct", isPropertiesFileOK);


            //Check groups.list error
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("groups.list.agent1.GROUP_1_1");
            baseConfigurationValidator.validateBaseConfiguration();
            isPropertiesFileOK = baseConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the base configuration is not correct", isPropertiesFileOK);


            //Check sourcesWithSelector.list error
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("sourcesWithSelector.list.agent1", "source1_4");
            baseConfigurationValidator.validateBaseConfiguration();
            isPropertiesFileOK = baseConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the base configuration is not correct", isPropertiesFileOK);


            //Check interceptors.list error
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("interceptors.list.source1_4", "interceptor1_4");
            baseConfigurationValidator.validateBaseConfiguration();
            isPropertiesFileOK = baseConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the base configuration is not correct", isPropertiesFileOK);


            //Check sources.commonProperty error
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("sources.commonProperty.type");
            baseConfigurationValidator.validateBaseConfiguration();
            isPropertiesFileOK = baseConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the base configuration is not correct", isPropertiesFileOK);


            //Check sources.partialProperty error
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("sources.partialProperty.propertyValues.topic");
            baseConfigurationValidator.validateBaseConfiguration();
            isPropertiesFileOK = baseConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the base configuration is not correct", isPropertiesFileOK);


            //Check selectors.commonProperty error
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.put("selectors.commonProperty.default", "channelFake");
            baseConfigurationValidator.validateBaseConfiguration();
            isPropertiesFileOK = baseConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the base configuration is not correct", isPropertiesFileOK);


            //Check selectors.partialProperty error
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("selectors.partialProperty.propertyValues.default");
            baseConfigurationValidator.validateBaseConfiguration();
            isPropertiesFileOK = baseConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the base configuration is not correct", isPropertiesFileOK);


            //Check interceptors.commonProperty error
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("interceptors.commonProperty.serializer.type");
            baseConfigurationValidator.validateBaseConfiguration();
            isPropertiesFileOK = baseConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the base configuration is not correct", isPropertiesFileOK);


            //Check interceptors.partialProperty error
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("interceptors.partialProperty.appliedElements.filename");
            baseConfigurationValidator.validateBaseConfiguration();
            isPropertiesFileOK = baseConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the base configuration is not correct", isPropertiesFileOK);
 

            //Check channels.commonProperty error
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("channels.commonProperty.type");
            baseConfigurationValidator.validateBaseConfiguration();
            isPropertiesFileOK = baseConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the base configuration is not correct", isPropertiesFileOK);
 

            //Check channels.partialProperty error
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("channels.partialProperty.appliedElements.type");
            baseConfigurationValidator.validateBaseConfiguration();
            isPropertiesFileOK = baseConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the base configuration is not correct", isPropertiesFileOK);
 

            //Check sinks.commonProperty error
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("sinks.commonProperty.type");
            baseConfigurationValidator.validateBaseConfiguration();
            isPropertiesFileOK = baseConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the base configuration is not correct", isPropertiesFileOK);


            //Check sinks.partialProperty error
            loadPropertiesFile();
            resetConfigurationValidator();
            baseConfigurationProperties.remove("sinks.partialProperty.appliedElements.hdfs.filePrefix");
            baseConfigurationValidator.validateBaseConfiguration();
            isPropertiesFileOK = baseConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the base configuration is not correct", isPropertiesFileOK);

        } catch (Exception e) {
            Assert.fail("An error has occurred [testValidateBaseConfiguration] method");
            logger.error("An error has occurred [testValidateBaseConfiguration] method", e);
        }
    }


}
