package de.tomalbrc.bil.model;

import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record Node(
        NodeType type,
        String name,
        UUID uuid,

        RPModelInfo modelInfo,
        ResourceLocation entityType
) {
    public enum NodeType {
        bone(true),
        locator(false);

        private final boolean hasModelData;

        NodeType(boolean hasModelData) {
            this.hasModelData = hasModelData;
        }

        public boolean hasModelData() {
            return this.hasModelData;
        }
    }
}