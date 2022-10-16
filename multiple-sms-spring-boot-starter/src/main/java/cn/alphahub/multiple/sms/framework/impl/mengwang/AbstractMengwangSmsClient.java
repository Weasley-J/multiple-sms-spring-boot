package cn.alphahub.multiple.sms.framework.impl.mengwang;

import cn.alphahub.multiple.sms.framework.SmsClient;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotBlank;

/**
 * Sms Service
 *
 * @author weasley
 * @version 1.0.0
 */
public abstract class AbstractMengwangSmsClient implements SmsClient {
    /**
     * 2.1    单条发送接口  send_single<br>
     * http://ip:port/sms/v2/std/send_single
     */
    public static final String SEND_SINGLE_URI = "/sms/v2/std/send_single";
    /**
     * 2.4 相同内容群发接口  send_batch<br>
     * http://ip:port/sms/v2/std/send_batch
     */
    public static final String SEND_BATCH_URI = "/sms/v2/std/send_batch";
    protected static final Logger log = LoggerFactory.getLogger(AbstractMengwangSmsClient.class);

    /**
     * 加密后的用户密码：定长小写32 位字符。密码规则详见“3.1鉴权规则”<br>
     * String preMd5 = userid.toUpperCase() + "00000000" + pwd + timestamp;
     * <p>
     * 示例：
     * 密码明文模式：111111
     * 密码加密模式：
     * 账号：J10003
     * 密码：111111
     * 固定字符串：00000000
     * 时间戳：0803192020
     * MD5加密之前的对应字符串：
     * J10003000000001111110803192020
     * MD5加密之后的密码字符串：
     * 26dad7f364507df18f3841cc9c4ff94d
     *
     * @param userid    用户账号：长度最大  6 个字符， 统一大写
     * @param pwd       用户密码明文
     * @param timestamp 时间戳，格式: MMddHHmmss
     * @return 加密后的用户密码
     */
    public static String getPwdMd5Hex(@NotBlank String userid, @NotBlank String pwd, @NotBlank String timestamp) {
        log.info("{} {} {}", userid, pwd, timestamp);
        String preMd5 = userid.toUpperCase() + "00000000" + pwd + timestamp;
        return DigestUtils.md5Hex(preMd5);
    }
}
