package de.tomalbrc.bil.core.holder.base;

import de.tomalbrc.bil.core.model.Model;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.phys.Vec2;

public class SimpleAnimatedHolder extends AbstractAnimationHolder {
    public SimpleAnimatedHolder(Model model) {
        super(model);
    }

    @Override
    public CommandSourceStack createCommandSourceStack() {
        return new CommandSourceStack(
                this.getLevel().getServer(),
                this.getPos(),
                Vec2.ZERO,
                this.getLevel(),
                PermissionSet.ALL_PERMISSIONS,
                this.getLevel().getServer(),
                null
        );
    }
}
