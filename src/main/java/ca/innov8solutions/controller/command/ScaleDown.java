package ca.innov8solutions.controller.command;

import ca.innov8solutions.controller.NetworkService;
import ca.innov8solutions.controller.models.ServerObject;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ScaleDown implements SimpleCommand {

	private final NetworkService service;

	public ScaleDown(NetworkService service) {
		this.service = service;
	}

	@Override
	public void execute(Invocation invocation) {
		if (!invocation.source().hasPermission("scale.down")) {
			return;
		}

		ServerObject object = this.service.findLeastLoaded();
	}
}
