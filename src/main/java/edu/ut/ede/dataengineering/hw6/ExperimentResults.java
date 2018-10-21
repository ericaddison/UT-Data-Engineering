package edu.ut.ede.dataengineering.hw6;

import java.time.Duration;

public class ExperimentResults {
    ExperimentOptions options;
    Duration dataLoadDuration;
    Duration query1Duration;
    Duration query2Duration;
    Duration query3Duration;
    int queryValue;

    static ExperimentResults create(
            ExperimentOptions options,
            Duration dataLoadDuration,
            Duration query1Duration,
            Duration query2Duration,
            Duration query3Duration,
            int queryValue) {
        return new ExperimentResults(
                options,
                dataLoadDuration,
                query1Duration,
                query2Duration,
                query3Duration,
                queryValue);
    }

    private ExperimentResults(
            ExperimentOptions options,
            Duration dataLoadDuration,
            Duration query1Duration,
            Duration query2Duration,
            Duration query3Duration,
            int queryValue) {
        this.options = options;
        this.dataLoadDuration = dataLoadDuration;
        this.query1Duration = query1Duration;
        this.query2Duration = query2Duration;
        this.query3Duration = query3Duration;
        this.queryValue = queryValue;
    }

    @Override
    public String toString() {
        return String.format("ExperimentResults[%s, %s, %s, %s, %s]",
                options,
                dataLoadDuration.toMillis() / 1000.0,
                query1Duration.toMillis() / 1000.0,
                query2Duration.toMillis() / 1000.0,
                query3Duration.toMillis() / 1000.0);
    }
}