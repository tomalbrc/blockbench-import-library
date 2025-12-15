package de.tomalbrc.bil.core.holder.wrapper;

import de.tomalbrc.bil.core.element.PerPlayerItemDisplayElement;
import de.tomalbrc.bil.core.model.Node;
import de.tomalbrc.bil.core.model.Pose;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ModelBone extends ItemBone {
    private final ItemStack item;

    protected ModelBone(PerPlayerItemDisplayElement element, Node node, Pose defaultPose) {
        super(element, node, defaultPose);
        this.item = element.getItem();
    }

    public static ModelBone of(PerPlayerItemDisplayElement element, @NotNull Node node, Pose defaultPose) {
        return new ModelBone(element, node, defaultPose);
    }

    public void updateModel(Identifier model) {
        this.item.set(DataComponents.ITEM_MODEL, model);

        if (!this.invisible) {
            this.setTrackedItem(this.item);
        }
    }
}
