package org.sunshine.core.tool.support;

import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchRequest;
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
     * 转化成elasticsearch中的分页SearchRequest
     *
     * @param pageReqDto 查询条件
     * @return SearchSourceBuilder
     */
    public static SearchRequest getPageSearchRequest(PageReqDto pageReqDto) {
        return SearchRequest.of(builder -> {
            builder.from((pageReqDto.getCurrent() - 1) * pageReqDto.getSize());
            builder.size(pageReqDto.getSize());

            List<SortOptions> sorts = new ArrayList<>();
            String ascs = pageReqDto.getAscs();
            if (StringUtils.isNotBlank(ascs)) {
                sorts.addAll(buildSortOptions(ascs, SortOrder.Asc));
            }

            String descs = pageReqDto.getDescs();
            if (StringUtils.isNotBlank(descs)) {
                sorts.addAll(buildSortOptions(descs, SortOrder.Desc));
            }

            if (!sorts.isEmpty()) {
                builder.sort(sorts);
            }
            return builder;
        });
    }

    /**
     * 构建排序条件的SortOptions列表
     *
     * @param fields 排序字段列表
     * @param order  排序顺序
     * @return SortOptions列表
     */
    private static List<SortOptions> buildSortOptions(String fields, SortOrder order) {
        String[] columns = StringUtils.delimitedListToStringArray(fields, StringPool.COMMA);
        return Arrays.stream(columns)
                .map(column -> SortOptions.of(sortBuilder -> {
                    String field = StringUtils.cleanIdentifier(column);
                    FieldSort fieldSort = FieldSort.of(fieldSortBuilder -> fieldSortBuilder.field(field).order(order));
                    sortBuilder.field(fieldSort);
                    return sortBuilder;
                }))
                .collect(Collectors.toList());
    }
}
