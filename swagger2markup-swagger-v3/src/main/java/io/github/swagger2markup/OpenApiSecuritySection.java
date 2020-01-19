package io.github.swagger2markup;

import io.github.swagger2markup.adoc.ast.impl.SectionImpl;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.Section;

import java.util.List;

import static io.github.swagger2markup.OpenApiHelpers.SECTION_TITLE_SECURITY;
import static io.github.swagger2markup.OpenApiHelpers.appendSecurityRequirementTable;

public class OpenApiSecuritySection {

    public static void appendSecurityRequirementSection(Document document, List<SecurityRequirement> securityRequirements) {
        if (null == securityRequirements || securityRequirements.isEmpty()) return;

        Section securityRequirementsSection = new SectionImpl(document);
        securityRequirementsSection.setTitle(SECTION_TITLE_SECURITY);
        appendSecurityRequirementTable(securityRequirementsSection, securityRequirements, false);
        document.append(securityRequirementsSection);
    }
}
