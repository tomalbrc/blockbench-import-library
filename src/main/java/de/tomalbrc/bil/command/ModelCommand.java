package de.tomalbrc.bil.command;

import com.google.gson.JsonParseException;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import de.tomalbrc.bil.BIL;
import de.tomalbrc.bil.api.AjEntity;
import de.tomalbrc.bil.api.AjEntityHolder;
import de.tomalbrc.bil.api.VariantController;
import de.tomalbrc.bil.file.loader.AjLoader;
import de.tomalbrc.bil.extra.ModelEntity;
import de.tomalbrc.bil.model.Model;
import de.tomalbrc.bil.model.Variant;
import de.tomalbrc.bil.util.Utils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.HexFormat;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModelCommand {
    private static final String TARGETS = "targets";
    private static final String ANIMATION = "animation";

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        var builder = Commands.literal("intermediate").requires(source -> source.hasPermission(2));

        // Create intermediate command
        builder.then(modelCreator());

        // Manipulate intermediate command
        builder.then(Commands.argument(TARGETS, EntityArgument.entities())
                .then(scaleManipulator())
                .then(colorManipulator())
                .then(variantManipulator())
                .then(animationManipulator())
        );

        return builder;
    }

    private static int manipulateModels(CommandSourceStack source, Collection<? extends Entity> targets, Consumer<AjEntityHolder> consumer) {
        int count = 0;
        for (Entity target : targets) {
            AjEntityHolder holder = AjEntity.getHolder(target);
            if (holder != null) {
                consumer.accept(holder);
                count++;
            }
        }

        if (count <= 0) {
            source.sendFailure(Component.literal("No models found!"));
        }

        return count;
    }

    private static int spawnModel(CommandSourceStack source, ResourceLocation id) throws CommandSyntaxException {
        return spawnModel(source, () -> new AjLoader().load(id), id.toString());
    }

    private static int spawnModel(CommandSourceStack source, String path) throws CommandSyntaxException {
        return spawnModel(source, () -> new AjLoader().load(path), path);
    }

    private static int spawnModel(CommandSourceStack source, Supplier<Model> supplier, String path) throws CommandSyntaxException {
        ServerLevel level = source.getLevel();
        Vec3 pos = source.getPosition();
        Vec2 rot = source.getRotation();

        try {
            Model model = supplier.get();
            ModelEntity entity = new ModelEntity(level, model);
            entity.moveTo(pos.x, pos.y, pos.z, rot.y, 0F);

            level.addFreshEntity(entity);
            source.sendSuccess(() -> Component.literal("Successfully spawned intermediate!"), false);
        } catch (JsonParseException ex) {
            BIL.LOGGER.error("{} is not a valid intermediate file!", path, ex);
            throw Utils.buildCommandException(ex.getMessage() + "\nCheck the server console for more information.");
        } catch (Throwable throwable) {
            throw Utils.buildCommandException("Failed to load intermediate!\n" + throwable.getMessage());
        }

        return Command.SINGLE_SUCCESS;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> modelCreator() {
        var builder = Commands.literal("create");

        // Create intermediate command
        builder.then(Commands.literal("id")
                .then(Commands.argument("intermediate", ResourceLocationArgument.id())
                        .suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                        .executes(context -> spawnModel(
                                context.getSource(),
                                ResourceLocationArgument.getId(context, "intermediate")
                        ))
                )
        );

        builder.then(Commands.literal("filepath")
                .then(Commands.argument("intermediate", StringArgumentType.greedyString())
                        .executes(context -> spawnModel(
                                context.getSource(),
                                StringArgumentType.getString(context, "intermediate")
                        ))
                )
        );

        return builder;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> scaleManipulator() {
        var builder = Commands.literal("scale");

        builder.then(Commands.argument("scale", FloatArgumentType.floatArg(0.01f))
                .executes(context -> {
                    float scale = FloatArgumentType.getFloat(context, "scale");
                    return manipulateModels(
                            context.getSource(),
                            EntityArgument.getEntities(context, TARGETS),
                            holder -> holder.setScale(scale)
                    );
                })
        );

        return builder;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> colorManipulator() {
        var builder = Commands.literal("color");

        builder.then(Commands.argument("color", StringArgumentType.word())
                .executes(context -> {
                    int color = convertHexCode(StringArgumentType.getString(context, "color"));
                    return manipulateModels(
                            context.getSource(),
                            EntityArgument.getEntities(context, TARGETS),
                            holder -> holder.setColor(color)
                    );
                })
        );

        return builder;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> variantManipulator() {
        var builder = Commands.literal("variant");

        builder.then(Commands.argument("variant", StringArgumentType.word())
                .suggests(availableVariants())
                .executes(context -> {
                    String variant = StringArgumentType.getString(context, "variant");
                    return manipulateModels(
                            context.getSource(),
                            EntityArgument.getEntities(context, TARGETS),
                            holder -> {
                                VariantController controller = holder.getVariantController();
                                if (variant.equals("default")) {
                                    controller.setDefaultVariant();
                                } else {
                                    controller.setVariant(variant);
                                }
                            }
                    );
                })
        );

        return builder;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> animationManipulator() {
        var builder = Commands.literal("animation");

        builder.then(Commands.argument(ANIMATION, StringArgumentType.word())
                .suggests(availableAnimations())
                .executes(context -> {
                    String animation = StringArgumentType.getString(context, ANIMATION);
                    return manipulateModels(
                            context.getSource(),
                            EntityArgument.getEntities(context, TARGETS),
                            (holder) -> holder.getAnimator().playAnimation(animation)
                    );
                })
                .then(Commands.literal("play")
                        .executes(context -> {
                            String animation = StringArgumentType.getString(context, ANIMATION);
                            return manipulateModels(
                                    context.getSource(),
                                    EntityArgument.getEntities(context, TARGETS),
                                    (holder) -> holder.getAnimator().playAnimation(animation)
                            );
                        })
                        .then(Commands.argument("priority", IntegerArgumentType.integer())
                                .executes(context -> {
                                    String animation = StringArgumentType.getString(context, ANIMATION);
                                    int priority = IntegerArgumentType.getInteger(context, "priority");
                                    return manipulateModels(
                                            context.getSource(),
                                            EntityArgument.getEntities(context, TARGETS),
                                            (holder) -> holder.getAnimator().playAnimation(animation, priority)
                                    );
                                })
                        )
                )
                .then(Commands.literal("set-frame")
                        .then(Commands.argument("frame", IntegerArgumentType.integer())
                                .executes(context -> {
                                    String animation = StringArgumentType.getString(context, ANIMATION);
                                    int frame = IntegerArgumentType.getInteger(context, "frame");
                                    return manipulateModels(
                                            context.getSource(),
                                            EntityArgument.getEntities(context, TARGETS),
                                            holder -> holder.getAnimator().setAnimationFrame(animation, frame)
                                    );
                                })
                        )
                )
                .then(Commands.literal("pause")
                        .executes(context -> {
                            String animation = StringArgumentType.getString(context, ANIMATION);
                            return manipulateModels(
                                    context.getSource(),
                                    EntityArgument.getEntities(context, TARGETS),
                                    holder -> holder.getAnimator().pauseAnimation(animation)
                            );
                        })
                )
                .then(Commands.literal("stop")
                        .executes(context -> {
                            String animation = StringArgumentType.getString(context, ANIMATION);
                            return manipulateModels(
                                    context.getSource(),
                                    EntityArgument.getEntities(context, TARGETS),
                                    holder -> holder.getAnimator().stopAnimation(animation)
                            );
                        })
                )
        );

        return builder;
    }

    private static SuggestionProvider<CommandSourceStack> availableAnimations() {
        return (ctx, builder) -> {
            forEachModel(ctx, model -> {
                for (String animation : model.animations().keySet()) {
                    builder.suggest(animation);
                }
            });
            return builder.buildFuture();
        };
    }

    private static SuggestionProvider<CommandSourceStack> availableVariants() {
        return (ctx, builder) -> {
            builder.suggest("default");
            forEachModel(ctx, model -> {
                for (Variant variant : model.variants().values()) {
                    builder.suggest(variant.name());
                }
            });
            return builder.buildFuture();
        };
    }

    private static void forEachModel(CommandContext<CommandSourceStack> ctx, Consumer<Model> consumer) throws CommandSyntaxException {
        // Make sure to only call this when we have the target context already.
        for (Entity entity : EntityArgument.getEntities(ctx, TARGETS)) {
            AjEntityHolder holder = AjEntity.getHolder(entity);
            if (holder != null) {
                consumer.accept(holder.getModel());
            }
        }
    }

    private static int convertHexCode(String hexCode) throws CommandSyntaxException {
        try {
            return HexFormat.fromHexDigits(hexCode);
        } catch (Throwable throwable) {
            throw Utils.buildCommandException("Invalid color: " + throwable.getMessage());
        }
    }
}
