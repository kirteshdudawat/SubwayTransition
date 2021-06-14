package com.kirtesh.subwaytransition.services.models;

import com.kirtesh.subwaytransition.cache.JvmCache;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class GraphNodePriorityQueue {
    private int priorityQueueSize;
    private Queue<GraphNodeDetails> priorityQueue;
    private Map<String, GraphNodeDetails> stationNameToDetails = new HashMap<>();
    private Map<String, GraphNodeDetails> traversedNodes = new HashMap<>();
    private boolean timeBased;

    public GraphNodePriorityQueue(boolean timeBased) {
        this.timeBased = timeBased;
        this.priorityQueueSize = JvmCache.getNodeMap().size();
        if (this.timeBased) {
            this.priorityQueue = new PriorityQueue<>(this.priorityQueueSize, new GraphNodeDetailsTimeComparator());
        } else {
            this.priorityQueue = new PriorityQueue<>(this.priorityQueueSize, new GraphNodeDetailsDistanceComparator());
        }
    }

    /**
     * This method would initialize the priority queue for finding path.
     * @param startingStationName -> Starting station name, as totalNode and time-taken for starting station would be 0.
     * @param travelTime -> Journey start time.
     */
    public void initializePriorityQueue(String startingStationName, LocalDateTime travelTime) {
        JvmCache.getNodeMap().forEach((stationName, graphNode) -> {
            GraphNodeDetails graphNodeDetails;
            if (stationName.equalsIgnoreCase(startingStationName)) {
                graphNodeDetails = new GraphNodeDetails(graphNode, 0, 0);
                graphNodeDetails.setTimeOfVisit(travelTime);
            } else {
                graphNodeDetails = new GraphNodeDetails(graphNode, Integer.MAX_VALUE, Integer.MAX_VALUE);
            }
            this.stationNameToDetails.put(stationName, graphNodeDetails);
            this.priorityQueue.add(graphNodeDetails);
        });
    }

    public boolean isNodeTraversed(String stationName) {
        return this.traversedNodes.containsKey(stationName);
    }

    /**
     * This would update the priority of element in PriorityQueue.
     * We would just remove the node whose priority is to be changed and re-insert it with new priority.
     *
     * @param newNode -> Node with new priority.
     */
    public void updateElementForOptimizedPath(GraphNodeDetails newNode) {
        String stationName = newNode.getNode().getStationName();
        GraphNodeDetails oldNode = this.stationNameToDetails.get(stationName);

        if (this.timeBased) {
            if(oldNode.getTotalTimeTakenInMinutes() > newNode.getTotalTimeTakenInMinutes()) {
                this.priorityQueue.remove(oldNode);
                this.priorityQueue.add(newNode);
                this.stationNameToDetails.put(stationName, newNode);
            }
        } else {
            if(oldNode.getTotalNodesInPath() > newNode.getTotalNodesInPath()) {
                this.priorityQueue.remove(oldNode);
                this.priorityQueue.add(newNode);
                this.stationNameToDetails.put(stationName, newNode);
            }
        }
    }

    public boolean hasMoreElements() {
        return !this.priorityQueue.isEmpty();
    }

    public GraphNodeDetails getNextElement() {
        GraphNodeDetails topElement = this.priorityQueue.poll();
        this.traversedNodes.put(topElement.getNode().getStationName(), topElement);

        return topElement;
    }
}
