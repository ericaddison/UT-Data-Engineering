package edu.ut.ede.dataengineering.hw6;

import com.google.common.collect.ImmutableList;

import java.sql.*;

public class ExperimentRunner {

    enum KeyOrder {
        IN_ORDER,
        RANDOM
    }

    public static void main(String[] args) {
        int numRecords = 5000;
        ImmutableList<ImmutableList<String>> indexColumnLists = ImmutableList.of(
                ImmutableList.of(),
                ImmutableList.of("columnA"),
                ImmutableList.of("columnB"),
                ImmutableList.of("columnA", "columnB")
        );

        ImmutableList.Builder<ExperimentOptions> builder = ImmutableList.builder();

        for (ImmutableList<String> indexes : indexColumnLists) {
            for (KeyOrder keyOrder : KeyOrder.values()) {
                builder.add(ExperimentOptions.create(keyOrder, indexes, numRecords));
            }
        }
        ImmutableList<ExperimentOptions> allOptions = builder.build();


        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/testdb",
                "postgres", "postgres")) {

            Statement statement = connection.createStatement();

            // create the experiment table
            statement.execute("CREATE TABLE IF NOT EXISTS experiments (\n" +
                    "numRecords INT,\n" +
                    "indexColumns VARCHAR(50),\n" +
                    "keyOrder VARCHAR(10),\n" +
                    "dataLoadTime REAL,\n" +
                    "queryTime REAL)");

            PreparedStatement resultInsert =
                    connection.prepareStatement("INSERT INTO experiments VALUES (?, ?, ?, ?, ?)");

            for (ExperimentOptions options : allOptions) {
                ExperimentResults results = Experiment.performExperiment(options);
                resultInsert.setInt(1, options.numRecords);
                resultInsert.setString(2, options.indexColumns.toString());
                resultInsert.setString(3, options.keyOrder.toString());
                resultInsert.setFloat(4, results.dataLoadDuration.toMillis()/1000.0f);
                resultInsert.setFloat(5, results.queryDuration.toMillis()/1000.0f);
                resultInsert.execute();
                System.out.println(results);
            }

        }  catch (SQLException e) {
            e.printStackTrace();
        }



    }
}
