package com.allomed.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service for sending email notifications for document verification
 */
@Service
public class EmailNotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailNotificationService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@allomed.com}")
    private String fromEmail;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Send document verification email notification
     *
     * @param toEmail The recipient email address
     * @param doctorName The doctor's name
     * @param documentType The type of document (CV, DIPLOMA, etc.)
     * @param verificationStatus The verification status (VERIFIED, REJECTED)
     * @param adminComment Optional admin comment
     */
    public void sendDocumentVerificationEmail(
        String toEmail,
        String doctorName,
        String documentType,
        String verificationStatus,
        String adminComment
    ) {
        LOG.info("=== EMAIL NOTIFICATION START ===");
        LOG.info("Attempting to send email to: {}", toEmail);
        LOG.info("Doctor name: {}", doctorName);
        LOG.info("Document type: {}", documentType);
        LOG.info("Verification status: {}", verificationStatus);
        LOG.info("From email configured as: {}", fromEmail);
        LOG.info("JavaMailSender available: {}", mailSender != null);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);

            // Set more specific subject based on status
            String subject;
            if ("VERIFIED".equals(verificationStatus)) {
                subject = "Document Verified - " + documentType;
            } else if ("REJECTED".equals(verificationStatus)) {
                subject = "Document Rejected - " + documentType;
            } else if ("PENDING".equals(verificationStatus)) {
                subject = "Document Status Reset - " + documentType;
            } else {
                subject = "Document Verification Update - " + documentType;
            }
            message.setSubject(subject);
            String emailContent = buildEmailContent(doctorName, documentType, verificationStatus, adminComment);
            message.setText(emailContent);

            LOG.info("Email message created successfully");
            LOG.info("Email subject: {}", message.getSubject());
            LOG.info("Email from: {}", message.getFrom());
            LOG.info("Email to: {}", java.util.Arrays.toString(message.getTo()));
            LOG.info("Sending email via JavaMailSender...");

            mailSender.send(message);

            LOG.info(
                "✅ SUCCESS: Document verification email sent to: {} for document: {} with status: {}",
                toEmail,
                documentType,
                verificationStatus
            );
        } catch (Exception e) {
            LOG.error("❌ FAILED: Could not send document verification email to: {}", toEmail);
            LOG.error("Error message: {}", e.getMessage());
            LOG.error("Full error details: ", e);
        }

        LOG.info("=== EMAIL NOTIFICATION END ===");
    }

    private String buildEmailContent(String doctorName, String documentType, String verificationStatus, String adminComment) {
        StringBuilder content = new StringBuilder();

        // Improve the doctor name display - avoid "doctor doctor"
        String displayName = doctorName;
        if (displayName != null && displayName.trim().toLowerCase().equals("doctor doctor")) {
            displayName = "Doctor";
        }

        content.append("Dear ").append(displayName != null ? displayName : "Doctor").append(",\n\n");

        if ("VERIFIED".equals(verificationStatus)) {
            content.append("Good news! Your ").append(documentType).append(" document has been VERIFIED by our admin team.\n\n");
            content.append("Your document is now approved and you can continue using our platform services.\n\n");
        } else if ("REJECTED".equals(verificationStatus)) {
            content.append("We regret to inform you that your ").append(documentType).append(" document has been REJECTED.\n\n");
            content.append("Please review the document and resubmit with the correct information.\n\n");

            if (adminComment != null && !adminComment.trim().isEmpty()) {
                content.append("Admin Comment: ").append(adminComment).append("\n\n");
            }
        } else if ("PENDING".equals(verificationStatus)) {
            content.append("Your ").append(documentType).append(" document status has been reset to PENDING.\n\n");
            content.append("This means your document is back under review by our admin team. ");
            content.append("You will receive another notification once the review is completed.\n\n");

            if (adminComment != null && !adminComment.trim().isEmpty()) {
                content.append("Admin Note: ").append(adminComment).append("\n\n");
            }
        }

        content.append("If you have any questions, please contact our support team.\n\n");
        content.append("Best regards,\n");
        content.append("Allomed Team\n\n");
        content.append("---\n");
        content.append("This is an automated message. Please do not reply to this email.");

        return content.toString();
    }
}
