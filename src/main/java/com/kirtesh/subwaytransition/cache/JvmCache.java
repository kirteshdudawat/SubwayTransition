package com.kirtesh.subwaytransition.cache;

import com.kirtesh.subwaytransition.cache.models.Graph;
import com.kirtesh.subwaytransition.cache.models.GraphNode;
import com.kirtesh.subwaytransition.cache.models.LineConfigs;
import com.kirtesh.subwaytransition.dto.StationDetailsDto;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class would contain all JVM level cache.
 */
@Component
public class JvmCache {
    public static Map<String, List<StationDetailsDto>> stationNameToDtoListMap = new ConcurrentHashMap<String, List<StationDetailsDto>>();

    public static Map<String, StationDetailsDto> stationCodeToDtoMap = new ConcurrentHashMap<String, StationDetailsDto>();

    public static Map<String, LinkedList<StationDetailsDto>> lineCodeToLineStationsMap = new ConcurrentHashMap<String, LinkedList<StationDetailsDto>>();

    public static Map<String, GraphNode> nodeMap = new ConcurrentHashMap<>();

    public static Map<String, LineConfigs> lineCodeTypeToLineConfigs = new ConcurrentHashMap<>();

    public static Graph graph = new Graph();

    /**
     * Would add node to stationNameToDtoListMap. If mapping already exist it would add new station to LinkedList.
     *
     * @param name -> Station Name
     * @param stationDetailsDto -> Station Details
     */
    public static void addToStationNameToDtoListMap(String name, StationDetailsDto stationDetailsDto) {
        List<StationDetailsDto> stationDetailsDtos;
        if (JvmCache.stationNameToDtoListMap.containsKey(name)) {
            stationDetailsDtos = JvmCache.stationNameToDtoListMap.get(name);
        } else {
            stationDetailsDtos = new LinkedList<>();
        }
        stationDetailsDtos.add(stationDetailsDto);
        JvmCache.stationNameToDtoListMap.put(name, stationDetailsDtos);
    }

    /**
     * lineCodeToLineStationsMap saves all stations on a line ordered by station number. We have overriden compareTo in StationDetailsDto class.
     *
     * @param name -> Line code eg. CC, EW
     * @param stationDetailsDtos -> list of all nodes on that line.
     */
    public static void addToLineCodeToLineStationsMap(String name, LinkedList<StationDetailsDto> stationDetailsDtos) {
        Collections.sort(stationDetailsDtos);
        JvmCache.lineCodeToLineStationsMap.put(name, stationDetailsDtos);
    }

    public static void addToStationCodeToDtoMap(String name, StationDetailsDto _stationCodeToDto) {
        JvmCache.stationCodeToDtoMap.put(name, _stationCodeToDto);
    }

    public static void addElementToGraphNode(String name, GraphNode _graphNode) {
        JvmCache.nodeMap.put(name, _graphNode);
    }

    public static Map<String, LineConfigs> getLineCodeTypeToLineConfigs() {
        return lineCodeTypeToLineConfigs;
    }

    public static void addElementToLineCodeTypeToLineConfigs(String lineCodeType, LineConfigs lineConfigs) {
        JvmCache.lineCodeTypeToLineConfigs.put(lineCodeType, lineConfigs);
    }

    public static void setGraph() {
        JvmCache.graph = new Graph(JvmCache.nodeMap);
    }

    public static Map<String, List<StationDetailsDto>> getStationNameToDtoListMap() {
        return stationNameToDtoListMap;
    }

    public static Map<String, StationDetailsDto> getStationCodeToDtoMap() {
        return stationCodeToDtoMap;
    }

    public static Map<String, LinkedList<StationDetailsDto>> getLineCodeToLineStationsMap() {
        return lineCodeToLineStationsMap;
    }

    public static Map<String, GraphNode> getNodeMap() {
        return nodeMap;
    }

    public static Graph getGraph() {
        return graph;
    }

    public static void clearCache() {
        JvmCache.nodeMap.clear();
        JvmCache.lineCodeToLineStationsMap.clear();
        JvmCache.stationNameToDtoListMap.clear();
        JvmCache.graph = new Graph();
        JvmCache.stationCodeToDtoMap.clear();
        JvmCache.lineCodeTypeToLineConfigs.clear();
    }
}
