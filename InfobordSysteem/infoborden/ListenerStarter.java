package infoborden;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public  class ListenerStarter implements Runnable, ExceptionListener {
	private String selector="";
	private Infobord infobord;
	private Berichten berichten;
	
	public ListenerStarter() {
	}
	
	public ListenerStarter(String selector, Infobord infobord, Berichten berichten) {
		this.selector=selector;
		this.infobord=infobord;
		this.berichten=berichten;
	}

	public void run() {
        try {
            ActiveMQConnectionFactory connectionFactory = 
            		new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
//			TODO maak de connection aan
          	Connection connection = connectionFactory.createConnection();
          	connection.start();
          	connection.setExceptionListener(this);
//			TODO maak de session aan
          	Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//			TODO maak de destination aan
			Destination destination = session.createQueue(this.selector); // halteNaam + Richting
//			TODO maak de consumer aan
          	MessageConsumer consumer = session.createConsumer(destination);
            System.out.println("Produce, wait, consume"+ selector);
//			TODO maak de Listener aan
          	consumer.setMessageListener(new QueueListener(this.selector, this.infobord, this.berichten));
        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    public synchronized void onException(JMSException ex) {
        System.out.println("JMS Exception occured.  Shutting down client.");
    }
}