package sysmon.shared;

import java.io.Serializable;
import java.util.ArrayList;

public class ComboResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private ArrayList<MetricResult> metricResults;


    public ComboResult() {
        metricResults = new ArrayList<>();
    }

    public ComboResult(ArrayList<MetricResult> metricResults) {
        this.metricResults = metricResults;
    }

    public ArrayList<MetricResult> getMetricResults() {
        return metricResults;
    }

    public void setMetricResults(ArrayList<MetricResult> metricResults) {
        this.metricResults = metricResults;
    }

    @Override
    public String toString() {
        return metricResults.size() + " results";
    }

}
