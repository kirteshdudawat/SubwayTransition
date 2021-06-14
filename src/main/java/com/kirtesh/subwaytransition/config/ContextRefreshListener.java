package com.kirtesh.subwaytransition.config;

import com.kirtesh.subwaytransition.services.startup.StartUpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class ContextRefreshListener implements ApplicationListener<ContextRefreshedEvent>{

	private static final Logger LOG = LoggerFactory.getLogger(ContextRefreshListener.class);
	
	@Autowired
	private StartUpService startupService;

	/**
	 * @param event This method would be called during start-up when context is been built.
	 */
	public void onApplicationEvent(ContextRefreshedEvent event) {
		LOG.info("Executing onApplicationEvent on ContextRefresh");
		try {
			startupService.executeStartupTasks();
		} catch (Exception e) {
			LOG.error("===============================================Application-Start-Up-Failed================================================");
			LOG.error("{}", e);
			LOG.error("===============================================Application-Start-Up-Failed================================================");
		}
		LOG.info("Completed onApplicationEvent on ContextRefresh");
	}

}
