# Concepts:

I will try to give a short summary of the concepts used in this library;\
As a TLDR of sorts

## AnimatedHolder

BIL uses Polymer's ElementHolder concept, which acts as a container for visual and interactive elements (and a bit more). This library extends this by providing specialized interfaces and implementations for animated models in various scenarios:

---
### `AnimatedEntityHolder extends AnimatedHolder`:
>`LivingEntityHolder`:
> Easiest to use for living entities. It automatically rotates nodes prefixed with "head" based on the entity's head rotation

> `LivingEntityHolderWithRideOffset`: As above but with a custom ride offset

> `InteractableEntityHolder`: Could be used for very basic entities, like different minecarts, boats or other custom entities like sleds

> `SimpleEntityHolder`: As above but without the interaction
---
### `AnimatedHolder`:
>`PositionedHolder`: Can be used with polymers `BlockWithElementHolder` or with Block entities to make custom chests or other animated blocks
---
### `BbModel`:

This class represents a POJO (Plain Old Java Object) interpretation of a blockbench file. It serves as a structured representation of the data within the blockbench file.

### `Model`:

This class acts as a simplified representation specifically designed for the animation engine

### `Animation`:

- Holds all matrices for all bones to play in a list. These matrices define the transformation (position, rotation, scale) of each bone at each frame (at 50ms intervals, or 1 tick)

DynamicAnimation (todo):
This future feature aims to dynamically sample keyframes and calculate transformations based on the scenegraph hierarchy.
Performance impact might be high

## Effect Keyframes

Effect keyframes extend the functionality beyond basic animation. They allow for effects such as:
- Playing sounds
- Switching between variants.
- Executing commands

## Commands

Commands can be run as effect keyframes.
When using the Animated Java blockbench plugin, commands enable you to conditionally trigger sounds, execute other commands, and switch variants based on specific criteria, using commands, within the animation.

## Variants

Animated Java exclusive feature;
It allows to dynamically switch the model's texture

