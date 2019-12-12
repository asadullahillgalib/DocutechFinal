package io.naztech.nuxeoclient.constants;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * @author Md. Mahbub Hasan Mohiuddin
 * @since 2019-09-11
 **/
public final class Constants {

	//Left square brace
	public static final String STR_LSB = "[";
	//Right square brace
	public static final String STR_RSB = "]";

	public static final String STR_OK = "OK";
	
	public static final String AES = "AES";
	public static final String SHA_1 = "SHA-1";
	public static final String ENCODING = "UTF-8";
	
	public static final String DB_NULL_STR = "?";
	public static final String FILE_SEPARATOR = File.separator;

	/*HTTP config*/
	public static final String USER_AGENT = "Mozilla/5.0";
	public static final String HTTP_CONTENT_TYPE = "Content-type";
	public static final String HTTP_TEXT_CONTENT_VALUE = "text/plain";
	public static final String HTTP_JSON_CONTENT_VALUE = "application/json";
	public static final String HTTP_XML_CONTENT_VALUE = "text/xml";
	
	/* mail Content Type */
	
	public static final String TEXT_HTML_CONTENT_VALUE = "text/html";
	public static final String MULTIPLE_PART = "multipart/*";
	public static final String MESSAGE_CONTENT = "message/rfc822";
	public static final String PDF_TYPE = "application/pdf" ;
	public static final String BINARY_TYPE = "application/binary" ;
	
	public static final String TEMPLATE = "TEMPLATE";
	public static final String EXCEPTION = "EXCEPTION";
	public static final String CUSTOMER = "CUSTOMER";
	public static final String DOC = "DOC";
	public static final String DOC_COUNT = "DOC_COUNT";
	public static final String DOC_DETAILS = "DOC_DETAILS";
	public static final String DOCUTECH = "docutech";
	public static final String JSON_REQUEST = "jsonRequest";
	public static final String IMAP = "imap";
	public static final String POP3 = "pop3";
	public static final String INBOX = "INBOX";
	public static final String PARSED = "Parsed";
	public static final String EMAIL = "EMAIL";
	public static final String FTP = "FTP";
	public static final String DOT = ".";
	public static final String UNDERSCORE = "_";
	//public static final String SELECT = "SELECT";
	
	public static final String INVOICE = "INVOICE";
	public static final String PDF = ".pdf";
	public static final String XML = "xml";
	public static final String ONEDRIVE_URL_PREFIX = "https://1drv.ms/";
	public static final String ONEDRIVE_URL_PREFIX2 = "onedrive.live.com/redir";
	public static final String HEADERFIELD="Content-Disposition";
	public static final String HEADER_INDEX_DROPBOX="filename*=UTF-8";
	public static final String HEADER_INDEX_ONEDRIVE="filename=";
	public static final String ONEDRIVE_REDIR="redir";
	public static final String ONEDRIVE_DOWNLOAD="download";
	public static final String INDEX1="filename=";
	public static final String INDEX2="\";filename*=UTF-8";
	public static final String GOOGLEDRIVE_FINAL_URL1="https://drive.google.com/uc?authuser=0&id=";
	public static final String GOOGLEDRIVE_FINAL_URL2="&export=download";
	
	public static final SimpleDateFormat DTF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat DTF_SENT = new SimpleDateFormat("yyyyMMdd_HH:mm:ss");
	/*
	 * public static int fileCount = 0;
	 * 
	 * // Downloaded file name public static String D_F_N = null;
	 */
}
