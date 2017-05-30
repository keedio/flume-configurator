package org.keedio.flume.configurator.builder;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.keedio.flume.configurator.constants.FlumeConfiguratorConstants;
import org.keedio.flume.configurator.structures.FlumeTopology;
import org.keedio.flume.configurator.topology.IGraph;
import org.keedio.flume.configurator.utils.FlumeConfiguratorUtils;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FlumeTopologyPropertiesGeneratorGraphTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FlumeTopologyPropertiesGeneratorGraphTest.class);

    private static final String GRAPH_TOPOLOGY_FILE_PATH = "src/test/resources/FlumeTopologyGraphWithComments_with2Agent.json";
    private static final String TOPOLOGY_FILE_PATH_ERROR = "src/test/resources/FlumeTopologyError.json";
    private static final String OUTPUT_GENERATED_FILE_PATH_DIRECTORY = ".";
    private static final String OUTPUT_GENERATED_FILE_PATH_FILE = "." + File.separator + "outputFile.conf";
    private static final Object[] objectNull = null; //to prevent warning
    private static final Class<?>[] classNull = null; //to prevent warning

    private static FlumeTopologyPropertiesGenerator flumeTopologyPropertiesGenerator = new FlumeTopologyPropertiesGenerator();

    private static Method loadJSONTopologyFileMethod;
    private static Method createInitialStructuresMethod;
    private static Method generateAgentListPropertyMethod;
    private static Method generateElementsListPropertiesMethod;
    private static Method generateGroupsListPropertiesMethod;
    private static Method generateInterceptorsListPropertiesMethod;
    private static Method generateElementsPropertiesMethod;
    private static Method writeConfigurationPropertiesFileMethod;
    private static Method generateInputPropertiesMethod;
    private static Method generateInputPropertiesFromDraw2DFlumeTopologyMethod;


    @BeforeClass
    public static void makePrivateMethodsAccesibleByReflection() {

        try {

            //loadJSONTopologyFile is a private method. Access by reflection
            loadJSONTopologyFileMethod = FlumeTopologyPropertiesGenerator.class.getDeclaredMethod("loadJSONTopologyFile", classNull);
            loadJSONTopologyFileMethod.setAccessible(true);

            //createInitialStructures is a private method. Access by reflection
            createInitialStructuresMethod = FlumeTopologyPropertiesGenerator.class.getDeclaredMethod("createInitialStructures", classNull);
            createInitialStructuresMethod.setAccessible(true);

            //generateAgentListProperty is a private method. Access by reflection
            generateAgentListPropertyMethod = FlumeTopologyPropertiesGenerator.class.getDeclaredMethod("generateAgentListProperty", classNull);
            generateAgentListPropertyMethod.setAccessible(true);

            //generateElementsListProperties is a private method. Access by reflection
            Class<?>[] argsGenerateElementsListPropertiesMethod = new Class[2];
            argsGenerateElementsListPropertiesMethod[0] = String.class;
            argsGenerateElementsListPropertiesMethod[1] = String.class;

            generateElementsListPropertiesMethod = FlumeTopologyPropertiesGenerator.class.getDeclaredMethod("generateElementsListProperties", argsGenerateElementsListPropertiesMethod);
            generateElementsListPropertiesMethod.setAccessible(true);

            //generateGroupsListProperties is a private method. Access by reflection
            Class<?>[] argsGenerateGroupsListPropertiesMethod = new Class[1];
            argsGenerateGroupsListPropertiesMethod[0] = boolean.class;

            generateGroupsListPropertiesMethod = FlumeTopologyPropertiesGenerator.class.getDeclaredMethod("generateGroupsListProperties", argsGenerateGroupsListPropertiesMethod);
            generateGroupsListPropertiesMethod.setAccessible(true);

            //generateInterceptorsListProperties is a private method. Access by reflection
            generateInterceptorsListPropertiesMethod = FlumeTopologyPropertiesGenerator.class.getDeclaredMethod("generateInterceptorsListProperties", classNull);
            generateInterceptorsListPropertiesMethod.setAccessible(true);

            //generateElementsProperties is a private method. Access by reflection
            Class<?>[] argsGenerateElementsPropertiesMethod = new Class[3];
            argsGenerateElementsPropertiesMethod[0] = String.class;
            argsGenerateElementsPropertiesMethod[1] = String.class;
            argsGenerateElementsPropertiesMethod[2] = String.class;

            generateElementsPropertiesMethod = FlumeTopologyPropertiesGenerator.class.getDeclaredMethod("generateElementsProperties", argsGenerateElementsPropertiesMethod);
            generateElementsPropertiesMethod.setAccessible(true);

            //writeConfigurationPropertiesFile is a private method. Access by reflection
            writeConfigurationPropertiesFileMethod = FlumeTopologyPropertiesGenerator.class.getDeclaredMethod("writeConfigurationPropertiesFile", classNull);
            writeConfigurationPropertiesFileMethod.setAccessible(true);

            //generateInputProperties is a private method. Access by reflection
            generateInputPropertiesMethod = FlumeTopologyPropertiesGenerator.class.getDeclaredMethod("generateInputProperties", classNull);
            generateInputPropertiesMethod.setAccessible(true);

            //generateInputPropertiesFromDraw2DFlumeTopology is a private method. Access by reflection
            Class<?>[] argsGenerateInputPropertiesFromDraw2DFlumeTopologyMethod = new Class[3];
            argsGenerateInputPropertiesFromDraw2DFlumeTopologyMethod[0] = String.class;
            argsGenerateInputPropertiesFromDraw2DFlumeTopologyMethod[1] = boolean.class;
            argsGenerateInputPropertiesFromDraw2DFlumeTopologyMethod[2] = boolean.class;

            generateInputPropertiesFromDraw2DFlumeTopologyMethod = FlumeTopologyPropertiesGenerator.class.getDeclaredMethod("generateInputPropertiesFromDraw2DFlumeTopology", argsGenerateInputPropertiesFromDraw2DFlumeTopologyMethod);
            generateInputPropertiesFromDraw2DFlumeTopologyMethod.setAccessible(true);

        } catch (Exception e) {
            Assert.fail("An error has occurred [@BeforeClass makePrivateMethodsAccesibleByReflection] method");
            logger.error("An error has occurred [@BeforeClass makePrivateMethodsAccesibleByReflection] method", e);
        }
    }

    @Test
    public void test01LoadJSONTopologyFileFileNotFound() {

        try {

            List<FlumeTopology> flumeTopologyList = flumeTopologyPropertiesGenerator.getFlumeTopologyList();
            Assert.assertNull("The load of the topology is not correct", flumeTopologyList);

            //Check not file found exception
            String pathJSONTopologyError = "src/test/resources/TopologyFileNotFound.json";
            FlumeTopologyPropertiesGenerator.setPathJSONTopology(pathJSONTopologyError);

            //Invoke method
            loadJSONTopologyFileMethod.invoke(flumeTopologyPropertiesGenerator, objectNull);

            //The exception must be thrown
            Assert.fail("The load of the topology is not correct");

        } catch (InvocationTargetException ite) {
            if (!(ite.getCause() instanceof NoSuchFileException)) {
                Assert.fail("An error has occurred [test01LoadJSONTopologyFileFileNotFound] method");
                logger.error("An error has occurred [test01LoadJSONTopologyFileFileNotFound] method", ite);
            }
        } catch (Exception e) {
            Assert.fail("An error has occurred [test01LoadJSONTopologyFileFileNotFound] method");
            logger.error("An error has occurred [test01LoadJSONTopologyFileFileNotFound] method", e);
        }
    }


    @Test
    public void test02LoadJSONTopologyFile() {

        try {

            List<FlumeTopology> flumeTopologyList = flumeTopologyPropertiesGenerator.getFlumeTopologyList();
            Assert.assertNull("The load of the topology is not correct", flumeTopologyList);

            FlumeTopologyPropertiesGenerator.setPathJSONTopology(GRAPH_TOPOLOGY_FILE_PATH);

            //Invoke method
            loadJSONTopologyFileMethod.invoke(flumeTopologyPropertiesGenerator, objectNull);

            flumeTopologyList = flumeTopologyPropertiesGenerator.getFlumeTopologyList();
            Assert.assertTrue("The load of the properties is not correct", flumeTopologyList.size() > 0);

        } catch (Exception e) {
            Assert.fail("An error has occurred [test02LoadJSONTopologyFile] method");
            logger.error("An error has occurred [test02LoadJSONTopologyFile] method", e);
        }
    }


    @Test
    public void test03CreateInitialStructures() {

        try {

            Map<String, FlumeTopology> mapTopology = flumeTopologyPropertiesGenerator.getMapTopology();
            Assert.assertNull("The creation of the initial structures is not correct", mapTopology);

            List<FlumeTopology> listTopologyAgents = flumeTopologyPropertiesGenerator.getListTopologyAgents();
            Assert.assertNull("The creation of the initial structures is not correct", listTopologyAgents);

            List<FlumeTopology> listTopologySources = flumeTopologyPropertiesGenerator.getListTopologySources();
            Assert.assertNull("The creation of the initial structures is not correct", listTopologySources);

            List<FlumeTopology> listTopologyConnections = flumeTopologyPropertiesGenerator.getListTopologyConnections();
            Assert.assertNull("The creation of the initial structures is not correct", listTopologyConnections);

            List<FlumeTopology> listTopologyInterceptors = flumeTopologyPropertiesGenerator.getListTopologyInterceptors();
            Assert.assertNull("The creation of the initial structures is not correct", listTopologyInterceptors);

            boolean isTreeCompliant = flumeTopologyPropertiesGenerator.isTreeCompliant();
            Assert.assertTrue("The creation of the initial structures is not correct", isTreeCompliant);

            Map<String, IGraph> flumeGraphTopology = flumeTopologyPropertiesGenerator.getFlumeGraphTopology();
            Assert.assertTrue("The creation of the initial structures is not correct", flumeGraphTopology.isEmpty());

            //Invoke method
            createInitialStructuresMethod.invoke(flumeTopologyPropertiesGenerator, objectNull);

            mapTopology = flumeTopologyPropertiesGenerator.getMapTopology();
            Assert.assertNotNull("The creation of the initial structures is not correct", mapTopology);
            Assert.assertTrue("The creation of the initial structures is not correct", mapTopology.size() > 0);

            listTopologyAgents = flumeTopologyPropertiesGenerator.getListTopologyAgents();
            Assert.assertNotNull("The creation of the initial structures is not correct", listTopologyAgents);
            Assert.assertTrue("The creation of the initial structures is not correct", listTopologyAgents.size() > 0);

            listTopologySources = flumeTopologyPropertiesGenerator.getListTopologySources();
            Assert.assertNotNull("The creation of the initial structures is not correct", listTopologySources);
            Assert.assertTrue("The creation of the initial structures is not correct", listTopologySources.size() > 0);

            listTopologyConnections = flumeTopologyPropertiesGenerator.getListTopologyConnections();
            Assert.assertNotNull("The creation of the initial structures is not correct", listTopologyConnections);
            Assert.assertTrue("The creation of the initial structures is not correct", listTopologyConnections.size() > 0);

            listTopologyInterceptors = flumeTopologyPropertiesGenerator.getListTopologyInterceptors();
            Assert.assertNotNull("The creation of the initial structures is not correct", listTopologyInterceptors);
            Assert.assertTrue("The creation of the initial structures is not correct", listTopologyInterceptors.size() > 0);

            isTreeCompliant = flumeTopologyPropertiesGenerator.isTreeCompliant();
            Assert.assertFalse("The creation of the initial structures is not correct", isTreeCompliant);

            flumeGraphTopology = flumeTopologyPropertiesGenerator.getFlumeGraphTopology();
            Assert.assertNotNull("The creation of the initial structures is not correct", flumeGraphTopology);
            Assert.assertTrue("The creation of the initial structures is not correct", flumeGraphTopology.size() > 0);

        } catch (Exception e) {
            Assert.fail("An error has occurred [test03CreateInitialStructures] method");
            logger.error("An error has occurred [test03CreateInitialStructures] method", e);
        }
    }


    @Test
    public void test04GenerateAgentListProperty() {

        try {

            Properties flumeConfigurationProperties = flumeTopologyPropertiesGenerator.getFlumeConfigurationProperties();
            Assert.assertTrue("The creation of the agent list property is not correct", flumeConfigurationProperties.isEmpty());
            int beforeGenerateAgentsListPropertiesNumber =  FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties,  FlumeConfiguratorConstants.AGENTS_LIST_PROPERTIES_PREFIX, true).size();
            Assert.assertEquals("The creation of the agent list property is not correct", beforeGenerateAgentsListPropertiesNumber, 0);

            //Invoke method
            generateAgentListPropertyMethod.invoke(flumeTopologyPropertiesGenerator, objectNull);

            flumeConfigurationProperties = flumeTopologyPropertiesGenerator.getFlumeConfigurationProperties();
            int afterGenerateAgentsListPropertiesNumber =  FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties,  FlumeConfiguratorConstants.AGENTS_LIST_PROPERTIES_PREFIX, true).size();
            Assert.assertTrue("The creation of the agent list property is not correct", afterGenerateAgentsListPropertiesNumber > beforeGenerateAgentsListPropertiesNumber);

        } catch (Exception e) {
            Assert.fail("An error has occurred [test04GenerateAgentListProperty] method");
            logger.error("An error has occurred [test04GenerateAgentListProperty] method", e);
        }
    }


    @Test
    public void test05GenerateElementsListProperties() {

        try {

            //SOURCES
            Properties flumeConfigurationProperties = flumeTopologyPropertiesGenerator.getFlumeConfigurationProperties();
            int beforeGenerateSourcesListPropertiesNumber =  FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties,  FlumeConfiguratorConstants.SOURCES_LIST_PROPERTIES_PREFIX, true).size();
            Assert.assertEquals("The creation of the sources list property is not correct", beforeGenerateSourcesListPropertiesNumber, 0);

            //Invoke method
            generateElementsListPropertiesMethod.invoke(flumeTopologyPropertiesGenerator, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE, FlumeConfiguratorConstants.SOURCES_LIST_PROPERTIES_PREFIX);

            flumeConfigurationProperties = flumeTopologyPropertiesGenerator.getFlumeConfigurationProperties();
            int afterGenerateSourcesListPropertiesNumber =  FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties,  FlumeConfiguratorConstants.SOURCES_LIST_PROPERTIES_PREFIX, true).size();
            Assert.assertTrue("The creation of the sources list property is not correct", afterGenerateSourcesListPropertiesNumber > beforeGenerateSourcesListPropertiesNumber);

            //CHANNELS
            int beforeGenerateChannelsListPropertiesNumber =  FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties,  FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX, true).size();
            Assert.assertEquals("The creation of the channels list property is not correct", beforeGenerateChannelsListPropertiesNumber, 0);

            //Invoke method
            generateElementsListPropertiesMethod.invoke(flumeTopologyPropertiesGenerator, FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL, FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX);

            int afterGenerateChannelsListPropertiesNumber =  FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties,  FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX, true).size();
            Assert.assertTrue("The creation of the channels list property is not correct", afterGenerateChannelsListPropertiesNumber > beforeGenerateChannelsListPropertiesNumber);

            //SINKS
            int beforeGenerateSinksListPropertiesNumber =  FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties,  FlumeConfiguratorConstants.SINKS_LIST_PROPERTIES_PREFIX, true).size();
            Assert.assertEquals("The creation of the sinks list property is not correct", beforeGenerateSinksListPropertiesNumber, 0);

            //Invoke method
            generateElementsListPropertiesMethod.invoke(flumeTopologyPropertiesGenerator, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK, FlumeConfiguratorConstants.SINKS_LIST_PROPERTIES_PREFIX);

            int afterGenerateSinksListPropertiesNumber =  FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties,  FlumeConfiguratorConstants.SINKS_LIST_PROPERTIES_PREFIX, true).size();
            Assert.assertTrue("The creation of the sinks list property is not correct", afterGenerateSinksListPropertiesNumber > beforeGenerateSinksListPropertiesNumber);

        } catch (Exception e) {
            Assert.fail("An error has occurred [test05GenerateElementsListProperties] method");
            logger.error("An error has occurred [test05GenerateElementsListProperties] method", e);
        }
    }


    @Test
    public void test06GenerateGroupsListProperties() {

        try {

            Properties flumeConfigurationProperties = flumeTopologyPropertiesGenerator.getFlumeConfigurationProperties();
            int beforeGenerateGroupsListPropertiesNumber =  FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties,  FlumeConfiguratorConstants.GROUPS_LIST_PROPERTIES_PREFIX, true).size();
            Assert.assertEquals("The creation of the groups list property is not correct", beforeGenerateGroupsListPropertiesNumber, 0);

            //Invoke method
            generateGroupsListPropertiesMethod.invoke(flumeTopologyPropertiesGenerator, true);

            flumeConfigurationProperties = flumeTopologyPropertiesGenerator.getFlumeConfigurationProperties();
            int afterGenerateGroupsListPropertiesNumber =  FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties,  FlumeConfiguratorConstants.GROUPS_LIST_PROPERTIES_PREFIX, true).size();
            Assert.assertTrue("The creation of the groups list property is not correct", afterGenerateGroupsListPropertiesNumber > beforeGenerateGroupsListPropertiesNumber);

        } catch (Exception e) {
            Assert.fail("An error has occurred [test06GenerateGroupsListProperties] method");
            logger.error("An error has occurred [test06GenerateGroupsListProperties] method", e);
        }
    }


    @Test
    public void test07GenerateInterceptorsListProperties() {

        try {

            Properties flumeConfigurationProperties = flumeTopologyPropertiesGenerator.getFlumeConfigurationProperties();
            int beforeGenerateInterceptorsListPropertiesNumber =  FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties,  FlumeConfiguratorConstants.INTERCEPTORS_LIST_PROPERTIES_PREFIX, true).size();
            Assert.assertEquals("The creation of the interceptors list property is not correct", beforeGenerateInterceptorsListPropertiesNumber, 0);

            //Invoke method
            generateInterceptorsListPropertiesMethod.invoke(flumeTopologyPropertiesGenerator, objectNull);

            flumeConfigurationProperties = flumeTopologyPropertiesGenerator.getFlumeConfigurationProperties();
            int afterGenerateInterceptorsListPropertiesNumber =  FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties,  FlumeConfiguratorConstants.INTERCEPTORS_LIST_PROPERTIES_PREFIX, true).size();
            Assert.assertTrue("The creation of the interceptors list property is not correct", afterGenerateInterceptorsListPropertiesNumber > beforeGenerateInterceptorsListPropertiesNumber);

        } catch (Exception e) {
            Assert.fail("An error has occurred [test07GenerateInterceptorsListProperties] method");
            logger.error("An error has occurred [test07GenerateInterceptorsListProperties] method", e);
        }
    }


    @Test
    public void test08GenerateElementsProperties() {

        try {

            //SOURCES
            Properties flumeConfigurationProperties = flumeTopologyPropertiesGenerator.getFlumeConfigurationProperties();
            int beforeGenerateSourcesCommonPropertiesNumber =  FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties,  FlumeConfiguratorConstants.SOURCES_COMMON_PROPERTY_PROPERTIES_PREFIX, true).size();
            Assert.assertEquals("The creation of the sources common properties is not correct", beforeGenerateSourcesCommonPropertiesNumber, 0);
            int beforeGenerateSourcesPartialPropertiesNumber =  FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties,  FlumeConfiguratorConstants.SOURCES_PARTIAL_PROPERTY_PROPERTIES_PREFIX, true).size();
            Assert.assertEquals("The creation of the sources partial properties is not correct", beforeGenerateSourcesPartialPropertiesNumber, 0);

            //Invoke method
            generateElementsPropertiesMethod.invoke(flumeTopologyPropertiesGenerator, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE, FlumeConfiguratorConstants.SOURCES_COMMON_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SOURCES_PARTIAL_PROPERTY_PROPERTIES_PREFIX);

            flumeConfigurationProperties = flumeTopologyPropertiesGenerator.getFlumeConfigurationProperties();
            int afterGenerateSourcesCommonPropertiesNumber =  FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties,  FlumeConfiguratorConstants.SOURCES_COMMON_PROPERTY_PROPERTIES_PREFIX, true).size();
            Assert.assertTrue("The creation of the sources common properties is not correct", afterGenerateSourcesCommonPropertiesNumber >= beforeGenerateSourcesCommonPropertiesNumber);
            int afterGenerateSourcesPartialPropertiesNumber =  FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties,  FlumeConfiguratorConstants.SOURCES_PARTIAL_PROPERTY_PROPERTIES_PREFIX, true).size();
            Assert.assertTrue("The creation of the sources partial properties is not correct", afterGenerateSourcesPartialPropertiesNumber > beforeGenerateSourcesPartialPropertiesNumber);


            //INTERCEPTORS
            int beforeGenerateInterceptorsCommonPropertiesNumber =  FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties,  FlumeConfiguratorConstants.INTERCEPTORS_COMMON_PROPERTY_PROPERTIES_PREFIX, true).size();
            Assert.assertEquals("The creation of the interceptors common properties is not correct", beforeGenerateInterceptorsCommonPropertiesNumber, 0);
            int beforeGenerateInterceptorsPartialPropertiesNumber =  FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties,  FlumeConfiguratorConstants.INTERCEPTORS_PARTIAL_PROPERTY_PROPERTIES_PREFIX, true).size();
            Assert.assertEquals("The creation of the interceptors partial properties is not correct", beforeGenerateInterceptorsPartialPropertiesNumber, 0);

            //Invoke method
            generateElementsPropertiesMethod.invoke(flumeTopologyPropertiesGenerator, FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR, FlumeConfiguratorConstants.INTERCEPTORS_COMMON_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.INTERCEPTORS_PARTIAL_PROPERTY_PROPERTIES_PREFIX);

            flumeConfigurationProperties = flumeTopologyPropertiesGenerator.getFlumeConfigurationProperties();
            int afterGenerateInterceptorsCommonPropertiesNumber =  FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties,  FlumeConfiguratorConstants.INTERCEPTORS_COMMON_PROPERTY_PROPERTIES_PREFIX, true).size();
            Assert.assertTrue("The creation of the interceptors common properties is not correct", afterGenerateInterceptorsCommonPropertiesNumber >= beforeGenerateInterceptorsCommonPropertiesNumber);
            int afterGenerateInterceptorsPartialPropertiesNumber =  FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties,  FlumeConfiguratorConstants.INTERCEPTORS_PARTIAL_PROPERTY_PROPERTIES_PREFIX, true).size();
            Assert.assertTrue("The creation of the interceptors common properties is not correct", afterGenerateInterceptorsPartialPropertiesNumber > beforeGenerateInterceptorsPartialPropertiesNumber);


            //CHANNELS
            int beforeGenerateChannelsCommonPropertiesNumber =  FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties,  FlumeConfiguratorConstants.CHANNELS_COMMON_PROPERTY_PROPERTIES_PREFIX, true).size();
            Assert.assertEquals("The creation of the channels common properties is not correct", beforeGenerateInterceptorsCommonPropertiesNumber, 0);
            int beforeGenerateIChannelsPartialPropertiesNumber =  FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties,  FlumeConfiguratorConstants.CHANNELS_PARTIAL_PROPERTY_PROPERTIES_PREFIX, true).size();
            Assert.assertEquals("The creation of the channels partial properties is not correct", beforeGenerateInterceptorsPartialPropertiesNumber, 0);

            //Invoke method
            generateElementsPropertiesMethod.invoke(flumeTopologyPropertiesGenerator, FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL, FlumeConfiguratorConstants.CHANNELS_COMMON_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.CHANNELS_PARTIAL_PROPERTY_PROPERTIES_PREFIX);

            flumeConfigurationProperties = flumeTopologyPropertiesGenerator.getFlumeConfigurationProperties();
            int afterGenerateChannelsCommonPropertiesNumber =  FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties,  FlumeConfiguratorConstants.CHANNELS_COMMON_PROPERTY_PROPERTIES_PREFIX, true).size();
            Assert.assertTrue("The creation of the channels common properties is not correct", afterGenerateChannelsCommonPropertiesNumber >= beforeGenerateChannelsCommonPropertiesNumber);
            int afterGenerateChannelsPartialPropertiesNumber =  FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties,  FlumeConfiguratorConstants.CHANNELS_PARTIAL_PROPERTY_PROPERTIES_PREFIX, true).size();
            Assert.assertTrue("The creation of the channels common properties is not correct", afterGenerateChannelsPartialPropertiesNumber > beforeGenerateIChannelsPartialPropertiesNumber);


            //SINKS
            int beforeGenerateSinksCommonPropertiesNumber =  FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties,  FlumeConfiguratorConstants.SINKS_COMMON_PROPERTY_PROPERTIES_PREFIX, true).size();
            Assert.assertEquals("The creation of the sinks common properties is not correct", beforeGenerateInterceptorsCommonPropertiesNumber, 0);
            int beforeGenerateISinksPartialPropertiesNumber =  FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties,  FlumeConfiguratorConstants.SINKS_PARTIAL_PROPERTY_PROPERTIES_PREFIX, true).size();
            Assert.assertEquals("The creation of the sinks partial properties is not correct", beforeGenerateInterceptorsPartialPropertiesNumber, 0);

            //Invoke method
            generateElementsPropertiesMethod.invoke(flumeTopologyPropertiesGenerator, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK, FlumeConfiguratorConstants.SINKS_COMMON_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SINKS_PARTIAL_PROPERTY_PROPERTIES_PREFIX);

            flumeConfigurationProperties = flumeTopologyPropertiesGenerator.getFlumeConfigurationProperties();
            int afterGenerateSinksCommonPropertiesNumber =  FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties,  FlumeConfiguratorConstants.SINKS_COMMON_PROPERTY_PROPERTIES_PREFIX, true).size();
            Assert.assertTrue("The creation of the sinks common properties is not correct", afterGenerateSinksCommonPropertiesNumber >= beforeGenerateSinksCommonPropertiesNumber);
            int afterGenerateSinksPartialPropertiesNumber =  FlumeConfiguratorUtils.matchingSubset(flumeConfigurationProperties,  FlumeConfiguratorConstants.SINKS_PARTIAL_PROPERTY_PROPERTIES_PREFIX, true).size();
            Assert.assertTrue("The creation of the sinks common properties is not correct", afterGenerateSinksPartialPropertiesNumber > beforeGenerateISinksPartialPropertiesNumber);

        } catch (Exception e) {
            Assert.fail("An error has occurred [test08GenerateElementsProperties] method");
            logger.error("An error has occurred [test08GenerateElementsProperties] method", e);
        }
    }


    @Test
    public void test09WriteConfigurationPropertiesFileFlumePropertiesInvalidPath() {

        try {

            FlumeTopologyPropertiesGenerator.setPathConfigurationGeneratedFile(null);

            //Invoke method
            writeConfigurationPropertiesFileMethod.invoke(flumeTopologyPropertiesGenerator, objectNull);

            //The exception must be thrown
            Assert.fail("The write of the base configuration properties file is not correct");

        } catch (InvocationTargetException ite) {
            if (!(ite.getCause() instanceof InvalidPathException)) {
                Assert.fail("An error has occurred [test09WriteConfigurationPropertiesFileFlumePropertiesInvalidPath] method");
                logger.error("An error has occurred [test09WriteConfigurationPropertiesFileFlumePropertiesInvalidPath] method", ite);
            }
        } catch (Exception e) {
            Assert.fail("An error has occurred [test09WriteConfigurationPropertiesFileFlumePropertiesInvalidPath] method");
            logger.error("An error has occurred [test09WriteConfigurationPropertiesFileFlumePropertiesInvalidPath] method", e);
        }
    }


    @Test
    public void test10WriteConfigurationPropertiesFileBaseConfigurationPropertiesInvalidPath() {

        try {

            //Check output directory & several configuration files
            FlumeTopologyPropertiesGenerator.setPathConfigurationGeneratedFile(OUTPUT_GENERATED_FILE_PATH_DIRECTORY);
            FlumeTopologyPropertiesGenerator.setPathConfigurationGeneratedFile(null);
            FlumeTopologyPropertiesGenerator.setMultipleAgentConfigurationFiles(true);
            FlumeTopologyPropertiesGenerator.setGenerateBaseConfigurationFiles(true);

            //Invoke method
            writeConfigurationPropertiesFileMethod.invoke(flumeTopologyPropertiesGenerator, objectNull);

            //The exception must be thrown
            Assert.fail("The write of the base configuration properties file is not correct");

        } catch (InvocationTargetException ite) {
            if (!(ite.getCause() instanceof InvalidPathException)) {
                Assert.fail("An error has occurred [test10WriteConfigurationPropertiesFileBaseConfigurationPropertiesInvalidPath] method");
                logger.error("An error has occurred [test10WriteConfigurationPropertiesFileBaseConfigurationPropertiesInvalidPath] method", ite);
            }
        } catch (Exception e) {
            Assert.fail("An error has occurred [test10WriteConfigurationPropertiesFileBaseConfigurationPropertiesInvalidPath] method");
            logger.error("An error has occurred [test10WriteConfigurationPropertiesFileBaseConfigurationPropertiesInvalidPath] method", e);
        }
    }


    @Test
    public void test11WriteConfigurationPropertiesFile() {

        try {

            //Check output directory & several configuration files
            FlumeTopologyPropertiesGenerator.setPathConfigurationGeneratedFile(OUTPUT_GENERATED_FILE_PATH_DIRECTORY);
            FlumeTopologyPropertiesGenerator.setMultipleAgentConfigurationFiles(true);
            FlumeTopologyPropertiesGenerator.setGenerateBaseConfigurationFiles(true);

            //Check output base configuration directory
            FlumeTopologyPropertiesGenerator.setPathBasePropertiesGeneratedFile(OUTPUT_GENERATED_FILE_PATH_DIRECTORY);

            //Invoke method
            writeConfigurationPropertiesFileMethod.invoke(flumeTopologyPropertiesGenerator, objectNull);

            //Check output base configuration file
            FlumeTopologyPropertiesGenerator.setPathBasePropertiesGeneratedFile(OUTPUT_GENERATED_FILE_PATH_FILE);

            //Invoke method
            writeConfigurationPropertiesFileMethod.invoke(flumeTopologyPropertiesGenerator, objectNull);

        } catch (Exception e) {
            Assert.fail("An error has occurred [test11WriteConfigurationPropertiesFile] method");
            logger.error("An error has occurred [test11WriteConfigurationPropertiesFile] method", e);
        }
    }


    @Test
    public void test12GenerateInputProperties() {

        try {

            flumeTopologyPropertiesGenerator = new FlumeTopologyPropertiesGenerator();

            //Check output directory & several configuration files
            FlumeTopologyPropertiesGenerator.setPathJSONTopology(TOPOLOGY_FILE_PATH_ERROR);
            FlumeTopologyPropertiesGenerator.setPathConfigurationGeneratedFile(OUTPUT_GENERATED_FILE_PATH_DIRECTORY);

            //Invoke method
            boolean isCorrect = (boolean) generateInputPropertiesMethod.invoke(flumeTopologyPropertiesGenerator, objectNull);
            Assert.assertFalse("The Flume configuration file has not been built correctly", isCorrect);

            flumeTopologyPropertiesGenerator = new FlumeTopologyPropertiesGenerator();

            //Check output base configuration directory
            FlumeTopologyPropertiesGenerator.setPathJSONTopology(GRAPH_TOPOLOGY_FILE_PATH);

            //Invoke method
            isCorrect = (boolean) generateInputPropertiesMethod.invoke(flumeTopologyPropertiesGenerator, objectNull);
            Assert.assertTrue("The Flume configuration file has not been built correctly", isCorrect);

        } catch (Exception e) {
            Assert.fail("An error has occurred [test12GenerateInputProperties] method");
            logger.error("An error has occurred [test12GenerateInputProperties] method", e);
        }
    }


    @Test
    public void test13GenerateInputPropertiesFromDraw2DFlumeTopology() {

        try {

            flumeTopologyPropertiesGenerator = new FlumeTopologyPropertiesGenerator();

            //Check output directory & several configuration files
            FlumeTopologyPropertiesGenerator.setPathJSONTopology(TOPOLOGY_FILE_PATH_ERROR);
            FlumeTopologyPropertiesGenerator.setPathConfigurationGeneratedFile(OUTPUT_GENERATED_FILE_PATH_DIRECTORY);

            byte[] encodedFile = Files.readAllBytes(Paths.get(TOPOLOGY_FILE_PATH_ERROR));
            String flumeJSONTopologyString = new String(encodedFile, Charset.defaultCharset());

            //Invoke method
            Map<String, String> configurationPropertiesMap = (Map<String, String>) generateInputPropertiesFromDraw2DFlumeTopologyMethod.invoke(flumeTopologyPropertiesGenerator, flumeJSONTopologyString, true, true);
            Assert.assertNull("The Flume configuration file has not been built correctly", configurationPropertiesMap);

            flumeTopologyPropertiesGenerator = new FlumeTopologyPropertiesGenerator();

            //Check output base configuration directory
            FlumeTopologyPropertiesGenerator.setPathJSONTopology(GRAPH_TOPOLOGY_FILE_PATH);

            encodedFile = Files.readAllBytes(Paths.get(GRAPH_TOPOLOGY_FILE_PATH));
            flumeJSONTopologyString = new String(encodedFile, Charset.defaultCharset());

            //Invoke method (no configuration properties are requested)
            configurationPropertiesMap = (Map<String, String>) generateInputPropertiesFromDraw2DFlumeTopologyMethod.invoke(flumeTopologyPropertiesGenerator, flumeJSONTopologyString, false, false);
            Assert.assertNotNull("The Flume configuration file has not been built correctly", configurationPropertiesMap);
            Assert.assertTrue("The Flume configuration file has not been built correctly", configurationPropertiesMap.isEmpty());

            //Invoke method (only base configuration properties is requested)
            configurationPropertiesMap = (Map<String, String>) generateInputPropertiesFromDraw2DFlumeTopologyMethod.invoke(flumeTopologyPropertiesGenerator, flumeJSONTopologyString, true, false);
            Assert.assertNotNull("The Flume configuration file has not been built correctly", configurationPropertiesMap);
            Assert.assertEquals("The Flume configuration file has not been built correctly", configurationPropertiesMap.size(), 1);
            Assert.assertNull("The Flume configuration file has not been built correctly", configurationPropertiesMap.get(FlumeConfiguratorConstants.FLUME_CONFIGURATION_KEY));
            Assert.assertFalse("The Flume configuration file has not been built correctly", configurationPropertiesMap.get(FlumeConfiguratorConstants.BASE_CONFIGURATION_KEY).isEmpty());

            //Invoke method (only flume configuration properties is requested)
            configurationPropertiesMap = (Map<String, String>) generateInputPropertiesFromDraw2DFlumeTopologyMethod.invoke(flumeTopologyPropertiesGenerator, flumeJSONTopologyString, false, true);
            Assert.assertNotNull("The Flume configuration file has not been built correctly", configurationPropertiesMap);
            Assert.assertEquals("The Flume configuration file has not been built correctly", configurationPropertiesMap.size(), 1);
            Assert.assertNull("The Flume configuration file has not been built correctly", configurationPropertiesMap.get(FlumeConfiguratorConstants.BASE_CONFIGURATION_KEY));
            Assert.assertFalse("The Flume configuration file has not been built correctly", configurationPropertiesMap.get(FlumeConfiguratorConstants.FLUME_CONFIGURATION_KEY).isEmpty());

            //Invoke method (base configuration properties and flume configuration properties are requested)
            configurationPropertiesMap = (Map<String, String>) generateInputPropertiesFromDraw2DFlumeTopologyMethod.invoke(flumeTopologyPropertiesGenerator, flumeJSONTopologyString, true, true);
            Assert.assertNotNull("The Flume configuration file has not been built correctly", configurationPropertiesMap);
            Assert.assertEquals("The Flume configuration file has not been built correctly", configurationPropertiesMap.size(), 2);
            Assert.assertFalse("The Flume configuration file has not been built correctly", configurationPropertiesMap.get(FlumeConfiguratorConstants.BASE_CONFIGURATION_KEY).isEmpty());
            Assert.assertFalse("The Flume configuration file has not been built correctly", configurationPropertiesMap.get(FlumeConfiguratorConstants.FLUME_CONFIGURATION_KEY).isEmpty());

        } catch (Exception e) {
            Assert.fail("An error has occurred [test13GenerateInputPropertiesFromDraw2DFlumeTopology] method");
            logger.error("An error has occurred [test13GenerateInputPropertiesFromDraw2DFlumeTopology] method", e);
        }
    }

}
