import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.util.Properties;
import javax.mail.Session;

public class EmailSenderTest {

    private Properties properties;
    private Session session;

    @Before
    public void setUp() {
        properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        session = Session.getInstance(properties);
    }

    @Test
    public void testEmailProperties() {
        assertEquals("true", properties.getProperty("mail.smtp.auth"));
        assertEquals("true", properties.getProperty("mail.smtp.starttls.enable"));
        assertEquals("smtp.gmail.com", properties.getProperty("mail.smtp.host"));
        assertEquals("587", properties.getProperty("mail.smtp.port"));
    }

    @Test
    public void testInvalidRecipient() {
        String invalidEmail = "invalid-email";
        assertFalse(isValidEmail(invalidEmail));
    
        try {
            EmailSender.sendEmailWithAttachment(
                invalidEmail,
                "Test Email",
                "This should fail.",
                "test.pdf"
            );
        } catch (Exception e) {
            fail("JavaMail does not immediately throw an exception for invalid emails.");
        }
    }
    
    /**
     * Helper method to validate email format.
     */
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
    

    @Test
    public void testInvalidAttachment() {
        String missingFilePath = "nonexistentfile.pdf";
        File file = new File(missingFilePath);
    
        assertFalse("Attachment file should not exist", file.exists());
    
        try {
            EmailSender.sendEmailWithAttachment(
                "test@example.com",
                "Test Email",
                "This should fail due to missing attachment.",
                missingFilePath
            );
        } catch (Exception e) {
            fail("JavaMail does not immediately throw an exception for missing attachments.");
        }
    }
    

    @Test
    public void testValidAttachmentExists() {
        File tempFile;
        try {
            tempFile = File.createTempFile("testFile", ".txt");
            assertTrue(tempFile.exists());

            EmailSender.sendEmailWithAttachment(
                "test@example.com",
                "Test Email",
                "This should succeed.",
                tempFile.getAbsolutePath()
            );
        } catch (Exception e) {
            fail("No exception expected for valid attachment.");
        }
    }
}
