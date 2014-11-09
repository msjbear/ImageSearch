<%@page import="com.homework.dao.SearchDaoImpl"%>
<%@page import="com.homework.dao.SearchDao"%>
<%@page import="com.homework.bean.Search"%>
<%@page import="java.net.URLEncoder"%>
<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>一点搜索</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<link rel="stylesheet" href="css/style.css" />
	
	<script type="text/javascript" src="/Struts2Test/dwr/engine.js"></script>
	<script type="text/javascript" src="/Struts2Test/dwr/interface/KeyWord.js"></script>
	<script type="text/javascript" src="js/jquery-1.7.2.js"></script>
	<script type="text/javascript" src="js/zzsc.js"></script>
	
	<script type="text/javascript">
		$(document).ready(function(){
			$("#searchKey").keyup(function(){
				var key = $("#searchKey").val();
				KeyWord.getName(key,callback);
			});
			function callback(date){
				var key = "";
				for(i=0;i<date.length;i++){
					key = key+date[i].name+"<br>";
				}
				$("#result").html("<b>"+key+"</b>");
			}
		});
	</script>
  </head>
  
  <body>
  	<center><b><font size="5" color="red">搜索热词</font></b></center>
  	<!-- 代码开始 --> 
	<div id="div1">
		<%
			SearchDao dao = new SearchDaoImpl();
			List<Search> hotsearchs = dao.findHotSearch();
			Search hotSearch = null;
			for(int i=0;i<hotsearchs.size();i++){
				hotSearch = hotsearchs.get(i);
		 %>
		<a href='search.action?searchKey=<%=URLEncoder.encode(hotSearch.getQueryString(),"utf-8") %>&pager.curPage=1'><%=hotSearch.getQueryString()%></a>
		<%
			}
			request.setAttribute("searchKey", hotSearch.getQueryString()); 
		 %>
	</div>
  	<center>
  		<form name="search" method="post" action="search">
	  		<!-- <img src="img/logo.jpg" style="padding-top:58px;"/> -->
	  		<br/>
	    	<input id="searchKey" name="searchKey" type="text" style="width:600px;height:38px;font-size:20px;font-weight:bold;"/>
	    	<input id="sub" type="submit" value="一点搜索"  style="height:40px;"/><br/>
	    	<div id="result"></div>
	    	<input type="hidden" name="pager.curPage" value="1"/>
	    	<input type="hidden" name="pager.perPageRows" value="3"/>
    	</form>
    </center>
  </body>
</html>
