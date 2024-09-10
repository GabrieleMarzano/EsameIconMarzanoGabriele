package com.example;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class Main {

    public static void main(String[] args) throws Exception {

        String pathDataset = "C:\\Users\\Gabriele\\Desktop\\ICON\\marzanogabrieleicon\\src\\Dataset\\DatasetSupermarket.csv";
        String pathDatasetNoINte = "C:\\Users\\Gabriele\\Desktop\\ICON\\marzanogabrieleicon\\src\\Dataset\\DatasetSupermarketNOintes.csv";
        Dataset dataset = new Dataset();
        dataset.caricaDaCSV(pathDataset);
        String inputFilePath = pathDatasetNoINte;
        String outputFilePath = "C:\\Users\\Gabriele\\Desktop\\ICON\\marzanogabrieleicon\\src\\Dataset\\DatasetSupermarketSolonumeri.csv";
        String userPath = "C:\\Users\\Gabriele\\Desktop\\ICON\\marzanogabrieleicon\\src\\Dataset\\User.csv";
        String ratings_datasetPath = "C:\\Users\\Gabriele\\Desktop\\ICON\\marzanogabrieleicon\\src\\Dataset\\ratings_dataset.csv";
        List<Articolo> listaArticoli;


 //controllo sulla correttezza e corerenza dei valori degli articoli tramite regole di prolog
        listaArticoli = ControllDataset.cleanData1(dataset.getListaArticoli());
//controllo sulla correttezza e corerenza dei valori degli articoli tramite funzioni java base
        listaArticoli = ControllDataset.cleanData(dataset.getListaArticoli());     //pulizia e controllo dell dataset

        dataset.setListaArticoli(listaArticoli);

        //statistiche di base per il dataset
        Sessione.CalcolaStatistiche(dataset);

        Sessione.GraficoVendite(dataset);

        Sessione.IstogrammaVendite(dataset);

        // Calcolo delle nuove feature per articoli nel dataset
        List<EnhancedArticolo> enhancedArticoli = FeatureEngineeringUtils.calculateFeatures(dataset.getListaArticoli());

        //apprendimento non Supervisionato per riduzione della dimensionalità
        RealMatrix datiRidotti = RiduzioneDimensionale.pca(dataset.getListaArticoli(), 7);
        System.out.println("Dati ridotti:");
        System.out.println(datiRidotti);
        try {
            // Converti i dati in Instances di Weka
            // Converti i dati in Instances di Weka
            Instances data = ControllDataset.convertToInstances(dataset.getListaArticoli());

            // Costruisci e valuta il modello
            ControllDataset.buildAndEvaluateModel(data);

        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            // Converti i dati in Instances di Weka
            // Converti i dati in Instances di Weka
            Instances data = ControllDataset.convertToInstances(dataset.getListaArticoli());

            // Costruisci e valuta il modello //cambiando i dati di test e train
            ControllDataset.buildAndEvaluateModelReverse(data);

        } catch (Exception e) {
            System.out.println(e);
        }

        //randomforest
        try {

            Instances dataForest = ControllDataset.convertToInstances(dataset.getListaArticoli());

            int trainSize = (int) Math.round(dataForest.numInstances() * 0.8);
            int testSize = dataForest.numInstances() - trainSize;
            Instances trainData = new Instances(dataForest, 0, trainSize);
            Instances testData = new Instances(dataForest, trainSize, testSize);

            trainData.setClassIndex(trainData.numAttributes() - 1); // Assicurati che l'attributo target sia l'ultimo

            // Crea e addestra il modello Random Forest
            RandomForestModel randomForestModel = new RandomForestModel(trainData);

            testData.setClassIndex(testData.numAttributes() - 1); // Imposta l'attributo target come l'ultimo

            // Valutazione del modello sui dati di test
            randomForestModel.evaluateModel(testData);

            // Fare una previsione con una nuova istanza (esempio)
            Instance newInstance = new DenseInstance(trainData.numAttributes());
            newInstance.setDataset(dataForest);

            // Imposta i valori per la nuova istanza (esempio)
            /* System.out.println("nerissima");
            // Prevedere il valore per la nuova istanza
            double prediction = randomForestModel.predict(newInstance);
            System.out.println("Valore Predetto: " + prediction);
             */
        } catch (Exception e) {
            System.out.println(e);
        }

        Instances dataCrossValidation = ControllDataset.convertToInstances(dataset.getListaArticoli());
        RandomForestModel.Crossvalidation(dataCrossValidation);

        // Costruisce il grafo e stampa i risultati
        ArticoloGraphAnalyzer.buildGraph(dataset.getListaArticoli());

//raccomender collaborative
        try {
            ControllDataset.convertFile(inputFilePath, outputFilePath);
            System.out.println("File convertito con successo.");
        } catch (IOException e) {
            System.out.println(e);
        }
        try {
            String dataFilePath = userPath;
            File processedFile = ControllDataset.convertIDsToNumeric(dataFilePath);
            // Usa il file processato per creare il recommender
            CollaborativeFilteringRecommender recommender = new CollaborativeFilteringRecommender(processedFile.getAbsolutePath());

            // Esegui le raccomandazioni e la valutazione
            List<RecommendedItem> recommendations = recommender.recommendForUser(1, 2);

            for (int i = 0; i <= 20; i++) {
                System.out.println("Raccomandazione per Utente: " + i);
                recommendations = recommender.recommendForUser(i, 1);
                if (recommendations.isEmpty()) {
                    System.out.println("Nessuna raccomandazione per utente: " + i);
                } else {
                    for (RecommendedItem recommendation : recommendations) {
                        ArticoloGraphAnalyzer.getLeftMostConnectedNode("2");
                        System.out.println("Prodotto Consigliato: " + recommendation);
                    }
                }

            }
            double score = recommender.evaluateModel();
            System.out.println("Evaluation score: " + score);

        } catch (IOException | TasteException e) {
            System.out.println(e);
        }
    }

}
