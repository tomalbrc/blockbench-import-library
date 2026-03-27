package de.tomalbrc.bil.core.holder.wrapper;

import de.tomalbrc.bil.core.element.PerPlayerItemDisplayElement;
import de.tomalbrc.bil.core.model.Node;
import de.tomalbrc.bil.core.model.Pose;
import eu.pb4.polymer.virtualentity.api.data.DisplayEntityData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import org.jetbrains.annotations.NotNull;

public class ItemBone extends Bone<PerPlayerItemDisplayElement> {
    protected final ItemStack item;
    protected boolean invisible;

    protected ItemBone(PerPlayerItemDisplayElement element, Node node, Pose defaultPose) {
        super(element, node, defaultPose);
        this.item = element.getItem();
    }

    public static ItemBone of(PerPlayerItemDisplayElement element, @NotNull Node node, Pose defaultPose) {
        return new ItemBone(element, node, defaultPose);
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
        this.item.set(DataComponents.DYED_COLOR, new DyedItemColor(color));

        if (!this.invisible) {
            this.setTrackedItem(this.item);
        }
    }

    public void updateModel(Identifier model) {
        this.item.set(DataComponents.ITEM_MODEL, model);

        if (!this.invisible) {
            this.setTrackedItem(this.item);
        }
    }

    protected void setTrackedItem(ItemStack item) {
        this.element().getSyncedData().set(DisplayEntityData.Item.ITEM, item, true);
    }
}
