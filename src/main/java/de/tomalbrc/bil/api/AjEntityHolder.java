package de.tomalbrc.bil.api;

import eu.pb4.polymer.virtualentity.api.elements.DisplayElement;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

@SuppressWarnings("unused")
public interface AjEntityHolder extends AjHolder {
    /**
     * Notifies the holder that the synchronized data has been updated.
     * <p>
     * Holder implementations can use this to update their element data.
     */
    void onSyncedDataUpdated(EntityDataAccessor<?> key, Object value);

    /**
     * Notifies the holder that the dimensions of the parent entity have been updated.
     * <p>
     * Holder implementations can use this to update their element data.
     */
    void onDimensionsUpdated(EntityDimensions dimensions);

    /**
     * Returns an array of entity ids used for displaying the intermediate.
     */
    int[] getDisplayIds();

    /**
     * Returns the entity id of the vehicle of the display entities.
     */
    int getDisplayVehicleId();

    /**
     * Returns the entity id of the vehicle used for mounting mobs and players.
     */
    int getVehicleId();

    /**
     * Returns the entity id that should be used for leashing mobs.
     * <p>
     * If the entity id is not from a {@link Mob} (clientside), the leash will not be rendered.
     */
    int getLeashedId();

    /**
     * Returns the entity id that should be used for handling entity events clientside.
     * <p>
     * Entity events are responsible certain entity particles, added by the client.
     * If for example the entity id is not from a {@link LivingEntity} on the client, death particles will not work.
     */
    int getEntityEventId();

    /**
     * Returns the entity id that should be used for handling critical hit particles.
     * <p>
     * If available, it is recommended to use the id of an element of approximately the same size as the intermediate.
     */
    int getCritParticleId();

    /**
     * Adds an additional display element to the holder.
     * This is needed to tell the holder to mount the display as a passenger on the display vehicle.
     */
    boolean addAdditionalDisplay(DisplayElement element);

    /**
     * Removes an additional display element from the holder.
     */
    boolean removeAdditionalDisplay(DisplayElement element);
}
