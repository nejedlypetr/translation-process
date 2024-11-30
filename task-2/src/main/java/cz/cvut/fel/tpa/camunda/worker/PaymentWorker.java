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

    @JobWorker(type = "calculate-translation-price")
    public Map<String, Double> calculateTranslationPrice(
            @Variable(name = "translationText") String translationText,
            @Variable(name = "sourceLanguage") SourceLanguage sourceLanguage,
            @Variable(name = "targetLanguage") TargetLanguage targetLanguage,
            @Variable(name = "formalityLevel") FormalityLevel formalityLevel,
            @Variable(name = "targetAudience") TargetAudience targetAudience,
            @Variable(name = "urgency") Urgency urgency) {
        LOG.info("Calculating translation price...");

        // Base price per character (in cents)
        double basePrice = calculateBasePrice(sourceLanguage, targetLanguage);

        // Calculate total characters (spaces included)
        int characterCount = translationText.length();

        // Calculate initial price based on character count
        double price = basePrice * characterCount;

        // Apply urgency multiplier
        double urgencyMultiplier = switch (urgency) {
            case normal -> 1.0;
            case express -> 1.5;
        };
        price *= urgencyMultiplier;

        // Apply formality level multiplier
        double formalityMultiplier = switch (formalityLevel) {
            case informal -> 1.0;
            case formal -> 1.2; // Formal texts require more attention
        };
        price *= formalityMultiplier;

        // Apply target audience multiplier
        double audienceMultiplier = switch (targetAudience) {
            case children -> 1.3; // Requires special adaptation
            case teenagers -> 1.2;
            case adults -> 1.0;
            case seniors -> 1.1;
            case professionals -> 1.4; // Requires domain knowledge
            case academic -> 1.5; // Requires highest precision
            case generalPublic -> 1.0;
            case technicalUsers -> 1.3;
        };
        price *= audienceMultiplier;

        // Convert cents to dollars and round to 2 decimal places
        double finalPrice = Math.round(price) / 100.0;

        // Minimum price threshold
        double originalPrice = finalPrice;
        finalPrice = Math.max(finalPrice, 20.0);
        if (originalPrice < 20.0) {
            LOG.info("Price adjusted to minimum threshold: ${} -> ${}", originalPrice, finalPrice);
        }

        LOG.info(
                "Price calculation breakdown: Character count: {}, Base price per character: ${}, Urgency multiplier: {}, Formality multiplier: {}, Audience multiplier: {}, Final translation price: ${}",
                characterCount,
                basePrice / 100.0,
                urgencyMultiplier,
                formalityMultiplier,
                audienceMultiplier,
                finalPrice);

        return Map.of("translationPrice", finalPrice);
    }

    private double calculateBasePrice(SourceLanguage source, TargetLanguage target) {
        // Base price in cents per character for different language combinations
        if (source == SourceLanguage.english && target == TargetLanguage.czech
                || source == SourceLanguage.czech && target == TargetLanguage.english) {
            return 0.8;
        }
        if (source == SourceLanguage.german && target == TargetLanguage.czech
                || source == SourceLanguage.czech && target == TargetLanguage.german) {
            return 1.0;
        }
        if (source == SourceLanguage.english && target == TargetLanguage.german
                || source == SourceLanguage.german && target == TargetLanguage.english) {
            return 0.9;
        }
        // Default price for any other combination
        return 1.2;
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
