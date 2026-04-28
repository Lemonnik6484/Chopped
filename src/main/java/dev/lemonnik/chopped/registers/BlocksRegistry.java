package dev.lemonnik.chopped.registers;

import dev.lemonnik.chopped.Chopped;
import dev.lemonnik.chopped.blocks.PlankBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class BlocksRegistry {
	public static Block register(Block block, String name) {
		return register(block, name, true);
	}

	public static Block register(Block block, String name, boolean shouldRegisterItem) {
		ResourceLocation id = Chopped.id(name);

		if (shouldRegisterItem) {
			BlockItem blockItem = new BlockItem(block, new Item.Properties());
			Registry.register(BuiltInRegistries.ITEM, id, blockItem);
		}

		return Registry.register(BuiltInRegistries.BLOCK, id, block);
	}

	public static Block TEST_PLANKS = register(new PlankBlock(), "test_planks");
	public static Block SUPER_TEST_PLANKS = register(new PlankBlock(), "super_test_planks");

	public static void initialize() {
		Chopped.LOGGER.info("Registering Chopped blocks...");
	}
}