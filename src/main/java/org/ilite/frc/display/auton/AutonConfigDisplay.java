package org.ilite.frc.display.auton;

import org.ilite.frc.common.types.ESupportedTypes;
import org.ilite.frc.display.frclog.data.RobotDataStream;

import javafx.application.Application;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class AutonConfigDisplay extends Application {

  public static void main(String[] pArgs) {
    launch(pArgs);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    BorderPane root = new BorderPane();
    Scene scene = new Scene(root, 1920, 800);

    Property<String> startPosition = new SimpleStringProperty();
    Property<String> doWhatWithCube = new SimpleStringProperty();
    Property<String> allowFieldCrossing = new SimpleStringProperty();
    
    startPosition.addListener((obs,old,mew) -> RobotDataStream.inst().sendDataToRobot("Position", ESupportedTypes.INTEGER, mew));
    doWhatWithCube.addListener((obs,old,mew) -> RobotDataStream.inst().sendDataToRobot("Position", ESupportedTypes.INTEGER, mew));
    allowFieldCrossing.addListener((obs,old,mew) -> RobotDataStream.inst().sendDataToRobot("Position", ESupportedTypes.INTEGER, mew));
    
    root.setCenter(new HBox(
        labeledDropdown("Start Position", startPosition, "Left", "Middle", "Right"),
        labeledDropdown("Place the Cube Here", doWhatWithCube, "Switch", "Scale", "Exchange", "Don't Place"),
        labeledDropdown("Allow Field Cross", allowFieldCrossing, "Don't Allow", "Carpet", "Platform")
    ));
  
    primaryStage.setTitle("Hello World!");
    primaryStage.setScene(scene);
    primaryStage.show();
  }
  
  private VBox labeledDropdown(String pLabel, Property<String> pTargetValue, String... pValues) {
    Label label = new Label(pLabel);
    label.setTextAlignment(TextAlignment.CENTER);
    ComboBox<String> combo = new ComboBox<>(FXCollections.observableArrayList(pValues));
    combo.setOnAction(event -> pTargetValue.setValue(combo.getSelectionModel().getSelectedItem()));
    combo.setValue(pValues[0]);
    VBox result = new VBox(label, combo);
    return result;
  }
}
