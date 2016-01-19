import java.io.*;
import java.net.*;
import java.util.Scanner;

public class SimpleFileClient {
	
	public final static int SOCKET_PORT = 20000;
	public final static String SERVER = "localhost"; //can connect to localhost or whatever ip the server is on
	public final static int FILE_SIZE = 60223860; 
        
    public static void main(String[] args) throws IOException 
    {
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        Socket sock = null;
        InputStreamReader isr = null;
        PrintStream PS = null;
        
        //used in put
		FileInputStream fis = null;
	    BufferedInputStream bis = null;
	    OutputStream os = null;
	    OutputStreamWriter osw = null;   
	    	
	    //used in get
	    BufferedReader br = null;   
        
        while(true)
        {
	        try
	        {
	        	sock = new Socket(SERVER, SOCKET_PORT);
	        	isr = new InputStreamReader(sock.getInputStream()); //InputStreamReader reads bytes and converts them into characters
	        	//BufferedReader reads text from a character input stream, buffering characters so as to provide for the efficient reading of charactacters
	        	br = new BufferedReader(isr);
	        	String line1 = br.readLine();
	        	
	        	System.out.print(line1 + ": ");
	        	Scanner scan = new Scanner(System.in);
	     		String stringCommand = scan.nextLine();
	        	
	        	PS = new PrintStream(sock.getOutputStream());
	        	PS.println(stringCommand);
	        	
	        	if(stringCommand.startsWith("get"))
	        	{
	        		try{
		        		byte [] fileByteArray  = new byte [FILE_SIZE];
						InputStream is = sock.getInputStream();
						
						//can specify directory here ie. fileToReceive ="c:\\Documents and Settings" + stringCommand.substring(4);
						//but it just goes go default directory if only name is provided
						String fileToReceive = stringCommand.substring(4);
						fos = new FileOutputStream(fileToReceive); //output stream for writing data to a File
						bos = new BufferedOutputStream(fos); //converts to bytes
						
						int bytesReadCount = is.read(fileByteArray,0,fileByteArray.length); //reads bytes from fileByteArray starting at 0 and ending at the end of the array
						int current = bytesReadCount;
						
						//fills up the fileByteArray
						do 
						{
						 	bytesReadCount = is.read(fileByteArray, current, (fileByteArray.length-current));
						 	if(bytesReadCount >= 0) current += bytesReadCount;
						} 
						while(bytesReadCount > -1);
						
						bos.write(fileByteArray, 0 , current);
						bos.flush();
						System.out.println("File " + fileToReceive+ " downloaded (" + current + " bytes read)");
	        		}

	        		catch(Exception e)
	        		{
	        			System.out.println("Invalid file. Try again."); 
	        		}
	        	}
	        	else if(stringCommand.startsWith("put"))
	        	{
	        		try
					{
						//gets the name of the file the user wants to download
						//user will type in something like 'get aFile.txt', so fileToSend gets set to 'aFile.txt'
						String fileToSend = stringCommand.substring(4);
    					os = sock.getOutputStream();
    					
    					osw = new OutputStreamWriter(sock.getOutputStream());
    					osw.write(stringCommand.substring(4) + "\n");
    					osw.flush();
    					
    					br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    					
    					String serverStatus = br.readLine();
    					
    					if(serverStatus.equals("READY"))
    					{
    						fis = new FileInputStream(fileToSend); //FileInputStream obtains input bytes from a file
    						
    						byte[] buffer = new byte[sock.getSendBufferSize()];
    						
    						int bytesReadCount = 0;
    						
    						while((bytesReadCount = fis.read(buffer))>0)
    							os.write(buffer,0,bytesReadCount);
    					}
					}
					catch(FileNotFoundException e)
					{
						System.out.println("That file does not exist in that directory. ");
					}
	        		    		
	        	}
	        	//if we're just writing something to console
	        	else
	        	{	
		        	while(true)
		        	{
			        	String line = br.readLine();
			        	if(line == null || line.equals("exit")) break;
			        	System.out.println(line);
			        }
	        	}
	        }
	      finally
	      {
	      		if(fos != null) fos.close();
	      		if(bos != null) bos.close();
	      		if(sock != null) sock.close();
	      		if(os != null) os.close();
	      		if(fis != null) fis.close();
	      		if(bis != null) bis.close();
	      		if(br != null) br.close();
	      		if(osw != null) osw.close();
	      		if(os != null) os.close();
	      }
       }
    }
}
