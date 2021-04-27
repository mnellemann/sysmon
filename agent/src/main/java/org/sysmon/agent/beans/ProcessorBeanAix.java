package org.sysmon.agent.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sysmon.shared.MetricBean;
import org.sysmon.shared.MetricResult;

public class ProcessorBeanAix implements MetricBean {

    private final static Logger log = LoggerFactory.getLogger(ProcessorBeanAix.class);


    @Override
    public MetricResult getMetrics() {
        log.warn("TODO: AIX processor stat.");
        return null;
    }

}


/*


# mpstat -v

System configuration: lcpu=8 ent=0.5 mode=Uncapped


vcpu    lcpu    us      sy      wa      id      pbusy          pc             VTB(ms)
----    ----    ----    ----    -----   -----   -----          -----          -------
0               12.26   10.89   0.11    76.74   0.00[ 23.1%]   0.00[  0.0%]  121967
        0       10.58   8.53    0.04    5.71    0.00[ 19.1%]   0.00[ 24.9%]       -
        1       1.32    1.21    0.05    11.16   0.00[  2.5%]   0.00[ 13.7%]       -
        2       0.22    0.28    0.01    8.24    0.00[  0.5%]   0.00[  8.8%]       -
        3       0.11    0.19    0.01    11.63   0.00[  0.3%]   0.00[ 11.9%]       -
        4       0.01    0.10    0.00    8.34    0.00[  0.1%]   0.00[  8.5%]       -
        5       0.00    0.07    0.00    11.69   0.00[  0.1%]   0.00[ 11.8%]       -
        6       0.00    0.13    0.00    8.33    0.00[  0.1%]   0.00[  8.5%]       -
        7       0.01    0.37    0.00    11.63   0.00[  0.4%]   0.00[ 12.0%]       -




# mpstat

System configuration: lcpu=8 ent=0.5 mode=Uncapped

cpu  min  maj  mpc  int   cs  ics   rq  mig lpa sysc us sy wa id   pc  %ec  lcs
  0 1489677 9337 2633 2146943 1160666 30547    3 2951 100 8361624 43 35  0 23 0.00  0.0 1646908
  1 336156 2711  383 266244 25376 5494    0 3401 100 1042507 10  9  0 80 0.00  0.0 230605
  2 45820  829  377 116004 5984 2326    0 1889 100 474631  3  3  0 94 0.00  0.0 117923
  3 46812  699  377 115297 6217 2306    0 1746 100 58549  1  2  0 97 0.00  0.0 117011
  4 2786   39  377 112634 1485 1124    0 1143 100 7432  0  1  0 99 0.00  0.0 114271
  5 1233   45  377 112032 1369 1111    0 1147 100 7591  0  1  0 99 0.00  0.0 113674
  6 25415  238  377 112763 1519 1235    0 1126 100 2403  0  2  0 98 0.00  0.0 114479
  7 3596  124  377 193193 1615 1181    0 1123 100 2572  0  3  0 97 0.00  0.0 195104
  U    -    -    -    -    -    -    -    -   -    -  -  -  0 100 0.50 100.0    -
ALL 1951495 14022 5278 3175110 1204231 45324    3 14526 100 9957309  0  0  0 100 0.00  0.0 2649975


%ec
    (Default, -a flag) The percentage of entitled capacity consumed by the logical processor.
    The %ec of the ALL CPU row represents the percentage of entitled capacity consumed.
    Because the time base over which this data is computed can vary, the entitled capacity
    percentage can sometimes exceed 100%. This excess is noticeable only with small sampling intervals.
    The attribute is displayed only in a shared partition.

 */
