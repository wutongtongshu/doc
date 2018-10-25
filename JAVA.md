# 1 实战

## 1.1 文件上传

###1.1.1 spring mvc 单文件上传

jsp文件如下：

```html
<form action="${pageContext.request.contextPath}/test/upload.do" 
      method="post" 
      enctype="multipart/form-data">
    
  <input type="file" name="img"><br /> 
  <input type="submit" name="提交">
</form>
```

java 的 controller如下：

```java
@Controller
@RequestMapping("/test")
public class MyController {
  @RequestMapping(value = "/upload, method = RequestMethod.POST)
  // 这里的MultipartFile对象变量名跟表单中的file类型的input标签的name相同，所以框架会自动用MultipartFile对象来接收上传过来的文件，当然也可以使用@RequestParam("img")指定其对应的参数名称
  public String upload(MultipartFile img, HttpSession session)
      throws Exception {
    // 如果没有文件上传，MultipartFile也不会为null，可以通过调用getSize()方法获取文件的大小来判断是否有上传文件
    if (img.getSize() > 0) {
      // 得到项目在服务器的真实根路径，如：/home/tomcat/webapp/项目名/images
      String path = session.getServletContext().getRealPath("images");
      // 得到文件的原始名称，如：美女.png
      String fileName = img.getOriginalFilename();
      // 通过文件的原始名称，可以对上传文件类型做限制，如：只能上传jpg和png的图片文件
      if (fileName.endsWith("jpg") || fileName.endsWith("png")) {
        File file = new File(path, fileName);
        img.transferTo(file);
        return "/success.jsp";
      }
    }
    return "/error.jsp";
  }
}
```

spring 配置：

```java
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:mvc="http://www.springframework.org/schema/mvc" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:aop="http://www.springframework.org/schema/aop" xmlns:tx="http://www.springframework.org/schema/tx"
  xmlns:context="http://www.springframework.org/schema/context"
  xsi:schemaLocation="
    http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans.xsd
    http://www.springframework.org/schema/tx
    http://www.springframework.org/schema/tx/spring-tx.xsd
    http://www.springframework.org/schema/mvc
    http://www.springframework.org/schema/mvc/spring-mvc.xsd
    http://www.springframework.org/schema/aop
    http://www.springframework.org/schema/aop/spring-aop.xsd
    http://www.springframework.org/schema/context
    http://www.springframework.org/schema/context/spring-context.xsd">

  ...

  <!-- 注意：CommonsMultipartResolver的id是固定不变的，一定是multipartResolver，不可修改 -->
  <bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
    <!-- 如果上传后出现文件名中文乱码可以使用该属性解决 -->
    <property name="defaultEncoding" value="utf-8"/>
    <!-- 单位是字节，不设置默认不限制总的上传文件大小，这里设置总的上传文件大小不超过1M（1*1024*1024） -->
    <property name="maxUploadSize" value="1048576"/>
    <!-- 跟maxUploadSize差不多，不过maxUploadSizePerFile是限制每个上传文件的大小，而maxUploadSize是限制总的上传文件大小 -->
    <property name="maxUploadSizePerFile" value="1048576"/>
    <!-- 设定文件上传时写入内存的最大值，如果小于这个参数不会生成临时文件，默认为10240 -->
    <property name="maxInMemorySize" value="40960"></property>
    <!-- 上传文件的临时路径,超过上述值后，会生成临时文件 -->
    <property name="uploadTempDir" value="fileUpload/temp"></property>
    <!-- 延迟文件解析 -->
    <property name="resolveLazily" value="true"/>
  </bean>

  <!-- 设置一个简单的异常解析器，当文件上传超过大小限制时跳转 -->
  <bean class="org.springframework.web.servlet.handler.SimpleMappingExceptionResolver">
    <property name="defaultErrorView" value="/error.jsp"/>
  </bean>
</beans>
```

###1.1.2 多文件上传

```html
<form action="${pageContext.request.contextPath}/test/upload.do" method="post" enctype="multipart/form-data">
  file 1 : <input type="file" name="imgs"><br /> 
  file 2 : <input type="file" name="imgs"><br /> 
  file 3 : <input type="file" name="imgs"><br /> 
  <input type="submit" name="提交">
</form>
```

**2、控制器**

