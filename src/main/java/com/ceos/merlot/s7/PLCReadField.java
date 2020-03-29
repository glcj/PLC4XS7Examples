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
 * Reading example for simple and complex types.
 * @author cgarcia
 */
public class PLCReadField {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(PLCReadField.class);
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try (PlcConnection plcConnection = new PlcDriverManager().getConnection("s7://192.168.1.23/0/2")){
            PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();  
            
            /****************************************************************
             * Test for S7300/S7400
             * Elementary Data Types
             * Bit Data Types
             ****************************************************************/            
            builder.addItem("001","%MX1.0:BOOL");
            builder.addItem("002","%MB3:BYTE");
            builder.addItem("003","%MW4:WORD");
            builder.addItem("004","%MD6:DWORD");

            /****************************************************************
             * Character Types
             ****************************************************************/            
            builder.addItem("005","%MB208:CHAR");
            
            /****************************************************************
             * Numeric Types
            ****************************************************************/            
            builder.addItem("006","%MW12:INT");
            builder.addItem("007","%MD14:DINT");
            builder.addItem("008","%MD18:REAL");

            /****************************************************************
             * Time Types
             ****************************************************************/
            
            /****************************************************************
             * TIME: TIME#-24d20h31m23s648ms / TIME#24d20h31m23s647ms
             *                         -2^31 / 2^31-1
             * The content is represented  as milliseconds (ms), saved as (int)
             * TODO: Change S7 TIME to Integer not LocalTime
             ****************************************************************/                      
            builder.addItem("009","%DB105.DBD20:TIME");
                                    
            /****************************************************************
             * DATE: DATE#1990-01-01 / DATE#2168-12-31 
             *              (=W#16#0000) / (=W#16#FF62)                   
             * The content is the number of days since 01.01.1990
             ****************************************************************/            
            builder.addItem("010","%DB106.DBW14:DATE");
                   
            /****************************************************************
             * TIME_OF_DAY: TIME_OF_DAY#00:00:00 / TIME_OF_DAY#23:59:59.999
             *               (=DW#16#0000_0000) / (=DW#16#0526_5BFF)
             ****************************************************************/            
            builder.addItem("011","%DB107.DBD60:TIME_OF_DAY");
            
            /****************************************************************
             * Test for S7300/S7400
             * Complex Data Types
             ****************************************************************/    
            builder.addItem("012","%DB108.DBX48.0:DT");
            builder.addItem("013","%DB108.DBX56.0:DATE_AND_TIME"); 
            
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
            //builder.addItem("014","%MB100:STRING");

            /****************************************************************
             * ARRAYS
             ****************************************************************/
            builder.addItem("015","%MX150.0:BOOL[12]");
            builder.addItem("016","%MX155.0:BOOL[12]");
            builder.addItem("017","%MX160.0:BOOL[12]");
            
            builder.addItem("018","%MB0:BYTE[200]");
           
            builder.addItem("019","%MW160:WORD[7]");
                      
            builder.addItem("020","%MW174:INT[7]");   
                        
            builder.addItem("021","%MD174:DINT[7]"); 

            builder.addItem("022","%MD174:REAL[7]"); 
            
            builder.addItem("023","%MD174:TIME[7]"); 
            
            builder.addItem("024","%MW174:DATE[7]");
            
            builder.addItem("025","%DB107.DBD20:TIME_OF_DAY[7]");

            builder.addItem("026","%DB108.DBX0.0:DT[7]");
            builder.addItem("027","%DB108.DBX112:DATE_AND_TIME[7]");
            
            builder.addItem("028","%DB109.DBB20:CHAR[7]");
            
            //builder.addItem("029","%MB20:STRING[3]");
                       
            PlcReadRequest readRequest = builder.build();  
            
            StopWatch watch = new StopWatch();
            watch.start();
            
            PlcReadResponse response = readRequest.execute().get(1, TimeUnit.SECONDS);
            
            watch.stop();
            System.out.println("Duration: " + watch.getTime() +" (msec)");
            
            
            //System.out.println("Response: " + response.getResponseCode("001"));
            //System.out.println("Response %MX200.0:BOOL: " + response.getAllBooleans("001"));
            
            Collection<String> names = response.getFieldNames();
            
            for (String name:names){
                System.out.println("Index: " + name +
                        " Response code: " + response.getResponseCode(name) +
                        " Number of values: " + ((response.getResponseCode(name)==PlcResponseCode.OK)?
                        ((Integer)(response.getNumberOfValues(name))).toString():response.getResponseCode(name)));
            }       

            //builder.addItem("001","%MX200.0:BOOL");
            if (response.isValidBoolean("001")){
                System.out.println("001: " + response.getRequest().getField("001").getDefaultJavaType() + " Value: " + response.getBoolean("001"));
            }

            System.out.println("002 [%MB201:BYTE]                       : " + response.getByte("002")); //18            
            System.out.println("003 [%MW202:WORD]                       : " + response.getShort("003")); //17185
            System.out.println("004 [%MD204:DWORD]                      : " + response.getInteger("004")); //-2_023_406_815            
            System.out.println("005 [%MB208:CHAR]                       : " + response.getString("005")); //'9'  
            System.out.println("006 [%MW12:INT]                         : " + response.getShort("006")); //2020
            System.out.println("007 [%MD14:DINT]                        : " + response.getInteger("007")); //20031970
            System.out.println("008 [%MD18:REAL]                        : " + response.getFloat("006")); //3.1416
            System.out.println("009 [%MD22:TIME]                        : " + response.getDuration("009")); //518_399_123
            System.out.println("010 [%MW26:DATE]                        : " + response.getDate("010")); //11022
            System.out.println("011 [%MD28:TIME_OF_DAY                  : " + response.getTime("011")); //86399999
            System.out.println("012 [%DB108.DBX48.0:DT]                 : " + response.getDateTime("012"));            
            System.out.println("013 [%DB108.DBX56.0:DATE_AND_TIME]      : " + response.getDateTime("013"));            
            System.out.println("015 [%MX150.0:BOOL[12]]                 : " + response.getBoolean("015", 0));
            System.out.println("016 [%MX155.0:BOOL[12]]                 : " + response.getBoolean("016", 1));
            System.out.println("017 [%MX160.0:BOOL[12]]                 : " + response.getBoolean("017", 2) );                       
            System.out.println("018 [%MB0:BYTE[200]]                    : " + response.getByte("018", 100));           
            System.out.println("019 [%MW160:WORD[7]]                    : " + response.getShort("019",5));                      
            System.out.println("020 [%MW174:INT[7]]                     : " + response.getShort("020",5));                           
            System.out.println("021 [%MD174:DINT[7]]                    : " + response.getInteger("021", 3)); 
            System.out.println("022 [%MD174:REAL[7]]                    : " + response.getFloat("022", 2));             
            System.out.println("023 [%MD174:TIME[7]]                    : " + response.getDuration("023", 3));             
            System.out.println("024 [%MW174:DATE[7]]                    : " + response.getDate("024",6));                        
            System.out.println("025 [%DB107.DBD20:TIME_OF_DAY[7]]       : " + response.getTime("025",4));
            System.out.println("026 [%DB108.DBX0.0:DT[7]]               : " + response.getDateTime("026", 4));
            System.out.println("027 [%DB108.DBX112:DATE_AND_TIME[7]]    : " + response.getDateTime("027",3));            
            System.out.println("028 [%DB109.DBB20:CHAR[7]]              : " + response.getString("028"));            
                        
            plcConnection.close();            
            
        } catch (Exception ex){
            LOGGER.info("Fail connection. Check IP address, rack, slot: " + ex);
        }             
    }
    
}
