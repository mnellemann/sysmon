# Base Plugin

## Processor Extension

Reports the following metrics seen:

- **user** - CPU time spend on user processes.
- **system** -CPU time spend on system processes.
- **iowait** - CPU time spend on waiting (for i/o).
- **idle** - CPU time spend on idle (doing nothing).
- **busy** - CPU time not spend on idle (working).


## Memory Extension

Reports the following metrics, from the *free* command:

- **total** - The total amount of (installed) memory (in KB).
- **used** - Used memory (calculated as total - free - buffers - cache) (in KB).
- **free** - Unused memory (MemFree and SwapFree in /proc/meminfo) (in KB).
- **shared** - Memory used (mostly) by tmpfs (Shmem in /proc/meminfo) (in KB).
- **buffers** - Sum of buffers and cache (in KB).
- **available** - Estimation of how much memory is available for starting new applications, without swapping (in KB).
- **usage** - Percentage of memory used out of the total amount of memory.


## Disk Extension


Only reports first device found. Improvements on the TODO.

Metrics reported are:

- **device** - Name of device.
- **reads** - The total number of KB read.
- **writes** - The total number of KB written.
