package dev.lemonnik.chopped;

import dev.lemonnik.chopped.network.NetworkHandler;
import dev.lemonnik.chopped.registers.BlocksRegistry;
import dev.lemonnik.chopped.registers.ItemsRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//? if neoforge {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
*/
//?}

//? if neoforge
//@Mod(Chopped.MOD_ID)
public class Chopped
		//? if fabric
		implements ModInitializer
{
	public static final String MOD_ID = "chopped";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final String VERSION = /*$ mod_version */"0.8.1";

	//? if fabric
	@Override
	public void onInitialize() {
		initialize();
	}

	//? if neoforge
	/*public Chopped(IEventBus modBus) {
		initialize();
		modBus.addListener(NetworkHandler::onRegisterPayloads);
	}*/

	public static void initialize() {
		LOGGER.info("Starting Chopped...");
		BlocksRegistry.initialize();
		ItemsRegistry.initialize();
		NetworkHandler.initialize();
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.tryBuild(MOD_ID, path);
	}
}