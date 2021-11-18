package ca.innov8solutions.controller.models;

public class ServerObject {

	private String ip;
	private int port;

	private int maxSlots = 10;
	private int usedSlots = 0;


	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getMaxSlots() {
		return maxSlots;
	}

	public void setMaxSlots(int maxSlots) {
		this.maxSlots = maxSlots;
	}

	public int getUsedSlots() {
		return usedSlots;
	}

	public void setUsedSlots(int usedSlots) {
		this.usedSlots = usedSlots;
	}
}
