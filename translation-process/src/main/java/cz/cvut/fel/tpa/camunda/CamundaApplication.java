package cz.cvut.fel.tpa.camunda;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import cz.cvut.fel.tpa.camunda.model.enums.FormalityLevel;
import cz.cvut.fel.tpa.camunda.model.enums.SourceLanguage;
import cz.cvut.fel.tpa.camunda.model.enums.TargetAudience;
import cz.cvut.fel.tpa.camunda.model.enums.TargetLanguage;
import cz.cvut.fel.tpa.camunda.model.enums.Urgency;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.annotation.Deployment;

@SpringBootApplication
@Deployment(resources = {
		"classpath:bpmn/*.bpmn",
		"classpath:forms/*.form",
		"classpath:dmn/*.dmn"
})
@ComponentScan(basePackages = { "cz.cvut.fel.tpa" })
public class CamundaApplication implements CommandLineRunner {
	private final static Logger LOG = LoggerFactory.getLogger(CamundaApplication.class);

	@Autowired
	private ZeebeClient client;

	public static void main(String[] args) {
		SpringApplication.run(CamundaApplication.class, args);
	}

	@Override
	public void run(final String... args) {
		LOG.info("STARTING Camunda Application...");

		final String processId = "nejedpe4-translation-process";

		try {
			LOG.info("Creating new instance of process {}", processId);
			client.newCreateInstanceCommand()
					.bpmnProcessId(processId)
					.latestVersion()
					.variables(Map.of(
							"urgency", Urgency.normal,
							"customerEmail", "nejedpe4@fel.cvut.cz",
							"targetLanguage", TargetLanguage.czech,
							"formalityLevel", FormalityLevel.informal,
							"targetAudience", TargetAudience.children,
							"sourceLanguage", SourceLanguage.english,
							"translationText", "Hello world! This is a test translation. Please ignore it. Thank you."))
					.send()
					.join();
			LOG.info("Instance created.");
		} catch (Exception e) {
			LOG.error("Error while starting Camunda Application", e);
		}
	}

}
