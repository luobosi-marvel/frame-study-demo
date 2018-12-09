package com.marvel.dyno;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.*;
import java.util.stream.Collectors;

import com.netflix.dyno.queues.redis.DynoShardSupplier;
import org.junit.Test;

import com.netflix.dyno.connectionpool.Host;
import com.netflix.dyno.connectionpool.Host.Status;
import com.netflix.dyno.connectionpool.HostSupplier;

/**
 * @author Viren
 *
 */
public class DynoShardSupplierTest {

	@Test
	public void test(){
		HostSupplier hs = () -> {
			List<Host> hosts = new LinkedList<>();
			hosts.add(new Host("host1", 8102, "us-east-1a", Status.Up));
			hosts.add(new Host("host1", 8102, "us-east-1b", Status.Up));
			hosts.add(new Host("host1", 8102, "us-east-1d", Status.Up));

			return hosts;
		};
		DynoShardSupplier supplier = new DynoShardSupplier(hs, "us-east-1", "a");
		String localShard = supplier.getCurrentShard();
		Set<String> allShards = supplier.getQueueShards();
		
		assertNotNull(localShard);
		assertEquals("a", localShard);
		assertNotNull(allShards);
		assertEquals(new HashSet<>(Arrays.asList("a", "b", "d")), allShards);
	}
}
