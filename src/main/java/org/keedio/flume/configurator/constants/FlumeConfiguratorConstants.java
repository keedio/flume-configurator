package org.keedio.flume.configurator.constants;

import java.util.ArrayList;

public class FlumeConfiguratorConstants {



    public static final String WHITE_SPACE = " ";
    public static final String ASTERISK = "*";
    public static final String HASH = "#";
    public static final String DOT_SEPARATOR = ".";
    public static final String UNDERSCORE_SEPARATOR = "_";
    public static final String COMMA_SEPARATOR = ",";
    public static final String PROPERTY_SEPARATOR_DEFAULT = ";";
    public static final String NEW_LINE = "\n";
    public static final String TABULATOR = "\t";

    public static final String DOT_REGEX = "\\.";
    public static final String WHITE_SPACE_REGEX = "\\s+";

    public static final String SOURCES_PROPERTY = "sources";
    public static final String CHANNELS_PROPERTY = "channels";
    public static final String CHANNEL_PROPERTY = "channel";
    public static final String SINKS_PROPERTY = "sinks";
    public static final String SINKGROUPS_PROPERTY = "sinkgroups";
    public static final String INTERCEPTORS_PROPERTY = "interceptors";
    public static final String SELECTOR_PROPERTY = "selector";
    public static final String TYPE_PROPERTY = "type";
    public static final String MAPPING_PROPERTY = "mapping";
    public static final String DEFAULT_PROPERTY = "default";
    public static final String OPTIONAL_PROPERTY = "optional";
    public static final String PROCESSOR_PROPERTY = "processor";
    public static final String PROCESSOR_TYPE_PROPERTY = "processor.type";
    public static final String COMMENT_PROPERTY_PREFIX = "comment";
    public static final String COMMENT_PREFIX = "# ";



    public static final String FLUME_FILE_CONFIGURATION_SUFFIX_DEFAULT = "flume.conf";
    public static final String FLUME_FILE_CONFIGURATION_FILE = "agent_flume.conf";


    public static final String AGENTS_LIST_PROPERTIES_PREFIX = "agents.list";
    public static final String SOURCES_LIST_PROPERTIES_PREFIX = "sources.list";
    public static final String CHANNELS_LIST_PROPERTIES_PREFIX = "channels.list";
    public static final String SINKS_LIST_PROPERTIES_PREFIX = "sinks.list";
    public static final String SINKGROUPS_LIST_PROPERTIES_PREFIX = "sinkgroups.list";
    public static final String INTERCEPTORS_LIST_PROPERTIES_PREFIX = "interceptors.list";
    public static final String GROUPS_LIST_PROPERTIES_PREFIX = "groups.list";
    public static final String SELECTORS_LIST_PROPERTIES_PREFIX = "sourcesWithSelector.list";
    public static final String SOURCES_COMMON_PROPERTY_PROPERTIES_PREFIX = "sources.commonProperty";
    public static final String SOURCES_PARTIAL_PROPERTY_PROPERTIES_PREFIX = "sources.partialProperty";
    public static final String SELECTORS_COMMON_PROPERTY_PROPERTIES_PREFIX = "selectors.commonProperty";
    public static final String SELECTORS_PARTIAL_PROPERTY_PROPERTIES_PREFIX = "selectors.partialProperty";
    public static final String INTERCEPTORS_COMMON_PROPERTY_PROPERTIES_PREFIX = "interceptors.commonProperty";
    public static final String INTERCEPTORS_PARTIAL_PROPERTY_PROPERTIES_PREFIX = "interceptors.partialProperty";
    public static final String CHANNELS_COMMON_PROPERTY_PROPERTIES_PREFIX = "channels.commonProperty";
    public static final String CHANNELS_PARTIAL_PROPERTY_PROPERTIES_PREFIX = "channels.partialProperty";
    public static final String SINKS_COMMON_PROPERTY_PROPERTIES_PREFIX = "sinks.commonProperty";
    public static final String SINKS_PARTIAL_PROPERTY_PROPERTIES_PREFIX = "sinks.partialProperty";
    public static final String SINKGROUPS_COMMON_PROPERTY_PROPERTIES_PREFIX = "sinkgroups.commonProperty";
    public static final String SINKGROUPS_PARTIAL_PROPERTY_PROPERTIES_PREFIX = "sinkgroups.partialProperty";

    public static final String PARTIAL_PROPERTY_APPLIED_ELEMENTS_PROPERTIES_PREFIX = "appliedElements";
    public static final String PARTIAL_PROPERTY_PROPERTY_VALUES_PROPERTIES_PREFIX = "propertyValues";
    public static final String PARTIAL_PROPERTY_COMMENT_PROPERTIES_PREFIX = "comment";

    public static final String CONFIGURATION_ERROR = "Configuration error ==> ";

    public static final String FLUME_TOPOLOGY_AGENT = "Agent";
    public static final String FLUME_TOPOLOGY_SOURCE = "Source";
    public static final String FLUME_TOPOLOGY_CHANNEL = "Channel";
    public static final String FLUME_TOPOLOGY_SINK = "Sink";
    public static final String FLUME_TOPOLOGY_SINKGROUP = "SinkGroup";
    public static final String FLUME_TOPOLOGY_INTERCEPTOR = "Interceptor";
    public static final String FLUME_TOPOLOGY_CONNECTION = "Connection";

