package org.ilite.frc.display.frclog.display;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.types.ECubeAction;
import org.ilite.frc.common.types.ECubeTarget;
import org.ilite.frc.common.types.EDriveTrain;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.common.types.EPigeon;
import org.ilite.frc.common.types.EPowerDistPanel;
import org.ilite.frc.common.util.SystemUtils;
import org.ilite.frc.display.frclog.data.RobotDataStream;
import org.ilite.frc.robot.SimpleNetworkTable;

import com.flybotix.hfr.util.lang.EnumUtils;
import com.flybotix.hfr.util.log.ELevel;
import com.flybotix.hfr.util.log.Logger;
//import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import edu.wpi.first.wpilibj.Timer;
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

public class DisplayApplication extends Application{
  
  //List<Color> positiveColors = new 

  // Just a bottom border to separate the charts visually
  Border bottomBorder = new Border(new BorderStroke(Color.TRANSPARENT, Color.TRANSPARENT, Color.BLACK, Color.TRANSPARENT,
                                                    BorderStrokeStyle.NONE, BorderStrokeStyle. NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
                                                    CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0), Insets.EMPTY));
  
  Stage stage = new Stage(StageStyle.UNDECORATED);
  static Map<String, List<String>> dataMatrix = new HashMap<>();
  


  // Color definitions for positive colors
  Color[] positiveColors = { Color.web("#FEE090"), Color.web("#FDAE61"), Color.web("#F46D43"), Color.web("#D73027") };
  Class<?> mSelectedCodexToLoad = null;
  
  public DisplayApplication() {
  }
  
  public static <E extends Enum<E>> List<String> getKeys(Class<E> pEnum) {
    List<String> keys = new ArrayList<>();
    EnumUtils.getEnums(pEnum).forEach(e -> keys.add(e.toString()));
    return keys;
  }
  
  public static <E extends Enum<E>> void putInMatrix(Class<E> pEnum) {
    dataMatrix.put(pEnum.getSimpleName(), getKeys(pEnum));
  }
  
  public static void matrixInit() {
	  dataMatrix.put("operator", getKeys(ELogitech310.class));
	  dataMatrix.put("driver", getKeys(ELogitech310.class));
	  dataMatrix.put("pigeon", getKeys(EPigeon.class));
	  dataMatrix.put("pdp", getKeys(EPowerDistPanel.class));
	  dataMatrix.put("drivetrain", getKeys(EDriveTrain.class));;
	  dataMatrix.put("vision", getKeys(ECubeTarget.class));
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public void start(Stage primaryStage) throws Exception {
//
    GridPane root = new GridPane();
    Scene scene = new Scene(root, 1920, 800);
    
    for(ELogitech310 e : ELogitech310.values()) {
    	
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
    
//    root.add(graphconfig, 0, 0);
    primaryStage.setTitle("Test");
    primaryStage.setScene(scene);
    primaryStage.show();
  }
  
  private void loadChart() {
  }
  
  private static void writeHeaders(Map<String, List<String>> dataMap) throws IOException {
    BufferedWriter writer = null;
    for(String key : dataMap.keySet()) {
      File file = new File(String.format("./logs/%s-log.csv", key));
      if(!file.exists()) file.createNewFile();
      dataMap.get(key).add(0, "TIME");
      writer = new BufferedWriter(new FileWriter(file, true));
      writer.append(SystemUtils.toCsvRow(dataMap.get(key)));
      writer.flush();
    }
    writer.close();
  }
  
  public static void writeData(Entry<String, List<String>> entry) {
	  File file = new File(String.format("./logs/%s-log.csv", entry.getKey()));
	  BufferedWriter bWriter = null;
	  try {
  	  if(!file.exists()) file.createNewFile();
  	  
  	  bWriter = new BufferedWriter(new FileWriter(file, true));
  	  
//  	  entry.getValue().add(0, SystemSettings.smartDashboard.getEntry("TIME").getNumber(-1).toString());
  	  
  	  String csvRow = SystemUtils.toCsvRow(entry.getValue().stream()
  	                             .map(entryKey -> SystemSettings.smartDashboard.getEntry(entryKey).getNumber(-1).toString())
  	                             .collect(Collectors.toList()));
  	  bWriter.append(csvRow + "\n");
  	  bWriter.flush();
	  }
	  catch (Exception e) {
	    e.printStackTrace();
		  System.err.println("Error xd");
	  }
	  
  }
  
  public static void main(String[] pArgs) throws Exception {
    //launch(pArgs);
    matrixInit();
    Logger.setLevel(ELevel.DEBUG);
    writeHeaders(dataMatrix);
    while(true) dataMatrix.entrySet().forEach(entry -> writeData(entry));
  }
}
