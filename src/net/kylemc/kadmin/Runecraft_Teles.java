package net.kylemc.kadmin;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Runecraft_Teles
  implements Listener
{
  static HashSet<Material> airBlocks;
  static HashMap<Signature, Location> wayPoints;
  static HashMap<Location, Signature> teles;
  
  public void setup()
  {
    airBlocks = new HashSet();
    airBlocks.add(Material.AIR);
    airBlocks.add(Material.DIRT);
    airBlocks.add(Material.GRASS);
    airBlocks.add(Material.SAND);
    airBlocks.add(Material.STONE);
    airBlocks.add(Material.MYCEL);
    airBlocks.add(Material.DOUBLE_PLANT);
    airBlocks.add(Material.LONG_GRASS);
    
    readFromFiles();
  }
  
  @SuppressWarnings("deprecation")
@EventHandler(ignoreCancelled=true)
  public void rightClickTele(PlayerInteractEvent event)
  {
    Player player = event.getPlayer();
    
    if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) && 
      (isTool(player.getItemInHand()))) {
      Block middleBlock = event.getClickedBlock();
      if (middleBlock == null) {
        return;
      }
      if (isCompass(middleBlock)) {
        makeCompass(middleBlock);
        return;
      }
      
      if (isWayPointShaped(middleBlock)) {
        Signature signature = Signature.readFromWayPoint(middleBlock);
        if (!signature.isValidSignature()) {
          player.sendMessage(ChatColor.RED + "Invalid signature.");
          return;
        }
        Location loc = (Location)wayPoints.get(signature);
        
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
        Signature signature = Signature.readFromTele(middleBlock);
        Location loc = (Location)wayPoints.get(signature);
        Signature WPsig = (Signature)teles.get(middleBlock.getLocation());
        Location WPloc = (Location)wayPoints.get(WPsig);
        

        if (!signature.isValidSignature()) {
          if (WPloc != null)
          {

            Location airloc = findAir(WPloc);
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
    Location find = new Location(wPloc.getWorld(), wPloc.getX(), wPloc.getY() + 2.0D, wPloc.getZ());
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
    Material mat = item.getType();
    
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
    Location loc = midBlock.getLocation();
    
    double x = loc.getX();
    double y = loc.getY();
    double z = loc.getZ();
    World world = loc.getWorld();
    
    Location temp = new Location(world, x + 1.0D, y, z + 1.0D);
    Block block = temp.getBlock();
    Material frameType = block.getType();
    

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
    Location loc = midBlock.getLocation();
    
    double x = loc.getX();
    double y = loc.getY();
    double z = loc.getZ();
    World world = loc.getWorld();
    


    Material frameType = midBlock.getType();
    

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
	private static Signature readFromWayPoint(Block midBlock) { Signature toRet = new Signature();
      
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
      Signature toRet = new Signature();
      
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
    

    @SuppressWarnings("deprecation")
	public String toString()
    {
      StringBuilder s = new StringBuilder();
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
    
    public boolean equals(Object o)
    {
      Signature s = (Signature)o;
      
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
    

    @SuppressWarnings("deprecation")
	public int hashCode()
    {
      int numBlocks = 255;
      
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
    writeWayPointFile("kim_waypoints.txt");
    writeTeleFile("kim_teles.txt");
  }
  
  /* Error */
  private static void writeWayPointFile(String filename)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: new 410	java/io/File
    //   5: dup
    //   6: getstatic 412	net/kylemc/kadmin/Kadmin:dFolder	Ljava/io/File;
    //   9: aload_0
    //   10: invokespecial 418	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   13: astore_2
    //   14: aload_2
    //   15: invokevirtual 421	java/io/File:delete	()Z
    //   18: pop
    //   19: new 425	java/io/BufferedWriter
    //   22: dup
    //   23: new 427	java/io/OutputStreamWriter
    //   26: dup
    //   27: new 429	java/io/FileOutputStream
    //   30: dup
    //   31: aload_2
    //   32: invokespecial 431	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   35: ldc_w 434
    //   38: invokespecial 436	java/io/OutputStreamWriter:<init>	(Ljava/io/OutputStream;Ljava/lang/String;)V
    //   41: invokespecial 439	java/io/BufferedWriter:<init>	(Ljava/io/Writer;)V
    //   44: astore_1
    //   45: getstatic 148	net/kylemc/kadmin/Runecraft_Teles:wayPoints	Ljava/util/HashMap;
    //   48: invokevirtual 442	java/util/HashMap:keySet	()Ljava/util/Set;
    //   51: invokeinterface 446 1 0
    //   56: astore 4
    //   58: goto +97 -> 155
    //   61: aload 4
    //   63: invokeinterface 452 1 0
    //   68: checkcast 113	net/kylemc/kadmin/Runecraft_Teles$Signature
    //   71: astore_3
    //   72: aload_1
    //   73: new 122	java/lang/StringBuilder
    //   76: dup
    //   77: aload_3
    //   78: invokevirtual 458	net/kylemc/kadmin/Runecraft_Teles$Signature:toString	()Ljava/lang/String;
    //   81: invokestatic 459	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   84: invokespecial 465	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   87: ldc_w 467
    //   90: invokevirtual 137	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   93: getstatic 148	net/kylemc/kadmin/Runecraft_Teles:wayPoints	Ljava/util/HashMap;
    //   96: aload_3
    //   97: invokevirtual 150	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   100: checkcast 156	org/bukkit/Location
    //   103: invokestatic 469	net/kylemc/kadmin/Runecraft_Teles:locFormat	(Lorg/bukkit/Location;)Ljava/lang/String;
    //   106: invokevirtual 137	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   109: ldc_w 473
    //   112: invokevirtual 137	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   115: invokevirtual 140	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   118: invokevirtual 475	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   121: goto +34 -> 155
    //   124: astore 5
    //   126: getstatic 169	java/lang/System:out	Ljava/io/PrintStream;
    //   129: new 122	java/lang/StringBuilder
    //   132: dup
    //   133: aload_3
    //   134: invokevirtual 458	net/kylemc/kadmin/Runecraft_Teles$Signature:toString	()Ljava/lang/String;
    //   137: invokestatic 459	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   140: invokespecial 465	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   143: ldc_w 478
    //   146: invokevirtual 137	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   149: invokevirtual 140	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   152: invokevirtual 177	java/io/PrintStream:println	(Ljava/lang/String;)V
    //   155: aload 4
    //   157: invokeinterface 480 1 0
    //   162: ifne -101 -> 61
    //   165: goto +39 -> 204
    //   168: astore_2
    //   169: getstatic 483	java/lang/System:err	Ljava/io/PrintStream;
    //   172: ldc_w 486
    //   175: invokevirtual 177	java/io/PrintStream:println	(Ljava/lang/String;)V
    //   178: aload_1
    //   179: invokevirtual 488	java/io/BufferedWriter:close	()V
    //   182: goto +31 -> 213
    //   185: astore 7
    //   187: goto +26 -> 213
    //   190: astore 6
    //   192: aload_1
    //   193: invokevirtual 488	java/io/BufferedWriter:close	()V
    //   196: goto +5 -> 201
    //   199: astore 7
    //   201: aload 6
    //   203: athrow
    //   204: aload_1
    //   205: invokevirtual 488	java/io/BufferedWriter:close	()V
    //   208: goto +5 -> 213
    //   211: astore 7
    //   213: return
    // Line number table:
    //   Java source line #469	-> byte code offset #0
    //   Java source line #472	-> byte code offset #2
    //   Java source line #473	-> byte code offset #14
    //   Java source line #474	-> byte code offset #19
    //   Java source line #475	-> byte code offset #45
    //   Java source line #477	-> byte code offset #72
    //   Java source line #478	-> byte code offset #121
    //   Java source line #479	-> byte code offset #124
    //   Java source line #480	-> byte code offset #126
    //   Java source line #475	-> byte code offset #155
    //   Java source line #483	-> byte code offset #165
    //   Java source line #484	-> byte code offset #169
    //   Java source line #486	-> byte code offset #178
    //   Java source line #485	-> byte code offset #190
    //   Java source line #486	-> byte code offset #192
    //   Java source line #487	-> byte code offset #201
    //   Java source line #486	-> byte code offset #204
    //   Java source line #488	-> byte code offset #213
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	214	0	filename	String
    //   1	204	1	writer	java.io.BufferedWriter
    //   13	19	2	f	java.io.File
    //   168	2	2	ex	java.io.IOException
    //   71	63	3	s	Signature
    //   56	100	4	localIterator	java.util.Iterator
    //   124	3	5	e	NullPointerException
    //   190	12	6	localObject	Object
    //   185	1	7	localException	Exception
    //   199	1	7	localException1	Exception
    //   211	1	7	localException2	Exception
    // Exception table:
    //   from	to	target	type
    //   72	121	124	java/lang/NullPointerException
    //   2	165	168	java/io/IOException
    //   178	182	185	java/lang/Exception
    //   2	178	190	finally
    //   192	196	199	java/lang/Exception
    //   204	208	211	java/lang/Exception
  }
  
  /* Error */
  private static void writeTeleFile(String filename)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: new 410	java/io/File
    //   5: dup
    //   6: getstatic 412	net/kylemc/kadmin/Kadmin:dFolder	Ljava/io/File;
    //   9: aload_0
    //   10: invokespecial 418	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   13: astore_2
    //   14: aload_2
    //   15: invokevirtual 421	java/io/File:delete	()Z
    //   18: pop
    //   19: new 425	java/io/BufferedWriter
    //   22: dup
    //   23: new 427	java/io/OutputStreamWriter
    //   26: dup
    //   27: new 429	java/io/FileOutputStream
    //   30: dup
    //   31: aload_2
    //   32: invokespecial 431	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   35: ldc_w 434
    //   38: invokespecial 436	java/io/OutputStreamWriter:<init>	(Ljava/io/OutputStream;Ljava/lang/String;)V
    //   41: invokespecial 439	java/io/BufferedWriter:<init>	(Ljava/io/Writer;)V
    //   44: astore_1
    //   45: getstatic 206	net/kylemc/kadmin/Runecraft_Teles:teles	Ljava/util/HashMap;
    //   48: invokevirtual 442	java/util/HashMap:keySet	()Ljava/util/Set;
    //   51: invokeinterface 446 1 0
    //   56: astore 4
    //   58: goto +97 -> 155
    //   61: aload 4
    //   63: invokeinterface 452 1 0
    //   68: checkcast 156	org/bukkit/Location
    //   71: astore_3
    //   72: aload_1
    //   73: new 122	java/lang/StringBuilder
    //   76: dup
    //   77: getstatic 206	net/kylemc/kadmin/Runecraft_Teles:teles	Ljava/util/HashMap;
    //   80: aload_3
    //   81: invokevirtual 150	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   84: checkcast 113	net/kylemc/kadmin/Runecraft_Teles$Signature
    //   87: invokevirtual 458	net/kylemc/kadmin/Runecraft_Teles$Signature:toString	()Ljava/lang/String;
    //   90: invokestatic 459	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   93: invokespecial 465	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   96: ldc_w 467
    //   99: invokevirtual 137	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   102: aload_3
    //   103: invokestatic 469	net/kylemc/kadmin/Runecraft_Teles:locFormat	(Lorg/bukkit/Location;)Ljava/lang/String;
    //   106: invokevirtual 137	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   109: ldc_w 473
    //   112: invokevirtual 137	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   115: invokevirtual 140	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   118: invokevirtual 475	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   121: goto +34 -> 155
    //   124: astore 5
    //   126: getstatic 169	java/lang/System:out	Ljava/io/PrintStream;
    //   129: new 122	java/lang/StringBuilder
    //   132: dup
    //   133: aload_3
    //   134: invokevirtual 509	org/bukkit/Location:toString	()Ljava/lang/String;
    //   137: invokestatic 459	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   140: invokespecial 465	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   143: ldc_w 510
    //   146: invokevirtual 137	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   149: invokevirtual 140	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   152: invokevirtual 177	java/io/PrintStream:println	(Ljava/lang/String;)V
    //   155: aload 4
    //   157: invokeinterface 480 1 0
    //   162: ifne -101 -> 61
    //   165: goto +39 -> 204
    //   168: astore_2
    //   169: getstatic 483	java/lang/System:err	Ljava/io/PrintStream;
    //   172: ldc_w 512
    //   175: invokevirtual 177	java/io/PrintStream:println	(Ljava/lang/String;)V
    //   178: aload_1
    //   179: invokevirtual 488	java/io/BufferedWriter:close	()V
    //   182: goto +31 -> 213
    //   185: astore 7
    //   187: goto +26 -> 213
    //   190: astore 6
    //   192: aload_1
    //   193: invokevirtual 488	java/io/BufferedWriter:close	()V
    //   196: goto +5 -> 201
    //   199: astore 7
    //   201: aload 6
    //   203: athrow
    //   204: aload_1
    //   205: invokevirtual 488	java/io/BufferedWriter:close	()V
    //   208: goto +5 -> 213
    //   211: astore 7
    //   213: return
    // Line number table:
    //   Java source line #491	-> byte code offset #0
    //   Java source line #494	-> byte code offset #2
    //   Java source line #495	-> byte code offset #14
    //   Java source line #496	-> byte code offset #19
    //   Java source line #497	-> byte code offset #45
    //   Java source line #499	-> byte code offset #72
    //   Java source line #500	-> byte code offset #121
    //   Java source line #501	-> byte code offset #124
    //   Java source line #502	-> byte code offset #126
    //   Java source line #497	-> byte code offset #155
    //   Java source line #505	-> byte code offset #165
    //   Java source line #506	-> byte code offset #169
    //   Java source line #508	-> byte code offset #178
    //   Java source line #507	-> byte code offset #190
    //   Java source line #508	-> byte code offset #192
    //   Java source line #509	-> byte code offset #201
    //   Java source line #508	-> byte code offset #204
    //   Java source line #510	-> byte code offset #213
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	214	0	filename	String
    //   1	204	1	writer	java.io.BufferedWriter
    //   13	19	2	f	java.io.File
    //   168	2	2	ex	java.io.IOException
    //   71	63	3	l	Location
    //   56	100	4	localIterator	java.util.Iterator
    //   124	3	5	e	NullPointerException
    //   190	12	6	localObject	Object
    //   185	1	7	localException	Exception
    //   199	1	7	localException1	Exception
    //   211	1	7	localException2	Exception
    // Exception table:
    //   from	to	target	type
    //   72	121	124	java/lang/NullPointerException
    //   2	165	168	java/io/IOException
    //   178	182	185	java/lang/Exception
    //   2	178	190	finally
    //   192	196	199	java/lang/Exception
    //   204	208	211	java/lang/Exception
  }
  
  private static String locFormat(Location loc)
    throws NullPointerException
  {
    StringBuilder s = new StringBuilder();
    s.append(loc.getWorld().getName() + "|");
    s.append(loc.getX() + "|");
    s.append(loc.getY() + "|");
    s.append(loc.getZ());
    
    return s.toString();
  }
  
  private void readFromFiles()
  {
    wayPoints = new HashMap();
    teles = new HashMap();
    try {
      readFromWayPointFile("kim_waypoints.txt");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    try {
      readFromTeleFile("kim_teles.txt");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
  
  /* Error */
  private void readFromWayPointFile(String filename)
    throws FileNotFoundException
  {
    // Byte code:
    //   0: new 410	java/io/File
    //   3: dup
    //   4: getstatic 412	net/kylemc/kadmin/Kadmin:dFolder	Ljava/io/File;
    //   7: aload_1
    //   8: invokespecial 418	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   11: astore_2
    //   12: aload_2
    //   13: invokevirtual 539	java/io/File:exists	()Z
    //   16: ifeq +109 -> 125
    //   19: new 542	java/io/BufferedReader
    //   22: dup
    //   23: new 544	java/io/FileReader
    //   26: dup
    //   27: aload_2
    //   28: invokespecial 546	java/io/FileReader:<init>	(Ljava/io/File;)V
    //   31: invokespecial 547	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   34: astore_3
    //   35: aload_3
    //   36: invokevirtual 550	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   39: astore 4
    //   41: goto +16 -> 57
    //   44: aload_0
    //   45: aload 4
    //   47: iconst_1
    //   48: invokespecial 553	net/kylemc/kadmin/Runecraft_Teles:addToHash	(Ljava/lang/String;Z)V
    //   51: aload_3
    //   52: invokevirtual 550	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   55: astore 4
    //   57: aload 4
    //   59: ifnonnull -15 -> 44
    //   62: goto +46 -> 108
    //   65: astore 4
    //   67: aload 4
    //   69: invokevirtual 557	java/io/IOException:printStackTrace	()V
    //   72: aload_3
    //   73: invokevirtual 558	java/io/BufferedReader:close	()V
    //   76: goto +88 -> 164
    //   79: astore 6
    //   81: aload 6
    //   83: invokevirtual 557	java/io/IOException:printStackTrace	()V
    //   86: goto +78 -> 164
    //   89: astore 5
    //   91: aload_3
    //   92: invokevirtual 558	java/io/BufferedReader:close	()V
    //   95: goto +10 -> 105
    //   98: astore 6
    //   100: aload 6
    //   102: invokevirtual 557	java/io/IOException:printStackTrace	()V
    //   105: aload 5
    //   107: athrow
    //   108: aload_3
    //   109: invokevirtual 558	java/io/BufferedReader:close	()V
    //   112: goto +52 -> 164
    //   115: astore 6
    //   117: aload 6
    //   119: invokevirtual 557	java/io/IOException:printStackTrace	()V
    //   122: goto +42 -> 164
    //   125: getstatic 169	java/lang/System:out	Ljava/io/PrintStream;
    //   128: new 122	java/lang/StringBuilder
    //   131: dup
    //   132: aload_1
    //   133: invokestatic 459	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   136: invokespecial 465	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   139: ldc_w 559
    //   142: invokevirtual 137	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   145: invokevirtual 140	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   148: invokevirtual 177	java/io/PrintStream:println	(Ljava/lang/String;)V
    //   151: aload_2
    //   152: invokevirtual 561	java/io/File:createNewFile	()Z
    //   155: pop
    //   156: goto +8 -> 164
    //   159: astore_3
    //   160: aload_3
    //   161: invokevirtual 557	java/io/IOException:printStackTrace	()V
    //   164: return
    // Line number table:
    //   Java source line #539	-> byte code offset #0
    //   Java source line #541	-> byte code offset #12
    //   Java source line #542	-> byte code offset #19
    //   Java source line #545	-> byte code offset #35
    //   Java source line #547	-> byte code offset #41
    //   Java source line #548	-> byte code offset #44
    //   Java source line #549	-> byte code offset #51
    //   Java source line #547	-> byte code offset #57
    //   Java source line #551	-> byte code offset #62
    //   Java source line #552	-> byte code offset #65
    //   Java source line #553	-> byte code offset #67
    //   Java source line #556	-> byte code offset #72
    //   Java source line #557	-> byte code offset #76
    //   Java source line #558	-> byte code offset #81
    //   Java source line #554	-> byte code offset #89
    //   Java source line #556	-> byte code offset #91
    //   Java source line #557	-> byte code offset #95
    //   Java source line #558	-> byte code offset #100
    //   Java source line #560	-> byte code offset #105
    //   Java source line #556	-> byte code offset #108
    //   Java source line #557	-> byte code offset #112
    //   Java source line #558	-> byte code offset #117
    //   Java source line #561	-> byte code offset #122
    //   Java source line #563	-> byte code offset #125
    //   Java source line #565	-> byte code offset #151
    //   Java source line #566	-> byte code offset #156
    //   Java source line #567	-> byte code offset #160
    //   Java source line #570	-> byte code offset #164
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	165	0	this	Runecraft_Teles
    //   0	165	1	filename	String
    //   11	141	2	f	java.io.File
    //   34	75	3	br	java.io.BufferedReader
    //   159	2	3	e	java.io.IOException
    //   39	19	4	line	String
    //   65	3	4	e	java.io.IOException
    //   89	17	5	localObject	Object
    //   79	3	6	e	java.io.IOException
    //   98	3	6	e	java.io.IOException
    //   115	3	6	e	java.io.IOException
    // Exception table:
    //   from	to	target	type
    //   35	62	65	java/io/IOException
    //   72	76	79	java/io/IOException
    //   35	72	89	finally
    //   91	95	98	java/io/IOException
    //   108	112	115	java/io/IOException
    //   151	156	159	java/io/IOException
  }
  
  /* Error */
  private void readFromTeleFile(String filename)
    throws FileNotFoundException
  {
    // Byte code:
    //   0: new 410	java/io/File
    //   3: dup
    //   4: getstatic 412	net/kylemc/kadmin/Kadmin:dFolder	Ljava/io/File;
    //   7: aload_1
    //   8: invokespecial 418	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   11: astore_2
    //   12: aload_2
    //   13: invokevirtual 539	java/io/File:exists	()Z
    //   16: ifeq +109 -> 125
    //   19: new 542	java/io/BufferedReader
    //   22: dup
    //   23: new 544	java/io/FileReader
    //   26: dup
    //   27: aload_2
    //   28: invokespecial 546	java/io/FileReader:<init>	(Ljava/io/File;)V
    //   31: invokespecial 547	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   34: astore_3
    //   35: aload_3
    //   36: invokevirtual 550	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   39: astore 4
    //   41: goto +16 -> 57
    //   44: aload_0
    //   45: aload 4
    //   47: iconst_0
    //   48: invokespecial 553	net/kylemc/kadmin/Runecraft_Teles:addToHash	(Ljava/lang/String;Z)V
    //   51: aload_3
    //   52: invokevirtual 550	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   55: astore 4
    //   57: aload 4
    //   59: ifnonnull -15 -> 44
    //   62: goto +46 -> 108
    //   65: astore 4
    //   67: aload 4
    //   69: invokevirtual 557	java/io/IOException:printStackTrace	()V
    //   72: aload_3
    //   73: invokevirtual 558	java/io/BufferedReader:close	()V
    //   76: goto +88 -> 164
    //   79: astore 6
    //   81: aload 6
    //   83: invokevirtual 557	java/io/IOException:printStackTrace	()V
    //   86: goto +78 -> 164
    //   89: astore 5
    //   91: aload_3
    //   92: invokevirtual 558	java/io/BufferedReader:close	()V
    //   95: goto +10 -> 105
    //   98: astore 6
    //   100: aload 6
    //   102: invokevirtual 557	java/io/IOException:printStackTrace	()V
    //   105: aload 5
    //   107: athrow
    //   108: aload_3
    //   109: invokevirtual 558	java/io/BufferedReader:close	()V
    //   112: goto +52 -> 164
    //   115: astore 6
    //   117: aload 6
    //   119: invokevirtual 557	java/io/IOException:printStackTrace	()V
    //   122: goto +42 -> 164
    //   125: getstatic 169	java/lang/System:out	Ljava/io/PrintStream;
    //   128: new 122	java/lang/StringBuilder
    //   131: dup
    //   132: aload_1
    //   133: invokestatic 459	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   136: invokespecial 465	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   139: ldc_w 559
    //   142: invokevirtual 137	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   145: invokevirtual 140	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   148: invokevirtual 177	java/io/PrintStream:println	(Ljava/lang/String;)V
    //   151: aload_2
    //   152: invokevirtual 561	java/io/File:createNewFile	()Z
    //   155: pop
    //   156: goto +8 -> 164
    //   159: astore_3
    //   160: aload_3
    //   161: invokevirtual 557	java/io/IOException:printStackTrace	()V
    //   164: return
    // Line number table:
    //   Java source line #573	-> byte code offset #0
    //   Java source line #575	-> byte code offset #12
    //   Java source line #577	-> byte code offset #19
    //   Java source line #579	-> byte code offset #35
    //   Java source line #581	-> byte code offset #41
    //   Java source line #582	-> byte code offset #44
    //   Java source line #583	-> byte code offset #51
    //   Java source line #581	-> byte code offset #57
    //   Java source line #586	-> byte code offset #62
    //   Java source line #587	-> byte code offset #65
    //   Java source line #588	-> byte code offset #67
    //   Java source line #592	-> byte code offset #72
    //   Java source line #593	-> byte code offset #76
    //   Java source line #594	-> byte code offset #81
    //   Java source line #590	-> byte code offset #89
    //   Java source line #592	-> byte code offset #91
    //   Java source line #593	-> byte code offset #95
    //   Java source line #594	-> byte code offset #100
    //   Java source line #596	-> byte code offset #105
    //   Java source line #592	-> byte code offset #108
    //   Java source line #593	-> byte code offset #112
    //   Java source line #594	-> byte code offset #117
    //   Java source line #597	-> byte code offset #122
    //   Java source line #599	-> byte code offset #125
    //   Java source line #601	-> byte code offset #151
    //   Java source line #602	-> byte code offset #156
    //   Java source line #603	-> byte code offset #160
    //   Java source line #606	-> byte code offset #164
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	165	0	this	Runecraft_Teles
    //   0	165	1	filename	String
    //   11	141	2	f	java.io.File
    //   34	75	3	br	java.io.BufferedReader
    //   159	2	3	e	java.io.IOException
    //   39	19	4	line	String
    //   65	3	4	e	java.io.IOException
    //   89	17	5	localObject	Object
    //   79	3	6	e	java.io.IOException
    //   98	3	6	e	java.io.IOException
    //   115	3	6	e	java.io.IOException
    // Exception table:
    //   from	to	target	type
    //   35	62	65	java/io/IOException
    //   72	76	79	java/io/IOException
    //   35	72	89	finally
    //   91	95	98	java/io/IOException
    //   108	112	115	java/io/IOException
    //   151	156	159	java/io/IOException
  }
  
  @SuppressWarnings("deprecation")
private void addToHash(String line, boolean isWayPoint)
  {
    String[] pieces = line.split("\\|");
    Signature s = new Signature();
    s.north = Material.getMaterial(Integer.parseInt(pieces[0]));
    s.south = Material.getMaterial(Integer.parseInt(pieces[1]));
    s.east = Material.getMaterial(Integer.parseInt(pieces[2]));
    s.west = Material.getMaterial(Integer.parseInt(pieces[3]));
    s.northD = Byte.parseByte(pieces[4]);
    s.southD = Byte.parseByte(pieces[5]);
    s.eastD = Byte.parseByte(pieces[6]);
    s.westD = Byte.parseByte(pieces[7]);
    
    Location l = new Location(Bukkit.getServer().getWorld(pieces[8]), Double.parseDouble(pieces[9]), 
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