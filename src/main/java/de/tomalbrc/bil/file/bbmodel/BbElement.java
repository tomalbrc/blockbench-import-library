package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.UUID;

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
    public float inflate;
    public Vector3f origin;
    public Vector3f position; // used by locators
    public Object2ObjectOpenHashMap<String, BbFace> faces;
    public ElementType type;
    public UUID uuid;

    public enum ElementType {
        @SerializedName("cube")
        CUBE,
        @SerializedName("locator")
        LOCATOR
    }
}
