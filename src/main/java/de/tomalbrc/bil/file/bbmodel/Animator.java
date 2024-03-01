package de.tomalbrc.bil.file.bbmodel;

import java.util.List;

public class Animator {
    public String name;
    public Type type;
    public List<Keyframe> keyframes;

    public enum Type {
        bone
    }
}
