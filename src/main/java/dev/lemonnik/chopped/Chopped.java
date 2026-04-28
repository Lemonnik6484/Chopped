package dev.lemonnik.chopped;

import dev.lemonnik.chopped.registers.BlocksRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//? if fabric
//? if neoforge {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
*/
//?}

//? if neoforge
//@Mod(MOD_ID)

@SuppressWarnings("removal")
public class Chopped
		//? if fabric
		implements ModInitializer

		{
	public static final String MOD_ID = "chopped";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final String VERSION = /*$ mod_version */"0.8.1";

	//? if fabric
	@Override public void onInitialize()

	//? if neoforge
	 //public Chopped(IEventBus modBus)

	{
		LOGGER.info("Starting Chopped...");

		BlocksRegistry.initialize();
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.tryBuild(MOD_ID, path);
	}
}