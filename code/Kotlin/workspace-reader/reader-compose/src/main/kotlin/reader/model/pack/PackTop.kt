package reader.model.pack

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import reader.model.Book

data class PackTop(@SerializedName("BookList") var list: List<Book>)

fun Flow<PackTop>.unpack(): Flow<List<Book>> = map { it.list }