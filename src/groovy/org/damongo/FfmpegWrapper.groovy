package org.damongo

import java.util.List;
import org.apache.commons.logging.impl.*;

class FfmpegWrapper {

	def tempDir, resultDir 
	SLF4JLocationAwareLog logger
		
	public FfmpegWrapper(String tempDir, logger){
		this.tempDir = tempDir		
		this.logger = logger
	}
	
	
	public boolean browse(String filePath, String fileName, String outFileName) {
		
		String[] cmdLine
		cmdLine = ["ffmpeg","-i",filePath,"-acodec","libmp3lame","-ab","96k","-vcodec","libx264","-vpre","slow","-crf","22","-threads","0",tempDir + "/" + outFileName]
		this.resultDir = "web-app/videos"
		runCmd(cmdLine,filePath,fileName,outFileName)
		
	}

	public boolean thumbnail(String filePath, String fileName, String outFileName) {
		
		String[] cmdLine
		cmdLine = ["ffmpeg","-y","-i",filePath,"-f","mjpeg","-ss","10","-vframes","1","-s","240x136","-an",tempDir + "/" + outFileName]
		this.resultDir = "web-app/images/thumbnails"
		runCmd(cmdLine,filePath,fileName,outFileName)
		
	}
	
	public boolean runCmd(String[] cmdLine, String filePath, String fileName, String outFileName) {
		
						File fOriginal = new File(filePath);
						File fTemp = new File(tempDir + "/" + outFileName);
						File fResult = new File(resultDir + "/" + outFileName);
												
						ProcessBuilder pb = new ProcessBuilder(cmdLine);
						pb.redirectErrorStream(true);
						Process p = null;
						
								try {
										p = pb.start();
										logProcessOutputAndErrors(p);
								} catch (Exception ex) {
										logger.error("Can't create process to convert '" + fileName	+ "'.", ex);
										p.destroy();
										return false;
								}
		
								// wait until the process is finished
								try {
										p.waitFor();
								} catch (InterruptedException e) {
										p.destroy();
								}
		
						if (p.exitValue() != 0) {
								logger.error("Error while converting '" + fileName + "'.");
								return false;
						}
		
						if (fTemp.length() == 0) {
								logger.error("Error while converting '" + fileName
												+ "'. File size is zero.");
								return false;
						}
		
						boolean renameResult = fTemp.renameTo(fResult);
						if (!renameResult) {
								logger.error("Can't move '" + fileName
												+ "' from temporary directory to result directory.");
								return false;
						}
		
						logger.info("'" + fileName + "' converted successfully.");
						return true;
		}
		
		private void logProcessOutputAndErrors(Process p) {
	
						BufferedReader processOutputReader = null;
						BufferedReader processErrorReader = null;
						try {
								int character;
								StringBuilder processLog = new StringBuilder();
		
								processOutputReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
								while ((character = processOutputReader.read()) != -1) {
										processLog.append((char) character);
								}
								processLog.append('\n');
		
								processErrorReader = new BufferedReader(new InputStreamReader(p
												.getErrorStream()));
								while ((character = processErrorReader.read()) != -1) {
										processLog.append((char) character);
								}
		
								logger.debug(processLog);
						} catch (Exception ex) {
										logger.error("Can't read process output.", ex);
						// this is not fatal, continue
						} finally {
								if (processOutputReader != null) {
										try {
												processOutputReader.close();
										} catch (Exception ex) {
												// ignore
										}
								}
								if (processErrorReader != null) {
										try {
												processErrorReader.close();
										} catch (Exception ex) {
												// ignore
										}
								}
						}
				}
	}	