package de.tomalbrc.bil.core.holder.wrapper;

import de.tomalbrc.bil.core.model.Node;
import de.tomalbrc.bil.core.model.Pose;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import eu.pb4.polymer.virtualentity.api.tracker.DisplayTrackedData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.DyedItemColor;

public class Bone extends DisplayWrapper<ItemDisplayElement> {
    private final ItemStack item;
    private boolean invisible;

    public static Bone of(ItemDisplayElement element, Node node, Pose defaultPose, boolean isHead) {
        return new Bone(element, node, defaultPose, isHead);
    }

    public static Bone of(ItemDisplayElement element, Node node, Pose defaultPose) {
        return new Bone(element, node, defaultPose, node.name().startsWith("head"));
    }

    protected Bone(ItemDisplayElement element, Node node, Pose defaultPose, boolean isHead) {
        super(element, node, defaultPose, isHead);
        this.item = element.getItem();
    }

    public void setInvisible(boolean invisible) {
        if (this.invisible == invisible) {
            return;
        }

        this.invisible = invisible;
        if (invisible) {
            this.setTrackedItem(ItemStack.EMPTY);
        } else {
            this.setTrackedItem(this.item);
        }
    }

    public void updateColor(int color) {
        this.item.set(DataComponents.DYED_COLOR, new DyedItemColor(color, false));

        if (!this.invisible) {
            this.setTrackedItem(this.item);
        }
    }

    public void updateModelData(ResourceLocation model) {
        this.item.set(DataComponents.ITEM_MODEL, model);

        if (!this.invisible) {
            this.setTrackedItem(this.item);
        }
    }

    private void setTrackedItem(ItemStack item) {
        this.element().getDataTracker().set(DisplayTrackedData.Item.ITEM, item, true);
    }
}
