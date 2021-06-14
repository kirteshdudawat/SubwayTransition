package com.kirtesh.subwaytransition.services.models;

import com.kirtesh.subwaytransition.cache.models.GraphNode;

import java.time.LocalDateTime;

public class GraphNodeDetails {
    private GraphNode node;
    private GraphNodeDetails predecessor;
    private LocalDateTime timeOfVisit;
    private int totalTimeTakenInMinutes;
    private int totalNodesInPath;
    private String predecessorLineCode;

    public GraphNodeDetails(GraphNode node, int totalTimeTakenInMinutes, int totalNodesInPath) {
        this.node = node;
        this.totalTimeTakenInMinutes = totalTimeTakenInMinutes;
        this.totalNodesInPath = totalNodesInPath;
    }

    public GraphNode getNode() {
        return node;
    }

    public void setNode(GraphNode node) {
        this.node = node;
    }

    public GraphNodeDetails getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(GraphNodeDetails predecessor) {
        this.predecessor = predecessor;
    }

    public LocalDateTime getTimeOfVisit() {
        return timeOfVisit;
    }

    public void setTimeOfVisit(LocalDateTime timeOfVisit) {
        this.timeOfVisit = timeOfVisit;
    }

    public int getTotalTimeTakenInMinutes() {
        return totalTimeTakenInMinutes;
    }

    public void setTotalTimeTakenInMinutes(int totalTimeTakenInMinutes) {
        this.totalTimeTakenInMinutes = totalTimeTakenInMinutes;
    }

    public int getTotalNodesInPath() {
        return totalNodesInPath;
    }

    public void setTotalNodesInPath(int totalNodesInPath) {
        this.totalNodesInPath = totalNodesInPath;
    }

    public String getPredecessorLineCode() {
        return predecessorLineCode;
    }

    public void setPredecessorLineCode(String predecessorLineCode) {
        this.predecessorLineCode = predecessorLineCode;
    }

    @Override
    public boolean equals(Object obj) {
        // If the object is compared with itself then return true
        if (obj == this) {
            return true;
        }

        /* Check if o is an instance of GraphNodeDetails or not
          "null instanceof [type]" also returns false */
        if (!(obj instanceof GraphNodeDetails)) {
            return false;
        }

        // typecast obj to GraphNodeDetails so that we can compare data members
        GraphNodeDetails graphNodeDetails = (GraphNodeDetails) obj;

        // Compare the data members and return accordingly
        return this.node.getStationName().equalsIgnoreCase(graphNodeDetails.getNode().getStationName());
    }

    /**
     * To check if node is firstNode of path or not.
     * @return boolean value
     */
    public boolean isFirstNode() {
        return this.totalNodesInPath == 0 && this.totalTimeTakenInMinutes == 0 && this.predecessor == null;
    }

    /**
     * To check if current node, is disconnected or not.
     * This would be useful in case when we are trying to search nodes and source/ destination are present on disconnected graph.
     * Hence, no path should exist in above scenario.
     *
     * @return boolean value.
     */
    public boolean isDisconnectedNode() {
        return this.totalTimeTakenInMinutes == Integer.MAX_VALUE
                && this.totalNodesInPath == Integer.MAX_VALUE
                && this.predecessor == null;
    }

    @Override
    public String toString() {
        StringBuffer str = new StringBuffer(String.format("GraphNodeWrapper { node='%s'", node.getStationName()));
        str.append(String.format(", predecessor='%s'", predecessor != null ? predecessor.getNode().getStationName() : "null" ));
        str.append(String.format(", timeOfVisit=%s", timeOfVisit));
        str.append(String.format(", totalTimeTakenInMinutes='%d'", totalTimeTakenInMinutes));
        str.append(String.format(", totalNodesInPath='%d'", totalNodesInPath));
        str.append(String.format(", predecessorLineCode='%s' }", predecessorLineCode));

        return str.toString();
    }
}
