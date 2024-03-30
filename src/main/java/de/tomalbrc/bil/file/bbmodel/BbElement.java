package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.bil.core.model.Node;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.Map;
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
    public int color;
    public float inflate;
    public Vector3f origin;
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
