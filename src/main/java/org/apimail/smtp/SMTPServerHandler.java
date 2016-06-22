package org.apimail.smtp;

import org.apimail.smtp.exception.BindPortException;
import org.apimail.smtp.exception.OutOfRangePortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.server.SMTPServer;

/**
 * Starts and stops the SMTP server.
 *
 * @author Nilhcem
 * @since 1.0
 */
public class SMTPServerHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SMTPServerHandler.class);

    private SMTPServer smtpServer;

    /**
     * Starts the server on the port and address specified in parameters.
     *
     * @param port
     *            the SMTP port to be opened.
     * @param bindAddress
     *            the address to bind to. null means bind to all.
     * @throws BindPortException
     *             when the port can't be opened.
     * @throws OutOfRangePortException
     *             when port is out of range.
     * @throws IllegalArgumentException
     *             when port is out of range.
     */
    public void startServer() throws BindPortException, OutOfRangePortException {
        int port = smtpServer.getPort();
        LOGGER.debug("Starting server on port {}", port);
        try {
            smtpServer.start();
        } catch (RuntimeException exception) {
            if (exception.getMessage().contains("BindException")) { // Can't open port
                LOGGER.error("{}. Port {}", exception.getMessage(), port);
                throw new BindPortException(exception, port);
            } else if (exception.getMessage().contains("out of range")) { // Port out of range
                LOGGER.error("Port {} out of range.", port);
                throw new OutOfRangePortException(exception, port);
            } else { // Unknown error
                LOGGER.error("", exception);
                throw exception;
            }
        }
    }

    /**
     * Stops the server.
     * <p>
     * If the server is not started, does nothing special.
     * </p>
     */
    public void stopServer() {
        if (smtpServer.isRunning()) {
            LOGGER.debug("Stopping server");
            smtpServer.stop();
        }
    }

    /**
     * Returns the {@code SMTPServer} object.
     *
     * @return the {@code SMTPServer} object.
     */
    public SMTPServer getSmtpServer() {
        return smtpServer;
    }

    public void setSmtpServer(SMTPServer smtpServer) {
        this.smtpServer = smtpServer;
    }
}
