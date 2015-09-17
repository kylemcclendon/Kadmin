package net.kylemc.kadmin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class Bonemeal implements Listener {
	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			final ItemStack item = event.getItem();
			Block block = event.getClickedBlock();

			if (block == null || !isBonemeal(item)) {
				return;
			}

			switch (block.getType()) {
			case CARROT:
			case CROPS:
			case MELON_STEM:
			case NETHER_WARTS:
			case POTATO:
			case PUMPKIN_STEM:
				block.setData((byte) 7);
				break;

			case SAPLING:
				Location l = block.getLocation();
				boolean bigtree = false;
				TreeType type = null;
				final byte data = block.getData();

				switch (data) {
				case 0: {
					type = TreeType.TREE;
					if ((int) (Math.random() * 20.0D) == 10) {
						type = TreeType.BIG_TREE;
					}
					break;
				}
				case 1: {
					type = TreeType.REDWOOD;
					if ((int) (Math.random() * 20.0D) == 10) {
						type = TreeType.TALL_REDWOOD;
					}
					final Block bl = isBigTree(block, (byte) 1);
					if (bl != null) {
						type = TreeType.MEGA_REDWOOD;
						block = bl;
						bigtree = true;
					}
					break;
				}
				case 2: {
					type = TreeType.BIRCH;
					if ((int) (Math.random() * 20.0D) == 10) {
						type = TreeType.TALL_BIRCH;
					}
					break;
				}
				case 3: {
					type = TreeType.SMALL_JUNGLE;
					final Block bl = isBigTree(block, (byte) 3);

					if (bl != null) {
						type = TreeType.JUNGLE;
						block = bl;
						bigtree = true;
					}
					break;
				}
				case 4: {
					type = TreeType.ACACIA;
					break;
				}
				case 5: {
					final Block bl = isBigTree(block, (byte) 5);

					if (bl != null) {
						type = TreeType.DARK_OAK;
						block = bl;
						bigtree = true;
					}
					if (type == null)
						return;
					break;
				}
				default:
					return;
				}

				if (!bigtree) {
					block.setType(Material.AIR);
				} else {
					block.setType(Material.AIR);
					block.getRelative(BlockFace.EAST).setType(Material.AIR);
					block.getRelative(BlockFace.SOUTH).setType(Material.AIR);
					block.getRelative(BlockFace.SOUTH_EAST).setType(
							Material.AIR);
				}
				l = block.getLocation();
				final boolean grew = l.getWorld().generateTree(l, type);
				if (!grew) {
					if (bigtree) {
						block.setTypeIdAndData(6, data, false);
						block.getRelative(BlockFace.EAST).setTypeIdAndData(6,
								data, false);
						block.getRelative(BlockFace.SOUTH).setTypeIdAndData(6,
								data, false);
						block.getRelative(BlockFace.SOUTH_EAST)
						.setTypeIdAndData(6, data, false);
					} else {
						block.setTypeIdAndData(6, data, false);
					}
					return;
				}
				break;

			default:
				return;
			}
		}
	}

	@SuppressWarnings("deprecation")
	private Block isBigTree(Block block, byte data) {
		if ((block.getRelative(BlockFace.NORTH) != null)
				&& (block.getRelative(BlockFace.NORTH).getType()
						.equals(Material.SAPLING))
						&& (block.getRelative(BlockFace.NORTH).getData() == data)
						&& (block.getRelative(BlockFace.EAST) != null)
						&& (block.getRelative(BlockFace.EAST).getType()
								.equals(Material.SAPLING))
								&& (block.getRelative(BlockFace.EAST).getData() == data)
								&& (block.getRelative(BlockFace.NORTH).getRelative(
										BlockFace.EAST) != null)
										&& (block.getRelative(BlockFace.NORTH)
												.getRelative(BlockFace.EAST).getType()
												.equals(Material.SAPLING))
												&& (block.getRelative(BlockFace.NORTH)
														.getRelative(BlockFace.EAST).getData() == data)) {
			block = block.getRelative(BlockFace.NORTH);
			return block;
		}
		if ((block.getRelative(BlockFace.NORTH) != null)
				&& (block.getRelative(BlockFace.NORTH).getType()
						.equals(Material.SAPLING))
						&& (block.getRelative(BlockFace.NORTH).getData() == data)
						&& (block.getRelative(BlockFace.WEST) != null)
						&& (block.getRelative(BlockFace.WEST).getType()
								.equals(Material.SAPLING))
								&& (block.getRelative(BlockFace.WEST).getData() == data)
								&& (block.getRelative(BlockFace.NORTH).getRelative(
										BlockFace.WEST) != null)
										&& (block.getRelative(BlockFace.NORTH)
												.getRelative(BlockFace.WEST).getType()
												.equals(Material.SAPLING))
												&& (block.getRelative(BlockFace.NORTH)
														.getRelative(BlockFace.WEST).getData() == data)) {
			block = block.getRelative(BlockFace.NORTH).getRelative(
					BlockFace.WEST);
			return block;
		}
		if ((block.getRelative(BlockFace.SOUTH) != null)
				&& (block.getRelative(BlockFace.SOUTH).getType()
						.equals(Material.SAPLING))
						&& (block.getRelative(BlockFace.SOUTH).getData() == data)
						&& (block.getRelative(BlockFace.WEST) != null)
						&& (block.getRelative(BlockFace.WEST).getType()
								.equals(Material.SAPLING))
								&& (block.getRelative(BlockFace.WEST).getData() == data)
								&& (block.getRelative(BlockFace.SOUTH).getRelative(
										BlockFace.WEST) != null)
										&& (block.getRelative(BlockFace.SOUTH)
												.getRelative(BlockFace.WEST).getType()
												.equals(Material.SAPLING))
												&& (block.getRelative(BlockFace.SOUTH)
														.getRelative(BlockFace.WEST).getData() == data)) {
			block = block.getRelative(BlockFace.WEST);
			return block;
		}
		if ((block.getRelative(BlockFace.SOUTH) != null)
				&& (block.getRelative(BlockFace.SOUTH).getType()
						.equals(Material.SAPLING))
						&& (block.getRelative(BlockFace.SOUTH).getData() == data)
						&& (block.getRelative(BlockFace.EAST) != null)
						&& (block.getRelative(BlockFace.EAST).getType()
								.equals(Material.SAPLING))
								&& (block.getRelative(BlockFace.EAST).getData() == data)
								&& (block.getRelative(BlockFace.SOUTH).getRelative(
										BlockFace.EAST) != null)
										&& (block.getRelative(BlockFace.SOUTH)
												.getRelative(BlockFace.EAST).getType()
												.equals(Material.SAPLING))
												&& (block.getRelative(BlockFace.SOUTH)
														.getRelative(BlockFace.EAST).getData() == data)) {
			return block;
		}
		return null;
	}

	private Boolean isBonemeal(ItemStack item) {
		if ((item != null) && (item.getType() == Material.INK_SACK)
				&& (item.getDurability() == 15)) {
			return true;
		}
		return false;
	}
}