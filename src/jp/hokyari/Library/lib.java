package jp.hokyari.Library;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Team;

import net.minecraft.server.v1_12_R1.ChatMessage;
import net.minecraft.server.v1_12_R1.ChatMessageType;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle.EnumTitleAction;




public class lib {

	static Random rnd = new Random();

	static public void sendPlayer(Player p, String msg){
		if (p != null){
			p.sendMessage(msg);
		}else{
			Bukkit.getLogger().info(msg);
			return;
		}
	}

	public static ItemStack createEnchantItem(Material material, int am ,Enchantment enc,int Lv, String name, String... list){
		List<String> lore = Arrays.asList(list);
		ItemStack item = new ItemStack(material,am);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(enc, Lv);
		return item;
	}

	static public void Soundworld(Location loc, Player p, Sound s ,Float f ,Float volume){
		float pitch = rnd.nextInt(4)*f;
		p.getWorld().playSound(p.getLocation(), s, volume, pitch + 0.4F);
	}

	static public void SoundPlayer(Player p, Sound s , Float f){
		float pitch = rnd.nextInt(4) *f;
		p.playSound(p.getLocation(), s, 10, pitch + 0.4F);
	}

	static public void SoundAllPlayer(Sound s, Float f){
		for(Player p : Bukkit.getOnlinePlayers()){
			float pitch = rnd.nextInt(4)*f;
			p.playSound(p.getLocation(), s, 10, pitch + 0.4F);
		}
	}

	@SuppressWarnings("deprecation")
	static public void SoundTeamPlayer(Team t ,Sound s, Float f){
		for(Player p : Bukkit.getOnlinePlayers()){
			if(t.hasPlayer(p)){
				float pitch = rnd.nextInt(4)*f;
				p.playSound(p.getLocation(), s, 10, pitch + 0.4F);
			}
		}
	}

    public static void sendActionBar(Player player, String message){
        CraftPlayer p = (CraftPlayer) player;
        IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, ChatMessageType.GAME_INFO);
        p.getHandle().playerConnection.sendPacket(ppoc);
    }

	public static void sendTitle(String main, String sub) {
		for(Player players : Bukkit.getOnlinePlayers()) {
			IChatBaseComponent text = new ChatMessage(main);
			PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, text);

			IChatBaseComponent subtext = new ChatMessage(sub);
			PacketPlayOutTitle subtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subtext);

			CraftPlayer cp = (CraftPlayer) players;
			cp.getHandle().playerConnection.sendPacket(title);
			cp.getHandle().playerConnection.sendPacket(subtitle);
		}
	}

	public static void sendTitleTarget(String main, String sub, Player target) {
			IChatBaseComponent text = new ChatMessage(main);
			PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, text);

			IChatBaseComponent subtext = new ChatMessage(sub);
			PacketPlayOutTitle subtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subtext);

			CraftPlayer cp = (CraftPlayer) target;
			cp.getHandle().playerConnection.sendPacket(title);
			cp.getHandle().playerConnection.sendPacket(subtitle);
	}

	public static void sendTab(Player player, String head, String foot){
		IChatBaseComponent header = new ChatMessage(head);
		IChatBaseComponent footer = new ChatMessage(foot);
		PacketPlayOutPlayerListHeaderFooter tablist = new PacketPlayOutPlayerListHeaderFooter();
		try {
			Field headerField = tablist.getClass().getDeclaredField("a");
			headerField.setAccessible(true);
			headerField.set(tablist, header);
			headerField.setAccessible(!headerField.isAccessible());
			Field footerField = tablist.getClass().getDeclaredField("b");
			footerField.setAccessible(true);
			footerField.set(tablist, footer);
			footerField.setAccessible(!footerField.isAccessible());
		} catch (Exception e) {
			e.printStackTrace();
		}

		CraftPlayer cp = (CraftPlayer) player;
		cp.getHandle().playerConnection.sendPacket(tablist);
	}

	public static void sendBossBar(BossBar bb){

	}

	public static void Fireworks(Player p , Location l){
		Random rnd = new Random();
		Firework fw = (Firework) p.getWorld().spawnEntity(l , EntityType.FIREWORK);
		FireworkMeta fwm = fw.getFireworkMeta();

		int rt = rnd.nextInt(1) + 1;
		Type type = Type.BALL;
		if (rt == 1) type = Type.BALL;

		int color = rnd.nextInt(17) + 1;
		int color2 = rnd.nextInt(17) + 1;
		Color c = getColor(color);
		Color c2 = getColor(color2);

		FireworkEffect effect = FireworkEffect.builder().flicker(rnd.nextBoolean()).withColor(c).withFade(c2).with(type).trail(rnd.nextBoolean()).build();
		fwm.addEffect(effect);
		int rp = 0;
		fwm.setPower(rp);

		fw.setFireworkMeta(fwm);
	}

	public static Color getColor(int i) {
		Color c = null;
		if(i == 1){
			c = Color.AQUA;
		}
		if(i == 2){
			c = Color.BLACK;
		}
		if(i == 3){
			c = Color.BLUE;
		}
		if(i == 4){
			c = Color.FUCHSIA;
		}
		if(i == 5){
			c = Color.GRAY;
		}
		if(i == 6){
			c = Color.GREEN;
		}
		if(i == 7){
			c = Color.LIME;
		}
		if(i == 8){
			c = Color.MAROON;
		}
		if(i == 9){
			c = Color.NAVY;
		}
		if(i == 10){
			c = Color.OLIVE;
		}
		if(i == 11){
			c = Color.ORANGE;
		}
		if(i == 12){
			c = Color.PURPLE;
		}
		if(i == 13){
			c = Color.RED;
		}
		if(i == 14){
			c = Color.SILVER;
		}
		if(i == 15){
			c = Color.TEAL;
		}
		if(i == 16){
			c = Color.WHITE;
		}
		if(i == 17){
			c = Color.YELLOW;
		}

		return c;
	}

	public static void clearBossBar(BossBar bb){
		for(Player p : Bukkit.getOnlinePlayers()){
			bb.removePlayer(p);
			sendPlayer(null, "BossBarをリセットしました。");
		}
	}

}
