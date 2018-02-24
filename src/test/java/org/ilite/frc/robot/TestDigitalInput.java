package org.ilite.frc.robot;

import wrappers.IDigitalInput;

public class TestDigitalInput implements IDigitalInput {

  boolean mState;
  boolean mIsTriggeredWhenTrue;
  ITestEvent mChangeEventCallback;
  
  public TestDigitalInput(boolean pIsTriggeredWhenTrue) {
    mIsTriggeredWhenTrue = pIsTriggeredWhenTrue;
    mState = !mIsTriggeredWhenTrue; // Set initial state to not pressed
  }
  
  public void set(boolean pState) {
    mChangeEventCallback.change(pState);
    mState = pState;
  }
  
  @Override
  public boolean get() {
    return mState;
  }
  
  public boolean isTriggered() {
    return mState == mIsTriggeredWhenTrue;
  }

  public void onChange(ITestEvent pEvent) {
    this.mChangeEventCallback = pEvent;
  }
  
  public String toString() {
    return isTriggered() ? "triggerd" : "not triggered";
  }
  
}
