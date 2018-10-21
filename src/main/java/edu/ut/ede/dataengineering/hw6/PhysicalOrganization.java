package edu.ut.ede.dataengineering.hw6;

import java.sql.*;

public enum PhysicalOrganization {

    NONE(1, false, false),
    COLUMN_A(2, true, false),
    COLUMN_B(3, false, true),
    BOTH(4, true, true);

    int id;
    boolean columnA;
    boolean columnB;

    PhysicalOrganization(int id, boolean columnA, boolean columnB) {
        this.id = id;
        this.columnA = columnA;
        this.columnB = columnB;
    }

    public static void main(String args[]) {
        setupPhysicalOrgTable();
    }

    private static void setupPhysicalOrgTable() {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/testdb",
                "postgres", "postgres")) {

            Statement statement = connection.createStatement();

            // create the experiment table
            statement.execute("CREATE TABLE IF NOT EXISTS physicalorg (\n" +
                    "id INT PRIMARY KEY,\n" +
                    "columnA BOOL,\n" +
                    "columnB BOOL)");
            statement.close();

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO physicalorg VALUES (?, ?, ?)");


            for (PhysicalOrganization org : PhysicalOrganization.values()) {
                preparedStatement.setInt(1, org.id);
                preparedStatement.setBoolean(2, org.columnA);
                preparedStatement.setBoolean(3, org.columnB);
                preparedStatement.execute();
            }
            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int boolToInt(boolean bool) {
        return bool ? 1 : 0;
    }
}
