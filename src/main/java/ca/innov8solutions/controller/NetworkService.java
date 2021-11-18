package ca.innov8solutions.controller;

import ca.innov8solutions.controller.models.ServerObject;
import com.google.common.collect.Lists;
import com.velocitypowered.api.proxy.server.ServerInfo;

import java.net.InetSocketAddress;
import java.util.List;

public class NetworkService {

	private List<ServerObject> serverData = Lists.newArrayList();
	private final Controller plugin;

	public NetworkService(Controller plugin) {
		this.plugin = plugin;
	}

	public void addServer(ServerObject object) {
		if (serverExists(object))
			return;

		this.serverData.add(object);

		ServerInfo info = new ServerInfo("skyblock" + serverData.size(), new InetSocketAddress(object.getIp(), object.getPort()));
		plugin.getServer().registerServer(info);
	}

	public boolean serverExists(ServerObject object) {
		return serverExists(object.getPort());
	}

	public boolean serverExists(int port) {
		for (ServerObject object : serverData) {
			if (object.getPort() == port) {
				return true;
			}
		}
		return false;
	}

	public ServerObject findLeastLoaded() {
		int slotsLoaded = Integer.MAX_VALUE;
		ServerObject server = null;
		for (ServerObject object : serverData) {

			if (slotsLoaded > object.getUsedSlots()) {
				slotsLoaded = object.getUsedSlots();
				server = object;
			}
		}
		return server;
	}

	public void update(ServerObject object) {
		for (ServerObject object1 : serverData) {
			if (object1.getPort() == object.getPort()) {
				object1.setUsedSlots(object.getUsedSlots());
			}
		}
	}
}
