package org.ilite.frclog.display.demo;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ilite.frclog.data.RobotDataElementCache;
import org.ilite.frclog.data.RobotDataStream;

import com.flybotix.hfr.cache.CodexElementHistory;
import com.flybotix.hfr.codex.CodexOf;
import com.flybotix.hfr.util.log.ELevel;
import com.flybotix.hfr.util.log.Logger;

import eu.hansolo.fx.horizon.Data;
import eu.hansolo.fx.horizon.HorizonChart;
import eu.hansolo.fx.horizon.Series;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Demo extends Application{

  @Override
  public void start(Stage primaryStage) throws Exception {
    BorderPane root = new BorderPane();
    Scene scene = new Scene(root, 1920, 800);
    
    Button resetlogs = new Button("Reset Logs");
    HBox graphconfig = new HBox(25d, resetlogs);
    graphconfig.setAlignment(Pos.CENTER);
        
    resetlogs.setOnAction(event -> {
      RobotDataStream.inst().resetLogs();
    });

    File f = new File("DriveTrainData.csv");
    if(!f.exists()) {
      System.err.println("Couldn't find data file " + f.getAbsolutePath());
      System.exit(-1);
    }
    

    RobotDataStream.inst().loadCodexHistoryFromFile(EDriveTrain.class, f.toPath());
    loadChart(root);
//
//    root.setBottom(graphconfig);
//    primaryStage.setTitle("Hello World!");
//    primaryStage.setScene(scene);
//    primaryStage.show();
  
  }
  
  private void loadChart(BorderPane root) {
    Series<Double> s = getChartData(EDriveTrain.RIGHT_TALON_MASTER_VOLTAGE);
    HorizonChart<Double> chart = new HorizonChart<>(1, s);
    root.setCenter(chart);
  }
  
  private static <E extends Enum<E> & CodexOf<Double>> Series<Double> getChartData(E pData) {
    List<Data<Double>> data = RobotDataElementCache.inst()
      .getHistoryOf(pData).getData()
      .stream()
      .map(element -> new Data<Double>(element.time, element.value))
      .collect(Collectors.toList());
    
    Series<Double> s = new Series<>(data, pData.toString());
    return s;
  }
  
  public static void main(String[] pArgs ) {
    Logger.setLevel(ELevel.DEBUG);
    launch(pArgs);
  }

}
