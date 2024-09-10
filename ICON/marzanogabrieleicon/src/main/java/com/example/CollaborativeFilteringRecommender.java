package com.example;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;

public class CollaborativeFilteringRecommender {

    private Recommender recommender;
    private DataModel dataModel;

    public CollaborativeFilteringRecommender(String dataFilePath) throws IOException, TasteException {
        // Carica il modello di dati dal dataset
        this.dataModel = new FileDataModel(new File(dataFilePath));

        // Definisci la similarità tra gli utenti
        UserSimilarity userSimilarity = new PearsonCorrelationSimilarity(dataModel);
        UserNeighborhood neighborhood = new NearestNUserNeighborhood(3, userSimilarity, dataModel);

        // Crea il recommender basato sugli utenti
        this.recommender = new GenericUserBasedRecommender(dataModel, neighborhood, userSimilarity);
    }

    public List<RecommendedItem> recommendForUser(long userId, int numberOfRecommendations) throws TasteException {
        return recommender.recommend(userId, numberOfRecommendations);
    }

    public double evaluateModel() throws TasteException {
        RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();

        RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
            @Override
            public Recommender buildRecommender(DataModel dataModel) throws TasteException {
                UserSimilarity userSimilarity = new PearsonCorrelationSimilarity(dataModel);
                UserNeighborhood neighborhood = new NearestNUserNeighborhood(3, userSimilarity, dataModel);
                return new GenericUserBasedRecommender(dataModel, neighborhood, userSimilarity);
            }
        };

        // Esegui la valutazione
        return evaluator.evaluate(recommenderBuilder, null, dataModel, 0.7, 1.0);
    }

    public void printRecommendationsForAllUsers() throws TasteException {
        for (LongPrimitiveIterator users = dataModel.getUserIDs(); users.hasNext();) {
            long userId = users.nextLong();
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
}
