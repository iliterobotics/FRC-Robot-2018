FRC-Vision: Ilite 2015

2015 Vision code for the Battlefield High School Ilite team. 

<h1>Dependencies</h1>
<ul>
<li>Gradle: This project uses gradle to compile and generate eclipse workspaces. Gradle can be found at: 
https://gradle.org/</li>

 <li>You can also get gradle at it's eclipse plugin 
    site http://dist.springsource.com/release/TOOLS/update/e4.4/ .
    Go to the install menu in eclipse (help -> install new software) and add a plugin site.
    Install the plugin and restart eclipse. You can now import gradle projects into your workspace via file -> import -> gradle.</li>

<li>OpenCV: Download OpenCV: http://docs.opencv.org/doc/tutorials/introduction/desktop_java/java_dev_intro.html Put openCV into libraries directory, with root of opencv named "opencv" </li>
</ul>

<h1>To Compile</h1>
On the root of the project, run: 

gradle build eclipse

This will compile the project and generate Eclipse project. 