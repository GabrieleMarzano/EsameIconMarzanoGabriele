package com.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

public class ControllDataset {

    // Set di valori validi per i campi categoriali 
    private static final Set<String> VALID_BRANCHES = new HashSet<>(Arrays.asList("A", "B", "C"));
    private static final Set<String> VALID_CUSTOMER_TYPES = new HashSet<>(Arrays.asList("Member", "Normal"));
    private static final Set<String> VALID_GENDERS = new HashSet<>(Arrays.asList("Male", "Female"));
    private static final Set<String> VALID_PRODUCT_LINES = new HashSet<>(Arrays.asList("Electronic accessories", "Cosmetics", "Food and beverages", "Sports and travel", "Home and lifestyle", "Fashion"));
    private static final Set<String> VALID_PAYMENTS = new HashSet<>(Arrays.asList("Cash", "Credit card", "Ewallet"));

    
    public static List<Articolo> cleanData(List<Articolo> articoli) {
        // Rimozione dei duplicati utilizzando un Set
        Set<String> uniqueInvoiceIds = new HashSet<>();
        List<Articolo> cleanedList = new ArrayList<>();

        

        for (Articolo articolo : articoli) {
            // Validazione dei valori categoriali
            if (!VALID_BRANCHES.contains(articolo.getBranch())
                    || !VALID_CUSTOMER_TYPES.contains(articolo.getCustomerType())
                    || !VALID_GENDERS.contains(articolo.getGender())
                    || !VALID_PRODUCT_LINES.contains(articolo.getProductLine())
                    || !VALID_PAYMENTS.contains(articolo.getPayment())) {
                continue;  // Salta questo articolo perché non è valido
            }
            // Validazione della coerenza dei valori numerici
            if (!isNumericValuesConsistent(articolo)) {
                continue;
            }

            // Verifica delle date
            if (!isDateValid(articolo.getDate())) {
                continue;
            }

            // Aggiungi l'articolo pulito alla lista
            cleanedList.add(articolo);
        }

        //rimozione duplicati
        for (Articolo articolo : articoli) {

            if (uniqueInvoiceIds.add(articolo.getInvoiceId())) {
                cleanedList.add(articolo);
            }
        }

        // Rimozione di articoli con valori null o invalidi
        cleanedList.removeIf(articolo -> articolo == null || articolo.getDate() == null || articolo.getTotal() <= 0);

        cleanedList = removeOutliers(cleanedList);

        return cleanedList;
    }


    


    // Metodo per la validazione della coerenza dei valori numerici
    private static boolean isNumericValuesConsistent(Articolo articolo) {
        double calculatedTotal = (articolo.getUnitPrice() * articolo.getQuantity()) + articolo.getTax();
        if (Math.abs(calculatedTotal - articolo.getTotal()) > 0.01) {
            return false;
        }

        double calculatedGrossIncome = articolo.getTotal() - articolo.getCogs();
        if (Math.abs(calculatedGrossIncome - articolo.getGrossIncome()) > 0.01) {
            return false;
        }

        return articolo.getGrossMarginPercentage() >= 0 && articolo.getGrossMarginPercentage() <= 100;
    }

    // Metodo per la validazione delle date
    private static boolean isDateValid(Date date) {
        Date currentDate = new Date();
        return !date.after(currentDate);  // La data non deve essere futura
    }

    // Metodo per la rimozione degli outliers
    private static List<Articolo> removeOutliers(List<Articolo> articoli) {
        List<Articolo> filteredArticoli = new ArrayList<>();

        double meanRating = articoli.stream().mapToDouble(Articolo::getRating).average().orElse(0.0);
        double stdDevRating = Math.sqrt(articoli.stream().mapToDouble(a -> Math.pow(a.getRating() - meanRating, 2)).average().orElse(0.0));

        for (Articolo articolo : articoli) {
            double rating = articolo.getRating();
            if (rating >= (meanRating - 2 * stdDevRating) && rating <= (meanRating + 2 * stdDevRating)) {
                filteredArticoli.add(articolo);
            }
        }

        return filteredArticoli;
    }


