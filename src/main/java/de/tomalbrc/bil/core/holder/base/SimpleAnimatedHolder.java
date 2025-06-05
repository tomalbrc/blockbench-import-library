package de.tomalbrc.bil.core.holder.base;

import de.tomalbrc.bil.BIL;
import de.tomalbrc.bil.core.model.Model;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec2;

public class SimpleAnimatedHolder extends AbstractAnimationHolder {
    protected SimpleAnimatedHolder(Model model) {
        super(model);
    }

    @Override
    public CommandSourceStack createCommandSourceStack() {
        String name = String.format("SimpleAnimatedHolder[%.1f, %.1f, %.1f]", this.getPos().x, this.getPos().y, this.getPos().z);
        return new CommandSourceStack(
                this.getServer(),
                this.getPos(),
                Vec2.ZERO,
                this.level,
                0,
                name,
                Component.literal(name),
                this.getServer(),
                null
        );
    }
}
