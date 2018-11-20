package org.ilite.frc.display.sim;

import org.ilite.frc.robot.modules.drivetrain.MikeyProfile;

import javafx.application.Application;
import javafx.stage.Stage;
import simulation.TrackingSimulation;
import ui.FieldWindow;

public class SimDisplay extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage pStage) {
        TrackingSimulation mTrackingSimulation = new TrackingSimulation(new MikeyProfile(), 0.01);

        FieldWindow mFieldWindow = new FieldWindow(mTrackingSimulation, 0.01);

        mFieldWindow.start(pStage);
        mFieldWindow.startDrawing();
    }

}