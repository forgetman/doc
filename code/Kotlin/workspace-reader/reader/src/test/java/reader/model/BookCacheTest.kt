//package reader.model
//
//import com.nhaarman.mockitokotlin2.mock
//import com.nhaarman.mockitokotlin2.verify
//import org.junit.Before
//import org.junit.Test
//import org.mockito.MockitoAnnotations
//
///**
// * @author yuansui
// * @since 2020/12/29
// */
//class BookCacheTest {
//
//    @Before
//    fun setup() {
//        MockitoAnnotations.openMocks(this)
//    }
//
//    @Test
//    fun testSetValue() {
//        val data = mock<BookCache> { bookCache ->
//            bookCache.bookId = "1"
//            bookCache.chapterId = "2"
//            bookCache.content = "3"
//        }
//
//        verify(data).bookId = "1"
//        verify(data).chapterId = "2"
//        verify(data).content = "3"
////        doAnswer<BookCache> { it.getArgument(0) }.`when`(data).bookId = "1"
//    }
//}