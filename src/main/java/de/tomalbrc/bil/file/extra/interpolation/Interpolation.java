package de.tomalbrc.bil.file.extra.interpolation;
public enum Interpolation {
    SMOOTH(new CatmullRomInterpolator()),
    LINEAR(new LinerInterpolator()),
    STEP(new StepInterpolator()),
    BEZIER(new BezierInterpolator());

    Interpolator interpolator;

    Interpolation(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    public Interpolator get() { return interpolator; }
}