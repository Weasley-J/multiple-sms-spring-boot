package cn.alphahub.multiple.sms.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 基础短信请求入参
 *
 * @author weasley
 * @version 1.0.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BaseSmsRequest extends AbstractSmsRequest{
    //手机号：一个、多个
    //短信内容
    //是否国际化
    //国际化类型: 国内、国外
}
