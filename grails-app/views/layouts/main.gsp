<html>
    <head>
        <title><g:layoutTitle default="Grails" /></title>
        <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
        <link rel="stylesheet" href="${resource(dir:'css',file:'grid.css')}" />        
        <g:layoutHead />
        <g:javascript library="application" />
    </head>
    <body>
    	<div id="header"> 
		    <div class="container clear-both"> 
		        <a href="/" id="logo"><img src="logo_print.png" alt="nclud™" /></a> 
		        <a href="#content" class="alt">skip nav</a> 
		        <ul id="nav"> 
		            <li class="current"><span><a href="/">Home</a></span></li> 
		            <li><span><a href="http://nclud.com/about/">About Us</a></span></li> 
		            <li><span><a href="http://nclud.com/work/">Our Work</a></span></li> 
		            <li><span><a href="http://nclud.com/process/">Our Process</a></span></li> 
		            <li><span><a href="http://nclud.com/sketchbook/">Sketchbook</a></span></li><li><span><a href="http://nclud.com/contact/">Contact Us</a></span></li> 
		        </ul> 
		    </div> 
		</div> 
        
        <div class="clear-both"></div>
        
        <div id="spinner" class="spinner container_16" style="display:none;">
	            <img src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt',default:'Loading...')}" />
	    </div>
	    <div class="clear"></div>
        <g:layoutBody />
    </body>
</html>