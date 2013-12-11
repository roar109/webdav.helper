package org.rageco.webdav;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.FileSet;

/**
 * Class that upload files to a webdav container.
 * @author roar109@gmail.com
 * @version 1.0
 * */
public class ResourceWebdav extends MatchingTask{
	private String username;
	private String password;
	private String url;
	private List fileSets = new ArrayList();
	private HashMap<String, File> resourcesCache = null;
	private Sardine sardine = null;
	
	/**
	 * Initial Execute
	 * @see org.apache.tools.ant.taskdefs.MatchingTask:execute
	 * */
	public void execute(){
		System.out.println("Using:");
		System.out.println("Username: "+username);
		System.out.println("Password: "+password);
		System.out.println("URL: "+url);
		System.out.println("Resources: "+fileSets);
		try{
			sardine = SardineFactory.begin(this.username, this.password);
			readOriginResources();
			uploadResources();
			sardine.shutdown();
		}catch(Exception e){
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Read resources from local folder.
	 * */
	@SuppressWarnings("rawtypes")
	private void readOriginResources(){
		Iterator it = fileSets.iterator();
		FileSet fileSet = null;
		System.out.println("---------------------------------------------");
		while(it.hasNext()){
			fileSet = (FileSet)it.next();
			DirectoryScanner dirScanner = fileSet.getDirectoryScanner(getProject());
            String[] fileSetFiles = dirScanner.getIncludedFiles();
            resourcesCache = new HashMap<String, File>(fileSetFiles.length);
			for(String s: fileSetFiles){
				System.out.println("Reading file: "+s);
				File f = new File(dirScanner.getBasedir(), s);
				resourcesCache.put(s, f);
			}
		}
		System.out.println("---------------------------------------------\n");
	}
	
	/**
	 * Upload all the resources available.
	 * */
	public void uploadResources() throws Exception{
		Set<String> filenamesLocales = resourcesCache.keySet();
		String fileName = "";
		for(String fn: filenamesLocales){
			fileName = fn.replace(" ","");//The names cannot contain spaces.
			System.out.println("---------------------------------------------");
			if(sardine.exists(this.url+fileName)){
				System.out.println("File "+fileName+" already exist, replacing...");
			}else{
				System.out.println("Uploading new file: "+fileName);
			}
			byte[] data = IOUtils.toByteArray(new FileInputStream(resourcesCache.get(fn)));
			sardine.put(this.url+fileName, data);
			System.out.println("File "+fileName+" upload is complete.");
			System.out.println("---------------------------------------------");
		}
	}
	
	/**
	 * Read all the resources on destination path.
	 * */
	@SuppressWarnings("unused")
	private void readResources(){
		List<DavResource> resources;
		try {
			resources = sardine.list(this.url);
			for (DavResource res : resources){
				System.out.println("---------------------------------------------");
				if(res.isDirectory())
					System.out.println("Folder: "+res);
				else
					System.out.println(res);
				System.out.println("---------------------------------------------");
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public void addFileset(FileSet fileset) {
		fileSets.add(fileset);
    }
}
