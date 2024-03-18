package de.tomalbrc.bil.file.extra.interpolation;

import org.joml.Vector3f;

// interface for interpolation between values
// not sure if we really need to use generics here...
public interface Tween {
    // Get the interpolated value at a specific progress
    Vector3f getValue(float progress);
}
