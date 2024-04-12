package de.tomalbrc.bil.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.tomalbrc.bil.mixin.accessor.ServerCommonPacketListenerImplAccessor;
import eu.pb4.polymer.core.impl.networking.PacketPatcher;
import eu.pb4.polymer.virtualentity.api.elements.InteractionElement;
import eu.pb4.polymer.virtualentity.api.tracker.EntityTrackedData;
import eu.pb4.polymer.virtualentity.api.tracker.InteractionTrackedData;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;

import java.util.List;

public class Utils {
    public static final ServerGamePacketListenerImpl[] EMPTY_CONNECTION_ARRAY = new ServerGamePacketListenerImpl[0];

    public static Connection getConnection(ServerGamePacketListenerImpl networkHandler) {
        return ((ServerCommonPacketListenerImplAccessor) networkHandler).getConnection();
    }

    public static CommandSyntaxException buildCommandException(String message) {
        return new SimpleCommandExceptionType(Component.literal(message)).create();
    }

    public static int toSlimeSize(float size) {
        return Mth.floor(size / 2.04F / 0.255F);
    }

    public static boolean getSharedFlag(byte value, int flag) {
        return (value & 1 << flag) != 0;
    }

    public static List<Packet<ClientGamePacketListener>> updateClientInteraction(InteractionElement interaction, EntityDimensions dimensions) {
        return updateClientInteraction(interaction, dimensions, dimensions.height);
    }

    public static List<Packet<ClientGamePacketListener>> updateClientInteraction(InteractionElement interaction, EntityDimensions dimensions, float height) {
        // Updates the dimensions and bounding box of the interaction on the client. Note that the interactions dimensions and bounding box are two different things.
        // - The bounding box is primarily used for detecting player attacks, interactions and rendering the hitbox.
        // - The dimensions are used for certain other properties, such as the passenger riding height or the fire animation.
        return List.of(
                // We update the POSE in this packet, which makes the client refresh the interactions dimensions.
                // We use this to move the passenger riding height of the interaction upwards.
                new ClientboundSetEntityDataPacket(interaction.getEntityId(), List.of(
                        SynchedEntityData.DataValue.create(InteractionTrackedData.HEIGHT, height),
                        SynchedEntityData.DataValue.create(InteractionTrackedData.WIDTH, dimensions.width),
                        SynchedEntityData.DataValue.create(EntityTrackedData.POSE, Pose.STANDING)
                )),
                // Afterward, we send another packet that only updates the bounding box height back to its original value, without updating its dimensions.
                // This lets us turn the bounding box back into the correct size whilst keeping the raised passenger riding height.
                new ClientboundSetEntityDataPacket(interaction.getEntityId(), List.of(
                        SynchedEntityData.DataValue.create(InteractionTrackedData.HEIGHT, dimensions.height)
                ))
        );
    }

    /**
     * Vanilla + Polymer copy of { ServerCommonPacketListenerImpl#send(Packet, PacketSendListener)} but without flushing the connection.
     * <p>
     * we will often have to send a ton of separate packet from a different thread.
     * Even though we always make sure to start and finish this process before player connection flushing gets resumed at the end of the game tick,
     * the normal send method will still flush the connection for every packet, causing a significant downgrade in network performance and ping.
     */
    public static void sendPacketNoFlush(ServerGamePacketListenerImpl networkHandler, Packet<ClientGamePacketListener> packet) {
        Packet<ClientGamePacketListener> modifiedPacket = PacketPatcher.replace(networkHandler, packet);
        if (PacketPatcher.prevent(networkHandler, modifiedPacket)) {
            return;
        }

        try {
            Utils.getConnection(networkHandler).send(modifiedPacket);
        } catch (Throwable throwable) {
            CrashReport report = CrashReport.forThrowable(throwable, "Sending packet");
            CrashReportCategory category = report.addCategory("Packet being sent");
            category.setDetail("Packet class", () -> modifiedPacket.getClass().getCanonicalName());
            throw new ReportedException(report);
        }

        PacketPatcher.sendExtra(networkHandler, packet);
    }
}
