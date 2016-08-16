# flume-configurator

An automatic tool to generate flume configuration files

Source code [flume-configurator] (https://github.com/keedio/flume-configurator)

## How to use

Clone the project:


$ git clone https://github.com/keedio/flume-configurator.git
```

Build with Maven:

```sh
$ mvn clean install
```

The jar file will be installed in your local maven repository and can be found in the target/ subdirectory also.

The jar execution is:

```sh
java -jar [jarFile] parameters

where parameters are:

-pathConfigurationProperties : Path of the input properties file
-elementsCharacterSeparator : Separator character used (input properties file)
-multipleAgentConfigurationFiles : boolean.
    True ==> Create one flume configuration file per agent.
    False ==> Only 1 Flume configuration file is created with the information of all agents
-addComments : Boolean
    True ==> The comments of the properties (input properties file) will be added to the Flume configuration file
    False ==> The comments of the properties (input properties file) won't be added to the Flume configuration file
-pathConfigurationGeneratedFile : Path where the Flume configuration file will be created. The path must be exist
```

Example:
```sh
java -jar flume-configurator-0.1.0.jar flume1.properties ; true true output
```




