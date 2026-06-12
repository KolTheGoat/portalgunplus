package me.portalgun.portalgunplus.client.screen;

import me.portalgun.portalgunplus.PortalMode;
import me.portalgun.portalgunplus.network.PortalModePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class PortalModeScreen extends Screen {
    public PortalModeScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 75;
        int y = this.height / 2 - 55;

        addModeButton("Nether", PortalMode.NETHER, x, y);
        addModeButton("End", PortalMode.END, x, y + 24);
        addModeButton("End Gateway", PortalMode.END_GATEWAY, x, y + 48);
        addModeButton("Spawn", PortalMode.SPAWN, x, y + 72);
    }

    private void addModeButton(String name, PortalMode mode, int x, int y) {
        this.addRenderableWidget(Button.builder(Component.literal(name), button -> {
            ClientPlayNetworking.send(new PortalModePayload(mode.ordinal()));
            this.onClose();
        }).bounds(x, y, 150, 20).build());
    }
}
