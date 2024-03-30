package de.tomalbrc.bil.core.model;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Intermediate/internal model representation
 */
public record Model(Object2ObjectOpenHashMap<UUID, Node> nodeMap,
                    Reference2ObjectOpenHashMap<UUID, Pose> defaultPose,

                    Reference2ObjectOpenHashMap<UUID, Variant> variants,
                    Object2ObjectOpenHashMap<String, Animation> animations,
                    @Nullable Vec2 size) {
}
