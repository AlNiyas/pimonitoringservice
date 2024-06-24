package org.isdb.pimonitoring.jobs;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.isdb.pimonitoring.models.MessageMonitorRowModel;
import org.isdb.pimonitoring.services.ChannelMonitoringService;
import org.isdb.pimonitoring.services.EmailService;
import org.isdb.pimonitoring.services.MessageMonitoringService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import generated.ActivationStateType;
import generated.AutomationStateType;
import generated.ChannelStateType;
import generated.ChannelType;
import generated.DirectionType;

@RunWith(MockitoJUnitRunner.class)
public class PeriodicMonitoringTaskTest {

	@Mock
	protected MessageMonitoringService mockMessageMonitoringService;
	@Mock
	protected ChannelMonitoringService mockChannelMonitoringService;
	@Mock
	protected EmailService mockEmailService;

	@InjectMocks
	PeriodicMonitoringTask periodicMonitoringTask;

	@Before
	public void beforeTestMethod() throws Exception {
		periodicMonitoringTask.set_ForTesting_SleepTimeInSeconds(0);
		doNothing().when(mockEmailService).sendHtmlMessage(Mockito.anyString(), Mockito.any());
	}

	@Test
	public void channelsTest1() throws Exception {
		// all runs nothing . Expected = nothing

		when(mockChannelMonitoringService.getChannels()).thenReturn(new ArrayList<ChannelType>())
				.thenReturn(new ArrayList<ChannelType>()).thenReturn(new ArrayList<ChannelType>())
				.thenReturn(new ArrayList<ChannelType>()).thenReturn(new ArrayList<ChannelType>())
				.thenReturn(new ArrayList<ChannelType>()).thenReturn(new ArrayList<ChannelType>());
		periodicMonitoringTask.execute();
		ArgumentCaptor<Map> emailVariables = ArgumentCaptor.forClass(Map.class);
		verify(mockEmailService).sendHtmlMessage(Mockito.anyString(), emailVariables.capture());
		assertTrue(((List<ChannelType>) emailVariables.getValue().get("channels")).size() == 0);

	}

	@Test
	public void channelsTest2() throws Exception {
		// first run nothing, consecutive returns 1. Expected = nothing
		when(mockChannelMonitoringService.getChannels()).thenReturn(new ArrayList<ChannelType>())
				.thenReturn(Arrays.asList(getChannelType(1))).thenReturn(Arrays.asList(getChannelType(1)))
				.thenReturn(Arrays.asList(getChannelType(1))).thenReturn(Arrays.asList(getChannelType(1)))
				.thenReturn(Arrays.asList(getChannelType(1))).thenReturn(Arrays.asList(getChannelType(1)));
		periodicMonitoringTask.execute();
		ArgumentCaptor<Map> emailVariables = ArgumentCaptor.forClass(Map.class);
		verify(mockEmailService).sendHtmlMessage(Mockito.anyString(), emailVariables.capture());
		assertTrue(((List<ChannelType>) emailVariables.getValue().get("channels")).size() == 0);

	}

	@Test
	public void channelsTest3() throws Exception {
		// all runs 1 2 . Expected = 1 2
		when(mockChannelMonitoringService.getChannels()).thenReturn(Arrays.asList(getChannelType(1), getChannelType(2)))
				.thenReturn(Arrays.asList(getChannelType(1), getChannelType(2)))
				.thenReturn(Arrays.asList(getChannelType(1), getChannelType(2)))
				.thenReturn(Arrays.asList(getChannelType(1), getChannelType(2)))
				.thenReturn(Arrays.asList(getChannelType(1), getChannelType(2)))
				.thenReturn(Arrays.asList(getChannelType(1), getChannelType(2)))
				.thenReturn(Arrays.asList(getChannelType(1), getChannelType(2)));
		periodicMonitoringTask.execute();
		ArgumentCaptor<Map> emailVariables = ArgumentCaptor.forClass(Map.class);
		verify(mockEmailService).sendHtmlMessage(Mockito.anyString(), emailVariables.capture());
		List<ChannelType> channelListInEmail = (List<ChannelType>) emailVariables.getValue().get("channels");
		assertTrue(channelListInEmail.size() == 2);
		assertTrue(channelListInEmail.get(0).getChannelName().equalsIgnoreCase(getChannelType(1).getChannelName()));
		assertTrue(channelListInEmail.get(1).getChannelName().equalsIgnoreCase(getChannelType(2).getChannelName()));

	}

