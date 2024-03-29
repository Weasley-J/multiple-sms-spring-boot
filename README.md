# multiple-sms-spring-boot-starter

[![Apache License, Version 2.0, January 2004](https://img.shields.io/github/license/mojohaus/templating-maven-plugin.svg?label=License)](http://www.apache.org/licenses/) [![Maven Central](https://img.shields.io/maven-central/v/io.github.weasley-j/multiple-sms-spring-boot-starter.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.weasley-j%22%20AND%20a:%22multiple-sms-spring-boot-starter%22)



**多短信模板&多短信供应商**， 可拔插，可拓展，支持使用者在同一个项目中指定注解以使用不同的短信模板实现，本项目已提交至`maven`中央仓库，你可以直接在项目`pom.xml`中引入使用,
找个[最新版](https://search.maven.org/search?q=multiple-sms-spring-boot-starter)引入坐标即可使用:

```xml
<dependency>
  <groupId>io.github.weasley-j</groupId>
  <artifactId>multiple-sms-spring-boot-starter</artifactId>
  <version>${multiple-email.verison}</version>
</dependency>
```

> `multiple-sms-spring-boot-starter`启动器解决的事：
>
> 如何使用一个注解`@SMS`和一个模板类`SmsTemplate`在多个**短信供应商**和**多个短信模板**之间**优雅**的切换

**requirement**

| item        | requirement                           | remark |
| ----------- | ------------------------------------- | ------ |
| SpringBoot  | 2.2.0.RELEASE  <= version <= 3.0.0-M3 |        |
| JDK         | JDK1.8 or latest                      |        |
| Environment | Spring Web Application                |        |

**project tree map**

![image-20220328232832302](https://alphahub-test-bucket.oss-cn-shanghai.aliyuncs.com/image/image-20220328232832302.png)

## 1 故事背景

我们先来看一张笔者一朋友张三所在的公司的短信模板图：

![image-20210929173756644](https://alphahub-test-bucket.oss-cn-shanghai.aliyuncs.com/image/image-20210929173756644.png)

张三的苦恼是每新开一个业务模块他都要把之前发送短信的业务代码**CV**一遍，有没有优雅的一种方式呢？接下来，我们一同交流下这种问题。

通常规模稍大点的公司的业务板块比较多，对应的则是每一种业务的短信消息通知，通常情况下，短信通知分为三类:

1. 验证码通知
2. 营销通知
3. 内容通知

从以上图片中张三所在的公司短信消息的模板已达到`9`个，传统的`XxxUtils`的`CV`编码方式会大大增加代码的重复率，基于`SpringBoot`极高的可拓展性，我们可以有更优雅的编码方式。

## 2 `controller`使用效果

```java
import cn.alphahub.multiple.sms.SmsTemplate;
import cn.alphahub.multiple.sms.annotation.EnableMultipleSms;
import cn.alphahub.multiple.sms.annotation.SMS;
import cn.alphahub.multiple.sms.enums.SmsSupplier;
import cn.alphahub.multiple.sms.demo.MyCustomSmsClientDemoImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static cn.alphahub.multiple.sms.SmsTemplate.SmsParam;

/**
 * SMS Service Test Controller
 * <p>多短信供应商、多短信模板示例Controller</p>
 *
 * @author lwj
 * @version 1.0
 * @date 2021-09-29 14:14
 */
@Slf4j
@RestController
@RequestMapping("/sms/support/demo")
@ConditionalOnBean(annotation = {EnableMultipleSmsSupport.class})
public class SmsServiceDemoController {

    @Autowired
    private SmsTemplate smsTemplate;

    /**
     * 使用默认短信模板发送短信
     * <p>默认模板可以注解{@code @SMS}，也可以不加</p>
     *
     * @param smsParam 短信参数
     * @return 发送结果
     */
    @PostMapping("/sendWithDefaultTemplate")
    public Object sendWithDefaultTemplate(@RequestBody SmsParam smsParam) {
        return smsTemplate.send(smsParam);
    }

    /**
     * 使用阿里云短信模板1发送短信
     *
     * @param smsParam 短信参数
     * @return 发送结果
     */
    @SMS(name = "促销短信模板")
    @PostMapping("/sendWithAliCloud1")
    public Object sendWithAliCloud1(@RequestBody SmsParam smsParam) {
        return smsTemplate.send(smsParam);
    }

    /**
     * 使用阿里云短信模板2发送短信
     *
     * @param smsParam 短信参数
     * @return 发送结果
     */
    @SMS(name = "秒杀短信模板")
    @PostMapping("/sendWithAliCloud2")
    public Object sendWithAliCloud2(@RequestBody SmsParam smsParam) {
        return smsTemplate.send(smsParam);
    }

    /**
     * 使用华为云发送短信
     *
     * @param smsParam 短信参数
     * @return 发送结果
     */
    @SMS(name = "验证码短信模板", supplier = SmsSupplier.HUAWEI)
    @PostMapping("/sendWithHuaweiCloud")
    public Object sendWithHuaweiCloud(@RequestBody SmsParam smsParam) {
        return smsTemplate.send(smsParam);
    }

    /**
     * 使用京东云发送短信
     *
     * @param smsParam 短信参数
     * @return 发送结果
     */
    @SMS(name = "京东云短信验证码模板", supplier = SmsSupplier.JINGDONG)
    @PostMapping("/sendWithJingdongCloud")
    public Object sendWithJingdongCloud(@RequestBody SmsParam smsParam) {
        return smsTemplate.send(smsParam);
    }

    /**
     * 使用七牛云发送短信
     *
     * @param smsParam 短信参数
     * @return 发送结果
     */
    @SMS(name = "验证码短信模板", supplier = SmsSupplier.QINIU)
    @PostMapping("/sendWithQiniuCloud")
    public Object sendWithQiniuCloud(@RequestBody SmsParam smsParam) {
        return smsTemplate.send(smsParam);
    }

    /**
     * 使用腾讯云发送短信
     *
     * @param smsParam 短信参数
     * @return 发送结果
     */
    @SMS(name = "内容短信模板", supplier = SmsSupplier.TENCENT)
    @PostMapping("/sendWithTencentCloud")
    public Object sendWithTencentCloud(@RequestBody SmsParam smsParam) {
        return smsTemplate.send(smsParam);
    }

    /**
     * 自定义短信实现发送短信
     *
     * @param smsParam 短信参数
     * @return 发送结果
     */
    @SMS(invokeClass = MyCustomSmsClientDemoImpl.class)
    @PostMapping("/sendCustomSmsClient")
    public Object sendWithCustomSmsClient(@RequestBody SmsParam smsParam) {
        return smsTemplate.send(smsParam);
    }
}

```

说明：

- 上面上`8`个发送短信消息的方法都是调用`SmsTemplate`类的`send(SmsParam smsParam)`方法
- 通过注解` @SMS`动态切换短信模板
- 第`8`个方法支持自定义短信实现，通过注解`@SMS(invokeClass = MyCustomSmsClientDemoImpl.class)`指定自定义短信实现类
- 通过注解`@SMS`在**同一短信提供商的多个短信模板**、**不同短信提供上商多个短信模板**之间自由切换短信模板
- 发送短信的方法只有一个`send()`方法
- 支持的短信提供商有`6`家：阿里云、腾讯云、华为云、京东云、七牛云、梦网国际

## 3 细节分享

### 3.1 配置文件

> 提示：同一短信供应商下面的多个模板的模板名称（`template-name`）不能重复。

以下是**阿里云**、**腾讯云**、**华为云**、**京东云**、**七牛云**、**梦网国际**
等短信模板的配置示例，包含模板情景：同一短信供应商多个短信模板、多个短信供应商多个模板, [默认配置示例](https://github.com/Weasley-J/multiple-sms-spring-boot/blob/master/multiple-sms-spring-boot-starter-test/src/main/resources/application-demo.yml)

```yaml
spring:
  sms:
    conf:
      thread:
        core-pool-size: 10
        maximum-pool-size: 100
        keep-alive-time: 10
        time-unit: seconds
        capacity: 1000
      sms-properties:
        default-template:
          sms-supplier: mengwang
          template-name: 梦网国际短信平台配置2
        multiple-templates:
          ali:
            [ { template-name: '促销短信模板1', sms-supplier: ali, access-key: 'accessKey1', secret-key: 'secretKey1', region-id: 'regionId1', sign-name: 'signName1', template-code: 'describe' },
              { template-name: '秒杀短信模板2', sms-supplier: ali, access-key: 'accessKey2', secret-key: 'secretKey2', region-id: 'regionId2', sign-name: 'signName2', template-code: 'username' },
              { template-name: '阿里短信模板3', sms-supplier: ali, access-key: 'accessKey3', secret-key: 'secretKey3', region-id: 'regionId3', sign-name: 'signName3', template-code: 'verifCode' },
            ]
          huawei:
            [ { template-name: '华为SMS模板1', sms-supplier: huawei, sender: '国内短信签名通道号或国际/港澳台短信通道号1', app-key: 'app-key1', app-secret: 'app-secret1', template-id: '模板ID1', signature: '签名名称,条件必填1' },
              { template-name: '华为SMS模板2', sms-supplier: huawei, sender: '国内短信签名通道号或国际/港澳台短信通道号2', app-key: 'app-key2', app-secret: 'app-secret2', template-id: '模板ID2', signature: '签名名称,条件必填2' },
            ]
          jingdong:
            [ { template-name: '京东短信模板1', sms-supplier: jingdong, access-key-id: '短信accessKeyId1', secret-access-key: '短信secretAccessKey1', sign-id: '短信签名id1', template-id: '短信模板ID1' },
              { template-name: '京东短信模板2', sms-supplier: jingdong, access-key-id: '短信accessKeyId2', secret-access-key: '短信secretAccessKey2', sign-id: '短信签名id2', template-id: '短信模板ID2' },
              { template-name: '京东短信模板3', sms-supplier: jingdong, access-key-id: '短信accessKeyId3', secret-access-key: '短信secretAccessKey3', sign-id: '短信签名id3', template-id: '短信模板ID3' },
              { template-name: '京东短信模板4', sms-supplier: jingdong, access-key-id: '短信accessKeyId4', secret-access-key: '短信secretAccessKey4', sign-id: '短信签名id4', template-id: '短信模板ID4' },
            ]
          mengwang:
            [ { template-name: '梦网国际短信平台配置1', sms-supplier: mengwang,
                sign-contents: { chinese: '【XX健康】', english: '【Xx Health】' },
                sms-i18n-mapping: { chinese: { userid: 'chinese_userid1', pwd: 'chinese_pwd1', server-url: 'http://chinese_ip:port' },
                                    english: { userid: 'english_userid1', pwd: 'english_pwd1', server-url: 'http://english_ip:port' } } },
              { template-name: '梦网国际短信平台配置2', sms-supplier: mengwang,
                sign-contents: { chinese: '【XX科技】', english: '【Xx Technology】' },
                sms-i18n-mapping: { chinese: { userid: 'chinese_userid2', pwd: 'chinese_pwd2', server-url: 'http://chinese_ip:port' },
                                    english: { userid: 'english_userid2', pwd: 'english_pwd2', server-url: 'http://english_ip:port' } } },
            ]
          qiniu:
            [ { template-name: '七牛短信模板1', sms-supplier: qiniu, access-key: 'ak1', secret-key: 'sk1', template-id: 't1', parameters: 'verifyCode' },
              { template-name: '七牛短信模板2', sms-supplier: qiniu, access-key: 'ak2', secret-key: 'sk2', template-id: 't2', parameters: 'username' },
            ]
          tencent:
            [ { template-name: '腾讯短信模板1', sms-supplier: tencent, secret-id: '短信secretId1', secret-key: '短信secretKey1', sms-sdk-app-id: '短信SdkAppId1', template-id: '已审核通过的模板ID1', region: 'ap-nanjing', sign-name: '短信签名1' },
              { template-name: '腾讯短信模板2', sms-supplier: tencent, secret-id: '短信secretId2', secret-key: '短信secretKey2', sms-sdk-app-id: '短信SdkAppId2', template-id: '已审核通过的模板ID2', region: 'ap-nanjing', sign-name: '短信签名2' },
            ]

```

### 3.2 注解说明

#### 3.2.1 业务注解`@SMS`

```java
import cn.alphahub.multiple.sms.framework.SmsClient;
import cn.alphahub.multiple.sms.enums.SmsSupplier;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static cn.alphahub.multiple.sms.framework.SmsClient.DefaultSmsClientPlaceholder;

/**
 * 多模板短信注解
 *
 * @author lwj
 * @version 1.0
 * @apiNote 基于此注解解析不同的短信模板, 使用注解{@code @SMS}指定以：短信供应商、短信模板发送短信
 * @date 2021-09-24
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SMS {

    /**
     * 默认模板名称
     */
    String DEFAULT_TEMPLATE = "DEFAULT";

    /**
     * 短信模板名称，必须和{@code yml}配置文件里面的{@code template-name}一致，默认：DEFAULT
     *
     * @return 短信模板名称
     */
    String name() default DEFAULT_TEMPLATE;

    /**
     * 短信供应商，默认短信供应商: 阿里云
     *
     * @apiNote 如果需要拓展其他短信供应商，见枚举{@code SmsSupplier}
     * @see SmsSupplier
     */
    SmsSupplier supplier() default SmsSupplier.ALI;

    /**
     * 自定义实现发送发送短信的实现类，必须显现或继承{@code SmsClient}接口
     *
     * @return 发送短信的实现类class
     * @apiNote 当指定自定义短信发送类时将优先采用自定义短信发送实现完成发送短信的逻辑
     */
    Class<? extends SmsClient> invokeClass() default DefaultSmsClientPlaceholder.class;
}
```

`@SMS`可作用于**类**、**方法**上, 支持**自定义发送短信逻辑**实现，通过`invokeClass`指定自定义实现类。

#### 3.2.2  自动配置注解`@EnableMultipleSmsSupport`

```java
import cn.alphahub.multiple.sms.SmsTemplate;
import cn.alphahub.multiple.sms.aspect.SmsAspect;
import cn.alphahub.multiple.sms.config.SmsConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enable Multiple SMS Support
 *
 * @author lwj
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({SmsConfig.class, SmsAspect.class, SmsTemplate.class})
public @interface EnableMultipleSmsSupport {

}
```

注解`@EnableMultipleSmsSupport`作用于**类**上，用于需要发送短信的**web应用**启用短信支持，并自动装配配置文件，只需要在发送短信的服务的`yml`配置文件中配置`3.1`的短信`AK`、`SK`
等数据即可，

### 3.3 短信提供商

目前只整合一下**5**种短信供应商的短信实现，读者可以自行增加其他短信产商的发送短信的实现

```java
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * 短信供应商类型枚举枚举
 *
 * @author lwj
 * @version 1.0
 * @date 2021-09-24
 */
@Getter
@AllArgsConstructor
public enum SmsSupplier {
    /**
     * 阿里云
     */
    ALI("ALI_CLOUD", "阿里云"),
    /**
     * 华为云
     */
    HUAWEI("HUAWEI_CLOUD", "华为云"),
    /**
     * 京东云
     */
    JINGDONG("JINGDONG_CLOUD", "京东云"),
    /**
     * 七牛云
     */
    QINIU("QINIU_CLOUD", "七牛云"),
    /**
     * 腾讯云
     */
    TENCENT("TENCENT_CLOUD", "腾讯云"),
    ;

    /**
     * 供应商名称编码
     */
    private final String code;
    /**
     * 供应商名称
     */
    private final String name;

    /**
     * 获取短信供应商枚举
     *
     * @param code 供应商名称编码
     * @return 短信供应商枚举
     */
    public static SmsSupplier getEnum(String code) {
        return Stream.of(SmsSupplier.values())
                .filter(smsSupplier -> smsSupplier.getCode().equals(code))
                .findFirst().orElse(null);
    }
}
```

### 3.4 自定义短信发送实现

自定义短信发送实现需实现`cn.alphahub.multiple.sms.framework.SmsClient`接口并覆写`send`方法。

![image-20211008174003086](https://alphahub-test-bucket.oss-cn-shanghai.aliyuncs.com/image/image-20211008174003086.png)

### 3.5 自动装配使用说明

**步骤：**

1. 新建一个`springboot`的`web`项目, `pom.xml`中引入`multiple-sms-spring-boot-starter`的`maven`坐标：

```xml
<!-- multiple-sms-spring-boot-starter -->
<dependency>
    <groupId>io.github.weasley-j</groupId>
    <artifactId>multiple-sms-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

2. 在该服务的启动类上标注注解`@EnableMultipleSmsSupport`启用短信支持

这是从我另一个[项目](https://github.com/Weasley-J/lejing-mall/tree/main/lejing-common/lejing-common-sms-support)
里面拆离的基础组件，注解是使用本项目的的注解是: `@EnableMultipleSmsSupport`

![](https://alphahub-test-bucket.oss-cn-shanghai.aliyuncs.com/image/image-20211008174758732.png)

3. 在`Service`或`Controller`层注入`SmsTemplate`，并在业务方法或业务类上标注注解`@SMS`
   发送短信，详见源码`cn.alphahub.multiple.sms.test.controller.SmsDemoController`

> 推荐注解`@SMS`作用于方法，方法级别使用。

![image-20211008175303335](https://alphahub-test-bucket.oss-cn-shanghai.aliyuncs.com/image/image-20211008175303335.png)

## 4 关于注解`@SMS`作用在类和方法的优先级问题

- 当注解`@SMS`同时作用类，和方法上时，方法上注解`@SMS`的优先级高于类上`@SMS`注解的优先级
- 当注解`@SMS`作用方法上时，该方法短信客户端的为注解`@SMS`指定的短信客户端
- 当注解`@SMS`作用类上时，该类所有短信模板方法发送短信的客户端都以注解`@SMS`指定为准客户端

## 5 关于`Spring IOC`容器中的同一个`Bean`实例里面被`@SMS`注解标注的方法间嵌套调用的问题

### 5.1 我们先看一个`AOP`嵌套调用的示例类：`SMSAnnotateWithClassAndMethod`

```java
import cn.alphahub.multiple.sms.SmsTemplate;
import cn.alphahub.multiple.sms.annotation.SMS;
import cn.alphahub.multiple.sms.demo.MyCustomSmsClientDemoImpl;
import cn.alphahub.multiple.sms.enums.SmsSupplier;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;

import static cn.alphahub.multiple.sms.SmsTemplate.SmsParam;

/**
 * 测试{@code @SMS}同时标注在方法类、类方法上面
 *
 * @author lwj
 * @version 1.0
 * @date 2021-10-02 18:18
 */
@SMS
@Service
public class SMSAnnotateWithClassAndMethod {

    @Autowired
    private SmsTemplate smsTemplate;

    /**
     * 使用腾讯云发送短信
     *
     * @param smsParam 短信参数
     * @return 发送结果
     */
    @SMS(name = "腾讯云内容短信模板", supplier = SmsSupplier.TENCENT)
    public Object sendWithTencentCloud(@RequestBody SmsParam smsParam) {
        return smsTemplate.send(smsParam);
    }

    /**
     * 自定义短信实现发送短信
     *
     * @param smsParam 短信参数
     * @return 发送结果
     */
    @SMS(invokeClass = MyCustomSmsClientDemoImpl.class)
    public Object sendWithCustomSmsClient(@RequestBody SmsParam smsParam) {
        return smsTemplate.send(smsParam);
    }

    /**
     * 嵌套调用，使用Spring AOP代理当前对象
     *
     * @param smsParam 短信参数
     */
    public void nested(@RequestBody SmsParam smsParam) {
        ((SMSAnnotateWithClassAndMethod) AopContext.currentProxy()).sendWithTencentCloud(smsParam);
        ((SMSAnnotateWithClassAndMethod) AopContext.currentProxy()).sendWithCustomSmsClient(smsParam);
    }
}
```

> **以上多模板短信实例代码的应用场景：**
>
> 当用户在乐璟商城下单成功后，平台方应当发送消息通知给**用户**和**商家**，告知用户物流情况，告知商家本单交易情况。
>
> 假设`sendWithTencentCloud()`是告诉用户的短信模板消息，`sendWithCustomSmsClient()`是告诉商家的短信模板消息，为了完成以上业务场景，我们写了`nested()`方法来完成这个业务场景。

### 5.2 结论分享

- `nested()`方法一共调用两个本类里面被注解`@SMS`标注的方法完成我们的业务需求：“当用户在乐璟商城下单成功后，平台方应当发送消息通知给**用户**和**商家**，告知用户物流情况，告知商家本单交易情况”，
- 基于`CGLib`的动态代理调用本类方法完成业务时，使用的是增强代理类去执行业务方法，并不是本类`this`自身，因此我们需要从线程变量中获取当前`AOP`的真实代理对象，让真实代理对象调用本类（`this`
  ）的方法执行业务，执行前后`AOP`能根据我们设定好的代理规则解析正确的业务参数完成业务需求。
