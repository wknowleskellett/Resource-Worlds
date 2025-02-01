package nl.birdswithlegs.resource_worlds;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.dimension.DimensionTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.nucleoid.fantasy.Fantasy;
import xyz.nucleoid.fantasy.RuntimeWorld;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;

import java.util.HashMap;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.literal;

public class ResourceWorlds implements ModInitializer {
	public static final String MOD_ID = "resource-worlds";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Map<Identifier, RuntimeWorldHandle> WORLDS = new HashMap<Identifier, RuntimeWorldHandle>();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
				literal("new").executes(context -> {
					// Run Command Code Here
					MinecraftServer server = context.getSource().getServer();
					Fantasy fantasy = Fantasy.get(server);

					Identifier worldId = new Identifier(MOD_ID, "bar");//context.getSource().getName());
					RuntimeWorldHandle worldHandle = WORLDS.get(worldId);
					if (worldHandle == null) {
						RuntimeWorldConfig worldConfig = new RuntimeWorldConfig()
								.setDimensionType(DimensionTypes.OVERWORLD)
								.setDifficulty(Difficulty.HARD)
								.setGameRule(GameRules.DO_DAYLIGHT_CYCLE, false)
								.setGenerator(server.getOverworld().getChunkManager().getChunkGenerator())
								.setSeed(1234L);
//						worldHandle = fantasy.openTemporaryWorld(worldConfig);
						worldHandle = fantasy.getOrOpenPersistentWorld(worldId, worldConfig);

						WORLDS.put(worldId, worldHandle);
						context.getSource().sendFeedback(() -> Text.literal("Created world: "+worldId), false);
					} else context.getSource().sendFeedback(() -> Text.literal("World already exists: "+worldId), false);

					return 1;
				})));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
				literal("del").executes(context -> {
					// Run Command Code Here
					MinecraftServer server = context.getSource().getServer();

					Identifier worldId = new Identifier(MOD_ID, "bar");//context.getSource().getName());
					RuntimeWorldHandle worldHandle = WORLDS.get(worldId);
					if (worldHandle != null) {
						worldHandle.delete();
						WORLDS.remove(worldId);
						context.getSource().sendFeedback(() -> Text.literal("Deleted world: "+worldId), false);
					} else context.getSource().sendFeedback(() -> Text.literal("World does not exist: "+worldId), false);

					return 1;
				})));
	}
}