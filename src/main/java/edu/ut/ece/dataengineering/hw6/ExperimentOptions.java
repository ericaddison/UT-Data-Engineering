package edu.ut.ece.dataengineering.hw6;

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
