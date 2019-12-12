package io.naztech.nuxeoclient.controller;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import io.naztech.nuxeoclient.constants.ActionType;
import io.naztech.nuxeoclient.constants.Constants;
import io.naztech.nuxeoclient.model.DocCount;
import io.naztech.nuxeoclient.service.AttachmentDownloadFromMailService;
import io.naztech.nuxeoclient.service.DocCountService;
import io.naztech.nuxeoclient.service.ScannedApsInvoiceUploadService;
import io.naztech.nuxeoclient.service.ScannedBristanGroupInvoiceUploadService;
import io.naztech.nuxeoclient.service.ScannedCtechInvoiceUploadService;
import io.naztech.nuxeoclient.service.ScannedFestoolInvoiceUploadService;
import io.naztech.nuxeoclient.service.ScannedMMAInvoiceUploadService;
import io.naztech.nuxeoclient.service.ScannedNmbsInvoiceUploadService;
import io.naztech.nuxeoclient.service.ScannedOxInvoiceUploadService;

/**
 * @author Md. Mahbub Hasan Mohiuddin
 * @since 2019-12-09
 **/

@Controller
@EnableScheduling
@ConditionalOnProperty(name = "spring.enable.scheduling")
public class NuxeoScheduler {
	private static Logger log = LoggerFactory.getLogger(NuxeoScheduler.class);

	@Value("${import.root.folder_path}")
	private String rootPath;

	@Value("${import.root.exception_folder_path}")
	private String exceptionPath;

	@Value("${import.root.processed_folder_path}")
	private String processedPath;

	@Value("${folder.name.aps}")
	private String aps;

	@Value("${folder.name.bristan}")
	private String bristan;

	@Value("${folder.name.ctech}")
	private String ctech;

	@Value("${folder.name.festool}")
	private String festool;

	@Value("${folder.name.mma}")
	private String mma;

	@Value("${folder.name.nmbs}")
	private String nmbs;

	@Value("${folder.name.ox}")
	private String ox;

	@Autowired
	private ScannedApsInvoiceUploadService apsService;

	@Autowired
	private ScannedBristanGroupInvoiceUploadService bristanService;

	@Autowired
	private ScannedCtechInvoiceUploadService ctechService;

	@Autowired
	private ScannedFestoolInvoiceUploadService festoolService;

	@Autowired
	private ScannedMMAInvoiceUploadService mmaService;

	@Autowired
	private ScannedNmbsInvoiceUploadService nmbsService;

	@Autowired
	private ScannedOxInvoiceUploadService oxService;

	@Autowired
	private DocCountService docCountService;

	@Autowired
	private AttachmentDownloadFromMailService atcMail;
	private List<String> result = new ArrayList<String>();

	public List<String> getResult() {
		return result;
	}

	public void setResult(List<String> result) {
		this.result = result;
	}

	static void addTree(File file, Collection<File> all) {
		File[] children = file.listFiles();
		if (children != null) {
			for (File child : children) {
				all.add(child);
				addTree(child, all);
			}
		}
	}

	/**
	 * @author asadullah.galib
	 * @since 2019-12-10 Scheduler for File download
	 * initial delay 0.5 second
	 */
	@Scheduled(fixedRateString = "${import.scheduled.value.attachmentDownload}", initialDelay = 500)
	public void attachmentDownload() throws ParseException {
		atcMail.downloadAttachments();
	}

	/**
	 * @author asadullah.galib
	 * @since 2019-12-10 Scheduler for File processing
	 * initial delay 60 second
	 */
	@Scheduled(fixedRateString = "${import.scheduled.value.exportedFileUpload}", initialDelay = 60000)
	public void fileProcessChecking() {

		boolean isDone = true;
		
		isDone = checkProcessedFiles();
		log.info("Checked processed files to IN folder: {}", isDone);

		isDone = checkUnsuccessfulFiles();
		log.info("Checked unsuccessful files: {}", isDone);

		isDone = checkErrorFile();
		log.info("Checked ABBY exception files: {}", isDone);

		isDone = handleNuxeoUpload();
		log.info("Files uploaded to Nuxeo: {}", isDone);
	}

