package de.tomalbrc.bil.core.holder.wrapper;

import de.tomalbrc.bil.core.model.Node;
import de.tomalbrc.bil.core.model.Pose;
import eu.pb4.polymer.virtualentity.api.elements.TextDisplayElement;
import eu.pb4.polymer.virtualentity.api.tracker.DisplayTrackedData;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class TextBone extends Bone<TextDisplayElement> {
    private Component text;
    private final byte opacity;
    private Integer backgroundColor;
    private boolean invisible;

    protected TextBone(TextDisplayElement element, Node node, Pose defaultPose, boolean isHead) {
        super(element, node, defaultPose, isHead);
        this.text = element.getText();
        this.opacity = element.getTextOpacity();
        this.backgroundColor = element.getDataTracker().get(DisplayTrackedData.Text.BACKGROUND);
    }

    public static TextBone of(TextDisplayElement element, Node node, Pose defaultPose, boolean isHead) {
        return new TextBone(element, node, defaultPose, isHead);
    }

    public static TextBone of(TextDisplayElement element, @NotNull Node node, Pose defaultPose) {
        Node current = node;
        boolean head = false;
        while (current != null) {
            if (current.headTag()) {
                head = true;
                break;
            }
            current = current.parent();
        }

        return new TextBone(element, node, defaultPose, head);
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
        this.element().getDataTracker().set(DisplayTrackedData.Text.BACKGROUND, color, true);
    }

    private void setTrackedOpacity(byte opacity) {
        this.element().getDataTracker().set(DisplayTrackedData.Text.TEXT_OPACITY, opacity, true);
    }

    private void setTrackedText(Component text) {
        this.element().getDataTracker().set(DisplayTrackedData.Text.TEXT, text, true);
    }
}