	@Test
	public void channelsTest4() throws Exception {
		// first run 1 2 3 , consecutive 2 3 . Expected = 2 3
		when(mockChannelMonitoringService.getChannels())
				.thenReturn(Arrays.asList(getChannelType(1), getChannelType(2), getChannelType(3)))
				.thenReturn(Arrays.asList(getChannelType(2), getChannelType(3)))
				.thenReturn(Arrays.asList(getChannelType(2), getChannelType(3)))
				.thenReturn(Arrays.asList(getChannelType(2), getChannelType(3)))
				.thenReturn(Arrays.asList(getChannelType(2), getChannelType(3)))
				.thenReturn(Arrays.asList(getChannelType(2), getChannelType(3)))
				.thenReturn(Arrays.asList(getChannelType(2), getChannelType(3)));
		periodicMonitoringTask.execute();
		ArgumentCaptor<Map> emailVariables = ArgumentCaptor.forClass(Map.class);
		verify(mockEmailService).sendHtmlMessage(Mockito.anyString(), emailVariables.capture());
		List<ChannelType> channelListInEmail = (List<ChannelType>) emailVariables.getValue().get("channels");
		assertTrue(channelListInEmail.size() == 2);
		assertTrue(channelListInEmail.get(0).getChannelName().equalsIgnoreCase(getChannelType(2).getChannelName()));
		assertTrue(channelListInEmail.get(1).getChannelName().equalsIgnoreCase(getChannelType(3).getChannelName()));

	}

	@Test
	public void channelsTest5() throws Exception {
		// first run 1 2 3 , second run 1 2 4, third run 1 consecutive 1 2 3 4. Expected
		// = 1
		when(mockChannelMonitoringService.getChannels())
				.thenReturn(Arrays.asList(getChannelType(1), getChannelType(2), getChannelType(3)))
				.thenReturn(Arrays.asList(getChannelType(1), getChannelType(2), getChannelType(4)))
				.thenReturn(Arrays.asList(getChannelType(1)))
				.thenReturn(Arrays.asList(getChannelType(1), getChannelType(2), getChannelType(3), getChannelType(4)))
				.thenReturn(Arrays.asList(getChannelType(1), getChannelType(2), getChannelType(3), getChannelType(4)))
				.thenReturn(Arrays.asList(getChannelType(1), getChannelType(2), getChannelType(3), getChannelType(4)))
				.thenReturn(Arrays.asList(getChannelType(1), getChannelType(2), getChannelType(3), getChannelType(4)));
		periodicMonitoringTask.execute();
		ArgumentCaptor<Map> emailVariables = ArgumentCaptor.forClass(Map.class);
		verify(mockEmailService).sendHtmlMessage(Mockito.anyString(), emailVariables.capture());
		List<ChannelType> channelListInEmail = (List<ChannelType>) emailVariables.getValue().get("channels");
		assertTrue(channelListInEmail.size() == 1);
		assertTrue(channelListInEmail.get(0).getChannelName().equalsIgnoreCase(getChannelType(1).getChannelName()));

	}

	private ChannelType getChannelType(int i) {
		ChannelType channelType = new ChannelType();
		channelType.setActivationState(ActivationStateType.STARTED);
		channelType.setAdapterType("FTP" + i);
		channelType.setChannelID("id" + i);
		channelType.setChannelName("name" + i);
		channelType.setChannelState(ChannelStateType.ERROR);
		channelType.setControl(AutomationStateType.MANUAL);
		channelType.setDirection(DirectionType.INBOUND);
		if (i % 2 == 0) {
			// Just to create some randomness
			channelType.setParty("party" + i);
			channelType.setService("service" + i);
		}

		return channelType;
	}

	// messages

