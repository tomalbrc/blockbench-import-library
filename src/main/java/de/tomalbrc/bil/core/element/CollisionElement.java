package de.tomalbrc.bil.core.element;

import eu.pb4.polymer.virtualentity.api.data.EntityData;
import eu.pb4.polymer.virtualentity.api.elements.GenericEntityElement;
import eu.pb4.polymer.virtualentity.mixin.SlimeEntityAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class CollisionElement extends GenericEntityElement {
    private InteractionHandler handler = InteractionHandler.EMPTY;

    public CollisionElement(InteractionHandler handler) {
        this.syncedData.set(EntityData.SILENT, true);
        this.syncedData.set(EntityData.NO_GRAVITY, true);
        this.syncedData.set(EntityData.FLAGS, (byte) ((1 << EntityData.INVISIBLE_FLAG_INDEX)));
        this.setHandler(handler);
    }

    public static CollisionElement createWithRedirect(Entity redirectedEntity) {
        return new CollisionElement(InteractionHandler.redirect(redirectedEntity));
    }

    public void setHandler(InteractionHandler handler) {
        this.handler = handler;
    }

    @Override
    public InteractionHandler getInteractionHandler(ServerPlayer player) {
        return this.handler;
    }

    @Override
    protected final EntityType<? extends Entity> getEntityType() {
        return EntityType.SLIME;
    }

    public int getSize() {
        return this.syncedData.get(SlimeEntityAccessor.getID_SIZE());
    }

    public void setSize(int size) {
        this.syncedData.set(SlimeEntityAccessor.getID_SIZE(), size);
    }
}