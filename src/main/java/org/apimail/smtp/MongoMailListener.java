package org.apimail.smtp;

import java.io.IOException;
import java.io.InputStream;

import org.apimail.dao.MongoDAO;
import org.springframework.stereotype.Component;
import org.subethamail.smtp.helper.SimpleMessageListener;

/**
 * Listens to incoming emails and redirects them to the {@code MailSaver} object.
 *
 * @author Nilhcem
 * @since 1.0
 */
@Component
public final class MongoMailListener implements SimpleMessageListener {

    private MongoDAO mongoDao;

    /**
     * Accepts all kind of email <i>(always return true)</i>.
     * <p>
     * Called once for every RCPT TO during a SMTP exchange.<br>
     * Each accepted recipient will result in a separate deliver() call later.
     * </p>
     *
     * @param from
     *            the user who send the email.
     * @param recipient
     *            the recipient of the email.
     * @return always return {@code true}
     */
    @Override
    public boolean accept(String from, String recipient) {
        return true;
    }

    /**
     * Receives emails and forwards them to the {@link MailSaver} object.
     */
    @Override
    public void deliver(String from, String recipient, InputStream data) throws IOException {
        mongoDao.saveMail(from, recipient, data);
    }

    public void setMongoDao(MongoDAO mongoDao) {
        this.mongoDao = mongoDao;
    }
}
