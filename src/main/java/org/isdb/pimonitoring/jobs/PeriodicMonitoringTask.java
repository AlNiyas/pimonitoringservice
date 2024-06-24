package org.isdb.pimonitoring.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.isdb.pimonitoring.models.MessageMonitorRowModel;
import org.isdb.pimonitoring.services.ChannelMonitoringService;
import org.isdb.pimonitoring.services.EmailService;
import org.isdb.pimonitoring.services.MessageMonitoringService;
import org.isdb.pimonitoring.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import generated.ChannelType;

@Component
/**
 * This runs periodically and monitors the messages and channels. If there are
 * continous errors for last one hour(it checks at 10 minutes interval) for the
 * same message and channel, it raises an alert
 */
public class PeriodicMonitoringTask {

	private static Logger LOG = LoggerFactory.getLogger(PeriodicMonitoringTask.class);

	@Value("${pimonitoring.periodicTask.sleepTimeInSeconds}")
	private int sleepTimeInSeconds;

	protected MessageMonitoringService messageMonitoringService;
	protected ChannelMonitoringService channelMonitoringService;
	protected EmailService emailService;

	public PeriodicMonitoringTask(MessageMonitoringService messageMonitoringService,
			ChannelMonitoringService channelMonitoringService, EmailService emailService) {
		this.messageMonitoringService = messageMonitoringService;
		this.channelMonitoringService = channelMonitoringService;
		this.emailService = emailService;
	}

	@Scheduled(cron = "0 0 8,11,15 ? * SUN-THU")
	public void execute() throws Exception {
		LOG.info("Begin PeriodicMonitoringTask");

		List<String> scheduledMessageModelsSinceFirstRun = new ArrayList<String>();
		List<MessageMonitorRowModel> problematicMessageModels = new ArrayList<MessageMonitorRowModel>();

		List<String> channelsSinceFirstRun = new ArrayList<String>();
		List<ChannelType> problematicChannels = new ArrayList<ChannelType>();

		try {
			/*
			 * For one hour, it checks with a gap of 10 minutes.
			 */
			for (int loopCounter = 0; loopCounter <= 6; loopCounter++) {
				LOG.info("Beginning loop:{}", loopCounter);

				problematicMessageModels = monitorMessages(scheduledMessageModelsSinceFirstRun, loopCounter);
				problematicChannels = monitorChannels(channelsSinceFirstRun, loopCounter);

				if (loopCounter != 6) {
					// No need to sleep for the last iteration
					Thread.sleep(sleepTimeInSeconds * 1000);
				}
			}

			List<MessageMonitorRowModel> reportMessageModels = getMessageModelsForReport(
					scheduledMessageModelsSinceFirstRun, problematicMessageModels);

			List<ChannelType> reportChannels = getChannelsForReport(channelsSinceFirstRun, problematicChannels);

			Map<String, Object> emailVariables = new HashMap<String, Object>();
			emailVariables.put("messageModels", reportMessageModels);
			emailVariables.put("channels", reportChannels);
			emailService.sendHtmlMessage("PI Periodic Alerts", emailVariables);

			LOG.info("End PeriodicMonitoringTask");
		} catch (Exception e) {
			LOG.error("Exception while exeuting PeriodicMonitoringTask", e);
		}
	}

	private List<ChannelType> getChannelsForReport(List<String> channelsSinceFirstRun,
			List<ChannelType> problematicChannels) {
		List<ChannelType> reportChannels = new ArrayList<ChannelType>();

		for (ChannelType channelType : problematicChannels) {
			if (channelsSinceFirstRun.contains(channelType.getChannelID())) {
				reportChannels.add(channelType);
			}
		}
		return reportChannels;
	}

	/*
	 * Only if the same interface appears in all the runs, it is considered as a error
	 */
	private List<MessageMonitorRowModel> getMessageModelsForReport(List<String> scheduledMessageModelsSinceFirstRun,
			List<MessageMonitorRowModel> problematicMessageModels) {
		List<MessageMonitorRowModel> reportMessageModels = new ArrayList<MessageMonitorRowModel>();
		for (MessageMonitorRowModel messageMonitorRowModel : problematicMessageModels) {
			if (messageMonitorRowModel.hasScheduledOnly()) {
				if (scheduledMessageModelsSinceFirstRun.contains(messageMonitorRowModel.getUniqueKey())) {
					reportMessageModels.add(messageMonitorRowModel);
				}
			} else {
				reportMessageModels.add(messageMonitorRowModel);
			}
		}
		return reportMessageModels;
	}

	/*
	 * Only if the same channel appears in all the runs, it is considered as a error
	 */
	private List<ChannelType> monitorChannels(List<String> channelsSinceFirstRun, int loopCounter) throws Exception {
		List<ChannelType> problematicChannels = channelMonitoringService.getChannels();

		if (loopCounter == 0) {
			channelsSinceFirstRun.addAll(getChannelNames(problematicChannels));
		} else {
			channelsSinceFirstRun.retainAll(getChannelNames(problematicChannels));
		}

		return problematicChannels;
	}

	private List<String> getChannelNames(List<ChannelType> problematicChannels) {
		return problematicChannels.stream().map(ChannelType::getChannelID).collect(Collectors.toList());
	}

	private List<MessageMonitorRowModel> monitorMessages(List<String> scheduledMessageModelsSinceFirstRun,
			int loopCounter) throws Exception {
		List<MessageMonitorRowModel> problematicMessageModels = messageMonitoringService
				.getMessageMonitorRowModels(Utils.getToday(), Utils.getTomorrow());

		if (loopCounter == 0) {
			scheduledMessageModelsSinceFirstRun.addAll(getScheduledMessageModels(problematicMessageModels));
		} else {
			scheduledMessageModelsSinceFirstRun.retainAll(getScheduledMessageModels(problematicMessageModels));
		}

		return problematicMessageModels;
	}

	private List<String> getScheduledMessageModels(List<MessageMonitorRowModel> messageModels) {
		return messageModels.stream().filter(x -> x.getScheduled() > 0).map(MessageMonitorRowModel::getUniqueKey)
				.collect(Collectors.toList());
	}

	public void set_ForTesting_SleepTimeInSeconds(int sleepTimeInSeconds) {
		this.sleepTimeInSeconds = sleepTimeInSeconds;
	}

}