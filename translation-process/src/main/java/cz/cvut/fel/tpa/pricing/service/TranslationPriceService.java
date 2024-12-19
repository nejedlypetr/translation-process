package cz.cvut.fel.tpa.pricing.service;

import cz.cvut.fel.tpa.camunda.model.enums.SourceLanguage;
import cz.cvut.fel.tpa.camunda.model.enums.TargetLanguage;
import cz.cvut.fel.tpa.pricing.dto.request.TranslationPriceRequest;
import cz.cvut.fel.tpa.pricing.dto.response.TranslationPriceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TranslationPriceService {

    private final static Logger LOG = LoggerFactory.getLogger(TranslationPriceService.class);

    public TranslationPriceResponse calculatePrice(TranslationPriceRequest request) {
        LOG.info("Calculating translation price for request: {}", request);

        // Base price per character (in cents)
        double basePrice = calculateBasePrice(request.getSourceLanguage(), request.getTargetLanguage());

        // Calculate total characters (spaces included)
        int characterCount = request.getTranslationText().length();

        // Calculate initial price based on character count
        double price = basePrice * characterCount;

        // Apply urgency multiplier
        double urgencyMultiplier = switch (request.getUrgency()) {
            case normal -> 1.0;
            case express -> 1.5;
        };
        price *= urgencyMultiplier;

        // Apply formality level multiplier
        double formalityMultiplier = switch (request.getFormalityLevel()) {
            case informal -> 1.0;
            case formal -> 1.2;
        };
        price *= formalityMultiplier;

        // Apply target audience multiplier
        double audienceMultiplier = switch (request.getTargetAudience()) {
            case children -> 1.3;
            case teenagers -> 1.2;
            case adults -> 1.0;
            case seniors -> 1.1;
            case professionals -> 1.4;
            case academic -> 1.5;
            case generalPublic -> 1.0;
            case technicalUsers -> 1.3;
        };
        price *= audienceMultiplier;

        // Convert cents to dollars and round to 2 decimal places
        double finalPrice = Math.round(price) / 100.0;

        // Minimum price threshold
        finalPrice = Math.max(finalPrice, 10.0);
        LOG.info("Calculated translation price: {}", finalPrice);

        return new TranslationPriceResponse(finalPrice);
    }

    private double calculateBasePrice(SourceLanguage source, TargetLanguage target) {
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
        return 1.2;
    }
}