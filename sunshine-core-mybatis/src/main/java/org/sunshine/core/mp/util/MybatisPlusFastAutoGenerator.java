package org.sunshine.core.mp.util;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.builder.Entity;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;
import org.sunshine.core.tool.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * @author Teamo
 * @since 2022/01/07
 */
public class MybatisPlusFastAutoGenerator {

    public static void generate(String url, String username, String password) {
        String module = scanner("模块名称：");
        String projectDir = System.getProperty("user.dir");
        FastAutoGenerator.create(url, username, password)
                // 全局配置
                .globalConfig((scanner, builder) -> builder.author(scanner.apply("作者："))
                        .outputDir(projectDir + "/" + module + "/src/main/java")
                )
                // 包配置
                .packageConfig((scanner, builder) -> builder.parent(scanner.apply("全限定名："))
                        .pathInfo(Collections.singletonMap(OutputFile.xml, projectDir + "/" + module + "/src/main/resources/mapper/"))
                )
                // 模板引擎配置
                .templateEngine(new FreemarkerTemplateEngine())
                // 策略配置
                .strategyConfig((scanner, builder) -> {
                    Entity.Builder entityBuilder = builder
                            .addTablePrefix(scanner.apply("表前缀："))
                            .addInclude(getTables(scanner.apply("请输入表名，多个以英文逗号分隔，所有输入 all：")))
                            .entityBuilder()
                            .enableFileOverride()
                            .enableLombok()
                            .disableSerialVersionUID()
                            .addTableFills(new Column(scanner.apply("创建时间字段名："), FieldFill.INSERT))
                            .addTableFills(new Column(scanner.apply("修改时间字段名："), FieldFill.INSERT_UPDATE));

                    String logicDeleteColumnName = scanner.apply("逻辑删除字段名：");
                    entityBuilder.addTableFills(new Column(logicDeleteColumnName, FieldFill.INSERT));

                    entityBuilder.logicDeleteColumnName(logicDeleteColumnName)
                            .controllerBuilder()
                            .disable()
                            .enableFileOverride()
                            .serviceBuilder()
                            .formatServiceFileName("I%sService")
                            .formatServiceImplFileName("I%sServiceImpl")
                            .enableFileOverride()
                            .mapperBuilder()
                            .enableBaseResultMap()
                            .enableBaseColumnList()
                            .formatMapperFileName("%sMapper")
                            .formatXmlFileName("%sMapper")
                            .enableFileOverride()
                            .build();
                })
                .execute();
    }

    private static List<String> getTables(String tables) {
        return "all".equals(tables) ? Collections.emptyList() : Arrays.asList(tables.split(","));
    }

    private static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(tip);
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotBlank(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }
}
