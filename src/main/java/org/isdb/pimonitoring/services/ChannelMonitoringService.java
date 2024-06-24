package org.isdb.pimonitoring.services;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.isdb.pimonitoring.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import generated.ChannelStatusResult;
import generated.ChannelType;

@Component
/**
 * Service class that monitors the channels and returns the channels with error
 * state. Service retries thrice, with an interval of 10 seconds.
 */
public class ChannelMonitoringService {

	private static Logger LOG = LoggerFactory.getLogger(MessageMonitoringService.class);

	private Client jerseyClient;

	@Value("${pimonitoring.baseUrl}")
	String baseUrl;

	@Value("${pimonitoring.channelMonitoringURL}")
	String channelMonitoringURL;

	public ChannelMonitoringService(Client jerseyClient) {
		this.jerseyClient = jerseyClient;
	}

	public List<ChannelType> getChannels() throws JAXBException {
		List<ChannelType> channels = new ArrayList<ChannelType>();
		int maxAttemptsCount = 3;
		for (int i = 1; i <= maxAttemptsCount; i++) {
			try {

				WebTarget webTarget = jerseyClient.target(baseUrl).path(channelMonitoringURL).queryParam("party", "*")
						.queryParam("service", "*").queryParam("channel", "*").queryParam("action", "status")
						.queryParam("showProcessLog", "true").queryParam("showAdminHistory", "true")
						.queryParam("status", "error");

				Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_XML);
				Response response = invocationBuilder.get();
				LOG.info("Retrieved channels status:{}", response.getStatus());

				String resultsXML = response.readEntity(String.class);

				// Hack otherwise there is some exception, no time to fix
				resultsXML = resultsXML.replace(
						"<!DOCTYPE ChannelStatusResult SYSTEM \"/AdapterFramework/channelAdmin/ChannelAdmin.dtd\">",
						"");

				JAXBContext jaxbContext = JAXBContext.newInstance(ChannelStatusResult.class);
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

				StringReader reader = new StringReader(resultsXML);
				ChannelStatusResult results = (ChannelStatusResult) unmarshaller.unmarshal(reader);

				if (results.getChannels().getChannels() != null) {
					channels = results.getChannels().getChannels();
				}

				String nonSuccess = String.join(",",
						channels.stream().map(ChannelType::getChannelName).collect(Collectors.toList()));
				LOG.info("ProblematicChannels:{}", nonSuccess);
				break;
			} catch (Exception e) {
				LOG.error("Exception in getChannels", e);
				if (i == maxAttemptsCount) {
					throw e;
				} else {
					Utils.sleepForTenSeconds();
					LOG.info("Retrying getChannels");
				}
			}
		}

		return channels;
	}

}
