package com.training.ec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


    @SpringBootTest
 public class ApplicationTest {

	@Test
	void contextLoads() {
		assertEquals(2, 1+1);
	}
}

