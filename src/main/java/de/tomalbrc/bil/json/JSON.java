package de.tomalbrc.bil.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.tomalbrc.bil.model.Frame;
import de.tomalbrc.bil.model.Pose;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.UUID;

public class JSON {
    public static final Gson GSON = new GsonBuilder()
            // Reference equality
            .registerTypeAdapter(UUID.class, new ReferenceUuidDeserializer())

            // Custom deserializers
            .registerTypeAdapter(Matrix4f.class, new Matrix4fDeserializer())
            .registerTypeAdapter(Vector3f.class, new Vector3fDeserializer())
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .registerTypeAdapter(Item.class, new RegistryDeserializer<>(BuiltInRegistries.ITEM))
            .registerTypeAdapter(SoundEvent.class, new RegistryDeserializer<>(BuiltInRegistries.SOUND_EVENT))
            .create();
}
