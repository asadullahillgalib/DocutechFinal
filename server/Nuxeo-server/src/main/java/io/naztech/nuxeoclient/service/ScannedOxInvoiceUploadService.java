package io.naztech.nuxeoclient.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.naztech.nuxeoclient.constants.ActionType;
import io.naztech.nuxeoclient.constants.Constants;
import io.naztech.nuxeoclient.model.DocumentWrapper;
import io.naztech.nuxeoclient.model.Template;
import io.naztech.nuxeoclient.model.invoice.ox.Documents;

/**
 * @author Asadullah.Galib
 * @since 2019-09-29
 **/

@Named
public class ScannedOxInvoiceUploadService extends AbstractScannedInvoidUploadService {
	private static Logger log = LoggerFactory.getLogger(ScannedOxInvoiceUploadService.class);
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

	@Value("${import.ox.archiveFolder-path}")
	private String archiveFolderPath;

	@Value("${import.ox.badFolder-path}")
	private String badfolderPath;

	@Value("${import.ox.nuxeoFailedFolder-path}")
	private String nuxeoFailedFolderPath;
	
	@Value("${import.nuxeo.RepoPath}")
	private String repoPath;

	@Value("${import.ox.Name}")
	private String nuxeoinvoiceName;

	@Value("${import.ox.type}")
	private String nuxeoinvoiceType;

	@Value("${import.ox.prefix}")
	private String prefix;
	
