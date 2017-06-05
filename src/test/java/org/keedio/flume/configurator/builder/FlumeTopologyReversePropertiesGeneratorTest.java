package org.keedio.flume.configurator.builder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.keedio.flume.configurator.constants.FlumeConfiguratorConstants;
import org.keedio.flume.configurator.exceptions.FlumeConfiguratorException;
import org.keedio.flume.configurator.structures.FlumeLinesProperties;
import org.keedio.flume.configurator.structures.FlumeTopology;
import org.keedio.flume.configurator.topology.IGraph;
import org.slf4j.LoggerFactory;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FlumeTopologyReversePropertiesGeneratorTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FlumeTopologyReversePropertiesGeneratorTest.class);

    private static final String FLUME_PROPERTIES_FILE_PATH = "src/test/resources/FlumeProperties/nAgent";
    private static final String FLUME_PROPERTIES_FILE_PATH_ERROR = "src/test/resources/FlumeProperties/nAgent_error";
    private static final String DRAW2D_FLUME_TOPOLOGY_GENERATED_FILE_PATH_DIRECTORY = ".";
    private static final String OUTPUT_GENERATED_FILE_PATH_FILE = "." + File.separator + "FlumeTopology.json";
    private static final Object[] objectNull = null; //to prevent warning
    private static final Class<?>[] classNull = null; //to prevent warning

    private static FlumeTopologyReversePropertiesGenerator flumeTopologyReversePropertiesGenerator = new FlumeTopologyReversePropertiesGenerator();

    private static Method createInitialStructuresMethod;
    private static Method loadFlumePropertiesFileMethod;
    private static Method generateSingleLinesPropertiesMethod;
    private static Method generateAgentsFlumeTopologyMethod;
    private static Method generateFlumeTopologyElementsMethod;
    private static Method generateElementsPropertiesMethod;
    private static Method generateFlumeTopologyConnectionsMethod;
    private static Method generateGraphMethod;
    private static Method generatePropertiesDraw2DMethod;
    private static Method writeDraw2DFlumeTopologyFileMethod;
    private static Method generateDraw2DFlumeTopologyMethod;
    private static Method generateDraw2DFlumeTopologyFromPropertiesMethod;
    private static Method generateDraw2DFlumeTopologyFromPropertiesStringMethod;

    @BeforeClass
    public static void makePrivateMethodsAccesibleByReflection() {

        try {

            FlumeTopologyReversePropertiesGenerator.setWithComments(true);
            FlumeTopologyReversePropertiesGenerator.setGeneratePositionCoordinates(false);

            //createInitialStructures is a private method. Access by reflection
            createInitialStructuresMethod = FlumeTopologyReversePropertiesGenerator.class.getDeclaredMethod("createInitialStructures", classNull);
            createInitialStructuresMethod.setAccessible(true);

            //loadFlumePropertiesFile is a private method. Access by reflection
            loadFlumePropertiesFileMethod = FlumeTopologyReversePropertiesGenerator.class.getDeclaredMethod("loadFlumePropertiesFile", classNull);
            loadFlumePropertiesFileMethod.setAccessible(true);

            //generateSingleLinesProperties is a private method. Access by reflection
            generateSingleLinesPropertiesMethod = FlumeTopologyReversePropertiesGenerator.class.getDeclaredMethod("generateSingleLinesProperties", classNull);
            generateSingleLinesPropertiesMethod.setAccessible(true);

            //generateAgentsFlumeTopology is a private method. Access by reflection
            generateAgentsFlumeTopologyMethod = FlumeTopologyReversePropertiesGenerator.class.getDeclaredMethod("generateAgentsFlumeTopology", classNull);
            generateAgentsFlumeTopologyMethod.setAccessible(true);

            //generateFlumeTopologyElements is a private method. Access by reflection
            Class<?>[] argsGenerateElementsFlumeTopologyMethod = new Class[3];
            argsGenerateElementsFlumeTopologyMethod[0] = String.class;
            argsGenerateElementsFlumeTopologyMethod[1] = String.class;
            argsGenerateElementsFlumeTopologyMethod[2] = int.class;

            generateFlumeTopologyElementsMethod = FlumeTopologyReversePropertiesGenerator.class.getDeclaredMethod("generateFlumeTopologyElements", argsGenerateElementsFlumeTopologyMethod);
            generateFlumeTopologyElementsMethod.setAccessible(true);

            //generateElementsProperties is a private method. Access by reflection
            generateElementsPropertiesMethod = FlumeTopologyReversePropertiesGenerator.class.getDeclaredMethod("generateElementsProperties", classNull);
            generateElementsPropertiesMethod.setAccessible(true);

            //generateFlumeTopologyConnections is a private method. Access by reflection
            generateFlumeTopologyConnectionsMethod = FlumeTopologyReversePropertiesGenerator.class.getDeclaredMethod("generateFlumeTopologyConnections", classNull);
            generateFlumeTopologyConnectionsMethod.setAccessible(true);

            //generateGraph is a private method. Access by reflection
            generateGraphMethod = FlumeTopologyReversePropertiesGenerator.class.getDeclaredMethod("generateGraph", classNull);
            generateGraphMethod.setAccessible(true);

            //generatePropertiesDraw2D is a private method. Access by reflection
            generatePropertiesDraw2DMethod = FlumeTopologyReversePropertiesGenerator.class.getDeclaredMethod("generatePropertiesDraw2D", classNull);
            generatePropertiesDraw2DMethod.setAccessible(true);

            //writeDraw2DFlumeTopologyFile is a private method. Access by reflection
            writeDraw2DFlumeTopologyFileMethod = FlumeTopologyReversePropertiesGenerator.class.getDeclaredMethod("writeDraw2DFlumeTopologyFile", classNull);
            writeDraw2DFlumeTopologyFileMethod.setAccessible(true);

            //generateDraw2DFlumeTopology is a private method. Access by reflection
            generateDraw2DFlumeTopologyMethod = FlumeTopologyReversePropertiesGenerator.class.getDeclaredMethod("generateDraw2DFlumeTopology", classNull);
            generateDraw2DFlumeTopologyMethod.setAccessible(true);

            //generateDraw2DFlumeTopologyFromProperties is a private method. Access by reflection
            Class<?>[] argsGenerateDraw2DFlumeTopologyFromPropertiesMethod = new Class[1];
            argsGenerateDraw2DFlumeTopologyFromPropertiesMethod[0] = Properties.class;

            generateDraw2DFlumeTopologyFromPropertiesMethod = FlumeTopologyReversePropertiesGenerator.class.getDeclaredMethod("generateDraw2DFlumeTopologyFromProperties", argsGenerateDraw2DFlumeTopologyFromPropertiesMethod);
            generateDraw2DFlumeTopologyFromPropertiesMethod.setAccessible(true);

            //generateDraw2DFlumeTopologyFromPropertiesString is a private method. Access by reflection
            Class<?>[] argsGenerateDraw2DFlumeTopologyFromPropertiesStringMethod = new Class[1];
            argsGenerateDraw2DFlumeTopologyFromPropertiesStringMethod[0] = String.class;

            generateDraw2DFlumeTopologyFromPropertiesStringMethod = FlumeTopologyReversePropertiesGenerator.class.getDeclaredMethod("generateDraw2DFlumeTopologyFromPropertiesString", argsGenerateDraw2DFlumeTopologyFromPropertiesStringMethod);
            generateDraw2DFlumeTopologyFromPropertiesStringMethod.setAccessible(true);


        } catch (Exception e) {
            Assert.fail("An error has occurred [@BeforeClass makePrivateMethodsAccesibleByReflection] method");
            logger.error("An error has occurred [@BeforeClass makePrivateMethodsAccesibleByReflection] method", e);
        }
    }


    @Test
    public void test01createInitialStructures() {

        try {

            //Invoke method
            createInitialStructuresMethod.invoke(flumeTopologyReversePropertiesGenerator, objectNull);

            Assert.assertNotNull("The creation of initial structures is not correct", flumeTopologyReversePropertiesGenerator.getMapFlumeLinesProperties());
            Assert.assertNotNull("The creation of initial structures is not correct", flumeTopologyReversePropertiesGenerator.getFlumeLinesProperties());
            Assert.assertNotNull("The creation of initial structures is not correct", flumeTopologyReversePropertiesGenerator.getFlumeLinesProperties().getLines());
            Assert.assertNotNull("The creation of initial structures is not correct", flumeTopologyReversePropertiesGenerator.getFlumeLinesProperties().getProperties());
            Assert.assertNotNull("The creation of initial structures is not correct", flumeTopologyReversePropertiesGenerator.getFlumeTopologyList());
            Assert.assertNotNull("The creation of initial structures is not correct", flumeTopologyReversePropertiesGenerator.getFlumeTopologyConnectionList());
            Assert.assertNotNull("The creation of initial structures is not correct", flumeTopologyReversePropertiesGenerator.getListTopologyConnections());
            Assert.assertNotNull("The creation of initial structures is not correct", flumeTopologyReversePropertiesGenerator.getFlumeGraphTopology());

            Assert.assertTrue("The creation of initial structures is not correct", flumeTopologyReversePropertiesGenerator.getMapFlumeLinesProperties().isEmpty());
            Assert.assertTrue("The creation of initial structures is not correct", flumeTopologyReversePropertiesGenerator.getFlumeLinesProperties().getLines().isEmpty());
            Assert.assertTrue("The creation of initial structures is not correct", flumeTopologyReversePropertiesGenerator.getFlumeLinesProperties().getProperties().isEmpty());
            Assert.assertTrue("The creation of initial structures is not correct", flumeTopologyReversePropertiesGenerator.getFlumeTopologyList().isEmpty());
            Assert.assertTrue("The creation of initial structures is not correct", flumeTopologyReversePropertiesGenerator.getFlumeTopologyConnectionList().isEmpty());
            Assert.assertTrue("The creation of initial structures is not correct", flumeTopologyReversePropertiesGenerator.getListTopologyConnections().isEmpty());
            Assert.assertTrue("The creation of initial structures is not correct", flumeTopologyReversePropertiesGenerator.getFlumeGraphTopology().isEmpty());

        } catch (Exception e) {
            Assert.fail("An error has occurred [test01createInitialStructures] method");
            logger.error("An error has occurred [test01createInitialStructures] method", e);
        }
    }


    @Test
    public void test02LoadFlumePropertiesFileFileNotFound() {

        try {

            Map<String, FlumeLinesProperties> mapFlumeLinesProperties = flumeTopologyReversePropertiesGenerator.getMapFlumeLinesProperties();
            Assert.assertEquals("The load of flume properties file(s) is no correct", mapFlumeLinesProperties.size(), 0);

            String pathFlumePropertiesError = "src/test/resources/FlumeProperties/nAgent/FileNotFound.properties";
            FlumeTopologyReversePropertiesGenerator.setPathFlumeProperties(pathFlumePropertiesError);

            //Invoke method
            loadFlumePropertiesFileMethod.invoke(flumeTopologyReversePropertiesGenerator, objectNull);

            //The exception must be thrown
            Assert.fail("The load of the Flume properties file(s) is not correct");

        } catch (InvocationTargetException ite) {
            if (!(ite.getCause() instanceof FileNotFoundException)) {
                Assert.fail("An error has occurred [test02LoadFlumePropertiesFileFileNotFound] method");
                logger.error("An error has occurred [test02LoadFlumePropertiesFileFileNotFound] method", ite);
            }
        } catch (Exception e) {
            Assert.fail("An error has occurred [test02LoadFlumePropertiesFileFileNotFound] method");
            logger.error("An error has occurred [test02LoadFlumePropertiesFileFileNotFound] method", e);
        }
    }


    @Test
    public void test03LoadFlumePropertiesFile() {

        try {

            Map<String, FlumeLinesProperties> mapFlumeLinesProperties = flumeTopologyReversePropertiesGenerator.getMapFlumeLinesProperties();
            Assert.assertEquals("The load of flume properties file(s) is no correct", mapFlumeLinesProperties.size(), 0);

            FlumeTopologyReversePropertiesGenerator.setPathFlumeProperties(FLUME_PROPERTIES_FILE_PATH);

            //Invoke method
            loadFlumePropertiesFileMethod.invoke(flumeTopologyReversePropertiesGenerator, objectNull);

            mapFlumeLinesProperties = flumeTopologyReversePropertiesGenerator.getMapFlumeLinesProperties();
            Assert.assertEquals("The load of the properties is not correct", mapFlumeLinesProperties.size(), 3);

        } catch (Exception e) {
            Assert.fail("An error has occurred [test03LoadFlumePropertiesFile] method");
            logger.error("An error has occurred [test03LoadFlumePropertiesFile] method", e);
        }
    }


    @Test
    public void test04GenerateSingleLinesPropertiesUniquenessValidationsError() {

        try {

            FlumeLinesProperties flumeLinesProperties = flumeTopologyReversePropertiesGenerator.getFlumeLinesProperties();
            Assert.assertNotNull("The generation of a single lines/properties structure is no correct", flumeLinesProperties);
            Assert.assertTrue("The generation of a single lines/properties structure is no correct", flumeLinesProperties.getLines().isEmpty());
            Assert.assertTrue("The generation of a single lines/properties structure is no correct", flumeLinesProperties.getProperties().isEmpty());

            FlumeTopologyReversePropertiesGenerator.setPathFlumeProperties(FLUME_PROPERTIES_FILE_PATH_ERROR);
            //Invoke load method
            loadFlumePropertiesFileMethod.invoke(flumeTopologyReversePropertiesGenerator, objectNull);

            //Invoke method
            generateSingleLinesPropertiesMethod.invoke(flumeTopologyReversePropertiesGenerator, objectNull);

            //The exception must be thrown
            Assert.fail("The generation of a single lines/properties structure is no correct");

        } catch (InvocationTargetException ite) {
            if (!(ite.getCause() instanceof FlumeConfiguratorException)) {
                Assert.fail("An error has occurred [test04GenerateSingleLinesPropertiesUniquenessValidationsError] method");
                logger.error("An error has occurred [test04GenerateSingleLinesPropertiesUniquenessValidationsError] method", ite);
            }
        } catch (Exception e) {
            Assert.fail("An error has occurred [test04GenerateSingleLinesPropertiesUniquenessValidationsError] method");
            logger.error("An error has occurred [test04GenerateSingleLinesPropertiesUniquenessValidationsError] method", e);
        }
    }


    @Test
    public void test05GenerateSingleLinesProperties() {

        try {

            FlumeLinesProperties flumeLinesProperties = flumeTopologyReversePropertiesGenerator.getFlumeLinesProperties();
            Assert.assertNotNull("The generation of a single lines/properties structure is no correct", flumeLinesProperties);

            FlumeTopologyReversePropertiesGenerator.setPathFlumeProperties(FLUME_PROPERTIES_FILE_PATH);
            //Invoke load method
            loadFlumePropertiesFileMethod.invoke(flumeTopologyReversePropertiesGenerator, objectNull);

            //Invoke method
            generateSingleLinesPropertiesMethod.invoke(flumeTopologyReversePropertiesGenerator, objectNull);

            flumeLinesProperties = flumeTopologyReversePropertiesGenerator.getFlumeLinesProperties();
            Assert.assertNotNull("The generation of a single lines/properties structure is no correct", flumeLinesProperties);
            Assert.assertFalse("The generation of a single lines/properties structure is no correct", flumeLinesProperties.getLines().isEmpty());
            Assert.assertFalse("The generation of a single lines/properties structure is no correct", flumeLinesProperties.getProperties().isEmpty());

        } catch (Exception e) {
            Assert.fail("An error has occurred [test05GenerateSingleLinesProperties] method");
            logger.error("An error has occurred [test05GenerateSingleLinesProperties] method", e);
        }
    }



    @Test
    public void test06GenerateAgentsFlumeTopology() {

        try {

            List<FlumeTopology> flumeTopologyList = flumeTopologyReversePropertiesGenerator.getFlumeTopologyList();
            Assert.assertNotNull("The generation of flume topology agents is no correct", flumeTopologyList);
            Assert.assertTrue("The generation of flume topology agents is no correct", flumeTopologyList.isEmpty());

            //Invoke method
            generateAgentsFlumeTopologyMethod.invoke(flumeTopologyReversePropertiesGenerator, objectNull);

            flumeTopologyList = flumeTopologyReversePropertiesGenerator.getFlumeTopologyList();
            Assert.assertNotNull("The generation of flume topology agents is no correct", flumeTopologyList);
            Assert.assertFalse("The generation of flume topology agents is no correct", flumeTopologyList.isEmpty());
            Assert.assertEquals("The generation of flume topology agents is no correct", flumeTopologyList.size(), 3);

            for (FlumeTopology flumeTopologyElement : flumeTopologyList) {
                String flumeTopologyElementType = flumeTopologyElement.getType();
                boolean allowedType = flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT);
                Assert.assertTrue("The generation of flume topology agents are no correct", allowedType);
                Assert.assertNotNull("The generation of flume topology agents is no correct", flumeTopologyElement.getId());
                Assert.assertFalse("The generation of flume topology agents is no correct", flumeTopologyElement.getId().isEmpty());
            }


        } catch (Exception e) {
            Assert.fail("An error has occurred [test06GenerateAgentsFlumeTopology] method");
            logger.error("An error has occurred [test06GenerateAgentsFlumeTopology] method", e);
        }
    }


    @Test
    public void test07GenerateElementsFlumeTopology() {

        try {

            List<FlumeTopology> flumeTopologyList = flumeTopologyReversePropertiesGenerator.getFlumeTopologyList();
            Assert.assertNotNull("The generation of flume topology elements is no correct", flumeTopologyList);
            Assert.assertFalse("The generation of flume topology elements is no correct", flumeTopologyList.isEmpty());
            Assert.assertEquals("The generation of flume topology elements is no correct", flumeTopologyList.size(), 3);

            //AGENTS
            int initialGenerateElementsFlumeTopologyAgentsElementsNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementsNumber(flumeTopologyList, FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT);
            Assert.assertEquals("The generation of flume topology elements is no correct", initialGenerateElementsFlumeTopologyAgentsElementsNumber, 3);


            //Only agents elements are present
            for (FlumeTopology flumeTopologyElement : flumeTopologyList) {
                String flumeTopologyElementType = flumeTopologyElement.getType();
                boolean allowedType = flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT);
                Assert.assertTrue("The generation of flume topology elements are no correct", allowedType);
                Assert.assertNotNull("The generation of flume topology elements is no correct", flumeTopologyElement.getId());
                Assert.assertFalse("The generation of flume topology elements is no correct", flumeTopologyElement.getId().isEmpty());
            }


            //SOURCES
            int beforeGenerateElementsFlumeTopologySourcesElementsNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementsNumber(flumeTopologyList, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE);
            Assert.assertEquals("The generation of flume topology elements is no correct", beforeGenerateElementsFlumeTopologySourcesElementsNumber, 0);

            //Invoke method
            generateFlumeTopologyElementsMethod.invoke(flumeTopologyReversePropertiesGenerator, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE, FlumeConfiguratorConstants.SOURCES_PROPERTY, FlumeConfiguratorConstants.SOURCES_LIST_PROPERTY_PART_INDEX);

            int afterGenerateElementsFlumeTopologySourcesElementsNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementsNumber(flumeTopologyList, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE);
            Assert.assertTrue("The generation of flume topology elements is no correct", afterGenerateElementsFlumeTopologySourcesElementsNumber > 0);

            //Only agents and sources elements are present
            for (FlumeTopology flumeTopologyElement : flumeTopologyList) {
                String flumeTopologyElementType = flumeTopologyElement.getType();
                boolean allowedType = flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT) ||
                        flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE);
                Assert.assertTrue("The generation of flume topology elements are no correct", allowedType);
                Assert.assertNotNull("The generation of flume topology elements is no correct", flumeTopologyElement.getId());
                Assert.assertFalse("The generation of flume topology elements is no correct", flumeTopologyElement.getId().isEmpty());
            }

            //Number of another elements types remain equal
            int generateElementsFlumeTopologyAgentsElementsNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementsNumber(flumeTopologyList, FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT);
            Assert.assertEquals("The generation of flume topology elements is no correct", generateElementsFlumeTopologyAgentsElementsNumber, initialGenerateElementsFlumeTopologyAgentsElementsNumber);


            //CHANNELS
            int beforeGenerateElementsFlumeTopologyChannelsElementsNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementsNumber(flumeTopologyList, FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL);
            Assert.assertEquals("The generation of flume topology elements is no correct", beforeGenerateElementsFlumeTopologyChannelsElementsNumber, 0);

            //Invoke method
            generateFlumeTopologyElementsMethod.invoke(flumeTopologyReversePropertiesGenerator, FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL, FlumeConfiguratorConstants.CHANNELS_PROPERTY, FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTY_PART_INDEX);

            int afterGenerateElementsFlumeTopologyChannelsElementsNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementsNumber(flumeTopologyList, FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL);
            Assert.assertTrue("The generation of flume topology elements is no correct", afterGenerateElementsFlumeTopologyChannelsElementsNumber > 0);

            //Only agents, sources and channels elements are present
            for (FlumeTopology flumeTopologyElement : flumeTopologyList) {
                String flumeTopologyElementType = flumeTopologyElement.getType();
                boolean allowedType = flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT) ||
                        flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE) ||
                        flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL);
                Assert.assertTrue("The generation of flume topology elements are no correct", allowedType);
                Assert.assertNotNull("The generation of flume topology elements is no correct", flumeTopologyElement.getId());
                Assert.assertFalse("The generation of flume topology elements is no correct", flumeTopologyElement.getId().isEmpty());
            }

            //Number of another elements types remain equal
            generateElementsFlumeTopologyAgentsElementsNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementsNumber(flumeTopologyList, FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT);
            int generateElementsFlumeTopologySourcesElementsNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementsNumber(flumeTopologyList, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE);
            Assert.assertEquals("The generation of flume topology elements is no correct", generateElementsFlumeTopologyAgentsElementsNumber, initialGenerateElementsFlumeTopologyAgentsElementsNumber);
            Assert.assertEquals("The generation of flume topology elements is no correct", generateElementsFlumeTopologySourcesElementsNumber, afterGenerateElementsFlumeTopologySourcesElementsNumber);


            //SINKS
            int beforeGenerateElementsFlumeTopologySinksElementsNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementsNumber(flumeTopologyList, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK);
            Assert.assertEquals("The generation of flume topology elements is no correct", beforeGenerateElementsFlumeTopologySinksElementsNumber, 0);

            //Invoke method
            generateFlumeTopologyElementsMethod.invoke(flumeTopologyReversePropertiesGenerator, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK, FlumeConfiguratorConstants.SINKS_PROPERTY, FlumeConfiguratorConstants.SINKS_LIST_PROPERTY_PART_INDEX);

            int afterGenerateElementsFlumeTopologySinksElementsNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementsNumber(flumeTopologyList, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK);
            Assert.assertTrue("The generation of flume topology elements is no correct", afterGenerateElementsFlumeTopologySinksElementsNumber > 0);

            //Only agents, sources, channels and sinks elements are present
            for (FlumeTopology flumeTopologyElement : flumeTopologyList) {
                String flumeTopologyElementType = flumeTopologyElement.getType();
                boolean allowedType = flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT) ||
                        flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE) ||
                        flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL) ||
                        flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK);
                Assert.assertTrue("The generation of flume topology elements are no correct", allowedType);
                Assert.assertNotNull("The generation of flume topology elements is no correct", flumeTopologyElement.getId());
                Assert.assertFalse("The generation of flume topology elements is no correct", flumeTopologyElement.getId().isEmpty());
            }

            //Number of another elements types remain equal
            generateElementsFlumeTopologyAgentsElementsNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementsNumber(flumeTopologyList, FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT);
            generateElementsFlumeTopologySourcesElementsNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementsNumber(flumeTopologyList, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE);
            int generateElementsFlumeTopologyChannelsElementsNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementsNumber(flumeTopologyList, FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL);
            Assert.assertEquals("The generation of flume topology elements is no correct", generateElementsFlumeTopologyAgentsElementsNumber, initialGenerateElementsFlumeTopologyAgentsElementsNumber);
            Assert.assertEquals("The generation of flume topology elements is no correct", generateElementsFlumeTopologySourcesElementsNumber, afterGenerateElementsFlumeTopologySourcesElementsNumber);
            Assert.assertEquals("The generation of flume topology elements is no correct", generateElementsFlumeTopologyChannelsElementsNumber, afterGenerateElementsFlumeTopologyChannelsElementsNumber);


            //SINKGROUPS
            int beforeGenerateElementsFlumeTopologySinkGroupsElementsNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementsNumber(flumeTopologyList, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP);
            Assert.assertEquals("The generation of flume topology elements is no correct", beforeGenerateElementsFlumeTopologySinkGroupsElementsNumber, 0);

            //Invoke method
            generateFlumeTopologyElementsMethod.invoke(flumeTopologyReversePropertiesGenerator, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP, FlumeConfiguratorConstants.SINKGROUPS_PROPERTY, FlumeConfiguratorConstants.SINKGROUPS_LIST_PROPERTY_PART_INDEX);

            int afterGenerateElementsFlumeTopologySinkGroupsElementsNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementsNumber(flumeTopologyList, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP);
            Assert.assertTrue("The generation of flume topology elements is no correct", afterGenerateElementsFlumeTopologySinkGroupsElementsNumber > 0);

            //Only agents, sources, channels ,sinks and sinkgroups elements are present
            for (FlumeTopology flumeTopologyElement : flumeTopologyList) {
                String flumeTopologyElementType = flumeTopologyElement.getType();
                boolean allowedType = flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT) ||
                        flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE) ||
                        flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL) ||
                        flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK) ||
                        flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP);
                Assert.assertTrue("The generation of flume topology elements are no correct", allowedType);
                Assert.assertNotNull("The generation of flume topology elements is no correct", flumeTopologyElement.getId());
                Assert.assertFalse("The generation of flume topology elements is no correct", flumeTopologyElement.getId().isEmpty());
            }

            //Number of another elements types remain equal
            generateElementsFlumeTopologyAgentsElementsNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementsNumber(flumeTopologyList, FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT);
            generateElementsFlumeTopologySourcesElementsNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementsNumber(flumeTopologyList, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE);
            generateElementsFlumeTopologyChannelsElementsNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementsNumber(flumeTopologyList, FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL);
            int generateElementsFlumeTopologySinksElementsNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementsNumber(flumeTopologyList, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK);
            Assert.assertEquals("The generation of flume topology elements is no correct", generateElementsFlumeTopologyAgentsElementsNumber, initialGenerateElementsFlumeTopologyAgentsElementsNumber);
            Assert.assertEquals("The generation of flume topology elements is no correct", generateElementsFlumeTopologySourcesElementsNumber, afterGenerateElementsFlumeTopologySourcesElementsNumber);
            Assert.assertEquals("The generation of flume topology elements is no correct", generateElementsFlumeTopologyChannelsElementsNumber, afterGenerateElementsFlumeTopologyChannelsElementsNumber);
            Assert.assertEquals("The generation of flume topology elements is no correct", generateElementsFlumeTopologySinksElementsNumber, afterGenerateElementsFlumeTopologySinksElementsNumber);



            //INTERCEPTORS
            int beforeGenerateElementsFlumeTopologyInterceptorsElementsNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementsNumber(flumeTopologyList, FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR);
            Assert.assertEquals("The generation of flume topology elements is no correct", beforeGenerateElementsFlumeTopologyInterceptorsElementsNumber, 0);

            //Invoke method
            generateFlumeTopologyElementsMethod.invoke(flumeTopologyReversePropertiesGenerator, FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR, FlumeConfiguratorConstants.INTERCEPTORS_PROPERTY, FlumeConfiguratorConstants.INTERCEPTORS_LIST_PROPERTY_PART_INDEX);

            int afterGenerateElementsFlumeTopologyInterceptorsElementsNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementsNumber(flumeTopologyList, FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR);
            Assert.assertTrue("The generation of flume topology elements is no correct", afterGenerateElementsFlumeTopologyInterceptorsElementsNumber > 0);

            //Only agents, sources, channels, sinks and interceptors elements are present
            for (FlumeTopology flumeTopologyElement : flumeTopologyList) {
                String flumeTopologyElementType = flumeTopologyElement.getType();
                boolean allowedType = flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT) ||
                        flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE) ||
                        flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL) ||
                        flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK) ||
                        flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP) ||
                        flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR);
                Assert.assertTrue("The generation of flume topology elements are no correct", allowedType);
                Assert.assertNotNull("The generation of flume topology elements is no correct", flumeTopologyElement.getId());
                Assert.assertFalse("The generation of flume topology elements is no correct", flumeTopologyElement.getId().isEmpty());
            }

            //Number of another elements types remain equal
            generateElementsFlumeTopologyAgentsElementsNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementsNumber(flumeTopologyList, FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT);
            generateElementsFlumeTopologySourcesElementsNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementsNumber(flumeTopologyList, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE);
            generateElementsFlumeTopologyChannelsElementsNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementsNumber(flumeTopologyList, FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL);
            generateElementsFlumeTopologySinksElementsNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementsNumber(flumeTopologyList, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK);
            int generateElementsFlumeTopologySinkGroupsElementsNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementsNumber(flumeTopologyList, FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP);
            Assert.assertEquals("The generation of flume topology elements is no correct", generateElementsFlumeTopologyAgentsElementsNumber, initialGenerateElementsFlumeTopologyAgentsElementsNumber);
            Assert.assertEquals("The generation of flume topology elements is no correct", generateElementsFlumeTopologySourcesElementsNumber, afterGenerateElementsFlumeTopologySourcesElementsNumber);
            Assert.assertEquals("The generation of flume topology elements is no correct", generateElementsFlumeTopologyChannelsElementsNumber, afterGenerateElementsFlumeTopologyChannelsElementsNumber);
            Assert.assertEquals("The generation of flume topology elements is no correct", generateElementsFlumeTopologySinksElementsNumber, afterGenerateElementsFlumeTopologySinksElementsNumber);
            Assert.assertEquals("The generation of flume topology elements is no correct", generateElementsFlumeTopologySinkGroupsElementsNumber, afterGenerateElementsFlumeTopologySinkGroupsElementsNumber);

        } catch (Exception e) {
            Assert.fail("An error has occurred [test07GenerateElementsFlumeTopology] method");
            logger.error("An error has occurred [test07GenerateElementsFlumeTopology] method", e);
        }
    }


    @Test
    public void test08GenerateElementsProperties() {

        try {

            List<FlumeTopology> flumeTopologyList = flumeTopologyReversePropertiesGenerator.getFlumeTopologyList();

            int beforeGenerateElementsPropertiesNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementPropertiesNumber(flumeTopologyList);
            Assert.assertTrue("The generation of properties for flume topology elements are no correct", beforeGenerateElementsPropertiesNumber > 0);


            //Check all elements only have certain common properties for all flume topology elements (flumeType,
            //elementTopologyName and elementTopologyName_comment_properties
            for (FlumeTopology flumeTopologyElement : flumeTopologyList) {
                Map<String, String> flumeTopologyElementData = flumeTopologyElement.getData();
                for(String propertyName : flumeTopologyElementData.keySet()) {
                    boolean commonProperty = propertyName.equals(FlumeConfiguratorConstants.FLUME_TYPE_PROPERTY) ||
                            propertyName.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME) ||
                            propertyName.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME + FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_COMMENT_SUFIX);

                    Assert.assertTrue("The generation of properties for flume topology elements are no correct", commonProperty);
                }
                Assert.assertNotNull("The generation of properties for flume topology elements is no correct", flumeTopologyElement.getId());
                Assert.assertFalse("The generation of properties for flume topology elements is no correct", flumeTopologyElement.getId().isEmpty());
            }

            //Invoke method
            generateElementsPropertiesMethod.invoke(flumeTopologyReversePropertiesGenerator, objectNull);

            int afterGenerateElementsPropertiesNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementPropertiesNumber(flumeTopologyList);
            Assert.assertTrue("The generation of properties for flume topology elements are no correct", afterGenerateElementsPropertiesNumber > 0);
            Assert.assertTrue("The generation of properties for flume topology elements are no correct", afterGenerateElementsPropertiesNumber > beforeGenerateElementsPropertiesNumber);


            //Check not common properties existence
            boolean existNonCommonProperty = false;
            for (FlumeTopology flumeTopologyElement : flumeTopologyList) {
                Map<String, String> flumeTopologyElementData = flumeTopologyElement.getData();
                for(String propertyName : flumeTopologyElementData.keySet()) {
                    boolean commonProperty = propertyName.equals(FlumeConfiguratorConstants.FLUME_TYPE_PROPERTY) ||
                            propertyName.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME) ||
                            propertyName.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME + FlumeConfiguratorConstants.FLUME_TOPOLOGY_PROPERTY_COMMENT_SUFIX);

                    existNonCommonProperty = existNonCommonProperty || !commonProperty;

                }
                Assert.assertNotNull("The generation of properties for flume topology elements is no correct", flumeTopologyElement.getId());
                Assert.assertFalse("The generation of properties for flume topology elements is no correct", flumeTopologyElement.getId().isEmpty());
            }

            Assert.assertTrue("The generation of properties for flume topology elements are no correct", existNonCommonProperty);

        } catch (Exception e) {
            Assert.fail("An error has occurred [test08GenerateElementsProperties] method");
            logger.error("An error has occurred [test08GenerateElementsProperties] method", e);
        }
    }


    @Test
    public void test09generateFlumeTopologyConnections() {

        try {

            List<FlumeTopology> flumeTopologyList = flumeTopologyReversePropertiesGenerator.getFlumeTopologyList();

            int beforeGenerateElementsFlumeTopologyConnectionsElementsNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementsNumber(flumeTopologyList, FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION);
            Assert.assertEquals("The generation of flume topology connections is no correct", beforeGenerateElementsFlumeTopologyConnectionsElementsNumber, 0);

            //Invoke method
            generateFlumeTopologyConnectionsMethod.invoke(flumeTopologyReversePropertiesGenerator, objectNull);

            int afterGenerateElementsFlumeTopologyConnectionsElementsNumber =
                    FlumeTopologyReversePropertiesGeneratorTestUtils.getFlumeElementTypeElementsNumber(flumeTopologyList, FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION);
            Assert.assertTrue("The generation of flume topology connections is no correct", afterGenerateElementsFlumeTopologyConnectionsElementsNumber > 0);

            for (FlumeTopology flumeTopologyElement : flumeTopologyList) {
                String flumeTopologyElementType = flumeTopologyElement.getType();
                if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION.equals(flumeTopologyElementType)) {
                    Assert.assertNotNull("The generation of flume topology connections is no correct", flumeTopologyElement.getSourceConnection());
                    Assert.assertNotNull("The generation of flume topology connections is no correct", flumeTopologyElement.getTargetConnection());
                }
                Assert.assertNotNull("The generation of flume topology connections  is no correct", flumeTopologyElement.getId());
                Assert.assertFalse("The generation of flume topology connections  is no correct", flumeTopologyElement.getId().isEmpty());
            }

        } catch (Exception e) {
            Assert.fail("An error has occurred [test09generateFlumeTopologyConnections] method");
            logger.error("An error has occurred [test09generateFlumeTopologyConnections] method", e);
        }
    }


    @Test
    public void test10GenerateGraph() {

        try {

            List<FlumeTopology> listTopologyConnections = flumeTopologyReversePropertiesGenerator.getListTopologyConnections();
            Assert.assertNotNull("The generation of flume topology graph is no correct", listTopologyConnections);
            Assert.assertTrue("The generation of flume topology graph is no correct", listTopologyConnections.isEmpty());

            Map<String, IGraph> flumeGraphTopology = flumeTopologyReversePropertiesGenerator.getFlumeGraphTopology();
            Assert.assertNotNull("The generation of flume topology graph is no correct", flumeGraphTopology);
            Assert.assertTrue("The generation of flume topology graph is no correct", flumeGraphTopology.isEmpty());

            //Invoke method
            generateGraphMethod.invoke(flumeTopologyReversePropertiesGenerator, objectNull);

            listTopologyConnections = flumeTopologyReversePropertiesGenerator.getListTopologyConnections();
            Assert.assertNotNull("The generation of flume topology graph is no correct", listTopologyConnections);
            Assert.assertTrue("The generation of flume topology graph is no correct", listTopologyConnections.size() > 0);

            flumeGraphTopology = flumeTopologyReversePropertiesGenerator.getFlumeGraphTopology();
            Assert.assertNotNull("The generation of flume topology graph is no correct", flumeGraphTopology);
            Assert.assertEquals("The generation of flume topology graph is no correct", flumeGraphTopology.size(), 3);

            for (String agentName : flumeGraphTopology.keySet()) {
                IGraph agentGraph = flumeGraphTopology.get(agentName);
                Set<FlumeTopology> agentGraphVertexSet = agentGraph.getVertexSet();
                Set<?> agentGraphEdgeSet = agentGraph.getEdgeSet();
                Assert.assertTrue("The generation of flume topology graph is no correct", agentGraphVertexSet.size() > 0);
                Assert.assertTrue("The generation of flume topology graph is no correct", agentGraphEdgeSet.size() > 0);
            }


        } catch (Exception e) {
            Assert.fail("An error has occurred [test10GenerateGraph] method");
            logger.error("An error has occurred [test10GenerateGraph] method", e);
        }
    }


    @Test
    public void test11GeneratePropertiesDraw2D() {

        try {

            List<FlumeTopology> flumeTopologyList = flumeTopologyReversePropertiesGenerator.getFlumeTopologyList();

            //Only agents, sources, channels, sinks and interceptors elements are present
            for (FlumeTopology flumeTopologyElement : flumeTopologyList) {
                String flumeTopologyElementType = flumeTopologyElement.getType();
                boolean allowedType = flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT) ||
                        flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SOURCE) ||
                        flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CHANNEL) ||
                        flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINK) ||
                        flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_SINKGROUP) ||
                        flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_INTERCEPTOR) ||
                        flumeTopologyElementType.equals(FlumeConfiguratorConstants.FLUME_TOPOLOGY_CONNECTION);
                Assert.assertTrue("The generation of Draw2D properties are no correct", allowedType);
                Assert.assertNotNull("The generation of Draw2D properties is no correct", flumeTopologyElement.getId());
                Assert.assertFalse("The generation of Draw2D properties is no correct", flumeTopologyElement.getId().isEmpty());
                Assert.assertNull("The generation of Draw2D properties are no correct", flumeTopologyElement.getX());
                Assert.assertNull("The generation of Draw2D properties are no correct", flumeTopologyElement.getY());
                Assert.assertNull("The generation of Draw2D properties are no correct", flumeTopologyElement.getWidth());
                Assert.assertNull("The generation of Draw2D properties are no correct", flumeTopologyElement.getHeight());
                Assert.assertNull("The generation of Draw2D properties are no correct", flumeTopologyElement.getBgColor());
                Assert.assertNull("The generation of Draw2D properties are no correct", flumeTopologyElement.getColor());
                Assert.assertNull("The generation of Draw2D properties are no correct", flumeTopologyElement.getStroke());
                Assert.assertNull("The generation of Draw2D properties are no correct", flumeTopologyElement.getAlpha());
                Assert.assertNull("The generation of Draw2D properties are no correct", flumeTopologyElement.getRadius());
                Assert.assertNull("The generation of Draw2D properties are no correct", flumeTopologyElement.getData().get(FlumeConfiguratorConstants.AGENT_GROUP_PROPERTY));

            }

            //Invoke method
            generatePropertiesDraw2DMethod.invoke(flumeTopologyReversePropertiesGenerator, objectNull);

            flumeTopologyList = flumeTopologyReversePropertiesGenerator.getFlumeTopologyList();

            for (FlumeTopology flumeTopologyElement : flumeTopologyList) {
                String flumeTopologyElementType = flumeTopologyElement.getType();
                boolean allowedType = flumeTopologyElementType.equals(FlumeConfiguratorConstants.DRAW2D_START_TYPE) ||
                        flumeTopologyElementType.equals(FlumeConfiguratorConstants.DRAW2D_BETWEEN_TYPE) ||
                        flumeTopologyElementType.equals(FlumeConfiguratorConstants.DRAW2D_END_TYPE) ||
                        flumeTopologyElementType.equals(FlumeConfiguratorConstants.DRAW2D_CONNECTION_TYPE);
                Assert.assertTrue("The generation of Draw2D properties are no correct", allowedType);
                Assert.assertNotNull("The generation of Draw2D properties is no correct", flumeTopologyElement.getId());
                Assert.assertFalse("The generation of Draw2D properties is no correct", flumeTopologyElement.getId().isEmpty());

                if (!FlumeConfiguratorConstants.DRAW2D_CONNECTION_TYPE.equals(flumeTopologyElementType)) {
                    Assert.assertFalse("The generation of Draw2D properties are no correct", flumeTopologyElement.getWidth().isEmpty());
                    Assert.assertFalse("The generation of Draw2D properties are no correct", flumeTopologyElement.getHeight().isEmpty());
                    Assert.assertFalse("The generation of Draw2D properties are no correct", flumeTopologyElement.getBgColor().isEmpty());
                    Assert.assertFalse("The generation of Draw2D properties are no correct", flumeTopologyElement.getColor().isEmpty());
                    Assert.assertFalse("The generation of Draw2D properties are no correct", flumeTopologyElement.getStroke().isEmpty());
                    Assert.assertFalse("The generation of Draw2D properties are no correct", flumeTopologyElement.getAlpha().isEmpty());
                    Assert.assertFalse("The generation of Draw2D properties are no correct", flumeTopologyElement.getRadius().isEmpty());
                    Assert.assertFalse("The generation of Draw2D properties are no correct", flumeTopologyElement.getCssClass().isEmpty());

                    if (FlumeTopologyReversePropertiesGenerator.isGeneratePositionCoordinates()) {
                        Assert.assertFalse("The generation of Draw2D properties are no correct", flumeTopologyElement.getX().isEmpty());
                        Assert.assertFalse("The generation of Draw2D properties are no correct", flumeTopologyElement.getY().isEmpty());
                        Assert.assertNull("The generation of Draw2D properties are no correct", flumeTopologyElement.getData().get(FlumeConfiguratorConstants.AGENT_GROUP_PROPERTY));
                    } else {
                        Assert.assertNull("The generation of Draw2D properties are no correct", flumeTopologyElement.getX());
                        Assert.assertNull("The generation of Draw2D properties are no correct", flumeTopologyElement.getY());
                        Assert.assertFalse("The generation of Draw2D properties are no correct", flumeTopologyElement.getData().get(FlumeConfiguratorConstants.AGENT_GROUP_PROPERTY).isEmpty());
                    }

                } else {
                    Assert.assertNull("The generation of Draw2D properties are no correct", flumeTopologyElement.getX());
                    Assert.assertNull("The generation of Draw2D properties are no correct", flumeTopologyElement.getY());
                    Assert.assertNull("The generation of Draw2D properties are no correct", flumeTopologyElement.getWidth());
                    Assert.assertNull("The generation of Draw2D properties are no correct", flumeTopologyElement.getHeight());
                    Assert.assertNull("The generation of Draw2D properties are no correct", flumeTopologyElement.getBgColor());
                    Assert.assertNull("The generation of Draw2D properties are no correct", flumeTopologyElement.getColor());
                    Assert.assertNull("The generation of Draw2D properties are no correct", flumeTopologyElement.getStroke());
                    Assert.assertNull("The generation of Draw2D properties are no correct", flumeTopologyElement.getAlpha());
                    Assert.assertNull("The generation of Draw2D properties are no correct", flumeTopologyElement.getRadius());
                    Assert.assertNull("The generation of Draw2D properties are no correct", flumeTopologyElement.getData().get(FlumeConfiguratorConstants.AGENT_GROUP_PROPERTY));
                }
            }

        } catch (Exception e) {
            Assert.fail("An error has occurred [test11GeneratePropertiesDraw2D] method");
            logger.error("An error has occurred [test11GeneratePropertiesDraw2D] method", e);
        }
    }


    @Test
    public void test12WriteDraw2DFlumeTopologyFileInvalidPath() {

        try {

            //Invoke method
            writeDraw2DFlumeTopologyFileMethod.invoke(flumeTopologyReversePropertiesGenerator, objectNull);

            //The exception must be thrown
            Assert.fail("The write of the Flume configuration file(s) is not correct");

        } catch (InvocationTargetException ite) {
            if (!(ite.getCause() instanceof InvalidPathException)) {
                Assert.fail("An error has occurred [test12WriteDraw2DFlumeTopologyFileInvalidPath] method");
                logger.error("An error has occurred [test12WriteDraw2DFlumeTopologyFileInvalidPath] method", ite);
            }
        } catch (Exception e) {
            Assert.fail("An error has occurred [test12WriteDraw2DFlumeTopologyFileInvalidPath] method");
            logger.error("An error has occurred [test12WriteDraw2DFlumeTopologyFileInvalidPath] method", e);
        }
    }


    @Test
    public void test13WriteDraw2DFlumeTopologyFile() {

        try {

            //Check output directory
            FlumeTopologyReversePropertiesGenerator.setPathDraw2DFlumeTopologyGeneratedFile(DRAW2D_FLUME_TOPOLOGY_GENERATED_FILE_PATH_DIRECTORY);

            //Invoke method
            writeDraw2DFlumeTopologyFileMethod.invoke(flumeTopologyReversePropertiesGenerator, objectNull);

            //Check output file
            FlumeTopologyReversePropertiesGenerator.setPathDraw2DFlumeTopologyGeneratedFile(OUTPUT_GENERATED_FILE_PATH_FILE);

            //Invoke method
            writeDraw2DFlumeTopologyFileMethod.invoke(flumeTopologyReversePropertiesGenerator, objectNull);

        } catch (Exception e) {
            Assert.fail("An error has occurred [test13WriteDraw2DFlumeTopologyFile] method");
            logger.error("An error has occurred [test13WriteDraw2DFlumeTopologyFile] method", e);
        }
    }


    @Test
    public void test14GenerateDraw2DFlumeTopology() {

        try {

            FlumeTopologyReversePropertiesGenerator.setGeneratePositionCoordinates(true);
            FlumeTopologyReversePropertiesGenerator.setWithComments(true);
            FlumeTopologyReversePropertiesGenerator.setPathDraw2DFlumeTopologyGeneratedFile(OUTPUT_GENERATED_FILE_PATH_FILE);

            //Build with validation error properties (same agent name for several agents)
            FlumeTopologyReversePropertiesGenerator.setPathFlumeProperties(FLUME_PROPERTIES_FILE_PATH_ERROR);

            //Invoke method
            boolean isCorrect = (boolean) generateDraw2DFlumeTopologyMethod.invoke(flumeTopologyReversePropertiesGenerator, objectNull);
            Assert.assertFalse("The Draw2D flume configuration file has not been built correctly", isCorrect);

            //Build correct
            FlumeTopologyReversePropertiesGenerator.setPathFlumeProperties(FLUME_PROPERTIES_FILE_PATH);

            //Invoke method
            isCorrect = (boolean) generateDraw2DFlumeTopologyMethod.invoke(flumeTopologyReversePropertiesGenerator, objectNull);
            Assert.assertTrue("The Draw2D flume configuration file has not been built correctly", isCorrect);

        } catch (Exception e) {
            Assert.fail("An error has occurred [test14GenerateDraw2DFlumeTopology] method");
            logger.error("An error has occurred [test14GenerateDraw2DFlumeTopology] method", e);
        }
    }



    @Test
    public void test15GenerateDraw2DFlumeTopologyFromProperties() {

        try {

            FlumeTopologyReversePropertiesGenerator.setGeneratePositionCoordinates(true);
            FlumeTopologyReversePropertiesGenerator.setWithComments(true);
            FlumeTopologyReversePropertiesGenerator.setPathDraw2DFlumeTopologyGeneratedFile(OUTPUT_GENERATED_FILE_PATH_FILE);


            FileInputStream fis = new FileInputStream(FLUME_PROPERTIES_FILE_PATH + "/agent1_flume.properties");
            Properties properties = new Properties();
            properties.load(fis);

            //Invoke method
            String draw2DFlumeTopology = (String) generateDraw2DFlumeTopologyFromPropertiesMethod.invoke(flumeTopologyReversePropertiesGenerator, properties);
            Assert.assertFalse("The Draw2D flume configuration file has not been built correctly", draw2DFlumeTopology.isEmpty());

        } catch (Exception e) {
            Assert.fail("An error has occurred [test15GenerateDraw2DFlumeTopologyFromProperties] method");
            logger.error("An error has occurred [test15GenerateDraw2DFlumeTopologyFromProperties] method", e);
        }
    }


    @Test
    public void test16GenerateDraw2DFlumeTopologyFromPropertiesString() {

        try {

            FlumeTopologyReversePropertiesGenerator.setGeneratePositionCoordinates(true);
            FlumeTopologyReversePropertiesGenerator.setWithComments(true);
            FlumeTopologyReversePropertiesGenerator.setPathDraw2DFlumeTopologyGeneratedFile(OUTPUT_GENERATED_FILE_PATH_FILE);

            byte[] flumeProperties = Files.readAllBytes(Paths.get(FLUME_PROPERTIES_FILE_PATH + "/agent1_flume.properties"));
            String flumePropertiesString = new String(flumeProperties);

            //Invoke method
            String draw2DFlumeTopology = (String) generateDraw2DFlumeTopologyFromPropertiesStringMethod.invoke(flumeTopologyReversePropertiesGenerator, flumePropertiesString);
            Assert.assertFalse("The Draw2D flume configuration file has not been built correctly", draw2DFlumeTopology.isEmpty());

        } catch (Exception e) {
            Assert.fail("An error has occurred [test16GenerateDraw2DFlumeTopologyFromPropertiesString] method");
            logger.error("An error has occurred [test16GenerateDraw2DFlumeTopologyFromPropertiesString] method", e);
        }
    }
}
