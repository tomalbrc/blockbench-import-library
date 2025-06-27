package de.tomalbrc.bil.core.holder.wrapper;

import de.tomalbrc.bil.core.element.PerPlayerBlockDisplayElement;
import de.tomalbrc.bil.core.model.Node;
import de.tomalbrc.bil.core.model.Pose;
import eu.pb4.polymer.virtualentity.api.tracker.DisplayTrackedData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockBone extends Bone<PerPlayerBlockDisplayElement> {
    private BlockState blockState;
    private boolean invisible;

    protected BlockBone(PerPlayerBlockDisplayElement element, Node node, Pose defaultPose, boolean isHead) {
        super(element, node, defaultPose, isHead);
        this.blockState = element.getBlockState();
    }

    public static BlockBone of(PerPlayerBlockDisplayElement element, Node node, Pose defaultPose, boolean isHead) {
        return new BlockBone(element, node, defaultPose, isHead);
    }

    public static BlockBone of(PerPlayerBlockDisplayElement element, @NotNull Node node, Pose defaultPose) {
        Node current = node;
        boolean head = false;
        while (current != null) {
            if (current.headTag()) {
                head = true;
                break;
            }
            current = current.parent();
        }

        return new BlockBone(element, node, defaultPose, head);
    }

    public void setInvisible(boolean invisible) {
        if (this.invisible == invisible) {
            return;
        }

        this.invisible = invisible;
        if (invisible) {
            this.setTrackedBlock(Blocks.AIR.defaultBlockState());
        } else {
            this.setTrackedBlock(this.blockState);
        }
    }

    public void updateBlockState(BlockState model) {
        this.blockState = model;

        if (!this.invisible) {
            this.setTrackedBlock(this.blockState);
        }
    }

    private void setTrackedBlock(BlockState block) {
        this.element().getDataTracker().set(DisplayTrackedData.Block.BLOCK_STATE, block, true);
    }
}
