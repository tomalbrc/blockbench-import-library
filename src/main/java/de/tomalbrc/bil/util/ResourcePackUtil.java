package de.tomalbrc.bil.util;

import eu.pb4.polymer.resourcepack.api.ResourcePackBuilder;
import net.minecraft.resources.Identifier;

import java.util.concurrent.ConcurrentHashMap;


public class ResourcePackUtil {
    private static ConcurrentHashMap<Identifier, byte[]> data = new ConcurrentHashMap<>();

    public static byte[] add(Identifier location, byte[] bytes) {
        return data.put(location, bytes);
    }

    public static void addAdditional(ResourcePackBuilder resourcePackBuilder) {
        for (var entry : data.entrySet()) {
            resourcePackBuilder.addData(entry.getKey().getPath(), entry.getValue());
        }
    }
}
