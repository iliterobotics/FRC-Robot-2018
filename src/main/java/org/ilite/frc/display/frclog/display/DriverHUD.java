package org.ilite.frc.display.frclog.display;

import com.flybotix.hfr.codex.Codex;
import com.flybotix.hfr.codex.CodexOf;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.robot.Data;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DriverHUD extends Application {

    // KeyButtons aKey = new KeyButtons( abtn, "A" );
    // KeyButtons bKey = new KeyButtons( bbtn, "B" );
    // KeyButtons xKey = new KeyButtons( xbtn, "X" );
    // KeyButtons yKey = new KeyButtons( ybtn, "Y" );

    private String state = "";
    private String realState = "";

    public void start(Stage stage) {

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, Color.PURPLE);
        Canvas canvas = new Canvas(1000, 300);

        // Initialize images

        //Gradients
        Stops[] stops = new Stop[] { new Stop( 0, Color.PURPLE ), new Stop( 1, Color.GREEN ) };
        LinearGradient linear = new LinearGradient( 0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops );

        Rectangle rect = new Rectangle( 0, 0, 1000, 300);
        rect.setFill( linear );
        root.getChildren().add( rect );

        // Draw Images
        GraphicsContext gc = canvas.getGraphicsContext2D();
        // gc.drawImage( abtn, canvas.getWidth()/2, canvas.getHeight()/2 );

        root.setCenter(canvas);

        stage.setScene(scene);

        HashSet<String> inputs = new HashSet<>();
        ArrayList<KeyButtons> registered = new ArrayList<>();

        // scene.setOnKeyPressed( e -> inputs.add( e.getCode().toString() ));

        // scene.setOnKeyReleased( e -> inputs.remove( e.getCode().toString() ) );

        // registered.add( yKey );

        final long startTime = System.nanoTime();

        new AnimationTimer() {

            public void handle(long currentNanoTime) {
                // state = SystemSettings.LOGGING_TABLE.getInstance().getEntry("Carriage
                // State").getString("heck.");

                // System.out.println(SystemSettings.LOGGING_TABLE.getInstance().getEntry("Carriage State").getString("heck."));

                if (!(realState.equals(state))) {
                    realState = state;
                }

                switch (realState) {
                case ("KICKING"):
                    System.out.println("KICKING");
                case ("RESET"):
                    System.out.println("RESET");
                case ("GRAB_CUBE"):
                    System.out.println("GRAB_CUBE");
                default:
                    System.out.println(state);

                }

            }
        }.start();

        stage.show();

    }

    public static void main(String[] args) {
        // TODO: Make more "professional"
        // while (true)System.out.println(SystemSettings.LOGGING_TABLE.getInstance().getEntry("Carriage State").getString("heck."));
        // C:\Users\Daniel T\Desktop\robotics\FRC-Robot-2018\lib\jfxrt.jar
        launch(args);
    }

}
