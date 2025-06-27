package de.tomalbrc.bil.api;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

@SuppressWarnings("unused")
public interface Animator {
    int DEFAULT_PRIORITY = 1;

    /**
     * Starts playing an animation on the intermediate.
     *
     * @param name: The name of the animation.
     */
    default void playAnimation(String name) {
        this.playAnimation(null, name);
    }
    default void playAnimation(ServerPlayer serverPlayer, String name) {
        this.playAnimation(serverPlayer, name, DEFAULT_PRIORITY, false, null, null);
    }

    /**
     * Starts playing an animation on the intermediate.
     *
     * @param name:     The name of the animation.
     * @param priority: The priority of the animation.
     */
    default void playAnimation(String name, int priority) {
        this.playAnimation(null, name, priority);
    }
    default void playAnimation(ServerPlayer serverPlayer, String name, int priority) {
        this.playAnimation(serverPlayer, name, priority, false, null, null);
    }

    /**
     * Starts playing an animation on the intermediate.
     *
     * @param name:     The name of the animation.
     * @param onFinish: Callback that runs when the animation finishes.
     */
    default void playAnimation(String name, @Nullable Consumer<ServerPlayer> onFinish) {
        this.playAnimation(null, name, onFinish);
    }
    default void playAnimation(ServerPlayer serverPlayer, String name, @Nullable Consumer<ServerPlayer> onFinish) {
        this.playAnimation(name, DEFAULT_PRIORITY, false, null, onFinish);
    }

    /**
     * Starts playing an animation on the intermediate.
     *
     * @param name:     The name of the animation.
     * @param onFrame:  Callback that runs on each frame of the animation. The frame index is passed as argument.
     * @param onFinish: Callback that runs when the animation finishes.
     */
    default void playAnimation(String name, @Nullable IntConsumer onFrame, @Nullable Consumer<ServerPlayer> onFinish) {
        this.playAnimation(null, name, onFrame, onFinish);
    }
    default void playAnimation(ServerPlayer serverPlayer, String name, @Nullable IntConsumer onFrame, @Nullable Consumer<ServerPlayer> onFinish) {
        this.playAnimation(name, DEFAULT_PRIORITY, false, onFrame, onFinish);
    }

    /**
     * Starts playing an animation on the intermediate.
     *
     * @param name:          The name of the animation.
     * @param priority:      The priority of the animation.
     * @param restartPaused: Whether to restart paused animations, rather than resuming them where they left off.
     */
    default void playAnimation(String name, int priority, boolean restartPaused) {
        this.playAnimation(null, name, priority, restartPaused);
    }
    default void playAnimation(ServerPlayer serverPlayer, String name, int priority, boolean restartPaused) {
        this.playAnimation(serverPlayer, name, priority, restartPaused, null, null);
    }

    /**
     * Starts playing an animation on the intermediate.
     *
     * @param name:     The name of the animation.
     * @param priority: The priority of the animation.
     * @param onFinish: Callback that runs when the animation finishes.
     */
    default void playAnimation(String name, int priority, @Nullable Consumer<ServerPlayer> onFinish) {
        this.playAnimation(null, name, priority, false, null, onFinish);
    }
    default void playAnimation(ServerPlayer serverPlayer, String name, int priority, @Nullable Consumer<ServerPlayer> onFinish) {
        this.playAnimation(serverPlayer, name, priority, false, null, onFinish);
    }

    /**
     * Starts playing an animation on the intermediate.
     *
     * @param name:     The name of the animation.
     * @param priority: The priority of the animation.
     * @param onFrame:  Callback that runs on each frame of the animation. The frame index is passed as argument.
     * @param onFinish: Callback that runs when the animation finishes.
     */
    default void playAnimation(String name, int priority, @Nullable IntConsumer onFrame, @Nullable Consumer<ServerPlayer> onFinish) {
        playAnimation(null, name, priority, onFrame, onFinish);
    }
    default void playAnimation(ServerPlayer serverPlayer, String name, int priority, @Nullable IntConsumer onFrame, @Nullable Consumer<ServerPlayer> onFinish) {
        this.playAnimation(name, priority, false, onFrame, onFinish);
    }

