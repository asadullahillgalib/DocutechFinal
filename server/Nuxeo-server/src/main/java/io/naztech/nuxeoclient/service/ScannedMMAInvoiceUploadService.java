package io.naztech.nuxeoclient.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.FilenameUtils;
import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.objects.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import io.naztech.nuxeoclient.constants.ActionType;
import io.naztech.nuxeoclient.constants.Constants;
import io.naztech.nuxeoclient.model.DocumentWrapper;
import io.naztech.nuxeoclient.model.Template;
import io.naztech.nuxeoclient.model.invoice.MMA.Documents;
/* import lombok.extern.slf4j.Slf4j; */

/**
 * @author Asadullah.Galib
 * @since 2019-10-21
 **/

@Named
/* @Slf4j */
public class ScannedMMAInvoiceUploadService extends AbstractScannedInvoidUploadService {

	private static Logger log = LoggerFactory.getLogger(ScannedMMAInvoiceUploadService.class);

	@Autowired
	private NuxeoClient nuxeoClient;
	@Autowired
	NuxeoClientService serv;

	@Autowired
	private TemplateService templateService;

	@Autowired
	private DocumentService documentService;

	@Autowired
	private DocDetailsService docDetailsService;

	@Value("${import.mma.archiveFolder-path}")
	private String archiveFolderPath;

	@Value("${import.mma.badFolder-path}")
	private String badfolderPath;

	@Value("${import.mma.nuxeoFailedFolder-path}")
	private String nuxeoFailedFolderPath;

	@Value("${import.nuxeo.RepoPath}")
	private String repoPath;

	@Value("${import.mma.Name}")
	private String nuxeoinvoiceName;

	@Value("${import.mma.type}")
	private String nuxeoinvoiceType;

	@Value("${import.mma.prefix}")
	private String prefix;

	@Value("${import.nuxeo.mma.description}")
	private String desc;

	@Value("${import.pdfType}")
	private String pdfType;

	@Value("${import.processorType}")
	private String proType;

	@Value("${import.docType}")
	private String docType;

	@Value("${import.source}")
	private String sourceType;

	@Value("${archive.enabled}")
	private boolean enabled;