	@Value("${import.nuxeo.ox.description}")
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
	 * Constructs a FestoolInvoice object from the xml file
	 * 
	 * @param file
	 * @return FestoolInvoice Object
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
	 * @param result
	 * @return DocumentWrapper object
	 */
	private DocumentWrapper convertInvoiceToDocumentReq(Documents result) {
		if (result == null) return null;
		try {
			DocumentWrapper ob = DocumentWrapper.createWithName(nuxeoinvoiceName, nuxeoinvoiceType);

			// Setting attributes of the document wrapper object
			ob.setTitle(nuxeoinvoiceName);
			ob.setDescription(desc);
			ob.setPrefix(prefix);
			ob.setRepoPath(repoPath);
			ob.addAttribute("account_number", result.getOXGroupDocumentDefinition().getAccountNumber());
			ob.addAttribute("client_name", result.getOXGroupDocumentDefinition().getCustomerName());

			ob.addAttribute("company_address", result.getOXGroupDocumentDefinition().getSupplierAddress());
			ob.addAttribute("customer_address", result.getOXGroupDocumentDefinition().getCustomerAddress());
			ob.addAttribute("deliver_to", result.getOXGroupDocumentDefinition().getCustomerAddress());
			ob.addAttribute("invoice_date", result.getOXGroupDocumentDefinition().getInvoiceDate());
			ob.addAttribute("invoice_number", Long.toString(result.getOXGroupDocumentDefinition().getInvoiceNumber()));
			if (result.getOXGroupDocumentDefinition().getNetInvoiceTotal() != null) {
				ob.addAttribute("net_invoice_total", bigDescimalToString(result.getOXGroupDocumentDefinition().getNetInvoiceTotal().toString()));
			}
			else {
				ob.addAttribute("net_invoice_total", "null");
			}

			ob.addAttribute("order_date", result.getOXGroupDocumentDefinition().getOrderDate());
			ob.addAttribute("order_id", result.getOXGroupDocumentDefinition().getOrderId());
			ob.addAttribute("order_number", result.getOXGroupDocumentDefinition().getOrderNo());
			ob.addAttribute("reference_no", result.getOXGroupDocumentDefinition().getReferenceNo());
			ob.addAttribute("supplier_name", result.getOXGroupDocumentDefinition().getSupplierName());
			if (result.getOXGroupDocumentDefinition().getNetInvoiceTotal() != null) {
				ob.addAttribute("value_of_goods", bigDescimalToString(result.getOXGroupDocumentDefinition().getValueOfGoods().toString()));
			}
			else {
				ob.addAttribute("value_of_goods", "null");
			}
			if (result.getOXGroupDocumentDefinition().getNetInvoiceTotal() != null) {
				ob.addAttribute("vat", bigDescimalToString(result.getOXGroupDocumentDefinition().getVat().toString()));
			}
			else {
				ob.addAttribute("vat", "null");
			}
			ob.addAttribute("vat_number", result.getOXGroupDocumentDefinition().getVatRegNumber());
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
		Template tmplt=new Template();
		io.naztech.nuxeoclient.model.Document document = new io.naztech.nuxeoclient.model.Document();
		io.naztech.nuxeoclient.model.DocDetails docdetails = new io.naztech.nuxeoclient.model.DocDetails();
		if (enabled && flag) {
			moveToArchive(archiveFolderPath, invoiceInfoXml, attachments, strDate);
		}
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

			tmplt.setClientName(result.getOXGroupDocumentDefinition().getCustomerName());
			tmplt.setReceivedDocId(tmplt.getClientName() + "_" + Long.toString(result.getOXGroupDocumentDefinition().getOrderNo()));

			DocumentWrapper req = convertInvoiceToDocumentReq(result);
			tmplt.setSupplierName(req.getTitle());

			if (result.getOXGroupDocumentDefinition().getCustomerName().equals(null)
			        && result.getOXGroupDocumentDefinition().getSupplierName().equals(null)
			        && result.getOXGroupDocumentDefinition().getNetInvoiceTotal().equals(null)) {

				log.error(invoiceInfoXml.getName() + " Moved to Bad Folder");

				moveToBadFolder(badfolderPath, invoiceInfoXml, attachments, strDate);

				tmplt.setIsSentToNuxeo(0);
				tmplt.setIsTemplateParsed(0);
				tmplt.setIsParsedSuccessful(0);

				isSuccess = 0;
			}
			else {
				isSuccess = 1;
				document.setAccountNumber(Long.toString(result.getOXGroupDocumentDefinition().getAccountNumber()));
				document.setCompanyAddress(result.getOXGroupDocumentDefinition().getSupplierAddress());
				document.setContactNumber(result.getOXGroupDocumentDefinition().getSupplierTelephoneNo().toString());
				document.setContactNumber(result.getOXGroupDocumentDefinition().getSupplierTelephoneNo().toString());
				document.setCustomerAddress(result.getOXGroupDocumentDefinition().getCustomerAddress());
				document.setCustomerNumber(Long.toString(result.getOXGroupDocumentDefinition().getOrderNo()));
				document.setCustomerOrderNo(Long.toString(result.getOXGroupDocumentDefinition().getOrderNo()));
				document.setDeliverTo(result.getOXGroupDocumentDefinition().getCustomerAddress());
				document.setDeliveryDetails(result.getOXGroupDocumentDefinition().getCustomerAddress());
				document.setDeliveryNoteNo(nullHandlerInteger(Long.toString(result.getOXGroupDocumentDefinition().getOrderNo())));
				document.setFaxNumber(result.getOXGroupDocumentDefinition().getFaxNumber().toString());
				document.setInvoiceAmount(
				        nullHandlerDouble(bigDescimalToString(result.getOXGroupDocumentDefinition().getNetInvoiceTotal().toString())));
				document.setInvoiceNumber(Long.toString(result.getOXGroupDocumentDefinition().getInvoiceNumber()));
				document.setInvoiceTo(result.getOXGroupDocumentDefinition().getCustomerAddress());
				document.setVat(nullHandlerDouble(bigDescimalToString(result.getOXGroupDocumentDefinition().getVat().toString())));
				document.setTotalBeforeVat(
				        nullHandlerDouble(bigDescimalToString(result.getOXGroupDocumentDefinition().getValueOfGoods().toString())));
				document.setVatNumber(result.getOXGroupDocumentDefinition().getVatRegNumber());
				document.setNetInvoiceTotal(
				        nullHandlerDouble(bigDescimalToString(result.getOXGroupDocumentDefinition().getNetInvoiceTotal().toString())));

				try {
					document.setDespatchDate(
					        new SimpleDateFormat("yyyy/MM/dd").parse(dateFormatter(result.getOXGroupDocumentDefinition().getInvoiceDate())));

					document.setDocumentDate(
					        new SimpleDateFormat("yyyy/MM/dd").parse(dateFormatter(result.getOXGroupDocumentDefinition().getInvoiceDate())));

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
				tmplt.setIsSentToNuxeo(0);
				moveToNuxeoFailed(nuxeoFailedFolderPath, invoiceInfoXml, attachments, strDate);
				tmplt.setErrorIssue(e.getMessage());
				tmplt.setErrorIssueSource("NuxeoFailed");
			}
		}

		Optional<io.naztech.nuxeoclient.model.Template> tmp = templateService.processAction(ActionType.NEW.toString(), tmplt, Constants.TEMPLATE).stream().findFirst();

		if (tmp.isPresent()) {

			Optional<io.naztech.nuxeoclient.model.Document> documentTemp = documentService.processAction(ActionType.NEW.toString(), document, Constants.DOC).stream()
			        .findFirst();

			if (documentTemp.isPresent()) {

				io.naztech.nuxeoclient.model.Document documentData = documentTemp.get();
				documentData.setDocId(documentTemp.get().getDocId());
				documentService.processAction(ActionType.NEW.toString(), documentData, Constants.CUSTOMER);
				for (int i = 0; i < result.getOXGroupDocumentDefinition().getInvoiceTable().size(); i++) {
					document.setItemQty(nullHandlerInteger(String.valueOf(result.getOXGroupDocumentDefinition().getInvoiceTable().get(i).getQty())));
					document.setItemDescription(result.getOXGroupDocumentDefinition().getInvoiceTable().get(i).getDescription());

					docdetails.setDocId(documentTemp.get().getDocId());
					docDetailsService.processAction(ActionType.NEW.toString(), docdetails, Constants.DOC_DETAILS);
				}
			}

		}
	}

	/**
	 * @author mahbub.hasan
	 * @since 2019-12-08
	 * Moved scheduler from Controller to service class
	 * and deleted unnecessary controller class ScannedOXInvoiceUploadController.java
	 * */

	/*	public void processOxDoc(List<File> fileList) throws IOException {
	
			if (fileList == null || fileList.size() < 2)
				return;
	
			// Getting all the xml files in the hotfolder
			// List<File> xmllist = Arrays.asList(fileList)
			List<File> xmllist = fileList.parallelStream()
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
