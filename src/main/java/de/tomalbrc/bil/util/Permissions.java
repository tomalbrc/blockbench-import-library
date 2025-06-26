package de.tomalbrc.bil.util;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Predicate;

public class Permissions {
    public static boolean check(ServerPlayer player, String node, int defaultCheck) {
        return Permissions.check(player, node, defaultCheck);
    }

    public static Predicate<CommandSourceStack> require(String node, int fallbackLevel) {
        return me.lucko.fabric.api.permissions.v0.Permissions.require(node, fallbackLevel);
    }
}
