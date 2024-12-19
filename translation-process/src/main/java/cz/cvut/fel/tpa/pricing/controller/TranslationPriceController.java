package cz.cvut.fel.tpa.pricing.controller;

import cz.cvut.fel.tpa.pricing.dto.request.TranslationPriceRequest;
import cz.cvut.fel.tpa.pricing.dto.response.TranslationPriceResponse;
import cz.cvut.fel.tpa.pricing.service.TranslationPriceService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pricing")
public class TranslationPriceController {

    private final TranslationPriceService translationPriceService;

    public TranslationPriceController(TranslationPriceService translationPriceService) {
        this.translationPriceService = translationPriceService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<?> calculateTranslationPrice(@RequestBody TranslationPriceRequest request) {
        // Validate that source and target languages are not the same
        if (request.getSourceLanguage().equals(request.getTargetLanguage())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Source and target languages must be different.");
        }

        TranslationPriceResponse response = translationPriceService.calculatePrice(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public String testEndpoint() {
        return "Test successful!";
    }
}
