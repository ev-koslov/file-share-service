<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<spring:url value="/webjars/jquery/2.2.4/jquery.min.js" var="jquery_js"></spring:url>
<%--<spring:url value="https://code.jquery.com/jquery-1.11.3.min.js" var="jquery_js"></spring:url>--%>
<spring:url value="/webjars/bootstrap/3.3.7-1/js/bootstrap.min.js" var="bootstap_js"></spring:url>
<spring:url value="/webjars/bootstrap/3.3.7-1/css/bootstrap.min.css" var="bootstap_css"></spring:url>

<script src="${jquery_js}"></script>
<script src="${bootstap_js}"></script>
<link href="${bootstap_css}" rel="stylesheet">