     // Metodo per convertire una lista di articoli in un formato Instances di Weka
     public static Instances convertToInstances(List<Articolo> articoli) {
        ArrayList<Attribute> attributes = new ArrayList<>();
    
        // Definire gli attributi
        Attribute classAttribute = new Attribute("InvoiceId"); // Attributo di classe deve essere unico //16
        attributes.add(classAttribute);
        attributes.add(new Attribute("Branch", Arrays.asList("A", "B", "C"))); //0
        attributes.add(new Attribute("City", Arrays.asList("Yangon", "Naypyitaw", "Mandalay"))); //1
        attributes.add(new Attribute("CustomerType", Arrays.asList("Member", "Normal"))); //2
        attributes.add(new Attribute("Gender", Arrays.asList("Male", "Female"))); //3
        attributes.add(new Attribute("ProductLine", Arrays.asList("Electronic accessories",  "Food and beverages", "Sports and travel", "Health and beauty", "Fashion accessories","Home and lifestyle"))); //4
        attributes.add(new Attribute("UnitPrice"));  // double 5
        attributes.add(new Attribute("Quantity"));   // int 6
        attributes.add(new Attribute("Tax"));        // double 7
        attributes.add(new Attribute("Total"));      // double 8
        attributes.add(new Attribute("DateInDays"));//9
        attributes.add(new Attribute("Payment", Arrays.asList("Cash", "Credit card", "Ewallet"))); //10
        attributes.add(new Attribute("TimeInMinutes")); // double, ore e minuti convertiti in minuti 11
        attributes.add(new Attribute("Cogs"));       // double 12
        attributes.add(new Attribute("GrossMarginPercentage")); // double 13
        attributes.add(new Attribute("GrossIncome")); // double, giorni dall'inizio dell'anno 14
        attributes.add(new Attribute("Rating"));     // float 15
        
     

        
        
        // Attributo di classe (variabile target)
       
        // Creare l'oggetto Instances
        Instances data = new Instances("SalesData", attributes, articoli.size());
        data.setClassIndex(data.numAttributes() - 1); // Imposta l'indice dell'attributo target
    
        // Aggiungere gli articoli come istanze
        for (Articolo articolo : articoli) {
            double[] values = new double[data.numAttributes()];
    
            // Verifica e assegna i valori
            values[0] = creaIdentificativoUnivoco(articolo.getInvoiceId()); // Variabile target
            values[1] = data.attribute(1).indexOfValue(articolo.getBranch());
            values[2] = data.attribute(2).indexOfValue(articolo.getCity());
            values[3] = data.attribute(3).indexOfValue(articolo.getCustomerType());
            values[4] = data.attribute(4).indexOfValue(articolo.getGender());
            values[5] = data.attribute(5).indexOfValue(articolo.getProductLine());

            // Controllo degli indici
            if (values[0] == -1 || values[1] == -1 || values[2] == -1 || values[3] == -1 || values[4] == -1) {
                System.err.println("errore nei primi 5 attributi " + articolo);
                continue; // Salta l'iterazione se un valore non è trovato
            }
    
            values[6] = articolo.getUnitPrice(); // UnitPrice
            
            values[7] = articolo.getQuantity();  // Quantity
            values[8] = articolo.getTax();       // Tax
            values[9] = articolo.getTotal();     // Total
            values[10] = convertDateToDays(articolo.getDate()); // DateInDays
            values[11] = data.attribute(11).indexOfValue(articolo.getPayment());
            values[12] = convertTimeToMinutes(articolo.getTime()); // TimeInMinutes
            values[13] = articolo.getCogs();      // Cogs
            values[14] = articolo.getGrossMarginPercentage(); // GrossMarginPercentage
            values[15] = articolo.getGrossIncome(); 
            values[16] = articolo.getRating();   // Rating
           


     
            // Controllo dell'indice del metodo di pagamento
            if (values[12] == -1) {
                System.err.println("Errore: Valore del metodo di pagamento non trovato. Ignorando l'articolo: " + articolo);
                continue; // Salta l'iterazione se il metodo di pagamento non è trovato
            }
    

            
           
            // Crea l'istanza e aggiungila ai dati
            data.add(new DenseInstance(1.0, values));
        }
    
        return data;
    }
    

    private static double convertDateToDays(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        return dayOfYear;
    }

