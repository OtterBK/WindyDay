package windyday.windyday;


import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;

public class MyUtility {

	public static int getRandom(int min, int max) {
		return (int)(Math.random() * (max - min + 1) + min);
	}

	public static Color getRandomColor() {
		int rn = getRandom(0, 11);
		switch(rn) {
			case 0: return Color.AQUA;
			case 1: return Color.BLACK;
			case 2: return Color.BLUE;
			case 3: return Color.GREEN;
			case 4: return Color.GRAY;
			case 5: return Color.LIME;
			case 6: return Color.NAVY;
			case 7: return Color.ORANGE;
			case 8: return Color.PURPLE;
			case 9: return Color.RED;
			case 10: return Color.WHITE;
			case 11: return Color.YELLOW;
			default: return Color.AQUA;
		}
	}

	public static void sendMessageForAllPlayer(String message){
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()){
			onlinePlayer.sendMessage(message);
		}
	}

	public static void playSoundForAllPlayer(Sound sound, float volume, float pitch){

		for(Player onlinePlayer : Bukkit.getOnlinePlayers()){
			onlinePlayer.playSound(onlinePlayer.getLocation(), sound, volume, pitch);
		}

	}

	public static void removeItem(Player p, Material itemMt, int amt) {
		for (int i = 0; i < p.getInventory().getSize(); i++) {
			if (amt > 0) {
				ItemStack pitem = p.getInventory().getItem(i);
				if (pitem != null && pitem.getType() == itemMt) {
					if (pitem.getAmount() >= amt) {
						int itemamt = pitem.getAmount() - amt;
						pitem.setAmount(itemamt);
						p.getInventory().setItem(i, amt > 0 ? pitem : null);
						p.updateInventory();
						return;
					} else {
						amt -= pitem.getAmount();
						p.getInventory().setItem(i, null);
						p.updateInventory();
					}
				}
			} else {
				return;
			}
		}
	}

	public static void spreadEffect(Plugin plugin, Location l){

		for(int i = 0; i < 4; i ++){
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, ()->{
				Location tmpL = l.clone();

				tmpL.add(MyUtility.getRandom(-3, 3), MyUtility.getRandom(-3, 3), MyUtility.getRandom(-3, 3));
				Firework fw = (Firework) tmpL.getWorld().spawnEntity(tmpL, EntityType.FIREWORK);
				FireworkMeta fwm = fw.getFireworkMeta();

				fwm.setPower(1);
				fwm.addEffect(FireworkEffect.builder().withColor(MyUtility.getRandomColor()).flicker(true).build());

				fw.setFireworkMeta(fwm);
				fw.detonate();

//                Firework fw2 = (Firework) tmpL.getWorld().spawnEntity(tmpL, EntityType.FIREWORK);
//                fw2.setFireworkMeta(fwm);
			}, i * 8);
		}

	}

}
