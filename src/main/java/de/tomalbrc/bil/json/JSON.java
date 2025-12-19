package de.tomalbrc.bil.json;

import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import org.joml.*;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class JSON {
    public static final Codec<Vector2i> VECTOR2I = Codec.either(
                    Codec.INT.listOf()
                            .comapFlatMap(l -> Util.fixedSize(l, 2).map(x -> new Vector2i(x.get(0), x.get(1))),
                                    v -> List.of(v.x(), v.y())),
                    RecordCodecBuilder.<Vector2i>create(i -> i.group(
                            Codec.INT.fieldOf("width").forGetter(Vector2i::x),
                            Codec.INT.fieldOf("height").forGetter(Vector2i::y)
                    ).apply(i, Vector2i::new))
            )
            .xmap(
                    e -> e.map(Function.identity(), Function.identity()),
                    Either::left
            );

    public static final GsonBuilder GENERIC_BUILDER = new GsonBuilder()
            // Reference equality
            .registerTypeHierarchyAdapter(UUID.class, new CachedUuidDeserializer())
            // Custom deserializers
            .registerTypeHierarchyAdapter(Matrix4fc.class, new SimpleCodecDeserializer<>(ExtraCodecs.MATRIX4F))
            .registerTypeHierarchyAdapter(Vector3fc.class, new SimpleCodecDeserializer<>(ExtraCodecs.VECTOR3F))
            .registerTypeHierarchyAdapter(Vector2ic.class, new SimpleCodecDeserializer<>(VECTOR2I))
            .registerTypeHierarchyAdapter(Identifier.class, new SimpleCodecDeserializer<>(Identifier.CODEC))
            .registerTypeHierarchyAdapter(Item.class, new RegistryDeserializer<>(BuiltInRegistries.ITEM))
            .registerTypeHierarchyAdapter(SoundEvent.class, new RegistryDeserializer<>(BuiltInRegistries.SOUND_EVENT));
}
