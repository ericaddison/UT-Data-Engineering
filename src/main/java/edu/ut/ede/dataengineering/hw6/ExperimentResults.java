package edu.ut.ede.dataengineering.hw6;

import java.time.Duration;

public class ExperimentResults {
    ExperimentOptions options;
    Duration dataLoadDuration;
    Duration queryDuration;

    static ExperimentResults create(ExperimentOptions options, Duration dataLoadDuration, Duration queryDuration) {
        return new ExperimentResults(options, dataLoadDuration, queryDuration);
    }

    private ExperimentResults(ExperimentOptions options, Duration dataLoadDuration, Duration queryDuration) {
        this.options = options;
        this.dataLoadDuration = dataLoadDuration;
        this.queryDuration = queryDuration;
    }

    @Override
    public String toString() {
        return String.format("ExperimentResults[%s, %s, %s]",
                options,
                dataLoadDuration.toMillis() / 1000.0,
                queryDuration.toMillis() / 1000.0);
    }
}