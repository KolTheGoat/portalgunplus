package me.portalgun.portalgunplus.client.screen;

import me.portalgun.portalgunplus.PotionEffectChoice;
import me.portalgun.portalgunplus.network.PotionSettingsPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class PotionGunScreen extends Screen {
    private int selectedEffect = 0;
    private int selectedLevel = 1;
    private int selectedDuration = 1;

    public PotionGunScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        int left = this.width / 2 - 155;
        int top = this.height / 2 - 110;

        PotionEffectChoice[] effects = PotionEffectChoice.values();
        for (int i = 0; i < effects.length; i++) {
            int index = i;
            int column = i % 2;
            int row = i / 2;
            this.addRenderableWidget(Button.builder(
                    Component.literal(effects[i].name()),
                    button -> selectedEffect = index
            ).bounds(left + column * 160, top + row * 22, 155, 20).build());
        }

        int controlsY = top + 10 * 22 + 8;

        this.addRenderableWidget(Button.builder(Component.literal("Level: " + selectedLevel), button -> {
            selectedLevel = selectedLevel % 5 + 1;
            button.setMessage(Component.literal("Level: " + selectedLevel));
        }).bounds(left, controlsY, 100, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Duration"), button -> {
            selectedDuration = (selectedDuration + 1) % 5;
            button.setMessage(Component.literal("Duration: " + durationLabel(selectedDuration)));
        }).bounds(left + 105, controlsY, 130, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Save"), button -> {
            ClientPlayNetworking.send(new PotionSettingsPayload(selectedEffect, selectedLevel, selectedDuration));
            this.onClose();
        }).bounds(left + 240, controlsY, 70, 20).build());
    }

    private static String durationLabel(int id) {
        return switch (id) {
            case 0 -> "5s";
            case 1 -> "10s";
            case 2 -> "20s";
            case 3 -> "40s";
            default -> "Infinite";
        };
    }
}
