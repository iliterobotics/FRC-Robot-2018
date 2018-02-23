package wrappers;

import edu.wpi.first.wpilibj.DigitalInput;

public class DigitalInputWrapper implements IDigitalInput {

  private DigitalInput mDigitalInput;
  
  public DigitalInputWrapper(int pChannel) {
    mDigitalInput = new DigitalInput(pChannel);
  }

  @Override
  public boolean get() {
    return mDigitalInput.get();
  }
  
}
