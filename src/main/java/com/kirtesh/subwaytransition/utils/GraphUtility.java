package com.kirtesh.subwaytransition.utils;

import com.kirtesh.subwaytransition.cache.JvmCache;
import com.kirtesh.subwaytransition.cache.models.GraphNode;
import com.kirtesh.subwaytransition.cache.models.LineConfigs;
import com.kirtesh.subwaytransition.dto.StationDetailsDto;
import com.kirtesh.subwaytransition.enums.ConfigKeys;
import com.kirtesh.subwaytransition.rules.ExpressionHandler;
import com.kirtesh.subwaytransition.services.models.CalculatedTime;
import com.kirtesh.subwaytransition.services.models.GraphNodeDetails;
import com.kirtesh.subwaytransition.services.models.GraphNodePriorityQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Component
public class GraphUtility {

    private static ExpressionHandler expressionHandler;

    public static String getConnectingLineCode(GraphNode source, GraphNode destination) {

        Set<String> sourceLineCodes = source.getLineCodes();
        Set<String> destinationLineCodes = destination.getLineCodes();
        sourceLineCodes.retainAll(destinationLineCodes);

        return sourceLineCodes.iterator().next();
    }

    /**
     * CASE- I (fetchNext=true)
     * Consider current node is A and line is A1 - A - B - C
     * Now, lets say if B node has opening date in future.
     * So Adjacent Node of A should be (A1, C), but currently its (A1, B)
     * So you fetch next station to B which is open, and return it as right side adjacent node.
     *
     * If B is last Node of line, than below method to return null as next right-side adjacent node
     *
     *
     * CASE- II (fetchNext = false)
     * Consider current node is B and line is A1 - A - B - C
     * Now, lets say if A node has opening date in future.
     * So Adjacent Node of B should be (A1, C), but currently its (A, C)
     * So you fetch previous station to A which is open, and return it as left side adjacent node.
     *
     * If A is last Node of line, than below method to return null as previous left side adjacent node
     *
     * @param connectingLine - LineCode
     * @param dto - stationDetails which has opening date in future
     * @param travelDate -> travelDate
     * @param fetchNext - to know we need to fetch next or previous active station.
     * @return nearest next / previous node which is already operational. Operational here means travelDate is post station start date.
     */
    public static GraphNode getNextActiveNodeForLineCode(String connectingLine, StationDetailsDto dto, LocalDate travelDate, boolean fetchNext) {
        if (fetchNext) {
            return fetchNextStation(connectingLine, dto, travelDate);
        } else {
            return fetchPreviousStation(connectingLine, dto, travelDate);
        }
    }

