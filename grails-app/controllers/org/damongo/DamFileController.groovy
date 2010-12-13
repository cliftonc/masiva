package org.damongo

import com.mongodb.gridfs.*;
import java.io.OutputStream;

class DamFileController {

    static allowedMethods = [save: "POST", update: "POST"]
	
	// File Manager service
	def fileManagerService
	def solrSearchService
	
    def index = {
        redirect(action: "list", params: params)
    }

	def addFile = {
		def ret = fileManagerService.addFile(params.file,params.type)
		render ret		
	}
	
	def downloadFile = {
		def ret = fileManagerService.downloadFile(params.url,params.file,params.type)
		render ret
	}
	
    def list = {
        params.max = Math.min(params.max ? params.int('max') : 24, 10000)
        [damFileInstanceList: DamFile.list(params), damFileInstanceTotal: DamFile.count()]
    }

	def show = {
				
		GridFSDBFile file = fileManagerService.getFile(params.id)			
		response.setContentType(file.contentType)
		response.setContentLength((int)file.length)
		OutputStream out = response.getOutputStream()
		file.writeTo out				
		out.close()
		
		
	}
	
	def solr = {
		
		def damFileInstance = DamFile.get(params.id)
        if (damFileInstance) {
			solrSearchService.indexDocument(damFileInstance)
        }
		
	}

	
    def delete = {
        def damFileInstance = DamFile.get(params.id)
        if (damFileInstance) {
            try {
	            damFileInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'damFile.label', default: 'DamFile'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'damFile.label', default: 'DamFile'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'damFile.label', default: 'DamFile'), params.id])}"
            redirect(action: "list")
        }
    }
}
