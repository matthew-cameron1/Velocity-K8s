package ca.innov8solutions.controller.comms;

import ca.innov8solutions.controller.NetworkService;
import ca.innov8solutions.controller.models.IslandRequest;
import ca.innov8solutions.controller.models.ServerObject;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

public class ChannelManager {

	private final JedisPool pool;
	private NetworkService service;

	private final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).setPrettyPrinting().create();

	public ChannelManager(NetworkService service, JedisPool pool) {
		this.service = service;
		this.pool = pool;
	}

	public void init() {
		Jedis jedis = pool.getResource();

		JedisPubSub jedisPubSub = new JedisPubSub() {
			@Override
			public void onMessage(String channel, String message) {
				if (channel.startsWith("sb-relay")) {
					String subChannel = channel.split(":")[1];

					if (subChannel.equalsIgnoreCase("addServer")) {
						ServerObject object = gson.fromJson(message, ServerObject.class);

						System.out.println("Getting object from Relay:");
						System.out.println(object.toString());

						String containerName = service.containerFromServerObject(object);

						service.addServer(containerName, object);
					} else if (subChannel.equalsIgnoreCase("slotUpdate")) {
						ServerObject object = gson.fromJson(message, ServerObject.class);

					}
				}
			}

			@Override
			public void onSubscribe(String channel, int subscribedChannels) {
				System.out.println("Subscribed to channel: " + channel);
			}

			@Override
			public void onUnsubscribe(String channel, int subscribedChannels) {
				System.out.println("Unsubscribed from channel: " + channel);
			}
		};
		new Thread(() -> {
			jedis.subscribe(jedisPubSub, "sb-relay:slotUpdate", "sb-relay:addServer", "sb-relay:loadIsland", "sb-relay:removeIsland", "sb-relay:shutdown");
		}, "jedis").start();
	}

	public void loadIsland(IslandRequest island) {
		Jedis jedis = pool.getResource();
		if (island.getObject() == null) {
			island.setObject(service.getServerObject(service.findLeastIslandDenseContainer()));
		}
		jedis.publish("sb-relay:loadIsland", gson.toJson(island));
	}
}