	/**
	 * @author asadullah.galib
	 * @since 2019-12-10 Upload all documents & data to Nuxeo server for archiving
	 */
	private boolean handleNuxeoUpload() {

		boolean isDone = true;

		List<File> files = new ArrayList<File>();
		Collection<File> all = new ArrayList<File>();
		List<String> folderList = new ArrayList<String>();
		Map<String, List<File>> fileMap = new HashMap<String, List<File>>();

		/* Scan the root folder and get all subfolder & files */
		addTree(new File(rootPath), all);

		for (File file : all) {

			String fileName = new File(file.getParent()).getName();

			if (!folderList.contains(fileName)) {
				folderList.add(fileName);
			}

			if (file.getName().contains(Constants.DOT)) {

				if (folderList.parallelStream().anyMatch(fileName::equals)) {

					if (!fileMap.containsKey(fileName)) {
						files = new ArrayList<File>();
						files.add(file);

						fileMap.put(fileName, files);
					} else {

						files.add(file);

						fileMap.put(fileName, files);
					}
				}
			}
		}

		fileMap.forEach((key, value) -> {

			List<File> xmlList = new ArrayList<File>();

			if (key.equalsIgnoreCase(aps)) {
				log.info("Scanning for files in aps folder");

				if (!value.isEmpty() && value.size() > 1) {

					xmlList = value.parallelStream()
							.filter(file -> "xml".equals(FilenameUtils.getExtension(file.getName())))
							.collect(Collectors.toList());

					/* Getting all the pdf files of the corresponding xml files in the hotfolder */
					xmlList.parallelStream().forEachOrdered(xml -> {
						String pdf = xml.getAbsolutePath().replaceAll("\\.xml", "\\.pdf");

						File pdfFile = new File(pdf);

						if (pdfFile.exists())
							try {
								apsService.uploadDocument(xml, Arrays.asList(pdfFile), true);
							} catch (IOException e) {
								log.error("Caught Error {} / {}", e, e);
							}
					});
				}
			} else if (key.equalsIgnoreCase(bristan)) {
				log.info("Scanning for files in bristan folder");

				if (!value.isEmpty() && value.size() > 1) {

					xmlList = value.parallelStream()
							.filter(file -> "xml".equals(FilenameUtils.getExtension(file.getName())))
							.collect(Collectors.toList());

					/* Getting all the pdf files of the corresponding xml files in the hotfolder */
					xmlList.parallelStream().forEachOrdered(xml -> {
						String pdf = xml.getAbsolutePath().replaceAll("\\.xml", "\\.pdf");

						File pdfFile = new File(pdf);

						if (pdfFile.exists())
							try {
								bristanService.uploadDocument(xml, Arrays.asList(pdfFile), true);
							} catch (IOException e) {
								log.error("Caught Error {} / {}", e, e);
							}
					});
				}
			} else if (key.equalsIgnoreCase(ctech)) {
				log.info("Scanning for files in ctech folder");

				if (!value.isEmpty() && value.size() > 1) {

					xmlList = value.parallelStream()
							.filter(file -> "xml".equals(FilenameUtils.getExtension(file.getName())))
							.collect(Collectors.toList());

					/* Getting all the pdf files of the corresponding xml files in the hotfolder */
					xmlList.parallelStream().forEachOrdered(xml -> {
						String pdf = xml.getAbsolutePath().replaceAll("\\.xml", "\\.pdf");

						File pdfFile = new File(pdf);

						if (pdfFile.exists())
							try {
								ctechService.uploadDocument(xml, Arrays.asList(pdfFile), true);
							} catch (IOException e) {
								log.error("Caught Error {} / {}", e, e);
							}
					});
				}
			} else if (key.equalsIgnoreCase(festool)) {
				log.info("Scanning for files in festool folder");

				if (!value.isEmpty() && value.size() > 1) {

					xmlList = value.parallelStream()
							.filter(file -> "xml".equals(FilenameUtils.getExtension(file.getName())))
							.collect(Collectors.toList());

					/* Getting all the pdf files of the corresponding xml files in the hotfolder */
					xmlList.parallelStream().forEachOrdered(xml -> {
						String pdf = xml.getAbsolutePath().replaceAll("\\.xml", "\\.pdf");

						File pdfFile = new File(pdf);

						if (pdfFile.exists())
							try {
								festoolService.uploadDocument(xml, Arrays.asList(pdfFile), true);
							} catch (IOException e) {
								log.error("Caught Error {} / {}", e, e);
							}
					});
				}
			} else if (key.equalsIgnoreCase(mma)) {
				log.info("Scanning for files in mma folder");

				if (!value.isEmpty() && value.size() > 1) {

					xmlList = value.parallelStream()
							.filter(file -> "xml".equals(FilenameUtils.getExtension(file.getName())))
							.collect(Collectors.toList());

					/* Getting all the pdf files of the corresponding xml files in the hotfolder */
					xmlList.parallelStream().forEachOrdered(xml -> {
						String pdf = xml.getAbsolutePath().replaceAll("\\.xml", "\\.pdf");

						File pdfFile = new File(pdf);

						if (pdfFile.exists())
							try {
								mmaService.uploadDocument(xml, Arrays.asList(pdfFile), true);
							} catch (IOException e) {
								log.error("Caught Error {} / {}", e, e);
							}
					});
				}
			} else if (key.equalsIgnoreCase(nmbs)) {
				log.info("Scanning for files in nmbs folder");

				if (!value.isEmpty() && value.size() > 1) {

					xmlList = value.parallelStream()
							.filter(file -> "xml".equals(FilenameUtils.getExtension(file.getName())))
							.collect(Collectors.toList());

					/* Getting all the pdf files of the corresponding xml files in the hotfolder */
					xmlList.parallelStream().forEachOrdered(xml -> {
						String pdf = xml.getAbsolutePath().replaceAll("\\.xml", "\\.pdf");

						File pdfFile = new File(pdf);

						if (pdfFile.exists())
							try {
								nmbsService.uploadDocument(xml, Arrays.asList(pdfFile), true);
							} catch (IOException e) {
								log.error("Caught Error {} / {}", e, e);
							}
					});
				}
			} else if (key.equalsIgnoreCase(ox)) {
				log.info("Scanning for files in ox folder");

				if (!value.isEmpty() && value.size() > 1) {

					xmlList = value.parallelStream()
							.filter(file -> "xml".equals(FilenameUtils.getExtension(file.getName())))
							.collect(Collectors.toList());

					/* Getting all the pdf files of the corresponding xml files in the hotfolder */
					xmlList.parallelStream().forEachOrdered(xml -> {
						String pdf = xml.getAbsolutePath().replaceAll("\\.xml", "\\.pdf");

						File pdfFile = new File(pdf);

						if (pdfFile.exists())
							try {
								oxService.uploadDocument(xml, Arrays.asList(pdfFile), true);
							} catch (IOException e) {
								log.error("Caught Error {} / {}", e, e);
							}
					});
				}
			}
		});

		return isDone;
	}

