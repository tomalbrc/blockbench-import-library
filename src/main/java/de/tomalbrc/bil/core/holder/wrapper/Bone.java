package de.tomalbrc.bil.core.holder.wrapper;

import de.tomalbrc.bil.core.model.Node;
import de.tomalbrc.bil.core.model.Pose;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import eu.pb4.polymer.virtualentity.api.tracker.DisplayTrackedData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.DyedItemColor;
import org.jetbrains.annotations.NotNull;

public class Bone extends DisplayWrapper<ItemDisplayElement> {
    private final ItemStack item;
    private boolean invisible;

    protected Bone(ItemDisplayElement element, Node node, Pose defaultPose, boolean isHead) {
        super(element, node, defaultPose, isHead);
        this.item = element.getItem();
    }

    public static Bone of(ItemDisplayElement element, Node node, Pose defaultPose, boolean isHead) {
        return new Bone(element, node, defaultPose, isHead);
    }

    public static Bone of(ItemDisplayElement element, @NotNull Node node, Pose defaultPose) {
        Node current = node;
        boolean head = false;
        while (current != null) {
            if (current.name().startsWith("head")) {
                head = true;
                break;
            }
            current = current.parent();
        }

        return new Bone(element, node, defaultPose, head);
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

    public void updateModelData(CustomModelData customModelData) {
        this.item.set(DataComponents.CUSTOM_MODEL_DATA, customModelData);

        if (!this.invisible) {
            this.setTrackedItem(this.item);
        }
    }

    private void setTrackedItem(ItemStack item) {
        this.element().getDataTracker().set(DisplayTrackedData.Item.ITEM, item, true);
    }
}
