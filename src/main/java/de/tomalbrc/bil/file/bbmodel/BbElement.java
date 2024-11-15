package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Display;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.UUID;

@SuppressWarnings("unused")
public class BbElement {
    public String name;
    @SerializedName("box_uv")
    public boolean boxUv;
    @SerializedName("uv_offset")
    public Vector2i uvOffset;

    public boolean rescale;
    public boolean locked;

    public Vector3f from;
    public Vector3f to;

    public Vector3f rotation;
    public int autouv;
    public int color;
    public float inflate;
    public Vector3f origin;
    public Vector3f position; // used by locators
    public Object2ObjectOpenHashMap<String, BbFace> faces;
    public ElementType type;
    public UUID uuid;

    // ajblueprint..
    ResourceLocation item;
    ResourceLocation block;
    String text;
    int lineWidth;
    ItemDisplayContext itemDisplayContext = ItemDisplayContext.NONE;
    Display.TextDisplay.Align align;
    boolean shadow = false;
    double backgroundAlpha;
    String backgroundColor;

    public enum ElementType {
        @SerializedName("cube")
        CUBE,
        @SerializedName("locator")
        LOCATOR,
        @SerializedName("animated_java:vanilla_block_display")
        BLOCK_DISPLAY,
        @SerializedName("animated_java:vanilla_item_display")
        ITEM_DISPLAY,
        @SerializedName("animated_java:vanilla_text_display")
        TEXT_DISPLAY
    }
}
