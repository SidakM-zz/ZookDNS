package com.sidakmanchanda.zookdns;

import java.net.InetAddress;

public interface ServerConfig {
	public InetAddress getServerAddress();
	public int getServerPort();
	public int getServerThreadPool();
}
