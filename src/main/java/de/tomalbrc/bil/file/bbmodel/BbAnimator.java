package de.tomalbrc.bil.file.bbmodel;

import java.util.List;

public class BbAnimator {
    public String name;
    public Type type;
    public List<BbKeyframe> keyframes;

    public enum Type {
        bone,
        effect
    }
}
