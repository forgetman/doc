package pretimmediat.viewmodel.inputpiece

import android.app.Application
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import logger.L
import pretimmediat.ext.throwUploadError
import pretimmediat.model.AppConfig
import pretimmediat.model.PieceUploadError
import pretimmediat.model.Region
import pretimmediat.model.inputpiece.InfoPiece
import pretimmediat.property.Properties
import pretimmediat.repo.InputPieceRepo
import sugar.ext.doOnNotNull
import vector.ext.isNotNullOrEmpty
import javax.inject.Inject

@HiltViewModel
class InfoPieceViewModel @Inject constructor(
    private val repo: InputPieceRepo,
    app: Application
) : AbstractPieceViewModel(app) {

    companion object {
        private const val LOG_TAG = "InfoPieceViewModel"
    }

    private val piece = MutableStateFlow<InfoPiece?>(null)

    val familyName = MutableStateFlow<String?>(null)
    val givenName = MutableStateFlow<String?>(null)
    val birthday = MutableStateFlow<String?>(null)

    var sexSelections: List<AppConfig> = emptyList()
    var sexCode: String? = null
    val sexText = MutableStateFlow<String?>(null)

    val email = MutableStateFlow<String?>(null)

    var provinceSelections: List<Region> = emptyList()
    val province = MutableStateFlow<Region?>(null)
    val city = MutableStateFlow<Region?>(null)
    val regionText = combine(province, city) { p, c ->
        if (p == null || c == null) {
            return@combine null
        }
        "${p.regionName} ${c.regionName}"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private val info1Valid = combine(familyName, givenName, birthday, sexText) { a, b, c, d ->
        a.isNotNullOrEmpty() && b.isNotNullOrEmpty() && c.isNotNullOrEmpty() && d.isNotNullOrEmpty()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private val info2Valid = combine(email, province, city) { e, p, c ->
        e.isNotNullOrEmpty() && p != null && c != null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val nextEnabled = combine(info1Valid, info2Valid) { a, b ->
        a && b
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)


    override fun onCreate() {
        fetchProvince().catch { e ->
            L.e(LOG_TAG, "fetchRegions", e)
        }.launchIn(viewModelScope)

        fetchBasic().catch { e ->
            L.e(LOG_TAG, "fetchBasicInfo", e)
        }.launchIn(viewModelScope)

        fetchSexSelection().catch { e ->
            L.e(LOG_TAG, "fetchSexSelection", e)
        }.launchIn(viewModelScope)

        piece.filterNotNull().onEach { data ->
            updateIfNeeded(data.familyName, familyName)
            updateIfNeeded(data.givenName, givenName)

            if (updateIfNeeded(data.sexText, sexText)) {
                sexCode = data.sex
            }

            updateIfNeeded(data.birthDay, birthday)
            updateIfNeeded(data.email, email)
            doOnNotNull(data.provinceCode, data.province) { code, name ->
                province.value = Region(code, name)
            }
            doOnNotNull(data.cityCode, data.city) { code, name ->
                city.value = Region(code, name)
            }
        }.launchIn(viewModelScope)
    }

    private fun fetchBasic() = repo.fetchBasic(userId, appSsid).onEach { value ->
        this.piece.value = value
    }

    fun uploadBasic(): Flow<String> {
        try {
            val familyName = familyName.value.throwUploadError(PieceUploadError.Type.FAMILY_NAME)
            val givenName = givenName.value.throwUploadError(PieceUploadError.Type.GIVEN_NAME)
            val birthday = birthday.value.throwUploadError(PieceUploadError.Type.BIRTH)
            val sex = sexCode.throwUploadError(PieceUploadError.Type.SEX)
            val sexText = sexText.value.throwUploadError(PieceUploadError.Type.SEX)

            val email = email.value.throwUploadError(PieceUploadError.Type.EMAIL)
            if (!email.checkEmail()) throwUploadError(PieceUploadError.Type.EMAIL)

            val provinceName = province.value?.regionName.throwUploadError(PieceUploadError.Type.PROVINCE)
            val provinceId = province.value?.regionId.throwUploadError(PieceUploadError.Type.PROVINCE)
            val cityName = city.value?.regionName.throwUploadError(PieceUploadError.Type.CITY)
            val cityId = city.value?.regionId.throwUploadError(PieceUploadError.Type.CITY)

            return repo.updateBasic(
                userId,
                appSsid,
                familyName,
                givenName,
                birthday,
                sex,
                sexText,
                email,
                provinceName,
                provinceId,
                cityName,
                cityId
            ).onEach {
                Properties.pieceGivenName.put(givenName)
            }
        } catch (e: PieceUploadError) {
            return flow {
                throw e
            }
        }
    }

    fun fetchProvince() = repo.fetchProvince().onEach { value ->
        provinceSelections = value
    }

    fun fetchCities(provinceIndex: Int) =
        repo.fetchCities(provinceSelections[provinceIndex].regionId)

    fun fetchSexSelection() = repo.fetchSexSelection().onEach {
        sexSelections = it
    }

    private fun String.checkEmail(): Boolean {
        return this.contains("@") && this.contains(".")
    }
}