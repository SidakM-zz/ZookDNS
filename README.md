# ZookDNS

ZookDNS is an authoritative DNS server intended to be utilized as a service registry or as a DNS server in a closed network. As the name suggests, ZookDNS leverages the sequential and ephemeral node primitives provided by [Zookeeper][zookeeper]. ZookDNS also supports service discovery over [DNS-SD][DNS-SD].

Also see [ZookDNS Agent][zookdnsagent]

## What it does
ZookDNS utilizes Zookeeper to create a service registery which can be queried through DNS requests. This is intended to simplify the service discovery process, since support for the Domain Name System exists for almost all tools and languages. Therefore, there is no additional overhead for clients attempting to discover services besides making a DNS query.

In addition ZookDNS provides a majority of the basic DNS functionality dictated by [RFC-1035][RFC-1035] and introduces the concept of record versioning.

### Discovery Process Overview

Services register themselves by creating PTR, SRV and/or TXT records in ZooKeeper. Since these records are stored in an ephemeral zNodes they will be wiped by ZooKeeper when the connection to the service is lost.

The [ZookDNS Agent][zookdnsagent] provides this functionality out of the box and can be run alongside a service. The agent also registers all records necessary for clients discovering services over [DNS-SD][DNS-SD].

Given the above, clients can either:

1. Follow the DNS-SD protocol and discover the address, port and additional information about a service by querying ZookDNS (1 or 2 queires).

2. Directly query ZookDNS for SRV records and get the service host and address within 1 query (through the additional records section).


## Run

Modify the [properties file][properties-file], and add any [master files][master-files] as needed. Build and run ZookDNS as a Maven project.

All services can utilize the lightweight [ZookDNS Agent][zookdnsagent] to register themselves or alternatively write their own registrar. 

Note: all records are serialized to JSON. However, it is very easy to write your own serialization adapter for both ZookDNS and the the service side agent.

Warning: ZookDNS is very new so do not use it in a production environment. :p

## Next Steps
- [x] align service discovery protocol with [DNS-SD][DNS-SD]
- [ ] Utilizing the versioned records in client responses
- [ ] Adding any remaining functionality for DNS operations

[RFC-1035]:
https://www.ietf.org/rfc/rfc1035.txt
[DNS-SD]:
https://www.ietf.org/rfc/rfc6763.txt
[zookeeper]:
https://zookeeper.apache.org/
[properties-file]:
https://github.com/SidakM/ZookDNS/blob/master/src/main/resources/zookdns.properties
[master-files]:
https://github.com/SidakM/ZookDNS/tree/master/master_files
[zookdnsagent]:
https://github.com/SidakM/ZookDNSAgent
