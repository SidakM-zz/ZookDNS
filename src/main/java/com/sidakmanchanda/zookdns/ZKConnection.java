package com.sidakmanchanda.zookdns;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

public class ZKConnection {
	
	private ZooKeeper zookeeper;
	private static final CountDownLatch latch = new CountDownLatch(1);
	private static final int TIMEOUT = 3000;
	
	/**
	 * Establishes a connection to the provided zookeeper host.
	 * Waits until the connection has been established or the connection thread is interrupted.
	 * 
	 * @param host
	 * @return ZooKeeper
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public ZooKeeper connect(String host) throws IOException, InterruptedException {
		zookeeper = new ZooKeeper(host, TIMEOUT, new Watcher() {

			@Override
			public void process(WatchedEvent arg0) {
				// TODO Auto-generated method stub
				if (arg0.getState() == KeeperState.SyncConnected) {
					latch.countDown();
				}
			}
			
		});
		
		latch.await();
		return zookeeper;
	}
	
	public void close() throws InterruptedException {
		zookeeper.close();
	}
	
}
