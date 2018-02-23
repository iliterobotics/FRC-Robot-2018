package wrappers;

public class TestDigitalInput implements IDigitalInput {

  boolean mState;
  boolean mIsTriggeredWhenTrue;
  
  public TestDigitalInput(boolean pIsTriggeredWhenTrue) {
    mIsTriggeredWhenTrue = pIsTriggeredWhenTrue;
    mState = !mIsTriggeredWhenTrue; // Set initial state to not pressed
  }
  
  public void set(boolean pState) {
    mState = pState;
  }
  
  @Override
  public boolean get() {
    return mState;
  }
  
  public boolean isTriggered() {
    return mState == mIsTriggeredWhenTrue;
  }
  
}
