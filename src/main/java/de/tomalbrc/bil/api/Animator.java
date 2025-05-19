package de.tomalbrc.bil.api;

import org.jetbrains.annotations.Nullable;

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
        this.playAnimation(name, DEFAULT_PRIORITY, false, null, (Runnable) null);
    }

    /**
     * Starts playing an animation on the intermediate.
     *
     * @param name:     The name of the animation.
     * @param priority: The priority of the animation.
     */
    default void playAnimation(String name, int priority) {
        this.playAnimation(name, priority, false, null, (Runnable) null);
    }

    /**
     * Starts playing an animation on the intermediate.
     *
     * @param name:          The name of the animation.
     * @param restartPaused: Whether to restart paused animations, rather than resuming them where they left off.
     */
    default void playAnimation(String name, boolean restartPaused) {
        this.playAnimation(name, DEFAULT_PRIORITY, restartPaused, null, (Runnable) null);
    }

    /**
     * Starts playing an animation on the intermediate.
     *
     * @param name:    The name of the animation.
     * @param onFrame: Callback that runs on each frame of the animation. The frame index is passed as argument.
     */
    default void playAnimation(String name, @Nullable IntConsumer onFrame) {
        this.playAnimation(name, DEFAULT_PRIORITY, false, onFrame, null);
    }

    /**
     * Starts playing an animation on the intermediate.
     *
     * @param name:     The name of the animation.
     * @param onFinish: Callback that runs when the animation finishes.
     */
    default void playAnimation(String name, @Nullable Runnable onFinish) {
        this.playAnimation(name, DEFAULT_PRIORITY, false, null, onFinish);
    }

    /**
     * Starts playing an animation on the intermediate.
     *
     * @param name:     The name of the animation.
     * @param onFrame:  Callback that runs on each frame of the animation. The frame index is passed as argument.
     * @param onFinish: Callback that runs when the animation finishes.
     */
    default void playAnimation(String name, @Nullable IntConsumer onFrame, @Nullable Runnable onFinish) {
        this.playAnimation(name, DEFAULT_PRIORITY, false, onFrame, onFinish);
    }

    /**
     * Starts playing an animation on the intermediate.
     *
     * @param name:     The name of the animation.
     * @param onFrame:  Callback that runs on each frame of the animation. The frame index is passed as argument.
     * @param onFinish: Callback that runs when the animation finishes.
     */
    default void playAnimation(String name, @Nullable Runnable onFinish, @Nullable IntConsumer onFrame) {
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
        this.playAnimation(name, priority, restartPaused, null, (Runnable) null);
    }

    /**
     * Starts playing an animation on the intermediate.
     *
     * @param name:     The name of the animation.
     * @param priority: The priority of the animation.
     * @param onFrame:  Callback that runs on each frame of the animation. The frame index is passed as argument.
     */
    default void playAnimation(String name, int priority, @Nullable IntConsumer onFrame) {
        this.playAnimation(name, priority, false, onFrame, null);
    }

    /**
     * Starts playing an animation on the intermediate.
     *
     * @param name:     The name of the animation.
     * @param priority: The priority of the animation.
     * @param onFinish: Callback that runs when the animation finishes.
     */
    default void playAnimation(String name, int priority, @Nullable Runnable onFinish) {
        this.playAnimation(name, priority, false, null, onFinish);
    }

    /**
     * Starts playing an animation on the intermediate.
     *
     * @param name:     The name of the animation.
     * @param priority: The priority of the animation.
     * @param onFrame:  Callback that runs on each frame of the animation. The frame index is passed as argument.
     * @param onFinish: Callback that runs when the animation finishes.
     */
    default void playAnimation(String name, int priority, @Nullable IntConsumer onFrame, @Nullable Runnable onFinish) {
        this.playAnimation(name, priority, false, onFrame, onFinish);
    }

    /**
     * Starts playing an animation on the intermediate.
     *
     * @param name:     The name of the animation.
     * @param priority: The priority of the animation.
     * @param onFrame:  Callback that runs on each frame of the animation. The frame index is passed as argument.
     * @param onFinish: Callback that runs when the animation finishes.
     */
    default void playAnimation(String name, int priority, @Nullable Runnable onFinish, @Nullable IntConsumer onFrame) {
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
        this.playAnimation(name, DEFAULT_PRIORITY, restartPaused, onFrame, null);
    }

    /**
     * Starts playing an animation on the intermediate.
     *
     * @param name:          The name of the animation.
     * @param restartPaused: Whether to restart paused animations, rather than resuming them where they left off.
     * @param onFinish:      Callback that runs when the animation finishes.
     */
    default void playAnimation(String name, boolean restartPaused, @Nullable Runnable onFinish) {
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
    default void playAnimation(String name, boolean restartPaused, @Nullable IntConsumer onFrame, @Nullable Runnable onFinish) {
        this.playAnimation(name, DEFAULT_PRIORITY, restartPaused, onFrame, onFinish);
    }

    /**
     * Starts playing an animation on the intermediate.
     *
     * @param name:          The name of the animation.
     * @param restartPaused: Whether to restart paused animations, rather than resuming them where they left off.
     * @param onFrame:       Callback that runs on each frame of the animation. The frame index is passed as argument.
     * @param onFinish:      Callback that runs when the animation finishes.
     */
    default void playAnimation(String name, boolean restartPaused, @Nullable Runnable onFinish, @Nullable IntConsumer onFrame) {
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
    default void playAnimation(String name, int priority, boolean restartPaused, @Nullable Runnable onFinish, @Nullable IntConsumer onFrame) {
        this.playAnimation(name, priority, restartPaused, onFrame, onFinish);
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
    void playAnimation(String name, int priority, boolean restartPaused, @Nullable IntConsumer onFrame, @Nullable Runnable onFinish);

    /**
     * Sets the current frame of an animation.
     *
     * @param name:  The name of the animation.
     * @param frame: The frame to set the animation to. A negative value will delay the animation from being played.
     */
    void setAnimationFrame(String name, int frame);

    /**
     * Pauses an animation on the intermediate.
     *
     * @param name: The name of the animation.
     */
    void pauseAnimation(String name);

    /**
     * Stops an animation from playing on the intermediate.
     *
     * @param name: The name of the animation.
     */
    void stopAnimation(String name);

    boolean isPlaying(String name);

    boolean hasRunningAnimations();
}
