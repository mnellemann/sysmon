# Base Plugin

The base plugins uses the [oshi](https://github.com/oshi/oshi) library to get it's metrics.

## Processor Extension

Reports the following metrics seen:

- **system** -CPU time (in ms) spend on system processes.
- **user** - CPU time (in ms) spend on user processes.
- **nice** - CPU time (in ms) spend on user processes running at lower priority.
- **iowait** - CPU time (in ms) spend waiting (for i/o).
- **steal** - CPU time (in ms) stolen by hypervisor and given to other virtual systems.
- **irq** - CPU time (in ms) spend by kernel on interrupt requests.
- **softirq** - CPU (in ms) time spend by kernel on soft interrupt requests.
- **idle** - CPU time (in ms) spend idling (doing nothing).
- **busy** - CPU time (in ms) spend working.


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
- **queue** - Length of disk IO queue.

## Filesystem Extension

### Metrics

- **free_bytes** - Free bytes for filesystem.
- **total_bytes** - Total bytes for filesystem.
- **free_inodes** - Free inodes for filesystem.
- **total_inodes** - Total inodes for filesystem.

### Configuration

```toml
[extension.base_filesystem]
enabled = true
interval = "10s"
exclude_type = [ "tmpfs", "ahafs" ]
exclude_mount = [ "/boot/efi" ]
```

## Process Extension

Reports metrics on one or more running processes.

- **mem_rss** - Resident set memory in bytes.
- **mem_vsz** - Virtual memory in bytes.
- **kernel_time** - Time spent (in milliseconds) in kernel space.
- **user_time** - Time used (in milliseconds) in user space.
- **read_bytes** - Bytes read by process.
- **write_bytes** - Bytes written by process.
- **files** - Files currently open by process.
- **threads** - Running threads.
- **user** - User running the process.
- **group** - Group running the process
- **prio** - Process priority.


### Configuration

The **include** option let's you specify what processes to report for.

```toml
[extension.base_process]
enabled = true # true or false
interval = "10s"
include = [ "java", "influxd", "grafana-server" ]
```
