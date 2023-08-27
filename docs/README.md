# Breakdown

This repository is divided into the following:

## [HistorySurvival.uk.minersonline.Application.java](../src/main/java/uk/minersonline/HistorySurvival/Application.java)
_Class with the main method:_

``` java
import window.glfw.Minecart.uk.minersonline.Window;

import static de.damios.guacamole.gdx.StartOnFirstThreadHelper.startNewJvmIfRequired;

public class HistorySurvival.uk.minersonline.Application {

    private static final Window WINDOW = Window.getInstance();

    public static void main(String[] args) {
        // Starts a new JVM if the application was started on macOS without the
        // -XstartOnFirstThread argument.
        if (startNewJvmIfRequired()) {
            System.exit(0);
        }
        WINDOW.run();
    }
}
```

GLFW windows may only be created on the main application thread. However, on MacOS systems, there's a strange behavior where this does not happen naturally and the following exception is thrown when trying to run the application.

```console
Caused by: java.lang.IllegalStateException: GLFW windows may only be created on the main thread and that thread must be the first thread in the process. Please run the JVM with -XstartOnFirstThread. For offscreen rendering, make sure another window toolkit (e.g. AWT or JavaFX) is initialized before GLFW.
```
_More on that [here](http://forum.lwjgl.org/index.php?topic=6077.0)._  
As cited on the message above, a fix for that would be setting up the JVM flag `-XstartOnFirstThread`. Even so, this is a manual process that needs to be done every time before the application is run on a MacOs system.  
In order to avoid having to do anything outside of the application itself, a helper function [startNewJvmIfRequired()](https://github.com/crykn/guacamole/blob/eabb0ae27aecafad2ced071daf505b7222ec0074/gdx-desktop/src/main/java/de/damios/guacamole/gdx/StartOnFirstThreadHelper.java#L150) is being used for this template, that will automatically start a new JVM with the necessary flag if the application was started on macOS without `-XstartOnFirstThread`.  
This solution can be seen being used inside the main method inside `HistorySurvival.uk.minersonline.Application.java` (as shown on the snippet above) and was provided by the [guacamole collection](https://github.com/crykn/guacamole), thanks for that! :pray:

## [Util package](/uk/minersonline/Minecart/util):
 - [Color.java](/uk/minersonline/Minecart/util/Color.java): Utilitary enum class for RGB colors. You can instantiate a Color with RGB values by its name.  
  For example, let's say you wanted to get the RGB values for the color `purple`:  
  ``` java
     // Call Color.from(nameOfYourColorAsString);
     Color purpleColor = Color.from("purple");
     
     System.out.println(purpleColor.getRed());
     System.out.println(purpleColor.getGreen());
     System.out.println(purpleColor.getBlue());

  ```
  The output would be:
  ``` console
     0.502
     0.0
     0.502
  ```
  
  Besides the already existing colors inside `Color.java`, it's possible to modify it and add any additional colors as you desire.  
  
  - [FileUtils.java](/uk/minersonline/Minecart/util/FileUtils.java): Utilitary class that can retrieve the contents of a resource file as an `InputStream`.  
   For example, let's say you wanted to get the contents of a file whose path is `{project_root}/src/main/resources/example/HelloWorld.txt`.  
   The following code would print out the file contents of the file:
   ``` java
     String fileContents = FileUtils.getFileFromResourceAsStream("example/HelloWorld.txt").toString();
     System.out.println(fileContents);
   ```
   _NOTE: Files should be stored inside: `{project_root}/src/main/resources/` as this is the starting root directory for resources.
   Storing files inside this directory will ensure the files can also be located when you build your application into a jar._
   
## [Geometry Configuration package:](/src/main/java/uk/minersonline/Minecart/geometry/configuration)
   - [World.java](src/main/java/uk/minersonline/Minecart/geometry/configuration/World.java):
   Configuration class that holds the values representing the projection of the world.
   The static funcion _`setCoordinatePlane()`_ will apply the configured values to the `org.lwjgl.opengl.GL11.glOrtho()` function.  
   For more details, take at look at [the class itself](/src/main/java/uk/minersonline/Minecart/geometry/configuration/World.java).
   
## [GLFW package:](/uk/minersonline/Minecart/glfw)
   
   - [Window.java](/uk/minersonline/Minecart/glfw): Singleton class that holds the GLFW window configuration setup and the main game loop.
   
   _Usage:_
   ``` java
     // Get the Window instance
     Window windowInstance = Window.getInstance();
     // Calling the run() method will initialize GLFW and OpenGL bindings. Soon after that, the main game loop will start.
     windowInstance.run();
     // Calls after this point will only be executed after GLFW has received a signal to terminate..
     // ...
   ```
   
   - [Listeners:](/src/main/java/uk/minersonline/Minecart/glfw/listener) A [Key Listener](/src/main/java/uk/minersonline/Minecart/glfw/listener/KeyListener.java) and a [Window Resize Listener](/src/main/java/uk/minersonline/Minecart/glfw/listener/WindowResizeListener.java) are configured for use with GLFW. Both are singletons.

   1) [Key Listener](/src/main/java/uk/minersonline/Minecart/glfw/listener/KeyListener.java)
   _Example for quering if a key is pressed during the game loop:_
   ``` java
     // Get KeyListener instance
     KeyListener keyListener = KeyListener.getInstance();
     if (keyListener.isKeyPressed(GLFW_KEY_SPACE)) {
            System.out.println("Space key is being pressed!");
        }
   ```
   2) [Window Resize Listener](/src/main/java/uk/minersonline/Minecart/glfw/listener/WindowResizeListener.java): This listener will execute whatever is inside the method `reshape(long window, int width, int height);` whenever GLFW detects that the window has been resized.