    private static double convertTimeToMinutes(String time) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            Date date = format.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        } catch (ParseException e) {
            System.out.println(e);
            return 0;
        }
    }



       // Metodo per costruire e valutare un modello di regressione lineare
       public static void buildAndEvaluateModel(Instances data) throws Exception {
        // Dividi i dati in training e test
        int trainSize = (int) Math.round(data.numInstances() * 0.8);
        int testSize = data.numInstances() - trainSize;
        Instances trainData = new Instances(data, 0, trainSize);
        Instances testData = new Instances(data, trainSize, testSize);

        // Crea e addestra il modello di regressione lineare
        LinearRegression model = new LinearRegression();
        model.buildClassifier(trainData);

        // Valuta il modello
        Evaluation eval = new Evaluation(trainData);
        eval.evaluateModel(model, testData);

        // Stampa i risultati della valutazione
        System.out.println("RMSE: " + eval.rootMeanSquaredError()); //Root Mean Squared Error Errore quadrato medio di radice
        System.out.println("R^2: " + eval.correlationCoefficient() * eval.correlationCoefficient()); //coefficiente di determinazione


        List<Double> actualValues = new ArrayList<>();
        List<Double> predictedValues = new ArrayList<>();
        // Previsione sui dati di test
        for (int i = 0; i < testData.numInstances(); i++) {
            double actual = testData.instance(i).classValue();
            double predicted = model.classifyInstance(testData.instance(i));
            //System.out.println("Actual: " + actual + ", Predicted: " + predicted);

            actualValues.add(actual);
            predictedValues.add(predicted);
        }

        SwingUtilities.invokeLater(() -> {
            SalesPredictionChart example = new SalesPredictionChart("Sales Prediction", actualValues, predictedValues);
            example.setSize(800, 600);
            example.setLocationRelativeTo(null);
            example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            example.setVisible(true);
        });


        


        
    }

    

    
    public static void buildAndEvaluateModelReverse(Instances data) throws Exception {
        // Dividi i dati in training e test
        int trainSize = (int) Math.round(data.numInstances() * 0.8);
        int testSize = data.numInstances() - trainSize;
        Instances testData = new Instances(data, 0, trainSize);
        Instances trainData = new Instances(data, trainSize, testSize);

        // Crea e addestra il modello di regressione lineare
        LinearRegression model = new LinearRegression();
        model.buildClassifier(trainData);

        // Valuta il modello
        Evaluation eval = new Evaluation(trainData);
        eval.evaluateModel(model, testData);

        // Stampa i risultati della valutazione
        System.out.println("RMSE: " + eval.rootMeanSquaredError()); //Root Mean Squared Error Errore quadrato medio di radice
        System.out.println("R^2: " + eval.correlationCoefficient() * eval.correlationCoefficient()); //coefficiente di determinazione

        List<Double> actualValues = new ArrayList<>();
        List<Double> predictedValues = new ArrayList<>();
       // Previsione sui dati di test
        for (int i = 0; i < testData.numInstances(); i++) {
            double actual = testData.instance(i).classValue();
            double predicted = model.classifyInstance(testData.instance(i));
     /*        System.out.println("Actual: " + actual + ", Predicted: " + predicted); */

            actualValues.add(actual);
            predictedValues.add(predicted);
        } 

                SwingUtilities.invokeLater(() -> {
            SalesPredictionChart example = new SalesPredictionChart("Sales Prediction", actualValues, predictedValues);
            example.setSize(800, 600);
            example.setLocationRelativeTo(null);
            example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            example.setVisible(true);
        });
    }





    public static double creaIdentificativoUnivoco(String input) {
        // Rimuovi i trattini dalla stringa
        String stringaSenzaTrattini = input.replace("-", "");
        
        // Converti la stringa risultante in double
        try {
            double identificativo = Double.parseDouble(stringaSenzaTrattini);
            return identificativo;
        } catch (NumberFormatException e) {
            // Gestione dell'errore nel caso in cui la stringa non possa essere convertita
            System.out.println("Errore: La stringa non può essere convertita in un double.");
            return -1; // Oppure un altro valore di default o gestisci diversamente l'errore
        }
    }


