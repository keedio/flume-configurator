package org.keedio.flume.configurator.builder;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.InvalidPathException;
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


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConfigurationBuilderTest {
	

    private static final String PROPERTIES_FILE_PATH = "src/test/resources/FlumeConfigurationExample.properties";
    private static final String PROPERTIES_FILE_VALIDATION_ERROR_PATH = "src/test/resources/FlumeConfigurationExampleValidationError.properties";
    private static final String OUTPUT_GENERATED_FILE_PATH_DIRECTORY = ".";
    private static final String OUTPUT_GENERATED_FILE_PATH_FILE = "." + File.separator + "outputFile.conf";
    private static final String DEFAULT_SEPARATOR = ";";
    private static final Object[] objectNull = null; //to prevent warning
    private static final Class<?>[] classNull = null; //to prevent warning
    
    private static ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
    
    private static Map<String, List<String>> mapAgentSources;
    private static Map<String, List<String>> mapAgentChannels;
    private static Map<String, List<String>> mapAgentSinks;
    
    private static Method loadPropertiesFileMethod;
    private static Method generateAgentsListMethod;
    private static Method generateAgentElementsMethod;
    private static Method generateSourcesInterceptorsMethod;
    private static Method generateInterceptorsCommonPropertiesMethod;
    private static Method generateInterceptorsPartialPropertiesMethod;
    private static Method generateElementsCommonPropertiesMethod;
    private static Method generateElementsPartialPropertiesMethod;
    private static Method generateFinalStructureMapMethod;
    private static Method writeConfigurationFilesMethod;
    private static Method buildConfigurationMapMethod;
    
    @BeforeClass
    public static void makePrivateMethodsAccesibleByReflection() {

    	try {
    	ConfigurationBuilder.setElementsCharacterSeparator(DEFAULT_SEPARATOR);
    	
    	
    	//loadPropertiesFile is a private method. Access by reflection
        loadPropertiesFileMethod = ConfigurationBuilder.class.getDeclaredMethod("loadPropertiesFile", classNull);
        loadPropertiesFileMethod.setAccessible(true);

    	//generateAgentsList is a private method. Access by reflection
        generateAgentsListMethod = ConfigurationBuilder.class.getDeclaredMethod("generateAgentsList", classNull);
        generateAgentsListMethod.setAccessible(true);
        
        //generateAgentElements is a private method. Access by reflection
        Class<?>[] argsGenerateAgentElements = new Class[2];
        argsGenerateAgentElements[0] = String.class;
        argsGenerateAgentElements[1] = String.class;	
        
        generateAgentElementsMethod = ConfigurationBuilder.class.getDeclaredMethod("generateAgentElements", argsGenerateAgentElements);
        generateAgentElementsMethod.setAccessible(true);
        
        //generateSourcesInterceptors is a private method. Access by reflection
        generateSourcesInterceptorsMethod = ConfigurationBuilder.class.getDeclaredMethod("generateSourcesInterceptors", classNull);
        generateSourcesInterceptorsMethod.setAccessible(true); 
        
        //generateSourcesInterceptorsCommonProperties is a private method. Access by reflection
        generateInterceptorsCommonPropertiesMethod = ConfigurationBuilder.class.getDeclaredMethod("generateInterceptorsCommonProperties", classNull);
        generateInterceptorsCommonPropertiesMethod.setAccessible(true); 
        
        //generateSourcesInterceptorsPartialProperties is a private method. Access by reflection
        generateInterceptorsPartialPropertiesMethod = ConfigurationBuilder.class.getDeclaredMethod("generateInterceptorsPartialProperties", classNull);
        generateInterceptorsPartialPropertiesMethod.setAccessible(true);
        
        //generateElementsCommonProperties is a private method. Access by reflection
        Class<?>[] argsGenerateElementsCommonProperties = new Class[3];
        argsGenerateElementsCommonProperties[0] = String.class;
        argsGenerateElementsCommonProperties[1] = String.class;
        argsGenerateElementsCommonProperties[2] = Map.class;
        
        generateElementsCommonPropertiesMethod = ConfigurationBuilder.class.getDeclaredMethod("generateElementsCommonProperties", argsGenerateElementsCommonProperties);
        generateElementsCommonPropertiesMethod.setAccessible(true);       

        //generateElementsPartialProperties is a private method. Access by reflection
        Class<?>[] argsGenerateElementsPartialProperties = new Class[3];
        argsGenerateElementsPartialProperties[0] = String.class;
        argsGenerateElementsPartialProperties[1] = String.class;
        argsGenerateElementsPartialProperties[2] = Map.class;
        
        generateElementsPartialPropertiesMethod = ConfigurationBuilder.class.getDeclaredMethod("generateElementsPartialProperties", argsGenerateElementsPartialProperties);
        generateElementsPartialPropertiesMethod.setAccessible(true);
        
        //generateFinalStructureMap is a private method. Access by reflection
        generateFinalStructureMapMethod = ConfigurationBuilder.class.getDeclaredMethod("generateFinalStructureMap", classNull);
        generateFinalStructureMapMethod.setAccessible(true);
        
        //writeConfigurationFiles is a private method. Access by reflection
        writeConfigurationFilesMethod = ConfigurationBuilder.class.getDeclaredMethod("writeConfigurationFiles", classNull);
        writeConfigurationFilesMethod.setAccessible(true); 
        
        //buildConfigurationMap is a private method. Access by reflection
        buildConfigurationMapMethod = ConfigurationBuilder.class.getDeclaredMethod("buildConfigurationMap", classNull);
        buildConfigurationMapMethod.setAccessible(true); 
        
    	} catch (Exception e) {
    		Assert.fail("An error has occurred [@BeforeClass makePrivateMethodsAccesibleByReflection] method");
    	}
    }    
    

    
  
    @Test
    public void test_01_testLoadPropertiesFileFileNotFound() {
    	
        try {
        	
        	Properties flumeConfigurationProperties = ConfigurationBuilder.getFlumeConfigurationProperties();
        	Assert.assertTrue("The load of the properties is not correct", flumeConfigurationProperties.size() == 0);
        	
        	//Check not file found exception
        	String propertiesFilePathError = "src/test/resources/FileNotFound.properties";
        	ConfigurationBuilder.setPathConfigurationProperties(propertiesFilePathError);
        	
        	//Invoke method
        	loadPropertiesFileMethod.invoke(configurationBuilder, objectNull);
        	
        	//The exception must be thrown
        	Assert.fail("The load of the properties is not correct");

        } catch (InvocationTargetException ite) {
        	   if (!(ite.getCause() instanceof FileNotFoundException)) {
				   Assert.fail("An error has occurred [test_01_testLoadPropertiesFileFileNotFound] method");
        	   }
	    } catch (Exception e) {
	        Assert.fail("An error has occurred [test_01_testLoadPropertiesFileFileNotFound] method");
	    }
    }
    
    
    
    @Test
    public void test_02_testLoadPropertiesFile() {
    	
        try {
        	
        	Properties flumeConfigurationProperties = ConfigurationBuilder.getFlumeConfigurationProperties();
        	Assert.assertTrue("The load of the properties is not correct", flumeConfigurationProperties.size() == 0);
        	
        	ConfigurationBuilder.setPathConfigurationProperties(PROPERTIES_FILE_PATH);
        	
        	//Invoke method
        	loadPropertiesFileMethod.invoke(configurationBuilder, objectNull);
      	
        	flumeConfigurationProperties = ConfigurationBuilder.getFlumeConfigurationProperties();
        	Assert.assertTrue("The load of the properties is not correct", flumeConfigurationProperties.size() > 0);        	

	    } catch (Exception e) {
	        Assert.fail("An error has occurred [test_02_testLoadPropertiesFile] method");
	    }
    }    
   
    
    
    @Test
    public void test_03_testGenerateAgentsList() {
    	
        try {
        	
        	int beforeGenerateAgentsListPropertiesNumber = ConfigurationBuilderTestUtils.calculatePropertiesTotalNumber(configurationBuilder.getConfigurationInitialMap());
        	Assert.assertEquals("The generation of the list of agents is not correct", beforeGenerateAgentsListPropertiesNumber, 0);

        	List<String> agentList = configurationBuilder.getAgentsList();
        	Assert.assertNull("The generation of the list of agents is not correct", agentList);
        	
        	Map<String,LinkedProperties> configurationInitialMap = configurationBuilder.getConfigurationInitialMap();
        	Assert.assertTrue("The generation of the list of agents is not correct", configurationInitialMap.size() == 0);
        	
        	//Invoke method
        	generateAgentsListMethod.invoke(configurationBuilder, objectNull);
      	
        	agentList = configurationBuilder.getAgentsList();
        	Assert.assertNotNull("The generation of the list of agents is not correct", agentList);
        	Assert.assertTrue("The generation of the list of agents is not correct", agentList.size() > 0);
        	
        	configurationInitialMap = configurationBuilder.getConfigurationInitialMap();
         	Assert.assertTrue("The generation of the list of agents is not correct", configurationInitialMap.size() > 0);

        	int afterGenerateAgentsListPropertiesNumber = ConfigurationBuilderTestUtils.calculatePropertiesTotalNumber(configurationBuilder.getConfigurationInitialMap());
        	Assert.assertTrue("The generation of the list of agents is not correct", afterGenerateAgentsListPropertiesNumber == beforeGenerateAgentsListPropertiesNumber);

	    } catch (Exception e) {
	        Assert.fail("An error has occurred [test_03_testGenerateAgentsList] method");
	    }
    }  
    
    
    
    @Test
    @SuppressWarnings("unchecked")
    public void test_04_testGenerateAgentElements() {
    	
        try {
        	

        	//SOURCES
        	int beforeGenerateSourcesElementsPropertiesNumber = ConfigurationBuilderTestUtils.calculatePropertiesTotalNumber(configurationBuilder.getConfigurationInitialMap());
        	boolean containsAgentElementsSources = ConfigurationBuilderTestUtils.containsAgentElements(configurationBuilder.getConfigurationInitialMap(), FlumeConfiguratorConstants.SOURCES_PROPERTY);        	
        	Assert.assertFalse("The generation of the list of sources is not correct", containsAgentElementsSources);
        	
        	//Invoke method
        	mapAgentSources = (Map<String, List<String>>) generateAgentElementsMethod.invoke(configurationBuilder, FlumeConfiguratorConstants.SOURCES_LIST_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SOURCES_PROPERTY);
        	Assert.assertTrue("The generation of the list of sources is not correct", mapAgentSources.size() > 0);
        	configurationBuilder.setMapAgentSources(mapAgentSources);
        	
        	int afterGenerateSourcesElementsPropertiesNumber = ConfigurationBuilderTestUtils.calculatePropertiesTotalNumber(configurationBuilder.getConfigurationInitialMap());
        	Assert.assertTrue("The generation of the list of sources is not correct", afterGenerateSourcesElementsPropertiesNumber > beforeGenerateSourcesElementsPropertiesNumber);
        	containsAgentElementsSources = ConfigurationBuilderTestUtils.containsAgentElements(configurationBuilder.getConfigurationInitialMap(), FlumeConfiguratorConstants.SOURCES_PROPERTY);        	
        	Assert.assertTrue("The generation of the list of sources is not correct", containsAgentElementsSources);
        	
        	//CHANNELS
        	int beforeGenerateChannelsElementsPropertiesNumber = ConfigurationBuilderTestUtils.calculatePropertiesTotalNumber(configurationBuilder.getConfigurationInitialMap());
        	boolean containsAgentElementsChannels = ConfigurationBuilderTestUtils.containsAgentElements(configurationBuilder.getConfigurationInitialMap(), FlumeConfiguratorConstants.CHANNELS_PROPERTY);        	
        	Assert.assertFalse("The generation of the list of channels is not correct", containsAgentElementsChannels);

        	//Invoke method
        	mapAgentChannels = (Map<String, List<String>>) generateAgentElementsMethod.invoke(configurationBuilder, FlumeConfiguratorConstants.CHANNELS_LIST_PROPERTIES_PREFIX, FlumeConfiguratorConstants.CHANNELS_PROPERTY);
        	Assert.assertTrue("The generation of the list of channels is not correct", mapAgentChannels.size() > 0);

        	int afterGenerateChannelsElementsPropertiesNumber = ConfigurationBuilderTestUtils.calculatePropertiesTotalNumber(configurationBuilder.getConfigurationInitialMap());
        	Assert.assertTrue("The generation of the list of channels is not correct", afterGenerateChannelsElementsPropertiesNumber > beforeGenerateChannelsElementsPropertiesNumber);
        	containsAgentElementsChannels = ConfigurationBuilderTestUtils.containsAgentElements(configurationBuilder.getConfigurationInitialMap(), FlumeConfiguratorConstants.CHANNELS_PROPERTY);        	
        	Assert.assertTrue("The generation of the list of channels is not correct", containsAgentElementsChannels);

        	
        	//SINKS
        	int beforeGenerateSinksElementsPropertiesNumber = ConfigurationBuilderTestUtils.calculatePropertiesTotalNumber(configurationBuilder.getConfigurationInitialMap());
        	boolean containsAgentElementsSinks = ConfigurationBuilderTestUtils.containsAgentElements(configurationBuilder.getConfigurationInitialMap(), FlumeConfiguratorConstants.SINKS_PROPERTY);        	
        	Assert.assertFalse("The generation of the list of sinks is not correct", containsAgentElementsSinks);

        	//Invoke method
        	mapAgentSinks = (Map<String, List<String>>) generateAgentElementsMethod.invoke(configurationBuilder, FlumeConfiguratorConstants.SINKS_LIST_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SINKS_PROPERTY);
        	Assert.assertTrue("The generation of the list of sinks is not correct", mapAgentSinks.size() > 0);

        	int afterGenerateSinksElementsPropertiesNumber = ConfigurationBuilderTestUtils.calculatePropertiesTotalNumber(configurationBuilder.getConfigurationInitialMap());
        	Assert.assertTrue("The generation of the list of sinks is not correct", afterGenerateSinksElementsPropertiesNumber > beforeGenerateSinksElementsPropertiesNumber);
        	containsAgentElementsSinks = ConfigurationBuilderTestUtils.containsAgentElements(configurationBuilder.getConfigurationInitialMap(), FlumeConfiguratorConstants.SINKS_PROPERTY);        	
        	Assert.assertTrue("The generation of the list of sinks is not correct", containsAgentElementsSinks);

	    } catch (Exception e) {
	        Assert.fail("An error has occurred [test_04_testGenerateAgentElements] method");
	    }
    }  
    
    
    
    @Test
    public void test_05_testGenerateSourcesInterceptors() {
    	
        try {    
        	
        	int beforeGenerateSourcesInterceptorsPropertiesNumber = ConfigurationBuilderTestUtils.calculatePropertiesTotalNumber(configurationBuilder.getConfigurationInitialMap());

        	Map<String, List<String>> mapSourcesInterceptors = configurationBuilder.getMapSourcesInterceptors();
        	Assert.assertNull("The generation of the list of interceptors is not correct", mapSourcesInterceptors);
        	boolean containsSourcesInterceptors = ConfigurationBuilderTestUtils.containsSourcesInterceptors(configurationBuilder.getConfigurationInitialMap());        	
        	Assert.assertFalse("The generation of the list of interceptors is not correct", containsSourcesInterceptors);
        	
        	//Invoke method
        	generateSourcesInterceptorsMethod.invoke(configurationBuilder, objectNull);
      	
        	mapSourcesInterceptors = configurationBuilder.getMapSourcesInterceptors();
        	Assert.assertNotNull("The generation of the list of interceptors is not correct", mapSourcesInterceptors);
        	Assert.assertTrue("The generation of the list of interceptors is not correct", mapSourcesInterceptors.size() > 0);
        	
        	int afterGenerateSourcesInterceptorsPropertiesNumber = ConfigurationBuilderTestUtils.calculatePropertiesTotalNumber(configurationBuilder.getConfigurationInitialMap());
        	Assert.assertTrue("The generation of the list of sources is not correct", afterGenerateSourcesInterceptorsPropertiesNumber > beforeGenerateSourcesInterceptorsPropertiesNumber);
        	containsSourcesInterceptors = ConfigurationBuilderTestUtils.containsSourcesInterceptors(configurationBuilder.getConfigurationInitialMap());        	
        	Assert.assertTrue("The generation of the list of interceptors is not correct", containsSourcesInterceptors);        	
        	
	    } catch (Exception e) {
	        Assert.fail("An error has occurred [test_05_testGenerateSourcesInterceptors] method");
	    }
    }  
    
    
    
    @Test
    public void test_06_testGenerateSourcesInterceptorsCommonProperties() {
    	
        try {    
        	
        	int beforeGenerateSourcesInterceptorsCommonPropertiesPropertiesNumber = ConfigurationBuilderTestUtils.calculatePropertiesTotalNumber(configurationBuilder.getConfigurationInitialMap());
        	boolean containsInterceptorsProperties = ConfigurationBuilderTestUtils.containsInterceptorsProperties(configurationBuilder.getConfigurationInitialMap());        	
        	Assert.assertFalse("The generation of the list of interceptors common properties is not correct", containsInterceptorsProperties);
        	
        	//Invoke method
        	generateInterceptorsCommonPropertiesMethod.invoke(configurationBuilder, objectNull);
        	
        	int afterGenerateSourcesInterceptorsCommonPropertiesPropertiesNumber = ConfigurationBuilderTestUtils.calculatePropertiesTotalNumber(configurationBuilder.getConfigurationInitialMap());
        	Assert.assertTrue("The generation of the list of interceptors common properties is not correct", afterGenerateSourcesInterceptorsCommonPropertiesPropertiesNumber > beforeGenerateSourcesInterceptorsCommonPropertiesPropertiesNumber);
        	containsInterceptorsProperties = ConfigurationBuilderTestUtils.containsInterceptorsProperties(configurationBuilder.getConfigurationInitialMap());        	
        	Assert.assertTrue("The generation of the list of interceptors common properties is not correct", containsInterceptorsProperties);
  
	    } catch (Exception e) {
	        Assert.fail("An error has occurred [test_06_testGenerateSourcesInterceptorsCommonProperties] method");
	    }
    }    
    
    
    
    @Test
    public void test_07_testGenerateSourcesInterceptorsPartialProperties() {
    	
        try {    
        	
        	int beforeGenerateSourcesInterceptorsCommonPropertiesPropertiesNumber = ConfigurationBuilderTestUtils.calculatePropertiesTotalNumber(configurationBuilder.getConfigurationInitialMap());
       	
        	//Invoke method
        	generateInterceptorsPartialPropertiesMethod.invoke(configurationBuilder, objectNull);
        	
        	int afterGenerateSourcesInterceptorsCommonPropertiesPropertiesNumber = ConfigurationBuilderTestUtils.calculatePropertiesTotalNumber(configurationBuilder.getConfigurationInitialMap());
        	Assert.assertTrue("The generation of the list of interceptors partial properties is not correct", afterGenerateSourcesInterceptorsCommonPropertiesPropertiesNumber > beforeGenerateSourcesInterceptorsCommonPropertiesPropertiesNumber);
        	
	    } catch (Exception e) {
	        Assert.fail("An error has occurred [test_07_testGenerateSourcesInterceptorsPartialProperties] method");
	    }
    }     
    
    
    
    @Test
    public void test_08_testGenerateElementsCommonProperties() {
    	
        try {    
        	
        	//SOURCES
        	int beforeGenerateSourcesElementsCommonPropertiesNumber = ConfigurationBuilderTestUtils.calculatePropertiesTotalNumber(configurationBuilder.getConfigurationInitialMap());
        	boolean containsSourcesProperties = ConfigurationBuilderTestUtils.containsElementsProperties(configurationBuilder.getConfigurationInitialMap(), FlumeConfiguratorConstants.SOURCES_PROPERTY);        	
        	Assert.assertFalse("The generation of the list of sources common properties is not correct", containsSourcesProperties);
        	
        	//Invoke method
        	generateElementsCommonPropertiesMethod.invoke(configurationBuilder, FlumeConfiguratorConstants.SOURCES_COMMON_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SOURCES_PROPERTY, mapAgentSources);
        	
        	int afterGenerateSourcesElementsCommonPropertiesNumber = ConfigurationBuilderTestUtils.calculatePropertiesTotalNumber(configurationBuilder.getConfigurationInitialMap());
        	Assert.assertTrue("The generation of the list of sources common properties is not correct", afterGenerateSourcesElementsCommonPropertiesNumber > beforeGenerateSourcesElementsCommonPropertiesNumber);
        	containsSourcesProperties = ConfigurationBuilderTestUtils.containsElementsProperties(configurationBuilder.getConfigurationInitialMap(), FlumeConfiguratorConstants.SOURCES_PROPERTY);        	
        	Assert.assertTrue("The generation of the list of sources common properties is not correct", containsSourcesProperties);
        	   
        	
        	//CHANNELS
        	boolean containsChannelsProperties = ConfigurationBuilderTestUtils.containsElementsProperties(configurationBuilder.getConfigurationInitialMap(), FlumeConfiguratorConstants.CHANNELS_PROPERTY);        	
        	Assert.assertFalse("The generation of the list of channels common properties is not correct", containsChannelsProperties);
        	        	
        	//Invoke method
        	generateElementsCommonPropertiesMethod.invoke(configurationBuilder, FlumeConfiguratorConstants.CHANNELS_COMMON_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.CHANNELS_PROPERTY, mapAgentChannels);
        	
        	int afterGenerateChannelsElementsCommonPropertiesNumber = ConfigurationBuilderTestUtils.calculatePropertiesTotalNumber(configurationBuilder.getConfigurationInitialMap());
        	Assert.assertTrue("The generation of the list of channels common properties is not correct", afterGenerateChannelsElementsCommonPropertiesNumber > afterGenerateSourcesElementsCommonPropertiesNumber);
        	containsChannelsProperties = ConfigurationBuilderTestUtils.containsElementsProperties(configurationBuilder.getConfigurationInitialMap(), FlumeConfiguratorConstants.CHANNELS_PROPERTY);        	
        	Assert.assertTrue("The generation of the list of channels common properties is not correct", containsChannelsProperties);
        	           	
        	
        	//SINKS
        	boolean containsSinksProperties = ConfigurationBuilderTestUtils.containsElementsProperties(configurationBuilder.getConfigurationInitialMap(), FlumeConfiguratorConstants.SINKS_PROPERTY);        	
        	Assert.assertFalse("The generation of the list of sinks common properties is not correct", containsSinksProperties);
        	
        	//Invoke method
        	generateElementsCommonPropertiesMethod.invoke(configurationBuilder, FlumeConfiguratorConstants.SINKS_COMMON_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SINKS_PROPERTY, mapAgentSinks);

        	int afterGenerateSinksElementsCommonPropertiesNumber = ConfigurationBuilderTestUtils.calculatePropertiesTotalNumber(configurationBuilder.getConfigurationInitialMap());
        	Assert.assertTrue("The generation of the list of sinks common properties is not correct", afterGenerateSinksElementsCommonPropertiesNumber > afterGenerateChannelsElementsCommonPropertiesNumber);
        	containsSinksProperties = ConfigurationBuilderTestUtils.containsElementsProperties(configurationBuilder.getConfigurationInitialMap(), FlumeConfiguratorConstants.SINKS_PROPERTY);        	
        	Assert.assertTrue("The generation of the list of sinks common properties is not correct", containsSinksProperties);
        	     
	    } catch (Exception e) {
	        Assert.fail("An error has occurred [test_08_testGenerateElementsCommonProperties] method");
	    }
    }      
    
    
    
    @Test
    public void test_09_testGenerateElementsPartialProperties() {
    	
        try {    
        	
        	//SOURCES    
        	int beforeGenerateSourcesElementsPartialPropertiesNumber = ConfigurationBuilderTestUtils.calculatePropertiesTotalNumber(configurationBuilder.getConfigurationInitialMap());
        	boolean containsSourcesProperties = ConfigurationBuilderTestUtils.containsElementsProperties(configurationBuilder.getConfigurationInitialMap(), FlumeConfiguratorConstants.SOURCES_PROPERTY);        	
        	Assert.assertTrue("The generation of the list of sources partial properties is not correct", containsSourcesProperties);
        	        	
        	//Invoke method
        	generateElementsPartialPropertiesMethod.invoke(configurationBuilder, FlumeConfiguratorConstants.SOURCES_PARTIAL_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SOURCES_PROPERTY, mapAgentSources);
        	
        	int afterGenerateSourcesElementsPartialPropertiesNumber = ConfigurationBuilderTestUtils.calculatePropertiesTotalNumber(configurationBuilder.getConfigurationInitialMap());
        	Assert.assertTrue("The generation of the list of sources partial properties is not correct", afterGenerateSourcesElementsPartialPropertiesNumber > beforeGenerateSourcesElementsPartialPropertiesNumber);
        	containsSourcesProperties = ConfigurationBuilderTestUtils.containsElementsProperties(configurationBuilder.getConfigurationInitialMap(), FlumeConfiguratorConstants.SOURCES_PROPERTY);        	
        	Assert.assertTrue("The generation of the list of sources partial properties is not correct", containsSourcesProperties);

        	
        	//CHANNELS    
        	int beforeGenerateChannelsElementsPartialPropertiesNumber = ConfigurationBuilderTestUtils.calculatePropertiesTotalNumber(configurationBuilder.getConfigurationInitialMap());
        	boolean containsChannelsProperties = ConfigurationBuilderTestUtils.containsElementsProperties(configurationBuilder.getConfigurationInitialMap(), FlumeConfiguratorConstants.CHANNELS_PROPERTY);        	
        	Assert.assertTrue("The generation of the list of channels partial properties is not correct", containsChannelsProperties);
        	        	
        	//Invoke method
        	generateElementsPartialPropertiesMethod.invoke(configurationBuilder, FlumeConfiguratorConstants.CHANNELS_PARTIAL_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.CHANNELS_PROPERTY, mapAgentChannels);
        	
        	int afterGenerateChannelsElementsPartialPropertiesNumber = ConfigurationBuilderTestUtils.calculatePropertiesTotalNumber(configurationBuilder.getConfigurationInitialMap());
        	Assert.assertTrue("The generation of the list of channels partial properties is not correct", afterGenerateChannelsElementsPartialPropertiesNumber > beforeGenerateChannelsElementsPartialPropertiesNumber);
        	containsChannelsProperties = ConfigurationBuilderTestUtils.containsElementsProperties(configurationBuilder.getConfigurationInitialMap(), FlumeConfiguratorConstants.CHANNELS_PROPERTY);        	
        	Assert.assertTrue("The generation of the list of channels partial properties is not correct", containsChannelsProperties);
        	              	
        	
        	//SINKS    
        	int beforeGenerateSinksElementsPartialPropertiesNumber = ConfigurationBuilderTestUtils.calculatePropertiesTotalNumber(configurationBuilder.getConfigurationInitialMap());
        	boolean containsSinksProperties = ConfigurationBuilderTestUtils.containsElementsProperties(configurationBuilder.getConfigurationInitialMap(), FlumeConfiguratorConstants.SINKS_PROPERTY);        	
        	Assert.assertTrue("The generation of the list of sinks partial properties is not correct", containsSinksProperties);
        	        	
        	//Invoke method
        	generateElementsPartialPropertiesMethod.invoke(configurationBuilder, FlumeConfiguratorConstants.SINKS_PARTIAL_PROPERTY_PROPERTIES_PREFIX, FlumeConfiguratorConstants.SINKS_PROPERTY, mapAgentSinks);
        	
        	int afterGenerateSinksElementsPartialPropertiesNumber = ConfigurationBuilderTestUtils.calculatePropertiesTotalNumber(configurationBuilder.getConfigurationInitialMap());
        	Assert.assertTrue("The generation of the list of sinks partial properties is not correct", afterGenerateSinksElementsPartialPropertiesNumber > beforeGenerateSinksElementsPartialPropertiesNumber);
        	containsSinksProperties = ConfigurationBuilderTestUtils.containsElementsProperties(configurationBuilder.getConfigurationInitialMap(), FlumeConfiguratorConstants.SINKS_PROPERTY);        	
        	Assert.assertTrue("The generation of the list of sinks partial properties is not correct", containsSinksProperties);
        	
	    } catch (Exception e) {
	        Assert.fail("An error has occurred [test_09_testGenerateElementsPartialProperties] method");
	    }
    }  
    
    
    @Test
    public void test_10_testGenerateFinalStructureMaps() {
    	
        try {
        	
        	Map<String, AgentConfigurationProperties> configurationFinalMap = configurationBuilder.getConfigurationFinalMap();
        	Assert.assertTrue("The generation of the final structure map is not correct", configurationFinalMap.size() == 0);
        	
        	//Invoke method
        	generateFinalStructureMapMethod.invoke(configurationBuilder, objectNull);
        	
        	configurationFinalMap = configurationBuilder.getConfigurationFinalMap();
         	Assert.assertTrue("The generation of the final structure map is not correct", configurationFinalMap.size() > 0);
         	
         	for (String agentName : configurationFinalMap.keySet()) {
         		AgentConfigurationProperties agentConfigurationProperties = configurationFinalMap.get(agentName);
         		
         		Assert.assertTrue("The generation of the final structure map is not correct", agentConfigurationProperties.getListGeneralProperties().size() > 0);
         		Assert.assertTrue("The generation of the final structure map is not correct", agentConfigurationProperties.getMapGroupProperties().size() > 0);
         	}
        	
	    } catch (Exception e) {
	        Assert.fail("An error has occurred [test_10_testGenerateFinalStructureMaps] method");
	    }
    }  
    
   
    
    @Test
    public void test_11_testWriteConfigurationFilesInvalidPath() {
    	
        try {
        	
        	//Invoke method
        	writeConfigurationFilesMethod.invoke(configurationBuilder, objectNull);
        	
        	//The exception must be thrown
        	Assert.fail("The write of the Flume configuration file(s) is not correct");

        } catch (InvocationTargetException ite) {
        	   if (!(ite.getCause() instanceof InvalidPathException)) {
				   Assert.fail("An error has occurred [test_11_testWriteConfigurationFilesInvalidPath] method");
        	   }
	    } catch (Exception e) {
	        Assert.fail("An error has occurred [test_11_testWriteConfigurationFilesInvalidPath] method");
	    }
    }    
    
    
    @Test
    public void test_12_testWriteConfigurationFiles() {
    	
        try {
        	
        	//Check output directory & one single configuration file
        	ConfigurationBuilder.setPathConfigurationGeneratedFile(OUTPUT_GENERATED_FILE_PATH_DIRECTORY);
        	ConfigurationBuilder.setMultipleAgentConfigurationFiles(false);
        	
        	//Invoke method
        	writeConfigurationFilesMethod.invoke(configurationBuilder, objectNull);
        	
        	
        	//Check output directory & several configuration files
        	ConfigurationBuilder.setPathConfigurationGeneratedFile(OUTPUT_GENERATED_FILE_PATH_DIRECTORY);
        	ConfigurationBuilder.setMultipleAgentConfigurationFiles(true);    
        	
        	//Invoke method
        	writeConfigurationFilesMethod.invoke(configurationBuilder, objectNull);
        	

        	//Check output file & one single configuration file
        	ConfigurationBuilder.setPathConfigurationGeneratedFile(OUTPUT_GENERATED_FILE_PATH_FILE);
        	ConfigurationBuilder.setMultipleAgentConfigurationFiles(false);
        	
        	//Invoke method
        	writeConfigurationFilesMethod.invoke(configurationBuilder, objectNull);
        	
        	
        	//Check output file & several configuration files
        	ConfigurationBuilder.setPathConfigurationGeneratedFile(OUTPUT_GENERATED_FILE_PATH_FILE);
        	ConfigurationBuilder.setMultipleAgentConfigurationFiles(true);
        	
        	//Invoke method
        	writeConfigurationFilesMethod.invoke(configurationBuilder, objectNull);
        	
	    } catch (Exception e) {
	        Assert.fail("An error has occurred [test_12_testWriteConfigurationFiles] method");
	    }
    }   
    
    
    @Test
    public void test_13_testBuildConfigurationMap() {
    	
        try {  

        	ConfigurationBuilder.setElementsCharacterSeparator(DEFAULT_SEPARATOR);
        	ConfigurationBuilder.setAddComments(true);
        	ConfigurationBuilder.setMultipleAgentConfigurationFiles(true);
        	ConfigurationBuilder.setPathConfigurationGeneratedFile(OUTPUT_GENERATED_FILE_PATH_DIRECTORY);

        	//Build with Validation Error Properties
        	ConfigurationBuilder.setPathConfigurationProperties(PROPERTIES_FILE_VALIDATION_ERROR_PATH);
        	
        	//Invoke method
        	boolean isCorrect = (boolean) buildConfigurationMapMethod.invoke(configurationBuilder, objectNull);
        	Assert.assertFalse("The Flume configuration file has not been built correctly", isCorrect);
        	
        	//Build correct
        	ConfigurationBuilder.setPathConfigurationProperties(PROPERTIES_FILE_PATH);
        	
        	//Invoke method
        	isCorrect = (boolean) buildConfigurationMapMethod.invoke(configurationBuilder, objectNull);
        	Assert.assertTrue("The Flume configuration file has not been built correctly", isCorrect);
            
	    } catch (Exception e) {
	        Assert.fail("An error has occurred [test_13_testBuildConfigurationMap] method");
	    }
    } 
    
}
