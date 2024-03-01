package de.tomalbrc.bil.holder.entity.living;

import de.tomalbrc.bil.api.AjEntity;
import de.tomalbrc.bil.model.Model;
import de.tomalbrc.bil.util.Utils;
import eu.pb4.polymer.virtualentity.api.elements.InteractionElement;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

/**
 * Extension of {@link LivingEntityHolder} that allows for custom passenger riding offsets.
 * <p>
 * This will use {@link Entity#getBbHeight} + {@link Entity#getMyRidingOffset} as the ride height.
 * <p>
 * It works by adding an extra zero bounding boxed {@link InteractionElement} to the parent entity,
 * which is used as the vehicle. This will add a very minor overhead on the client.
 */
public class LivingEntityHolderWithRideOffset<T extends LivingEntity & AjEntity> extends LivingEntityHolder<T> {
    private static final EntityDimensions ZERO = EntityDimensions.fixed(0, 0);
    protected final InteractionElement rideInteraction;

    public LivingEntityHolderWithRideOffset(T parent, Model model) {
        super(parent, model);

        this.rideInteraction = new InteractionElement();
        this.rideInteraction.setInvisible(true);
        this.addElement(this.rideInteraction);
    }

    protected float getRideOffset() {
        return this.parent.getBbHeight() + this.parent.getMyRidingOffset(this.parent);
    }

    @Override
    protected void startWatchingExtraPackets(ServerGamePacketListenerImpl player, Consumer<Packet<ClientGamePacketListener>> consumer) {
        super.startWatchingExtraPackets(player, consumer);

        for (var packet : Utils.updateClientInteraction(this.rideInteraction, ZERO, this.getRideOffset())) {
            consumer.accept(packet);
        }
    }

    @Override
    protected void addDirectPassengers(IntList passengers) {
        super.addDirectPassengers(passengers);
        passengers.add(this.rideInteraction.getEntityId());
    }

    @Override
    public void onDimensionsUpdated(EntityDimensions dimensions) {
        super.onDimensionsUpdated(dimensions);
        this.sendPacket(new ClientboundBundlePacket(Utils.updateClientInteraction(this.rideInteraction, ZERO, this.getRideOffset())));
    }

    @Override
    public int getVehicleId() {
        return this.rideInteraction.getEntityId();
    }
}
