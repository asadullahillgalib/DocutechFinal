package io.naztech.nuxeoclient.itextServices;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.FilenameUtils;
import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.objects.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.duallab.pdf2data.Pdf2DataExtractor;
import com.duallab.pdf2data.result.ParsingResult;
import com.duallab.pdf2data.template.Template;
import com.itextpdf.licensekey.LicenseKey;

import io.naztech.nuxeoclient.constants.Constants;
import io.naztech.nuxeoclient.model.DocumentWrapper;
import io.naztech.nuxeoclient.service.DocumentService;
import io.naztech.nuxeoclient.service.NuxeoClientService;
import io.naztech.nuxeoclient.service.TemplateService;

/**
 * @author Asadullah.galib
 * @since 2019-09-15
 **/
@Named
public class ScannedItextNmbsInvoiceService extends AbstractItextScannedInvoiceUploadService {
	private static Logger log = LoggerFactory.getLogger(ScannedItextNmbsInvoiceService.class);
	@Autowired
	private NuxeoClient nuxeoClient;
	@Autowired
	NuxeoClientService serv;

	@Autowired
	private TemplateService templateService;

	@Autowired
	private DocumentService documentService;

	@Value("${import.template.nmbsTemplate-path}")
	private String nmbsTemplatePath;

	@Value("${import.nmbs.archiveFolder-path}")
	private String archiveFolderPath;

	@Value("${import.nmbs.badFolder-path}")
	private String badfolderPath;

	@Value("${import.nmbs.nuxeoFailedFolder-path}")
	private String nuxeoFailedFolderPath;

	@Value("${archive.enabled}")
	private boolean enabled;

