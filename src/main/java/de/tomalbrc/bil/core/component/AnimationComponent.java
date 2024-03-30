package de.tomalbrc.bil.core.component;

import de.tomalbrc.bil.api.Animator;
import de.tomalbrc.bil.core.holder.base.AbstractAnimationHolder;
import de.tomalbrc.bil.core.holder.wrapper.AbstractWrapper;
import de.tomalbrc.bil.core.model.Animation;
import de.tomalbrc.bil.core.model.Frame;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.core.model.Pose;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.IntConsumer;

public class AnimationComponent extends ComponentBase implements Animator {
    private final Object2ObjectOpenHashMap<String, AnimationPlayer> animationMap = new Object2ObjectOpenHashMap<>();
    private final CopyOnWriteArrayList<AnimationPlayer> animationPlayerList = new CopyOnWriteArrayList<>();

    public AnimationComponent(Model model, AbstractAnimationHolder holder) {
        super(model, holder);
    }

    @Override
    public void playAnimation(String name, int priority, boolean restartPaused, IntConsumer onFrame, Runnable onFinish) {
        AnimationPlayer animationPlayer = this.animationMap.get(name);
        if (priority < 0) {
            priority = 0;
        }

        if (animationPlayer == null) {
            Animation animation = this.model.animations().get(name);
            if (animation != null) {
                this.addAnimationPlayer(new AnimationPlayer(name, animation, this.holder, priority, onFrame, onFinish));
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
    public void setAnimationFrame(String name, int frame) {
        AnimationPlayer animationPlayer = this.animationMap.get(name);
        if (animationPlayer != null) {
            animationPlayer.skipToFrame(frame);
        }
    }

    @Override
    public void pauseAnimation(String name) {
        AnimationPlayer animationPlayer = this.animationMap.get(name);
        if (animationPlayer != null && animationPlayer.state == AnimationPlayer.State.PLAYING) {
            animationPlayer.state = AnimationPlayer.State.PAUSED;
        }
    }

    @Override
    public void stopAnimation(String name) {
        AnimationPlayer animationPlayer = this.animationMap.remove(name);
        if (animationPlayer != null) {
            this.animationPlayerList.remove(animationPlayer);
        }
    }

    private void addAnimationPlayer(AnimationPlayer animationPlayer) {
        this.animationMap.put(animationPlayer.name, animationPlayer);

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
                this.animationMap.remove(animationPlayer.name);
                this.animationPlayerList.remove(index);
                animationPlayer.onFinished();
            } else {
                animationPlayer.tick();
            }
        }
    }

    @Nullable
    public Pose findPose(AbstractWrapper wrapper) {
        UUID uuid = wrapper.node().uuid();
        Pose pose = null;

        for (AnimationPlayer animationPlayer : this.animationPlayerList) {
            if (this.canAnimationAffect(animationPlayer, uuid)) {
                if (animationPlayer.inResetState()) {
                    pose = wrapper.getDefaultPose();
                } else {
                    pose = this.findAnimationPose(wrapper, animationPlayer, uuid);
                    if (pose != null) {
                        return pose;
                    }
                }
            }
        }

        if (pose != null) {
            wrapper.setLastPose(pose, null);
        }

        return pose;
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
            wrapper.setLastPose(pose, animation);
            return pose;
        }

        if (animation == wrapper.getLastAnimation()) {
            return wrapper.getLastPose();
        }

        // Since the animation just switched, the last known pose is no longer valid.
        // To ensure that this node still gets updated properly, we must backtrack the new animation to find a valid pose.
        // This should preferably be avoided as much as possible, as it is a bit expensive.
        final Frame[] frames = animation.frames();
        final int startIndex = (frames.length - 1) - Math.max(anim.frameCounter - 1, 0);

        for (int i = startIndex; i >= 0; i--) {
            pose = frames[i].poses().get(uuid);
            if (pose != null) {
                wrapper.setLastPose(pose, animation);
                return pose;
            }
        }
        return null;
    }

    private static class AnimationPlayer implements Comparable<AnimationPlayer> {
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
        private Runnable onFinishCallback;

        private AnimationPlayer(String name, @NotNull Animation animation, AbstractAnimationHolder holder, int priority, @Nullable IntConsumer onFrame, @Nullable Runnable onFinish) {
            this.name = name;
            this.holder = holder;
            this.animation = animation;
            this.state = State.PLAYING;
            this.priority = priority;
            this.onFrameCallback = onFrame;
            this.onFinishCallback = onFinish;
            this.resetFrameCounter(false);
        }

        private void onFinished() {
            if (this.onFinishCallback != null) {
                this.onFinishCallback.run();
            }
        }

        private void tick() {
            if (this.frameCounter < 0) {
                this.onFramesFinished();
                return;
            }

            if (this.shouldAnimate()) {
                this.updateFrame();
                this.frameCounter--;
            }
        }

        private void updateFrame() {
            Frame[] frames = this.animation.frames();
            if (this.frameCounter >= 0 && this.frameCounter < frames.length) {
                int index = (frames.length - 1) - this.frameCounter;
                this.currentFrame = frames[index];

                if (this.onFrameCallback != null) {
                    this.onFrameCallback.accept(index);
                }

                if (this.currentFrame.requiresUpdates()) {
                    this.currentFrame.runEffects(this.holder);
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
                case HOLD -> {
                    this.state = State.FINISHED;
                }
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
