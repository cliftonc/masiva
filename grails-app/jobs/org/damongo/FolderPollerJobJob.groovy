package org.damongo

class FolderPollerJobJob {
    
	def timeout = 5000l // execute job once in 5 seconds
	def concurrent = false
	def ingestService
	
    def execute() {
        
		def t = new DirectoryScanner(
			'ingest',
			50,
			[".*\\.jpeg",".*\\.jpg",".*\\.png",".*\\.mp4",".*\\.mov"],
			ingestService,
			500) 
		t.scan()		
		
    }
}