	/**
	 * Constructs a NmbsInvoice object from the xml file
	 * 
	 * @param file
	 * @return NmbsInvoice Object
	 * @throws IOException
	 */
	private ParsingResult getResultFromPdf(File file) throws IOException {

		LicenseKey.loadLicenseFile(getClassLoader().getResource("itextkey1567936395276_0.xml").getPath());
		Template template;
		try {
			template = Pdf2DataExtractor.parseTemplateFromPDF(nmbsTemplatePath);
			Pdf2DataExtractor extractor = new Pdf2DataExtractor(template);
			ParsingResult result = extractor.recognize(file.getAbsolutePath());
			return result;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
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
	private DocumentWrapper convertInvoiceToDocumentReq(ParsingResult inv) {
		if (inv == null) return null;
		try {
			DocumentWrapper ob = DocumentWrapper.createWithName("Nmbs", "Nmbs");

			// Setting attributes of the document wrapper object
			ob.setTitle("Nmbs");
			ob.setDescription("");
			ob.setPrefix("nmbs:");
			ob.setRepoPath("/default-domain/workspaces/Sample Content/Invoice");
			ob.addAttribute("client_name", valueToStringOrEmpty(inv, "clientName"));
			ob.addAttribute("company_address", valueToStringOrEmpty(inv, "companyAddress"));
			ob.addAttribute("customer_order_no", valueToStringOrEmpty(inv, "customerOrderNo"));
			ob.addAttribute("deliver_to", valueToStringOrEmpty(inv, "deliverTo"));
			ob.addAttribute("delivery_note_no", valueToStringOrEmpty(inv, "deliveryNoteNo"));
			ob.addAttribute("invoice_date", valueToStringOrEmpty(inv, "invoiceDate"));
			ob.addAttribute("invoice_to", valueToStringOrEmpty(inv, "invoiceTo"));
			ob.addAttribute("net_invoice_total", valueToStringOrEmpty(inv, "netInvoiceTotal"));
			ob.addAttribute("supplier_name", valueToStringOrEmpty(inv, "supplierName"));
			ob.addAttribute("supplier_number", valueToStringOrEmpty(inv, "supplierNumber"));
			ob.addAttribute("vat", valueToStringOrEmpty(inv, "vat"));
			ob.addAttribute("voucher_number", valueToStringOrEmpty(inv, "voucherNumber"));

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
	public void uploadDocument(List<File> attachments, boolean flag) throws IOException {
		// TODO Auto-generated method stub
		int isSuccess = 0;
		String resultErrors = null;
		LocalDate date = LocalDate.now();
		String strDate = date.toString();

		io.naztech.nuxeoclient.model.Template tmplt = new io.naztech.nuxeoclient.model.Template();
		io.naztech.nuxeoclient.model.Document document = new io.naztech.nuxeoclient.model.Document();

		if (enabled && flag) {
			moveToArchive(archiveFolderPath, attachments, strDate);
		}

		serv.setNuxeoClient(nuxeoClient);

		ParsingResult result = null;

		try {
			result = getResultFromPdf(attachments.get(0));
		}
		catch (IOException ex) {

			tmplt.setErrorIssue(ex.getCause().toString());

			log.error("Error parsing attachment: {}", ex.getMessage());
		}

		String name = attachments.get(0).getName();
		if (name.contains("EMAIL")) {
			tmplt.setDocSource("EMAIL");
		}
		else if (name.contains("FTP")) {
			tmplt.setDocSource("FTP");
		}

		tmplt.setPdfType("TEXT");
		tmplt.setProcessorType("iTEXT");

		if (null != result) {

			try {
				resultErrors = getErrorsMessage(result, "errorsMessage");
			}
			catch (NoSuchFieldException e1) {
				e1.printStackTrace();
			}
			catch (SecurityException e1) {
				e1.printStackTrace();
			}
			catch (IllegalArgumentException e1) {
				e1.printStackTrace();
			}
			catch (IllegalAccessException e1) {
				e1.printStackTrace();
			}

			if (null != resultErrors) {
				tmplt.setErrorIssue(resultErrors);
			}

			tmplt.setClientName(valueToStringOrEmpty(result, "clientName"));
			tmplt.setReceivedDocId(tmplt.getClientName() + "_" + valueToStringOrEmpty(result, "supplierNumber" + ""));

			DocumentWrapper req = convertInvoiceToDocumentReq(result);
			tmplt.setSupplierName(req.getTitle());

			if (result.getErrorsNumber() >= 3) {

				log.error(attachments.get(0).getName() + " Moved to Bad Folder");

				moveToBadFolder(badfolderPath, attachments, strDate);

				tmplt.setIsSentToNuxeo(0);
				tmplt.setIsTemplateParsed(0);
				tmplt.setIsParsedSuccessful(0);

				isSuccess = 0;
			}
			else {
				isSuccess = 1;
				document.setAccountNumber(valueToStringOrEmpty(result, "accountNo"));
				document.setAssociationNumber(valueToStringOrEmpty(result, "associationNo"));
				document.setSupplierNumber(valueToStringOrEmpty(result, "supplierNumber"));
				document.setCompanyAddress(valueToStringOrEmpty(result, "companyAddress"));
				document.setContactNumber(valueToStringOrEmpty(result, "contactNumber"));
				document.setCurrency(valueToStringOrEmpty(result, "currency"));
				document.setCustomerAddress(valueToStringOrEmpty(result, "invoiceTo"));
				document.setCustomerNumber(valueToStringOrEmpty(result, "customerNo"));
				document.setCustomerOrderNo(valueToStringOrEmpty(result, "customerOrderNo"));
				document.setDeliverTo(valueToStringOrEmpty(result, "deliveryTo"));
				document.setDeliveryDetails(valueToStringOrEmpty(result, "deliveryDetails"));
				document.setDeliveryNote(valueToStringOrEmpty(result, "deliveryNo"));
				document.setDeliveryNoteNo(Integer.parseInt(valueToStringOrEmpty(result, "deliveryNoteNo")));
				document.setFaxNumber(valueToStringOrEmpty(result, "faxNumber"));
				document.setInvoiceAmount(Double.parseDouble(valueToStringOrEmpty(result, "invoiceAmount")));
				document.setInvoiceNumber(valueToStringOrEmpty(result, "supplierNumber"));
				document.setInvoiceTo(valueToStringOrEmpty(result, "invoiceTo"));
				document.setItemCode(valueToStringOrEmpty(result, "itemCode"));
				document.setItemDescription(valueToStringOrEmpty(result, "itemDescription"));
				document.setItemName(valueToStringOrEmpty(result, "itemName"));
				if (valueToStringOrEmpty(result, "netInvoiceTotal").isEmpty()) {
					document.setNetInvoiceTotal(0.0);
				}
				else {
					document.setNetInvoiceTotal(Double.parseDouble(valueToStringOrEmpty(result, "netInvoiceTotal")));
				}
				document.setOrderId(valueToStringOrEmpty(result, "customerOrderNo"));
				document.setPack(valueToStringOrEmpty(result, "pack"));
				document.setPaymentDetails(valueToStringOrEmpty(result, "paymentDetails"));
				document.setPropertyAddress(valueToStringOrEmpty(result, "propertyAddress"));
				document.setRent(valueToStringOrEmpty(result, "rent"));
				document.setTotalBeforeVat(Double.parseDouble(valueToStringOrEmpty(result, "totalBeforeVat")));
				if (valueToStringOrEmpty(result, "netInvoiceTotal").isEmpty()) {
					document.setTotalPrice(0.0);
				}
				else {
					document.setTotalPrice(Double.parseDouble(valueToStringOrEmpty(result, "netInvoiceTotal")));
				}
				if (valueToStringOrEmpty(result, "vat").isEmpty()) {
					document.setVat(0.0);
				}
				else {
					document.setVat(Double.parseDouble(valueToStringOrEmpty(result, "vat")));
				}
				document.setVatNumber(valueToStringOrEmpty(result, "vatNumber"));
				document.setVoucherNumber(valueToStringOrEmpty(result, "voucherNumber"));

				try {
					document.setDespatchDate(new SimpleDateFormat("yyyy/MM/dd").parse(valueToDateOrDefault(result, "despatchDate")));
					document.setDocumentDate(new SimpleDateFormat("yyyy/MM/dd").parse(valueToDateOrDefault(result, "invoiceDate")));
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

					Document res = serv.createDocument(req);

					if (res == null && flag) {

						tmplt.setIsSentToNuxeo(0);

						moveToNuxeoFailed(nuxeoFailedFolderPath, attachments, strDate);

						log.error(attachments.get(0).getName() + " Moved to NuxeoFailed Folder");
					}
					else {

						tmplt.setIsSentToNuxeo(1);
						tmplt.setIsTemplateParsed(1);
						tmplt.setIsParsedSuccessful(1);

						log.info("Successfully Uploaded Document Into Nuxeo: " + attachments.get(0).getName());

						for (File doc : attachments) {
							Files.delete(Paths.get(doc.getAbsolutePath()));
						}
					}
				}
			}
			catch (Exception e) {
				log.error("Moved to Nuxeo Failed ", e);

				tmplt.setIsSentToNuxeo(0);

				moveToNuxeoFailed(nuxeoFailedFolderPath, attachments, strDate);
				tmplt.setErrorIssue(e.getMessage());
				tmplt.setErrorIssueSource("NuxeoFailed");
			}
		}

		Optional<io.naztech.nuxeoclient.model.Template> tmp = templateService.processAction("NEW", tmplt, Constants.TEMPLATE).stream().findFirst();

		if (tmp.isPresent()) {
			io.naztech.nuxeoclient.model.Template data = tmp.get();
			//documentService.processAction("NEW", document, Constants.DOC);
			Optional<io.naztech.nuxeoclient.model.Document> documentTemp = documentService.processAction("NEW", document, Constants.DOC).stream()
			        .findFirst();

			if (documentTemp.isPresent()) {

				io.naztech.nuxeoclient.model.Document documentData = documentTemp.get();
				documentData.setDocId(documentTemp.get().getDocId());
				documentService.processAction("NEW", documentData, Constants.CUSTOMER);
				documentService.processAction("NEW", documentData, Constants.DOC_DETAILS);
			}

			if ((isSuccess == 0) && (null != resultErrors)) {

				data.setErrorIssue(tmplt.getErrorIssue());
				data.setErrorIssueSource("ParseFailed");

				templateService.processAction("NEW", data, Constants.EXCEPTION);
			}
		}

		// Move uploaded files to another directory
		return;
	}

	// Access private field by using filed name.
	private String getErrorsMessage(ParsingResult book, String fieldName)
	        throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		Field field = book.getClass().getDeclaredField(fieldName);
		if (Modifier.isPrivate(field.getModifiers())) {
			field.setAccessible(true);
			log.info(fieldName + " : " + field.get(book));

			return field.get(book).toString();
		}

		return null;
	}
	
	/**
	 * @author mahbub.hasan
	 * @since 2019-12-08
	 * Moved scheduler from Controller to service class
	 * and deleted unnecessary controller class ScannedNmbsInvoiceUploadController.java
	 * */
	@Value("${import.nmbs.nmbsFolder-path}")
	private String nmbsFolderPath;

	//@Scheduled(fixedRate = 10000, initialDelay = 500)
	public void uploadNmbhDocument() throws IOException {
		File[] fileList = new File(nmbsFolderPath).listFiles();
		log.info("Scanning for files in Nmbs folder in schedule; " + fileList);
		if (fileList == null || fileList.length < 1)
			return;

		// Getting all the xml files in the hotfolder
		List<File> pdflist = Arrays.asList(fileList).parallelStream()
				.filter(file -> "pdf".equals(FilenameUtils.getExtension(file.getName()))).collect(Collectors.toList());

		// Getting all the pdf files of the corresponding xml files in the hotfolder
		for (File pdf : pdflist) {
			uploadDocument(Arrays.asList(pdf), true);
		}
	}
}
