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

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cgarcia
 */
public class PLCJira179 {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(PLCJira179.class);
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try (PlcConnection plcConnection = new PlcDriverManager().getConnection("s7://192.168.1.10/0/3")){
            PlcReadRequest.Builder readBuilder = plcConnection.readRequestBuilder();
            
            readBuilder.addItem("StatusBit", "%DB5031.DBW0:WORD");
            readBuilder.addItem("HandshakeStatus", "%DB5031.DB2:BYTE");
            readBuilder.addItem("ZpJahr", "%DB5031.DBW4:INT");
            readBuilder.addItem("ZpMonat", "%DB5031.DBW6:INT");
            readBuilder.addItem("ZpTag", "%DB5031.DBW8:INT");
            readBuilder.addItem("ZpStunde", "%DB5031.DBW10:INT");
            readBuilder.addItem("ZpMinute", "%DB5031.DBW12:INT");
            readBuilder.addItem("ZpSekunde", "%DB5031.DBW14:INT");
            readBuilder.addItem("Reserve", "%DB5031.DB16:DWORD");
            readBuilder.addItem("Identnummer", "%DB5031.DBB20:CHAR[20]");
            readBuilder.addItem("Station", "%DB5031.DBB40:CHAR[10]");
            readBuilder.addItem("Gesamtstatus", "%DB5031.DBW50:INT");
            readBuilder.addItem("DMC1", "%DB5031.DBB52:CHAR[70]"); 
            readBuilder.addItem("PRUEBA", "%DB5031.DBB20:CHAR");             
            
            PlcReadRequest readRequest = readBuilder.build();  
            
            StopWatch watch = new StopWatch();
            watch.start();
            
            PlcReadResponse response = readRequest.execute().get(1, TimeUnit.SECONDS);
            
            watch.stop();
            System.out.println("Duration: " + watch.getTime() +" (msec)");            
            
            
            Collection<String> names = response.getFieldNames();
            
            for (String name:names){
                System.out.println("Index: " + name +
                        " Response code: " + response.getResponseCode(name) +
                        " Number of values: " + ((response.getResponseCode(name)==PlcResponseCode.OK)?
                        ((Integer)(response.getNumberOfValues(name))).toString():response.getResponseCode(name)));
            }
            
            System.out.println("StatusBit [%DB5031.DBW0:WORD]           : " + response.getShort("StatusBit"));
            System.out.println("HandshakeStatus [%DB5031.DB2:BYTE]      : " + response.getByte("HandshakeStatus"));
            System.out.println("ZpJahr [%DB5031.DBW4:INT]               : " + response.getShort("ZpJahr"));
            System.out.println("ZpMonat [%DB5031.DBW6:INT]              : " + response.getShort("ZpMonat"));
            System.out.println("ZpTag [%DB5031.DBW8:INT]                : " + response.getShort("ZpTag"));
            System.out.println("ZpStunde [%DB5031.DBW10:INT]            : " + response.getShort("ZpStunde"));
            System.out.println("ZpMinute [%DB5031.DBW12:INT]            : " + response.getShort("ZpMinute"));
            System.out.println("ZpSekunde [%DB5031.DBW14:INT]           : " + response.getShort("ZpSekunde"));
            System.out.println("Reserve [%DB5031.DB16:DWORD]            : " + response.getInteger("Reserve"));
            System.out.println("Identnummer [%DB5031.DBB20:CHAR[20]]    : " + response.getString("Identnummer"));
            System.out.println("Station [%DB5031.DBB40:CHAR[10]]        : " + response.getString("Station"));
            System.out.println("Gesamtstatus [%DB5031.DBW50:INT]        : " + response.getShort("Gesamtstatus",0));
            System.out.println("DMC1 [%DB5031.DBB52:CHAR[70]]           : "+ response.getString("DMC1",0));
          
            
            plcConnection.close();            
            
        } catch (Exception ex){
            LOGGER.info("Fail connection. Check IP address, rack, slot: " + ex);
        }               
    }
    
}
