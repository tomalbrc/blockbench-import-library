package de.tomalbrc.bil.model;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents an Animated Java JSON intermediate exported from Blockbench.
 * <a href="https://github.com/Animated-Java/animated-java/blob/main/exporters/jsonExporter/jsonStructure.json">JSON Structure</a>
 */
public record Model(
        @Nullable Vec2 size,

        Object2ObjectOpenHashMap<UUID, Node> nodeMap,
        Reference2ObjectOpenHashMap<UUID, Pose> defaultPose,

        Reference2ObjectOpenHashMap<UUID, Variant> variants,
        Object2ObjectOpenHashMap<String, Animation> animations
) {
}
