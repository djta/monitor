package com.ccms.util.jms;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.InvalidClientIDException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

import com.ccms.context.InitializerServlet;

/**
 * @author zhangchuan
 * JMS
 *
 */
public class ReceiveSendJMS {

	private static Logger logger = Logger.getLogger(ReceiveSendJMS.class.getName());
	private static final int count = 5;
	
	public static List<MsgBean> receiveMsg(String destination){
		List<MsgBean> beanList = null;
        ConnectionFactory factory = null;
        Connection connection = null;
        Session session = null;
        MessageConsumer receiver = null;
        Destination dest = null;
        try {
        	String url = InitializerServlet.getContext().getInitParameter("jms-url");
            factory = new ActiveMQConnectionFactory(url);
            connection = factory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            dest = session.createQueue(destination); 
            receiver = session.createConsumer(dest);
            for(int i=0;i<count;i++){
            	Message message = receiver.receive(5000);
                if(message instanceof ObjectMessage) {
                	MsgBean bean = (MsgBean)((ObjectMessage) message).getObject();
                	if(beanList == null){
                		beanList = new ArrayList<MsgBean>();
                	}
                	beanList.add(bean);
                }else{
                	break;
                } 
            }
        } catch (JMSException exception) {
        	logger.error(exception);
        } finally {
            if (session != null) {
                try {
                	session.close();
                } catch (JMSException exception) {
                	logger.error(exception);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException exception) {
                	logger.error(exception);
                }
            }
        }
		return beanList;
	}
	
	public static boolean sendMsg(MsgBean bean, String destination){
		boolean flag = false;
        ConnectionFactory factory = null;
        Connection connection = null;
        Session session = null;
        MessageProducer sender = null;
        Destination dest = null;
        try {
        	String url = InitializerServlet.getContext().getInitParameter("jms-url");
            factory = new ActiveMQConnectionFactory(url);
            connection = factory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            dest = session.createQueue(destination);
            sender = session.createProducer(dest);
            sender.setDeliveryMode(DeliveryMode.PERSISTENT); //设置保存消息
            ObjectMessage message = session.createObjectMessage();
            message.setObject(bean);
            sender.send(message);
            flag = true;
        } catch (JMSException exception) {
        	logger.error(exception);
        } finally {
            if (session != null) {
                try {
                	session.close();
                } catch (JMSException exception) {
                	logger.error(exception);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException exception) {
                	logger.error(exception);
                }
            }
        }
		return flag;
	}
	
	public static List<MsgBean> receiveTopicMsg(String subscriptionName, String topicName){
		List<MsgBean> beanList = null;
        ConnectionFactory factory = null;
        Connection connection = null;
        Topic topic = null;
        Session session = null;
		TopicSubscriber subscriber = null;
        try {
        	String url = InitializerServlet.getContext().getInitParameter("jms-url");
            factory = new ActiveMQConnectionFactory(url);
            connection = factory.createConnection();
            connection.setClientID(subscriptionName);
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            topic = session.createTopic(topicName);
            subscriber = session.createDurableSubscriber(topic, subscriptionName);
            connection.start();
            for(int i=0;i<count;i++){
            	Message message = subscriber.receive(5000);
                if(message instanceof ObjectMessage) {
                	MsgBean bean = (MsgBean)((ObjectMessage) message).getObject();
                	if(beanList == null){
                		beanList = new ArrayList<MsgBean>();
                	}
                	beanList.add(bean);
                }else{
                	break;
                } 
            }
        } catch(InvalidClientIDException e){
        	
        } catch (JMSException exception) {
        	logger.error(exception);
        } finally {
            if (session != null) {
                try {
                	session.close();
                } catch (JMSException exception) {
                	logger.error(exception);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException exception) {
                	logger.error(exception);
                }
            }
        }
		return beanList;
	}
	
	public static boolean sendTopicMsg(MsgBean bean, String subscriptionName, String topicName){
		boolean flag = false;
		
        ConnectionFactory factory = null;
        Connection connection = null;
        Topic topic = null;
        Session session = null;
        MessageProducer sender = null;
        try {
        	String url = InitializerServlet.getContext().getInitParameter("jms-url");
            factory = new ActiveMQConnectionFactory(url);
            connection = factory.createConnection();
            connection.setClientID(subscriptionName);
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            topic = session.createTopic(topicName);
            sender = session.createProducer(topic);
            sender.setDeliveryMode(DeliveryMode.PERSISTENT); //设置保存消息
            connection.start();
            ObjectMessage message = session.createObjectMessage();
            message.setObject(bean);
            sender.send(message);
            flag = true;//发送成功
        } catch(InvalidClientIDException e){
        	
        } catch (JMSException exception) {
        	logger.error(exception);
        } finally {
            if (session != null) {
                try {
                	session.close();
                } catch (JMSException exception) {
                	logger.error(exception);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException exception) {
                	logger.error(exception);
                }
            }
        }
		return flag;
	}
}
