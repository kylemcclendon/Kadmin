package net.kylemc.kadmin;

import java.io.File;
import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.PluginManager;

public final class Kadmin extends org.bukkit.plugin.java.JavaPlugin
{
  public static Kadmin plugin;
  private File settingsFile;
  public YamlConfiguration settings;
  public static File dFolder = null;

  private final Bonemeal bm = new Bonemeal();
  private final CategorizedWarps cw = new CategorizedWarps(this);
  private final FeatherClip fc = new FeatherClip();
  private final Giant gi = new Giant(this);
  private final HorseTeleport ht = new HorseTeleport();
  private final PVP pv = new PVP();
//  private final Rainbow rb = new Rainbow();
  private final Restart restart = new Restart(this);
  private final Timber tim = new Timber();
  private final Runecraft_Teles run = new Runecraft_Teles();
  private static Thread t;
  protected static boolean threadVar = true;

  @Override
public void onEnable()
  {
    if (getServer().hasWhitelist()) {
      new Maintenance(this);
    }

    final PluginManager pm = getServer().getPluginManager();
    dFolder = getDataFolder();

    if (!dFolder.exists()) {
      dFolder.mkdirs();
    }

    this.settingsFile = new File(dFolder, "config.yml");
    if (!this.settingsFile.exists()) {
      this.settings = new YamlConfiguration();
      this.settings.set("Bonemeal", Boolean.valueOf(true));
      this.settings.set("CategorizedWarps", Boolean.valueOf(true));
      this.settings.set("Crafting", Boolean.valueOf(true));
      this.settings.set("Exploration", Boolean.valueOf(true));
      this.settings.set("Feather", Boolean.valueOf(true));
      this.settings.set("Flower", Boolean.valueOf(true));
      this.settings.set("Giant", Boolean.valueOf(true));
      this.settings.set("HorseTeleport", Boolean.valueOf(true));
      this.settings.set("Hyrule", Boolean.valueOf(true));
      this.settings.set("PVP", Boolean.valueOf(true));
      this.settings.set("Rainbow", Boolean.valueOf(true));
      this.settings.set("Restart", Boolean.valueOf(true));
      this.settings.set("Runecraft", Boolean.valueOf(true));
      this.settings.set("Timber", Boolean.valueOf(true));
      saveSettings();
    }

    this.settings = YamlConfiguration.loadConfiguration(this.settingsFile);
    if (this.settings.getBoolean("Bonemeal")) {
      pm.registerEvents(this.bm, this);
    }
    if (this.settings.getBoolean("CategorizedWarps")) {
      pm.registerEvents(this.cw, this);
      getCommand("cwarps").setExecutor(this.cw);
    }
    if (this.settings.getBoolean("Crafting")) {
      final ShapedRecipe book = new ShapedRecipe(new ItemStack(Material.BOOK, 1));
      book.shape(new String[] { " A ", " A ", " A " });
      book.setIngredient('A', Material.PAPER);
      getServer().addRecipe(book);

      final ShapedRecipe saddle = new ShapedRecipe(new ItemStack(Material.SADDLE, 1));
      saddle.shape(new String[] { "A A", "AAA", " B " });
      saddle.setIngredient('A', Material.LEATHER);
      saddle.setIngredient('B', Material.IRON_INGOT);
      getServer().addRecipe(saddle);

      final ShapelessRecipe nametag = new ShapelessRecipe(new ItemStack(Material.NAME_TAG, 1));
      nametag.addIngredient(Material.STRING);
      nametag.addIngredient(Material.WOOD);
      nametag.addIngredient(Material.INK_SACK);
      getServer().addRecipe(nametag);

      final ItemStack flesh = new ItemStack(Material.LEATHER, 1);
      final FurnaceRecipe leather = new FurnaceRecipe(flesh, Material.ROTTEN_FLESH);
      getServer().addRecipe(leather);
    }
    if (this.settings.getBoolean("Exploration")) {
      t = new Thread(new Exploration());
      t.start();
    }
    if (this.settings.getBoolean("Feather")) {
      pm.registerEvents(this.fc, this);
    }
    if (this.settings.getBoolean("Flower")) {
      getCommand("flower").setExecutor(new Flower());
    }
    if (this.settings.getBoolean("Giant")) {
      pm.registerEvents(this.gi, this);
      this.gi.initItems();
    }
    if (this.settings.getBoolean("HorseTeleport")) {
      pm.registerEvents(this.ht, this);
    }
    if (this.settings.getBoolean("PVP")) {
      pm.registerEvents(this.pv, this);
    }
//    if (this.settings.getBoolean("Rainbow")) {
//      pm.registerEvents(this.rb, this);
//      getCommand("hoe_undo").setExecutor(this.rb);
//      getCommand("hoe_help").setExecutor(this.rb);
//      getCommand("hoe").setExecutor(this.rb);
//      getCommand("rename").setExecutor(this.rb);
//    }
    if (this.settings.getBoolean("Restart")) {
      getCommand("restart").setExecutor(this.restart);
      getCommand("ls").setExecutor(this.restart);
      getCommand("quartz").setExecutor(this.restart);
      this.restart.initRestarts();
    }
    if (this.settings.getBoolean("Runecraft")) {
      pm.registerEvents(this.run, this);
      this.run.setup();
    }
    if (this.settings.getBoolean("Timber")) {
      pm.registerEvents(this.tim, this);
    }
  }

  @Override
public void onDisable()
  {
    threadVar = false;
    try {
      System.out.println("Waiting for Exploration thread to end...");
      t.join();
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }

    if (this.settings.getBoolean("Runecraft")) {
      Runecraft_Teles.writeToFiles();
    }
    getLogger().info("Kadmin disabled");
  }

  public final boolean saveSettings() {
    if (!this.settingsFile.exists()) {
      this.settingsFile.getParentFile().mkdirs();
    }
    try {
      this.settings.save(this.settingsFile);
      return true;
    }
    catch (final IOException e) {
      e.printStackTrace();
    }
    return false;
  }
}