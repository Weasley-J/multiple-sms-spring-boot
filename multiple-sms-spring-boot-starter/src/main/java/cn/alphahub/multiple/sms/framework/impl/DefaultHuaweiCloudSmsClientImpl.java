package cn.alphahub.multiple.sms.framework.impl;

import cn.alphahub.multiple.sms.annotation.EnableMultipleSms;
import cn.alphahub.multiple.sms.config.entity.HuaweiSmsProperties;
import cn.alphahub.multiple.sms.domain.AbstractSmsRequest;
import cn.alphahub.multiple.sms.domain.AbstractSmsResponse;
import cn.alphahub.multiple.sms.domain.BaseSmsResponse;
import cn.alphahub.multiple.sms.framework.SmsClient;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static javax.servlet.RequestDispatcher.ERROR_MESSAGE;

/**
 * 华为云短信实现
 *
 * @author lwj
 * @version 1.0
 * @apiNote <a href="https://support.huaweicloud.com/devg-msgsms/sms_04_0002.html">华为云短信帮助链接</a>，暂不支持个人用户申请
 */
@Slf4j
@Component
@ConditionalOnBean(annotation = {EnableMultipleSms.class})
public class DefaultHuaweiCloudSmsClientImpl implements SmsClient {
    /**
     * 无需修改,用于格式化鉴权头域,给"X-WSSE"参数赋值
     */
    private static final String WSSE_HEADER_FORMAT = "UsernameToken Username=\"%s\",PasswordDigest=\"%s\",Nonce=\"%s\",Created=\"%s\"";
    /**
     * 无需修改,用于格式化鉴权头域,给"Authorization"参数赋值
     */
    private static final String AUTH_HEADER_VALUE = "WSSE realm=\"SDP\",profile=\"UsernameToken\",type=\"Appkey\"";
    /**
     * 短信配置元数据
     */
    private final HuaweiSmsProperties smsProperties;

    public DefaultHuaweiCloudSmsClientImpl(HuaweiSmsProperties smsProperties) {
        this.smsProperties = smsProperties;
    }

    @Override
    public AbstractSmsResponse send(@Valid AbstractSmsRequest smsRequest) {
        log.info("sms_request: {}", JSONUtil.toJsonStr(smsRequest));
        //必填,请参考"开发准备"获取如下数据,替换为实际值
        //APP接入地址+接口访问URI
        String url = "https://rtcsms.cn-north-1.myhuaweicloud.com:10743/sms/batchSendSms/v1";
        //必填,全局号码格式(包含国家码),示例:+8615123456789,多个号码之间用英文逗号分隔
        //短信接收人号码
        String receiver = StringUtils.join(smsRequest.getPhones(), ",");

        //选填,短信状态报告接收地址,推荐使用域名,为空或者不填表示不接收状态报告
        String statusCallBack = "";

        // 选填,使用无变量模板时请赋空值
        // 单变量模板示例:模板内容为"您的验证码是${1}"时,templateParas可填写为"[\"369751\"]"
        // 双变量模板示例:模板内容为"您有${1}件快递请到${2}领取"时,templateParas可填写为"[\"3\",\"人民公园正门\"]"
        // 模板中的每个变量都必须赋值，且取值不能为空
        // 查看更多模板和变量规范:产品介绍>模板和变量规范
        // 模板变量，此处以单变量验证码短信为例，请客户自行生成6位验证码，并定义为字符串类型，以杜绝首位0丢失的问题（例如：002569变成了2569）
        String templateParas = JSONUtil.toJsonStr(smsRequest.getContents().split(","));
        //请求Body,不携带签名名称时,signature请填null
        String body = buildRequestBody(smsProperties.getSender(),
                receiver,
                smsProperties.getTemplateId(),
                templateParas,
                statusCallBack,
                smsProperties.getSignature()
        );
        log.info("huawei_http_body: {}", JSONUtil.toJsonStr(body));
        if (null == body || body.isEmpty()) {
            log.error("body is null.");
            return null;
        }

        //请求Headers中的X-WSSE参数值
        String wsseHeader = buildWsseHeader(smsProperties.getAppKey(), smsProperties.getAppSecret());
        if (null == wsseHeader || wsseHeader.isEmpty()) {
            log.error("wsse header is null.");
            return null;
        }

        Writer out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        InputStream is = null;


        try {
            trustAllHttpsCertificates();
        } catch (Exception e) {
            log.error("{}", e.getLocalizedMessage(), e);
        }

        try {
            URL realUrl = new URL(url);
            HttpsURLConnection connection = (HttpsURLConnection) realUrl.openConnection();

            connection.setHostnameVerifier((hostname, sslSession) -> Boolean.TRUE);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(true);
            //请求方法
            connection.setRequestMethod("POST");
            //请求Headers参数
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Authorization", AUTH_HEADER_VALUE);
            connection.setRequestProperty("X-WSSE", wsseHeader);

            connection.connect();
            out = new OutputStreamWriter(connection.getOutputStream());
            //发送请求Body参数
            out.write(body);
            out.flush();
            out.close();

            int status = connection.getResponseCode();
            //200
            if (200 == status) {
                is = connection.getInputStream();
            } else { //400/401
                is = connection.getErrorStream();
            }
            in = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            //打印响应消息实体
            log.info("result:{}", JSONUtil.toJsonStr(result.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != out) {
                    out.close();
                }
                if (null != is) {
                    is.close();
                }
                if (null != in) {
                    in.close();
                }
            } catch (Exception e) {
                log.error("prams: {}", JSONUtil.toJsonStr(result.toString()), e);
            }
        }
        AbstractSmsResponse smsResponse = new BaseSmsResponse().setThirdResult(result);
        log.info("sms_response: {}", JSONUtil.toJsonStr(smsResponse));
        return smsResponse;
    }


