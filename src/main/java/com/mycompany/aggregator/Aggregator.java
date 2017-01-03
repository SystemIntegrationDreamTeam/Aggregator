/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aggregator;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import java.util.ArrayList;

/**
 *
 * @author Buhrkall
 */
public class Aggregator {

    //AggregatorQueue
    private final static String EXCHANGE_NAME = "AggregatorExchange";
    private final static String SENDING_EXCHANGE = "BestQuote";

    private static Gson gson = new Gson();
    private static ArrayList<Aggregate> aggregates = new ArrayList<Aggregate>();

    public static void main(String[] argv) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("datdb.cphbusiness.dk");
        factory.setUsername("Dreamteam");
        factory.setPassword("bastian");
        Connection connection = factory.newConnection();
        Channel listeningChannel = connection.createChannel();
        final Channel sendingChannel = connection.createChannel();
        
        sendingChannel.exchangeDeclare(SENDING_EXCHANGE, "direct");

        listeningChannel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        String queueName = listeningChannel.queueDeclare().getQueue();
        listeningChannel.queueBind(queueName, EXCHANGE_NAME, "");
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        QueueingConsumer consumer = new QueueingConsumer(listeningChannel);
        listeningChannel.basicConsume(queueName, true, consumer);

        QueueingConsumer.Delivery delivery = null;

        while (true) {
            delivery = consumer.nextDelivery(1000);

            if (delivery != null) {
                String message = new String(delivery.getBody());
                System.out.println(" [x] Received '" + message + "'");

                Message msg = gson.fromJson(message, Message.class);

                if (aggregates.isEmpty()) {
                    Aggregate newAggregate = new Aggregate(msg.ssn);
                    newAggregate.addMessage(msg);
                    aggregates.add(newAggregate);
                } else {
                    for (int i = 0; i < aggregates.size(); i++) {

                        try {
                            if (aggregates.get(i).ssn.equals(msg.ssn)) {
                                aggregates.get(i).addMessage(msg);
                                break;
                            } else {
                                Aggregate newAggregate = new Aggregate(msg.ssn);
                                newAggregate.addMessage(msg);
                                aggregates.add(newAggregate);
                                break;
                            }

                        } catch (Exception exception) {
                            System.out.println(exception);
                        }
                    }
                }
            }

            for (int j = 0; j < aggregates.size(); j++) {
                if (aggregates.get(j).checkTime()) {
                    Message resultMessage = aggregates.get(j).getBest();
                    System.out.println(resultMessage);
                    String result = gson.toJson(resultMessage, Message.class);
                    System.out.println(result);
                    
                    sendingChannel.basicPublish(SENDING_EXCHANGE, aggregates.get(j).ssn, null, result.getBytes());

                    
                    boolean deleted = aggregates.remove(aggregates.get(j));
                    System.out.println(deleted);
                    System.out.println("");
                    System.out.println("");
                    System.out.println("");
                    

                    
                    
                    //SEND TO THE EXCHANGE (DIRECT) WITH SSN AS KEY
                    //EXHANGE NAME = BestQuote
                    
                    
                    
                }
            }
        }
    }

}
