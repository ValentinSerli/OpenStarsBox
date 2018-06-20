package com.serli.telescope.serie;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Classe comSerie qui intéragie avec le port série
 */

public class comSerie
{

	private static SerialPort serialPort;
    
	public static SerialPort getSerialPort() {
		return serialPort;
	}
	
    public void connect ( String portName ) throws Exception
    {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Le port est déjà utilisé");
        }
        else
        {
            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
            
            if ( commPort instanceof SerialPort )
            {
                serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                
                @SuppressWarnings("unused")
				InputStream in = serialPort.getInputStream();
                @SuppressWarnings("unused")
				OutputStream out = serialPort.getOutputStream();
                
               // (new Thread(new SerialReader(in))).start();
               // (new Thread(new SerialWriter(out))).start();

            }
            else
            {
                System.out.println("Erreur seul les port série sont autorisée");
            }
        }     
    }
    
    /** */
    public static class SerialReader implements Runnable 
    {
        InputStream in;
        
        public SerialReader ( InputStream in )
        {
            this.in = in;
        }
        
        public void run ()
        {
            byte[] buffer = new byte[1024];
            int len = -1;
            try
            {
                while ( ( len = this.in.read(buffer)) > -1 )
                {
                    System.out.print(new String(buffer,0,len));
                }
                System.out.println("SerialReader - Done");
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }            
        }
    }

    /** */
    public static class SerialWriter implements Runnable 
    {
        OutputStream out;
        
        public SerialWriter ( OutputStream out )
        {
            this.out = out;
        }
        
        public void run ()
        {
            try
            {                
                int c = 0;
                while ( ( c = System.in.read()) > -1 )
                {
                    this.out.write(c);
                }           
                System.out.println("SerialWriter - Done");
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }            
        }
    }
} 

