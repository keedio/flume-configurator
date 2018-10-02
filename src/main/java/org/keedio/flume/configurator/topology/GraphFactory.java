package org.keedio.flume.configurator.topology;

import org.jgrapht.DirectedGraph;

public class GraphFactory {

    public static IGraph createGraph(String graphClass) {

        IGraph igraph = null;

        if (graphClass.equals("jgrapht")) {
            igraph = new JGraphtWrapper();
            igraph.createGraph();
        }

        return igraph;
    }

    public static IGraph createDefaultDirectedGraph(String graphClass) {

        IGraph igraph = null;

        if (graphClass.equals("jgrapht")) {
            igraph = new JGraphtDefaultDirectedGraphWrapper();
            igraph.createGraph();
        }

        return igraph;
    }
}
