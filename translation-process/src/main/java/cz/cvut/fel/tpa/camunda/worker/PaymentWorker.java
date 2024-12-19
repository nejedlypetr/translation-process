package cz.cvut.fel.tpa.camunda.worker;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import cz.cvut.fel.tpa.camunda.model.enums.SourceLanguage;
import cz.cvut.fel.tpa.camunda.model.enums.FormalityLevel;
import cz.cvut.fel.tpa.camunda.model.enums.TargetAudience;
import cz.cvut.fel.tpa.camunda.model.enums.TargetLanguage;
import cz.cvut.fel.tpa.camunda.model.enums.Urgency;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;

@Component
public class PaymentWorker {

    private final static Logger LOG = LoggerFactory.getLogger(PaymentWorker.class);

    private final ZeebeClient client;

    public PaymentWorker(ZeebeClient client) {
        this.client = client;
    }

    @JobWorker(type = "send-payment-details")
    public void sendPaymentDetails(
            final ActivatedJob job,
            @Variable(name = "translationPrice") Double translationPrice,
            @Variable(name = "customerEmail") String customerEmail) {

        LOG.info("Calculated translation price of ${} and payment details sent to: {}", translationPrice,
                customerEmail);

        mockPaymentReceivedMessage();
    }

    private CompletableFuture<Boolean> mockPaymentReceivedMessage() {
        return CompletableFuture.supplyAsync(() -> {
            // Mock payment logic
            final long delay = 10_000;
            final boolean paymentReceived = true;

            if (!paymentReceived) {
                LOG.info("MOCK PAYMENT: Payment will never be sent");
                return false;
            }

            try {
                // Simulate payment delay
                LOG.info("MOCK PAYMENT: Payment will be sent in {} seconds", delay / 1000);
                Thread.sleep(delay);

                client.newPublishMessageCommand()
                        .messageName("Message_PaymentReceived")
                        .correlationKey("paymentReceived")
                        .send()
                        .join();
                LOG.info("MOCK PAYMENT: Payment received message published");

                return paymentReceived;
            } catch (InterruptedException e) {
                LOG.error("Payment processing was interrupted", e);
                Thread.currentThread().interrupt();
                return false;
            }
        });
    }

}
