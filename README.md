**PLC4XS7Examples**

Examples for the use of communication libraries developed in the Apache PLC4X project.


![Image of PLC4X](https://plc4x.apache.org/images/apache_plc4x_logo.png)

For more details visit: http://plc4x.apache.org

If you find this information useful, feel free to collaborate with the project.

If you are not a programmer, you can document or promote the use of this tool.


File | Description
------------ | -------------
PLCReadWriteTime | Example for Read and Write time type for S7300 & S7400
PLCJira1799 | Example to demonstrate data support according to PLC4X Jira-179
PLCWriteField | Writing example for simple and complex types.
PLCReadField | Reading example for simple and complex types.


*Possible SSL Partial Lists:*
The interpretation of the information is done in the _S7Helper.java_ class.


Module class                                              |    SSL-ID    | Implemented
-----------------------------------------------------------|--------------|----
Module identification                                     |    16#xy11   | X
CPU characteristics                                       |    16#xy12   | X
User memory areas                                         |    16#xy13   | X
System areas                                              |    16#xy14   | X
Block types                                               |    16#xy15   | X
CPU information                                           |    16#xy1C   | X
Interrupt status                                          |    16#xy22   | X
Assignment between process image partitions and OBs       |    16#xy25   |
Communication status data                                 |    16#xy32   |
H CPU group information                                   |    16#xy71   |
Status of the module LEDs                                 |    16#xy74   | X
Switched DP slaves in the H-system                        |    16#xy75   |
Module status information                                 |    16#xy91   |
Rack / station status information                         |    16#xy92   |
Rack / station status information                         |    16#xy94   |
Extended DP master system / PROFINET IO system information|    16#xy95   |
Module status information, PROFINET IO and PROFIBUS DP    |    16#xy96   |
Tool changer information (PROFINET IO)                    |    16#xy9C   |
Diagnostic buffer of the CPU                              |    16#xyA0   | X
Module diagnostic information (data record 0)             |    16#xyB1   |
Module diagnostic information (data record 1),geographical address |    16#xyB2   |
Module diagnostic information (data record 1), logical address|    16#xyB3   |
Diagnostic data of a DP slave                             | 16#xyB4   |