控制器中的处理方法使用MultipartFile[]数组作为接收参数，并不能直接使用，需要校正参数，具体说明请看代码注释。

```java
// 这里的MultipartFile[] imgs表示前端页面上传过来的多个文件，imgs对应页面中多个file类型的input标签的name，但框架只会将一个文件封装进一个MultipartFile对象，
  // 并不会将多个文件封装进一个MultipartFile[]数组，直接使用会报[Lorg.springframework.web.multipart.MultipartFile;.<init>()错误，
  // 所以需要用@RequestParam校正参数（参数名与MultipartFile对象名一致），当然也可以这么写：@RequestParam("imgs") MultipartFile[] files。
  public String upload(@RequestParam MultipartFile[] imgs, HttpSession session) throws Exception 
```

###1.1.3 文件上传综合

当然，项目开发中，场景可能并不是这么简单，上述的多文件上传是一个个文件选择后一起上传（即多个name相同的input标签），那要是我项目中只要一个input标签就可以一次性多个文件呢？又或者一个页面中既要一个个选择的多文件上传，又要一次性选择的多文件上传，还要有单文件上传呢？没问题，MultipartFile[]通吃，代码也很easy，下面直接上代码。

```html
<form action="${pageContext.request.contextPath}/test/upload.do" method="post" enctype="multipart/form-data">

  一次选择多个文件的多文件上传 : <br /> 
  <input type="file" name="imgs1" multiple><br /> <br /> 

  一次选择一个文件的多文件上传 : <br /> 
  <input type="file" name="imgs2"><br /> 
  <input type="file" name="imgs2"><br /><br /> 

  单文件上传 : <br /> 
  <input type="file" name="imgs3"><br /><br /> 
  <input type="submit" name="提交">
</form>
```

```java
@Controller
@RequestMapping("/test")
public class MyController {

  @RequestMapping(value = "/upload.do", method = RequestMethod.POST)
  public String upload(@RequestParam MultipartFile[] imgs1,@RequestParam MultipartFile[] imgs2,@RequestParam MultipartFile[] imgs3, HttpSession session)
      throws Exception {
    String path = session.getServletContext().getRealPath("images");
    for (MultipartFile img : imgs1) {
      uploadFile(path, img);
    }
    for (MultipartFile img : imgs2) {
      uploadFile(path, img);
    }
    for (MultipartFile img : imgs3) {
      uploadFile(path, img);
    }
    return "/success.jsp";
  }

  private void uploadFile(String path, MultipartFile img) throws IOException {
    if (img.getSize() > 0) {
      String fileName = img.getOriginalFilename();
      if (fileName.endsWith("jpg") || fileName.endsWith("png")) {
        File file = new File(path, fileName);
        img.transferTo(file);
      }
    }
  }
}
```

MultipartFile[]就是如此强大，不管单个多个，逻辑处理一样，所以建议在项目开发中使用MultipartFile[]作为文件的接收参数。

### 1.1.4 拓展

MultipartFile类常用的一些方法

```java
String getContentType()//获取文件MIME类型
InputStream getInputStream()//获取文件流
String getName() //获取表单中文件组件的名字
String getOriginalFilename() //获取上传文件的原名
long getSize() //获取文件的字节大小，单位byte
boolean isEmpty() //是否为空
void transferTo(File dest) 
```

CommonsMultipartResolver的属性

```java
defaultEncoding：表示用来解析request请求的默认编码格式，当没有指定的时候根据Servlet规范会使用默认值ISO-8859-1。当request自己指明了它的编码格式的时候就会忽略这里指定的defaultEncoding。
uploadTempDir：设置上传文件时的临时目录，默认是Servlet容器的临时目录。
maxUploadSize：设置允许上传的总的最大文件大小，以字节为单位计算。当设为-1时表示无限制，默认是-1。
maxUploadSizePerFile：跟maxUploadSize差不多，不过maxUploadSizePerFile是限制每个上传文件的大小，而maxUploadSize是限制总的上传文件大小。
maxInMemorySize：设置在文件上传时允许写到内存中的最大值，以字节为单位计算，默认是10240。
resolveLazily：为true时，启用推迟文件解析，以便在UploadAction中捕获文件大小异常。
```

**注意**