	@Test
	public void messagesTest1() throws Exception {
		// all runs nothing . Expected = nothing
		when(mockMessageMonitoringService.getMessageMonitorRowModels(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(new ArrayList<MessageMonitorRowModel>()).thenReturn(new ArrayList<MessageMonitorRowModel>())
				.thenReturn(new ArrayList<MessageMonitorRowModel>()).thenReturn(new ArrayList<MessageMonitorRowModel>())
				.thenReturn(new ArrayList<MessageMonitorRowModel>()).thenReturn(new ArrayList<MessageMonitorRowModel>())
				.thenReturn(new ArrayList<MessageMonitorRowModel>());
		periodicMonitoringTask.execute();
		ArgumentCaptor<Map> emailVariables = ArgumentCaptor.forClass(Map.class);
		verify(mockEmailService).sendHtmlMessage(Mockito.anyString(), emailVariables.capture());
		List<MessageMonitorRowModel> messageListInEmail = (List<MessageMonitorRowModel>) emailVariables.getValue()
				.get("messageModels");
		assertTrue(messageListInEmail.size() == 0);
	}

	@Test
	public void messagesTest2() throws Exception {
		// first run nothing, consecutive runs 1=error/scheduled/terminated , 2=error,
		// 3=scheduled. Expected = 1 2
		when(mockMessageMonitoringService.getMessageMonitorRowModels(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(new ArrayList<MessageMonitorRowModel>())
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 1, 1, 1), getMessageMonitorRowModel(2, 1, 0, 0),
						getMessageMonitorRowModel(3, 0, 1, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 1, 1, 1), getMessageMonitorRowModel(2, 1, 0, 0),
						getMessageMonitorRowModel(3, 0, 1, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 1, 1, 1), getMessageMonitorRowModel(2, 1, 0, 0),
						getMessageMonitorRowModel(3, 0, 1, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 1, 1, 1), getMessageMonitorRowModel(2, 1, 0, 0),
						getMessageMonitorRowModel(3, 0, 1, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 1, 1, 1), getMessageMonitorRowModel(2, 1, 0, 0),
						getMessageMonitorRowModel(3, 0, 1, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 1, 1, 1), getMessageMonitorRowModel(2, 1, 0, 0),
						getMessageMonitorRowModel(3, 0, 1, 0)));
		periodicMonitoringTask.execute();
		ArgumentCaptor<Map> emailVariables = ArgumentCaptor.forClass(Map.class);
		verify(mockEmailService).sendHtmlMessage(Mockito.anyString(), emailVariables.capture());
		List<MessageMonitorRowModel> messageListInEmail = (List<MessageMonitorRowModel>) emailVariables.getValue()
				.get("messageModels");
		assertTrue(messageListInEmail.size() == 2);
		assertTrue(messageListInEmail.get(0).getInterfaceName()
				.equalsIgnoreCase(getMessageMonitorRowModel(1, 1, 0, 0).getInterfaceName()));
		assertTrue(messageListInEmail.get(1).getInterfaceName()
				.equalsIgnoreCase(getMessageMonitorRowModel(2, 1, 0, 0).getInterfaceName()));
	}

	@Test
	public void messagesTest3() throws Exception {
		// all runs 1=error , 2=error. Expected = 1 2
		when(mockMessageMonitoringService.getMessageMonitorRowModels(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 1, 0, 0), getMessageMonitorRowModel(2, 1, 0, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 1, 0, 0), getMessageMonitorRowModel(2, 1, 0, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 1, 0, 0), getMessageMonitorRowModel(2, 1, 0, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 1, 0, 0), getMessageMonitorRowModel(2, 1, 0, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 1, 0, 0), getMessageMonitorRowModel(2, 1, 0, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 1, 0, 0), getMessageMonitorRowModel(2, 1, 0, 0)))
				.thenReturn(
						Arrays.asList(getMessageMonitorRowModel(1, 1, 0, 0), getMessageMonitorRowModel(2, 1, 0, 0)));
		periodicMonitoringTask.execute();
		ArgumentCaptor<Map> emailVariables = ArgumentCaptor.forClass(Map.class);
		verify(mockEmailService).sendHtmlMessage(Mockito.anyString(), emailVariables.capture());
		List<MessageMonitorRowModel> messageListInEmail = (List<MessageMonitorRowModel>) emailVariables.getValue()
				.get("messageModels");
		assertTrue(messageListInEmail.size() == 2);

		assertTrue(messageListInEmail.get(0).getInterfaceName()
				.equalsIgnoreCase(getMessageMonitorRowModel(1, 1, 0, 0).getInterfaceName()));
		assertTrue(messageListInEmail.get(1).getInterfaceName()
				.equalsIgnoreCase(getMessageMonitorRowModel(2, 1, 0, 0).getInterfaceName()));
	}

