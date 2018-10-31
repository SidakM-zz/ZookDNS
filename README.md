# ZookDNS

ZookDNS is an authoritative DNS server intended to be utilized as a service registry or as a DNS server in a closed network. As the name suggests, ZookDNS leverages the sequential and ephemeral node primitives provided by [Zookeeper][zookeeper].

This project is quite new so it is definitely not meant to be utilized in a production environment in its current state. 

## What it does
ZookDNS provides a service discovery and registry service using Zookeeper. This is intended to simplify the service discovery process, since support for the DNS protocol exists for almost all tools and languages. Therefore, there is no additional overhead for clients attempting to discover services besides making a DNS request.

In addition ZookDNS provides a majority of the Basic DNS functionality dictated by [RFC-1035][RFC-1035] and introduces the concept of record versioning.

### Discovery Process Overview

A service is required to "register" itself by creating an ephemeral node containing an SRV and optionally TXT DNS record in ZooKeeper. Since these records are stored in an ephemeral zNode they will be wiped by ZooKeeper when the connection to the service is lost.

Given this a client can simply request ZookDNS for the SRV and TXT record of the service it is seeking and have sufficient information to establish a connection with this service.

## Next Steps
- [ ] align service discovery protocol with [DNS-SD][DNS-SD]
- [ ] Utilizing the records being saved sequentially in ZooKeeper to return "versioned" records to clients
- [ ] Finish adding remaining functionality for DNS operations

[RFC-1035]:
https://www.ietf.org/rfc/rfc1035.txt
[DNS-SD]:
https://www.ietf.org/rfc/rfc6763.txt
[zookeeper]:
https://zookeeper.apache.org/
