package MailConsole;
import javax.mail.PasswordAuthentication;
import java.util.Properties;
import java.util.Scanner;
import java.util.prefs.Preferences;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import com.sun.mail.imap.IMAPFolder;

public class launcher {

	public Preferences prefs  = Preferences.userRoot().node(launcher.class.getName());
	
	public launcher()
	{
		// xnwstdduneeufohl
		System.out.println("Welcome to the Java E-Mail console ");
		Scanner kb = new Scanner(System.in);
		showMainMenu();
		
		int response = kb.nextInt();
		while(response != 4)
		{
			switch(response)
			{
			case 1:
				setupEmail(kb);
				break;
			case 2:
				sendEmail(kb);
				break;
			case 3:
				checkEmail();
				break;
			}
			showMainMenu();
			response = kb.nextInt();
		}
	}
	
	public static void main(String args[]) throws Exception
	{
		new launcher();
	}

	private void checkEmail() 
	{
		System.out.println("Connecting to mail account");
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
		
		Session session = Session.getDefaultInstance(props, new MyMailAuthenticator());
		
		try
		{	
			Store store = session.getStore("imaps");
			store = session.getStore("imaps");
			store.connect(prefs.get("imap", "imap.gmail.com"),prefs.get("username", ""),prefs.get("password",""));
			IMAPFolder folder = (IMAPFolder) store.getFolder("Inbox");
			folder.addMessageCountListener(new MyMessageListner());
			if(!folder.isOpen())
				folder.open(Folder.READ_ONLY);
			
			System.out.println("Loading Messages");
			Message[] msgs = folder.getMessages();
			
			String usr = prefs.get("username", "");
			
			System.out.printf(" __________________________________ \n"
							+ "| %16s%-16s |\n"
							+ "|----------------------------------|\n"
							+ "| Total Messages : %6d          |\n"
							+ "| Total Unread   : %6d          |\n"
							+ "| Oldest Message : %tD        |\n"
							+ "| Newest Message : %tD        |\n"
							+ "|__________________________________|\n"
							,usr.substring(0,usr.length()/2),usr.substring(usr.length()/2)
							,folder.getMessageCount()
							,folder.getUnreadMessageCount()
							,msgs[0].getReceivedDate()
							,msgs[msgs.length-1].getReceivedDate());
			
			
		}catch(MessagingException e) {
			e.printStackTrace();
		}
		sleep();
	}

	private void sleep() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public class MyMessageListner implements MessageCountListener
	{
		@Override
		public void messagesAdded(MessageCountEvent e) {
			// Code you want to run on message received 
			e.getMessages();
		}

		@Override
		public void messagesRemoved(MessageCountEvent e) {
			// Code to run when a message is removed
			
		}
	}
	
	private class MyMailAuthenticator extends javax.mail.Authenticator // The Authenticator to log into the mail account. 
	{
		public PasswordAuthentication getPasswordAuthentication() 
		{
			return new PasswordAuthentication(prefs.get("username", ""), prefs.get("password", ""));
		}
			
	}
	
	
	private void sendEmail(Scanner kb) 
	{
		if(!prefs.get("connection","Not Connected").equals("Success"))
			System.out.println("Connection has not been verified. Email may fail to send");
		
		Properties props = System.getProperties();
		props.setProperty("mail.smtp.host", prefs.get("smtp", "smtp.gmail.com"));
		props.setProperty("mail.smtp.port", "465");
		props.setProperty("mail.smtp.auth", "true");
		props.setProperty("maul.smtp.socketFactory.port","465");
		props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		
		Session session = Session.getDefaultInstance(props, new MyMailAuthenticator());
		
		System.out.println("Enter Recipient");
		String recip = kb.next();
		kb.nextLine();
		System.out.println("Enter Subject");
		String subj = kb.nextLine();
		System.out.println("Enter Body");
		String body = kb.nextLine();
		
		try 
		{
			System.out.println("Sending Message");
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(prefs.get("username", ""));
			msg.setSubject(subj);
			msg.setText(body);
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recip));
			
