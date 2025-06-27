package de.tomalbrc.bil.core.extra;

import de.tomalbrc.bil.core.holder.base.AbstractAnimationHolder;
import de.tomalbrc.bil.core.holder.wrapper.DisplayWrapper;
import de.tomalbrc.bil.core.holder.wrapper.Locator;
import de.tomalbrc.bil.core.model.Pose;
import net.minecraft.server.level.ServerPlayer;

/**
 * Listener for locators, updates a single DisplayElement
 */
public class DisplayElementUpdateListener implements Locator.LocatorListener {
    protected final DisplayWrapper<?> display;

    public DisplayElementUpdateListener(DisplayWrapper<?> display) {
        this.display = display;
    }

    @Override
    public void update(ServerPlayer serverPlayer, AbstractAnimationHolder holder, Pose pose) {
        holder.updateElement(serverPlayer, this.display, pose);
    }
}