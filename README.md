# ILITE Robotics 2018 FRC Robot Code Development Repository

## Deploying Code to the RoboRIO

1. Install the latest FRC Update Suite from here:
> <http://www.ni.com/download/first-robotics-software-2015/5112/en/>

  This installs the mDNS handler needed for the computer to communicate with the RoboRIO.

2. Setting up Eclipse
> Instructions for installing Eclipse and the necessary plugins can be found here:
> <http://wpilib.screenstepslive.com/s/4485/m/13503/l/599679-installing-eclipse-c-java>

3. Cloning the Development Branch
> Use `git clone` to clone this URL:
> <https://github.com/iliterobotics/FRC-Robot-2017.git>

4. Import the Gradle Project in Eclipse
> Go to Import > Gradle > Gradle Project
> Click `next` to get past the Welcome Screen
> Browse to the root folder of the gradle project you cloned in step 4.
> Keep clicking `next` until you're done

5. Deploying to the RoboRIO through the command line
> Open your terminal and navigate to your project directory, then build and deploy with the following command:

> The terminal can be opened with the <kbd>Shift</kbd> + Right-Click on Windows.

> Linux: `./gradlew build deploy`
> Windows: `gradlew build deploy`

6. Deploying to the RoboRIO through the Eclipse GUI 
> Open the Gradle Task View by going to Window > Show View > Other... > Gradle > Gradle Tasks
> Scroll to `deploy` and double-click




