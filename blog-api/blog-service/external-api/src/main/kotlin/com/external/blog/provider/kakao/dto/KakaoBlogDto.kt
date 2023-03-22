package com.external.blog.provider.kakao.dto

import com.external.blog.dto.BlogResponseDto
import com.external.blog.dto.BlogSearchRequestDto
import com.external.blog.dto.DocumentsResponse
import com.external.blog.dto.MetaResponse
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal class KakaoBlogRequestDto(
    val query: String = "",
    val sort: KakaoBlogSearchSortType = KakaoBlogSearchSortType.accuracy,
    val page: Int = 1,
    val size: Int = 10,
) {

    companion object {

        fun of(requestDto: BlogSearchRequestDto): KakaoBlogRequestDto {

            return KakaoBlogRequestDto(
                query = requestDto.query,
                sort = KakaoBlogSearchSortType.findByName(requestDto.sort.name),
                page = requestDto.page,
                size = requestDto.size,
            )
        }
    }
}

internal enum class KakaoBlogSearchSortType(
    val desc: String,
) {
    accuracy(desc = "정확도순"),
    recency(desc = "최신순"),
    ;

    companion object {

        fun findByName(name: String): KakaoBlogSearchSortType {

            return values().first {
                it.name == name
            }
        }
    }
}

internal class KakaoBlogResponseDto(
    var meta: KakaoMetaResponse = KakaoMetaResponse(),
    var documents: List<KakaoDocumentsResponse> = emptyList(),
) {

    fun toBlogResponseDto(): BlogResponseDto {

        return BlogResponseDto(
            meta = MetaResponse(
                totalCount = this.meta.pageableCount,   //전체 숫자에 표출 가능한 숫자를 넣는다.
                isEnd = this.meta.isEnd,
                //isEnd = true
            ),
            documents = documents.map {

                DocumentsResponse(
                    title = it.title,
                    contents = it.contents,
                    url = it.url,
                    blogname = it.blogname,
                    thumbnail = it.thumbnail,
                    datetime = it.parseDateTime()
                )
            }
        )

    }
}

internal class KakaoMetaResponse(

    //@field:JsonProperty("total_count")
    var totalCount: Int = 0,   //검색 된 문서 수
    var pageableCount: Int = 0,  //total count 중 노출 가능 문서 수

    @field:JsonProperty("is_end")
    var isEnd: Boolean = true, //현재 페이지가 마지막 페이지인지 여부, 값이 false면 page를 증가시켜 다음 페이지를 요청할 수 있음
)

internal class KakaoDocumentsResponse(
    var title: String = "",
    var contents: String = "",
    var url: String = "",
    var blogname: String = "",
    var thumbnail: String = "",
    var datetime: String = "",
) {

    fun parseDateTime(): LocalDateTime {
        //val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        return LocalDateTime.parse(datetime, formatter)
    }
}