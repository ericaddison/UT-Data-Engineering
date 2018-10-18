package edu.ut.ede.dataengineering.hw6;

import java.sql.*;

public class Play {

    private static final String TABLE_NAME = "benchmark";

    private static final String DROP_TABLE = String.format("DROP TABLE IF EXISTS %s;", TABLE_NAME);

    private static final String CREATE_TABLE = String.format("CREATE TABLE IF NOT EXISTS %s (\n" +
            "theKey INT PRIMARY KEY,\n" +
            "columnA INT,\n" +
            "columnB INT,\n" +
            "filler CHAR(247)\n" +
            ");", TABLE_NAME);

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/testdb",
                "postgres", "postgres")) {

            resetTable(connection);

            PreparedStatement preparedStatement =
                    connection.prepareStatement(
                            String.format("INSERT INTO %s VALUES (?, ?, ?, ?)", TABLE_NAME));
            preparedStatement.setInt(1, 1);
            preparedStatement.setInt(2, 1);
            preparedStatement.setInt(3, 1);
            preparedStatement.setString(4, "lol");
            preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void resetTable(Connection connection) throws SQLException {
        // DROP and CREATE the benchmark table
        Statement statement = connection.createStatement();
        statement.executeUpdate(DROP_TABLE);
        statement.executeUpdate(CREATE_TABLE);
    }

}
