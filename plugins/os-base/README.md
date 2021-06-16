# Base Plugin

The base plugin uses the [oshi](https://github.com/oshi/oshi) library to get it's metrics.

## Processor Extension

Reports the following metrics seen:

- **system** -CPU time spend on system processes.
- **user** - CPU time spend on user processes.
- **nice** - CPU time spend on user processes running at lower priority.
- **iowait** - CPU time spend waiting (for i/o).
- **steal** - CPU time stolen by hypervisor and given to other virtual systems.
- **irq** - CPU time spend by kernel on interrupt requests.
- **softirq** - CPU time spend by kernel on soft interrupt requests.
- **idle** - CPU time spend idling (doing nothing).
- **busy** - CPU time spend working.


## Memory Extension

Reports the following metrics (in bytes):

- **available** - Estimation of how much memory is available for starting new applications, without swapping.
- **total** - The total amount of (installed) memory.
- **usage** - Percentage of memory used out of the total amount of memory.
- **paged** - ...
- **virtual** - ...


## Disk Extension

Metrics reported are:

- **reads** - The total number of bytes read.
- **writes** - The total number of bytes written.
- **iotime** - Time spent on IO in milliseconds.
- **queue** - Lenght of IO queue.