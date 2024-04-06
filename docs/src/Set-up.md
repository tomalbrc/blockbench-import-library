Merge these into your `build.gradle` file to add the blockbench-import-library as dependency.\
blockbench-import-library requires certain [Polymer](https://polymer.pb4.eu/) modules aswell, which are included here.
```groovy
repositories {
    maven { url 'https://maven.tomalbrc.de' } // blockbench-import-library
    maven { url 'https://maven.nucleoid.xyz' } // Polymer
}

dependencies {
    modImplementation include("de.tomalbrc:blockbench-import-library:[BIL_VERSION]")

    modImplementation "eu.pb4:polymer-core:[POLYMER_VERSION]"
    modImplementation "eu.pb4:polymer-networking:[POLYMER_VERSION]"
    modImplementation "eu.pb4:polymer-resource-pack:[POLYMER_VERSION]"
    modImplementation "eu.pb4:polymer-virtual-entity:[POLYMER_VERSION]"

    // Useful for automatically handling resourcepacks in dev environment, but not required.
    modRuntimeOnly "eu.pb4:polymer-autohost:[POLYMER_VERSION]"
}
```
It is important to make sure these dependencies are available at runtime.\
You can do this by either adding these modules to your dependencies in `fabric.mod.json`, or by including them in your jar file as Jar-in-Jar.
```json
"blockbench-import-library": "*",
"polymer-core": "*",
"polymer-networking": "*",
"polymer-virtual-entity": "*",
"polymer-resource-pack": "*"
```