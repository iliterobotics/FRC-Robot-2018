package org.ilite.frclog.display;
import java.util.HashMap;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javafx.application.Platform;
import javafx.scene.layout.StackPane;

public class IliteGraph extends StackPane {

  private final XYSeriesCollection mDataset = new XYSeriesCollection();
  private final Map<String, XYSeries> mSeries = new HashMap<>();
  private final JFreeChart mChart;
  private final ChartViewer mChartViewer;
  
  public IliteGraph(String pTitle, String pXLabel, String pYLabel) {
    mChart = ChartFactory.createXYLineChart(pTitle, pXLabel, pYLabel, mDataset);
    mChartViewer = new ChartViewer(mChart, false);
    getChildren().add(mChartViewer);
  }
  
  public void addSeries(String pKey, XYSeries pSeries) {
    mSeries.put(pKey, pSeries);

    Platform.runLater(() -> {
      mDataset.addSeries(pSeries);
    });
  }

  public void removeSeries(String pKey) {
    XYSeries series = mSeries.remove(pKey);
    if(series != null) {
      mDataset.removeSeries(series);
    }
  }
}
