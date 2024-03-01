package de.tomalbrc.bil.core.model;

import java.util.UUID;

public record Node(
        NodeType type,
        String name,
        UUID uuid,

        RPModelInfo modelInfo
) {
    public enum NodeType {
        bone,
        locator
    }
}