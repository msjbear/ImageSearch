<%@page import="java.net.URLEncoder"%>
<%@page import="com.homework.bean.Pager"%>
<%@page import="org.apache.lucene.document.Document"%>
<%@ page contentType="text/html; charset=utf-8" language="java" import="java.util.*" errorPage="" %>
<link href="css/style.css" type="text/css" rel="stylesheet">
<%@ taglib uri="/struts-tags" prefix="s" %>
<html>
<script type="text/javascript">
		function select(){
			var curPage=document.getElementById("curPage").value;
			location.href="search.action?searchKey=<%=request.getAttribute("searchKey") %>&pager.curPage="+curPage;
		}
</script>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>一点搜索——搜索好帮手</title>
</head>
<%
List<Document> docs = (List)request.getAttribute("docs");
List<String> snippets = (List)request.getAttribute("snippets");
List<Float> score = (List)request.getAttribute("scores");
%>
<body>
<table width="876" border="0" cellspacing="0" cellpadding="0">
  <tr>
	<form name="search" method="post" action="search">
	   	<input id="searchKey" value="<%=new String(request.getParameter("searchKey").getBytes("utf-8"),"utf-8") %>" name="searchKey" type="text" style="width:600px;height:38px;font-size:20px;font-weight:bold;"/>
	   	<input id="sub" type="submit" value="一点搜索"  style="height:40px;"/><br/>
	   	<div id="result"></div>
	   	<input type="hidden" name="pager.curPage" value="1"/>
	    <input type="hidden" name="pager.perPageRows" value="${pager.perPageRows}"/>
	</form>
</table>
<br>
<br>
<label>找到${pagesCount}条结果,搜索用时${sessionScope.spendTime/1000}秒</label>
<br>
<br>
<%
	for(int i=0;i<docs.size();i++){
		Document doc = docs.get(i);
%>
<table width="1000" height="150" cellpadding="1" cellspacing="1" border="0">
	<tr>
		<td width="800">
			<a href="<%=doc.get("url") %>"><%=doc.get("title") %></a>
		</td>
		<td>
			相似度：<%=score.get(i) %>
		</td>
	</tr>
	<tr>
		<td  colspan="2">
		<%=snippets.get(i) %>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<a href="<%=doc.get("url") %>"><%=doc.get("url")%></a>
		</td>
	</tr>
</table>
<br><br>
<%
	}
	String searchKey = request.getParameter("searchKey"); 
 %>
 
 <!-- 分页超链接部分 -->
<table align="left">
  <tr >
	<td width="130">
		<A href='search.action?searchKey=<%=URLEncoder.encode(searchKey,"utf-8") %>&pager.curPage=1'>首页</A>&nbsp;&nbsp;
		<s:if test="pager.curPage>1">
			<A href='search.action?searchKey=<%=URLEncoder.encode(searchKey,"utf-8") %>&pager.curPage=${pager.curPage-1 }'>上一页</A>
		</s:if>
	</td>
	<td width="130">
		<s:if test="pager.curPage < pager.pageCount">
			<A href='search.action?searchKey=<%=URLEncoder.encode(searchKey,"utf-8") %>&pager.curPage=${pager.curPage+1}'>下一页</A>&nbsp;&nbsp;
		</s:if>
		<A href='search.action?searchKey=<%=URLEncoder.encode(searchKey,"utf-8") %>&pager.curPage=${pager.pageCount }'>尾页</A>
	</td>
	<td>共${pager.curPage}/${pager.pageCount}页&nbsp;
		转至	<select onchange="select()" id="curPage">
		<s:iterator begin="1" end="pager.pageCount" status="status" >
			<s:if test="#status.count==pager.curPage">
				<option value="${status.count}" selected="selected">${status.count }</option>
			</s:if>
			<s:else>
				<option value="${status.count }">${status.count } </option>
			</s:else>
		</s:iterator>
	</select>页	
	</td>
  </tr>
</table>
		
</body>
</html>
