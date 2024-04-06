## 1. Creating a model
To create a model you can use, you will first need to install [Blockbench](https://www.blockbench.net/). You can optionally install the [Animated Java Blockbench Plugin](https://github.com/Animated-Java/animated-java#how-to-install) - it offers a few more features such as variants and conditional effect keyframes (Sounds, Commands, Variants).\
In Blockbench you can either choose a `Generic Entity Model`, or an `Animated Java Rig`.\
From there you can create the model. If you aren't familiar with Blockbench or Animated Java yet, you can find the [Blockbench Wiki here](https://www.blockbench.net/wiki) and [AnimatedJava documentation here](https://animated-java.dev/docs/home).

Once you have finished the model, you can just save the `.bbmodel` or `.ajmodel`\

## 2. Adding the model to mod resources
To make the model available ingame, it needs to be included in your mod's resources folder.\
Models in mod resources are referenced by identifier, in format `namespace:path`. 

Add the `.bbmodel` or `.ajmodel` file - the model - to `/resources/model/<namespace>/<path>.json`. 
This is the file that will be parsed by BIL on the server and a resourcepack will be generated.
The models will have to be loaded before the world is loaded, otherwise the resourcepack generatio has to be started manually again on order for the models to be available in the resourcepack

## Notes
- Head bones of mobs should have their names starting with `head`, so BIL can recognize it for head rotations.
- Child bone transformations of head bone rotations currently have a flaw, this will be addressed soon!