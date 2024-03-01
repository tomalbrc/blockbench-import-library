package de.tomalbrc.bil.core.holder.wrapper;

import de.tomalbrc.bil.core.holder.base.AbstractAnimationHolder;
import de.tomalbrc.bil.core.model.Node;
import de.tomalbrc.bil.core.model.Pose;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;

public class Locator extends AbstractWrapper {
    private final ObjectSet<LocatorListener> listeners;

    public static Locator of(Node node, Pose defaultPose) {
        return new Locator(node, defaultPose);
    }

    public Locator(Node node, Pose defaultPose) {
        super(node, defaultPose);
        this.listeners = ObjectSets.synchronize(new ObjectArraySet<>());
    }

    public boolean requiresUpdate() {
        return this.listeners.size() > 0;
    }

    public void updateListeners(AbstractAnimationHolder holder, Pose pose) {
        this.listeners.forEach(listener -> listener.update(holder, pose));
    }

    public void addListener(LocatorListener newListener) {
        this.listeners.add(newListener);
    }

    public void removeListener(LocatorListener oldListener) {
        this.listeners.remove(oldListener);
    }

    public void removeAllListeners() {
        this.listeners.clear();
    }

    public interface LocatorListener {
        /**
         * Called whenever a locator is updated.
         * This method can be called asynchronously.
         */
        void update(AbstractAnimationHolder holder, Pose pose);
    }
}
