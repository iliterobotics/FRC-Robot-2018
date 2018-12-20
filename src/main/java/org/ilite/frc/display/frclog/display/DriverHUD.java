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
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.robot.Data;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DriverHUD extends Application {

    Image abtn = new Image( "abtn.png" );
    Image bbtn = new Image( "bbtn.png" );
    Image wht = new Image( "White.png" );
    Image xbtn = new Image( "xbtn.png" );
    Image ybtn = new Image( "ybtn.png" );


    KeyButtons aKey = new KeyButtons( abtn, "A" );
    KeyButtons bKey = new KeyButtons( bbtn, "B" );
    KeyButtons xKey = new KeyButtons( xbtn, "X" );
    KeyButtons yKey = new KeyButtons( ybtn, "Y" );


    public void start( Stage stage ) {

        BorderPane root = new BorderPane( );
        Scene scene = new Scene( root, Color.WHITE );
        Canvas canvas = new Canvas( 500, 500 );

        //Initialize images

        //Draw Images
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage( abtn, canvas.getWidth()/2, canvas.getHeight()/2 );

        root.setCenter( canvas );

        stage.setScene( scene );

        HashSet<String> inputs = new HashSet<>(  );
        ArrayList<KeyButtons> registered = new ArrayList<>(  );

        scene.setOnKeyPressed( e -> inputs.add( e.getCode().toString() ));

        scene.setOnKeyReleased( e -> inputs.remove( e.getCode().toString() ) );

        registered.add( yKey );

        final long startTime = System.nanoTime();

        new AnimationTimer() {
            public void handle( long currentNanoTime ) {

                if ( inputs.contains( "A" ) && !registered.contains( aKey ) ) {
                    registered.add( aKey );
                } else if ( !inputs.contains( "A" ) ){
                    registered.remove( aKey );
                }

                if ( inputs.contains( "B" ) && !registered.contains( bKey ) ) {
                    registered.add( bKey );
                } else if ( !inputs.contains( "B" ) ){
                    registered.remove( bKey );
                }

                if ( inputs.contains( "X" ) && !registered.contains( xKey ) ) {
                    registered.add( xKey );
                } else if ( !inputs.contains( "X" ) ){
                    registered.remove( xKey );
                }

//                if ( inputs.contains( "Y" ) && !registered.contains( yKey )) {
//                    registered.add( yKey );
//                } else if ( !inputs.contains( "Y" ) ) {
//                    registered.remove( yKey );
//                }


                System.out.println( inputs.toString() );
                gc.clearRect( 0, 0, canvas.getWidth(), canvas.getHeight() );
                for ( int i = 0; i < registered.size(); i++ ) {
                    gc.drawImage( registered.get( i ).getImage(), canvas.getWidth() * ( ( ( i )/ ( ( double )registered.size() ) ) ), canvas.getHeight()/2  );
                }

            }
        }.start();

        stage.show();

    }

    public static void main ( String[] args ) {
        launch( args );
    }

}

