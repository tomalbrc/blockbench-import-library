package de.tomalbrc.bil.core.element;

import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.data.SynchedEntityDataLike;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityDimensions;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public interface PolymerDisplayElementLike extends PolymerGenericElementLike {
    @Nullable ElementHolder getHolder();
    SynchedEntityDataLike getSyncedData();
    SynchedEntityDataLike createSyncedData();
    int getEntityId();

    Display.BillboardConstraints getBillboardMode();
    void setBillboardMode(Display.BillboardConstraints billboardMode);
    @Nullable Brightness getBrightness();
    void setBrightness(@Nullable Brightness brightness);

    float getViewRange();
    void setViewRange(float viewRange);
    float getShadowRadius();
    void setShadowRadius(float shadowRadius);

    float getShadowStrength();

    void setShadowStrength(float shadowStrength);

    float getDisplayWidth();
    float getDisplayHeight();
    void setDisplayWidth(float width);
    void setDisplayHeight(float height);
    void setDisplaySize(float width, float height);
    void setDisplaySize(EntityDimensions dimensions);
    int getGlowColorOverride();
    void setGlowColorOverride(int glowColorOverride);
}
