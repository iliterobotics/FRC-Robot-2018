# ILITE Robotics 2018 FRC Robot Code

## Project Structure
```
FRC-Robot-2018/
   common/ - This contains all of the types used in communications, on the display(s), in vision, and on the robot.
   robot/ - Our robot code lives here.  It depends on the common and visionRIO projects.
   display/ - Our configuration and output display lives here.  It may incorporate Shufflboard at some point.
   visionRIO/ - Our vision code start out being specific to how the RoboRIO will process images and distill them to targeting info.
```
> Note that we will do vision processing on the RIO.  If you disagree, then get this 'simple' version of vision working (from processing all the way through integration into robot code and driver inputs) early.  We'll then discuss other options.

---
## General Mantra for Data Structures & Network Communications
1. The first step to applying neural nets to our robot is to collect data, even when we aren't using it directly.  Once we realize how we can apply neural nets, we will have a great set of training data if we've already collected it all!  Therefore:
   * All Joystick inputs, sensory data, and outputs (including Talon, Pneumatic and custom outputs like LED's) will be managed via the Codex framework.
   * Local state variables may be put into a Codex framework if you feel it is worth capturing.  Hint: it probably is, but only AFTER we have a better understanding of the states we want to capture.  That way we aren't constantly changing the enumerations so quickly.
1. Critical configuration data will use NetworkTables. Generally this is most custom data from the driver's station to the robot.  
   * Since 2015, the NetworkTables protocol has been the only network comms that have worked 99% of the time (albeit a bit bandwidth-heavy).
   * NetworkTables had some issues with stale cache data in the past, but v4.0 may have addressed this.
1. Single-use or low rate data may be in JSON format, and is sent via NetworkTables (assumes v4.0 still includes JSON support).  
   * If NetworkTables does not support JSON, or it has a critical flaw with JSON, it is somewhat easy to use the Codex IO code to create a custom String data sender.  
   * For easy JSON conversion, use Google's GSON library.
1. High-frequency data from the robot to the driver's station, such as our sensor and joystick data, should use the Codex protocols over UDP.
1. Storing data to a file may happen either on the RIO (to a mounted USB drive) or to the driver's station.
   * JSON data should have a .json extension
   * Codex data should be stored in a CSV format with a .csv extension
   * As soon as possible post-match, get the data to our (TBD) Google Sheet for analysis by the pit crew.
1. The WPILIB screensteps describe how to setup our wireless bridge to be similar to the QoS and bandwidth limitations live on a field.  When testing a robot during a 'live' match in the classroom, we should set those parameters up. 

---
## Deploying Code to the RoboRIO
1. Install the latest FRC Update Suite from here
   * This installs the mDNS handler needed for the computer to communicate with the RoboRIO.  
   * NOTE - you will need to create an NI account.  Who knows why they force this step...  If you don't feel comfortable making one, ask around - someone already has this installer.
> <http://www.ni.com/download/first-robotics-software-2015/5112/en/>
1. Setting up Eclipse
   * Go to www.eclipse.org and install the latest "Eclipse for Java Developers".
   * Go to www.git-scm.org and install the Git Bash terminal.  If you want help from your mentors in getting out of a mess with Git, they'll need this tool.  This terminal is also a 'Linux-lite' terminal - many standard unix commands work.  Thus it is far superior to the command prompt for platform-independent code development in Windows.
1. Cloning the Development Branch
   * Use `git clone` to clone this URL:
   * <https://github.com/iliterobotics/FRC-Robot-2018.git>
1. Import the Gradle Project in Eclipse
   * Go to Import > Gradle > Gradle Project
   * Click `next` to get past the Welcome Screen
   * Browse to the root folder of the gradle project you cloned in step 4.
   * Keep clicking `next` until you're done
1. Deploying to the RoboRIO through the command line
   * This is preferrable if you like to keep your build history separate from your Eclipse Java console output.
   * Open your terminal and navigate to your project directory, then build and deploy with the following command:
      * Linux (or Git Bash on Windows): `./gradlew build deploy`
      * Windows: `gradlew build deploy`
   * The `--offline` argument can be used at competition when internet is not available.
   * If you're having weird issues you can try cleaning the project and refreshing the dependencies with `gradlew clean cleanEclipse build eclipse --refresh-dependencies`
1. Deploying to the RoboRIO through the Eclipse GUI 
   1. Open the Gradle Task View by going to Window > Show View > Other... > Gradle > Gradle Tasks
   1. Scroll to `deploy` and double-click
---
## Commiting Code to Github
[Stephen TBD]
---
## Basic Linux Commands
[Jesse/Chris/(or really anyone who wants to chip in) TBD]
