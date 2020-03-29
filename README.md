**PLC4XS7Examples**

Examples for the use of communication libraries developed in the Apache PLC4X project.


![Image of PLC4X](https://plc4x.apache.org/images/apache_plc4x_logo.png)

For more details visit: http://plc4x.apache.org

If you find this information useful, feel free to collaborate with the project.

If you are not a programmer, you can document or promote the use of this tool.

To run the sample programs, the version hosted at https://github.com/glcj/plc4x (s7alarm branch) is required. This version is expected to become part of version 0.6.1 (LTS) of the PLC4X master branch in the near future.

What is the added value?

1. The event handling of the S7300 & S7400 PLCs. While these devices have been with us for a couple of decades now, there is little benefit to be gained from their notification and event systems. Siemens' own applications take full advantage and are effective, such as Braumat Classic and PCS7.

2. Optimize the request of items from the controller through the cyclical subscriptions, which allows an efficient use of the resources of the controllers especially for embedded equipment.

3. Support for native data types, such as S5TIME.

others,

Many of these features are available on the S7-1500 in its own version of the protocol. Unfortunately this is not open, allowing only PUT / GET functions. Event handling is proprietary, which is a shame.

The examples indicated below each represent the most basic possible handling of the features to be exploited, to facilitate the introduction to the use of the PLC4X libraries.

I hope you find it useful,


File | Description
------------ | -------------
PLCEventSubscription | Example of subscription to PLC events (USR, SYS, MODE, ALM_8, ALM_S).
PLCItemSubscription | Example of cyclical subscription from the PLC.
PLCJira1799 | Example to demonstrate data support according to PLC4X Jira-179
PLCReadField | Reading example for simple and complex types.
PLCReadWriteTimes | Example for Read and Write time type for S7300 & S7400
PLCSslDiagnostic | Example of reading the partial diagnostic list of the PLC.
PLCWriteField | Writing example for simple and complex types.



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
