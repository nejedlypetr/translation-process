package cz.cvut.fel.tpa.camunda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.annotation.Deployment;

@SpringBootApplication
@Deployment(resources = {
		"classpath:translation-process.bpmn",
		"classpath:forms/*.form"
})
public class CamundaApplication implements CommandLineRunner {
	private final static Logger LOG = LoggerFactory.getLogger(CamundaApplication.class);

	@Autowired
	private ZeebeClient zeebeClient;

	public static void main(String[] args) {
		SpringApplication.run(CamundaApplication.class, args);
	}

	@Override
	public void run(final String... args) {
		LOG.info("STARTING Camunda Application...");

		final String processId = "nejedpe4-task-1";

		try {
			LOG.info("Creating new instance of process {}", processId);
			zeebeClient.newCreateInstanceCommand()
					.bpmnProcessId(processId)
					.latestVersion()
					.send()
					.join();
			LOG.info("Instance created.");
		} catch (Exception e) {
			LOG.error("Error starting Camunda Application", e);
		}
	}

}
