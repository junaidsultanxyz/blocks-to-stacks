package com.junaidsultan.blockstostacks.text;

import java.util.Locale;

import com.junaidsultan.blockstostacks.math.StackBreakdown;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

/**
 * Builds the chat output.
 *
 * <p>Everything that talks to Minecraft's text API lives here, so a game update
 * that renames a chat class only ever touches this file.
 *
 * <p>Chat uses a variable width font, so the layout leans on a left rail and
 * middle dots instead of padded columns, which would render ragged.
 */
public final class ResultRenderer {
	private static final String RAIL = "│ ";
	private static final String SEPARATOR = "  ·  ";

	private static final ChatFormatting RAIL_COLOR = ChatFormatting.DARK_GRAY;
	private static final ChatFormatting LABEL_COLOR = ChatFormatting.GRAY;
	private static final ChatFormatting VALUE_COLOR = ChatFormatting.WHITE;
	private static final ChatFormatting STACK_COLOR = ChatFormatting.GREEN;
	private static final ChatFormatting REMAINDER_COLOR = ChatFormatting.YELLOW;
	private static final ChatFormatting NOTE_COLOR = ChatFormatting.DARK_GRAY;
	private static final ChatFormatting STORAGE_COLOR = ChatFormatting.DARK_AQUA;

	private ResultRenderer() {
	}

	/**
	 * Renders a finished calculation as chat lines, ready to be handed to
	 * {@code sendFeedback} one at a time.
	 */
	public static Component[] result(StackBreakdown breakdown) {
		String plain = plainResult(breakdown);

		MutableComponent input = line(count(breakdown.totalBlocks(), "block", "blocks", VALUE_COLOR))
				.append(Component.literal(SEPARATOR).withStyle(NOTE_COLOR))
				.append(Component.literal(number(breakdown.stackSize()) + " per stack").withStyle(LABEL_COLOR));

		MutableComponent result = line(resultBody(breakdown))
				.append(Component.literal("  [copy]").withStyle(NOTE_COLOR))
				.withStyle(style -> style
						.withClickEvent(new ClickEvent.CopyToClipboard(plain))
						.withHoverEvent(new HoverEvent.ShowText(Component.literal("Click to copy \"" + plain + "\"")
								.withStyle(LABEL_COLOR))));

		if (breakdown.slots() == 0) {
			return new Component[]{header(), input, result};
		}

		return new Component[]{header(), input, result, storage(breakdown)};
	}

	/** Renders the usage shown when the command is run with no arguments. */
	public static Component[] usage(String rootCommand) {
		return new Component[]{
				header(),
				usageLine(rootCommand + " <blocks>", "split a count into stacks of "
						+ StackBreakdown.DEFAULT_STACK_SIZE),
				usageLine(rootCommand + " <blocks> <stack-size>", "for items that stack to 16 or 1")
		};
	}

	/** The one-line summary the [copy] button puts on the clipboard. */
	public static String plainResult(StackBreakdown breakdown) {
		if (breakdown.isPartialOnly()) {
			return plural(breakdown.remainder(), "block", "blocks");
		}

		if (breakdown.isExact()) {
			return plural(breakdown.stacks(), "stack", "stacks");
		}

		return plural(breakdown.stacks(), "stack", "stacks")
				+ " + " + plural(breakdown.remainder(), "block", "blocks");
	}

	private static MutableComponent resultBody(StackBreakdown breakdown) {
		if (breakdown.isPartialOnly()) {
			MutableComponent body = count(breakdown.remainder(), "block", "blocks", REMAINDER_COLOR);
			return breakdown.totalBlocks() == 0
					? body
					: body.append(note("under one stack"));
		}

		MutableComponent body = count(breakdown.stacks(), "stack", "stacks", STACK_COLOR)
				.withStyle(ChatFormatting.BOLD);

		if (breakdown.isExact()) {
			return body.append(note("exact"));
		}

		return body.append(Component.literal(" + ").withStyle(LABEL_COLOR))
				.append(count(breakdown.remainder(), "block", "blocks", REMAINDER_COLOR));
	}

	/**
	 * How much storage the pile takes up. Container counts are only spelled out
	 * once they stop being "one of them", otherwise the line is mostly noise.
	 */
	private static MutableComponent storage(StackBreakdown breakdown) {
		MutableComponent body = line(count(breakdown.slots(), "slot", "slots", STORAGE_COLOR))
				.append(Component.literal(SEPARATOR).withStyle(NOTE_COLOR));

		if (breakdown.slots() <= StackBreakdown.SHULKER_BOX_SLOTS) {
			return body.append(Component.literal("fits in one shulker box").withStyle(LABEL_COLOR));
		}

		if (breakdown.slots() <= StackBreakdown.DOUBLE_CHEST_SLOTS) {
			return body.append(Component.literal("fits in one double chest").withStyle(LABEL_COLOR));
		}

		return body.append(count(breakdown.containers(StackBreakdown.SHULKER_BOX_SLOTS),
						"shulker box", "shulker boxes", STORAGE_COLOR))
				.append(Component.literal(SEPARATOR).withStyle(NOTE_COLOR))
				.append(count(breakdown.containers(StackBreakdown.DOUBLE_CHEST_SLOTS),
						"double chest", "double chests", STORAGE_COLOR));
	}

	private static MutableComponent header() {
		return line(Component.literal("Blocks to Stacks")
				.withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));
	}

	private static MutableComponent usageLine(String command, String description) {
		return line(Component.literal("/" + command).withStyle(ChatFormatting.AQUA))
				.append(Component.literal("  —  " + description).withStyle(LABEL_COLOR))
				.withStyle(style -> style
						.withClickEvent(new ClickEvent.SuggestCommand("/" + command))
						.withHoverEvent(new HoverEvent.ShowText(
								Component.literal("Click to put this in the chat box").withStyle(LABEL_COLOR))));
	}

	private static MutableComponent line(Component body) {
		return Component.literal(RAIL).withStyle(RAIL_COLOR).append(body);
	}

	private static MutableComponent count(int value, String one, String many, ChatFormatting color) {
		return Component.literal(number(value)).withStyle(color)
				.append(Component.literal(" " + (value == 1 ? one : many)).withStyle(LABEL_COLOR));
	}

	private static MutableComponent note(String text) {
		return Component.literal("  (" + text + ")").withStyle(NOTE_COLOR);
	}

	private static String plural(int value, String one, String many) {
		return number(value) + " " + (value == 1 ? one : many);
	}

	private static String number(int value) {
		return String.format(Locale.ROOT, "%,d", value);
	}
}
