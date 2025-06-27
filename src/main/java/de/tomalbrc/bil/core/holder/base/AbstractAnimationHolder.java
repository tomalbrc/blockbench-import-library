package de.tomalbrc.bil.core.holder.base;

import de.tomalbrc.bil.api.AnimatedHolder;
import de.tomalbrc.bil.api.Animator;
import de.tomalbrc.bil.core.component.AnimationComponent;
import de.tomalbrc.bil.core.component.VariantComponent;
import de.tomalbrc.bil.core.element.PerPlayerBlockDisplayElement;
import de.tomalbrc.bil.core.element.PerPlayerItemDisplayElement;
import de.tomalbrc.bil.core.element.PerPlayerTextDisplayElement;
import de.tomalbrc.bil.core.holder.wrapper.*;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.core.model.Node;
import de.tomalbrc.bil.core.model.Pose;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractAnimationHolder extends AbstractElementHolder implements AnimatedHolder {
    protected final Model model;

    protected final AnimationComponent animationComponent;

    protected final VariantComponent variantComponent;
    protected final Object2ObjectOpenHashMap<String, Locator> locatorMap;

    protected Bone<?>[] bones;
    protected Locator[] locators;
    protected float scale = 1F;
    protected int color = -1;

    @Deprecated(forRemoval = true)
    protected AbstractAnimationHolder(Model model, ServerLevel level) {
        this(model);
    }

    protected AbstractAnimationHolder(Model model) {
        super();
        this.model = model;
        this.animationComponent = new AnimationComponent(model, this);
        this.variantComponent = new VariantComponent(model, this);
        this.locatorMap = new Object2ObjectOpenHashMap<>();
    }

    @Override
    protected final void initializeElements() {
        ObjectArrayList<Bone<?>> bones = new ObjectArrayList<>();
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

    public void addBoneDataTracker(ServerPlayer serverPlayer) {
        for (int i = 0; i < this.getBones().length; i++) {
            var bone = this.getBones()[i];
            bone.setLastPose(serverPlayer, bone.getLastPose(null), bone.getLastAnimation(null));
            bone.element().addDataTracker(serverPlayer);
        }
    }

    public void resetBoneDataTracker(ServerPlayer serverPlayer) {
        for (int i = 0; i < this.getBones().length; i++) {
            var bone = this.getBones()[i];
            bone.resetLastPose(serverPlayer);
            bone.element().resetDataTracker(serverPlayer);
        }
    }

    @Nullable
    protected PerPlayerItemDisplayElement createBoneDisplay(ResourceLocation modelData) {
        if (modelData == null)
            return null;

        ItemStack itemStack = new ItemStack(Items.LEATHER_HORSE_ARMOR);
        itemStack.set(DataComponents.ITEM_MODEL, modelData);
        itemStack.set(DataComponents.DYED_COLOR, new DyedItemColor(-1));

        PerPlayerItemDisplayElement element = createItemDisplayElement(itemStack);
        element.setItemDisplayContext(ItemDisplayContext.HEAD);
        return element;
    }

    @NotNull protected PerPlayerItemDisplayElement createItemDisplayElement(ItemStack item) {
        var element = new PerPlayerItemDisplayElement(item);
        element.setInterpolationDuration(3);
        element.setTeleportDuration(3);
        element.setInvisible(true);
        return element;
    }

    @NotNull protected PerPlayerBlockDisplayElement createBlockDisplayElement(BlockState blockState) {
        var element = new PerPlayerBlockDisplayElement(blockState);
        element.setInterpolationDuration(3);
        element.setTeleportDuration(3);
        element.setInvisible(true);
        return element;
    }

    @NotNull protected PerPlayerTextDisplayElement createTextDisplayElement(Component text) {
        var element = new PerPlayerTextDisplayElement(text);
        element.setInterpolationDuration(3);
        element.setTeleportDuration(3);
        return element;
    }

    protected void setupElements(List<Bone<?>> bones) {
        for (Node node : this.model.nodeMap().values()) {
            Pose defaultPose = this.model.defaultPose().get(node.uuid());
            switch (node.type()) {
                case BONE -> {
                    var bone = this.createBoneDisplay(node.modelData());
                    if (bone != null) {
                        bones.add(ModelBone.of(bone, node, defaultPose));
                        this.addElement(bone);
                    }
                }
                case ITEM -> {
                    if (node.displayDataElement() != null) {
                        var bone = createItemDisplayElement(BuiltInRegistries.ITEM.getValue(node.displayDataElement().getItem()).getDefaultInstance());
                        bones.add(ItemBone.of(bone, node, defaultPose));
                        this.addElement(bone);
                    }
                }
                case BLOCK -> {
                    if (node.displayDataElement() != null) {
                        var bone = createBlockDisplayElement(BuiltInRegistries.BLOCK.getValue(node.displayDataElement().getBlock()).defaultBlockState());
                        bones.add(BlockBone.of(bone, node, defaultPose));
                        this.addElement(bone);
                    }
                }
                case TEXT -> {
                    if (node.displayDataElement() != null) {
                        var bone = createTextDisplayElement(Component.literal(node.displayDataElement().getText()));
                        bones.add(TextBone.of(bone, node, defaultPose));
                        this.addElement(bone);
                    }
                }
                case LOCATOR -> this.locatorMap.put(node.name(), Locator.of(node, defaultPose));
            }
        }
    }

    @Override
    protected void onDataLoaded() {
        for (Bone<?> bone : this.bones) {
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
        Arrays.stream(this.watchingPlayers).parallel().forEach(this::asyncTickFor);
    }

    protected void asyncTickFor(ServerGamePacketListenerImpl watchingPlayer) {
        for (int boneIdx = 0; boneIdx < this.bones.length; boneIdx++) {
            this.updateElement(watchingPlayer.player, this.bones[boneIdx]);
        }

        for (int locatorIdx = 0; locatorIdx < this.locators.length; locatorIdx++) {
            this.updateLocator(watchingPlayer.player, this.locators[locatorIdx]);
        }
    }

    public void initializeDisplay(DisplayWrapper<?> display) {
        this.updateElement(null, display, display.getDefaultPose());
    }

    protected void updateElement(ServerPlayer serverPlayer, DisplayWrapper<?> display) {
        var queryResult = this.animationComponent.findPose(serverPlayer, display);
        if (queryResult != null) {
            if (queryResult.owner() != serverPlayer && display.element().getDataTracker().isDirty()) {
                return;
            }

            this.updateElement(queryResult.owner(), display, queryResult.pose());
        }
    }

    public void updateElement(@Nullable ServerPlayer serverPlayer, DisplayWrapper<?> display, @Nullable Pose pose) {
        if (pose != null) {
            this.applyPose(serverPlayer, pose, display);
        } else {
            this.applyPose(serverPlayer, display.getLastPose(serverPlayer), display);
        }
    }

    protected void updateLocator(ServerPlayer serverPlayer, Locator locator) {
        if (locator.requiresUpdate()) {
            var queryResult = this.animationComponent.findPose(serverPlayer, locator);
            if (queryResult != null) {
                var pose = queryResult.pose() == null ? locator.getLastPose(serverPlayer) : queryResult.pose();
                if (pose != null)
                    locator.updateListeners(queryResult.owner(), this, pose);
            }
        }
    }

    protected void applyPose(ServerPlayer serverPlayer, Pose pose, DisplayWrapper<?> display) {
        if (this.scale != 1F) {
            display.element().setScale(serverPlayer, pose.scale().mul(this.scale));
            display.element().setTranslation(serverPlayer, pose.translation().mul(this.scale));
        } else {
            display.element().setScale(serverPlayer, pose.readOnlyScale());
            display.element().setTranslation(serverPlayer, pose.readOnlyTranslation());
        }

        display.element().setLeftRotation(serverPlayer, pose.readOnlyLeftRotation());
        display.element().setRightRotation(serverPlayer, pose.readOnlyRightRotation());

        display.element().startInterpolationIfDirty(serverPlayer);
    }

    @Override
    public void setColor(int color) {
        if (color != this.color) {
            this.color = color;
            for (int i = 0; i < this.bones.length; i++) {
                if (this.bones[i] instanceof ItemBone itemBone) itemBone.updateColor(color);
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

    public Bone<?>[] getBones() {
        return this.bones;
    }

    public Locator[] getLocators() {
        return this.locators;
    }

    abstract public CommandSourceStack createCommandSourceStack();

    public SoundSource getSoundSource() {
        return SoundSource.BLOCKS;
    }
}
