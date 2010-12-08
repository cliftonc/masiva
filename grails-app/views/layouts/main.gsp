<html>
    <head>
        <title><g:layoutTitle default="Grails" /></title>
        <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
        <link rel="stylesheet" href="${resource(dir:'css',file:'grid.css')}" />        
        <g:layoutHead />
        <g:javascript library="application" />
    </head>
    <body>
    	<div class="header container_16">	        
	        <div id="masivaLogo" class="alpha grid_16"><a href="http://cliftonc.github.com/masiva"><img src="${resource(dir:'images',file:'masiva_logo.png')}" alt="masiva" border="0" /></a></div>	        
        </div>
        <div id="spinner" class="spinner container_16" style="display:none;">
	            <img src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt',default:'Loading...')}" />
	    </div>
	    <div class="clear"></div>
        <g:layoutBody />
    </body>
</html>