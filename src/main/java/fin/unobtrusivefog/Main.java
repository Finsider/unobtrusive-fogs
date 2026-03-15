package fin.unobtrusivefog;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

public class Main implements ModInitializer {

    public static Settings settings;

    @Override
    public void onInitialize() {
        AutoConfig.register(Settings.class, GsonConfigSerializer::new);
        ConfigHolder<Settings> holder = AutoConfig.getConfigHolder(Settings.class);
        settings = holder.getConfig();
    }
}