package mockDatabaseLogger;

import com.thoughtworks.xstream.XStream;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class ArrivaLogger implements Runnable{
	private MessageConsumer	consumer;
	private Session session;
	private Connection connection;

	private int aantalBerichten;
	private int aantalETAs;

	@Override
	public void run() {

		try {
			setupConnection();
			listenForMessages();
			closeConnection();
			System.out.println(this.aantalBerichten + " berichten met " + this.aantalETAs + " ETAs verwerkt.");
		} catch (Exception e) {
			System.out.println("Caught: " + e);
			e.printStackTrace();
		}
	}

	// Setup connection and return the consumer
	private void setupConnection() throws Exception {
		ActiveMQConnectionFactory connectionFactory =
				new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);

		this.connection = connectionFactory.createConnection();
		this.connection.start();
		this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destination = this.session.createQueue("ARRIVALOGGER");

		this.consumer = this.session.createConsumer(destination);
	}

	// Listen for new messages on the queue
	private void listenForMessages() throws Exception {
		this.aantalBerichten = 0;
		this.aantalETAs = 0;
		Message message;

		while (true) {
			message = consumer.receive(2000);
			if (message instanceof TextMessage) {

				Bericht bericht = transformMessage(message);

				this.aantalBerichten++;
				this.aantalETAs+=bericht.ETAs.size();
			} else {
				break;
			}
		}

		System.out.println("Received: " + message);
	}

	// Transform message from message to Bericht
	private Bericht transformMessage(Message message) throws Exception {
		TextMessage textMessage = (TextMessage) message;
		String text = textMessage.getText();

		XStream xstream = new XStream();
		xstream.alias("Bericht", Bericht.class);
		xstream.alias("ETA", ETA.class);
		return (Bericht)xstream.fromXML(text);
	}

	// Close the created connection
	private void closeConnection() throws Exception {
		this.consumer.close();
		this.session.close();
		this.connection.close();
	}
}
