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

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.messages.PlcWriteRequest;
import org.apache.plc4x.java.api.messages.PlcWriteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cgarcia
 */
public class PLCReadWriteTimes {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(PLCReadWriteTimes.class);
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try (PlcConnection plcConnection = new PlcDriverManager().getConnection("s7://192.168.1.23/0/2")){
            PlcWriteRequest.Builder builder = plcConnection.writeRequestBuilder();
                            
            Duration ms = Duration.ofMillis(50);
            Duration segundos = Duration.ofSeconds(10);
            Duration horas = Duration.ofHours(2);

            builder.addItem("001","%DB111.DBW0:S5TIME", ms);
            builder.addItem("002","%DB111.DBW2:S5TIME", segundos);
            builder.addItem("003","%DB111.DBW4:S5TIME", horas);
            
            Duration[] durations = new Duration[]{ms,segundos,horas};
            builder.addItem("004","%DB111.DBW6:S5TIME[3]", durations);
            
            Duration time = Duration.parse("P24DT20H31M");
            builder.addItem("005","%DB105.DBD20:TIME", time);
            
            Duration[] times = new Duration[]{Duration.parse("P23DT19H30M"),
                                                Duration.parse("P22DT18H29M"),
                                                Duration.parse("P21DT17H28M")};
            builder.addItem("006","%DB105.DBD28:TIME[3]", times);
            
            LocalDate date = LocalDate.of(2003, 4, 17);
            builder.addItem("007","%DB106.DBW14:DATE", date);
            
            LocalDate[] dates = new LocalDate[]{LocalDate.of(2003, 4, 17),
                                                LocalDate.of(2010, 1, 1),
                                                LocalDate.of(2020, 3, 8)};
            builder.addItem("008","%DB106.DBW30:DATE[3]", dates);
            
            LocalTime time01 = LocalTime.of(12, 30);
            LocalTime time02 = LocalTime.of(22,59,15);
            
            builder.addItem("009","%DB107.DBD60:TIME_OF_DAY", time01);
            builder.addItem("010","%DB107.DBD64:TOD", time02);
            
            LocalTime[] tods = new LocalTime[]{LocalTime.of(16, 16),time01,time02};
            builder.addItem("011","%DB107.DBD68:TOD[3]", tods);
            
            LocalDateTime ldt01 = LocalDateTime.now();
            System.out.println("LocalDateTime: " + ldt01 );
            LocalDateTime ldt02 = LocalDateTime.of(2020, Month.MARCH, 9, 00, 54,49,123000000);
            builder.addItem("012","%DB108.DBX48.0:DATE_AND_TIME", ldt01);
            builder.addItem("013","%DB108.DBX56.0:DT", ldt02);
            
            LocalDateTime[] dts = new LocalDateTime[]{ldt01,ldt02,ldt01,ldt02};
            builder.addItem("014","%DB108.DBX64.0:DT[4]", dts);
            
            PlcWriteRequest writeRequest = builder.build();
            
            StopWatch watch = new StopWatch();
            watch.start();
            
            PlcWriteResponse response = writeRequest.execute().get(1, TimeUnit.SECONDS);
            
            watch.stop();
            System.out.println("Duration: " + watch.getTime() +" (msec)");
            
            
            //System.out.println("Response: " + response.getResponseCode("001"));
            //System.out.println("Response %MX200.0:BOOL: " + response.getAllBooleans("001"));
            
            Collection<String> names = response.getFieldNames();

            for (String name:names){
                System.out.println("Index: " + name +
                        " Response code: " + response.getResponseCode(name));
            }
            
            /***********************************************************
             * Now read
             ***********************************************************/
            PlcReadRequest.Builder readBuilder = plcConnection.readRequestBuilder();
            readBuilder.addItem("101","%DB111.DBW0:S5TIME");
            readBuilder.addItem("102","%DB111.DBW2:S5TIME");
            readBuilder.addItem("103","%DB111.DBW4:S5TIME"); 
            readBuilder.addItem("104","%DB111.DBW6:S5TIME[3]");
            
            readBuilder.addItem("105","%DB105.DBD20:TIME");
            readBuilder.addItem("106","%DB105.DBD28:TIME[3]");            
            
            readBuilder.addItem("107","%DB106.DBW14:DATE");
            readBuilder.addItem("108","%DB106.DBW30:DATE[3]");
                        
            readBuilder.addItem("109","%DB107.DBD60:TIME_OF_DAY");
            readBuilder.addItem("110","%DB107.DBD68:TOD[3]");
                        
            readBuilder.addItem("112","%DB108.DBX48.0:DATE_AND_TIME");            
            readBuilder.addItem("113","%DB108.DBX56.0:DT");
            readBuilder.addItem("114","%DB108.DBX64.0:DT[4]");
            
            PlcReadRequest readRequest = readBuilder.build();
            PlcReadResponse readResponse = readRequest.execute().get(1, TimeUnit.SECONDS);
            
            Collection<String> namesResponse = readResponse.getFieldNames();
            
            System.out.println("***********");
            
            for (String name:namesResponse){
                System.out.println("Index: " + name +
                        " Read Response code: " + readResponse.getResponseCode(name));
            }
            
            
            System.out.println("***********");
            System.out.println("101: " + readResponse.getDuration("101"));
            System.out.println("102: " + readResponse.getDuration("102"));
            System.out.println("103: " + readResponse.getDuration("103"));
            System.out.println("104: " + readResponse.getDuration("104",0));
            System.out.println("104: " + readResponse.getDuration("104",1));            
            System.out.println("104: " + readResponse.getDuration("104",2));            
            System.out.println("***********");
            System.out.println("105: " + readResponse.getDuration("105"));
            System.out.println("106: " + readResponse.getDuration("106",0));
            System.out.println("106: " + readResponse.getDuration("106",1));            
            System.out.println("106: " + readResponse.getDuration("106",2));            
            System.out.println("***********");
            System.out.println("107: " + readResponse.getDate("107"));
            System.out.println("108: " + readResponse.getDate("108",0));
            System.out.println("108: " + readResponse.getDate("108",1));
            System.out.println("108: " + readResponse.getDate("108",2));
            System.out.println("***********");
            System.out.println("109: " + readResponse.getTime("109")); 
            System.out.println("110: " + readResponse.getTime("110",0));
            System.out.println("110: " + readResponse.getTime("110",1));
            System.out.println("110: " + readResponse.getTime("110",2));            
            System.out.println("***********");
            System.out.println("112: " + readResponse.getDateTime("112"));            
            System.out.println("113: " + readResponse.getDateTime("113"));
            System.out.println("114: " + readResponse.getDateTime("114",0));
            System.out.println("114: " + readResponse.getDateTime("114",1));
            System.out.println("114: " + readResponse.getDateTime("114",2));
            System.out.println("114: " + readResponse.getDateTime("114",3));            
            System.out.println("***********");
            plcConnection.close();
            
        } catch (Exception ex){
            LOGGER.info("Fail connection. Check IP address, rack, slot: " + ex);
        } 
    }
    
}
