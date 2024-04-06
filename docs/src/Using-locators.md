## Locators
Locators are nodes you can add to your model when using the AnimatedJava Blockbench Plugin.\
They are very useful for tracking the position of a specific place on the model.\
Example uses cases of this are adding hand items, displaying particles and many other things.

BIL has support for these locators, although they work a little bit different here.\
AnimatedJava has an `Entity Type` field for locators, placing that entity on the locator position, this field does not get used here.\
With BIL, locators work by registering listeners to it that receive updates when the locators' transform changes.

You can write your own listeners to do exactly what you want, but BIL also provides a few for entity elements.

### [ElementUpdateListener](https://github.com/tomalbrc/blockbench-import-library/blob/src/main/java/de/tomalbrc/bil/core/extra/ElementUpdateListener.java)

Allows you to add any `GenericEntityElement` and updates its position based on the locators transform.

```java 
Locator locator = holder.getLocator("<locator_name>");
GenericEntityElement element = new GenericEntityElement() {
  @Override
  protected EntityType<? extends Entity> getEntityType() {
    return EntityType.COW;
  }
};

// Adds listener for our simple cow element.
locator.addListener(new ElementUpdateListener(element));

// Adds the element to the holder and sends it to nearby clients.
holder.addElement(element);
```

### [DisplayElementUpdateListener](https://github.com/tomalbrc/blockbench-import-library/blob/src/main/java/de/tomalbrc/bil/core/extra/DisplayElementUpdateListener.java#L11)

Allows you to add any `DisplayElement` and applies the locators transform on it.

```java
Locator locator = holder.getLocator("<locator_name>");
DisplayElement displayElement = new ItemDisplayElement();

// Puts the display element in a wrapper. This wrapper stores certain information about the element, like whether it is a head element.
// Head elements can transform differently for mobs, as their head rotation can be different from their body rotation.
DisplayWrapper<?> display = new DisplayWrapper<>(displayElement, locator, isHeadElement);
locator.addListener(new DisplayElementUpdateListener(display));

// We initialize the display entity before adding it, to make sure the transform is applied before it gets sent to the client.
holder.initializeDisplay(display);

// Custom method for adding additional display elements. This tells the holder to mount the display as a passenger.
// This internally calls holder#addElement, and should be used instead of it for display elements registered with this listener.
holder.addAdditionalDisplay(displayElement);
```

## Notes
- When a locator has 0 listeners, it won't have to update. Remove listeners you aren't using for best performance.
- Locator updates can be called async from a non-server thread. Take this into account when writing your own listeners.