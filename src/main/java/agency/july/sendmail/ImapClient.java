package agency.july.sendmail;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.sun.mail.imap.IMAPFolder;

import agency.july.config.models.EmailParams; // agency.july.test.config.models.EmailParams;

public class ImapClient  {
	
	private Session session;
	private Message messages[];
	private Store store;
	private IMAPFolder folder;
	
	public String lastReadMessage;
	public String lastReadPlainText;
	public String lastReadHTML;
	
	public ImapClient (EmailParams email) {
	try {
        Properties props = new Properties();
        props.put(email.getProtocol(), email.getStore());

        session = Session.getDefaultInstance(props, null);
        store = session.getStore(email.getStore());
        store.connect(email.getConnect(), email.getAccount(), email.getPassword());

        folder = (IMAPFolder) store.getFolder(email.getFolder());
        folder.open(Folder.READ_WRITE); // READ_ONLY

        Flags seen = new Flags(Flags.Flag.SEEN);
        FlagTerm unseenFlagTerm = new FlagTerm( seen, false);
        messages = folder.search(unseenFlagTerm);

        } catch (MessagingException e) {
            System.out.println("Error: ImapClient() >> " + e);
        }
    }

	public ImapClient (String aprotocol, String astore, String aconnect, String afolder, String anusername, String apassword) {
	try {
        Properties props = new Properties();
//        props.put("mail.store.protocol","imaps");
        props.put(aprotocol, astore);

        session = Session.getDefaultInstance(props, null);
//        Store store = session.getStore("imaps");
//        store = session.getStore("imaps");
        store = session.getStore(astore);
//        store.connect("imap.gmail.com", anusername, apassword);
        store.connect(aconnect, anusername, apassword);

//        IMAPFolder folder = (IMAPFolder) store.getFolder("inbox");
//        folder = (IMAPFolder) store.getFolder("Hippson no-reply");
        folder = (IMAPFolder) store.getFolder(afolder);
        folder.open(Folder.READ_WRITE); // READ_ONLY

        Flags seen = new Flags(Flags.Flag.SEEN);
        FlagTerm unseenFlagTerm = new FlagTerm( seen, false);
        messages = folder.search(unseenFlagTerm);

        } catch (MessagingException e) {
            System.out.println("Error: ImapClient() >> " + e);
        }
    }
    
    public void readMsgs() {
        System.out.println(messages.length);
	   	try {
	        for (int i = 0; i < messages.length; i++) {
	            System.out.println("Message " + (i));
	            System.out.println("From : " + messages[i].getFrom()[0].toString());
	            System.out.println("Subject : " + messages[i].getSubject());
	            try {
	                System.out.println("Body: " + messages[i].getContent().toString());
	            } catch (IOException ex) {
	                System.out.println(ex);
	            }
	        }    
	    } catch (MessagingException e) {
	        System.out.println("Error: readMsgs() >> " + e);
	    }
    }
    
    public void markAsSeen(String from) {
	   	try {
	        for (int i = 0; i < messages.length; i++) {
	        	messages[i].setFlag(Flag.SEEN, true);
	        }    
	    } catch (MessagingException e) {
	        e.printStackTrace();
	    }
    }

