<html>
<body>
<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<h2>Hello World!</h2>

springmvc上传文件
<form name="upload test" action="/manage/product/upload.do"  method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" value="upload file" />
</form>

富文本文件上传
<form name="upload test" action="/manage/product/richtext_img_upload.do"  method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" value="upload richtext" />
</form>

</body>
</html>
