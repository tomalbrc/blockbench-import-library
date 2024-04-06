## 1. Loading the model
To load a model file into the actual game, you can use the utility methods in the `BbModelLoader` and `AjModelLoader` classes.\
Models required in mods should be retrieved by ID, but it's also possible to load files from the server.\
Note that these models are **not** cached by BIL. They are safe to reuse and should be cached when possible
```java
public static final Model MODEL_FROM_ID = BbModelLoader.load(new ResourceLocation("namespace", "path"));
public static final Model MODEL_FROM_FILEPATH = BbModelLoader.load("file/path/example.bbmodel");
```
These `Model` objects can be used with BIL's custom implementations of Polymer's ElementHolder.\
Anything extending `AbstractAnimatedHolder` supports it.

## 2. Entity Attachments
BIL models can be attached to entities.\
Writing a custom entity with model works mostly the same as writing one normally.\
You just have to implement `AnimatedEntity` and initialize an `EntityHolder` in the constructor with the model.


```java
public class RedstoneGolem extends Monster implements AnimatedEntity {
    public static final ResourceLocation ID = new ResourceLocation("bil", "redstone_golem");
    public static final Model MODEL = BbModelImporter.load(ID);
    private final EntityHolder<RedstoneGolem> holder;

    public RedstoneGolem(EntityType<? extends Monster> type, Level level) {
        super(type, level);

        // Creates model holder with out of the box support for most LivingEntity features.
        // Note that it is always possible to write your own or override some of their methods.
        this.holder = new LivingEntityHolder<>(this, MODEL);

        // Attaches the holder to this entity in Polymer.
        // Make sure that ticking is enabled, as it is required for model updates.
        EntityAttachment.ofTicking(this.holder, this);
    }

    @Override
    public EntityHolder<RedstoneGolem> getHolder() {
        return this.holder;
    }
}
```

## 3. Other Attachments
While BIL is mostly targeted towards entities, it is possible to attach model holders to other things.\
Polymer provides a few of its own other attachments, like ChunkAttachment and BlockBoundAttachment.\
You can attach your model holder those aswell.

## 4. Variants and Animations
One thing BIL can't automatically do for you is decide when certain animations or variants should be used.\
To do this, every holder provides an `Animator` and `VariantController`, which can for example be used like this:

```java
Animator animator = holder.getAnimator();

// Completely stops the animation named 'idle'.
animator.stopAnimation("idle");

// Pauses the animation named 'walk'.
animator.pauseAnimation("walk");

// Plays the melee animation with priority 10.
animator.playAnimation("melee", 10); 

VariantController controller = holder.getVariantController();

// Sets the variant to the variant named 'hurt' if the current variant is default.
if (controller.isDefaultVariant()) {
    controller.setVariant("hurt");
}
```