	@Test
	public void messagesTest4() throws Exception {
		// first run nothing, consecutive runs 1=scheduled. Expected = nothing
		when(mockMessageMonitoringService.getMessageMonitorRowModels(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(new ArrayList<MessageMonitorRowModel>())
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 0, 1, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 0, 1, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 0, 1, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 0, 1, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 0, 1, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 0, 1, 0)));
		periodicMonitoringTask.execute();
		ArgumentCaptor<Map> emailVariables = ArgumentCaptor.forClass(Map.class);
		verify(mockEmailService).sendHtmlMessage(Mockito.anyString(), emailVariables.capture());
		List<MessageMonitorRowModel> messageListInEmail = (List<MessageMonitorRowModel>) emailVariables.getValue()
				.get("messageModels");
		assertTrue(messageListInEmail.size() == 0);
	}

	@Test
	public void messagesTest5() throws Exception {
		// all runs 1=error 2=scheduled. last run 1=error, 3=terminated. Expected = 1 3
		when(mockMessageMonitoringService.getMessageMonitorRowModels(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 1, 0, 0), getMessageMonitorRowModel(2, 0, 1, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 1, 0, 0), getMessageMonitorRowModel(2, 0, 1, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 1, 0, 0), getMessageMonitorRowModel(2, 0, 1, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 1, 0, 0), getMessageMonitorRowModel(2, 0, 1, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 1, 0, 0), getMessageMonitorRowModel(2, 0, 1, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 1, 0, 0), getMessageMonitorRowModel(2, 0, 1, 0)))
				.thenReturn(
						Arrays.asList(getMessageMonitorRowModel(1, 1, 0, 0), getMessageMonitorRowModel(3, 0, 0, 1)));
		periodicMonitoringTask.execute();
		ArgumentCaptor<Map> emailVariables = ArgumentCaptor.forClass(Map.class);
		verify(mockEmailService).sendHtmlMessage(Mockito.anyString(), emailVariables.capture());
		List<MessageMonitorRowModel> messageListInEmail = (List<MessageMonitorRowModel>) emailVariables.getValue()
				.get("messageModels");
		assertTrue(messageListInEmail.size() == 2);
		assertTrue(messageListInEmail.get(0).getInterfaceName()
				.equalsIgnoreCase(getMessageMonitorRowModel(1, 1, 0, 0).getInterfaceName()));
		assertTrue(messageListInEmail.get(1).getInterfaceName()
				.equalsIgnoreCase(getMessageMonitorRowModel(3, 0, 0, 1).getInterfaceName()));
	}

	@Test
	public void messagesTest6() throws Exception {
		// first run 1=scheduled 2=scheduled 3=scheduled, successive 1=error 2=scheduled
		// 3=scheduled, last 1=error/scheduled 2=scheduled 3=scheduled . Expected = 1 2
		// 3

		when(mockMessageMonitoringService.getMessageMonitorRowModels(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 0, 1, 0), getMessageMonitorRowModel(2, 0, 1, 0),
						getMessageMonitorRowModel(3, 0, 1, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 1, 0, 0), getMessageMonitorRowModel(2, 0, 1, 0),
						getMessageMonitorRowModel(3, 0, 1, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 1, 0, 0), getMessageMonitorRowModel(2, 0, 1, 0),
						getMessageMonitorRowModel(3, 0, 1, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 1, 0, 0), getMessageMonitorRowModel(2, 0, 1, 0),
						getMessageMonitorRowModel(3, 0, 1, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 1, 0, 0), getMessageMonitorRowModel(2, 0, 1, 0),
						getMessageMonitorRowModel(3, 0, 1, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 1, 0, 0), getMessageMonitorRowModel(2, 0, 1, 0),
						getMessageMonitorRowModel(3, 0, 1, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 1, 1, 0), getMessageMonitorRowModel(2, 0, 1, 0),
						getMessageMonitorRowModel(3, 0, 1, 0)));
		periodicMonitoringTask.execute();
		ArgumentCaptor<Map> emailVariables = ArgumentCaptor.forClass(Map.class);
		verify(mockEmailService).sendHtmlMessage(Mockito.anyString(), emailVariables.capture());
		List<MessageMonitorRowModel> messageListInEmail = (List<MessageMonitorRowModel>) emailVariables.getValue()
				.get("messageModels");
		assertTrue(messageListInEmail.size() == 3);
		assertTrue(messageListInEmail.get(0).getInterfaceName()
				.equalsIgnoreCase(getMessageMonitorRowModel(1, 1, 0, 0).getInterfaceName()));
		assertTrue(messageListInEmail.get(1).getInterfaceName()
				.equalsIgnoreCase(getMessageMonitorRowModel(2, 0, 1, 0).getInterfaceName()));
		assertTrue(messageListInEmail.get(2).getInterfaceName()
				.equalsIgnoreCase(getMessageMonitorRowModel(3, 0, 1, 0).getInterfaceName()));
	}

	@Test
	public void messagesTest7() throws Exception {
		// first run 1=scheduled 2=scheduled 3=scheduled, successive 2=scheduled
		// 3=scheduled 4=terminated. Expected = 2 3 4

		when(mockMessageMonitoringService.getMessageMonitorRowModels(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 0, 1, 0), getMessageMonitorRowModel(2, 0, 1, 0),
						getMessageMonitorRowModel(3, 0, 1, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(2, 0, 1, 0), getMessageMonitorRowModel(3, 0, 1, 0),
						getMessageMonitorRowModel(4, 0, 0, 1)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(2, 0, 1, 0), getMessageMonitorRowModel(3, 0, 1, 0),
						getMessageMonitorRowModel(4, 0, 0, 1)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(2, 0, 1, 0), getMessageMonitorRowModel(3, 0, 1, 0),
						getMessageMonitorRowModel(4, 0, 0, 1)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(2, 0, 1, 0), getMessageMonitorRowModel(3, 0, 1, 0),
						getMessageMonitorRowModel(4, 0, 0, 1)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(2, 0, 1, 0), getMessageMonitorRowModel(3, 0, 1, 0),
						getMessageMonitorRowModel(4, 0, 0, 1)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(2, 0, 1, 0), getMessageMonitorRowModel(3, 0, 1, 0),
						getMessageMonitorRowModel(4, 0, 0, 1)));
		periodicMonitoringTask.execute();
		ArgumentCaptor<Map> emailVariables = ArgumentCaptor.forClass(Map.class);
		verify(mockEmailService).sendHtmlMessage(Mockito.anyString(), emailVariables.capture());
		List<MessageMonitorRowModel> messageListInEmail = (List<MessageMonitorRowModel>) emailVariables.getValue()
				.get("messageModels");
		assertTrue(messageListInEmail.size() == 3);
		assertTrue(messageListInEmail.get(0).getInterfaceName()
				.equalsIgnoreCase(getMessageMonitorRowModel(2, 0, 1, 0).getInterfaceName()));
		assertTrue(messageListInEmail.get(1).getInterfaceName()
				.equalsIgnoreCase(getMessageMonitorRowModel(3, 0, 1, 0).getInterfaceName()));
		assertTrue(messageListInEmail.get(2).getInterfaceName()
				.equalsIgnoreCase(getMessageMonitorRowModel(4, 0, 0, 1).getInterfaceName()));
	}

	@Test
	public void messagesTest8() throws Exception {
		// first run 1=scheduled, successive run 2=error, last run 1=terminated.
		// Expected = 1

		when(mockMessageMonitoringService.getMessageMonitorRowModels(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 0, 1, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(2, 1, 0, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(2, 1, 0, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(2, 1, 0, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(2, 1, 0, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(2, 1, 0, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 0, 0, 1)));
		periodicMonitoringTask.execute();
		ArgumentCaptor<Map> emailVariables = ArgumentCaptor.forClass(Map.class);
		verify(mockEmailService).sendHtmlMessage(Mockito.anyString(), emailVariables.capture());
		List<MessageMonitorRowModel> messageListInEmail = (List<MessageMonitorRowModel>) emailVariables.getValue()
				.get("messageModels");
		assertTrue(messageListInEmail.size() == 1);
		assertTrue(messageListInEmail.get(0).getInterfaceName()
				.equalsIgnoreCase(getMessageMonitorRowModel(1, 0, 0, 1).getInterfaceName()));
	}

	@Test
	public void messagesTest9() throws Exception {
		// all runs 1=scheduled 2=error, last run 1=scheduled 2=error 3=scheduled
		// 4=terminated. Expected = 1 2 4

		when(mockMessageMonitoringService.getMessageMonitorRowModels(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 0, 1, 0), getMessageMonitorRowModel(2, 1, 0, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 0, 1, 0), getMessageMonitorRowModel(2, 1, 0, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 0, 1, 0), getMessageMonitorRowModel(2, 1, 0, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 0, 1, 0), getMessageMonitorRowModel(2, 1, 0, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 0, 1, 0), getMessageMonitorRowModel(2, 1, 0, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 0, 1, 0), getMessageMonitorRowModel(2, 1, 0, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 0, 1, 0), getMessageMonitorRowModel(2, 1, 0, 0),
						getMessageMonitorRowModel(3, 0, 1, 0), getMessageMonitorRowModel(4, 0, 0, 1)));
		periodicMonitoringTask.execute();
		ArgumentCaptor<Map> emailVariables = ArgumentCaptor.forClass(Map.class);
		verify(mockEmailService).sendHtmlMessage(Mockito.anyString(), emailVariables.capture());
		List<MessageMonitorRowModel> messageListInEmail = (List<MessageMonitorRowModel>) emailVariables.getValue()
				.get("messageModels");
		assertTrue(messageListInEmail.size() == 3);
		assertTrue(messageListInEmail.get(0).getInterfaceName()
				.equalsIgnoreCase(getMessageMonitorRowModel(1, 0, 1, 0).getInterfaceName()));
		assertTrue(messageListInEmail.get(1).getInterfaceName()
				.equalsIgnoreCase(getMessageMonitorRowModel(2, 1, 0, 0).getInterfaceName()));
		assertTrue(messageListInEmail.get(2).getInterfaceName()
				.equalsIgnoreCase(getMessageMonitorRowModel(4, 0, 0, 1).getInterfaceName()));
	}

	@Test
	public void messagesTest10() throws Exception {
		// first run 1=scheduled 2=scheduled 3=scheduled , second run 1=scheduled
		// 2=scheduled 4=scheduled, third run 1=scheduled 7=error, consecutive
		// 1=scheduled
		// 2=scheduled 3=scheduled 4=scheduled 5=error 6=terminated. Expected
		// = 1 5 6

		when(mockMessageMonitoringService.getMessageMonitorRowModels(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 0, 1, 0), getMessageMonitorRowModel(2, 0, 1, 0),
						getMessageMonitorRowModel(3, 0, 1, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 0, 1, 0), getMessageMonitorRowModel(2, 0, 1, 0),
						getMessageMonitorRowModel(4, 0, 1, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 0, 1, 0), getMessageMonitorRowModel(7, 1, 0, 0)))
				.thenReturn(Arrays.asList(getMessageMonitorRowModel(1, 0, 1, 0), getMessageMonitorRowModel(2, 0, 1, 0),
						getMessageMonitorRowModel(3, 0, 1, 0), getMessageMonitorRowModel(4, 0, 1, 0),
						getMessageMonitorRowModel(5, 1, 0, 0), getMessageMonitorRowModel(6, 0, 0, 1)));
		periodicMonitoringTask.execute();
		ArgumentCaptor<Map> emailVariables = ArgumentCaptor.forClass(Map.class);
		verify(mockEmailService).sendHtmlMessage(Mockito.anyString(), emailVariables.capture());
		List<MessageMonitorRowModel> messageListInEmail = (List<MessageMonitorRowModel>) emailVariables.getValue()
				.get("messageModels");
		assertTrue(messageListInEmail.size() == 3);
		assertTrue(messageListInEmail.get(0).getInterfaceName()
				.equalsIgnoreCase(getMessageMonitorRowModel(1, 0, 1, 0).getInterfaceName()));
		assertTrue(messageListInEmail.get(1).getInterfaceName()
				.equalsIgnoreCase(getMessageMonitorRowModel(5, 1, 0, 0).getInterfaceName()));
		assertTrue(messageListInEmail.get(2).getInterfaceName()
				.equalsIgnoreCase(getMessageMonitorRowModel(6, 0, 0, 1).getInterfaceName()));
	}

	private MessageMonitorRowModel getMessageMonitorRowModel(int i, int errorCount, int scheduledCount,
			int terminatedCount) {
		MessageMonitorRowModel messageMonitorRowModel = new MessageMonitorRowModel();
		messageMonitorRowModel.setInterfaceName("interface" + i);
		messageMonitorRowModel.setInterfaceNamespace("interfaceNamespace" + i);
		messageMonitorRowModel.setReceiverComponent("receiverComponent" + i);
		messageMonitorRowModel.setScenarioIdentifier("id" + 1);
		messageMonitorRowModel.setSenderComponent("senderComponent" + i);
		if (i % 2 == 0) {
			// Just to create some randomness
			messageMonitorRowModel.setReceiverPartner("receiverPartner" + i);
			messageMonitorRowModel.setSenderPartner("senderPartner" + i);
		}

		messageMonitorRowModel.setSystemError(errorCount);
		messageMonitorRowModel.setScheduled(scheduledCount);
		messageMonitorRowModel.setTerminatedWithErrors(terminatedCount);

		return messageMonitorRowModel;
	}
}