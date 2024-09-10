package com.example;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FeatureEngineeringUtils {

    // Metodo per l'estrazione del giorno della settimana
    private static String getDayOfWeek(Date date) {
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE"); // il formato "EEEE" restituisce il nome completo del giorno
        return simpleDateformat.format(date);
    }

    // Metodo per l'estrazione del mese dell'anno
    private static String getMonth(Date date) {
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("MMMM"); // il formato "MMMM" restituisce il nome completo del mese
        return simpleDateformat.format(date);
    }

    // Metodo per l'estrazione del periodo del giorno
    private static String getTimeOfDay(String time) {
        int hour = Integer.parseInt(time.split(":")[0]);
        if (hour >= 6 && hour < 12) {
            return "Morning";
        } else if (hour >= 12 && hour < 18) {
            return "Afternoon";
        } else if (hour >= 18 && hour < 21) {
            return "Evening";
        } else {
            return "Night";
        }
    }

    // Metodo per la categorizzazione del prezzo unitario
    private static String categorizeUnitPrice(double unitPrice) {
        if (unitPrice < 50) {
            return "Low";
        } else if (unitPrice < 150) {
            return "Medium";
        } else {
            return "High";
        }
    }

    // Metodo per il calcolo delle nuove feature
    public static List<EnhancedArticolo> calculateFeatures(List<Articolo> articoli) {
        List<EnhancedArticolo> enhancedArticoli = new ArrayList<>();

        for (Articolo articolo : articoli) {
            String dayOfWeek = getDayOfWeek(articolo.getDate());
            String month = getMonth(articolo.getDate());
            String timeOfDay = getTimeOfDay(articolo.getTime());
            String priceCategory = categorizeUnitPrice(articolo.getUnitPrice());

            // Creazione di un oggetto EnhancedArticolo con nuove feature
            EnhancedArticolo enhancedArticolo = new EnhancedArticolo(
                articolo.getInvoiceId(),
                articolo.getBranch(),
                articolo.getCity(),
                articolo.getCustomerType(),
                articolo.getGender(),
                articolo.getProductLine(),
                articolo.getUnitPrice(),
                articolo.getQuantity(),
                articolo.getTax(),
                articolo.getTotal(),
                articolo.getDate(),
                articolo.getTime(),
                articolo.getPayment(),
                articolo.getCogs(),
                articolo.getGrossMarginPercentage(),
                articolo.getGrossIncome(),
                articolo.getRating(),
                dayOfWeek,
                month,
                timeOfDay,
                priceCategory
            );

            enhancedArticoli.add(enhancedArticolo);
        }

        return enhancedArticoli;
    }

}
