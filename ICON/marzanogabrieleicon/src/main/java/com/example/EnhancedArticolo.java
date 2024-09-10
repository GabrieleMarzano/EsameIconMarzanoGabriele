package com.example;

import java.util.Date;

public class EnhancedArticolo extends Articolo {
    private String dayOfWeek;     // Giorno della settimana
    private String month;         // Mese dell'anno
    private String timeOfDay;     // Fascia oraria
    private String priceCategory; // Categoria di prezzo unitario

    public EnhancedArticolo(String invoiceId, String branch, String city, String customerType, String gender, String productLine, double unitPrice, int quantity, double tax, double total, Date date, String time, String payment, double cogs, double grossMarginPercentage, double grossIncome, float rating, String dayOfWeek, String month, String timeOfDay, String priceCategory) {
        super(invoiceId, branch, city, customerType, gender, productLine, unitPrice, quantity, tax, total, date, time, payment, cogs, grossMarginPercentage, grossIncome, rating);
        this.dayOfWeek = dayOfWeek;
        this.month = month;
        this.timeOfDay = timeOfDay;
        this.priceCategory = priceCategory;
    }

    // Getter e Setter per le nuove feature

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(String timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public String getPriceCategory() {
        return priceCategory;
    }

    public void setPriceCategory(String priceCategory) {
        this.priceCategory = priceCategory;
    }

    @Override
    public String toString() {
        return super.toString() + ", DayOfWeek=" + dayOfWeek + ", Month=" + month + ", TimeOfDay=" + timeOfDay + ", PriceCategory=" + priceCategory;
    }
}
