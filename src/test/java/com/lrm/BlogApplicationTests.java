package com.lrm;

import com.lrm.dao.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BlogApplicationTests {

	@Autowired
	private UserRepository userRepository;

	@Test
	public void contextLoads() {

	}

}
