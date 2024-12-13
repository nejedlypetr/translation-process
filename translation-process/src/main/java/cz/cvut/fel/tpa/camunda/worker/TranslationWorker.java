package cz.cvut.fel.tpa.camunda.worker;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;

@Component
public class TranslationWorker {

    private final static Logger LOG = LoggerFactory.getLogger(TranslationWorker.class);

    @JobWorker(type = "deliver-translation")
    public void deliverTranslation(@Variable(name = "customerEmail") String customerEmail) {
        LOG.info("Translation delivered to: {}", customerEmail);
    }

    @JobWorker(type = "process-machine-translation-result")
    public Map<String, String> processMachineTranslationResult(
            @Variable(name = "openAiResponse") String openAiResponse) {
        LOG.info("Save machine translation.");
        return Map.of("translatedText", openAiResponse);
    }
}