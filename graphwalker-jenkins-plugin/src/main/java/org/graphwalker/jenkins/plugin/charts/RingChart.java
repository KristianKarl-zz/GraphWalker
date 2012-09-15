package org.graphwalker.jenkins.plugin.charts;

import hudson.util.Graph;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.RingPlot;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.*;

public class RingChart extends Graph {

    private DefaultPieDataset myDataset = new DefaultPieDataset();

    public RingChart() {
        super(-1, 500, 200);
    }

    public void setValue(Comparable key, Number value) {
        myDataset.setValue(key, value);
    }

    public void setValue(Comparable key, double value) {
        myDataset.setValue(key, value);
    }

    @Override
    protected JFreeChart createGraph() {
        final JFreeChart chart = ChartFactory.createRingChart("", myDataset, true, false, false);

        RingPlot ringplot = (RingPlot) chart.getPlot();
        ringplot.setSectionDepth(0.44999999999999998D);
        chart.setBackgroundPaint(Color.WHITE);

        return chart;
    }
}