    /**
     * This method calculates time for all neighboring nodes.
     *
     * Case:
     * P --Green line--> A --- Blue Line ---> B ---- Yellow Line ----> C
     * Consider P, A, B, C to be nodes. Starting time is Monday, 1 Jun 2021,  10:00 PM, currently you are on Node A, you are travelling from Node P.
     *
     * How this method check:
     * Step 0: Fetch all active adjacent nodes of Node A at Monday, 1, June 2021, 10:00 via getOpenAdjacentNodes() method.
     * Step 1: Check if  Blue line is operational at 10:00? If no, quit.
     * Step 2: Calculate lineChange time from Green to Blue on Node A at 10:00 PM, lets say line change time is 20 minutes.
     * Step 2(a): Add 20 min to time. So new calculated time is 10:20 PM.
     * Step 3. Now check if Blue line is active at 10:20 PM or not, if not quit.
     * Step 4. Calculate travel time from A -> B, for 10:20 PM. Lets say travel time is 15 minutes.
     * Step 5. Update time of reach as 10:35 PM and increase node count by 1 for Node B.
     * Step 6. Repeat above statements for all of the adjacent nodes of A.
     *
     * @param nodeDetails -> Current traversing Node
     * @param priorityQueue -> PriorityQueue for graph traversal.
     */
    public static void traverseAdjacentElements(GraphNodeDetails nodeDetails, GraphNodePriorityQueue priorityQueue) {
        LocalDate travelDate = nodeDetails.getTimeOfVisit().toLocalDate();
        String lineCode = nodeDetails.getPredecessorLineCode();
        List<GraphNode> neighbours = nodeDetails.getNode().getOpenAdjacentNodes(travelDate);

        for (GraphNode neighbour: neighbours) {
            String connectingLine = GraphUtility.getConnectingLineCode(nodeDetails.getNode(), neighbour);
            CalculatedTime lineChangeTime = GraphUtility.getLineChangeTime(nodeDetails.getNode().isIntersection(), nodeDetails.getTimeOfVisit(), nodeDetails.isFirstNode(), lineCode, connectingLine);

            // Check if line is operational before line change.
            if (lineChangeTime.getChangeTimeInMinutes() != Integer.MIN_VALUE) {
                CalculatedTime travelTimeInMinutes = GraphUtility.getTravelTime(nodeDetails.getTimeOfVisit().plusMinutes(lineChangeTime.getChangeTimeInMinutes()), connectingLine);

                // Check if line is operation after adding lineChangeTime.
                if(travelTimeInMinutes.getTravelTimeInMinutes() != Integer.MIN_VALUE) {
                    int totalTime = nodeDetails.getTotalTimeTakenInMinutes() + lineChangeTime.getChangeTimeInMinutes() + travelTimeInMinutes.getTravelTimeInMinutes();
                    int timeToAdd = lineChangeTime.getChangeTimeInMinutes() + travelTimeInMinutes.getTravelTimeInMinutes();

                    GraphNodeDetails newNode = new GraphNodeDetails(neighbour, totalTime, nodeDetails.getTotalNodesInPath() + 1);
                    newNode.setPredecessor(nodeDetails);
                    newNode.setTimeOfVisit(nodeDetails.getTimeOfVisit().plusMinutes(timeToAdd));
                    newNode.setPredecessorLineCode(connectingLine);

                    priorityQueue.updateElementForOptimizedPath(newNode);
                }
            }
        }
    }

    private static CalculatedTime getTravelTime(LocalDateTime timeOfVisit, String newLineCode) {
        return calculateTime(newLineCode, timeOfVisit);
    }

    /**
     * Calculate line change time. line change time is calculated based of provided timeOfVisit.
     *
     * @param isSourceIntersection
     * @param timeOfVisit
     * @param isSource
     * @param lineCode
     * @param newLineCode -> for calculating line change time.
     * @return
     */
    private static CalculatedTime getLineChangeTime(boolean isSourceIntersection, LocalDateTime timeOfVisit, boolean isSource, String lineCode, String newLineCode) {
        // If starting node, no line change duration.
        // If node is on single line, than no line change time.
        // If both source and destination are intersections and predecessor line code is same as ongoing.
        if (isSource || !isSourceIntersection || newLineCode.equalsIgnoreCase(lineCode)) {
            return new CalculatedTime(0, 0);
        }

        // LineChangeTime
        return calculateTime(newLineCode, timeOfVisit);
    }

    /**
     * This method calculates travelTime between two station at particular time via RuleExecution.
     * This method calculates lineChangeTime between two line at particular time via RuleExecution.
     *
     * In case if line / station is not operational, it would return time as INTEGER.MIN_VALUE
     *
     * @param newLineCode
     * @param timeOfVisit
     * @return WaitTimeAtLineChange, TraveTimeBetweenStation
     */
    private static CalculatedTime calculateTime(String newLineCode, LocalDateTime timeOfVisit) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String dayOfWeek = timeOfVisit.getDayOfWeek().name();
        String travelTimeWithoutSeconds = formatter.format(timeOfVisit.toLocalTime());

        String availableKey = Generator.generateLineCodeType(newLineCode, ConfigKeys.DEFAULT_AVAILABLE_HOURS);
        String nightHoursKey = Generator.generateLineCodeType(newLineCode, ConfigKeys.NIGHT_HOURS);
        String peakHours = Generator.generateLineCodeType(newLineCode, ConfigKeys.PEAK_HOURS);

