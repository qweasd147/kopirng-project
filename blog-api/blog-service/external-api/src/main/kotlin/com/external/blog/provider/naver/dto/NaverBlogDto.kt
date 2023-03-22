package com.external.blog.provider.naver.dto

import com.external.blog.dto.*
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal class NaverBlogRequestDto(
    val query: String = "",
    val sort: NaverBlogSearchSortType = NaverBlogSearchSortType.sim,
    val display: Int = 10, //한번에 표시할 검색 결과 개수
    val start: Int = 1,     //검색 시작 위치 1~1000
) {

    companion object {

        fun of(requestDto: BlogSearchRequestDto): NaverBlogRequestDto {

            //1 -> 1
            //2 -> 20 + 1
            //3 -> 30 + 1
            val start = when (requestDto.page) {
                1 -> 1
                else -> requestDto.page * requestDto.size + 1
            }

            return NaverBlogRequestDto(
                query = requestDto.query,
                sort = NaverBlogSearchSortType.findBy(requestDto.sort),
                start = start,
                display = requestDto.size,
            )
        }
    }
}

internal enum class NaverBlogSearchSortType(
    val desc: String,
    val commonType: BlogSearchSortType,
) {
    sim(desc = "정확도순", BlogSearchSortType.accuracy),
    date(desc = "최신순", BlogSearchSortType.recency),
    ;

    companion object {

        fun findBy(commonSortType: BlogSearchSortType): NaverBlogSearchSortType {

            return values().first {
                it.commonType == commonSortType
            }
        }
    }
}

internal class NaverBlogResponseDto(

    @field:JsonProperty("lastBuildDate")
    var lastBuildDate: String = "",
    var total: Int = 0,
    var start: Int = 0,
    var display: Int = 0,
    var items: List<NaverDocumentsResponse> = emptyList(),
) {

    fun toBlogResponseDto(): BlogResponseDto {

        val isEnd = when {
            start >= 1000 -> true           //start가 1000넘어가면 지원해주지 않음
            total < start + display -> true //skip한 문서 수 + 현재 표출 중인 문서 수 합친게 전체 보다 클 때
            else -> false
        }

        return BlogResponseDto(
            meta = MetaResponse(
                totalCount = this.total,
                //pageableCount = this.total / this.display,
                isEnd = isEnd
                //isEnd = true
            ),
            documents = items.map {

                DocumentsResponse(
                    title = it.title,
                    contents = it.description,
                    url = it.link,
                    blogname = it.bloggername,
                    thumbnail = "",
                    datetime = it.parsePostDate()
                )
            }
        )

    }
}

internal class NaverDocumentsResponse(
    var title: String = "",
    var link: String = "",
    var description: String = "",
    var bloggername: String = "",
    var bloggerlink: String = "",

    //yyyyMMdd ex->20190607
    var postdate: String = "",
) {

    fun parsePostDate(): LocalDateTime {

        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        return LocalDate.parse(this.postdate, formatter).atStartOfDay()
    }
}