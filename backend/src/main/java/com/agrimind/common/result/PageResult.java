package com.agrimind.common.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PageResult<T> {

    private List<T> records;

    private Long total;

    private Long pageNum;

    private Long pageSize;

    private Long pages;

    public static <T> PageResult<T> from(IPage<T> page) {
        return new PageResult<>(
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize(),
                page.getPages()
        );
    }
}
