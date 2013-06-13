package org.graphwalker.jenkins.plugin;

import hudson.util.ColorPalette;
import hudson.util.Graph;
import hudson.util.ShiftedCategoryAxis;
import hudson.util.StackedAreaRenderer2;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedAreaRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.RectangleInsets;

import java.awt.*;

public class TrendChart extends Graph {

    private static final int DEFAULT_HEIGHT = 220;
    private static final int DEFAULT_WIDTH = 300;
    private final CategoryDataset dataset;

    protected TrendChart(CategoryDataset dataset) {
        super(-1, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.dataset = dataset;
    }

    @Override
    protected JFreeChart createGraph() {
        final JFreeChart chart = ChartFactory.createStackedAreaChart(
                null,
                Messages.project_trend_date(),
                Messages.project_trend_count(),
                dataset,
                PlotOrientation.VERTICAL,
                false,
                false,
                false
        );
        chart.setBackgroundPaint(Color.white);
        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setForegroundAlpha(0.8f);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.black);
        CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
        plot.setDomainAxis(domainAxis);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);
        domainAxis.setCategoryMargin(0.0);
        domainAxis.setTickLabelsVisible(false);
        final NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        StackedAreaRenderer stackedAreaRenderer = new StackedAreaRenderer2();
        plot.setRenderer(stackedAreaRenderer);
        stackedAreaRenderer.setSeriesPaint(0, ColorPalette.RED);
        stackedAreaRenderer.setSeriesPaint(1, ColorPalette.YELLOW);
        stackedAreaRenderer.setSeriesPaint(2, ColorPalette.BLUE);
        plot.setInsets(new RectangleInsets(0, 0, 0, 5.0));
        return chart;
    }
}
