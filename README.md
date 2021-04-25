# System Monitor

... or some better name.


## Agent

Runs on your host and collects metrics. Metrics are aggregated and sent to central *collector*.

Metrics are currently processor, memory and disk usage statistics.


## TODO: Collector

Receives aggregated measurements from agents and saves metrics info InfluxDB.