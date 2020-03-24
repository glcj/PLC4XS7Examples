/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.s7;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.messages.PlcSubscriptionEvent;
import org.apache.plc4x.java.api.messages.PlcSubscriptionRequest;
import org.apache.plc4x.java.api.messages.PlcSubscriptionResponse;
import org.apache.plc4x.java.s7.connection.S7PlcConnection;
import org.apache.plc4x.java.s7.protocol.event.S7AlarmEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cgarcia
 */
public class PLCEventSubscription {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(PLCEventSubscription.class);
             
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try (PlcConnection plcConnection = new PlcDriverManager().getConnection("s7://192.168.1.10/0/3")){
            S7PlcConnection comm = (S7PlcConnection) plcConnection ;
            
            
            /*
            * Individual constructors for each type of event.
            * USR:  User events.
            * SYS:  System events
            * MODE: PLC operating status.
            * ALM:  Alarm and events, from the PLC or from the user.
            */
            PlcSubscriptionRequest.Builder subsUsr = plcConnection.subscriptionRequestBuilder();
            PlcSubscriptionRequest.Builder subsSys = plcConnection.subscriptionRequestBuilder();
            PlcSubscriptionRequest.Builder subsMode = plcConnection.subscriptionRequestBuilder();
            PlcSubscriptionRequest.Builder subsAlm = plcConnection.subscriptionRequestBuilder();

            /*
            * A single constructor for all events.
            * USR:  User events.
            * SYS:  System events
            * MODE: PLC operating status.
            * ALM:  Alarm and events, from the PLC or from the user.
            */            
            PlcSubscriptionRequest.Builder subsAll = plcConnection.subscriptionRequestBuilder(); 
            
            /*
            * Specifying individual events. 
            */
            subsUsr.addEventField("USR-1", "USR");
            subsSys.addEventField("SYS-1", "SYS");
            subsMode.addEventField("MODE-1", "MODE");             
            subsAlm.addEventField("ALARM-1", "ALM_8");
            
            /*
            * Specifying multiple events for a single subscription.. 
            */            
            subsAll.addEventField("USR-1", "USR");
            subsAll.addEventField("SYS-1", "SYS");
            subsAll.addEventField("MODE-1", "MODE");  
            subsAll.addEventField("ALARM-1", "ALM_S");             

            /*
            * I already build the individual structures. 
            */               
            PlcSubscriptionRequest subsRequestUsr = subsUsr.build();
            PlcSubscriptionRequest subsRequestSys = subsSys.build();
            PlcSubscriptionRequest subsRequestMode = subsMode.build();
            PlcSubscriptionRequest subsRequestAlm = subsAlm.build();

            PlcSubscriptionRequest subsRequestAll = subsAll.build();            
            
            /*
            * Defining the way to consume events.
            */
            
            Consumer<PlcSubscriptionEvent> eventUsrConsumer = (event) -> {
                System.out.println("Consumiendo mensajes USR: " +  event.toString());
            };            
            
            Consumer<PlcSubscriptionEvent> eventSysConsumer = (event) -> {
                System.out.println("Consumiendo mensajes SYS: " + event.toString());                
            };               
            
            Consumer<PlcSubscriptionEvent> eventModeConsumer = (event) -> {
                System.out.println("Consumiendo mensajes MODE: " + event.toString());
            };             
            
            Consumer<PlcSubscriptionEvent> eventAlmConsumer = (event) -> {
                Map<String, Object> map = (Map<String, Object>)event.getObject(S7AlarmEvent.Fields.MAP.name());
                System.out.println("Consumiendo mensajes ALM " + map.toString());
            }; 
            
            Consumer<PlcSubscriptionEvent> eventAllConsumer = (event) -> {
                System.out.println("Consumiendo mensajes object ALL: " + event.toString());
                //System.out.println("EVENT_ID: " + event.getInteger(S7AlarmEvent.Fields.EVENT_ID.toString()));
                Map<String, Object> map = (Map<String, Object>)event.getObject(S7AlarmEvent.Fields.MAP.name());                
                System.out.println("Consumiendo mensajes ALL: "  + map.toString());
            };              
            
            /*
            * Subscribes to the controller.
            */                        
            PlcSubscriptionResponse subsResponseUsr = subsRequestUsr.execute().get(2, TimeUnit.SECONDS);
            PlcSubscriptionResponse subsResponseSys = subsRequestSys.execute().get(2, TimeUnit.SECONDS);
            PlcSubscriptionResponse subsResponseMode = subsRequestMode.execute().get(2, TimeUnit.SECONDS);
            PlcSubscriptionResponse subsResponseAlm = subsRequestAlm.execute().get(2, TimeUnit.SECONDS);
            PlcSubscriptionResponse subsResponseAll = subsRequestAll.execute().get(2, TimeUnit.SECONDS);            
            
            
            /*
            * Check for response status.
            */              
            System.out.println("USR Subscription        : " + subsResponseUsr.getResponseCode("USR-1"));
            System.out.println("SYS Subscription        : " + subsResponseSys.getResponseCode("SYS-1"));
            System.out.println("MODE Subscription       : " + subsResponseMode.getResponseCode("MODE-1"));
            System.out.println("ALARM_X Subscription    : " + subsResponseAlm.getResponseCode("ALARM-1"));
            System.out.println("");
            System.out.println("ALL USR Subscription    : " + subsResponseAll.getResponseCode("USR-1"));
            System.out.println("ALL SYS Subscription    : " + subsResponseAll.getResponseCode("SYS-1"));
            System.out.println("ALL MODE Subscription   : " + subsResponseAll.getResponseCode("MODE-1"));
            System.out.println("ALL ALARM_X Subscription: " + subsResponseAll.getResponseCode("ALARM-1"));            
            System.out.println("");
            
            /*
            * Register my consumers
            */            
            comm.register(eventUsrConsumer, subsResponseUsr.getSubscriptionHandles());
            comm.register(eventSysConsumer, subsResponseSys.getSubscriptionHandles());
            comm.register(eventModeConsumer, subsResponseMode.getSubscriptionHandles());
            comm.register(eventAlmConsumer, subsResponseAlm.getSubscriptionHandles());
            comm.register(eventAllConsumer, subsResponseAll.getSubscriptionHandles()); 
            
            System.out.println("Waiting for events");
            
            /*
            * 
            */
            PlcSubscriptionRequest.Builder queryBuilder = plcConnection.subscriptionRequestBuilder();
            queryBuilder.addEventField("theQuery", "QUERY:ALARM_8");
            PlcSubscriptionRequest queryEvents = queryBuilder.build();
            PlcSubscriptionResponse queryResponse = queryEvents.execute().get(2, TimeUnit.SECONDS);             
            
            System.out.println("QUERY ALARM_S: " + queryResponse.getResponseCode("theQuery"));            
            
            Thread.sleep(90000);
            
            System.out.println("Bye...");
            
            plcConnection.close();
            
        }  catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
}
