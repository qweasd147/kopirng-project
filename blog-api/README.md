# Blog API

## 1. 사용 기술 및 라이브러리

- java 17 (kotlin 1.7.22)
- Spring boot 3.0
- Spring Webflux
- Coroutine
- H2 DB
- Spring data-r2dbc
- spring cloud
  - open feign
  - circuit breaker

### 그 외 오픈소스 라이브러리

- [kotlin-logging](https://github.com/oshai/kotlin-logging)
- [mockk](https://github.com/mockk/mockk)
- [kotest](https://kotest.io/)

## 2. 사용방법

#### 빌드 후 jar 파일 실행

```sh
$ ./gradlew build
$ java -jar blog-app/build/libs/blog-app-1.0.0.jar
```

## 3. API 명세

### 3.1 블로그 검색

`GET - http://localhost:8080/v1/search`

#### Param

- query : 검색 할 키워드
- page : 페이지 번호
- size : 한 페이지에 보여질 문서 수
- sort 정결 방식. accuracy(정확도순) 또는 recency(최신순)

#### Response

```
{
    "meta": {
        "totalCount": 전체 개수,
        "isEnd": 마지막 여부
    },
    "documents": [
        {
            "title": "제목",
            "contents": "내용",
            "url": "url 주소",
            "blogname": "blog name",
            "thumbnail": "섬네일. 빈 값일 수도 있음",
            "datetime": "작성시간"
        }
        , ...
    ]
}
```

### 3.2 인기 검색어 top 10

`GET - http://localhost:8080/v1/word`

#### Response

```
[
    {
        "word": 검색 한 단어,
        "count": 검색 횟수
    }
    , ...
]
```

## 4. 구현 방법

### 4.1 블로그 검색 및 검색어 수집

#### 기본

검색 요청 api를 kakao blog api에 맞추어 요청 및 응답을 전달 해줍니다. 만약 kakao api가 장애가 발생하여 반복적으로 에러가 발생 할 경우, `Circuit Breaker`가 `Open`되며 `Open`시 `Naver blog api`로 요청을 보내어 응답 받게 됩니다. 또한 `Webflux`와 요청 API는 `Open feign`을 사용하였는데 외부 api 요청 시, 해당 `Webflux` 스레드는 blocking되어 퍼포먼스가 오히려 떨어질 수 있어 별도의 `coroutine scope`를 만들어 처리하였습니다.

> 현재 `Circuit Breaker` 상태가 바뀌면 log 정도만 남기도록 구현

#### 검색 데이터 수집

검색 요청이 오면 검색 event를 발행하는데(`blogEventPublisher.sendSearchEvent`) event 발생 하는 도구는 `ApplicationEventPublisher`를 통해 발생시켰지만 언제든 바뀔 수 있기 때문에 `interface` 기반으로 별도의 구현체를 두었습니다. 이벤트를 처리하는 `BlogEventSubscriber`도 똑같이 `interface`기반으로 구현하였고, `subscriber`는 검색어 수집에 있어서 검색 자체 api가 딜레이되거나 에러가 발생하는걸 방지하기 위해 별도로 실행됩니다. `subscriber`에서는 검색어를 DB에 저장하는 역할을 합니다.

이렇게 수집 된 데이터는 주기적으로 scheduler를 통하여 저장된 검색어가 있으면 가공하여 다시 DB에 저장, 이미 저장된 검색어라면 count를 증가 시키고 아니라면 새롭게 insert 합니다.

> 검색어는 `SearchLog` 테이블에 저장되어 5초에 한번씩 이 테이블에서 처리 안된 데이터를 가져와 `SearchWord` 테이블에 저장합니다. (`CollectorScheduler` 클래스 참고)

`publisher interface`를 제외한 다른 구현체는 `internal` 키워드를 통해 다른 모듈에서 접근 할 수 없습니다. 이는 해당 모듈 외에 수집 처리하는 곳을 두지 않기 위해 이렇게 설계하였습니다.

TODO

- 데이터 가공 및 저장되는 검색어는 `trim`을 제외한 별도의 처리는 안하고 있고, 이는 검색어를 기반으로 단어나 비슷한 내용을 처리하기엔 시간이 많이 걸릴꺼라 판단하여 간단하게 구현했습니다.
- 너무 긴 검색어는 실질적으로 의미없는 데이터라고 판단하였고, 비용문제를 고려하여 일단 skip하고 별도로 저장하지 않습니다.
- 현재 혹시라도 여러 worker가 `SearchLog`에서 데이터를 가져와서 처리하면 별도의 Lock이 없어 정합성이 깨질 수 있습니다. 이는 현재 중간 저장 단계가 RDB로 `SearchLog` 테이블에 저장하고 있어 별도의 message queue로 분리하면 이테이블이 없어질수도 있어 크게 고려하지 않았습니다(현재는 단일로 돌아가서 문제 x).

#### blog 검색소스 관련 class 설계

블로그 외부 검색 관련 모듈은 `external-api`로 두었고, 필수 `paramer dto`와 `BlogApiClient` 클래스를 제외하곤 다른 외부 모듈에서 접근 할 수 없습니다(`internal`).

각 blog 검색 소스는 `BlogAPIProvider` interface만 구현하면 되며, 구현하여 `Bean`으로 등록되면 `provider` 모음에 등록되어 `BlogAPIProvider`에서 바로 접근 할 수 있게 설계 되었습니다.

### 4.2 인기검색어 목록 조회

수집 되어 DB에 저장된 데이터를 바로 조회, 출력 되도록 만들었습니다.
