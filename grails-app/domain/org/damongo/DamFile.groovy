package org.damongo

import org.bson.types.ObjectId
import org.grails.solr.*

class DamFile {

	static enableSolrSearch = true	
		
    ObjectId id
	
	@Solr(asTextAlso=true)
    String filename
	
	@Solr(asTextAlso=true)
	String contentType
	
	@Solr(asTextAlso=true)
	String url
	
	String md5
	
	int length
	int chunkSize
	Date uploadDate

	@Solr(asTextAlso=true)
	Object metadata
	
	static mapWith = "mongo"
	
    static constraints = {
    }
    
    static mapping = {
      collection "fs.files"
      database "damongo-files"      
    }
   
}
