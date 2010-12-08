
<%@ page import="org.damongo.DamFile" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'damFile.label', default: 'DamFile')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav container_16">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
        </div>
        <div class="clear"></div>
        <div id="body" class="container_16">
            <g:if test="${flash.message}">
	            <div class="message alpha grid_16">${flash.message}</div>
            </g:if>
        	<div class="clear"></div>    
                   	<g:each in="${damFileInstanceList}" status="i" var="damFileInstance">
                    <div id="${damFileInstance.id}" class="asset grid_2 <%
											if(i % 8 == 0) {
												print "clear-row"
											}
										 %>">                   
                            	<div class="asset-image">
                            		<a href="show/${damFileInstance.id}">
                            			<img title="${damFileInstance.contentType}" class="asset-image" src="${resource(dir:'images/thumbnails',file: damFileInstance.id.toString() + "_thumb.jpg")}"/>                            			                            			
                            		</a>                            		
                            	</div>                            	
							</div>
					</g:each>                   
            
            <div class="clear"></div>
            
            <div class="paginateButtons">
                <g:paginate total="${damFileInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
