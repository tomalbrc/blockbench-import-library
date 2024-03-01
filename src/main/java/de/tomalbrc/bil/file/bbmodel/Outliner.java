package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.UUID;

public class Outliner {
    private String name;
    private List<Integer> origin;
    private int color;
    private String nbt;
    private UUID uuid;
    private boolean export;
    @SerializedName("mirror_uv")
    private boolean mirrorUv;
    @SerializedName("is_open")
    private boolean isOpen;
    private boolean locked;
    private boolean visibility;
    private int autouv;
    private List<UUID> children;
}
