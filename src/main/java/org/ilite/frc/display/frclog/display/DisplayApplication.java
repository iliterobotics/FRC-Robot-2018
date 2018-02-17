package org.ilite.frc.display.frclog.display;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ilite.frc.common.input.DriverInputUtils;
import org.ilite.frc.common.types.EDriveTrain;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.common.types.EPigeon;
import org.ilite.frc.common.types.EPowerDistPanel;
import org.ilite.frc.common.types.ETalonSRX;
import org.ilite.frc.display.frclog.data.RobotDataElementCache;
import org.ilite.frc.display.frclog.data.RobotDataStream;
import org.ilite.frc.display.frclog.data.RobotDataStream;

import com.flybotix.hfr.util.log.ELevel;
import com.flybotix.hfr.util.log.Logger;
//import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

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
import javafx.scene.layout.BorderPane;
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
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.io.File;
import java.io.BufferedWriter;

public class DisplayApplication extends Application{

  // Just a bottom border to separate the charts visually
  Border bottomBorder = new Border(new BorderStroke(Color.TRANSPARENT, Color.TRANSPARENT, Color.BLACK, Color.TRANSPARENT,
                                                    BorderStrokeStyle.NONE, BorderStrokeStyle. NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
                                                    CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0), Insets.EMPTY));
  
  Stage stage = new Stage(StageStyle.UNDECORATED);
  static List<String> driverInputKeys = new ArrayList<String>();
  static List<String> operatorKeys = new ArrayList<String>();
  static List<String> pigeonKeys = new ArrayList<String>();
  static List<String> pdpKeys = new ArrayList<String>();
  //static List<String> navxKeys = new ArrayList<String>();
  static List<String> drivetrainKeys = new ArrayList<String>();
  static List<String> talonKeys = new ArrayList<String>();
  static List<List<String>> dataMatrix = new ArrayList<>();


  // Color definitions for positive colors
  Color[] positiveColors = { Color.web("#FEE090"), Color.web("#FDAE61"), Color.web("#F46D43"), Color.web("#D73027") };
  Class<?> mSelectedCodexToLoad = null;
  
  public static void addKeys() {
	  for(EPigeon e : EPigeon.values()) {
		  pigeonKeys.add(e.toString());
	  }
	  for(EPowerDistPanel e : EPowerDistPanel.values()) {
		  pdpKeys.add(e.toString());
	  }
	  for(EDriveTrain e : EDriveTrain.values()) {
		  drivetrainKeys.add(e.toString());
	  }
	  for(ETalonSRX e : ETalonSRX.values()) {
		  talonKeys.add(e.toString());
	  }
  }
  public static void matrixInit() {
	  addKeys();
	  dataMatrix.add(driverInputKeys);
	  dataMatrix.add(operatorKeys);
	  //dataMatrix.add(pigeonKeys);
	  dataMatrix.add(pdpKeys);
	  //dataMatrix.add(navxKeys);
	  dataMatrix.add(drivetrainKeys);
	  dataMatrix.add(talonKeys);
  }
  
  @SuppressWarnings("unchecked")
@Override
  public void start(Stage primaryStage) throws Exception {
//
    GridPane root = new GridPane();
    Scene scene = new Scene(root, 1920, 800);
    boolean data;
    String Sdata;
    
    for(ELogitech310 e : ELogitech310.values()) {
    	
      driverInputKeys.add(e.toString());
      operatorKeys.add(e.toString()+"2");
      ObservableList<Data<Long>> items = FXCollections.observableArrayList(new Data<Long>(0l, 1d), new Data<Long>(1l, 1d));
      Series<Long> series = new Series<>(items);
      HorizonChart<Long> chart = new HorizonChart<>(1, series);
      chart.setPrefSize(800, 80);
      chart.setBorder(bottomBorder);
      chart.setPositiveColors(Color.web("#FEE090"), Color.web	("#FDAE61"));
//      public Data(final String NAME, final T X, final double Y, final Color COLOR)
//     RobotDataStream.inst().addListenerToData(e, value -> {
//       if(value.value != null) {
//          series.getItems().addAll((Collection<? extends Data<Long>>) new Data<>("", value.time, value.value));
//          series.refresh();
//       }
//      });
      Label l = new Label(e.name());
     l.setTextAlignment(TextAlignment.RIGHT);
     root.add(l, 0, e.ordinal());
      root.add(chart, 1, e.ordinal());
   }
    
    
    
    ComboBox<Class<Enum<?>>> combo = new ComboBox<>(FXCollections.observableArrayList(RobotDataStream.inst().getRegisteredCodexes()));
    Button resetlogs = new Button("Reset Logs");
    Button loadLogs = new Button("Choose Log to Import");
    HBox graphconfig = new HBox(25d, resetlogs, combo, loadLogs);
    graphconfig.setAlignment(Pos.CENTER);
        
    combo.setOnAction(event -> {
      mSelectedCodexToLoad = combo.getSelectionModel().getSelectedItem();
      loadLogs.setDisable(mSelectedCodexToLoad == null);
    });
    
    
    resetlogs.setOnAction(event -> {
      RobotDataStream.inst().resetLogs();
    });

    loadLogs.setDisable(mSelectedCodexToLoad == null);
    loadLogs.setOnAction(event -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Open Data Log File");
      fileChooser.getExtensionFilters().addAll(new ExtensionFilter("CSV Files", "*.csv"));
      File selectedFile = fileChooser.showOpenDialog(primaryStage);
      
      if(selectedFile != null && mSelectedCodexToLoad != null) {
//        RobotDataStream.inst().loadUnsafeCodexHistoryFromFile(mSelectedCodexToLoad, selectedFile.toPath());
        loadChart();
      }
      
    });
    
    //root.setBottom(graphconfig);
    primaryStage.setTitle("Test");
    primaryStage.setScene(scene);
    primaryStage.show();
  }
  
  private void loadChart() {
    
  }
  
  public static void dumpToCSV() {
	  File file = new File("./log.csv");
	  BufferedWriter bWriter = null;
	  try {
	  if(!file.exists()){
		  file.createNewFile();
	  }
	  bWriter = new BufferedWriter(new PrintWriter(file));
	  for(List<String> xs : dataMatrix)
	  { 
		  for(String s : xs) {
			  
			  bWriter.write(s+", " + SmartDashboard.getNumber(s, -99) + "\n");
			  System.out.println(s+", " + SmartDashboard.getNumber(s, -99) + "\n");
		  }
	  }
	  bWriter.close();
	  
	  }
	  catch (Exception e) {
		  System.err.println("Error xd");
	  }
	  
  }
  
  public static void main(String[] pArgs) {
	launch(pArgs);
	matrixInit();
    Logger.setLevel(ELevel.DEBUG);
    RobotDataStream.inst();
    dumpToCSV();
//    Logger.setLevel(ELevel.INFO);
    
  }
}
