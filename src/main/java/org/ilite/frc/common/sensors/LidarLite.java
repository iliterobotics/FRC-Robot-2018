package org.ilite.frc.common.sensors;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.Timer;

public class LidarLite
{
	//Example code:
	//https://github.com/PulsedLight3D/LIDARLite_Basics
	//https://books.google.com/books?id=pHeWWBY3cPEC&pg=PA402&lpg=PA402&dq=lidar+light+example+code&source=bl&ots=SUQp-ltY_q&sig=nIM9QGPjCILXEax58yyHsXLYiUA&hl=en&sa=X&ved=0ahUKEwiTlInOsvjYAhVR6GMKHWH2ARE4ChDoAQguMAE#v=onepage&q=lidar%20light%20example%20code&f=false
	//https://www.chiefdelphi.com/forums/showthread.php?p=1532896
	//https://gist.github.com/tech2077/c4ba2d344bdfcddd48d2
	//https://github.com/Team254/FRC-2017-Public/blob/41446e26a510b499602b228a0aa21d68526f4cc4/src/com/team254/lib/util/drivers/LidarLiteSensor.java
	
	//Other:
	//https://tecexco.wordpress.com/2016/11/26/lidar-v-3-interfacing-with-arduino/
	//http://learn.trossenrobotics.com/projects/154-lidar-light-getting-started-guide.html
	//https://learn.sparkfun.com/tutorials/lidar-lite-v3-hookup-guide
	//https://www.robotshop.com/blog/en/lidar-lite-laser-rangefinder-simple-arduino-sketch-of-a-180-degree-radar-15284
	//https://static.garmin.com/pumac/LIDAR_Lite_v3_Operation_Manual_and_Technical_Specifications.pdf
	//https://www.robotshop.com/forum/lidar-lite-v3-communication-with-frc-roborio-t15519
	//https://www.reddit.com/r/FRC/comments/7g6oei/how_do_i_use_the_navxmxp_with_a_lidarlite/
	//https://www.chiefdelphi.com/forums/showthread.php?t=150887
	private I2C lidari2c;
	private boolean hasSignal;
	
	private class Distance {
		private byte[] distance;	
		
		public void setDistance(byte [] distance) { 
			this.distance = distance;
		}
	}
	
	private Distance distance = new Distance();
	private final static int LIDAR_ADDR = 0x62;
	private final static int LIDAR_CONFIG_REGISTER = 0x00;
	private final static int LIDAR_DISTANCE_REGISTER = 0x8f;
	
	public LidarLite()
	{
		lidari2c = new I2C(Port.kOnboard, LIDAR_ADDR);
		hasSignal = false;
		distance.distance = new byte[2];
	}
	
	
	
	public String getDistance()
	{
		return "Distance[0]: " + Double.toString(distance.distance[0]) + " \n Distance[1]: " + Double.toString(distance.distance[1]);
		//return ((double) Integer.toUnsignedLong(distance[0] << 8)) + ((double) Byte.toUnsignedInt(distance[1])) / 100.0d;
	}
	
	public boolean checkSignal()
	{
		return hasSignal;
	}
	
	public void update()
	{
		if(!lidari2c.write(LIDAR_CONFIG_REGISTER, 0x04))
		{
			hasSignal = true;
		} else {
			System.out.println("NO LIDAR INIT");
			return;
		}
		Timer.delay(0.04);
		if(!lidari2c.read(LIDAR_DISTANCE_REGISTER, 2, distance.distance))
		{
			System.out.println("LIDAR HAS VALUE: " + distance.distance[0] + " " + distance.distance[1]);
			hasSignal = true;
		} else {
			System.out.println("NO LIDAR!");
			return;
		}
		Timer.delay(0.005);
	}
}
