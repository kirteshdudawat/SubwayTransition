package com.kirtesh.subwaytransition.services.startup.impl;

import com.kirtesh.subwaytransition.cache.JvmCache;
import com.kirtesh.subwaytransition.cache.models.GraphNode;
import com.kirtesh.subwaytransition.cache.models.LineConfigs;
import com.kirtesh.subwaytransition.config.models.LineConfigurations;
import com.kirtesh.subwaytransition.config.models.LineLevelConfig;
import com.kirtesh.subwaytransition.config.models.TimeBasedLineConfig;
import com.kirtesh.subwaytransition.dao.service.DaoService;
import com.kirtesh.subwaytransition.dto.StationDetailsDto;
import com.kirtesh.subwaytransition.enums.APIErrorCodes;
import com.kirtesh.subwaytransition.enums.ConfigKeys;
import com.kirtesh.subwaytransition.enums.DataSource;
import com.kirtesh.subwaytransition.exception.ApiServiceException;
import com.kirtesh.subwaytransition.rules.ExpressionHandler;
import com.kirtesh.subwaytransition.services.startup.StartUpService;
import com.kirtesh.subwaytransition.utils.Generator;
import com.kirtesh.subwaytransition.utils.converter.EntityModelToDtoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/*
 * author : kirtesh
 */
@Service("startUpService")
public class StartUpServiceImpl implements StartUpService {
	
	private static final Logger LOG = LoggerFactory.getLogger(StartUpServiceImpl.class);

	@Autowired
	private DaoService daoService;

	@Autowired
	private LineConfigurations lineConfigurations;

	@Autowired
	private ExpressionHandler expressionHandler;

	@Override
	public void executeStartupTasks() throws ApiServiceException {
		loadJvmCache();
		loadStartupConfigurations();
	}

	/**
	 * This methods populate JVM cache at start-up from application.yml file.
	 * It would compile expression and save line related configs.
	 *
	 * JVM cache updated in below method (follow JvmCache.java for more info):
	 * 1. lineCodeTypeToLineConfigs
	 *
	 * @throws ApiServiceException if LineCode is missing
	 */
	private void loadStartupConfigurations() throws ApiServiceException {
		LOG.info("==============================================STARTING CONFIGURATION LOADING==========================================");
		Set<String> configLineCodes = lineConfigurations.getLineLevelConfigs().stream().map(LineLevelConfig::getLineCode).collect(Collectors.toSet());
		for (String lineCode : JvmCache.getLineCodeToLineStationsMap().keySet()) {
			if (!configLineCodes.contains(lineCode)) {
				LOG.error("No lineCodeConfig found for {}. Please check configuration in application.yml.", lineCode);
				throw new ApiServiceException(APIErrorCodes.MISSING_LINE_CODE_CONFIG);
			}
		}

		compileAndPopulateLineCodeConfig(lineConfigurations.getLineLevelConfigs());
		LOG.info("==============================================STARTING CONFIGURATION FINISHED==========================================");
	}

	private void compileAndPopulateLineCodeConfig(List<LineLevelConfig> lineLevelConfigs) throws ApiServiceException {
		for (LineLevelConfig lineLevelConfig: lineLevelConfigs) {
			String lineCodeInUpperCase = lineLevelConfig.getLineCode().toUpperCase();

			for (TimeBasedLineConfig timeBasedLineConfig: lineLevelConfig.getTimeBasedLineConfigs()) {
				String lineCodeType = Generator.generateLineCodeType(lineCodeInUpperCase, ConfigKeys.fromString(timeBasedLineConfig.getTimeType()));
				LineConfigs lineConfigs = EntityModelToDtoConverter.convert(timeBasedLineConfig);
				Serializable compiledExpression = expressionHandler.compileStatement(timeBasedLineConfig.getExpression());
				lineConfigs.setCompiledExpression(compiledExpression);
				JvmCache.addElementToLineCodeTypeToLineConfigs(lineCodeType, lineConfigs);
			}
		}
	}

	/**
	 * This methods populate JVM cache at start-up.
	 * JVM cache updated in below method (follow JvmCache.java for more info):
	 * 1. stationNameToDtoListMap
	 * 2. stationCodeToDtoMap
	 * 3. lineCodeToLineStationsMap
	 * 4. nodeMap
	 * 5. graph via populateGraph()
	 *
	 * @throws ApiServiceException if invalid data is present at datasource.
	 */
	private void loadJvmCache() throws ApiServiceException {
		LOG.info("==============================================Starting to Populate JVM Cache==========================================");
		List<StationDetailsDto> result = daoService.getStationList(DataSource.CSV_FILE);
		result.forEach(element -> JvmCache.addToStationNameToDtoListMap(element.getStationName(), element));
		result.forEach(element -> JvmCache.addToStationCodeToDtoMap(element.getStationCode(), element));

		Map<String, Set<StationDetailsDto>> map = result.stream().collect(Collectors.groupingBy(StationDetailsDto::getLineCode, Collectors.toSet()));
		map.forEach((key, value) -> JvmCache.addToLineCodeToLineStationsMap(key, new LinkedList<>(value)));

		populateGraph();
		LOG.info("==============================================JVM Cache Populated==========================================");
	}

	private void populateGraph() {
		JvmCache.getStationNameToDtoListMap().forEach((stationName, stationDtoList) -> {
				GraphNode node = new GraphNode(stationName);
				stationDtoList.forEach(stationDto -> node.addLineCodeToMapInfo(stationDto.getLineCode(), stationDto));
				JvmCache.addElementToGraphNode(stationName, node);
			}
		);

		JvmCache.getNodeMap().forEach((nodeName, node) -> {
				node.populateAdjacencyList();
			}
		);

		JvmCache.setGraph();
	}

}
