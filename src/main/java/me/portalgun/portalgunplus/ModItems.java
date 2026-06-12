package me.portalgun.portalgunplus;

import java.util.function.Function;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ModItems {

    private ModItems() {
        // Utility class: do not create instances.
    }

    public static final Item PORTAL_GUN = register(
            "portal_gun",
            Item::new,
            new Item.Properties().stacksTo(1)
    );

    public static final Item POTION_GUN = register(
            "potion_gun",
            Item::new,
            new Item.Properties().stacksTo(1)
    );

    public static final Item INFINITE_WATER_BUCKET = register(
            "infinite_water_bucket",
            Item::new,
            new Item.Properties().stacksTo(1)
    );

    public static final Item INFINITE_LAVA_BUCKET = register(
            "infinite_lava_bucket",
            Item::new,
            new Item.Properties().stacksTo(1)
    );

    public static final Item OBSIDIAN_BOAT = register(
            "obsidian_boat",
            Item::new,
            new Item.Properties().stacksTo(1)
    );

    public static final Item NETHERITE_ELYTRA = register(
            "netherite_elytra",
            Item::new,
            new Item.Properties().stacksTo(1)
    );

    public static final ResourceKey<CreativeModeTab> PORTALGUNPLUS_TAB_KEY =
            ResourceKey.create(
                    BuiltInRegistries.CREATIVE_MODE_TAB.key(),
                    Identifier.fromNamespaceAndPath(First.MOD_ID, "portalgunplus_tab")
            );

    public static final CreativeModeTab PORTALGUNPLUS_TAB =
            FabricCreativeModeTab.builder()
                    .icon(() -> new ItemStack(PORTAL_GUN))
                    .title(Component.translatable("creativeTab.portalgunplus"))
                    .displayItems((parameters, output) -> {
                        output.accept(PORTAL_GUN);
                        output.accept(POTION_GUN);
                        output.accept(INFINITE_WATER_BUCKET);
                        output.accept(INFINITE_LAVA_BUCKET);
                        output.accept(OBSIDIAN_BOAT);
                        output.accept(NETHERITE_ELYTRA);
                    })
                    .build();

    private static <T extends Item> T register(
            String name,
            Function<Item.Properties, T> itemFactory,
            Item.Properties properties
    ) {
        ResourceKey<Item> itemKey = ResourceKey.create(
                Registries.ITEM,
                Identifier.fromNamespaceAndPath(First.MOD_ID, name)
        );

        T item = itemFactory.apply(properties.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return item;
    }

    public static void initialize() {
        Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                PORTALGUNPLUS_TAB_KEY,
                PORTALGUNPLUS_TAB
        );
    }
}