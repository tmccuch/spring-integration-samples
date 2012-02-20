Cafe Sample Application
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

## Instructions for running the CafeDemo sample

1. The example comes with two identical configurations. One is ANNOTATION-based another is XML-based

2. To run this sample simply execute the CafeDemoApp test classes in the **org.springframework.integration.samples.cafe.xml** or  **org.springframework.integration.samples.cafe.annotation** package.

3. The example also provides an alternative configuration that uses AMQP channels to distribute the components in the **CafeDemo** sample. To run this alternative configuration of the sample, be sure to have a RabbitMQ broker started on localhost:5672 configured with the default guest|guest client credentials on the / vHost, then execute the following test classes in order:
   
   1. **cafeDemoAppBaristaColdAmqp** - starts the Cold Drink Barista
   2. **cafeDemoAppBaristaHotAmqp**  - starts the Hot Drink Barista
   3. **cafeDemoAppAmqp**            - starts the Cafe Storefront (Places 100 orders on the orders queue)
   4. **cafeDemoAppOperationsAmqp**  - starts the Cafe Operations (OrderSplitter, DrinkRouter, PreparedDrinkAggregator)
   
4. The example also provides an alternative configuration that uses RabbitMQ mirrored queues to provide highly available channels between the 
   distributed components in the **CafeDemo** sample. To run this alternative configuration of the sample, be sure to:
   1. pull the AMQP-206a branch from github.com/spring-tom/spring-amqp
   2. pull the INT-2394 branch from github.com/spring-tom/spring-integration
   3. pull the master branch from github.com/spring-tom/rabbitmq-java-client
   4. build the rabbitmq java client by running shell> ant dist from within its base directory
   5. add the rabbitmq-client.jar from the build/dist directory of the rabbitmq java client to the Java Build Path of the Cafe project
   6. start up 2 RabbitMQ brokers, configured with the default guest|guest client credentials on the / vHost
   7. cluster the 2 RabbitMQ brokers following the instructions here: (http://www.rabbitmq.com/clustering.html)
   8. update the addresses element in the rabbitConnectionFactory bean definition within META-INF/spring/integration/ha-amqp/cafeDemo-amqp-config-xml.xml with the IP addresses of the two clustered RabbitMQ brokers
   9. execute the following test classes in order:
      1. **cafeDemoAppBaristaColdAmqp** - starts the Cold Drink Barista
      2. **cafeDemoAppBaristaHotAmqp**  - starts the Hot Drink Barista
      3. **cafeDemoAppAmqp**            - starts the Cafe Storefront (Places 100 orders on the orders queue)
      4. **cafeDemoAppOperationsAmqp**  - starts the Cafe Operations (OrderSplitter, DrinkRouter, PreparedDrinkAggregator)
  10. test the failover of your broker cluster by:
      1. verify the mirroring of the queues by bringing up the RabbitMQ management console on both brokers and looking at the Queues tab
      2. fail the first broker that is mastering all of the mirrored queues for the app while it is processing the 100 orders
      3. verify the mastering of the mirrored queues immediately failed over to the second broker 
      4. verify that all 100 deliveries ultimately get processed by following the count on the all-deliveries queue within the management console Queues tab
      
**Note**: All AMQP exchanges, queues, and bindings needed for this sample are defined within the different xml config files that support the above test classes.
   
Upon running any of the alternatives, you should see the output similar to this:

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