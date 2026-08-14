package com.junaidsultan.blockstostacks.math;

/**
 * The whole point of the mod, expressed without a single Minecraft import.
 *
 * <p>Keeping the arithmetic here means a Minecraft update can never break it, and
 * the only files that have to be reviewed when porting are the ones that actually
 * touch the game API.
 *
 * @param totalBlocks how many blocks were asked about
 * @param stackSize   how many blocks fit in one stack
 * @param stacks      complete stacks the total splits into
 * @param remainder   blocks left over after filling those stacks
 */
public record StackBreakdown(int totalBlocks, int stackSize, int stacks, int remainder) {
	/** Vanilla stack size for blocks, and the default when the player omits one. */
	public static final int DEFAULT_STACK_SIZE = 64;

	/** Storage slots in a shulker box, single chest or barrel. */
	public static final int SHULKER_BOX_SLOTS = 27;

	/** Storage slots in a double chest. */
	public static final int DOUBLE_CHEST_SLOTS = 54;

	public static StackBreakdown of(int totalBlocks, int stackSize) {
		if (totalBlocks < 0) {
			throw new IllegalArgumentException("totalBlocks must not be negative, got " + totalBlocks);
		}

		if (stackSize < 1) {
			throw new IllegalArgumentException("stackSize must be at least 1, got " + stackSize);
		}

		return new StackBreakdown(totalBlocks, stackSize, totalBlocks / stackSize, totalBlocks % stackSize);
	}

	/**
	 * Inventory slots the pile occupies: one per full stack, plus one more if
	 * there is a partial stack left over.
	 */
	public int slots() {
		return stacks + (remainder > 0 ? 1 : 0);
	}

	/** How many containers of the given capacity are needed to hold {@link #slots()}. */
	public int containers(int slotsPerContainer) {
		if (slotsPerContainer < 1) {
			throw new IllegalArgumentException("slotsPerContainer must be at least 1, got " + slotsPerContainer);
		}

		return Math.ceilDiv(slots(), slotsPerContainer);
	}

	/** True when the total divides evenly into stacks with nothing left over. */
	public boolean isExact() {
		return totalBlocks > 0 && remainder == 0;
	}

	/** True when there is not even one full stack. */
	public boolean isPartialOnly() {
		return stacks == 0;
	}
}
