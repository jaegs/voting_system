package votingSystem.cTF;
/**
 * Adapted from http://tutorials.jenkov.com/java-multithreaded-servers/thread-pooled-server.html
 * @author Benjamin Jaeger
 *
 */

import votingSystem.Constants;
import votingSystem.Tools;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

import mixNet.Mix;

public class Server implements Runnable{

    protected int          serverPort   = Constants.PORT;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    protected Thread       runningThread= null;
    private final CTF ctf;
    
    public Server(CTF ctf) {
    	this.ctf = ctf;
    }
    
    public void run(){
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        //Open mix socket on another thread.
        Mix serverMix = new CTFMixServer(ctf);
        Tools.WriteObjectToFile(serverMix.getPubKey(), Constants.MIX_SERVER_KEY_FILE);
        serverMix.start();
        
        openServerSocket();
        while(!isStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Server Stopped.") ;
                    return;
                }
                throw new RuntimeException(
                    "Error accepting client connection", e);
            }
            try {
				ctf.getThreadPool().execute(new ServerThread(clientSocket, ctf));
			} catch (IOException e) {
				throw new RuntimeException("Error reading socket",e);
			}
        }
        ctf.getThreadPool().shutdown();
        System.out.println("Server Stopped.") ;
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port", e);
        }
    }
}
