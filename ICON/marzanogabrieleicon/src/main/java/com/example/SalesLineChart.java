package com.example;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.category.DefaultCategoryDataset;




public class SalesLineChart extends ApplicationFrame {

    public enum ChartType {
        LINE, BAR
    }

    public SalesLineChart(String title, List<Articolo> articoli) {
        super(title);
        JFreeChart lineChart = createChart(articoli);
        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        setContentPane(chartPanel);
    }

    private JFreeChart createChart(List<Articolo> articoli) {
        // Prepara il dataset raggruppando le vendite per data
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Raggruppa le vendite per data e calcola il totale per ogni giorno
        Map<String, Double> salesByDate = articoli.stream()
                .collect(Collectors.groupingBy(
                        articolo -> dateFormat.format(articolo.getDate()),
                        Collectors.summingDouble(Articolo::getTotal)
                ));

        // Aggiungi i dati al dataset
        for (Map.Entry<String, Double> entry : salesByDate.entrySet()) {
            dataset.addValue(entry.getValue(), "Vendite Totali", entry.getKey());
        }

        // Crea il grafico a linee
        return ChartFactory.createLineChart(
                "Andamento delle Vendite nel Tempo",
                "Data",
                "Vendite Totali",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);
    }
}
