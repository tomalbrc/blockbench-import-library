package de.tomalbrc.bil.core.component;

import de.tomalbrc.bil.api.Animator;
import de.tomalbrc.bil.core.holder.base.AbstractAnimationHolder;
import de.tomalbrc.bil.core.holder.wrapper.AbstractWrapper;
import de.tomalbrc.bil.core.model.Animation;
import de.tomalbrc.bil.core.model.Frame;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.core.model.Pose;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class AnimationComponent extends ComponentBase implements Animator {
    private final Map<String, AnimationPlayer> animationMap = new Object2ReferenceOpenHashMap<>();
    private final Map<ServerPlayer, Map<String, AnimationPlayer>> perPlayerAnimationMap = new Object2ReferenceOpenHashMap<>();

    private final List<AnimationPlayer> animationPlayerList = new CopyOnWriteArrayList<>();

    public AnimationComponent(Model model, AbstractAnimationHolder holder) {
        super(model, holder);
    }

    private Map<String, AnimationPlayer> animationMap(ServerPlayer serverPlayer) {
        return serverPlayer == null ? animationMap : perPlayerAnimationMap.computeIfAbsent(serverPlayer, (x) -> new Object2ObjectOpenHashMap<>());
    }

    private AnimationPlayer removeFromAnimationMap(ServerPlayer serverPlayer, String name) {
        if (serverPlayer == null) {
            return animationMap.remove(name);
        }

        var map = perPlayerAnimationMap.get(serverPlayer);
        if (map != null) {
            var val = map.remove(name);
            if (map.isEmpty())
                perPlayerAnimationMap.remove(serverPlayer);
            return val;
        }
        return null;
    }

    @Override
    public void playAnimation(ServerPlayer serverPlayer, String name, int priority, boolean restartPaused, IntConsumer onFrame, Consumer<ServerPlayer> onFinish) {
        Map<String, AnimationPlayer> map = this.animationMap(serverPlayer);

        AnimationPlayer animationPlayer = map.get(name);
        if (priority < 0) {
            priority = 0;
        }

        if (animationPlayer == null) {
            Animation animation = this.model.animations().get(name);
            if (animation != null) {
                if (serverPlayer != null) holder.addBoneDataTracker(serverPlayer);

                this.addAnimationPlayer(new AnimationPlayer(serverPlayer, name, animation, this.holder, priority, onFrame, (serverPlayer1) -> {
                    if (serverPlayer1 != null && !this.hasRunningAnimationsSinglePlayer(serverPlayer1))
                        holder.resetBoneDataTracker(serverPlayer1);
                    if (onFinish != null)
                        onFinish.accept(serverPlayer1);
                }));
            }
        } else {
            // Update values of the existing animation.
            animationPlayer.onFrameCallback = onFrame;
            animationPlayer.onFinishCallback = onFinish;

            if (animationPlayer.state == AnimationPlayer.State.PAUSED) {
                if (restartPaused) {
                    animationPlayer.resetFrameCounter(false);
                }
                animationPlayer.state = AnimationPlayer.State.PLAYING;
            }

            if (priority != animationPlayer.priority) {
                animationPlayer.priority = priority;
                Collections.sort(this.animationPlayerList);
            }
        }
    }

    @Override
    public void setAnimationFrame(ServerPlayer serverPlayer, String name, int frame) {
        AnimationPlayer animationPlayer = this.animationMap(serverPlayer).get(name);
        if (animationPlayer != null) {
            animationPlayer.skipToFrame(frame);
        }
    }

    @Override
    public void pauseAnimation(ServerPlayer serverPlayer, String name) {
        AnimationPlayer animationPlayer = this.animationMap(serverPlayer).get(name);
        if (animationPlayer != null && animationPlayer.state == AnimationPlayer.State.PLAYING) {
            animationPlayer.state = AnimationPlayer.State.PAUSED;
        }
    }

    @Override
    public void stopAnimation(ServerPlayer serverPlayer, String name) {
        AnimationPlayer animationPlayer = removeFromAnimationMap(serverPlayer, name);
        if (animationPlayer != null) {
            this.animationPlayerList.remove(animationPlayer);
        }
    }

    private void addAnimationPlayer(AnimationPlayer animationPlayer) {
        this.animationMap(animationPlayer.getOwner()).put(animationPlayer.name, animationPlayer);

        if (this.animationPlayerList.size() > 0 && animationPlayer.priority > 0) {
            int index = Collections.binarySearch(this.animationPlayerList, animationPlayer);
            this.animationPlayerList.add(index < 0 ? -index - 1 : index, animationPlayer);
        } else {
            this.animationPlayerList.add(animationPlayer);
        }
    }

    public void tickAnimations() {
        for (int index = this.animationPlayerList.size() - 1; index >= 0; index--) {
            AnimationPlayer animationPlayer = this.animationPlayerList.get(index);
            if (animationPlayer.hasFinished()) {
                ServerPlayer serverPlayer = animationPlayer.getOwner();
                removeFromAnimationMap(serverPlayer, animationPlayer.name);
                this.animationPlayerList.remove(index);
                animationPlayer.onFinished(serverPlayer);
            } else {
                animationPlayer.tick(animationPlayer.getOwner());
            }
        }
    }

    @Nullable
    public PoseQueryResult findPose(ServerPlayer serverPlayer, AbstractWrapper wrapper) {
        UUID uuid = wrapper.node().uuid();
        PoseQueryResult queryResult = null;

        for (int i = 0; i < this.animationPlayerList.size(); i++) {
            AnimationPlayer animationPlayer = this.animationPlayerList.get(i);
            if ((animationPlayer.owner == null || animationPlayer.owner == serverPlayer) && this.canAnimationAffect(animationPlayer, uuid)) {
                if (animationPlayer.inResetState()) {
                    queryResult = new PoseQueryResult(wrapper.getDefaultPose(), animationPlayer.owner);
                } else {
                    var animationPose = this.findAnimationPose(wrapper, animationPlayer, uuid);
                    if (animationPose != null) {
                        queryResult = new PoseQueryResult(animationPose, animationPlayer.owner);
                    }
                }
            }
        }

        if (queryResult != null) {
            wrapper.setLastPose(queryResult.owner, queryResult.pose, null);
        }

        return queryResult;
    }

    private boolean canAnimationAffect(AnimationPlayer anim, UUID uuid) {
        final boolean canAnimate = anim.inResetState() || anim.shouldAnimate();
        return canAnimate && anim.animation.isAffected(uuid);
    }

    @Nullable
    private Pose findAnimationPose(AbstractWrapper wrapper, AnimationPlayer anim, UUID uuid) {
        Animation animation = anim.animation;
        Frame frame = anim.currentFrame;
        if (frame == null) {
            return null;
        }

        Pose pose = frame.poses().get(uuid);
        if (pose != null) {
            wrapper.setLastPose(anim.getOwner(), pose, animation);
            return pose;
        }

        if (animation == wrapper.getLastAnimation(anim.getOwner())) {
            return wrapper.getLastPose(anim.getOwner());
        }

        // Since the animation just switched, the last known pose is no longer valid.
        // To ensure that this node still gets updated properly, we must backtrack the new animation to find a valid pose.
        // This should preferably be avoided as much as possible, as it is a bit expensive.
        final Frame[] frames = animation.frames();
        final int startIndex = (frames.length - 1) - Math.max(anim.frameCounter - 1, 0);

        for (int i = startIndex; i >= 0; i--) {
            pose = frames[i].poses().get(uuid);
            if (pose != null) {
                wrapper.setLastPose(anim.getOwner(), pose, animation);
                return pose;
            }
        }
        return null;
    }

    @Override
    public boolean isPlaying(ServerPlayer serverPlayer, String name) {
        return this.animationMap(serverPlayer).containsKey(name);
    }

    @Override
    public boolean hasRunningAnimations(ServerPlayer serverPlayer) {
        return this.animationMap(serverPlayer) != null && !this.animationMap(serverPlayer).isEmpty();
    }

    public boolean hasRunningAnimationsSinglePlayer(ServerPlayer serverPlayer) {
        var map = this.perPlayerAnimationMap.get(serverPlayer);
        return map != null && !map.isEmpty();
    }

    public record PoseQueryResult(@Nullable Pose pose, @Nullable ServerPlayer owner) {}

    static class AnimationPlayer implements Comparable<AnimationPlayer> {
        @NotNull
        private final Animation animation;
        private final AbstractAnimationHolder holder;
        private final String name;

        private Frame currentFrame;
        private int frameCounter = -1;
        private int priority;
        private boolean looped;
        private State state;

        @Nullable
        private IntConsumer onFrameCallback;
        @Nullable
        private Consumer<ServerPlayer> onFinishCallback;

        @Nullable
        private final ServerPlayer owner;

        private AnimationPlayer(@Nullable ServerPlayer owner, String name, @NotNull Animation animation, AbstractAnimationHolder holder, int priority, @Nullable IntConsumer onFrame, @Nullable Consumer<ServerPlayer> onFinish) {
            this.name = name;
            this.holder = holder;
            this.animation = animation;
            this.state = State.PLAYING;
            this.priority = priority;
            this.onFrameCallback = onFrame;
            this.onFinishCallback = onFinish;
            this.resetFrameCounter(false);
            this.owner = owner;
        }

        public @Nullable ServerPlayer getOwner() {
            return this.owner;
        }

        private void onFinished(ServerPlayer serverPlayer) {
            if (this.onFinishCallback != null) {
                this.onFinishCallback.accept(serverPlayer);
            }
        }

        private void tick(ServerPlayer serverPlayer) {
            if (this.frameCounter < 0) {
                this.onFramesFinished();
                return;
            }

            if (this.shouldAnimate()) {
                this.updateFrame(serverPlayer);
                this.frameCounter--;
            }
        }

        private void updateFrame(ServerPlayer serverPlayer) {
            Frame[] frames = this.animation.frames();
            if (this.frameCounter >= 0 && this.frameCounter < frames.length) {
                int index = (frames.length - 1) - this.frameCounter;
                this.currentFrame = frames[index];

                if (this.onFrameCallback != null) {
                    this.onFrameCallback.accept(index);
                }

                if (this.currentFrame.requiresUpdates()) {
                    this.currentFrame.runEffects(serverPlayer, this.holder);
                }
            }
        }

        private void skipToFrame(int frame) {
            this.frameCounter = this.animation.duration() - 1 - frame;
        }

        private void resetFrameCounter(boolean isLooping) {
            this.frameCounter = this.animation.duration() - 1 + (isLooping ? this.animation.loopDelay() : this.animation.startDelay());
        }

        private void onFramesFinished() {
            switch (this.animation.loopMode()) {
                case ONCE -> {
                    if (this.state == State.FINISHED_RESET_DEFAULT) {
                        this.state = State.FINISHED;
                    } else {
                        this.state = State.FINISHED_RESET_DEFAULT;
                    }
                }
                case HOLD -> this.state = State.FINISHED;
                case LOOP -> {
                    this.resetFrameCounter(true);
                    this.looped = true;
                }
            }
        }

        private boolean inLoopDelay() {
            return this.animation.loopDelay() > 0 && this.looped && this.frameCounter >= this.animation.duration() - this.animation.loopDelay();
        }

        private boolean inStartDelay() {
            return this.animation.startDelay() > 0 && this.frameCounter >= this.animation.duration() - (this.looped ? 0 : this.animation.startDelay());
        }

        public boolean inResetState() {
            return this.state == State.FINISHED_RESET_DEFAULT;
        }

        public boolean hasFinished() {
            return this.state == State.FINISHED;
        }

        public boolean shouldAnimate() {
            return this.state != State.PAUSED && this.state != State.FINISHED && !this.inLoopDelay() && !this.inStartDelay();
        }

        @Override
        public int compareTo(@NotNull AnimationPlayer other) {
            return Integer.compare(other.priority, this.priority);
        }

        private enum State {
            PLAYING,
            PAUSED,
            FINISHED_RESET_DEFAULT,
            FINISHED,
        }
    }
}
