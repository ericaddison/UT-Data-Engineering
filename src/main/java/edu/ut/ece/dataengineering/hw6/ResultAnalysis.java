package edu.ut.ece.dataengineering.hw6;

import com.google.common.collect.ImmutableList;

import java.sql.*;

public class ResultAnalysis {

    enum Variation {I, II}

    private static float getAverage(String column, Variation variation, int physicalOrg) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/testdb",
                "postgres", "postgres")) {

            Statement statement = connection.createStatement();

            ExperimentRunner.KeyOrder keyOrder =
                    variation == Variation.I
                    ? ExperimentRunner.KeyOrder.IN_ORDER
                    : ExperimentRunner.KeyOrder.RANDOM;

            ResultSet results =
                    statement.executeQuery("SELECT AVG(" + column + ") FROM experiments WHERE numrecords=5000000 " +
                    " AND keyorder=\'" + keyOrder + "\'" +
                    " AND physicalorg=" + physicalOrg + ";");
            results.next();
            return results.getFloat(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void main(String args[]) {

        ImmutableList<String> columns = ImmutableList.of(
                "dataloadtime",
                "query1time",
                "query2time",
                "query3time");

        boolean speedup = true;
        float normFactor = 1f;
        float[][] values = new float[4][8];

        int col = 0;
        int row = 0;

        for(int i=0; i<columns.size(); i++) {
            String column = columns.get(i);
            if(speedup) {
                normFactor = getAverage(column, Variation.I, 1);
            }
            col = 2*i;
            for(Variation variation : Variation.values()) {
                if(variation==Variation.II) {
                    col += 1;
                }
                for(int physicalOrg=1; physicalOrg<=4; physicalOrg++) {
                    row = physicalOrg-1;
                    float avg = getAverage(column, variation, physicalOrg);
                    System.out.println(String.format(
                            "Result[%s, %s, %s] = %s sec",
                            column,
                            variation,
                            physicalOrg,
                            avg));
                    values[row][col] = speedup ? normFactor/avg : avg;
                }
            }
            System.out.println();
        }

        // print out latex table
        for(int i=0; i<values.length; i++) {
            System.out.print((i+1) + " & ");
            for(int j=0; j<values[i].length-1; j++) {
                System.out.print(String.format("%8.3f & ", values[i][j]));
            }
            System.out.println(String.format("%8.3f\\\\\n\\hline", values[i][values[i].length-1]));
        }
    }
}
