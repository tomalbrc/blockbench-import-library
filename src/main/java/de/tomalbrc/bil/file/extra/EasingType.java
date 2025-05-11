package de.tomalbrc.bil.file.extra;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public enum EasingType {

    @SerializedName("linear")
    LINEAR((t, args) -> t),

    @SerializedName("easeInSine")
    EASE_IN_SINE((t, args) -> -1 * Math.cos(t * (Math.PI / 2)) + 1),

    @SerializedName("easeOutSine")
    EASE_OUT_SINE((t, args) -> Math.sin(t * (Math.PI / 2))),

    @SerializedName("easeInOutSine")
    EASE_IN_OUT_SINE((t, args) -> -0.5 * (Math.cos(Math.PI * t) - 1)),

    @SerializedName("easeInQuad")
    EASE_IN_QUAD((t, args) -> t * t),

    @SerializedName("easeOutQuad")
    EASE_OUT_QUAD((t, args) -> t * (2 - t)),

    @SerializedName("easeInOutQuad")
    EASE_IN_OUT_QUAD((t, args) -> t < 0.5 ? 2 * t * t : -1 + (4 - 2 * t) * t),

    @SerializedName("easeInCubic")
    EASE_IN_CUBIC((t, args) -> t * t * t),

    @SerializedName("easeOutCubic")
    EASE_OUT_CUBIC((t, args) -> {
        double t1 = t - 1;
        return t1 * t1 * t1 + 1;
    }),

    @SerializedName("easeInOutCubic")
    EASE_IN_OUT_CUBIC((t, args) -> t < 0.5 ? 4 * t * t * t : (t - 1) * (2 * t - 2) * (2 * t - 2) + 1),

    @SerializedName("easeInBack")
    EASE_IN_BACK((t, args) -> {
        double s = args.length > 0 ? args[0] : 1.70158;
        return t * t * ((s + 1) * t - s);
    }),

    @SerializedName("easeOutBack")
    EASE_OUT_BACK((t, args) -> {
        double s = args.length > 0 ? args[0] : 1.70158;
        double t1 = t - 1;
        return t1 * t1 * ((s + 1) * t1 + s) + 1;
    }),

    @SerializedName("easeInOutBack")
    EASE_IN_OUT_BACK((t, args) -> {
        double s = (args.length > 0 ? args[0] : 1.70158) * 1.525;
        double t2 = t * 2;
        if (t2 < 1) {
            return 0.5 * (t2 * t2 * ((s + 1) * t2 - s));
        } else {
            t2 -= 2;
            return 0.5 * (t2 * t2 * ((s + 1) * t2 + s) + 2);
        }
    }),

    @SerializedName("easeInElastic")
    EASE_IN_ELASTIC((t, args) -> {
        if (t == 0 || t == 1) return t;
        double magnitude = args.length > 0 ? args[0] : 0.7;
        double p = 1 - magnitude;
        double s = p / (2 * Math.PI) * Math.asin(1);
        t -= 1;
        return -Math.pow(2, 10 * t) * Math.sin((t - s) * (2 * Math.PI) / p);
    }),

    @SerializedName("easeOutElastic")
    EASE_OUT_ELASTIC((t, args) -> {
        if (t == 0 || t == 1) return t;
        double magnitude = args.length > 0 ? args[0] : 0.7;
        double p = 1 - magnitude;
        double s = p / (2 * Math.PI) * Math.asin(1);
        return Math.pow(2, -10 * t) * Math.sin((t - s) * (2 * Math.PI) / p) + 1;
    }),

    @SerializedName("easeInOutElastic")
    EASE_IN_OUT_ELASTIC((t, args) -> {
        if (t == 0 || t == 1) return t;
        double magnitude = args.length > 0 ? args[0] : 0.65;
        double p = 1 - magnitude;
        double s = p / (2 * Math.PI) * Math.asin(1);
        double t2 = t * 2 - 1;
        if (t * 2 < 1) {
            return -0.5 * Math.pow(2, 10 * t2) * Math.sin((t2 - s) * (2 * Math.PI) / p);
        }
        return Math.pow(2, -10 * t2) * Math.sin((t2 - s) * (2 * Math.PI) / p) * 0.5 + 1;
    });

    public interface EasingFunction {
        double apply(double t, double[] args);
    }

    public final EasingFunction function;

    EasingType(EasingFunction function) {
        this.function = function;
    }

    public double apply(double t, double... args) {
        return function.apply(t, args);
    }
}