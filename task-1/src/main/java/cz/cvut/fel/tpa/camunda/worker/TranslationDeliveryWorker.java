package cz.cvut.fel.tpa.camunda.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;

@Component
public class TranslationDeliveryWorker {

    private final static Logger LOG = LoggerFactory.getLogger(TranslationDeliveryWorker.class);

    @JobWorker(type = "deliver-translation")
    public void deliverTranslation(@Variable(name = "customerEmail") String customerEmail) {
        LOG.info("Delivering translation...");
        LOG.info("Translation delivered to: {}", customerEmail);
        // TODO: log final translation text result
    }
}
