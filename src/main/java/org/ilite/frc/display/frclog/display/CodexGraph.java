package org.ilite.frc.display.frclog.display;

import org.ilite.frc.display.frclog.data.RobotDataElementCache;
import org.jfree.data.xy.XYSeries;

import com.flybotix.hfr.cache.CodexElementHistory;
import com.flybotix.hfr.codex.CodexOf;

public class CodexGraph extends IliteGraph{
  
  public CodexGraph(String pTitle, String pXLabel, String pYLabel) {
    super(pTitle, pXLabel, pYLabel);
  }

  public <E extends Enum<E> & CodexOf<Double>> void addTimeSeries(E pEnum) {
    CodexElementHistory<Double,E> history = RobotDataElementCache.inst().getHistoryOf(pEnum);
    XYSeries series = new XYSeries(pEnum);
    history.getData().stream().forEach(hist -> {
      series.add(hist.time, hist.value);
    });
    addSeries(pEnum.name(), series);
  }
}
