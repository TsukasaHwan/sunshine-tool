package org.sunshine.core.tool.support;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.sunshine.core.tool.api.request.PageReqDto;
import org.sunshine.core.tool.util.StringPool;
import org.sunshine.core.tool.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Teamo
 * @since 2024/5/14
 */
public class ElasticCondition extends Condition {

    /**
     * 转化成elasticsearch中的分页SearchSourceBuilder
     *
     * @param pageReqDto 查询条件
     * @return SearchSourceBuilder
     */
    public static SearchSourceBuilder getPageSearchSourceBuilder(PageReqDto pageReqDto) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from((pageReqDto.getCurrent() - 1) * pageReqDto.getSize());
        searchSourceBuilder.size(pageReqDto.getSize());

        List<SortBuilder<?>> sorts = new ArrayList<>();
        String ascs = pageReqDto.getAscs();
        if (StringUtils.isNotBlank(ascs)) {
            sorts.addAll(buildSortBuilders(StringUtils.delimitedListToStringArray(ascs, StringPool.COMMA), SortOrder.ASC));
        }

        String descs = pageReqDto.getDescs();
        if (StringUtils.isNotBlank(descs)) {
            sorts.addAll(buildSortBuilders(StringUtils.delimitedListToStringArray(descs, StringPool.COMMA), SortOrder.DESC));
        }

        if (!sorts.isEmpty()) {
            searchSourceBuilder.sort(sorts);
        }
        return searchSourceBuilder;
    }

    /**
     * 构建排序条件的SortBuilder列表
     *
     * @param columns 排序字段列表
     * @param order   排序顺序
     * @return SortBuilder列表
     */
    private static List<SortBuilder<?>> buildSortBuilders(String[] columns, SortOrder order) {
        return Arrays.stream(columns)
                .map(column -> SortBuilders.fieldSort(StringUtils.cleanIdentifier(column)).order(order))
                .collect(Collectors.toList());
    }
}
