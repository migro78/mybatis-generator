package com.migro.mybatis.convert;

import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.ITypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.PropertyInfo;

/**
 * <p>
 * 自定义类型转换
 * </p>
 *
 * @author migro
 * @since 2020/6/11 17:19
 */
public class MyPostgresTypeConvert implements ITypeConvert {

    public MyPostgresTypeConvert() {

    }

    @Override
    public PropertyInfo processTypeConvert(GlobalConfig globalConfig, String fieldType) {
        String t = fieldType.toLowerCase();
        if (!t.contains("char") && !t.contains("text")) {
            if (t.contains("bigint")) {
                return DbColumnType.LONG;
            } else if (t.contains("int")) {
                return DbColumnType.INTEGER;
            } else if (!t.contains("date") && !t.contains("time") && !t.contains("year")) {
                if (t.contains("text")) {
                    return DbColumnType.STRING;
                } else if (t.contains("bit")) {
                    return DbColumnType.BOOLEAN;
                } else if (t.contains("decimal")) {
                    return DbColumnType.BIG_DECIMAL;
                } else if (t.contains("numeric")) {
                    return DbColumnType.DOUBLE;
                } else if (t.contains("clob")) {
                    return DbColumnType.CLOB;
                } else if (t.contains("blob")) {
                    return DbColumnType.BYTE_ARRAY;
                } else if (t.contains("float")) {
                    return DbColumnType.FLOAT;
                } else if (t.contains("double")) {
                    return DbColumnType.DOUBLE;
                } else if (!t.contains("json") && !t.contains("enum")) {
                    return t.contains("boolean") ? DbColumnType.BOOLEAN : DbColumnType.STRING;
                } else {
                    return DbColumnType.STRING;
                }
            } else {
                return DbColumnType.DATE;
            }
        } else {
            return DbColumnType.STRING;
        }
    }
}
