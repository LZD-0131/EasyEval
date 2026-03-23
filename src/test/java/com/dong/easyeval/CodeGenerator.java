package com.dong.easyeval;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;

import java.util.Collections;

public class CodeGenerator {

    public static void main(String[] args) {
        // 1. 数据源配置
        // 注意：这里读取的是你 application.properties 里的配置
        String url = "jdbc:mysql://localhost:3306/eaasyeval?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "123456";

        FastAutoGenerator.create(url, username, password)
                // 2. 全局配置
                .globalConfig(builder -> {
                    builder.author("dong") // 设置作者
                            .outputDir(System.getProperty("user.dir") + "/src/main/java") // 指定输出目录
                            .disableOpenDir(); // 生成后不自动打开文件夹
                })
                // 3. 包配置
                .packageConfig(builder -> {
                    builder.parent("com.dong.easyeval") // 设置父包名
                            // 设置 mapper.xml 生成路径
                            .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") + "/src/main/resources/mapper"));
                })
                // 4. 策略配置
                .strategyConfig(builder -> {
                    builder.addInclude("t_user","t_payment_history","t_coupon_type","t_coupon_redemption_detail","t_coupon","t_correction_request","t_correction_count") // 设置需要生成的表名 (如果你有多个表，用逗号隔开)
                            // .addTablePrefix("t_") // 设置过滤表前缀，比如 t_user 生成的实体类就是 User (如果你想保持 TUser，可以注释掉这行)
                            
                            // 实体类策略配置
                            .entityBuilder()
                            .enableLombok() // 开启 lombok 模型
                            .enableTableFieldAnnotation() // 开启生成实体时生成字段注解
                            .enableFileOverride() // 覆盖已生成文件
                            .formatFileName("%s") // 保持实体类原名（如 TUser）
                            .idType(IdType.AUTO)
                            
                            // 控制器策略配置
                            .controllerBuilder()
                            .enableFileOverride()
                            .enableRestStyle() // 开启生成 @RestController 控制器
                            
                            // Mapper 策略配置
                            .mapperBuilder()
                            .enableFileOverride()
                            .enableMapperAnnotation() // 开启 @Mapper 注解
                            
                            // Service 策略配置
                            .serviceBuilder()
                            .enableFileOverride();
                })
                // 5. 模板引擎配置 (默认是 Velocity)
                .templateEngine(new VelocityTemplateEngine())
                .execute();
    }
}

