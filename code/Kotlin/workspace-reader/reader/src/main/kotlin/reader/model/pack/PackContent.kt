package reader.model.pack

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

data class PackContent(var content: String?)

fun Flow<PackContent>.unpack() = mapNotNull { it.content }