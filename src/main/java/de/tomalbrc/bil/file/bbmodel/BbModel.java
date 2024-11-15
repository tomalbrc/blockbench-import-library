package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.bil.file.ajblueprint.AjBlueprintVariants;
import de.tomalbrc.bil.file.ajmodel.AjMeta;
import de.tomalbrc.bil.file.extra.BbVariablePlaceholders;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

@SuppressWarnings("unused")
public class BbModel {
    public BbMeta meta;

    @Nullable
    @SerializedName("animated_java")
    public AjMeta ajMeta; // aj support

    public String name;
    @SerializedName("model_identifier")
    public String modelIdentifier;

    public Vector2i resolution;
    public ObjectArrayList<BbElement> elements;
    public ObjectArrayList<BbOutliner.ChildEntry> outliner;
    public ObjectArrayList<BbTexture> textures;
    public ObjectArrayList<BbAnimation> animations;

    @SerializedName("animation_variable_placeholders")
    public BbVariablePlaceholders animationVariablePlaceholders; // aj support ..?

    // ajblueprint support
    public AjBlueprintVariants variants;
}
