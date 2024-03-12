package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;

public class BbMeta {
    @SerializedName("format_version")
    String formatVersion;

    @SerializedName("model_format")
    String modelFormat;

    @SerializedName("box_uv")
    boolean boxUV;
}
