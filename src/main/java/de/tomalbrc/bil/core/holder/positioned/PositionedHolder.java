package de.tomalbrc.bil.core.holder.positioned;

import de.tomalbrc.bil.core.holder.base.AbstractAnimationHolder;
import de.tomalbrc.bil.core.model.Model;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

@Deprecated(forRemoval = true)
public class PositionedHolder extends AbstractAnimationHolder {
    public PositionedHolder(ServerLevel level, Vec3 pos, Model model) {
        super(model, level);
        this.currentPos = pos;
    }

    public PositionedHolder(ServerLevel level, Model model) {
        super(model, level);
    }

    @Override
    public CommandSourceStack createCommandSourceStack() {
        String name = String.format("PositionedHolder[%.1f, %.1f, %.1f]", this.getPos().x, this.getPos().y, this.getPos().z);
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
