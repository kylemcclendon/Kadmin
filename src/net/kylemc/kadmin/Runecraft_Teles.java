package net.kylemc.kadmin;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Sets;

public class Runecraft_Teles
  implements Listener
{
  static HashSet<Material> airBlocks;
  static HashMap<Signature, Location> wayPoints;
  static HashMap<Location, Signature> teles;
  static Logger log = Bukkit.getLogger();

  public void setup()
  {
    airBlocks = Sets.newHashSet(Material.AIR, Material.DIRT, Material.GRASS, Material.SAND,
    		Material.STONE, Material.MYCEL, Material.DOUBLE_PLANT, Material.LONG_GRASS);

    log.info("Reading Waypoints and Teleports files...");
    readFromFiles();
    log.info("Loaded " + wayPoints.size() + " waypoints.");
    log.info("Loaded " + teles.size() + " teles.");
  }

  @SuppressWarnings("deprecation")
@EventHandler(ignoreCancelled=true)
  public void rightClickTele(PlayerInteractEvent event)
  {
    final Player player = event.getPlayer();

    if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
      (isTool(player.getItemInHand()))) {
      final Block middleBlock = event.getClickedBlock();
      if (middleBlock == null) {
        return;
      }
      if (isCompass(middleBlock)) {
        makeCompass(middleBlock);
        return;
      }

      if (isWayPointShaped(middleBlock)) {
        final Signature signature = Signature.readFromWayPoint(middleBlock);
        if (!signature.isValidSignature()) {
          player.sendMessage(ChatColor.RED + "Invalid signature.");
          return;
        }
        final Location loc = wayPoints.get(signature);

        if (loc == null)
        {
          player.sendMessage(ChatColor.YELLOW + "Waypoint accepted.");
          if (middleBlock.getLocation() == null) {
            System.out.println("bad block, find fix");
          }
          wayPoints.put(signature, middleBlock.getLocation());


        }
        else if (middleBlock.getLocation().equals(loc)) {
          player.sendMessage(ChatColor.RED + "Waypoint already established.");

        }
        else if ((isWayPointShaped(loc.getBlock())) &&
          (Signature.readFromWayPoint(loc.getBlock()).equals(signature))) {
          player.sendMessage(ChatColor.RED + "Another Waypoint already uses this signature.");
        }
        else {
          player.sendMessage(ChatColor.GREEN + "Waypoint accepted.");
          wayPoints.put(signature, middleBlock.getLocation());
        }


      }
      else if (isTeleShaped(middleBlock)) {
        final Signature signature = Signature.readFromTele(middleBlock);
        final Location loc = wayPoints.get(signature);
        final Signature WPsig = teles.get(middleBlock.getLocation());
        final Location WPloc = wayPoints.get(WPsig);

        if (!signature.isValidSignature()) {
          if (WPloc != null)
          {
            final Location airloc = findAir(WPloc);
            if ((isWayPointShaped(WPloc.getBlock())) && (Signature.readFromWayPoint(WPloc.getBlock()).equals(WPsig)) && (airloc != null)) {
              player.teleport(airloc.add(0.5D, 0.0D, 0.5D));
              player.sendMessage(ChatColor.YELLOW + "Teleporter used.");
            }
            else {
              player.sendMessage(ChatColor.RED + "Your way has been barred from the other side.");
            }
          }
          else {
            player.sendMessage(ChatColor.RED + "This tele doesn't go anywhere!");
          }


        }
        else if (loc != null)
        {
          teles.put(middleBlock.getLocation(), signature);


          middleBlock.getWorld().getBlockAt(new Location(middleBlock.getWorld(), middleBlock.getX() + 2, middleBlock.getY(), middleBlock.getZ())).setType(Material.AIR);
          middleBlock.getWorld().getBlockAt(new Location(middleBlock.getWorld(), middleBlock.getX() + 2, middleBlock.getY(), middleBlock.getZ())).setData((byte)0);

          middleBlock.getWorld().getBlockAt(new Location(middleBlock.getWorld(), middleBlock.getX() - 2, middleBlock.getY(), middleBlock.getZ())).setType(Material.AIR);
          middleBlock.getWorld().getBlockAt(new Location(middleBlock.getWorld(), middleBlock.getX() - 2, middleBlock.getY(), middleBlock.getZ())).setData((byte)0);

          middleBlock.getWorld().getBlockAt(new Location(middleBlock.getWorld(), middleBlock.getX(), middleBlock.getY(), middleBlock.getZ() + 2)).setType(Material.AIR);
          middleBlock.getWorld().getBlockAt(new Location(middleBlock.getWorld(), middleBlock.getX(), middleBlock.getY(), middleBlock.getZ() + 2)).setData((byte)0);

          middleBlock.getWorld().getBlockAt(new Location(middleBlock.getWorld(), middleBlock.getX(), middleBlock.getY(), middleBlock.getZ() - 2)).setType(Material.AIR);
          middleBlock.getWorld().getBlockAt(new Location(middleBlock.getWorld(), middleBlock.getX(), middleBlock.getY(), middleBlock.getZ() - 2)).setData((byte)0);

          player.sendMessage(ChatColor.GREEN + "Teleporter accepted.");
        }
        else {
          player.sendMessage(ChatColor.RED + "Signature not recognized.");
        }
      }
    }
  }

  private Location findAir(Location wPloc)
  {
    final Location find = new Location(wPloc.getWorld(), wPloc.getX(), wPloc.getY() + 2.0D, wPloc.getZ());
    int y = (int)find.getY();

    while (y < 256) {
      if ((find.getBlock().getType().equals(Material.AIR)) && (find.getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR))) {
        return find.getBlock().getRelative(BlockFace.DOWN).getLocation();
      }
      y += 2;
      find.add(0.0D, 2.0D, 0.0D);
    }
    return null;
  }

  private boolean isTool(ItemStack item)
  {
    if (item == null) {
      return true;
    }
    final Material mat = item.getType();

    if (mat.equals(Material.AIR)) {
      return true;
    }
    if ((mat.equals(Material.WOOD_AXE)) || (mat.equals(Material.WOOD_HOE)) || (mat.equals(Material.WOOD_PICKAXE)) || (mat.equals(Material.WOOD_SPADE)) || (mat.equals(Material.WOOD_SWORD))) {
      return true;
    }

    if ((mat.equals(Material.STONE_AXE)) || (mat.equals(Material.STONE_HOE)) || (mat.equals(Material.STONE_PICKAXE)) || (mat.equals(Material.STONE_SPADE)) || (mat.equals(Material.STONE_SWORD))) {
      return true;
    }

    if ((mat.equals(Material.GOLD_AXE)) || (mat.equals(Material.GOLD_HOE)) || (mat.equals(Material.GOLD_PICKAXE)) || (mat.equals(Material.GOLD_SPADE)) || (mat.equals(Material.GOLD_SWORD))) {
      return true;
    }

    if ((mat.equals(Material.IRON_AXE)) || (mat.equals(Material.IRON_HOE)) || (mat.equals(Material.IRON_PICKAXE)) || (mat.equals(Material.IRON_SPADE)) || (mat.equals(Material.IRON_SWORD))) {
      return true;
    }

    if ((mat.equals(Material.DIAMOND_AXE)) || (mat.equals(Material.DIAMOND_HOE)) || (mat.equals(Material.DIAMOND_PICKAXE)) || (mat.equals(Material.DIAMOND_SPADE)) || (mat.equals(Material.DIAMOND_SWORD))) {
      return true;
    }

    return false;
  }

  private boolean isTeleShaped(Block midBlock)
  {
    final Location loc = midBlock.getLocation();

    final double x = loc.getX();
    final double y = loc.getY();
    final double z = loc.getZ();
    final World world = loc.getWorld();

    Location temp = new Location(world, x + 1.0D, y, z + 1.0D);
    Block block = temp.getBlock();
    final Material frameType = block.getType();


    if ((frameType.equals(midBlock.getType())) || (airBlocks.contains(frameType))) {
      return false;
    }


    for (int i = -2; i <= 2; i++) {
      temp = new Location(world, x + i, y, z + 1.0D);
      block = temp.getBlock();
      if (!block.getType().equals(frameType)) { return false;
      }
      temp = new Location(world, x + i, y, z - 1.0D);
      block = temp.getBlock();
      if (!block.getType().equals(frameType)) { return false;
      }
    }

    for (int i = -2; i <= 2; i += 2) {
      temp = new Location(world, x + 1.0D, y, z + i);
      block = temp.getBlock();
      if (!block.getType().equals(frameType)) { return false;
      }
      temp = new Location(world, x - 1.0D, y, z + i);
      block = temp.getBlock();
      if (!block.getType().equals(frameType)) { return false;
      }
    }

    if (!airBlocks.contains(new Location(world, x + 2.0D, y, z + 2.0D).getBlock().getType())) {
      return false;
    }
    if (!airBlocks.contains(new Location(world, x + 2.0D, y, z - 2.0D).getBlock().getType())) {
      return false;
    }
    if (!airBlocks.contains(new Location(world, x - 2.0D, y, z + 2.0D).getBlock().getType())) {
      return false;
    }
    if (!airBlocks.contains(new Location(world, x - 2.0D, y, z - 2.0D).getBlock().getType())) {
      return false;
    }
    return true;
  }

  private boolean isWayPointShaped(Block midBlock)
  {
    final Location loc = midBlock.getLocation();

    final double x = loc.getX();
    final double y = loc.getY();
    final double z = loc.getZ();
    final World world = loc.getWorld();

    final Material frameType = midBlock.getType();

    if (airBlocks.contains(frameType)) {
      return false;
    }


    for (int i = -1; i <= 1; i++) {
      Location temp = new Location(world, x + i, y, z + 2.0D);
      Block block = temp.getBlock();
      if (!block.getType().equals(frameType)) { return false;
      }
      temp = new Location(world, x + i, y, z - 2.0D);
      block = temp.getBlock();
      if (!block.getType().equals(frameType)) { return false;
      }
      temp = new Location(world, x + 2.0D, y, z + i);
      block = temp.getBlock();
      if (!block.getType().equals(frameType)) { return false;
      }
      temp = new Location(world, x - 2.0D, y, z + i);
      block = temp.getBlock();
      if (!block.getType().equals(frameType)) { return false;
      }
    }

    if (!frameType.equals(new Location(world, x + 1.0D, y, z + 1.0D).getBlock().getType())) {
      return false;
    }
    if (!frameType.equals(new Location(world, x + 1.0D, y, z - 1.0D).getBlock().getType())) {
      return false;
    }
    if (!frameType.equals(new Location(world, x - 1.0D, y, z + 1.0D).getBlock().getType())) {
      return false;
    }
    if (!frameType.equals(new Location(world, x - 1.0D, y, z - 1.0D).getBlock().getType())) {
      return false;
    }


    if (!airBlocks.contains(new Location(world, x + 2.0D, y, z + 2.0D).getBlock().getType())) {
      return false;
    }
    if (!airBlocks.contains(new Location(world, x + 2.0D, y, z - 2.0D).getBlock().getType())) {
      return false;
    }
    if (!airBlocks.contains(new Location(world, x - 2.0D, y, z + 2.0D).getBlock().getType())) {
      return false;
    }
    if (!airBlocks.contains(new Location(world, x - 2.0D, y, z - 2.0D).getBlock().getType())) {
      return false;
    }


    if (frameType.equals(new Location(world, x + 1.0D, y, z).getBlock().getType())) {
      return false;
    }
    if (frameType.equals(new Location(world, x + 1.0D, y, z).getBlock().getType())) {
      return false;
    }
    if (frameType.equals(new Location(world, x, y, z + 1.0D).getBlock().getType())) {
      return false;
    }
    if (frameType.equals(new Location(world, x, y, z - 1.0D).getBlock().getType())) {
      return false;
    }
    return true;
  }

  private static class Signature { Material north;
    Material south;
    Material east;
    Material west;
    byte northD;
    byte southD;
    byte eastD;
    byte westD;

    @SuppressWarnings("deprecation")
	private static Signature readFromWayPoint(Block midBlock) { final Signature toRet = new Signature();

      Block temp = midBlock.getRelative(BlockFace.NORTH);
      toRet.north = temp.getType();
      toRet.northD = temp.getData();

      temp = midBlock.getRelative(BlockFace.SOUTH);
      toRet.south = temp.getType();
      toRet.southD = temp.getData();

      temp = midBlock.getRelative(BlockFace.EAST);
      toRet.east = temp.getType();
      toRet.eastD = temp.getData();

      temp = midBlock.getRelative(BlockFace.WEST);
      toRet.west = temp.getType();
      toRet.westD = temp.getData();

      return toRet;
    }



    @SuppressWarnings("deprecation")
	private static Signature readFromTele(Block midBlock)
    {
      final Signature toRet = new Signature();

      Block temp = midBlock.getRelative(BlockFace.NORTH, 2);
      toRet.north = temp.getType();
      toRet.northD = temp.getData();

      temp = midBlock.getRelative(BlockFace.SOUTH, 2);
      toRet.south = temp.getType();
      toRet.southD = temp.getData();

      temp = midBlock.getRelative(BlockFace.EAST, 2);
      toRet.east = temp.getType();
      toRet.eastD = temp.getData();

      temp = midBlock.getRelative(BlockFace.WEST, 2);
      toRet.west = temp.getType();
      toRet.westD = temp.getData();

      return toRet;
    }

    private boolean isValidSignature()
    {
      if ((Runecraft_Teles.airBlocks.contains(this.north)) && (Runecraft_Teles.airBlocks.contains(this.south)) &&
        (Runecraft_Teles.airBlocks.contains(this.east)) && (Runecraft_Teles.airBlocks.contains(this.west))) {
        return false;
      }
      return true;
    }


    @Override
	@SuppressWarnings("deprecation")
	public String toString()
    {
      final StringBuilder s = new StringBuilder();
      s.append(this.north.getId() + "|");
      s.append(this.south.getId() + "|");
      s.append(this.east.getId() + "|");
      s.append(this.west.getId() + "|");
      s.append(this.northD + "|");
      s.append(this.southD + "|");
      s.append(this.eastD + "|");
      s.append(this.westD);
      return s.toString();
    }

    @Override
	public boolean equals(Object o)
    {
      final Signature s = (Signature)o;

      if ((!this.north.equals(s.north)) || (!this.south.equals(s.south)) ||
        (!this.east.equals(s.east)) || (!this.west.equals(s.west))) {
        return false;
      }
      if ((this.northD != s.northD) || (this.southD != s.southD) ||
        (this.eastD != s.eastD) || (this.westD != s.westD)) {
        return false;
      }
      return true;
    }


    @Override
	@SuppressWarnings("deprecation")
	public int hashCode()
    {
      final int numBlocks = 255;

      int code = this.north.getId();
      code *= numBlocks;
      code += this.south.getId();
      code *= numBlocks;
      code += this.east.getId();
      code *= numBlocks;
      code += this.west.getId();
      code += this.northD;
      code += this.southD;
      code += this.eastD;
      code += this.westD;

      return code;
    }
  }

  public static void writeToFiles()
  {
	log.info("Writing to Waypoint and Teleports files...");
    writeWayPointFile("kim_waypoints.txt");
    writeTeleFile("kim_teles.txt");
    log.info("Waypoint and Tele files written");
  }

  private static void writeWayPointFile(String filename)
  {
	  final StringBuilder sb = new StringBuilder();
	  for(final Map.Entry<Signature, Location> entry : wayPoints.entrySet()){
		  sb.append(entry.getKey().toString());
		  sb.append("|");
		  sb.append(locFormat(entry.getValue()));
		  sb.append('\n');
	  }

	  sb.setLength(sb.length() - 1);

	  PrintWriter writer;
	try {
		writer = new PrintWriter(Kadmin.dFolder + "/" + filename, "UTF-8");
		  writer.println(sb.toString());
		  writer.close();
	} catch (FileNotFoundException | UnsupportedEncodingException e) {
		Bukkit.getLogger().severe("Could not find waypoint file");
	}
  }

  private static void writeTeleFile(String filename)
  {
	  final StringBuilder sb = new StringBuilder();
	  for(final Map.Entry<Location, Signature> entry : teles.entrySet()){
		  sb.append(entry.getValue().toString());
		  sb.append("|");
		  sb.append(locFormat(entry.getKey()));
		  sb.append('\n');
	  }

	  sb.setLength(sb.length() - 1);

	  PrintWriter writer;
	try {
		writer = new PrintWriter(Kadmin.dFolder + "/" + filename, "UTF-8");
		  writer.println(sb.toString());
		  writer.close();
	} catch (FileNotFoundException | UnsupportedEncodingException e) {
		Bukkit.getLogger().severe("Could not find tele file");
	}
  }

  private static String locFormat(Location loc)
  {
    final StringBuilder s = new StringBuilder();
    s.append(loc.getWorld().getName() + "|");
    s.append(loc.getX() + "|");
    s.append(loc.getY() + "|");
    s.append(loc.getZ());

    return s.toString();
  }

  private void readFromFiles()
  {
    wayPoints = new HashMap<Signature, Location>();
    teles = new HashMap<Location, Signature>();
    try {
      readFromWayPointFile("kim_waypoints.txt");
    } catch (final FileNotFoundException e) {
    	Bukkit.getLogger().severe("Could not load waypoint file!");
    } catch (final IOException e) {
    	Bukkit.getLogger().severe("Problem loading waypoints");
	}
    try {
      readFromTeleFile("kim_teles.txt");
    } catch (final FileNotFoundException e) {
      Bukkit.getLogger().severe("Could not load tele file!");
    } catch (final IOException e) {
    	Bukkit.getLogger().severe("Problem loading teles");
	}
  }

  private void readFromWayPointFile(String filename)
    throws IOException
  {
	  try {
		final BufferedReader br = new BufferedReader(new FileReader(Kadmin.dFolder + "/" + filename));

		String line;
		while((line = br.readLine()) != null){
			addToHash(line, true);
		}
		br.close();
	} catch (final FileNotFoundException e) {
		Bukkit.getLogger().severe("Unable to load waypoint file!");
	}
  }

  private void readFromTeleFile(String filename)
    throws IOException
  {
	  try {
		final BufferedReader br = new BufferedReader(new FileReader(Kadmin.dFolder + "/" + filename));

		String line;
		while((line = br.readLine()) != null){
			addToHash(line, false);
		}
		br.close();
	} catch (final FileNotFoundException e) {
		Bukkit.getLogger().severe("Unable to load tele file!");
	}
  }

  @SuppressWarnings("deprecation")
