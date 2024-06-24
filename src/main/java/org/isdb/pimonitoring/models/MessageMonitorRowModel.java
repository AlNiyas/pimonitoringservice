package org.isdb.pimonitoring.models;

import java.util.List;

import generated.RowType;

public class MessageMonitorRowModel {

	private String senderPartner;
	private String senderComponent;
	private String receiverPartner;
	private String receiverComponent;
	private String interfaceName;
	private String interfaceNamespace;
	private String scenarioIdentifier;
	private int systemError;
	private int waiting;
	private int toBeDelivered;
	private int delivering;
	private int holding;
	private int copied;
	// grouping all these 5 to scheduled
	private int scheduled;
	private int successful;
	private int terminatedWithErrors;// aka cancelled with errors

	private List<String> entries;

	public static MessageMonitorRowModel of(RowType rowType) {
		MessageMonitorRowModel messageMonitorRowModel = new MessageMonitorRowModel();
		messageMonitorRowModel.senderPartner = rowType.getEntries().get(0);
		messageMonitorRowModel.senderComponent = rowType.getEntries().get(1);
		messageMonitorRowModel.receiverPartner = rowType.getEntries().get(2);
		messageMonitorRowModel.receiverComponent = rowType.getEntries().get(3);
		messageMonitorRowModel.interfaceName = rowType.getEntries().get(4);
		messageMonitorRowModel.interfaceNamespace = rowType.getEntries().get(5);
		messageMonitorRowModel.scenarioIdentifier = rowType.getEntries().get(6);
		messageMonitorRowModel.systemError = rowType.getEntries().get(7).equalsIgnoreCase("-") ? 0
				: Integer.parseInt(rowType.getEntries().get(7));
		messageMonitorRowModel.waiting = rowType.getEntries().get(8).equalsIgnoreCase("-") ? 0
				: Integer.parseInt(rowType.getEntries().get(8));
		messageMonitorRowModel.toBeDelivered = rowType.getEntries().get(9).equalsIgnoreCase("-") ? 0
				: Integer.parseInt(rowType.getEntries().get(9));
		messageMonitorRowModel.delivering = rowType.getEntries().get(10).equalsIgnoreCase("-") ? 0
				: Integer.parseInt(rowType.getEntries().get(10));
		messageMonitorRowModel.holding = rowType.getEntries().get(11).equalsIgnoreCase("-") ? 0
				: Integer.parseInt(rowType.getEntries().get(11));
		messageMonitorRowModel.copied = rowType.getEntries().get(12).equalsIgnoreCase("-") ? 0
				: Integer.parseInt(rowType.getEntries().get(12));
		messageMonitorRowModel.scheduled = messageMonitorRowModel.waiting + messageMonitorRowModel.toBeDelivered
				+ messageMonitorRowModel.delivering + messageMonitorRowModel.holding + messageMonitorRowModel.copied;
		messageMonitorRowModel.successful = rowType.getEntries().get(13).equalsIgnoreCase("-") ? 0
				: Integer.parseInt(rowType.getEntries().get(13));
		messageMonitorRowModel.terminatedWithErrors = rowType.getEntries().get(14).equalsIgnoreCase("-") ? 0
				: Integer.parseInt(rowType.getEntries().get(14));
		messageMonitorRowModel.entries = rowType.getEntries();
		return messageMonitorRowModel;
	}

	public String toString() {
		return String.join(",", entries);
	}

	public String getLogString() {
		return interfaceName + "\t" + systemError + "\t" + scheduled + "\t" + terminatedWithErrors;
	}

	public String getUniqueKey() {
		return String.join("-", senderPartner, senderComponent, receiverPartner, receiverComponent, interfaceName,
				interfaceNamespace, scenarioIdentifier);
	}

	public boolean hasScheduledOnly() {
		return systemError == 0 && terminatedWithErrors == 0 && scheduled > 0;
	}

	// Constructor
	public MessageMonitorRowModel() {
		
	}

	// Getters and Setters
	public String getSenderPartner() {
		return senderPartner;
	}

	public void setSenderPartner(String senderPartner) {
		this.senderPartner = senderPartner;
	}

	public String getSenderComponent() {
		return senderComponent;
	}

	public void setSenderComponent(String senderComponent) {
		this.senderComponent = senderComponent;
	}

	public String getReceiverPartner() {
		return receiverPartner;
	}

	public void setReceiverPartner(String receiverPartner) {
		this.receiverPartner = receiverPartner;
	}

	public String getReceiverComponent() {
		return receiverComponent;
	}

	public void setReceiverComponent(String receiverComponent) {
		this.receiverComponent = receiverComponent;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getInterfaceNamespace() {
		return interfaceNamespace;
	}

	public void setInterfaceNamespace(String interfaceNamespace) {
		this.interfaceNamespace = interfaceNamespace;
	}

	public String getScenarioIdentifier() {
		return scenarioIdentifier;
	}

	public void setScenarioIdentifier(String scenarioIdentifier) {
		this.scenarioIdentifier = scenarioIdentifier;
	}

	public int getSystemError() {
		return systemError;
	}

	public void setSystemError(int systemError) {
		this.systemError = systemError;
	}

	public int getWaiting() {
		return waiting;
	}

	public void setWaiting(int waiting) {
		this.waiting = waiting;
	}

	public int getToBeDelivered() {
		return toBeDelivered;
	}

	public void setToBeDelivered(int toBeDelivered) {
		this.toBeDelivered = toBeDelivered;
	}

	public int getDelivering() {
		return delivering;
	}

	public void setDelivering(int delivering) {
		this.delivering = delivering;
	}

	public int getHolding() {
		return holding;
	}

	public void setHolding(int holding) {
		this.holding = holding;
	}

	public int getCopied() {
		return copied;
	}

	public void setCopied(int copied) {
		this.copied = copied;
	}

	public int getScheduled() {
		return scheduled;
	}

	public void setScheduled(int scheduled) {
		this.scheduled = scheduled;
	}

	public int getSuccessful() {
		return successful;
	}

	public void setSuccessful(int successful) {
		this.successful = successful;
	}

	public int getTerminatedWithErrors() {
		return terminatedWithErrors;
	}

	public void setTerminatedWithErrors(int terminatedWithErrors) {
		this.terminatedWithErrors = terminatedWithErrors;
	}

	public List<String> getEntries() {
		return entries;
	}

	public void setEntries(List<String> entries) {
		this.entries = entries;
	}

}
