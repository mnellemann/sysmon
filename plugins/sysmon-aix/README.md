# AIX Plugin

## Processor Extension

The processor extension works for both AIX and Linux on the Power ppc64/ppc64le architecture.

Metrics reported are:

- **mode** - Processor mode, Capped or Uncapped 
- **type** - Processor type, Shared or Dedicated
- **lcpu** - Number of logical CPU's available for this partition
- **ent** - Processor entitlements available for this partition
- **user** - Indicates the percentage of the entitled processing capacity used while executing at the user level (application).
- **sys** - Indicates the percentage of the entitled processing capacity used while executing at the system level (kernel).
- **idle** - Indicates the percentage of the entitled processing capacity unused while the partition was idle and did not have any outstanding disk I/O request. 
- **wait** - Indicates the percentage of the entitled processing capacity unused while the partition was idle and had outstanding disk I/O request(s). 
- **physc** - Indicates the number of physical processors consumed.
- **entc** - Indicates the percentage of the entitled capacity consumed.
- **lbusy** - Indicates the percentage of logical processor(s) utilization that occurred while executing at the user and system level. 


## Memory Extension

Metrics reported are:

- **total** - Total amount of memory (in KB).
- **used** - real memory consumption (in KB).
- **free** - free memory for use (in KB).
- **pin** - pinned memory consumption (in KB).
- **virtual** - virtual memory consumption (in KB).
- **available** - available memory (if freeing up virtual) (in KB).
- **paged** -  paging space consumption (in KB).

*Pinning a memory region prohibits the pager from stealing pages from the pages backing the pinned memory region.*

## Disk Extension

Only reports first device found. Improvements on the TODO.

Metrics reported are:

- **device** - Name of device.
- **reads** - The total number of KB read.
- **writes** - The total number of KB written.
