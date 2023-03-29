package org.sunshine.core.tool.util;

import java.util.Collections;
import java.util.List;

/**
 * @author Teamo
 * @since 2020/4/10
 */
public class PageUtils {
    public static <T> List<T> pageList(int pageNum, int pageSize, List<T> list) {
        if (list == null || list.size() == 0) {
            return list;
        }
        // 记录总数
        final int count = list.size();
        // 页数
        int pageCount;
        if (count % pageSize == 0) {
            pageCount = count / pageSize;
        } else {
            pageCount = count / pageSize + 1;
        }
        // 开始索引
        int fromIndex = (pageNum - 1) * pageSize;
        // 结束索引
        int toIndex = count;
        if (pageNum != pageCount) {
            toIndex = fromIndex + pageSize;
        }
        try {
            return list.subList(fromIndex, toIndex);
        } catch (IndexOutOfBoundsException e) {
            return Collections.emptyList();
        }
    }

    /**
     * 返回总页数
     *
     * @param obj      列表
     * @param pageSize 页大小
     * @return 总页数
     */
    public static int totalPage(List<?> obj, int pageSize) {
        int totalCount = obj.size();
        if (pageSize == 0) {
            return 0;
        }
        return totalCount % pageSize == 0 ? (totalCount / pageSize) : (totalCount / pageSize + 1);
    }
}