1. 在开发过程中，建议把配置文件中的异常解析器（SimpleMappingExceptionResolver）先注释掉，方便我们查看错误。
2. 有时候上传出错，是因为我们在配置文件中限制了上传文件的大小，你可以不加这个限制，但个人建议这个限制最好还是加上，具体文件大小限制请根据公司项目情况而定。
3. SpringMVC中使用MultipartFile接收上传文件需要依赖两个jar包，分别是：commons-fileupload-1.3.3.jar、

### 1.1.5 MIME类型 /maim/

| 扩展名  | 类型/子类型                             |
| ------- | --------------------------------------- |
|         | application/octet-stream                |
| 323     | text/h323                               |
| acx     | application/internet-property-stream    |
| ai      | application/postscript                  |
| aif     | audio/x-aiff                            |
| aifc    | audio/x-aiff                            |
| aiff    | audio/x-aiff                            |
| asf     | video/x-ms-asf                          |
| asr     | video/x-ms-asf                          |
| asx     | video/x-ms-asf                          |
| au      | audio/basic                             |
| avi     | video/x-msvideo                         |
| axs     | application/olescript                   |
| bas     | text/plain                              |
| bcpio   | application/x-bcpio                     |
| bin     | application/octet-stream                |
| bmp     | image/bmp                               |
| c       | text/plain                              |
| cat     | application/vnd.ms-pkiseccat            |
| cdf     | application/x-cdf                       |
| cer     | application/x-x509-ca-cert              |
| class   | application/octet-stream                |
| clp     | application/x-msclip                    |
| cmx     | image/x-cmx                             |
| cod     | image/cis-cod                           |
| cpio    | application/x-cpio                      |
| crd     | application/x-mscardfile                |
| crl     | application/pkix-crl                    |
| crt     | application/x-x509-ca-cert              |
| csh     | application/x-csh                       |
| css     | text/css                                |
| dcr     | application/x-director                  |
| der     | application/x-x509-ca-cert              |
| dir     | application/x-director                  |
| dll     | application/x-msdownload                |
| dms     | application/octet-stream                |
| doc     | application/msword                      |
| dot     | application/msword                      |
| dvi     | application/x-dvi                       |
| dxr     | application/x-director                  |
| eps     | application/postscript                  |
| etx     | text/x-setext                           |
| evy     | application/envoy                       |
| exe     | application/octet-stream                |
| fif     | application/fractals                    |
| flr     | x-world/x-vrml                          |
| gif     | image/gif                               |
| gtar    | application/x-gtar                      |
| gz      | application/x-gzip                      |
| h       | text/plain                              |
| hdf     | application/x-hdf                       |
| hlp     | application/winhlp                      |
| hqx     | application/mac-binhex40                |
| hta     | application/hta                         |
| htc     | text/x-component                        |
| htm     | text/html                               |
| html    | text/html                               |
| htt     | text/webviewhtml                        |
| ico     | image/x-icon                            |
| ief     | image/ief                               |
| iii     | application/x-iphone                    |
| ins     | application/x-internet-signup           |
| isp     | application/x-internet-signup           |
| jfif    | image/pipeg                             |
| jpe     | image/jpeg                              |
| jpeg    | image/jpeg                              |
| jpg     | image/jpeg                              |
| js      | application/x-javascript                |
| latex   | application/x-latex                     |
| lha     | application/octet-stream                |
| lsf     | video/x-la-asf                          |
| lsx     | video/x-la-asf                          |
| lzh     | application/octet-stream                |
| m13     | application/x-msmediaview               |
| m14     | application/x-msmediaview               |
| m3u     | audio/x-mpegurl                         |
| man     | application/x-troff-man                 |
| mdb     | application/x-msaccess                  |
| me      | application/x-troff-me                  |
| mht     | message/rfc822                          |
| mhtml   | message/rfc822                          |
| mid     | audio/mid                               |
| mny     | application/x-msmoney                   |
| mov     | video/quicktime                         |
| movie   | video/x-sgi-movie                       |
| mp2     | video/mpeg                              |
| mp3     | audio/mpeg                              |
| mpa     | video/mpeg                              |
| mpe     | video/mpeg                              |
| mpeg    | video/mpeg                              |
| mpg     | video/mpeg                              |
| mpp     | application/vnd.ms-project              |
| mpv2    | video/mpeg                              |
| ms      | application/x-troff-ms                  |
| mvb     | application/x-msmediaview               |
| nws     | message/rfc822                          |
| oda     | application/oda                         |
| p10     | application/pkcs10                      |
| p12     | application/x-pkcs12                    |
| p7b     | application/x-pkcs7-certificates        |
| p7c     | application/x-pkcs7-mime                |
| p7m     | application/x-pkcs7-mime                |
| p7r     | application/x-pkcs7-certreqresp         |
| p7s     | application/x-pkcs7-signature           |
| pbm     | image/x-portable-bitmap                 |
| pdf     | application/pdf                         |
| pfx     | application/x-pkcs12                    |
| pgm     | image/x-portable-graymap                |
| pko     | application/ynd.ms-pkipko               |
| pma     | application/x-perfmon                   |
| pmc     | application/x-perfmon                   |
| pml     | application/x-perfmon                   |
| pmr     | application/x-perfmon                   |
| pmw     | application/x-perfmon                   |
| pnm     | image/x-portable-anymap                 |
| pot,    | application/vnd.ms-powerpoint           |
| ppm     | image/x-portable-pixmap                 |
| pps     | application/vnd.ms-powerpoint           |
| ppt     | application/vnd.ms-powerpoint           |
| prf     | application/pics-rules                  |
| ps      | application/postscript                  |
| pub     | application/x-mspublisher               |
| qt      | video/quicktime                         |
| ra      | audio/x-pn-realaudio                    |
| ram     | audio/x-pn-realaudio                    |
| ras     | image/x-cmu-raster                      |
| rgb     | image/x-rgb                             |
| rmi     | audio/mid                               |
| roff    | application/x-troff                     |
| rtf     | application/rtf                         |
| rtx     | text/richtext                           |
| scd     | application/x-msschedule                |
| sct     | text/scriptlet                          |
| setpay  | application/set-payment-initiation      |
| setreg  | application/set-registration-initiation |
| sh      | application/x-sh                        |
| shar    | application/x-shar                      |
| sit     | application/x-stuffit                   |
| snd     | audio/basic                             |
| spc     | application/x-pkcs7-certificates        |
| spl     | application/futuresplash                |
| src     | application/x-wais-source               |
| sst     | application/vnd.ms-pkicertstore         |
| stl     | application/vnd.ms-pkistl               |
| stm     | text/html                               |
| svg     | image/svg+xml                           |
| sv4cpio | application/x-sv4cpio                   |
| sv4crc  | application/x-sv4crc                    |
| swf     | application/x-shockwave-flash           |
| t       | application/x-troff                     |
| tar     | application/x-tar                       |
| tcl     | application/x-tcl                       |
| tex     | application/x-tex                       |
| texi    | application/x-texinfo                   |
| texinfo | application/x-texinfo                   |
| tgz     | application/x-compressed                |
| tif     | image/tiff                              |
| tiff    | image/tiff                              |
| tr      | application/x-troff                     |
| trm     | application/x-msterminal                |
| tsv     | text/tab-separated-values               |
| txt     | text/plain                              |
| uls     | text/iuls                               |
| ustar   | application/x-ustar                     |
| vcf     | text/x-vcard                            |
| vrml    | x-world/x-vrml                          |
| wav     | audio/x-wav                             |
| wcm     | application/vnd.ms-works                |
| wdb     | application/vnd.ms-works                |
| wks     | application/vnd.ms-works                |
| wmf     | application/x-msmetafile                |
| wps     | application/vnd.ms-works                |
| wri     | application/x-mswrite                   |
| wrl     | x-world/x-vrml                          |
| wrz     | x-world/x-vrml                          |
| xaf     | x-world/x-vrml                          |
| xbm     | image/x-xbitmap                         |
| xla     | application/vnd.ms-excel                |
| xlc     | application/vnd.ms-excel                |
| xlm     | application/vnd.ms-excel                |
| xls     | application/vnd.ms-excel                |
| xlt     | application/vnd.ms-excel                |
| xlw     | application/vnd.ms-excel                |
| xof     | x-world/x-vrml                          |
| xpm     | image/x-xpixmap                         |
| xwd     | image/x-xwindowdump                     |
| z       | application/x-compress                  |
| zip     | application/zip                         |