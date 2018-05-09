package agency.july.sendmail;

public class Main {

//    private static com.sitrus.ssl.Sender sslSender = new com.sitrus.ssl.Sender("andrew.tverdokhleb@sitrus.com", "Boat32Trip%");
    
//    private static ImapClient client = new ImapClient ("mail.store.protocol", "imaps", "imap.gmail.com", "inbox", "andrew.tverdokhleb@sitrus.com", "Boat32Trip%");
    private static ImapClient client = new ImapClient ("mail.store.protocol", "imaps", "mail.plur.se", "inbox", "autotest@july.agency", "ghbdtn");
 
    public static void main(String[] args) throws Exception {
    	
    	// Send a message
//        sslSender.send("This is Subject2", "SSL: This is text2!", "andrew.tverdokhleb@sitrus.com", "andrew.tverdokhleb@sitrus.com");
    	
        // Mail client
//    	client.readMsgs();
//    	System.out.println(client.сontentContains("no-reply-dev@hippsonmarket.se", "Din annons är publicerad")); // no-reply-dev@hippsonmarket.se //no-reply@dev.sitrus.com
//    	System.out.println(client.сontentContains("no-reply@dev.sitrus.com", "andrew.tverdokhleb@sitrus.com", "Din annons")); //  är publicerad no-reply-dev@hippsonmarket.se //no-reply@dev.sitrus.com

//    	System.out.println(client.getURLfromContent("no-reply@dev.corite.com", "autotest-corite-newuser@july.agency", "https://mandrillapp.com/track/click" ));
    	
    	client.getHref("no-reply@dev.corite.com", "autotest-corite-newuser@july.agency", "a.confirm_registration_link");
//    	System.out.println("client.getHTML \n>>>>>>>>>>>>\n" + client.getHTML("no-reply@dev.corite.com"));
        client.close();
    }

}
