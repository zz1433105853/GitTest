import com.google.common.collect.Maps;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.ty.common.mapper.JaxbMapper;
import com.ty.common.mapper.JsonMapper;
import com.ty.common.utils.*;
import com.ty.modules.msg.entity.MsgRecord;
import com.ty.modules.msg.entity.MsgResponse;
import com.ty.modules.msg.entity.SpecialMsgVo;
import com.ty.modules.tunnel.entity.MsgReply;
import com.ty.modules.tunnel.send.container.entity.container.md.ThirdMdSendMsgResult;
import com.ty.modules.tunnel.send.container.entity.container.qxt.ThirdQxt;
import com.ty.modules.tunnel.send.container.entity.container.yx.ThirdYxMo;
import com.ty.modules.tunnel.send.container.entity.container.yx.ThirdYxSendMoResult;
import com.ty.modules.tunnel.send.container.entity.container.yx.ThirdYxSendMsgresult;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.RandomStringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by tykfkf02 on 2017/7/22.
 */
public class Test {
    @org.junit.Test
    public void testYx() throws Exception {
        String s = RandomStringUtils.randomNumeric(4);
        System.out.println(s);

    }

    @org.junit.Test
    public void test(){
        String test = "[{\"mobile\":\"13934119175\",\"content\":\"【天元科技】\n" +
                "尊敬的穆志全先生您好，您在马西信用社的贷款余额26000元截止2017年8月21日，本月应归还利息377.76元，请及时办理结息手续，以免对您的信用记录造成影响，如果您已归还，请忽略此条信息，详\n" +
                "马西信用社0358-3066085.\"},{\"mobile\":\"13485355633\",\"content\":\"【天元科技】\n" +
                "尊敬的穆志全先生您好，您在马西信用社的贷款余额26000元截止2017年8月21日，本月应归还利息377.76元，请及时办理结息手续，以免对您的信用记录造成影响，如果您已归还，请忽略此条信息，详\n" +
                "马西信用社0358-3066085.\"}]";
        test = test.replace("\n","\\n");
        List<SpecialMsgVo> data = JsonMapper.getInstance().fromJson(test, JsonMapper.getInstance().createCollectionType(ArrayList.class, SpecialMsgVo.class));
    }

    public void sort(int[] a)
    {
        int temp = 0;
        for (int i = a.length - 1; i > 0; --i)
        {
            for (int j = 0; j < i; ++j)
            {
                if (a[j + 1] < a[j])
                {
                    temp = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = temp;
                }
            }
        }
    }

