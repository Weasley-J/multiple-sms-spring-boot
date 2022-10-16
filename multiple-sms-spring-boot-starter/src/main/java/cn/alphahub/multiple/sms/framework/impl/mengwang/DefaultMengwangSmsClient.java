package cn.alphahub.multiple.sms.framework.impl.mengwang;

import cn.alphahub.multiple.sms.annotation.EnableMultipleSms;
import cn.alphahub.multiple.sms.config.entity.MengwangSmsProperties;
import cn.alphahub.multiple.sms.domain.AbstractSmsResponse;
import cn.alphahub.multiple.sms.exception.SmsException;
import cn.alphahub.multiple.sms.framework.impl.mengwang.entity.MwSmsRequest;
import cn.alphahub.multiple.sms.framework.impl.mengwang.entity.MwSmsResponse;
import cn.alphahub.multiple.sms.framework.impl.mengwang.entity.MwSmsWrapper;
import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 梦网平台短信实现
 * <p>
 *
 * </p>
 *
 * @author weasley
 * @version 1.0.0
 */
@Slf4j
@Component
@Validated
@ConditionalOnBean(annotation = {EnableMultipleSms.class})
public class DefaultMengwangSmsClient {
    /**
     * sms client
     */
    protected static final AbstractMengwangSmsClient CLIENT = null;

    /**
     * 执行发送短信
     *
     * @param wrapper sms aggregation wrapper
     * @return BaseSmsResponse
     */
    public AbstractSmsResponse doSend(@Valid MwSmsWrapper wrapper) {
        log.info("默认短信聚合入参：{} ", JSONUtil.toJsonStr(wrapper));
        if (ObjectUtils.isNotEmpty(wrapper.getRequest().getPhones())) {
            for (String phone : wrapper.getRequest().getPhones()) {
                if (StringUtils.isBlank(phone)) throw new SmsException("提交的手机号里面不能含有空白字符串！");
            }
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddHHmmss"));
        //推断元数据
        MengwangSmsProperties.MwSmsProperties smsProperties = wrapper.getMengwangSmsProperties().deduceServerProperties(wrapper.getRequest().getSmsI18n());

        MwSmsRequest mwRequest = new MwSmsRequest();
        mwRequest.setSmsI18n(null);
        /* parameter start*/
        mwRequest.setUserid(smsProperties.getUserid());
        mwRequest.setPwd(CLIENT.getPwdMd5Hex(smsProperties.getUserid(), smsProperties.getPwd(), timestamp));
        mwRequest.setMobile(StringUtils.join(wrapper.getRequest(), ","));
        mwRequest.setContent(URLEncodeUtil.encode(wrapper.getSignContent() + wrapper.getRequest().getContents()));
        mwRequest.setTimestamp(timestamp);
        mwRequest.setSvrtype(null);
        mwRequest.setExno(null);
        mwRequest.setCustid(null);
        mwRequest.setExdata(null);
        /* parameter end*/

        String url = smsProperties.getServerUrl() + CLIENT.SEND_BATCH_URI;
        String body = JSONUtil.toJsonStr(mwRequest);
        log.info("请求URL：{} 发送http原始json数据：{}", url, body);

        HttpResponse httpResponse = HttpUtil.createPost(url).body(body, ContentType.JSON.getValue()).timeout(5 * 1000).execute();

        MwSmsResponse response = new MwSmsResponse();
        response.setThirdResult(httpResponse.body());
        if (httpResponse.isOk()) {
            response = JSONUtil.toBean(httpResponse.body(), MwSmsResponse.class);
        }

        log.info("发送http响应结果: {}\n{}", JSONUtil.toJsonStr(httpResponse.body()), JSONUtil.toJsonStr(response));

        //失败直接抛异常易于排查原因
        if (response.getSuccess().equals(false)) throw new SmsException(JSONUtil.toJsonStr(response));

        return response;
    }
}
