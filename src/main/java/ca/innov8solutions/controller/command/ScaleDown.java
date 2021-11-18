package ca.innov8solutions.controller.command;

import ca.innov8solutions.controller.Controller;
import ca.innov8solutions.controller.NetworkService;
import ca.innov8solutions.controller.models.ServerObject;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ScaleDown implements SimpleCommand {

	private final Controller plugin;

	public ScaleDown(Controller plugin) {
		this.plugin = plugin;
	}

	@Override
	public void execute(Invocation invocation) {
		if (!invocation.source().hasPermission("scale.down")) {
			return;
		}

		String containerName = this.plugin.getNetworkService().findLeastIslandDenseContainer();
		ServerObject object = this.plugin.getNetworkService().getServerObject(containerName);

		RegisteredServer velocityServer = plugin.getServer().getServer(containerName).orElse(null);
		if (velocityServer != null) {
			//TODO loop through all players: velocityServer.getConnectedPlayers().forEach(Player::fuckOff)
			//Send relay a shutdown notice as well
			plugin.getServer().unregisterServer(velocityServer.getServerInfo());

			try (KubernetesClient client = new DefaultKubernetesClient()) {
				Pod toDelete = client.pods().inNamespace("skyblock-server").withName(containerName).get();

				System.out.println("Container names equal? " + containerName.equals(toDelete.getMetadata().getName()));

				client.pods().inNamespace("skyblock-server").delete(toDelete);

				System.out.println("Deleting pod with name: " + toDelete.getMetadata().getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
