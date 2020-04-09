package jp.hokyari.Manager;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import jp.hokyari.Main;


public class TeamManager implements Listener {
	Random rnd = new Random();

	private Main plugin;

	public TeamManager(Main plugin) {
		this.plugin = plugin;

		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);

		return;

	}

	public void ScoreboardCreate(){

		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getMainScoreboard();


		plugin.Players = board.getTeam("Players");

		if(plugin.Players  != null){
			plugin.Players.unregister();
		}

		plugin.Players = board.registerNewTeam("Players");
		plugin.Players.setPrefix(ChatColor.GOLD + "[ALIVE]" + ChatColor.RESET);
		plugin.Players.setSuffix(ChatColor.RESET.toString());
		plugin.Players.setOption(Option.COLLISION_RULE , OptionStatus.NEVER);
		plugin.Players.setOption(Option.NAME_TAG_VISIBILITY , OptionStatus.NEVER);

		plugin.Players.setAllowFriendlyFire(true);
	}



}
