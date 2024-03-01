package de.tomalbrc.bil.component;

import de.tomalbrc.bil.holder.base.AbstractAnimationHolder;
import de.tomalbrc.bil.model.Model;

public abstract class ComponentBase {
    protected final Model model;
    protected final AbstractAnimationHolder holder;

    public ComponentBase(Model model, AbstractAnimationHolder holder) {
        this.model = model;
        this.holder = holder;
    }
}