        LineConfigs defaultAvailableConfigs = JvmCache.getLineCodeTypeToLineConfigs().get(availableKey);
        LineConfigs nightHoursConfig = JvmCache.getLineCodeTypeToLineConfigs().get(nightHoursKey);
        LineConfigs peakHourConfigs = JvmCache.getLineCodeTypeToLineConfigs().get(peakHours);

        // Check if new line is operational
        if (!expressionHandler.executeStatement(defaultAvailableConfigs.getCompiledExpression(), dayOfWeek, travelTimeWithoutSeconds)){
            return new CalculatedTime(Integer.MIN_VALUE, Integer.MIN_VALUE);
        }

        // If code comes here, means line is operational.
        CalculatedTime calculatedTime = new CalculatedTime(defaultAvailableConfigs.getExchangeWaitTime(), defaultAvailableConfigs.getTravelTimeBetweenStation());

        // Check for night hours
        if (nightHoursConfig != null && expressionHandler.executeStatement(nightHoursConfig.getCompiledExpression(), dayOfWeek, travelTimeWithoutSeconds)) {
            calculatedTime.setChangeTimeInMinutes(nightHoursConfig.getExchangeWaitTime());
            calculatedTime.setTravelTimeInMinutes(nightHoursConfig.getTravelTimeBetweenStation());
        }

        // Check for peak hours
        if (expressionHandler.executeStatement(peakHourConfigs.getCompiledExpression(), dayOfWeek, travelTimeWithoutSeconds)) {
            calculatedTime.setChangeTimeInMinutes(peakHourConfigs.getExchangeWaitTime());
            calculatedTime.setTravelTimeInMinutes(peakHourConfigs.getTravelTimeBetweenStation());
        }

        return calculatedTime;
    }

    /**
     *  This method is used to fetch adjacent open node on left side.
     *
     *  Used in GraphUtility.getNextActiveNodeForLineCode(), check getNextActiveNodeForLineCode() method documentation above for more info.
     */
    private static GraphNode fetchPreviousStation(String connectingLine, StationDetailsDto dto, LocalDate travelDate) {
        GraphNode previousOpenNode = null;
        StationDetailsDto currentStation;
        LinkedList<StationDetailsDto> lineStationInSeq = JvmCache.getLineCodeToLineStationsMap().get(connectingLine);

        int iterator = 0;
        do {
            currentStation = lineStationInSeq.get(iterator);
            if (currentStation.getStationNumber() < dto.getStationNumber() && currentStation.isOpen(travelDate)) {
                previousOpenNode = JvmCache.getNodeMap().get(currentStation.getStationName());
            }
            iterator = iterator + 1;
        } while(iterator < lineStationInSeq.size() && currentStation.getStationNumber() < dto.getStationNumber());

        // First Station scenario
        return previousOpenNode;
    }

    /**
     *  This method is used to fetch adjacent open node on right side.
     *
     *  Used in GraphUtility.getNextActiveNodeForLineCode(), check getNextActiveNodeForLineCode() method documentation above for more info.
     */
    private static GraphNode fetchNextStation(String connectingLine, StationDetailsDto dto, LocalDate travelDate) {
        LinkedList<StationDetailsDto> lineStationInSeq = JvmCache.getLineCodeToLineStationsMap().get(connectingLine);

        for (StationDetailsDto stationDto: lineStationInSeq) {
            // LinkedList is already sorted.
            if (stationDto.getStationNumber() > dto.getStationNumber() && stationDto.isOpen(travelDate)) {
                return JvmCache.getNodeMap().get(stationDto.getStationName());
            }
        }
        // Last Station scenario
        return null;
    }

    @Autowired
    private GraphUtility(@Qualifier("expressionHandler") ExpressionHandler expressionHandler) {
        this.expressionHandler = expressionHandler;
    }
}
