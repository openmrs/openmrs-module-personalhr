<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<body>
<h2>Import CCD</h2>

FileName : "<strong> ${fileName} </strong>" - Uploaded Successful.

<br/><br/>
<h3>Formatted display: </h3>
<br/>

${displayContent}
 
<br/><br/>
<h3>FileContent: </h3>
<br/>
<c:out value="${fileContent}"></c:out>

</body>
</html>