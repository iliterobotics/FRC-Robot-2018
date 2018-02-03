package org.ilite.frc.display.auton;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.types.ECross;
import org.ilite.frc.common.types.ECubeAction;
import org.ilite.frc.common.types.EStartingPosition;
import org.ilite.frc.common.input.EDriverControlMode;

import com.flybotix.hfr.util.lang.EnumUtils;
import com.google.gson.Gson;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback; 


public class AutonConfigDisplay extends Application {

  private Integer[] preferredCubeActions;
  private String awesomeCss = AutonConfigDisplay.class.getResource("./AwesomeStyle.css").toExternalForm();
	private String iliteCss = AutonConfigDisplay.class.getResource("./ILITEStyle.css").toExternalForm();
  public static void main(String[] pArgs) {
    launch(pArgs);
  }
  
  @Override
  public void start(Stage primaryStage) throws Exception {
    BorderPane root = new BorderPane();
    Scene scene = new Scene(root, 800, 600);
		
    scene.getStylesheets().add(iliteCss);
    
    preferredCubeActions = new Integer[ECubeAction.values().length];
    for(int i = 0; i < preferredCubeActions.length; i++) preferredCubeActions[i] = -1;
    
    scene.setOnMouseClicked(e -> {
      if(scene.getStylesheets().contains(awesomeCss)) {
        playSound("./airhorn.mp3");
      }
    });
    
    Button send = new Button("Send");
    send.setOnAction(e -> {
      SystemSettings.AUTON_TABLE.putNumberArray(ECubeAction.class.getSimpleName(), preferredCubeActions);
    });
    
    Button mode = new Button("Enhanced Mode");
    mode.setOnAction(e -> {
      if(scene.getStylesheets().contains(awesomeCss)) {
        mode.setText("Enhanced Mode");
        scene.getStylesheets().clear();
        scene.getStylesheets().add(iliteCss);
      } else {
        mode.setText("Judge's Mode");
        scene.getStylesheets().clear();
        scene.getStylesheets().add(awesomeCss);
        setFieldImage("./field.png");
      }
    });
    
    
    HBox selectionBoxes = new HBox(
    		labeledCheckboxDropdown(ECubeAction.class, preferredCubeActions),
    		labeledDropdown(EStartingPosition.class),
    		labeledDropdown(ECross.class),
    		labeledDropdown(EDriverControlMode.class)
    );
    
    HBox modeOptions = new HBox(mode, send);
   
    modeOptions.setMargin(send, new Insets(0, 40, 0, 20));
    
    selectionBoxes.setSpacing(10d);
    root.setCenter(selectionBoxes);
    root.setBottom(modeOptions);
    BorderPane.setAlignment(selectionBoxes, Pos.CENTER);
    BorderPane.setAlignment(modeOptions, Pos.BOTTOM_LEFT);
    
    primaryStage.setTitle("ILITE Autonomous Configuration");
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
		    SystemSettings.AUTON_TABLE.putNumber(pEnumeration.getSimpleName(), combo.getSelectionModel().getSelectedItem().ordinal())
	    );
	    combo.setValue(enums.get(0));
	    VBox result = new VBox(label, combo);
	    return result;
  }
  
  private <E extends Enum<E>> VBox labeledCheckboxDropdown(Class<E> pEnumeration, Object[] preferenceArray) {
    List<E> enums = EnumUtils.getEnums(pEnumeration, true);
    Label label = new Label(toPrettyCase(pEnumeration.getSimpleName().substring(1)));
    label.setTextAlignment(TextAlignment.CENTER);
    ListView<String> listView = new ListView<>();
    for (E e : enums) {
        listView.getItems().add(e.name());
    }
    listView.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
        @Override
        public ObservableValue<Boolean> call(String item) {
            BooleanProperty observable = new SimpleBooleanProperty();
            observable.addListener(e -> {
    					if(observable.get()) {
    			      preferenceArray[listView.getItems().indexOf(item)] = ECubeAction.valueOf(item).ordinal();
    					} else {
    						preferenceArray[listView.getItems().indexOf(item)] = -1;
    					}
    					System.out.println(Arrays.toString(preferenceArray));
            });
            return observable;
        }
    }));
    
    listView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener() {

		@Override
		public void onChanged(Change arg0) {
			System.out.println("On changed");
			SystemSettings.AUTON_TABLE.putNumberArray(pEnumeration.getSimpleName(), preferredCubeActions);
		}
    	
    });
    
    Button up = new Button("Up");
    Button down = new Button("Down");
    up.setOnAction(e -> swapEntriesUp(listView, preferenceArray) );
    down.setOnAction(e -> swapEntriesDown(listView, preferenceArray) );
    up.setMinWidth(60);
    down.setMinWidth(60);
    HBox buttons = new HBox(up, down);
    buttons.setMargin(up, new Insets(10, 40, 10, 40));
    buttons.setMargin(down, new Insets(10, 40, 10, 40));
    VBox result = new VBox(label, listView, buttons);
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
  
  private static void swapEntriesUp(ListView listView, Object[] outputArray) {
	  ObservableList list = listView.getItems();
	  Object selectedItem = listView.getSelectionModel().getSelectedItem();
	  int selectedIndex = list.indexOf(selectedItem);
	  Object temp = selectedItem;
	  
	  if(selectedIndex - 1 >= 0) {
		  list.set(selectedIndex, list.get(selectedIndex - 1));
		  list.set(selectedIndex - 1, temp);
		  outputArray[selectedIndex] = -1;
		  outputArray[selectedIndex - 1] = -1;
		  listView.getSelectionModel().select(selectedIndex - 1);
	  }
	  listView.setItems(list);
  }
  
  private static void swapEntriesDown(ListView listView, Object[] outputArray) {
	  ObservableList list = listView.getItems();
	  Object selectedItem = listView.getSelectionModel().getSelectedItem();
	  int selectedIndex = list.indexOf(selectedItem);
	  Object temp = selectedItem;
	  
	  if(selectedIndex + 1 < list.size()) {
		  list.set(selectedIndex, list.get(selectedIndex + 1));
		  list.set(selectedIndex + 1, temp);
		  outputArray[selectedIndex] = -1;
		  outputArray[selectedIndex + 1] = -1;
	    listView.getSelectionModel().select(selectedIndex + 1);
	  }
	  listView.setItems(list);
  }
  
  private static void setFieldImage(String path) {
    try {
      Image field = new Image(new File(path).toURI().toURL().toExternalForm());
      ImageView fieldView = new ImageView(field);
      fieldView.setX(400);
      fieldView.setY(200);
      fieldView.setFitHeight(400);
      fieldView.setFitWidth(600);
      fieldView.setPreserveRatio(true);
    } catch (Exception e) {
      System.err.println("File not found.");
    }
  }
  
  private static void playSound(String sound){
    // cl is the ClassLoader for the current class, ie. CurrentClass.class.getClassLoader();
    URL file = AutonConfigDisplay.class.getResource(sound);
    final Media media = new Media(file.toString());
    final MediaPlayer mediaPlayer = new MediaPlayer(media);
    mediaPlayer.play();
  }
  
}
