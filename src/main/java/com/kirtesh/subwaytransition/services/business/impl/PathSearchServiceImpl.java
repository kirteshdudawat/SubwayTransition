package com.kirtesh.subwaytransition.services.business.impl;

import com.kirtesh.subwaytransition.cache.JvmCache;
import com.kirtesh.subwaytransition.enums.APIErrorCodes;
import com.kirtesh.subwaytransition.exception.ApiServiceException;
import com.kirtesh.subwaytransition.response.SearchAvailablePathResponse;
import com.kirtesh.subwaytransition.services.business.PathSearchService;
import com.kirtesh.subwaytransition.services.models.CalculatedPath;
import com.kirtesh.subwaytransition.services.models.GraphNodeDetails;
import com.kirtesh.subwaytransition.services.models.GraphNodePriorityQueue;
import com.kirtesh.subwaytransition.services.validation.ValidationService;
import com.kirtesh.subwaytransition.sro.SearchAvailablePathRequestSro;
import com.kirtesh.subwaytransition.utils.GraphUtility;
import com.kirtesh.subwaytransition.utils.converter.ModelToResponseConverter;
import com.kirtesh.subwaytransition.utils.converter.RequestToSroConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service("pathSearchService")
public class PathSearchServiceImpl implements PathSearchService {

    private static final Logger LOG = LoggerFactory.getLogger(PathSearchServiceImpl.class);

    @Autowired
    private ValidationService validationService;

    @Override
    public SearchAvailablePathResponse searchAvailablePaths(String fromStation, String toStation, LocalDateTime travelTime) throws ApiServiceException {
        SearchAvailablePathRequestSro requestSro = RequestToSroConverter.getAvailablePathRequestSro(fromStation, toStation, travelTime);
        validationService.validateRequest(requestSro);
        LOG.info("Validation successful for request: {}", requestSro);

        CalculatedPath fastestPath = getPath(requestSro, true);
        CalculatedPath shortestPath = getPath(requestSro, false);
        return ModelToResponseConverter.generateResponse(fastestPath, shortestPath, requestSro);
    }

    private CalculatedPath getPath(SearchAvailablePathRequestSro requestSro, boolean isTimeBased) throws ApiServiceException {
        String sourceStationName = JvmCache.getStationCodeToDtoMap().get(requestSro.getSourceStationCode()).getStationName();
        String destinationStationName = JvmCache.getStationCodeToDtoMap().get(requestSro.getDestinationStationCode()).getStationName();

        GraphNodePriorityQueue priorityQueue = new GraphNodePriorityQueue(isTimeBased);
        priorityQueue.initializePriorityQueue(sourceStationName, requestSro.getDateTime());

        return findPath(priorityQueue, destinationStationName);
    }

    /**
     * This method implements Dijkstra Algorithm using priority queue for fetching shortest path.
     * Ref: https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
     *
     * @param priorityQueue holding all nodes based on priority, priority could be time-based / travelled-node based.
     * @param destination destination Node name
     * @return Calculated Path
     * @throws ApiServiceException if no path exist between source and destination
     */
    private CalculatedPath findPath(GraphNodePriorityQueue priorityQueue, String destination) throws ApiServiceException {
        while (priorityQueue.hasMoreElements()) {
            GraphNodeDetails nodeDetails = priorityQueue.getNextElement();
            // Validate if first Node is active. If not, return with result no path available
            if (nodeDetails.isFirstNode()) {
                LocalDate travelDate = nodeDetails.getTimeOfVisit().toLocalDate();
                boolean isOpen = nodeDetails.getNode().getNodeInformation().stream().map(dto -> dto.isOpen(travelDate)).collect(Collectors.toSet()).contains(true);
                if (!isOpen) {
                    LOG.error("No path exist for incoming request as source node is not active.");
                    throw new ApiServiceException(APIErrorCodes.NO_PATH_AVAILABLE_BETWEEN_SOURCE_AND_DESTINATION);
                }
            }

            // No Path available, return as node we are parsing has not been explored via any predecessor.
            if (nodeDetails.isDisconnectedNode()) {
                LOG.error("No path exist for incoming request as graph is disconnected.");
                throw new ApiServiceException(APIErrorCodes.NO_PATH_AVAILABLE_BETWEEN_SOURCE_AND_DESTINATION);
            }

            // generate traversed Path here.
            if (nodeDetails.getNode().getStationName().equals(destination)) {
                return generateResponse(nodeDetails);
            }

            GraphUtility.traverseAdjacentElements(nodeDetails, priorityQueue);
        }

        // If code reaches here, meaning no path exist between source and destination.
        LOG.error("No path exist even after parsing the complete PriorityQueue.");
        throw new ApiServiceException(APIErrorCodes.NO_PATH_AVAILABLE_BETWEEN_SOURCE_AND_DESTINATION);
    }

