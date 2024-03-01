package de.tomalbrc.bil.api;

import de.tomalbrc.bil.core.holder.wrapper.Locator;
import de.tomalbrc.bil.core.model.Model;

@SuppressWarnings("unused")
public interface AnimatedHolder {
    /**
     * Returns the intermediate of this holder.
     */
    Model getModel();

    /**
     * Returns the locator with the given name.
     */
    Locator getLocator(String name);

    /**
     * Returns the variant controller for this holder.
     */
    VariantController getVariantController();

    /**
     * Returns the animator for this holder.
     */
    Animator getAnimator();

    /**
     * Returns the scale of this holder.
     */
    float getScale();

    /**
     * Sets the scale of this holder.
     */
    void setScale(float scale);

    /**
     * Sets the color of this holder.
     * Only works if the item is dyeable and the intermediate faces have tintindex 0.
     */
    void setColor(int color);

    /**
     * Clears the color of this holder.
     * Only works if the item is dyeable and the intermediate faces have tintindex 0.
     */
    default void clearColor() {
        this.setColor(-1);
    }
}
