package com.blog.dto

import com.external.blog.dto.BlogSearchRequestDto
import com.external.blog.dto.BlogSearchSortType
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Range

class SearchRequest(
    @field:NotBlank(message = "검색어를 입력 해주세요")
    override val query: String = "",
    sort: BlogSearchSortType = BlogSearchSortType.accuracy,

    @field:Range(min = 1, max = 50, message = "page 값은 1~50까지 가능합니다.")
    override val page: Int = 1,
    @field:Range(min = 1, max = 50, message = "size 값은 1~50까지 가능합니다.")
    override val size: Int = 10,
) : BlogSearchRequestDto(
    query = query, sort = sort, page = page, size = size
)