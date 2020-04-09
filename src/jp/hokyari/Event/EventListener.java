package jp.hokyari.Event;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.xxmicloxx.NoteBlockAPI.NBSDecoder;
import com.xxmicloxx.NoteBlockAPI.NoteBlockPlayerMain;
import com.xxmicloxx.NoteBlockAPI.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.Song;
import com.xxmicloxx.NoteBlockAPI.SongPlayer;

import jp.hokyari.Main;
import jp.hokyari.Library.lib;

@SuppressWarnings("deprecation")
public class EventListener implements Listener {
	Random rnd = new Random();

	private Main plugin;

	public EventListener(Main plugin) {
		this.plugin = plugin;

		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);

		return;

	}


	@EventHandler
	public void JoinEvent(PlayerJoinEvent b){
		Player p = b.getPlayer();

		if(plugin.Players.hasEntry(p.getName())){

		} else {

		}

		plugin.pm.DefaultStuff(p);

	}

	@EventHandler
	public void QuitEvent(PlayerQuitEvent b){
		Player p = b.getPlayer();

		if(plugin.Players.hasEntry(p.getName())){
			plugin.Players.removeEntry(p.getName());
		} else {

		}
	}


	@EventHandler
	public void onDropedItems(PlayerDropItemEvent b){
		if(plugin.Players.hasEntry(b.getPlayer().getName())){
			b.setCancelled(true);
		}
	}


	@EventHandler
	public void onDamage(EntityDamageEvent b){
		if(b.getEntity() instanceof Player){
			b.setCancelled(true);
			if (b.getCause() == DamageCause.VOID){
				plugin.configLocationTeleport("Lobby", (Player)b.getEntity(), true);
			}
		} else {
			return;
		}
	}



	@EventHandler
	public void onDamage(EntityDamageByEntityEvent b){

		Player deadman;
		if((Player) b.getEntity() == null){
			return;
		} else {
			 deadman = (Player) b.getEntity();
		}
		Player killer = null;

		Arrow ar;


		if(b.getDamager() instanceof Arrow){
			Projectile pj = (Projectile) b.getDamager();
			ar = (Arrow) b.getDamager();



			if(pj.getShooter() instanceof Player){
				killer = (Player) pj.getShooter();
			} else {

			}

		} else {
			b.setCancelled(true);
			return;
		}

		if(plugin.Players.hasEntry(killer.getName()) && plugin.Players.hasEntry(deadman.getName())){

			ar.remove();

			//全員への処理
			b.setCancelled(true);
			lib.SoundAllPlayer(Sound.ENTITY_ARROW_HIT_PLAYER, 20F);
			String st = "" + ChatColor.RED + "[LastManStanding] "+ ChatColor.GOLD + killer.getName() + ChatColor.GRAY + " が " + ChatColor.BLUE + deadman.getName() + ChatColor.GRAY + " を殺した。" + " - " + ChatColor.GOLD + "#" + plugin.Players.getSize() + ChatColor.RED + " " + plugin.kills.get(deadman.getName()) + ChatColor.GRAY + "kill";

			String rank = ChatColor.GOLD + "#" + plugin.Players.getSize() + ChatColor.RED + " " + plugin.kills.get(deadman.getName()) + ChatColor.GRAY + "KILL" + " - " +ChatColor.RED + deadman.getName();

			plugin.rank.add(rank);
			Bukkit.broadcastMessage(st);


			//killerの処理
			plugin.kills.put(killer.getName(), plugin.kills.get(killer.getName())+1);
			killer.getInventory().addItem(new ItemStack(Material.ARROW,2));
			killer.removePotionEffect(PotionEffectType.SPEED);
			lib.SoundPlayer(killer, Sound.ENTITY_GHAST_SHOOT, 20F);
			killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED ,20*5 ,2));

			//deadmanの処理
			lib.sendTitleTarget(ChatColor.GRAY + "#" + plugin.Players.getSize() + " / " + ChatColor.RED + plugin.kills.get(deadman.getName()) + "KILL", ChatColor.WHITE +  "次はもっと頑張ろうね^^", deadman);
			plugin.Players.removeEntry(deadman.getName());
			deadman.getInventory().clear();

			deadman.setGameMode(GameMode.SPECTATOR);
			deadman.teleport(killer.getLocation());


			//プレイヤーが一人になった処理
			if(plugin.Players.getSize() == 1){
				plugin.pm.endingGame();
			}
		}



		//Bukkit.broadcastMessage(deadman.getName() + " <- " + killer.getName());
	}


	@EventHandler
	public void onDeathEvent(PlayerDeathEvent b){
		b.setDeathMessage("");
	}

	/*
		Player deadman = b.getEntity().getPlayer();
		Player killer = b.getEntity().getKiller();

		deadman.setBedSpawnLocation(plugin.configLocation("Lobby"), true);

		Inventory inv = deadman.getInventory();
		if(killer != null){
			if(plugin.Players.hasEntry(deadman.getName())){

				if(deadman.equals(killer)){

					b.setDeathMessage(ChatColor.RED + "[LastManStanding] "+ ChatColor.BLUE + deadman.getName() + ChatColor.GRAY + " が自滅した。" + " - " + ChatColor.RED + "#" + plugin.Players.getSize());
					lib.SoundAllPlayer(Sound.ENTITY_ARROW_HIT_PLAYER, 20F);
					plugin.Players.removeEntry(deadman.getName());

				} else {

					b.setDeathMessage(ChatColor.RED + "[LastManStanding] "+ ChatColor.GOLD + killer.getName() + ChatColor.GRAY + " が " + ChatColor.BLUE + deadman.getName() + ChatColor.GRAY + " を殺した。" + " - " + ChatColor.RED + "#" + plugin.Players.getSize());

					lib.sendTitleTarget(ChatColor.GRAY + "#" + plugin.Players.getSize() + " / " + ChatColor.RED + plugin.kills.get(deadman.getName()) + "KILL", ChatColor.WHITE +  "次はもっと頑張ろうね^^", deadman);

					lib.SoundAllPlayer(Sound.ENTITY_ARROW_HIT_PLAYER, 20F);
					plugin.Players.removeEntry(deadman.getName());

					//キル1追加
					plugin.kills.put(killer.getName(), plugin.kills.get(killer.getName())+1);
					killer.getInventory().addItem(new ItemStack(Material.ARROW));
					killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED ,20*5 ,1));

				}


			} else {
				b.setDeathMessage(null);
			}

		} else {

			b.setDeathMessage(ChatColor.RED + "[LastManStanding] "+ ChatColor.BLUE + deadman.getName() + ChatColor.GRAY + " が自滅した。" + " - " + ChatColor.RED + "#" + plugin.Players.getSize());
			lib.SoundAllPlayer(Sound.ENTITY_ARROW_HIT_PLAYER, 20F);
			plugin.Players.removeEntry(deadman.getName());

		}



		inv.clear();


		if(plugin.Players.getSize() == 1){
			plugin.endingGame();
		}
	}*/


	@SuppressWarnings({ "unused", "resource" })
	@EventHandler
	public void ChatEvent(PlayerChatEvent b){
		Player p = b.getPlayer();
		String st = b.getMessage();
		//ChatColor cc;

		if(p.isOp()){
			//NoteBlockAPI
			for(Player a : Bukkit.getOnlinePlayers()){
				a.playSound(a.getLocation(), st , 20F, 1F);
			}
			try {

				File f = new File (plugin.getDataFolder(), st + ".nbs");
				FileReader filereader = new FileReader(f);

				if(f != null){
					b.setCancelled(true);
					Song son = NBSDecoder.parse(new File(plugin.getDataFolder(), st + ".nbs"));

					if(son != null){
						for(Player a : Bukkit.getOnlinePlayers()){
							if(plugin.music.contains(a)){
								SongPlayer sp = new RadioSongPlayer(son);
								sp.setAutoDestroy(true);
								sp.addPlayer(a);
								sp.setVolume((byte)100);
								sp.setPlaying(true);
								lib.sendPlayer(a, ChatColor.GREEN + " " + p.getName() + ChatColor.RESET +" Playing " + st + ".");

							}
						}
						return;
					}
				}
			} catch(FileNotFoundException n){
				//System.out.println(n);
			}

			if(b.getMessage().startsWith("nanaco")){
				b.setCancelled(true);
				Bukkit.broadcastMessage(ChatColor.GREEN + " " + p.getName() + ChatColor.RESET +" Stopping Music.");
				for(Player a : Bukkit.getOnlinePlayers()){
					NoteBlockPlayerMain.stopPlaying(a);
				}
				return;
			}
		}
	}



	@EventHandler
	public void onSignCreate(SignChangeEvent e) {

		String line1 = e.getLine(0);
		//String line2 = e.getLine(1);
		if(e.getPlayer().isOp()){

			if (line1.equals("gent")) {
				e.setLine(0, ChatColor.RED + "[GenT]");
			}


			if (line1.equals("Join")) {
				e.setLine(0, ChatColor.BOLD + "==============");
				e.setLine(1, ChatColor.BOLD + "Join to the");
				e.setLine(2, ChatColor.BOLD + "LastManStanding");
				e.setLine(3, ChatColor.BOLD + "==============");

			}
		}
	}

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Action act = e.getAction();
		Block clickedBlock = e.getClickedBlock();


		if (act == Action.RIGHT_CLICK_BLOCK) {

			if(!(clickedBlock.getType()==Material.SIGN || clickedBlock.getType()==Material.SIGN_POST || clickedBlock.getType()==Material.WALL_SIGN))
				return;
			Sign sign = (Sign) clickedBlock.getState();
			String line2 = sign.getLine(1);
			String line3 = sign.getLine(2);

			if (line2.contains(ChatColor.BOLD + "Join to the")) {
				if (line3.contains(ChatColor.BOLD + "LastManStanding")) {

					if(plugin.GamePhase == 0){
						if(plugin.Players.hasEntry(p.getName())){

							lib.sendPlayer(p, ChatColor.RED + "[LastManStanding] "+ ChatColor.GRAY + "あなたはすでにゲームに参加しています。");

						} else {

							Bukkit.broadcastMessage(ChatColor.RED + "[LastManStanding] "+ ChatColor.GOLD + p.getName() + ChatColor.GRAY +  " がゲームに参加しました。");
							plugin.Players.addEntry(p.getName());
							p.setGameMode(GameMode.ADVENTURE);
						}
					}

					if(plugin.GamePhase == 1){
						lib.sendPlayer(p, ChatColor.RED + "[LastManStanding] "+ ChatColor.GRAY + "すでにゲームが進行しています。");
						return;
					}

				}
			}
		}
	}


}
