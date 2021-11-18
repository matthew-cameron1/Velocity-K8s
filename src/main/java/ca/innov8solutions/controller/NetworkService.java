package ca.innov8solutions.controller;

import ca.innov8solutions.controller.models.ServerObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.velocitypowered.api.proxy.server.ServerInfo;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NetworkService {

	/*
		Realistically this should handle creating pods and handling the call back from Relay once booted
		This is a test, a production application would be more complete
	 */

	private Map<String, Optional<ServerObject>> serverObjects = Maps.newHashMap();

	private final Controller plugin;

	public NetworkService(Controller plugin) {
		this.plugin = plugin;
	}

	public void addServer(String containerName, ServerObject object) {
		if (serverExists(object))
			return;

		this.serverObjects.put(containerName, Optional.of(object));

		ServerInfo info = new ServerInfo(containerName, new InetSocketAddress(object.getIp(), object.getPort()));
		plugin.getServer().registerServer(info);

		//We could also just store the server info, but for the sake of JSON objects, we will use our models.
	}

	public boolean serverExists(ServerObject object) {
		return serverExists(object.getPort());
	}

	public boolean serverExists(int port) {
		for (Optional<ServerObject> object : serverObjects.values()) {
			if (object.isPresent()) {
				if (object.get().getPort() == port) {
					return true;
				}
			}
		}
		return false;
	}

	public String findLeastIslandDenseContainer() {
		int slotsLoaded = Integer.MAX_VALUE;
		String containerName = null;
		for (Map.Entry<String, Optional<ServerObject>> entry : serverObjects.entrySet()) {

			ServerObject object = entry.getValue().orElse(null);
			if (object == null)
				continue;

			if (slotsLoaded > object.getUsedSlots()) {
				slotsLoaded = object.getUsedSlots();
				containerName = entry.getKey();
			}
		}
		return containerName;
	}

	public void update(ServerObject object) {
		for (Optional<ServerObject> object1 : serverObjects.values()) {
			if (object1.isPresent()) {
				ServerObject temp = object1.get();
				if (temp.getPort() == object.getPort()) {
					object1.get().setUsedSlots(object.getUsedSlots());
				}
			}
		}
	}

	public ServerObject getServerObject(String container) {
		return this.serverObjects.get(container).orElse(null);
	}

	/*
		I really don't like this but wanted to kind of finish all of this tonight and it is late and I am baked
	 */
	public String containerFromServerObject(ServerObject object) {
		for (Map.Entry<String, Optional<ServerObject>> entry : this.serverObjects.entrySet()) {
			if (entry.getValue().isPresent()) {
				ServerObject temp = entry.getValue().get();
				if (temp.getIp().equals(object.getIp())) {
					return entry.getKey();
				}
			}
		}
		return null;
	}
}
