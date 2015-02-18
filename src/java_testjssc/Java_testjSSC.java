
package java_testjssc;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

/// This is a test for git push
public class Java_testjSSC {

    static SerialPort serialPort;
    static int count=0;
    
    public static void main(String[] args) {
        
        String message = "O\n";
        String temp = "";
        String port = findPort(); /* Port Discovery */
        
        if (port.equals("exit")){
            System.out.println("Error: no device connected.\n Please connect a compatible device.");
            System.exit(1);
        }
        
        serialPort = new SerialPort(port);
        try {
            System.out.println("Port opened: " + serialPort.openPort());
            System.out.println("Params Set: " + serialPort.setParams(9600, 8, 1, 0));
            int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;//Prepare mask
            serialPort.setEventsMask(mask);//Set mask
            serialPort.addEventListener(new SerialPortReader());//Add SerialPortEventListener
        }
        catch (SerialPortException ex){
            System.out.println(ex);
        }
        
        //for (int i=0; i<100; i++){
        //    writeToPort(serialPort, "O");
        //    writeToPort(serialPort, "F");
        //}
   
        //temp = readFromPort(serialPort);
        
        
        //System.out.println("Temp = " + temp);
        
      //  closePort(serialPort);
    }
    static class SerialPortReader implements SerialPortEventListener {

        @Override
        public void serialEvent(SerialPortEvent event) {  
            StringBuilder message = new StringBuilder();
            Boolean receivingMessage = false;
            
            if(event.isRXCHAR()){//If data is available
               try {
                    byte buffer[] = serialPort.readBytes();  // Problem: all bytes may not be available when buffer is read
                    for (byte b: buffer){
                        if (b == '>'){
                            receivingMessage = true;
                            message.setLength(0);
                            //System.out.println("CLEARING STRING");
                        }
                        else if (receivingMessage = true){
                            if (b == '\r' || b == '\n'){
                                receivingMessage = false;                       
                            }
                            else {
                                message.append((char)b);
                            }
                        }
                    }
                    System.out.print(message);
                    if (receivingMessage == false){
                        System.out.println("\nMessage:" + ++count + "\n\t");
                    }
               }
                catch (SerialPortException ex) {
                    System.out.println(ex);
                    System.out.println("serialEvent");
                }
            }
//            else if(event.isCTS()){//If CTS line has changed state
//                if(event.getEventValue() == 1){//If line is ON
//                    System.out.println("CTS - ON");
//                }
//                else {
//                    System.out.println("CTS - OFF");
//                }
//            }
//            else if(event.isDSR()){///If DSR line has changed state
//                if(event.getEventValue() == 1){//If line is ON
//                    System.out.println("DSR - ON");
//                }
//                else {
//                    System.out.println("DSR - OFF");
//                }
//            }

        }
    }
    
    private static String findPort(){
        /* Port Discovery */
        
        String[] portNames = SerialPortList.getPortNames();
        for (String portName : portNames) {
            System.out.println("Found port: " + portName);
        }
        if (portNames.length == 0){
            return "exit";
        }
        else return portNames[0];
    }
    
    private static boolean writeToPort(SerialPort serialPort, String message){
        /* Writing to a serial port */
        
        try {
            System.out.println("\"Hello World!!!\" successfully written to port: " + serialPort.writeBytes(message.getBytes()));
            return true;
        }
        catch (SerialPortException ex){
            System.out.println(ex);
        }
        return false;
    }
    
    private static String readFromPort(SerialPort serialPort){
        /* Reading from a serial port */
        byte[] buffer = {};
        try {
            serialPort.openPort();
            serialPort.setParams(9600, 8, 1, 0);
            buffer = serialPort.readBytes(10); //Read 10 bytes from serial port
        }
        catch (SerialPortException ex){
            System.out.println(ex);
        }
        String temp = new String(buffer); // Convert byte arr to String
        System.out.println(buffer);
        return temp;
    }
    
    private static void closePort(SerialPort serialPort){
        try{ 
            serialPort.closePort();
        }
        catch(SerialPortException ex){
            System.out.println(ex);
        }
    }
    
}

