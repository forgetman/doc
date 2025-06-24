package test.compose.ui.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import logger.L
import test.compose.ui.activity.PagingItem
import test.compose.ui.activity.SamplePagingSource
import vector.app.compose.ui.viewmodel.ViewModelEx
import javax.inject.Inject

@HiltViewModel
class PagingViewModel @Inject constructor(
    private val repo: SampleRepository,
) : ViewModelEx() {

    val itemsFlow: Flow<PagingData<PagingItem>> = repo.getItems().cachedIn(viewModelScope)

    override fun onCleared() {
        L.www("paging onCleared")
    }
}

@ViewModelScoped
class SampleRepository @Inject constructor() {
    fun getItems(): Flow<PagingData<PagingItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SamplePagingSource() }
        ).flow
    }
}

