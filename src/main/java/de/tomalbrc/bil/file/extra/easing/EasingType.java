package de.tomalbrc.bil.file.extra.easing;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public enum EasingType {
    @SerializedName("linear")
    LINEAR((t, args) -> t),

    @SerializedName("quad")
    QUAD((t, args) -> t * t),

    @SerializedName("cubic")
    CUBIC((t, args) -> t * t * t),

    @SerializedName("poly")
    POLY((t, args) -> {
        double n = args.length > 0 ? args[0] : 2;
        return Math.pow(t, n);
    }),

    @SerializedName("sin")
    SIN((t, args) -> 1 - Math.cos((t * Math.PI) / 2)),

    @SerializedName("circle")
    CIRCLE((t, args) -> 1 - Math.sqrt(1 - t * t)),

    @SerializedName("exp")
    EXP((t, args) -> Math.pow(2, 10 * (t - 1))),

    @SerializedName("elastic")
    ELASTIC((t, args) -> {
        double b = args.length > 0 ? args[0] : 1;
        double p = b * Math.PI;
        return 1 - Math.pow(Math.cos((t * Math.PI) / 2), 3) * Math.cos(t * p);
    }),

    @SerializedName("back")
    BACK((t, args) -> {
        double s = args.length > 0 ? args[0] : 1.70158;
        return t * t * ((s + 1) * t - s);
    }),

    @SerializedName("bounce")
    BOUNCE((t, args) -> {
        double k = args.length > 0 ? args[0] : 0.5;
        double q = (121.0 / 16) * t * t;
        double w = (121.0 / 4) * k * Math.pow(t - 6.0 / 11.0, 2) + 1 - k;
        double r = 121 * k * k * Math.pow(t - 9.0 / 11.0, 2) + 1 - k*k;
        double u = 484 * k*k*k * Math.pow(t - 10.5/11.0, 2) + 1 - k*k*k;
        return Math.min(Math.min(q, w), Math.min(r, u));
    }),

    @SerializedName("easeInQuad")
    EASE_IN_QUAD(in_(QUAD.function)),

    @SerializedName("easeOutQuad")
    EASE_OUT_QUAD(out(QUAD.function)),

    @SerializedName("easeInOutQuad")
    EASE_IN_OUT_QUAD(inOut(QUAD.function)),

    @SerializedName("easeInCubic")
    EASE_IN_CUBIC(in_(CUBIC.function)),

    @SerializedName("easeOutCubic")
    EASE_OUT_CUBIC(out(CUBIC.function)),

    @SerializedName("easeInOutCubic")
    EASE_IN_OUT_CUBIC(inOut(CUBIC.function)),

    @SerializedName("easeInQuart")
    EASE_IN_QUART(in_((t, a) -> Math.pow(t, 4))),

    @SerializedName("easeOutQuart")
    EASE_OUT_QUART(out((t, a) -> Math.pow(t, 4))),

    @SerializedName("easeInOutQuart")
    EASE_IN_OUT_QUART(inOut((t, a) -> Math.pow(t, 4))),

    @SerializedName("easeInQuint")
    EASE_IN_QUINT(in_((t, a) -> Math.pow(t, 5))),

    @SerializedName("easeOutQuint")
    EASE_OUT_QUINT(out((t, a) -> Math.pow(t, 5))),

    @SerializedName("easeInOutQuint")
    EASE_IN_OUT_QUINT(inOut((t, a) -> Math.pow(t, 5))),

    @SerializedName("easeInSine")
    EASE_IN_SINE(in_(SIN.function)),

    @SerializedName("easeOutSine")
    EASE_OUT_SINE(out(SIN.function)),

    @SerializedName("easeInOutSine")
    EASE_IN_OUT_SINE(inOut(SIN.function)),

    @SerializedName("easeInCirc")
    EASE_IN_CIRC(in_(CIRCLE.function)),

    @SerializedName("easeOutCirc")
    EASE_OUT_CIRC(out(CIRCLE.function)),

    @SerializedName("easeInOutCirc")
    EASE_IN_OUT_CIRC(inOut(CIRCLE.function)),

    @SerializedName("easeInExpo")
    EASE_IN_EXPO(in_(EXP.function)),

    @SerializedName("easeOutExpo")
    EASE_OUT_EXPO(out(EXP.function)),

    @SerializedName("easeInOutExpo")
    EASE_IN_OUT_EXPO(inOut(EXP.function)),

    @SerializedName("easeInBack")
    EASE_IN_BACK(in_(BACK.function)),

    @SerializedName("easeOutBack")
    EASE_OUT_BACK(out(BACK.function)),

    @SerializedName("easeInOutBack")
    EASE_IN_OUT_BACK(inOut(BACK.function)),

    @SerializedName("easeInElastic")
    EASE_IN_ELASTIC(in_(ELASTIC.function)),

    @SerializedName("easeOutElastic")
    EASE_OUT_ELASTIC(out(ELASTIC.function)),

    @SerializedName("easeInOutElastic")
    EASE_IN_OUT_ELASTIC(inOut(ELASTIC.function)),

    @SerializedName("easeInBounce")
    EASE_IN_BOUNCE(in_(BOUNCE.function)),

    @SerializedName("easeOutBounce")
    EASE_OUT_BOUNCE(out(BOUNCE.function)),

    @SerializedName("easeInOutBounce")
    EASE_IN_OUT_BOUNCE(inOut(BOUNCE.function));

    public final EasingFunction function;

    EasingType(EasingFunction function) {
        this.function = function;
    }

    public double apply(double t, double... args) {
        return function.apply(t, args);
    }

    private static EasingFunction in_(EasingFunction e) {
        return e;
    }

    private static EasingFunction out(EasingFunction e) {
        return (t, args) -> 1 - e.apply(1 - t, args);
    }

    private static EasingFunction inOut(EasingFunction e) {
        return (t, args) -> {
            if (t < 0.5) {
                return e.apply(t * 2, args) / 2;
            } else {
                return 1 - e.apply((1 - t) * 2, args) / 2;
            }
        };
    }
}