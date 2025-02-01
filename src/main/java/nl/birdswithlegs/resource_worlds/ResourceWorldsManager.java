package nl.birdswithlegs.resource_worlds;

import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.dimension.DimensionTypes;
import xyz.nucleoid.fantasy.Fantasy;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static nl.birdswithlegs.resource_worlds.ResourceWorlds.MOD_ID;

public class ResourceWorldsManager {
    // - Make/delete new dimensions

    // These are persistent and managed by the ResourceWorldsPersistance class
    public static int generation;

    // These are managed here at runtime
    public static Map<Identifier, Supplier<RuntimeWorldConfig>> WorldConfigSuppliers;
    public static Map<Identifier, RuntimeWorldHandle> WORLDS = new HashMap<>();

    public static boolean newWorlds(MinecraftServer server) {
        if (!WORLDS.isEmpty()) {
            //Cannot make worlds when they already exist
            return false;
        }

        Fantasy fantasy = Fantasy.get(server);
        WORLDS = new HashMap<>();

        WorldConfigSuppliers.forEach((Identifier worldId, Supplier<RuntimeWorldConfig> worldConfigSupplier) -> {
            RuntimeWorldConfig worldConfig = worldConfigSupplier.get();
//            RuntimeWorldConfig worldConfig = new RuntimeWorldConfig()
//                    .setDimensionType(DimensionTypes.OVERWORLD)
//                    .setDifficulty(Difficulty.HARD)
//                    .setGameRule(GameRules.DO_DAYLIGHT_CYCLE, false)
//                    .setGenerator(server.getOverworld().getChunkManager().getChunkGenerator())
//                    .setSeed(1234L);
//						worldHandle = fantasy.openTemporaryWorld(worldConfig);

            RuntimeWorldHandle worldHandle = fantasy.getOrOpenPersistentWorld(worldId, worldConfig);

            WORLDS.put(worldId, worldHandle);

        });
        return true;
    }

    public static boolean deleteWorlds(MinecraftServer server) {
        if (!WORLDS.isEmpty()) {
            //Cannot make worlds when they already exist
            return false;
        }

        Fantasy fantasy = Fantasy.get(server);

        WORLDS.forEach((Identifier worldId, RuntimeWorldHandle worldHandle) -> {
            worldHandle.delete();
            WORLDS.remove(worldId);
        });
        return true;
    }
}