    private String getTextFromMessage(Message message) throws MessagingException, IOException {
//    	System.out.println("ContentType : " + message.getContentType());
        String result = "";
        this.lastReadPlainText = "";
        if ( message.isMimeType("text/*")  ) {
        	this.lastReadPlainText = message.getContent().toString();
            return this.lastReadPlainText;
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart)message.getContent();
            this.lastReadHTML = "";
            for (int i = 0; i < mimeMultipart.getCount(); i ++){
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
//            	System.out.println("bodyPart ContentType : " + bodyPart.getContentType());
            	if ( bodyPart.isMimeType("text/plain") ) {
            		this.lastReadPlainText += bodyPart.getContent();
                    result += "\n" + bodyPart.getContent();
                } else if (bodyPart.isMimeType("text/html")){
                    String html = (String) bodyPart.getContent();
            		this.lastReadHTML += html;
                    result += "\n" + org.jsoup.Jsoup.parse(html).text();
                } else {
                	// Неизвестный тип почтового сообщения
                }
            }
            lastReadMessage = result;
            return result;
        }
        return "";
    }
    
    private String getHTMLFromCoriteMessage(Message message) throws MessagingException, IOException {
//    	System.out.println("ContentType : " + message.getContentType());
        String result = "";
        this.lastReadPlainText = "";
        if ( message.isMimeType("text/*")  ) {
        	this.lastReadPlainText = message.getContent().toString();
            return this.lastReadPlainText;
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart)message.getContent();
            this.lastReadHTML = "";
            for (int i = 0; i < mimeMultipart.getCount(); i ++){
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            	if ( bodyPart.isMimeType("text/plain") ) {
            		this.lastReadPlainText += bodyPart.getContent();
                    result += "\n" + bodyPart.getContent();
                } else if (bodyPart.isMimeType("text/html")) {
                    String html = (String) bodyPart.getContent();
            		this.lastReadHTML += html;
                    result += "\n" + html;
                } else if ( bodyPart.isMimeType("multipart/*") ) {
//                	System.out.println("bodyPart.isMimeType('multipart/*') >> " + bodyPart.getContentType());
//                	System.out.println("getLineCount >> " + bodyPart.getSize());
//                	System.out.println("getContent >> " + bodyPart.getContent().toString());
                	
                	MimeMultipart mimeMultipart2 = (MimeMultipart) bodyPart.getContent();
                	
//                	System.out.println("mimeMultipart.getCount() >> " + mimeMultipart2.getCount());
//                	System.out.println( ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> \n");
//                	System.out.println( "mimeMultipart2.getBodyPart(0).getContentType() >> \n" + mimeMultipart2.getBodyPart(0).getContentType() );
//                	System.out.println( mimeMultipart2.getBodyPart(0).getContent().toString() );
                	return mimeMultipart2.getBodyPart(0).getContent().toString();
                } else {
                	// Неизвестный тип почтового сообщения
                	System.err.println("Unknown type of BodyPart");
                }
            }
            lastReadMessage = result;
            return result;
        }
        return "";
    }
    
    // Перебирает все непросмотренные письма, выбирает первое, которое пришло от from и возвращает true, если в нем есть текст subсontent
    public boolean сontentContains (String from, String to, String subсontent) /*throws Exception*/ {
	   	try {
	        for (int i = 0; i < messages.length; i++) {
	        	String content = getTextFromMessage(messages[i]);
	        	if ( messages[i].getRecipients(Message.RecipientType.TO)[0].toString().contains(to) && messages[i].getFrom()[0].toString().contains(from) ) { // Сообщение пришло от from
	        		messages[i].setFlag(Flag.SEEN, true); 
//	        		message[i].setFlag(Flag.DELETED, true); 
	        		return content.contains(subсontent); // Есть ли искомый текст
	        	} else { // Восстанавливаем для всех "не наших" писем флаг "не просмотрено" (чтобы не нарушать состояние почты из-за автоматического перебора писем)
	        		messages[i].setFlag(Flag.SEEN, false); 
	        	}
	        }    
	    } catch (MessagingException e) {
	        System.err.println("Error: ImapClient.сontentContains() >> " + e);
        } catch (IOException ex) {
            System.err.println("IO Error: ImapClient.сontentContains() >> " + ex);
        } 
    	return false;
    }

    // Перебирает все непросмотренные письма и выбирает первое, которое пришло от from и содержит текст prefix в теле письма.
    public String getURLfromContent (String from, String to, String prefix) /*throws Exception*/ {
//    	String result = "";
	   	try {
	        for (int i = 0; i < messages.length; i++) {
//	        	String content = message[i].getContent().toString(); // Все содержимое если только текст (не Multipart)
	        	String content = getTextFromMessage(messages[i]);
    	        System.out.println("content: readMsgs() >> " + content);
    	        System.out.println("from: readMsgs() >> " + messages[i].getFrom()[0].toString());
	        	if (messages[i].getRecipients(Message.RecipientType.TO)[0].toString().contains(to) && messages[i].getFrom()[0].toString().contains(from) && content.contains(prefix)) { // Сообщение удовлетворяет заданным параметрам
	        		messages[i].setFlag(Flag.SEEN, true); 
//	        		message[i].setFlag(Flag.DELETED, true);
	        		String result = Pattern.compile(".+(" + prefix + ".{43}).+", Pattern.DOTALL).matcher(content).replaceFirst("$1"); // Выдергиваем ссылку
	    	        System.out.println("URL: readMsgs() >> " + result);
	        		return result; 
	        	} else { // Восстанавливаем для всех "не наших" писем флаг "не просмотрено" (чтобы не нарушать состояние почты из-за автоматического перебора писем)
	        		messages[i].setFlag(Flag.SEEN, false); 
	        	}
	        }    
	    } catch (MessagingException e) {
	        System.err.println("Error: readMsgs() >> " + e);
        } catch (IOException ex) {
            System.err.println("IO Error: readMsgs() >> " + ex);
        }
    	return ""; 
    }

    // Перебирает все непросмотренные письма и выбирает первое, которое пришло от from и было направлено to и по selector возвращает ссылку
    public String getHref (String from, String to, String selector) {
	   	try {
	        for (int i = 0; i < messages.length; i++) {
//    	        System.out.println( "from: readMsgs() >> " + message[i].getFrom()[0].toString() );
//    	        System.out.println( "to: readMsgs() >> " + message[i].getRecipients(Message.RecipientType.TO)[0].toString() );
   	     // Сообщение удовлетворяет заданным параметрам, то есть пришло от from и было направлено to
	        	if (messages[i].getRecipients(Message.RecipientType.TO)[0].toString().contains(to) && messages[i].getFrom()[0].toString().contains(from)) { 
		        	String html = getHTMLFromCoriteMessage(messages[i]);
		        	
//	    	        System.out.println("before getHTMLFromCoriteMessage >> ");
		        	Element link = Jsoup.parse(html).select(selector).first();
		        	if (link != null) {
		        		String linkHref = link.attr("href");
		        		messages[i].setFlag(Flag.SEEN, true); 
	//	        		message[i].setFlag(Flag.DELETED, true);
//		    	        System.out.println("linkHref >> " + linkHref);
		        		
		        		return linkHref;
		        	}
	        	}
	        	// Восстанавливаем для всех "не наших" писем флаг "не просмотрено" (чтобы не нарушать состояние почты из-за автоматического перебора писем)
	        	messages[i].setFlag(Flag.SEEN, false); 
	        }    
	    } catch (MessagingException e) {
	        System.err.println("Error: readMsgs() >> " + e);
        } catch (IOException ex) {
            System.err.println("IO Error: readMsgs() >> " + ex);
        }
    	return ""; 
    }
    
    // Перебирает все непросмотренные письма и выбирает первое, которое пришло от from и было направлено to и по selector возвращает ссылку
    public String getHref (String from, String to, String selector, String content) {
	   	try {
	        for (int i = 0; i < messages.length; i++) {
//    	        System.out.println( "from: readMsgs() >> " + message[i].getFrom()[0].toString() );
//    	        System.out.println( "to: readMsgs() >> " + message[i].getRecipients(Message.RecipientType.TO)[0].toString() );
   	     // Сообщение удовлетворяет заданным параметрам, то есть пришло от from и было направлено to
	        	if (messages[i].getRecipients(Message.RecipientType.TO)[0].toString().contains(to) && messages[i].getFrom()[0].toString().contains(from)) { 
		        	String html = getHTMLFromCoriteMessage(messages[i]);
		        	
		        	if ( html.contains(content) ) {
	//	    	        System.out.println("before getHTMLFromCoriteMessage >> ");
			        	Element link = Jsoup.parse(html).select(selector).first();
			        	if (link != null) {
			        		String linkHref = link.attr("href");
			        		messages[i].setFlag(Flag.SEEN, true); 
		//	        		message[i].setFlag(Flag.DELETED, true);
	//		    	        System.out.println("linkHref >> " + linkHref);
			        		
			        		return linkHref;
			        	}
		        	}
	        	}
	        	// Восстанавливаем для всех "не наших" писем флаг "не просмотрено" (чтобы не нарушать состояние почты из-за автоматического перебора писем)
	        	messages[i].setFlag(Flag.SEEN, false); 
	        }    
	    } catch (MessagingException e) {
	        System.err.println("Error: readMsgs() >> " + e);
        } catch (IOException ex) {
            System.err.println("IO Error: readMsgs() >> " + ex);
        }
    	return ""; 
    }
  
    public void close() {
	    try {
	        folder.close(false);
	        store.close();    	
	    } catch (MessagingException e) {
	        System.out.println("Error: close() >> " + e);
	    }
    }
}
