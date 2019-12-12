package io.naztech.nuxeoclient.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.nuxeo.client.objects.Document;

/*import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;*/

// TODO add an java property 'prefix' later if it differs from 'type'
/*@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)*/
public class DocumentWrapper extends Document {

	String title, description;
	String repoPath;
	String prefix;
	
	/** List of {@link File} objects with full path */
	List<File> files = new ArrayList<>();

	Map<String, Object> attributes = new HashMap<>();

	public Document buildDocument() {
		Document ob = Document.createWithName(this.name, this.type);
		ob.setPropertyValue("dc:title", this.title);
		ob.setPropertyValue("dc:description", this.description);
		ob.setPropertyValue("repoPath", this.getRepoPath());
		for (Entry<String, Object> en : attributes.entrySet()) {
			ob.setPropertyValue(prefix + en.getKey(), en.getValue());
		}
		// TODO whether there is file in list, if not throw exception
		return ob;
	}

	public DocumentWrapper addAttribute(String key, Object val) {
		this.attributes.put(key, val);
		return this;
	}

	public DocumentWrapper addFile(File file) {
		this.files.add(file);
		return this;
	}

	public DocumentWrapper addFile(String filePath) {
		this.files.add(FileUtils.getFile(filePath));
		return this;
	}

	public static DocumentWrapper createWithName(String name, String type) {
		DocumentWrapper ob = new DocumentWrapper();
		ob.name = name;
		ob.type = type;
		return ob;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRepoPath() {
		return repoPath;
	}

	public void setRepoPath(String repoPath) {
		this.repoPath = repoPath;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public List<File> getFiles() {
		return files;
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}
}
