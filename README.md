===============
docker-baseball
===============

Client-server implementation for exploring the 6 consistency semantics in Doug Terry's paper: 
[Replicated Data Consistency Explained Through Baseball](http://research.microsoft.com/pubs/206913/ConsistencyAndBaseballCACMAccepted.pdf)

Quickstart
==========

Compiled classes are already present in bin folder.

To run the compiled classes, go to bin folder.

Open a terminal and run the following commands:

1. rmiregistry &
2. java Clusters.Cluster

open terminal 2
1. java Proxy.ProxyServer (for strong consistency)
2. java Proxy.Proxy_Eventual_Server (for eventual consistency)
3. java Proxy.MonotonicServer (for monotonic reads)
4. java Proxy.StaleBoundServer (for stale bounds)

open terminal 3

1. java Clients.Client (for strong and eventual consistencies)
2. java Clients.MonotonicClient (for monotonic)
3. java Clients.StaleBoundClient (for stalebound)

To rerun the experiments terminate all three process and run again.

Compile and Install
===================

If you want to compile the code, go to the src folder and compile appropriate files

```bash
javac Client.java ../Proxy/Client_Proxy_interface.java
javac Proxy/ProxyServer.java Clusters/Proxy_Cluster_interface.java
javac Clusters/Cluster.java
```

and run the class files from the src directory.
