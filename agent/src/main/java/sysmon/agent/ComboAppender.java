package sysmon.agent;

import sysmon.shared.ComboResult;
import sysmon.shared.MetricResult;

public class ComboAppender {

    public ComboResult append(ComboResult comboResult, MetricResult metricResult) {

        if (comboResult == null) {
            comboResult = new ComboResult();
        }

        comboResult.getMetricResults().add(metricResult);
        return comboResult;
    }

}
