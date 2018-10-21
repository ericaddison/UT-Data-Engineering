package edu.ut.ede.dataengineering.hw6;

import com.google.common.collect.ImmutableList;

import java.sql.*;

public class ExperimentRunner {

    enum KeyOrder {
        IN_ORDER,
        RANDOM
    }

    public static void main(String[] args) {
        int numRecords = 5000000;
        ImmutableList<ImmutableList<String>> indexColumnLists = ImmutableList.of(
                ImmutableList.of(),
                ImmutableList.of("columnA"),
                ImmutableList.of("columnB"),
                ImmutableList.of("columnA", "columnB")
        );

        ImmutableList.Builder<ExperimentOptions> builder = ImmutableList.builder();

        for (PhysicalOrganization org : PhysicalOrganization.values()) {
            for (KeyOrder keyOrder : KeyOrder.values()) {
                builder.add(ExperimentOptions.create(keyOrder, org, numRecords));
            }
        }
        ImmutableList<ExperimentOptions> allOptions = builder.build();


        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/testdb",
                "postgres", "postgres")) {

            Statement statement = connection.createStatement();

            // create the experiment table
            statement.execute("CREATE TABLE IF NOT EXISTS experiments (\n" +
                    "numRecords INT,\n" +
                    "physicalOrg INT REFERENCES physicalorg(id),\n" +
                    "keyOrder VARCHAR(10),\n" +
                    "dataLoadTime REAL,\n" +
                    "query1Time REAL,\n" +
                    "query2Time REAL,\n" +
                    "query3Time REAL,\n" +
                    "queryValue INT)");

            PreparedStatement resultInsert =
                    connection.prepareStatement("INSERT INTO experiments VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

            // perform experiments 100 times for each set of options
            for(int i=0; i<100; i++) {
                System.out.println("Iteration " + i);
                for (ExperimentOptions options : allOptions) {
                    ExperimentResults results = Experiment.performExperiment(options);
                    resultInsert.setInt(1, options.numRecords);
                    resultInsert.setInt(2, options.physicalOrganization.id);
                    resultInsert.setString(3, options.keyOrder.toString());
                    resultInsert.setFloat(4, results.dataLoadDuration.toMillis() / 1000.0f);
                    resultInsert.setFloat(5, results.query1Duration.toMillis() / 1000.0f);
                    resultInsert.setFloat(6, results.query2Duration.toMillis() / 1000.0f);
                    resultInsert.setFloat(7, results.query3Duration.toMillis() / 1000.0f);
                    resultInsert.setFloat(8, results.queryValue);
                    resultInsert.execute();
                    System.out.println("\t" + results);
                }
            }

        }  catch (SQLException e) {
            e.printStackTrace();
        }



    }
}
