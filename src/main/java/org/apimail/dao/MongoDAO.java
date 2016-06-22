package org.apimail.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDAO.class);

    MongoCollection<Document> coll;

    private MongoClient mongoClient;

    private String database;

    private String collection;

    public void init() {
        MongoDatabase db = mongoClient.getDatabase(database);
        coll = db.getCollection(collection);
    }

    private FindIterable<Document> last(FindIterable<Document> query) {
        BasicDBObject last = new BasicDBObject("$natural", -1);
        // BasicDBObject query = new BasicDBObject("_id", exist);
        return query.limit(1).sort(last);
    }

    public FindIterable<Document> last() {
        return last(find());
    }

    public FindIterable<Document> find() {
        return coll.find();
    }

    public FindIterable<Document> find(String header, String value) {
        BasicDBObject query = new BasicDBObject(header, java.util.regex.Pattern.compile(value));
        return coll.find(query);
    }

    public FindIterable<Document> findLast(String header, String value) {
        return last(find(header, value));
    }

    public void saveMail(String from, String to, InputStream data) {
        Document document = new Document();

        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("New mail from " + from + " to " + to);
            }

            Session s = Session.getDefaultInstance(new Properties());
            MimeMessage message = new MimeMessage(s, data);

            Enumeration<Header> headers = message.getAllHeaders();
            while (headers.hasMoreElements()) {
                Header header = headers.nextElement();
                document.put(header.getName().toLowerCase(), header.getValue());
            }
            document.put("date", new Date().getTime());

            if (message.isMimeType("text/*")) {
                document.put("content", message.getContent());
            } else if (message.isMimeType("multipart/*")) {
                Multipart mp = (Multipart) message.getContent();
                String encoding = message.getEncoding();
                getMultipart(document, mp, encoding);

            } else if (message.isMimeType("message/rfc822")) {
                document.put("content", message.getContent());
            }

            coll.insertOne(document);
        } catch (MessagingException e) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Error while saving mail from " + from + " to " + to, e);
            }
        } catch (IOException e) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Error while getting content from mail from " + from + " to " + to, e);
            }
        }
    }

    private void getMultipart(Document document, Multipart mp, String encoding) throws MessagingException, IOException {
        int count = mp.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart part = mp.getBodyPart(i);
            StringWriter writer = new StringWriter();
            if (part.isMimeType("text/*")) {
                IOUtils.copy(part.getInputStream(), writer, encoding);
                String content = writer.toString();
                String format = "";
                for (String types : part.getHeader("Content-Type")) {
                    for (String type : types.split(";")) {
                        if (type.startsWith("text/")) {
                            format = type.substring("text/".length()).toLowerCase();
                            break;
                        }
                    }
                }
                if (format.equalsIgnoreCase("plain")) {
                    document.put("content", content);
                }
                document.put("content" + format, content);
            } else if (part.isMimeType("multipart/*")) {
                Multipart multipart = (Multipart) part.getContent();
                getMultipart(document, multipart, encoding);
            }
        }
    }

    /* Getter && Setter */

    public void setMongoClient(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }
}
