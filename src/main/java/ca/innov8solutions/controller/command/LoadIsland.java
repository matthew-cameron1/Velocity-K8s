package ca.innov8solutions.controller.command;

import ca.innov8solutions.controller.Controller;
import ca.innov8solutions.controller.NetworkService;
import ca.innov8solutions.controller.comms.ChannelManager;
import ca.innov8solutions.controller.models.IslandRequest;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.inject.Inject;

public class LoadIsland implements SimpleCommand {

	private final Controller plugin;

	public LoadIsland(Controller plugin) {
		this.plugin = plugin;
	}

	@Override
	public void execute(Invocation invocation) {
		CommandSource commandSource = invocation.source();
		String[] args = invocation.arguments();

		if (args.length == 1) {
			String name = args[0];
			IslandRequest request = new IslandRequest(name, plugin.getNetworkService().findLeastLoaded());
			plugin.getChannelManager().loadIsland(request);
		} else {
			commandSource.sendMessage(Component.text("/load [username] - loads users island onto least loaded node"));
		}
	}
}
