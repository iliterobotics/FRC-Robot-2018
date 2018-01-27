package org.ilite.frc.common.sensors;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;

public class LidarLite
{
	//Example code:
	//https://github.com/PulsedLight3D/LIDARLite_Basics
	//https://books.google.com/books?id=pHeWWBY3cPEC&pg=PA402&lpg=PA402&dq=lidar+light+example+code&source=bl&ots=SUQp-ltY_q&sig=nIM9QGPjCILXEax58yyHsXLYiUA&hl=en&sa=X&ved=0ahUKEwiTlInOsvjYAhVR6GMKHWH2ARE4ChDoAQguMAE#v=onepage&q=lidar%20light%20example%20code&f=false
	//https://www.chiefdelphi.com/forums/showthread.php?p=1532896
	
	//Other:
	//https://tecexco.wordpress.com/2016/11/26/lidar-v-3-interfacing-with-arduino/
	//http://learn.trossenrobotics.com/projects/154-lidar-light-getting-started-guide.html
	//https://learn.sparkfun.com/tutorials/lidar-lite-v3-hookup-guide
	//https://www.robotshop.com/blog/en/lidar-lite-laser-rangefinder-simple-arduino-sketch-of-a-180-degree-radar-15284
	//https://static.garmin.com/pumac/LIDAR_Lite_v3_Operation_Manual_and_Technical_Specifications.pdf
	public LidarLite(int I2Cport)
	{
		I2C lidari2c = new I2C(Port.kOnboard, I2Cport);
		
	}
}
