import sysmon.plugins.os_aix.AixProcessorExtension
import sysmon.plugins.os_aix.AixProcessorStat
import spock.lang.Specification

class AixProcessorTest extends Specification {

    void "test AIX lparstat shared output processing"() {

        setup:
        InputStream inputStream = getClass().getResourceAsStream('/lparstat-aix-shared.txt')

        when:
        AixProcessorExtension extension = new AixProcessorExtension()
        AixProcessorStat stats = extension.processCommandOutput(inputStream)

        then:
        stats.getUser() == 83.7f
        stats.getSys() == 3.3f
        stats.getWait() == 0.0f
        stats.getIdle() == 13.0f
        stats.getFields().get("smt") == 8
        stats.getFields().get("ent") == 0.50f
        stats.getFields().get("type") == "Shared"

    }

    void "test AIX lparstat dedicated-donating output processing"() {

        setup:
        InputStream inputStream = getClass().getResourceAsStream('/lparstat-aix-dedicated-donating.txt')

        when:
        AixProcessorExtension extension = new AixProcessorExtension()
        AixProcessorStat stats = extension.processCommandOutput(inputStream)

        then:
        stats.getUser() == 0.1f
        stats.getSys() == 0.2f
        stats.getWait() == 0.0f
        stats.getIdle() == 99.7f
        stats.getFields().get("smt") == 8
        stats.getFields().get("physc") == 0.07f
        stats.getFields().get("type") == "Dedicated"
        stats.getFields().get("mode") == "Donating"

    }

    void "test AIX lparstat dedicated-capped output processing"() {

        setup:
        InputStream inputStream = getClass().getResourceAsStream('/lparstat-aix-dedicated-capped.txt')

        when:
        AixProcessorExtension extension = new AixProcessorExtension()
        AixProcessorStat stats = extension.processCommandOutput(inputStream)

        then:
        stats.getUser() == 0.0f
        stats.getSys() == 0.1f
        stats.getWait() == 0.0f
        stats.getIdle() == 99.9f
        stats.getFields().get("smt") == 4
        stats.getFields().get("type") == "Dedicated"
        stats.getFields().get("mode") == "Capped"

    }

    void "test Linux lparstat output processing"() {

        setup:
        InputStream inputStream = getClass().getResourceAsStream('/lparstat-linux.txt')

        when:
        AixProcessorExtension extension = new AixProcessorExtension()
        AixProcessorStat stats = extension.processCommandOutput(inputStream)

        then:
        stats.getUser() == 0.03f
        stats.getSys() == 0.0f
        stats.getWait() == 0.0f
        stats.getIdle() == 99.97f
        stats.getFields().get("smt") == 8
        stats.getFields().get("ent") == 4.00f
        stats.getFields().get("mode") == "Uncapped"
        stats.getFields().get("type") == "Shared"

    }

}
