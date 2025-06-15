# Email Notification Setup Guide

This guide explains how to configure email notifications for the document verification system in Allomed.

## Overview

The email notification system sends automatic emails to doctors when their documents are verified or rejected by administrators. The system uses Spring Boot Mail with SMTP configuration.

## Features

- **Document Verification Emails**: Automatic emails sent when documents are verified
- **Document Rejection Emails**: Automatic emails sent when documents are rejected (with admin comments)
- **Professional Email Templates**: Clean, branded email templates
- **Error Handling**: Graceful handling of email failures without affecting document verification
- **Multiple SMTP Providers**: Support for Gmail, Outlook, and custom SMTP servers

## Configuration

### 1. Environment Variables

Create a `.env` file in the project root with your SMTP configuration:

```env
# Gmail Configuration (Recommended)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=your-email@gmail.com
SMTP_PASSWORD=your-app-password
SMTP_FROM=your-email@gmail.com
SMTP_AUTH=true
SMTP_STARTTLS=true
SMTP_SSL_TRUST=smtp.gmail.com
```

### 2. Gmail Setup (Recommended)

1. **Enable 2-Factor Authentication** on your Gmail account
2. **Generate App Password**:
   - Go to [Google Account Settings](https://myaccount.google.com/)
   - Security → 2-Step Verification → App passwords
   - Generate a new app password for "Mail"
   - Use this password in `SMTP_PASSWORD`

### 3. Alternative SMTP Providers

#### Outlook/Hotmail

```env
SMTP_HOST=smtp-mail.outlook.com
SMTP_PORT=587
SMTP_USERNAME=your-email@outlook.com
SMTP_PASSWORD=your-password
SMTP_FROM=your-email@outlook.com
SMTP_AUTH=true
SMTP_STARTTLS=true
SMTP_SSL_TRUST=smtp-mail.outlook.com
```

#### Custom SMTP Server

```env
SMTP_HOST=your-smtp-server.com
SMTP_PORT=587
SMTP_USERNAME=your-username
SMTP_PASSWORD=your-password
SMTP_FROM=noreply@yourdomain.com
SMTP_AUTH=true
SMTP_STARTTLS=true
SMTP_SSL_TRUST=your-smtp-server.com
```

## Backend Components

### 1. EmailNotificationService

- **Location**: `src/main/java/com/allomed/app/service/EmailNotificationService.java`
- **Purpose**: Handles email composition and sending
- **Features**: Professional email templates, error handling

### 2. KeycloakAdminService Enhancement

- **Location**: `src/main/java/com/allomed/app/service/KeycloakAdminService.java`
- **New Methods**:
  - `getUserEmail(String username)`: Retrieves user email from Keycloak
  - `getUserFullName(String username)`: Retrieves user's full name from Keycloak

### 3. DoctorDocumentResource API

- **New Endpoint**: `POST /api/doctor-documents/{id}/verify`
- **Purpose**: Verifies documents and sends email notifications
- **Request Body**:
  ```json
  {
    "status": "VERIFIED" | "REJECTED",
    "comment": "Optional admin comment"
  }
  ```

## Frontend Components

### Admin Dashboard Enhancement

- **Location**: `src/main/webapp/app/modules/administration/admin-dashboard/admin-dashboard.tsx`
- **Features**:
  - Comment modal for document rejections
  - Email-enabled verification buttons
  - Professional UI with clear action indicators

### Button Labels

- **Verify**: "Verify & Send Email"
- **Reject**: "Reject & Send Email" (opens comment modal)
- **Reset**: "Reset" (immediate action)

## Email Templates

### Verification Email

```
Subject: Document Verification Update - [DOCUMENT_TYPE]

Dear [DOCTOR_NAME],

Good news! Your [DOCUMENT_TYPE] document has been VERIFIED by our admin team.

Your document is now approved and you can continue using our platform services.

If you have any questions, please contact our support team.

Best regards,
Allomed Team
```

### Rejection Email

```
Subject: Document Verification Update - [DOCUMENT_TYPE]

Dear [DOCTOR_NAME],

We regret to inform you that your [DOCUMENT_TYPE] document has been REJECTED.

Please review the document and resubmit with the correct information.

Admin Comment: [ADMIN_COMMENT]

If you have any questions, please contact our support team.

Best regards,
Allomed Team
```

## Testing

### 1. Test Email Configuration

```bash
# Start the application
./gradlew bootRun

# Check logs for email configuration
tail -f logs/spring.log | grep -i mail
```

### 2. Test Document Verification

1. Upload a document as a doctor
2. Login as admin
3. Navigate to Admin Dashboard
4. Verify or reject a document
5. Check doctor's email for notification

### 3. Common Issues

#### Email Not Sending

- Check SMTP credentials in `.env` file
- Verify Gmail app password is correct
- Check firewall/network restrictions
- Review application logs for errors

#### Gmail Authentication Errors

- Ensure 2FA is enabled
- Use app password, not regular password
- Check "Less secure app access" is disabled (use app passwords instead)

## Security Considerations

1. **Environment Variables**: Never commit `.env` file to version control
2. **App Passwords**: Use Gmail app passwords instead of regular passwords
3. **SMTP Credentials**: Store securely and rotate regularly
4. **Email Content**: Avoid sensitive information in email templates

## Monitoring

### Application Logs

```bash
# Monitor email sending
tail -f logs/spring.log | grep EmailNotificationService

# Monitor Keycloak integration
tail -f logs/spring.log | grep KeycloakAdminService
```

### Email Delivery

- Monitor email delivery rates
- Check spam folders for test emails
- Verify email formatting across different clients

## Troubleshooting

### Common Error Messages

1. **"Authentication failed"**

   - Check SMTP username/password
   - Verify app password for Gmail

2. **"Connection refused"**

   - Check SMTP host and port
   - Verify network connectivity

3. **"Could not send email - doctor email not found"**
   - Check Keycloak user has email address
   - Verify Keycloak admin credentials

### Debug Mode

Enable debug logging in `application-dev.yml`:

```yaml
logging:
  level:
    com.allomed.app.service.EmailNotificationService: DEBUG
    org.springframework.mail: DEBUG
```

## Production Deployment

1. **Use Production SMTP Server**: Configure reliable SMTP service
2. **Monitor Email Queue**: Implement email queue monitoring
3. **Backup Configuration**: Backup SMTP configuration securely
4. **Rate Limiting**: Consider email rate limiting for high volume

## Support

For issues with email configuration:

1. Check application logs
2. Verify SMTP provider settings
3. Test with simple email client first
4. Contact system administrator if issues persist
