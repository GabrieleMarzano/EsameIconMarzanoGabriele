package com.example;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// Classe che rappresenta il Grafo degli Articoli
class ArticoloGraph {
    private static Map<String, Set<String>> adjList;  // Lista di adiacenza per il grafo

    public ArticoloGraph() {
        adjList = new HashMap<>();
    }

    // Aggiungi un nodo (articolo) al grafo
    public void addNode(String invoiceId) {
        adjList.putIfAbsent(invoiceId, new HashSet<>());
    }

    // Aggiungi un arco tra due articoli
    public void addEdge(String fromInvoiceId, String toInvoiceId) {
        adjList.get(fromInvoiceId).add(toInvoiceId);
        adjList.get(toInvoiceId).add(fromInvoiceId);
    }

    // Funzione per ottenere la lista di nodi collegati a un dato articolo
    public static Set<String> getNeighbors(String invoiceId) {
        return adjList.getOrDefault(invoiceId, new HashSet<>());
    }

    // Stampa il grafo
    public void printGraph() {
        for (String node : adjList.keySet()) {
            System.out.println(node + " --> " + adjList.get(node));
        }
    }


    public String getFirstLeftMostNode() {
        if (adjList.isEmpty()) {
            return null;
        }

        // Assumiamo che il primo nodo inserito nella mappa sia il più a sinistra
        return adjList.keySet().iterator().next();
    }


}

