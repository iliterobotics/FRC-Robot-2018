package org.ilite.frc.robot.controlloop;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ControlLoopManagerTest {

	private ITimerProvider timeProvider;
	private INotifier notifer;
	private ControlLoopManager clm;

	@Before
	public void setUp() throws Exception {
		timeProvider = Mockito.mock(ITimerProvider.class);
		notifer = Mockito.mock(INotifier.class);
		clm = new ControlLoopManager(notifer,timeProvider);
		clm.mControlLoops =(List<IControlLoop>) Mockito.mock(List.class);
	}

	@Test
	public void testSetRunningControlLoops_Null() {
		clm.setRunningControlLoops(( IControlLoop[])null);
		Mockito.verify(clm.mControlLoops, Mockito.never()).clear();
	}
	
	@Test
	public void testSetRunningControlLoops_empty() {
		clm.setRunningControlLoops(new IControlLoop[0]);
		Mockito.verify(clm.mControlLoops, Mockito.never()).clear();
		Mockito.verify(clm.mControlLoops, Mockito.never()).add(Mockito.any(IControlLoop.class));
	}
	@Test
	public void testSetRunningControlLoops_single() {
		IControlLoop mockedControlLoop = Mockito.mock(IControlLoop.class);
	
		clm.setRunningControlLoops(new IControlLoop[] {mockedControlLoop});
		
		Mockito.verify(clm.mControlLoops, Mockito.times(1)).clear();
		Mockito.verify(clm.mControlLoops, Mockito.times(1)).add(mockedControlLoop);
	}

	@Test
	public void testStart_NotRunning() {
		clm.mControlLoops = new ArrayList<>();
		IControlLoop mock = Mockito.mock(IControlLoop.class);
		clm.mControlLoops.add(mock);
		Mockito.when(timeProvider.getFPGATimestamp()).thenReturn(0.5d);
		
		Assert.assertFalse(clm.mIsRunning);

		clm.start();
		
		Mockito.verify(mock).initialize(0.5d);
		Assert.assertTrue(clm.mIsRunning);
		
	}
	@Test
	public void testStart_Running() {
		clm.mIsRunning = true;
		clm.mControlLoops = new ArrayList<>();
		IControlLoop mock = Mockito.mock(IControlLoop.class);
		clm.mControlLoops.add(mock);
		Mockito.when(timeProvider.getFPGATimestamp()).thenReturn(0.5d);
		
		Assert.assertTrue(clm.mIsRunning);

		clm.start();
		
		Mockito.verify(mock, Mockito.never()).initialize(0.5d);
		Assert.assertTrue(clm.mIsRunning);
		
	}

	@Test
	public void testStop_isRunning() {
		clm.mIsRunning = true;
		Mockito.when(timeProvider.getFPGATimestamp()).thenReturn(0.5d);
		
		IControlLoop mockedConrolLoop = Mockito.mock(IControlLoop.class);
		clm.mControlLoops = new ArrayList<>();
		clm.mControlLoops.add(mockedConrolLoop);

		
		clm.stop();
		
		Mockito.verify(mockedConrolLoop, Mockito.times(1)).shutdown(0.5d);
		Mockito.verify(notifer, Mockito.times(1)).stop();
		
	}
	
	@Test
	public void testStop_isNotRunning() {
		clm.mIsRunning = false;
		Mockito.when(timeProvider.getFPGATimestamp()).thenReturn(0.5d);
		
		IControlLoop mockedConrolLoop = Mockito.mock(IControlLoop.class);
		clm.mControlLoops = new ArrayList<>();
		clm.mControlLoops.add(mockedConrolLoop);

		
		clm.stop();
		
		Mockito.verify(mockedConrolLoop, Mockito.never()).shutdown(0.5d);
		Mockito.verify(notifer, Mockito.never()).stop();
		
	}

	@Test
	public void testRun_isRunning() {
		clm.mIsRunning = true;
		Mockito.when(timeProvider.getFPGATimestamp()).thenReturn(0.5d);
		IControlLoop mockedControlLoop = Mockito.mock(IControlLoop.class);
		clm.mControlLoops = new ArrayList<>();
		clm.mControlLoops.add(mockedControlLoop);
		
		clm.run();
		
		Mockito.verify(mockedControlLoop, Mockito.times(1)).loop(0.5d);
		
	}
	
	@Test
	public void testRun_isNotRunning() {
		clm.mIsRunning = false;
		Mockito.when(timeProvider.getFPGATimestamp()).thenReturn(0.5d);
		IControlLoop mockedControlLoop = Mockito.mock(IControlLoop.class);
		clm.mControlLoops = new ArrayList<>();
		clm.mControlLoops.add(mockedControlLoop);
		
		clm.run();
		
		Mockito.verify(mockedControlLoop, Mockito.never()).loop(0.5d);
		
	}

}
