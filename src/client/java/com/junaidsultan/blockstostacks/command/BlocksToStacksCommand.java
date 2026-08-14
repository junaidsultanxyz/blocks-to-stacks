package com.junaidsultan.blockstostacks.command;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.junaidsultan.blockstostacks.math.StackBreakdown;
import com.junaidsultan.blockstostacks.text.ResultRenderer;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

/**
 * Brigadier wiring for {@code /b2s}.
 *
 * <p>This is the only class that touches the Fabric command API, which is the
 * part most likely to move between Minecraft versions. The arithmetic lives in
 * {@link StackBreakdown} and the formatting in {@link ResultRenderer}.
 */
public final class BlocksToStacksCommand {
	/** Command literal. Kept here so the usage text and the registration cannot drift apart. */
	public static final String LITERAL = "b2s";

	private static final String BLOCKS_ARGUMENT = "blocks";
	private static final String STACK_SIZE_ARGUMENT = "stack-size";

	/** Offered as completions for the optional stack size, each with a hint of what stacks that way. */
	private record StackSizeHint(int size, String examples) {
	}

	private static final List<StackSizeHint> COMMON_STACK_SIZES = List.of(
			new StackSizeHint(64, "blocks, ingots, most items"),
			new StackSizeHint(16, "ender pearls, snowballs, signs"),
			new StackSizeHint(1, "tools, armour, potions"));

	private BlocksToStacksCommand() {
	}

	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(ClientCommands.literal(LITERAL)
				// Bare `/b2s` explains itself instead of erroring out.
				.executes(context -> {
					send(context, ResultRenderer.usage(LITERAL));
					return Command.SINGLE_SUCCESS;
				})
				.then(ClientCommands.argument(BLOCKS_ARGUMENT, IntegerArgumentType.integer(0))
						.executes(context -> calculate(context, StackBreakdown.DEFAULT_STACK_SIZE))
						.then(ClientCommands.argument(STACK_SIZE_ARGUMENT, IntegerArgumentType.integer(1))
								.suggests(BlocksToStacksCommand::suggestStackSizes)
								.executes(context -> calculate(context,
										IntegerArgumentType.getInteger(context, STACK_SIZE_ARGUMENT))))));
	}

	private static int calculate(CommandContext<FabricClientCommandSource> context, int stackSize) {
		int totalBlocks = IntegerArgumentType.getInteger(context, BLOCKS_ARGUMENT);
		send(context, ResultRenderer.result(StackBreakdown.of(totalBlocks, stackSize)));
		return Command.SINGLE_SUCCESS;
	}

	private static CompletableFuture<Suggestions> suggestStackSizes(
			CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) {
		String typed = builder.getRemaining();

		for (StackSizeHint hint : COMMON_STACK_SIZES) {
			if (Integer.toString(hint.size()).startsWith(typed)) {
				builder.suggest(hint.size(), Component.literal(hint.examples()).withStyle(ChatFormatting.GRAY));
			}
		}

		return builder.buildFuture();
	}

	private static void send(CommandContext<FabricClientCommandSource> context, Component[] lines) {
		for (Component line : lines) {
			context.getSource().sendFeedback(line);
		}
	}
}
