package com.sidakmanchanda.zookdns;

import java.net.InetAddress;

public interface ServerConfig {
	public InetAddress getAddress();
	public InetAddress getPort();
	public int getPoolSize();
}
