package com.example;

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class SalesPredictionChart extends JFrame {

    public SalesPredictionChart(String title, List<Double> actualValues, List<Double> predictedValues) {
        super(title);

        // Crea il dataset
        XYSeriesCollection dataset = createDataset(actualValues, predictedValues);

        // Crea il grafico
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Actual vs Predicted Sales",
                "Index",
                "Sales",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        // Personalizza il grafico
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);    // Colore per la serie actual
        renderer.setSeriesPaint(1, Color.BLUE);   // Colore per la serie predicted
        renderer.setSeriesShapesVisible(0, false);
        renderer.setSeriesShapesVisible(1, false);
        plot.setRenderer(renderer);

        // Aggiungi il grafico al JFrame
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        setContentPane(chartPanel);
    }

    private XYSeriesCollection createDataset(List<Double> actualValues, List<Double> predictedValues) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        XYSeries actualSeries = new XYSeries("Actual Sales");
        XYSeries predictedSeries = new XYSeries("Predicted Sales");

        for (int i = 0; i < actualValues.size(); i++) {
            actualSeries.add(i, actualValues.get(i));
            predictedSeries.add(i, predictedValues.get(i));
        }

        dataset.addSeries(actualSeries);
        dataset.addSeries(predictedSeries);

        return dataset;
    }

}
