
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class Server {

   public static void main(String[] args) {
      
      final KeyStore ks;
      final KeyManagerFactory kmf;
      final SSLContext sc;
      
      
      String desktopPath = System.getProperty("user.home") + "\\Desktop\\catHubServer";
      File base = new File(desktopPath);
      base.mkdir();
      
      final String runRoot = "C://";

      SSLServerSocketFactory ssf = null;
      SSLServerSocket s = null;
      SSLSocket c = null;

      if (args.length != 1) {
         System.out.println("Usage: Classname Port");
         System.exit(1);
      }
      int sPort = Integer.parseInt(args[0]);
      String ksName = runRoot + ".keystore//SSLSocketServerKey";
      
      char keyStorePass[] = "154840".toCharArray();
      char keyPass[] = "154840".toCharArray();
      try {
    	 java.rmi.registry.LocateRegistry.createRegistry(1099);
         RmiOpImpl remObj = new RmiOpImpl();
         java.rmi.Naming.rebind("rmi://localhost:1099/HelloRemote",remObj);
      }catch(Exception e) {
         e.printStackTrace();
      }
         
      try {
         ks = KeyStore.getInstance("JKS");
         ks.load(new FileInputStream(ksName), keyStorePass);
         //
         
         kmf = KeyManagerFactory.getInstance("SunX509");
         kmf.init(ks, keyPass);
         
         sc = SSLContext.getInstance("TLS");
         sc.init(kmf.getKeyManagers(), null, null);
         
         ssf = sc.getServerSocketFactory();
         s = (SSLServerSocket)ssf.createServerSocket(sPort);
         System.out.println("\n");
         System.out.println("    /\\     /\\");
         System.out.println("   {  `---'  }");
         System.out.println("   {  O   O  }");
         System.out.println("   ~~>  V  <~~");
         System.out.println("    \\  \\|/  /");
		 System.out.println("     `-----'____");
		 System.out.println("     /     \\    \\_");
		 System.out.println("    {       }\\  )_\\_");
		 System.out.println("    |  \\_/  |/ /  \\_\\_/ )");
		 System.out.println("     \\__/  /(_/     \\__/");
		 System.out.println("       (__/");
		 System.out.println();
		 System.out.println("     Welcome to CatHub !!");
         while(true) {
        	 c = (SSLSocket) s.accept();
        	 if(c != null) {
      			Thread recv = new ServerRecv(c);
       			recv.start();
        	 }
         }

            
      } catch (SSLException se) {
         System.out.println("SSL problem, exit~");
         try {
            s.close();
         } catch (IOException i) {
         }
      } catch (Exception e) {
         System.out.println("What?? exit~");
         try {
            s.close();
         } catch (IOException i) {
         }
      }
   }
   

}