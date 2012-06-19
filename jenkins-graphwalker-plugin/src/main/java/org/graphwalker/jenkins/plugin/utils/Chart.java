package org.graphwalker.jenkins.plugin.utils;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.RingPlot;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.*;

public class Chart {

    private Chart() {
    }

    public static JFreeChart create() {

        DefaultPieDataset defaultPieDataset = new DefaultPieDataset();
        defaultPieDataset.setValue("x", 100 / 3);
        defaultPieDataset.setValue("y", 100 / 3);
        defaultPieDataset.setValue("z", 100 / 3);

        final JFreeChart chart = ChartFactory.createRingChart("", defaultPieDataset, true, false, false);

        RingPlot ringplot = (RingPlot) chart.getPlot();
        ringplot.setSectionDepth(0.44999999999999998D);
        chart.setBackgroundPaint(Color.WHITE);

        return chart;
    }
}
