package cz.cvut.fel.tpa.camunda.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;

@Component
public class NotificationWorker {

    private final static Logger LOG = LoggerFactory.getLogger(NotificationWorker.class);

    @JobWorker(type = "send-order-cancelled-notification")
    public void sendOrderCancelledNotification(@Variable(name = "customerEmail") String customerEmail) {
        LOG.info("Order cancelled notification sent to: {}", customerEmail);
    }

    @JobWorker(type = "notify-about-machine-translation-error")
    public void notifyAboutMachineTranslationError() {
        LOG.info("Machine translation error notification sent to admin");
    }
}
