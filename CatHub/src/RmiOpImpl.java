import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;;

public class RmiOpImpl extends UnicastRemoteObject implements RmiOp{
	
    String path = System.getProperty("user.home") + "\\Desktop\\catHubServer";
	protected RmiOpImpl() throws RemoteException {
		super();
	}

	private static final long serialVersionUID = 1L;
	public String getFileList() {
	    String output = "";
		File dirFile=new File(path);
		File []fileList=dirFile.listFiles();
		for(File tempFile : fileList) {
		  if(tempFile.isFile()) {
		    String tempPath=tempFile.getParent();
		    String tempFileName=tempFile.getName();
		    output += tempPath+"\\"+tempFileName+"\r\n";
		  }
		}
		return output;
	}

	
	public boolean doFileDelete(String filename) throws RemoteException {
		try {
			File del = new File(path + "\\" + filename );
            del.delete();
			return true;
		}
		catch (Exception e) {
			
			return false;
		}
		
		
		
	}
	
}