	private boolean checkProcessedFiles() {
		boolean isDone = true;

		DocCount dc1 = new DocCount();
		DocCount dc2 = new DocCount();
		List<File> xmlList = new ArrayList<File>();
		Collection<File> all = new ArrayList<File>();

		addTree(new File(rootPath), all);

		try {

			if (!all.isEmpty() && all.size() > 1) {

				xmlList = all.parallelStream()
						.filter(file -> Constants.XML.equals(FilenameUtils.getExtension(file.getName())))
						.collect(Collectors.toList());

				xmlList.parallelStream().forEachOrdered(xml -> {

					String pdfFile = xml.getName().replaceAll("\\.xml", "\\.pdf");

					dc1.setFileName(pdfFile);

					List<DocCount> docCountList = docCountService.processAction(ActionType.SELECT.toString(), dc1);

					if (docCountList.parallelStream().findFirst().isPresent()) {

						int ver = docCountList.get(0).getDocCountVer();
						Integer id = docCountList.get(0).getDocCountId();
						Integer userId = docCountList.get(0).getIdUserModKey();

						dc2.setDocCountId(id);
						dc2.setDocCountVer(ver);
						dc2.setIdUserModKey(userId);
						dc2.setIsAbbyProcessed(1);
						dc2.setIsProcessedSuccessfully(1);
						
						docCountList = docCountService.processAction(ActionType.UPDATE.toString(), dc2);

						if (docCountList.stream().findFirst().isPresent()
								&& docCountList.stream().findFirst().get().getDocCountVer() > ver)
							log.info("******Data updated successfully******");

						docCountList.clear();
					}
				});
			}

		} catch (Exception ex) {
			isDone = false;
			log.error("Error checking processed files: {}", ex.getMessage());
		}

		all = null;
		xmlList = null;

		return isDone;
	}

