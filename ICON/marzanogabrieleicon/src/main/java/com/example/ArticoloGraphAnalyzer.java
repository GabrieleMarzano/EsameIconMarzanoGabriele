package com.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ArticoloGraphAnalyzer {
public static ArticoloGraph articoloGraph = new ArticoloGraph();

    // Metodo per confrontare gli articoli e costruire il grafo
    public static void buildGraph(List <Articolo> articoli) {
        

        // Aggiungi tutti i nodi al grafo
        for (Articolo articolo : articoli) {
            articoloGraph.addNode(articolo.getInvoiceId());
        }

        // Confronta ogni articolo con tutti gli altri e crea connessioni
        for (Articolo articolo : articoli) {
            Map<Articolo, Integer> similarityMap = new HashMap<>();
            int maxSimilarity = 0;

            // Confronta l'articolo corrente con tutti gli altri
            for (Articolo other : articoli) {
                if (!articolo.equals(other)) {
                    int similarity = calculateSimilarity(articolo, other);
                    similarityMap.put(other, similarity);
                    if (similarity > maxSimilarity) {
                        maxSimilarity = similarity;
                    }
                }
            }

            // Aggiungi archi verso gli articoli con la massima somiglianza
            for (Map.Entry<Articolo, Integer> entry : similarityMap.entrySet()) {
                if (entry.getValue() == maxSimilarity) {
                    articoloGraph.addEdge(articolo.getInvoiceId(), entry.getKey().getInvoiceId());
                }
            }
        }


        String leftMostNode = articoloGraph.getFirstLeftMostNode();
        System.out.println("Il primo nodo più a sinistra è: " + leftMostNode);
        // Stampa il grafo risultante
        articoloGraph.printGraph();
    }


    public static String getLeftMostConnectedNode(String nodeId) {
        Set<String> neighbors = ArticoloGraph.getNeighbors(nodeId);
        
        if (neighbors.isEmpty()) {
            return null;
        }
        
        // Assumiamo che il primo nodo nella collezione sia il più a sinistra
        return neighbors.iterator().next();
    }
    // Metodo per calcolare il numero di caratteristiche in comune tra due articoli
    private static int calculateSimilarity(Articolo a1, Articolo a2) {
        int similarity = 0;

        if (a1.getProductLine().equals(a2.getProductLine())) similarity++;
        if (a1.getCity().equals(a2.getCity())) similarity++;
        if (a1.getCustomerType().equals(a2.getCustomerType())) similarity++;
        if (a1.getGender().equals(a2.getGender())) similarity++;
        if (a1.getPayment().equals(a2.getPayment())) similarity++;
        if (isSamePriceRange(a1.getTotal(), a2.getTotal())) similarity++;
        if (isSameRating(a1.getRating(), a2.getRating())) similarity++;

        return similarity;
    }

    // Metodo per verificare se due articoli appartengono alla stessa fascia di prezzo
    private static boolean isSamePriceRange(double total1, double total2) {
        return Math.abs(total1 - total2) < 50;  // Considera articoli simili se la differenza è minore di 50 euro
    }

    // Metodo per verificare se la valutazione è simile
    private static boolean isSameRating(float rating1, float rating2) {
        return Math.abs(rating1 - rating2) < 1.0;  // Considera valutazioni simili se la differenza è inferiore a 1
    }


}
