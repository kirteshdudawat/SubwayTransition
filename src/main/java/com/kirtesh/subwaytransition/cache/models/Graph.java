package com.kirtesh.subwaytransition.cache.models;

import java.util.HashMap;
import java.util.Map;

public class Graph {
    private Map<String, GraphNode> nodeMap;

    public Graph() {
        this.nodeMap = new HashMap<>();
    }

    public Graph(Map<String, GraphNode> nodeMap) {
        this.nodeMap = nodeMap;
    }

    public Map<String, GraphNode> getNodeMap() {
        return nodeMap;
    }

    public void setNodeMap(Map<String, GraphNode> nodeMap) {
        this.nodeMap = nodeMap;
    }

    /**
     * This would put in JVM cache
     * @param nodeNameAsKey - Station Name
     * @param nodeAsValue - Graph Node
     */
    public void setNodeMapElement(String nodeNameAsKey, GraphNode nodeAsValue) {
        this.nodeMap.put(nodeNameAsKey, nodeAsValue);
    }

    @Override
    public String toString() {
        return "Graph{" +
                "nodeMap=" + nodeMap +
                '}';
    }
}
