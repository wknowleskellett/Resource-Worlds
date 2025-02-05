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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import static net.minecraft.world.World.NETHER;
import static nl.birdswithlegs.resource_worlds.ResourceWorlds.LOGGER;
import static nl.birdswithlegs.resource_worlds.ResourceWorlds.MOD_ID;

public class ResourceWorldsManager {
    // - Make/delete new dimensions

    // These are persistent and managed by the ResourceWorldsPersistance class
    public static int generation = -1;

    // These are managed here at runtime
    public static Map<Identifier, RuntimeWorldConfigSupplier> WorldConfigSuppliers = new HashMap<>();
    public static Map<Identifier, RuntimeWorldHandle> WORLDS = new HashMap<>();

    public interface RuntimeWorldConfigSupplier {
        RuntimeWorldConfig get(MinecraftServer server);
    }

    public static void init() {
        WorldConfigSuppliers.put(new Identifier(MOD_ID, "overworld"), (MinecraftServer server) -> {
            return new RuntimeWorldConfig()
                    .setDimensionType(DimensionTypes.OVERWORLD)
                    .setDifficulty(Difficulty.HARD)
                    .setGameRule(GameRules.DO_DAYLIGHT_CYCLE, false)
                    .setGenerator(server.getOverworld().getChunkManager().getChunkGenerator())
                    .setSeed(1234L);
        });
//        WorldConfigSuppliers.put(new Identifier(MOD_ID, "nether"), (MinecraftServer server) -> {
//            return new RuntimeWorldConfig()
//                    .setDimensionType(DimensionTypes.THE_NETHER)
//                    .setDifficulty(Difficulty.HARD)
//                    .setGameRule(GameRules.DO_DAYLIGHT_CYCLE, false)
//                    .setGenerator(Objects.requireNonNull(server.getWorld(NETHER)).getChunkManager().getChunkGenerator())
//                    .setSeed(1234L);
//        });
    }

    public static boolean newWorlds(MinecraftServer server) {
        if (!WORLDS.isEmpty()) {
            //Cannot make worlds when they already exist
            return false;
        }

        Fantasy fantasy = Fantasy.get(server);
        WORLDS = new HashMap<>();

        generation++;

        WorldConfigSuppliers.forEach((Identifier worldId, RuntimeWorldConfigSupplier worldConfigSupplier) -> {
            RuntimeWorldConfig worldConfig = worldConfigSupplier.get(server);

            worldId = new Identifier(worldId + "-" + generation);
//            RuntimeWorldHandle worldHandle = fantasy.openTemporaryWorld(worldConfig);
            RuntimeWorldHandle worldHandle = fantasy.getOrOpenPersistentWorld(worldId, worldConfig);

            WORLDS.put(worldId, worldHandle);

        });
        return true;
    }

    public static boolean deleteWorlds(MinecraftServer server) {
        if (WORLDS.isEmpty()) {
            //Cannot delete worlds when they don't exist
            return false;
        }

//        Fantasy fantasy = Fantasy.get(server);
        try {
            WORLDS.forEach((Identifier worldId, RuntimeWorldHandle worldHandle) -> {
                worldHandle.delete();
                WORLDS.remove(worldId);
            });
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
        return true;
    }
}
