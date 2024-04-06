Example of a hostile entity (mojang mappings)
```java
import ...

public class Snake extends Monster implements AnimatedEntity {
    public static final ResourceLocation ID = Util.id("snake");
    public static final Model MODEL = AjModelLoader.load(ID);
    private final EntityHolder<Snake> holder;

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.MAX_HEALTH, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4);
    }

    public static boolean checkSnakeSpawnRules(EntityType<? extends Monster> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.canSeeSky(pos) && checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    @Override
    public EntityHolder<Snake> getHolder() {
        return this.holder;
    }

    public Snake(EntityType<? extends Snake> type, Level level) {
        super(type, level);

        this.moveControl = new MoveControl(this);
        this.jumpControl = new JumpControl(this);

        this.holder = new LivingEntityHolder<>(this, MODEL);
        EntityAttachment.ofTicking(this.holder, this);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 2.0F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.59));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Snake.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Chicken.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount % 2 == 0) {
            AnimationHelper.updateWalkAnimation(this, this.holder);
            AnimationHelper.updateHurtVariant(this, this.holder);
        }
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean result = super.doHurtTarget(entity);

        if (result) {
            if (entity instanceof LivingEntity livingEntity && this.random.nextInt(5) == 1) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, level().getDifficulty().getId() * 2 * 20, 1));
            }
        }

        return result;
    }
}
```

AnimationHelper implementation:

```java
public class AnimationHelper {

    public static void updateWalkAnimation(LivingEntity entity, AnimatedHolder holder) {
        updateWalkAnimation(entity, holder, 0);
    }

    public static void updateWalkAnimation(LivingEntity entity, AnimatedHolder holder, int priority) {
        Animator animator = holder.getAnimator();
        if (entity.walkAnimation.isMoving() && entity.walkAnimation.speed() > 0.02) {
            animator.playAnimation("walk", priority);
            animator.pauseAnimation("idle");
        } else {
            animator.pauseAnimation("walk");
            animator.playAnimation("idle", priority, true);
        }
    }

    public static void updateHurtVariant(LivingEntity entity, AnimatedHolder holder) {
        updateHurtColor(entity, holder);
    }

    public static void updateHurtColor(LivingEntity entity, AnimatedHolder holder) {
        if (entity.hurtTime > 0 || entity.deathTime > 0)
            holder.setColor(0xff7e7e);
        else
            holder.clearColor();
    }
}
```
