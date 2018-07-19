package com.example.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FundooNotesApplicationTests {

	/*private static final Logger logger = LoggerFactory.getLogger(FundooNotesApplicationTests.class);

	public static void main(String[] args) {
		SpringApplication.run(FundooNotesApplicationTests.class, args);
	}*/
	@Test
	public void contextLoads() {
	}
	/*@Bean
	public CommandLineRunner setup(UserRepository userRepository) {
		return (args) -> {
			userRepository.save(new User("Remove unused imports", true));
			userRepository.save(new User("Clean the code", true));
			userRepository.save(new User("Build the artifacts", false));
			userRepository.save(new User("Deploy the jar file", true));
			logger.info("The sample data has been generated");
		};
	}*/
}
