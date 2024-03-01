package de.tomalbrc.bil.extra;

import de.tomalbrc.bil.holder.base.AbstractAnimationHolder;
import de.tomalbrc.bil.holder.wrapper.DisplayWrapper;
import de.tomalbrc.bil.holder.wrapper.Locator;
import de.tomalbrc.bil.model.Pose;

/**
 * Listener for locators, updates a single DisplayElement
 */
public class DisplayElementUpdateListener implements Locator.LocatorListener {
    protected final DisplayWrapper<?> display;

    public DisplayElementUpdateListener(DisplayWrapper<?> display) {
        this.display = display;
    }

    @Override
    public void update(AbstractAnimationHolder holder, Pose pose) {
        holder.updateElement(this.display, pose);
    }
}