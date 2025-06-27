package de.tomalbrc.bil.core.element;

import it.unimi.dsi.fastutil.ints.IntList;

import java.util.UUID;

@SuppressWarnings("unused")
public interface PolymerGenericElementLike {
    boolean isDirty();
    boolean isRotationDirty();
    void ignorePositionUpdates();
    void instantPositionUpdates();
    void setInstantPositionUpdates(boolean value);
    void setSendPositionUpdates(boolean b);
    boolean isSendingPositionUpdates();
    void setPitch(float pitch);

    void setYaw(float yaw);

    float getYaw();
    float getPitch();
    IntList getEntityIds();
    UUID getUuid();
    int getEntityId();

    boolean isSneaking();
    void setSneaking(boolean sneaking);
    boolean isSprinting();
    void setSprinting(boolean sprinting);
    boolean isGlowing();
    void setGlowing(boolean glowing);
    boolean isInvisible();
    void setInvisible(boolean invisible);

}
