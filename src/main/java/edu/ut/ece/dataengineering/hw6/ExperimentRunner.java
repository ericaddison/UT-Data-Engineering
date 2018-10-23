package edu.ut.ece.dataengineering.hw6;

import com.google.common.collect.ImmutableList;

import java.sql.*;
import java.time.Duration;
import java.time.Instant;

public class ExperimentRunner {

    enum KeyOrder {IN_ORDER, RANDOM}

    public static void main(String[] args) {
        int numRecords = 5000000;
        ImmutableList.Builder<ExperimentOptions> builder = ImmutableList.builder();

        for (PhysicalOrganization org : PhysicalOrganization.values()) {
            for (KeyOrder keyOrder : KeyOrder.values()) {
                builder.add(ExperimentOptions.create(keyOrder, org, numRecords));
            }
        }
        ImmutableList<ExperimentOptions> allOptions = builder.build();

        executeSingleStatement(
                "CREATE TABLE IF NOT EXISTS experiments (\n" +
                        "numRecords INT,\n" +
                        "physicalOrg INT REFERENCES physicalorg(id),\n" +
                        "keyOrder VARCHAR(10),\n" +
                        "dataLoadTime REAL,\n" +
                        "query1Time REAL,\n" +
                        "query2Time REAL,\n" +
                        "query3Time REAL,\n" +
                        "queryValue INT)");

        // perform experiments 100 times for each set of options
        Experiment experiment = new Experiment(numRecords);
        for (int i = 0; i < 100; i++) {
            System.out.println("Iteration " + i);
            for (ExperimentOptions options : allOptions) {
                System.out.println(options);
                ExperimentResults results = experiment.performExperiment(options);
                insertResult(results);
                System.out.println(results + "\n");
            }
        }

    }

    static Duration executeSingleStatement(String command) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/testdb",
                "postgres", "postgres");
             Statement statement = connection.createStatement()) {
            Instant start = Instant.now();
            statement.execute(command);
            Instant end = Instant.now();
            return Duration.between(start, end);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("wrong!");
    }

    static void insertResult(ExperimentResults results) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/testdb",
                "postgres", "postgres");
             PreparedStatement statement =
                     connection.prepareStatement("INSERT INTO experiments VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            statement.setInt(1, results.options.numRecords);
            statement.setInt(2, results.options.physicalOrganization.id);
            statement.setString(3, results.options.keyOrder.toString());
            statement.setFloat(4, results.dataLoadDuration.toMillis() / 1000.0f);
            statement.setFloat(5, results.query1Duration.toMillis() / 1000.0f);
            statement.setFloat(6, results.query2Duration.toMillis() / 1000.0f);
            statement.setFloat(7, results.query3Duration.toMillis() / 1000.0f);
            statement.setFloat(8, results.queryValue);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
