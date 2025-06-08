package de.tomalbrc.bil.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ContextManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Predicate;

// screw "fabric-permission-api", that shit sucks ass
public class Permissions {
    static boolean loaded = FabricLoader.getInstance().isModLoaded("luckperms");

    public static boolean check(ServerPlayer player, String node, int defaultCheck) {
        return (loaded && hasLuckPermsPermission(player, node)) || player.hasPermissions(defaultCheck);
    }

    public static Predicate<CommandSourceStack> require(String node, int fallbackLevel) {
        return source -> {
            try {
                return check(source.getPlayerOrException(), node, fallbackLevel);
            } catch (CommandSyntaxException e) {
                return false;
            }
        };
    }

    private static boolean hasLuckPermsPermission(ServerPlayer player, String node) {
        User user = LuckPermsProvider.get().getUserManager().getUser(player.getUUID());
        if (user == null) return false;

        ContextManager contextManager = LuckPermsProvider.get().getContextManager();
        QueryOptions options = contextManager.getQueryOptions(user)
                .orElse(contextManager.getStaticQueryOptions());

        return user.getCachedData()
                .getPermissionData(options)
                .checkPermission(node)
                .asBoolean();
    }
}
