package org.isdb;

import java.util.List;

import org.isdb.pimonitoring.models.MessageMonitorRowModel;
import org.isdb.pimonitoring.services.ChannelMonitoringService;
import org.isdb.pimonitoring.services.EmailService;
import org.isdb.pimonitoring.services.MessageMonitoringService;
import org.isdb.pimonitoring.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import generated.ChannelType;

@RestController
@Component
public class HomeController {

	private static Logger LOG = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	EmailService emailService;

	@Autowired
	MessageMonitoringService messageMonitoringService;

	@Autowired
	ChannelMonitoringService channelMonitoringService;

	@GetMapping(value = "/home")
	@ResponseBody
	/**
	 * Health checkup API.
	 * @return
	 */
	public String testMethod() {
		StringBuilder response = new StringBuilder("At Home\n");
		try {
			List<MessageMonitorRowModel> messageMonitorRowModels = messageMonitoringService
					.getMessageMonitorRowModels(Utils.getYesterday(), Utils.getToday());

			LOG.info("Inside Home Controller");
			response.append("\nMessage Monitor Rows:" + messageMonitorRowModels.size() + System.lineSeparator());

			List<ChannelType> channels = channelMonitoringService.getChannels();
			response.append("\nChannel Monitor Rows:" + channels.size() + System.lineSeparator());

			emailService.sendHtmlMessage("Test Mail", null);
			response.append("\nMail send successfully" + System.lineSeparator());

		} catch (Exception e) {
			return response.toString() + System.lineSeparator() + e.getMessage();
		}
		return response.toString();
	}

	@GetMapping(value = "/")
	public String testIndexMethod() {
		LOG.info("Inside Home Controller");
		return "index";
	}
}
