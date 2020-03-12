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
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.messages.PlcWriteRequest;
import org.apache.plc4x.java.api.messages.PlcWriteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cgarcia
 */
public class PLCWriteField {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(PLCWriteField.class);
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
                
        try (PlcConnection plcConnection = new PlcDriverManager().getConnection("s7://192.168.1.23/0/2")){
            PlcWriteRequest.Builder builder = plcConnection.writeRequestBuilder();
            
            /****************************************************************
             * Test for S7300/S7400
             * Elementary Data Types
             * Bit Data Types
             ****************************************************************/            
            builder.addItem("001","%MX10.0:BOOL", true);
            builder.addItem("002","%MB3:BYTE", (byte) 0x12);
            builder.addItem("003","%MW4:WORD", (short) 0x4321);
            builder.addItem("004","%MD6:DWORD", (int) 0x87654321);

            /****************************************************************
             * Character Types
             ****************************************************************/            
            char letra='9';
            builder.addItem("005","%MB10:CHAR", letra);
            
            /****************************************************************
             * Numeric Types
            ****************************************************************/            
            builder.addItem("006","%MW12:INT", (short) 2020);
            builder.addItem("007","%MD14:DINT", (int) 20031970);
            builder.addItem("008","%MD18:REAL", (float) 3.141516);

            /****************************************************************
             * Time Types
             ****************************************************************/
            
            /****************************************************************
             * TIME: TIME#-24d20h31m23s648ms / TIME#24d20h31m23s647ms
             *                         -2^31 / 2^31-1
             * The content is represented  as milliseconds (ms), saved as (int)
             * TODO: Change S7 TIME to Integer not LocalTime
             ****************************************************************/            
            Duration time = Duration.parse("P24DT20H31M");           
            builder.addItem("009","%MD22:TIME", time);
                                    
            /****************************************************************
             * DATE: DATE#1990-01-01 / DATE#2168-12-31 
             *              (=W#16#0000) / (=W#16#FF62)                   
             * The content is the number of days since 01.01.1990
             ****************************************************************/            
            LocalDate date = LocalDate.of(2003, 4, 17);
            builder.addItem("010","%MW26:DATE", date);
                   
            /****************************************************************
             * TIME_OF_DAY: TIME_OF_DAY#00:00:00 / TIME_OF_DAY#23:59:59.999
             *               (=DW#16#0000_0000) / (=DW#16#0526_5BFF)
             ****************************************************************/            
            LocalTime time01 = LocalTime.of(12, 30);
            builder.addItem("011","%MD28:TIME_OF_DAY", time01);
            
            /****************************************************************
             * Test for S7300/S7400
             * Complex Data Types
             ****************************************************************/    
            LocalDateTime dt = LocalDateTime.now();
            builder.addItem("012","%MX32.0:DT", dt);
            builder.addItem("013","%MX40.0:DATE_AND_TIME", dt); 
            
            /**************************************************************** 
             * STRING: "BARCELONA_VENEZUELA"
             * Byte no           Range
             *    n*         Maximum length (k)
             *   n+1         Current length (m)
             *   n+2         1st character \         \
             *   n+3         2nd character  | current | Maximum
             *   ...                        | length  | length (k)
             *   m+m+1       m-th character/          >
             *   ...                                  |
             *   n+k+1       ...                     /     *n = even
             ****************************************************************/                        
            builder.addItem("014","%MB100:STRING", "BARCELONA_VENEZUELA");

            /****************************************************************
             * ARRAYS
             ****************************************************************/
            Boolean[] booleanArray = new Boolean[]{true,false,true,true,false,true,true,false,true,true,true,false};
            builder.addItem("015","%DB100.DBX0.0:BOOL[12]", booleanArray);//TO DBX1.3
            builder.addItem("016","%DB100.DBX1.4:BOOL[12]", booleanArray);//TO DB2.7 
            builder.addItem("017","%DB100.DBX3.0:BOOL[12]", booleanArray);//TO DBX4.3
            
            Byte[] byteArray = new Byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06};
            builder.addItem("018","%DB101.DB0:BYTE[6]", byteArray);
            builder.addItem("019","%DB101.DBB6:BYTE[6]", byteArray);//DBB is Valid too
            builder.addItem("020","%DB101.DB12:BYTE[6]", byteArray);
            
            Short[] shortArray = new Short[]{0x1111, 0x2222, 0x3333, 0x4444, 0x5555, 0x6666, 0x7777};            
            builder.addItem("021","%DB102.DBW0:WORD[7]", shortArray);
            builder.addItem("022","%DB102.DBW14:WORD[7]", shortArray);
            builder.addItem("023","%DB102.DBW28:WORD[7]", shortArray);
                      