			Transport.send(msg);
			System.out.println("Message Sent Successfully");
			sleep();
		}catch (MessagingException e) {System.out.println("Message Failed To send"); e.printStackTrace();}
		
	}

	private void setupEmail(Scanner kb) 
	{
		int input = -1;
		
		while(input != 8)
		{
			showSetupMenu();
			input = kb.nextInt();
			String t = "";
			int p = 0;
			switch(input)
			{
			case 1:
				System.out.print("Enter A username:");
				t = kb.next();
				if(t.matches(".+@.+\\..+"))
					prefs.put("username", t);
				else
					System.out.println("Invalid Email Address IF you think this is innacurate change t.matches(\".@.+\\\\..+\")");
				System.out.println("");
				prefs.put("connection", "Untested");
				break;
			case 2:
				System.out.print("Enter A Password");
				t = kb.next();
				if(t.length() > 0)
					prefs.put("password", t);
				System.out.println("");
				prefs.put("connection", "Untested");
				break;
			case 3:
				System.out.println("Enter Imap Server: (Default imap.gmail.com)");
				t = kb.next();
				if(t.length() > 0)
					prefs.put("imap",t);
				prefs.put("connection", "Untested");
				break;
			case 4:
				System.out.println("Enter Imap Port: (Default 993)");
				p = kb.nextInt();
				if(p > 0)
					prefs.putInt("imapPort",p);
				prefs.put("connection", "Untested");
				break;
			case 5:
				System.out.println("Enter Smtp Server: (Default smtp.gmail.com)");
				t = kb.next();
				if(t.length() > 0)
					prefs.put("smtp",t);
				prefs.put("connection", "Untested");
				break;
			case 6:
				System.out.println("Enter Smtp Port: (Default 465)");
				p = kb.nextInt();
				if(p > 0)
					prefs.putInt("smtpPort",p);
				prefs.put("connection", "Untested");
				break;
			case 7:
				try
				{
					Properties props = System.getProperties();
					props.setProperty("mail.store.protocol", "imaps");
					Session session = Session.getDefaultInstance(props, new MyMailAuthenticator());
					Store store = session.getStore("imaps");
					store = session.getStore("imaps");
					store.connect(prefs.get("imap", "imap.gmail.com"),prefs.get("username", ""),prefs.get("password",""));
					prefs.put("connection","Success");
					System.out.println("Mail connection Established you should now be able to send and check mail");
					sleep();
				}
				catch(MessagingException e)
				{prefs.put("connection", "Failed To Connect");
				e.printStackTrace();}
				break;
			}
		}
	}

	private void showSetupMenu() 
	{
		System.out.printf(" _____________________________________________________\n"
						+ "| Please Selecet an option                           |\n"
				 		+ "|----------------------------------------------------|\n"
						+ "| 1. Username       | %-30s |\n"
						+ "| 2. Password       | %-30s |\n"
						+ "| 3. IMAP           | %-30s |\n"
						+ "| 4. IMAP Port      | %-30d |\n"
						+ "| 5. SMTP           | %-30s |\n"
						+ "| 6. SMTP Port      | %-30d |\n"
						+ "| 7. Test Connection| %-30s |\n"
						+ "|___________________|________________________________|\n"
						+ "| 8. Main Menu                                       |\n"
						+ "|____________________________________________________|\n"
						,prefs.get("username", "")
						,prefs.get("password", "")
						,prefs.get("imap", "imap.gmail.com")
						,prefs.getInt("imapPort", 993)
						,prefs.get("smtp", "smtp.gmail.com")
						,prefs.getInt("smtpPort", 465)
						,prefs.get("connection", "Failed To Connect"));
	}

	private void showMainMenu() 
	{
		System.out.println(" __________________________\n"
						 + "| Please Selecet an option |\n"
						 + "|--------------------------|\n"
						 + "| 1. Setup Email           |\n"
						 + "| 2. Send Email            |\n"
						 + "| 3. Check Email           |\n"
						 + "| 4. Exit                  |\n"
						 + "|__________________________|");
		
	}
	
}
