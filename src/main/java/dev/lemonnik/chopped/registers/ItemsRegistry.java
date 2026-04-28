package dev.lemonnik.chopped.registers;

import dev.lemonnik.chopped.Chopped;
import dev.lemonnik.chopped.items.ChiselItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ItemsRegistry {
	public static Item register(Item item, String id) {
		ResourceLocation itemID = Chopped.id(id);

		return Registry.register(BuiltInRegistries.ITEM, itemID, item);
	}

	public static final Item CHISEL_ITEM = register(new ChiselItem(), "chisel");

	public static void initialize() {
		Chopped.LOGGER.info("Registering Chopped items...");
	}
}