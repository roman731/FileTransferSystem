import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleFileServer {
	
	public final static int SOCKET_PORT = 20000;
	
	public static ServerSocket servsock = null;
    public static Socket sock = null;
    public static InputStreamReader isr = null;
    public static BufferedReader br = null;
    public static PrintStream PS = null;
    	
    //used in 'get'
    public static FileInputStream fis = null;
    public static BufferedInputStream bis = null;
    public static OutputStream os = null;
    	    	
    //used in put        
    public static InputStream input = null; 
	public static BufferedWriter bw = null;
	public static FileOutputStream fos = null;
	        
    public static void main(String[] args) throws Exception 
    {
    	startServer();	   
    }
    
    public static void startServer() throws Exception
    {
    	try
    	{
    		servsock = new ServerSocket(SOCKET_PORT);
    		while(true)
    		{
    			System.out.println("Waiting...");
	    			try
	    			{
	    				sock = servsock.accept();
	    				System.out.println("Accepting connection: " + sock);	    			
	    				
	    				//displays working directory when client connects
	    				{
					        PS = new PrintStream(sock.getOutputStream()); //PrintStream gives the ability to write text to the client
	    					PS.println( System.getProperty("user.dir")); //Print working directory to the client
	    				}
	    				
	    				//reads client message/input
	    				isr = new InputStreamReader(sock.getInputStream()); //InputStreamReader reads bytes and converts them into characters
	    				
	    				//BufferedReader reads text from a character input stream, buffering characters so as to provide for the efficient reading of charactacters
	    				br = new BufferedReader(isr);
	    				String message = br.readLine();
	    				System.out.println("Input recieved from client: " + message);
	    				
	    				if(message.equals("ls"))
	    				{
							lsCommand();	
	    				}
	    				
	    				else if(message.startsWith("mkdir"))
	    				{
	    					mkdirCommand(message);  
	    				}
	    				
	    				else if(message.startsWith("cd"))
	    				{
	    					cdCommand(message);	    					
	    				}	  									
	    				
	    				else if(message.startsWith("get"))
	    				{
	    					getCommand(message);
	    				}
	    				
	    				else if(message.startsWith("put"))
	    				{
		    				putCommand(message);
	    				}
						
						else if(message.startsWith("pwd"))
						{
							pwdCommand();
						}	
	    				
	    				else
	    				{
	    					System.out.println("ERROR: invalid input.");
	    					PS.println("Invalid input. Try again.");
	    				}	    			
	    				
	    				System.out.println("Finished!");
	    			}
		    		finally
		    		{	
						if(sock != null) sock.close();
						if(os != null) os.close();
						if(fis != null) fis.close();
						if(bis != null) bis.close();
						if(input != null) input.close();
						if(bw != null) bw.close();
						if(fos != null) fos.close();
		    		}
    		}
    	}
	    catch(java.net.SocketException e)
	    {
	    	System.out.println("Client has closed connection."); 
	    	
	    }
	    finally
	    {
	    	if(servsock != null) servsock.close();
	    	startServer(); //server continues to run when a user disconnects
	    }    
    }
    //prints out all files and directories in the directory the client is in
    public static void lsCommand()
    {
		String outputString = "";
		File dir = new File(System.getProperty("user.dir"));
		File[] filesList = dir.listFiles();
		for (File file : filesList)	 
			{
				if (file.isFile())
				{
					outputString += "File: "+file.getName() + "\n";
				}
				else if (file.isDirectory())
				{
					outputString += "Directory: " +file.getName() + "\n";
				}	
			}
		PS.println(outputString);
    }
    //creates a new directory in the directory the client is in
    public static void mkdirCommand(String message)
    {
    	try
		{
			File dir = new File(System.getProperty("user.dir") + "\\" + message.substring(6));
			dir.mkdir();		    					
		}
		catch(IndexOutOfBoundsException i)
		{
			System.out.println("Invalid command." + i);
		} 
    }
    //changes current directory the user is viewing
    public static void cdCommand(String message)
    {
    	if(message.equals("cd ..") || message.equals("cd.."))
		{	
			String pwdString = System.getProperty("user.dir");
			int flag = 0;
								
			//Looks for last '\' starting from end of string
			for(int i = pwdString.length() - 1; (i >= 3) && (flag == 0); i--){
									
				if(pwdString.substring(0,i).equals("C:\\")){
					System.setProperty("user.dir", "C:\\");
					flag =1;
				}
				else if (pwdString.charAt(i) == '\\'){
					System.setProperty("user.dir",pwdString.substring(0,i));
					flag = 1;
				}
			}
	    }
		//multiple backs like cd ..\..\..
		else if(message.startsWith("cd ..\\") || message.startsWith("cd..\\"))
		{
			String pwdString = System.getProperty("user.dir");
			int flag = 0;
			int countDashes = 0;
			
			//this if takes care of situation where msg is: cd ../..
			//there's only 1 '/' but two sets of .. 
			if (message.charAt(message.length()-1) == '.')
				countDashes = 1;

			//Looks for last '/' starting from end of string
			for(int i = message.length() - 1; (i >= 4); i--){
				if (message.charAt(i) == '\\'){
					countDashes++;
				}	
			}
			//now that we have # dir we want to go back
			int count = 0;
			//Looks for last '\' starting from end of string
			for(int i = pwdString.length() - 1; (i >= 3) && (count < countDashes); i--){
									
				if(pwdString.substring(0,i).equals("C:\\")){
					System.setProperty("user.dir", "C:\\");
					count++;
				}
				else if (pwdString.charAt(i) == '\\'){
					System.setProperty("user.dir",pwdString.substring(0,i));
					count++;
				}
			}
			
		}
		//This is where we go forward in directories
		else
		{
			if(message.equals("cd")){
				pwdCommand();
			}
			else
			{
				int flag = 0;
				String outputString ="";
				File dir = new File(System.getProperty("user.dir"));
				File[] filesList = dir.listFiles();
				for (File file : filesList)	 {
					//Check's if directory asked for exists
					if (file.isDirectory() && file.getAbsolutePath().equals(System.getProperty("user.dir")+"\\"+message.substring(3, message.length()))){
						outputString += "Directory: " +file.getName() + "\n";
						System.setProperty("user.dir", System.getProperty("user.dir")+"\\"+message.substring(3, message.length()));
						flag = 1;		//if it exists flag is 1
					}
					//if we are in the C:\ directory
					else if(System.getProperty("user.dir").equals("C:\\")&& file.isDirectory() && file.getAbsolutePath().equals(System.getProperty("user.dir")+message.substring(3, message.length()))){
						outputString += "Directory: " +file.getName() + "\n";
						System.setProperty("user.dir", System.getProperty("user.dir")+message.substring(3, message.length()));
						flag = 1;		//if it exists flag is 1
					}
				}
				if(flag == 0)
				{
					if(message.length() <= 3)
						PS.println("Invalid command"); //handles if user enters 'cd.' or something without a directory name
					else
						PS.println("Error: There is no directory named -- "  + message.substring(3, message.length()));
				}
			}
		}
    }
    
   public static void pwdCommand()
   {
   		String outputString = " --- Path --- \n";
		outputString += System.getProperty("user.dir") + "\n";
		PS.println(outputString);	
   }
   
   //client requests file from server
   public static void getCommand(String message)
   {
   		try
		{
			//gets the name of the file the user wants to download
			//user will type in something like 'get aFile.txt', so fileToSend gets set to 'aFile.txt'
			String fileToSend = message.substring(4); 
			
			//new file gets created
			File myFile = new File(System.getProperty("user.dir")+ "\\"+ fileToSend);
			
			byte [] byteArray = new byte[(int)myFile.length()]; 
			fis = new FileInputStream(myFile); //FileInputStream obtains input bytes from a file
			bis = new BufferedInputStream(fis); //As bytes from the stream are read or skipped, the internal buffer is refilled as necessary from the contained input stream 
			bis.read(byteArray,0,byteArray.length); //reads bytes from byteArray starting at 0 and ending at the end of the array
			os = sock.getOutputStream(); //OutputStream accepts the bytes and later writes them to the file using os.write()
			System.out.println("Sending " + fileToSend + "(" + byteArray.length + " bytes)");
			os.write(byteArray,0,byteArray.length);
			os.flush();
		}
		//checks the file the client is trying to recieve actually 
		catch(FileNotFoundException e)
		{
			System.out.println("That file does not exist in that directory. ");
		}
		catch(Exception e)
		{
			System.out.println("Something went wrong...");
		}
   }
   //client uploads file to server
   public static void putCommand(String message)
   {
   		try
		{
			input = sock.getInputStream();  
	        br = new BufferedReader(new InputStreamReader(sock.getInputStream()));  //BufferedReader reads text from a character input stream
	        bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())); //Writes text to a character-output stream
	        	
	        String fileToRecieve = message.substring(4);	
	        	
	        if ( !fileToRecieve.equals("") )
	        {  	   
	            bw.write("READY\n");  
	            bw.flush();  
	        }  	
					
			    byte[] buffer = new byte[sock.getReceiveBufferSize()];  
			    fos = new FileOutputStream(new File(System.getProperty("user.dir")+ "\\" + fileToRecieve)); //output stream for writing data to a File
			    int bytesReceived = 0;  
			
			    //transfers all bytes from file to buffer
			    while((bytesReceived = input.read(buffer))>0)  
			    {  
			       fos.write(buffer,0,bytesReceived);  
			    }   
	        
		}
		catch(FileNotFoundException e)
		{
			System.out.println("That file does not exist in that directory. !");
		}
		catch(Exception e)
		{
			System.out.println("Invalid file. Try again.");
		} 	
   }  
}
