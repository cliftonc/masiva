package org.damongo

import java.io.File;
import javax.activation.MimetypesFileTypeMap
import org.springframework.datastore.mapping.mongo.*

class SolrQueueService {
	
	MongoDatastore mongoDatastore
	
	static rabbitQueue = "solrQueue"
	
	void handleMessage(msg) {

		try {
			
			// Index our domain object
			mongoDatastore.connect()
			def damFileInstance = DamFile.get(msg)			
			damFileInstance.indexSolr()												
		
		} catch (Exception e) {
		
			println "Solr index handling error occcurred : " + e.message			
			tempFile.renameTo tempFile.getName().replace(".queued",".errored")			
		
		}
	}	
	
	
}
