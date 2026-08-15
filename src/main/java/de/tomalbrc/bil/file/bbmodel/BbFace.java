package de.tomalbrc.bil.file.bbmodel;

import com.mojang.datafixers.util.Either;

import java.util.List;
import java.util.UUID;

public class BbFace {
    public List<Float> uv;
    public Either<Integer, UUID> texture;
    public int tintindex = 0;

    public int rotation;
    public String cullface;
}