    /**
     * Starts playing an animation on the intermediate.
     *
     * @param name:          The name of the animation.
     * @param restartPaused: Whether to restart paused animations, rather than resuming them where they left off.
     * @param onFrame:       Callback that runs on each frame of the animation. The frame index is passed as argument.
     */
    default void playAnimation(String name, boolean restartPaused, @Nullable IntConsumer onFrame) {
        playAnimation(null, name, restartPaused, onFrame);
    }
    default void playAnimation(ServerPlayer serverPlayer, String name, boolean restartPaused, @Nullable IntConsumer onFrame) {
        this.playAnimation(name, DEFAULT_PRIORITY, restartPaused, onFrame, null);
    }

    /**
     * Starts playing an animation on the intermediate.
     *
     * @param name:          The name of the animation.
     * @param restartPaused: Whether to restart paused animations, rather than resuming them where they left off.
     * @param onFinish:      Callback that runs when the animation finishes.
     */
    default void playAnimation(String name, boolean restartPaused, @Nullable Consumer<ServerPlayer> onFinish) {
        playAnimation(null, name, restartPaused, onFinish);
    }
    default void playAnimation(ServerPlayer serverPlayer, String name, boolean restartPaused, @Nullable Consumer<ServerPlayer> onFinish) {
        this.playAnimation(name, DEFAULT_PRIORITY, restartPaused, null, onFinish);
    }

    /**
     * Starts playing an animation on the intermediate.
     *
     * @param name:          The name of the animation.
     * @param restartPaused: Whether to restart paused animations, rather than resuming them where they left off.
     * @param onFrame:       Callback that runs on each frame of the animation. The frame index is passed as argument.
     * @param onFinish:      Callback that runs when the animation finishes.
     */
    default void playAnimation(String name, boolean restartPaused, @Nullable IntConsumer onFrame, @Nullable Consumer<ServerPlayer> onFinish) {
        playAnimation(null, name, restartPaused, onFrame, onFinish);
    }
    default void playAnimation(ServerPlayer serverPlayer, String name, boolean restartPaused, @Nullable IntConsumer onFrame, @Nullable Consumer<ServerPlayer> onFinish) {
        this.playAnimation(name, DEFAULT_PRIORITY, restartPaused, onFrame, onFinish);
    }

    /**
     * Starts playing an animation on the intermediate.
     *
     * @param name:          The name of the animation.
     * @param priority:      The priority of the animation.
     * @param restartPaused: Whether to restart paused animations, rather than resuming them where they left off.
     * @param onFrame:       Callback that runs on each frame of the animation. The frame index is passed as argument.
     * @param onFinish:      Callback that runs when the animation finishes.
     */
    default void playAnimation(String name, int priority, boolean restartPaused, @Nullable IntConsumer onFrame, @Nullable Consumer<ServerPlayer> onFinish) { playAnimation(null, name, priority, restartPaused, onFrame, onFinish); }
    void playAnimation(ServerPlayer serverPlayer, String name, int priority, boolean restartPaused, @Nullable IntConsumer onFrame, @Nullable Consumer<ServerPlayer> onFinish);

    /**
     * Sets the current frame of an animation.
     *
     * @param name:  The name of the animation.
     * @param frame: The frame to set the animation to. A negative value will delay the animation from being played.
     */
    default void setAnimationFrame(String name, int frame) { setAnimationFrame(null, name, frame); }
    void setAnimationFrame(ServerPlayer serverPlayer, String name, int frame);

    /**
     * Pauses an animation on the intermediate.
     *
     * @param name: The name of the animation.
     */
    default void pauseAnimation(String name) { pauseAnimation(null, name); }
    void pauseAnimation(ServerPlayer serverPlayer, String name);

    /**
     * Stops an animation from playing on the intermediate.
     *
     * @param name: The name of the animation.
     */
    default void stopAnimation(String name) { stopAnimation(null, name); }
    void stopAnimation(ServerPlayer serverPlayer, String name);

    default boolean isPlaying(String name) { return isPlaying(null, name); }
    boolean isPlaying(ServerPlayer serverPlayer, String name);

    default boolean hasRunningAnimations() { return hasRunningAnimations(null); }
    boolean hasRunningAnimations(ServerPlayer serverPlayer);
}
