package com.junaidsultan.blockstostacks;

import com.junaidsultan.blockstostacks.command.BlocksToStacksCommand;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

/**
 * Client entrypoint.
 *
 * <p>Everything this mod does happens on the local client: the command is never
 * sent to the server and the results are printed straight into the player's own
 * chat, so servers do not need the mod installed.
 */
public class BlocksToStacksClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientCommandRegistrationCallback.EVENT.register(
				(dispatcher, buildContext) -> BlocksToStacksCommand.register(dispatcher));
	}
}
