package cn.alphahub.multiple.sms.framework.impl.mengwang.entity;

import cn.alphahub.multiple.sms.domain.BaseSmsRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 梦网国际短信Request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MwSmsRequest extends BaseSmsRequest {
    /**
     * 用户账号：长度最大  6 个字符，
     * 统一大写
     */
    private String userid;
    /**
     * 加密后的用户密码：定长小写
     * 32 位字符。密码规则详见“3.1
     * 鉴权规则”
     */
    private String pwd;
    /**
     * 短信接收的手机号：多个号码
     * 使用逗号分隔，需要携带国家
     * 码。 单次最多支持 1000 个号
     * 码。号码规则详见“3.2 手机
     * 号码规则”
     */
    private String mobile;
    /**
     * 短 信 内 容 ： 最 大 支 持 1000 个
     * 字。使用 UrlEncode 编码 UTF-8
     * 的消息内容。
     */
    private String content;
    /**
     * 密码选择 MD5 加密方式时必
     * 填该参数，时间戳：24 小时制
     * 格式：MMDDHHMMSS,即月
     * 日时分秒，定长 10 位,月、日、
     * 时、分、秒每段不足 2 位时左
     * 补 0，密码选择明文方式时则
     * 不用填写
     */
    private String timestamp;
    /**
     * 业务类型：描述本条短信的内
     * 容类型，属于验证码、通知、
     * 或营销
     * <p>
     * 验证码类：填写为“1”
     * 通知类：填写为“2”
     * 营销类：填写为“3”
     * </p>
     */
    private String svrtype;
    /**
     * 填   写   本   条   短   信   的     OA     或
     * SenderID。可留空。
     */
    private String exno;
    /**
     * 用户自定义流水号：该条短信
     * 在您业务系统内的 ID，比如订
     * 单号或者短信发送记录的流
     * 水号。填写后发送状态返回值
     * 内将包含用户自定义流水号。
     * 最 大 可 支 持 64 位 的 ASCII 字符串：字母、数字、底线、减
     * 号，如不需要则不用提交此字
     * 段或填空
     */
    private String custid;
    /**
     * 自定义扩展资料：额外提供的
     * 最 大 64 个 长 度 的 ASCII 字 符
     * 串：字母、数字、底线、减号，
     * 作为自定义扩展数据，填写
     * 后，状态报告返回时将会包含
     * 这 部 分 数 据 ,如 不 需 要 则 不 用
     * 提交此字段或填空
     */
    private String exdata;
}
