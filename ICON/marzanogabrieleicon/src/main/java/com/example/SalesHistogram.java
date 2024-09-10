package com.example;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.category.DefaultCategoryDataset;

public class SalesHistogram extends ApplicationFrame {

    public SalesHistogram(String title, List<Articolo> articoli, String groupBy) {
        super(title);
        JFreeChart barChart = createChart(articoli, groupBy);
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        setContentPane(chartPanel);
    }

    private JFreeChart createChart(List<Articolo> articoli, String groupBy) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Raggruppa e somma le vendite in base al criterio specificato (productLine o branch)
        Map<String, Double> salesData;
        switch (groupBy) {
            case "productLine" -> salesData = articoli.stream()
                        .collect(Collectors.groupingBy(
                                Articolo::getProductLine,
                                Collectors.summingDouble(Articolo::getTotal)
                        ));
            case "branch" -> salesData = articoli.stream()
                        .collect(Collectors.groupingBy(
                                Articolo::getBranch,
                                Collectors.summingDouble(Articolo::getTotal)
                        ));
            default -> throw new IllegalArgumentException("Parametro di raggruppamento non valido: " + groupBy);
        }

        // Aggiungi i dati al dataset
        for (Map.Entry<String, Double> entry : salesData.entrySet()) {
            dataset.addValue(entry.getValue(), "Vendite Totali", entry.getKey());
        }

        // Crea l'istogramma
        return ChartFactory.createBarChart(
                "Vendite Totali per " + groupBy,
                groupBy,
                "Vendite Totali",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);
    }

    public  static void centerWindow(ApplicationFrame frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - frame.getWidth()) / 2;
        int y = (screenSize.height - frame.getHeight()) / 2;
        frame.setLocation(x, y);
    }

}