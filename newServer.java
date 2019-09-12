import java.io.*; 
import java.net.*; 
import java.util.*;

class TCPServer
{
	public static HashMap<String, Socket[]> registered = new HashMap<>();

	public static void main(String[] argv) throws Exception
	{
		ServerSocket welcomeSocket = new ServerSocket(6789);

		while (true)
		{
			Socket forThread = welcomeSocket.accept();
			DataOutputStream outStream = new DataOutputStream(forThread.getOutputStream()); 
      		BufferedReader inStream = new BufferedReader(new InputStreamReader(forThread.getInputStream()));

      		SocketThread sthread = new SocketThread(outStream, inStream, forThread);
      		Thread thread = new Thread(sthread);
      		thread.start();

		}
	}
}

class SocketThread implements Runnable
{
	DataOutputStream out;
	BufferedReader in;
	Socket sock;

	SocketThread(DataOutputStream outStream, BufferedReader inStream, Socket s)
	{
		this.out = outStream;
		this.in = inStream;
		this.sock = s;
	}

	public void run()
	{
		try
		{
			//Socket temp;
			String regMessage = in.readLine();

			//System.out.println(regMessage);

			String regMessage2 = in.readLine();

			System.out.println(2);

			if (regMessage2.equals(""))
			{
				//System.out.println(3);

				String[] regSplit = regMessage.split(" ");
				if (regSplit[0].equals("REGISTER"))
				{
					//System.out.println(3);
					if (regSplit[1].equals("TORECV"))
					{
						//System.out.println(4);
						if (regSplit[2].matches("[a-zA-Z0-9]+"))
						{
							//System.out.println(5);
							TCPServer.registered.put(regSplit[2], new Socket[]{sock, null});
							if (TCPServer.registered.containsKey(regSplit[2]))
							{
								System.out.println("Added");
							}
							out.writeBytes("REGISTERED TORECV " + regSplit[2] + "\n\n");
							//System.out.println(6);
						}
						else
						{
							out.writeBytes("ERROR 100 Malformed username\n\n");
						}
					}
					else if (regSplit[1].equals("TOSEND"))
					{
						if (regSplit[2].matches("[a-zA-Z0-9]+"))
						{
							Socket sent = TCPServer.registered.get(regSplit[2])[0];
							TCPServer.registered.put(regSplit[2], new Socket[]{sent,sock});

							out.writeBytes("REGISTERED TOSEND " + regSplit[2] + "\n\n");

							while (true)
							{
								String message = in.readLine();

								System.out.println(message);

								String[] sent1 = message.split(" ");
								if (sent1[0].equals("SEND"))
								{
									String recipient = sent1[1];

									//System.out.println(recipient);

									if (TCPServer.registered.containsKey(recipient))
									{
										message = in.readLine();
										sent1 = message.split(" ");
										if (sent1[0].equals("Content-length:"))
										{
											int size = Integer.parseInt(sent1[1]);

											System.out.println(51);

											message = in.readLine();

											System.out.println(52);
											if (message.equals(""))
											{
												message = in.readLine();
												System.out.println(53);
												message = message.substring(0,size);

												Socket sendAck = TCPServer.registered.get(recipient)[0];
												DataOutputStream toRecipient = new DataOutputStream(sendAck.getOutputStream());
												BufferedReader fromRecipient = new BufferedReader(new InputStreamReader(sendAck.getInputStream()));

												toRecipient.writeBytes("FORWARD " + regSplit[2] + "\nContent-length: " + size + "\n\n" + message + "\n");

												System.out.println(41);

												String recAck1 = fromRecipient.readLine();
												String recAck2 = fromRecipient.readLine();
												if (recAck2.equals(""))
												{
													if (recAck1.equals("RECEIVED " + regSplit[2]))
													{
														out.writeBytes("SENT " + recipient + "\n\n");
													}
													else if (recAck1.equals("ERROR 103 Header Incomplete"))
													{
														out.writeBytes("ERROR 103 Header Incomplete\n\n");
													}
												}
												else
												{
													System.out.println(31);
													out.writeBytes("ERROR 102 Unable To Send\n\n");
												}
											}
											else
											{
												System.out.println(32);
												out.writeBytes("ERROR 102 Unable To Send\n\n");
											}

										}
										else
										{
											System.out.println(33);
											out.writeBytes("ERROR 102 Unable To Send\n\n");
										}
									}
									else
									{
										System.out.println(34);
										out.writeBytes("ERROR 102 Unable To Send\n\n");
									}
								}
								else
								{
									out.writeBytes("ERROR 101 No user registered\n\n");	
								}
							}
						}
						else
						{
							out.writeBytes("ERROR 100 Malformed username\n\n");
						}
					}
					else
					{
						out.writeBytes("ERROR 101 No user registered\n\n");
					}
				}
				else
				{
					out.writeBytes("ERROR 101 No user registered\n\n");
				}
			}
			else
			{
				out.writeBytes("ERROR 101 No user registered\n\n");
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
}