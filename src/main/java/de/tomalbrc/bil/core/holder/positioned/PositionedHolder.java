package de.tomalbrc.bil.core.holder.positioned;

import de.tomalbrc.bil.core.holder.base.AbstractAnimationHolder;
import de.tomalbrc.bil.core.model.Model;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class PositionedHolder extends AbstractAnimationHolder {
    protected final Vec3 pos;

    public PositionedHolder(ServerLevel level, Vec3 pos, Model model) {
        super(model, level);
        this.pos = pos;
    }

    @Override
    public CommandSourceStack createCommandSourceStack() {
        String name = String.format("PositionedHolder[%.1f, %.1f, %.1f]", this.pos.x, this.pos.y, this.pos.z);
        return new CommandSourceStack(
                this.getServer(),
                this.pos,
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
