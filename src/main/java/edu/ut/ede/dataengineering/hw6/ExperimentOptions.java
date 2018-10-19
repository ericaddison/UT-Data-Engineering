package edu.ut.ede.dataengineering.hw6;

import com.google.common.collect.ImmutableList;

public class ExperimentOptions {
    ExperimentRunner.KeyOrder keyOrder;
    ImmutableList<String> indexColumns;
    int numRecords;

    static ExperimentOptions create(ExperimentRunner.KeyOrder keyOrder, ImmutableList<String> indexColumns, int numRecords) {
        return new ExperimentOptions(keyOrder, indexColumns, numRecords);
    }

    private ExperimentOptions(ExperimentRunner.KeyOrder keyOrder, ImmutableList<String> indexColumns, int numRecords) {
        this.keyOrder = keyOrder;
        this.indexColumns = indexColumns;
        this.numRecords = numRecords;
    }

    @Override
    public String toString() {
        return String.format("ExperimentOptions[%s, %s, %s]",
                numRecords,
                keyOrder,
                indexColumns);
    }
}