            builder.addItem("024","%DB102.DBW42:INT[7]", shortArray);   
            
            Integer[] intArray = new Integer[]{0x6666_7777, 0x2222_3333, 0x5555_6666, 0x1234_4444, 0x2222_5555, 0x1212_6666, 0xAAAA_7777};
            builder.addItem("025","%DB103.DBD12:DINT[7]", intArray); 
            builder.addItem("026","%DB103:DBD40:DINT[7]", intArray);
            builder.addItem("027","%DB103:DBD68:DINT[7]", intArray); 
            //INVALID_ADDRESS for test
            builder.addItem("028","%DB103:DBD460:DINT[7]", intArray);
            
            Float[] floatArray = new Float[]{(float)3.1416, (float)1.4142, (float)2.7182, (float)0.5963, (float)2.5029, (float)0.3183, (float)1.2599}; 
            builder.addItem("029","%DB104:DBD4:REAL[7]", floatArray); 
            builder.addItem("030","%DB104:DBD44:REAL[7]", floatArray);
            builder.addItem("031","%DB104:DBD120:REAL[7]", floatArray);
            //INVALID_ADDRESS for test
            builder.addItem("032","%DB104:DBD500:REAL[7]", floatArray);
            
            
            Duration[] timeArray = new Duration[]{Duration.parse("P23DT19H30M"),
                                                Duration.parse("P22DT18H29M"),
                                                Duration.parse("P21DT17H28M")};
            builder.addItem("033","%DB105.DBD20:TIME[3]", timeArray); 
            builder.addItem("034","%DB105.DBD32:TIME[3]", timeArray);
            builder.addItem("035","%DB105.DBD44:TIME[3]", timeArray);
            
            LocalDate[] dateArray = new LocalDate[]{LocalDate.of(2003, 4, 17),
                                                LocalDate.of(2010, 1, 1),
                                                LocalDate.of(2020, 3, 8)};
            builder.addItem("036","%DB106.DBW8:DATE[3]", dateArray);
            builder.addItem("037","%DB106.DBW14:DATE[3]", dateArray);
            builder.addItem("038","%DB106.DBW20:DATE[3]", dateArray);
            
            LocalTime tod01 = LocalTime.of(12, 30);
            LocalTime tod02 = LocalTime.of(22,59,15);
            
            LocalTime[] todArray = new LocalTime[]{tod01, tod02, tod01};
            
            builder.addItem("039","%DB107.DBD20:TIME_OF_DAY[3]", todArray);
            builder.addItem("040","%DB107.DBD32:TOD[3]", todArray);
            builder.addItem("041","%DB107.DBD44:TOD[3]", todArray);
            
            LocalDateTime ldt01 = LocalDateTime.now();
            LocalDateTime[] dtArray = new LocalDateTime[]{ldt01,ldt01,ldt01,ldt01};
            
            builder.addItem("042","%DB108.DBX0.0:DT[4]", dtArray);
            builder.addItem("043","%DB108.DBX56.0:DATE_AND_TIME[4]", dtArray);
            builder.addItem("044","%DB108.DBX112.0:DT[4]", dtArray);
            
            Character[] characterArray = new Character[]{'A','B','C','D','E','F','G'};
            builder.addItem("045","%DB109.DBB0:CHAR[7]", characterArray);
            builder.addItem("046","%DB109.DBB10:CHAR[7]", characterArray);
            builder.addItem("047","%DB109.DBB20:CHAR[7]", characterArray);
            
            //TODO: String array dont support for this versión. write only
            //      the firts Item.
            //TODO: Change by the user the Maximum length (k)
            String[] strArray = new String[]{"BARCELONA","ANZOATEGUI","BARCELONA_ANZOATEGUI_VENEZUELA"};
            builder.addItem("048","%DB110.DBB0:STRING[3]", strArray);
            builder.addItem("049","%DB110.DBB768:STRING", strArray[1]);
            builder.addItem("050","%DB110.DBB1024:STRING", strArray[2]);
                        
            PlcWriteRequest request = builder.build();
            
            StopWatch watch = new StopWatch();
            watch.start();
            
            PlcWriteResponse response = request.execute().get(30, TimeUnit.SECONDS);

            watch.stop();
            System.out.println("Duration: " + watch.getTime() +" (msec)");
            
            Collection<String> names = response.getFieldNames();
            
            for (String name:names){
                System.out.println("Index: " + name +
                        " Response code: " + response.getResponseCode(name));
            }
                                                        
            plcConnection.close();
        } catch (Exception ex){
            LOGGER.info("Fail connection. Check IP address, rack, slot: " + ex);
        } 
    }
    
}
