/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

package com.ceos.merlot.s7;

import java.time.Instant;
import java.util.Arrays;
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
 * Example of subscription to PLC events (USR, SYS, MODE, ALM_8, ALM_S).
 * @author cgarcia
 */
public class PLCEventSubscription {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(PLCEventSubscription.class);
             
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try (PlcConnection plcConnection = new PlcDriverManager().getConnection("s7://192.168.1.34/0/1")){
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
                byte type = (byte) map.get(S7AlarmEvent.Fields.TYPE.name());
                System.out.println("Type: " + map.get(S7AlarmEvent.Fields.TYPE.name()));
                System.out.println("Event id: " + ((int) map.get(S7AlarmEvent.Fields.EVENT_ID.name())));
                System.out.println("Time stamp: " + ((Instant) map.get(S7AlarmEvent.Fields.TIMESTAMP.name())));
                System.out.println("SIG_1_DATA: " + Arrays.toString(((byte[]) map.get("SIG_1_DATA"))));
                System.out.println("SIG_2_DATA: " + Arrays.toString(((byte[]) map.get("SIG_2_DATA"))));
                System.out.println("SIG_3_DATA: " + Arrays.toString(((byte[]) map.get("SIG_3_DATA"))));
                System.out.println("SIG_4_DATA: " + Arrays.toString(((byte[]) map.get("SIG_4_DATA"))));
                System.out.println("SIG_5_DATA: " + Arrays.toString(((byte[]) map.get("SIG_5_DATA"))));
                System.out.println("SIG_6_DATA: " + Arrays.toString(((byte[]) map.get("SIG_6_DATA"))));
                System.out.println("SIG_7_DATA: " + Arrays.toString(((byte[]) map.get("SIG_7_DATA"))));
                System.out.println("SIG_8_DATA: " + Arrays.toString(((byte[]) map.get("SIG_8_DATA"))));
                System.out.println("SIG_9_DATA: " + Arrays.toString(((byte[]) map.get("SIG_9_DATA"))));
                System.out.println("SIG_10_DATA: " + Arrays.toString(((byte[]) map.get("SIG_10_DATA"))));                
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
            
            Thread.sleep(190000);
            
            System.out.println("Bye...");
            
            plcConnection.close();
            
        }  catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
}
