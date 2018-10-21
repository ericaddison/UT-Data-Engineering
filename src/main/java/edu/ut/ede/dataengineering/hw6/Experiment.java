package edu.ut.ede.dataengineering.hw6;

import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

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


    public static ExperimentResults performExperiment(ExperimentOptions options) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/testdb",
                "postgres", "postgres");
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ROW)) {

            // reset the table
            resetTable(connection);

            // create a list of primary keys
            List<Integer> primaryKeys = new ArrayList<>(options.numRecords);
            for (int i = 0; i < options.numRecords; i++) {
                primaryKeys.add(i);
            }

            // shuffle list if requested
            if (options.keyOrder == ExperimentRunner.KeyOrder.RANDOM) {
                Collections.shuffle(primaryKeys);
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
            addRandomInserts(preparedStatement, primaryKeys);
            preparedStatement.executeBatch();
            connection.commit();
            Instant inOrderEnd = Instant.now();
            Duration dataLoadTime = Duration.between(inOrderStart, inOrderEnd);

            int queryValue = (new Random()).nextInt(50000);

            // query 1
            Instant query1Start = Instant.now();
            statement = connection.createStatement();
            statement.execute("SELECT * FROM benchmark WHERE benchmark.columnA = " + queryValue);
            Instant query1End = Instant.now();
            Duration query1Time = Duration.between(query1Start, query1End);

            // query 2
            Instant query2Start = Instant.now();
            statement = connection.createStatement();
            statement.execute("SELECT * FROM benchmark WHERE benchmark.columnB = " + queryValue);
            Instant query2End = Instant.now();
            Duration query2Time = Duration.between(query2Start, query2End);

            // query 3
            Instant query3Start = Instant.now();
            statement = connection.createStatement();
            statement.execute("SELECT * FROM benchmark WHERE benchmark.columnA = " + queryValue
                    + " AND benchmark.columnB = " + queryValue);
            Instant query3End = Instant.now();
            Duration query3Time = Duration.between(query3Start, query3End);

            // drop indexes
            statement = connection.createStatement();
            if (options.physicalOrganization.columnA) {
                statement.execute("DROP INDEX columna_index");
            }
            if (options.physicalOrganization.columnB) {
                statement.execute("DROP INDEX columnb_index");
            }
            statement.close();

            // create and return results
            return ExperimentResults.create(
                    options,
                    dataLoadTime,
                    query1Time,
                    query2Time,
                    query3Time,
                    queryValue);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        throw new IllegalStateException("Should not get here");
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


}
