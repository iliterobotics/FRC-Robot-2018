package org.ilite.frc.robot.controlloop;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ControlLoopManagerTest {

	private ITimerProvider timeProvider;
	private INotifier notifer;
	private ControlLoopManager clm;

	@Before
	public void setUp() throws Exception {
		timeProvider = mock(ITimerProvider.class);
		notifer = mock(INotifier.class);
		clm = new ControlLoopManager(notifer,timeProvider);
		clm.mControlLoops =(List<IControlLoop>) mock(List.class);
	}

	@Test
	public void testSetRunningControlLoops_Null() {
		clm.setRunningControlLoops(( IControlLoop[])null);
		verify(clm.mControlLoops, never()).clear();
	}
	
	@Test
	public void testSetRunningControlLoops_empty() {
		clm.setRunningControlLoops(new IControlLoop[0]);
		verify(clm.mControlLoops, never()).clear();
		verify(clm.mControlLoops, never()).add(any(IControlLoop.class));
	}
	@Test
	public void testSetRunningControlLoops_single() {
		IControlLoop mockedControlLoop = mock(IControlLoop.class);
	
		clm.setRunningControlLoops(new IControlLoop[] {mockedControlLoop});
		
		verify(clm.mControlLoops, times(1)).clear();
		verify(clm.mControlLoops, times(1)).add(mockedControlLoop);
	}

	@Test
	public void testStart_NotRunning() {
		clm.mControlLoops = new ArrayList<>();
		IControlLoop mock = mock(IControlLoop.class);
		clm.mControlLoops.add(mock);
		when(timeProvider.getFPGATimestamp()).thenReturn(0.5d);
		
		Assert.assertFalse(clm.mIsRunning);

		clm.start();
		
		verify(mock).initialize(0.5d);
		Assert.assertTrue(clm.mIsRunning);
		
	}
	@Test
	public void testStart_Running() {
		clm.mIsRunning = true;
		clm.mControlLoops = new ArrayList<>();
		IControlLoop mock = mock(IControlLoop.class);
		clm.mControlLoops.add(mock);
		when(timeProvider.getFPGATimestamp()).thenReturn(0.5d);
		
		Assert.assertTrue(clm.mIsRunning);

		clm.start();
		
		verify(mock, never()).initialize(0.5d);
		Assert.assertTrue(clm.mIsRunning);
		
	}

	@Test
	public void testStop_isRunning() {
		clm.mIsRunning = true;
		when(timeProvider.getFPGATimestamp()).thenReturn(0.5d);
		
		IControlLoop mockedConrolLoop = mock(IControlLoop.class);
		clm.mControlLoops = new ArrayList<>();
		clm.mControlLoops.add(mockedConrolLoop);

		
		clm.stop();
		
		verify(mockedConrolLoop, times(1)).shutdown(0.5d);
		verify(notifer, times(1)).stop();
		
	}
	
	@Test
	public void testStop_isNotRunning() {
		clm.mIsRunning = false;
		when(timeProvider.getFPGATimestamp()).thenReturn(0.5d);
		
		IControlLoop mockedConrolLoop = mock(IControlLoop.class);
		clm.mControlLoops = new ArrayList<>();
		clm.mControlLoops.add(mockedConrolLoop);

		
		clm.stop();
		
		verify(mockedConrolLoop, never()).shutdown(0.5d);
		verify(notifer, never()).stop();
		
	}

	@Test
	public void testRun_isRunning() {
		clm.mIsRunning = true;
		when(timeProvider.getFPGATimestamp()).thenReturn(0.5d);
		IControlLoop mockedControlLoop = mock(IControlLoop.class);
		clm.mControlLoops = new ArrayList<>();
		clm.mControlLoops.add(mockedControlLoop);
		
		clm.run();
		
		verify(mockedControlLoop, times(1)).loop(0.5d);
		
	}
	
	@Test
	public void testRun_isNotRunning() {
		clm.mIsRunning = false;
		when(timeProvider.getFPGATimestamp()).thenReturn(0.5d);
		IControlLoop mockedControlLoop = mock(IControlLoop.class);
		clm.mControlLoops = new ArrayList<>();
		clm.mControlLoops.add(mockedControlLoop);
		
		clm.run();
		
		verify(mockedControlLoop, never()).loop(0.5d);
		
	}

}
