package org.ilite.frc.display.frclog.networktable;

import java.io.*;
import java.util.Timer;
import java.util.TimerTask;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.display.frclog.data.RobotDataStream;
import org.ilite.frc.display.frclog.data.*;
import com.flybotix.hfr.util.log.ELevel;
import com.flybotix.hfr.util.log.Logger;

import edu.wpi.first.networktables.*;
import eu.hansolo.fx.horizon.Data;
import eu.hansolo.fx.horizon.HorizonChart;
import eu.hansolo.fx.horizon.Series;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;



public class NetworkTablesDisplay{
	private static final double UPDATE_RATE_HZ = 0.5;
	private static final long UPDATE_PERIOD_MS = (long)(1000d/UPDATE_RATE_HZ);	
	private NetworkTable table;
	String name = "SmartDashboard";
	int flag = 0x20;

	private NetworkTablesDisplay() {
	    table = NetworkTableInstance.getDefault().getTable(name);
	    
	    //table.addEntryListener(new NetworkListener(), flag);
	    new Timer().scheduleAtFixedRate(new TimerTask() {
	        public void run() {
	            StringBuilder sb = new StringBuilder();
	            for(String key : table.getKeys()) {	
	              sb.append(key).append('=').append((table.getEntry(key)));
	            }
	            System.out.println(sb);
	          }
	        
	      }, UPDATE_PERIOD_MS, UPDATE_PERIOD_MS);
	    }   
	
	
	public static void main(String[] args) {
		new NetworkTablesDisplay();
	}
	


private class NetworkListener implements TableEntryListener{

	public void valueChanged(NetworkTable table, String key, NetworkTableEntry entry, NetworkTableValue value,
			int flags) {
		
		
	}
	
//	public static void main(String[] args) {
//		NetworkTable table = NetworkTableInstance.getDefault().getTable("");
//		
//		;
//		table.addEntryListener(new NetworkListener(), EntryListenerFlags.kNew | EntryListenerFlags.kDelete);
//	}
	
	

}
}

