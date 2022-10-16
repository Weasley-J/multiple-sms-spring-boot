package cn.alphahub.multiple.sms.framework.impl.mengwang.entity;

import cn.alphahub.multiple.sms.domain.BaseSmsResponse;
import cn.hutool.core.net.URLDecoder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * 梦网国际短信Response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MwSmsResponse extends BaseSmsResponse {
    /**
     * 是否成功
     */
    private Boolean success;
    /**
     * 短信发送请求处理结果：0：成功, 非0：失败
     */
    private int result;
    /**
     * 平台流水号：非0，64位整型，对应Java和C#的long，不可用int解析。result非0时，msgid为0
     */
    private Long msgid;
    /**
     * 用户自定义流水号：默认与请求报文中的custid保持一致，若请求报文中没有custid参数或值为空，则返回由梦网生成的代表本批短信的唯一编号result非0时，custid为空
     */
    private String custid;
    /**
     * 应答结果描述，当result非0时，为错误描述编码方式：urlencode(UTF-8)
     */
    private String desc;
    /**
     * 消息信息对象数组
     */
    private List<MessagesItem> messages;

    public String getDesc() {
        return URLDecoder.decode(desc, StandardCharsets.UTF_8);
    }

    public Boolean getSuccess() {
        return Objects.equals(result, 0);
    }

    /**
     * 消息信息对象数组
     */
    @Data
    public static class MessagesItem {
        /**
         * 短信接收的手机号：携带国家码。
         * 号码规则详见“3.2 手机号码规则”
         */
        private String mobile;
        /**
         * 短信流水号：非 0 表示下发成功，
         * 64 位无符号整型十进制数。特别注
         * 意：msgid 为 0，表示该消息下发失
         * 败。
         */
        private String msgid;
    }
}
