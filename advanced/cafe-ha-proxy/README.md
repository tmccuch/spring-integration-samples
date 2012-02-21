Cafe HA Proxy Application
=======================

The Cafe sample emulates a simple operation of the Coffee shop when modeled using Enterprise Integration Patterns (EIP). It is inspired by one of the samples featured in Gregor Hohpe's Ramblings. The domain is that of a Cafe, and the basic flow is depicted in the following diagram:


	                                                                                          Barista
					                                                     hotDrinks       ____________________        
	                                                                    |==========| -->|                    |
	                     orders                   drinks               /                | prepareHotDrink()  |
	Place Order ->Cafe->|======|->OrderSplitter->|======|->DrinkRouter                  |                    |
	                                                                   \ coldDrinks     | prepareColdDrink() |
	                                                                    |==========| -->|                    |
	                                                                                    |____________________|
										
														Legend: |====| - channels  
													
                                                                       
The Order object may contain multiple OrderItems. Once the order is placed, a **Splitter** will break the composite order message into a single message per drink. Each of these is then processed by a **Router** that determines whether the drink is hot or cold (checking the OrderItem object's 'isIced' property). The Barista prepares each drink, but hot and cold drink preparation are handled by two distinct methods: 

* prepareHotDrink
* prepareColdDrink

The prepared drinks are then sent to the Waiter where they are aggregated into a Delivery object.

## Instructions for running the Cafe HA Proxy sample

1. The example provides a HTTP endpoint that can be deployed on your local tc Server Dev Edition instance. To deploy the project: 
   * If you are using STS and project is imported as Eclipse project in your workspace you can 
   just execute **Run on Server**
   * You can also run **mvn clean install** and generate the WAR file that you can deploy the 
   conventional way

2. The example provides a configuration that uses RabbitMQ mirrored queues to provide highly available channels between the 
   distributed components in the **CafeDemo** sample. To run this configuration of the sample, be sure to:
   1. pull the AMQP-206a branch from github.com/spring-tom/spring-amqp, cd to home directory and run shell> mvn install -P bootstrap
   2. pull the INT-2394 branch from github.com/spring-tom/spring-integration, cd to spring-integration-amqp directory within the home directory and run shell> ../gradlew install
   3. pull the master branch from github.com/spring-tom/rabbitmq-java-client
   4. build the rabbitmq java client by running shell> ant dist from within its base directory
   5. add the rabbitmq-client.jar from the build/dist directory of the rabbitmq java client to the Java Build Path of the Cafe project
   6. start up 2 RabbitMQ brokers, configured with the default guest|guest client credentials on the / vHost
   7. cluster the 2 RabbitMQ brokers following the instructions here: (http://www.rabbitmq.com/clustering.html)
   8. update the addresses element in the rabbitConnectionFactory bean definition within META-INF/spring/integration/ha-amqp/cafeDemo-amqp-config-xml.xml with the IP addresses of the two clustered RabbitMQ brokers
   9. execute the following test classes in order:
      1. **cafeDemoAppBaristaColdAmqp** - starts the Cold Drink Barista
      2. **cafeDemoAppBaristaHotAmqp**  - starts the Hot Drink Barista
      3. **cafeDemoAppOperationsAmqp**  - starts the Cafe Operations (OrderSplitter, DrinkRouter, PreparedDrinkAggregator)

3. run the simple REST client program: 
   **org.springframework.integration.samples.cafe.CafeDemoAppHttp**
   This will submit the a json order within a HTTP Request to the REST endpoint, which will then reply with an HTTP Response that embeds the json delivery confirmation.
      
**Note**: All AMQP exchanges, queues, and bindings needed for this sample are defined within the different xml config files that support the above test classes.
   
Upon running the REST client, you should see the output similar to this within the cafeDemoAppOperationsAmqp console:

	INFO : org.springframework.integration.samples.cafe.annotation.Barista - task-scheduler-1 prepared cold drink #1 for order #1: iced 3 shot MOCHA
	INFO : org.springframework.integration.samples.cafe.annotation.Barista - task-scheduler-1 prepared cold drink #2 for order #2: iced 3 shot MOCHA
	INFO : org.springframework.integration.samples.cafe.annotation.Barista - task-scheduler-1 prepared cold drink #3 for order #3: iced 3 shot MOCHA
	INFO : org.springframework.integration.samples.cafe.annotation.Barista - task-scheduler-1 prepared cold drink #4 for order #4: iced 3 shot MOCHA
	INFO : org.springframework.integration.samples.cafe.annotation.Barista - task-scheduler-2 prepared hot drink #1 for order #1: hot 2 shot LATTE
	-----------------------
	Order #1
	Iced MOCHA, 3 shots.
	Hot LATTE, 2 shots.
	-----------------------
	INFO : org.springframework.integration.samples.cafe.annotation.Barista - task-scheduler-1 prepared cold drink #5 for order #5: iced 3 shot MOCHA
	INFO : org.springframework.integration.samples.cafe.annotation.Barista - task-scheduler-1 prepared cold drink #6 for order #6: iced 3 shot MOCHA
	INFO : org.springframework.integration.samples.cafe.annotation.Barista - task-scheduler-1 prepared cold drink #7 for order #7: iced 3 shot MOCHA
	INFO : org.springframework.integration.samples.cafe.annotation.Barista - task-scheduler-1 prepared cold drink #8 for order #8: iced 3 shot MOCHA
	INFO : org.springframework.integration.samples.cafe.annotation.Barista - task-scheduler-1 prepared cold drink #9 for order #9: iced 3 shot MOCHA
	INFO : org.springframework.integration.samples.cafe.annotation.Barista - task-scheduler-2 prepared hot drink #2 for order #2: hot 2 shot LATTE
	-----------------------
	Order #2
	Iced MOCHA, 3 shots.
	Hot LATTE, 2 shots.
	-----------------------
   			
Happy integration :-)