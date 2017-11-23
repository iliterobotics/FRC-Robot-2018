package org.ilite.frclog.display;

import java.util.HashSet;
import java.util.Set;

import com.flybotix.hfr.util.lang.IUpdate;

import javafx.application.Platform;

/**
 * Like a JavaFX property, but lighter weight.  Instead of
 * updating all observers on the calling thread, this class
 * will update all listeners on the JavaFX UI thread.
 * @param <T>
 */
public class ObservableFX <T> {
  private T mValue = null;
  private final Set<IUpdate<T>> mListeners = new HashSet<>();
  
  public ObservableFX(T pValue) {
    mValue = pValue;
  }
  
  public ObservableFX() {
    
  }
  
  public void setValue(T pValue) {
    mValue = pValue;
    
    // Runs on the FX UI update thread
    Platform.runLater(() -> mListeners.forEach(listener -> listener.update(mValue)));
  }
  
  public T getValue() {
    return mValue;
  }
  
  public void addListener(IUpdate<T> pListener) {
    mListeners.add(pListener);
  }
  
  public void removeListener(IUpdate<T> pListener) {
    mListeners.remove(pListener);
  }
}
