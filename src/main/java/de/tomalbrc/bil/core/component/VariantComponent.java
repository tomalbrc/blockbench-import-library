package de.tomalbrc.bil.core.component;

import de.tomalbrc.bil.api.VariantController;
import de.tomalbrc.bil.core.holder.base.AbstractAnimationHolder;
import de.tomalbrc.bil.core.holder.wrapper.ItemBone;
import de.tomalbrc.bil.core.holder.wrapper.ModelBone;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.core.model.Variant;
import net.minecraft.resources.ResourceLocation;
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
            for (int i = 0; i < this.holder.getBones().length; i++) {
                if (this.holder.getBones()[i] instanceof ItemBone itemBone) {
                    itemBone.updateModel(itemBone.node().modelData());
                }
            }
        }
    }

    @Override
    public boolean isDefaultVariant() {
        return this.currentVariant == null;
    }

    @Override
    public boolean isCurrent(String variantName) {
        return this.currentVariant != null && this.currentVariant.name().equals(variantName);
    }

    @Override
    public void setVariant(String variantName) {
        if (this.getCurrentVariant() != null && this.getCurrentVariant().name().equals(variantName)) {
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
        for (int i = 0; i < this.holder.getBones().length; i++) {
            if (this.holder.getBones()[i] instanceof ModelBone bone) {
                UUID uuid = bone.node().uuid();
                ResourceLocation modelData = variant.models().get(uuid);
                if (modelData != null && variant.isAffected(uuid)) {
                    bone.updateModel(modelData);
                }
            }
        }
    }
}
