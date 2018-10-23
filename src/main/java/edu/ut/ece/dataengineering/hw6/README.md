# HW 6

This homework performed database experiments by building a database table of 5,000,000 rows with various physical 
configurations and running queries against it. 

To build and run the program using Gradle, issue the command:
```
gradle runExperiment
```

## Class descriptions

- Experiment: The primary experiment, which contains code to create and query the table.
- ExperimentOptions: The options that define an Experiment, e.g. Data Generator and Indexes.
- ExperimentResults: Container class to hold timing results from an Experiment.
- ExperimentRunner: Class with primary main() method. Runs the experiment.
- PhysicalOrganization: Defines which columns to create indexes for.
- ResultAnalysis: Performs simple analysis on results and prints out latex formatted table entries. 
