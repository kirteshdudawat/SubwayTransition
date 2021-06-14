package com.kirtesh.subwaytransition.services.models;

import java.util.Comparator;

/**
 * To be used in PriorityQueue with minimum stations travelled.
 */
public class GraphNodeDetailsDistanceComparator implements Comparator<GraphNodeDetails> {
    @Override
    public int compare(GraphNodeDetails node1, GraphNodeDetails node2) {
        return Integer.compare(node1.getTotalNodesInPath(), node2.getTotalNodesInPath());
    }
}
