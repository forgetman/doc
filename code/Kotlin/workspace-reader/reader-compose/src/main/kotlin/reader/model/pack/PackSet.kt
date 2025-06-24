package reader.model.pack

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import reader.model.Chapter

/**
 * 书本目录包装
 */
class PackSet {
    var id: String? = null
    var name: String? = null // 书名
    var list: List<PackChapter?>? = null
}

class PackChapter {
    var name: String? = null // 卷名
    var list: List<Chapter?>? = null
        set(value) {
            value?.forEach {
                it?.name = this.name + " " + it?.name
            }
            field = value
        }
}

fun Flow<PackSet>.unpack() = unpackSet().unpackChapters()

private fun Flow<PackSet>.unpackSet() =
    mapNotNull {
        it.list?.filterNotNull() ?: listOf()
    }

private fun Flow<List<PackChapter>>.unpackChapters() =
    map { pack ->
        pack.flatMap { it.list?.filterNotNull() ?: listOf() }
    }

