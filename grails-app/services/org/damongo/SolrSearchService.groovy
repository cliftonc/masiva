/*
* Copyright 2010 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
* ----------------------------------------------------------------------------
* Original Author: Mike Brevoort, http://mike.brevoort.com
* Project sponsored by:
*     Avalon Consulting LLC - http://avalonconsult.com
*     Patheos.com - http://patheos.com
* ----------------------------------------------------------------------------
*/

package org.damongo

import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer
import org.apache.solr.client.solrj.impl.StreamingUpdateSolrServer
import org.codehaus.groovy.grails.commons.metaclass.*
import org.apachedomainDesc.solr.client.solrj.impl.*
import org.apache.solr.common.*
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.client.solrj.SolrQuery

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler
import org.codehaus.groovy.grails.commons.GrailsClassUtils

import org.grails.solr.Solr
import org.grails.solr.SolrUtil

class SolrSearchService {

  boolean transactional = false
  def grailsApplication  
  
  /**
  * Return a SolrServer
  * {@link http://lucene.apache.org/solr/api/org/apache/solr/client/solrj/SolrServer.html}
  */
  def getSolrServer() {
    def url =  (grailsApplication.config.solr?.url) ? grailsApplication.config.solr.url : "http://localhost:8983/solr"
    def server = new CommonsHttpSolrServer( url )
    //server.setParser(new XMLResponseParser())
	return server
  }
  
  def getStreamingUpdateServer(queueSize=20, numThreads=3) {
	
    def url =  (grailsApplication.config.solr?.url) ? grailsApplication.config.solr.url : "http://localhost:8983/solr"
	def server = new StreamingUpdateSolrServer( url, queueSize, numThreads)
	return server     
  }

  /**
  * Execute a basic Solr query given a string formatted query
  *
  * @param query - the Solr query string
  * @return Map with 'resultList' - list of Maps representing results and 'queryResponse' - Solrj query result
  */
  def search(String query) {
    search( new SolrQuery( query ) )
  }
  
  /**
  * Given SolrQuery object, execute Solr query
  *
  * @param solrQuery - SolrQuery object representating the query {@link http://lucene.apache.org/solr/api/org/apache/solr/client/solrj/SolrQuery.html}
  * @return Map with 'resultList' - list of Maps representing results and 'queryResponse' - Solrj query result
  */
  def search(SolrQuery solrQuery) {
    QueryResponse rsp = getServer().query( solrQuery );
    

    def results = []
    rsp.getResults().each { doc ->
      def map = [:]     
      doc.getFieldNames().each { it ->
                
        // add both the stripped field name and the actual solr field name to 
        // the result map... little redundant but greater flexibilty in retrieving
        // results
        def strippedFieldName = SolrUtil.stripFieldName(it)
        map."${strippedFieldName}" = doc.getFieldValue(it)
        if(!map."${it}" && strippedFieldName != it)
          map."${it}" = doc.getFieldValue(it)
      }
      map.id = SolrUtil.parseSolrId(doc.getFieldValue("id"))?.id
      
      // Add the SolrDocument to the map as well
      // http://lucene.apache.org/solr/api/org/apache/solr/common/SolrDocument.html
      map.solrDocument = doc
    
      results << map
    }
     
    return results 
      
  }
    
