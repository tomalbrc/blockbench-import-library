package de.tomalbrc.bil.core.holder.entity.simple;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.holder.entity.EntityHolder;
import de.tomalbrc.bil.core.model.Model;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.entity.Entity;

public class SimpleEntityHolder<T extends Entity & AnimatedEntity> extends EntityHolder<T> {
    public SimpleEntityHolder(T parent, Model model) {
        super(parent, model);
    }

    @Override
    protected void addDirectPassengers(IntList passengers) {
        super.addDirectPassengers(passengers);
        for (int display : this.getDisplayIds()) {
            passengers.add(display);
        }
    }
}
