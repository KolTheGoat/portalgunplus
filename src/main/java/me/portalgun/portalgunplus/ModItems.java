package me.portalgun.portalgunplus;

import java.util.function.Function;
import me.portalgun.portalgunplus.item.ObsidianBoatItem;
import me.portalgun.portalgunplus.item.InfiniteBucketItem;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.Blocks;

public final class ModItems {
    private ModItems() {}

    public static final Item PORTAL_GUN = register("portal_gun", Item::new, new Item.Properties().stacksTo(1));
    public static final Item POTION_GUN = register("potion_gun", Item::new, new Item.Properties().stacksTo(1));

    public static final Item INFINITE_WATER_BUCKET = register(
            "infinite_water_bucket",
            props -> new InfiniteBucketItem(props, Blocks.WATER),
            new Item.Properties().stacksTo(1)
    );

    public static final Item INFINITE_LAVA_BUCKET = register(
            "infinite_lava_bucket",
            props -> new InfiniteBucketItem(props, Blocks.LAVA),
            new Item.Properties().stacksTo(1).fireResistant()
    );

    /*
     * This item is registered now so recipes, textures and the creative tab are complete.
     * Lava movement is intentionally isolated for a later custom-entity pass.
     */
    public static final Item OBSIDIAN_BOAT = register(
            "obsidian_boat",
            ObsidianBoatItem::new,
            new Item.Properties().stacksTo(1).fireResistant()
    );

    public static final Item NETHERITE_ELYTRA = register(
            "netherite_elytra",
            Item::new,
            new Item.Properties()
                    .stacksTo(1)
                    .fireResistant()
                    .durability(ArmorType.CHESTPLATE.getDurability(37))
                    .humanoidArmor(ArmorMaterials.NETHERITE, ArmorType.CHESTPLATE)
                    .component(DataComponents.GLIDER, Unit.INSTANCE)
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
