package org.keedio.flume.configurator.topology;

public class GraphFactory {

    public static IGraph createGraph(String graphClass) {

        IGraph igraph = null;

        if (graphClass.equals("jgrapht")) {
            igraph = new JGraphtWrapper();
            igraph.createGraph();
        }

        return igraph;
    }
}
