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
		        <a href="/masiva" id="logo"><img src="logo_print.png" alt="nclud™" /></a> 
		        <a href="#content" class="alt">skip nav</a> 
		        <ul id="nav"> 
		            <li class="current"><span><a href="/masiva">Home</a></span></li> 
		            <li><span><a href="/masiva/damFile">Media Browser</a></span></li> 
		            <li><span><a href="/masiva/help">Help</a></span></li>		             
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