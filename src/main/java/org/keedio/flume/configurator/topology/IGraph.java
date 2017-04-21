package org.keedio.flume.configurator.topology;

import org.keedio.flume.configurator.structures.FlumeTopology;

import java.io.OutputStream;
import java.util.List;
import java.util.Set;

public interface IGraph {

    /**
     * Create a directed graph
     * @param <T> Type of Directed Graph
     * @return Directed Graph
     */
    <T> T createGraph();

    /**
     * Get the graph
     * @param <T> Type of Directed Graph
     * @return Directed Graph
     */
    <T> T getGraph();


    /**
     * Detect if there are cycles in the graph
     * @return true if there are cycles, false otherwise
     */
    boolean detectCycles();

    /**
     * Find the cycles of the graph
     * @return
     */
    Set<FlumeTopology> findCycles();

    /**
     * Add a vertex to the graph
     * @param node
     */
    void addGraphVertex(FlumeTopology node);

    /**
     * Add a edge to the graph
     * @param sourceNode source Node/Vertex
     * @param targetNode target Node/Vertex
     * @param <T> Type of the Edge
     * @return Edge
     */

     <T> T addGraphEdge(FlumeTopology sourceNode, FlumeTopology targetNode);

    /**
     * Get the vertex of the graph with the indicated id
     * @param vertexId String with the vertex's id
     * @param withAgentNodes boolean indicating if there are real agent node/vertex
     * @return the vertex with the indicated id if exists, null otherwise
     */
    <T> FlumeTopology getVertex(String vertexId, boolean withAgentNodes);

    /**
     * Export the graph to DOT format
     * @param fileName
     */
    void exportToDot(String fileName);

    /**
     * Export the graph to DOT format
     * @param stream
     */
    void exportToDot(OutputStream stream);

    /**
     * Export the graph to DOT format
     */
    String exportToDot();


    /**
     * Get the vertex set
     * @return vertex set
     */
    Set<FlumeTopology> getVertexSet();

    /**
     * Get List of succesors
     * @param vertex
     * @return
     */
    List<FlumeTopology> successorListOf(FlumeTopology vertex);

    /**
     * Get list of predecessors
     * @param vertex
     * @return
     */
    List<FlumeTopology> predecessorListOf(FlumeTopology vertex);

    /**
     * Get ancestors of the vertex
     * @param vertex
     * @return
     */
    Set<FlumeTopology> getVertexAncestors(FlumeTopology vertex);

    /**
     * Get descendants of the vertex
     * @param vertex
     * @return
     */
    Set<FlumeTopology> getVertexDescendants(FlumeTopology vertex);



}
