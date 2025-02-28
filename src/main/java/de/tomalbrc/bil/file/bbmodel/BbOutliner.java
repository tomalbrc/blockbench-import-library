package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;

public class BbOutliner {
    public String name = "";
    public Vector3f origin = new Vector3f();
    public Vector3f rotation = new Vector3f();
    public UUID uuid;
    public boolean export;
    @SerializedName("mirror_uv")
    public boolean mirrorUv;

    public int autouv;

    // AnimatedJava compat hack, not part of file format
    public float scale = 1.f;

    public List<ChildEntry> children;

    public boolean hasModel() {
        for (ChildEntry childEntry : this.children) {
            if (!childEntry.isNode())
                return true;
        }
        return false;
    }

    public boolean hasUuidChild(UUID uuid) {
        for (ChildEntry childEntry : this.children) {
            if (!childEntry.isNode() && childEntry.uuid.equals(uuid))
                return true;
        }
        return false;
    }

    public boolean hasChildOutliner(BbOutliner outliner) {
        for (ChildEntry childEntry : this.children) {
            if (childEntry.isNode() && childEntry.outliner.uuid.equals(outliner.uuid))
                return true;
        }
        return false;
    }

    public boolean isHitbox() {
        return this.name != null && this.name.equals("hitbox");
    }

    static public class ChildEntry {
        public UUID uuid;
        public BbOutliner outliner;

        public boolean isNode() {
            return this.outliner != null;
        }
    }
}
