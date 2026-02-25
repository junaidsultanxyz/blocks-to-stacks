package com.junaidsultan;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.ChatFormatting;

public class ClientApplication implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		// Register the command during client startup
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {

			dispatcher.register(ClientCommandManager.literal("calc")
					.then(ClientCommandManager.literal("stacks")

							// Required Argument: number of blocks (minimum 0)
							.then(ClientCommandManager.argument("number-of-blocks", IntegerArgumentType.integer(0))

									// Execution Path 1: User stops typing. Default stack size is 64.
									.executes(context -> executeCalculation(context, 64))

									// Optional Argument: custom stack size (minimum 1 to prevent division by zero)
									.then(ClientCommandManager.argument("stack-size", IntegerArgumentType.integer(1))

											// Execution Path 2: User provided a custom stack size.
											.executes(context -> {
												int customStackSize = IntegerArgumentType.getInteger(context, "stack-size");
												return executeCalculation(context, customStackSize);
											})
									)
							)
					)
			);
		});
	}

	/**
	 * Calculates the stacks and sends a formatted message to the local player.
	 */
	private int executeCalculation(CommandContext<FabricClientCommandSource> context, int stackSize) {
		// Retrieve the required argument
		int totalBlocks = IntegerArgumentType.getInteger(context, "number-of-blocks");

		// The core calculation
		int stacks = totalBlocks / stackSize;
		int remainder = totalBlocks % stackSize;

		// Build the modern text component using Mojang mappings
		MutableComponent message = Component.literal("Calculation: ")
				.withStyle(ChatFormatting.GRAY)
				.append(Component.literal(String.valueOf(totalBlocks)).withStyle(ChatFormatting.AQUA))
				.append(Component.literal(" blocks = ").withStyle(ChatFormatting.GRAY))
				.append(Component.literal(String.valueOf(stacks)).withStyle(ChatFormatting.GREEN))
				.append(Component.literal(" stacks and ").withStyle(ChatFormatting.GRAY))
				.append(Component.literal(String.valueOf(remainder)).withStyle(ChatFormatting.YELLOW))
				.append(Component.literal(" blocks (Stack size: " + stackSize + ")").withStyle(ChatFormatting.DARK_GRAY));

		// Send strictly to the local client chat
		context.getSource().sendFeedback(message);

		// Brigadier requires returning an integer (usually 1 for success, 0 for fail)
		return 1;
	}
}