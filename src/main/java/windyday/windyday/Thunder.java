package windyday.windyday;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;

public class Thunder extends Weather{

    private int thunder_timer;
    private int thunder_time = 600;
    private double thunder_damage = 600;
    private boolean thunder_do = true;

    public Thunder(Plugin plugin){
        super(plugin);
        thunderTimer();

        Bukkit.getPluginManager().registerEvents(new ThunderEvent(), parent_plugin);
    }

    public void setThunderTime(int time){
        this.thunder_time = time;
    }

    public void setThunderDamage(double damage){
        this.thunder_damage = damage;
    }

    public void thunderTimer(){

        thunder_timer = Bukkit.getScheduler().scheduleSyncRepeatingTask(parent_plugin, new Runnable(){
            public void run(){

                Bukkit.broadcastMessage("§f[ §e시스템 §f] §f곧 썬더러스의 번개가 내리칩니다.§f");
                for(Player player : Bukkit.getOnlinePlayers()){
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 0.5f);
                }

                Bukkit.getScheduler().scheduleSyncDelayedTask(parent_plugin, new Runnable(){
                    public void run(){

                        if(!thunder_do) return;

                        int maxPlayer = Bukkit.getOnlinePlayers().size();

                        if(maxPlayer == 0) return;

                        boolean findPlayer = false;
                        Player player = null;

                        boolean allSpectator = true;
                        for(Player allP : Bukkit.getOnlinePlayers()){
                            if(allP.getGameMode() != GameMode.CREATIVE && allP.getGameMode() != GameMode.SPECTATOR){
                                allSpectator = false;
                                break;
                            }
                        }

                        if(!allSpectator) {
                            while(!findPlayer){

                                int playerNum = MyUtility.getRandom(0, maxPlayer - 1);
                                player = (Player)Bukkit.getOnlinePlayers().toArray()[playerNum];

                                if(player != null && player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR){
                                    findPlayer = false;
                                } else {
                                    findPlayer = true;
                                }
                            }

                            player.getWorld().strikeLightning(player.getLocation());

                            thunderSoundEffect();

                            Bukkit.getLogger().info("[DEBUG]" + "thunder_time: " + thunder_time);
                        }

                    }
                }, 200l);

            }
        }, 0l, thunder_time * 20);

    }

    public void thunderSoundEffect(){
        Bukkit.getScheduler().scheduleSyncDelayedTask(parent_plugin, new ThunderSoundRunnable(), 5l);
        Bukkit.getScheduler().scheduleSyncDelayedTask(parent_plugin, new ThunderSoundRunnable(), 10l);
        Bukkit.getScheduler().scheduleSyncDelayedTask(parent_plugin, new ThunderSoundRunnable(), 15l);
        Bukkit.getScheduler().scheduleSyncDelayedTask(parent_plugin, new ThunderSoundRunnable(), 20l);
        Bukkit.getScheduler().scheduleSyncDelayedTask(parent_plugin, new ThunderSoundRunnable(), 25l);
    }

    public class ThunderSoundRunnable implements Runnable{
        public void run(){
            for(Player player : Bukkit.getOnlinePlayers()){
                player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_THUNDER, 1.0f, 0.5f);
            }
        }
    }

    public class ThunderEvent implements Listener {

        @EventHandler
        public void onEntityDamaged(EntityDamageEvent evt){

            Entity entity = evt.getEntity();
            if(entity instanceof Player && evt.getCause() == EntityDamageEvent.DamageCause.LIGHTNING){
                evt.setDamage(thunder_damage);
            }

        }

        @EventHandler
        public void onEntityDeath(EntityDeathEvent evt){

            if(!Bukkit.getScheduler().isCurrentlyRunning(thunder_timer)) return;

            Entity entity = evt.getEntity();
            if(entity == null || entity.getName() == null || entity.getCustomName() == null) return;
            if(entity.getCustomName().equalsIgnoreCase("Thundurus")
                && entity.getCustomName().equalsIgnoreCase("Thundurus")){

                Bukkit.getScheduler().cancelTask(thunder_timer);
                thunder_do = false;

                Bukkit.broadcastMessage("§f[ §b썬더로스 격파 §f ] 썬더로스의 저주는 사라졌습니다!");

                for(Player player : Bukkit.getOnlinePlayers()){
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.25f);
                }

                MyUtility.spreadEffect(parent_plugin, entity.getLocation());

                FileConfiguration config = parent_plugin.getConfig();
                config.set("thundurus_debuff", false);
                parent_plugin.saveConfig();

            }
        }

    }

}
