package cn.alphahub.multiple.sms.config;

import cn.alphahub.multiple.sms.config.entity.AliSmsProperties;
import cn.alphahub.multiple.sms.config.entity.HuaweiSmsProperties;
import cn.alphahub.multiple.sms.config.entity.JingdongSmsProperties;
import cn.alphahub.multiple.sms.config.entity.MengwangSmsProperties;
import cn.alphahub.multiple.sms.config.entity.QiniuSmsProperties;
import cn.alphahub.multiple.sms.config.entity.TencentSmsProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 多个短信模板配置属性
 *
 * @author weasley
 * @version 1.0.0
 * @see cn.alphahub.multiple.sms.enums.SmsSupplier
 */
@Data
@ConfigurationProperties(prefix = "spring.sms.conf.sms-properties.multiple-templates")
public class MultipleSmsTemplatesProperties {
    /**
     * 阿里短信属性
     */
    private List<AliSmsProperties> ali;
    /**
     * 华为短信属性
     */
    private List<HuaweiSmsProperties> huawei;
    /**
     * 京东短信属性
     */
    private List<JingdongSmsProperties> jingdong;
    /**
     * 梦网短信属性
     */
    private List<MengwangSmsProperties> mengwang;
    /**
     * 七牛短信属性
     */
    private List<QiniuSmsProperties> qiniu;
    /**
     * 腾讯短信属性
     */
    private List<TencentSmsProperties> tencent;
}
