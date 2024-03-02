package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;
import org.joml.Vector2i;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public Element getElement(UUID uuid) {
        for (Element element: this.elements) {
            if (element.uuid.equals(uuid))
                return element;
        }
        return null;
    }

    public Outliner getOutliner(UUID uuid) {
        for (Outliner outliner1: this.outliner) {
            if (outliner1.uuid.equals(uuid))
                return outliner1;
        }
        return null;
    }

    public Outliner getParent(Element element) {
        for (Outliner outliner: this.outliner) {
            if (outliner.children.contains(element.uuid))
                return outliner;
        }
        return null;
    }
}