  /**
  * Constitute SolrQuery for a haversine based spatial search. This method returns 
  * the SolrQuery object in case you need to manipulate it further (add facets, etc)
  *
  * @param query  a lucene formatted query to execute in addition to the location range query
  * @param lat    latitude in degrees
  * @param lng    longitude in degrees
  * @param range  the proximity range to filter results by (small the better performance). unit is miles unless the radius param is passed in which case it's whatever the unit of the radius is
  * @param start  result number of the first returned result - used in paging (optional, default: 0)
  * @param rows   number of results to include - aka page size (optional, default: 10)
  * @param sort   sort direction asc or desc (optional, default: asc)
  * @param funcQuery provide a function query to be summed with the hsin function (optional)
  * @param radius sphere radius for haversine algorigthm (optional, default: 3963.205 [earth radius in miles])
  * @param lat_field SOLR index field for latitude in radians (optional, default: latitude_d)
  * @param lng_field SOLR index field for latitude in radians (optional, default: longitude_d)
  * @return SolrQuery object representing this spatial query
  */  
  SolrQuery getSpatialQuery(query, lat, lng, range, start=0, rows=10, sort="asc", funcQuery="", radius=3963.205, lat_field="latitude_rad_d", lng_field="longitude_rad_d") {
    def lat_rad = Math.toRadians( lat )
    def lng_rad = Math.toRadians( lng )
    def hsin = "hsin(${lat_rad},${lng_rad},${lat_field},${lng_field},${radius})"
    def order = [asc: SolrQuery.ORDER.asc, desc: SolrQuery.ORDER.desc]
    
    if(funcQuery != "")
      funcQuery = "${funcQuery},"
    
    SolrQuery solrQuery = new SolrQuery( (query && query.trim()) ? "(${query}) AND _val_:\"sum(${funcQuery}${hsin})\"" : "_val_:\"sum(${funcQuery}${hsin})\"" )
    solrQuery.addFilterQuery("{!frange l=0 u=${range}}${hsin}")
    solrQuery.setStart(start)
    solrQuery.setRows(rows)
    solrQuery.addSortField("score", order[sort])
    return solrQuery
  }
  
  /**
  * Same as getSpatialQuery but executes query
  * @return Map with 'resultList' - list of Maps representing results and 'queryResponse' - Solrj query result
  */
  def querySpatial(query, lat, lng, range, start=0, rows=10, sort="asc", funcQuery="", radius=3963.205, lat_field="latitude_rad_d", lng_field="longitude_rad_d") {
    def solrQuery = getSpatialQuery(query, lat, lng, range, start, rows, sort, funcQuery, radius, lat_field, lng_field)
    return querySpatial(solrQuery, lat, lng, lat_field, lng_field)
  } 

  /**
  * Expected to be called after getSpatialQuery, assuming you need to further 
  * manipulate the SolrQuery object before executing the query. 
  * @return Map with 'resultList' - list of Maps representing results and 'queryResponse' - Solrj query result
  */  
  def querySpatial(SolrQuery solrQuery, lat, lng, lat_field="latitude_rad_d", lng_field="longitude_rad_d") {
    log.debug ("spatial query: ${solrQuery}")  
    def result = search(solrQuery)
    result.resultList.each {
      it.dist = Haversine.computeMi(lat, lng, Math.toDegrees(it."${SolrUtil.stripFieldName(lat_field)}"), Math.toDegrees(it."${SolrUtil.stripFieldName(lng_field)}"))
    }
    return result   
  }

  
	def indexDocument(Object delegate) {
				
		def delegateDomainOjbect = delegate		
		def server = getStreamingUpdateServer()
		def doc = new SolrInputDocument()
		indexDomain(delegateDomainOjbect, doc)
		server.add(doc)
		// server.commit()
	}
	
	def indexDomain(delegateDomainOjbect, doc, depth = 1, prefix = "") {
		
		// println "Indexing ..."
		
		def domainDesc = grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE, delegateDomainOjbect.class.name)
		def clazz = (delegateDomainOjbect.class.name == 'java.lang.Class') ? delegateDomainOjbect : delegateDomainOjbect.class
		
