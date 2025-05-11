package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Display;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3f;

import java.util.UUID;

@SuppressWarnings("unused")
public class BbElement {
    public String name;
    public boolean rescale;

    @SerializedName("light_emission")
    public int lightEmission = 0;

    public Vector3f from;
    public Vector3f to;
    public Boolean shade;
    public Vector3f rotation;

    public float inflate;
    public Vector3f origin;
    public Vector3f position; // used by locators
    public Object2ObjectOpenHashMap<String, BbFace> faces;
    public ElementType type;
    public UUID uuid;

    // ajblueprint...
    // Yes, camelCase in the json, this is intended
    ResourceLocation item;
    ResourceLocation block;
    String text;
    int lineWidth;
    ItemDisplayContext itemDisplayContext = ItemDisplayContext.NONE;
    Display.TextDisplay.Align align;
    boolean shadow = false;
    double backgroundAlpha;
    String backgroundColor;

    public ResourceLocation getItem() {
        return item;
    }

    public ResourceLocation getBlock() {
        return block;
    }

    public String getText() {
        return text;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public ItemDisplayContext getItemDisplayContext() {
        return itemDisplayContext;
    }

    public Display.TextDisplay.Align getAlign() {
        return align;
    }

    public boolean isShadow() {
        return shadow;
    }

    public double getBackgroundAlpha() {
        return backgroundAlpha;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public enum ElementType {
        @SerializedName("cube")
        CUBE_MODEL,
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
