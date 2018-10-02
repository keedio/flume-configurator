package org.keedio.flume.configurator.topology;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.StringComponentNameProvider;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.keedio.flume.configurator.constants.FlumeConfiguratorConstants;
import org.keedio.flume.configurator.structures.FlumeTopology;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class JGraphtDefaultDirectedGraphWrapper implements IGraph {

    private DirectedGraph<FlumeTopology, DefaultEdge> directedGraph = null;

    @Override
    public DirectedGraph<FlumeTopology, DefaultEdge> createGraph() {

        directedGraph = new DefaultDirectedGraph<FlumeTopology, DefaultEdge>(DefaultEdge.class);

        return directedGraph;
    }

    @Override
    public DirectedGraph<FlumeTopology, DefaultEdge> getGraph() {
        return directedGraph;
    }


    @Override
    public boolean detectCycles() {

        boolean withCycles = false;
        // Are there cycles in the dependencies.
        CycleDetector cycleDetector = new CycleDetector<FlumeTopology, DefaultEdge>(directedGraph);

        if (cycleDetector.detectCycles()) {
            withCycles = true;

        }

        return withCycles;
    }


    @Override
    public Set<FlumeTopology> findCycles() {

        Set<FlumeTopology> cycleVertices = null;

        CycleDetector cycleDetector = new CycleDetector<FlumeTopology, DefaultEdge>(directedGraph);

        if (cycleDetector.detectCycles()) {

            cycleVertices = cycleDetector.findCycles();

        }

        return cycleVertices;
    }


    @Override
    public void addGraphVertex(FlumeTopology node) {
        directedGraph.addVertex(node);
    }


    @Override
    public DefaultEdge addGraphEdge(FlumeTopology sourceNode, FlumeTopology destNode) {
        DefaultEdge edge = directedGraph.addEdge(sourceNode, destNode);
        return edge;
    }

    @Override
    public FlumeTopology getVertex(String vertexId, boolean withAgentNodes) {

        FlumeTopology theVertex = null;
        Set<FlumeTopology> vertexSet = directedGraph.vertexSet();

        Iterator<FlumeTopology> itVertexSet = vertexSet.iterator();
        while (itVertexSet.hasNext() && theVertex == null) {
            FlumeTopology vertex = itVertexSet.next();
            if (FlumeConfiguratorConstants.FLUME_TOPOLOGY_AGENT.equals(vertex.getType())) {
                if (!withAgentNodes) {
                    if (vertex.getAgentName().equals(vertexId)) {
                        theVertex = vertex;
                    }
                } else {
                    if (vertex.getId().equals(vertexId)) {
                        theVertex = vertex;
                    }
                }

            } else {
                if (vertex.getId().equals(vertexId)) {
                    theVertex = vertex;
                }
            }
        }

        return theVertex;
    }


    @Override
    public void exportToDot(String fileName) {

        try {
            StringComponentNameProvider<FlumeTopology> stringComponentNameProvider = new StringComponentNameProvider<FlumeTopology> ();
            new DOTExporter<FlumeTopology,DefaultEdge>(stringComponentNameProvider, null, null).exportGraph(this.getGraph(), new FileWriter(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void exportToDot(OutputStream stream) {
        StringComponentNameProvider<FlumeTopology> stringComponentNameProvider = new StringComponentNameProvider<FlumeTopology> ();
        BufferedWriter log = new BufferedWriter(new OutputStreamWriter(stream));
        new DOTExporter<FlumeTopology,DefaultEdge>(stringComponentNameProvider, null, null).exportGraph(this.getGraph(), log);
    }

    @Override
    public String exportToDot() {
        StringComponentNameProvider<FlumeTopology> stringComponentNameProvider = new StringComponentNameProvider<FlumeTopology> ();
        StringWriter sw = new StringWriter();
        new DOTExporter<FlumeTopology,DefaultEdge>(stringComponentNameProvider, null, null).exportGraph(this.getGraph(), sw);

        return sw.toString();
    }


    public void printIGraph() {

        //TopologicalOrderIterator
        TopologicalOrderIterator<FlumeTopology, DefaultEdge> topologicalOrderIterator = new TopologicalOrderIterator(directedGraph);
        System.out.println("\n topologicalOrderIterator Ordering:");
        while (topologicalOrderIterator.hasNext()) {
            FlumeTopology v = topologicalOrderIterator.next();
            System.out.println(v);
        }
    }

    @Override
    public Set<FlumeTopology> getVertexSet() {
        return directedGraph.vertexSet();
    }


    @Override
    public Set<DefaultEdge> getEdgeSet() {return directedGraph.edgeSet();}


    @Override
    public List<FlumeTopology> successorListOf (FlumeTopology vertex) {
        return Graphs.successorListOf(directedGraph, vertex);
    }


    @Override
    public List<FlumeTopology> predecessorListOf(FlumeTopology vertex) {
        return Graphs.predecessorListOf(directedGraph, vertex);
    }

    @Override
    public Set<FlumeTopology> getVertexAncestors(FlumeTopology vertex) {
        return ((DirectedAcyclicGraph) directedGraph).getAncestors(((DirectedAcyclicGraph) directedGraph), vertex);
    }

    @Override
    public Set<FlumeTopology> getVertexDescendants(FlumeTopology vertex) {
        return ((DirectedAcyclicGraph) directedGraph).getDescendants(((DirectedAcyclicGraph) directedGraph), vertex);
    }


    @Override
    public List<Set> getConnectedSets() {
        ConnectivityInspector connectivityInspector = new ConnectivityInspector(directedGraph);
        return connectivityInspector.connectedSets();
    }

}
