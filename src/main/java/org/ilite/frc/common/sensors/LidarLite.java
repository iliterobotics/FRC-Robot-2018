package org.ilite.frc.common.sensors;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;

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
	private final Timer t = new Timer();
	private TimerTask lidarReadTask = null;
	private int mCount = 0;
	private byte[] distance;	
		
	
	private final static int LIDAR_ADDR = 0x62;
	private final static int LIDAR_CONFIG_REGISTER = 0x00;
	private final static int LIDAR_DISTANCE_REGISTER = 0x8f;
	
	public LidarLite()
	{
		lidari2c = new I2C(Port.kOnboard, LIDAR_ADDR);
		hasSignal = false;
		distance = new byte[2];
	}
	
	public byte[] getDistanceRegister() {
		return Arrays.copyOf(distance, 2);
	}
	
	
	public double getDistance()
	{
//		return "Distance[0]: " + Double.toString(distance.distance[0]) + " \n Distance[1]: " + Double.toString(distance.distance[1]);
//		return ((double) Integer.toUnsignedLong(distance[0] << 8)) + ((double) Byte.toUnsignedInt(distance[1])) / 100.0d;
		
		int distCm = (int) Integer.toUnsignedLong(distance[0] << 8) + Byte.toUnsignedInt(distance[1]);
		return distCm / 100.0;
	}
	
	public boolean checkSignal()
	{
		return hasSignal;
	}
	
	public void start() {
		lidarReadTask = new TimerTask() {
			@Override
			public void run() {
				update();
			}
		};
		t.scheduleAtFixedRate(lidarReadTask, 1000, 1000);
	}
	
	public void stop() {
		lidarReadTask.cancel();
	}
	
	public void update()
	{
		if(lidari2c.write(LIDAR_CONFIG_REGISTER, 0x04b))
		{
			hasSignal = false;
			return;
		}
		edu.wpi.first.wpilibj.Timer.delay(0.05);
		if(!lidari2c.read(LIDAR_DISTANCE_REGISTER, 2, distance))
		{
			return;
		} else {
			hasSignal = true;
		}
		edu.wpi.first.wpilibj.Timer.delay(0.05);
	}
}
