package de.tomalbrc.bil.json;

import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;

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
            .registerTypeAdapter(UUID.class, new CachedUuidDeserializer())
            // Custom deserializers
            .registerTypeAdapter(Matrix4f.class, new SimpleCodecDeserializer<>(ExtraCodecs.MATRIX4F))
            .registerTypeAdapter(Vector3f.class, new SimpleCodecDeserializer<>(ExtraCodecs.VECTOR3F))
            .registerTypeAdapter(Vector2i.class, new SimpleCodecDeserializer<>(VECTOR2I))
            .registerTypeAdapter(ResourceLocation.class, new SimpleCodecDeserializer<>(ResourceLocation.CODEC))
            .registerTypeAdapter(Item.class, new RegistryDeserializer<>(BuiltInRegistries.ITEM))
            .registerTypeAdapter(SoundEvent.class, new RegistryDeserializer<>(BuiltInRegistries.SOUND_EVENT));
}
