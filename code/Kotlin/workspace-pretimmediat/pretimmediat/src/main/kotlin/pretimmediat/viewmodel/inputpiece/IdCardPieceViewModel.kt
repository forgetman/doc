package pretimmediat.viewmodel.inputpiece

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import image.api.ImageLoadListener
import image.drawable.DrawableResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.suspendCancellableCoroutine
import logger.L
import pretimmediat.ext.throwUploadError
import pretimmediat.model.PieceUploadError
import pretimmediat.model.inputpiece.IdCardImagePiece
import pretimmediat.model.inputpiece.IdCardPiece
import pretimmediat.property.Properties
import pretimmediat.repo.InputPieceRepo
import vector.app.ext.saveToFile
import vector.app.util.BitmapLoader
import vector.ext.getSize
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Suppress("OPT_IN_USAGE")
@HiltViewModel
class IdCardPieceViewModel @Inject constructor(
    private val repo: InputPieceRepo,
    app: Application
) : AbstractPieceViewModel(app) {

    companion object {
        private const val LOG_TAG = "IdCardPieceViewModel"
        private const val COMPRESS_SIZE = 800
        private const val COMPRESS_SIZE_SMALL = 600
        private const val MAX_COMPRESSED_SIZE = 50 * 1024 // 最大压缩后的图片大小
    }

    private val piece = MutableStateFlow<IdCardPiece?>(null)
    private var idImages = MutableStateFlow<IdCardImagePiece?>(null)

    val idCardSource = MutableStateFlow<Any?>(null)
    val idCardLoading = MutableStateFlow(false)
    val idCardUploadResult = MutableStateFlow<Boolean?>(null)
    val idCardListener = object : ImageLoadListener {
        override fun onStart() {
            idCardLoading.value = true
        }

        override fun onSuccess(dr: DrawableResource?) {
            idCardLoading.value = false
        }

        override fun onCancel() {
            idCardLoading.value = false
        }

        override fun onError(dr: DrawableResource?, error: Throwable) {
            L.e(LOG_TAG, "onError, idcard", error)
            idCardLoading.value = false
        }
    }
    private val idCardValid = combine(
        idCardSource,
        idCardUploadResult,
        idImages
    ) { source, uploadResult, images ->
        when {
            images != null && images.cardFrontFlag == "1" -> true // 之前已经上传过
            source == null -> false
            source is Uri && uploadResult == true -> true // 本地数据上传成功
            else -> false
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val faceSource = MutableStateFlow<Any?>(null)
    val faceLoading = MutableStateFlow(false)
    val faceUploadResult = MutableStateFlow<Boolean?>(null)
    val faceListener = object : ImageLoadListener {
        override fun onStart() {
            faceLoading.value = true
        }

        override fun onSuccess(dr: DrawableResource?) {
            faceLoading.value = false
        }

        override fun onCancel() {
            faceLoading.value = false
        }

        override fun onError(dr: DrawableResource?, error: Throwable) {
            L.e(LOG_TAG, "onError, face", error)
            faceLoading.value = false
        }
    }
    private val faceValid = combine(
        faceSource,
        faceUploadResult,
        idImages
    ) { source, uploadResult, images ->
        when {
            images != null && images.faceVerified == "1" -> true // 之前已经上传过
            source == null -> false
            source is Uri && uploadResult == true -> true // 本地数据上传成功
            else -> false
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val sampleExpand = MutableStateFlow(false)

    val idNumber = MutableStateFlow<String?>(null)

    val nextEnabled = combine(idCardValid, faceValid, idNumber) { a, b, c ->
        a && b && c != null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private var uploadIdCardJob: Job? = null
    private var uploadFaceJob: Job? = null


    override fun onCreate() {
        idCardSource.filterNotNull().filter { it is Uri }.map { it as Uri }
            .onEach { uri ->
                val file = File(applicationContext.cacheDir, "id_card_front.jpg")
                if (file.exists()) file.delete()
                uploadIdCardJob?.cancel()
                uploadIdCardJob = callbackFlow {
                    val path = suspendCancellableCoroutine { cont ->
                        compress(file, uri) { result ->
                            if (result) {
                                cont.resume(file.absolutePath)
                            } else {
                                cont.resumeWithException(IllegalArgumentException("can not compress the idcard"))
                            }
                        }
                    }
                    trySend(path)
                    close()
                }.flatMapConcat { path ->
                    repo.uploadIdCardFront(userId, appSsid, path)
                }.onStart {
                    idCardUploadResult.value = null
                    idCardLoading.value = true
                }.onCompletion {
                    L.d(LOG_TAG, "onCreate, uploadIdCard, onCompletion")
                    idCardLoading.value = false
                    if (file.exists()) file.deleteOnExit()
                }.catch { e ->
                    L.e(LOG_TAG, "uploadIdCardFront", e)
                    idCardUploadResult.value = false
                }.flowOn(Dispatchers.IO).onEach {
                    idCardUploadResult.value = true
                }.launchIn(viewModelScope)
            }.launchIn(viewModelScope)

        faceSource.filterNotNull().filter { it is Uri }.map { it as Uri }
            .onEach { uri ->
                val file = File(applicationContext.cacheDir, "face.jpg")
                if (file.exists()) file.delete()
                uploadFaceJob?.cancel()
                uploadFaceJob = callbackFlow {
                    val path = suspendCancellableCoroutine { cont ->
                        compress(file, uri) { result ->
                            if (result) {
                                cont.resume(file.absolutePath)
                            } else {
                                cont.resumeWithException(IllegalArgumentException("can not compress the idcard"))
                            }
                        }
                    }
                    trySend(path)
                    close()
                }.flatMapConcat { path ->
                    repo.uploadFace(userId, appSsid, path)
                }.onStart {
                    faceUploadResult.value = null
                    faceLoading.value = true
                }.onCompletion {
                    L.d(LOG_TAG, "onCreate, uploadFace, onCompletion")
                    faceLoading.value = false
                    if (file.exists()) file.deleteOnExit()
                }.catch { e ->
                    L.e(LOG_TAG, "uploadFace", e)
                    faceUploadResult.value = false
                }.flowOn(Dispatchers.IO).onEach {
                    faceUploadResult.value = true
                }.launchIn(viewModelScope)
            }.launchIn(viewModelScope)

        fetchIdCard().catch { e ->
            L.e(LOG_TAG, "fetchIdCard", e)
        }.launchIn(viewModelScope)

        fetchIdImages().catch { e ->
            L.e(LOG_TAG, "fetchIdImages", e)
        }.launchIn(viewModelScope)

        piece.filterNotNull().onEach { p ->
            updateIfNeeded(p.idNumber, idNumber)
        }.launchIn(viewModelScope)
    }

    fun uploadIdCard(): Flow<String> {
        try {
            val idCardValid = idCardValid.value
            if (!idCardValid) {
                throw PieceUploadError(PieceUploadError.Type.ID_CARD)
            }

            val faceValid = faceValid.value
            if (!faceValid) {
                throw PieceUploadError(PieceUploadError.Type.FACE)
            }

            val idNumber = idNumber.value.throwUploadError(PieceUploadError.Type.ID_NUMBER)
            return repo.uploadIdCard(userId, appSsid, idNumber)
        } catch (e: PieceUploadError) {
            return flow {
                throw e
            }
        }
    }

    private fun fetchIdCard() = repo.fetchIdCard(userId, appSsid).onEach { value ->
        this.piece.value = value
    }

    private fun fetchIdImages() = repo.fetchIdImages(userId, appSsid).onEach { value ->
        idImages.value = value
        if (value.cardFrontFlag == "1" && value.cardFrontUrl != null) {
            idCardSource.value = value.cardFrontUrl
        }
        if (value.faceVerified == "1" && value.faceUrl != null) {
            faceSource.value = value.faceUrl
            Properties.pieceFaceUrl.putIn(viewModelScope, value.faceUrl)
        }
    }

    /**
     * 压缩图片, 尽量保持在50K以内
     * 最多压缩两次(原图调整参数重新压缩, 不是叠加再次压缩)
     */
    private fun compress(
        file: File,
        uri: Uri,
        callback: (result: Boolean) -> Unit
    ) {
        if (file.exists()) file.delete()
        BitmapLoader.fromUri(uri).asBitmap(COMPRESS_SIZE, COMPRESS_SIZE)?.let {
            it.saveToFile(file, Bitmap.CompressFormat.JPEG, 80)
            val fileSize = file.getSize()
            L.d(LOG_TAG, "compress, first size = $fileSize")
            if (fileSize > MAX_COMPRESSED_SIZE) {
                BitmapLoader.fromPath(file.path).asBitmap(COMPRESS_SIZE_SMALL, COMPRESS_SIZE_SMALL)
                    ?.let {
                        file.delete()
                        it.saveToFile(file, Bitmap.CompressFormat.JPEG, 60)
                        L.d(LOG_TAG, "compress, second size = ${file.getSize()}")
                        callback(true)
                    } ?: callback(false)
            } else {
                callback(true)
            }
        } ?: callback(false)
    }
}