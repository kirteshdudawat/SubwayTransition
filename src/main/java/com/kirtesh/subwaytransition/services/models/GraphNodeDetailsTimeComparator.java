package com.kirtesh.subwaytransition.services.models;

import java.util.Comparator;

/**
 * To be used in PriorityQueue to reach destination in least time.
 */
public class GraphNodeDetailsTimeComparator implements Comparator<GraphNodeDetails> {
    @Override
    public int compare(GraphNodeDetails node1, GraphNodeDetails node2) {
        return Integer.compare(node1.getTotalTimeTakenInMinutes(), node2.getTotalTimeTakenInMinutes());
    }
}
