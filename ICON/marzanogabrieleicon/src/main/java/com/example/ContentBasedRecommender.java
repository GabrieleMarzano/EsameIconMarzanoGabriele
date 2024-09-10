package com.example;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

public class ContentBasedRecommender {

    private Recommender recommender;

    public ContentBasedRecommender(String dataFilePath) throws IOException, TasteException {
        // Carica il modello di dati dal dataset
        DataModel dataModel = new FileDataModel(new File(dataFilePath));

        // Definisci la similarità tra i prodotti
        ItemSimilarity itemSimilarity = new PearsonCorrelationSimilarity(dataModel);

        // Crea il recommender basato sugli articoli
        this.recommender = new GenericItemBasedRecommender(dataModel, itemSimilarity);
    }

    public List<RecommendedItem> recommendForUser(long userId, int numberOfRecommendations) throws TasteException {
        return recommender.recommend(userId, numberOfRecommendations);
    }

    public void printRecommendationsForAllUsers() throws TasteException {
        DataModel dataModel = recommender.getDataModel();
        LongPrimitiveIterator users = dataModel.getUserIDs();

        if (!users.hasNext()) {
            System.out.println("Nessun utente trovato nel dataset.");
            return;
        }

        // Stampa le raccomandazioni per tutti gli utenti
        for (LongPrimitiveIterator it = dataModel.getUserIDs(); it.hasNext();) {
            long userId = it.nextLong();
            List<RecommendedItem> recommendations = recommendForUser(userId, 5);
            if (recommendations.isEmpty()) {
                System.out.println("No recommendations for user " + userId);
            } else {
                for (RecommendedItem recommendation : recommendations) {
                    System.out.println("User " + userId + ": " + recommendation);
                }
            }
        }
    }

    public void printRecommendationsForSingleUser(long userId) throws TasteException {
        List<RecommendedItem> recommendations = recommendForUser(userId, 1);
        if (recommendations.isEmpty()) {
            System.out.println("No recommendations for user " + recommendations);
        } else {
            for (RecommendedItem recommendation : recommendations) {
                System.out.println("User " + userId + ": " + recommendation);
            }
        }
    }
}
