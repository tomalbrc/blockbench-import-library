package de.tomalbrc.bil.extra;

import de.tomalbrc.bil.holder.base.AbstractAnimationHolder;
import de.tomalbrc.bil.holder.entity.EntityHolder;
import de.tomalbrc.bil.holder.wrapper.Locator;
import de.tomalbrc.bil.model.Pose;
import eu.pb4.polymer.virtualentity.api.VirtualEntityUtils;
import eu.pb4.polymer.virtualentity.api.elements.GenericEntityElement;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * Listener for locators, updates a single GenericEntityElement
 */
public class ElementUpdateListener implements Locator.LocatorListener {
    protected final GenericEntityElement element;

    public ElementUpdateListener(GenericEntityElement element) {
        this.element = element;
    }

    @Override
    public void update(AbstractAnimationHolder holder, Pose pose) {
        if (this.element.isSendingPositionUpdates()) {
            if (holder instanceof EntityHolder<?> entityHolder) {
                this.updateEntityBasedHolder(entityHolder, pose);
            } else {
                this.updateNonEntityBasedHolder(holder, pose);
            }
        }
    }

    private void updateEntityBasedHolder(EntityHolder<?> holder, Pose pose) {
        Entity parent = holder.getParent();
        float scale = holder.getScale();
        float yRot = parent.getYRot();
        float angle = yRot * Mth.DEG_TO_RAD;

        Vector3f offset = pose.translation();
        if (scale != 1F) {
            offset.mul(scale);
        }
        offset.rotateY(-angle);

        Vec3 pos = holder.getPos().add(offset.x, offset.y, offset.z);
        holder.sendPacket(VirtualEntityUtils.createMovePacket(
                this.element.getEntityId(),
                pos,
                pos,
                true,
                yRot,
                0F
        ));
    }

    private void updateNonEntityBasedHolder(AbstractAnimationHolder holder, Pose pose) {
        float scale = holder.getScale();
        Vector3fc offset = scale != 1F
                ? pose.translation().mul(scale)
                : pose.readOnlyTranslation();

        Vec3 pos = holder.getPos().add(offset.x(), offset.y(), offset.z());
        holder.sendPacket(VirtualEntityUtils.createMovePacket(
                this.element.getEntityId(),
                pos,
                pos,
                false,
                0F,
                0F
        ));
    }
}