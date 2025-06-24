package pretimmediat.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import image.api.load
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pretimmediat.R
import pretimmediat.util.decodeExifOrientation
import vector.app.activity.SimpleActivityEx
import vector.app.ext.bind.bindView
import vector.app.ext.view.setOnDebounceClickListener
import vector.app.util.inflate
import java.io.BufferedInputStream
import java.io.File
import kotlin.math.max

@Creator(forResult = true)
class ImageViewerActivity : SimpleActivityEx() {

    @Extra
    var orientation: Int = 0

    @Extra
    var depth: Boolean = false

    @Extra
    var filePath: String = ""

    /** Default Bitmap decoding options */
    private val bitmapOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = false
        // Keep Bitmaps at less than 1 MP
        if (max(outHeight, outWidth) > DOWNSAMPLE_SIZE) {
            val scaleFactorX = outWidth / DOWNSAMPLE_SIZE + 1
            val scaleFactorY = outHeight / DOWNSAMPLE_SIZE + 1
            inSampleSize = max(scaleFactorX, scaleFactorY)
        }
    }

    /** Bitmap transformation derived from passed arguments */
    private val bitmapTransformation: Matrix by lazy { decodeExifOrientation(orientation) }

    /** Flag indicating that there is depth data available for this image */
    private val isDepth: Boolean by lazy { depth }

    /** Data backing our Bitmap viewpager */
    private val bitmapList: MutableList<Bitmap> = mutableListOf()

    private fun imageViewFactory() = ImageView(this).apply {
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
    }

    /** Utility function used to read input file into a byte array */
    private fun loadInputBuffer(): ByteArray {
        val inputFile = File(filePath)
        return BufferedInputStream(inputFile.inputStream()).let { stream ->
            ByteArray(stream.available()).also {
                stream.read(it)
                stream.close()
            }
        }
    }

    /** Utility function used to add an item to the viewpager and notify it, in the main thread */
    private fun addItemToViewPager(view: ViewPager2, item: Bitmap) = view.post {
        bitmapList.add(item)
        view.adapter?.notifyDataSetChanged()
    }

    /** Utility function used to decode a [Bitmap] from a byte array */
    private fun decodeBitmap(buffer: ByteArray, start: Int, length: Int): Bitmap {

        // Load bitmap from given buffer
        val bitmap = BitmapFactory.decodeByteArray(buffer, start, length, bitmapOptions)

        // Transform bitmap orientation using provided metadata
        return Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, bitmapTransformation, true
        )
    }

    companion object {
        private const val LOG_TAG = "ImageViewerActivity"

        /** Maximum size of [Bitmap] decoded */
        private const val DOWNSAMPLE_SIZE: Int = 1024  // 1MP

        /** These are the magic numbers used to separate the different JPG data chunks */
        private val JPEG_DELIMITER_BYTES = arrayOf(-1, -39)

        /**
         * Utility function used to find the markers indicating separation between JPEG data chunks
         */
        private fun findNextJpegEndMarker(jpegBuffer: ByteArray, start: Int): Int {

            // Sanitize input arguments
            assert(start >= 0) { "Invalid start marker: $start" }
            assert(jpegBuffer.size > start) {
                "Buffer size (${jpegBuffer.size}) smaller than start marker ($start)"
            }

            // Perform a linear search until the delimiter is found
            for (i in start until jpegBuffer.size - 1) {
                if (jpegBuffer[i].toInt() == JPEG_DELIMITER_BYTES[0] &&
                    jpegBuffer[i + 1].toInt() == JPEG_DELIMITER_BYTES[1]
                ) {
                    return i + 2
                }
            }

            // If we reach this, it means that no marker was found
            throw RuntimeException("Separator marker not found in buffer (${jpegBuffer.size})")
        }
    }

    private val viewPager2 by bindView<ViewPager2>(R.id.view_pager)
    private val ivConfirm by bindView<View>(R.id.iv_confirm)
    private val ivCancel by bindView<View>(R.id.iv_cancel)

    override fun createContentView(): View {
        return R.layout.activity_image_viewer.inflate(this)
    }

    override fun initializeContentView() {
        viewPager2.apply {
            // Populate the ViewPager and implement a cache of two media items
            offscreenPageLimit = 2
            adapter = GenericListAdapter(
                bitmapList,
                itemViewFactory = { imageViewFactory() }) { view, item, _ ->
                view as ImageView
                view.load {
                    source(item)
                }
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val view = this@apply

                // Load input image file
                val inputBuffer = loadInputBuffer()

                // Load the main JPEG image
                addItemToViewPager(view, decodeBitmap(inputBuffer, 0, inputBuffer.size))

                // If we have depth data attached, attempt to load it
                if (isDepth) {
                    try {
                        val depthStart = findNextJpegEndMarker(inputBuffer, 2)
                        addItemToViewPager(
                            view, decodeBitmap(
                                inputBuffer, depthStart, inputBuffer.size - depthStart
                            )
                        )

                        val confidenceStart = findNextJpegEndMarker(inputBuffer, depthStart)
                        addItemToViewPager(
                            view, decodeBitmap(
                                inputBuffer, confidenceStart, inputBuffer.size - confidenceStart
                            )
                        )

                    } catch (exc: RuntimeException) {
                        Log.e(LOG_TAG, "Invalid start marker for depth or confidence data")
                    }
                }
            }
        }


        ivConfirm.setOnDebounceClickListener {
            setResult(RESULT_OK)
            finish()
        }

        ivCancel.setOnDebounceClickListener {
            finish()
        }
    }
}

/** Type helper used for the callback triggered once our view has been bound */
typealias BindCallback<T> = (view: View, data: T, position: Int) -> Unit

/** List adapter for generic types, intended used for small-medium lists of data */
class GenericListAdapter<T>(
    private val dataset: List<T>,
    private val itemLayoutId: Int? = null,
    private val itemViewFactory: (() -> View)? = null,
    private val onBind: BindCallback<T>
) : RecyclerView.Adapter<GenericListAdapter.GenericListViewHolder>() {

    class GenericListViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GenericListViewHolder(
        when {
            itemViewFactory != null -> itemViewFactory.invoke()
            itemLayoutId != null -> {
                LayoutInflater.from(parent.context)
                    .inflate(itemLayoutId, parent, false)
            }

            else -> {
                throw IllegalStateException(
                    "Either the layout ID or the view factory need to be non-null"
                )
            }
        }
    )

    override fun onBindViewHolder(holder: GenericListViewHolder, position: Int) {
        if (position < 0 || position > dataset.size) return
        onBind(holder.view, dataset[position], position)
    }

    override fun getItemCount() = dataset.size
}

