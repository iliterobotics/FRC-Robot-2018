package org.ilite.frc.display.auton;

import java.util.List;

import org.ilite.frc.common.types.ECross;
import org.ilite.frc.common.types.ECubeAction;
import org.ilite.frc.common.types.EStartingPosition;
import org.ilite.frc.common.types.ESupportedTypes;
import org.ilite.frc.display.frclog.data.RobotDataStream;

import com.flybotix.hfr.util.lang.EnumUtils;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
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
    
    HBox h = new HBox(
        labeledDropdown(ECubeAction.class),
        labeledDropdown(EStartingPosition.class),
        labeledDropdown(ECross.class)
    );
    
    h.setSpacing(10d);
    root.setCenter(h);
    BorderPane.setAlignment(h, Pos.CENTER);
  
    primaryStage.setTitle("Hello World!");
    primaryStage.setScene(scene);
    primaryStage.show();
  }
  
  private static <E extends Enum<E>> VBox labeledDropdown(Class<E> pEnumeration) {
    List<E> enums = EnumUtils.getEnums(pEnumeration, true);
    Label label = new Label(toPrettyCase(pEnumeration.getSimpleName().substring(1)));
    label.setTextAlignment(TextAlignment.CENTER);
    ComboBox<E> combo = new ComboBox<>(FXCollections.observableArrayList(enums));
    combo.setOnAction(
        event -> 
        RobotDataStream.inst().sendDataToRobot(
            pEnumeration.getSimpleName(), 
            ESupportedTypes.INTEGER, 
            Integer.toString(combo.getSelectionModel().getSelectedItem().ordinal()))
    );
    combo.setValue(enums.get(0));
    VBox result = new VBox(label, combo);
    return result;
  }
  
  private static String toPrettyCase(String pInput) {
    StringBuilder sb = new StringBuilder();
    sb.append(Character.toUpperCase(pInput.charAt(0)));
    for(int i = 1; i < pInput.length(); i++) {
      if(Character.isUpperCase(pInput.charAt(i))) {
        sb.append(' ');
      }
      sb.append(pInput.charAt(i));
    }
    return sb.toString();
  }
}
