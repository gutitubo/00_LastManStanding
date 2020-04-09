package jp.hokyari.Manager;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import jp.hokyari.Main;
import jp.hokyari.Library.lib;


public class PlayerManager implements Listener {
	Random rnd = new Random();

	private Main plugin;

	public PlayerManager(Main plugin) {
		this.plugin = plugin;

		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);

		return;

	}

	public ItemStack SUGOIYUMI = lib.createEnchantItem(Material.BOW, 1 , Enchantment.LUCK, 5, ChatColor.GOLD + "GOLDEN GUN", ChatColor.WHITE + "すべては、君の手に掛かっている。");


	public void killHashMap(Player p){
		String name = p.getName();

		if(plugin.kills.containsKey(name)){
			return;
		} else {
			plugin.kills.put(p.getName(), 0);
		}
	}

	public void DefaultStuff(Player p){
		if(!p.getGameMode().equals(GameMode.CREATIVE)){
			Inventory inv = p.getInventory();
			plugin.configLocationTeleport("Lobby" , p , true);

			p.setGameMode(GameMode.ADVENTURE);

			p.setHealth(20);
			inv.clear();
		}
	}


	public void endingGame(){

		if(plugin.GamePhase == 1){
			for(Player a : Bukkit.getOnlinePlayers()){

				if(plugin.Players.getSize() != 0){
					if(plugin.Players.hasEntry(a.getName())){
						String endmessage = ChatColor.RED + "[LastManStanding] "+ ChatColor.GOLD + a.getName() + ChatColor.GRAY + " がゲームに勝利しました" + ChatColor.RED + " " + plugin.kills.get(a.getName()) + ChatColor.GRAY + "kill";
						new BukkitRunnable() {
							@Override
							public void run() {
								Bukkit.broadcastMessage(endmessage);
							}
						}.runTaskLater(plugin, 20L * 1);

						lib.sendTitle(ChatColor.GOLD + a.getName() + "の勝ちだ", ChatColor.RED + "" + plugin.kills.get(a.getName()) + ChatColor.GRAY + "kill");

						String rank = ChatColor.GOLD + "#" + plugin.Players.getSize() + ChatColor.RED + " " + plugin.kills.get(a.getName()) + ChatColor.GRAY + "KILL" + " - " +ChatColor.RED + a.getName();
						plugin.rank.add(rank);
					}
				} else {
					lib.sendTitleTarget(ChatColor.GOLD + "勝者無し", "", a);
				}
			}

			lib.SoundAllPlayer( Sound.ENTITY_WOLF_HOWL, 20F);

			for (Arrow arrow : Bukkit.getWorld("world").getEntitiesByClass(Arrow.class)) {
				Vector vector = arrow.getVelocity();
				if (vector.getX() != 0 || vector.getY() != 0 || vector.getZ() != 0) {
					continue;
				}

				arrow.remove();
			}
			plugin.GamePhase = 2;

			RankPop();
			plugin.Reset();
		}

	}

	public void RankPop(){

		Bukkit.broadcastMessage("==========================");
		Bukkit.broadcastMessage("");

		for(int i = 0; i < plugin.rank.size(); i++){
			Bukkit.broadcastMessage(plugin.rank.get(i));
		}
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage("==========================");
	}


	public void startingGame(){
		plugin.rank.clear();
		Bukkit.broadcastMessage(ChatColor.RED + "[LastManStanding] "+ ChatColor.GREEN + plugin.StartTime + ChatColor.GRAY + " 秒後に弓が配布され、すべてのプレイヤーが見えるようになります。");

		for(Player a : Bukkit.getOnlinePlayers()){
			if(plugin.Players.hasEntry(a.getName())){
				PlayersStuff(a);
				int rn = rnd.nextInt(16)+1;
				String teleportloc = plugin.map + "-" + rn;
				plugin.configLocationTeleport(teleportloc , a , false);
			}
		}

		for(Player a : Bukkit.getOnlinePlayers()){
			if(plugin.Players.hasEntry(a.getName())){
				for(Player hide : Bukkit.getOnlinePlayers()){
					a.hidePlayer(hide);
					a.setWalkSpeed(0.5f);
				}
			}
		}
	}



	public void PlayersStuff(Player p){
		Inventory inv = p.getInventory();
		inv.clear();


		p.setHealth(20);
		p.setFoodLevel(20);
	}


}
