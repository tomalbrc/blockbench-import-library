package de.tomalbrc.bil.core.element;

import eu.pb4.polymer.virtualentity.api.elements.GenericEntityElement;
import eu.pb4.polymer.virtualentity.api.tracker.EntityTrackedData;
import eu.pb4.polymer.virtualentity.mixin.SlimeEntityAccessor;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

public class CollisionElement extends GenericEntityElement {
    private InteractionHandler handler = InteractionHandler.EMPTY;

    public CollisionElement(InteractionHandler handler) {
        this.dataTracker.set(EntityTrackedData.SILENT, true);
        this.dataTracker.set(EntityTrackedData.NO_GRAVITY, true);
        this.dataTracker.set(EntityTrackedData.FLAGS, (byte) ((1 << EntityTrackedData.INVISIBLE_FLAG_INDEX)));
        this.setHandler(handler);
    }

    public static CollisionElement createWithRedirect(Entity redirectedEntity) {
        return new CollisionElement(new InteractionHandler() {
            @Override
            public void interact(ServerPlayer player, InteractionHand hand) {
                player.connection.handleInteract(ServerboundInteractPacket.createInteractionPacket(redirectedEntity, player.isShiftKeyDown(), hand));
            }

            @Override
            public void interactAt(ServerPlayer player, InteractionHand hand, Vec3 pos) {
                player.connection.handleInteract(ServerboundInteractPacket.createInteractionPacket(redirectedEntity, player.isShiftKeyDown(), hand, pos));
            }

            @Override
            public void attack(ServerPlayer player) {
                player.connection.handleInteract(ServerboundInteractPacket.createAttackPacket(redirectedEntity, player.isShiftKeyDown()));
            }
        });
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
        return this.dataTracker.get(SlimeEntityAccessor.getSLIME_SIZE());
    }

    public void setSize(int size) {
        this.dataTracker.set(SlimeEntityAccessor.getSLIME_SIZE(), size);
    }
}