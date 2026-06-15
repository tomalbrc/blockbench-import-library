package de.tomalbrc.bil.util;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

import java.util.function.Predicate;

public class Permissions {
    public static boolean check(ServerPlayer player, String nodeName, int defaultLevel) {
        return player.permissions().hasPermission(Permission.Atom.create(nodeName));
    }

    public static Predicate<CommandSourceStack> require(String nodeName, int fallbackLevel) {
        return (source) -> source.permissions().hasPermission(Permission.Atom.create(nodeName));
    }
}