    /**
     * 构造请求Body体
     *
     * @param sender         sender
     * @param receiver       receiver
     * @param templateId     templateId
     * @param templateParas  templateParas
     * @param statusCallBack statusCallBack
     * @param signature      签名名称,使用国内短信通用模板时填写
     * @return RequestBody
     */
    private String buildRequestBody(String sender, String receiver, String templateId, String templateParas, String statusCallBack, String signature) {
        if (null == sender || null == receiver || null == templateId
                || sender.isEmpty() || receiver.isEmpty() || templateId.isEmpty()) {
            log.error("buildRequestBody(): sender, receiver or templateId is null.");
            return null;
        }
        Map<String, String> params = new HashMap<>();

        params.put("from", sender);
        params.put("to", receiver);
        params.put("templateId", templateId);
        if (null != templateParas && !templateParas.isEmpty()) {
            params.put("templateParas", templateParas);
        }
        if (null != statusCallBack && !statusCallBack.isEmpty()) {
            params.put("statusCallback", statusCallBack);
        }
        if (null != signature && !signature.isEmpty()) {
            params.put("signature", signature);
        }
        return HttpUtil.toParams(params, StandardCharsets.UTF_8);
    }

    /**
     * 构造X-WSSE参数值
     *
     * @param appKey    appKey
     * @param appSecret appSecret
     * @return X-WSSE参数值
     */
    private String buildWsseHeader(String appKey, String appSecret) {
        if (StringUtils.isBlank(appKey) || StringUtils.isBlank(appSecret)) {
            log.warn("{}", "buildWsseHeader(): appKey or appSecret is null.");
            return null;
        }
        //Created
        String time = DateUtil.format(new Date(), "yyyy-MM-dd'T'HH:mm:ss'Z'");
        //Nonce
        String nonce = IdUtil.fastSimpleUUID();

        MessageDigest md;
        byte[] passwordDigest = null;

        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update((nonce + time + appSecret).getBytes());
            passwordDigest = md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String passwordDigestBase64Str = Base64.getEncoder().encodeToString(passwordDigest);
        return String.format(WSSE_HEADER_FORMAT, appKey, passwordDigestBase64Str, nonce, time);
    }

    /**
     * trust all https certificates
     *
     * @throws NoSuchAlgorithmException no such algorithm exception
     * @throws KeyManagementException   key management exception
     */
    private void trustAllHttpsCertificates() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        log.info("{},{}", Level.SEVERE, ERROR_MESSAGE);
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        log.info("{},{}", Level.SEVERE, ERROR_MESSAGE);
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };
        SSLContext sc = SSLContext.getInstance("TLSv1.2");
        sc.init(null, trustAllCerts, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

}
