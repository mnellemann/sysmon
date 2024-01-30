# Proxmox Notes

2 minute interval RRD data available.

## Get Nodes

```shell
pvesh get /nodes
```


### Find our local-node



### VMs (qemu)

#### List VMs

```shell
pvesh get /nodes/localhost/qemu
```

#### Get stats for specific VM

```shell
pvesh get /nodes/localhost/qemu/104/rrddata --timeframe hour
```

### LXC (Containers)

#### List LXC

```shell
pvesh get /nodes/localhost/lxc
```

#### Get stats for specific container

```shell
pvesh get /nodes/localhost/lxc/100/rrddata --timeframe hour
```

