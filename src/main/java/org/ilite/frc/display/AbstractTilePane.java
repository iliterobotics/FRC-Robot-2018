package org.ilite.frc.display;

import org.ilite.frc.display.frclog.data.RobotDataStream;
import org.ilite.frc.display.frclog.display.DisplayConfig;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.Tile.SkinType;
import eu.hansolo.tilesfx.Tile.TextSize;
import eu.hansolo.tilesfx.TileBuilder;
import javafx.geometry.Insets;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

public abstract class AbstractTilePane extends FlowPane{

  protected final RobotDataStream rds = RobotDataStream.inst();
  
  public static void decorate(Region pRegion) {
    pRegion.setPadding(new Insets(10));
    pRegion.setBackground(DisplayConfig.PANEL_BACKGROUND);
  }
  
  public AbstractTilePane() {
    decorate(this);
    setHgap(10);
    setVgap(10);
  }
  
  protected final Tile createBlankTile() {
    return tile("",SkinType.CUSTOM).backgroundColor(DisplayConfig.PANEL_BACKGROUND_COLOR).build();
  }

  public static final TileBuilder<?> tile(String title, SkinType pSkinType) {
    return TileBuilder.create()
      .skinType(pSkinType)
      .backgroundColor(DisplayConfig.TILE_BACKGROUND)
      .title(title)
      .textSize(TextSize.BIGGER)
      .valueColor(DisplayConfig.DEFAULT_TILE_TEXT_COLOR)
      .unitColor(DisplayConfig.DEFAULT_TILE_TEXT_COLOR)
      .roundedCorners(true)
      .prefSize(DisplayConfig.DEFAULT_TILE_WIDTH, DisplayConfig.DEFAULT_TILE_WIDTH);
  }
}
