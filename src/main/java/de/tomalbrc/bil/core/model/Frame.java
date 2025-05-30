package de.tomalbrc.bil.core.model;

import com.mojang.brigadier.CommandDispatcher;
import de.tomalbrc.bil.BIL;
import de.tomalbrc.bil.core.holder.base.AbstractAnimationHolder;
import de.tomalbrc.bil.util.command.ParsedCommand;
import eu.pb4.polymer.virtualentity.api.attachment.HolderAttachment;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record Frame(
        float time,
        Reference2ObjectOpenHashMap<UUID, Pose> poses,

        @Nullable Variant variant,
        @Nullable Commands commands,
        @Nullable SoundEvent soundEffect,
        @Nullable Particle particleEffect
) {
    private static boolean satisfiesConditions(ParsedCommand[] conditions, CommandDispatcher<CommandSourceStack> dispatcher, CommandSourceStack source) {
        if (conditions != null) {
            for (ParsedCommand condition : conditions) {
                if (condition.execute(dispatcher, source) <= 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean requiresUpdates() {
        return this.variant != null || this.commands != null || this.soundEffect != null;
    }

    public void runEffects(AbstractAnimationHolder holder) {
        CommandDispatcher<CommandSourceStack> dispatcher = BIL.SERVER.getCommands().getDispatcher();
        CommandSourceStack source = holder.createCommandSourceStack().withPermission(2).withSuppressedOutput();

        if (this.soundEffect != null) {
            Entity entity = source.getEntity();
            if (entity != null) {
                entity.playSound(this.soundEffect);
            } else {
                HolderAttachment attachment = holder.getAttachment();
                if (attachment != null) {
                    attachment.getWorld().playSound(
                            null, BlockPos.containing(source.getPosition()),
                            this.soundEffect, SoundSource.MASTER,
                            1.0F, 1.0F
                    );
                }
            }
        }

        if (this.variant != null) {
            if (satisfiesConditions(this.variant.conditions, dispatcher, source)) {
                holder.getVariantController().setVariant(this.variant.uuid);
            }
        }

        if (this.commands != null && this.commands.commands.length > 0) {
            if (satisfiesConditions(this.commands.conditions, dispatcher, source)) {
                for (ParsedCommand command : this.commands.commands) {
                    command.execute(dispatcher, source);
                }
            }
        }
    }

    public record Variant(
            UUID uuid,
            @Nullable ParsedCommand[] conditions
    ) {
    }

    public record Commands(
            ParsedCommand[] commands,
            @Nullable ParsedCommand[] conditions
    ) {
    }

    public record Particle(
            String effect,
            String locator,
            ParsedCommand[] script,
            String file
    ) {
    }
}