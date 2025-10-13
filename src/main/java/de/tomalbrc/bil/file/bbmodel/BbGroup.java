package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;
import org.joml.Vector3f;

import java.util.UUID;

public class BbGroup {
    public String name = "";
    public Vector3f origin = new Vector3f();
    public Vector3f rotation = new Vector3f();
    public UUID uuid;
    public boolean export;
    @SerializedName("mirror_uv")
    public boolean mirrorUv;

    public int autouv;

    public float scale;

    public BbGroup(UUID id) {
        this.uuid = id;
    }
}