private void addToHash(String line, boolean isWayPoint)
  {
    final String[] pieces = line.split("\\|");
    final Signature s = new Signature();
    s.north = Material.getMaterial(Integer.parseInt(pieces[0]));
    s.south = Material.getMaterial(Integer.parseInt(pieces[1]));
    s.east = Material.getMaterial(Integer.parseInt(pieces[2]));
    s.west = Material.getMaterial(Integer.parseInt(pieces[3]));
    s.northD = Byte.parseByte(pieces[4]);
    s.southD = Byte.parseByte(pieces[5]);
    s.eastD = Byte.parseByte(pieces[6]);
    s.westD = Byte.parseByte(pieces[7]);

    final Location l = new Location(Bukkit.getServer().getWorld(pieces[8]), Double.parseDouble(pieces[9]),
      Double.parseDouble(pieces[10]), Double.parseDouble(pieces[11]));

    if (isWayPoint) {
      wayPoints.put(s, l);
    } else {
      teles.put(l, s);
    }
  }

  public boolean isCompass(Block midBlock)
  {
    if ((midBlock.getType().equals(Material.COBBLESTONE)) &&
      (midBlock.getRelative(BlockFace.NORTH_EAST).getType().equals(Material.COBBLESTONE)) &&
      (midBlock.getRelative(BlockFace.SOUTH_EAST).getType().equals(Material.COBBLESTONE)) &&
      (midBlock.getRelative(BlockFace.NORTH_WEST).getType().equals(Material.COBBLESTONE)) &&
      (midBlock.getRelative(BlockFace.SOUTH_WEST).getType().equals(Material.COBBLESTONE)) &&
      (midBlock.getRelative(BlockFace.NORTH).getType().equals(Material.AIR)) &&
      (midBlock.getRelative(BlockFace.SOUTH).getType().equals(Material.AIR)) &&
      (midBlock.getRelative(BlockFace.EAST).getType().equals(Material.AIR)) &&
      (midBlock.getRelative(BlockFace.WEST).getType().equals(Material.AIR))) {
      return true;
    }
    return false;
  }

  public void makeCompass(Block midBlock)
  {
    midBlock.getRelative(BlockFace.NORTH_EAST).setType(Material.AIR);
    midBlock.getRelative(BlockFace.NORTH).setType(Material.COBBLESTONE);
    midBlock.getRelative(BlockFace.NORTH_WEST).setType(Material.AIR);
    midBlock.getRelative(BlockFace.EAST).setType(Material.COBBLESTONE);
    midBlock.setType(Material.AIR);
    midBlock.getRelative(BlockFace.WEST).setType(Material.COBBLESTONE);
  }
}