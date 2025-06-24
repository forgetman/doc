package pretimmediat.viewmodel.inputpiece

import android.app.Activity
import android.app.Application
import android.database.Cursor
import android.provider.ContactsContract
import androidx.lifecycle.viewModelScope
import coroutine.flow.launchIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import logger.L
import pretimmediat.ext.throwUploadError
import pretimmediat.manager.BigJsonManager
import pretimmediat.model.AppConfig
import pretimmediat.model.PieceUploadError
import pretimmediat.model.inputpiece.ContactPiece
import pretimmediat.repo.InputPieceRepo
import vector.ext.getInt
import vector.ext.getString
import vector.ext.isNotNullOrEmpty
import vector.ext.replaceBlank
import vector.ext.safeQuery
import vector.util.intent.IntentAction
import javax.inject.Inject

@Suppress("OPT_IN_USAGE")
@HiltViewModel
class ContactPieceViewModel @Inject constructor(
    private val repo: InputPieceRepo,
    app: Application
) : AbstractPieceViewModel(app) {

    companion object {
        private const val LOG_TAG = "ContactPieceViewModel"
        const val CONTACT_FROM_SELECT = 0
        const val CONTACT_FROM_INPUT = 1
    }

    private val piece = MutableStateFlow<ContactPiece?>(null)

    var relationshipSelections: List<AppConfig> = emptyList()
    var secRelationshipSelections: List<AppConfig> = emptyList()
    var firstRelationshipCode: String? = null
    val firstRelationshipDesc = MutableStateFlow<String?>(null)
    var secRelationshipCode: String? = null
    val secRelationshipDesc = MutableStateFlow<String?>(null)

    val contactFrom = MutableStateFlow(CONTACT_FROM_SELECT)

    val firstPhoneNumber = MutableStateFlow<String?>(null)
    val firstName = MutableStateFlow<String?>(null)
    val firstNumberClearVisible = combine(firstPhoneNumber, contactFrom) { number, from ->
        number.isNotNullOrEmpty() && from == CONTACT_FROM_INPUT
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)
    val firstNameClearVisible = combine(firstName, contactFrom) { name, from ->
        name.isNotNullOrEmpty() && from == CONTACT_FROM_INPUT
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val secPhoneNumber = MutableStateFlow<String?>(null)
    val secName = MutableStateFlow<String?>(null)
    val secNumberClearVisible = combine(secPhoneNumber, contactFrom) { number, from ->
        number.isNotNullOrEmpty() && from == CONTACT_FROM_INPUT
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)
    val secNameClearVisible = combine(secName, contactFrom) { name, from ->
        name.isNotNullOrEmpty() && from == CONTACT_FROM_INPUT
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)


    private val firstValid =
        combine(firstRelationshipDesc, firstPhoneNumber, firstName) { a, b, c ->
            a.isNotNullOrEmpty() && b.isNotNullOrEmpty() && c.isNotNullOrEmpty()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private val secondValid = combine(secRelationshipDesc, secPhoneNumber, secName) { a, b, c ->
        a.isNotNullOrEmpty() && b.isNotNullOrEmpty() && c.isNotNullOrEmpty()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val nextEnabled = combine(firstValid, secondValid) { first, second ->
        first && second
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    override fun onCreate() {
        fetchContact().catch { e ->
            L.e(LOG_TAG, "fetchEmergencyContact", e)
        }.launchIn(viewModelScope)

        fetchRelationship().catch { e ->
            L.e(LOG_TAG, "fetchRelationship", e)
        }.launchIn(viewModelScope)

        fetchSecRelationship().catch { e ->
            L.e(LOG_TAG, "fetchSecRelationship", e)
        }.launchIn(viewModelScope)

        piece.filterNotNull().onEach { data ->
            if (updateIfNeeded(data.rsName, firstRelationshipDesc)) {
                firstRelationshipCode = data.relationshipCode
            }
            if (updateIfNeeded(data.rsSecName, secRelationshipDesc)) {
                secRelationshipCode = data.rsSecCode
            }

            updateIfNeeded(data.phoneNumber, firstPhoneNumber)
            updateIfNeeded(data.pnSec, secPhoneNumber)
            updateIfNeeded(data.name, firstName)
            updateIfNeeded(data.nameSec, secName)
        }.launchIn(viewModelScope)
    }

    private fun fetchContact() = repo.fetchContact(userId, appSsid).onEach { value ->
        this.piece.value = value
    }

    fun fetchRelationship() = repo.fetchRelationship().onEach {
        relationshipSelections = it
    }

    fun fetchSecRelationship() = repo.fetchSecRelationship().onEach {
        secRelationshipSelections = it
    }

    fun uploadContact(): Flow<String> {
        try {
            val firstRelationshipCode =
                firstRelationshipCode.throwUploadError(PieceUploadError.Type.FIRST_RELATIONSHIP)
            val firstRelationshipDesc =
                firstRelationshipDesc.value.throwUploadError(PieceUploadError.Type.FIRST_RELATIONSHIP)
            val firstNumber =
                firstPhoneNumber.value.throwUploadError(PieceUploadError.Type.FIRST_PHONE_NUMBER)
            val firstName = firstName.value.throwUploadError(PieceUploadError.Type.FIRST_NAME)

            val secRelationshipCode =
                secRelationshipCode.throwUploadError(PieceUploadError.Type.SEC_RELATIONSHIP)
            val secRelationshipDesc =
                secRelationshipDesc.value.throwUploadError(PieceUploadError.Type.SEC_RELATIONSHIP)
            val secNumber =
                secPhoneNumber.value.throwUploadError(PieceUploadError.Type.SEC_PHONE_NUMBER)
            val setName = secName.value.throwUploadError(PieceUploadError.Type.SEC_NAME)

            return repo.uploadContact(
                userId,
                appSsid,
                firstNumber,
                firstName,
                firstRelationshipDesc,
                firstRelationshipCode,
                secNumber,
                setName,
                secRelationshipDesc,
                secRelationshipCode
            )
        } catch (e: PieceUploadError) {
            return flow {
                throw e
            }
        }
    }

    fun uploadBigJson() = BigJsonManager.bigJsonFlow(applicationContext).flatMapConcat {
        repo.uploadBigJson(userId, appSsid, it)
    }.flowOn(Dispatchers.IO)

    fun selectFromContacts(host: Activity, callback: (number: String, name: String) -> Unit) {
        IntentAction.contacts().host(host).callback { resultCode, data ->
            val contactUri = data?.data
            L.d(LOG_TAG, "resultCode = $resultCode, contactUri = $contactUri")
            if (resultCode == Activity.RESULT_OK) {
                if (contactUri != null) {
                    val projection = arrayOf(
                        ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.Contacts.HAS_PHONE_NUMBER,
                    )

                    try {
                        applicationContext.contentResolver.query(
                            contactUri,
                            projection,
                            null,
                            null,
                            null
                        ).use { cursor: Cursor? ->
                            if (cursor != null && cursor.moveToFirst()) {
                                val id = cursor.getString(ContactsContract.Contacts._ID)
                                val name = cursor.getString(ContactsContract.Contacts.DISPLAY_NAME)
                                val hasNumber =
                                    cursor.getInt(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                                L.d(
                                    LOG_TAG,
                                    "contacts action, id = $id, name = $name, hasNumber = $hasNumber"
                                )
                                if (hasNumber == 1) {
                                    val numberProjection =
                                        arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                    val selection =
                                        ContactsContract.CommonDataKinds.Phone._ID + " = ?"
                                    val selectionArgs = arrayOf(id)
                                    val result = applicationContext.contentResolver.safeQuery(
                                        contactUri,
                                        numberProjection,
                                        selection,
                                        selectionArgs,
                                        null
                                    ) { numberCursor ->
                                        if (numberCursor.moveToNext()) {
                                            val number =
                                                numberCursor.getString(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                            L.d(LOG_TAG, "contacts action, number = $number")
                                            callback(number.replaceBlank(), name)
                                        } else {
                                            cannotAccessContacts()
                                        }
                                    }
                                    if (result == null) {
                                        cannotAccessContacts()
                                    }
                                }
                            } else {
                                cannotAccessContacts()
                            }
                        }
                    } catch (e: Exception) {
                        L.e(LOG_TAG, e)
                        cannotAccessContacts()
                    }
                } else {
                    cannotAccessContacts()
                }
            } else {
                // 点击了返回, 不处理
                // do nothing
            }
        }.launch()
    }

    private fun cannotAccessContacts() {
        L.d(LOG_TAG, "get contact error, switch to input")
        contactFrom.value = CONTACT_FROM_INPUT
    }
}