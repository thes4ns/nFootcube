package io.github.divinerealms.footcube.todo;

import io.github.divinerealms.footcube.managers.UtilManager;
import io.github.divinerealms.footcube.utils.Logger;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.Collection;

public class Organization {
  @Getter private final Plugin plugin;
  @Getter private final Logger logger;
 /* @Getter private final int setupType = 0, lobby2v2 = 0, lobby3v3 = 0, lobby4v4 = 0; */
  public String pluginString;
  
  private String adminString;
  
  private String or;
  
  private String setupGuy;
  
  private Location setupLoc;
  
  private Match[] matches2v2;
  
  private Match[] matches3v3;
  
  private Match[] matches4v4;
  
  private int lobby2v2;
  
  private int lobby3v3;
  
  private int lobby4v4;
  
  public ArrayList<Slime> practiceBalls;
  
  public HashMap<String, Integer> waitingPlayers;
  
  public ArrayList<String> playingPlayers;
  
  private HashMap<Player, Player> team;
  
  private HashMap<Player, Player> teamReverse;
  
  private HashMap<Player, Integer> teamType;
  
  private Player[][] waitingTeams;
  
  private ArrayList<Player> waitingTeamPlayers;
  
  private Match[] leftMatches;
  
  private boolean[] leftPlayerIsRed;
  
  private long announcementTime;
 
  /*@Getter private final Location setupLocation;
  @Getter private final ArrayList<Slime> practiceCubes;
  @Getter private final HashMap<Player, Player> firstTeam, secondTeam;*/
  @Getter @Setter private static Economy econ = null;

  public Organization(final Plugin plugin, final UtilManager utilManager) {
    this.pluginString = ChatColor.translateAlternateColorCodes('&', "&b&lFUT &8");
    this.adminString = ChatColor.translateAlternateColorCodes('&', "&b&lFUT &c&lAdmin &8");
    this.or = ChatColor.YELLOW + "|" + ChatColor.AQUA;
    this.setupGuy = null;
    this.setupType = 0;
    this.setupLoc = null;
    this.matches2v2 = new Match[0];
    this.matches3v3 = new Match[0];
    this.matches4v4 = new Match[0];
    this.lobby2v2 = 0;
    this.lobby3v3 = 0;
    this.lobby4v4 = 0;
    this.practiceBalls = new ArrayList<>();
    this.waitingPlayers = new HashMap<>();
    this.playingPlayers = new ArrayList<>();
    this.team = new HashMap<>();
    this.teamReverse = new HashMap<>();
    this.teamType = new HashMap<>();
    this.waitingTeams = new Player[0][0];
    this.waitingTeamPlayers = new ArrayList<>();
    this.leftMatches = new Match[0];
    this.leftPlayerIsRed = new boolean[0];
    this.economy = null;
    this.plugin = pl;
    this.disableCommands = new DisableCommands(this.plugin, this);
    this.plugin.getServer().getPluginManager().registerEvents(this, (Plugin)this.plugin);
    FileConfiguration cfg = this.plugin.getConfig();
    cfg.addDefault("arenas.2v2.amount", Integer.valueOf(0));
    cfg.addDefault("arenas.3v3.amount", Integer.valueOf(0));
    cfg.addDefault("arenas.4v4.amount", Integer.valueOf(0));
    cfg.options().copyDefaults(true);
    this.plugin.saveConfig();
    loadArenas(cfg);
    this.plugin = plugin;
    this.logger = utilManager.getLogger();
    if (!setupEconomy()) getLogger().info("&cVault not found, plugin won't use economy.");
  }

