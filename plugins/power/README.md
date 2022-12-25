# IBM Power Plugin

## Power LPAR Processor Extension

The processor extension works for both AIX and Linux on the Power ppc64/ppc64le architecture.

Metrics reported are:

- **mode** - Processor mode, Capped or Uncapped
- **type** - Processor type, Shared or Dedicated
- **lcpu** - Number of logical CPU's available for this partition
- **ent** - Processor entitlements available for this partition
- **physc** - Indicates the number of physical processors consumed.
- **entc** - Indicates the percentage of the entitled capacity consumed.
- **lbusy** - Indicates the percentage of logical processor(s) utilization that occurred while executing at the user and system level.
