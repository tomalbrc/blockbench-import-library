package de.tomalbrc.bil.file.ajblueprint;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.bil.file.ajmodel.AjVariant;

import java.util.List;

public record AjBlueprintVariants(
        @SerializedName("default") AjVariant defaultVariant,
        List<AjVariant> list
) {
}
