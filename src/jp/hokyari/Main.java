package jp.hokyari;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import com.xxmicloxx.NoteBlockAPI.NoteBlockPlayerMain;

import jp.hokyari.Event.EventListener;
import jp.hokyari.Library.lib;
import jp.hokyari.Manager.PlayerManager;
import jp.hokyari.Manager.TeamManager;

public class Main extends JavaPlugin implements Listener {


	public EventListener el = null;
	public TeamManager tm = null;
	public PlayerManager pm = null;


	Random rnd = new Random();

	//ゲームに参加している人間
	public Team Players;

	//0 = ゲーム未進行 / 1 = ゲーム実施中 / 2 = ゲーム終了
	public int GamePhase = 0;

	//プレイヤーが一定数揃った状態での待ち時間
	public int WaitingTime = getConfig().getInt("WatingTime");

	//ゲームのじかん
	public int GameTime = getConfig().getInt("GameTime");

	//ゲームの時間 ひく StartTime
	public int StartTime = getConfig().getInt("StartTime");
	public int StartTime2;

	//参加最低人数
	int MinPlayer = getConfig().getInt("MinPlayers");

	public String map = "1";


	public HashMap <String, Integer> kills = new HashMap<String, Integer>();

	public ArrayList<Player> music = new ArrayList<Player>();

	public ArrayList<String> rank = new ArrayList<String>();

	public void onEnable(){

		el = new EventListener(this);
		tm = new TeamManager(this);
		pm = new PlayerManager(this);


		saveConfig();
		reloadConfig();

		Reset();
		Timer();
	}

	/*
	public void onDisable(){

	}*/

	public void Reset(){

		int map3 = rnd.nextInt(4)+1;
		String map2 = map3 + "";

		map = map2;

		tm.ScoreboardCreate();

		WaitingTime = getConfig().getInt("WatingTime");
		MinPlayer = getConfig().getInt("MinPlayers");
		GameTime = getConfig().getInt("GameTime");

		StartTime = getConfig().getInt("StartTime");
		StartTime2 = GameTime-StartTime;

		kills.clear();

		GamePhase = 0;

		System.out.println(map);

		for(Player a : Bukkit.getOnlinePlayers()){
			pm.DefaultStuff(a);
		}
	}


	@Override
	public boolean onCommand(CommandSender s, Command c, String cl, String[] args) {
		boolean ret = false;
		Player p = null;

		if (s instanceof Player) p = (Player)s;

		//float pitch = rnd.nextInt(4) * 0.2F;

		if (c.getName().equalsIgnoreCase("mj")) {
			if(music.contains(p)){
				for(Player a : Bukkit.getOnlinePlayers()){
					if(music.contains(a)){
						lib.sendPlayer(a, ChatColor.GREEN + p.getName() + ChatColor.RESET +" が音楽聴きたくないらしい");
					}
				}
				NoteBlockPlayerMain.stopPlaying(p);
				music.remove(p);
			} else {
				music.add(p);
				for(Player a : Bukkit.getOnlinePlayers()){
					if(music.contains(a)){
						lib.sendPlayer(a, ChatColor.GREEN + p.getName() + ChatColor.RESET +" が音楽聴きたいらしい");
					}
				}
				return ret;
			}
		}

		if (c.getName().equalsIgnoreCase("ls")) {

			if(!p.isOp()){
				return ret;
			}

			if(args.length > 0){
				String cmd = args[0];
				if(cmd != null){

					if(cmd.equalsIgnoreCase("int")){
						if (args.length > 1) {
							if (args.length > 2) {
								String warp = args[1];
								String n = args[2];

								getConfig().set(warp ,Integer.valueOf(n));
								saveConfig();
								reloadConfig();

								lib.sendPlayer(p, args[1] + "が"+args[2]+"にSETされました。");
							} else {
								lib.sendPlayer(p, args[1] +" の値を設定してください。");
							}
						} else {
							lib.sendPlayer(p,  args[1] +" の値を設定してください。");
						}
						return ret;
					}



					if(cmd.equalsIgnoreCase("arrow")){
						for (Arrow arrow : Bukkit.getWorld("world").getEntitiesByClass(Arrow.class)) {
							arrow.remove();
						}
						return ret;
					}

					if(cmd.equalsIgnoreCase("loc")){
						if(args.length > 1){
							String st = args[1];
							getConfig().set(st +".x" , p.getLocation().getBlockX());
							getConfig().set(st +".y" , p.getLocation().getBlockY());
							getConfig().set(st +".z" , p.getLocation().getBlockZ());
							getConfig().set(st +".world", p.getLocation().getWorld().getName());

							saveConfig();
							reloadConfig();


							lib.sendPlayer(p, st + " のLocationを設定しました。");

						} else {

						}
						return ret;
					}

					if(cmd.equalsIgnoreCase("aj")){
						for(Player a : Bukkit.getOnlinePlayers()){
							Players.addEntry(a.getName());
							a.setGameMode(GameMode.ADVENTURE);

						}
						return ret;
					}


					if(cmd.equalsIgnoreCase("tp")){
						if(args.length > 1){
							String st = args[1];
							if(getConfig().contains(st)){
								configLocationTeleport(st, p, false);
							} else {
								lib.sendPlayer(p, "s");
							}
						}
						return ret;
					}


					if(cmd.equalsIgnoreCase("kick")){
						for(Player a : Bukkit.getOnlinePlayers()){
							if(a.isOp()){

							} else {
								a.kickPlayer("SHUT THE FUCK UP");
							}
						}
						return ret;
					}


				}
			}
		}
		return ret;
	}


