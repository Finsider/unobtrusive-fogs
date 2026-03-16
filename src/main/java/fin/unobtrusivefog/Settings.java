package fin.unobtrusivefog;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "unobtrusive-fog")
public class Settings implements ConfigData {

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public FogRenderType renderDistanceFogType = FogRenderType.UNOBTRUSIVE;

    @ConfigEntry.Gui.PrefixText
    public boolean applyToAll = false;
    public boolean applyToOverworld = true;
    public boolean applyToNetherFog = true;
    public boolean applyToEndFog = true;

    public boolean applyToWaterFog = false;
    public boolean applyToLavaFog = false;
    public boolean applyToSnowFog = false;

    public boolean applyToBlindnessFog = false;
    public boolean applyToDarkessFog = false;
}
