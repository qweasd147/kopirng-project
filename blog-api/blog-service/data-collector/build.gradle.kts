dependencies {


    implementation("org.springframework:spring-context")
    api(project(":blog-data:blog-r2dbc"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:${property("mockkVersion")}")
    testImplementation("org.mockito.kotlin:mockito-kotlin:${property("mockitoKotlin")}")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:${property("kotestVersion")}")
    testImplementation("io.kotest:kotest-assertions-core-jvm:${property("kotestVersion")}")
}