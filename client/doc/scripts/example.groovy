import sysmon.shared.MetricResult
import sysmon.shared.MetricScript
import sysmon.shared.Measurement

class ExampleScript implements MetricScript {

    @Override
    MetricResult getMetrics() {
        Map<String,String> tags = new TreeMap<>();
        Map<String,Object> fields = new TreeMap<>();

        tags.put("location", "blabla");
        fields.put("temp1", 23);
        fields.put("temp2", 25);

        Measurement measurement = new Measurement(tags, fields);
        return new MetricResult("script_example", measurement);
    }

}
