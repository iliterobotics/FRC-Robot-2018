package org.ilite.frc.display.frclog.display;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class DisplayConfig {
  public static final Property<String> ROBOT_IP_ADDRESS = new SimpleStringProperty("10.18.85.2");
  
  public static final Property<String> ROBOT_TELEMETRY_TABLE = new SimpleStringProperty("RobotTelemetry");
  
  public static final SimpleLongProperty DATA_POLLING_INTERVAL_MS = new SimpleLongProperty(67);
  
  public static final String NETWORK_TABLES_CONNECTION = "Network Tables Connected";
  
  public static final int MAX_ROBOT_LOG_SIZE = 100;
  
  public static final double DEFAULT_TILE_WIDTH = 200;
  public static final double DEFAULT_TILE_HEIGHT = 200;
  
  public static final Color ILITE_PURPLE = Color.valueOf("#5d31b6").darker();
  public static final Color ILITE_GREEN = Color.valueOf("#00FF00");
  

  public static final Background ILITE_GREEN_BACKGROUND = new Background(
    new BackgroundFill(ILITE_GREEN, CornerRadii.EMPTY, Insets.EMPTY));
  
  public static final Color PANEL_BACKGROUND_COLOR = ILITE_PURPLE;
  public static final Background PANEL_BACKGROUND = new Background(
    new BackgroundFill(PANEL_BACKGROUND_COLOR, CornerRadii.EMPTY, Insets.EMPTY));

  public static final Color TILE_BACKGROUND = Color.BLACK;

  public static final Color DEFAULT_TILE_TEXT_COLOR = ILITE_GREEN;
  
  public static final Color ERROR_COLOR = Color.RED;
  
  public static final String ICON_FONT_AWESOME_TILE_SIZE = "50";
  
  
  public static final Color GRAPH_POS_COLOR = Color.BLUE;
  public static final Color GRAPH_NEG_COLOR = Color.RED;
  
}
