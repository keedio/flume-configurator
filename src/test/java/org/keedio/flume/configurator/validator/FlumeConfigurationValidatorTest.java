package org.keedio.flume.configurator.validator;

import static org.keedio.flume.configurator.validator.FlumeConfigurationValidator.mapSources;
import static org.keedio.flume.configurator.validator.FlumeConfigurationValidator.mapChannels;
import static org.keedio.flume.configurator.validator.FlumeConfigurationValidator.mapSinks;
import static org.keedio.flume.configurator.validator.FlumeConfigurationValidator.mapSinkgroups;
import static org.keedio.flume.configurator.validator.FlumeConfigurationValidator.mapInterceptors;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

import org.junit.*;
import org.junit.rules.TestName;
import org.junit.runners.MethodSorters;
import org.keedio.flume.configurator.constants.FlumeConfiguratorConstants;
import org.slf4j.LoggerFactory;



@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FlumeConfigurationValidatorTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FlumeConfigurationValidatorTest.class);

    private static final String AGENT1_NAME = "agent1";

    private static Properties flumeConfigurationProperties = new Properties();
    private static List<String> agentList = new ArrayList<>();

    private static FlumeConfigurationValidator flumeConfigurationValidator = new FlumeConfigurationValidator(flumeConfigurationProperties);

    @Rule public TestName testName = new TestName();

    private static Method createInitialStructuresMethod;
    private static Method checkPropertiesFilePropertiesListMethod;
    private static Method checkPropertiesFileInterceptorListMethod;
    private static Method checkPropertiesFileElementsPropertiesMethod;
    private static Method checkPropertiesFileInterceptorsPropertiesMethod;
    private static Method checkNamespacesUniquenessMethod;


    @BeforeClass
    public static void loadPropertiesFile() throws IOException {
        FileInputStream fis = new FileInputStream("src/test/resources/FlumeProperties/nAgent/agent1_flume.properties");
        flumeConfigurationProperties.clear();
        flumeConfigurationProperties.load(fis);
        agentList = new ArrayList<>();
        agentList.add(AGENT1_NAME);
    }

    @BeforeClass
    public static void makePrivateMethodsAccesibleByReflection() {

        try {

            //createInitialStructures is a private method. Access by reflection
            Class<?>[] argsCreateInitialStructuresMethod = new Class[1];
            argsCreateInitialStructuresMethod[0] = List.class;

            createInitialStructuresMethod = FlumeConfigurationValidator.class.getDeclaredMethod("createInitialStructures", argsCreateInitialStructuresMethod);
            createInitialStructuresMethod.setAccessible(true);

            //checkPropertiesFileSourcesChannelsSinks is a private method. Access by reflection
            Class<?>[] argsCheckPropertiesFilePropertiesListMethod = new Class[3];
            argsCheckPropertiesFilePropertiesListMethod[0] = String.class;
            argsCheckPropertiesFilePropertiesListMethod[1] = List.class;
            argsCheckPropertiesFilePropertiesListMethod[2] = boolean.class;

            checkPropertiesFilePropertiesListMethod = FlumeConfigurationValidator.class.getDeclaredMethod("checkPropertiesFilePropertiesList", argsCheckPropertiesFilePropertiesListMethod);
            checkPropertiesFilePropertiesListMethod.setAccessible(true);

            //checkPropertiesFileInterceptorList is a private method. Access by reflection
            Class<?>[] argsCheckPropertiesFileInterceptorListMethod = new Class[1];
            argsCheckPropertiesFileInterceptorListMethod[0] = List.class;

            checkPropertiesFileInterceptorListMethod = FlumeConfigurationValidator.class.getDeclaredMethod("checkPropertiesFileInterceptorList", argsCheckPropertiesFileInterceptorListMethod);
            checkPropertiesFileInterceptorListMethod.setAccessible(true);

            //checkPropertiesFilePropertiesElements is a private method. Access by reflection
            Class<?>[] argsCheckPropertiesFileElementsPropertiesMethod = new Class[3];
            argsCheckPropertiesFileElementsPropertiesMethod[0] = List.class;
            argsCheckPropertiesFileElementsPropertiesMethod[1] = String.class;
            argsCheckPropertiesFileElementsPropertiesMethod[2] = Map.class;

            checkPropertiesFileElementsPropertiesMethod = FlumeConfigurationValidator.class.getDeclaredMethod("checkPropertiesFileElementsProperties", argsCheckPropertiesFileElementsPropertiesMethod);
            checkPropertiesFileElementsPropertiesMethod.setAccessible(true);

            //checkPropertiesFileInterceptorsProperties is a private method. Access by reflection
            Class<?>[] argsCheckPropertiesFileInterceptorsPropertiesMethod = new Class[1];
            argsCheckPropertiesFileInterceptorsPropertiesMethod[0] = List.class;

            checkPropertiesFileInterceptorsPropertiesMethod = FlumeConfigurationValidator.class.getDeclaredMethod("checkPropertiesFileInterceptorsProperties", argsCheckPropertiesFileInterceptorsPropertiesMethod);
            checkPropertiesFileInterceptorsPropertiesMethod.setAccessible(true);

            //checkNamespacesUniqueness is a private method. Access by reflection
            Class<?>[] argsCheckNamespacesUniquenessMethod = new Class[2];
            argsCheckNamespacesUniquenessMethod[0] = List.class;
            argsCheckNamespacesUniquenessMethod[1] = boolean.class;

            checkNamespacesUniquenessMethod = FlumeConfigurationValidator.class.getDeclaredMethod("checkNamespacesUniqueness", argsCheckNamespacesUniquenessMethod);
            checkNamespacesUniquenessMethod.setAccessible(true);

        } catch (Exception e) {
            Assert.fail("An error has occurred [@BeforeClass makePrivateMethodsAccesibleByReflection] method");
            logger.error("An error has occurred [@BeforeClass makePrivateMethodsAccesibleByReflection] method", e);
        }
    }

    @Before
    public void resetConfigurationValidator() throws IOException {
        flumeConfigurationValidator = new FlumeConfigurationValidator(flumeConfigurationProperties);
        agentList = new ArrayList<>();
        agentList.add(AGENT1_NAME);
    }


    @Before
    public void createInitialStructures() throws IOException {
        flumeConfigurationValidator.createInitialStructures(agentList);
    }

    private void showCheckErrors() {
        logger.error("[Test " + testName.getMethodName() + "] " + flumeConfigurationValidator.getSbCheckErrors().toString());
    }


    @Test
    public void test01CreateInitialStructures() {

        try {

            loadPropertiesFile();
            resetConfigurationValidator();
            Assert.assertTrue("The creation of initial structures not correct", FlumeConfigurationValidator.mapSources.isEmpty());
            Assert.assertTrue("The creation of initial structures not correct", FlumeConfigurationValidator.mapChannels.isEmpty());
            Assert.assertTrue("The creation of initial structures not correct", FlumeConfigurationValidator.mapSinks.isEmpty());
            Assert.assertTrue("The creation of initial structures not correct", FlumeConfigurationValidator.mapSinkgroups.isEmpty());
            Assert.assertTrue("The creation of initial structures not correct", FlumeConfigurationValidator.mapInterceptors.isEmpty());

            createInitialStructuresMethod.invoke(flumeConfigurationValidator, agentList);

            Assert.assertFalse("The creation of initial structures not correct", FlumeConfigurationValidator.mapSources.isEmpty());
            Assert.assertFalse("The creation of initial structures not correct", FlumeConfigurationValidator.mapChannels.isEmpty());
            Assert.assertFalse("The creation of initial structures not correct", FlumeConfigurationValidator.mapSinks.isEmpty());
            Assert.assertFalse("The creation of initial structures not correct", FlumeConfigurationValidator.mapSinkgroups.isEmpty());
            Assert.assertFalse("The creation of initial structures not correct", FlumeConfigurationValidator.mapInterceptors.isEmpty());

        } catch (Exception e) {
            Assert.fail("An error has occurred [test01CreateInitialStructures] method");
            logger.error("An error has occurred [test01CreateInitialStructures] method", e);
        }
    }

    @Test
    public void test02CheckNamespacesUniqueness() {

        boolean isPropertiesFileOK;

        try {

            //Check namespaces uniqueness OK
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            isPropertiesFileOK = (boolean) checkNamespacesUniquenessMethod.invoke(flumeConfigurationValidator, agentList, true);
            Assert.assertTrue("The namespaces uniqueness validation is not correct", isPropertiesFileOK);

            //Check agent name duplication name
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            agentList.add(AGENT1_NAME);
            isPropertiesFileOK = (boolean) checkNamespacesUniquenessMethod.invoke(flumeConfigurationValidator, agentList, false);
            Assert.assertFalse("The namespaces uniqueness validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check agent name duplication name (strict validation)
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            agentList.add(AGENT1_NAME);
            isPropertiesFileOK = (boolean) checkNamespacesUniquenessMethod.invoke(flumeConfigurationValidator, agentList, true);
            Assert.assertFalse("The namespaces uniqueness validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check source name duplication name
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            mapSources.get(AGENT1_NAME).add("source1_1");
            isPropertiesFileOK = (boolean) checkNamespacesUniquenessMethod.invoke(flumeConfigurationValidator, agentList, false);
            Assert.assertTrue("The namespaces uniqueness validation is not correct", isPropertiesFileOK);

            //Check source name duplication name (strict validation)
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            mapSources.get(AGENT1_NAME).add("source1_1");
            isPropertiesFileOK = (boolean) checkNamespacesUniquenessMethod.invoke(flumeConfigurationValidator, agentList, true);
            Assert.assertFalse("The namespaces uniqueness validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check channel name duplication name
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            mapChannels.get(AGENT1_NAME).add("channel1_1");
            isPropertiesFileOK = (boolean) checkNamespacesUniquenessMethod.invoke(flumeConfigurationValidator, agentList, false);
            Assert.assertTrue("The namespaces uniqueness validation is not correct", isPropertiesFileOK);

            //Check channel name duplication name (strict validation)
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            mapChannels.get(AGENT1_NAME).add("channel1_1");
            isPropertiesFileOK = (boolean) checkNamespacesUniquenessMethod.invoke(flumeConfigurationValidator, agentList, true);
            Assert.assertFalse("The namespaces uniqueness validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check sink name duplication name
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            mapSinks.get(AGENT1_NAME).add("sink1_1");
            isPropertiesFileOK = (boolean) checkNamespacesUniquenessMethod.invoke(flumeConfigurationValidator, agentList, false);
            Assert.assertTrue("The namespaces uniqueness validation is not correct", isPropertiesFileOK);

            //Check sink name duplication name (strict validation)
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            mapSinks.get(AGENT1_NAME).add("sink1_1");
            isPropertiesFileOK = (boolean) checkNamespacesUniquenessMethod.invoke(flumeConfigurationValidator, agentList, true);
            Assert.assertFalse("The namespaces uniqueness validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check sinkgroup name duplication name
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            mapSinkgroups.get(AGENT1_NAME).add("g1");
            isPropertiesFileOK = (boolean) checkNamespacesUniquenessMethod.invoke(flumeConfigurationValidator, agentList, false);
            Assert.assertTrue("The namespaces uniqueness validation is not correct", isPropertiesFileOK);

            //Check sinkgroup name duplication name (strict validation)
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            mapSinkgroups.get(AGENT1_NAME).add("g1");
            isPropertiesFileOK = (boolean) checkNamespacesUniquenessMethod.invoke(flumeConfigurationValidator, agentList, true);
            Assert.assertFalse("The namespaces uniqueness validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check interceptor name duplication name
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            mapInterceptors.get(AGENT1_NAME).get("source1_1").add("interceptor1_1a");
            isPropertiesFileOK = (boolean) checkNamespacesUniquenessMethod.invoke(flumeConfigurationValidator, agentList, false);
            Assert.assertTrue("The namespaces uniqueness validation is not correct", isPropertiesFileOK);

            //Check interceptor name duplication name (strict validation)
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            mapInterceptors.get(AGENT1_NAME).get("source1_1").add("interceptor1_1a");
            isPropertiesFileOK = (boolean) checkNamespacesUniquenessMethod.invoke(flumeConfigurationValidator, agentList, true);
            Assert.assertFalse("The namespaces uniqueness validation is not correct", isPropertiesFileOK);
            showCheckErrors();




        } catch (Exception e) {
            Assert.fail("An error has occurred [test02CheckNamespacesUniqueness] method");
            logger.error("An error has occurred [test02CheckNamespacesUniqueness] method", e);
        }
    }

    @Test
    public void test03CheckPropertiesFilePropertiesListSourcesListCheck() {

        String prefixProperty = FlumeConfiguratorConstants.SOURCES_PROPERTY;
        boolean isPropertiesFileOK;

        try {

            //Check sources OK
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            isPropertiesFileOK = (boolean) checkPropertiesFilePropertiesListMethod.invoke(flumeConfigurationValidator, prefixProperty, agentList, true);
            Assert.assertTrue("The sources list validation is not correct", isPropertiesFileOK);

            //Check agent list empty
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            isPropertiesFileOK = (boolean) checkPropertiesFilePropertiesListMethod.invoke(flumeConfigurationValidator, prefixProperty, new ArrayList(), true);
            Assert.assertFalse("The sources list validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check sources without properties
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.remove("agent1.sources");
            isPropertiesFileOK = (boolean) checkPropertiesFilePropertiesListMethod.invoke(flumeConfigurationValidator, prefixProperty, agentList, true);
            Assert.assertFalse("The sources list validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check sources list empty
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.put("agent1.sources","");
            isPropertiesFileOK = (boolean) checkPropertiesFilePropertiesListMethod.invoke(flumeConfigurationValidator, prefixProperty, agentList, true);
            Assert.assertFalse("The sources list validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check "type" property not found
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.remove("agent1.sources.source1_1.type");
            isPropertiesFileOK = (boolean) checkPropertiesFilePropertiesListMethod.invoke(flumeConfigurationValidator, prefixProperty, agentList, true);
            Assert.assertFalse("The sources list validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check "channels" property not found
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.remove("agent1.sources.source1_1.channels");
            isPropertiesFileOK = (boolean) checkPropertiesFilePropertiesListMethod.invoke(flumeConfigurationValidator, prefixProperty, agentList, true);
            Assert.assertFalse("The sources list validation is not correct", isPropertiesFileOK);
            showCheckErrors();

        } catch (Exception e) {
            Assert.fail("An error has occurred [test03CheckPropertiesFilePropertiesListSourcesListCheck] method");
            logger.error("An error has occurred [test03CheckPropertiesFilePropertiesListSourcesListCheck] method", e);
        }
    }


    @Test
    public void test04CheckPropertiesFilePropertiesListChannelsListCheck() {

        String prefixProperty = FlumeConfiguratorConstants.CHANNELS_PROPERTY;
        boolean isPropertiesFileOK;

        try {

            //Check channels OK
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            isPropertiesFileOK = (boolean) checkPropertiesFilePropertiesListMethod.invoke(flumeConfigurationValidator, prefixProperty, agentList, true);
            Assert.assertTrue("The channels list validation is not correct", isPropertiesFileOK);

            //Check agent list empty
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            isPropertiesFileOK = (boolean) checkPropertiesFilePropertiesListMethod.invoke(flumeConfigurationValidator, prefixProperty, new ArrayList(), true);
            Assert.assertFalse("The channels list validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check channels without properties
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.remove("agent1.channels");
            isPropertiesFileOK = (boolean) checkPropertiesFilePropertiesListMethod.invoke(flumeConfigurationValidator, prefixProperty, agentList, true);
            Assert.assertFalse("The channels list validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check channels list empty
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.put("agent1.channels","");
            isPropertiesFileOK = (boolean) checkPropertiesFilePropertiesListMethod.invoke(flumeConfigurationValidator, prefixProperty, agentList, true);
            Assert.assertFalse("The channels list validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check "type" property not found
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.remove("agent1.channels.channel1_1.type");
            isPropertiesFileOK = (boolean) checkPropertiesFilePropertiesListMethod.invoke(flumeConfigurationValidator, prefixProperty, agentList, true);
            Assert.assertFalse("The channels list validation is not correct", isPropertiesFileOK);
            showCheckErrors();


        } catch (Exception e) {
            Assert.fail("An error has occurred [test04CheckPropertiesFilePropertiesListChannelsListCheck] method");
            logger.error("An error has occurred [test04CheckPropertiesFilePropertiesListChannelsListCheck] method", e);
        }
    }


    @Test
    public void test05CheckPropertiesFilePropertiesListSinksListCheck() {

        String prefixProperty = FlumeConfiguratorConstants.SINKS_PROPERTY;
        boolean isPropertiesFileOK;

        try {

            //Check sinks OK
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            isPropertiesFileOK = (boolean) checkPropertiesFilePropertiesListMethod.invoke(flumeConfigurationValidator, prefixProperty, agentList, true);
            Assert.assertTrue("The sinks list validation is not correct", isPropertiesFileOK);

            //Check agent list empty
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            isPropertiesFileOK = (boolean) checkPropertiesFilePropertiesListMethod.invoke(flumeConfigurationValidator, prefixProperty, new ArrayList(), true);
            Assert.assertFalse("The sinks list validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check sinks without properties
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.remove("agent1.sinks");
            isPropertiesFileOK = (boolean) checkPropertiesFilePropertiesListMethod.invoke(flumeConfigurationValidator, prefixProperty, agentList, true);
            Assert.assertFalse("The sinks list validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check sinks list empty
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.put("agent1.sinks","");
            isPropertiesFileOK = (boolean) checkPropertiesFilePropertiesListMethod.invoke(flumeConfigurationValidator, prefixProperty, agentList, true);
            Assert.assertFalse("The sinks list validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check "type" property not found
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.remove("agent1.sinks.sink1_1.type");
            isPropertiesFileOK = (boolean) checkPropertiesFilePropertiesListMethod.invoke(flumeConfigurationValidator, prefixProperty, agentList, true);
            Assert.assertFalse("The sinks list validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check "channels" property not found
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.remove("agent1.sinks.sink1_1.channel");
            isPropertiesFileOK = (boolean) checkPropertiesFilePropertiesListMethod.invoke(flumeConfigurationValidator, prefixProperty, agentList, true);
            Assert.assertFalse("The sinks list validation is not correct", isPropertiesFileOK);
            showCheckErrors();

        } catch (Exception e) {
            Assert.fail("An error has occurred [test05CheckPropertiesFilePropertiesListSinksListCheck] method");
            logger.error("An error has occurred [test05CheckPropertiesFilePropertiesListSinksListCheck] method", e);
        }
    }


    @Test
    public void test06CheckPropertiesFilePropertiesListSinkgroupsListCheck() {

        String prefixProperty = FlumeConfiguratorConstants.SINKGROUPS_PROPERTY;
        boolean isPropertiesFileOK;

        try {

            //Check sinkgroups OK
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            isPropertiesFileOK = (boolean) checkPropertiesFilePropertiesListMethod.invoke(flumeConfigurationValidator, prefixProperty, agentList, false);
            Assert.assertTrue("The sinkgroups list validation is not correct", isPropertiesFileOK);

            //Check agent list empty
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            isPropertiesFileOK = (boolean) checkPropertiesFilePropertiesListMethod.invoke(flumeConfigurationValidator, prefixProperty, new ArrayList(), false);
            Assert.assertFalse("The sinkgroups list validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check sinkgroups without properties
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.remove("agent1.sinkgroups");
            isPropertiesFileOK = (boolean) checkPropertiesFilePropertiesListMethod.invoke(flumeConfigurationValidator, prefixProperty, agentList, false);
            Assert.assertTrue("The sinkgroups list validation is not correct", isPropertiesFileOK);

            //Check sinkgroups list empty
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.put("agent1.sinkgroups","");
            isPropertiesFileOK = (boolean) checkPropertiesFilePropertiesListMethod.invoke(flumeConfigurationValidator, prefixProperty, agentList, false);
            Assert.assertFalse("The sinkgroups list validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check "processor.type" property not found
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.remove("agent1.sinkgroups.g1.processor.type");
            isPropertiesFileOK = (boolean) checkPropertiesFilePropertiesListMethod.invoke(flumeConfigurationValidator, prefixProperty, agentList, false);
            Assert.assertFalse("The sinkgroups list validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check "sinks" property not found
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.remove("agent1.sinkgroups.g1.sinks");
            isPropertiesFileOK = (boolean) checkPropertiesFilePropertiesListMethod.invoke(flumeConfigurationValidator, prefixProperty, agentList, false);
            Assert.assertFalse("The sinkgroups list validation is not correct", isPropertiesFileOK);
            showCheckErrors();

        } catch (Exception e) {
            Assert.fail("An error has occurred [test06CheckPropertiesFilePropertiesListSinkgroupsListCheck] method");
            logger.error("An error has occurred [test06CheckPropertiesFilePropertiesListSinkgroupsListCheck] method", e);
        }
    }

    @Test
    public void test07CheckPropertiesFileInterceptorList() {

        boolean isPropertiesFileOK;

        try {

            //Check interceptors OK
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            isPropertiesFileOK = (boolean) checkPropertiesFileInterceptorListMethod.invoke(flumeConfigurationValidator, agentList);
            Assert.assertTrue("The interceptors list validation is not correct", isPropertiesFileOK);

            //Check agent list empty
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            isPropertiesFileOK = (boolean) checkPropertiesFileInterceptorListMethod.invoke(flumeConfigurationValidator, new ArrayList());
            Assert.assertFalse("The interceptors list validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check interceptor references a non-exist agent
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.put("agentX.sources.source1_1.interceptors","interceptor1");
            isPropertiesFileOK = (boolean) checkPropertiesFileInterceptorListMethod.invoke(flumeConfigurationValidator, agentList);
            Assert.assertFalse("The interceptors list validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check interceptor references a non-exist source for the agent
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.put("agent1.sources.sourceX.interceptors","interceptor1");
            isPropertiesFileOK = (boolean) checkPropertiesFileInterceptorListMethod.invoke(flumeConfigurationValidator, agentList);
            Assert.assertFalse("The interceptors list validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check empty interceptors list
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.put("agent1.sources.source1_1.interceptors","");
            isPropertiesFileOK = (boolean) checkPropertiesFileInterceptorListMethod.invoke(flumeConfigurationValidator, agentList);
            Assert.assertFalse("The interceptors list validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check "type" property not found
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.put("agent1.sources.source1_1.interceptors","interceptorX");
            isPropertiesFileOK = (boolean) checkPropertiesFileInterceptorListMethod.invoke(flumeConfigurationValidator, agentList);
            Assert.assertFalse("The interceptors list validation is not correct", isPropertiesFileOK);
            showCheckErrors();


        } catch (Exception e) {
            Assert.fail("An error has occurred [test07CheckPropertiesFileInterceptorList] method");
            logger.error("An error has occurred [test07CheckPropertiesFileInterceptorList] method", e);
        }
    }

    @Test
    public void test08CheckPropertiesFileElementsPropertiesSourcesPropertiesCheck() {

        boolean isPropertiesFileOK;

        try {

            //Check sources properties OK
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            isPropertiesFileOK = (boolean) checkPropertiesFileElementsPropertiesMethod.invoke(flumeConfigurationValidator, agentList, FlumeConfiguratorConstants.SOURCES_PROPERTY, mapSources);
            Assert.assertTrue("The sources properties validation is not correct", isPropertiesFileOK);

            //Check agent list empty
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            isPropertiesFileOK = (boolean) checkPropertiesFileElementsPropertiesMethod.invoke(flumeConfigurationValidator, new ArrayList(), FlumeConfiguratorConstants.SOURCES_PROPERTY, mapSources);
            Assert.assertFalse("The sources properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check sources property references a non exist agent
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.put("agentX.sources.source_X.propertyName","propertyValue");
            isPropertiesFileOK = (boolean) checkPropertiesFileElementsPropertiesMethod.invoke(flumeConfigurationValidator, agentList, FlumeConfiguratorConstants.SOURCES_PROPERTY, mapSources);
            Assert.assertFalse("The sources properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check sources property references a non exist source for the agent
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.put("agent1.sources.source_X.propertyName","propertyValue");
            isPropertiesFileOK = (boolean) checkPropertiesFileElementsPropertiesMethod.invoke(flumeConfigurationValidator, agentList, FlumeConfiguratorConstants.SOURCES_PROPERTY, mapSources);
            Assert.assertFalse("The sources properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check empty source property
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.put("agent1.sources.source1_1.zookeeperConnect","");
            isPropertiesFileOK = (boolean) checkPropertiesFileElementsPropertiesMethod.invoke(flumeConfigurationValidator, agentList, FlumeConfiguratorConstants.SOURCES_PROPERTY, mapSources);
            Assert.assertFalse("The sources properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check .channels property references a non exist channel for the agent of the source
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.put("agent1.sources.source1_1.channels","channel1_1 ch_X");
            isPropertiesFileOK = (boolean) checkPropertiesFileElementsPropertiesMethod.invoke(flumeConfigurationValidator, agentList, FlumeConfiguratorConstants.SOURCES_PROPERTY, mapSources);
            Assert.assertFalse("The sources properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

        } catch (Exception e) {
            Assert.fail("An error has occurred [test08CheckPropertiesFileElementsPropertiesSourcesPropertiesCheck] method");
            logger.error("An error has occurred [test08CheckPropertiesFileElementsPropertiesSourcesPropertiesCheck] method", e);
        }
    }


    @Test
    public void test09CheckPropertiesFileElementsPropertiesChannelsPropertiesCheck() {

        boolean isPropertiesFileOK;

        try {

            //Check channels properties OK
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            isPropertiesFileOK = (boolean) checkPropertiesFileElementsPropertiesMethod.invoke(flumeConfigurationValidator, agentList, FlumeConfiguratorConstants.CHANNELS_PROPERTY, mapChannels);
            Assert.assertTrue("The channels properties validation is not correct", isPropertiesFileOK);

            //Check agent list empty
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            isPropertiesFileOK = (boolean) checkPropertiesFileElementsPropertiesMethod.invoke(flumeConfigurationValidator, new ArrayList(), FlumeConfiguratorConstants.CHANNELS_PROPERTY, mapChannels);
            Assert.assertFalse("The channels properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check channels property references a non exist agent
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.put("agentX.channels.channel_X.propertyName","propertyValue");
            isPropertiesFileOK = (boolean) checkPropertiesFileElementsPropertiesMethod.invoke(flumeConfigurationValidator, agentList, FlumeConfiguratorConstants.CHANNELS_PROPERTY, mapChannels);
            Assert.assertFalse("The channels properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check channels property references a non exist channel for the agent
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.put("agent1.channels.channel_X.propertyName","propertyValue");
            isPropertiesFileOK = (boolean) checkPropertiesFileElementsPropertiesMethod.invoke(flumeConfigurationValidator, agentList, FlumeConfiguratorConstants.CHANNELS_PROPERTY, mapChannels);
            Assert.assertFalse("The channels properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check empty channel property
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.put("agent1.channels.channel1_1.capacity","");
            isPropertiesFileOK = (boolean) checkPropertiesFileElementsPropertiesMethod.invoke(flumeConfigurationValidator, agentList, FlumeConfiguratorConstants.CHANNELS_PROPERTY, mapChannels);
            Assert.assertFalse("The channels properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

        } catch (Exception e) {
            Assert.fail("An error has occurred [test09CheckPropertiesFileElementsPropertiesChannelsPropertiesCheck] method");
            logger.error("An error has occurred [test09CheckPropertiesFileElementsPropertiesChannelsPropertiesCheck] method", e);
        }
    }


    @Test
    public void test10CheckPropertiesFileElementsPropertiesSinksPropertiesCheck() {

        boolean isPropertiesFileOK;

        try {

            //Check sinks properties OK
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            isPropertiesFileOK = (boolean) checkPropertiesFileElementsPropertiesMethod.invoke(flumeConfigurationValidator, agentList, FlumeConfiguratorConstants.SINKS_PROPERTY, mapSinks);
            Assert.assertTrue("The sinks properties validation is not correct", isPropertiesFileOK);

            //Check agent list empty
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            isPropertiesFileOK = (boolean) checkPropertiesFileElementsPropertiesMethod.invoke(flumeConfigurationValidator, new ArrayList(), FlumeConfiguratorConstants.SINKS_PROPERTY, mapSinks);
            Assert.assertFalse("The sinks properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check sinks property references a non exist agent
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.put("agentX.sinks.sink_X.propertyName","propertyValue");
            isPropertiesFileOK = (boolean) checkPropertiesFileElementsPropertiesMethod.invoke(flumeConfigurationValidator, agentList, FlumeConfiguratorConstants.SINKS_PROPERTY, mapSinks);
            Assert.assertFalse("The sinks properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check sinks property references a non exist sink for the agent
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.put("agent1.sinks.sink_X.propertyName","propertyValue");
            isPropertiesFileOK = (boolean) checkPropertiesFileElementsPropertiesMethod.invoke(flumeConfigurationValidator, agentList, FlumeConfiguratorConstants.SINKS_PROPERTY, mapSinks);
            Assert.assertFalse("The sinks properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check empty sink property
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.put("agent1.sinks.sink1_1.hdfs.path","");
            isPropertiesFileOK = (boolean) checkPropertiesFileElementsPropertiesMethod.invoke(flumeConfigurationValidator, agentList, FlumeConfiguratorConstants.SINKS_PROPERTY, mapSinks);
            Assert.assertFalse("The sinks properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check .channel property references a non exist channel for the agent of the sink
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.put("agent1.sinks.sink1_1.channel","ch_X");
            isPropertiesFileOK = (boolean) checkPropertiesFileElementsPropertiesMethod.invoke(flumeConfigurationValidator, agentList, FlumeConfiguratorConstants.SINKS_PROPERTY, mapSinks);
            Assert.assertFalse("The sources sinks validation is not correct", isPropertiesFileOK);
            showCheckErrors();

        } catch (Exception e) {
            Assert.fail("An error has occurred [test10CheckPropertiesFileElementsPropertiesSinksPropertiesCheck] method");
            logger.error("An error has occurred [test10CheckPropertiesFileElementsPropertiesSinksPropertiesCheck] method", e);
        }
    }


    @Test
    public void test11CheckPropertiesFileElementsPropertiesSinkgroupsPropertiesCheck() {

        boolean isPropertiesFileOK;

        try {

            //Check sinkgroups properties OK
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            isPropertiesFileOK = (boolean) checkPropertiesFileElementsPropertiesMethod.invoke(flumeConfigurationValidator, agentList, FlumeConfiguratorConstants.SINKGROUPS_PROPERTY, mapSinkgroups);
            Assert.assertTrue("The sinkgroups properties validation is not correct", isPropertiesFileOK);

            //Check agent list empty
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            isPropertiesFileOK = (boolean) checkPropertiesFileElementsPropertiesMethod.invoke(flumeConfigurationValidator, new ArrayList(), FlumeConfiguratorConstants.SINKGROUPS_PROPERTY, mapSinkgroups);
            Assert.assertFalse("The sinkgroups properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check sinkgroups property references a non exist agent
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.put("agentX.sinkgroups.gX.propertyName","propertyValue");
            isPropertiesFileOK = (boolean) checkPropertiesFileElementsPropertiesMethod.invoke(flumeConfigurationValidator, agentList, FlumeConfiguratorConstants.SINKGROUPS_PROPERTY, mapSinkgroups);
            Assert.assertFalse("The sinkgroups properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check sinkgroups property references a non exist sinkgroup for the agent
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.put("agent1.sinkgroups.gX.propertyName","propertyValue");
            isPropertiesFileOK = (boolean) checkPropertiesFileElementsPropertiesMethod.invoke(flumeConfigurationValidator, agentList, FlumeConfiguratorConstants.SINKGROUPS_PROPERTY, mapSinkgroups);
            Assert.assertFalse("The sinkgroups properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check empty sinkgroup property
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.put("agent1.sinkgroups.g1.processor.maxpenalty","");
            isPropertiesFileOK = (boolean) checkPropertiesFileElementsPropertiesMethod.invoke(flumeConfigurationValidator, agentList, FlumeConfiguratorConstants.SINKGROUPS_PROPERTY, mapSinkgroups);
            Assert.assertFalse("The sinkgroups properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check .sinks property references a non exist sink for the agent of the sink
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.put("agent1.sinkgroups.g1.sinks","sink1_1 sink_X");
            isPropertiesFileOK = (boolean) checkPropertiesFileElementsPropertiesMethod.invoke(flumeConfigurationValidator, agentList, FlumeConfiguratorConstants.SINKGROUPS_PROPERTY, mapSinkgroups);
            Assert.assertFalse("The sinkgroups sinks validation is not correct", isPropertiesFileOK);
            showCheckErrors();

        } catch (Exception e) {
            Assert.fail("An error has occurred [test11CheckPropertiesFileElementsPropertiesSinkgroupsPropertiesCheck] method");
            logger.error("An error has occurred [test11CheckPropertiesFileElementsPropertiesSinkgroupsPropertiesCheck] method", e);
        }
    }


    @Test
    public void test12CheckPropertiesFileInterceptorsProperties() {

        boolean isPropertiesFileOK;

        try {

            //Check interceptors properties OK
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            isPropertiesFileOK = (boolean) checkPropertiesFileInterceptorsPropertiesMethod.invoke(flumeConfigurationValidator, agentList);
            Assert.assertTrue("The interceptors properties validation is not correct", isPropertiesFileOK);

            //Check agent list empty
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            isPropertiesFileOK = (boolean) checkPropertiesFileInterceptorsPropertiesMethod.invoke(flumeConfigurationValidator, new ArrayList());
            Assert.assertFalse("The interceptors properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check interceptors property references a non exist agent
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.put("agentX.sources.sourceX.interceptors.interceptorX.propertyName","propertyValue");
            isPropertiesFileOK = (boolean) checkPropertiesFileInterceptorsPropertiesMethod.invoke(flumeConfigurationValidator, agentList);
            Assert.assertFalse("The interceptors properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check interceptors property references a non exist source for the agent
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.put("agent1.sources.sourceX.interceptors.interceptor1_1a.propertyName","propertyValue");
            isPropertiesFileOK = (boolean) checkPropertiesFileInterceptorsPropertiesMethod.invoke(flumeConfigurationValidator, agentList);
            Assert.assertFalse("The interceptors properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check interceptors property references a non exist interceptor for the indicated source and agent
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.put("agent1.sources.source1_1.interceptors.interceptorX.propertyName","propertyValue");
            isPropertiesFileOK = (boolean) checkPropertiesFileInterceptorsPropertiesMethod.invoke(flumeConfigurationValidator, agentList);
            Assert.assertFalse("The interceptors properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check empty interceptor property
            loadPropertiesFile();
            resetConfigurationValidator();
            createInitialStructures();
            flumeConfigurationProperties.put("agent1.sources.source1_1.interceptors.interceptor1_1a.serializer.type","");
            isPropertiesFileOK = (boolean) checkPropertiesFileInterceptorsPropertiesMethod.invoke(flumeConfigurationValidator, agentList);
            Assert.assertFalse("The interceptors properties validation is not correct", isPropertiesFileOK);
            showCheckErrors();

        } catch (Exception e) {
            Assert.fail("An error has occurred [test12CheckPropertiesFileInterceptorsProperties] method");
            logger.error("An error has occurred [test12CheckPropertiesFileInterceptorsProperties] method", e);
        }
    }

    @Test
    public void test13ValidateFlumeConfiguration() {

        boolean isPropertiesFileOK;

        try {

            //Check validation OK
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationValidator.validateFlumeConfiguration();
            isPropertiesFileOK = flumeConfigurationValidator.isPropertiesFileOK();
            Assert.assertTrue("The validation of the Flume configuration is not correct", isPropertiesFileOK);

            //Check property unknown
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("unknown.property", "Unknown value");
            flumeConfigurationValidator.validateFlumeConfiguration();
            isPropertiesFileOK = flumeConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the Flume configuration is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check namespaces uniqueness
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("agent2.sources", "source1_1");
            flumeConfigurationValidator.validateFlumeConfiguration();
            isPropertiesFileOK = flumeConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the Flume configuration is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check sources list error
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("agent1.sources");
            flumeConfigurationValidator.validateFlumeConfiguration();
            isPropertiesFileOK = flumeConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the Flume configuration is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check channels list error
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("agent1.channels");
            flumeConfigurationValidator.validateFlumeConfiguration();
            isPropertiesFileOK = flumeConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the Flume configuration is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check sinks list error
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.remove("agent1.sinks");
            flumeConfigurationValidator.validateFlumeConfiguration();
            isPropertiesFileOK = flumeConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the Flume configuration is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check sinkgroups list error (is an optional property)
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("agent1.sinkgroups", "sinkgroupX");
            flumeConfigurationValidator.validateFlumeConfiguration();
            isPropertiesFileOK = flumeConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the Flume configuration is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check interceptors list error
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("agent1.sources.source1_1.interceptors", "interceptorX");
            flumeConfigurationValidator.validateFlumeConfiguration();
            isPropertiesFileOK = flumeConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the Flume configuration is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check sources properties error
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("agent1.sources.source1_1.channels", "channelX channelY");
            flumeConfigurationValidator.validateFlumeConfiguration();
            isPropertiesFileOK = flumeConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the Flume configuration is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check channels properties error
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("agent1.channels.channelX.type", "propertyValue");
            flumeConfigurationValidator.validateFlumeConfiguration();
            isPropertiesFileOK = flumeConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the Flume configuration is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check sinks properties error
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("agent1.sinks.sink1_1.channel", "channelX");
            flumeConfigurationValidator.validateFlumeConfiguration();
            isPropertiesFileOK = flumeConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the Flume configuration is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check sinkgroups properties error
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("agent1.sinkgroups.g1.sinks", "sink1_1 sinkX sink1_3");
            flumeConfigurationValidator.validateFlumeConfiguration();
            isPropertiesFileOK = flumeConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the Flume configuration is not correct", isPropertiesFileOK);
            showCheckErrors();

            //Check interceptors properties error
            loadPropertiesFile();
            resetConfigurationValidator();
            flumeConfigurationProperties.put("agent1.sources.source1_2.interceptors.interceptorX.serializer.type", "propertyValue");
            flumeConfigurationValidator.validateFlumeConfiguration();
            isPropertiesFileOK = flumeConfigurationValidator.isPropertiesFileOK();
            Assert.assertFalse("The validation of the Flume configuration is not correct", isPropertiesFileOK);
            showCheckErrors();

        } catch (Exception e) {
            Assert.fail("An error has occurred [test13ValidateFlumeConfiguration] method");
            logger.error("An error has occurred [test13ValidateFlumeConfiguration] method", e);
        }
    }

}
