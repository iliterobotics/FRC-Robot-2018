package wrappers;

public class TestSolenoid implements ISolenoid {
  
  boolean mState;
  boolean mExtendedWhenTrue;
  
  public TestSolenoid(boolean pExtendedWhenTrue) {
    mState = false;
    mExtendedWhenTrue = pExtendedWhenTrue;
  }
  
  @Override
  public void set(boolean pState) {
    mState = pState;
  }

  @Override
  public boolean get() {
    return mState;
  }
  
  public boolean isExtended() {
    return mState == mExtendedWhenTrue;
  }

}
