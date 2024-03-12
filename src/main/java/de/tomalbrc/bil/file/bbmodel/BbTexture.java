package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class BbTexture {
    public String path;
    public String name;
    public String folder;
    public String namespace;
    public String id;
    public int width;
    public int height;
    @SerializedName("uv_width")
    public int uvWidth;
    @SerializedName("uv_height")
    public int uvHeight;
    public boolean particle;
    @SerializedName("layers_enabled")
    public boolean layersEnabled;
    @SerializedName("sync_to_project")
    public boolean syncToProject;
    @SerializedName("render_mode")
    public String renderMode;
    @SerializedName("render_sides")
    public String renderSides;
    @SerializedName("frame_time")
    public int frameTime;
    @SerializedName("frame_order_type")
    public String frameOrderType;
    @SerializedName("frame_order")
    public String frameOrder;
    public boolean visible;
    public boolean internal;
    public boolean saved;
    public UUID uuid;
    public String source;
}
