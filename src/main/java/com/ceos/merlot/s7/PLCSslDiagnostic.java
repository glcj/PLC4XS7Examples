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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import java.util.concurrent.TimeUnit;
import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.apache.plc4x.java.s7.protocol.S7ByteReadResponse;
import org.apache.plc4x.java.s7.utils.S7Helper.SSL;
import org.apache.plc4x.java.s7.utils.S7ParamErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example of reading the partial diagnostic list of the PLC.
 * @author cgarcia
 */
public class PLCSslDiagnostic {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(PLCSslDiagnostic.class);
         
    private static PlcConnection plcConnection;    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try (PlcConnection plcConnection = new PlcDriverManager().getConnection("s7://192.168.1.23/0/2")){
            
            System.out.println("***********************************************");
            System.out.println("Before using, take a look at:");
            System.out.println("System Software for S7-300/400.\r\nSystem and Standard Functions - Volume 1/2");
            System.out.println("Document: A5E02789976-01");
            System.out.println("Chapter 34 System Status Lists (SSL).");
            System.out.println("URL: https://cache.industry.siemens.com/dl/files/604/44240604/att_67003/v1/s7sfc_en-EN.pdf");
            System.out.println("***********************************************\r\n");
            
            System.out.println("Request: SSL_ID=16#001C;INDEX=16#0000");
            PlcReadRequest.Builder sslbuilder = plcConnection.readRequestBuilder();
            sslbuilder.addItem("MySSL", "SSL_ID=16#011C;INDEX=16#000A");
            PlcReadRequest sslReadRequest = sslbuilder.build();  
            
            PlcReadResponse sslresponse = sslReadRequest.execute().get(2, TimeUnit.SECONDS);
                                    
            if (sslresponse.getResponseCode("MySSL") == PlcResponseCode.OK){
                ByteBuf data = ((S7ByteReadResponse) sslresponse).getByteBufValues("MySSL");
                
                System.out.println("DATA: \r\n" + ByteBufUtil.prettyHexDump(data));
                System.out.println("");
                
                SSL ssl = SSL.valueOf(0x001C);
                StringBuilder sb =  ssl.execute(data);
                System.out.println(sb.toString());                
            } else if (sslresponse.getResponseCode("MySSL") == PlcResponseCode.NOT_FOUND){
                System.out.println("Service not found."); 
                ByteBuf data = ((S7ByteReadResponse) sslresponse).getByteBufValues("MySSL");
                System.out.println(S7ParamErrorCode.valueOf(data.getShort(0)) + 
                        " : " + 
                        S7ParamErrorCode.valueOf(data.getShort(0)).getEvent());
            }

            Thread.sleep(3000);
            plcConnection.close();
            
        } catch (Exception ex){
            logger.info("Fallo la conexión: " + ex);
        }        
    }
    
}
