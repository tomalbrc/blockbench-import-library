package de.tomalbrc.bil.core.holder.wrapper;

import de.tomalbrc.bil.core.element.PerPlayerTextDisplayElement;
import de.tomalbrc.bil.core.model.Node;
import de.tomalbrc.bil.core.model.Pose;
import eu.pb4.polymer.virtualentity.api.data.DisplayEntityData;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class TextBone extends Bone<PerPlayerTextDisplayElement> {
    private Component text;
    private final byte opacity;
    private Integer backgroundColor;
    private boolean invisible;

    protected TextBone(PerPlayerTextDisplayElement element, Node node, Pose defaultPose) {
        super(element, node, defaultPose);
        this.text = element.getText();
        this.opacity = element.getTextOpacity();
        this.backgroundColor = element.getSyncedData().get(DisplayEntityData.Text.BACKGROUND);
    }

    public static TextBone of(PerPlayerTextDisplayElement element, @NotNull Node node, Pose defaultPose) {
        return new TextBone(element, node, defaultPose);
    }

    public void setInvisible(boolean invisible) {
        if (this.invisible == invisible) {
            return;
        }

        this.invisible = invisible;
        if (invisible) {
            this.setTrackedBackground(0);
            this.setTrackedOpacity((byte)0);
        } else {
            this.setTrackedBackground(backgroundColor);
            this.setTrackedOpacity(opacity);
        }
    }

    public void updateBackgroundColor(int color) {
        this.backgroundColor = color;
        setTrackedBackground(color);
    }

    public void updateText(Component text) {
        this.text = text;
        setTrackedText(text);
    }

    private void setTrackedBackground(int color) {
        this.element().getSyncedData().set(DisplayEntityData.Text.BACKGROUND, color, true);
    }

    private void setTrackedOpacity(byte opacity) {
        this.element().getSyncedData().set(DisplayEntityData.Text.TEXT_OPACITY, opacity, true);
    }

    private void setTrackedText(Component text) {
        this.element().getSyncedData().set(DisplayEntityData.Text.TEXT, text, true);
    }
}
