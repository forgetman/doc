package catroom.repo

import catroom.network.api.RoomApi
import catroom.network.createApi
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ViewModelScoped
class RoomRepo @Inject constructor() {

    fun fetchRoomInfo(
        clientId: String,
        longitude: String,
        latitude: String
    ) = createApi<RoomApi>().info(clientId, longitude, latitude).flowOn(Dispatchers.IO)
}