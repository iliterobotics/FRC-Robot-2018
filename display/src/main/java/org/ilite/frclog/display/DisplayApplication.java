package org.ilite.frclog.display;

import org.ilite.frc.robot.config.SystemSettings;
import org.ilite.frc.robot.types.ELogitech310;
import org.ilite.frc.robot.types.ENavX;
import org.ilite.frc.robot.types.EPowerDistPanel;
import org.ilite.frc.robot.types.ETalonSRX;

import com.flybotix.hfr.codex.CodexReceiver;
import com.flybotix.hfr.io.Protocols;
import com.flybotix.hfr.io.receiver.IReceiveProtocol;
import com.flybotix.hfr.util.log.ELevel;
import com.flybotix.hfr.util.log.Logger;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DisplayApplication  extends Application{

  @Override
  public void start(Stage primaryStage) throws Exception {

    VBox root = new VBox();
    Scene scene = new Scene(root, 1920, 300);
    primaryStage.setTitle("Hello World!");
    primaryStage.setScene(scene);
    primaryStage.show();
  }
  
  public static void main(String[] pArgs) {
    Logger.setLevel(ELevel.DEBUG);
    IReceiveProtocol receiver = Protocols.createReceiver(SystemSettings.CODEX_DATA_PROTOCOL, SystemSettings.DRIVER_STATION_CODEX_DATA_RECEIVER_PORT, "");
    new CodexReceiver<>(EPowerDistPanel.class, receiver).addListener(codex -> System.out.println(codex));
    new CodexReceiver<>(ELogitech310.class, receiver).addListener(codex -> System.out.println(codex));
    new CodexReceiver<>(ETalonSRX.class, receiver).addListener(codex -> System.out.println(codex));
    new CodexReceiver<>(ENavX.class, receiver).addListener(codex -> System.out.println(codex));
//    launch(pArgs);
  }
}
