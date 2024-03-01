package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.UUID;

public class Outliner {
    public String name;
    public List<Integer> origin;
    public int color;
    public String nbt;
    public UUID uuid;
    public boolean export;
    @SerializedName("mirror_uv")
    public boolean mirrorUv;
    @SerializedName("is_open")
    public boolean isOpen;
    public boolean locked;
    public boolean visibility;
    public int autouv;
    public List<UUID> children;
}