    /**
     * This would generate response by back-tracking all nodes in path from destination till we reach source and reversing it to find best traversed path
     */
    private CalculatedPath generateResponse(GraphNodeDetails destinationNode) {
        ArrayList<GraphNodeDetails> path = new ArrayList<>();
        GraphNodeDetails traversedNode = destinationNode;

        while (traversedNode != null) {
            path.add(traversedNode);
            traversedNode = traversedNode.getPredecessor();
        }

        Collections.reverse(path);
        return getCalculatePath(path);
    }

    /**
     *
     * @param path Path from sourceNode to destination.
     * @return
     *  routes -> which is list of all station code in order to reach destination from source.
     *  totalJourneyTime -> Total time taken in journey (in minutes)
     *  totalStationsInJourney -> total number of stations to be travelled. Let's say if user has to go from A -> C.
     *                  And line is A -> B -> C so totalStation travelled is 2. B and C. User is already on A.
     *  journeyStartTime -> start-time of journey.
     *  journeyEndTime -> End time of journey.
     *  steps -> Calculate instruction for user on how to reach from source to destination via generateStep()
     */
    private CalculatedPath getCalculatePath(ArrayList<GraphNodeDetails> path) {
        List<String> routes = path.stream().map(node -> node.getNode().getStationName()).collect(Collectors.toList());

        GraphNodeDetails destination = path.get(path.size() - 1);
        int totalJourneyTime = destination.getTotalTimeTakenInMinutes();
        int totalStationsInJourney = destination.getTotalNodesInPath();
        LocalDateTime journeyStartTime = path.get(0).getTimeOfVisit();

        List<String> steps = generateSteps(path);

        return new CalculatedPath(routes, steps, totalJourneyTime, totalStationsInJourney, journeyStartTime, destination.getTimeOfVisit());
    }

    /**
     * This would generate steps for user to follow.
     *
     * @param path Path from sourceNode to destination.
     * @return Steps on how to reach from source to destination
     */
    private List<String> generateSteps(ArrayList<GraphNodeDetails> path) {
        String previousLine = path.get(1).getPredecessorLineCode();

        List<String> steps = new ArrayList<>();
        for (int iterator = 1; iterator < path.size(); iterator++) {
            GraphNodeDetails currentNode = path.get(iterator);
            GraphNodeDetails previousNode = path.get(iterator - 1);
            if (!previousLine.equals(currentNode.getPredecessorLineCode())) {
                String changeLineString = new StringBuilder("Change from ").append(previousLine).append(" line to ").append(currentNode.getPredecessorLineCode()).append(" line").toString();
                steps.add(changeLineString);
                previousLine = currentNode.getPredecessorLineCode();
            }
            String travelString = new StringBuilder("Take ").append(currentNode.getPredecessorLineCode()).append(" line from ").append(previousNode.getNode().getStationName()).append(" to ").append(currentNode.getNode().getStationName()).append(" [ TimeOfVisit: ").append(currentNode.getTimeOfVisit()).append(", Travelled stations so far: ").append(currentNode.getTotalNodesInPath()).append(", JourneyTime in Minutes: ").append(currentNode.getTotalTimeTakenInMinutes()).append(" ]").toString();
            steps.add(travelString);
        }

        return steps;
    }
}