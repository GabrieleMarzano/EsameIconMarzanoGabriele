package com.example;

import java.util.Date;
import java.util.List;

public class Articolo {

    // Attributi della classe Articolo
    private String invoiceId;              // Identificativo della fattura
    private String branch;                 // Ramo del supercentro (A, B, C)
    private String city;                   // Ubicazione del supercentro
    private String customerType;           // Tipo di cliente (Membro o Normale)
    private String gender;                 // Genere del cliente (Maschio o Femmina)
    private String productLine;            // Categoria di prodotto (Es. Accessori elettronici)
    private double unitPrice;              // Prezzo unitario del prodotto
    private int quantity;                  // Quantità di prodotti acquistati
    private double tax;                    // Tassa (5% del totale)
    private double total;                  // Prezzo totale (incluse le tasse)
    private Date date;                     // Data di acquisto
    private String time;                   // Ora di acquisto
    private String payment;                // Metodo di pagamento (Contanti, Carta di credito, Ewallet)
    private double cogs;                   // Costo dei beni venduti
    private double grossMarginPercentage;  // Percentuale di margine lordo
    private double grossIncome;            // Reddito lordo
    private float rating;                    // Valutazione dell'esperienza d'acquisto (da 1 a 10)
    private String salesCategory;

    // Costruttore
    public Articolo(String invoiceId, String branch, String city, String customerType, String gender, String productLine,
            double unitPrice, int quantity, double tax, double total, Date date, String time, String payment,
            double cogs, double grossMarginPercentage, double grossIncome, float rating) {
        this.invoiceId = invoiceId;
        this.branch = branch;
        this.city = city;
        this.customerType = customerType;
        this.gender = gender;
        this.productLine = productLine;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.tax = tax;
        this.total = total;
        this.date = date;
        this.time = time;
        this.payment = payment;
        this.cogs = cogs;
        this.grossMarginPercentage = grossMarginPercentage;
        this.grossIncome = grossIncome;
        this.rating = rating;
    }

    // Getter e Setter per ogni attributo
    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProductLine() {
        return productLine;
    }

    public void setProductLine(String productLine) {
        this.productLine = productLine;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public double getCogs() {
        return cogs;
    }

    public void setCogs(double cogs) {
        this.cogs = cogs;
    }

    public double getGrossMarginPercentage() {
        return grossMarginPercentage;
    }

    public void setGrossMarginPercentage(double grossMarginPercentage) {
        this.grossMarginPercentage = grossMarginPercentage;
    }

    public double getGrossIncome() {
        return grossIncome;
    }

    public void setGrossIncome(double grossIncome) {
        this.grossIncome = grossIncome;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getSalesCategory() {
        return salesCategory;
    }

    public void setSalesCategory(String salesCategory) {
        this.salesCategory = salesCategory;
    }

    // Aggiungiamo la logica per determinare la categoria di vendita
    public static void prepareData(List<Articolo> articoli, double threshold) {
        for (Articolo articolo : articoli) {
            if (articolo.getTotal() > threshold) {
                articolo.setSalesCategory("Alta");
            } else {
                articolo.setSalesCategory("Bassa");
            }
        }
    }

    // Metodo per visualizzare i dettagli dell'articolo
    @Override
    public String toString() {
        return "Articolo{"
                + "invoiceId='" + invoiceId + '\''
                + ", branch='" + branch + '\''
                + ", city='" + city + '\''
                + ", customerType='" + customerType + '\''
                + ", gender='" + gender + '\''
                + ", productLine='" + productLine + '\''
                + ", unitPrice=" + unitPrice
                + ", quantity=" + quantity
                + ", tax=" + tax
                + ", total=" + total
                + ", date=" + date
                + ", time='" + time + '\''
                + ", payment='" + payment + '\''
                + ", cogs=" + cogs
                + ", grossMarginPercentage=" + grossMarginPercentage
                + ", grossIncome=" + grossIncome
                + ", rating=" + rating
                + '}';
    }

}
