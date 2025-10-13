package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;

public class BbMeta {
    @SerializedName("format_version")
    public String formatVersion;

    @SerializedName("model_format")
    public String modelFormat;

    @SerializedName("box_uv")
    public boolean boxUV;
}
