package nl.birdswithlegs.resource_worlds;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;

import static net.minecraft.server.command.CommandManager.literal;

//- Command to initialize process
//- Command to immediately do world reset
//- Command to tp to new world
//- Command to tp home

public class ResourceWorldsCommands {
    public static void init() {

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                literal("new").executes(context -> {
                    // Run Command Code Here
                    MinecraftServer server = context.getSource().getServer();
                    context.getSource().sendFeedback(() -> Text.literal(
                            ResourceWorldsManager.newWorlds(server) ?
                                    "Created "+ResourceWorldsManager.WORLDS.size()+" worlds." :
                                    "Did not create worlds as "+ResourceWorldsManager.WORLDS.size()+" already exist."
                            ), false);
                    return 1;
                })));


        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                literal("del").executes(context -> {
                    // Run Command Code Here
                    MinecraftServer server = context.getSource().getServer();
                    int worldsCount = ResourceWorldsManager.WORLDS.size();
                    context.getSource().sendFeedback(() -> Text.literal(
                            ResourceWorldsManager.deleteWorlds(server) ?
                                    "Deleted "+worldsCount+" worlds." :
                                    "Did not delete worlds as "+worldsCount+" worlds exist."
                    ), false);

                    return 1;
                })));


        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                literal("list").executes(context -> {
                    // Run Command Code Here
                    int worldsCount = ResourceWorldsManager.WORLDS.size();
                    context.getSource().sendMessage(Text.literal("There are "+worldsCount+" resource worlds."));
                    if (worldsCount == 0) {
                        // Worlds map may not have been initialized
                        return 1;
                    }
                    ResourceWorldsManager.WORLDS.forEach((Identifier identifier, RuntimeWorldHandle worldHandle) -> {
                        context.getSource().sendMessage(Text.literal(identifier.toString()));
                    });
                    return 1;
                })));
    }
}
