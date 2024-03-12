package de.tomalbrc.bil.file.bbmodel;

import it.unimi.dsi.fastutil.floats.FloatArrayList;

import java.util.List;

public class BbFace {
    public BbFace(BbFace other) {
        this.texture = other.texture;
        this.uv = new FloatArrayList(other.uv);
        this.tintindex = other.tintindex;
        this.cullface = other.cullface;
    }

    public List<Float> uv;
    public int texture = -1;

    public int tintindex = -1;

    public String cullface;
}