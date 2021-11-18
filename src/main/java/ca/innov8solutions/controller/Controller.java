package ca.innov8solutions.controller;

import ca.innov8solutions.controller.command.LoadIsland;
import ca.innov8solutions.controller.comms.ChannelManager;
import ca.innov8solutions.controller.models.ServerObject;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


import javax.inject.Inject;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Plugin(id="controller", name = "Controller", description = "Handles player routing and k8s integration",
authors = {"Matthew Cameron"})
public class Controller {

	// Service for K8s: https://pastebin.com/8fTZnR9Y
	// Server for K8s: https://pastebin.com/J09ti8Yv
	// SETUP REMEBER THE IP CHANGES: USE: addr ip

	private final ProxyServer server;
	private final Logger logger;
	private final ChannelManager channelManager;
	private final NetworkService networkService;
	private final JedisPool pool;

	@Inject
	public Controller(ProxyServer server, Logger logger) {
		this.server = server;
		this.logger = logger;
		this.networkService = new NetworkService(this);
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(100);

		this.pool = new JedisPool(config);
		this.channelManager = new ChannelManager(networkService, pool);
		this.channelManager.init();

		try (KubernetesClient client = new DefaultKubernetesClient()) {
			Pod pod = client.pods().load(getClass().getResourceAsStream("/k8s/skyblock-server.yml")).get();
			Pod returned = client.pods().inNamespace("skyblock-server").create(pod);

			System.out.println("Created pod with name: " + returned.getMetadata().getName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("Controller instantiated and executed pod creation");
	}

	@Subscribe
	public void proxyInit(ProxyInitializeEvent event) {
		server.getCommandManager().register(server.getCommandManager().metaBuilder("load").build(), new LoadIsland(this));
	}

	@Subscribe
	public void onJoin(ServerPreConnectEvent event) {
		String leastLoadedContainer = this.networkService.findLeastIslandDenseContainer();
		ServerObject leastLoaded = this.networkService.getServerObject(leastLoadedContainer);
		event.setResult(ServerPreConnectEvent.ServerResult.allowed(getVelocityServer(leastLoaded.getPort())));
		System.out.println("Routing to least loaded island server");

		//TODO jedis - send load island
	}

	public RegisteredServer getVelocityServer(int port) {
		for (RegisteredServer server : this.server.getAllServers()) {
			if (server.getServerInfo().getAddress().getPort() == port) {
				return server;
			}
		}
		return null;
	}

	public ProxyServer getServer() {
		return server;
	}

	public Logger getLogger() {
		return logger;
	}

	public ChannelManager getChannelManager() {
		return channelManager;
	}

	public NetworkService getNetworkService() {
		return networkService;
	}

	public JedisPool getJedisPool() {
		return pool;
	}
}
