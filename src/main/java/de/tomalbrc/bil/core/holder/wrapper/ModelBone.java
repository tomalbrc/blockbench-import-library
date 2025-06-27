package de.tomalbrc.bil.core.holder.wrapper;

import de.tomalbrc.bil.core.element.PerPlayerItemDisplayElement;
import de.tomalbrc.bil.core.model.Node;
import de.tomalbrc.bil.core.model.Pose;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ModelBone extends ItemBone {
    private final ItemStack item;

    protected ModelBone(PerPlayerItemDisplayElement element, Node node, Pose defaultPose, boolean isHead) {
        super(element, node, defaultPose, isHead);
        this.item = element.getItem();
    }

    public static ModelBone of(PerPlayerItemDisplayElement element, Node node, Pose defaultPose, boolean isHead) {
        return new ModelBone(element, node, defaultPose, isHead);
    }

    public static ModelBone of(PerPlayerItemDisplayElement element, @NotNull Node node, Pose defaultPose) {
        Node current = node;
        boolean head = false;
        while (current != null) {
            if (current.headTag()) {
                head = true;
                break;
            }
            current = current.parent();
        }

        return new ModelBone(element, node, defaultPose, head);
    }

    public void updateModel(ResourceLocation model) {
        this.item.set(DataComponents.ITEM_MODEL, model);

        if (!this.invisible) {
            this.setTrackedItem(this.item);
        }
    }
}
