package com.example;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Statistiche {

    public static Map<String, Double> calculateTotalSalesPerProductLine(List<Articolo> articoli) {
        // Usa Stream API per raggruppare gli articoli per `productLine` e sommare i totali
        return articoli.stream()
                .collect(Collectors.groupingBy(
                        Articolo::getProductLine, // Raggruppa per productLine
                        Collectors.summingDouble(Articolo::getTotal) // Somma i valori del campo total
                ));
    }

    public static void StampaTotaleperProductline(Map<String, Double> totalSalesPerProductLine) {

        totalSalesPerProductLine.forEach((productLine, totalSales) -> System.out.println("Product Line: " + productLine + ", Total Sales: " + totalSales));

    }

    public static Map<String, Double> calculateTotalSalesPerBranch(List<Articolo> articoli) {
        // Usa Stream API per raggruppare gli articoli per `branch` e sommare i totali
        return articoli.stream()
                .collect(Collectors.groupingBy(
                        Articolo::getBranch, // Raggruppa per branch
                        Collectors.summingDouble(Articolo::getTotal) // Somma i valori del campo total
                ));

    }

    public static void StampaTotaleperBranch(Map<String, Double> totalSalesPerBranch) {

        totalSalesPerBranch.forEach((branch, totalSales)
                -> System.out.println("Branch: " + branch + ", Total Sales: " + totalSales)
        );
    }

    // Metodo per convertire una data in una rappresentazione LocalDate
    private static LocalDate toLocalDate(Date date) {
        return LocalDate.ofInstant(date.toInstant(), java.time.ZoneId.systemDefault());
    }

    // Metodo per raggruppare le vendite per giorno
    public static Map<LocalDate, Double> calculateDailySales(List<Articolo> articoli) {
        return articoli.stream()
                .collect(Collectors.groupingBy(
                        articolo -> toLocalDate(articolo.getDate()), // Raggruppa per data
                        Collectors.summingDouble(Articolo::getTotal) // Somma i totali per ogni data
                ));
    }

    // Metodo per raggruppare le vendite per mese (utilizzando LocalDate)
    public static Map<String, Double> calculateMonthlySales(List<Articolo> articoli) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return articoli.stream()
                .collect(Collectors.groupingBy(
                        articolo -> toLocalDate(articolo.getDate()).format(formatter), // Raggruppa per mese
                        Collectors.summingDouble(Articolo::getTotal) // Somma i totali per ogni mese
                ));
    }

    // Metodo per raggruppare le vendite per settimana (utilizzando LocalDate)
    public static Map<Integer, Double> calculateWeeklySales(List<Articolo> articoli) {
        return articoli.stream()
                .collect(Collectors.groupingBy(
                        articolo -> toLocalDate(articolo.getDate()).get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR), // Raggruppa per settimana dell'anno
                        Collectors.summingDouble(Articolo::getTotal) // Somma i totali per ogni settimana
                ));
    }

    // Funzione per calcolare il profitto totale per ogni product line
    public static Map<String, Double> calculateProfitPerProductLine(List<Articolo> articoli) {
        // Usa Stream API per raggruppare gli articoli per `productLine` e sommare il grossIncome
        return articoli.stream()
                .collect(Collectors.groupingBy(
                        Articolo::getProductLine, // Raggruppa per productLine
                        Collectors.summingDouble(Articolo::getGrossIncome) // Somma i valori di grossIncome
                ));
    }

    // Funzione per raggruppare per fasce di unitPrice e calcolare la quantità media venduta
    public static Map<String, Double> calculateAverageQuantityPerPriceRange(List<Articolo> articoli) {
        // Definiamo le fasce di prezzo
        List<Double> priceRanges = Arrays.asList(0.0, 50.0, 100.0, 150.0, 200.0, 250.0, 300.0);

        // Raggruppiamo gli articoli in base alla fascia di prezzo e calcoliamo la quantità media
        return articoli.stream()
                .collect(Collectors.groupingBy(
                        articolo -> getPriceRange(articolo.getUnitPrice(), priceRanges), // Determina la fascia di prezzo
                        Collectors.averagingInt(Articolo::getQuantity) // Calcola la quantità media
                ));

    }

    // Funzione per determinare la fascia di prezzo di un articolo
    private static String getPriceRange(double price, List<Double> ranges) {
        for (int i = 0; i < ranges.size() - 1; i++) {
            if (price >= ranges.get(i) && price < ranges.get(i + 1)) {
                return ranges.get(i) + " - " + ranges.get(i + 1);
            }
        }
        return "Oltre " + ranges.get(ranges.size() - 1); // Per valori oltre l'ultima fascia
    }

    public static List<String> checkGrossMarginConsistency(List<Articolo> articoli) {
        List<String> inconsistencies = new ArrayList<>();

        for (Articolo articolo : articoli) {
            double calculatedMarginPercentage = (articolo.getGrossIncome() / articolo.getTotal()) * 100;

            // Verifica se la differenza tra il margine lordo calcolato e quello fornito è significativa
            if (Math.abs(calculatedMarginPercentage - articolo.getGrossMarginPercentage()) > 0.01) {
                inconsistencies.add("Incoerenza trovata per Invoice ID: " + articolo.getInvoiceId()
                        + ". Calcolato: " + calculatedMarginPercentage
                        + ", Fornito: " + articolo.getGrossMarginPercentage());
            }
        }

        return inconsistencies;
    }

    // Funzione per analizzare l'incidenza di tax e cogs su total e grossIncome
    public static void analyzeTaxAndCogsImpact(List<Articolo> articoli) {
        double totalTax = 0;
        double totalCogs = 0;
        double totalGrossIncome = 0;
        double totalTotal = 0;

        // Calcola i totali
        for (Articolo articolo : articoli) {
            totalTax += articolo.getTax();
            totalCogs += articolo.getCogs();
            totalGrossIncome += articolo.getGrossIncome();
            totalTotal += articolo.getTotal();
        }

        // Calcola l'incidenza percentuale di tax e cogs su total e grossIncome
        double taxImpactOnTotal = (totalTax / totalTotal) * 100;
        double cogsImpactOnTotal = (totalCogs / totalTotal) * 100;

        double taxImpactOnGrossIncome = (totalTax / totalGrossIncome) * 100;
        double cogsImpactOnGrossIncome = (totalCogs / totalGrossIncome) * 100;

        // Stampa i risultati
        System.out.println("Incidenza di Tax su Total: " + taxImpactOnTotal + "%");
        System.out.println("Incidenza di Cogs su Total: " + cogsImpactOnTotal + "%");
        System.out.println("Incidenza di Tax su Gross Income: " + taxImpactOnGrossIncome + "%");
        System.out.println("Incidenza di Cogs su Gross Income: " + cogsImpactOnGrossIncome + "%");
    }

    // Funzione per confrontare le vendite totali in base al metodo di pagamento
    public static void analyzeSalesByPaymentMethod(List<Articolo> articoli) {
        // Mappa per memorizzare il totale delle vendite per ciascun metodo di pagamento
        Map<String, Double> salesByPayment = new HashMap<>();

        // Raggruppa gli articoli per metodo di pagamento e calcola il totale delle vendite
        for (Articolo articolo : articoli) {
            String paymentMethod = articolo.getPayment();
            double totalSale = articolo.getTotal();

            // Aggiungi il totale delle vendite alla mappa
            salesByPayment.put(paymentMethod, salesByPayment.getOrDefault(paymentMethod, 0.0) + totalSale);
        }

        // Stampa i risultati
        System.out.println("Totale delle vendite per metodo di pagamento:");
        for (Map.Entry<String, Double> entry : salesByPayment.entrySet()) {
            System.out.println("Metodo di pagamento: " + entry.getKey() + " - Vendite totali: " + entry.getValue());
        }

        // Determina il metodo di pagamento più comune e più redditizio
        String mostCommonPaymentMethod = Collections.max(salesByPayment.entrySet(), Map.Entry.comparingByValue()).getKey();
        double highestTotalSales = Collections.max(salesByPayment.values());

        System.out.println("Il metodo di pagamento più redditizio è: " + mostCommonPaymentMethod + " con vendite totali di: " + highestTotalSales);

        //analizza utenti
        Map<String, Double> salesByCustomerType = articoli.stream()
                .collect(Collectors.groupingBy(
                        Articolo::getCustomerType,
                        Collectors.summingDouble(Articolo::getTotal)
                ));

        // Stampa i risultati per tipo di utenti
        System.out.println("Totale vendite per tipo di cliente:");
        for (Map.Entry<String, Double> entry : salesByCustomerType.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        // Raggruppa per genere e calcola il totale delle vendite
        Map<String, Double> salesByGender = articoli.stream()
                .collect(Collectors.groupingBy(
                        Articolo::getGender,
                        Collectors.summingDouble(Articolo::getTotal)
                ));

        // Stampa i risultati
        System.out.println("Totale vendite per genere:");
        for (Map.Entry<String, Double> entry : salesByGender.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }




     
            Map<String, DoubleSummaryStatistics> ratingsByCustomerType = articoli.stream()
                .collect(Collectors.groupingBy(
                    Articolo::getCustomerType,
                    Collectors.summarizingDouble(Articolo::getRating)
                ));
    
            for (Map.Entry<String, DoubleSummaryStatistics> entry : ratingsByCustomerType.entrySet()) {
                String customerType = entry.getKey();
                DoubleSummaryStatistics stats = entry.getValue();
                System.out.printf("Tipo di cliente: %s%n", customerType);
                System.out.printf("  Media: %.2f%n", stats.getAverage());
                System.out.printf("  Minimo: %.2f%n", stats.getMin());
                System.out.printf("  Massimo: %.2f%n", stats.getMax());
                System.out.printf("  Somma: %.2f%n", stats.getSum());
                System.out.printf("  Numero di valutazioni: %d%n", stats.getCount());

            }
            

            Map<String, DoubleSummaryStatistics> ratingsByProductLine = articoli.stream()
            .collect(Collectors.groupingBy(
                Articolo::getProductLine,
                Collectors.summarizingDouble(Articolo::getRating)
            ));

        for (Map.Entry<String, DoubleSummaryStatistics> entry : ratingsByProductLine.entrySet()) {
            String productLine = entry.getKey();
            DoubleSummaryStatistics stats = entry.getValue();
            System.out.printf("Categoria di prodotto: %s%n", productLine);
            System.out.printf("  Media: %.2f%n", stats.getAverage());
            System.out.printf("  Minimo: %.2f%n", stats.getMin());
            System.out.printf("  Massimo: %.2f%n", stats.getMax());
            System.out.printf("  Somma: %.2f%n", stats.getSum());
            System.out.printf("  Numero di valutazioni: %d%n", stats.getCount());
        }
        

    }

}
