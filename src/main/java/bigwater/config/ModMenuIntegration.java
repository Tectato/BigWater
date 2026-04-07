package bigwater.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.Minecraft;
import bigwater.config.ConfigScreen;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory(){
        return parent -> new ConfigScreen(parent, Minecraft.getInstance(), parent.width, parent.height);
    }
}
