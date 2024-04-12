## Commands

- `/bil model create id<.ajmodel>|filepath <model>`
  > Spawns a model ingame based on mob identifier or a file path (from server root folder) to the model json file. These models are not saved and are mostly intended for testing. If you want load an .ajmodel file, make sure to append `.ajmodel` to either the id or file path.
  
- `/bil model <targets> animation|variant|scale <args>`
  > Modifies the model of any entity selected in <targets> that has a custom model. Allows you to temporarily change the scale of the model, update the variant and play / pause / stop animations. This is also mostly intended for testing and playing with the models.