	/**
	 * @author asadullah.galib
	 * Checking for Unsuccessfully processed files in ABBY
	 *        processed folder by data from db
	 * @since 2019-12-10
	 */
	private boolean checkUnsuccessfulFiles() {

		boolean isDone = true;

		File file = new File(processedPath);

		DocCount dcCount = new DocCount();
		DocCount dcCount2 = new DocCount();

		dcCount.setIsAbbyProcessed(0);
		dcCount.setIsProcessedSuccessfully(0);

		List<DocCount> docCountList = docCountService.processAction(ActionType.SELECT.toString(), dcCount);

		try {
			if (docCountList.parallelStream().findFirst().isPresent()) {

				for (int i = 0; i < docCountList.size(); i++) {
					
					String fileName = docCountList.get(i).getFileName();
					
					List<String> foundFile = search(file, fileName);

					if (foundFile.parallelStream().findFirst().isPresent()) {
						
						int ver = docCountList.get(i).getDocCountVer();
						Integer id = docCountList.get(i).getDocCountId();
						Integer userId = docCountList.get(i).getIdUserModKey();

						dcCount2.setDocCountId(id);
						dcCount2.setDocCountVer(ver);
						dcCount2.setIdUserModKey(userId);
						dcCount2.setIsAbbyProcessed(1);
						
						docCountList = docCountService.processAction(ActionType.UPDATE.toString(), dcCount2);

						if (docCountList.stream().findFirst().isPresent()
								&& docCountList.stream().findFirst().get().getDocCountVer() > ver)
							log.info("******Data updated successfully******");

						docCountList.clear();
					}
				}
			}

		} catch (Exception ex) {
			log.error("Error searching unsuccessful files: {}", ex.getMessage());

			isDone = false;
		}

		dcCount = null;
		dcCount2 = null;
		docCountList = null;

		return isDone;
	}

	/**
	 * @author asadullah.galib
	 * @since 2019-12-12 Check for ABBY error files
	 */
	private boolean checkErrorFile() {
		boolean isDone = true;
		File file = new File(exceptionPath);

		DocCount dcCount = new DocCount();
		DocCount dcCount2 = new DocCount();
		dcCount.setIsAbbyError(0);
		dcCount.setIsAbbyProcessed(0);
		dcCount.setIsProcessedSuccessfully(0);

		List<DocCount> docCountList = docCountService.processAction(ActionType.SELECT.toString(), dcCount);

		try {
			if (docCountList.parallelStream().findFirst().isPresent()) {

				for (int i = 0; i < docCountList.size(); i++) {
					
					String fileName = docCountList.get(i).getFileName();
					
					List<String> foundFile = search(file, fileName);
					
					if (foundFile.parallelStream().findFirst().isPresent()) {
						int ver = docCountList.get(i).getDocCountVer();
						Integer id = docCountList.get(i).getDocCountId();
						Integer userId = docCountList.get(i).getIdUserModKey();

						dcCount2.setDocCountId(id);
						dcCount2.setDocCountVer(ver);
						dcCount2.setIdUserModKey(userId);
						dcCount2.setIsAbbyError(1);

						docCountList = docCountService.processAction(ActionType.UPDATE.toString(), dcCount2);
						if (docCountList.stream().findFirst().isPresent()
								&& docCountList.stream().findFirst().get().getDocCountVer() > ver)
							log.info("******Data updated successfully******");

						docCountList.clear();
					}
				}
			}
		} catch (Exception ex) {
			isDone = false;
			log.error("Error checking exception files: {}", ex.getMessage());
		}

		dcCount = null;
		dcCount2 = null;
		docCountList = null;

		return isDone;
	}

	/**
	 *  Search files inside directory
	 *  */
	private List<String> search(File file, String name) {

		if (file.isDirectory()) {
			log.info("[{}]", "Searching directory ... " + file.getAbsoluteFile());

			// do you have permission to read this directory?
			if (file.canRead()) {
				for (File temp : file.listFiles()) {
					if (temp.isDirectory()) {
						search(temp, name);
					} else {
						if (name.equals(temp.getName())) {
							result.add(temp.getAbsoluteFile().toString());
						}

					}
				}

			} else {
				log.info("[{}]", file.getAbsoluteFile() + "Permission Denied");
			}
		}
		return result;
	}
}
