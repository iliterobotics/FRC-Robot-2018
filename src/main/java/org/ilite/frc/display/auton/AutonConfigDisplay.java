package org.ilite.frc.display.auton;

import java.util.Arrays;
import java.util.List;

import org.ilite.frc.common.types.ECross;
import org.ilite.frc.common.types.ECubeAction;
import org.ilite.frc.common.types.EStartingPosition;
import org.ilite.frc.common.types.ESupportedTypes;
import org.ilite.frc.display.frclog.data.RobotDataStream;

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
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;

public class AutonConfigDisplay extends Application {

  private Gson gson;
  private String[] preferredCubeActions;
	
  public static void main(String[] pArgs) {
    launch(pArgs);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    BorderPane root = new BorderPane();
    Scene scene = new Scene(root, 1920, 800);
    
    gson = new Gson();
    preferredCubeActions = new String[ECubeAction.values().length];
    
    HBox h = new HBox(
    		labeledCheckboxDropdown(ECubeAction.class, preferredCubeActions),
    		labeledDropdown(EStartingPosition.class),
    		labeledDropdown(ECross.class)
    );
    
    h.setSpacing(10d);
    root.setCenter(h);
    BorderPane.setAlignment(h, Pos.CENTER);
  
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
	        RobotDataStream.inst().sendDataToRobot(
	            pEnumeration.getSimpleName(), 
	            ESupportedTypes.INTEGER, 
	            Integer.toString(combo.getSelectionModel().getSelectedItem().ordinal()))
	    );
	    combo.setValue(enums.get(0));
	    VBox result = new VBox(label, combo);
	    return result;
  }
  
  private <E extends Enum<E>> VBox labeledCheckboxDropdown(Class<E> pEnumeration, String[] preferenceArray) {
    List<E> enums = EnumUtils.getEnums(pEnumeration, true);
    Label label = new Label(toPrettyCase(pEnumeration.getSimpleName().substring(1)));
    label.setTextAlignment(TextAlignment.CENTER);
    ListView<String> listView = new ListView<>();
    for (E e : enums) {
        listView.getItems().add(e.toString());
    }
    listView.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
        @Override
        public ObservableValue<Boolean> call(String item) {
            BooleanProperty observable = new SimpleBooleanProperty();
            observable.addListener(e -> {
					if(observable.get()) {
			            preferenceArray[listView.getItems().indexOf(item)] = item;
					} else {
						preferenceArray[listView.getItems().indexOf(item)] = null;
					}
					System.out.println(Arrays.toString(preferenceArray));
            });
            return observable;
        }
    }));
    
    listView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener() {

		@Override
		public void onChanged(Change arg0) {
			RobotDataStream.inst().sendDataToRobot( 
		            pEnumeration.getSimpleName(),  
		            ESupportedTypes.STRING, 
		            gson.toJson(preferredCubeActions)
		            );
			System.out.println("List change detected.");
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
  
  private static void swapEntriesUp(ListView listView, String[] outputArray) {
	  ObservableList list = listView.getItems();
	  Object selectedItem = listView.getSelectionModel().getSelectedItem();
	  int selectedIndex = list.indexOf(selectedItem);
	  Object temp = selectedItem;
	  
	  if(selectedIndex - 1 >= 0) {
		  list.set(selectedIndex, list.get(selectedIndex - 1));
		  list.set(selectedIndex - 1, temp);
		  outputArray[selectedIndex] = null;
		  outputArray[selectedIndex - 1] = null;
	  }
	  listView.setItems(list);
  }
  
  private static void swapEntriesDown(ListView listView, String[] outputArray) {
	  ObservableList list = listView.getItems();
	  Object selectedItem = listView.getSelectionModel().getSelectedItem();
	  int selectedIndex = list.indexOf(selectedItem);
	  Object temp = selectedItem;
	  
	  if(selectedIndex + 1 < list.size()) {
		  list.set(selectedIndex, list.get(selectedIndex + 1));
		  list.set(selectedIndex + 1, temp);
		  outputArray[selectedIndex] = null;
		  outputArray[selectedIndex + 1] = null;
	  }
	  listView.setItems(list);
  }
  
}
