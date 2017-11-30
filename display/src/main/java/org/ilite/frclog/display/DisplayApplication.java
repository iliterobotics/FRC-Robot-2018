package org.ilite.frclog.display;

import java.util.List;

import org.ilite.frc.robot.types.ELogitech310;
import org.ilite.frclog.data.RobotDataStream;

import com.flybotix.hfr.util.log.ELevel;
import com.flybotix.hfr.util.log.Logger;

import eu.hansolo.fx.horizon.Data;
import eu.hansolo.fx.horizon.HorizonChart;
import eu.hansolo.fx.horizon.Series;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class DisplayApplication  extends Application{

  // Just a bottom border to separate the charts visually
  Border bottomBorder = new Border(new BorderStroke(Color.TRANSPARENT, Color.TRANSPARENT, Color.BLACK, Color.TRANSPARENT,
                                                    BorderStrokeStyle.NONE, BorderStrokeStyle. NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
                                                    CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0), Insets.EMPTY));


  // Color definitions for positive colors
  Color[] positiveColors = { Color.web("#FEE090"), Color.web("#FDAE61"), Color.web("#F46D43"), Color.web("#D73027") };

  @Override
  public void start(Stage primaryStage) throws Exception {
//
    GridPane root = new GridPane();
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
    
    Button resetlogs = new Button("Reset Logs");
    resetlogs.setOnMouseReleased(event -> {
      RobotDataStream.inst().resetLogs();
    });
    
    root.add(resetlogs, 0, 0);
//    
    primaryStage.setTitle("Hello World!");
    primaryStage.setScene(scene);
    primaryStage.show();
  }
  
  public static void main(String[] pArgs) {
    Logger.setLevel(ELevel.INFO);
    RobotDataStream.inst();
    launch(pArgs);
//    Logger.setLevel(ELevel.INFO);
    
  }
}
