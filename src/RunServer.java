import mayflower.Mayflower;

public class RunServer {

	public static void main(String[] args)
	{
		String sPort = Mayflower.ask("Port?");
		if("".equals(sPort.trim()))
			sPort = "8083";
		try
		{
			int port = Integer.parseInt(sPort);
			System.out.println("Listening to Port " + port);
			
			new AmazonsServer(port);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
