package org.damongo

import java.io.File;
import javax.activation.MimetypesFileTypeMap;

class IngestService {

	static rabbitQueue = "ingestQueue"
	
	def fileManagerService
	
	void handleMessage(msg) {

		// Process our file
		File tempFile = new File(msg)

		try {
			
			def fileName = tempFile.getName().replace(".queued","")
			
			MimetypesFileTypeMap mimeTypes = new MimetypesFileTypeMap('web-app/mime.types')						
						
			def fileType = mimeTypes.getContentType(fileName);						
			
			// Add our file to Mongo
			def mongoFileId = fileManagerService.addFile(msg,fileName,fileType)				
			tempFile.delete()
			
			if(mongoFileId) {
				// 	Delete our ingested file						
				rabbitSend "solrQueue", mongoFileId
			}
		
		} catch (Exception e) {
		
			println "An error occurred ingesting : " + fileName + " : " + e.message			
			tempFile.renameTo tempFile.getName().replace(".queued",".errored")			
		
		}
	}	
	
	void sendMessage(queueName,msg) {
		rabbitSend queueName, msg
	}
	
}
