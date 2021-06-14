package com.kirtesh.subwaytransition.cache.models;

import com.kirtesh.subwaytransition.cache.JvmCache;
import com.kirtesh.subwaytransition.dto.StationDetailsDto;
import com.kirtesh.subwaytransition.utils.GraphUtility;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class GraphNode {

    private String stationName;

    /**
     * A station could be on multiple lines, Hence could have multiple station code.
     */
    private Map<String, StationDetailsDto> lineCodeToNodeInformation;

    /**
     * All connected nodes to current station, including station that have future opening date.
     */
    private List<GraphNode> adjacentNodes;

    public GraphNode(String stationName) {
        this.stationName = stationName;
        this.lineCodeToNodeInformation = new HashMap<>();
        this.adjacentNodes = new LinkedList<>();
    }

    public String getStationName() {
        return this.stationName;
    }

    /**
     * @return All lineCodes which has this station.
     */
    public Set<String> getLineCodes() {
        return new HashSet<>(lineCodeToNodeInformation.keySet());
    }

    public void addLineCodeToMapInfo(String lineCode, StationDetailsDto stationDetailsDto) {
        this.lineCodeToNodeInformation.put(lineCode, stationDetailsDto);
    }

    /**
     * @return All stationDetails.
     */
    public List<StationDetailsDto> getNodeInformation() {
        return new LinkedList<>(lineCodeToNodeInformation.values());
    }

    /**
     * This method will be used to populate Adjacency list line-by-line.
     */
    public void populateAdjacencyList() {
        lineCodeToNodeInformation.forEach((lineCode, stationDto) -> {
            addAdjacentNodes(lineCode, stationDto.getStationNumber());
        });
    }

    /**
     * For a line, any node could have max of two adjacent nodes. This method is been used in populateAdjacencyList() on line-by-line call.
     *
     * CASE-I
     * Line only has one node, hence No Adjacent nodes.
     *
     * CASE-II
     * First element of line is the current node, hence only one adjacent node exist, which is second node.
     *
     * CASE-III
     * Last element of line is the current node, hence only one adjacent node exist, which is second-last node.
     *
     * CASE-IV
     * Current node is somewhere in middle, hence would have two, adjacent nodes. Previous and next node.
     *
     * @param lineCode
     * @param stationNumber
     */
    private void addAdjacentNodes(String lineCode, int stationNumber) {
        LinkedList<StationDetailsDto> list = JvmCache.getLineCodeToLineStationsMap().get(lineCode);
        int size = list.size();
        if (size == 1){
            // CASE-I
        } else if (list.getFirst().getStationNumber() == stationNumber) {
            // CASE-II
            String name = list.get(1).getStationName();
            this.adjacentNodes.add(JvmCache.getNodeMap().get(name));
        } else if (list.getLast().getStationNumber() == stationNumber) {
            // CASE-III
            int previousStation = size - 2;
            String name = list.get(previousStation).getStationName();
            this.adjacentNodes.add(JvmCache.getNodeMap().get(name));
        } else {
            // CASE-IV
            for (int nodeIndex = 0; nodeIndex < size; nodeIndex++) {
                int _stationNumber = list.get(nodeIndex).getStationNumber();
                if (_stationNumber == stationNumber) {
                    String previousNodeName = list.get(nodeIndex - 1).getStationName();
                    String nextNode = list.get(nodeIndex + 1).getStationName();
                    this.adjacentNodes.add(JvmCache.getNodeMap().get(previousNodeName));
                    this.adjacentNodes.add(JvmCache.getNodeMap().get(nextNode));
                }
            }
        }
    }

    public List<GraphNode> getAdjacentNodes() {
        return adjacentNodes;
    }

    /**
     * CASE- I
     * Consider current node is A and line is A1 - A - B - C
     * Now, lets say if B node has opening date in future.
     * So Adjacent Node of A are (A1, B)
     * But if you would call below mentioned method, it would return adjacentNodes of A as (A1, C)
     *
     * If B is last Node of line, than below method to return adjacentNodes of A as (A1)
     *
     *
     * CASE- II
     * Consider current node is B and line is A1 - A - B - C
     * Now, lets say if A node has opening date in future.
     * So Adjacent Node of B are (A, C)
     * But if you would call below mentioned method, it would return adjacentNodes of B as (A1, C)
     *
     * If A is last Node of line, than below method to return adjacentNodes of B as (C)
     *
     * @param travelDate - Date of your travel, this would be used against station open date.
     * @return adjacent nodes
     */
    public List<GraphNode> getOpenAdjacentNodes(LocalDate travelDate) {
        List<GraphNode> updatedAdjacentNodes = new LinkedList<>();

        for (GraphNode adjacentNode: adjacentNodes) {
            String connectingLineCode = GraphUtility.getConnectingLineCode(this, adjacentNode);
            StationDetailsDto dto = adjacentNode.lineCodeToNodeInformation.get(connectingLineCode);
            StationDetailsDto currentNodeDto = this.lineCodeToNodeInformation.get(connectingLineCode);

            if (dto.isOpen(travelDate)) {
                updatedAdjacentNodes.add(adjacentNode);
            } else {
                boolean fetchNext = currentNodeDto.getStationNumber() > dto.getStationNumber();
                GraphNode node = GraphUtility.getNextActiveNodeForLineCode(connectingLineCode, dto, travelDate, fetchNext);
                if (node != null) {
                    updatedAdjacentNodes.add(node);
                }
            }

        }
        return updatedAdjacentNodes;
    }

    public void setAdjacentNodes(List<GraphNode> adjacentNodes) {
        this.adjacentNodes = adjacentNodes;
    }

    /**
     * Check if current node is on multiple lines or not i.e. is station intersection or not.
     * This method would be useful to check if line-change is possible on node or not.
     * If node is not an intersection, line-change is not possible.
     *
     * @return true if current node is an intersection, else false.
     */
    public boolean isIntersection() {
        return this.lineCodeToNodeInformation.keySet().size() >= 2;
    }

    @Override
    public String toString() {
        StringBuffer str = new StringBuffer(String.format("GraphNode { stationName='%s'", stationName));
        str.append(String.format(", lineCodeToNodeInformation='%s'", lineCodeToNodeInformation));
        str.append(String.format(", adjacentNodes='%s' }", adjacentNodes.stream().map(node -> node.stationName).collect(Collectors.toSet())));

        return str.toString();
    }
}
