package windyday.windyday;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Wind extends Weather{

    private boolean wind_sch = true;
    private int wind_vector_timer_id = -1;
    private int wind_change_timer_id = -1;
    private int wind_sound_timer_id = -1;

    private Vector wind_vector = new Vector(0 , 0, 0);
    private Vector wind_vector_for_entity = new Vector(0,0,0);

    private float wind_sound_volume = 0.1f;
    private int wind_power_min = 10;
    private int wind_power_max = 30;
    private int wind_change_time = 600;
    private boolean wind_sound = true;
    private boolean wind_entity_affect = true;
    private boolean wind_only_living_entity = true;
    private int wind_entity_affect_distance = 100;
    private double wind_entity_vector_multiple = 0.7;
    private int wind_min_delay = 10;
    private int wind_max_delay = 50;

    private int repeat_count = 10;
    private int playwind_Timer = -1;

    private boolean wind_pause = false;

    private boolean wind_do = true;

    public Wind(Plugin plugin){
        super(plugin);

        windChangeTimer();
        windVectorTimer();
        windSoundTimer();

        Bukkit.getPluginManager().registerEvents(new WindEvent(), parent_plugin);

    }

    public void setWindPowerMin(int power){
        this.wind_power_min = power;
    }

    public void setWindPowerMax(int power){
        this.wind_power_max = power;
    }

    public void setChangeTime(int time){
        this.wind_change_time = time;
    }

    public void setWindSound(boolean value){
        this.wind_sound = value;
    }

    public void setWindEntityAffect(boolean value){
        this.wind_entity_affect = value;
    }

    public void setWindOnlyLivingEntity(boolean value){
        this.wind_only_living_entity = value;
    }

    public void setWindAffectDistance(int distance){
        this.wind_entity_affect_distance = distance;
    }

    public void setWindEntityVectorMultiple(double multipleValue){
        this.wind_entity_vector_multiple = multipleValue;
    }

    public void setWindMinDelay(int delay){
        this.wind_min_delay = delay;
    }

    public void setWindMaxdelay(int delay){
        this.wind_max_delay = delay;
    }

    public void windVectorTimer(){

        if(!wind_sch) return;

        long rdTime = MyUtility.getRandom(wind_min_delay, wind_max_delay);

        List<Integer> alreadyEffectedEntity = new ArrayList<Integer>();

        wind_vector_timer_id = Bukkit.getScheduler().scheduleSyncDelayedTask(parent_plugin, new Runnable(){
            public void run(){

                if(!wind_pause){
                    boolean worldWindCheck = false;

                    for(Player player : Bukkit.getOnlinePlayers()){

                        if(player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR){
                            player.setVelocity(wind_vector);
                        }

                        if(wind_entity_affect && wind_entity_affect_distance > 0){
                            for(Entity entity : player.getNearbyEntities(wind_entity_affect_distance, wind_entity_affect_distance, wind_entity_affect_distance)){
                                if(entity instanceof Player) continue; //플레이어는 대상 아님

                                if(wind_only_living_entity && !(entity instanceof LivingEntity)) continue;

                                if(!alreadyEffectedEntity.contains(entity.getEntityId())){
                                    entity.setVelocity(wind_vector_for_entity);
                                    alreadyEffectedEntity.add(entity.getEntityId());
                                }
                            }
                        } else if(wind_entity_affect && wind_entity_affect_distance == 0 && !worldWindCheck){
                            worldWindCheck = true;
                            for(Entity entity : player.getWorld().getEntities()){
                                if(entity instanceof Player) continue;

                                if(wind_only_living_entity && !(entity instanceof LivingEntity)) continue;
                                entity.setVelocity(wind_vector_for_entity);
                            }
                        }
                    }
                }

               windVectorTimer();
            }
        }, rdTime);

    }

//    public void playWind(){
//        repeat_count = MyUtility.getRandom(5, 20);
//        playwind_Timer = Bukkit.getScheduler().scheduleSyncRepeatingTask(parent_plugin, new Runnable(){
//           public void run(){
//               if(repeat_count-- > 0) {
//                   for(Player player : Bukkit.getOnlinePlayers()){
//                       player.setVelocity(wind_vector);
//                   }
//               } else {
//                   Bukkit.getScheduler().cancelTask(playwind_Timer);
//               }
//           }
//        }, 0l , 1l);
//    }

    public void windChangeTimer(){
        wind_change_timer_id = Bukkit.getScheduler().scheduleSyncRepeatingTask(parent_plugin, new Runnable(){
            public void run(){

                Bukkit.broadcastMessage("§f[ §e시스템 §f] 10초 뒤 바람이 바뀝니다.");
                for(Player player : Bukkit.getOnlinePlayers()){
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 0.5f);
                }

                Bukkit.getScheduler().scheduleSyncDelayedTask(parent_plugin, new Runnable(){
                   public void run(){

                       if(!wind_do) return;;

                       if(wind_pause) return;

                       //방향 랜덤
                       int x = MyUtility.getRandom(1, 10) * (MyUtility.getRandom(0, 1) == 0 ? -1 : 1);
                       int y = MyUtility.getRandom(1, 10) * (MyUtility.getRandom(0, 1) == 0 ? -1 : 1);;
                       int z = MyUtility.getRandom(1, 10) * (MyUtility.getRandom(0, 1) == 0 ? -1 : 1);;

                       Vector vector = new Vector(x, 0, z);


                       double power = ((double)MyUtility.getRandom(wind_power_min, wind_power_max) / 100);
                       vector.normalize().multiply(power);

                       wind_sound_volume = (float)power/5;
                       if(wind_sound_volume > 2) wind_sound_volume = 2;
                       wind_vector = vector.setY(0.25f);
                       wind_vector_for_entity = wind_vector.clone().multiply(wind_entity_vector_multiple).setY(0.1f);

                       Bukkit.getLogger().info("[DEBUG]"+"vector: " + vector.toString() + ", power: " + power);
                       Bukkit.getLogger().info("[DEBUG]" + "wind_change_time: " + wind_change_time);
                       Bukkit.broadcastMessage("§f[ §e시스템 §f] 풍향과 풍속이 바뀌었습니다.");
                       for(Player player : Bukkit.getOnlinePlayers()){
                           player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1f, 0.5f);
                       }
                   }
                }, 200l);
            }
        }, 0l, wind_change_time * 20);
    }

    public void windSoundTimer(){
        wind_sound_timer_id = Bukkit.getScheduler().scheduleSyncRepeatingTask(parent_plugin, new Runnable(){
            public void run(){

                if(!wind_sound) return;

                if(wind_pause) return;

                for(Player player : Bukkit.getOnlinePlayers()){
                    player.playSound(player.getLocation(), Sound.ITEM_ELYTRA_FLYING, wind_sound_volume, 1.0f);
                }
            }
        }, 0l, 18l);
    }

    public class WindEvent implements Listener{

        @EventHandler
        public void onCommandInput(PlayerCommandPreprocessEvent evt){
            Player player = evt.getPlayer();
            if(evt.getMessage().equalsIgnoreCase("/바람") && player.isOp()){

                if(!wind_pause) {
                    player.sendMessage("§a바람을 멈췄습니다.");
                    wind_pause = true;
                } else {
                    player.sendMessage("§a바람이 다시 붑니다.");
                    wind_pause = false;
                }
                evt.setCancelled(true);
            }
        }

        @EventHandler
        public void onEntityDeath(EntityDeathEvent evt){

            if(!wind_sch) return;

            Entity entity = evt.getEntity();
            if(entity == null || entity.getName() == null || entity.getCustomName() == null) return;
            if(entity.getCustomName().equalsIgnoreCase("Tornadus")
                    && entity.getCustomName().equalsIgnoreCase("Tornadus")){

                wind_sch = false;
                wind_do = false;
                Bukkit.getScheduler().cancelTask(wind_sound_timer_id);
                Bukkit.getScheduler().cancelTask(wind_change_timer_id);
//                Bukkit.getScheduler().cancelTask(wind_vector_timer_id);

                Bukkit.broadcastMessage("§f[ §b토네로스 격파 §f ] 토네로스의 저주는 사라졌습니다!");

                for(Player player : Bukkit.getOnlinePlayers()){
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.25f);
                }

                MyUtility.spreadEffect(parent_plugin, entity.getLocation());

                FileConfiguration config = parent_plugin.getConfig();
                config.set("tornadus_debuff", false);
                parent_plugin.saveConfig();

            }
        }

    }

}
