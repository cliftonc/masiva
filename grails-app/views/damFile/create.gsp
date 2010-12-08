

<%@ page import="org.damongo.DamFile" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'damFile.label', default: 'DamFile')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${damFileInstance}">
            <div class="errors">
                <g:renderErrors bean="${damFileInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="chunkSize"><g:message code="damFile.chunkSize.label" default="Chunk Size" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: damFileInstance, field: 'chunkSize', 'errors')}">
                                    <g:textField name="chunkSize" value="${fieldValue(bean: damFileInstance, field: 'chunkSize')}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="contentType"><g:message code="damFile.contentType.label" default="Content Type" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: damFileInstance, field: 'contentType', 'errors')}">
                                    <g:textField name="contentType" value="${damFileInstance?.contentType}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="filename"><g:message code="damFile.filename.label" default="Filename" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: damFileInstance, field: 'filename', 'errors')}">
                                    <g:textField name="filename" value="${damFileInstance?.filename}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="length"><g:message code="damFile.length.label" default="Length" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: damFileInstance, field: 'length', 'errors')}">
                                    <g:textField name="length" value="${fieldValue(bean: damFileInstance, field: 'length')}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="md5"><g:message code="damFile.md5.label" default="Md5" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: damFileInstance, field: 'md5', 'errors')}">
                                    <g:textField name="md5" value="${damFileInstance?.md5}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="uploadDate"><g:message code="damFile.uploadDate.label" default="Upload Date" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: damFileInstance, field: 'uploadDate', 'errors')}">
                                    
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="url"><g:message code="damFile.url.label" default="Url" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: damFileInstance, field: 'url', 'errors')}">
                                    <g:textField name="url" value="${damFileInstance?.url}" />
                                </td>
                            </tr>
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
