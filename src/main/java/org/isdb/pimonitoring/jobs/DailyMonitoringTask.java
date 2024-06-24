package org.isdb.pimonitoring.jobs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.isdb.pimonitoring.models.MessageMonitorRowModel;
import org.isdb.pimonitoring.services.EmailService;
import org.isdb.pimonitoring.services.MessageMonitoringService;
import org.isdb.pimonitoring.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
/**
 * This monitors the messages of the previous day and alerts if there are any
 * failures. Runs once a day in the morning.
 */
public class DailyMonitoringTask {

	private static Logger LOG = LoggerFactory.getLogger(DailyMonitoringTask.class);

	protected MessageMonitoringService messageMonitoringService;
	protected EmailService emailService;

	public DailyMonitoringTask(MessageMonitoringService messageMonitoringService, EmailService emailService) {
		this.messageMonitoringService = messageMonitoringService;
		this.emailService = emailService;
	}

	@Scheduled(cron = "0 0 7 ? * *")
	public void execute() {
		LOG.info("Begin DailyMonitoringTask");

		try {
			List<MessageMonitorRowModel> problematicMessageModels = monitorMessages();

			Map<String, Object> emailVariables = new HashMap<String, Object>();
			emailVariables.put("messageModels", problematicMessageModels);
			emailService.sendHtmlMessage("PI Daily Alerts", emailVariables);

			LOG.info("End DailyMonitoringTask");
		} catch (Exception e) {
			LOG.error("Exception while exeuting DailyMonitoringTask", e);

		}
	}

	private List<MessageMonitorRowModel> monitorMessages() throws Exception {
		return messageMonitoringService.getMessageMonitorRowModels(Utils.getYesterday(), Utils.getToday());
	}

}