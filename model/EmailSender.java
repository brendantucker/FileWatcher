import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import javax.activation.*;

public class EmailSender {

    private static final String SMTP_HOST = "smtp.gmail.com"; // Change if using another provider
    private static final String SMTP_PORT = "587";  // Use 465 for SSL, 587 for TLS
    private static final String USERNAME = "tcss360w24t6@gmail.com"; // Replace with your email
    private static final String PASSWORD = "uwdx zpfo knls zorm";  // Generate an App Password

    public static void sendEmailWithAttachment(String recipient, String subject, String body, String filePath) {
        // Set up email properties
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);
        //uwdx zpfo knls zorm

        // Authenticate using username and password
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            // Create email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);

            // Create a multipart message
            Multipart multipart = new MimeMultipart();

            // Add email text
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(body);
            multipart.addBodyPart(textPart);

            // Add file attachment
            MimeBodyPart attachmentPart = new MimeBodyPart();
            File file = new File(filePath);
            attachmentPart.setDataHandler(new DataHandler(new FileDataSource(file)));
            attachmentPart.setFileName(file.getName());
            multipart.addBodyPart(attachmentPart);

            // Set the complete message parts
            message.setContent(multipart);

            // Send email
            Transport.send(message);
            System.out.println("Email sent successfully with attachment: " + filePath);

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Failed to send email.");
        }
    }
}
