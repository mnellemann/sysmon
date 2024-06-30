
class ExampleScript implements MetricScript {

    MetricResult getMetrics() {
        Map<String,String> tags = new TreeMap<>();
        Map<String,Object> fields = new TreeMap<>();

        tags.put("type", "temp");
        fields.put("sensor1", 23.2);
        fields.put("sensor2", 25.8);

        Measurement measurement = new Measurement(tags, fields);
        return new MetricResult("script_sensors", measurement);
    }

}



