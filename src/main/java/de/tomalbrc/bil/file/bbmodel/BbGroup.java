package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.UUID;

public class BbGroup {
    public String name = "";
    public Vector3fc origin = new Vector3f();
    public Vector3fc rotation = new Vector3f();
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