	/**
	 * Constructs a mmaInvoice object from the xml file
	 * 
	 * @param file
	 * @return mmaInvoice Object
	 * @throws IOException
	 */
	private Documents getInvoiceFromXml(File file) throws IOException {
		try {
			byte[] bytes = Files.readAllBytes(file.toPath());
			Unmarshaller ums = JAXBContext.newInstance(Documents.class).createUnmarshaller();
			XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new ByteArrayInputStream(bytes));
			JAXBElement<Documents> jaob = ums.unmarshal(reader, Documents.class);
			Documents ob = jaob.getValue();
			return ob;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new IOException("Error processing file.", e.getCause());
		}

	}

	/**
	 * Converts Invoice to DocumentRequest
	 * 
	 * @param inv
	 * @return DocumentWrapper object
	 */
	private DocumentWrapper convertInvoiceToDocumentReq(Documents inv) {
		if (inv == null) return null;
		try {
			DocumentWrapper ob = DocumentWrapper.createWithName(nuxeoinvoiceName, nuxeoinvoiceType);

			// ""inv.getMMADocumentDefinition().getAccountNumber()
			// inv.getMMADocumentDefinition().getCompanyAddress()
			// inv.getMMADocumentDefinition().getCompanyName()
			// inv.getMMADocumentDefinition().getCustomerAddress()
			// inv.getMMADocumentDefinition().getCustomerName()
			// inv.getMMADocumentDefinition().getEmail()
			// inv.getMMADocumentDefinition().getFaxNumber()
			// inv.getMMADocumentDefinition().getInsurancePremium()
			// inv.getMMADocumentDefinition().getPropertyAddress()
			// inv.getMMADocumentDefinition().getRentAdvance()
			// inv.getMMADocumentDefinition().getSupplierAddress()
			// inv.getMMADocumentDefinition().getSupplierName()
			// inv.getMMADocumentDefinition().getSupplierTelephoneNo()
			// inv.getMMADocumentDefinition().getTotalAmountDue()
			// inv.getMMADocumentDefinition().getVat()
			// inv.getMMADocumentDefinition().getVatNumber()

			// Setting attributes of the document wrapper object
			ob.setTitle(nuxeoinvoiceName);
			ob.setDescription(desc);
			ob.setPrefix(prefix);
			ob.setRepoPath(repoPath);
			ob.addAttribute("account_number", Long.toString(inv.getMMADocumentDefinition().getAccountNumber()));
			ob.addAttribute("company_address", inv.getMMADocumentDefinition().getCompanyAddress());
			ob.addAttribute("company_name", inv.getMMADocumentDefinition().getCompanyName());
			ob.addAttribute("customer_address", inv.getMMADocumentDefinition().getCustomerAddress());
			ob.addAttribute("customer_name", inv.getMMADocumentDefinition().getCustomerName());
			ob.addAttribute("insurance_premium", inv.getMMADocumentDefinition().getInsurancePremium().toString());
			ob.addAttribute("rent_advance", inv.getMMADocumentDefinition().getRentAdvance());
			ob.addAttribute("supplier_address", inv.getMMADocumentDefinition().getSupplierAddress());
			ob.addAttribute("supplier_name", inv.getMMADocumentDefinition().getSupplierName());
			ob.addAttribute("total_amount_due", inv.getMMADocumentDefinition().getTotalAmountDue());

			return ob;
		}
		catch (Exception e) {
			log.warn("Failed to convert to Document Wrapper Object", e);
			return null;
		}

	}

	/**
	 * Uploads the file to the Nuxeo server and moves the file from hotfolder to
	 * archive
	 * 
	 * @param invoiceInfoXml
	 * @param attachments
	 * @return request
	 * @throws IOException
	 */
	@Override
	public void uploadDocument(File invoiceInfoXml, List<File> attachments, boolean flag) throws IOException {
		int isSuccess = 0;
		LocalDate date = LocalDate.now();
		String strDate = date.toString();
		Template tmplt = new Template();
		io.naztech.nuxeoclient.model.Document document = new io.naztech.nuxeoclient.model.Document();
		io.naztech.nuxeoclient.model.DocDetails docdetails = new io.naztech.nuxeoclient.model.DocDetails();
		if (enabled && flag) {
			moveToArchive(archiveFolderPath, invoiceInfoXml, attachments, strDate);
		}
		// Setup Nuxeo Connection
		serv.setNuxeoClient(nuxeoClient);
		Documents result = null;

		try {
			result = getInvoiceFromXml(invoiceInfoXml);
		}
		catch (IOException ex) {

			tmplt.setErrorIssue(ex.getCause().toString());

			log.error("Error parsing attachment: {}", ex.getMessage());
		}
		sourceInitial();
		String name = attachments.get(0).getName();
		if (name.contains(sourceType)) {
			tmplt.setDocSource(getSource(sourceType));
		}
		tmplt.setPdfType(getSource(pdfType));
		tmplt.setProcessorType(getSource(proType));
		tmplt.setDocType(getSource(docType));

		if (null != result) {

			tmplt.setClientName(result.getMMADocumentDefinition().getCustomerName());
			tmplt.setReceivedDocId(tmplt.getClientName().substring(0, 5) + "_" + Long.toString(result.getMMADocumentDefinition().getAccountNumber()));

			DocumentWrapper req = convertInvoiceToDocumentReq(result);
			tmplt.setSupplierName(req.getTitle());

			if (result.getMMADocumentDefinition().getCustomerName().equals(null) && result.getMMADocumentDefinition().getSupplierName().equals(null)
			        && result.getMMADocumentDefinition().getTotalAmountDue().equals(null)) {

				log.error(invoiceInfoXml.getName() + " Moved to Bad Folder");

				moveToBadFolder(badfolderPath, invoiceInfoXml, attachments, strDate);

				tmplt.setIsSentToNuxeo(0);
				tmplt.setIsTemplateParsed(0);
				tmplt.setIsParsedSuccessful(0);

				isSuccess = 0;
			}
			else {
				isSuccess = 1;
				document.setAccountNumber(Long.toString(result.getMMADocumentDefinition().getAccountNumber()));
				// document.setAssociationNumber(valueToStringOrEmpty(result, "associationNo"));
				// document.setSupplierNumber(nullHandlerInteger(valueToStringOrEmpty(result,
				// "supplierNumber")));

				document.setCompanyAddress(result.getMMADocumentDefinition().getSupplierAddress());
				document.setContactNumber(result.getMMADocumentDefinition().getSupplierTelephoneNo());
				// document.setCurrency(result.getMMADocumentDefinition().getCurrency());
				document.setCustomerAddress(result.getMMADocumentDefinition().getCustomerAddress());
				// document.setCustomerNumber(Long.toString(result.getMMADocumentDefinition().getCustomerNumber()));
				// document.setCustomerOrderNo(Long.toString(result.getMMADocumentDefinition().getOrderNo()));
				document.setDeliverTo(result.getMMADocumentDefinition().getCustomerAddress());
				document.setDeliveryDetails(result.getMMADocumentDefinition().getCustomerAddress());
				// document.setDeliveryNote(valueToStringOrEmpty(result, "deliveryNo"));
				// document.setDeliveryNoteNo(nullHandlerInteger(Long.toString(result.getMMADocumentDefinition().getOrderNo())));
				document.setFaxNumber(result.getMMADocumentDefinition().getFaxNumber());
				document.setInvoiceAmount(Double.valueOf(stringSplitter(result.getMMADocumentDefinition().getTotalAmountDue())));
				// nullHandlerDouble(bigDescimalToString(result.getMMADocumentDefinition().getTotalAmountDue().toString())));
				// document.setInvoiceNumber(Long.toString(result.getMMADocumentDefinition().getCustomerNumber()));
				document.setInvoiceTo(result.getMMADocumentDefinition().getCustomerAddress());
				document.setVat(nullHandlerDouble(bigDescimalToString(result.getMMADocumentDefinition().getVat().toString())));
				document.setTotalBeforeVat(Double.valueOf(stringSplitter(result.getMMADocumentDefinition().getTotalAmountDue())));
				document.setVatNumber(result.getMMADocumentDefinition().getVatNumber());
				document.setNetInvoiceTotal(Double.valueOf(stringSplitter(result.getMMADocumentDefinition().getTotalAmountDue())));
				// document.setVoucherNumber(valueToStringOrEmpty(result, "voucherNumber"));
				document.setInsurancePremium(new BigDecimal(result.getMMADocumentDefinition().getInsurancePremium().toString()).doubleValue());

				try {
					document.setDespatchDate(
					        new SimpleDateFormat("yyyy/MM/dd").parse(dateFormatter(result.getMMADocumentDefinition().getInvoiceDate())));
					// document.setDespatchDate(
					// new
					// SimpleDateFormat("yyyy/MM/dd").parse(result.getMMADocumentDefinition().getInvoiceDate()));

					document.setDocumentDate(
					        new SimpleDateFormat("yyyy/MM/dd").parse(dateFormatter(result.getMMADocumentDefinition().getInvoiceDate())));
					// document.setDocumentDate(
					// new
					// SimpleDateFormat("yyyy/MM/dd").parse(result.getMMADocumentDefinition().getInvoiceDate()));

					// docDetails table

				}
				catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			// Uploading the file to Nuxeo Server
			try {

				if (isSuccess == 1) {

					req.setFiles(attachments);
					// Uploading File to Nuxeo Server
					Document res = serv.createDocument(req);

					if (res == null && flag) {

						tmplt.setIsSentToNuxeo(0);

						moveToNuxeoFailed(nuxeoFailedFolderPath, invoiceInfoXml, attachments, strDate);

						log.error(attachments.get(0).getName() + " Moved to NuxeoFailed Folder");
					}
					else {

						tmplt.setIsSentToNuxeo(1);
						tmplt.setIsTemplateParsed(1);
						tmplt.setIsParsedSuccessful(1);

						log.info("Successfully Uploaded Document Into Nuxeo: " + attachments.get(0).getName());
						Files.delete(Paths.get(invoiceInfoXml.getAbsolutePath()));
						for (File doc : attachments) {
							Files.delete(Paths.get(doc.getAbsolutePath()));

						}

					}
				}
			}
			catch (Exception e) {
				log.error("Moved to Nuxeo Failed ", e);

				// tmplt.setIsSentToNuxeo(0);
				//
				// moveToNuxeoFailed(nuxeoFailedFolderPath, invoiceInfoXml, attachments,
				// strDate);
				tmplt.setErrorIssue(e.getMessage());
				tmplt.setErrorIssueSource("NuxeoFailed");
			}
		}
		// Insert Data into Database
		Optional<io.naztech.nuxeoclient.model.Template> tmp = templateService.processAction(ActionType.NEW.toString(), tmplt, Constants.TEMPLATE).stream().findFirst();

		if (tmp.isPresent()) {

			Optional<io.naztech.nuxeoclient.model.Document> documentTemp = documentService.processAction(ActionType.NEW.toString(), document, Constants.DOC).stream()
			        .findFirst();

			if (documentTemp.isPresent()) {

				io.naztech.nuxeoclient.model.Document documentData = documentTemp.get();
				documentData.setDocId(documentTemp.get().getDocId());
				documentService.processAction(ActionType.NEW.toString(), documentData, Constants.CUSTOMER);
				docdetails.setRent(result.getMMADocumentDefinition().getRentAdvance());
				docdetails.setNetValue(Double.valueOf(stringSplitter(result.getMMADocumentDefinition().getTotalAmountDue())));
				docdetails.setTotalPrice(Double.valueOf(stringSplitter(result.getMMADocumentDefinition().getTotalAmountDue())));
				docdetails.setValueOfGoods(Double.valueOf(stringSplitter(result.getMMADocumentDefinition().getTotalAmountDue())));
				docdetails.setDocId(documentTemp.get().getDocId());
				docDetailsService.processAction(ActionType.NEW.toString(), docdetails, Constants.DOC_DETAILS);
				//				for (int i = 0; i < result.getMMADocumentDefinition().getInvoiceTable().size(); i++) {
				//					docdetails.setItemQty(nullHandlerInteger(result.getMMADocumentDefinition().getInvoiceTable().get(i).getQty()));
				//					docdetails.setPartNo(nullHandlerInteger(result.getMMADocumentDefinition().getInvoiceTable().get(i).getPartNo()));
				//					// docdetails.setReferenceNo(referenceNo);
				//					//			docdetails.setTrade(trade);
				//					docdetails.setUnitPrice(nullHandlerDouble(result.getMMADocumentDefinition().getInvoiceTable().get(i).getUnitPrice()));
				//					//			docdetails.setNetValue(netValue);
				//					docdetails.setValueOfGoods(nullHandlerDouble(result.getMMADocumentDefinition().getInvoiceTable().get(i).getTotal()));
				//					//			docdetails.setInsurancePremium(insurancePremium);
				//					//			docdetails.setItemCode(valueToStringOrEmpty(result, "itemCode"));
				//					docdetails.setItemDescription(result.getMMADocumentDefinition().getInvoiceTable().get(i).getDesciption());
				//					//			docdetails.setItemName(valueToStringOrEmpty(result, "itemName"));
				//					//			docdetails.setOrderId(valueToStringOrEmpty(result, "customerOrderNo"));
				//					//			docdetails.setPack(valueToStringOrEmpty(result, "pack"));
				//					//			docdetails.setPaymentDetails(valueToStringOrEmpty(result, "paymentDetails"));
				//					//			docdetails.setPropertyAddress(valueToStringOrEmpty(result, "propertyAddress"));
				//					//			docdetails.setRent(valueToStringOrEmpty(result, "rent"));
				//
				//					docdetails.setTotalPrice(nullHandlerDouble(result.getMMADocumentDefinition().getInvoiceTable().get(i).getTotal()));
				//					docdetails.setDocId(documentTemp.get().getDocId());
				//					docDetailsService.processAction(ActionType.NEW.toString(), docdetails, Constants.DOC_DETAILS);
				//
				//				}
			}

			else {
				log.info("Document Data is not available");
			}
		}
	}

	/**
	 * @author mahbub.hasan
	 * @since 2019-12-08
	 *        Moved scheduler from Controller to service class
	 *        and deleted unnecessary controller class ScannedMMAInvoiceUploadController.java
	 */

	/*	public void mmaInvoiceUpload() throws IOException {
			File[] fileList = new File(mmaFolderPath).listFiles();
			log.info("Scanning for files in MMA folder in schedule; " + fileList);
			if (fileList == null || fileList.length < 2)
				return;
	
			// Getting all the xml files in the hotfolder
			List<File> xmllist = Arrays.asList(fileList).parallelStream()
					.filter(file -> "xml".equals(FilenameUtils.getExtension(file.getName()))).collect(Collectors.toList());
	
			// Getting all the pdf files of the corresponding xml files in the hotfolder
			for (File xml : xmllist) {
				String pdf = xml.getAbsolutePath().replaceAll("\\.xml", "\\.pdf");
				File pdfFile = new File(pdf);
	
				if (pdfFile.exists())
					uploadDocument(xml, Arrays.asList(pdfFile), true);
			}
		}*/

	/**
	 * @author mahbub.hasan
	 * @since 2019-12-09
	 **/
	/*	public void processDoc(List<File> fileList) throws IOException {
	
			if (fileList == null || fileList.size() < 2) return;
	
			List<File> xmllist = fileList.parallelStream().filter(file -> "xml".equals(FilenameUtils.getExtension(file.getName())))
			        .collect(Collectors.toList());
	
			// Getting all the pdf files of the corresponding xml files in the hotfolder
			for (File xml : xmllist) {
				String pdf = xml.getAbsolutePath().replaceAll("\\.xml", "\\.pdf");
				File pdfFile = new File(pdf);
				if (pdfFile.exists()) uploadDocument(xml, Arrays.asList(pdfFile), true);
			}
		}*/
}