		domainDesc.getProperties().each { prop ->
	
		  // if the property is a closure, the type (by observation) is java.lang.Object
		  // TODO: reconsider passing on all java.lang.Objects		
		
		  if(!SolrUtil.IGNORED_PROPS.contains(prop.name) && prop.type != java.lang.Object) {
		  
			// look to see if the property has a solrIndex override method
			def overrideMethodName = (prop.name?.length() > 1) ? "indexSolr${prop.name[0].toUpperCase()}${prop.name.substring(1)}" : ""
			def overrideMethod = delegateDomainOjbect.metaClass.pickMethod(overrideMethodName)
			if(overrideMethod != null) {
			  overrideMethod.invoke(delegateDomainOjbect, doc)
			}
			else if(delegateDomainOjbect."${prop.name}" != null) {
				
			  def fieldName = solrFieldName(delegateDomainOjbect,prop.name);
			  				  
			  // fieldName may be null if the ignore annotion is used, not the best way to handle but ok for now
			  if(fieldName) {
				def docKey = prefix + fieldName
				def docValue = delegateDomainOjbect.properties[prop.name]
				
				// Removed because of issues with stale indexing when composed index changes
				// Recursive indexing of composition fields
				//if(GrailsClassUtils.getStaticPropertyValue(docValue.class, "enableSolrSearch") && depth < 3) {
				//  def innerDomainDesc = application.getArtefact(DomainClassArtefactHandler.TYPE, docValue.class.name)
				//  indexDomain(application, docValue, doc, ++depth, "${docKey}.")
				//} else {
				//  doc.addField(docKey, docValue)
				//}
			  
				// instead of the composition logic above, if the class is a domain class
				// then set the value to the Solr Id
				// TODO - reconsider this indexing logic as a whole
				if(DomainClassArtefactHandler.isDomainClass(docValue.class))
				  doc.addField(docKey, SolrUtil.getSolrId(docValue))
				else
				  doc.addField(docKey, docValue)
				
				// if the annotation asTextAlso is true, then also index this field as a text type independant of how else it's
				// indexed. The best way to handle the need to do this would be the properly configure the schema.xml file but
				// for those not familiar with Solr this is an easy way to make sure the field is processed as text which should
				// be the default search and processed with a WordDelimiterFilter
			  
				def clazzProp = clazz.declaredFields.find{ field -> field.name == prop.name}
				if(clazzProp && clazzProp.isAnnotationPresent(Solr) && clazzProp.getAnnotation(Solr).asTextAlso()) {
				  doc.addField("${prefix}${prop.name}_t", docValue)
				}
			  }
				
			  //println "Indexing: ${docKey} = ${docValue}"
			}
		  } // if ignored props
		} // domainDesc.getProperties().each
	
		// add a field to the index for the field type
		doc.addField(prefix + SolrUtil.TYPE_FIELD, delegateDomainOjbect.class.name)
		
		// add a field for the id which will be the classname dash id
		doc.addField("${prefix}id", "${delegateDomainOjbect.class.name}-${delegateDomainOjbect.id.toString()}")
		// println "${delegateDomainOjbect.class.name}-${delegateDomainOjbect.id.toString()}"
		
		if(doc.getField(SolrUtil.TITLE_FIELD) == null) {
		  def solrTitleMethod = delegateDomainOjbect.metaClass.pickMethod("solrTitle")
		  def solrTitle = (solrTitleMethod != null) ? solrTitleMethod.invoke(delegateDomainOjbect) : delegateDomainOjbect.toString()
		  doc.addField(SolrUtil.TITLE_FIELD, solrTitle)
		}
	  } // indexDomain
	
	def solrFieldName(delegate,name) { 
		
		def delegateDomainOjbect = delegate
		def prefix = ""
		def solrFieldName
		def clazz = (delegate.class.name == 'java.lang.Class') ? delegate : delegate.class
		def prop = clazz.declaredFields.find{ field -> field.name == name}
		
		if(!prop && name.contains(".")) {
		  prefix = name[0..name.lastIndexOf('.')]
		  name = name.substring(name.lastIndexOf('.')+1)
		  List splitName = name.split(/\./)
		  splitName.remove(splitName.size()-1)
		  splitName.each {
			//println "Before: ${delegateDomainOjbect}   ${it}"
			delegateDomainOjbect = delegateDomainOjbect."${it}"
			//println "After ${delegateDomainOjbect}"
		  }

		  prop = clazz.declaredFields.find{ field -> field.name == name}
		}
		
		def typeMap = SolrUtil.typeMapping["${prop?.type}"]
		solrFieldName = (typeMap) ? "${prefix}${name}${typeMap}" : "${prefix}${name}"
		
		// check for annotations
		if(prop?.isAnnotationPresent(Solr)) {
		  def anno = prop.getAnnotation(Solr)
		  if(anno.field())
			solrFieldName = prop.getAnnotation(Solr).field()
		  else if(anno.asText())
			solrFieldName = "${prefix}${name}_t"
		  else if(anno.ignore())
			solrFieldName = null;
		}
				
		return solrFieldName
	  }
   
}
