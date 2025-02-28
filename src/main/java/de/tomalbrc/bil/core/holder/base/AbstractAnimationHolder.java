package de.tomalbrc.bil.core.holder.base;

import de.tomalbrc.bil.api.AnimatedHolder;
import de.tomalbrc.bil.api.Animator;
import de.tomalbrc.bil.core.component.AnimationComponent;
import de.tomalbrc.bil.core.component.VariantComponent;
import de.tomalbrc.bil.core.holder.wrapper.Bone;
import de.tomalbrc.bil.core.holder.wrapper.DisplayWrapper;
import de.tomalbrc.bil.core.holder.wrapper.Locator;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.core.model.Node;
import de.tomalbrc.bil.core.model.Pose;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import eu.pb4.polymer.virtualentity.api.tracker.DisplayTrackedData;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractAnimationHolder extends AbstractElementHolder implements AnimatedHolder {

    protected final Model model;
    protected final AnimationComponent animationComponent;
    protected final VariantComponent variantComponent;
    protected final Object2ObjectOpenHashMap<String, Locator> locatorMap;

    protected Bone[] bones;
    protected Locator[] locators;
    protected float scale = 1F;
    protected int color = -1;

    protected AbstractAnimationHolder(Model model, ServerLevel level) {
        super(level);
        this.model = model;
        this.animationComponent = new AnimationComponent(model, this);
        this.variantComponent = new VariantComponent(model, this);
        this.locatorMap = new Object2ObjectOpenHashMap<>();
    }

    @Override
    protected final void initializeElements() {
        ObjectArrayList<Bone> bones = new ObjectArrayList<>();
        this.setupElements(bones);

        this.locators = new Locator[this.locatorMap.size()];
        this.bones = new Bone[bones.size()];

        int index = 0;
        for (Locator locator : this.locatorMap.values()) {
            this.locators[index++] = locator;
        }

        for (index = 0; index < bones.size(); index++) {
            this.bones[index] = bones.get(index);
        }
    }

    @Nullable
    protected ItemDisplayElement createBoneDisplay(PolymerModelData modelData) {
        if (modelData == null)
            return null;

        ItemDisplayElement element = new ItemDisplayElement();
        element.setModelTransformation(ItemDisplayContext.HEAD);
        element.setInvisible(true);
        element.setInterpolationDuration(2);
        element.getDataTracker().set(DisplayTrackedData.TELEPORTATION_DURATION, 3);

        ItemStack itemStack = new ItemStack(modelData.item());
        itemStack.set(DataComponents.CUSTOM_MODEL_DATA, modelData.asComponent());
        itemStack.set(DataComponents.DYED_COLOR, new DyedItemColor(-1, false));

        element.setItem(itemStack);
        return element;
    }

    protected void setupElements(List<Bone> bones) {
        for (Node node : this.model.nodeMap().values()) {
            Pose defaultPose = this.model.defaultPose().get(node.uuid());
            switch (node.type()) {
                case BONE -> {
                    ItemDisplayElement bone = this.createBoneDisplay(node.modelData());
                    if (bone != null) {
                        bones.add(Bone.of(bone, node, defaultPose));
                        this.addElement(bone);
                    }
                }
                case LOCATOR -> this.locatorMap.put(node.name(), Locator.of(node, defaultPose));
            }
        }
    }

    @Override
    protected void onDataLoaded() {
        for (Bone bone : this.bones) {
            this.initializeDisplay(bone);
        }
    }

    @Override
    protected boolean shouldSkipTick() {
        return false;
    }

    @Override
    protected void onTick() {
        this.animationComponent.tickAnimations();
    }

    @Override
    protected void onAsyncTick() {
        for (Bone bone : this.bones) {
            this.updateElement(bone);
        }

        for (Locator locator : this.locators) {
            this.updateLocator(locator);
        }
    }

    protected void updateElement(DisplayWrapper<?> display) {
        this.updateElement(display, this.animationComponent.findPose(display));
    }

    public void initializeDisplay(DisplayWrapper<?> display) {
        this.updateElement(display, display.getDefaultPose());
    }

    public void updateElement(DisplayWrapper<?> display, @Nullable Pose pose) {
        if (pose != null) {
            this.applyPose(pose, display);
        }
    }

    protected void updateLocator(Locator locator) {
        if (locator.requiresUpdate()) {
            Pose pose = this.animationComponent.findPose(locator);
            if (pose != null) {
                locator.updateListeners(this, pose);
            }
        }
    }

    protected void applyPose(Pose pose, DisplayWrapper<?> display) {
        if (this.scale != 1F) {
            display.element().setScale(pose.scale().mul(this.scale));
            display.element().setTranslation(pose.translation().mul(this.scale));
        } else {
            display.element().setScale(pose.readOnlyScale());
            display.element().setTranslation(pose.readOnlyTranslation());
        }

        display.element().setLeftRotation(pose.readOnlyLeftRotation());
        display.element().setRightRotation(pose.readOnlyRightRotation());

        display.element().startInterpolationIfDirty();
    }

    @Override
    public void setColor(int color) {
        if (color != this.color) {
            this.color = color;
            for (Bone bone : this.bones) {
                bone.updateColor(color);
            }
        }
    }

    @Override
    public Model getModel() {
        return this.model;
    }

    @Override
    public Locator getLocator(String name) {
        return this.locatorMap.get(name);
    }

    @Override
    public VariantComponent getVariantController() {
        return this.variantComponent;
    }

    @Override
    public Animator getAnimator() {
        return this.animationComponent;
    }

    @Override
    public float getScale() {
        return this.scale;
    }

    @Override
    public void setScale(float scale) {
        this.scale = scale;
    }

    public Bone[] getBones() {
        return this.bones;
    }

    public Locator[] getLocators() {
        return this.locators;
    }

    abstract public CommandSourceStack createCommandSourceStack();
}
