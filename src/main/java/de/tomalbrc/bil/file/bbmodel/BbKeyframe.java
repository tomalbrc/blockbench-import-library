package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.bil.BIL;
import de.tomalbrc.bil.file.extra.BbVariablePlaceholders;
import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BbKeyframe {
    public Channel channel;
    @SerializedName("data_points")
    public List<Map<String, DataPointValue>> dataPoints;

    public UUID uuid;
    public float time;
    public int color;
    public String interpolation;
    @SerializedName("bezier_linked")
    public boolean bezierLinked;
    @SerializedName("bezier_left_time")
    public List<Float> bezierLeftTime;
    @SerializedName("bezier_left_value")
    public List<Float> bezierLeftValue;
    @SerializedName("bezier_right_time")
    public List<Float> bezierRightTime;
    @SerializedName("bezier_right_value")
    public List<Float> bezierRightValue;

    public enum Channel {
        position,
        rotation,
        scale
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
        private String expression;
        private MolangExpression molangExpression;

        public void setValue(float value) {
            this.value = value;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }

        public float getValue(BbVariablePlaceholders placeholders, MolangEnvironment environment) throws MolangRuntimeException {
            if (this.expression == null)
                return this.value;

            if (this.expression.trim().length() <= 2) {
                this.value = Float.parseFloat(this.expression.trim());
                this.expression = null;
                return this.value;
            }

            // dirty hack, caching the molang expression for performance,
            // would be better to post-process the expressions as string,
            // for the placeholder substitution
            if (this.molangExpression == null) {
                String modifiedExpression = this.expression;
                if (placeholders != null)
                    modifiedExpression = placeholders.substituteVariables(this.expression);

                try {
                    this.molangExpression = BIL.COMPILER.compile(modifiedExpression);
                } catch(Exception e) {
                    throw new RuntimeException(e);
                }
            }

            return this.molangExpression.get(environment);
        }
    }
}