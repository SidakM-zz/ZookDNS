package com.sidakmanchanda.zookdns;

import java.net.InetAddress;

public interface DatabaseConfig {
	public InetAddress getDatabaseAddress();
	public int getDatabasePort();
}
