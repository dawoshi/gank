package com.qings.util;


import org.apache.commons.lang.StringUtils;
import org.assertj.core.util.Preconditions;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import java.util.Properties;


public class MailUtils {

    private static final Logger logger = LoggerFactory.getLogger( MailUtils.class );

    private static final String LOG_PREFIX = "MAIL-DELIVERY-FAILURE - description: [%s] recipient: [%s]";
    private static final String LOG_PREFIX_OK = "MAIL-DELIVERY-SUCCESS - recipient: [%s]";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String MIME_VERSION = "MIME-Version";
    private static final String MAIL_PROP_PREFIX = "mail.";
    private static final String PROP_TRANSPORT = MAIL_PROP_PREFIX + "transport.protocol";


    public void sendMail( Properties props, String to, String from, String subject, String plainText,
                          String htmlText ) {
        Preconditions.checkArgument( StringUtils.isNotBlank( htmlText ) || StringUtils.isNotBlank( plainText ),
                "htmlText and plainText were both blank" );

        try {
            if ( props == null ) {
                props = System.getProperties();
            }

            String protocol = props.getProperty( PROP_TRANSPORT, "smtp" );
            String host = props.getProperty( MAIL_PROP_PREFIX + protocol + ".host" );
            String username = props.getProperty( MAIL_PROP_PREFIX + protocol + ".username" );
            String password = props.getProperty( MAIL_PROP_PREFIX + protocol + ".password" );

            Session session = Session.getInstance( props );

            MimeMultipart msgContent = new MimeMultipart( "alternative" );

            if ( ( htmlText != null ) && ( plainText == null ) ) {
                try {
                    plainText = Jsoup.parse( htmlText ).body().wrap( "<pre></pre>" ).text();
                }
                catch ( Exception e ) {
                    logger.error( "Html parse error", e );
                }
            }

            if ( plainText != null ) {
                MimeBodyPart plainPart = new MimeBodyPart();
                plainPart.setContent( plainText, "text/plain" );
                plainPart.setHeader( MIME_VERSION, "1.0" );
                plainPart.setHeader( CONTENT_TYPE, "text/plain; charset=iso-8859-1" );
                msgContent.addBodyPart( plainPart );
            }

            if ( htmlText != null ) {
                MimeBodyPart htmlPart = new MimeBodyPart();
                htmlPart.setContent( htmlText, "text/html" );
                htmlPart.setHeader( MIME_VERSION, "1.0" );
                htmlPart.setHeader( CONTENT_TYPE, "text/html; charset=iso-8859-1" );
                msgContent.addBodyPart( htmlPart );
            }

            MimeMessage msg = new MimeMessage( session );
            msg.setContent( msgContent );
            msg.setFrom( new InternetAddress( from ) );
            msg.addRecipient( Message.RecipientType.TO, new InternetAddress( to ) );
            msg.setSubject( subject );

            Transport transport = session.getTransport();

            transport.connect( host, username, password );

            transport.sendMessage( msg, msg.getAllRecipients() );
            transport.close();
            logger.info( String.format( LOG_PREFIX_OK, to ) );
        }
        catch ( AddressException ae ) {
            logger.error( createErrorMessage( "could not send to bad address", to ), ae );
        }
        catch ( MessagingException me ) {
            logger.error( createErrorMessage( "could not deliver message", to ), me );
        }
        catch ( Exception e ) {
            logger.error( "General exception delivering mail", to, e );
        }
    }


    public void sendHtmlMail( Properties props, String to, String from, String subject, String html ) {
        sendMail( props, to, from, subject, null, html );
    }


    String createErrorMessage( String message, String toAddress ) {
        return String.format( LOG_PREFIX, message, toAddress );
    }
}
