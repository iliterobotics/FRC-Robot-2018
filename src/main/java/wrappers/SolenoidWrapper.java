package wrappers;

import edu.wpi.first.wpilibj.Solenoid;

public class SolenoidWrapper implements ISolenoid{

  private Solenoid mSolenoid;
  
  public SolenoidWrapper(int pChannel) {
    mSolenoid = new Solenoid(pChannel);
  }
  
  public void set(boolean pState) {
    mSolenoid.set(pState);
  }
  
  public boolean get() {
    return mSolenoid.get();
  }
  
}
