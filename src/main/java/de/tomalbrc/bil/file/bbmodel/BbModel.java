package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;
import org.joml.Vector2i;

import java.util.List;
import java.util.Map;

public class BbModel {
    public Meta meta;
    public String name;
    @SerializedName("model_identifier")
    public String modelIdentifier;
    @SerializedName("visible_box")
    public List<Integer> visibleBox;
    @SerializedName("variable_placeholders")
    public String variablePlaceholders;
    @SerializedName("variable_placeholder_buttons")
    public List<String> variablePlaceholderButtons;
    @SerializedName("timeline_setups")
    public List<String> timelineSetups;
    @SerializedName("unhandled_root_fields")
    public Map<String, Object> unhandledRootFields;
    public Vector2i resolution;
    public List<Element> elements;
    public List<Outliner> outliner;
    public List<Texture> textures;
    public List<Animation> animations;
}
