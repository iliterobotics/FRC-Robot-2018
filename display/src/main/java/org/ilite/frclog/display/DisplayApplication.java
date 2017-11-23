package org.ilite.frclog.display;

import java.util.Timer;
import java.util.TimerTask;

import org.ilite.frc.robot.config.SystemSettings;
import org.ilite.frc.robot.types.ELogitech310;
import org.ilite.frc.robot.types.ENavX;
import org.ilite.frc.robot.types.EPowerDistPanel;
import org.ilite.frc.robot.types.ETalonSRX;

import com.flybotix.hfr.codex.CodexReceiver;
import com.flybotix.hfr.io.MessageProtocols;
import com.flybotix.hfr.io.receiver.IReceiveProtocol;
import com.flybotix.hfr.util.log.ELevel;
import com.flybotix.hfr.util.log.Logger;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DisplayApplication  extends Application{
  private static final double UPDATE_RATE_HZ = 25;
  private static final long UPDATE_PERIOD_MS = (long)(1000d/UPDATE_RATE_HZ);

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
//    IReceiveProtocol receiver = MessageProtocols.createReceiver(SystemSettings.CODEX_DATA_PROTOCOL, SystemSettings.DRIVER_STATION_CODEX_DATA_RECEIVER_PORT, "");
//    new CodexReceiver<>(EPowerDistPanel.class, receiver).addListener(codex -> System.out.println(codex));
//    new CodexReceiver<>(ELogitech310.class, receiver).addListener(codex -> System.out.println(codex));
//    new CodexReceiver<>(ETalonSRX.class, receiver).addListener(codex -> System.out.println(codex));
//    new CodexReceiver<>(ENavX.class, receiver).addListener(codex -> System.out.println(codex));
//    launch(pArgs);
    
    final NetworkTable nt = NetworkTable.getTable(ELogitech310.class.getSimpleName().toUpperCase());
    // valueChanged(ITable source, String key, Object value, boolean isNew);
    
    new Timer().scheduleAtFixedRate(new TimerTask() {
      public void run() {
        StringBuilder sb = new StringBuilder();
        for(ELogitech310 e : ELogitech310.values()) {
          nt.getNumber(e.name(), 0d);
        }
      }
    }, UPDATE_PERIOD_MS, UPDATE_PERIOD_MS);
  }
}
