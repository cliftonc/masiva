package org.damongo

import java.io.File;
import java.util.regex.*;

class DirectoryScanner {
	
	def root_dir, exts, thread_max_cnt, thread_tracker, count, ingestService, minFileAge
	
	public DirectoryScanner(String basedir, int t_count, List extensions, def ingestService,Long minFileAge){
		this.root_dir = basedir
		this.exts = compile_regex(extensions)
		this.thread_max_cnt = t_count
		this.thread_tracker = []
		this.ingestService = ingestService
		this.count=0
		this.minFileAge=minFileAge
	}
	
	def scan(){
		try{
			def dir = new File(this.root_dir)
			check_for_files(this.root_dir)
			//recursively search directories
			dir.eachDir{ subDir->
				//thread it off
				if(this.thread_tracker.size() > this.thread_max_cnt){
					this.thread_tracker.each{it->it.join()}
					this.thread_tracker=[]
				}
				this.thread_tracker << Thread.start{
					subDir.eachFileRecurse{ fh ->
						check_using_compiled_regex(fh.canonicalPath)
					}
				}
			}
		}catch(Exception e){
			println("error ${e}")
		}
		this.thread_tracker.each{it->it.join()}		
	}
	def check_using_compiled_regex(String file){
		try{
			def var = this.exts.find{it.matcher(file).matches()}
			if(var){
			
				// We have the file, mark it and add it to the ingest queue
				def qfileName = file + ".queued"				
				
				File tmpFile = new File(file)
							
				Long curDate = new Date().getTime()
				Long fileDate = new Date(tmpFile.lastModified()).getTime()
				
				def fileAge = curDate - fileDate
					
				if(fileAge > this.minFileAge) {
				
					tmpFile.renameTo(qfileName)							
					this.count+=1;				
					this.ingestService.sendMessage("ingestQueue",qfileName)
				
				}
				
			}
		} catch(Exception e) {
			println("Error processing file ${file}\n$e")
		}
	}
	def check_for_files(String dir){
		try{ new File(dir).eachFile{ file ->
				check_using_compiled_regex(file.canonicalPath)
		}
		}catch(Exception e){println("Not a Directory ${dir}\n$e")}
	}
	def compile_regex(List list){
		List ret_list=[]
		list.each{ ret_list <<    Pattern.compile(it,Pattern.CASE_INSENSITIVE)}
		return ret_list
	}
}