 public void command(CommandSender sender, Command cmd, String c, String[] args) {
    this.disableCommands.command(sender, cmd, c, args);
    Player p = (Player)sender;
    if (cmd.getName().equalsIgnoreCase("tc")) {
      String message = "";
      byte b;
      int j;
      String[] arrayOfString;
      for (j = (arrayOfString = args).length, b = 0; b < j; ) {
        String s = arrayOfString[b];
        message = String.valueOf(String.valueOf(String.valueOf(String.valueOf(message)))) + s + " ";
        b = (byte)(b + 1);
      } 
      Match[] matches2v2;
      for (int length3 = (matches2v2 = this.matches2v2).length, n = 0; n < length3; n++) {
        Match m = matches2v2[n];
        m.teamchat(p, message);
      } 
      Match[] matches3v3;
      for (int i = (matches3v3 = this.matches3v3).length, k = 0; k < i; k++) {
        Match m = matches3v3[k];
        m.teamchat(p, message);
      } 
      Match[] matches4v4;
      for (int length4 = (matches4v4 = this.matches4v4).length, n2 = 0; n2 < length4; n2++) {
        Match m = matches4v4[n2];
        m.teamchat(p, message);
      } 
    } 
    if (cmd.getName().equalsIgnoreCase("footcube")) {
      boolean success = true;
      if (args.length < 1) {
        success = false;
      } else if (args[0].equalsIgnoreCase("join")) {
        if (this.waitingPlayers.containsKey(p.getName()) || this.playingPlayers.contains(p.getName())) {
          p.sendMessage(ChatColor.RED + "Vec ste u utakmici...");
        } else if (this.waitingTeamPlayers.contains(p)) {
          p.sendMessage(ChatColor.RED + "Vec ste u timu, ne mozete uci u utakmicu...");
        } else if (args.length < 2) {
          p.sendMessage(ChatColor.RED + "Molimo vas da odaberete arenu...");
          p.sendMessage(ChatColor.DARK_AQUA + "/fc join [2v2, 3v3 or 4v4]");
        } else if (args[1].equalsIgnoreCase("2v2")) {
          this.matches2v2[this.lobby2v2].join(p, false);
          this.waitingPlayers.put(p.getName(), Integer.valueOf(2));
          removeTeam(p);
        } else if (args[1].equalsIgnoreCase("3v3")) {
          this.matches3v3[this.lobby3v3].join(p, false);
          this.waitingPlayers.put(p.getName(), Integer.valueOf(3));
          removeTeam(p);
        } else if (args[1].equalsIgnoreCase("4v4")) {
          this.matches4v4[this.lobby4v4].join(p, false);
          this.waitingPlayers.put(p.getName(), Integer.valueOf(4));
          removeTeam(p);
        } else {
          p.sendMessage(ChatColor.RED + args[1] + " nije pravi tip arene);
          p.sendMessage(ChatColor.DARK_AQUA + "/fc join [2v2, 3v3 or 4v4]");
        } 
      } else if (args[0].equalsIgnoreCase("team")) {
        if (args.length < 2) {
          p.sendMessage(ChatColor.DARK_AQUA + "/fc team [2v2, 3v3, 4v4] [ime igraca]");
          p.sendMessage(ChatColor.DARK_AQUA + "/fc team accept/decline/cancel");
        } else if (args[1].equalsIgnoreCase("2v2") || args[1].equalsIgnoreCase("3v3") || args[1].equalsIgnoreCase("4v4")) {
          if (this.waitingPlayers.containsKey(p.getName()) || this.playingPlayers.contains(p.getName())) {
            p.sendMessage(ChatColor.GRAY + "Ne mozete poslati zahtjev za tim, ako ste u utakmici...");
          } else if (this.waitingTeamPlayers.contains(p)) {
            p.sendMessage(ChatColor.RED + "Vi ste vec u utakmici...");
          } else if (this.team.containsKey(p)) {
            String matchType = (new StringBuilder()).append(this.teamType.get(this.team.get(p))).append("v").append(this.teamType.get(this.team.get(p))).toString();
            p.sendMessage(ChatColor.DARK_AQUA + "Vec imate zahtjev za tim od " + ChatColor.AQUA + ((Player)this.team.get(p)).getName() + ChatColor.DARK_AQUA + " za  " + matchType + " utakmicu...");
            p.sendMessage(ChatColor.AQUA + "/fc team accept" + ChatColor.DARK_AQUA + " ili " + ChatColor.RED + "/fc team decline" + ChatColor.DARK_AQUA + " da odgovorite na zahtjev za tim...");
          } else if (this.teamReverse.containsKey(p)) {
            String matchType = (new StringBuilder()).append(this.teamType.get(p)).append("v").append(this.teamType.get(p)).toString();
            p.sendMessage(ChatColor.DARK_AQUA + "Vec ste poslali zahtjev za tim  " + ((Player)this.teamReverse.get(p)).getName() + " za " + matchType + " utakmicu");
          } else if (args.length < 3) {
            p.sendMessage(ChatColor.DARK_AQUA + "/fc team " + args[1] + " [ime igraca]");
          } else if (isOnlinePlayer(args[2])) {
            Player player = this.plugin.getServer().getPlayer(args[2]);
            if (this.waitingTeamPlayers.contains(player)) {
              p.sendMessage(ChatColor.RED + args[2] + " je vec u timu...");
            } else if (this.waitingPlayers.containsKey(player.getName()) || this.playingPlayers.contains(player.getName())) {
              p.sendMessage(ChatColor.RED + args[2] + " vec igra utakmicu...");
            } else if (this.team.containsKey(player)) {
              p.sendMessage(ChatColor.AQUA + args[2] + " je vec dobio zahtjev za tim");
            } else if (this.teamReverse.containsKey(player)) {
              p.sendMessage(ChatColor.AQUA + args[2] + " zahtjev za tim je poslat nekom drugom igracu...");
            } else {
              this.team.put(player, p);
              this.teamReverse.put(p, player);
              int matchType2 = 2;
              if (args[1].equalsIgnoreCase("3v3"))
                matchType2 = 3; 
              if (args[1].equalsIgnoreCase("4v4"))
                matchType2 = 4; 
              this.teamType.put(p, Integer.valueOf(matchType2));
              player.sendMessage(String.valueOf(String.valueOf(String.valueOf(String.valueOf(this.pluginString)))) + ChatColor.AQUA + p.getName() + ChatColor.GRAY + " zeli biti tim sa vama u " + ChatColor.DARK_AQUA + matchType2 + "v" + ChatColor.DARK_AQUA + matchType2 + ChatColor.GRAY + " u utakmici.");
              player.sendMessage(ChatColor.AQUA + "/fc team accept" + ChatColor.GRAY + " ili " + ChatColor.RED + "/fc team decline" + ChatColor.GRAY + " da odgovorite na zahtjev za tim...");
              p.sendMessage(String.valueOf(String.valueOf(String.valueOf(String.valueOf(this.pluginString)))) + ChatColor.GRAY + "Uspjesno ste poslali igracu " + ChatColor.AQUA + player.getName() + ChatColor.GRAY + " zahtjev za tim u " + ChatColor.DARK_AQUA + matchType2 + "v" + ChatColor.DARK_AQUA + matchType2 + ChatColor.GRAY + " utakmici.");
              p.sendMessage(ChatColor.RED + "/fc team cancel" + ChatColor.GRAY + " da odbijete zahtjev...");
            } 
          } else {
            p.sendMessage(ChatColor.GRAY + "Igrac" + ChatColor.AQUA + args[2] + ChatColor.GRAY + " nije online...");
          } 
        } else if (args[1].equalsIgnoreCase("cancel")) {
          if (this.teamReverse.containsKey(p)) {
            Player player = this.teamReverse.get(p);
            player.sendMessage(String.valueOf(String.valueOf(String.valueOf(String.valueOf(this.pluginString)))) + ChatColor.AQUA + p.getName() + ChatColor.GRAY + " je otkazao zahtjev za tim...");
            p.sendMessage(String.valueOf(String.valueOf(String.valueOf(String.valueOf(this.pluginString)))) + ChatColor.DARK_AQUA + "Uspjesno ste otkazali zahtjev za tim...");
            this.teamType.remove(p);
            this.teamReverse.remove(p);
            this.team.remove(player);
          } else {
            p.sendMessage(ChatColor.RED + "Niste poslali zahtjev za tim...");
          } 
        } else if (args[1].equalsIgnoreCase("accept")) {
          if (this.team.containsKey(p)) {
            Player player = this.team.get(p);
            if (((Integer)this.teamType.get(player)).intValue() == 2) {
              this.waitingPlayers.put(p.getName(), Integer.valueOf(2));
              this.waitingPlayers.put(player.getName(), Integer.valueOf(2));
              if (!this.matches2v2[this.lobby2v2].team(p, player)) {
                this.waitingPlayers.remove(p.getName());
                this.waitingPlayers.remove(player.getName());
                this.waitingTeams = extendArray(this.waitingTeams, new Player[] { p, player });
                this.waitingTeamPlayers.add(p);
                this.waitingTeamPlayers.add(player);
                p.sendMessage(String.valueOf(String.valueOf(String.valueOf(String.valueOf(this.pluginString)))) + ChatColor.GRAY + "U tim ste upareni sa igracem " + ChatColor.AQUA + player.getName());
                p.sendMessage(ChatColor.GRAY + "Morate sacekati da dobijete mjesto u timu, nece dugo potrajati!");
                player.sendMessage(String.valueOf(String.valueOf(String.valueOf(String.valueOf(this.pluginString)))) + ChatColor.GRAY + "U tim ste upareni sa igracem " + ChatColor.AQUA + p.getName());
                player.sendMessage(ChatColor.GRAY + "Morate sacekati da dobijete mjesto u timu, ovo ce trajati kratko...");
              } 
            } else if (((Integer)this.teamType.get(player)).intValue() == 3) {
              this.waitingPlayers.put(p.getName(), Integer.valueOf(3));
              this.waitingPlayers.put(player.getName(), Integer.valueOf(3));
              if (!this.matches3v3[this.lobby3v3].team(p, player)) {
                this.waitingPlayers.remove(p.getName());
                this.waitingPlayers.remove(player.getName());
                this.waitingTeams = extendArray(this.waitingTeams, new Player[] { p, player });
                this.waitingTeamPlayers.add(p);
                this.waitingTeamPlayers.add(player);
                p.sendMessage(String.valueOf(String.valueOf(String.valueOf(String.valueOf(this.pluginString)))) + ChatColor.GRAY + "U tim ste upareni sa igracem " + ChatColor.AQUA + player.getName());
                p.sendMessage(ChatColor.GRAY + "Morate sacekati da dobijete mjesto u timu, nece dugo potrajati!");
                player.sendMessage(String.valueOf(String.valueOf(String.valueOf(String.valueOf(this.pluginString)))) + ChatColor.GRAY + "U tim ste upareni sa igracem " + ChatColor.AQUA + p.getName());
                player.sendMessage(ChatColor.GRAY + "Morate sacekati da dobijete mjesto u timu, ovo ce trajati kratko...");
              } 
            } else {
              this.waitingPlayers.put(p.getName(), Integer.valueOf(4));
              this.waitingPlayers.put(player.getName(), Integer.valueOf(4));
              if (!this.matches4v4[this.lobby4v4].team(p, player)) {
                this.waitingPlayers.remove(p.getName());
                this.waitingPlayers.remove(player.getName());
                this.waitingTeams = extendArray(this.waitingTeams, new Player[] { p, player });
                this.waitingTeamPlayers.add(p);
                this.waitingTeamPlayers.add(player);
                p.sendMessage(String.valueOf(String.valueOf(String.valueOf(String.valueOf(this.pluginString)))) + ChatColor.GRAY + "U tim ste upareni sa igracem " + ChatColor.AQUA + player.getName());
                p.sendMessage(ChatColor.GRAY + "Morate sacekati da dobijete mjesto u timu, nece dugo potrajati!");
                player.sendMessage(String.valueOf(String.valueOf(String.valueOf(String.valueOf(this.pluginString)))) + ChatColor.GRAY + "U tim ste upareni sa igracem " + ChatColor.AQUA + p.getName());
                player.sendMessage(ChatColor.GRAY + "Morate sacekati da dobijete mjesto u timu, ovo ce trajati kratko...");
              } 
            } 
            this.team.remove(p);
            this.teamReverse.remove(player);
            this.teamType.remove(player);
          } else {
            p.sendMessage(ChatColor.RED + "There is no team request to accept");
          } 
        } else if (args[1].equalsIgnoreCase("decline")) {
          if (this.team.containsKey(p)) {
            Player player = this.team.get(p);
            player.sendMessage(String.valueOf(String.valueOf(String.valueOf(String.valueOf(this.pluginString)))) + ChatColor.RED + p.getName() + ChatColor.GRAY + " je odbio vas zahtjev za tim...");
            p.sendMessage(String.valueOf(String.valueOf(String.valueOf(String.valueOf(this.pluginString)))) + ChatColor.GRAY + "Uspjesno ste odbili zahtjev za tim...");
            this.teamType.remove(player);
            this.teamReverse.remove(player);
            this.team.remove(p);
          } else {
            p.sendMessage(ChatColor.DARK_AQUA + "Nemas zahtjeva za tim koje bi mogao odbiti...");
          } 
        } else {
          p.sendMessage(ChatColor.DARK_AQUA + "/fc team [3v3" + this.or + ChatColor.DARK_AQUA + ",4v4] [ime igraca]");
          p.sendMessage(ChatColor.DARK_AQUA + "/fc team accept/decline/cancel");
        } 
      } else if (args[0].equalsIgnoreCase("takeplace")) {
        if (this.leftMatches.length > 0) {
          if (this.waitingPlayers.containsKey(p.getName()) || this.playingPlayers.contains(p.getName())) {
            p.sendMessage(ChatColor.AQUA + " " + ChatColor.BOLD + "FUT" + ChatColor.DARK_GRAY + " + ChatColor.GRAY + " Vec se nalazite u utakmici!");
          } else {
            this.leftMatches[0].takePlace(p);
            this.playingPlayers.add(p.getName());
            Match[] newL = new Match[this.leftMatches.length - 1];
            boolean[] newB = new boolean[this.leftMatches.length - 1];
            for (int i = 0; i < newL.length; i++) {
              newL[i] = this.leftMatches[i + 1];
              newB[i] = this.leftPlayerIsRed[i + 1];
            } 
            this.leftMatches = newL;
            this.leftPlayerIsRed = newB;
          } 
        } else {
          p.sendMessage(ChatColor.AQUA + " " + ChatColor.BOLD + "FUT" + ChatColor.DARK_GRAY + " + ChatColor.GRAY + " Nema mjesta koje bi mogli preuzeti, ostajete na klupi :(");
        } else if (args[0].equalsIgnoreCase("leave")) {
        if (this.waitingPlayers.containsKey(p.getName())) {
          if (((Integer)this.waitingPlayers.get(p.getName())).intValue() == 2) {
            this.matches2v2[this.lobby2v2].leave(p);
            this.waitingPlayers.remove(p.getName());
            int team = -1;
            for (int j = 0; j < this.waitingTeams.length; j++) {
              if ((this.waitingTeams[j]).length > 1) {
                team = j;
                break;
              } 
            } 
            if (team > -1 && this.matches2v2[this.lobby2v2].team(this.waitingTeams[team][0], this.waitingTeams[team][1])) {
              this.waitingTeamPlayers.remove(this.waitingTeams[team][0]);
              this.waitingTeamPlayers.remove(this.waitingTeams[team][1]);
              reduceArray(this.waitingTeams, this.waitingTeams[team][0]);
            } 
          } 
          if (((Integer)this.waitingPlayers.get(p.getName())).intValue() == 3) {
            this.matches3v3[this.lobby3v3].leave(p);
            this.waitingPlayers.remove(p.getName());
            int team = -1;
            for (int j = 0; j < this.waitingTeams.length; j++) {
              if ((this.waitingTeams[j]).length > 2) {
                team = j;
                break;
              } 
            } 
            if (team > -1 && this.matches3v3[this.lobby3v3].team(this.waitingTeams[team][0], this.waitingTeams[team][1])) {
              this.waitingTeamPlayers.remove(this.waitingTeams[team][0]);
              this.waitingTeamPlayers.remove(this.waitingTeams[team][1]);
              reduceArray(this.waitingTeams, this.waitingTeams[team][0]);
            } 
          } else {
            this.matches4v4[this.lobby4v4].leave(p);
            this.waitingPlayers.remove(p.getName());
            int team = -1;
            for (int j = 0; j < this.waitingTeams.length; j++) {
              if ((this.waitingTeams[j]).length < 3) {
                team = j;
                break;
              } 
            } 
            if (team > -1 && this.matches4v4[this.lobby4v4].team(this.waitingTeams[team][0], this.waitingTeams[team][1])) {
              this.waitingTeamPlayers.remove(this.waitingTeams[team][0]);
              this.waitingTeamPlayers.remove(this.waitingTeams[team][1]);
              reduceArray(this.waitingTeams, this.waitingTeams[team][0]);
            } 
          } 
        } else if (!this.playingPlayers.contains(p.getName())) {
          p.sendMessage(ChatColor.AQUA + " " + ChatColor.BOLD + "FUT" + ChatColor.DARK_GRAY + " + ChatColor.GRAY + " Ne nalazite se u utakmici");
        } else {
          p.sendMessage(ChatColor.AQUA + " " + ChatColor.BOLD + "FUT" + ChatColor.DARK_GRAY + " + ChatColor.GRAY + " Ne mozete izaci jer je utakmica pocela");
        } 
      } else if (args[0].equalsIgnoreCase("setuparena") && p.hasPermission("nfootcube.admin")) {
        if (this.setupGuy == null) {
          if (args.length < 2) {
            p.sendMessage(ChatColor.AQUA + ChatColor.BOLD + "FUT" + ChatColor.DARK_GRAY + " + ChatColor.RED + " Morate navesti vrstu arene...");
            p.sendMessage(ChatColor.GRAY + "/fc setuparena [3v3" + this.or + "4v4]");
          } else {
            if (args[1].equalsIgnoreCase("2v2"))
              this.setupType = 2; 
            if (args[1].equalsIgnoreCase("3v3")) {
              this.setupType = 3;
            } else if (args[1].equalsIgnoreCase("4v4")) {
              this.setupType = 4;
            } else {
              p.sendMessage(ChatColor.AQUA + " " + ChatColor.BOLD + "FUT" + ChatColor.DARK_GRAY + " + ChatColor.RED + args[1] + " nije vrsta arene...");
              p.sendMessage(ChatColor.GRAY + "/fc setuparena [2v2, 3v3 or 4v4]");
            } 
            if (this.setupType > 0) {
              this.setupGuy = p.getName();
              p.sendMessage(String.valueOf(String.valueOf(String.valueOf(String.valueOf(this.pluginString)))) + ChatColor.GREEN + "You just started to setup an arena");
              p.sendMessage(ChatColor.AQUA + " " + ChatColor.BOLD + "FUT" + ChatColor.DARK_GRAY + " + ChatColor.GRAY + " Ako ste negdje zabrljali koristite " + ChatColor.DARK_AQUA + "/fc undo");
              p.sendMessage(ChatColor.AQUA + " " + ChatColor.BOLD + "FUT" + ChatColor.DARK_GRAY + " + ChatColor.GRAY + " Korak 1: Stanite na sredinu bloka iza linije " + "plavog gola, gledajte prema crvenom i zatim koristite komandu " + ChatColor.DARK_AQUA + "/fc set");
            } 
          } 
        } else {
          p.sendMessage(ChatColor.AQUA + " " + ChatColor.BOLD + "FUT" + ChatColor.DARK_GRAY + " + ChatColor.AQUA + this.setupGuy + ChatColor.GRAY + " vec radi arene...");
        } 
      } else if (args[0].equalsIgnoreCase("cleararenas") && p.hasPermission("nfootcube.admin")) {
        FileConfiguration cfg = this.plugin.getConfig();
        cfg.set("arenas", null);
        cfg.addDefault("arenas.2v2.amount", Integer.valueOf(0));
        cfg.addDefault("arenas.3v3.amount", Integer.valueOf(0));
        cfg.addDefault("arenas.4v4.amount", Integer.valueOf(0));
        cfg.options().copyDefaults(true);
        this.plugin.saveConfig();
        this.matches2v2 = new Match[0];
        this.matches3v3 = new Match[0];
        this.matches4v4 = new Match[0];
        p.sendMessage(String.valueOf(String.valueOf(String.valueOf(String.valueOf(this.pluginString)))) + ChatColor.GREEN + " Uspjesno sve obrisali sve arene");
      } else if (args[0].equalsIgnoreCase("set") && this.setupGuy == p.getName()) {
        if (this.setupLoc == null) {
          this.setupLoc = p.getLocation();
          p.sendMessage(String.valueOf(String.valueOf(String.valueOf(String.valueOf(this.pluginString)))) + ChatColor.GREEN + " Prva lokacija je uspjesno postavljena.");
          p.sendMessage(ChatColor.AQUA + ChatColor.BOLD + "FUT" + ChatColor.DARK_GRAY + " + ChatColor.GRAY + " Ucinite isto za crveni gol...");
        } else {
          FileConfiguration cfg = this.plugin.getConfig();
          String v = String.valueOf(String.valueOf(String.valueOf(String.valueOf(this.setupType)))) + "v" + this.setupType;
          int arena = cfg.getInt("arenas." + v + ".amount") + 1;
          String blue = "arenas." + v + "." + arena + ".blue.";
          String red = "arenas." + v + "." + arena + ".red.";
          Location b = this.setupLoc;
          Location r = p.getLocation();
          cfg.set("arenas." + v + ".amount", Integer.valueOf(arena));
          cfg.set("arenas.world", p.getWorld().getName());
          cfg.set(String.valueOf(String.valueOf(String.valueOf(String.valueOf(blue)))) + "x", Double.valueOf(b.getX()));
          cfg.set(String.valueOf(String.valueOf(String.valueOf(String.valueOf(blue)))) + "y", Double.valueOf(b.getY()));
          cfg.set(String.valueOf(String.valueOf(String.valueOf(String.valueOf(blue)))) + "z", Double.valueOf(b.getZ()));
          cfg.set(String.valueOf(String.valueOf(String.valueOf(String.valueOf(blue)))) + "pitch", Float.valueOf(b.getPitch()));
          cfg.set(String.valueOf(String.valueOf(String.valueOf(String.valueOf(blue)))) + "yaw", Float.valueOf(b.getYaw()));
          cfg.set(String.valueOf(String.valueOf(String.valueOf(String.valueOf(red)))) + "x", Double.valueOf(r.getX()));
          cfg.set(String.valueOf(String.valueOf(String.valueOf(String.valueOf(red)))) + "y", Double.valueOf(r.getY()));
          cfg.set(String.valueOf(String.valueOf(String.valueOf(String.valueOf(red)))) + "z", Double.valueOf(r.getZ()));
          cfg.set(String.valueOf(String.valueOf(String.valueOf(String.valueOf(red)))) + "pitch", Float.valueOf(r.getPitch()));
          cfg.set(String.valueOf(String.valueOf(String.valueOf(String.valueOf(red)))) + "yaw", Float.valueOf(r.getYaw()));
          this.plugin.saveConfig();
          addArena(this.setupType, b, r);
          this.setupGuy = null;
          this.setupType = 0;
          this.setupLoc = null;
          p.sendMessage(String.valueOf(String.valueOf(String.valueOf(String.valueOf(this.pluginString)))) + ChatColor.GRAY + "Uspjesno ste postavili arenu !");
        } 
      } else {
        success = false;
      } 
      if (!success) {
        p.sendMessage(String.valueOf(String.valueOf(String.valueOf(String.valueOf(this.pluginString)))) + ChatColor.GRAY + "Lista komanda sa" + ChatColor.AQUA + " /fc");
        p.sendMessage(ChatColor.GRAY + "/fc join [2v2, 3v3 or 4v4]");
        p.sendMessage(ChatColor.GRAY + "/fc team [2v2, 3v3 or 4v4] [ime igraca]");
        p.sendMessage(ChatColor.GRAY + "/fc team accept/decline/cancel");
        p.sendMessage(ChatColor.GRAY + "/fc group");
        p.sendMessage(ChatColor.GRAY + "/fc takeplace");
        p.sendMessage(ChatColor.GRAY + "/fc stats");
        p.sendMessage(ChatColor.GRAY + "/fc store");
        p.sendMessage(ChatColor.GRAY + "/fc best");
        if (p.hasPermission("nfootcube.admin")) {
          p.sendMessage(String.valueOf(String.valueOf(String.valueOf(String.valueOf(this.adminString)))) + ChatColor.DARK_AQUA + "/fc setuparena [2v2, 3v3 or 4v4]");
          p.sendMessage(String.valueOf(String.valueOf(String.valueOf(String.valueOf(this.adminString)))) + ChatColor.DARK_AQUA + "/fc cleararenas");
        } 
      } 
    } 
  }

  /*private void removeTeam(final Player player) {
    if (getFirstTeam().containsKey(player)) {

    } else if (getSecondTeam().containsKey(player)) {

    }
  }*/

  public boolean isPlayerOnline(final Player player) {
    final Collection<? extends Player> onlinePlayers = getPlugin().getServer().getOnlinePlayers();
    return onlinePlayers.contains(player);
  }

  private void addArena(final int type, final Location firstTeam, final Location secondTeam) {
    final Location center = firstTeam.add(firstTeam.subtract(secondTeam).multiply(0.5));

  }

  private void loadArenas(FileConfiguration cfg) {
    int i;
    for (i = 1; i <= cfg.getInt("arenas.2v2.amount"); i++) {
      World world = this.plugin.getServer().getWorld(cfg.getString("arenas.world"));
      String blue = "arenas.2v2." + i + ".blue.";
      String red = "arenas.2v2." + i + ".red.";
      Location b = new Location(world, cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(blue)))) + "x"), cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(blue)))) + "y"), cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(blue)))) + "z"));
      b.setPitch((float)cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(blue)))) + "pitch"));
      b.setYaw((float)cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(blue)))) + "yaw"));
      Location r = new Location(world, cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(red)))) + "x"), cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(red)))) + "y"), cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(red)))) + "z"));
      r.setPitch((float)cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(red)))) + "pitch"));
      r.setYaw((float)cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(red)))) + "yaw"));
      addArena(2, b, r);
    } 
    for (i = 1; i <= cfg.getInt("arenas.3v3.amount"); i++) {
      World world = this.plugin.getServer().getWorld(cfg.getString("arenas.world"));
      String blue = "arenas.3v3." + i + ".blue.";
      String red = "arenas.3v3." + i + ".red.";
      Location b = new Location(world, cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(blue)))) + "x"), cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(blue)))) + "y"), cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(blue)))) + "z"));
      b.setPitch((float)cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(blue)))) + "pitch"));
      b.setYaw((float)cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(blue)))) + "yaw"));
      Location r = new Location(world, cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(red)))) + "x"), cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(red)))) + "y"), cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(red)))) + "z"));
      r.setPitch((float)cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(red)))) + "pitch"));
      r.setYaw((float)cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(red)))) + "yaw"));
      addArena(3, b, r);
    } 
    for (i = 1; i <= cfg.getInt("arenas.4v4.amount"); i++) {
      World world = this.plugin.getServer().getWorld(cfg.getString("arenas.world"));
      String blue = "arenas.4v4." + i + ".blue.";
      String red = "arenas.4v4." + i + ".red.";
      Location b = new Location(world, cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(blue)))) + "x"), cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(blue)))) + "y"), cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(blue)))) + "z"));
      b.setPitch((float)cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(blue)))) + "pitch"));
      b.setYaw((float)cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(blue)))) + "yaw"));
      Location r = new Location(world, cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(red)))) + "x"), cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(red)))) + "y"), cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(red)))) + "z"));
      r.setPitch((float)cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(red)))) + "pitch"));
      r.setYaw((float)cfg.getDouble(String.valueOf(String.valueOf(String.valueOf(String.valueOf(red)))) + "yaw"));
      addArena(4, b, r);
    } 
  }

  private boolean setupEconomy() {
    final Server server = getPlugin().getServer();
    if (server.getPluginManager().getPlugin("Vault") == null) return false;
    RegisteredServiceProvider<Economy> rsp = server.getServicesManager().getRegistration(Economy.class);
    if (rsp == null) return false;
    setEcon(rsp.getProvider());
    return getEcon() != null;
  }
}
