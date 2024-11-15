package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.bil.BIL;
import de.tomalbrc.bil.file.extra.BbVariablePlaceholders;
import de.tomalbrc.bil.file.extra.interpolation.Interpolation;
import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public class BbKeyframe implements Comparable<BbKeyframe> {
    public Channel channel;
    @SerializedName("data_points")
    public List<Map<String, DataPointValue>> dataPoints;

    public UUID uuid;
    public float time;
    public int color;

    public Interpolation interpolation;
    @SerializedName("bezier_linked")
    public boolean bezierLinked;
    @SerializedName("bezier_left_time")
    public Vector3f bezierLeftTime;
    @SerializedName("bezier_left_value")
    public Vector3f bezierLeftValue;
    @SerializedName("bezier_right_time")
    public Vector3f bezierRightTime;
    @SerializedName("bezier_right_value")
    public Vector3f bezierRightValue;

    @Override
    public int compareTo(BbKeyframe other) {
        return Float.compare(this.time, other.time);
    }

    public enum Channel {
        @SerializedName("position")
        POSITION,
        @SerializedName("rotation")
        ROTATION,
        @SerializedName("scale")
        SCALE,
        @SerializedName("timeline")
        TIMELINE, // model
        @SerializedName("sound")
        SOUND, // model
        @SerializedName("variants")
        VARIANTS, // ajmodel
        @SerializedName("commands")
        COMMANDS // ajmodel
    }

    public Vector3f getVector3f(int index, BbVariablePlaceholders placeholders, MolangEnvironment environment) throws MolangRuntimeException {
        return new Vector3f(
                this.dataPoints.get(index).get("x").getValue(placeholders, environment),
                this.dataPoints.get(index).get("y").getValue(placeholders, environment),
                this.dataPoints.get(index).get("z").getValue(placeholders, environment)
        );
    }

    static public class DataPointValue {
        private float value;
        private String stringValue;
        private MolangExpression molangExpression;

        public void setValue(float value) {
            this.value = value;
        }

        public String getStringValue() {
            return this.stringValue;
        }

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }

        public float getValue(BbVariablePlaceholders placeholders, MolangEnvironment environment) throws MolangRuntimeException {
            if (this.stringValue == null)
                return this.value;

            var length = this.stringValue.trim().length();
            if (length <= 2 || NumberUtils.isParsable(this.stringValue)) {
                if (length == 0 || this.stringValue.equals("-") || this.stringValue.equals("+") || this.stringValue.equals(".") || this.stringValue.equals(","))
                    return 0;

                this.value = Float.parseFloat(this.stringValue.trim());
                this.stringValue = null;
                return this.value;
            }

            // dirty hack, caching the molang expression for performance,
            // would be better to post-process the expressions as string,
            // for the placeholder substitution
            if (this.molangExpression == null) {
                String modifiedExpression = this.stringValue;
                if (placeholders != null)
                    modifiedExpression = placeholders.substituteVariables(this.stringValue);

                try {
                    this.molangExpression = BIL.COMPILER.compile(modifiedExpression);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            return environment.resolve(this.molangExpression);
        }
    }
}