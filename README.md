# flume-configurator

An automatic tool to generate flume configuration files

Source code [flume-configurator] (https://github.com/keedio/flume-configurator)

## How to use

Clone the project:

```sh
$ git clone https://github.com/keedio/flume-configurator.git
```

Build with Maven:

```sh
$ mvn clean install
```

The jar file will be installed in your local maven repository and can be found in the target/ subdirectory also.

The jar execution is:

```sh
java -jar [jarFile] <configuration mode> <configuration mode parameters>

where available configuration modes are:

baseToFlume => Create flume configuration file from a base (template) configuration file
draw2DToFlume => Create flume configuration file from a Draw2D Flume Topology configuration file
flumeToDraw2D => Create a Draw2D Flume Topology configuration file from a flume configuration file


The baseToFlume configuration mode available parameters are:

-pathBaseConfigurationProperties => Path of the base (template) configuration file
-elementsCharacterSeparator => Separator character used in base (template) configuration file
-multipleAgentConfigurationFiles => (boolean)
    true -> Every agent has an own configuration file
    false -> All agents configuration in one single file
-addComments => (boolean)
    true -> Property comments are added to Flume configuration file
    false -> Property comments are not added to Flume configuration file
-pathConfigurationGeneratedFile => Path of the created flume configuration file(s).May be a directory if several configuration files are created (the directory must be exist)



The draw2DToFlume configuration mode available parameters are:

-pathJSONTopology => Path of the Draw2D Flume Topology configuration file
-multipleAgentConfigurationFiles => (boolean)
    true -> Every agent has an own configuration file
    false -> All agents configuration in one single file
-addComments => (boolean)
    true -> Property comments are added to Flume configuration file
    false -> Property comments are not added to Flume configuration file
-computeTreeAsGraph => (boolean)
    true -> The tree topology is processed with a graph library
    false -> The tree topology is processed without any graph library
-ratioCommonProperty => Ratio used for determinate if a property is considered as a common property or not (generation of template configuration file)
-pathPropertiesGeneratedFile => Path of the created flume configuration file(s).May be a directory if several configuration files are created (the directory must be exist)
-pathBasePropertiesGeneratedFile => (Optional parameter) Path of the created base (template) configuration file.May be a directory (the directory must be exist)



The flumeToDraw2D configuration mode available parameters are:

-pathFlumeProperties => Path of the Flume configuration file
-withComments => (boolean)
    true -> Property comments are added to Draw2D Flume topology configuration file
    false -> Property comments are not added to Draw2D Flume topology configuration file
-generatePositionCoordinates => (boolean)
    true -> The created Draw2D Flume topology configuration file includes position coordinates for its elements
    false -> The created Draw2D Flume topology configuration file doesn't include position coordinates for its elements
-pathJSONFlumeTopologyGeneratedFile => Path of the created Draw2D Flume topology configuration file.May be a directory (the directory must be exist)
```

Examples:

Create a Flume configuration from a base (template) configuration

```sh
java -jar flume-configurator-1.0.0.jar baseToFlume base_configuration.properties ';' true true output
```

Create a Flume configuration from a Draw2D Flume Topology configuration

```sh
java -jar flume-configurator-1.0.0.jar draw2DToFlume draw2D_Flume_Topology.json true true true 0.8 output
```

Create a Base & Flume configuration from a Draw2D Flume Topology configuration

```sh
java -jar flume-configurator-1.0.0.jar draw2DToFlume draw2D_Flume_Topology.json true true true 0.8 output output
```

Create a Draw2D Flume Topology configuration from a Flume configuration

```sh
java -jar flume-configurator-1.0.0.jar flumeToDraw2D flume_configuration.properties true true output
```


A log4j.properties file is required in order to determine the level and output of the generated logs
(An example log4j.properties can be downloaded from resources directory of the project)



