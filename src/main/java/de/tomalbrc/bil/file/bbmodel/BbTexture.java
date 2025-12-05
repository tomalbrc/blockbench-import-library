package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

@SuppressWarnings("unused")
public class BbTexture {
    public String path;
    public String name;
    public String folder;
    public String namespace;
    public int id;
    public float width;
    public float height;
    @SerializedName("uv_width")
    public float uvWidth;
    @SerializedName("uv_height")
    public float uvHeight;
    public boolean particle;
    @SerializedName("layers_enabled")
    public boolean layersEnabled;
    @SerializedName("render_mode")
    public String renderMode;
    @SerializedName("render_sides")
    public String renderSides;
    @SerializedName("frame_time")
    public float frameTime;
    @SerializedName("frame_order_type")
    public String frameOrderType;
    @SerializedName("frame_order")
    public String frameOrder;
    public boolean visible;
    public UUID uuid;
    public String source;
}
