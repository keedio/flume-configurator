package org.keedio.flume.configurator.constants;

public class FlumeConfiguratorConstants {



    public static final String WHITE_SPACE = " ";
    public static final String ASTERISK = "*";
    public static final String HASH = "#";
    public static final String DOT_SEPARATOR = ".";
    public static final String UNDERSCORE_SEPARATOR = "_";
    public static final String COMMA_SEPARATOR = ",";
    public static final String PROPERTY_SEPARATOR_DEFAULT = ";";
    public static final String NEW_LINE = "\n";

    public static final String DOT_REGEX = "\\.";
    public static final String WHITE_SPACE_REGEX = "\\s+";

    public static final String SOURCES_PROPERTY = "sources";
    public static final String CHANNELS_PROPERTY = "channels";
    public static final String CHANNEL_PROPERTY = "channel";
    public static final String SINKS_PROPERTY = "sinks";
    public static final String INTERCEPTORS_PROPERTY = "interceptors";
    public static final String COMMENT_PROPERTY_PREFIX = "comment";
    public static final String COMMENT_PREFIX = "# ";



    public static final String FLUME_FILE_CONFIGURATION_SUFFIX_DEFAULT = "flume.conf";
    public static final String FLUME_FILE_CONFIGURATION_FILE = "agent_flume.conf";


    public static final String AGENTS_LIST_PROPERTIES_PREFIX = "agents.list";
    public static final String SOURCES_LIST_PROPERTIES_PREFIX = "sources.list";
    public static final String CHANNELS_LIST_PROPERTIES_PREFIX = "channels.list";
    public static final String SINKS_LIST_PROPERTIES_PREFIX = "sinks.list";
    public static final String INTERCEPTORS_LIST_PROPERTIES_PREFIX = "interceptors.list";
    public static final String GROUPS_LIST_PROPERTIES_PREFIX = "groups.list";
    public static final String SOURCES_COMMON_PROPERTY_PROPERTIES_PREFIX = "sources.commonProperty";
    public static final String SOURCES_PARTIAL_PROPERTY_PROPERTIES_PREFIX = "sources.partialProperty";
    public static final String INTERCEPTORS_COMMON_PROPERTY_PROPERTIES_PREFIX = "interceptors.commonProperty";
    public static final String INTERCEPTORS_PARTIAL_PROPERTY_PROPERTIES_PREFIX = "interceptors.partialProperty";
    public static final String CHANNELS_COMMON_PROPERTY_PROPERTIES_PREFIX = "channels.commonProperty";
    public static final String CHANNELS_PARTIAL_PROPERTY_PROPERTIES_PREFIX = "channels.partialProperty";
    public static final String SINKS_COMMON_PROPERTY_PROPERTIES_PREFIX = "sinks.commonProperty";
    public static final String SINKS_PARTIAL_PROPERTY_PROPERTIES_PREFIX = "sinks.partialProperty";

    public static final String PARTIAL_PROPERTY_APPLIED_ELEMENTS_PROPERTIES_PREFIX = "appliedElements";
    public static final String PARTIAL_PROPERTY_PROPERTY_VALUES_PROPERTIES_PREFIX = "propertyValues";
    public static final String PARTIAL_PROPERTY_COMMENT_PROPERTIES_PREFIX = "comment";

    public static final String CONFIGURATION_ERROR = "Configuration error ==> ";

    public static final String FLUME_TOPOLOGY_AGENT = "Agent";
    public static final String FLUME_TOPOLOGY_SOURCE = "Source";
    public static final String FLUME_TOPOLOGY_CHANNEL = "Channel";
    public static final String FLUME_TOPOLOGY_SINK = "Sink";
    public static final String FLUME_TOPOLOGY_INTERCEPTOR = "Interceptor";
    public static final String FLUME_TOPOLOGY_CONNECTION = "Connection";

    public static final String FLUME_TOPOLOGY_PROPERTY_AGENT_NAME = "agentName";
    public static final String FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME = "elementTopologyName";
    public static final String FLUME_TOPOLOGY_GROUP_NAME_DEFAULT = "GROUP";
    public static final String FLUME_TOPOLOGY_PROPERTY_COMMENT_SUFIX = "_comment";

    public static final double FLUME_TOPOLOGY_COMMON_PROPERTY_RATIO = 1.0d;
    public static final String CONFIGURATION_BASE_PROPERTIES_FILE = "flume_configuration_base.properties";


    private FlumeConfiguratorConstants() {
        super();
    }

}

