package org.isdb.pimonitoring.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.isdb.pimonitoring.models.MessageMonitorRowModel;
import org.isdb.pimonitoring.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import generated.MessageStatisticsQueryResults;
import generated.RowType;

@Component
/**
 * Service class that monitors the messages and returns the messages interfaces
 * with scheduled, error and terminated status. Service retries thrice, with an
 * interval of 10 seconds.
 */

public class MessageMonitoringService {

	private static Logger LOG = LoggerFactory.getLogger(MessageMonitoringService.class);

	private Client jerseyClient;

	@Value("${pimonitoring.baseUrl}")
	String baseUrl;

	@Value("${pimonitoring.messageMonitoringURL}")
	String messageMonitoringURL;

	@Value("${pimonitoring.messageMonitoringComponentName}")
	String messageMonitoringComponentName;

	public MessageMonitoringService(Client jerseyClient) {
		this.jerseyClient = jerseyClient;
	}

	public List<MessageMonitorRowModel> getMessageMonitorRowModels(String begin, String end) throws JAXBException {

		List<MessageMonitorRowModel> problematicMessageModels = new ArrayList<MessageMonitorRowModel>();
		int maxAttemptsCount = 3;
		for (int i = 1; i <= maxAttemptsCount; i++) {
			try {
				String nonSuccess = null;

				WebTarget webTarget = jerseyClient.target(baseUrl).path(messageMonitoringURL)
						.queryParam("component", messageMonitoringComponentName).queryParam("view", "SR_ENTRY_VIEW_XPI")
						.queryParam("begin", begin).queryParam("end", end).queryParam("detailedStatus", "true");

				Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_XML);
				Response response = invocationBuilder.get();
				LOG.info("Retrieved messageMonitorRowModels status:{}", response.getStatus());

				MessageStatisticsQueryResults results = response.readEntity(MessageStatisticsQueryResults.class);

				if (results != null && results.getData() != null && results.getData().getDataRows() != null
						&& results.getData().getDataRows().getRows() != null) {
					List<RowType> rows = results.getData().getDataRows().getRows();
					List<MessageMonitorRowModel> messageMonitorModels = rows.stream().map(MessageMonitorRowModel::of)
							.collect(Collectors.toList());

					problematicMessageModels = messageMonitorModels.stream().filter(
							x -> x.getSystemError() > 0 || x.getScheduled() > 0 || x.getTerminatedWithErrors() > 0)
							.collect(Collectors.toList());
					nonSuccess = problematicMessageModels.stream().map(MessageMonitorRowModel::getLogString)
							.reduce("\n", String::concat);
				}
				LOG.info("ProblematicMessages:{}", nonSuccess);
				break;
			} catch (Exception e) {
				LOG.error("Exception in getMessageMonitorRowModels", e);
				if (i == maxAttemptsCount) {
					throw e;
				} else {
					Utils.sleepForTenSeconds();
					LOG.info("Retrying getMessageMonitorRowModels");
				}
			}
		}
		return problematicMessageModels;

	}

}
