package sysmon.plugins.proxmox;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.software.os.OSFileStore;
import sysmon.shared.Measurement;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;
import sysmon.shared.PluginHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Extension
public class ProxmoxStatExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(ProxmoxStatExtension.class);

    // Extension details
    private final String name = "proxmox_stat";
    private final String description = "Proxmox Metrics";

    // Configuration / Options
    private boolean enabled = true;
    private boolean threaded = false;
    private String interval = "120s";
    private List<?> excludeType = new ArrayList<String>() {{
      add("tmpfs");
      add("ahafs");
    }};
    private List<?> excludeMount = new ArrayList<String>() {{
        add("/boot/efi");
    }};

    private List<OSFileStore> fileStores;
    private int refreshCounter = 0;


    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isThreaded() {
        return threaded;
    }

    @Override
    public boolean isSupported() {

        String osName = System.getProperty("os.name").toLowerCase();
        System.err.println("OS NAME: " + osName);
        /* TODO: Check Linux / Proxmox
        if(!osArch.startsWith("ppc64")) {
            log.debug("Requires CPU Architecture ppc64 or ppc64le, this is: " + osArch);
            return false;
        }*/

        if(PluginHelper.notExecutable("pvesh")) {
            log.warn("Requires the 'pvesh' command.");
            return false;
        }

        return true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getInterval() {
        return interval;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setConfiguration(Map<String, Object> map) {
        if (map.containsKey("enabled")) {
            enabled = (boolean) map.get("enabled");
        }

        if(map.containsKey("threaded")) {
            threaded = (boolean) map.get("threaded");
        }

        if(map.containsKey("interval")) {
            interval = (String) map.get("interval");
        }

        if(map.containsKey("exclude_type")) {
            excludeType = (List<?>) map.get("exclude_type");
        }

        if(map.containsKey("exclude_mount")) {
            excludeMount = (List<?>) map.get("exclude_mount");
        }
    }

    @Override
    public MetricResult getMetrics() {


        TreeMap<String, String> tagsMap = null;
        TreeMap<String, Object> fieldsMap = null;

        /*
        try (InputStream buf = PluginHelper.executeCommand("lparstat 3 1")) {
            PowerProcessorStat processorStat = processCommandOutput(buf);
            tagsMap = processorStat.getTags();
            fieldsMap = processorStat.getFields();
        } catch (IOException e) {
            log.error("lparstat error", e);
        }

        log.debug("getMetrics() - tags: {}, fields: {}", tagsMap, fieldsMap);
        return new MetricResult(name, new Measurement(tagsMap, fieldsMap));
        */

        // TODO:

        // List LXC's and QEMU's

        // Loop and get metrics

        // Return

        ArrayList<String> alreadyProcessed = new ArrayList<>();
        ArrayList<Measurement> measurementList = new ArrayList<>();


        for(OSFileStore store : fileStores) {

            String name = store.getName();
            String type = store.getType();
            String mount = store.getMount();

            if(excludeType.contains(type)) {
                log.debug("Excluding type: " + type);
                continue;
            }

            if(excludeMount.contains(mount)) {
                log.debug("Excluding mount: " + mount);
                continue;
            }

            if(alreadyProcessed.contains(name)) {
                log.debug("Skipping name: " + name);
                continue;
            }

            alreadyProcessed.add(name);
            store.updateAttributes();

            TreeMap<String, String> tagsMap = new TreeMap<String, String>() {{
                put("name", name);
                put("type", type);
                put("mount", mount);
            }};

            TreeMap<String, Object> fieldsMap = new TreeMap<String, Object>() {{
                put("free_bytes", store.getFreeSpace());
                put("total_bytes", store.getTotalSpace());
                put("free_inodes", store.getFreeInodes());
                put("total_inodes", store.getTotalInodes());
            }};

            measurementList.add(new Measurement(tagsMap, fieldsMap));
        }

        return new MetricResult(name, measurementList);
    }

}