    public static final String FLUME_TOPOLOGY_PROPERTY_AGENT_NAME = "agentName";
    public static final String FLUME_TOPOLOGY_PROPERTY_ELEMENT_TOPOLOGY_NAME = "elementTopologyName";
    public static final String FLUME_TOPOLOGY_GROUP_NAME_DEFAULT = "GROUP";
    public static final String FLUME_TOPOLOGY_PROPERTY_COMMENT_SUFIX = "_comment";

    //public static final double FLUME_TOPOLOGY_COMMON_PROPERTY_RATIO = 0.75d;
    public static final String CONFIGURATION_BASE_PROPERTIES_FILE = "flume_configuration_base.properties";
    public static final String DRAW2D_FLUME_TOPOLOGY_FILE = "flume_topology.json";


    public static final int AGENTS_PROPERTY_PART_INDEX = 1;
    public static final int SOURCES_LIST_PROPERTY_PART_INDEX = 2;
    public static final int CHANNELS_LIST_PROPERTY_PART_INDEX = 2;
    public static final int SINKS_LIST_PROPERTY_PART_INDEX = 2;
    public static final int SINKGROUPS_LIST_PROPERTY_PART_INDEX = 2;
    public static final int INTERCEPTORS_LIST_PROPERTY_PART_INDEX = 4;

    public static final int ELEMENT_PROPERTY_PART_INDEX = 3;
    public static final int INTERCEPTOR_PROPERTY_PART_INDEX = 5;
    public static final int SOURCE_INTERCEPTORS_PART_INDEX = 4;
    public static final int SOURCE_CHANNELS_PART_INDEX = 4;
    public static final int SINK_CHANNEL_PART_INDEX = 4;
    public static final int SINKGROUP_PROCESSOR_PART_INDEX = 4;
    public static final int SINKGROUP_SINKS_PART_INDEX = 4;

    public static final String FLUME_TYPE_PROPERTY = "flumeType";
    public static final String DRAW2D_START_TYPE = "draw2d.shape.node.Start";
    public static final String DRAW2D_BETWEEN_TYPE = "draw2d.shape.node.Between";
    public static final String DRAW2D_END_TYPE = "draw2d.shape.node.End";
    public static final String DRAW2D_CONNECTION_TYPE = "draw2d.Connection";

    public static final String DRAW2D_AGENT_TYPE = "draw2d.Agent";
    public static final String DRAW2D_SOURCE_TYPE = "draw2d.Source";
    public static final String DRAW2D_CHANNEL_TYPE = "draw2d.Channel";
    public static final String DRAW2D_SINK_TYPE = "draw2d.Sink";
    public static final String DRAW2D_SINKGROUP_TYPE = "draw2d.Sinkgroup";
    public static final String DRAW2D_INTERCEPTOR_TYPE = "draw2d.Interceptor";


    public static final String DRAW2D_START_CSS_CLASS = "draw2d_shape_node_Start";
    public static final String DRAW2D_BETWEEN_CSS_CLASS = "draw2d_shape_node_Between";
    public static final String DRAW2D_END_CSS_CLASS = "draw2d_shape_node_End";
    public static final String DRAW2D_CONNECTION_CSS_CLASS = "draw2d_Connection";

    public static final String OUTPUT_PORT = "output0";
    public static final String INTPUT_PORT = "input0";

    public static final int CANVAS_PX_WIDTH = 2000;
    public static final int CANVAS_PX_HEIGHT = 2000;
    public static final int CANVAS_ELEMENT_PX_WIDTH = 50;
    public static final int CANVAS_ELEMENT_PX_HEIGHT= 50;
    //public static final String CANVAS_ELEMENT_BGCOLOR = "#4D90FE";

    public static final String CANVAS_AGENT_BGCOLOR = "#0000FF";
    public static final String CANVAS_SOURCE_BGCOLOR = "#FFB266";
    public static final String CANVAS_INTERCEPTOR_BGCOLOR = "#99FFFF";
    public static final String CANVAS_CHANNEL_BGCOLOR = "#99FF99";
    public static final String CANVAS_SINK_BGCOLOR = "#FF99FF";
    public static final String CANVAS_SINKGROUP_BGCOLOR = "#f44242";

    public static final String CANVAS_ELEMENT_COLOR = "#000000";
    public static final String CANVAS_ELEMENT_STROKE= "1";
    public static final String CANVAS_ELEMENT_ALPHA = "1";
    public static final String CANVAS_ELEMENT_RADIUS = "2";

    public static final int CANVAS_ELEMENTS_HEIGHT_PX_SEPARATION = 50;
    public static final int CANVAS_AGENTS_HEIGHT_PX_SEPARATION = 200;
    public static final int CANVAS_HORIZONTAL_MARGIN_PX = 100;

    public static final String AGENT_GROUP_PROPERTY = "agentGroup";

    public static final String BASE_TO_FLUME_CONFIGURATION_MODE = "baseToFlume";
    public static final String DRAW2D_TO_FLUME_CONFIGURATION_MODE = "draw2DToFlume";
    public static final String FLUME_TO_DRAW2D_CONFIGURATION_MODE = "flumeToDraw2D";

    public static final String BASE_CONFIGURATION_KEY = "baseConfiguration";
    public static final String FLUME_CONFIGURATION_KEY = "FLUMEConfiguration";

    public static final int FIXED_SLICES_NUMBER = 4;


    private FlumeConfiguratorConstants() {
        super();
    }

}

