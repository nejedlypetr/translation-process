package cz.cvut.fel.tpa.pricing.dto.request;

import cz.cvut.fel.tpa.camunda.model.enums.FormalityLevel;
import cz.cvut.fel.tpa.camunda.model.enums.SourceLanguage;
import cz.cvut.fel.tpa.camunda.model.enums.TargetAudience;
import cz.cvut.fel.tpa.camunda.model.enums.TargetLanguage;
import cz.cvut.fel.tpa.camunda.model.enums.Urgency;
import lombok.Data;

@Data
public class TranslationPriceRequest {
    private String translationText;
    private SourceLanguage sourceLanguage;
    private TargetLanguage targetLanguage;
    private FormalityLevel formalityLevel;
    private TargetAudience targetAudience;
    private Urgency urgency;
}