public static File convertIDsToNumeric(String filePath) throws IOException {
        File tempFile = File.createTempFile("mahout_data_numeric_", ".csv");
        Map<String, Long> userMapping = new HashMap<>();
        Map<String, Long> itemMapping = new HashMap<>();
        long userIdCounter = 1;
        long itemIdCounter = 1;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");

                // Assuming the first column is User ID and the second column is Item ID
                String userId = tokens[0];
                String itemId = tokens[1];

                // Convert user ID to numeric
                if (!userMapping.containsKey(userId)) {
                    userMapping.put(userId, userIdCounter++);
                }
                tokens[0] = userMapping.get(userId).toString();

                // Convert item ID to numeric
                if (!itemMapping.containsKey(itemId)) {
                    itemMapping.put(itemId, itemIdCounter++);
                }
                tokens[1] = itemMapping.get(itemId).toString();

                // Write the new line with numeric IDs
                writer.write(String.join(",", tokens));
                writer.newLine();
            }
        }

        return tempFile;
    }


    private static Map<String, Integer> categoryToNumberMap = new HashMap<>();
    private static int nextId = 1;

    public static void convertFile(String inputFilePath, String outputFilePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {

            String line;
            while ((line = reader.readLine()) != null) {
                // Trasforma la riga e scrivi nel file di output
                String transformedLine = transformLine(line);
                writer.write(transformedLine);
                writer.newLine();
            }
        }
    }
    private static String transformLine(String line) {
        String[] parts = line.split(",");
        StringBuilder transformedLine = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                transformedLine.append(",");
            }
            transformedLine.append(transformValue(parts[i]));
        }

        return transformedLine.toString();
    }


    public static String transformValue(String value) {
        if (isNumeric(value)) {
            return value; // Già un numero, non fare nulla
        } else {
            // Trasforma il valore non numerico
            return String.valueOf(categoryToNumberMap.computeIfAbsent(value, k -> nextId++));
        }
    }

    public  static boolean isNumeric(String str) {
        return str != null && str.matches("-?\\d+(\\.\\d+)?");
    }

  

 private static final String PROLOG_FILE_PATH = "C:\\Users\\Gabriele\\Desktop\\ICON\\marzanogabrieleicon\\regole.pl";

    // Metodo per caricare il file Prolog+
    public static void loadPrologFile() {
        Query consultQuery = new Query("consult('" + PROLOG_FILE_PATH + "')");
        if (consultQuery.hasSolution()) {
            System.out.println("Regole Prolog caricate con successo.");
        } else {
            System.out.println("Errore nel caricamento delle regole Prolog.");
        }
    }

    //applico regole di prolog
    public static List<Articolo> cleanData1(List<Articolo> articoli) {
        // Carica le regole Prolog
        loadPrologFile();

        // Rimozione dei duplicati utilizzando un Set
        Set<String> uniqueInvoiceIds = new HashSet<>();
        List<Articolo> cleanedList = new ArrayList<>();

        for (Articolo articolo : articoli) {
            // Validazione dei valori categoriali tramite Prolog
            if (!isValidBranch(articolo.getBranch())
                    || !isValidCustomerType(articolo.getCustomerType())
                    || !isValidGender(articolo.getGender())
                    || !isValidProductLine(articolo.getProductLine())
                    || !isValidPayment(articolo.getPayment())) {
                continue;  // Salta questo articolo perché non è valido
            }

            // Validazione della coerenza dei valori numerici tramite Prolog
            if (!isNumericValuesConsistent(articolo)) {
                continue;
            }

            // Verifica delle date tramite Prolog
            if (!isDateValid(articolo.getDate())) {
                continue;
            }

            // Aggiungi l'articolo pulito alla lista
            cleanedList.add(articolo);
        }

        return cleanedList;
    }

    // Metodi per fare query Prolog per ogni controllo

    private static boolean isValidBranch(String branch) {
        Query query = new Query("valid_branch", new Term[]{new Atom(branch)});
        return query.hasSolution();
    }

    private static boolean isValidCustomerType(String customerType) {
        Query query = new Query("valid_customer_type", new Term[]{new Atom(customerType)});
        return query.hasSolution();
    }

    private static boolean isValidGender(String gender) {
        Query query = new Query("valid_gender", new Term[]{new Atom(gender)});
        return query.hasSolution();
    }

    private static boolean isValidProductLine(String productLine) {
        Query query = new Query("valid_product_line", new Term[]{new Atom(productLine)});
        return query.hasSolution();
    }

    private static boolean isValidPayment(String payment) {
        Query query = new Query("valid_payment", new Term[]{new Atom(payment)});
        return query.hasSolution();
    }

    // Verifica la coerenza dei valori numerici tramite Prolog
    private static boolean isNumericValuesConsistent(Articolo articolo) {
        Query query = new Query("numeric_values_consistent", new Term[]{
            new org.jpl7.Float(articolo.getUnitPrice()),
            new org.jpl7.Integer(articolo.getQuantity()),
            new org.jpl7.Float(articolo.getTax()),
            new org.jpl7.Float(articolo.getTotal()),
            new org.jpl7.Float(articolo.getCogs()),
            new org.jpl7.Float(articolo.getGrossIncome()),
            new org.jpl7.Float(articolo.getGrossMargin())
        });
        return query.hasSolution();
    }

    // Verifica se la data è valida tramite Prolog
    private static boolean isDateValid(Date date) {
        long timestamp = date.getTime() / 1000L;  // Converte la data in timestamp Unix
        Query query = new Query("date_valid", new Term[]{new org.jpl7.Float(timestamp)});
        return query.hasSolution();
    }

    
}
