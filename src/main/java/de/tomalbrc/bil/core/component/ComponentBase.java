package de.tomalbrc.bil.core.component;

import de.tomalbrc.bil.core.holder.base.AbstractAnimationHolder;
import de.tomalbrc.bil.core.model.Model;

public abstract class ComponentBase {
    protected final Model model;
    protected final AbstractAnimationHolder holder;

    public ComponentBase(Model model, AbstractAnimationHolder holder) {
        this.model = model;
        this.holder = holder;
    }
}
