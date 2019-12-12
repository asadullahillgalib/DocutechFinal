package io.naztech.nuxeoclient.utility;

import java.util.Date;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.search.SearchTerm;

public class EmailSearchTerm extends SearchTerm {

	/**
	 * Default serial versin ID
	 */
	private static final long serialVersionUID = 1L;
	
	private Date afterDate;
	private String fromEmail;
    
    public EmailSearchTerm(Date afterDate, String fromEmail) {
        this.afterDate = afterDate;
        this.fromEmail = fromEmail;
    }
    
	@Override
	public boolean match(Message message) {
		try {
            Address[] fromAddress = message.getFrom();
            
            if (fromAddress != null && fromAddress.length > 0) {
            	
            	String from = ((InternetAddress) fromAddress[0]).getAddress();
            	
                if (from.contains(fromEmail) && message.getSentDate().after(afterDate)) {
                    return true;
                }
            }
            else {
            	if (message.getSentDate().after(afterDate)) {
                    return true;
                }
            }
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
         
        return false;
	}

}
