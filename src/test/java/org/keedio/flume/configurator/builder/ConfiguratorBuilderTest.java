package org.keedio.flume.configurator.builder;

import org.junit.Assert;
import org.junit.Test;
import org.keedio.flume.configurator.constants.FlumeConfiguratorConstants;
import org.keedio.flume.configurator.exceptions.FlumeConfiguratorException;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;


public class ConfiguratorBuilderTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ConfiguratorBuilderTest.class);

    private static final String BASE_CONFIGURATION_PROPERTIES_FILE_PATH = "src/test/resources/FlumeConfigurationExample.properties";
    private static final String BASE_CONFIGURATION_PROPERTIES_FILE_VALIDATION_PATH_ERROR = "src/test/resources/FlumeConfigurationExampleValidationError.properties";
    private static final String DEFAULT_SEPARATOR = ";";

    private static final String DRAW2D_GRAPH_TOPOLOGY_FILE_PATH = "src/test/resources/FlumeTopologyGraphWithComments_with2Agent.json";
    private static final String DRAW2D_TOPOLOGY_FILE_PATH_ERROR = "src/test/resources/FlumeTopologyError.json";

    private static final String FLUME_PROPERTIES_FILE_PATH = "src/test/resources/FlumeProperties/nAgent/";
    private static final String FLUME_PROPERTIES_FILE_PATH_ERROR = "src/test/resources/FlumeProperties/nAgent_error";

    private static final boolean withComments = true;

    @Test
    public void testGetBaseToFlumeConfigurationError() {

        try {

            byte[] baseProperties = Files.readAllBytes(Paths.get(BASE_CONFIGURATION_PROPERTIES_FILE_VALIDATION_PATH_ERROR));
            String basePropertiesString = new String(baseProperties, Charset.defaultCharset());

            //Invoke method
            ConfiguratorBuilder.getBaseToFlumeConfiguration(basePropertiesString, DEFAULT_SEPARATOR, withComments);

            //An exception must be thrown
            Assert.fail("The Flume configuration creation from a base configuration is not correct");

        } catch (Exception e) {
            if (!(e instanceof FlumeConfiguratorException)) {
                Assert.fail("An error has occurred [testGetBaseToFlumeConfigurationError] method");
                logger.error("An error has occurred [testGetBaseToFlumeConfigurationError] method", e);
            }
        }
    }

    @Test
    public void testGetBaseToFlumeConfiguration() {

        try {

            byte[] baseProperties = Files.readAllBytes(Paths.get(BASE_CONFIGURATION_PROPERTIES_FILE_PATH));
            String basePropertiesString = new String(baseProperties, Charset.defaultCharset());

            //Invoke method
            String flumeConfiguration = ConfiguratorBuilder.getBaseToFlumeConfiguration(basePropertiesString, DEFAULT_SEPARATOR, withComments);

            Assert.assertFalse("The Flume configuration creation from a base configuration is not correct", flumeConfiguration.isEmpty());

        } catch (Exception e) {
            Assert.fail("An error has occurred [testGetBaseToFlumeConfiguration] method");
            logger.error("An error has occurred [testGetBaseToFlumeConfiguration] method", e);
        }
    }


    @Test
    public void testGetDraw2DToFlumeConfigurationError() {

        try {

            byte[] draw2DFlumeTopology = Files.readAllBytes(Paths.get(DRAW2D_TOPOLOGY_FILE_PATH_ERROR));
            String draw2DFlumeTopologyString = new String(draw2DFlumeTopology, Charset.defaultCharset());

            //Invoke method
            ConfiguratorBuilder.getDraw2DToFlumeConfiguration(draw2DFlumeTopologyString, true, true);

            //An exception must be thrown
            Assert.fail("The creation of Flume configuration / Base configuration from a Draw2D Flume topology configuration is not correct");

        } catch (Exception e) {
            if (!(e instanceof FlumeConfiguratorException)) {
                Assert.fail("An error has occurred [testGetDraw2DToFlumeConfigurationError] method");
                logger.error("An error has occurred [testGetDraw2DToFlumeConfigurationError] method", e);
            }
        }
    }


    @Test
    public void testGetDraw2DToFlumeConfiguration() {

        try {

            byte[] draw2DFlumeTopology = Files.readAllBytes(Paths.get(DRAW2D_GRAPH_TOPOLOGY_FILE_PATH));
            String draw2DFlumeTopologyString = new String(draw2DFlumeTopology, Charset.defaultCharset());

            //Invoke method (Flume configuration and Base configuration are requested)
            Map<String,String> baseAndFlumeConfigurationMap = ConfiguratorBuilder.getDraw2DToFlumeConfiguration(draw2DFlumeTopologyString, true, true);

            Assert.assertFalse("The Flume & Base configuration creation from a Draw2D Flume topology configuration is not correct", baseAndFlumeConfigurationMap.isEmpty());
            Assert.assertEquals("The Flume & Base configuration creation from a Draw2D Flume topology configuration is not correct", baseAndFlumeConfigurationMap.size(), 2);
            Assert.assertFalse("The Flume & Base configuration creation from a Draw2D Flume topology configuration is not correct", baseAndFlumeConfigurationMap.get(FlumeConfiguratorConstants.BASE_CONFIGURATION_KEY).isEmpty());
            Assert.assertFalse("The Flume & Base configuration creation from a Draw2D Flume topology configuration is not correct", baseAndFlumeConfigurationMap.get(FlumeConfiguratorConstants.FLUME_CONFIGURATION_KEY).isEmpty());


            //Invoke method (Only Flume configuration is requested)
            baseAndFlumeConfigurationMap = ConfiguratorBuilder.getDraw2DToFlumeConfiguration(draw2DFlumeTopologyString, false, true);

            Assert.assertFalse("The Flume & Base configuration creation from a Draw2D Flume topology configuration is not correct", baseAndFlumeConfigurationMap.isEmpty());
            Assert.assertEquals("The Flume & Base configuration creation from a Draw2D Flume topology configuration is not correct", baseAndFlumeConfigurationMap.size(), 1);
            Assert.assertNull("The Flume & Base configuration creation from a Draw2D Flume topology configuration is not correct", baseAndFlumeConfigurationMap.get(FlumeConfiguratorConstants.BASE_CONFIGURATION_KEY));
            Assert.assertFalse("The Flume & Base configuration creation from a Draw2D Flume topology configuration is not correct", baseAndFlumeConfigurationMap.get(FlumeConfiguratorConstants.FLUME_CONFIGURATION_KEY).isEmpty());


            //Invoke method (Only Base configuration is requested)
            baseAndFlumeConfigurationMap = ConfiguratorBuilder.getDraw2DToFlumeConfiguration(draw2DFlumeTopologyString, true, false);

            Assert.assertFalse("The Flume & Base configuration creation from a Draw2D Flume topology configuration is not correct", baseAndFlumeConfigurationMap.isEmpty());
            Assert.assertEquals("The Flume & Base configuration creation from a Draw2D Flume topology configuration is not correct", baseAndFlumeConfigurationMap.size(), 1);
            Assert.assertNull("The Flume & Base configuration creation from a Draw2D Flume topology configuration is not correct", baseAndFlumeConfigurationMap.get(FlumeConfiguratorConstants.FLUME_CONFIGURATION_KEY));
            Assert.assertFalse("The Flume & Base configuration creation from a Draw2D Flume topology configuration is not correct", baseAndFlumeConfigurationMap.get(FlumeConfiguratorConstants.BASE_CONFIGURATION_KEY).isEmpty());

        } catch (Exception e) {
            Assert.fail("An error has occurred [testGetDraw2DToFlumeConfiguration] method");
            logger.error("An error has occurred [testGetDraw2DToFlumeConfiguration] method", e);
        }
    }


    @Test
    public void testGetFlumeToDraw2DConfiguration() {

        try {

            byte[] flumeProperties;
            String flumePropertiesString;
            StringBuilder sb = new StringBuilder(10000);

            if (Files.isDirectory(Paths.get(FLUME_PROPERTIES_FILE_PATH))) {
                Stream<Path> filesList = Files.list(Paths.get(FLUME_PROPERTIES_FILE_PATH));
                Iterator<Path> filesIterator = filesList.iterator();
                while (filesIterator.hasNext()) {
                    Path filePath = filesIterator.next();
                    flumeProperties = Files.readAllBytes(filePath);
                    sb.append(new String(flumeProperties, Charset.defaultCharset()));
                    sb.append(FlumeConfiguratorConstants.NEW_LINE);
                }

                flumePropertiesString = sb.toString();
            } else {
                flumeProperties = Files.readAllBytes(Paths.get(FLUME_PROPERTIES_FILE_PATH));
                flumePropertiesString = new String(flumeProperties, Charset.defaultCharset());
            }

            //Invoke method
            String draw2DFlumeTopologyConfiguration = ConfiguratorBuilder.getFlumeToDraw2DConfiguration(flumePropertiesString);

            Assert.assertFalse("The Draw2D Flume Topology configuration creation from a Flumeconfiguration is not correct", draw2DFlumeTopologyConfiguration.isEmpty());

        } catch (Exception e) {
            Assert.fail("An error has occurred [testGetFlumeToDraw2DConfiguration] method");
            logger.error("An error has occurred [testGetFlumeToDraw2DConfiguration] method", e);
        }
    }
}
