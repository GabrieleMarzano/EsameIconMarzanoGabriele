package com.example;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Sessione {

    public static void CalcolaStatistiche(Dataset dataset) {

        Map<String, Double> totalSalesPerProductLine = Statistiche.calculateTotalSalesPerProductLine(dataset.getListaArticoli());
        // Stampa totale sales per categorie di prodotto
        Statistiche.StampaTotaleperProductline(totalSalesPerProductLine);

        Map<String, Double> totalSalesPerBranch = Statistiche.calculateTotalSalesPerBranch(dataset.getListaArticoli());
        // Stampa totale sales per Branch
        Statistiche.StampaTotaleperProductline(totalSalesPerBranch);

        Map<LocalDate, Double> dailySales = Statistiche.calculateDailySales(dataset.getListaArticoli());
        // Stampa totale sales per giorno
        System.out.println("Vendite giornaliere:");
        dailySales.forEach((date, total) -> System.out.println("Data: " + date + ", Totale vendite: " + total));

        Map<String, Double> monthlySales = Statistiche.calculateMonthlySales(dataset.getListaArticoli());
        // Stampa totale sales per mese
        System.out.println("Vendite mensili:");
        monthlySales.forEach((month, total) -> System.out.println("Mese: " + month + ", Totale vendite: " + total));

        Map<Integer, Double> weeklySales = Statistiche.calculateWeeklySales(dataset.getListaArticoli());
        // Stampa totale sales per settimana
        System.out.println("Vendite settimanali:");
        weeklySales.forEach((week, total) -> System.out.println("Settimana: " + week + ", Totale vendite: " + total));

        Map<String, Double> profitPerProductLine = Statistiche.calculateProfitPerProductLine(dataset.getListaArticoli());

        // Stampa i risultati profitto per categoria
        profitPerProductLine.forEach((productLine, totalProfit)
                -> System.out.println("Product Line: " + productLine + ", Total Profit: " + totalProfit)
        );

        Map<String, Double> avgQuantityPerPriceRange = Statistiche.calculateAverageQuantityPerPriceRange(dataset.getListaArticoli());

        // Stampa i risultati
        avgQuantityPerPriceRange.forEach((priceRange, avgQuantity)
                -> System.out.println("Price Range: " + priceRange + ", Average Quantity: " + avgQuantity)
        );

        //calcolo margine lordo
        List<String> inconsistencies = Statistiche.checkGrossMarginConsistency(dataset.getListaArticoli());
        // Stampa le incoerenze trovate
        if (inconsistencies.isEmpty()) {
            System.out.println("Tutti i margini lordi sono coerenti con i dati forniti.");
        } else {
            inconsistencies.forEach(System.out::println);
        }

        //analisi dei costi e delle tasse 
        Statistiche.analyzeTaxAndCogsImpact(dataset.getListaArticoli());

        //analisi miglior metodo di pagamento
        Statistiche.analyzeSalesByPaymentMethod(dataset.getListaArticoli());

    }

    //stampa grafico vendite
    public static void GraficoVendite(Dataset dataset) {

        SalesLineChart chart = new SalesLineChart("Analisi delle Vendite", dataset.getListaArticoli());
        chart.pack();
        // Centra la finestra sullo schermo
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - chart.getWidth()) / 2;
        int y = (screenSize.height - chart.getHeight()) / 2;
        chart.setLocation(x, y);

        chart.setVisible(true);

    }

    public static void IstogrammaVendite(Dataset dataset) {

        // Creare e visualizzare un istogramma delle vendite per categoria di prodotto
        SalesHistogram chartByProductLine = new SalesHistogram("Istogramma Vendite per Categoria di Prodotto", dataset.getListaArticoli(), "productLine");
        chartByProductLine.pack();
        SalesHistogram.centerWindow(chartByProductLine);
        chartByProductLine.setVisible(true);

        // Creare e visualizzare un istogramma delle vendite per ramo del supercentro
        SalesHistogram chartByBranch = new SalesHistogram("Istogramma Vendite per Ramo del Supercentro", dataset.getListaArticoli(), "branch");
        chartByBranch.pack();
        SalesHistogram.centerWindow(chartByBranch);
        chartByBranch.setVisible(true);

    }

}
