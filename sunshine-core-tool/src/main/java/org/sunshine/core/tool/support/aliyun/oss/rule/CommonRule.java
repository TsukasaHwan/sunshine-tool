package org.sunshine.core.tool.support.aliyun.oss.rule;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.io.file.FileNameUtil;
import org.sunshine.core.tool.util.DateUtils;
import org.sunshine.core.tool.util.StringPool;
import org.sunshine.core.tool.util.StringUtils;

/**
 * @author Teamo
 * @since 2021/11/02
 */
public class CommonRule implements OssRule {
    @Override
    public String bucketName(String bucketName) {
        return bucketName;
    }

    @Override
    public String fileName(String originalFilename) {
        return "upload" + StringPool.SLASH + DateUtils.date().toString(DatePattern.PURE_DATE_FORMAT) + StringPool.SLASH + StringUtils.randomUUID() + StringPool.DOT + FileNameUtil.extName(originalFilename);
    }
}
