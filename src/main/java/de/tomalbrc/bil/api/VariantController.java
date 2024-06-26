package de.tomalbrc.bil.api;

import de.tomalbrc.bil.core.model.Variant;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@SuppressWarnings("unused")
public interface VariantController {
    /**
     * Returns the current variant of the entity.
     */
    @Nullable Variant getCurrentVariant();

    /**
     * Applies the default variant to the intermediate of the entity.
     */
    void setDefaultVariant();

    /**
     * Applies the given variant to the intermediate of the entity.
     */
    void setVariant(String variantName);

    /**
     * Applies the given variant to the intermediate of the entity.
     */
    void setVariant(UUID variantUuid);

    /**
     * Returns true if the current variant is the default variant
     */
    boolean isDefaultVariant();

    /**
     * Returns true is the name of the current variant equals to variantName
     */
    boolean isCurrent(String variantName);
}
