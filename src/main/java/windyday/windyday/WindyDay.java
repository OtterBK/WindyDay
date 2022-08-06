package windyday.windyday;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class WindyDay extends JavaPlugin {

    public Weather wind;
    public Weather thunder;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("윈디데이 플러그인 로드됨");

        File file = new File(getDataFolder(), "config.yml");
        if(!file.exists()){
            saveDefaultConfig();
        }

        FileConfiguration config = this.getConfig();
        if(config.getBoolean("thundurus_debuff")){
            thunder = new Thunder(this);
            Thunder thunder_option = (Thunder)thunder;

            int thunder_time = config.getInt("thunder_time");
            thunder_option.setThunderTime(thunder_time);

            double thunder_damage = config.getDouble("thunder_damage");
            thunder_option.setThunderDamage(thunder_damage);

            Bukkit.getLogger().info("[DEBUG]" + "썬더러스 저주 활성화");
        }

        if(config.getBoolean("tornadus_debuff")){
            wind = new Wind(this);
            Wind wind_option = (Wind)wind;

            int wind_power_min = config.getInt("wind_power_min");
            wind_option.setWindPowerMin(wind_power_min);

            int wind_power_max = config.getInt("wind_power_max");
            wind_option.setWindPowerMax(wind_power_max);

            int wind_change_time = config.getInt("wind_change_time");
            wind_option.setChangeTime(wind_change_time);

            boolean wind_sound = config.getBoolean("wind_sound");
            wind_option.setWindSound(wind_sound);

            boolean wind_entity_affect = config.getBoolean("wind_entity_affect");
            wind_option.setWindEntityAffect(wind_entity_affect);

            boolean wind_only_living_entity = config.getBoolean("wind_only_living_entity");
            wind_option.setWindOnlyLivingEntity(wind_only_living_entity);

            int wind_entity_affect_distance = config.getInt("wind_entity_affect_distance");
            wind_option.setWindAffectDistance(wind_entity_affect_distance);

            double wind_entity_vector_multiple = config.getDouble("wind_entity_vector_multiple");
            wind_option.setWindEntityVectorMultiple(wind_entity_vector_multiple);

            int wind_min_delay = config.getInt("wind_min_delay");
            wind_option.setWindMinDelay(wind_min_delay);

            int wind_max_delay = config.getInt("wind_max_delay");
            wind_option.setWindMaxdelay(wind_max_delay);

            Bukkit.getLogger().info("[DEBUG]" + "토네러스 저주 활성화");
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("윈디데이 플러그인 언로드됨");

    }
}
