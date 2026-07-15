package com.sentinal.monitoring;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "sentinal.monitoring.scheduler-enabled=false")
class MonitoringApplicationTests {

	@Test
	void contextLoads() {
	}

}
