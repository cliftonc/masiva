package org.damongo

import java.io.File;

import com.mongodb.*;
import com.mongodb.util.*;
import com.mongodb.gridfs.*;
import org.bson.types.ObjectId;
import net.ttt.image.exif.*;

class FileManagerService {

    static transactional = false

	// Link to mongo instance
	def mongo
		
	def addFile(String filePath, String fileName, String contentType) {
		
   	    def db = mongo.getDB("damongo-files")
		GridFS fs = new GridFS(db);				
		GridFSInputFile inputFile		
		def fileId
		
		try {		 
		  
		  File file = new File(filePath)
		  		  		  
		  // Save the original file		  
		  inputFile = fs.createFile(file)		  
		  inputFile.put("objectType","original")
		  inputFile.put("filename",fileName)
		  inputFile.put("contentType", contentType);		  
		  inputFile.put("url",filePath.replace(".queued",""))		  
		  inputFile.save();		 
		  
		  fileId = inputFile.getId().toString()
		  		  
		  // Process based on type
		  switch(contentType) {
			  case "image/jpeg":
			  	createImageThumbnail(fileId,file)
				extractExifData(inputFile,filePath)
				break
			  
			  case "image/png":
			  	createImageThumbnail(fileId,file)
			  	break
				
			  case "video/mp4":
				  createVideoThumbnail(fileId,file)
				  break
				  
			  default:
			    println "Unhandled: " + contentType
			  	break
		  }
		  
		} catch (IOException e) {		  
			throw e				  		  
		} catch (Exception e) {
		    throw e
		}
		
		return fileId
		
	}
	
	def extractExifData(GridFSInputFile inputFile, String filePath) {
		ExifDecoder decoder = new ExifDecoder(filePath)
		def metadata = [:]
		def exifdata = [:]
		if(decoder.getTitle())  exifdata.put('title',decoder.getTitle().toString())
		if(decoder.getMake())  exifdata.put('make',decoder.getMake().toString())
		if(decoder.getModel())  exifdata.put('model',decoder.getModel().toString())
		metadata.put("exifdata",exifdata)
		inputFile.put("metadata",metadata)
		inputFile.save();
	}
	
	def createImageThumbnail(String fileId, File file) {
		
		ImageMagickWrapper converter = new ImageMagickWrapper ("tmp",this.log)		
		converter .thumbnail file.getPath(), fileId, fileId + "_thumb.jpg"

				
	}
	
	def createVideoThumbnail(String fileId, File file) {
		
		FfmpegWrapper ffmpeg = new FfmpegWrapper("tmp",this.log)		
		ffmpeg.thumbnail file.getPath(), fileId, fileId + "_thumb.jpg"		
		ffmpeg.browse file.getPath(), fileId, fileId + "_browse.mp4"
		
	}
	
	
	
	def getFile(String id) {
		
		def db = mongo.getDB("damongo-files")
		GridFS fs = new GridFS(db);				
		GridFSDBFile f = fs.findOne(new ObjectId(id))
		return f
		
	}
	
	def downloadFile(String fileUrl, String fileName, String contentType) {
		
		def db = mongo.getDB("damongo-files")
		GridFS fs = new GridFS(db)
		GridFSInputFile inputFile				
		
		try {
			def url= new URL(fileUrl);
			def conn = url.openConnection()
			def is = conn.getInputStream()
			inputFile = fs.createFile(is)
			inputFile.put("contentType", contentType)
			inputFile.put("url",fileUrl)
			inputFile.put("filename",fileName)
			inputFile.save();
			is.close();
						
			
		  } catch (IOException e) {		  
		  	println "Error downloading file."
		  }
		  
		  return inputFile
		
	}
	
}
