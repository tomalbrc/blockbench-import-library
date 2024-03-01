package de.tomalbrc.bil.component;

import de.tomalbrc.bil.api.VariantController;
import de.tomalbrc.bil.holder.base.AbstractAnimationHolder;
import de.tomalbrc.bil.holder.wrapper.Bone;
import de.tomalbrc.bil.model.Model;
import de.tomalbrc.bil.model.Variant;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class VariantComponent extends ComponentBase implements VariantController {
    @Nullable
    private Variant currentVariant;

    public VariantComponent(Model model, AbstractAnimationHolder holder) {
        super(model, holder);
    }

    @Nullable
    @Override
    public Variant getCurrentVariant() {
        return this.currentVariant;
    }

    @Override
    public void setDefaultVariant() {
        if (this.currentVariant != null) {
            this.currentVariant = null;
            for (Bone bone : this.holder.getBones()) {
                bone.updateModelData(bone.node().modelInfo().customModelData());
            }
        }
    }

    @Override
    public void setVariant(String variantName) {
        if (this.isCurrentVariant(variantName)) {
            return;
        }

        Variant variant = this.findByName(variantName);
        if (variant != null) {
            this.currentVariant = variant;
            this.applyVariantToBones(variant);
        }
    }

    @Override
    public void setVariant(UUID variantUuid) {
        Variant variant = this.model.variants().get(variantUuid);
        if (variant == null || variant == this.currentVariant) {
            return;
        }

        this.currentVariant = variant;
        this.applyVariantToBones(variant);
    }

    @Nullable
    private Variant findByName(String variantName) {
        for (Variant variant : this.model.variants().values()) {
            if (variant.name().equals(variantName)) {
                return variant;
            }
        }
        return null;
    }

    private void applyVariantToBones(Variant variant) {
        for (Bone bone : this.holder.getBones()) {
            UUID uuid = bone.node().uuid();
            Variant.ModelInfo modelInfo = variant.models().get(uuid);
            if (modelInfo != null && variant.isAffected(uuid)) {
                bone.updateModelData(modelInfo.customModelData());
            }
        }
    }
}
