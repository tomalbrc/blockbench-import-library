package de.tomalbrc.bil.holder.entity.simple;

import de.tomalbrc.bil.api.AjEntity;
import de.tomalbrc.bil.holder.entity.EntityHolder;
import de.tomalbrc.bil.model.Model;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.entity.Entity;

public class SimpleEntityHolder<T extends Entity & AjEntity> extends EntityHolder<T> {
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
