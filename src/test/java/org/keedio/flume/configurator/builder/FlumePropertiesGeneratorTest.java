package org.keedio.flume.configurator.builder;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.keedio.flume.configurator.constants.FlumeConfiguratorConstants;
import org.keedio.flume.configurator.structures.AgentConfigurationProperties;
import org.keedio.flume.configurator.structures.LinkedProperties;
import org.slf4j.LoggerFactory;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FlumePropertiesGeneratorTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FlumePropertiesGeneratorTest.class);

    private static final String PROPERTIES_FILE_PATH = "src/test/resources/FlumeConfigurationExample.properties";
    private static final String PROPERTIES_FILE_VALIDATION_ERROR_PATH = "src/test/resources/FlumeConfigurationExampleValidationError.properties";
    private static final String OUTPUT_GENERATED_FILE_PATH_DIRECTORY = ".";
    private static final String OUTPUT_GENERATED_FILE_PATH_FILE = "." + File.separator + "outputFile.conf";
    private static final String DEFAULT_SEPARATOR = ";";
    private static final Object[] objectNull = null; //to prevent warning
    private static final Class<?>[] classNull = null; //to prevent warning
    
    private static FlumePropertiesGenerator flumePropertiesGenerator = new FlumePropertiesGenerator();
    
    private static Map<String, List<String>> mapAgentSources;
    private static Map<String, List<String>> mapAgentChannels;
    private static Map<String, List<String>> mapAgentSinks;
    private static Map<String, List<String>> mapAgentSinkGroups;

    private static Method createInitialStructuresMethod;
    private static Method loadPropertiesFileMethod;
    private static Method generateAgentsListMethod;
    private static Method generateAgentElementsMethod;
    private static Method generateSourcesSelectorsMethod;
    private static Method generateSelectorsCommonPropertiesMethod;
    private static Method generateSelectorsPartialPropertiesMethod;
    private static Method generateSourcesInterceptorsMethod;
    private static Method generateInterceptorsCommonPropertiesMethod;
    private static Method generateInterceptorsPartialPropertiesMethod;
    private static Method generateElementsCommonPropertiesMethod;
    private static Method generateElementsPartialPropertiesMethod;
    private static Method generateFinalStructureMapMethod;
    private static Method writeConfigurationFilesMethod;
    private static Method buildConfigurationMapMethod;
    private static Method buildConfigurationMapFromStringPropertiesMethod;
    
    @BeforeClass
    public static void makePrivateMethodsAccesibleByReflection() {

        try {
        FlumePropertiesGenerator.setElementsCharacterSeparator(DEFAULT_SEPARATOR);

        //createInitialStructures is a private method. Access by reflection
        createInitialStructuresMethod = FlumePropertiesGenerator.class.getDeclaredMethod("createInitialStructures", classNull);
        createInitialStructuresMethod.setAccessible(true);

        //loadPropertiesFile is a private method. Access by reflection
        loadPropertiesFileMethod = FlumePropertiesGenerator.class.getDeclaredMethod("loadPropertiesFile", classNull);
        loadPropertiesFileMethod.setAccessible(true);

        //generateAgentsList is a private method. Access by reflection
        generateAgentsListMethod = FlumePropertiesGenerator.class.getDeclaredMethod("generateAgentsList", classNull);
        generateAgentsListMethod.setAccessible(true);
        
        //generateAgentElements is a private method. Access by reflection
        Class<?>[] argsGenerateAgentElementsMethod = new Class[2];
        argsGenerateAgentElementsMethod[0] = String.class;
        argsGenerateAgentElementsMethod[1] = String.class;
        
        generateAgentElementsMethod = FlumePropertiesGenerator.class.getDeclaredMethod("generateAgentElements", argsGenerateAgentElementsMethod);
        generateAgentElementsMethod.setAccessible(true);

        //generateSourcesSelectors is a private method. Access by reflection
        generateSourcesSelectorsMethod = FlumePropertiesGenerator.class.getDeclaredMethod("generateSourcesSelectors", classNull);
        generateSourcesSelectorsMethod.setAccessible(true);

        //generateSelectorsCommonProperties is a private method. Access by reflection
        generateSelectorsCommonPropertiesMethod = FlumePropertiesGenerator.class.getDeclaredMethod("generateSelectorsCommonProperties", classNull);
        generateSelectorsCommonPropertiesMethod.setAccessible(true);

        //generateSelectorsPartialProperties is a private method. Access by reflection
        generateSelectorsPartialPropertiesMethod = FlumePropertiesGenerator.class.getDeclaredMethod("generateSelectorsPartialProperties", classNull);
        generateSelectorsPartialPropertiesMethod.setAccessible(true);

        //generateSourcesInterceptors is a private method. Access by reflection
        generateSourcesInterceptorsMethod = FlumePropertiesGenerator.class.getDeclaredMethod("generateSourcesInterceptors", classNull);
        generateSourcesInterceptorsMethod.setAccessible(true); 
        
        //generateSourcesInterceptorsCommonProperties is a private method. Access by reflection
        generateInterceptorsCommonPropertiesMethod = FlumePropertiesGenerator.class.getDeclaredMethod("generateInterceptorsCommonProperties", classNull);
        generateInterceptorsCommonPropertiesMethod.setAccessible(true); 
        
        //generateSourcesInterceptorsPartialProperties is a private method. Access by reflection
        generateInterceptorsPartialPropertiesMethod = FlumePropertiesGenerator.class.getDeclaredMethod("generateInterceptorsPartialProperties", classNull);
        generateInterceptorsPartialPropertiesMethod.setAccessible(true);
        
        //generateElementsCommonProperties is a private method. Access by reflection
        Class<?>[] argsGenerateElementsCommonPropertiesMethod = new Class[3];
        argsGenerateElementsCommonPropertiesMethod[0] = String.class;
        argsGenerateElementsCommonPropertiesMethod[1] = String.class;
        argsGenerateElementsCommonPropertiesMethod[2] = Map.class;
        
        generateElementsCommonPropertiesMethod = FlumePropertiesGenerator.class.getDeclaredMethod("generateElementsCommonProperties", argsGenerateElementsCommonPropertiesMethod);
        generateElementsCommonPropertiesMethod.setAccessible(true);       

        //generateElementsPartialProperties is a private method. Access by reflection
        Class<?>[] argsGenerateElementsPartialPropertiesMethod = new Class[3];
        argsGenerateElementsPartialPropertiesMethod[0] = String.class;
        argsGenerateElementsPartialPropertiesMethod[1] = String.class;
        argsGenerateElementsPartialPropertiesMethod[2] = Map.class;
        
        generateElementsPartialPropertiesMethod = FlumePropertiesGenerator.class.getDeclaredMethod("generateElementsPartialProperties", argsGenerateElementsPartialPropertiesMethod);
        generateElementsPartialPropertiesMethod.setAccessible(true);
        
        //generateFinalStructureMap is a private method. Access by reflection
        generateFinalStructureMapMethod = FlumePropertiesGenerator.class.getDeclaredMethod("generateFinalStructureMap", classNull);
        generateFinalStructureMapMethod.setAccessible(true);
        
        //writeConfigurationFiles is a private method. Access by reflection
        writeConfigurationFilesMethod = FlumePropertiesGenerator.class.getDeclaredMethod("writeConfigurationFiles", classNull);
        writeConfigurationFilesMethod.setAccessible(true); 
        
        //buildConfigurationMap is a private method. Access by reflection
        buildConfigurationMapMethod = FlumePropertiesGenerator.class.getDeclaredMethod("buildConfigurationMap", classNull);
        buildConfigurationMapMethod.setAccessible(true);

        //buildConfigurationMapFromStringProperties is a private method. Access by reflection
        Class<?>[] argsBuildConfigurationMapFromStringPropertiesMethod = new Class[6];
        argsBuildConfigurationMapFromStringPropertiesMethod[0] = String.class;
        argsBuildConfigurationMapFromStringPropertiesMethod[1] = String.class;
        argsBuildConfigurationMapFromStringPropertiesMethod[2] = boolean.class;
        argsBuildConfigurationMapFromStringPropertiesMethod[3] = boolean.class;
        argsBuildConfigurationMapFromStringPropertiesMethod[4] = String.class;
        argsBuildConfigurationMapFromStringPropertiesMethod[5] = boolean.class;

        buildConfigurationMapFromStringPropertiesMethod = FlumePropertiesGenerator.class.getDeclaredMethod("buildConfigurationMapFromStringProperties", argsBuildConfigurationMapFromStringPropertiesMethod);
        buildConfigurationMapFromStringPropertiesMethod.setAccessible(true);

        } catch (Exception e) {
            Assert.fail("An error has occurred [@BeforeClass makePrivateMethodsAccesibleByReflection] method");
            logger.error("An error has occurred [@BeforeClass makePrivateMethodsAccesibleByReflection] method", e);
        }
    }

    @Test
    public void test01CreateInitialStructures() {

        try {

            //Invoke method
            createInitialStructuresMethod.invoke(flumePropertiesGenerator, objectNull);

            Assert.assertNotNull("The creation of initial structures is not correct", flumePropertiesGenerator.getFlumeConfigurationProperties());
            Assert.assertNotNull("The creation of initial structures is not correct", flumePropertiesGenerator.getConfigurationInitialMap());
            Assert.assertNotNull("The creation of initial structures is not correct", flumePropertiesGenerator.getConfigurationFinalMap());
            Assert.assertNotNull("The creation of initial structures is not correct", flumePropertiesGenerator.getAgentsList());
            Assert.assertNotNull("The creation of initial structures is not correct", flumePropertiesGenerator.getMapAgentSources());
            Assert.assertNotNull("The creation of initial structures is not correct", flumePropertiesGenerator.getMapSourcesInterceptors());

            Assert.assertTrue("The creation of initial structures is not correct", flumePropertiesGenerator.getFlumeConfigurationProperties().isEmpty());
            Assert.assertTrue("The creation of initial structures is not correct", flumePropertiesGenerator.getConfigurationInitialMap().isEmpty());
            Assert.assertTrue("The creation of initial structures is not correct", flumePropertiesGenerator.getConfigurationFinalMap().isEmpty());
            Assert.assertTrue("The creation of initial structures is not correct", flumePropertiesGenerator.getAgentsList().isEmpty());
            Assert.assertTrue("The creation of initial structures is not correct", flumePropertiesGenerator.getMapAgentSources().isEmpty());
            Assert.assertTrue("The creation of initial structures is not correct", flumePropertiesGenerator.getMapSourcesInterceptors().isEmpty());

        } catch (Exception e) {
            Assert.fail("An error has occurred [test01CreateInitialStructures] method");
            logger.error("An error has occurred [test01CreateInitialStructures] method", e);
        }
    }
    
  
    @Test
    public void test02LoadPropertiesFileFileNotFound() {

        try {

            Properties flumeConfigurationProperties = FlumePropertiesGenerator.getFlumeConfigurationProperties();
            Assert.assertTrue("The load of the properties is not correct", flumeConfigurationProperties.size() == 0);

            //Check not file found exception
            String propertiesFilePathError = "src/test/resources/FileNotFound.properties";
            FlumePropertiesGenerator.setPathBaseConfigurationProperties(propertiesFilePathError);

            //Invoke method
            loadPropertiesFileMethod.invoke(flumePropertiesGenerator, objectNull);

            //The exception must be thrown
            Assert.fail("The load of the properties is not correct");

        } catch (InvocationTargetException ite) {
               if (!(ite.getCause() instanceof FileNotFoundException)) {
                   Assert.fail("An error has occurred [test02LoadPropertiesFileFileNotFound] method");
                   logger.error("An error has occurred [test02LoadPropertiesFileFileNotFound] method", ite);
               }
        } catch (Exception e) {
            Assert.fail("An error has occurred [test02LoadPropertiesFileFileNotFound] method");
            logger.error("An error has occurred [test02LoadPropertiesFileFileNotFound] method", e);
        }
    }
    
    
    
    @Test
    public void test03LoadPropertiesFile() {

        try {

            Properties flumeConfigurationProperties = FlumePropertiesGenerator.getFlumeConfigurationProperties();
            Assert.assertTrue("The load of the properties is not correct", flumeConfigurationProperties.size() == 0);

            FlumePropertiesGenerator.setPathBaseConfigurationProperties(PROPERTIES_FILE_PATH);

            //Invoke method
            loadPropertiesFileMethod.invoke(flumePropertiesGenerator, objectNull);

            flumeConfigurationProperties = FlumePropertiesGenerator.getFlumeConfigurationProperties();
            Assert.assertTrue("The load of the properties is not correct", flumeConfigurationProperties.size() > 0);

        } catch (Exception e) {
            Assert.fail("An error has occurred [test03LoadPropertiesFile] method");
            logger.error("An error has occurred [test03LoadPropertiesFile] method", e);
        }
    }    
   
    
    
    @Test
    public void test04GenerateAgentsList() {

        try {

            int beforeGenerateAgentsListPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            Assert.assertEquals("The generation of the list of agents is not correct", beforeGenerateAgentsListPropertiesNumber, 0);

            List<String> agentList = flumePropertiesGenerator.getAgentsList();
            Assert.assertTrue("The generation of the list of agents is not correct", agentList.isEmpty());

            Map<String,LinkedProperties> configurationInitialMap = flumePropertiesGenerator.getConfigurationInitialMap();
            Assert.assertTrue("The generation of the list of agents is not correct", configurationInitialMap.isEmpty());

            //Invoke method
            generateAgentsListMethod.invoke(flumePropertiesGenerator, objectNull);

            agentList = flumePropertiesGenerator.getAgentsList();
            Assert.assertNotNull("The generation of the list of agents is not correct", agentList);
            Assert.assertFalse("The generation of the list of agents is not correct", agentList.isEmpty());

            configurationInitialMap = flumePropertiesGenerator.getConfigurationInitialMap();
            Assert.assertTrue("The generation of the list of agents is not correct", configurationInitialMap.size() > 0);

            int afterGenerateAgentsListPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            Assert.assertEquals("The generation of the list of agents is not correct", afterGenerateAgentsListPropertiesNumber, beforeGenerateAgentsListPropertiesNumber);

        } catch (Exception e) {
            Assert.fail("An error has occurred [test04GenerateAgentsList] method");
            logger.error("An error has occurred [test04GenerateAgentsList] method", e);
        }
    }  
    
    
    
    @Test
    @SuppressWarnings("unchecked")
    public void test05GenerateAgentElements() {

        try {


            //SOURCES
            int beforeGenerateSourcesElementsPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            boolean containsAgentElementsSources = FlumePropertiesGeneratorTestUtils.containsAgentElements(flumePropertiesGenerator.getConfigurationInitialMap(), FlumeConfiguratorConstants.SOURCES_PROPERTY);
            Assert.assertFalse("The generation of the list of sources is not correct", containsAgentElementsSources);

            //Invoke method
            mapAgentSources = (Map<String, List<String>>) generateAgentElementsMethod.invoke(flumePropertiesGenerator, FlumeConfiguratorConstants.SOURCES_LIST_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SOURCES_PROPERTY);
            Assert.assertTrue("The generation of the list of sources is not correct", mapAgentSources.size() > 0);
            flumePropertiesGenerator.setMapAgentSources(mapAgentSources);

            int afterGenerateSourcesElementsPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            Assert.assertTrue("The generation of the list of sources is not correct", afterGenerateSourcesElementsPropertiesNumber > beforeGenerateSourcesElementsPropertiesNumber);
            containsAgentElementsSources = FlumePropertiesGeneratorTestUtils.containsAgentElements(flumePropertiesGenerator.getConfigurationInitialMap(), FlumeConfiguratorConstants.SOURCES_PROPERTY);
            Assert.assertTrue("The generation of the list of sources is not correct", containsAgentElementsSources);

            //CHANNELS
            int beforeGenerateChannelsElementsPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            boolean containsAgentElementsChannels = FlumePropertiesGeneratorTestUtils.containsAgentElements(flumePropertiesGenerator.getConfigurationInitialMap(), FlumeConfiguratorConstants.CHANNELS_PROPERTY);
            Assert.assertFalse("The generation of the list of channels is not correct", containsAgentElementsChannels);

            //Invoke method
            mapAgentChannels = (Map<String, List<String>>) generateAgentElementsMethod.invoke(flumePropertiesGenerator, FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX, FlumeConfiguratorConstants.CHANNELS_PROPERTY);
            Assert.assertTrue("The generation of the list of channels is not correct", mapAgentChannels.size() > 0);

            int afterGenerateChannelsElementsPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            Assert.assertTrue("The generation of the list of channels is not correct", afterGenerateChannelsElementsPropertiesNumber > beforeGenerateChannelsElementsPropertiesNumber);
            containsAgentElementsChannels = FlumePropertiesGeneratorTestUtils.containsAgentElements(flumePropertiesGenerator.getConfigurationInitialMap(), FlumeConfiguratorConstants.CHANNELS_PROPERTY);
            Assert.assertTrue("The generation of the list of channels is not correct", containsAgentElementsChannels);


            //SINKS
            int beforeGenerateSinksElementsPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            boolean containsAgentElementsSinks = FlumePropertiesGeneratorTestUtils.containsAgentElements(flumePropertiesGenerator.getConfigurationInitialMap(), FlumeConfiguratorConstants.SINKS_PROPERTY);
            Assert.assertFalse("The generation of the list of sinks is not correct", containsAgentElementsSinks);

            //Invoke method
            mapAgentSinks = (Map<String, List<String>>) generateAgentElementsMethod.invoke(flumePropertiesGenerator, FlumeConfiguratorConstants.SINKS_LIST_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SINKS_PROPERTY);
            Assert.assertTrue("The generation of the list of sinks is not correct", mapAgentSinks.size() > 0);

            int afterGenerateSinksElementsPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            Assert.assertTrue("The generation of the list of sinks is not correct", afterGenerateSinksElementsPropertiesNumber > beforeGenerateSinksElementsPropertiesNumber);
            containsAgentElementsSinks = FlumePropertiesGeneratorTestUtils.containsAgentElements(flumePropertiesGenerator.getConfigurationInitialMap(), FlumeConfiguratorConstants.SINKS_PROPERTY);
            Assert.assertTrue("The generation of the list of sinks is not correct", containsAgentElementsSinks);


            //SINK GROUPS
            int beforeGenerateSinkGroupsElementsPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            boolean containsAgentElementsSinkGroups = FlumePropertiesGeneratorTestUtils.containsAgentElements(flumePropertiesGenerator.getConfigurationInitialMap(), FlumeConfiguratorConstants.SINKGROUPS_PROPERTY);
            Assert.assertFalse("The generation of the list of sinkgroups is not correct", containsAgentElementsSinkGroups);

            //Invoke method
            mapAgentSinkGroups = (Map<String, List<String>>) generateAgentElementsMethod.invoke(flumePropertiesGenerator, FlumeConfiguratorConstants.SINKGROUPS_LIST_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SINKGROUPS_PROPERTY);
            Assert.assertTrue("The generation of the list of sinkgroups is not correct", mapAgentSinkGroups.size() > 0);

            int afterGenerateSinkGroupsElementsPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            Assert.assertTrue("The generation of the list of sinkgroups is not correct", afterGenerateSinkGroupsElementsPropertiesNumber > beforeGenerateSinkGroupsElementsPropertiesNumber);
            containsAgentElementsSinkGroups = FlumePropertiesGeneratorTestUtils.containsAgentElements(flumePropertiesGenerator.getConfigurationInitialMap(), FlumeConfiguratorConstants.SINKGROUPS_PROPERTY);
            Assert.assertTrue("The generation of the list of sinkgroups is not correct", containsAgentElementsSinkGroups);


        } catch (Exception e) {
            Assert.fail("An error has occurred [test05GenerateAgentElements] method");
            logger.error("An error has occurred [test05GenerateAgentElements] method", e);
        }
    }


    @Test
    public void test06GenerateSourcesSelectors() {

        try {

            int beforeGenerateSourcesSelectorsPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());

            Map<String, List<String>> mapAgentSelectors = flumePropertiesGenerator.getMapAgentSelectors();
            Assert.assertTrue("The generation of the list of sources with selector is not correct", mapAgentSelectors.isEmpty());

            //Invoke method
            generateSourcesSelectorsMethod.invoke(flumePropertiesGenerator, objectNull);

            mapAgentSelectors = flumePropertiesGenerator.getMapAgentSelectors();
            Assert.assertNotNull("The generation of the list of sources with selector is not correct", mapAgentSelectors);
            Assert.assertTrue("The generation of the list of sources with selector is not correct", mapAgentSelectors.size() > 0);

            int afterGenerateSourcesSelectorsPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            Assert.assertEquals("The generation of the list of sources with selector is not correct", beforeGenerateSourcesSelectorsPropertiesNumber, afterGenerateSourcesSelectorsPropertiesNumber);

        } catch (Exception e) {
            Assert.fail("An error has occurred [test06GenerateSourcesSelectors] method");
            logger.error("An error has occurred [test06GenerateSourcesSelectors] method", e);
        }
    }


    @Test
    public void test07GenerateSourcesSelectorsCommonProperties() {

        try {

            int beforeGenerateSourcesSelectorsCommonPropertiesPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            boolean containsSelectorsProperties = FlumePropertiesGeneratorTestUtils.containsSelectorsProperties(flumePropertiesGenerator.getConfigurationInitialMap());
            Assert.assertFalse("The generation of the list of selectors common properties is not correct", containsSelectorsProperties);

            //Invoke method
            generateSelectorsCommonPropertiesMethod.invoke(flumePropertiesGenerator, objectNull);

            int afterGenerateSourcesSelectorsCommonPropertiesPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            Assert.assertTrue("The generation of the list of selectors common properties is not correct", afterGenerateSourcesSelectorsCommonPropertiesPropertiesNumber > beforeGenerateSourcesSelectorsCommonPropertiesPropertiesNumber);
            containsSelectorsProperties = FlumePropertiesGeneratorTestUtils.containsSelectorsProperties(flumePropertiesGenerator.getConfigurationInitialMap());
            Assert.assertTrue("The generation of the list of selectors common properties is not correct", containsSelectorsProperties);

        } catch (Exception e) {
            Assert.fail("An error has occurred [test07GenerateSourcesSelectorsCommonProperties] method");
            logger.error("An error has occurred [test07GenerateSourcesSelectorsCommonProperties] method", e);
        }
    }


    @Test
    public void test08GenerateSourcesSelectorsPartialProperties() {

        try {

            int beforeGenerateSourcesSelectorsPartialPropertiesPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());

            //Invoke method
            generateSelectorsPartialPropertiesMethod.invoke(flumePropertiesGenerator, objectNull);

            int afterGenerateSourcesSelectorsPartialPropertiesPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            Assert.assertTrue("The generation of the list of selectors partial properties is not correct", afterGenerateSourcesSelectorsPartialPropertiesPropertiesNumber > beforeGenerateSourcesSelectorsPartialPropertiesPropertiesNumber);

        } catch (Exception e) {
            Assert.fail("An error has occurred [test08GenerateSourcesSelectorsPartialProperties] method");
            logger.error("An error has occurred [test08GenerateSourcesSelectorsPartialProperties] method", e);
        }
    }


    @Test
    public void test09GenerateSourcesInterceptors() {

        try {    

            int beforeGenerateSourcesInterceptorsPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());

            Map<String, List<String>> mapSourcesInterceptors = flumePropertiesGenerator.getMapSourcesInterceptors();
            Assert.assertTrue("The generation of the list of interceptors is not correct", mapSourcesInterceptors.isEmpty());
            boolean containsSourcesInterceptors = FlumePropertiesGeneratorTestUtils.containsSourcesInterceptors(flumePropertiesGenerator.getConfigurationInitialMap());
            Assert.assertFalse("The generation of the list of interceptors is not correct", containsSourcesInterceptors);

            //Invoke method
            generateSourcesInterceptorsMethod.invoke(flumePropertiesGenerator, objectNull);

            mapSourcesInterceptors = flumePropertiesGenerator.getMapSourcesInterceptors();
            Assert.assertNotNull("The generation of the list of interceptors is not correct", mapSourcesInterceptors);
            Assert.assertTrue("The generation of the list of interceptors is not correct", mapSourcesInterceptors.size() > 0);

            int afterGenerateSourcesInterceptorsPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            Assert.assertTrue("The generation of the list of interceptors is not correct", afterGenerateSourcesInterceptorsPropertiesNumber > beforeGenerateSourcesInterceptorsPropertiesNumber);
            containsSourcesInterceptors = FlumePropertiesGeneratorTestUtils.containsSourcesInterceptors(flumePropertiesGenerator.getConfigurationInitialMap());
            Assert.assertTrue("The generation of the list of interceptors is not correct", containsSourcesInterceptors);

        } catch (Exception e) {
            Assert.fail("An error has occurred [test09GenerateSourcesInterceptors] method");
            logger.error("An error has occurred [test09GenerateSourcesInterceptors] method", e);
        }
    }  
    
    
    
    @Test
    public void test10GenerateSourcesInterceptorsCommonProperties() {

        try {    

            int beforeGenerateSourcesInterceptorsCommonPropertiesPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            boolean containsInterceptorsProperties = FlumePropertiesGeneratorTestUtils.containsInterceptorsProperties(flumePropertiesGenerator.getConfigurationInitialMap());
            Assert.assertFalse("The generation of the list of interceptors common properties is not correct", containsInterceptorsProperties);

            //Invoke method
            generateInterceptorsCommonPropertiesMethod.invoke(flumePropertiesGenerator, objectNull);

            int afterGenerateSourcesInterceptorsCommonPropertiesPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            Assert.assertTrue("The generation of the list of interceptors common properties is not correct", afterGenerateSourcesInterceptorsCommonPropertiesPropertiesNumber > beforeGenerateSourcesInterceptorsCommonPropertiesPropertiesNumber);
            containsInterceptorsProperties = FlumePropertiesGeneratorTestUtils.containsInterceptorsProperties(flumePropertiesGenerator.getConfigurationInitialMap());
            Assert.assertTrue("The generation of the list of interceptors common properties is not correct", containsInterceptorsProperties);
  
        } catch (Exception e) {
            Assert.fail("An error has occurred [test10GenerateSourcesInterceptorsCommonProperties] method");
            logger.error("An error has occurred [test10GenerateSourcesInterceptorsCommonProperties] method", e);
        }
    }    
    
    
    
    @Test
    public void test11GenerateSourcesInterceptorsPartialProperties() {

        try {    

            int beforeGenerateSourcesInterceptorsPartialPropertiesPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());

            //Invoke method
            generateInterceptorsPartialPropertiesMethod.invoke(flumePropertiesGenerator, objectNull);

            int afterGenerateSourcesInterceptorsPartialPropertiesPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            Assert.assertTrue("The generation of the list of interceptors partial properties is not correct", afterGenerateSourcesInterceptorsPartialPropertiesPropertiesNumber > beforeGenerateSourcesInterceptorsPartialPropertiesPropertiesNumber);

        } catch (Exception e) {
            Assert.fail("An error has occurred [test11GenerateSourcesInterceptorsPartialProperties] method");
            logger.error("An error has occurred [test11GenerateSourcesInterceptorsPartialProperties] method", e);
        }
    }     
    
    
    
    @Test
    public void test12GenerateElementsCommonProperties() {

        try {    

            //SOURCES
            int beforeGenerateSourcesElementsCommonPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            boolean containsSourcesProperties = FlumePropertiesGeneratorTestUtils.containsElementsProperties(flumePropertiesGenerator.getConfigurationInitialMap(), FlumeConfiguratorConstants.SOURCES_PROPERTY);
            Assert.assertFalse("The generation of the list of sources common properties is not correct", containsSourcesProperties);

            //Invoke method
            generateElementsCommonPropertiesMethod.invoke(flumePropertiesGenerator, FlumeConfiguratorConstants.SOURCES_COMMON_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SOURCES_PROPERTY, mapAgentSources);

            int afterGenerateSourcesElementsCommonPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            Assert.assertTrue("The generation of the list of sources common properties is not correct", afterGenerateSourcesElementsCommonPropertiesNumber > beforeGenerateSourcesElementsCommonPropertiesNumber);
            containsSourcesProperties = FlumePropertiesGeneratorTestUtils.containsElementsProperties(flumePropertiesGenerator.getConfigurationInitialMap(), FlumeConfiguratorConstants.SOURCES_PROPERTY);
            Assert.assertTrue("The generation of the list of sources common properties is not correct", containsSourcesProperties);


            //CHANNELS
            boolean containsChannelsProperties = FlumePropertiesGeneratorTestUtils.containsElementsProperties(flumePropertiesGenerator.getConfigurationInitialMap(), FlumeConfiguratorConstants.CHANNELS_PROPERTY);
            Assert.assertFalse("The generation of the list of channels common properties is not correct", containsChannelsProperties);

            //Invoke method
            generateElementsCommonPropertiesMethod.invoke(flumePropertiesGenerator, FlumeConfiguratorConstants.CHANNELS_COMMON_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.CHANNELS_PROPERTY, mapAgentChannels);

            int afterGenerateChannelsElementsCommonPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            Assert.assertTrue("The generation of the list of channels common properties is not correct", afterGenerateChannelsElementsCommonPropertiesNumber > afterGenerateSourcesElementsCommonPropertiesNumber);
            containsChannelsProperties = FlumePropertiesGeneratorTestUtils.containsElementsProperties(flumePropertiesGenerator.getConfigurationInitialMap(), FlumeConfiguratorConstants.CHANNELS_PROPERTY);
            Assert.assertTrue("The generation of the list of channels common properties is not correct", containsChannelsProperties);


            //SINKS
            boolean containsSinksProperties = FlumePropertiesGeneratorTestUtils.containsElementsProperties(flumePropertiesGenerator.getConfigurationInitialMap(), FlumeConfiguratorConstants.SINKS_PROPERTY);
            Assert.assertFalse("The generation of the list of sinks common properties is not correct", containsSinksProperties);

            //Invoke method
            generateElementsCommonPropertiesMethod.invoke(flumePropertiesGenerator, FlumeConfiguratorConstants.SINKS_COMMON_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SINKS_PROPERTY, mapAgentSinks);

            int afterGenerateSinksElementsCommonPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            Assert.assertTrue("The generation of the list of sinks common properties is not correct", afterGenerateSinksElementsCommonPropertiesNumber > afterGenerateChannelsElementsCommonPropertiesNumber);
            containsSinksProperties = FlumePropertiesGeneratorTestUtils.containsElementsProperties(flumePropertiesGenerator.getConfigurationInitialMap(), FlumeConfiguratorConstants.SINKS_PROPERTY);
            Assert.assertTrue("The generation of the list of sinks common properties is not correct", containsSinksProperties);


            //SINKGROUPS
            boolean containsSinkGroupsProperties = FlumePropertiesGeneratorTestUtils.containsElementsProperties(flumePropertiesGenerator.getConfigurationInitialMap(), FlumeConfiguratorConstants.SINKGROUPS_PROPERTY);
            Assert.assertFalse("The generation of the list of sinkgroups common properties is not correct", containsSinkGroupsProperties);

            //Invoke method
            generateElementsCommonPropertiesMethod.invoke(flumePropertiesGenerator, FlumeConfiguratorConstants.SINKGROUPS_COMMON_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SINKGROUPS_PROPERTY, mapAgentSinkGroups);

            int afterGenerateSinkGroupsElementsCommonPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            Assert.assertTrue("The generation of the list of sinkgroups common properties is not correct", afterGenerateSinkGroupsElementsCommonPropertiesNumber > afterGenerateSinksElementsCommonPropertiesNumber);
            containsSinkGroupsProperties = FlumePropertiesGeneratorTestUtils.containsElementsProperties(flumePropertiesGenerator.getConfigurationInitialMap(), FlumeConfiguratorConstants.SINKGROUPS_PROPERTY);
            Assert.assertTrue("The generation of the list of sinkgroups common properties is not correct", containsSinkGroupsProperties);


        } catch (Exception e) {
            Assert.fail("An error has occurred [test12GenerateElementsCommonProperties] method");
            logger.error("An error has occurred [test12GenerateElementsCommonProperties] method", e);
        }
    }      
    
    
    
    @Test
    public void test13GenerateElementsPartialProperties() {

        try {    

            //SOURCES
            int beforeGenerateSourcesElementsPartialPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            boolean containsSourcesProperties = FlumePropertiesGeneratorTestUtils.containsElementsProperties(flumePropertiesGenerator.getConfigurationInitialMap(), FlumeConfiguratorConstants.SOURCES_PROPERTY);
            Assert.assertTrue("The generation of the list of sources partial properties is not correct", containsSourcesProperties);

            //Invoke method
            generateElementsPartialPropertiesMethod.invoke(flumePropertiesGenerator, FlumeConfiguratorConstants.SOURCES_PARTIAL_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SOURCES_PROPERTY, mapAgentSources);

            int afterGenerateSourcesElementsPartialPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            Assert.assertTrue("The generation of the list of sources partial properties is not correct", afterGenerateSourcesElementsPartialPropertiesNumber > beforeGenerateSourcesElementsPartialPropertiesNumber);
            containsSourcesProperties = FlumePropertiesGeneratorTestUtils.containsElementsProperties(flumePropertiesGenerator.getConfigurationInitialMap(), FlumeConfiguratorConstants.SOURCES_PROPERTY);
            Assert.assertTrue("The generation of the list of sources partial properties is not correct", containsSourcesProperties);


            //CHANNELS
            int beforeGenerateChannelsElementsPartialPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            boolean containsChannelsProperties = FlumePropertiesGeneratorTestUtils.containsElementsProperties(flumePropertiesGenerator.getConfigurationInitialMap(), FlumeConfiguratorConstants.CHANNELS_PROPERTY);
            Assert.assertTrue("The generation of the list of channels partial properties is not correct", containsChannelsProperties);

            //Invoke method
            generateElementsPartialPropertiesMethod.invoke(flumePropertiesGenerator, FlumeConfiguratorConstants.CHANNELS_PARTIAL_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.CHANNELS_PROPERTY, mapAgentChannels);

            int afterGenerateChannelsElementsPartialPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            Assert.assertTrue("The generation of the list of channels partial properties is not correct", afterGenerateChannelsElementsPartialPropertiesNumber > beforeGenerateChannelsElementsPartialPropertiesNumber);
            containsChannelsProperties = FlumePropertiesGeneratorTestUtils.containsElementsProperties(flumePropertiesGenerator.getConfigurationInitialMap(), FlumeConfiguratorConstants.CHANNELS_PROPERTY);
            Assert.assertTrue("The generation of the list of channels partial properties is not correct", containsChannelsProperties);


            //SINKS
            int beforeGenerateSinksElementsPartialPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            boolean containsSinksProperties = FlumePropertiesGeneratorTestUtils.containsElementsProperties(flumePropertiesGenerator.getConfigurationInitialMap(), FlumeConfiguratorConstants.SINKS_PROPERTY);
            Assert.assertTrue("The generation of the list of sinks partial properties is not correct", containsSinksProperties);

            //Invoke method
            generateElementsPartialPropertiesMethod.invoke(flumePropertiesGenerator, FlumeConfiguratorConstants.SINKS_PARTIAL_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SINKS_PROPERTY, mapAgentSinks);

            int afterGenerateSinksElementsPartialPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            Assert.assertTrue("The generation of the list of sinks partial properties is not correct", afterGenerateSinksElementsPartialPropertiesNumber > beforeGenerateSinksElementsPartialPropertiesNumber);
            containsSinksProperties = FlumePropertiesGeneratorTestUtils.containsElementsProperties(flumePropertiesGenerator.getConfigurationInitialMap(), FlumeConfiguratorConstants.SINKS_PROPERTY);
            Assert.assertTrue("The generation of the list of sinks partial properties is not correct", containsSinksProperties);


            //SINKGROUPS
            int beforeGenerateSinkGroupsElementsPartialPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            boolean containsSinkGroupsProperties = FlumePropertiesGeneratorTestUtils.containsElementsProperties(flumePropertiesGenerator.getConfigurationInitialMap(), FlumeConfiguratorConstants.SINKGROUPS_PROPERTY);
            Assert.assertTrue("The generation of the list of sinkgroups partial properties is not correct", containsSinkGroupsProperties);

            //Invoke method
            generateElementsPartialPropertiesMethod.invoke(flumePropertiesGenerator, FlumeConfiguratorConstants.SINKGROUPS_PARTIAL_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SINKGROUPS_PROPERTY, mapAgentSinkGroups);

            int afterGenerateSinkGroupsElementsPartialPropertiesNumber = FlumePropertiesGeneratorTestUtils.calculatePropertiesTotalNumber(flumePropertiesGenerator.getConfigurationInitialMap());
            Assert.assertTrue("The generation of the list of sinkgroups partial properties is not correct", afterGenerateSinkGroupsElementsPartialPropertiesNumber > beforeGenerateSinkGroupsElementsPartialPropertiesNumber);
            containsSinkGroupsProperties = FlumePropertiesGeneratorTestUtils.containsElementsProperties(flumePropertiesGenerator.getConfigurationInitialMap(), FlumeConfiguratorConstants.SINKGROUPS_PROPERTY);
            Assert.assertTrue("The generation of the list of sinkgroups partial properties is not correct", containsSinkGroupsProperties);

        } catch (Exception e) {
            Assert.fail("An error has occurred [test13GenerateElementsPartialProperties] method");
            logger.error("An error has occurred [test13GenerateElementsPartialProperties] method", e);
        }
    }  
    
    
    @Test
    public void test14GenerateFinalStructureMaps() {

        try {

            Map<String, AgentConfigurationProperties> configurationFinalMap = flumePropertiesGenerator.getConfigurationFinalMap();
            Assert.assertTrue("The generation of the final structure map is not correct", configurationFinalMap.size() == 0);

            //Invoke method
            generateFinalStructureMapMethod.invoke(flumePropertiesGenerator, objectNull);

            configurationFinalMap = flumePropertiesGenerator.getConfigurationFinalMap();
            Assert.assertTrue("The generation of the final structure map is not correct", configurationFinalMap.size() > 0);

            for (String agentName : configurationFinalMap.keySet()) {
                AgentConfigurationProperties agentConfigurationProperties = configurationFinalMap.get(agentName);

                Assert.assertTrue("The generation of the final structure map is not correct", agentConfigurationProperties.getListGeneralProperties().size() > 0);
                Assert.assertTrue("The generation of the final structure map is not correct", agentConfigurationProperties.getMapGroupProperties().size() > 0);
            }

        } catch (Exception e) {
            Assert.fail("An error has occurred [test14GenerateFinalStructureMaps] method");
            logger.error("An error has occurred [test14GenerateFinalStructureMaps] method", e);
        }
    }  
    
   
    
    @Test
    public void test15WriteConfigurationFilesInvalidPath() {

        try {

            //Invoke method
            writeConfigurationFilesMethod.invoke(flumePropertiesGenerator, objectNull);

            //The exception must be thrown
            Assert.fail("The write of the Flume configuration file(s) is not correct");

        } catch (InvocationTargetException ite) {
               if (!(ite.getCause() instanceof InvalidPathException)) {
                   Assert.fail("An error has occurred [test15WriteConfigurationFilesInvalidPath] method");
                   logger.error("An error has occurred [test15WriteConfigurationFilesInvalidPath] method", ite);
               }
        } catch (Exception e) {
            Assert.fail("An error has occurred [test15WriteConfigurationFilesInvalidPath] method");
            logger.error("An error has occurred [test15WriteConfigurationFilesInvalidPath] method", e);
        }
    }    
    
    
    @Test
    public void test16WriteConfigurationFiles() {

        try {

            //Check output directory & one single configuration file
            FlumePropertiesGenerator.setPathConfigurationGeneratedFile(OUTPUT_GENERATED_FILE_PATH_DIRECTORY);
            FlumePropertiesGenerator.setMultipleAgentConfigurationFiles(false);

            //Invoke method
            writeConfigurationFilesMethod.invoke(flumePropertiesGenerator, objectNull);


            //Check output directory & several configuration files
            FlumePropertiesGenerator.setPathConfigurationGeneratedFile(OUTPUT_GENERATED_FILE_PATH_DIRECTORY);
            FlumePropertiesGenerator.setMultipleAgentConfigurationFiles(true);

            //Invoke method
            writeConfigurationFilesMethod.invoke(flumePropertiesGenerator, objectNull);


            //Check output file & one single configuration file
            FlumePropertiesGenerator.setPathConfigurationGeneratedFile(OUTPUT_GENERATED_FILE_PATH_FILE);
            FlumePropertiesGenerator.setMultipleAgentConfigurationFiles(false);

            //Invoke method
            writeConfigurationFilesMethod.invoke(flumePropertiesGenerator, objectNull);


            //Check output file & several configuration files
            FlumePropertiesGenerator.setPathConfigurationGeneratedFile(OUTPUT_GENERATED_FILE_PATH_FILE);
            FlumePropertiesGenerator.setMultipleAgentConfigurationFiles(true);

            //Invoke method
            writeConfigurationFilesMethod.invoke(flumePropertiesGenerator, objectNull);

        } catch (Exception e) {
            Assert.fail("An error has occurred [test16WriteConfigurationFiles] method");
            logger.error("An error has occurred [test16WriteConfigurationFiles] method", e);
        }
    }   
    
    
    @Test
    public void test17BuildConfigurationMap() {

        try {  

            FlumePropertiesGenerator.setElementsCharacterSeparator(DEFAULT_SEPARATOR);
            FlumePropertiesGenerator.setAddComments(true);
            FlumePropertiesGenerator.setMultipleAgentConfigurationFiles(true);
            FlumePropertiesGenerator.setPathConfigurationGeneratedFile(OUTPUT_GENERATED_FILE_PATH_DIRECTORY);

            //Build with Validation Error Properties
            FlumePropertiesGenerator.setPathBaseConfigurationProperties(PROPERTIES_FILE_VALIDATION_ERROR_PATH);

            //Invoke method
            boolean isCorrect = (boolean) buildConfigurationMapMethod.invoke(flumePropertiesGenerator, objectNull);
            Assert.assertFalse("The Flume configuration file has not been built correctly", isCorrect);

            //Build correct
            FlumePropertiesGenerator.setPathBaseConfigurationProperties(PROPERTIES_FILE_PATH);

            //Invoke method
            isCorrect = (boolean) buildConfigurationMapMethod.invoke(flumePropertiesGenerator, objectNull);
            Assert.assertTrue("The Flume configuration file has not been built correctly", isCorrect);
            
        } catch (Exception e) {
            Assert.fail("An error has occurred [test17BuildConfigurationMap] method");
            logger.error("An error has occurred [test17BuildConfigurationMap] method", e);
        }
    }


    @Test
    public void test18BuildConfigurationMapFromStringProperties() {

        try {

            byte[] baseProperties = Files.readAllBytes(Paths.get(PROPERTIES_FILE_VALIDATION_ERROR_PATH));
            String basePropertiesString = new String(baseProperties);

            //Invoke method with Validation Error Properties
            String configurationMapString = (String) buildConfigurationMapFromStringPropertiesMethod.invoke(flumePropertiesGenerator, basePropertiesString, DEFAULT_SEPARATOR, true, true, OUTPUT_GENERATED_FILE_PATH_DIRECTORY, true);
            Assert.assertNull("The Flume configuration file has not been built correctly", configurationMapString);

            baseProperties = Files.readAllBytes(Paths.get(PROPERTIES_FILE_PATH));
            basePropertiesString = new String(baseProperties);

            //Invoke method
            configurationMapString = (String) buildConfigurationMapFromStringPropertiesMethod.invoke(flumePropertiesGenerator, basePropertiesString, DEFAULT_SEPARATOR, true, true, OUTPUT_GENERATED_FILE_PATH_DIRECTORY, true);
            Assert.assertNotNull("The Flume configuration file has not been built correctly", configurationMapString);
            Assert.assertFalse("The Flume configuration file has not been built correctly", configurationMapString.isEmpty());

        } catch (Exception e) {
            Assert.fail("An error has occurred [test18BuildConfigurationMapFromStringProperties] method");
            logger.error("An error has occurred [test18BuildConfigurationMapFromStringProperties] method", e);
        }
    }

}
