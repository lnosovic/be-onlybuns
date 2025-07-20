package rs.ac.uns.ftn.informatika.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class Producer {
	
	private static final Logger log = LoggerFactory.getLogger(Producer.class);
	
	/*
	 * RabbitTemplate je pomocna klasa koja uproscava sinhronizovani
	 * pristup RabbitMQ za slanje i primanje poruka.
	 */
	private final RestTemplate restTemplate = new RestTemplate();
	@Autowired
	private RabbitTemplate rabbitTemplate;
	@Value("${message.broker.url}")
	private String brokerUrl;

	/*
	 * U ovom slucaju routingKey ce biti ime queue.
	 * Poruka se salje u exchange (default exchange u ovom primeru) i
	 * exchange ce rutirati poruke u pravi queue.
	 */
	public void sendTo(String routingkey, RabbitCare message){
		log.info("Sending> ... Message=[ " + message.getName() + " ] RoutingKey=[" + routingkey + "]");
		this.rabbitTemplate.convertAndSend(routingkey, message);
	}
	public void sendTo2(String routingkey, RabbitCare message){
		String url = brokerUrl + "/send/" + routingkey;
		log.info("Slanje poruke na URL: " + url + " ... Poruka=[ " + message.getName() + " ]");
		restTemplate.postForEntity(url, message, Void.class);
	}
	
	/*
	 * U ovom slucaju routingKey ce biti ime queue.
	 * Poruka se salje u exchange ciji je naziv prosledjen kao prvi parametar i
	 * exchange ce rutirati poruke u pravi queue.
	 */
	public void sendToExchange(String exchange, String routingkey, String message){
		log.info("Sending> ... Message=[ " + message + " ] Exchange=[" + exchange + "] RoutingKey=[" + routingkey + "]");
		this.rabbitTemplate.convertAndSend(exchange, routingkey, message);
	}
}
