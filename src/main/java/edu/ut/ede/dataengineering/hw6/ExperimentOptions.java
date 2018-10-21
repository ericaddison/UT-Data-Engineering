package edu.ut.ede.dataengineering.hw6;

import com.google.common.collect.ImmutableList;

public class ExperimentOptions {
    ExperimentRunner.KeyOrder keyOrder;
    PhysicalOrganization physicalOrganization;
    int numRecords;

    static ExperimentOptions create(ExperimentRunner.KeyOrder keyOrder, PhysicalOrganization physicalOrganization, int numRecords) {
        return new ExperimentOptions(keyOrder, physicalOrganization, numRecords);
    }

    private ExperimentOptions(ExperimentRunner.KeyOrder keyOrder, PhysicalOrganization physicalOrganization, int numRecords) {
        this.keyOrder = keyOrder;
        this.physicalOrganization = physicalOrganization;
        this.numRecords = numRecords;
    }

    @Override
    public String toString() {
        return String.format("ExperimentOptions[%s, %s, %s]",
                numRecords,
                keyOrder,
                physicalOrganization);
    }
}
