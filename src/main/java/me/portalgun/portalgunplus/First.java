package me.portalgun.portalgunplus;

import me.portalgun.portalgunplus.manager.ServerSystems;
import me.portalgun.portalgunplus.network.ModNetworking;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class First implements ModInitializer {
    public static final String MOD_ID = "portalgunplus";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItems.initialize();
        ModNetworking.initialize();
        ServerSystems.initialize();
        LOGGER.info("PortalGunPlus PRO initialized.");
    }
}
