package io.naztech.nuxeoclient.model;

import java.math.BigInteger;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class DocCount {
	
	private static Map<String, String> sql2BeanMap = null;
	private static Map<String, String> rs2BeanMap = null;

	private Integer isMax;
	private BigInteger fileDownloadId;
	private Integer docCountId;
	private Integer docCountVer;
	private Integer noOfFilesDownloaded;
	private Integer isAbbyProcessed;
	private Integer isAbbyError;
	private Integer isProcessedSuccessfully;
	private Integer idUserModKey;
	private Integer docCountKey;

	private String senderAddress;
	private String downloadSource;
	private String docType;
	private String subject;
	private String fileName;

	private Date modifiedOn;

	// getter/setter

	public Integer getNoOfFilesDownloaded() {
		return noOfFilesDownloaded;
	}

	public Integer getIsMax() {
		return isMax;
	}

	public void setIsMax(Integer isMax) {
		this.isMax = isMax;
	}

	public BigInteger getFileDownloadId() {
		return fileDownloadId;
	}

	public void setFileDownloadId(BigInteger fileDownloadId) {
		this.fileDownloadId = fileDownloadId;
	}

	public void setNoOfFilesDownloaded(Integer noOfFilesDownloaded) {
		this.noOfFilesDownloaded = noOfFilesDownloaded;
	}

	public Integer getIsAbbyProcessed() {
		return isAbbyProcessed;
	}

	public void setIsAbbyProcessed(Integer isAbbyProcessed) {
		this.isAbbyProcessed = isAbbyProcessed;
	}

	public Integer getIsAbbyError() {
		return isAbbyError;
	}

	public void setIsAbbyError(Integer isAbbyError) {
		this.isAbbyError = isAbbyError;
	}

	public Integer getIsProcessedSuccessfully() {
		return isProcessedSuccessfully;
	}

	public void setIsProcessedSuccessfully(Integer isProcessedSuccessfully) {
		this.isProcessedSuccessfully = isProcessedSuccessfully;
	}

	public String getSenderAddress() {
		return senderAddress;
	}

	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}

	public String getDownloadSource() {
		return downloadSource;
	}

	public void setDownloadSource(String downloadSource) {
		this.downloadSource = downloadSource;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Integer getDocCountId() {
		return docCountId;
	}

	public void setDocCountId(Integer docCountId) {
		this.docCountId = docCountId;
	}

	public Integer getDocCountVer() {
		return docCountVer;
	}

	public void setDocCountVer(Integer docCountVer) {
		this.docCountVer = docCountVer;
	}

	public Date getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public Integer getIdUserModKey() {
		return idUserModKey;
	}

	public Integer getDocCountKey() {
		return docCountKey;
	}

	public void setDocCountKey(Integer docCountKey) {
		this.docCountKey = docCountKey;
	}

	public void setIdUserModKey(Integer idUserModKey) {
		this.idUserModKey = idUserModKey;
	}

	public static final Map<String, String> getSql2BeanMap() {

		if (sql2BeanMap == null) {
			sql2BeanMap = new LinkedHashMap<String, String>();

			sql2BeanMap.put("@is_max", "isMax");
			sql2BeanMap.put("@dtt_mod", "modifiedOn");
			sql2BeanMap.put("@id_doc_count_key", "docCountId");
			sql2BeanMap.put("@id_doc_count_ver", "docCountVer");
			sql2BeanMap.put("@tx_sender_address", "senderAddress");
			sql2BeanMap.put("@tx_download_source", "downloadSource");
			sql2BeanMap.put("@tx_document_type", "docType");
			sql2BeanMap.put("@id_user_mod_key", "idUserModKey");
			sql2BeanMap.put("@ct_downloaded", "noOfFilesDownloaded");
			sql2BeanMap.put("@is_abbyy_processed", "isAbbyProcessed");
			sql2BeanMap.put("@is_abbyy_error", "isAbbyError");
			sql2BeanMap.put("@is_abbyy_successful", "isProcessedSuccessfully");
			sql2BeanMap.put("@tx_subject", "subject");
			sql2BeanMap.put("@tx_file_name", "fileName");
		}

		return sql2BeanMap;
	}

	public static final Map<String, String> getRs2BeanMap() {

		if (rs2BeanMap == null) {
			rs2BeanMap = new LinkedHashMap<String, String>();

			rs2BeanMap.put("id_doc_count_key", "docCountId");
			rs2BeanMap.put("id_doc_count_ver", "docCountVer");
			rs2BeanMap.put("ct_downloaded", "noOfFilesDownloaded");
			rs2BeanMap.put("dtt_mod", "modifiedOn");
			rs2BeanMap.put("is_abbyy_processed", "isAbbyProcessed");
			rs2BeanMap.put("is_abbyy_error", "isAbbyError");
			rs2BeanMap.put("is_abbyy_successful", "isProcessedSuccessfully");
			rs2BeanMap.put("tx_sender_address", "senderAddress");

			rs2BeanMap.put("tx_download_source", "downloadSource");
			rs2BeanMap.put("tx_document_type", "docType");
			rs2BeanMap.put("id_user_mod_key", "idUserModKey");
			rs2BeanMap.put("id_file_download", "fileDownloadId");
			rs2BeanMap.put("tx_subject", "subject");
			rs2BeanMap.put("tx_file_name", "fileName");

		}

		return rs2BeanMap;
	}

}
