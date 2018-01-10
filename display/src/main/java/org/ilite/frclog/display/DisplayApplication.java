package org.ilite.frclog.display;

import java.io.File;

import org.ilite.frclog.data.RobotDataElementCache;
import org.ilite.frclog.data.RobotDataStream;

import com.flybotix.hfr.util.log.ELevel;
import com.flybotix.hfr.util.log.Logger;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class DisplayApplication  extends Application{

  // Just a bottom border to separate the charts visually
  Border bottomBorder = new Border(new BorderStroke(Color.TRANSPARENT, Color.TRANSPARENT, Color.BLACK, Color.TRANSPARENT,
                                                    BorderStrokeStyle.NONE, BorderStrokeStyle. NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
                                                    CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0), Insets.EMPTY));


  // Color definitions for positive colors
  Color[] positiveColors = { Color.web("#FEE090"), Color.web("#FDAE61"), Color.web("#F46D43"), Color.web("#D73027") };
  Class<?> mSelectedCodexToLoad = null;

  @Override
  public void start(Stage primaryStage) throws Exception {
//
    BorderPane root = new BorderPane();
    Scene scene = new Scene(root, 1920, 800);
    
//    for(ELogitech310 e : ELogitech310.values()) {
//      List<Data<Long>> items = FXCollections.observableArrayList(new Data<Long>(0l, 1d), new Data<Long>(1l, 1d));
//      Series<Long> series = new Series<>(items);
//      HorizonChart<Long> chart = new HorizonChart<>(1, series);
//      chart.setPrefSize(800, 80);
//      chart.setBorder(bottomBorder);
//      chart.setPositiveColors(Color.web("#FEE090"), Color.web("#FDAE61"));
////      public Data(final String NAME, final T X, final double Y, final Color COLOR)
//      RobotDataStream.inst().addListenerToData(e, value -> {
//        if(value.value != null) {
//          series.getItems().add(new Data<>("", value.time, value.value));
//          series.refresh();
//        }
//      });
//      Label l = new Label(e.name());
//      l.setTextAlignment(TextAlignment.RIGHT);
//      root.add(l, 0, e.ordinal());
//      root.add(chart, 1, e.ordinal());
//    }
    
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
    
    root.setBottom(graphconfig);
    primaryStage.setTitle("Hello World!");
    primaryStage.setScene(scene);
    primaryStage.show();
  }
  
  private void loadChart() {
    
  }
  
  public static void main(String[] pArgs) {
    Logger.setLevel(ELevel.DEBUG);
    RobotDataStream.inst();
    launch(pArgs);
//    Logger.setLevel(ELevel.INFO);
    
  }
}
