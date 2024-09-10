package com.example;

import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;


public class RandomForestModel {

    private RandomForest randomForest;

    // Costruttore: addestra il modello utilizzando i dati di training
    public RandomForestModel(Instances trainingData) {
        try {
            // Crea una nuova istanza di RandomForest
            randomForest = new RandomForest();
            
            // Configura il numero di alberi nella foresta (ad esempio 100)
            String[] options = weka.core.Utils.splitOptions("-P 100");
            randomForest.setOptions(options);// Numero di alberi nella foresta
            
            // Addestra il modello sui dati di training
            randomForest.buildClassifier(trainingData);
            
            System.out.println("Modello Random Forest addestrato con successo.");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Metodo per prevedere il valore della classe per una nuova istanza
    public double predict(Instance instance) {
        try {
            // Usa il modello addestrato per prevedere il valore della classe
            double prediction = randomForest.classifyInstance(instance);
            return prediction;
        } catch (Exception e) {
            System.out.println(e);
        }
        return Double.NaN;
    }

    // Metodo per prevedere i valori di classe per un insieme di istanze
    public double[] predict(Instances instances) {
        double[] predictions = new double[instances.numInstances()];
        for (int i = 0; i < instances.numInstances(); i++) {
            predictions[i] = predict(instances.instance(i));
        }
        return predictions;
    }

    // Metodo per valutare il modello utilizzando dati di test
    public void evaluateModel(Instances testData) {
        try {
            weka.classifiers.Evaluation evaluation = new weka.classifiers.Evaluation(testData);
            evaluation.evaluateModel(randomForest, testData);

            System.out.println(evaluation.toSummaryString("\nRisultati di valutazione:\n", false));
            System.out.println("RMSE: " + evaluation.rootMeanSquaredError());
            System.out.println("R^2: " + evaluation.correlationCoefficient());
           
        } catch (Exception e) {
           System.out.println(e);
        }
    }

    // Restituisce l'oggetto RandomForest per ulteriori configurazioni o utilizzi
    public RandomForest getRandomForest() {
        return randomForest;
    }



    public static void Crossvalidation(Instances data){

 try {
            // Carica il dataset
        

            // Imposta l'indice della classe (variabile target)
            data.setClassIndex(data.numAttributes() - 1);

            // Normalizza i dati (opzionale ma consigliato)
            Normalize filter = new Normalize();
            filter.setInputFormat(data);
            Instances normalizedData = Filter.useFilter(data, filter);

            // Crea e configura il modello RandomForest
            RandomForest rf = new RandomForest();
            String[] options = weka.core.Utils.splitOptions("-P 100");
            rf.setOptions(options);// Numero di alberi nella foresta

            // Esegui la cross-validation
            int folds = 5; // Numero di fold per la cross-validation
            Evaluation evaluation = new Evaluation(normalizedData);

            evaluation.crossValidateModel(rf, normalizedData, folds, new Random(1));

            // Stampa i risultati della valutazione
            System.out.println("Cross-Validation Risultati:");
            System.out.println("RMSE: " + evaluation.rootMeanSquaredError());
            System.out.println("R^2: " + evaluation.correlationCoefficient());
            System.out.println("MAE: " + evaluation.meanAbsoluteError());
            System.out.println("Relative Absolute Error: " + evaluation.relativeAbsoluteError());
            System.out.println("Root Relative Squared Error: " + evaluation.rootRelativeSquaredError());

        } catch (Exception e) {
           System.out.println(e);
        }


    }
}
