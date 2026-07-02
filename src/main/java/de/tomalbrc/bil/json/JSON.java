package de.tomalbrc.bil.json;

import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import org.joml.Matrix4fc;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector3fc;

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

    public static final Codec<Either<Integer, UUID>> EITHER_INT_UUID = Codec.of(new Encoder<>() {
        @Override
        public <T> DataResult<T> encode(Either<Integer, UUID> input, DynamicOps<T> ops, T prefix) {
            return DataResult.success(input.map(ops::createInt, u -> ops.createString(u.toString())));
        }
    }, new Decoder<>() {
        @Override
        public <T> DataResult<Pair<Either<Integer, UUID>, T>> decode(DynamicOps<T> ops, T input) {
            DataResult<Number> result = ops.getNumberValue(input);
            if (result.isError()) {
                return DataResult.success(Pair.of(Either.right(UUID.fromString(ops.getStringValue(input).getOrThrow())), ops.empty()));
            }
            return DataResult.success(Pair.of(Either.left(result.getOrThrow().intValue()), ops.empty()));
        }
    });

    public static final GsonBuilder GENERIC_BUILDER = new GsonBuilder()
            // Reference equality
            .registerTypeHierarchyAdapter(UUID.class, new CachedUuidDeserializer())
            // Custom deserializers
            .registerTypeHierarchyAdapter(Matrix4fc.class, new SimpleCodecDeserializer<>(ExtraCodecs.MATRIX4F))
            .registerTypeHierarchyAdapter(Vector3fc.class, new SimpleCodecDeserializer<>(ExtraCodecs.VECTOR3F))
            .registerTypeHierarchyAdapter(Vector2ic.class, new SimpleCodecDeserializer<>(VECTOR2I))
            .registerTypeHierarchyAdapter(Identifier.class, new SimpleCodecDeserializer<>(Identifier.CODEC))
            .registerTypeHierarchyAdapter(Item.class, new RegistryDeserializer<>(BuiltInRegistries.ITEM))
            .registerTypeHierarchyAdapter(SoundEvent.class, new RegistryDeserializer<>(BuiltInRegistries.SOUND_EVENT))
            .registerTypeHierarchyAdapter(Either.class, new SimpleCodecDeserializer<>(EITHER_INT_UUID));
}
