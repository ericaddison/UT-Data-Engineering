package edu.ut.ece.dataengineering.hw6;

import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static edu.ut.ece.dataengineering.hw6.ExperimentRunner.executeSingleStatement;

public class Experiment {

    private static final String TABLE_NAME = "benchmark";

    private static final String DROP_TABLE = String.format("DROP TABLE IF EXISTS %s;", TABLE_NAME);

    private static final String CREATE_TABLE = String.format("CREATE TABLE IF NOT EXISTS %s (\n" +
            "theKey INT PRIMARY KEY,\n" +
            "columnA INT,\n" +
            "columnB INT,\n" +
            "filler CHAR(247)\n" +
            ");", TABLE_NAME);

    private static final String INSERT_ROW = String.format("INSERT INTO %s VALUES (?, ?, ?, ?)", TABLE_NAME);

    private List<Integer> primaryKeys;
    private List<Integer> shuffledKeys;

    Experiment(int numRecords) {
        // create a list of primary keys
        primaryKeys =
                IntStream.rangeClosed(1, numRecords)
                        .boxed()
                        .collect(Collectors.toList());

        shuffledKeys =
                IntStream.rangeClosed(1, numRecords)
                        .boxed()
                        .collect(Collectors.toList());
    }


    public ExperimentResults performExperiment(ExperimentOptions options) {
        int queryValue = (new Random()).nextInt(50000);


        // query 1
        Duration dataLoadTime = rebuildTable(options);
        System.out.println("table build time: " + dataLoadTime.toMillis()/1000.0f);
        Duration query1Time = executeSingleStatement("SELECT * FROM benchmark WHERE benchmark.columnA = " + queryValue);
        System.out.println("Query 1 time: " + query1Time.toMillis()/1000.0f);
        System.out.flush();

        // query 2
        dataLoadTime = rebuildTable(options);
        System.out.println("table build time: " + dataLoadTime.toMillis()/1000.0f);
        Duration query2Time = executeSingleStatement("SELECT * FROM benchmark WHERE benchmark.columnB = " + queryValue);
        System.out.println("Query 2 time: " + query2Time.toMillis()/1000.0f);
        System.out.flush();

        // query 3
        dataLoadTime = rebuildTable(options);
        System.out.println("table build time: " + dataLoadTime.toMillis()/1000.0f);
        Duration query3Time = executeSingleStatement("SELECT * FROM benchmark WHERE benchmark.columnA = " + queryValue
                + " AND benchmark.columnB = " + queryValue);
        System.out.println("Query 3 time: " + query3Time.toMillis()/1000.0f);
        System.out.flush();

        // drop indexes
        if (options.physicalOrganization.columnA) {
            executeSingleStatement("DROP INDEX columna_index");
        }
        if (options.physicalOrganization.columnB) {
            executeSingleStatement("DROP INDEX columnb_index");
        }

        // create and return results
        return ExperimentResults.create(
                options,
                dataLoadTime,
                query1Time,
                query2Time,
                query3Time,
                queryValue);
    }

    private Duration rebuildTable(ExperimentOptions options) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/testdb",
                "postgres", "postgres");
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ROW)) {

            // reset the table
            resetTable(connection);

            List<Integer> myKeys = primaryKeys;

            // shuffle list if requested
            if (options.keyOrder == ExperimentRunner.KeyOrder.RANDOM) {
                myKeys = shuffledKeys;
                Collections.shuffle(shuffledKeys);
            }

            // create requested indexes
            Statement statement = connection.createStatement();
            if (options.physicalOrganization.columnA) {
                statement.execute("CREATE INDEX columna_index ON " + TABLE_NAME + " (columnA)");
            }
            if (options.physicalOrganization.columnB) {
                statement.execute("CREATE INDEX columnb_index ON " + TABLE_NAME + " (columnB)");
            }
            statement.close();

            // build table
            connection.setAutoCommit(false);
            Instant inOrderStart = Instant.now();
            addRandomInserts(preparedStatement, myKeys);
            preparedStatement.executeBatch();
            connection.commit();
            Instant inOrderEnd = Instant.now();
            return Duration.between(inOrderStart, inOrderEnd);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("wrong@!");
    }

    private static void resetTable(Connection connection) throws SQLException {
        // DROP and CREATE the benchmark table
        Statement statement = connection.createStatement();
        statement.executeUpdate(DROP_TABLE);
        statement.executeUpdate(CREATE_TABLE);
    }

    private static void addRandomInserts(PreparedStatement statement, List<Integer> primaryKeys) throws SQLException {

        Random rand = new Random(System.currentTimeMillis());

        for (int key : primaryKeys) {
            statement.setInt(1, key);
            statement.setInt(2, 1 + rand.nextInt(50000));
            statement.setInt(3, 1 + rand.nextInt(50000));
            statement.setString(4, randomString(rand, 20));
            statement.addBatch();
        }
    }

    private static String randomString(Random rand, int length) {
        byte[] buffer = new byte[length];
        rand.nextBytes(buffer);
        return new String(Base64.getEncoder().encode(buffer));
    }

    public static void main(String[] args) {
        ExperimentOptions options =
                ExperimentOptions.create(
                        ExperimentRunner.KeyOrder.IN_ORDER,
                        PhysicalOrganization.BOTH,
                        5000000);

        Experiment exp = new Experiment(5000000);
        exp.rebuildTable(options);
    }
}
