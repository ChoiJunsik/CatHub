public interface RmiOp extends java.rmi.Remote{
	public String getFileList() throws java.rmi.RemoteException;
	public boolean doFileDelete(String filename) throws java.rmi.RemoteException;
}
