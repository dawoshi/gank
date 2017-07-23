package com.qings.activemq;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

/**
 * Created by qings on 2017/7/23.
 */
@Component
public class MyConnectionFactory implements ConnectionFactory{

    private ConnectionFactory connectionFactory = null;

    @PostConstruct
    private void createConnectionFactory(){
        connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,ActiveMQConnection.DEFAULT_PASSWORD,"tcp://locahost:61616");
    }

    @Override
    public Connection createConnection() throws JMSException {
        return connectionFactory.createConnection();
    }

    @Override
    public Connection createConnection(String s, String s1) throws JMSException {
        return connectionFactory.createConnection(s,s1);
    }
}
