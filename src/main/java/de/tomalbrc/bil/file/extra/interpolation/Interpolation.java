package de.tomalbrc.bil.file.extra.interpolation;

import com.google.gson.annotations.SerializedName;

public enum Interpolation {
    @SerializedName("catmullrom")
    SMOOTH(new CatmullRomInterpolator()),
    @SerializedName("linear")
    LINEAR(new LinerInterpolator()),
    @SerializedName("step")
    STEP(new StepInterpolator()),

    @SerializedName("bezier")
    BEZIER(new BezierInterpolator());

    Interpolator interpolator;

    Interpolation(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    public Interpolator get() {
        return interpolator;
    }
}