# FileTransferSystem
FileTransferSystem is a client-server file transfer system built with Java that uses the Socket API. The server is run on one machine and the client on another. The server is run on a specific port and listens for requests. The client must know the server IP and port it's running on in order to connect to the server. Once the client is connected there are several commands the client is able to execute which are explained in detail below. 

Created using JCreator 4.5 IDE. Tested in Windows 7 and XP.

# GettingStarted
Since the application is command line based, you need an IDE to run it. If you're running the client and server on separate systems then compile the files and run them normally. Make sure to run the server before running the client and make sure the client is configured with the appropriate IP address and port of the server. If you're running the client and server on the same system over localhost, then the same rules apply but you may also need to open both files in separate IDEs.

# User Commands
The client has the ability to run the following commands: 
'ls': Prints on the client window the contents(including files and folders) of the current directory on the server.
'pwd': Prints on the client window the full pathname of the current directory on the server.

'mkdir [new-folder-name]': Creates a new folder in the current directory on the server.

'cd [file-path]' OR 'cd ..': Used to change directories. The client can walk through basically any directory on the server. 'cd ..' goes up a single directory.

'get [file-name]': Downloads the specific file from the server and stores it on the client machine. The client file is created with the same name as it had on the server machine. You can specify where the file is saved on the client machine but by default it's saved in the same folder as where the client is ran from. 

'put [file-name]': Uploads the file from the client machine and stores it in the current directory of the server machine. On the server, it is given the same name it has on the client machine.

The get/put commands convert the file to be transferred into binary and then using the TCP protocol transfer them to their destination. 
Tested formats that can be transferred include: .pdf, .mp3, .txt, .jpg, .exe

# KnownBugs
If the user tries to 'put' a file that does not exist in the client side current directory, an empty file with the that name will still be created in the server side directory. For example, if client tries to do: 'put aFile.txt' but 'aFile.txt' does not exist in the client's current directory, 'aFile.txt' will still be created on the server side but will just be a blank file. 