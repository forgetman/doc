package test.model

import inject.annotation.builder.Builder

/**
 * @author yuansui
 * @since 2018/6/5
 */
@Builder
data class User(
    val name: String,
    val age: Int
)