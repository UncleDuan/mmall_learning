<html>
<body>
<h2>Hello World!</h2>
SpringMVC上传文件
<form name="upload" action="/manage/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" value="上传文件" />
</form>

富文本图片上传文件
<form name="upload" action="/manage/product/richtext_img_upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" value="上传文件" />
</form>
</body>
</html>
