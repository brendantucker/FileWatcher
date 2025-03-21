import java.util.Properties;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import java.io.File;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;

/**
 * Class to send an email with an attachment using JavaMail API.
 */
public final class EmailSender {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587"; 
    private static final String USERNAME = "tcss360w24t6@gmail.com"; 
    private static final String PASSWORD = "uwdx zpfo knls zorm"; 

    /**
     * Send an email with an attachment.
     * @param theRecipient Email recipient
     * @param theSubject Email subject
     * @param theBody Email body
     * @param theFilePath File path of the attachment
     */
    public final static void sendEmailWithAttachment(final String theRecipient, final String theSubject,
            final String theBody, final String theFilePath) {
        // Set up email properties
        final Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);

        // Authenticate using username and password
        final Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            // Check if the file path is null or file doesn't exist
            if (theFilePath == null || theFilePath.isEmpty()) {
                throw new IllegalArgumentException("File path is null or empty.");
            }

            final File file = new File(theFilePath);
            if (!file.exists()) {
                throw new IllegalArgumentException("File does not exist: " + theFilePath);
            }

            // Create email message
            final Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(theRecipient));
            message.setSubject(theSubject);

            // Create a multipart message
            final Multipart multipart = new MimeMultipart();

            // Add email text
            final MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(theBody);
            multipart.addBodyPart(textPart);

            // Add file attachment
            final MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.setDataHandler(new DataHandler(new FileDataSource(file)));
            attachmentPart.setFileName(file.getName());
            multipart.addBodyPart(attachmentPart);

            // Set the complete message parts
            message.setContent(multipart);

            // Send email
            Transport.send(message);
            System.out.println("Email sent successfully with attachment: " + theFilePath);

        } catch (final MessagingException e) { // Catch messaging exceptions
            e.printStackTrace(); // Log the stack trace for further analysis
        } catch (final IllegalArgumentException e) { // Catch illegal argument exceptions
            // Handle invalid file path exception
        } catch (final Exception e) { // Catch any other unexpected exceptions
            // Catch any other unexpected exceptions
            e.printStackTrace(); // Log the stack trace for debugging
        }
    }
}
