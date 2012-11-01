<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>code=<%=request.getParameter("code")%></title>
</head>
<body>
<% if(request.getParameter("error") != null) { %>
   <div>
		error:<br>
		<textarea id="code" name="code" cols="72" rows=""><%=request.getParameter("error")%></textarea>
	</div>
	<div>
		error_description:<br>
		<textarea id="code" name="code" cols="72" rows=""><%=request.getParameter("error_description")%></textarea>
	</div>
	<div>
		error_uri:<br>
		<textarea id="code" name="code" cols="72" rows=""><%=request.getParameter("error_uri")%></textarea>
	</div>	
	 
<% } else { %>
    <div>
		Code:<br>
		<textarea id="code" name="code" cols="72" rows=""><%=request.getParameter("code")%></textarea>
	</div>
<% } %>

	<div>
		State:<br>
		<textarea id="state" name="" cols="72" rows=""><%=request.getParameter("state")%></textarea>
	</div>	
</body>

</html>