    @org.junit.Test
    public void testString(){
        String url = "http://sdk.entinfo.cn:8060/webservice.asmx/mdMmsSend";
        String mobile = "13485355633,18903404576";
        String content = "测试";
        String title = "测试2组";
        try {
            String sendContent = generateContent(content);
            if(StringUtils.isNotBlank(sendContent)){
                sendContent = sendContent.substring(0,sendContent.length()-1);
                HttpResponse<String> result = Unirest.post(url)
                        .field("sn", "DXX-FFF-010-01331")
                        .field("pwd", MD5Utils.getMD5("DXX-FFF-010-01331" + "b64fd3d-a49", "UTF-8").toUpperCase())
                        .field("mobile", mobile)
                        .field("content", sendContent)
                        .field("title", title)
                        .field("stime", "").asString();
                String xmlResult = result.getBody();
                ThirdMdSendMsgResult thirdMdSendMsgResult = JaxbMapper.fromXml(xmlResult, ThirdMdSendMsgResult.class);
                thirdMdSendMsgResult = thirdMdSendMsgResult;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateContent(String content) throws Exception{
        StringBuffer sendContent = new StringBuffer();
        String[] contentSplit = content.split("////");
        for(int i=1;i<=contentSplit.length;i++){
            String[] c = contentSplit[i-1].split("##");
            if(!"null".equals(c[0])){
                String separator = File.separator;
                String savePath = separator+"storage"+separator+"temp.txt";
                Encodes.writeStringToFile(savePath,c[0]);
                String txtBase64 = Encodes.encodeBase64File(savePath);
                sendContent.append(i).append("_1.txt,").append(txtBase64).append(";");
            }
            if(!"null".equals(c[1])){
                String prefix=c[1].substring(c[1].lastIndexOf(".")+1);
                String imgBase64 = Encodes.getURLImage(c[1]);
                sendContent.append(i).append("_2.").append(prefix).append(",").append(imgBase64).append(";");
            }
            if(!"null".equals(c[2])){
                String prefix=c[2].substring(c[2].lastIndexOf(".")+1);
                String syBase64 = Encodes.getURLImage(c[2]);
                sendContent.append(i).append("_3.").append(prefix).append(",").append(syBase64).append(";");
            }
        }
        return sendContent.toString();
    }


    public void WriteStringToFile(String filePath,String content) {
        try {
            File file = new File(filePath);
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            ps.println(content);// 往文件里写入字符串
           // ps.append("http://www.jb51.net");// 在已有的基础上添加字符串
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeStringToFile(String filePath,String content) throws Exception {
        try {
            File file = new File(filePath);
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            ps.print("");// 往文件里写入字符串
            ps.close();

            Writer outTxt = new OutputStreamWriter(new FileOutputStream(file,true), "GBK");
            outTxt.write(content);
            outTxt.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void test1() throws Exception {
      String a = "sdfsdfsdfsdf;sfsfsdfdgdfg;";
      String ss = a.substring(0,a.length()-1);
      ss=ss;
    }

    @org.junit.Test
    public void mdCx(){
        String url = "http://sdk.entinfo.cn:8060/webservice.asmx/mdMmsSend";
        try {
            String txt = encodeBase64File("C:/Users/tykfkf02/Desktop/ttttt.txt");
           // String img = encodeBase64File("C:/Users/tykfkf02/Desktop/1.png");
            String img = getURLImage("https://imgsa.baidu.com/forum/wh%3D200%2C90%3B/sign=eebbb267adefce1bea7ec0c89f61dfe7/b8efb2fb43166d22311ee4054c2309f79152d2cf.jpg");
            decoderBase64File(img,"C:/Users/tykfkf02/Desktop/test1.png");
            String content = "1_1.txt,"+txt+";"+"1_2.jpg,"+img+";"+"2_1.txt,"+txt+";"+"2_2.jpg,"+img;
            HttpResponse<String> result = Unirest.post(url)
                    .field("sn", "DXX-FFF-010-01331")
                    .field("pwd", MD5Utils.getMD5("DXX-FFF-010-01331" + "b64fd3d-a49", "UTF-8").toUpperCase())
                    .field("mobile", "18903404576")
                    .field("content", content)
                    .field("title", "国庆假要来了")
                    .field("stime", "").asString();
            String xmlResult = result.getBody();
            xmlResult = xmlResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将文件转成base64 字符串
     * @return  *
     * @throws Exception
     */
    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return new BASE64Encoder().encode(buffer);
    }

    /**
     * 将base64字符解码保存文件
     * @param base64Code
     * @param targetPath
     * @throws Exception
     */
    public static void decoderBase64File(String base64Code, String targetPath)
            throws Exception {
        byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
        FileOutputStream out = new FileOutputStream(targetPath);
        out.write(buffer);
        out.close();
    }

    /**
     * @Title: GetImageStrFromUrl
     * @Description: TODO(将一张网络图片转化成Base64字符串)
     * @param imgURL 网络资源位置
     * @return Base64字符串
     */
    public static String getImageStrFromUrl(String imgURL) {
        byte[] data = null;
        try {
            // 创建URL
            URL url = null;
            try {
                url = new URL(imgURL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            // 创建链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            InputStream inStream = conn.getInputStream();
            data = new byte[inStream.available()];
            inStream.read(data);
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        // 返回Base64编码过的字节数组字符串
        return encoder.encode(data);
    }

    public static String getURLImage(String imageUrl) throws Exception {
        //new一个URL对象
        URL url = new URL(imageUrl);
        //打开链接
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置请求方式为"GET"
        conn.setRequestMethod("GET");
        //超时响应时间为5秒
        conn.setConnectTimeout(5 * 1000);
        //通过输入流获取图片数据
        InputStream inStream = conn.getInputStream();
        //得到图片的二进制数据，以二进制封装得到数据，具有通用性
        byte[] data = readInputStream(inStream);
        BASE64Encoder encode = new BASE64Encoder();
        String s = encode.encode(data);
        return s;
    }

    private static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while( (len=inStream.read(buffer)) != -1 ){
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }
}
