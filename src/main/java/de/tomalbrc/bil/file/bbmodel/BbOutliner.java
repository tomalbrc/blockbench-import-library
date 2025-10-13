package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;

public class BbOutliner {
    public UUID uuid;

    // old format, these are now fields in BbGroup for Blockbench 5+
    @Deprecated public String name = "";
    @Deprecated public Vector3f origin = new Vector3f();
    @Deprecated public Vector3f rotation = new Vector3f();
    @Deprecated public boolean export;
    @Deprecated @SerializedName("mirror_uv") public boolean mirrorUv;
    @Deprecated public int autouv;
    @Deprecated public float scale = 1.f; // AnimatedJava compat hack, not part of bbmodel file format

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
