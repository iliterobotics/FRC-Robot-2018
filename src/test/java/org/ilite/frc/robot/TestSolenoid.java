package org.ilite.frc.robot;

import wrappers.ISolenoid;

public class TestSolenoid implements ISolenoid {
  
  boolean mState;
  boolean mExtendedWhenTrue;
  ITestEvent mChangeEventCallback;
  
  public TestSolenoid(boolean pExtendedWhenTrue) {
    mState = false;
    mExtendedWhenTrue = pExtendedWhenTrue;
  }
  
  @Override
  public void set(boolean pState) {
    mChangeEventCallback.change(pState);
    mState = pState;
  }

  @Override
  public boolean get() {
    return mState;
  }
  
  public boolean isExtended() {
    return mState == mExtendedWhenTrue;
  }
  
  public void onChange(ITestEvent pEvent) {
    this.mChangeEventCallback = pEvent;
  }
  
  public String toString() {
    return isExtended() ? "extended" : "retracted";
  }

}
