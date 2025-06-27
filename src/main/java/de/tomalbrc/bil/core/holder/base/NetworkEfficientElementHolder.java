package de.tomalbrc.bil.core.holder.base;

import de.tomalbrc.bil.BIL;
import de.tomalbrc.bil.util.Utils;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class NetworkEfficientElementHolder extends ElementHolder {
    protected static final double FOV = 80 * Mth.DEG_TO_RAD; // assume fov of 160°
    protected static final double MAX_DISTANCE_SQR = Mth.square(9*16); // 8 chunks, always stop animating
    protected static final double MIN_DISTANCE_SQR = Mth.square(7*16); // 6 chunks, ignore fov

    protected final List<Packet<? super ClientGamePacketListener>> stagedPackets = new ObjectArrayList<>();

    @Override
    public void sendPacket(Packet<? extends ClientGamePacketListener> packet) {
        this.stagedPackets.add((Packet<? super ClientGamePacketListener>) packet);
    }

    @Override
    public void tick() {
        super.tick();
    }

    protected double getFov() {
        return FOV;
    }

    protected double getMaxAnimationDistance() {
        return MAX_DISTANCE_SQR;
    }

    protected double getMinAnimationDistance() {
        return MIN_DISTANCE_SQR;
    }

    protected boolean isInFov(ServerGamePacketListenerImpl player) {
        double dist = player.player.distanceToSqr(this.getPos());
        if (dist > this.getMaxAnimationDistance())
            return false;
        else if (dist <= this.getMinAnimationDistance())
            return true;

        Vec3 directionFacing = player.player.getViewVector(1).normalize();
        Vec3 directionToTarget = this.getPos().subtract(player.player.position()).normalize();

        Vec3 horizontalFacing = new Vec3(directionFacing.x, 0, directionFacing.z).normalize();
        Vec3 horizontalToTarget = new Vec3(directionToTarget.x, 0, directionToTarget.z).normalize();
        double horizontalAngle = Math.acos(horizontalFacing.dot(horizontalToTarget));
        if (horizontalAngle > this.getFov())
            return false;

        double pitchFacing = Math.asin(directionFacing.y);
        double pitchTarget = Math.asin(directionToTarget.y);
        double verticalAngle = Math.abs(pitchFacing - pitchTarget);
        return verticalAngle <= this.getFov();
    }

    protected List<Packet<? super ClientGamePacketListener>> filterForPlayer(List<Packet<? super ClientGamePacketListener>> packetList, ServerGamePacketListenerImpl packetListener) {
        if (!isInFov(packetListener)) {
            List<Packet<? super ClientGamePacketListener>> filteredList = new ObjectArrayList<>(packetList);
            filteredList.removeIf(packet -> packet instanceof ClientboundSetEntityDataPacket);
            return filteredList;
        }

        return packetList;
    }

    protected void flushPackets(ServerGamePacketListenerImpl[] watchingPlayers) {
        if (this.stagedPackets.isEmpty())
            return;

        for (int i = 0; i < watchingPlayers.length; i++) {
            var player = watchingPlayers[i];
            if (player != null) {
                var playerPackets = filterForPlayer(this.stagedPackets, player);
                var list = playerPackets != this.stagedPackets ? playerPackets : this.stagedPackets;
                if (playerPackets != this.stagedPackets) {
                    list = playerPackets;
                }

                for (var packet : list) {
                    sendPacketDirect(player, (Packet<? extends ClientGamePacketListener>) packet);
                }
            }
        }

        this.stagedPackets.clear();
    }

    public void sendPacketDirect(ServerGamePacketListenerImpl player, Packet<? extends ClientGamePacketListener> packet) {
        if (player != null) {
            if (BIL.SERVER.isSameThread()) {
                player.send(packet);
            } else {
                Utils.sendPacketNoFlush(player, packet);
            }
        }
    }

    @Override
    public void destroy() {
        this.flushPackets(this.getWatchingPlayers().toArray(new ServerGamePacketListenerImpl[0]));
        super.destroy();
    }
}
