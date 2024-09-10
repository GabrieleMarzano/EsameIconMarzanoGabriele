package com.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Dataset {
    // Lista di oggetti Articolo
    private  List<Articolo> listaArticoli;

    // Costruttore
    public Dataset() {
        this.listaArticoli = new ArrayList<>();
    }

    public Dataset(List<Articolo> art) {
        this.listaArticoli = new ArrayList<>(art);
    }

    // Metodo per caricare i dati dal file CSV
    public void caricaDaCSV(String nomeFile) {
        
        String linea;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy"); // Formato data nel CSV

      

        try (BufferedReader br = new BufferedReader(new FileReader(nomeFile))) {

           
            // Leggere la prima riga (intestazioni) e ignorarla
            br.readLine();
           

            // Leggere ogni riga del file CSV
            while ((linea = br.readLine()) != null) {
                String[] valori = linea.split(","); // Separare i valori per colonna
             

                // Parsing dei dati e creazione di un oggetto Articolo
                String invoiceId = valori[0];
                String branch = valori[1];
                String city = valori[2];
                String customerType = valori[3];
                String gender = valori[4];
                String productLine = valori[5];
                double unitPrice = Double.parseDouble(valori[6]);
                int quantity = Integer.parseInt(valori[7]);
                double tax = Double.parseDouble(valori[8]);
                double total = Double.parseDouble(valori[9]);
                Date date = dateFormat.parse(valori[10]);
                String time = valori[11];
                String payment = valori[12];
                double cogs = Double.parseDouble(valori[13]);
                double grossMarginPercentage = Double.parseDouble(valori[14]);
                double grossIncome = Double.parseDouble(valori[15]);
                float rating = Float.parseFloat(valori[16]) ;
                
            

                // Creare un nuovo oggetto Articolo con i valori letti
                Articolo articolo = new Articolo(invoiceId, branch, city, customerType, gender, productLine,
                        unitPrice, quantity, tax, total, date, time, payment,
                        cogs, grossMarginPercentage, grossIncome, rating);

                // Aggiungere l'articolo alla lista
                listaArticoli.add(articolo);
                
            }
        } 
        
        catch (IOException | ParseException e) {
            System.out.println(e + "lettura errata dal dataset");
        }
    }

    // Getter per ottenere la lista degli articoli
    public List<Articolo> getListaArticoli() {
        return listaArticoli;
    }

    // Metodo per stampare la lista degli articoli
    public void stampaArticoli() {
        for (Articolo articolo : listaArticoli) {
            System.out.println(articolo);
        }
    }

    public void setListaArticoli(List<Articolo> listaArticoli) {
        this.listaArticoli = listaArticoli;
    }


}