	public void Timer(){

		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){

			@Override
			public void run() {
				if(GamePhase == 0){

					for(Player a : Bukkit.getOnlinePlayers()){
						lib.sendActionBar(a, ChatColor.GOLD + "JOINED : " + ChatColor.RED + Players.getSize() + ChatColor.WHITE + " | " + ChatColor.GRAY + "STARTING IN " + ChatColor.RED + WaitingTime + "" + ChatColor.GRAY + " SECONDS." );
						pm.killHashMap(a);
						a.setFoodLevel(15);
					}

					if(WaitingTime == 0){
						GamePhase = 1;
						pm.startingGame();
						return;
					}
					if(Players.getSize() >= MinPlayer){
						WaitingTime = WaitingTime -1;
						lib.SoundAllPlayer(Sound.BLOCK_ANVIL_BREAK, 20f);
					}

				}

				if(GamePhase == 1){
					GameTime = GameTime-1;

					if(GameTime >= 0){

						if(GameTime == StartTime2){
							for(Player a : Bukkit.getOnlinePlayers()){
								for(Player show : Bukkit.getOnlinePlayers()){
									a.showPlayer(show);
								}

								if(Players.hasEntry(a.getName())){
									a.getInventory().addItem(pm.SUGOIYUMI);

									lib.sendPlayer(a, ChatColor.RED + "[LastManStanding] "+ ChatColor.GREEN + StartTime + ChatColor.GRAY + " 秒が経過したため、すべてのプレイヤーが表示されます。");
									lib.SoundPlayer(a,Sound.ENTITY_WITHER_SPAWN, 1f);
								}
							}
						}



						if(Players.getSize() == 1){

							pm.endingGame();

						} else {


							if(Players.getSize() == 0){
								pm.endingGame();
							}

							for(Player a : Bukkit.getOnlinePlayers()){

								/*if(a.getGameMode().equals(GameMode.SPECTATOR)){
									for(Player a){

									}
								}*/
								a.setFoodLevel(15);
								lib.sendActionBar(a, ChatColor.GRAY + "ALIVE : " + ChatColor.RED + Players.getSize() + ChatColor.WHITE + " | " + ChatColor.GRAY + "KILL : " + ChatColor.RED + kills.get(a.getName()) + ChatColor.WHITE + " | " + ChatColor.GRAY + "TIME : " + ChatColor.RED + GameTime);
							}
							return;
						}
					} else {
						for(Player a : Bukkit.getOnlinePlayers()){
							Players.removeEntry(a.getName());
						}

						pm.endingGame();
					}

				}

			}


		}, 0L, 20L);

		//10秒
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){

			@Override
			public void run() {

				if(GamePhase == 1){

					for(Player a : Bukkit.getOnlinePlayers()){
						if(Players.hasEntry(a.getName())){
							a.getInventory().addItem(new ItemStack(Material.ARROW, 1));
							//a.getInventory().addItem(new ItemStack(Material.BREAD, 1));
						}
					}
				}
			}
		}, 0L, 20L * 10);

		//30秒
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){

			@Override
			public void run() {

				if(GamePhase == 1){

					for(Player a : Bukkit.getOnlinePlayers()){
						if(Players.hasEntry(a.getName())){
							a.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING ,20 *3 ,1));
						}
					}
				}
			}




		}, 0L, 20L * 30);
	}



	public Location configLocation(String xyz) {
		int x1 = getConfig().getInt(xyz + ".x");
		int y1 = getConfig().getInt(xyz + ".y");
		int z1 = getConfig().getInt(xyz + ".z");
		String st = getConfig().getString(xyz + ".world");
		World w1 = Bukkit.getWorld(st);
		Location loc = new Location(w1 , x1 +0.5D , y1 +0.5D , z1 +0.5D);
		return loc;
	}

	public void LocationWorldTeleport(String xyz , Player p, Boolean Spawn) {
		WorldCreator uk = new WorldCreator(xyz);
		World unk = uk.createWorld();

		Location loc = p.getLocation();

		loc.setYaw(p.getLocation().getYaw());
		loc.setPitch(p.getLocation().getPitch());

		p.teleport(unk.getSpawnLocation());

		if(Spawn == true){
			p.setBedSpawnLocation(unk.getSpawnLocation(), true);
		}

		lib.SoundPlayer(p, Sound.ENTITY_ENDERMEN_TELEPORT, 0.2F);
	}



	public void configLocationTeleport(String xyz , Player p, Boolean Spawn) {
		if(getConfig().contains(xyz)){
			int x1 = getConfig().getInt(xyz + ".x");
			int y1 = getConfig().getInt(xyz + ".y");
			int z1 = getConfig().getInt(xyz + ".z");

			String st = getConfig().getString(xyz + ".world");
			World w1 = Bukkit.getWorld(st);

			Location loc = new Location(w1 , x1 +0.5D , y1 +0.5D , z1 +0.5D);

			loc.setYaw(p.getLocation().getYaw());
			loc.setPitch(p.getLocation().getPitch());
			p.teleport(loc);

			if(Spawn == true){
				p.setBedSpawnLocation(loc, true);
			}

			lib.SoundPlayer(p, Sound.ENTITY_ENDERMEN_TELEPORT, 0.2F);

		} else {

			lib.sendPlayer(p, "存在しないLocationです。 - " + xyz);

		}
	}
}