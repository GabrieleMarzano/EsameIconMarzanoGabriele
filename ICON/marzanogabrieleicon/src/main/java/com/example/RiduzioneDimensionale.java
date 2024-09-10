package com.example;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class RiduzioneDimensionale {

    public static RealMatrix pca(List<Articolo> articoli, int numComponenti) {
        // Step 1: Converti la lista di Articolo in una matrice
        RealMatrix matriceDati = convertiInMatrice(articoli);
        
        // Step 2: Centralizza i dati
        RealMatrix matriceCentralizzata = centraMatrice(matriceDati);

        // Step 3: Calcola la matrice di covarianza
        RealMatrix matriceCovarianza = matriceCentralizzata.transpose().multiply(matriceCentralizzata)
                                         .scalarMultiply(1.0 / (matriceCentralizzata.getRowDimension() - 1));
         
        // Step 4: Esegui la decomposizione agli autovalori
        EigenDecomposition eigDecomp = new EigenDecomposition(matriceCovarianza);
        RealMatrix vettoriPropri = eigDecomp.getV();
        RealVector valoriPropri = new ArrayRealVector(eigDecomp.getRealEigenvalues());

        // Step 5: Ordina gli autovalori e gli autovettori
        int[] indici = ordinaIndici(valoriPropri.toArray());
        RealMatrix vettoriPropriOrdinati = vettoriPropri.getSubMatrix(0, vettoriPropri.getRowDimension() - 1, indici[0], indici[numComponenti - 1]);

        // Step 6: Proietta i dati
        RealMatrix datiRidotti = matriceCentralizzata.multiply(vettoriPropriOrdinati);

        return datiRidotti;
    }

    private static RealMatrix convertiInMatrice(List<Articolo> articoli) {
        int numCaratteristiche = 12; // Numero di caratteristiche numeriche
        RealMatrix matrice = new Array2DRowRealMatrix(articoli.size(), numCaratteristiche);

        // Mappa per la conversione delle categorie in numeri
        Map<String, Integer> mappaCategoria = new HashMap<>();
        int index = 0;

        for (int i = 0; i < articoli.size(); i++) {
            Articolo articolo = articoli.get(i);
            matrice.setEntry(i, 0, articolo.getUnitPrice());
            matrice.setEntry(i, 1, articolo.getQuantity());
            matrice.setEntry(i, 2, articolo.getTax());
            matrice.setEntry(i, 3, articolo.getTotal());
            matrice.setEntry(i, 4, articolo.getCogs());
            matrice.setEntry(i, 5, articolo.getGrossMarginPercentage());
            matrice.setEntry(i, 6, articolo.getGrossIncome());
            matrice.setEntry(i, 7, articolo.getRating());
            matrice.setEntry(i, 8, convertiCategoria(articolo.getBranch(), mappaCategoria)); 
            matrice.setEntry(i, 9, convertiCategoria(articolo.getCustomerType(), mappaCategoria));
            matrice.setEntry(i, 10, convertiCategoria(articolo.getGender(), mappaCategoria));
            matrice.setEntry(i, 11, convertiCategoria(articolo.getProductLine(), mappaCategoria));
        }

        return matrice;
    }

    private static RealMatrix centraMatrice(RealMatrix matrice) {
        RealVector media = matrice.getColumnVector(0);
        for (int i = 1; i < matrice.getColumnDimension(); i++) {
            media = media.add(matrice.getColumnVector(i));
        }
        media = media.mapDivide(matrice.getColumnDimension());

        RealMatrix mediaMatrix = MatrixUtils.createRowRealMatrix(media.toArray());
        RealMatrix repeatedMediaMatrix = MatrixUtils.createRealMatrix(
            matrice.getRowDimension(),
            matrice.getColumnDimension()
        );
        for (int i = 0; i < matrice.getRowDimension(); i++) {
            repeatedMediaMatrix.setRowVector(i, media);
        }
    
        // Sottrai la matrice media ripetuta dalla matrice originale
        return matrice.subtract(repeatedMediaMatrix);
    }

    private static int[] ordinaIndici(double[] valoriPropri) {
        Integer[] indici = new Integer[valoriPropri.length];
        for (int i = 0; i < valoriPropri.length; i++) {
            indici[i] = i;
        }

        Arrays.sort(indici, (a, b) -> Double.compare(valoriPropri[b], valoriPropri[a]));

        return Arrays.stream(indici).mapToInt(Integer::intValue).toArray();
    }

    private static double convertiCategoria(String categoria, Map<String, Integer> mappaCategoria) {
        if (!mappaCategoria.containsKey(categoria)) {
            mappaCategoria.put(categoria, mappaCategoria.size());
        }
        return mappaCategoria.get(categoria);
    }
}
