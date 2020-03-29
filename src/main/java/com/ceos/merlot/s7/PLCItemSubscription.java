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

import io.netty.buffer.ByteBufUtil;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.messages.PlcSubscriptionEvent;
import org.apache.plc4x.java.api.messages.PlcSubscriptionRequest;
import org.apache.plc4x.java.api.messages.PlcSubscriptionResponse;
import org.apache.plc4x.java.api.messages.PlcUnsubscriptionRequest;
import org.apache.plc4x.java.api.messages.PlcUnsubscriptionResponse;
import org.apache.plc4x.java.api.model.PlcSubscriptionHandle;
import org.apache.plc4x.java.base.messages.DefaultPlcUnsubscriptionResponse;
import org.apache.plc4x.java.base.messages.InternalPlcUnsubscriptionRequest;
import org.apache.plc4x.java.s7.connection.S7PlcConnection;
import org.apache.plc4x.java.s7.protocol.S7CyclicServicesSubscriptionHandle;

/**
 * Example of cyclical subscription from the PLC.
 * @author cgarcia
 */
public class PLCItemSubscription {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try (PlcConnection plcConnection = new PlcDriverManager().getConnection("s7://192.168.1.23/0/2")){     
            PlcSubscriptionRequest.Builder subsBuilder = plcConnection.subscriptionRequestBuilder();            
            subsBuilder.addCyclicField("PRUEBA1", "%DB85.DB0:SINT[50]", Duration.ofMillis(100));
            subsBuilder.addCyclicField("PRUEBA2", "%DB85.DB200:SINT[50]", Duration.ofMillis(100));
            
            PlcSubscriptionRequest subsRequest = subsBuilder.build();
            PlcSubscriptionResponse subsResponse = subsRequest.execute().get(20, TimeUnit.SECONDS);            
            
            System.out.println("Fields: " + subsResponse.getFieldNames().toString());
            
            S7CyclicServicesSubscriptionHandle handle = null;
            
            
            if (subsResponse.getSubscriptionHandle("PRUEBA1") instanceof S7CyclicServicesSubscriptionHandle) {
                

                /*
                * 
                */
                Consumer<PlcSubscriptionEvent> consumer = (event)->{
                    PlcSubscriptionEvent s7Event = (PlcSubscriptionEvent) event;
                    System.out.println("Ejecuto el consumidor en el cliente..." + s7Event.getFieldNames());
                    System.out.println("Ejecuto el consumidor en el cliente..." + Arrays.toString(s7Event.getByteArray("PRUEBA1")));
                    System.out.println("Ejecuto el consumidor en el cliente..." + Arrays.toString(s7Event.getByteArray("PRUEBA2")));                    
                };
                
                S7PlcConnection s7conn = (S7PlcConnection) plcConnection ;
                                                
                s7conn.register(consumer, subsResponse.getSubscriptionHandles());
                
                handle = (S7CyclicServicesSubscriptionHandle) subsResponse.getSubscriptionHandle("PRUEBA1");
                
                System.out.println("JobId: " + handle.getJobId());
                System.out.println("Error code: " + handle.getError());
                for (int i=0; i<10; i++){
                    if (handle.getError() == 0x0000) {
                        System.out.println("PRUEBA1: " + handle.getFieldName());
                        System.out.println("PRUEBA 1 DATA: " + ByteBufUtil.hexDump(handle.getValueItem().getData()));
                    }
                    Thread.sleep(500);
                };
                            
            }
            
            Thread.sleep(10000);
            
            PlcUnsubscriptionRequest.Builder  unsubsBuilder = plcConnection.unsubscriptionRequestBuilder();
            unsubsBuilder.addHandles((PlcSubscriptionHandle) handle);
            PlcUnsubscriptionRequest unsubrequest = unsubsBuilder.build();
            PlcUnsubscriptionResponse unsubsresponse = (PlcUnsubscriptionResponse) unsubrequest.execute().get(30, TimeUnit.SECONDS);
            
            System.out.println("Se paro la suscripción. Hay que verificar la condición devuelta" + unsubsresponse.toString());
            DefaultPlcUnsubscriptionResponse res = (DefaultPlcUnsubscriptionResponse) unsubsresponse;
            InternalPlcUnsubscriptionRequest peticion = (InternalPlcUnsubscriptionRequest) unsubsresponse.getRequest();  
           
            
            plcConnection.close();
                        
        }   catch (Exception e) {
            e.printStackTrace();
        } 
    }
}
