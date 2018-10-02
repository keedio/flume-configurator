package org.keedio.flume.configurator.builder;

import org.apache.log4j.PropertyConfigurator;
import org.keedio.flume.configurator.constants.FlumeConfiguratorConstants;
import org.keedio.flume.configurator.exceptions.FlumeConfiguratorException;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfiguratorBuilder {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ConfiguratorBuilder.class);

    private static String configuratorMode;

    private static String pathBaseConfigurationProperties;
    private static String elementsCharacterSeparator;
    private static boolean multipleAgentConfigurationFiles = false;
    private static boolean addComments = true;
    private static String pathConfigurationGeneratedFile;

    private static String pathJSONTopology;
    private static boolean computeTreeAsGraph = false;
    private static double ratioCommonProperty = 1.0d;
    private static boolean generateBaseConfigurationFiles = false;
    private static String pathBasePropertiesGeneratedFile;

    private static String pathFlumeProperties;
    private static String pathDraw2DFlumeTopologyGeneratedFile;
    private static boolean withComments = false;
    private static boolean generatePositionCoordinates = true;
    private static List<Integer> alternativeOptimizationPermutationAgentList = new ArrayList<>();


    /**
     * Generate a Flume configuration string from a base (template) configuration properties
     * @param properties Base (template) properties
     * @param characterSeparator Separator character used in base (template) configuration file
     * @param withComments  true -> Property comments are added to Flume configuration file");
                            false -> Property comments are not added to Flume configuration file");
     * @return String with the Flume configuration generated
     * @throws FlumeConfiguratorException
     */
    public static String getBaseToFlumeConfiguration(String properties, String characterSeparator, boolean withComments) throws FlumeConfiguratorException {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN getBaseToFlumeConfiguration");
        }

        FlumePropertiesGenerator flumePropertiesGenerator = new FlumePropertiesGenerator();

        String flumeConfiguration = flumePropertiesGenerator.buildConfigurationMapFromStringProperties(properties, characterSeparator, withComments,
                false, null, false);

        if (logger.isDebugEnabled()) {
            logger.debug("END getBaseToFlumeConfiguration");
        }

        if (flumeConfiguration != null) {
            return flumeConfiguration;
        } else {
            throw new FlumeConfiguratorException("An error has occurred on the creation of Flume configuration from a base configuration");
        }
    }


    /**
     * Generate a Flume configuration from a Draw2D Flume topology (json format)
     * @param jsonTopology String with JSON topology
     * @param generateBaseConfigurationProperties true -> the base configuration properties is generated
     *                                            false -> otherwise
     * @param generateFlumeConfigurationProperties true -> the flume configuration properties is generated,
     *                                             false -> otherwise
     * @return Map with the content of properties generated (base configuration properties and flume configuration properties)
     * @throws FlumeConfiguratorException
     */
    public static Map<String,String> getDraw2DToFlumeConfiguration(String jsonTopology, boolean generateBaseConfigurationProperties,
                                                                   boolean generateFlumeConfigurationProperties) throws FlumeConfiguratorException {
        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN getDraw2DToFlumeConfiguration");
        }

        FlumeTopologyPropertiesGenerator flumeTopologyPropertiesGenerator = new FlumeTopologyPropertiesGenerator();


        Map<String,String> baseAndFlumeConfigurationsList = flumeTopologyPropertiesGenerator.generateInputPropertiesFromDraw2DFlumeTopology(jsonTopology, generateBaseConfigurationProperties,
                    generateFlumeConfigurationProperties);

        if (logger.isDebugEnabled()) {
            logger.debug("END getDraw2DToFlumeConfiguration");
        }

        if (baseAndFlumeConfigurationsList != null) {
            return baseAndFlumeConfigurationsList;
        } else {
            throw new FlumeConfiguratorException("An error has occurred on the creation of Flume & Base configuration from a Draw2D Flume topology configuration");
        }

    }

    /**
     * Generate Draw 2D Flume Topology (json format) from a Flume configuration string
     * @param flumePropertiesString Flume properties string
     * @return String with the json representation of the flume topology
     * @throws FlumeConfiguratorException
     */
    public static String getFlumeToDraw2DConfiguration(String flumePropertiesString) throws FlumeConfiguratorException {

        if (logger.isDebugEnabled()) {
            logger.debug("BEGIN getFlumeToDraw2DConfiguration");
        }

        FlumeTopologyReversePropertiesGenerator flumeTopologyReversePropertiesGenerator = new FlumeTopologyReversePropertiesGenerator();

        String draw2DFlumeTopologyConfiguration =  flumeTopologyReversePropertiesGenerator.generateDraw2DFlumeTopologyFromPropertiesString(flumePropertiesString);

        if (logger.isDebugEnabled()) {
            logger.debug("END getFlumeToDraw2DConfiguration");
        }

        if (draw2DFlumeTopologyConfiguration != null) {
            return draw2DFlumeTopologyConfiguration;
        } else {
            throw new FlumeConfiguratorException("An error has occurred on the creation of Draw2D Flume Topology configuration from a Flume configuration");
        }
    }



    /**
     * Get associated message to an error code
     * @param errorCode error code
     * @return associated message to an error code
     */
    public static String getErrorMessage(int errorCode) {

        String error = "";
        StringBuilder sb = new StringBuilder(10000);


        if (errorCode == 1) {
            sb.append("An error has occurred in configurator builder.Check the configuration input file(s) and the generated logs");
        } else if (errorCode == 2) {
            sb.append("Incorrect configuration mode parameter");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("Usage: java -jar jarFile <configuration mode> <configuration mode parameters>");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("The available configuration modes are:");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("baseToFlume => Create flume configuration file from a base (template) configuration file");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("draw2DToFlume => Create flume configuration file from a Draw2D Flume Topology configuration file");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("flumeToDraw2D => Create a Draw2D Flume Topology configuration file from a flume configuration file");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);

        } else if (errorCode == 3) {

            sb.append("Incorrect parameter number (for baseToFlume configuration mode)");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("Usage: java -jar jarFile baseToFlume <baseToFlume configuration mode parameters >");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("The parameters are:");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("pathBaseConfigurationProperties => Path of the base (template) configuration file");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("elementsCharacterSeparator => Separator character used in base (template) configuration file. Example: ';'");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("multipleAgentConfigurationFiles => (boolean)");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                                     true -> Every agent has an own configuration file");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                                     false -> All agents configuration in one single file");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("addComments => (boolean)");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                 true -> Property comments are added to Flume configuration file");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                 false -> Property comments are not added to Flume configuration file");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("pathConfigurationGeneratedFile => Path of the created flume configuration file(s).May be a directory if several");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                                  configuration files are created (the directory must be exist)");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);

        } else if (errorCode == 4) {

            sb.append("Incorrect parameter number (for draw2DToFlume configuration mode)");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("Usage: java -jar jarFile draw2DToFlume <draw2DToFlume configuration mode parameters>");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("The parameters are:");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("pathJSONTopology => Path of the Draw2D Flume Topology configuration file");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("multipleAgentConfigurationFiles => (boolean)");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                                     true -> Every agent has an own configuration file");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                                     false -> All agents configuration in one single file");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("addComments => (boolean)");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                 true -> Property comments are added to Flume configuration file");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                 false -> Property comments are not added to Flume configuration file");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("computeTreeAsGraph => (boolean)");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                         true -> The tree topology is processed with a graph library");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                         false -> The tree topology is processed without any graph library");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("ratioCommonProperty => (float) Ratio used for determinate if a property is considered as a common property or not (generation of template configuration file). Example: 0.8");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("pathPropertiesGeneratedFile => Path of the created flume configuration file(s).May be a directory if several");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                               configuration files are created (the directory must be exist)");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("pathBasePropertiesGeneratedFile => (Optional parameter) Path of the created base (template) configuration file.May be a directory (the directory must be exist)");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);

        } else if (errorCode == 5) {

            sb.append("Incorrect parameter number (for flumeToDraw2D configuration mode)");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("Usage: java -jar jarFile flumeToDraw2D <flumeToDraw2D configuration mode parameters>");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("The parameters are:");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("pathFlumeProperties => Path of the Flume configuration file");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("withComments => (boolean)");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                 true -> Property comments are added to Draw2D Flume topology configuration file");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                 false -> Property comments are not added to Draw2D Flume topology configuration file");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("generatePositionCoordinates => (boolean)");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                                 true -> The created Draw2D Flume topology configuration file includes position coordinates for its elements");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                                 false -> The created Draw2D Flume topology configuration file doesn't include position coordinates for its elements");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("pathJSONFlumeTopologyGeneratedFile => Path of the created Draw2D Flume topology configuration file.May be a directory (the directory must be exist)");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("alternativeOptimizationPermutationAgentList => (Optional parameter(s)) list with numbers of alternative best permutations of shared sources for the agent(s).");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                                 If the parameter(s) is not present for an agent, the alternative number by default is 1. Shared sources are sources that share the channel with another source");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                                 Example: 1 3 2 (the first agent show best calculated permutation of his shared sources, the second agent will show his third best permutation of his shared sources");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);
            sb.append("                                 and the third agent will show his second best permutation of his shared sources. The agent number four and following will show their best permutation sources (alternative 1)");
            sb.append(FlumeConfiguratorConstants.NEW_LINE);

        }

        return sb.toString();
    }

    /**
     * Main method.
     * @param args String array with the parameters of the execution.
     */
    public static void main(String[] args) {

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource("log4j.properties");
        PropertyConfigurator.configure(url);

        if (logger.isDebugEnabled()) {
            logger.debug("******* BEGIN CONFIGURATOR BUILDER PROCESS *****************");
        }

        try {

            if (args.length >= 1) {

                configuratorMode = args[0];

                if (FlumeConfiguratorConstants.BASE_TO_FLUME_CONFIGURATION_MODE.toLowerCase().equals(configuratorMode.toLowerCase())) {

                    if (args.length == 6) {
                        pathBaseConfigurationProperties = args[1];
                        elementsCharacterSeparator = args[2];
                        multipleAgentConfigurationFiles = Boolean.valueOf(args[3]);
                        addComments = Boolean.valueOf(args[4]);
                        pathConfigurationGeneratedFile = args[5];

                        if (logger.isDebugEnabled()) {
                            logger.debug("Parameter pathBaseConfigurationProperties: " + pathBaseConfigurationProperties);
                            logger.debug("Parameter elementsCharacterSeparator: " + elementsCharacterSeparator);
                            logger.debug("Parameter multipleAgentConfigurationFiles: " + multipleAgentConfigurationFiles);
                            logger.debug("Parameter addComments: " + addComments);
                            logger.debug("Parameter patConfigurationGeneratedFile: " + pathConfigurationGeneratedFile);
                        }

                        FlumePropertiesGenerator flumePropertiesGenerator = new FlumePropertiesGenerator();

                        FlumePropertiesGenerator.setPathBaseConfigurationProperties(pathBaseConfigurationProperties);
                        FlumePropertiesGenerator.setElementsCharacterSeparator(elementsCharacterSeparator);
                        FlumePropertiesGenerator.setMultipleAgentConfigurationFiles(multipleAgentConfigurationFiles);
                        FlumePropertiesGenerator.setAddComments(addComments);
                        FlumePropertiesGenerator.setPathConfigurationGeneratedFile(pathConfigurationGeneratedFile);

                        flumePropertiesGenerator.buildConfigurationMap();

                    } else {

                        logger.error(getErrorMessage(3));
                        if (logger.isDebugEnabled()) {
                            logger.debug("******* END CONFIGURATOR BUILDER PROCESS *****************");
                        }
                    }

                } else if (FlumeConfiguratorConstants.DRAW2D_TO_FLUME_CONFIGURATION_MODE.toLowerCase().equals(configuratorMode.toLowerCase())) {

                    if (args.length == 7) {

                        try {

                            pathJSONTopology = args[1];
                            multipleAgentConfigurationFiles = Boolean.valueOf(args[2]);
                            addComments = Boolean.valueOf(args[3]);
                            computeTreeAsGraph = Boolean.valueOf(args[4]);
                            ratioCommonProperty = Double.valueOf(args[5]);
                            pathConfigurationGeneratedFile = args[6];

                            if (logger.isDebugEnabled()) {
                                logger.debug("Parameter pathJSONTopology: " + pathJSONTopology);
                                logger.debug("Parameter multipleAgentConfigurationFiles: " + multipleAgentConfigurationFiles);
                                logger.debug("Parameter addComments: " + addComments);
                                logger.debug("Parameter computeTreeAsGraph: " + computeTreeAsGraph);
                                logger.debug("Parameter ratioCommonProperty: " + ratioCommonProperty);
                                logger.debug("Parameter pathPropertiesGeneratedFile: " + pathConfigurationGeneratedFile);
                            }
                            FlumeTopologyPropertiesGenerator flumeTopologyPropertiesGenerator = new FlumeTopologyPropertiesGenerator();

                            FlumeTopologyPropertiesGenerator.setPathJSONTopology(pathJSONTopology);
                            FlumeTopologyPropertiesGenerator.setMultipleAgentConfigurationFiles(multipleAgentConfigurationFiles);
                            FlumeTopologyPropertiesGenerator.setAddComments(addComments);
                            FlumeTopologyPropertiesGenerator.setComputeTreeAsGraph(computeTreeAsGraph);
                            FlumeTopologyPropertiesGenerator.setRatioCommonProperty(ratioCommonProperty);
                            FlumeTopologyPropertiesGenerator.setPathConfigurationGeneratedFile(pathConfigurationGeneratedFile);

                            flumeTopologyPropertiesGenerator.generateInputProperties();

                        } catch (NumberFormatException nfe) {
                            logger.error(getErrorMessage(4), nfe);
                            if (logger.isDebugEnabled()) {
                                logger.debug("******* END CONFIGURATOR BUILDER PROCESS *****************");
                            }
                        }

                    } else if (args.length == 8) {

                        try {

                            pathJSONTopology = args[1];
                            multipleAgentConfigurationFiles = Boolean.valueOf(args[2]);
                            addComments = Boolean.valueOf(args[3]);
                            computeTreeAsGraph = Boolean.valueOf(args[4]);
                            ratioCommonProperty = Double.valueOf(args[5]);
                            pathConfigurationGeneratedFile = args[6];
                            generateBaseConfigurationFiles = true;
                            pathBasePropertiesGeneratedFile = args[7];

                            if (logger.isDebugEnabled()) {
                                logger.debug("Parameter pathJSONTopology: " + pathJSONTopology);
                                logger.debug("Parameter multipleAgentConfigurationFiles: " + multipleAgentConfigurationFiles);
                                logger.debug("Parameter addComments: " + addComments);
                                logger.debug("Parameter computeTreeAsGraph: " + computeTreeAsGraph);
                                logger.debug("Parameter ratioCommonProperty: " + ratioCommonProperty);
                                logger.debug("Parameter pathPropertiesGeneratedFile: " + pathConfigurationGeneratedFile);
                                logger.debug("Parameter pathBasePropertiesGeneratedFile: " + pathBasePropertiesGeneratedFile);
                            }

                            FlumeTopologyPropertiesGenerator flumeTopologyPropertiesGenerator = new FlumeTopologyPropertiesGenerator();

                            FlumeTopologyPropertiesGenerator.setPathJSONTopology(pathJSONTopology);
                            FlumeTopologyPropertiesGenerator.setMultipleAgentConfigurationFiles(multipleAgentConfigurationFiles);
                            FlumeTopologyPropertiesGenerator.setAddComments(addComments);
                            FlumeTopologyPropertiesGenerator.setComputeTreeAsGraph(computeTreeAsGraph);
                            FlumeTopologyPropertiesGenerator.setRatioCommonProperty(ratioCommonProperty);
                            FlumeTopologyPropertiesGenerator.setPathConfigurationGeneratedFile(pathConfigurationGeneratedFile);
                            FlumeTopologyPropertiesGenerator.setGenerateBaseConfigurationFiles(generateBaseConfigurationFiles);
                            FlumeTopologyPropertiesGenerator.setPathBasePropertiesGeneratedFile(pathBasePropertiesGeneratedFile);

                            flumeTopologyPropertiesGenerator.generateInputProperties();

                        } catch (NumberFormatException nfe) {
                            logger.error(getErrorMessage(4), nfe);
                            if (logger.isDebugEnabled()) {
                                logger.debug("******* END CONFIGURATOR BUILDER PROCESS *****************");
                            }
                        }

                    } else {
                        logger.error(getErrorMessage(4));
                        if (logger.isDebugEnabled()) {
                            logger.debug("******* END CONFIGURATOR BUILDER PROCESS *****************");
                        }
                    }
                } else if (FlumeConfiguratorConstants.FLUME_TO_DRAW2D_CONFIGURATION_MODE.toLowerCase().equals(configuratorMode.toLowerCase())) {

                    if (args.length == 5) {

                        pathFlumeProperties = args[1];
                        withComments = Boolean.valueOf(args[2]);
                        generatePositionCoordinates = Boolean.valueOf(args[3]);
                        pathDraw2DFlumeTopologyGeneratedFile = args[4];

                        if (logger.isDebugEnabled()) {
                            logger.debug("Parameter pathFlumeProperties: " + pathFlumeProperties);
                            logger.debug("Parameter withComments: " + withComments);
                            logger.debug("Parameter generatePositionCoordinates: " + generatePositionCoordinates);
                            logger.debug("Parameter pathDraw2DFlumeTopologyGeneratedFile: " + pathDraw2DFlumeTopologyGeneratedFile);
                        }

                        FlumeTopologyReversePropertiesGenerator flumeTopologyReversePropertiesGenerator = new FlumeTopologyReversePropertiesGenerator();

                        FlumeTopologyReversePropertiesGenerator.setPathFlumeProperties(pathFlumeProperties);
                        FlumeTopologyReversePropertiesGenerator.setWithComments(withComments);
                        FlumeTopologyReversePropertiesGenerator.setGeneratePositionCoordinates(generatePositionCoordinates);
                        FlumeTopologyReversePropertiesGenerator.setPathDraw2DFlumeTopologyGeneratedFile(pathDraw2DFlumeTopologyGeneratedFile);

                        flumeTopologyReversePropertiesGenerator.generateDraw2DFlumeTopology();

                    } else if (args.length > 5) {

                        try {

                            pathFlumeProperties = args[1];
                            withComments = Boolean.valueOf(args[2]);
                            generatePositionCoordinates = Boolean.valueOf(args[3]);
                            pathDraw2DFlumeTopologyGeneratedFile = args[4];


                            for (int i = 5; i < args.length; i++) {
                                int alternativeOptimizationPermutationAgent = Integer.valueOf(args[i]);
                                if (alternativeOptimizationPermutationAgent <= 0) {
                                    int agentIndex = i - 5;
                                    throw new NumberFormatException("The alternative optimization permutation number for agent at index " + agentIndex + " must be greater than zero");
                                }
                                alternativeOptimizationPermutationAgentList.add(alternativeOptimizationPermutationAgent);
                            }

                            if (logger.isDebugEnabled()) {
                                logger.debug("Parameter pathFlumeProperties: " + pathFlumeProperties);
                                logger.debug("Parameter withComments: " + withComments);
                                logger.debug("Parameter generatePositionCoordinates: " + generatePositionCoordinates);
                                logger.debug("Parameter pathDraw2DFlumeTopologyGeneratedFile: " + pathDraw2DFlumeTopologyGeneratedFile);
                                for (int i = 0; i < alternativeOptimizationPermutationAgentList.size(); i++) {
                                    logger.debug("Parameter alternative optimization permutation number for agent (index " + i + "): " + alternativeOptimizationPermutationAgentList.get(i));
                                }
                            }

                            FlumeTopologyReversePropertiesGenerator flumeTopologyReversePropertiesGenerator = new FlumeTopologyReversePropertiesGenerator();

                            FlumeTopologyReversePropertiesGenerator.setPathFlumeProperties(pathFlumeProperties);
                            FlumeTopologyReversePropertiesGenerator.setWithComments(withComments);
                            FlumeTopologyReversePropertiesGenerator.setGeneratePositionCoordinates(generatePositionCoordinates);
                            FlumeTopologyReversePropertiesGenerator.setPathDraw2DFlumeTopologyGeneratedFile(pathDraw2DFlumeTopologyGeneratedFile);
                            FlumeTopologyReversePropertiesGenerator.setAlternativeOptimizationPermutationAgentList(alternativeOptimizationPermutationAgentList);

                            flumeTopologyReversePropertiesGenerator.generateDraw2DFlumeTopology();

                        } catch (NumberFormatException nfe) {
                            logger.error(getErrorMessage(5), nfe);
                            if (logger.isDebugEnabled()) {
                                logger.debug("******* END CONFIGURATOR BUILDER PROCESS *****************");
                            }
                        }

                    } else {
                        logger.error(getErrorMessage(5));
                        if (logger.isDebugEnabled()) {
                            logger.debug("******* END CONFIGURATOR BUILDER PROCESS *****************");
                        }
                    }
                } else {
                    logger.error(getErrorMessage(2));
                    if (logger.isDebugEnabled()) {
                        logger.debug("******* END CONFIGURATOR BUILDER PROCESS *****************");
                    }
                }
            } else {
                logger.error(getErrorMessage(2));
                if (logger.isDebugEnabled()) {
                    logger.debug("******* END CONFIGURATOR BUILDER PROCESS *****************");
                }
            }
        } catch (Exception e) {
            logger.error(getErrorMessage(1), e);
            if (logger.isDebugEnabled()) {
                logger.debug("******* END CONFIGURATOR BUILDER PROCESS *****************");
            }
        }
    }

}
