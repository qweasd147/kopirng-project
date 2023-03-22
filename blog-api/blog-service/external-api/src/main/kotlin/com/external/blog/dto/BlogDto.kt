package com.external.blog.dto

import java.time.LocalDateTime


open class BlogSearchRequestDto(

    open val query: String = "",
    open val sort: BlogSearchSortType = BlogSearchSortType.accuracy,
    open val page: Int = 1,
    open val size: Int = 10,
)

enum class BlogSearchSortType(
    val desc: String,
) {
    accuracy("정확도순"),
    recency("최신순"),
    ;
}

class BlogResponseDto(
    var meta: MetaResponse,
    var documents: List<DocumentsResponse>,
)

class MetaResponse(
    var totalCount: Int = 0,   //전체 문서 수(노출 가능)
    var isEnd: Boolean = true, //현재 페이지가 마지막 페이지인지 여부, 값이 false면 page를 증가시켜 다음 페이지를 요청할 수 있음
)

class DocumentsResponse(
    var title: String = "",
    var contents: String = "",
    var url: String,
    var blogname: String,
    var thumbnail: String,          //kakao는 지원, naver는 미지원
    var datetime: LocalDateTime,
)