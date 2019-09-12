import java.io.*; 
import java.net.*; 
class TCPClient
{
	public static int Register(DataOutputStream out, BufferedReader in, String username, int mode) throws Exception
	{
		String phrase;
		if (mode == 0)
		{
			phrase = "SEND ";
		}
		else
		{
			phrase = "RECV ";
		}

		out.writeBytes("REGISTER TO" + phrase + username + "\n\n");
		System.out.println("REGISTER TO" + phrase + username + "\n\n");
		String recv1 = in.readLine();
		System.out.println(recv1);
		String recv2 = in.readLine();

		//System.out.print("*" + recv2 + "&");

		if (recv2.equals(""))
		{
			if (recv1.equals("REGISTERED TO" + phrase + username))
			{
				return 1;
			}
			else
			{
				System.out.println(recv1);
				return 0;
			}
		}
		else
		{
			System.out.println("Error in server");
			return 0;
		}
	}

	//public static int SendMessage(DataOutputStream out, BufferedReader in, String username) throws Exception

	public static void main(String[] argv) throws Exception
	{
		// boolean regSend = false;
		// boolean regRecv = false;

		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

	    Socket toReceive = new Socket("localhost", 6789); 
	    Socket toSend = new Socket("localhost", 6789);

	    DataOutputStream outRecvStream = new DataOutputStream(toReceive.getOutputStream()); 
		BufferedReader inRecvStream = new BufferedReader(new InputStreamReader(toReceive.getInputStream()));

	    DataOutputStream outSendStream = new DataOutputStream(toSend.getOutputStream()); 
		BufferedReader inSendStream = new BufferedReader(new InputStreamReader(toSend.getInputStream()));

		int boolRecv = Register(outRecvStream, inRecvStream, argv[0], 1);
		int boolSend = Register(outSendStream, inSendStream, argv[0], 0);

		if ((boolSend == 1) && (boolRecv == 1))
		{
			System.out.println("Registered");
			SendMessage sent = new SendMessage(outSendStream, inSendStream, toSend, argv[0]);
			Thread sentThread = new Thread(sent);
			sentThread.start();

			RecvMessage recv = new RecvMessage(outRecvStream, inRecvStream);
			Thread recvThread = new Thread(recv);
			recvThread.start();


		}

		
	}


}

class SendMessage implements Runnable
{
	DataOutputStream out;
	BufferedReader in;
	Socket socket;
	String username;

	SendMessage(DataOutputStream outSend, BufferedReader inSend, Socket socket, String uname)
	{
		this.out = outSend;
		this.in = inSend;
		this.socket = socket;
		this.username = uname;
	}

	public void run()
	{
		try
		{
			String recipient;
			while (true)
			{
				BufferedReader inputFromUser = new BufferedReader(new InputStreamReader(System.in));
				String[] message = inputFromUser.readLine().split(" ", 2);
				if (message[0].charAt(0) == '@')
				{
					recipient = message[0].substring(1);
					System.out.println(recipient);
					//if (recipient.matches('[A-Za-z0-9'))
				}
				else
				{
					System.out.println("Incorrect Format");
					continue;
				}

				out.writeBytes("SEND " + recipient + "\nContent-length: " + message[1].length() + "\n\n" + message[1] + "\n");
				String recv1 = in.readLine();
				String recv2 = in.readLine();

				if (recv2.equals(""))
				{
					System.out.println("*" + recv1 + "*");
					if (recv1.equals("SENT " + recipient))
					{
						System.out.println(recv1);
					}
					else if (recv1.equals("ERROR 102 Unable To Send")) 
					{
						System.out.println(recv1);	
					}
					else if (recv1.equals("ERROR 103 Header Incomplete"))
					{
						System.out.println(recv1);
					}
					else
					{
						System.out.println(12);
						System.out.println("Server Error");
					}
				}
				else
				{
					System.out.println(123);
					System.out.println("Server Error");
				}

			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			System.out.println("OFFO");
		}
	}
}

class RecvMessage implements Runnable
{
	DataOutputStream out;
	BufferedReader in;

	RecvMessage(DataOutputStream outStream, BufferedReader inStream)
	{
		this.out = outStream;
		this.in = inStream;
	}

	public void run()
	{
		
			while(true)
			{
				try{
				String username;
				int len;
				String recv1 = in.readLine();
				String[] m1 = recv1.split(" ");
				if(m1[0].equals("FORWARD"))
				{
					username = m1[1];
					String recv2 = in.readLine();
					String[] m2 = recv2.split(" ");
					if (m2[0].equals("Content-length:"))
					{
						len = Integer.parseInt(m2[1]);
						String recv3 = in.readLine();
						if (recv3.equals(""))
						{
							String recv4 = in.readLine();
							System.out.println(username + recv4);
							out.writeBytes("RECEIVED " + username + "\n\n");
						}
						else
						{
							out.writeBytes("ERROR 103 Header Incomplete\n\n");

						}
					}
					else
					{
						//continue;
						out.writeBytes("ERROR 103 Header Incomplete\n\n");
						continue;
					}

				}
				else
				{
					//continue;
					out.writeBytes("ERROR 103 Header Incomplete\n\n");
					continue;
				}
			
		}
		catch (Exception e)
		{
			//out.writeBytes("ERROR 103 Header Incomplete\n\n");
			continue;
		}
		}
	}
}


















